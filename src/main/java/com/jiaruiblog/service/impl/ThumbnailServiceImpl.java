package com.jiaruiblog.service.impl;

import com.jiaruiblog.entity.FileDocument;
import com.jiaruiblog.entity.Thumbnail;
import com.jiaruiblog.enums.ThumbSizeEnum;
import com.jiaruiblog.enums.ThumbnailEnum;
import com.jiaruiblog.repository.ThumbnailRepository;
import com.jiaruiblog.service.DocumentService;
import com.jiaruiblog.service.ThumbnailService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 缩略图服务实现类
 *
 * @author luojiarui
 * @version 1.0
 */
@Slf4j
@Service
public class ThumbnailServiceImpl implements ThumbnailService {

    @Resource
    private ThumbnailRepository thumbnailRepository;
    
    @Resource
    private DocumentService documentService;

    /**
     * 保存缩略图信息
     *
     * @param thumbnail 缩略图对象
     */
    @Override
    public void save(Thumbnail thumbnail) {
        String objectId = thumbnail.getObjectId();
        if (!StringUtils.hasText(objectId) || !StringUtils.hasText(thumbnail.getGridfsId())) {
            log.warn("缩略图保存失败：objectId或gridfsId为空");
            return;
        }
        
        try {
            // 先删除已存在的缩略图
            if (searchByObjectId(objectId) != null) {
                this.removeByObjectId(objectId);
            }
            
            // 保存到独立表
            thumbnailRepository.save(thumbnail);
            
            // 同步更新文档中的缩略图列表
            syncToDocumentThumbnailList(thumbnail);
            
            log.info("缩略图保存成功：objectId={}, gridfsId={}", objectId, thumbnail.getGridfsId());
        } catch (Exception e) {
            log.error("缩略图保存失败：objectId={}", objectId, e);
            throw new RuntimeException("缩略图保存失败", e);
        }
    }

    /**
     * 批量保存缩略图信息
     *
     * @param thumbnails 缩略图对象列表
     */
    @Override
    public void saveBatch(List<Thumbnail> thumbnails) {
        if (CollectionUtils.isEmpty(thumbnails)) {
            return;
        }
        
        for (Thumbnail thumbnail : thumbnails) {
            try {
                save(thumbnail);
            } catch (Exception e) {
                log.error("批量保存缩略图失败：objectId={}", thumbnail.getObjectId(), e);
            }
        }
    }

    /**
     * 根据对象ID查询缩略图
     *
     * @param objectId 对象ID
     * @return 缩略图对象
     */
    @Override
    public Thumbnail searchByObjectId(String objectId) {
        if (!StringUtils.hasText(objectId)) {
            return null;
        }
        return thumbnailRepository.findByObjectId(objectId);
    }

    /**
     * 根据对象ID查询所有缩略图
     *
     * @param objectId 对象ID
     * @return 缩略图对象列表
     */
    @Override
    public List<Thumbnail> searchAllByObjectId(String objectId) {
        if (!StringUtils.hasText(objectId)) {
            return List.of();
        }
        return thumbnailRepository.findAllByObjectId(objectId);
    }

    /**
     * 根据对象ID删除缩略图
     *
     * @param objectId 对象ID
     */
    @Override
    public void removeByObjectId(String objectId) {
        if (!StringUtils.hasText(objectId)) {
            return;
        }
        
        try {
            // 删除独立表中的缩略图
            thumbnailRepository.deleteByObjectId(objectId);
            
            // 同步清空文档中的缩略图列表
            clearDocumentThumbnailList(objectId);
            
            log.info("缩略图删除成功：objectId={}", objectId);
        } catch (Exception e) {
            log.error("缩略图删除失败：objectId={}", objectId, e);
            throw new RuntimeException("缩略图删除失败", e);
        }
    }

    /**
     * 根据对象ID和缩略图类型查询
     *
     * @param objectId 对象ID
     * @param thumbnailEnum 缩略图类型
     * @return 缩略图对象
     */
    @Override
    public Thumbnail searchByObjectIdAndType(String objectId, String thumbnailEnum) {
        if (!StringUtils.hasText(objectId) || !StringUtils.hasText(thumbnailEnum)) {
            return null;
        }
        return thumbnailRepository.findByObjectIdAndType(objectId, thumbnailEnum);
    }

    /**
     * 根据对象ID和尺寸查询
     *
     * @param objectId 对象ID
     * @param thumbSizeEnum 缩略图尺寸
     * @return 缩略图对象
     */
    @Override
    public Thumbnail searchByObjectIdAndSize(String objectId, String thumbSizeEnum) {
        if (!StringUtils.hasText(objectId) || !StringUtils.hasText(thumbSizeEnum)) {
            return null;
        }
        return thumbnailRepository.findByObjectIdAndSize(objectId, thumbSizeEnum);
    }

    /**
     * 同步缩略图到文档的thumbnailList
     */
    private void syncToDocumentThumbnailList(Thumbnail thumbnail) {
        try {
            Optional<FileDocument> documentOpt = documentService.getById(thumbnail.getObjectId());
            if (documentOpt.isPresent()) {
                FileDocument document = documentOpt.get();
                List<Thumbnail> thumbnailList = document.getThumbnailList();
                
                // 移除同类型的旧缩略图
                if (thumbnailList != null) {
                    thumbnailList.removeIf(t -> 
                        t.getThumbnailEnum() == thumbnail.getThumbnailEnum() && 
                        t.getThumbSizeEnum() == thumbnail.getThumbSizeEnum());
                } else {
                    thumbnailList = new ArrayList<>();
                }
                
                // 添加新缩略图
                thumbnailList.add(thumbnail);
                document.setThumbnailList(thumbnailList);
                
                // 更新文档
                documentService.updateFile(document);
            }
        } catch (Exception e) {
            log.error("同步文档缩略图列表失败：objectId={}", thumbnail.getObjectId(), e);
        }
    }

    /**
     * 清空文档中的缩略图列表
     */
    private void clearDocumentThumbnailList(String objectId) {
        try {
            Optional<FileDocument> documentOpt = documentService.getById(objectId);
            if (documentOpt.isPresent()) {
                FileDocument document = documentOpt.get();
                document.setThumbnailList(new ArrayList<>());
                documentService.updateFile(document);
            }
        } catch (Exception e) {
            log.error("清空文档缩略图列表失败：objectId={}", objectId, e);
        }
    }

    /**
     * 创建并保存缩略图
     *
     * @param objectId 文档对象ID
     * @param gridfsId GridFS文件ID
     * @param thumbnailEnum 缩略图类型
     * @param thumbSizeEnum 缩略图尺寸
     */
    public void createAndSaveThumbnail(String objectId, String gridfsId, 
                                     String thumbnailEnum, String thumbSizeEnum) {
        Thumbnail thumbnail = new Thumbnail();
        thumbnail.setObjectId(objectId);
        thumbnail.setGridfsId(gridfsId);
        thumbnail.setThumbnailEnum(ThumbnailEnum.valueOf(thumbnailEnum));
        thumbnail.setThumbSizeEnum(ThumbSizeEnum.valueOf(thumbSizeEnum));
        
        save(thumbnail);
    }
}
