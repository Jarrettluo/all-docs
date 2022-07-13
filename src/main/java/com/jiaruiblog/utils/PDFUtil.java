package com.jiaruiblog.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.*;
import java.nio.file.Path;

/**
 * @ClassName PDFUtil
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/7/13 7:55 下午
 * @Version 1.0
 **/
public class PDFUtil {

    public static void readPDFText(String path) throws IOException {
        File file = new File(path);
        InputStream is = new FileInputStream(file);
        readPDFText(is, "xxx.txt");
    }

    public static void readPDFText(InputStream file, String textPath) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (PDDocument document = PDDocument.load(file)) {
            AccessPermission ap = document.getCurrentAccessPermission();
            if (!ap.canExtractContent()) {
                throw new IOException("You do not have permission to extract text");
            }

            PDFTextStripper stripper = new PDFTextStripper();

            stripper.setSortByPosition(true);

            FileWriter fileWriter = new FileWriter(textPath, true);
            for (int p = 1; p <= document.getNumberOfPages(); ++p) {
                stripper.setStartPage(p);
                stripper.setEndPage(p);
                String text = stripper.getText(document);
                text = text.replace("\n","");
                fileWriter.write(text.trim());
            }
            fileWriter.close();
        }
    }

    /**
     * @Author luojiarui
     * @Description // 保存文本为某个文件
     * @Date 8:01 下午 2022/7/13
     * @Param [text, path]
     * @return void
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

    public static void handlePDF(InputStream inputStream, String path) throws IOException {
        long startTime = System.currentTimeMillis();
        readPDFText(inputStream, "加油.txt");
        long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);
    }

    public static void main(String[] args) throws IOException {
        String filePath = "/Users/molly/Desktop/test.pdf/测试专用.pdf";
//        readPDFText(filePath);
        File file = new File(filePath);
        InputStream is = new FileInputStream(file);

        String tempPath = "xxxxxx.txt";
        handlePDF(is, tempPath);

    }


}
