package com.jiaruiblog.common.converter;

import com.jiaruiblog.common.enums.RedisActionEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA Converter for RedisActionEnum
 * Converts between Integer (database) and RedisActionEnum (Java)
 */
@Converter(autoApply = true)
public class RedisActionEnumConverter implements AttributeConverter<RedisActionEnum, Integer> {

    @Override
    public Integer convertToDatabaseColumn(RedisActionEnum attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getCode();
    }

    @Override
    public RedisActionEnum convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        for (RedisActionEnum enumValue : RedisActionEnum.values()) {
            if (enumValue.getCode().equals(dbData)) {
                return enumValue;
            }
        }
        return null;
    }
}
