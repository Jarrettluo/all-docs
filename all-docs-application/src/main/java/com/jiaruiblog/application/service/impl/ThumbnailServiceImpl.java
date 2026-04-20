package com.jiaruiblog.application.service.impl;

import com.jiaruiblog.application.service.ThumbnailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * 缩略图服务实现类
 *
 * @author luojiarui
 * @version 1.0
 */
@Slf4j
@Service
public class ThumbnailServiceImpl implements ThumbnailService {

    @Override
    public String makeThumb(InputStream inputStream, String fileName) {
        return "";
    }

    @Override
    public String makeThumb(InputStream inputStream, String fileName, int width, int height) {
        return "";
    }

    @Override
    public String makePreview(InputStream inputStream, String fileName) {
        return "";
    }
}