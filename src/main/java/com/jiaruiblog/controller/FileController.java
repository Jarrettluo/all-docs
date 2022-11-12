package com.jiaruiblog.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.google.common.collect.Lists;
import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.entity.FileDocument;
import com.jiaruiblog.entity.ResponseModel;
import com.jiaruiblog.service.ElasticService;
import com.jiaruiblog.service.IFileService;
import com.jiaruiblog.service.TaskExecuteService;
import com.jiaruiblog.util.FileContentTypeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author jiarui.luo
 */
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("files")
public class FileController {

    private static final String DOT = ".";

    @Autowired
    private IFileService fileService;

    @Autowired
    private ElasticService elasticService;

    @Autowired
    private TaskExecuteService taskExecuteService;


    /**
     * 列表数据
     *
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @RequestMapping("/list")
    public List<FileDocument> list(int pageIndex, int pageSize) {
        return fileService.listFilesByPage(pageIndex, pageSize);
    }

    /**
     * 在线显示文件
     *
     * @param id 文件id
     * @return
     */
    @GetMapping("/view/{id}")
    public ResponseEntity<Object> serveFileOnline(@PathVariable String id) {
        Optional<FileDocument> file = fileService.getById(id);
        if (file.isPresent()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "fileName=" + file.get().getName())
                    .header(HttpHeaders.CONTENT_TYPE, file.get().getContentType())
                    .header(HttpHeaders.CONTENT_LENGTH, file.get().getSize() + "").header("Connection", "close")
                    .header(HttpHeaders.CONTENT_LENGTH, file.get().getSize() + "")
                    .body(file.get().getContent());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MessageConstant.FILE_NOT_FOUND);
        }
    }

    /**
     * 下载附件
     *
     * @param id
     * @return
     * @throws UnsupportedEncodingException
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> downloadFileById(@PathVariable String id) throws UnsupportedEncodingException {
        Optional<FileDocument> file = fileService.getById(id);
        if (file.isPresent()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileName=" + URLEncoder.encode(file.get().getName(), "utf-8"))
                    .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                    .header(HttpHeaders.CONTENT_LENGTH, file.get().getSize() + "").header("Connection", "close")
                    .header(HttpHeaders.CONTENT_LENGTH, file.get().getSize() + "")
                    .body(file.get().getContent());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MessageConstant.FILE_NOT_FOUND);
        }
    }

    /**
     * JS传字节流上传 - 暂时未完成
     *
     * @param md5
     * @param request
     * @return
     * @deprecated 废弃
     */
    @Deprecated
    @PostMapping("/upload/{md5}/{ext}")
    public ResponseModel jsUpload(@PathVariable String md5, @PathVariable String ext, HttpServletRequest request, @RequestBody byte[] data) {
        ResponseModel model = ResponseModel.getInstance();
        if (StrUtil.isEmpty(md5)) {
            model.setMessage("请传入文件的md5值");
            return model;
        }
        if (!StrUtil.isEmpty(ext) && !ext.startsWith(DOT)) {
            ext = DOT + ext;
        }
        try {

            String name = request.getParameter("name");
            String description = request.getParameter("description");
            InputStream in = new ByteArrayInputStream(data);
            log.info("data_string:" + StrUtil.str(data, "UTF-8"));
            if (data.length > 0) {
                FileDocument fileDocument = new FileDocument();
                fileDocument.setName(name);
                fileDocument.setSize(data.length);
                fileDocument.setContentType(FileContentTypeUtils.getContentType(ext));
                fileDocument.setUploadDate(new Date());
                fileDocument.setSuffix(ext);
                String fileMd5 = SecureUtil.md5(in);
                fileDocument.setMd5(fileMd5);
                log.info("文件的md5 ==> {} ", fileMd5);
                fileDocument.setDescription(description);
                fileService.saveFile(fileDocument, in);

                log.info("保存的fileDocument ==> {}", fileDocument);
                model.setData(fileDocument.getId());
                model.setCode(ResponseModel.SUCCESS);
                model.setMessage("上传成功");
            } else {
                model.setMessage("请传入文件");
            }
            in.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            model.setMessage("上传失败");
        }
        return model;
    }

    /**
     * 表单上传文件
     * 当数据库中存在该md5值时，可以实现秒传功能
     *
     * @param file 文件
     * @return
     */
    @PostMapping("/upload")
    public ResponseModel formUpload(@RequestParam("file") MultipartFile file) throws IOException {
        List<String> availableSuffixList = Lists.newArrayList("pdf", "png", "docx", "pptx", "xlsx");
        ResponseModel model = ResponseModel.getInstance();
        try {
            if (file != null && !file.isEmpty()) {
                String originFileName = file.getOriginalFilename();
                if (!StringUtils.hasText(originFileName)) {
                    model.setMessage("格式不支持！");
                    return model;
                }
                //获取文件后缀名
                String suffix = originFileName.substring(originFileName.lastIndexOf(".") + 1);
                if (!availableSuffixList.contains(suffix)) {
                    model.setMessage("格式不支持！");
                    return model;
                }
                String fileMd5 = SecureUtil.md5(file.getInputStream());
                FileDocument fileDocument = fileService.saveFile(fileMd5, file);

                switch (suffix) {
                    case "pdf":
//                        // 在这里进行上传
//                        elasticService.uploadFileToEs(file.getInputStream(), fileDocument);
//                        // 异步进行缩略图的制作
//                        fileService.updateFileThumb(file.getInputStream(), fileDocument);
                        taskExecuteService.execute(fileDocument);
                        break;
                    case "docx":
                    case "pptx":
                    case "xlsx":
                        elasticService.uploadFileToEsDocx(file.getInputStream(), fileDocument);
                        break;
                    default:
                        break;
                }

                model.setData(fileDocument.getId());
                model.setCode(ResponseModel.SUCCESS);
                model.setMessage("上传成功");
            } else {
                model.setMessage("请传入文件");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            model.setMessage(ex.getMessage());
        }
        return model;
    }


    /**
     * 删除附件
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseModel deleteFile(@PathVariable String id) {
        ResponseModel model = ResponseModel.getInstance();
        if (!StrUtil.isEmpty(id)) {
            fileService.removeFile(id, true);
            model.setCode(ResponseModel.SUCCESS);
            model.setMessage("删除成功");
        } else {
            model.setMessage("请传入文件id");
        }
        return model;
    }


    /**
     * 删除附件
     *
     * @param id
     * @return
     */
    @GetMapping("/delete/{id}")
    public ResponseModel deleteFileByGetMethod(@PathVariable String id) {
        ResponseModel model = ResponseModel.getInstance();
        if (!StrUtil.isEmpty(id)) {
            fileService.removeFile(id, true);
            model.setCode(ResponseModel.SUCCESS);
            model.setMessage("删除成功");
        } else {
            model.setMessage("请传入文件id");
        }
        return model;
    }

    /**
     * @return byte[]
     * @Author luojiarui
     * @Description previewThumb
     * @Date 8:02 下午 2022/7/24
     * @Param [thumbId]
     **/
    @GetMapping(value = "/image/{thumbId}", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] previewThumb(@PathVariable String thumbId) throws Exception {
        InputStream inputStream = fileService.getFileThumb(thumbId);
        FileInputStream fileInputStream = (FileInputStream) (inputStream);
        if (inputStream == null) {
            return new byte[0];
        }
        byte[] bytes = new byte[fileInputStream.available()];
        fileInputStream.read(bytes, 0, fileInputStream.available());
        return bytes;
    }

    @GetMapping(value = "/image", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] test() {

        File file = new File("thumbnail20220724194018003.png");
        try (FileInputStream inputStream = new FileInputStream(file);) {
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes, 0, inputStream.available());
            return bytes;
        } catch (Exception e) {
            log.error("预览文档图片报错 ==> {}", e);
            return new byte[0];
        }
    }


    @GetMapping("/thumb/{id}")
    public ResponseEntity<Object> previewThumb1(@PathVariable String id) {

        if (StringUtils.hasText(id)) {
            InputStream inputStream = fileService.getFileThumb(id);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "fileName=" + id)
                    .header(HttpHeaders.CONTENT_TYPE, "image/png")
                    .header(HttpHeaders.CONTENT_LENGTH, "123")
                    .body(IoUtil.readBytes(inputStream));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MessageConstant.FILE_NOT_FOUND);
        }
    }

    @GetMapping(value = "/image2/{thumbid}", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] previewThumb2(@PathVariable String thumbid) {
        InputStream inputStream = fileService.getFileThumb(thumbid);
        if (inputStream == null) {
            return new byte[0];
        }
        return IoUtil.readBytes(inputStream);
    }
}
