package com.jiaruiblog.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

/**
 * @program: transformation
 * @description: 多张图片合成
 * @author: cuixy
 * @create: 2019-07-26 17:10
 **/
public class MergeImage {

    /**
     * 合并任数量的图片成一张图片
     *
     * @param isHorizontal
     *            true代表水平合并，fasle代表垂直合并
     * @param imgs
     *            待合并的图片数组
     * @return
     * @throws IOException
     */
    public static BufferedImage mergeImage(boolean isHorizontal, List<BufferedImage> imgs) throws IOException {
        // 生成新图片
        BufferedImage destImage = null;
        // 计算新图片的长和高
        int allw = 0, allh = 0, allwMax = 0, allhMax = 0;
        // 获取总长、总宽、最长、最宽
        for (int i = 0; i < imgs.size(); i++) {
            BufferedImage img = imgs.get(i);
            allw += img.getWidth();

            if (imgs.size() != i + 1) {
                allh += img.getHeight() + 2;
            } else {
                allh += img.getHeight();
            }

            if (img.getWidth() > allwMax) {
                allwMax = img.getWidth();
            }

            if (img.getHeight() > allhMax) {
                allhMax = img.getHeight();
            }
        }

        // 创建新图片
        if (isHorizontal) {
            destImage = new BufferedImage(allw, allhMax, BufferedImage.TYPE_INT_RGB);
        } else {
            destImage = new BufferedImage(allwMax, allh, BufferedImage.TYPE_INT_RGB);
        }

        // 注释，分隔线从灰色变成纯黑
        // Graphics2D g2 = (Graphics2D) destImage.getGraphics();
        // g2.setBackground(Color.LIGHT_GRAY);
        // g2.clearRect(0, 0, allw, allh);
        // g2.setPaint(Color.RED);

        // 合并所有子图片到新图片
        int wx = 0, wy = 0;
        for (int i = 0; i < imgs.size(); i++) {
            BufferedImage img = imgs.get(i);
            int w1 = img.getWidth();
            int h1 = img.getHeight();
            // 从图片中读取RGB
            int[] ImageArrayOne = new int[w1 * h1];
            ImageArrayOne = img.getRGB(0, 0, w1, h1, ImageArrayOne, 0, w1); // 逐行扫描图像中各个像素的RGB到数组中
            if (isHorizontal) { // 水平方向合并
                destImage.setRGB(wx, 0, w1, h1, ImageArrayOne, 0, w1); // 设置上半部分或左半部分的RGB
            } else { // 垂直方向合并
                destImage.setRGB(0, wy, w1, h1, ImageArrayOne, 0, w1); // 设置上半部分或左半部分的RGB
            }

            wx += w1;
            wy += h1 + 2;
        }
        return destImage;
    }



}
