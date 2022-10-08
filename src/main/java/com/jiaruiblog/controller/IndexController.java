package com.jiaruiblog.controller;

import com.jiaruiblog.service.IFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author jiarui.luo
 */
@Controller
public class IndexController {

    @Autowired
    private IFileService fileService;

    @RequestMapping("/")
    public String index(ModelMap map, @RequestParam(value = "pageIndex", defaultValue = "1") int pageIndex, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        map.addAttribute("list", fileService.listFilesByPage(pageIndex, pageSize));
        return "index";
    }

    @RequestMapping("/upload")
    public String upload() {
        return "upload";
    }

    @RequestMapping("/jsupload")
    public String jsupload() {
        return "jsupload";
    }

}
