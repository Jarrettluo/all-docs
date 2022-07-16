package com.jiaruiblog.utils;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @ClassName OCRUtil
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/7/16 10:23 下午
 * @Version 1.0
 **/
public class OCRUtil {

    private static final String testResourcesLanguagePath = "src/main/resources/tessdata";

    private static void tess4j() throws TesseractException, IOException {
        ITesseract instance = new Tesseract();
        //如果未将tessdata放在根目录下需要指定绝对路径
        //instance.setDatapath("the absolute path of tessdata");
        instance.setDatapath(testResourcesLanguagePath);

        //如果需要识别英文之外的语种，需要指定识别语种，并且需要将对应的语言包放进项目中
        instance.setLanguage("chi_sim");

        // 指定识别图片
//        File imgDir = new File("/Users/molly/IdeaProjects/document-sharing-site/document-sharing-site/test20220716220233777.png");
        File imgDir = new File("/Users/molly/IdeaProjects/document-sharing-site/document-sharing-site/ocr.png");
        if(imgDir.exists()){
            long startTime = System.currentTimeMillis();
            System.out.println(startTime);
            BufferedImage bufferedImage = ImageIO.read(imgDir);
            String ocrResult = instance.doOCR(bufferedImage);

            // 输出识别结果
            System.out.println("OCR Result: \n" + ocrResult + "\n 耗时：" + (System.currentTimeMillis() - startTime) + "ms");
        } else {
            System.out.println("====");
        }

    }

    public static void main(String[] args) throws TesseractException, IOException {
        tess4j();
    }
}
