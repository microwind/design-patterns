package com.github.microwind.springwind.jdbc;

import java.sql.*;

/**
 * 行映射器接口
 * 用于将数据库结果集中的每一行映射为一个对象
 */
public interface RowMapper<T> {
    /**
     * 将结果集中的当前行映射为一个对象
     * @param rs 结果集
     * @param rowNum 行号
     * @return 映射后的对象
     * @throws SQLException SQL异常
     */
    T mapRow(ResultSet rs, int rowNum) throws SQLException;
}