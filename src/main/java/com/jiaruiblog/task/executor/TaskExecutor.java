package com.jiaruiblog.task.executor;

import cn.hutool.core.util.IdUtil;
import com.jiaruiblog.entity.FileDocument;
import com.jiaruiblog.entity.FileObj;
import com.jiaruiblog.enums.FileFormatEnum;
import com.jiaruiblog.service.IFileService;
import com.jiaruiblog.service.impl.ElasticServiceImpl;
import com.jiaruiblog.task.data.TaskData;
import com.jiaruiblog.task.exception.TaskRunException;
import com.jiaruiblog.util.SpringApplicationContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author Jarrett Luo
 * @Date 2022/10/20 18:23
 * @Version 1.0
 */
@Slf4j
public abstract class TaskExecutor {


    private static final String PDF_SUFFIX = ".pdf";

    public void execute(TaskData taskData) throws TaskRunException {

        // 第一步下载文件，转换为byte数组
        FileDocument fileDocument = taskData.getFileDocument();
        InputStream docInputStream = new ByteArrayInputStream(downFileBytes(fileDocument.getGridfsId()));

        try {
            // 将文本索引到es中
            uploadFileToEs(docInputStream, fileDocument);
        } catch (Exception e) {
            throw new TaskRunException("建立索引的时候出错拉！{}", e);
        }

//        try {
//            // 制作不同分辨率的缩略图
//            updateFileThumb(new FileInputStream("abc"), taskData.getFileDocument());
//        } catch (Exception e) {
//            throw new TaskRunException("建立缩略图的时候出错啦！{}", e);
//        }

    }

    /**
     * @Author luojiarui
     * @Description // 从gridFS 系统中下载文件为字节流
     * @Date 15:02 2022/11/13
     * @Param [gridFsId]
     * @return byte[]
     **/
    protected byte[] downFileBytes(String gridFsId){
        IFileService fileService = SpringApplicationContext.getBean(IFileService.class);
        return fileService.getFileBytes(gridFsId);
    }

    public void uploadFileToEs(InputStream is, FileDocument fileDocument) {
        String textFilePath = "./"+ fileDocument.getMd5() + fileDocument.getName() + ".txt";
        try {
            // 根据不同的执行器，执行不同的文本提取方法，在这里做出区别
            readText(is, textFilePath);
            if (!new File(textFilePath).exists()) {
                throw new TaskRunException("文本文件不存在，需要进行重新提取");
            }
            FileObj fileObj = new FileObj();
            fileObj.setId(fileDocument.getMd5());
            fileObj.setName(fileDocument.getName());
            fileObj.setType(fileDocument.getContentType());
            fileObj.readFile(textFilePath);
//            this.upload(fileObj);

        } catch (IOException | TaskRunException e) {
            e.printStackTrace();
        }

        // 被文本文件上传到gridFS系统中
        try(FileInputStream inputStream = new FileInputStream(textFilePath)) {

            IFileService fileService = SpringApplicationContext.getBean(IFileService.class);
            String txtObjId = fileService.uploadFileToGridFs(FileFormatEnum.TEXT.getFilePrefix(),
                    inputStream, FileFormatEnum.TEXT.getContentType());
            fileDocument.setTextFileId(txtObjId);

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Files.delete(Paths.get(textFilePath));
        }  catch (IOException e) {
            log.error("删除文件路径{} ==> 失败信息{}", textFilePath, e);
        }

    }

    /**
     * 读取文件流，取到其中的文字信息
     * @param is 文件流
     * @param textFilePath 存储文本文件
     * @throws IOException -> IO
     */
    protected abstract void readText(InputStream is, String textFilePath) throws IOException;

    /**
     * 制作缩略图
     * @param is 文件流
     * @param picPath 图片地址
     * @throws IOException -> IOException
     */
    protected abstract void makeThumb(InputStream is, String picPath) throws IOException;

    /**
     * @Author luojiarui
     * @Description // 上传整备好的文本文件进行上传到es中
     * @Date 15:11 2022/11/13
     * @Param [fileObj]
     * @return void
     **/
    public void upload(FileObj fileObj) throws IOException {
        ElasticServiceImpl elasticService = SpringApplicationContext.getBean(ElasticServiceImpl.class);
        elasticService.upload(fileObj);
    }

    private byte[] getContent(File file) throws IOException {

        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            log.error("file too big...");
            return new byte[0];
        }
        try (FileInputStream fi = new FileInputStream(file)) {
            byte[] buffer = new byte[(int) fileSize];
            int offset = 0;
            int numRead = 0;
            while (offset < buffer.length
                    && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
                offset += numRead;
            }
            // 确保所有数据均被读取
            if (offset != buffer.length) {
                throw new IOException("Could not completely read file "
                        + file.getName());
            }
            return buffer;
        } catch (IOException e) {
            log.error("获取文件内容报错", e);
            return new byte[0];
        }

    }


    public void updateFileThumb(InputStream inputStream, FileDocument fileDocument) throws IOException {
        // 新建pdf文件的路径
        String path = "thumbnail";
        String picPath = path + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + ".png";
        String gridfsId = IdUtil.simpleUUID();
        if(PDF_SUFFIX.equals(fileDocument.getSuffix())) {
            // 将pdf输入流转换为图片并临时保存下来
            // PdfUtil.pdfThumbnail(inputStream, picPath);

            makeThumb(inputStream, picPath);

            if(new File(picPath).exists()) {
                String contentType = "image/png";
                FileInputStream in = new FileInputStream(picPath);
                //文件，存储在GridFS
                // gridFsTemplate.store(in, gridfsId, contentType);
                try {
                    Files.delete(Paths.get(picPath));
                }  catch (IOException e) {
                    log.error("删除文件路径{} ==> 失败信息{}", picPath, e);
                }
            }
        }

        Query query = new Query().addCriteria(Criteria.where("_id").is(fileDocument.getId()));
        Update update = new Update().set("thumbId", gridfsId);
        // mongoTemplate.updateFirst(query, update, FileDocument.class, collectionName);

    }

    // /**
    //  * 有三种类型
    //  * 1.文件的名字
    //  * 2.文件type
    //  * 3.文件的data 64编码
    //  */
    // public void upload(FileObj file) throws IOException {
    //     IndexRequest indexRequest = new IndexRequest(INDEX_NAME);
    //     //上传同时，使用attachment pipline进行提取文件
    //     indexRequest.source(JSON.toJSONString(file), XContentType.JSON);
    //     indexRequest.setPipeline("attachment");
    //     client.index(indexRequest, RequestOptions.DEFAULT);
    // }
}
