package com.jiaruiblog.util.poi;

import java.io.*;

/**
 * @ClassName MainClass
 * @Description 参考自：https://github.com/yeokm1/docs-to-pdf-converter
 * @Author luojiarui
 * @Date 2023/2/22 23:04
 * @Version 1.0
 **/
public class MainClass {

    public static void main(String[] args) throws Exception {

        String inPath = "/Users/molly/Downloads/《如何与领导达成共识》-孙淑静.pptx";
        String outPath = "/Users/molly/Downloads/1x44x6.pdf";
        InputStream inStream = getInFileStream(inPath);
        OutputStream outStream = getOutFileStream(outPath);

        boolean shouldShowMessages = true;

        Converter converter = new PptxToPDFConverter(inStream, outStream, shouldShowMessages,
                true);
        converter.convert();
    }

    protected static InputStream getInFileStream(String inputFilePath) throws FileNotFoundException {
        File inFile = new File(inputFilePath);
        FileInputStream iStream = new FileInputStream(inFile);
        return iStream;
    }

    protected static OutputStream getOutFileStream(String outputFilePath) throws IOException {
        File outFile = new File(outputFilePath);

        try{
            //Make all directories up to specified
            outFile.getParentFile().mkdirs();
        } catch (NullPointerException e){
            //Ignore error since it means not parent directories
        }

        outFile.createNewFile();
        FileOutputStream oStream = new FileOutputStream(outFile);
        return oStream;
    }

}
