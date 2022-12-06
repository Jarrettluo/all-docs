package com.jiaruiblog.controller;

import com.jiaruiblog.annontation.MustAdmin;
import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.entity.BasePageDTO;
import com.jiaruiblog.entity.User;
import com.jiaruiblog.service.DocReviewService;
import com.jiaruiblog.service.impl.UserServiceImpl;
import com.jiaruiblog.util.BaseApiResult;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @Author Jarrett Luo
 * @Date 2022/11/25 15:56
 * @Version 1.0
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/docReview")
public class DocReviewController {

    @Autowired
    private DocReviewService docReviewService;

    @Autowired
    private UserServiceImpl userServiceImpl;

    /**
     * 普通用户、管理员用户，列表查询
     * 该接口必须在登录条件下才能查询
     * 普通普通查询到自己上传的文档
     * 管理员查询到所有的文档评审信息
     * @return BaseApiResult
     */
    @MustAdmin()
    @ApiOperation(value = "查询需要评审的文档列表", notes = "查询需要评审的文档列表")
    @GetMapping("queryDocForReview")
    public BaseApiResult queryDocReviewList(@ModelAttribute("pageParams") BasePageDTO pageParams,
                                            HttpServletRequest request) {
        String userId = (String) request.getAttribute("id");

//        System.out.println(request.get);
        System.out.println(userId);
        System.out.println(pageParams);
//        User user = userServiceImpl.queryById(userId);
        // 必须是管理员才有资格进行评审
//        if (user == null) {
//            assert false;
//            if (user.getUsername() != "sf") {
//                return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_FORMAT_ERROR);
//            }
//        }
        return docReviewService.queryReviewsByPage(pageParams, new User());
    }


    /**
     * 修改已读， 只有普通用户有此权限
     * 用户必须是文档的上传人
     * @return BaseApiResult
     */
    @ApiOperation(value = "修改已读", notes = "修改已读功能只有普通用户有此权限")
    @PutMapping("userRead")
    public BaseApiResult updateDocReview(@RequestBody List<String> ids, HttpServletRequest request) {
        String userId = (String) request.getAttribute("id");
        User user = userServiceImpl.queryById(userId);
        if (user == null) {
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_FORMAT_ERROR);
        }
        // 修改评审意见为通过
        return docReviewService.userRead(ids);
    }

    /**
     * @Author luojiarui
     * @Description 单个进行拒绝
     * @Date 21:12 2022/11/30
     * @Param [docId, reason]
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    @ApiOperation(value = "管理员拒绝某个文档", notes = "管理员拒绝某个文档，只有管理员有操作该文档的权限")
    @PostMapping("refuse")
    public BaseApiResult refuse(String docId, String reason, HttpServletRequest request) {
        String userId = (String) request.getAttribute("id");
        User user = userServiceImpl.queryById(userId);
        // 必须是管理员才行
        if (user == null) {
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_FORMAT_ERROR);
        }
        return docReviewService.refuse(docId, reason);
    }

    /**
     * @Author luojiarui
     * @Description 批量进行拒绝，并删除文档
     * @Date 21:12 2022/11/30
     * @Param [docIds]
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    @ApiOperation(value = "管理员拒绝一批文档", notes = "管理员拒绝一批文档，只有管理员有操作该文档的权限")
    @PostMapping("refuseBatch")
    public BaseApiResult refuseBatch(List<String> docIds, HttpServletRequest request) {
        String userId = (String) request.getAttribute("id");
        User user = userServiceImpl.queryById(userId);
        if (user == null) {
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_FORMAT_ERROR);
        }
        return docReviewService.refuseBatch(docIds);
    }

    /**
     * @Author luojiarui
     * @Description 管理员和普通用户分别查询
     * @Date 21:15 2022/11/30
     * @Param [pageParams, request]
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    @ApiOperation(value = "管理员和普通用户分别查询数据", notes = "查询文档审批的列表")
    @GetMapping("queryReviewResultList")
    public BaseApiResult queryReviewResultList(@ModelAttribute("pageParams") BasePageDTO pageParams,
                                               HttpServletRequest request) {
        String userId = (String) request.getAttribute("id");
        User user = userServiceImpl.queryById(userId);
        if (user == null) {
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_FORMAT_ERROR);
        }
        return docReviewService.queryReviewLog(pageParams, new User());
    }


    /**
     * 普通用户删除，管理员删除，删除评审日志
     * @return BaseApiResult
     */
//    @ApiOperation(value = "2.6 更新评论", notes = "更新评论")
//    @DeleteMapping("removeDocReview")
//    public BaseApiResult removeDocReview(List<String> docIds, HttpServletRequest request) {
//        String userId = (String) request.getAttribute("id");
//        User user = userServiceImpl.queryById(userId);
//        if (user == null) {
//            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_FORMAT_ERROR);
//        }
//        return docReviewService.deleteReviewsBatch(docIds);
//    }


    /**
     * @Author luojiarui
     * @Description 系统用户日志查询
     * @Date 21:16 2022/11/30
     * @Param [pageParams]
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    @ApiOperation(value = "管理员查询系统日志信息", notes = "只有管理员有权限查询日志列表")
    @GetMapping("queryLogList")
    public BaseApiResult queryLogList(@ModelAttribute("pageParams") BasePageDTO pageParams) {
        return docReviewService.queryDocLogs(pageParams, new User());
    }

    /**
     * @Author luojiarui
     * @Description 删除用户日志
     * @Date 21:16 2022/11/30
     * @Param [logIds]
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    @ApiOperation(value = "管理员删除文档信息", notes = "只有管理员有权限删除文档的日志")
    @DeleteMapping("removeDocReview")
    public BaseApiResult removeLog(@RequestBody Map<String, List<String>> params) {
        List<String> logIds = params.get("ids");
        return docReviewService.deleteDocLogBatch(logIds);
    }

    @ApiOperation(value = "管理员修改系统设置", notes = "只有管理员有权限修改系统的设置信息")
    @PutMapping("systemConfig")
    public BaseApiResult systemConfig(@RequestBody String params) {
        return BaseApiResult.success();
    }

}
