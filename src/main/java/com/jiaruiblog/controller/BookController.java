package com.jiaruiblog.controller;

/**
 * @ClassName BookController
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/6/6 11:20 下午
 * @Version 1.0
 **/

import cn.hutool.crypto.SecureUtil;
import com.jiaruiblog.entity.Book;
import com.jiaruiblog.entity.FileDocument;
import com.jiaruiblog.service.BookService;
import com.jiaruiblog.service.ElasticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author geng
 * 2020/12/20
 */
@RestController
public class BookController {
//    private final BookService bookService;

//    public BookController(BookService bookService) {
//        this.bookService = bookService;
//    }

    @Autowired
    ElasticService elasticService;

    @PostMapping("/book")
    public Map<String,String> addBook(@RequestBody Book book){
//        bookService.addBook(book);
        Map<String,String> map = new HashMap<>();
//        map.put("msg","ok");
        return map;
    }

    @GetMapping("/es/search")
    public List<FileDocument> search(String key) throws IOException {
        return elasticService.search(key);
    }

    @PostMapping("/es/upload")
    public long upload(@RequestParam("file") MultipartFile file) throws IOException {
        FileDocument fileDocument = new FileDocument();

        String name = file.getName();
        String fileMd5 = SecureUtil.md5(file.getInputStream());
        fileDocument.setName(name);
        fileDocument.setMd5(fileMd5);
        fileDocument.setContentType(file.getContentType());
        long startTime = System.currentTimeMillis();
        elasticService.uploadFileToEs(file.getInputStream(), fileDocument);
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }
}

