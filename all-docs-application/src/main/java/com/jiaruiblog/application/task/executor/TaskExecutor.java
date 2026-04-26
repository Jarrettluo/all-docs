package com.jiaruiblog.application.task.executor;

import com.jiaruiblog.application.service.DocumentService;
import com.jiaruiblog.application.service.ElasticService;
import com.jiaruiblog.application.task.data.TaskData;
import com.jiaruiblog.application.task.exception.TaskRunException;
import com.jiaruiblog.common.constants.StorageConstants;
import com.jiaruiblog.common.enums.FileFormatEnum;
import com.jiaruiblog.common.util.SpringApplicationContext;
import com.jiaruiblog.domain.entity.po.FileDocument;
import com.jiaruiblog.domain.entity.po.SearchDocument;
import com.jiaruiblog.infrastructure.storage.StorageFactory;
import com.jiaruiblog.infrastructure.storage.StorageStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author Jarrett Luo
 * @Date 2022/10/20 18:23
 * @Version 1.0
 */
@Slf4j
public abstract class TaskExecutor {

    public void execute(TaskData taskData) throws TaskRunException {

        // 第一步下载文件，转换为byte数组
        FileDocument fileDocument = taskData.getFileDocument();
        byte[] dfsBytes = downFileBytes(fileDocument.getGridfsId());
        InputStream docInputStream = new ByteArrayInputStream(dfsBytes);

        // 第二步 将文本索引到es中
        try {
            uploadFileToEs(docInputStream, fileDocument, taskData);
        } catch (Exception e) {
            throw new TaskRunException("建立索引的时候出错!", e);
        }
        docInputStream = new ByteArrayInputStream(dfsBytes);
        try {
            // 制作不同分辨率的缩略图
            updateFileThumb(docInputStream, taskData.getFileDocument(), taskData);
        } catch (Exception e) {
            throw new TaskRunException("建立缩略图的时候出错啦！", e);
        }
        // 第三步 制作预览文件
        docInputStream = new ByteArrayInputStream(dfsBytes);
        makePreviewFile(docInputStream, taskData);

        // 清空内存占用
        dfsBytes = new byte[]{};
    }

    /**
     * @return byte[]
     * @author luojiarui
     * @Description // 从gridFS 系统中下载文件为字节流
     * @Date 15:02 2022/11/13
     * @Param [gridFsId]
     **/
    protected byte[] downFileBytes(String gridFsId) {
        DocumentService fileService = SpringApplicationContext.getBean(DocumentService.class);
        return fileService.getFileBytes(gridFsId);
    }

    public void uploadFileToEs(InputStream is, FileDocument fileDocument, TaskData taskData) {
        String textFilePath = "./" + fileDocument.getMd5() + fileDocument.getName() + ".txt";
        taskData.setTxtFilePath(textFilePath);

        try {
            // 根据不同的执行器，执行不同的文本提取方法，在这里做出区别
            readText(is, textFilePath);
            if (!new File(textFilePath).exists()) {
                throw new TaskRunException("文本文件不存在，需要进行重新提取");
            }
            SearchDocument searchDocument = new SearchDocument();
            searchDocument.setId(fileDocument.getMd5());
            searchDocument.setName(fileDocument.getName());
            searchDocument.setType(fileDocument.getContentType());
            // 直接读取提取的文本内容，不做 base64 编码
            searchDocument.setContent(Files.readString(Paths.get(textFilePath), StandardCharsets.UTF_8));
            this.upload(searchDocument);

        } catch (IOException | TaskRunException e) {
            throw new TaskRunException("存入es的过程中报错了", e);
        }

        handleDescription(textFilePath, fileDocument);

        // 被文本文件上传到gridFS系统中
        try (FileInputStream inputStream = new FileInputStream(textFilePath)) {

            // 使用 document-texts/{md5}_{originalName}.txt 路径存储文本文件
            String originalName = fileDocument.getName();
            String md5 = fileDocument.getMd5();
            // textObjectKey 已经包含 .txt 后缀，documentTextPath 会再添加一次，所以传入时不带 .txt
            String textObjectKey = md5 + "_" + originalName;
            String fullPath = StorageConstants.documentTextPath(textObjectKey);

            DocumentService fileService = SpringApplicationContext.getBean(DocumentService.class);
            StorageFactory storageFactory = SpringApplicationContext.getBean(StorageFactory.class);
            StorageStrategy storageStrategy = storageFactory.getStorageStrategy();
            storageStrategy.upload(inputStream, fullPath, FileFormatEnum.TEXT.getContentType());

            fileDocument.setTextFileId(textObjectKey);

        } catch (IOException e) {
            throw new TaskRunException("存储文本文件报错了，请核对", e);
        } finally {
            // 确保文本文件被删除
            try {
                Files.deleteIfExists(Paths.get(textFilePath));
            } catch (IOException e) {
                log.error("删除文本文件失败: {}", textFilePath, e);
            }
        }

    }

