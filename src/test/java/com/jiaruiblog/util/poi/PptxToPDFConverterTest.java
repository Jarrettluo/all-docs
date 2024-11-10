package com.jiaruiblog.util.poi;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;


public class PptxToPDFConverterTest {

    @Test
    public void convert() {
        String inputFilePath = "C:\\project\\java\\test-files\\《草船借箭》ppt课件[1].ppt2.ppt";
        String outputFilePath = "C:\\project\\java\\test-files\\《草船借箭》ppt课件[1]_01.pdf";
        FileInputStream fileInputStream;
        FileOutputStream fileOutputStream;
        try {
            fileInputStream = new FileInputStream(inputFilePath);
            fileOutputStream = new FileOutputStream(outputFilePath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        Converter converter = new PptxToPDFConverter(fileInputStream, fileOutputStream, true,
                true);
        try {
            converter.convert();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}