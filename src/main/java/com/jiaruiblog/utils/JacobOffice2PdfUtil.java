package com.jiaruiblog.utils;

import java.io.File;
import java.util.Date;

import org.apache.log4j.Logger;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;


/**
 * @ClassName JacobOffice2PdfUtil
 * @Description office文件转换为PDF文件
 * @Author luojiarui
 * @Date 2022/8/22 22:03
 * @Version 1.0
 **/


public class JacobOffice2PdfUtil {

    private String office_path = "C:/officeconvert/officefile/";
    private String pdf_path = "C:/officeconvert/pdffile/";

    private static final int wdFormatPDF = 17;
    private static final int xlTypePDF = 0;
    private static final int ppSaveAsPDF = 32;

    private static Logger logger = Logger.getLogger(JacobOffice2PdfUtil.class);

    /**
     * 转换office文件为PDF
     *
     * @param inputFileName
     * @return
     */
    public boolean office2pdf(String inputFileName, String username) {
        String officeUserPath = String.format(office_path + "/%s/%s", username, inputFileName);
        inputFileName = inputFileName.substring(0, inputFileName.lastIndexOf("."));
        String pdfUserPath = String.format(pdf_path + "/%s/%s", username, inputFileName + ".pdf");

        int time = convert2PDF(officeUserPath, pdfUserPath);
        boolean result = false;
        if (time == -4) {
            logger.info("转化失败，未知错误...");
        } else if (time == -3) {
            result = true;
            logger.info("原文件就是PDF文件,无需转化...");
        } else if (time == -2) {
            logger.info("转化失败，文件不存在...");
        } else if (time == -1) {
            logger.info("转化失败，请重新尝试...");
        } else if (time < -4) {
            logger.info("转化失败，请重新尝试...");
        } else {
            result = true;
            logger.info("转化成功，用时：  " + time + "s...");
        }
        return result;
    }

    /***
     * 判断需要转化文件的类型（Excel、Word、ppt）
     *
     * @param inputFile
     * @param pdfFile
     */
    private int convert2PDF(String inputFile, String pdfFile) {
        String kind = getFileSufix(inputFile);
        File file = new File(inputFile);
        if (!file.exists()) {
            logger.info("文件不存在");
            return -2;
        }
        if (kind.equals("pdf")) {
            logger.info("原文件就是PDF文件");
            return -3;
        }
        if (kind.equals("doc") || kind.equals("docx") || kind.equals("txt")) {
            return word2PDF(inputFile, pdfFile);
        } else if (kind.equals("ppt") || kind.equals("pptx")) {
            return ppt2PDF(inputFile, pdfFile);
        } else if (kind.equals("xls") || kind.equals("xlsx")) {
            return Ex2PDF(inputFile, pdfFile);
        } else {
            return -4;
        }
    }

    /***
     * 判断文件类型
     *
     * @param fileName
     * @return
     */
    public static String getFileSufix(String fileName) {
        int splitIndex = fileName.lastIndexOf(".");
        return fileName.substring(splitIndex + 1);
    }

    /***
     *
     * Word转PDF
     *
     * @param inputFile
     * @param pdfFile
     * @return
     */

    private static int word2PDF(String inputFile, String pdfFile) {
        try {
            // 打开Word应用程序
            ActiveXComponent app = new ActiveXComponent("KWPS.Application");
            logger.info("开始转化Word为PDF...");
            long date = new Date().getTime();
            // 设置Word不可见
            app.setProperty("Visible", new Variant(false));
            // 禁用宏
            app.setProperty("AutomationSecurity", new Variant(3));
            // 获得Word中所有打开的文档，返回documents对象
            Dispatch docs = app.getProperty("Documents").toDispatch();
            // 调用Documents对象中Open方法打开文档，并返回打开的文档对象Document
            Dispatch doc = Dispatch.call(docs, "Open", inputFile, false, true).toDispatch();
            /***
             *
             * 调用Document对象的SaveAs方法，将文档保存为pdf格式
             *
             * Dispatch.call(doc, "SaveAs", pdfFile, wdFormatPDF word保存为pdf格式宏，值为17 )
             *
             */
            Dispatch.call(doc, "ExportAsFixedFormat", pdfFile, wdFormatPDF);// word保存为pdf格式宏，值为17
            logger.info(doc);
            // 关闭文档
            long date2 = new Date().getTime();
            int time = (int) ((date2 - date) / 1000);

            Dispatch.call(doc, "Close", false);
            // 关闭Word应用程序
            app.invoke("Quit", 0);
            return time;
        } catch (Exception e) {
            // TODO: handle exception
            return -1;
        }

    }

