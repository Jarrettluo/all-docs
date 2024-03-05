package com.jiaruiblog.util;

import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.io.IOException;

public class MsExcelParseTest {

    public static void main(String[] args) throws IOException, TikaException, SAXException {
        String filePath = "/Users/molly/Downloads/寻找CRMEB开源项目推广大使活动.docx";
        FileInputStream fileInputStream = new FileInputStream(filePath);
        String s = MsExcelParse.parseExcel(fileInputStream);
        System.out.println(s);
    }
}