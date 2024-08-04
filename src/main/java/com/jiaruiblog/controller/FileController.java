package com.jiaruiblog.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.auth0.jwt.interfaces.Claim;
import com.google.common.collect.Lists;
import com.jiaruiblog.auth.PermissionEnum;
import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.config.SystemConfig;
import com.jiaruiblog.entity.FileDocument;
import com.jiaruiblog.entity.ResponseModel;
import com.jiaruiblog.entity.User;
import com.jiaruiblog.entity.dto.BasePageDTO;
import com.jiaruiblog.entity.dto.upload.FileUploadDTO;
import com.jiaruiblog.entity.dto.upload.UrlUploadDTO;
import com.jiaruiblog.enums.DocStateEnum;
import com.jiaruiblog.intercepter.SensitiveFilter;
import com.jiaruiblog.service.IFileService;
import com.jiaruiblog.service.IUserService;
import com.jiaruiblog.service.TaskExecuteService;
import com.jiaruiblog.util.BaseApiResult;
import com.jiaruiblog.util.FileContentTypeUtils;
import com.jiaruiblog.util.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.AuthenticationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author jiarui.luo
 */
@Api(tags = "查询文档详情的接口")
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("files")
public class FileController {

    private static final String DOT = ".";

    private static final String USERNAME = "username";

    @Resource
    private IFileService fileService;

    @Resource
    private TaskExecuteService taskExecuteService;

    @Resource
    private IUserService userService;

    @Resource
    SystemConfig systemConfig;

    /**
     * @return java.util.List<com.jiaruiblog.entity.FileDocument>
     * @Author luojiarui
     * @Description 列表数据
     * @Date 22:41 2023/3/15
     * @Param [basePageDTO]
     **/
    @ApiOperation(value = "查询列表", notes = "已经变更！")
    @GetMapping("/list")
    public List<FileDocument> list(@ModelAttribute BasePageDTO basePageDTO) {
        return fileService.listFilesByPage(basePageDTO.getPage(), basePageDTO.getRows());
    }

