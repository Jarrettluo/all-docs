package com.jiaruiblog.controller;

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
    @ApiOperation(value = "2.6 更新评论", notes = "更新评论")
    @GetMapping("queryDocForReview")
    public BaseApiResult queryDocReviewList(@ModelAttribute("pageParams") BasePageDTO pageParams,
                                            HttpServletRequest request) {
        String userId = (String) request.getAttribute("id");
        User user = userServiceImpl.queryById(userId);
        // 必须是管理员才有资格进行评审
        if (user == null) {
            assert false;
            if (user.getUsername() != "sf") {
                return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_FORMAT_ERROR);
            }
        }
        return docReviewService.queryReviewsByPage(pageParams, new User());
    }


    /**
     * 修改已读， 只有普通用户有此权限
     * 用户必须是文档的上传人
     * @return BaseApiResult
     */
    @ApiOperation(value = "2.6 更新评论", notes = "更新评论")
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
    @DeleteMapping("removeDocReview")
    public BaseApiResult removeLog(List<String> logIds) {
        return docReviewService.deleteDocLogBatch(logIds);
    }

}
