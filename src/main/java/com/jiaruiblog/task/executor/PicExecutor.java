package com.jiaruiblog.task.executor;

import com.jiaruiblog.entity.FileDocument;
import com.jiaruiblog.task.data.TaskData;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @ClassName PicExecutor
 * @Description jepg, jpg, gif ... to png
 * @Author luojiarui
 * @Date 2023/10/6 23:36
 * @Version 1.0
 **/
public class PicExecutor extends TaskExecutor{

    public static final String PNG = "png";
    public static final Integer TARGET_WIDTH = 120;
    public static final Integer TARGET_HEIGHT = 200;

    @Override
    public void uploadFileToEs(InputStream is, FileDocument fileDocument, TaskData taskData) {
        // do nothing for pic file
    }

    @Override
    protected void readText(InputStream is, String textFilePath) throws IOException {
        // do nothing for pic
        // read text from pic by orc etc.
    }

    @Override
    protected void makeThumb(InputStream is, String picPath) throws IOException {
        BufferedImage bim = ImageIO.read(is);
        bim = resizeImage(bim, TARGET_WIDTH, TARGET_HEIGHT);
        File output = new File(picPath);
        ImageIO.write(bim, PNG, output);
    }

    @Override
    protected void makePreviewFile(InputStream is, TaskData taskData) {
        // do nothing for pic
    }


    /**
     * 通过BufferedImage图片流调整图片大小
     * 指定压缩后长宽
     */
    public static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_AREA_AVERAGING);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
        return outputImage;
    }

}
