package com.jiaruiblog.util.poi;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.xslf.usermodel.*;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * TODO 解决ppt 转 pdf 乱码的问题：https://developer.aliyun.com/article/112289
 * @ClassName PptxToPDFConverter
 * @Description pptx转换为pdf
 * @Author luojiarui
 * @Date 2023/2/22 22:59
 * @Version 1.0
 **/
public class PptxToPDFConverter extends Converter {


    public PptxToPDFConverter(InputStream inStream, OutputStream outStream,
                              boolean showMessages, boolean closeStreamsWhenComplete) {
        super(inStream, outStream, showMessages, closeStreamsWhenComplete);
    }


    private XSLFSlide[] slides;


    @Override
    public void convert() throws Exception {
        Dimension pgSize = processSlides();
        double zoom = 1; // magnify it by 2 as typical slides are low res
        AffineTransform at = new AffineTransform();
        at.setToScale(zoom, zoom);
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, outStream);
        document.open();

        for (int i = 0; i < getNumSlides(); i++) {


            XSLFSlide slide = slides[i];
            // 设置字体, 解决中文乱码
            for (XSLFShape shape : slide.getShapes()) {
                if (shape instanceof XSLFTextShape) {
                    XSLFTextShape textShape = (XSLFTextShape) shape;

                    for (XSLFTextParagraph textParagraph : textShape.getTextParagraphs()) {
                        for (XSLFTextRun textRun : textParagraph.getTextRuns()) {
                            textRun.setFontFamily("宋体");
                        }
                    }
                }

            }

            BufferedImage bufImg = new BufferedImage((int) Math.ceil(pgSize.width * zoom),
                    (int) Math.ceil(pgSize.height * zoom), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = bufImg.createGraphics();
            graphics.setTransform(at);
            //clear the drawing area
            graphics.setPaint(getSlideBGColor(i));
            graphics.fill(new Rectangle2D.Float(0, 0, pgSize.width, pgSize.height));
            try {
                drawOntoThisGraphic(i, graphics);
            } catch (Exception e) {
                //Just ignore, draw what I have
            }

            Image image = Image.getInstance(bufImg, null);
            document.setPageSize(new Rectangle(image.getScaledWidth(), image.getScaledHeight()));
            document.newPage();
            image.setAbsolutePosition(0, 0);
            document.add(image);
        }
        //Seems like I must close document if not output stream is not complete
        document.close();

        //Not sure what repercussions are there for closing a writer but just do it.
        writer.close();
    }

    protected Dimension processSlides() throws IOException {
        InputStream iStream = inStream;
        XMLSlideShow ppt = new XMLSlideShow(iStream);
        Dimension dimension = ppt.getPageSize();
        slides = ppt.getSlides().toArray(new XSLFSlide[0]);
        return dimension;
    }

    protected int getNumSlides() {
        return slides.length;
    }


    protected void drawOntoThisGraphic(int index, Graphics2D graphics) {
        slides[index].draw(graphics);
    }

    protected Color getSlideBGColor(int index) {
        return slides[index].getBackground().getFillColor();
    }


}
