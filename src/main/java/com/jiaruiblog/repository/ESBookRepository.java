package com.jiaruiblog.repository;

import com.jiaruiblog.entity.Book;
//import org.elasticsearch.search.SearchHits;
//import org.springframework.data.elasticsearch.annotationsI.Highlight;
//import org.springframework.data.elasticsearch.annotations.HighlightField;
//import org.springframework.data.elasticsearch.annotations.Query;
//import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ESBookRepository extends ElasticsearchRepository<Book, String> {

//    List<Book> findByTitleOrAuthor(String title, String author);
//
//    @Highlight(fields = {
//            @HighlightField(name = "title"),
//            @HighlightField(name = "author")
//    })
//    @Query("{\"match\":{\"title\":\"?0\"}}")
//    SearchHits<Book> find(String keyword);

}