package com.jiaruiblog.infrastructure.config.mybatis;

import com.jiaruiblog.common.converter.BaseEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MyBatis Enum Type Handler for BaseEnum implementations
 * Converts between INT (database) and Enum (Java)
 *
 * @author Jarrett Luo
 * @version 1.0
 */
public class EnumTypeHandler<E extends Enum<E> & BaseEnum> extends BaseTypeHandler<E> {

    private final Class<E> enumClass;

    public EnumTypeHandler(Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int code = rs.getInt(columnName);
        return code == 0 && rs.wasNull() ? null : toEnum(code);
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int code = rs.getInt(columnIndex);
        return code == 0 && rs.wasNull() ? null : toEnum(code);
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int code = cs.getInt(columnIndex);
        return code == 0 && cs.wasNull() ? null : toEnum(code);
    }

    private E toEnum(int code) {
        E[] constants = enumClass.getEnumConstants();
        for (E constant : constants) {
            if (constant.getCode().equals(code)) {
                return constant;
            }
        }
        return null;
    }
}