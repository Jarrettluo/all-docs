package com.jiaruiblog.util;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.parser.microsoft.ooxml.OOXMLParser;
import org.xml.sax.SAXException;

import java.io.*;

/**
 * @ClassName MSExcelParser
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/6/4 10:04 下午
 * @Version 1.0
 **/
public class MsExcelParse {

    /**
     * @Author luojiarui
     * @Description //TODO 
     * @Date 22:59 2022/8/28
     * @Param [file, textPath]
     * @return void
     **/
    public static void readPdfText(InputStream file, String textPath) throws IOException {
        FileWriter fileWriter = new FileWriter(textPath, true);
        try {
            fileWriter.write(textPath);
            fileWriter.write(parseExcel(file));
        } catch (Exception e) {
            fileWriter.write("error");
        } finally {
            fileWriter.close();
        }
    }

    /**
     * @Author luojiarui
     * @Description //TODO
     * @Date 22:59 2022/8/28
     * @Param [inputStream]
     * @return java.lang.String
     **/
    public static String parseExcel(InputStream inputStream) throws IOException, TikaException, SAXException {

        //detecting the file type

        BodyContentHandler handler = new BodyContentHandler();

        Metadata metadata = new Metadata();

        ParseContext pcontext = new ParseContext();

        //OOXml parser

        OOXMLParser  msofficeparser = new OOXMLParser ();

        msofficeparser.parse(inputStream, handler, metadata,pcontext);

        System.out.println("Contents of the document:" + handler.toString());
        return handler.toString();

    }

    public static String tikaTool(File f){
        Tika tika=new Tika();
        try
        {
            return tika.parseToString(f);
        }
        catch (IOException e)
        {

            e.printStackTrace();
        }
        catch (TikaException e)
        {

            e.printStackTrace();
        }
        return null;
    }


    public static void main(final String[] args) throws IOException, TikaException, SAXException {
        String tempPath = "/Users/molly/Desktop/test.pdf";
//        String path = tempPath + File.separator + "AD here部署要求的副本.docx";
////        parseExcel(path);
//        FileInputStream fileInputStream = new FileInputStream(path);
//        readPDFText(fileInputStream, "xxx.txt");

        String path = tempPath + File.separator + "WechatIMG26的副本.jpeg";
        System.out.println(tikaTool(new File(path)));


    }

}

