package com.jiaruiblog.task.executor;

import cn.hutool.core.util.IdUtil;
import com.jiaruiblog.entity.FileDocument;
import com.jiaruiblog.entity.FileObj;
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
import java.util.Base64;
import java.util.Date;

/**
 * @Author Jarrett Luo
 * @Date 2022/10/20 18:23
 * @Version 1.0
 */
@Slf4j
public abstract class TaskExecutor {

    protected String downloadFile() {
        return "1";
    }

    private static final String PDF_SUFFIX = ".pdf";

    public void execute(TaskData taskData) throws TaskRunException {
        try {
            // 将文本索引到es中
            uploadFileToEs(new FileInputStream("abc"), taskData.getFileDocument());
        } catch (Exception e) {
            throw new TaskRunException("建立索引的时候出错拉！{}", e);
        }

        try {

            // 制作不同分辨率的缩略图
            updateFileThumb(new FileInputStream("abc"), taskData.getFileDocument());
        } catch (Exception e) {
            throw new TaskRunException("建立缩略图的时候出错啦！{}", e);
        }

    }

    public void uploadFileToEs(InputStream is, FileDocument fileDocument) {

        String textFilePath = fileDocument.getMd5() + fileDocument.getName() + ".txt";

        try {
            // TODO 就是在这里做出区别
            readText(is, textFilePath);
            FileObj fileObj = readFile(textFilePath);
            fileObj.setId(fileDocument.getMd5());
            fileObj.setName(fileDocument.getName());
            fileObj.setType(fileDocument.getContentType());
            this.upload(fileObj);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 删除临时的txt文件
            File file = new File(textFilePath);
            if(file.exists()) {
                file.deleteOnExit();
            }
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


    public FileObj readFile(String path) throws IOException {

        //读文件
        File file = new File(path);
        FileObj fileObj = new FileObj();

        fileObj.setName(file.getName());
        fileObj.setType(file.getName().substring(file.getName().lastIndexOf(".") + 1));

        byte[] bytes = getContent(file);

        //将文件内容转化为base64编码
        String base64 = Base64.getEncoder().encodeToString(bytes);
        fileObj.setContent(base64);

        return fileObj;
    }




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
