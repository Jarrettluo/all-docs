package com.jiaruiblog.util.converter;

import cn.hutool.core.util.ObjectUtil;
import com.google.common.collect.Maps;
import org.springframework.core.convert.converter.Converter;

import java.util.Map;

/**
 * @ClassName IntegerToEnumConverter
 * @Description IntegerToEnumConverter
 * @Author luojiarui
 * @Date 2022/6/19 5:05 下午
 * @Version 1.0
 **/
public class IntegerToEnumConverter<T extends BaseEnum> implements Converter<Integer, T> {
    private Map<Integer, T> enumMap = Maps.newHashMap();

    public IntegerToEnumConverter(Class<T> enumType) {
        T[] enums = enumType.getEnumConstants();
        for (T e : enums) {
            enumMap.put(e.getCode(), e);
        }
    }

    @Override
    public T convert(Integer source) {
        T t = enumMap.get(source);
        if (ObjectUtil.isNull(t)) {
            throw new IllegalArgumentException("无法匹配对应的枚举类型");
        }
        return t;
    }
}