    /**
     * @author luojiarui
     * @Description 设置描述内容
     * @Date 18:54 2022/11/13
     * @Param [textFilePath, fileDocument]
     **/
    private void handleDescription(String textFilePath, FileDocument fileDocument) {
        try{
            List<String> stringList = FileUtils.readLines(new File(textFilePath),
                    StandardCharsets.UTF_8);
            String str = null;
            if (!stringList.isEmpty()) {
                str = stringList.get(0);
            }

            if (str == null) {
                str = "无描述";
            } else if (str.length() > 128) {
                str = str.substring(0, 128);
            }
            fileDocument.setDescription(str);
        } catch (IOException e) {
            log.error("读取文件描述失败: {}", textFilePath, e);
        }
    }

    /**
     * 读取文件流，取到其中的文字信息
     *
     * @param is           文件流
     * @param textFilePath 存储文本文件
     * @throws IOException -> IO
     */
    protected abstract void readText(InputStream is, String textFilePath) throws IOException;

    /**
     * 制作缩略图
     *
     * @param is      文件流
     * @param picPath 图片地址
     * @throws IOException -> IOException
     */
    protected abstract void makeThumb(InputStream is, String picPath) throws IOException;

    protected abstract void makePreviewFile(InputStream is, TaskData taskData);

    /**
     * @author luojiarui
     * @Description // 上传整备好的文本文件进行上传到es中
     * @Date 15:11 2022/11/13
     * @Param [searchDocument]
     **/
    public void upload(SearchDocument searchDocument) throws IOException {
        ElasticService elasticService = SpringApplicationContext.getBean(ElasticService.class);
        elasticService.upload(searchDocument);
    }

    /**
     * @author luojiarui
     * @Description // 上传文件的缩略图
     * @Date 17:48 2022/11/13
     * @Param [inputStream, fileDocument]
     **/
    public void updateFileThumb(InputStream inputStream, FileDocument fileDocument,
                                TaskData taskData) throws IOException {

        String picPath = "./" + java.util.UUID.randomUUID().toString() + ".png";
        taskData.setThumbFilePath(picPath);

        // 将pdf输入流转换为图片并临时保存下来
        makeThumb(inputStream, picPath);
        if ( !new File(picPath).exists()) {
            return;
        }

        try (FileInputStream thumbIns = new FileInputStream(picPath)){
            // 存储到GridFS系统中
            DocumentService fileService = SpringApplicationContext.getBean(DocumentService.class);
            String txtObjId = fileService.uploadFileToGridFs(
                    FileFormatEnum.PNG.getFilePrefix(),
                    thumbIns,
                    FileFormatEnum.PNG.getContentType());
            fileDocument.setThumbId(txtObjId);
        } catch (IOException e) {
            throw new TaskRunException("存储缩略图文件报错了，请核对", e);
        }

        try {
            Files.delete(Paths.get(picPath));
        } catch (IOException e) {
            log.error("删除文件路径{} ==> 失败信息{}", picPath, e);
        }

    }

    /**
     * @author luojiarui
     * @Description 某个文件中存储到dfs系统中
     * @date 21:40 2023/2/25
     * @Param [filePath, fileFormatEnum]
     * @return java.lang.String
     **/
    protected String saveFileToDFS(String filePath,
                                   FileFormatEnum fileFormatEnum,
                                   String prefix) {
        String objId;
        try (FileInputStream thumbIns = new FileInputStream(filePath)){
            // 存储到GridFS系统中
            DocumentService fileService = SpringApplicationContext.getBean(DocumentService.class);
            objId = fileService.uploadFileToGridFs(
                    prefix,
                    thumbIns,
                    fileFormatEnum.getContentType());
        } catch (IOException e) {
            throw new TaskRunException("存储缩略图文件报错了，请核对", e);
        }

        try {
            Files.delete(Paths.get(filePath));
        } catch (IOException e) {
            log.error("删除文件路径{} ==> 失败信息{}", filePath, e);
        }
        return objId;
    }
}