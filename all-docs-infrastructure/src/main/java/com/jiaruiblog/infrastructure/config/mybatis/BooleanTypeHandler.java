package com.jiaruiblog.infrastructure.config.mybatis;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MyBatis Type Handler for Boolean fields mapped to MySQL TINYINT(1)
 * MySQL JDBC driver returns TINYINT as java.lang.Integer, not java.lang.Boolean
 * This handler converts Integer (0/1) to Boolean
 *
 * @author Jarrett Luo
 * @version 1.0
 */
public class BooleanTypeHandler extends BaseTypeHandler<Boolean> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Boolean parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter ? 1 : 0);
    }

    @Override
    public Boolean getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int value = rs.getInt(columnName);
        return value != 0;
    }

    @Override
    public Boolean getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int value = rs.getInt(columnIndex);
        return value != 0;
    }

    @Override
    public Boolean getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int value = cs.getInt(columnIndex);
        return value != 0;
    }
}