    /***
     *
     * Excel转化成PDF
     *
     * @param inputFile
     * @param pdfFile
     * @return
     */
    private int Ex2PDF(String inputFile, String pdfFile) {
        try {
            ComThread.InitSTA(true);
            ActiveXComponent ax = new ActiveXComponent("KET.Application");
            logger.info("开始转化Excel为PDF...");
            long date = new Date().getTime();
            ax.setProperty("Visible", false);
            ax.setProperty("AutomationSecurity", new Variant(3)); // 禁用宏
            Dispatch excels = ax.getProperty("Workbooks").toDispatch();

            Dispatch excel = Dispatch
                    .invoke(excels, "Open", Dispatch.Method,
                            new Object[] { inputFile, new Variant(false), new Variant(false) }, new int[9])
                    .toDispatch();
            // 转换格式
            Dispatch.invoke(excel, "ExportAsFixedFormat", Dispatch.Method, new Object[] { new Variant(0), // PDF格式=0
                    pdfFile, new Variant(xlTypePDF) // 0=标准 (生成的PDF图片不会变模糊) 1=最小文件
                    // (生成的PDF图片糊的一塌糊涂)
            }, new int[1]);

            // 这里放弃使用SaveAs
            /*
             * Dispatch.invoke(excel,"SaveAs",Dispatch.Method,new Object[]{ outFile, new
             * Variant(57), new Variant(false), new Variant(57), new Variant(57), new
             * Variant(false), new Variant(true), new Variant(57), new Variant(true), new
             * Variant(true), new Variant(true) },new int[1]);
             */
            long date2 = new Date().getTime();
            int time = (int) ((date2 - date) / 1000);
            Dispatch.call(excel, "Close", new Variant(false));

            if (ax != null) {
                ax.invoke("Quit", new Variant[] {});
                ax = null;
            }
            ComThread.Release();
            return time;
        } catch (Exception e) {
            // TODO: handle exception
            return -1;
        }
    }

    /***
     * ppt转化成PDF
     *
     * @param inputFile
     * @param pdfFile
     * @return
     */
    private int ppt2PDF(String inputFile, String pdfFile) {
        logger.info("开始转化PPT为PDF...");
        try {
            ComThread.InitSTA(true);
            ActiveXComponent app = new ActiveXComponent("KWPP.Application");
//            app.setProperty("Visible", false);
            long date = new Date().getTime();
            Dispatch ppts = app.getProperty("Presentations").toDispatch();
            Dispatch ppt = Dispatch.call(ppts, "Open", inputFile, true, // ReadOnly
                    // false, // Untitled指定文件是否有标题
                    false// WithWindow指定文件是否可见
            ).toDispatch();
            Dispatch.invoke(ppt, "SaveAs", Dispatch.Method, new Object[] { pdfFile, new Variant(ppSaveAsPDF) },
                    new int[1]);
            logger.info("PPT");
            Dispatch.call(ppt, "Close");
            long date2 = new Date().getTime();
            int time = (int) ((date2 - date) / 1000);
            app.invoke("Quit");
            return time;
        } catch (Exception e) {
            logger.info(e);
            // TODO: handle exception
            return -1;
        }
    }



    // 删除多余的页，并转换为PDF
    public static void interceptPPT(String inputFile, String pdfFile) {
        ActiveXComponent app = null;
        try {
            ComThread.InitSTA(true);
            app = new ActiveXComponent("KWPP.Application");
            ActiveXComponent presentations = app.getPropertyAsComponent("Presentations");
            ActiveXComponent presentation = presentations.invokeGetComponent("Open", new Variant(inputFile),
                    new Variant(false));
            int count = Dispatch.get(presentations, "Count").getInt();
            System.out.println("打开文档数:" + count);
            ActiveXComponent slides = presentation.getPropertyAsComponent("Slides");
            int slidePages = Dispatch.get(slides, "Count").getInt();
            System.out.println("ppt幻灯片总页数:" + slidePages);

            // 总页数的20%取整+1 最多不超过5页
            int target = (int) (slidePages * 0.5) + 1 > 5 ? 5 : (int) (slidePages * 0.5) + 1;
            // 删除指定页数
            while (slidePages > target) {
                // 选中指定页幻灯片
                Dispatch slide = Dispatch.call(presentation, "Slides", slidePages).toDispatch();
                Dispatch.call(slide, "Select");
                Dispatch.call(slide, "Delete");
                slidePages--;
                System.out.println("当前ppt总页数:" + slidePages);
            }
            Dispatch.invoke(presentation, "SaveAs", Dispatch.Method, new Object[] { pdfFile, new Variant(32) },
                    new int[1]);
            Dispatch.call(presentation, "Save");
            Dispatch.call(presentation, "Close");
            presentation = null;
            app.invoke("Quit");
            app = null;
            ComThread.Release();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public static void main(String[] args) {
        String word = "C:\\Users\\Administrator\\Desktop\\abc.docx";
        String pdf = "C:\\Users\\Administrator\\Desktop\\abc.pdf";
        word2PDF(word,pdf);
    }
}

