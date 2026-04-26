package com.jiaruiblog.common.converter;

import com.jiaruiblog.common.enums.DocStateEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA Converter for DocStateEnum
 * Converts between Integer (database) and DocStateEnum (Java)
 */
@Converter(autoApply = true)
public class DocStateEnumConverter implements AttributeConverter<DocStateEnum, Integer> {

    @Override
    public Integer convertToDatabaseColumn(DocStateEnum attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getCode();
    }

    @Override
    public DocStateEnum convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        for (DocStateEnum enumValue : DocStateEnum.values()) {
            if (enumValue.getCode().equals(dbData)) {
                return enumValue;
            }
        }
        return null;
    }
}
