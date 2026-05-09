package com.jiaruiblog.common.converter;

import com.jiaruiblog.common.enums.ThumbSizeEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA Converter for ThumbSizeEnum
 * Converts between Integer (database) and ThumbSizeEnum (Java)
 */
@Converter(autoApply = true)
public class ThumbSizeEnumConverter implements AttributeConverter<ThumbSizeEnum, Integer> {

    @Override
    public Integer convertToDatabaseColumn(ThumbSizeEnum attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getCode();
    }

    @Override
    public ThumbSizeEnum convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        for (ThumbSizeEnum enumValue : ThumbSizeEnum.values()) {
            if (enumValue.getCode().equals(dbData)) {
                return enumValue;
            }
        }
        return null;
    }
}
