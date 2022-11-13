package com.jiaruiblog.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName PDFUtil
 * @Description pdf 准换工具
 * @Author luojiarui
 * @Date 2022/7/13 7:55 下午
 * @Version 1.0
 **/
@Slf4j
public class PdfUtil {

    public static void readPdfText(String path) throws IOException {
        File file = new File(path);
        InputStream is = new FileInputStream(file);
        readPdfText(is, "xxx.txt");
    }

    public static void readPdfText(InputStream file, String textPath) throws IOException {
        if (file == null) {
            log.info("inputstream is null");
            return;
        }
        try (PDDocument document = PDDocument.load(file)) {
            AccessPermission ap = document.getCurrentAccessPermission();
            if (!ap.canExtractContent()) {
                throw new IOException("You do not have permission to extract text");
            }

            PDFTextStripper stripper = new PDFTextStripper();

            stripper.setSortByPosition(true);

            FileWriter fileWriter = new FileWriter(textPath, true);
            System.out.println("==f==fdsfdslfj");
            for (int p = 1; p <= document.getNumberOfPages(); ++p) {
                stripper.setStartPage(p);
                stripper.setEndPage(p);
                String text = stripper.getText(document);
                text = text.replace("\n", "");
                System.out.println(text);
                fileWriter.write(text.trim());
            }
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("解析pdf文本文件出错", e);
        }
    }

    /**
     * @return void
     * @Author luojiarui
     * @Description // 保存文本为某个文件
     * @Date 8:01 下午 2022/7/13
     * @Param [text, path]
     **/
    public static void saveTxt(String text, String path) {
        FileWriter writer;
        try {
            // 将文本写入文本文件
            writer = new FileWriter(String.valueOf(path));
            writer.write(text);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void handlePdf(InputStream inputStream, String path) throws IOException {
        long startTime = System.currentTimeMillis();
        readPdfText(inputStream, "加油.txt");
        long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);
    }

    /**
     * @return void
     * @Author luojiarui
     * @Description //TODO
     * @Date 10:01 下午 2022/7/16
     * @Param [pdfPath]
     **/
    public static void transPdf2png(String pdfPath) {
        try {
            //根据pdf文件路径取得pdf文件
            File invoiceFile = new File(pdfPath);
            // 新建pdf文件的路径
            String path = "test";
            PDDocument doc = PDDocument.load(invoiceFile);
            PDFRenderer renderer = new PDFRenderer(doc);
            int pageCount = doc.getNumberOfPages();
            for (int i = 0; i < pageCount; i++) {
                // 第二个参数是设置缩放比(即像素)
                BufferedImage image = renderer.renderImage(i, 2.5f);
                ImageIO.write(image, "PNG", new File(path + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + ".png"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @return void
     * @Author luojiarui
     * @Description //根据文件路径保存pdf的缩略图
     * @Date 7:21 下午 2022/7/24
     * @Param [pdfPath]
     **/
    public static void pdfThumbnail(String pdfPath) throws FileNotFoundException {
        pdfThumbnail(new FileInputStream(pdfPath));
    }

    /**
     * @return void
     * @Author luojiarui
     * @Description //根据文件输入流保存pdf缩略图
     * @Date 7:21 下午 2022/7/24
     * @Param [inputStream]
     **/
    public static void pdfThumbnail(InputStream inputStream) {
        // 新建pdf文件的路径
        String path = "thumbnail";
        String picPath = path + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + ".png";
        pdfThumbnail(inputStream, picPath);
    }

    /**
     * @return void
     * @Author luojiarui
     * @Description //根据文件输入流和图片地址保存缩略图
     * @Date 7:22 下午 2022/7/24
     * @Param [inputStream, picPath]
     **/
    public static void pdfThumbnail(InputStream inputStream, String picPath) {
        try {
            PDDocument doc = PDDocument.load(inputStream);
            PDFRenderer renderer = new PDFRenderer(doc);
            // 第二个参数是设置缩放比(即像素)
            BufferedImage image = renderer.renderImage(0, 2.0f);
            ImageIO.write(image, "PNG", new File(picPath));
            doc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException {
//        String filePath = "/Users/molly/Desktop/test.pdf/测试专用.pdf";
////        readPDFText(filePath);
//        File file = new File(filePath);
//        InputStream is = new FileInputStream(file);
//
//        String tempPath = "xxxxxx.txt";
//        handlePDF(is, tempPath);

        String pdfPath = "/Users/molly/Desktop/test.pdf/习近平在厦门的副本.pdf";
//        transPDF2png(pdfPath);

        pdfThumbnail(pdfPath);

    }


}
