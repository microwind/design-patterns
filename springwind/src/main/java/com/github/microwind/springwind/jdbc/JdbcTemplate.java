package com.github.microwind.springwind.jdbc;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SpringWind JDBC模板类
 */
public class JdbcTemplate {
    // 数据源
    private final DataSource dataSource;

    /**
     * 构造函数
     * @param dataSource 数据源
     */
    public JdbcTemplate(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 执行更新操作
     * @param sql SQL语句
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
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("执行更新失败", e);
        } finally {
            closeResources(conn, ps, null);
        }
    }

    /**
     * 查询单个对象
     * @param sql SQL语句
     * @param rowMapper 行映射器
     * @param args 参数数组
     * @return 查询结果对象
     */
    public <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object... args) {
        List<T> results = query(sql, rowMapper, args);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * 查询对象列表
     * @param sql SQL语句
     * @param rowMapper 行映射器
     * @param args 参数数组
     * @return 查询结果对象列表
     */
    public <T> List<T> query(String sql, RowMapper<T> rowMapper, Object... args) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            // 获取数据库连接
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(sql);
            setParameters(ps, args);
            // 执行查询
            rs = ps.executeQuery();
            
            List<T> results = new ArrayList<>();
            int rowNum = 0;
            // 遍历结果集
            while (rs.next()) {
                results.add(rowMapper.mapRow(rs, rowNum++));
            }
            return results;
        } catch (SQLException e) {
            throw new RuntimeException("执行查询失败", e);
        } finally {
            closeResources(conn, ps, rs);
        }
    }

    /**
     * 设置参数
     * @param ps 预编译语句
     * @param args 参数数组
     * @throws SQLException SQL异常
     */
    private void setParameters(PreparedStatement ps, Object[] args) throws SQLException {
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
        }
    }

    /**
     * 关闭资源
     * @param conn 数据库连接
     * @param ps 预编译语句
     * @param rs 结果集
     */
    private void closeResources(Connection conn, PreparedStatement ps, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}