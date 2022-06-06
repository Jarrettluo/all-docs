package com.jiaruiblog.controller;

/**
 * @ClassName BookController
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/6/6 11:20 下午
 * @Version 1.0
 **/

import com.jiaruiblog.entity.Book;
import com.jiaruiblog.service.BookService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author geng
 * 2020/12/20
 */
@RestController
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping("/book")
    public Map<String,String> addBook(@RequestBody Book book){
        bookService.addBook(book);
        Map<String,String> map = new HashMap<>();
        map.put("msg","ok");
        return map;
    }

    @GetMapping("/book/search")
    public SearchHits<com.gyb.elasticsearch.demo.entity.es.Book> search(String key){
        return bookService.searchBook1(key);
    }
}

