package com.jiaruiblog.domain.entity.vo;

import com.jiaruiblog.common.enums.DocStateEnum;
import lombok.Data;

import java.util.List;

/**
 * @ClassName DocumentVO
 * @Description DocumentVO
 * @author luojiarui
 * @Date 2022/6/21 9:03 下午
 * @Version 1.0
 **/
@Data
public class DocumentVO {

    private String id;

    private String title;

    private String description;

    private Long size;

    private Long collectNum;

    private Long commentNum;

    private Long likeNum;

    private CategoryVO categoryVO;

    private List<TagVO> tagVOList;

    private String userName;

    private String thumbId;

    private DocStateEnum docState;

    private String errorMsg;

    private String txtId;

    private String previewFileId;

    private java.util.Date createTime;

//    private List<PageVO> pageVOList;

}