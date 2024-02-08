package com.jiaruiblog.task.executor;

import com.jiaruiblog.entity.FileDocument;
import com.jiaruiblog.task.data.TaskData;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;

/**
 * @ClassName PicExecutor
 * @Description jepg, jpg, gif ... to png
 * @Author luojiarui
 * @Date 2023/10/6 23:36
 * @Version 1.0
 **/
public class PicExecutor extends TaskExecutor {

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
        bim = zoomByScale(bim, TARGET_WIDTH, TARGET_HEIGHT);
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

    /**
     * @param width  缩放后的宽
     * @param height 缩放后的高
     * @param img    BufferedImage
     * @return BufferedImage
     * @description 按比例对图片进行缩放. 检测图片是横图还是竖图
     * @author YWR
     * @date 2020/9/17 0:08
     */
    public static BufferedImage zoomByScale(BufferedImage img, int width, int height) {
        //横向图
        if (img.getWidth() >= img.getHeight()) {
            double ratio = calculateZoomRatio(width, img.getWidth());
            // 计算x轴y轴缩放比例--如需等比例缩放，在调用之前确保參数width和height是等比例变化的
            //获取压缩对象
            BufferedImage newBufferedImage = zoomByScale(ratio, ratio, img);
            //当图片大于图片压缩高时 再次缩放
            if (newBufferedImage.getHeight() > height) {
                ratio = calculateZoomRatio(height, newBufferedImage.getHeight());
                newBufferedImage = zoomByScale(ratio, ratio, img);

            }
            return newBufferedImage;
        }
        //纵向图
        double ratio = calculateZoomRatio(height, img.getHeight());
        //获取压缩对象
        BufferedImage newbufferedImage = zoomByScale(ratio, ratio, img);
        //当图片宽大于图片压缩宽时 再次缩放
        if (newbufferedImage.getWidth() > width) {
            ratio = calculateZoomRatio(width, newbufferedImage.getWidth());
            newbufferedImage = zoomByScale(ratio, ratio, img);
        }
        return newbufferedImage;
    }

    /**
     * @param xScale 缩放比率
     * @param yScale 缩放比率
     * @param img    BufferedImage
     * @return BufferedImage
     * @description 按比例对图片进行缩放.
     * @author YWR
     * @date 2020/9/17 0:07
     */
    public static BufferedImage zoomByScale(double xScale, double yScale, BufferedImage img) {
        //设置缩放目标图片模板
        //缩放图片
        AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(xScale, yScale), null);
        return ato.filter(img, null);
    }

    /**
     * @param divisor  除数
     * @param dividend 被除数
     * @return double
     * @description 缩放比率计算
     * @author YWR
     * @date 2020/9/17 0:07
     */
    private static double calculateZoomRatio(int divisor, int dividend) {
        return BigDecimal.valueOf(divisor).divide(BigDecimal.valueOf(dividend), 6, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

}
