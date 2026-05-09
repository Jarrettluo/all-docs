package com.jiaruiblog.application.service;

import com.jiaruiblog.application.service.impl.DocLogServiceImpl;
import com.jiaruiblog.domain.entity.po.DocLog;
import com.jiaruiblog.domain.entity.po.FileDocument;
import com.jiaruiblog.domain.entity.po.User;
import com.jiaruiblog.domain.entity.dto.BasePageDTO;
import com.jiaruiblog.domain.entity.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * @author Jarrett Luo
 * @Date 2022/6/7 11:39
 * @Version 1.0
 */
public interface IDocLogService {

    /**
     * @author luojiarui
     * @Description 删除操作日志
     * @Date 15:43 2022/11/5
     * @Param [docLog]
     */
    void remove(DocLog docLog);

    /**
     * @author luojiarui
     * @Description 更新操作日志
     * @Date 15:43 2022/11/5
     * @Param [docLog]
     */
    void update(DocLog docLog);

    /**
     * @author luojiarui
     * @Description 搜索操作日志
     * @Date 15:43 2022/11/5
     * @Param [docLog]
     */
    void search(DocLog docLog);

    /**
     * @author luojiarui
     * @Description 查询操作日志列表
     * @Date 15:43 2022/11/5
     * @Param []
     * @return PageVO<DocLog>
     */
    PageVO<DocLog> queryByPage(int pageNum, int pageSize);

    /**
     * @author luojiarui
     * @Description 根据用户名查询操作日志列表
     * @Date 15:43 2022/11/5
     * @Param [userName]
     * @return List<DocLog>
     */
    List<DocLog> queryByUserName(String userName);

    /**
     * @author luojiarui
     * @Description 根据文档ID查询操作日志列表
     * @Date 15:43 2022/11/5
     * @Param [docName]
     * @return List<DocLog>
     */
    List<DocLog> queryByDocName(String docName);

    String addLog(User user, FileDocument document, DocLogServiceImpl.Action action);

    Map<String, Object> queryDocLogs(BasePageDTO page);

    void deleteDocLogBatch(List<String> logIds, String userId);
}