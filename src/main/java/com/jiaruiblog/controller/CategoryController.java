package com.jiaruiblog.controller;

import com.jiaruiblog.common.ApiResult;
import com.jiaruiblog.common.RegexConstant;
import com.jiaruiblog.entity.CateDocRelationship;
import com.jiaruiblog.entity.Category;
import com.jiaruiblog.entity.Tag;
import com.jiaruiblog.entity.TagDocRelationship;
import com.jiaruiblog.entity.dto.CategoryDTO;
import com.jiaruiblog.entity.dto.FileDocumentDTO;
import com.jiaruiblog.entity.dto.QueryDocByTagCateDTO;
import com.jiaruiblog.entity.dto.RelationDTO;
import com.jiaruiblog.entity.vo.CateOrTagVO;
import com.jiaruiblog.entity.vo.PageVO;
import com.jiaruiblog.enums.FilterTypeEnum;
import com.jiaruiblog.exception.BusinessExceptionBuilder;
import com.jiaruiblog.exception.ErrorCode;
import com.jiaruiblog.service.CategoryService;
import com.jiaruiblog.service.TagService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {

    @Resource
    CategoryService categoryService;

    @Resource
    TagService tagService;


    @PostMapping(value = "/insert")
    public ApiResult<Void> insert(@RequestBody CategoryDTO categoryDTO) {
        categoryDTO.setId(null);
        String name = categoryDTO.getName();
        // 中英文下划线横向，1-64位
        if (!name.matches(RegexConstant.CH_ENG_WORD)) {
            throw BusinessExceptionBuilder.of(ErrorCode.PARAMS_ERROR).build();
        }
        switch (categoryDTO.getType()) {
            case CATEGORY:
                Category category = new Category();
                category.setName(categoryDTO.getName());
                category.setCreateDate(new Date());
                category.setUpdateDate(new Date());
                categoryService.insert(category);
                break;
            case TAG:
                Tag tag = new Tag();
                tag.setName(categoryDTO.getName());
                tag.setCreateDate(new Date());
                tag.setUpdateDate(new Date());
                tagService.insert(tag);
                break;
            default:
                break;
        }
        return ApiResult.success();
    }

    @PutMapping(value = "/update")
    public ApiResult<Void> update(@RequestBody CategoryDTO categoryDTO) {
        String name = categoryDTO.getName();
        if (!name.matches(RegexConstant.CH_ENG_WORD)) {
            throw BusinessExceptionBuilder.of(ErrorCode.PARAMS_ERROR).build();
        }
        switch (categoryDTO.getType()) {
            case CATEGORY:
                Category category = new Category();
                category.setName(categoryDTO.getName());
                category.setId(categoryDTO.getId());
                category.setUpdateDate(new Date());
                categoryService.update(category);
                break;
            case TAG:
                Tag tag = new Tag();
                tag.setName(categoryDTO.getName());
                tag.setId(categoryDTO.getId());
                tag.setUpdateDate(new Date());
                tagService.update(tag);
                break;
            default:
                break;
        }
        return ApiResult.success();
    }

    @DeleteMapping(value = "/remove")
    public ApiResult<Void> remove(@RequestBody CategoryDTO categoryDTO) {
        switch (categoryDTO.getType()) {
            case CATEGORY:
                Category category = new Category();
                category.setId(categoryDTO.getId());
                categoryService.remove(category);
                break;
            case TAG:
                Tag tag = new Tag();
                tag.setId(categoryDTO.getId());
                tagService.remove(tag);
                break;
            default:
                break;
        }
        return ApiResult.success();
    }

    @GetMapping(value = "/all")
    public ApiResult<List<CateOrTagVO>> list(@RequestParam FilterTypeEnum type, HttpServletResponse response) {
        response.setHeader("Cache-Control", "max-age=10, public");
        List<CateOrTagVO> cateOrTagVOList = new ArrayList<>();
        switch (type) {
            case CATEGORY:
                cateOrTagVOList = categoryService.list();
                break;
            case TAG:
                cateOrTagVOList = tagService.list();
                break;
            default:
                break;
        }
        return ApiResult.success(cateOrTagVOList);
    }

    @PostMapping(value = "/addRelationship")
    public ApiResult<Void> addRelationship(@RequestBody RelationDTO relationDTO) {
        switch (relationDTO.getType()) {
            case CATEGORY:
                CateDocRelationship category = new CateDocRelationship();
                category.setCategoryId(relationDTO.getId());
                category.setFileId(relationDTO.getDocId());
                categoryService.addRelationShip(category);
                break;
            case TAG:
                TagDocRelationship tag = new TagDocRelationship();
                tag.setTagId(relationDTO.getId());
                tag.setFileId(relationDTO.getDocId());
                tag.setCreateDate(new Date());
                tag.setUpdateDate(new Date());
                tagService.addRelationShip(tag);
                break;
            default:
                break;
        }
        return ApiResult.success();
    }

    @DeleteMapping(value = "/removeRelationship")
    public ApiResult<Void> removeRelationship(@RequestBody RelationDTO relationDTO) {
        switch (relationDTO.getType()) {
            case CATEGORY:
                CateDocRelationship category = new CateDocRelationship();
                category.setCategoryId(relationDTO.getId());
                category.setFileId(relationDTO.getDocId());
                categoryService.cancelCategoryRelationship(category);
                break;
            case TAG:
                TagDocRelationship tag = new TagDocRelationship();
                tag.setTagId(relationDTO.getId());
                tag.setFileId(relationDTO.getDocId());
                tagService.cancelTagRelationship(tag);
                break;
            default:
                break;
        }
        return ApiResult.success();
    }

    @GetMapping(value = "getDocByTagCateKeyWord")
    public ApiResult<PageVO<FileDocumentDTO>> getDocByTagCateKeyWord(@ModelAttribute("pageDTO")
                                                                         QueryDocByTagCateDTO pageDTO) {
        if (pageDTO.getPage() < 1 || pageDTO.getRows() < 1) {
            throw BusinessExceptionBuilder.of(ErrorCode.PARAMS_ERROR).build();
        }
        PageVO<FileDocumentDTO> result = categoryService.getDocByTagAndCate(pageDTO.getCateId(),
                pageDTO.getTagId(),
                pageDTO.getKeyword(),
                Integer.toUnsignedLong(pageDTO.getPage() - 1),
                Integer.toUnsignedLong(pageDTO.getRows()));
        return ApiResult.success(result);
    }

    @GetMapping(value = "/auth/getMyCollection")
    public ApiResult<PageVO<FileDocumentDTO>> getMyCollection(@ModelAttribute("pageDTO") QueryDocByTagCateDTO pageDTO,
                                                               HttpServletRequest request) {
        if (pageDTO.getPage() < 1 || pageDTO.getRows() < 1) {
            throw BusinessExceptionBuilder.of(ErrorCode.PARAMS_ERROR).build();
        }
        String userId = (String) request.getAttribute("id");
        PageVO<FileDocumentDTO>  result = categoryService.getMyCollection(pageDTO.getCateId(), pageDTO.getTagId(), pageDTO.getKeyword(),
                Integer.toUnsignedLong(pageDTO.getPage() - 1), Integer.toUnsignedLong(pageDTO.getRows()),
                userId);
        return ApiResult.success(result);
    }

    @GetMapping(value = "/auth/getMyUploaded")
    public ApiResult<PageVO<FileDocumentDTO>> getMyUploaded(@ModelAttribute("pageDTO") QueryDocByTagCateDTO pageDTO,
                                                            HttpServletRequest request) {
        if (pageDTO.getPage() < 1 || pageDTO.getRows() < 1) {
            throw BusinessExceptionBuilder.of(ErrorCode.PARAMS_ERROR).build();
        }
        String userId = (String) request.getAttribute("id");
        PageVO<FileDocumentDTO> result = categoryService.getMyUploaded(pageDTO.getCateId(), pageDTO.getTagId(), pageDTO.getKeyword(),
                Integer.toUnsignedLong(pageDTO.getPage() - 1), Integer.toUnsignedLong(pageDTO.getRows()),
                userId);
        return ApiResult.success(result);
    }
}
