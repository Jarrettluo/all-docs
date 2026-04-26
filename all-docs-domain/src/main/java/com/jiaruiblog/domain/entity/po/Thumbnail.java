package com.jiaruiblog.domain.entity.po;

import com.jiaruiblog.common.enums.ThumbSizeEnum;
import com.jiaruiblog.common.enums.ThumbnailEnum;
import lombok.Data;

/**
 * 缩略图相关的类
 **/
@Data
public class Thumbnail {

    /**
     * 缩略图id
     */
    private String id;

    /**
     * 对象的id
     */
    private String objectId;

    /**
     * 不同种类型
     */
    private ThumbnailEnum thumbnailEnum;

    /**
     * 大文件管理GridFS的ID
     */
    private String gridfsId;

    /**
     * 缩略图的尺寸大小
     **/
    private ThumbSizeEnum thumbSizeEnum;


}
