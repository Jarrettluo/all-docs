package com.jiaruiblog.common.converter;

import com.jiaruiblog.common.enums.PermissionEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA Converter for PermissionEnum
 * Converts between Integer (database) and PermissionEnum (Java)
 */
@Converter(autoApply = true)
public class PermissionEnumConverter implements AttributeConverter<PermissionEnum, Integer> {

    @Override
    public Integer convertToDatabaseColumn(PermissionEnum attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getCode();
    }

    @Override
    public PermissionEnum convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        for (PermissionEnum enumValue : PermissionEnum.values()) {
            if (enumValue.getCode().equals(dbData)) {
                return enumValue;
            }
        }
        return null;
    }
}
