package com.jiaruiblog.api.controller;

import com.jiaruiblog.api.auth.Permission;
import com.jiaruiblog.common.enums.PermissionEnum;
import com.jiaruiblog.common.ApiResult;
import com.jiaruiblog.domain.entity.TagDocRelationship;
import com.jiaruiblog.domain.entity.dto.FileDocumentDTO;
import com.jiaruiblog.domain.entity.vo.CateOrTagVO;
import com.jiaruiblog.domain.entity.vo.PageVO;
import com.jiaruiblog.application.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName TagController
 * @Description 标签管理控制器
 * @author luojiarui
 * @Date 2022/6/7 11:38
 * @Version 1.0
 **/
@Tag(name = "标签管理模块")
@RestController
@Slf4j
@RequestMapping("/api/v1/tag")
public class TagController {

    @Resource
    TagService tagService;

    @Operation(summary = "新增标签", description = "创建新的标签")
    @PostMapping("/insert")
    public ApiResult<Void> insert(@RequestBody com.jiaruiblog.domain.entity.Tag tag) {
        tag.setId(null);
        tag.setCreateDate(new Date());
        tag.setUpdateDate(new Date());
        tagService.insert(tag);
        return ApiResult.success();
    }

    @Operation(summary = "更新标签", description = "修改现有标签信息")
    @PutMapping("/update")
    public ApiResult<Void> update(@RequestBody com.jiaruiblog.domain.entity.Tag tag) {
        tag.setUpdateDate(new Date());
        tagService.update(tag);
        return ApiResult.success();
    }

    @Operation(summary = "删除标签", description = "根据ID删除标签")
    @DeleteMapping("/remove")
    public ApiResult<Void> remove(@RequestBody com.jiaruiblog.domain.entity.Tag tag) {
        tagService.remove(tag);
        return ApiResult.success();
    }

    @Operation(summary = "获取所有标签", description = "获取标签列表")
    @GetMapping("/all")
    public ApiResult<List<CateOrTagVO>> list() {
        return ApiResult.success(tagService.list());
    }

    @Operation(summary = "根据ID查询标签", description = "根据标签ID查询标签详情")
    @GetMapping("/detail")
    public ApiResult<com.jiaruiblog.domain.entity.Tag> queryById(@Parameter(description = "标签ID", required = true)
                                    @RequestParam String tagId) {
        com.jiaruiblog.domain.entity.Tag tag = tagService.queryById(tagId);
        return ApiResult.success(tag);
    }

    @Operation(summary = "根据名称查询标签", description = "根据标签名称查询标签详情")
    @GetMapping("/detailByName")
    public ApiResult<com.jiaruiblog.domain.entity.Tag> queryByName(@Parameter(description = "标签名称", required = true)
                                      @RequestParam String tagName) {
        com.jiaruiblog.domain.entity.Tag tag = tagService.queryByName(tagName);
        return ApiResult.success(tag);
    }

    @Operation(summary = "根据文档ID查询标签列表", description = "获取某个文档关联的所有标签")
    @GetMapping("/byDocId")
    public ApiResult<List<com.jiaruiblog.domain.entity.Tag>> queryByDocId(@Parameter(description = "文档ID", required = true)
                                              @RequestParam String docId) {
        List<com.jiaruiblog.domain.entity.Tag> tags = tagService.queryByDocId(docId);
        return ApiResult.success(tags);
    }

    @Operation(summary = "获取标签列表", description = "获取标签列表（用于下拉框）")
    @GetMapping("/list")
    public ApiResult<List<com.jiaruiblog.domain.entity.Tag>> getTagList() {
        return ApiResult.success(tagService.getTagList());
    }

    @Operation(summary = "删除文档的标签关联", description = "删除某个文档的所有标签关联")
    @DeleteMapping("/removeRelateByDocId")
    public ApiResult<Void> removeRelateByDocId(@Parameter(description = "文档ID", required = true)
                                                @RequestParam String docId) {
        tagService.removeRelateByDocId(docId);
        return ApiResult.success();
    }

    @Operation(summary = "添加标签关联", description = "为文档添加标签关联")
    @PostMapping("/addRelationship")
    public ApiResult<Void> addRelationship(@RequestBody TagDocRelationship tag) {
        tag.setCreateDate(new Date());
        tag.setUpdateDate(new Date());
        tagService.addRelationShip(tag);
        return ApiResult.success();
    }

    @Operation(summary = "取消标签关联", description = "取消文档的标签关联")
    @DeleteMapping("/cancelRelationship")
    public ApiResult<Void> cancelTagRelationship(@RequestBody TagDocRelationship tag) {
        tagService.cancelTagRelationship(tag);
        return ApiResult.success();
    }

    @Operation(summary = "模糊搜索标签", description = "根据关键词模糊搜索标签")
    @GetMapping("/fuzzySearch")
    public ApiResult<List<String>> fuzzySearch(@Parameter(description = "搜索关键词", required = true)
                                               @RequestParam String keyWord) {
        List<String> result = tagService.fuzzySearchDoc(keyWord);
        return ApiResult.success(result);
    }

    @Operation(summary = "获取随机标签", description = "获取随机标签列表")
    @GetMapping("/random")
    public ApiResult<List<com.jiaruiblog.domain.entity.Tag>> getRandom() {
        return ApiResult.success(tagService.getRandom());
    }

    @Operation(summary = "保存或更新标签", description = "如果标签存在则更新，不存在则创建")
    @PostMapping("/saveOrUpdate")
    public ApiResult<String> saveOrUpdate(@Parameter(description = "标签名称", required = true)
                                          @RequestParam String tagName) {
        String tagId = tagService.saveOrUpdateTag(tagName);
        return ApiResult.success(tagId);
    }

    @Operation(summary = "根据分类和标签查询文档", description = "根据分类ID和标签ID联合查询文档")
    @GetMapping("/docs")
    public ApiResult<PageVO<FileDocumentDTO>> getDocByTagAndCate(
            @Parameter(description = "分类ID") @RequestParam(required = false) String cateId,
            @Parameter(description = "标签ID") @RequestParam(required = false) String tagId,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码") @RequestParam Long pageNum,
            @Parameter(description = "每页数量") @RequestParam Long pageSize) {
        PageVO<FileDocumentDTO> result = tagService.getDocByTagAndCate(cateId, tagId, keyword, pageNum, pageSize);
        return ApiResult.success(result);
    }

    @Operation(summary = "获取最近的标签关联关系", description = "获取最近创建的标签关联关系")
    @GetMapping("/recentRelationships")
    public ApiResult<Map<com.jiaruiblog.domain.entity.Tag, List<TagDocRelationship>>> getRecentTagRelationship() {
        return ApiResult.success(tagService.getRecentTagRelationship());
    }
}
