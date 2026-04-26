package com.jiaruiblog.common.converter;

import com.jiaruiblog.common.enums.ThumbnailEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA Converter for ThumbnailEnum
 * Converts between Integer (database) and ThumbnailEnum (Java)
 */
@Converter(autoApply = true)
public class ThumbnailEnumConverter implements AttributeConverter<ThumbnailEnum, Integer> {

    @Override
    public Integer convertToDatabaseColumn(ThumbnailEnum attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getCode();
    }

    @Override
    public ThumbnailEnum convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        for (ThumbnailEnum enumValue : ThumbnailEnum.values()) {
            if (enumValue.getCode().equals(dbData)) {
                return enumValue;
            }
        }
        return null;
    }
}
