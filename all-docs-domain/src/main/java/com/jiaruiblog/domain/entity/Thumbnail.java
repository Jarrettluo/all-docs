package com.jiaruiblog.domain.entity;

import com.jiaruiblog.enums.ThumbSizeEnum;
import com.jiaruiblog.enums.ThumbnailEnum;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @ClassName Thumbnail
 * @Description 缩略图相关的类
 * @author luojiarui
 * @Date 2022/7/23 5:57 下午
 * @Version 1.0
 **/
@Table(name = "thumbnail")
@Data
public class Thumbnail {

    /**
     * 缩略图id
     */
    @Id
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