package com.github.microwind.springwind.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SpringWind JDBC模板类
 * 
 * <p>注意：本模板不支持事务管理。如需事务，请在外部通过 Connection 手动控制，
 * 或使用 Spring 的 @Transactional。</p>
 */
public class JdbcTemplate {

    private static final Logger logger = LoggerFactory.getLogger(JdbcTemplate.class);

    private final DataSource dataSource;

    public JdbcTemplate(DataSource dataSource) {
        if (dataSource == null) {
            throw new IllegalArgumentException("DataSource 不能为 null");
        }
        this.dataSource = dataSource;
    }

    /**
     * 执行更新/插入/删除操作
     * @param sql SQL语句（支持 ? 占位符）
     * @param args 参数数组
     * @return 受影响的行数
     */
    public int update(String sql, Object... args) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(sql);
            setParameters(ps, args);
            int rows = ps.executeUpdate();
            logger.debug("执行更新 SQL: {}, 影响行数: {}", sql, rows);
            return rows;
        } catch (SQLException e) {
            logger.error("执行更新失败: {}", sql, e);
            throw new RuntimeException("执行更新失败: " + sql, e);
        } finally {
            closeResources(conn, ps, null);
        }
    }

    /**
     * 查询单个对象（期望返回 0 或 1 条记录）
     * @param sql SQL语句
     * @param rowMapper 行映射器
     * @param args 参数数组
     * @return 查询结果对象（无结果返回 null，多于1条抛异常）
     */
    public <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object... args) {
        if (rowMapper == null) {
            throw new IllegalArgumentException("RowMapper 不能为 null");
        }
        List<T> results = query(sql, rowMapper, args);
        if (results.isEmpty()) {
            return null;
        }
        if (results.size() > 1) {
            throw new RuntimeException("查询结果数量不正确，期望 1 条，实际 " + results.size() + " 条: " + sql);
        }
        return results.get(0);
    }

    /**
     * 查询对象列表
     * @param sql SQL语句
     * @param rowMapper 行映射器
     * @param args 参数数组
     * @return 查询结果对象列表
     */
    public <T> List<T> query(String sql, RowMapper<T> rowMapper, Object... args) {
        if (rowMapper == null) {
            throw new IllegalArgumentException("RowMapper 不能为 null");
        }

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(sql);
            setParameters(ps, args);
            rs = ps.executeQuery();

            List<T> results = new ArrayList<>();
            int rowNum = 0;
            while (rs.next()) {
                results.add(rowMapper.mapRow(rs, rowNum++));
            }
            logger.debug("执行查询 SQL: {}, 返回行数: {}", sql, results.size());
            return results;
        } catch (SQLException e) {
            logger.error("执行查询失败: {}", sql, e);
            throw new RuntimeException("执行查询失败: " + sql, e);
        } finally {
            closeResources(conn, ps, rs);
        }
    }

    /**
     * 查询单个标量值（如 count(*), max(id) 等）
     * @param sql SQL语句（通常返回单列单行）
     * @param requiredType 期望的类型（Integer.class, Long.class, String.class 等）
     * @param args 参数数组
     * @return 标量值（无结果返回 null）
     */
    @SuppressWarnings("unchecked")
    public <T> T queryForScalar(String sql, Class<T> requiredType, Object... args) {
        if (requiredType == null) {
            throw new IllegalArgumentException("requiredType 不能为 null");
        }

        return queryForObject(sql, (rs, rowNum) -> {
            Object value = rs.getObject(1);
            if (value == null) {
                return null;
            }

            // 如果类型已经匹配，直接返回
            if (requiredType.isInstance(value)) {
                return requiredType.cast(value);
            }

            // 数值类型转换
            if (value instanceof Number) {
                Number num = (Number) value;
                if (requiredType == Integer.class || requiredType == int.class) {
                    return (T) Integer.valueOf(num.intValue());
                }
                if (requiredType == Long.class || requiredType == long.class) {
                    return (T) Long.valueOf(num.longValue());
                }
                if (requiredType == Double.class || requiredType == double.class) {
                    return (T) Double.valueOf(num.doubleValue());
                }
                if (requiredType == Float.class || requiredType == float.class) {
                    return (T) Float.valueOf(num.floatValue());
                }
            }

            // BigDecimal 转换（常见于数据库 DECIMAL 类型）
            if (value instanceof BigDecimal) {
                BigDecimal bd = (BigDecimal) value;
                if (requiredType == Integer.class || requiredType == int.class) {
                    return (T) Integer.valueOf(bd.intValueExact());
                }
                if (requiredType == Long.class || requiredType == long.class) {
                    return (T) Long.valueOf(bd.longValueExact());
                }
                if (requiredType == Double.class || requiredType == double.class) {
                    return (T) Double.valueOf(bd.doubleValue());
                }
            }

            // 字符串转换
            if (requiredType == String.class) {
                return (T) value.toString();
            }

            throw new RuntimeException("无法将 " + value.getClass().getName() +
                    " 转换为 " + requiredType.getName() + "。建议使用自定义 RowMapper。");
        }, args);
    }

    /**
     * 批量更新操作
     * @param sql SQL语句
     * @param batchArgs 批量参数列表（每个元素是一个参数数组）
     * @return 每条SQL影响的行数数组
     */
    public int[] batchUpdate(String sql, List<Object[]> batchArgs) {
        if (batchArgs == null || batchArgs.isEmpty()) {
            return new int[0];
        }

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(sql);

            for (Object[] args : batchArgs) {
                setParameters(ps, args);
                ps.addBatch();
            }

            int[] result = ps.executeBatch();
            ps.clearBatch();
            logger.debug("执行批量更新 SQL: {}, 批次大小: {}", sql, batchArgs.size());
            return result;
        } catch (SQLException e) {
            logger.error("执行批量更新失败: {}", sql, e);
            throw new RuntimeException("执行批量更新失败: " + sql, e);
        } finally {
            closeResources(conn, ps, null);
        }
    }

    /**
     * 设置 PreparedStatement 参数
     */
    private void setParameters(PreparedStatement ps, Object[] args) throws SQLException {
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (arg == null) {
                    ps.setNull(i + 1, Types.NULL);
                } else {
                    ps.setObject(i + 1, arg);
                }
            }
        }
    }

    /**
     * 关闭资源（每个资源独立 try-catch，避免连锁失败）
     */
    private void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                logger.error("关闭 ResultSet 失败", e);
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                logger.error("关闭 Statement 失败", e);
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.error("关闭 Connection 失败", e);
            }
        }
    }

    /**
     * 获取数据源
     */
    public DataSource getDataSource() {
        return dataSource;
    }
}