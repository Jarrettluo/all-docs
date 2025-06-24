package com.jiaruiblog.controller;

import com.jiaruiblog.common.ApiResult;
import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.common.RegexConstant;
import com.jiaruiblog.entity.CateDocRelationship;
import com.jiaruiblog.entity.Category;
import com.jiaruiblog.entity.Tag;
import com.jiaruiblog.entity.TagDocRelationship;
import com.jiaruiblog.entity.dto.CategoryDTO;
import com.jiaruiblog.entity.dto.QueryDocByTagCateDTO;
import com.jiaruiblog.entity.dto.RelationDTO;
import com.jiaruiblog.enums.FilterTypeEnum;
import com.jiaruiblog.service.CategoryService;
import com.jiaruiblog.service.TagService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Date;


@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {

    @Resource
    CategoryService categoryService;

    @Resource
    TagService tagService;


    @PostMapping(value = "/insert")
    public ApiResult<Object> insert(@RequestBody CategoryDTO categoryDTO) {
        categoryDTO.setId(null);
        String name = categoryDTO.getName();
        if (!name.matches(RegexConstant.CH_ENG_WORD)) {
            return ApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_FORMAT_ERROR);
        }
        switch (categoryDTO.getType()) {
            case CATEGORY:
                Category category = new Category();
                category.setName(categoryDTO.getName());
                category.setCreateDate(new Date());
                category.setUpdateDate(new Date());
                categoryService.insert(category);
            case TAG:
                Tag tag = new Tag();
                tag.setName(categoryDTO.getName());
                tag.setCreateDate(new Date());
                tag.setUpdateDate(new Date());
                tagService.insert(tag);
            default:
                break;
        }
        return ApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_FORMAT_ERROR);
    }

    @PutMapping(value = "/update")
    public ApiResult<Object> update(@RequestBody CategoryDTO categoryDTO) {
        String name = categoryDTO.getName();
        if (!name.matches(RegexConstant.CH_ENG_WORD)) {
            return ApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_FORMAT_ERROR);
        }
        switch (categoryDTO.getType()) {
            case CATEGORY:
                Category category = new Category();
                category.setName(categoryDTO.getName());
                category.setId(categoryDTO.getId());
                category.setUpdateDate(new Date());
                categoryService.update(category);
            case TAG:
                Tag tag = new Tag();
                tag.setName(categoryDTO.getName());
                tag.setId(categoryDTO.getId());
                tag.setUpdateDate(new Date());
                tagService.update(tag);
            default:
                break;
        }
        return ApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_FORMAT_ERROR);
    }

    @DeleteMapping(value = "/remove")
    public ApiResult<Object> remove(@RequestBody CategoryDTO categoryDTO) {
        switch (categoryDTO.getType()) {
            case CATEGORY:
                Category category = new Category();
                category.setId(categoryDTO.getId());
                categoryService.remove(category);
            case TAG:
                Tag tag = new Tag();
                tag.setId(categoryDTO.getId());
                tagService.remove(tag);
            default:
                break;
        }
        return ApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_FORMAT_ERROR);
    }

    @GetMapping(value = "/all")
    public ApiResult<Object> list(@RequestParam FilterTypeEnum type, HttpServletResponse response) {
        response.setHeader("Cache-Control", "max-age=10, public");
        switch (type) {
            case CATEGORY:
                categoryService.list();
            case TAG:
                tagService.list();
            default:
                break;
        }
        return ApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_FORMAT_ERROR);
    }

    @PostMapping(value = "/addRelationship")
    public synchronized ApiResult<Object> addRelationship(@RequestBody RelationDTO relationDTO) {
        switch (relationDTO.getType()) {
            case CATEGORY:
                CateDocRelationship category = new CateDocRelationship();
                category.setCategoryId(relationDTO.getId());
                category.setFileId(relationDTO.getDocId());
                categoryService.addRelationShip(category);
            case TAG:
                TagDocRelationship tag = new TagDocRelationship();
                tag.setTagId(relationDTO.getId());
                tag.setFileId(relationDTO.getDocId());
                tag.setCreateDate(new Date());
                tag.setUpdateDate(new Date());
                tagService.addRelationShip(tag);
            default:
                break;
        }
        return ApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_FORMAT_ERROR);
    }

    @DeleteMapping(value = "/removeRelationship")
    public ApiResult<Object> removeRelationship(@RequestBody RelationDTO relationDTO) {
        switch (relationDTO.getType()) {
            case CATEGORY:
                CateDocRelationship category = new CateDocRelationship();
                category.setCategoryId(relationDTO.getId());
                category.setFileId(relationDTO.getDocId());
                categoryService.cancelCategoryRelationship(category);
            case TAG:
                TagDocRelationship tag = new TagDocRelationship();
                tag.setTagId(relationDTO.getId());
                tag.setFileId(relationDTO.getDocId());
                tagService.cancelTagRelationship(tag);
            default:
                break;
        }
        return ApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_FORMAT_ERROR);
    }

    @GetMapping(value = "getDocByTagCateKeyWord")
    public ApiResult<Object> getDocByTagCateKeyWord(@ModelAttribute("pageDTO") QueryDocByTagCateDTO pageDTO) {
        categoryService.getDocByTagAndCate(pageDTO.getCateId(), pageDTO.getTagId(), pageDTO.getKeyword(),
                Integer.toUnsignedLong(pageDTO.getPage() - 1), Integer.toUnsignedLong(pageDTO.getRows()));
        return ApiResult.success("");
    }

    @GetMapping(value = "/auth/getMyCollection")
    public ApiResult<Object> getMyCollection(@ModelAttribute("pageDTO") QueryDocByTagCateDTO pageDTO, HttpServletRequest request) {
        String userId = (String) request.getAttribute("id");
        categoryService.getMyCollection(pageDTO.getCateId(), pageDTO.getTagId(), pageDTO.getKeyword(),
                Integer.toUnsignedLong(pageDTO.getPage() - 1), Integer.toUnsignedLong(pageDTO.getRows()),
                userId);
        return ApiResult.success("");
    }

    @GetMapping(value = "/auth/getMyUploaded")
    public ApiResult<Object> getMyUploaded(@ModelAttribute("pageDTO") QueryDocByTagCateDTO pageDTO, HttpServletRequest request) {
        String userId = (String) request.getAttribute("id");
        categoryService.getMyUploaded(pageDTO.getCateId(), pageDTO.getTagId(), pageDTO.getKeyword(),
                Integer.toUnsignedLong(pageDTO.getPage() - 1), Integer.toUnsignedLong(pageDTO.getRows()),
                userId);
        return ApiResult.success("");
    }
}
