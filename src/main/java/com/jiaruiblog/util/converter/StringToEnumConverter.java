package com.jiaruiblog.util.converter;

import cn.hutool.core.util.ObjectUtil;
import com.google.common.collect.Maps;
import org.springframework.core.convert.converter.Converter;

import java.util.Map;

/**
 * @ClassName StringToEnumConverter
 * @Description StringToEnumConverter
 * @Author luojiarui
 * @Date 2022/6/19 5:07 下午
 * @Version 1.0
 **/
public class StringToEnumConverter<T extends BaseEnum> implements Converter<String, T> {
    private Map<String, T> enumMap = Maps.newHashMap();

    public StringToEnumConverter(Class<T> enumType) {
        T[] enums = enumType.getEnumConstants();
        for (T e : enums) {
            enumMap.put(e.getCode().toString(), e);
        }
    }

    @Override
    public T convert(String source) {
        T t = enumMap.get(source);
        if (ObjectUtil.isNull(t)) {
            throw new IllegalArgumentException("无法匹配对应的枚举类型");
        }
        return t;
    }
}
