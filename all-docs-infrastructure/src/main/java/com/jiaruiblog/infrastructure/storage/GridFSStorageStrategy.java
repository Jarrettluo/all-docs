package com.jiaruiblog.infrastructure.storage;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.ByteArrayOutputStream;

/**
 * GridFS存储策略实现
 *
 * @author luojiarui
 * @version 1.0
 */
@Component
public class GridFSStorageStrategy implements StorageStrategy {

    @Autowired
    private GridFSBucket gridFSBucket;

    @Override
    public String upload(InputStream inputStream, String filename, String contentType) {
        GridFSUploadOptions options = new GridFSUploadOptions()
                .metadata(new Document()
                        .append("contentType", contentType)
                        .append("filename", filename));
        ObjectId fileId = gridFSBucket.uploadFromStream(filename, inputStream, options);
        return fileId.toString();
    }

    @Override
    public InputStream download(String fileId) {
        try (GridFSDownloadStream downloadStream = gridFSBucket.openDownloadStream(new ObjectId(fileId));
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = downloadStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return new java.io.ByteArrayInputStream(outputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean delete(String fileId) {
        try {
            gridFSBucket.delete(new ObjectId(fileId));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String getUrl(String fileId) {
        // GridFS不直接提供URL，需要通过应用服务器代理访问
        return "/file/download/" + fileId;
    }
}