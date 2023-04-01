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
        // 中文文件依赖 linux 必须有字体包 https://blog.csdn.net/Darling_qi/article/details/120485688
        String inPath = "/Users/molly/Downloads/2022年共青团“学习二十大、永远跟党走、奋进新征程”专题组织生活会.pptx";
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
