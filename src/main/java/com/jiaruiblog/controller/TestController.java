package com.jiaruiblog.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private MessageSource messageSource;

    @GetMapping("/i18n")
    public String testI18n(@RequestParam String code, HttpServletRequest request) {
        return messageSource.getMessage(
            code, 
            null, 
            request.getLocale());
    }
}