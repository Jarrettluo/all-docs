package com.jiaruiblog.util.word;


import com.aspose.words.Document;
import com.aspose.words.ImageSaveOptions;
import com.aspose.words.SaveFormat;
import com.jiaruiblog.util.CompactAlgorithm;
import com.jiaruiblog.util.MergeImage;
import org.apache.pdfbox.pdmodel.PDPage;


import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * word文档（doc，docx）转换pdf/图片，
 * @ClassName WordUtil
 * @Description 图片转换base64用于前端在线预览
 * @Author luojiarui
 * @Date 2022/10/26 22:50
 * @Version 1.0
 **/
public class WordUtil {


    /**
     * word 转每页pdf
     * @param wordfile
     * @return
     * @throws Exception
     */
    public static String parseFileToBase64_PNG1(String wordfile) throws Exception {

        if (!isWordLicense()) {
            return null;
        }

        // 声明一个
        InputStream inputStream = new FileInputStream(wordfile);



        //文件 获取文件名字
        File file = new File(wordfile);
        String name = file.getName();
        //截取不带后缀名的字段
        String fileName = name.substring(0, name.lastIndexOf("."));
        //文件上传路径
        String parent = file.getParent();

        //创建同名文件夹
        new File(parent+"/"+ fileName).mkdir();

        List<BufferedImage> bufferedImages = wordToImg1(inputStream);

        for (int i = 0; i < bufferedImages.size(); i++){
            ImageIO.write(bufferedImages.get(i), "png", new File(parent +"/"+ fileName +"/"+ "第"+ i +"页" + fileName + ".png"));
        }


        //压缩同名文件夹
        File f = new File(parent  +"/" + fileName );
        new CompactAlgorithm(new File( parent+ "/",f.getName()+".zip")).zipFiles(f);

        //关闭流
        inputStream.close();


        return "转换成功";
    }

    /**
     * @Description: word和txt文件转换图片
     */
    private static List<BufferedImage> wordToImg1(InputStream inputStream) throws Exception {
        if (!isWordLicense()) {
            return null;
        }

        try {
            Document doc = new Document(inputStream);

            ImageSaveOptions options = new ImageSaveOptions(SaveFormat.PNG);
            options.setPrettyFormat(true);
            options.setUseAntiAliasing(true);
            options.setUseHighQualityRendering(true);
            int pageCount = doc.getPageCount();

            List<BufferedImage> imageList = new ArrayList<BufferedImage>();
            for (int i =  0; i < pageCount; i++) {
                OutputStream output = new ByteArrayOutputStream();
                options.setPageIndex(i);

                doc.save(output, options);

                ImageInputStream imageInputStream = ImageIO.createImageInputStream(parse(output));

                imageList.add(ImageIO.read(imageInputStream));
            }
            return imageList;

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }



    // 将word 转化为图片一张
    public static String parseFileToBase64_PNG(String wordfile) throws Exception {

        //文件流
        InputStream inputStream = new FileInputStream(wordfile);
        //文件 获取文件名字
        File file = new File(wordfile);
        String name = file.getName();
        //截取不带后缀名的字段
        String fileName = name.substring(0, name.lastIndexOf("."));

        //文件上传路径
        String parent = file.getParent();

        List<BufferedImage> bufferedImages = new ArrayList<BufferedImage>();
        BufferedImage image = null;
        bufferedImages = wordToImg(inputStream);
        image = MergeImage.mergeImage(false, bufferedImages);

        boolean png = ImageIO.write(image, "png", new File(parent + "/" + fileName + ".png"));// 写入流中


        if(png == false){
            return "转换失败";
        }

        //关闭流
        inputStream.close();
        return "转换成功";
    }

    /**
     * @Description: 验证aspose.word组件是否授权：无授权的文件有水印和试用标记
     */
    public static boolean isWordLicense() {
        boolean result = false;
        try {
            // InputStream inputStream = new
            // FileInputStream("D:\\Workspaces\\TestFilters\\lib\\license.xml");
            // 避免文件遗漏
            String licensexml = "<License>\n" + "<Data>\n" + "<Products>\n"
                    + "<Product>Aspose.Total for Java</Product>\n" + "<Product>Aspose.Words for Java</Product>\n"
                    + "</Products>\n" + "<EditionType>Enterprise</EditionType>\n"
                    + "<SubscriptionExpiry>20991231</SubscriptionExpiry>\n"
                    + "<LicenseExpiry>20991231</LicenseExpiry>\n"
                    + "<SerialNumber>23dcc79f-44ec-4a23-be3a-03c1632404e9</SerialNumber>\n" + "</Data>\n"
                    + "<Signature>\n"
                    + "sNLLKGMUdF0r8O1kKilWAGdgfs2BvJb/2Xp8p5iuDVfZXmhppo+d0Ran1P9TKdjV4ABwAgKXxJ3jcQTqE/2IRfqwnPf8itN8aFZlV3TJPYeD3yWE7IT55Gz6EijUpC7aKeoohTb4w2fpox58wWoF3SNp6sK6jDfiAUGEHYJ9pjU=\n"
                    + "</Signature>\n" + "</License>";
            InputStream inputStream = new ByteArrayInputStream(licensexml.getBytes());
            com.aspose.words.License license = new com.aspose.words.License();
            license.setLicense(inputStream);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @Description: word和txt文件转换图片
     */
    private static List<BufferedImage> wordToImg(InputStream inputStream) throws Exception {
        if (!isWordLicense()) {
            return null;
        }

        try {

            Document doc = new Document(inputStream);
            ImageSaveOptions options = new ImageSaveOptions(SaveFormat.PNG);
            options.setPrettyFormat(true);
            options.setUseAntiAliasing(true);
            options.setUseHighQualityRendering(true);
            int pageCount = doc.getPageCount();

            List<BufferedImage> imageList = new ArrayList<BufferedImage>();
            for (int i = 0; i < pageCount; i++) {
                OutputStream output = new ByteArrayOutputStream();
                options.setPageIndex(i);

                doc.save(output, options);
                ImageInputStream imageInputStream = ImageIO.createImageInputStream(parse(output));
                imageList.add(ImageIO.read(imageInputStream));
            }
            return imageList;

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }



    // outputStream转inputStream
    public static ByteArrayInputStream parse(OutputStream out) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos = (ByteArrayOutputStream) out;
        ByteArrayInputStream swapStream = new ByteArrayInputStream(baos.toByteArray());
        return swapStream;
    }
}