    /**
     * 在线显示文件
     *
     * @param id 文件id
     * @return 查询结果返回
     */
    @ApiOperation(value = "查询文档预览结果")
    @GetMapping("/view/{id}")
    public ResponseEntity<Object> serveFileOnline(@PathVariable String id,
                                                  @RequestParam("token") String token,
                                                  HttpServletResponse response)
            throws UnsupportedEncodingException {
        Map<String, Claim> userData = JwtUtil.verifyToken(token);
        if (CollectionUtils.isEmpty(userData)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }
        Optional<FileDocument> file = fileService.getById(id);
        if (file.isPresent()) {
            return ResponseEntity.ok()
                    // 这里需要进行中文编码
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "fileName=" + URLEncoder.encode(file.get().getName(), "utf-8"))
                    .header(HttpHeaders.CONTENT_TYPE, file.get().getContentType())
                    .header(HttpHeaders.CONTENT_LENGTH, file.get().getSize() + "")
                    .header("Connection", "close")
                    .header(HttpHeaders.CONTENT_LENGTH, file.get().getSize() + "")
                    .body(file.get().getContent());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MessageConstant.FILE_NOT_FOUND);
        }
    }

    /**
     * 在线显示文件
     *
     * @param id 文件id
     * @return ResponseEntity<Object> 返回实体
     */
    @GetMapping("/view2/{id}")
    public ResponseEntity<Object> previewFileOnline(@PathVariable String id) throws UnsupportedEncodingException {
        Optional<FileDocument> file = fileService.getPreviewById(id);
        if (file.isPresent()) {
            return ResponseEntity.ok()
                    // 这里需要进行中文编码
                    .header(HttpHeaders.CONTENT_DISPOSITION, "fileName=" + URLEncoder.encode(file.get().getName(), "utf-8") + ".pdf")
                    .header(HttpHeaders.CONTENT_TYPE, FileContentTypeUtils.getContentType("pdf"))
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
     * @param id 请求文件id
     * @return ResponseEntity<Object>
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
            if (data.length > 0) {
                FileDocument fileDocument = new FileDocument();
                fileDocument.setName(name);
                fileDocument.setSize(data.length);
                fileDocument.setContentType(FileContentTypeUtils.getContentType(ext));
                fileDocument.setUploadDate(new Date());
                fileDocument.setSuffix(ext);
                String fileMd5 = SecureUtil.md5(in);
                fileDocument.setMd5(fileMd5);
                fileDocument.setDescription(description);
                fileService.saveFile(fileDocument, in);

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
     * <p>
     * 由于增加了用户登录后上传的验证，因此该方法废弃
     * 最新的上传方式使用：documentUpload
     *
     * @param file 文件
     * @return
     */
    @Deprecated
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
                    case "docx":
                    case "pptx":
                    case "xlsx":
                        taskExecuteService.execute(fileDocument);
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
     * 表单上传文件
     * 当数据库中存在该md5值时，可以实现秒传功能
     *
     * @param file 文件
     * @return BaseApiResult
     */
    @PostMapping("auth/upload")
    public BaseApiResult documentUpload(@RequestParam("file") MultipartFile file, HttpServletRequest request)
            throws AuthenticationException {
        String username = (String) request.getAttribute(USERNAME);
        String userId = (String) request.getAttribute("id");

        User user = userService.queryById(userId);
        if (user == null) {
            throw new AuthenticationException();
        }
        // 用户非管理员且普通用户禁止
        if (Boolean.TRUE.equals(!systemConfig.getUserUpload()) && user.getPermissionEnum() != PermissionEnum.ADMIN) {
            throw new AuthenticationException();
        }

        return fileService.documentUpload(file, userId, username);
    }

    /**
     * @return java.util.List<java.lang.String>
     * @Author luojiarui
     * @Description 批量上传文件
     * @Date 23:12 2023/4/21
     * @Param [req, files]
     **/
    @ApiOperation(value = "用户批量上传文件", notes = "需要文件分类标签信息！")
    @PostMapping("/auth/uploadBatch")
    public BaseApiResult uploadBatch(FileUploadDTO fileUploadDTO, HttpServletRequest request) {

        String username = (String) request.getAttribute(USERNAME);
        String userId = (String) request.getAttribute("id");

        String category = fileUploadDTO.getCategory();
        List<String> tags = fileUploadDTO.getTags();
        String description = fileUploadDTO.getDescription();
        Boolean skipError = fileUploadDTO.getSkipError();
        MultipartFile[] files = fileUploadDTO.getFiles();

        // 检查传递的参数是否正确
        if (checkParam(tags, category, description, null).equals(Boolean.FALSE)
                || files == null || files.length < 1) {
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_FORMAT_ERROR);
        }
        // 最多只能添加10个标签
        if (!CollectionUtils.isEmpty(tags) && tags.size() > 10) {
            tags = tags.subList(0, 10);
        }
        // 当只上传一个文档的时候，跳过错误肯定是False
        if (files.length < 2) {
            skipError = Boolean.FALSE;
        }
        return fileService.uploadBatch(category, tags, description, skipError, files, userId, username);
    }

    /**
     * @return java.util.List<java.lang.String>
     * @Author luojiarui
     * @Description 通过url上传
     * @Date 23:12 2023/4/21
     * @Param [req, files]
     **/
    @ApiOperation(value = "根据用户的提供的url进行上传", notes = "需要提供url和文件分类标签信息！")
    @PostMapping("/auth/uploadByUrl")
    public BaseApiResult uploadByUrl(@RequestBody UrlUploadDTO urlUploadDTO, HttpServletRequest request) {

        String username = (String) request.getAttribute(USERNAME);
        String userId = (String) request.getAttribute("id");

        String category = urlUploadDTO.getCategory();
        List<String> tags = urlUploadDTO.getTags();
        String description = urlUploadDTO.getDescription();
        String url = urlUploadDTO.getUrl();
        String name = urlUploadDTO.getName();

        if (checkParam(tags, category, description, name).equals(Boolean.FALSE)) {
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_FORMAT_ERROR);
        }
        // 最多只能添加10个标签
        if (!CollectionUtils.isEmpty(tags) && tags.size() > 10) {
            tags = tags.subList(0, 10);
        }
        return fileService.uploadByUrl(category, tags, name, description, url, userId, username);
    }

    /**
     * @return java.lang.Boolean
     * @Author luojiarui
     * @Description 文件上传时的参数检查：长度要求；格式要求；敏感词要求
     * @Date 16:14 2023/4/22
     * @Param [tags, category, description, name]
     **/
    private static Boolean checkParam(List<String> tags, String category, String description, String name) {

        List<String> inputStrList = new ArrayList<>();
        Optional.ofNullable(tags).ifPresent(value -> {
            value.removeAll(Collections.singleton(null));
            inputStrList.addAll(value);
        });
        Optional.ofNullable(category).ifPresent(inputStrList::add);
        Optional.ofNullable(name).ifPresent(inputStrList::add);


        // STEP.1 长度检查
        for (String s : inputStrList) {
            if (s.length() > 64) {
                return Boolean.FALSE;
            }
        }
        if (description != null && description.length() > 512) {
            return Boolean.FALSE;
        }
        Optional.ofNullable(description).ifPresent(inputStrList::add);

        try {

            for (String s : inputStrList) {
                if (s == null) {
                    return Boolean.FALSE;
                }
                // STEP.2 正则检查，不能有换行，空字符串等
                Matcher matcher = Pattern.compile("[\\s\\r\\n]+").matcher(s);
                if (matcher.find()) {
                    return Boolean.FALSE;
                }
                // STEP.3 敏感词检查
                SensitiveFilter filter = SensitiveFilter.getInstance();
                int n = filter.checkSensitiveWord(s, 0, 1);
                // 存在非法字符
                if (n > 0) {
                    return Boolean.FALSE;
                }
            }
        } catch (IOException | NullPointerException e) {
            return Boolean.TRUE;
        }
        return Boolean.TRUE;
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
    public byte[] previewThumb(@PathVariable String thumbId,
                               @RequestParam("token") String token,
                               HttpServletResponse response) throws Exception {
        Map<String, Claim> userData = JwtUtil.verifyToken(token);
        if (CollectionUtils.isEmpty(userData)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return new byte[]{};
        }
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
        try (FileInputStream inputStream = new FileInputStream(file)) {
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes, 0, inputStream.available());
            return bytes;
        } catch (Exception e) {
            log.error("预览文档图片报错 ==> ", e);
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

    @GetMapping(value = "/image2/{thumbId}", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] previewThumb2(@PathVariable String thumbId,
                                @RequestParam("token") String token,
                                HttpServletResponse response) {
        Map<String, Claim> userData = JwtUtil.verifyToken(token);
        if (CollectionUtils.isEmpty(userData)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return new byte[]{};
        }
        // 设置响应头，缓存 1 小时
        response.setHeader("Cache-Control", "max-age=3600, public");
        return fileService.getFileBytes(thumbId);
    }

    @GetMapping(value = "/text2/{txtId}", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public byte[] previewTxt(@PathVariable String txtId) {
        return fileService.getFileBytes(txtId);
    }

    @GetMapping(value = "/text/{txtId}", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public void downloadTxt(@PathVariable String txtId, HttpServletResponse response) {
        try {
            byte[] buffer = fileService.getFileBytes(txtId);
            extracted(response, buffer);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void extracted(HttpServletResponse response, byte[] buffer) throws IOException {
        // 清空response
        response.reset();
        // 设置response的Header
        response.setCharacterEncoding("UTF-8");
        // 解决跨域问题，这句话是关键，对任意的域都可以，如果需要安全，可以设置成安前的域名
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
        //Content-Disposition的作用：告知浏览器以何种方式显示响应返回的文件，用浏览器打开还是以附件的形式下载到本地保存
        //attachment表示以附件方式下载   inline表示在线打开   "Content-Disposition: inline; filename=文件名.mp3"
        // filename表示文件的默认名称，因为网络传输只支持URL编码的相关支付，因此需要将文件名URL编码后进行传输,前端收到后需要反编码才能获取到真正的名称
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("字符文件", "UTF-8") + ".txt");
        // 告知浏览器文件的大小
        response.addHeader("Content-Length", "" + buffer.length);
        OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
        response.setContentType("application/octet-stream");
        outputStream.write(buffer);
        outputStream.flush();
    }

    /**
     * @return com.jiaruiblog.util.BaseApiResult
     * @Author luojiarui
     * @Description 重建文档索引，继续加入到列表中
     * @Date 22:19 2022/11/14
     * @Param [docId]
     **/
    @GetMapping("/rebuildIndex")
    public BaseApiResult rebuildIndex(@RequestParam("docId") String docId) {
        if (!StringUtils.hasText(docId)) {
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_IS_NOT_NULL);
        }
        FileDocument fileDocument = fileService.queryById(docId);
        if (fileDocument != null && fileDocument.getDocState() != DocStateEnum.ON_PROCESS) {
            taskExecuteService.execute(fileDocument);
            return BaseApiResult.success(MessageConstant.SUCCESS);
        } else {
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
    }

    @PostMapping("/temporaryFileDownloadLink")
    public BaseApiResult temporaryFileDownloadLink() {

//
//        public class TemporaryFileDownloadLink {
//
//            private static final String REDIS_HOST = "localhost"; // Redis 服务器地址
//            private static final int REDIS_PORT = 6379; // Redis 端口
//
//            public static void main(String[] args) {
//                Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT);
//
//                // 生成一个随机的下载链接令牌
//                String downloadToken = generateRandomToken();
//
//                // 设置下载链接有效期（例如，1小时，单位秒）
//                int expirationSeconds = 3600;
//
//                // 存储下载链接信息到 Redis 中，包括文件信息和过期时间
//                String fileKey = "download:" + downloadToken; // 使用前缀以区分不同类型的链接
//                String fileUrl = "https://example.com/files/sample.pdf"; // 文件的实际下载链接

        // jedis.setex 是 Redis 客户端库 Jedis 提供的方法，用于设置 Redis 中的键的过期时间。
//                jedis.setex(fileKey, expirationSeconds, fileUrl);
//
//                System.out.println("Temporary download link: " + downloadToken);
//
//                // 模拟用户访问下载链接
//                String userToken = "your_user_token"; // 用户提供的令牌
//
//                if (jedis.exists("download:" + userToken)) {
//                    String downloadUrl = jedis.get("download:" + userToken);
//                    System.out.println("Accessing download link: " + downloadUrl);
//                    // 此时可以重定向用户到 downloadUrl 进行文件下载
//                } else {
//                    System.out.println("Invalid or expired download link.");
//                }
//
//                jedis.close();
//            }
//
//            private static String generateRandomToken() {
//                // 生成一个随机的UUID作为下载链接令牌
//                return UUID.randomUUID().toString();
//            }
//        }


        return BaseApiResult.success();
    }
}
