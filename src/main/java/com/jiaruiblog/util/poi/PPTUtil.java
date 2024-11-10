package com.jiaruiblog.util.poi;

import org.apache.poi.hslf.usermodel.*;
import org.apache.poi.xslf.usermodel.*;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

/**
 * @author RedRush
 * @date 2022/6/29 17:10
 * @description: ppt转化工具类
 */
public class PPTUtil {

    public static void main(String[] args) {
        PPTUtil pptUtil = new PPTUtil();
        pptUtil.transPPTXToPic();
    }

    /**
     * @Author: RedRush
     * @Date:   2022/6/29 22:32
     * @description: ppt/pptx 转换为图片
     */
    public void transPPTXToPic(){
        String root = "C:\\project\\java\\test-files\\";
        String inPath = "C:\\project\\java\\test-files\\《草船借箭》ppt课件[1].ppt2.ppt";
        String outPath = "C:\\project\\java\\test-files\\《草船借箭》ppt课件[1]_01.pdf";
//        String fileName = "Test.pptx";
        String fileName = "《草船借箭》ppt课件[1].ppt2.ppt";
        try {
            if(fileName.toUpperCase().endsWith(".PPTX")){
                transPPTXToPic(root, fileName);
            }else if(fileName.toUpperCase().endsWith(".PPT")){
                transPPTToPic(root, fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // PPT输出为图片 hslf解析
    private void transPPTToPic(String filePath, String fileName){
        // 生成输出
        String outRoot = filePath + fileName.substring(0, fileName.indexOf('.')) + File.separator;
        System.err.printf("图片输出路径为:%s\n", outRoot);
        // 不存在则创建文件夹
        mkdir(outRoot);
        // ppt读取路径
        String pptName = filePath + fileName;
        System.err.printf("PPT读取路径为:%s\n", pptName);
        FileInputStream fis = null;

        try{
            // 获取系统可用字体
            GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
            String[] fontNames  = e.getAvailableFontFamilyNames();

            // 读取ppt
            fis = new FileInputStream(new File(pptName));
            HSLFSlideShow ppt = new HSLFSlideShow(fis);


            /*
             * 解析PPT基本内容
             * */
            Dimension sheet = ppt.getPageSize();
            int width = sheet.width, height = sheet.height;
            List<HSLFSlide> pages = ppt.getSlides();

            System.err.printf("ppt基本信息: 共%s页,尺寸: %s , %s", pages.size(), width, height);

            BufferedImage img      = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D    graphics = img.createGraphics();
            int i = 1;
            // 逐页遍历
            for(HSLFSlide slide : pages){

                // 清空画板
                graphics.setPaint(Color.white);
                graphics.fill(new Rectangle2D.Float(0, 0, width, height));
                slide.draw(graphics);
                // 输出为图片
                File f = new File(outRoot + i++ + ".png");
                System.out.printf("输出图片：%s\n", f.getAbsolutePath());
                FileOutputStream fos = new FileOutputStream(f);
                javax.imageio.ImageIO.write(img, "PNG", fos);
                fos.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // PPTX输出为图片 xmls包解析
    private void transPPTXToPic(String filePath, String fileName){
        // 生成输出
        String outRoot = filePath + fileName.substring(0, fileName.indexOf('.')) + File.separator;
        System.err.printf("图片输出路径为:%s\n", outRoot);
        // 不存在则创建文件夹
        mkdir(outRoot);
        // ppt读取路径
        String pptName = filePath + fileName;
        System.err.printf("PPT读取路径为:%s\n", pptName);
        FileInputStream fis = null;

        try{
            // 获取系统可用字体
            GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
            String[] fontNames  = e.getAvailableFontFamilyNames();

            // 读取ppt
            fis = new FileInputStream(new File(pptName));
            XMLSlideShow ppt = new XMLSlideShow(fis);


            /*
            * 解析PPT基本内容
            * */
            Dimension sheet = ppt.getPageSize();
            int width = sheet.width, height = sheet.height;
            int count = ppt.getSlides().size();
            System.err.printf("ppt基本信息: 共%s页,尺寸: %s , %s", count, width, height);


            BufferedImage img      = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D    graphics = img.createGraphics();
            int i = 1;
            // 逐页遍历
            for(XSLFSlide shape : ppt.getSlides()){

                // 清空画板
                graphics.setPaint(Color.white);
                graphics.fill(new Rectangle2D.Float(0, 0, width, height));
                shape.draw(graphics);
                // 输出为图片
                File f = new File(outRoot + i++ + ".png");
                System.out.printf("输出图片：%s\n", f.getAbsolutePath());
                FileOutputStream fos = new FileOutputStream(f);
                javax.imageio.ImageIO.write(img, "PNG", fos);
                fos.close();
            }
        }catch (Exception e){
            System.err.println("======ppt转换异常");
            e.printStackTrace();
        }finally {
            if (fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    // 生成文件夹
    private void mkdir(String path){
        File f = new File(path);
        if(!f.exists()){
            f.mkdirs();
        }
    }
}
