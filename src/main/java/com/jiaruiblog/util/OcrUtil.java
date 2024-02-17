package com.jiaruiblog.util;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;
import java.io.IOException;

/**
 * 如果未将tessdata放在根目录下需要指定绝对路径
 * instance.setDatapath("the absolute path of tessdata");
 * 对应的动态连接库下载地址：
 * Downloads are available here https://github.com/ollydev/libtesseract/releases
 * @ClassName OCRUtil
 * @Description orc 离线识别
 * @Author luojiarui
 * @Date 2022/7/16 10:23 下午
 * @Version 1.0
 **/
public class OcrUtil {

    private static final String TEST_RESOURCES_LANGUAGE_PATH = "src/main/resources/tessdata";

    private static void tess4j() throws TesseractException, IOException {


        ITesseract instance = new Tesseract();

        instance.setDatapath(TEST_RESOURCES_LANGUAGE_PATH);

        //如果需要识别英文之外的语种，需要指定识别语种，并且需要将对应的语言包放进项目中
        instance.setLanguage("chi_sim");

        // 指定识别图片
//        File imgDir = new File("/Users/molly/IdeaProjects/document-sharing-site/document-sharing-site/test20220716220233777.png");
        File imgDir = new File("/Users/molly/IdeaProjects/document-sharing-site/document-sharing-site/ocr.png");
        if(imgDir.exists()){
//            long startTime = System.currentTimeMillis();
//            BufferedImage bufferedImage = ImageIO.read(imgDir);
//            String ocrResult = instance.doOCR(bufferedImage);


            String result = instance.doOCR(imgDir);
            System.out.println(result);

            // 输出识别结果
//            System.out.println("OCR Result: \n" + ocrResult + "\n 耗时：" + (System.currentTimeMillis() - startTime) + "ms");
        } else {
            System.out.println("====");
        }

    }

    public static void xx() {
        try {
            // TODO 该种方法只支持在windows下进行，而linux下需要so的动态连接库
            // https://blog.csdn.net/qq_62357662/article/details/132430352
            //获取本地图片
            File file = new File("D:\\测试\\测试.png");
            //创建Tesseract对象
            ITesseract tesseract = new Tesseract();
            //设置字体库路径 （你的chi_sim.traineddata放的个文件夹
            tesseract.setDatapath("D:\\Tess4J\\tessdata");
            //中文识别 chi_sim.traineddata的前缀
            tesseract.setLanguage("chi_sim");
            //执行ocr识别
            String result = tesseract.doOCR(file);
            //替换回车和tal键  使结果为一行
            result = result.replaceAll("\\r|\\n","-").replaceAll(" ","");
            System.out.println("识别的结果为："+result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws TesseractException, IOException {
        tess4j();
    }
}
