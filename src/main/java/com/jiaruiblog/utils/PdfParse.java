package com.jiaruiblog.utils;

import java.io.File;

import java.io.FileInputStream;

import java.io.IOException;



import org.apache.tika.exception.TikaException;

import org.apache.tika.metadata.Metadata;

import org.apache.tika.parser.ParseContext;

import org.apache.tika.parser.pdf.PDFParser;

import org.apache.tika.sax.BodyContentHandler;



import org.xml.sax.SAXException;




/**
 * @ClassName JavaParser
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/6/4 10:02 下午
 * @Version 1.0
 **/
public class PdfParse {

//    private final static String filePath = "/Users/molly/Downloads/软件开发云服务白皮书.pdf";

    private final static String filePath = "/Users/molly/Desktop/test.pdf/习近平在厦门的副本.pdf";

    public static void main(final String[] args) throws IOException, TikaException, SAXException {


        BodyContentHandler handler = new BodyContentHandler();

        Metadata metadata = new Metadata();

        FileInputStream inputstream = new FileInputStream(new File(filePath));

        ParseContext pcontext = new ParseContext();



        //parsing the document using PDF parser

        PDFParser pdfparser = new PDFParser();

        pdfparser.parse(inputstream, handler, metadata,pcontext);



        //getting the content of the document

        System.out.println("Contents of the PDF :" + handler.toString());



        //getting metadata of the document

        System.out.println("Metadata of the PDF:");

        String[] metadataNames = metadata.names();



        for(String name : metadataNames) {

            System.out.println(name+ " : " + metadata.get(name));

        }

    }

}
