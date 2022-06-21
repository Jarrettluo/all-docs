package com.jiaruiblog.service;

/**
 * @ClassName BookService
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/6/6 11:17 下午
 * @Version 1.0
 **/

import com.jiaruiblog.entity.Book;
import com.jiaruiblog.repository.ESBookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author geng
 * 2020/12/19
 */
@Slf4j
@Service
public class BookService {
//    private final BookRepository bookRepository;
//    private final ESBookRepository esBookRepository;
//    private final TransactionTemplate transactionTemplate;
//
//    public BookService(BookRepository bookRepository,
//                       ESBookRepository esBookRepository,
//                       TransactionTemplate transactionTemplate) {
//        this.bookRepository = bookRepository;
//        this.esBookRepository = esBookRepository;
//        this.transactionTemplate = transactionTemplate;
//    }
//
//    public void addBook(Book book) {
//        final Book saveBook = transactionTemplate.execute((status) ->
//                bookRepository.save(book)
//        );
//        final com.gyb.elasticsearch.demo.entity.es.Book esBook = new com.gyb.elasticsearch.demo.entity.es.Book();
//        assert saveBook != null;
//        BeanUtils.copyProperties(saveBook, esBook);
//        esBook.setId(saveBook.getId() + "");
//        try {
//            esBookRepository.save(esBook);
//        }catch (Exception e){
//            log.error(String.format("保存ES错误！%s", e.getMessage()));
//        }
//    }
//
//    public List<com.gyb.elasticsearch.demo.entity.es.Book> searchBook(String keyword){
//        return esBookRepository.findByTitleOrAuthor(keyword, keyword);
//    }
//
//    public SearchHits<com.gyb.elasticsearch.demo.entity.es.Book> searchBook1(String keyword){
//        return esBookRepository.find(keyword);
//    }
}

