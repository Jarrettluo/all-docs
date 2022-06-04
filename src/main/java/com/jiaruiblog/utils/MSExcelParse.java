package com.jiaruiblog.utils;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.parser.microsoft.ooxml.OOXMLParser;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @ClassName MSExcelParser
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/6/4 10:04 下午
 * @Version 1.0
 **/
public class MSExcelParse {



    public static void main(final String[] args) throws IOException, TikaException, SAXException {



        //detecting the file type

        BodyContentHandler handler = new BodyContentHandler();

        Metadata metadata = new Metadata();

        FileInputStream inputstream = new FileInputStream(new File("example_msExcel.xlsx"));

        ParseContext pcontext = new ParseContext();



        //OOXml parser

        OOXMLParser  msofficeparser = new OOXMLParser ();

        msofficeparser.parse(inputstream, handler, metadata,pcontext);

        System.out.println("Contents of the document:" + handler.toString());

        System.out.println("Metadata of the document:");

        String[] metadataNames = metadata.names();



        for(String name : metadataNames) {

            System.out.println(name + ": " + metadata.get(name));

        }

    }

}

