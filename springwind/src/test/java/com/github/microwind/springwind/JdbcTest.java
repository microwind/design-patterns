package com.github.microwind.springwind;

import com.github.microwind.springwind.jdbc.JdbcTemplate;
import com.github.microwind.springwind.jdbc.RowMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * JdbcTemplate最简单测试用例
 */
public class JdbcTest {

    // 测试用实体类：重写toString()方法，方便打印
    static class User {
        private Long id;
        private String name;

        public User(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        // getter
        public Long getId() { return id; }
        public String getName() { return name; }

        // 关键：重写toString()，自定义打印格式
        @Override
        public String toString() {
            return "User{id=" + id + ", name='" + name + "'}";
        }
    }

    // 行映射器实现（复用之前的逻辑）
    static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new User(
                    rs.getLong("id"),
                    rs.getString("name")
            );
        }
    }

    private DataSource dataSource;
    private Connection connection;

    @Before
    public void init() throws SQLException {
        // 初始化数据源（已修复）
        dataSource = new DataSource() {
            @Override
            public Connection getConnection() throws SQLException {
                return DriverManager.getConnection(
                        "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
                        "sa",
                        ""
                );
            }

            @Override
            public Connection getConnection(String username, String password) throws SQLException {
                return getConnection();
            }

            @Override
            public PrintWriter getLogWriter() throws SQLException {
                return null;
            }

            @Override
            public void setLogWriter(PrintWriter out) throws SQLException {}

            @Override
            public void setLoginTimeout(int seconds) throws SQLException {}

            @Override
            public int getLoginTimeout() throws SQLException {
                return 0;
            }

            @Override
            public Logger getParentLogger() throws SQLFeatureNotSupportedException {
                throw new SQLFeatureNotSupportedException();
            }

            @Override
            public <T> T unwrap(Class<T> iface) throws SQLException {
                return null;
            }

            @Override
            public boolean isWrapperFor(Class<?> iface) throws SQLException {
                return false;
            }
        };

        // 创建测试表（表名加反引号）
        connection = dataSource.getConnection();
        connection.prepareStatement("CREATE TABLE IF NOT EXISTS `user` (id BIGINT, name VARCHAR(20))").execute();
    }

    @Test
    public void testUpdateAndQuery() {
        // 1. 创建JdbcTemplate实例
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        // 2. 插入第一条数据
        int rows = jdbcTemplate.update("INSERT INTO `user` (id, name) VALUES (?, ?)", 1L, "测试用户");
        assertEquals(1, rows);

        // 3. 插入第二条数据（Jarry）
        rows = jdbcTemplate.update("INSERT INTO `user` (id, name) VALUES (?, ?)", 2L, "Jarry");
        System.out.println("插入第二条数据的影响行数:" + rows);

        // 4. 关键：查询所有用户，获取List<User>集合
        List<User> userList = jdbcTemplate.query(
                "SELECT id, name FROM `user`",  // SQL：查询所有用户
                new UserRowMapper()             // 行映射器：将结果集转成User对象
                // 无参数（因为不需要where条件）
        );

        // 5. 打印集合（三种常用方式，任选其一或组合）
        System.out.println("\n=== 方式1：直接打印集合（依赖User的toString()）===");
        System.out.println("用户集合: " + userList);  // 会自动遍历集合元素，调用每个User的toString()

        System.out.println("\n=== 方式2：增强for循环遍历打印 ===");
        for (User u : userList) {
            System.out.println("用户: " + u);  // 等价于 System.out.println("用户: " + u.toString());
        }

        System.out.println("\n=== 方式3：Java 8流遍历（更简洁）===");
        userList.forEach(u -> System.out.println("流遍历用户: " + u));

        // 6. 可选：断言集合正确性（确保查询结果符合预期）
        assertEquals(2, userList.size());  // 断言集合有2条数据
        assertTrue(userList.stream().anyMatch(u -> u.getName().equals("Jarry")));  // 断言存在name=Jarry的用户
    }

    @After
    public void clean() throws SQLException {
        // 清理测试表
        if (connection != null) {
            connection.prepareStatement("DROP TABLE `user`").execute();
            connection.close();
        }
    }
}