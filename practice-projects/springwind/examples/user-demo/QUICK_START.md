# Springwind 框架 - User Demo 数据库操作完整指南

## 🎯 项目概述

这个示例项目展示了如何在 Springwind 框架中使用 JdbcTemplate 进行完整的数据库操作。项目包含了用户管理系统的完整实现，涵盖了增、删、改、查等所有基本操作。

## 📋 快速开始

### 前置条件

- Java 17+
- Maven 3.6+
- MySQL 8.0+

### 第一步：初始化数据库

1. 创建数据库和用户：

```bash
# 使用 root 用户登录 MySQL
mysql -u root -p

# 执行以下 SQL 命令
CREATE DATABASE IF NOT EXISTS frog CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'frog_admin'@'localhost' IDENTIFIED BY 'frog798';
GRANT ALL PRIVILEGES ON frog.* TO 'frog_admin'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

2. 初始化表结构和数据：

```bash
# 进入项目目录
cd examples/user-demo

# 导入初始化脚本
mysql -u frog_admin -p frog < init-db.sql
# 输入密码：frog798
```

### 第二步：编译和打包

```bash
# 在项目根目录执行
cd examples/user-demo

# 编译
mvn clean compile

# 打包
mvn package
```

### 第三步：启动应用

#### Web 模式（推荐）- 提供 HTTP API 接口

```bash
# 方式 1：使用 Maven 运行
mvn exec:java -Dexec.args="--web"

# 方式 2：使用 JAR 文件运行
java -jar target/springwind-user-demo-*.jar --web
```

应用将在 `http://localhost:8080` 启动，您可以通过 HTTP 请求访问用户管理 API。

#### 控制台模式 - 用于本地测试

```bash
# 方式 1：使用 Maven 运行
mvn exec:java

# 方式 2：使用 JAR 文件运行
java -jar target/springwind-user-demo-*.jar
```

## 🏗️ 项目架构

### 分层设计

```
┌─────────────────────────────────────┐
│         Controller (控制层)         │
│      UserController                 │
│  - 处理 HTTP 请求                   │
│  - 参数验证                         │
│  - 调用 Service                     │
└────────────────┬────────────────────┘
                 │
┌────────────────▼────────────────────┐
│        Service (业务逻辑层)         │
│      UserService                    │
│  - 业务规则检查                     │
│  - 事务管理（可选）                 │
│  - 调用 DAO                         │
└────────────────┬────────────────────┘
                 │
┌────────────────▼────────────────────┐
│        DAO (数据访问层)             │
│      UserDao                        │
│  - SQL 操作                         │
│  - 结果映射                         │
│  - 调用 JdbcTemplate                │
└────────────────┬────────────────────┘
                 │
┌────────────────▼────────────────────┐
│   JdbcTemplate (JDBC 模板)          │
│  - 连接管理                         │
│  - 参数绑定                         │
│  - 资源释放                         │
└────────────────┬────────────────────┘
                 │
┌────────────────▼────────────────────┐
│   DataSource (数据源)               │
│  - HikariCP 连接池                  │
└─────────────────────────────────────┘
        │
        ▼
     MySQL 数据库
```

### 核心组件详解

#### 1. User 实体类 (Model)

```java
public class User {
    private Long id;              // 主键
    private String username;      // 用户名（唯一）
    private String password;      // 密码
    private String email;         // 邮箱
    private String phone;         // 手机号
    private Integer status;       // 状态（1=激活, 0=禁用）
    private Long createdTime;     // 创建时间戳
    private Long updatedTime;     // 更新时间戳
}
```

**数据库表结构：**

```sql
CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    status INT DEFAULT 1,
    created_time BIGINT,
    updated_time BIGINT,
    INDEX idx_username (username),
    INDEX idx_status (status)
) CHARSET=utf8mb4;
```

#### 2. UserDao (数据访问层)

使用 JdbcTemplate 进行数据库操作：

```java
@Repository
public class UserDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // CRUD 操作
    public int create(User user) { ... }        // 创建
    public User findById(Long id) { ... }       // 按 ID 查询
    public List<User> findAll() { ... }         // 查询全部
    public int update(User user) { ... }        // 更新
    public int delete(Long id) { ... }          // 删除
}
```

**关键方法说明：**

| 方法 | 说明 | 用途 |
|------|------|------|
| `update()` | 执行 INSERT/UPDATE/DELETE | 修改数据 |
| `queryForObject()` | 查询单个对象 | 根据 ID 查询单条记录 |
| `query()` | 查询对象列表 | 查询多条记录 |

#### 3. UserService (业务逻辑层)

```java
@Service
public class UserService {
    @Autowired
    private UserDao userDao;

    // 业务方法
    public boolean createUser(User user) { ... }
    public User getUserById(Long id) { ... }
    public List<User> getAllUsers() { ... }
    public boolean updateUser(User user) { ... }
    public boolean deleteUser(Long id) { ... }
    public boolean validateLogin(String username, String password) { ... }
}
```

#### 4. UserController (控制层)

```java
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("/list")
    @ResponseBody
    public void list(...) { ... }

    @RequestMapping("/get")
    @ResponseBody
    public void getById(...) { ... }

    // 其他接口...
}
```

#### 5. DataSourceConfig (配置)

```java
@Configuration
public class DataSourceConfig {
    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/frog...");
        config.setUsername("frog_admin");
        config.setPassword("frog798");
        config.setMaximumPoolSize(10);
        return new HikariDataSource(config);
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
```

## 🔌 REST API 文档

### API 基础 URL

```
http://localhost:8080/user
```

### 1. 用户列表

```http
GET /user/list

响应 (200):
{
    "code": 200,
    "message": "获取用户列表成功",
    "data": [
        {
            "id": 1,
            "username": "admin",
            "password": "123456",
            "email": "admin@example.com",
            "phone": "13800138000",
            "status": 1,
            "createdTime": 1704744000000,
            "updatedTime": 1704744000000
        }
    ]
}
```

### 2. 获取用户详情

```http
GET /user/get?id=1

响应 (200):
{
    "code": 200,
    "message": "获取用户详情成功",
    "data": {
        "id": 1,
        "username": "admin",
        ...
    }
}

错误响应 (404):
{
    "code": 404,
    "message": "用户不存在"
}
```

### 3. 创建用户

```http
POST /user/create
Content-Type: application/json

{
    "username": "newuser",
    "password": "password123",
    "email": "newuser@example.com",
    "phone": "13800138888"
}

响应 (201):
{
    "code": 201,
    "message": "创建用户成功"
}

错误响应 (400):
{
    "code": 400,
    "message": "用户名已存在"
}
```

### 4. 更新用户

```http
POST /user/update
Content-Type: application/json

{
    "id": 1,
    "username": "admin",
    "password": "newpassword",
    "email": "admin@newemail.com",
    "phone": "13800138000",
    "status": 1
}

响应 (200):
{
    "code": 200,
    "message": "更新用户成功"
}
```

### 5. 删除用户

```http
POST /user/delete?id=1

响应 (200):
{
    "code": 200,
    "message": "删除用户成功"
}
```

### 6. 用户登录

```http
POST /user/login
Content-Type: application/json

{
    "username": "admin",
    "password": "123456"
}

响应 (200 - 成功):
{
    "code": 200,
    "message": "登录成功",
    "data": {
        "id": 1,
        "username": "admin",
        "email": "admin@example.com",
        ...
    }
}

响应 (401 - 失败):
{
    "code": 401,
    "message": "用户名或密码错误"
}
```

## 🧪 API 测试

### 使用 curl 测试

```bash
# 获取用户列表
curl http://localhost:8080/user/list

# 创建用户
curl -X POST http://localhost:8080/user/create \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"123","email":"test@example.com"}'

# 用户登录
curl -X POST http://localhost:8080/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'

# 获取用户信息
curl "http://localhost:8080/user/get?id=1"

# 删除用户
curl -X POST "http://localhost:8080/user/delete?id=2"
```

### 使用脚本测试

项目提供了自动化测试脚本：

```bash
cd examples/user-demo
chmod +x test-user-api.sh
./test-user-api.sh
```

### 使用 Postman 测试

1. 打开 Postman
2. 创建新请求
3. 选择 HTTP 方法（GET/POST）
4. 输入 URL：`http://localhost:8080/user/list`
5. 点击 Send 按钮

## 🔑 关键概念讲解

### JdbcTemplate 使用

**查询单个对象：**

```java
String sql = "SELECT * FROM user WHERE id = ?";
User user = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
    User u = new User();
    u.setId(rs.getLong("id"));
    u.setUsername(rs.getString("username"));
    // ... 其他字段
    return u;
}, userId);
```

**查询列表：**

```java
String sql = "SELECT * FROM user WHERE status = ?";
List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> {
    User u = new User();
    // ... 映射逻辑
    return u;
}, 1);
```

**执行更新：**

```java
String sql = "INSERT INTO user (username, password, email, phone, status, created_time, updated_time) VALUES (?, ?, ?, ?, ?, ?, ?)";
int rowsAffected = jdbcTemplate.update(sql,
    user.getUsername(),
    user.getPassword(),
    user.getEmail(),
    user.getPhone(),
    user.getStatus(),
    user.getCreatedTime(),
    user.getUpdatedTime()
);
```

### Springwind 依赖注入

```java
// 1. 在配置类中定义 Bean
@Configuration
public class DataSourceConfig {
    @Bean
    public DataSource dataSource() { ... }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource ds) { ... }
}

// 2. 在需要使用的类中注入
@Repository
public class UserDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;  // 自动注入
}
```

### 事务处理（高级）

当前示例未使用事务。如需添加事务支持，可在 Service 层添加：

```java
@Service
public class UserService {
    @Transactional  // 需要实现事务支持
    public boolean createUser(User user) {
        // 多个数据库操作
        userDao.create(user);
        // ... 其他操作
    }
}
```

## 🐛 常见问题解决

### 问题 1：连接被拒绝

**错误信息：** `Connection refused`

**原因：** MySQL 数据库未启动或连接地址/端口错误

**解决方案：**
```bash
# 检查 MySQL 是否运行
brew services list | grep mysql

# 启动 MySQL（macOS）
brew services start mysql

# 或直接运行 MySQL 服务器
/usr/local/bin/mysql.server start
```

### 问题 2：认证失败

**错误信息：** `Access denied for user 'frog_admin'@'localhost'`

**原因：** 用户名或密码错误

**解决方案：** 检查 DataSourceConfig.java 中的配置是否与数据库用户匹配

### 问题 3：表不存在

**错误信息：** `Table 'frog.user' doesn't exist`

**原因：** 未执行初始化脚本

**解决方案：**
```bash
mysql -u frog_admin -p frog < init-db.sql
```

### 问题 4：字符编码问题

**错误信息：** 中文显示为乱码

**原因：** 数据库/连接编码未设置为 UTF-8

**解决方案：** 使用以下连接字符串：
```
jdbc:mysql://localhost:3306/frog?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC
```

## 📚 学习资源

### Springwind 框架相关

- [Springwind 框架文档](../../README.md)
- [手工编写的 Spring 框架](../../Hand-Crafted-Spring-Framework.md)

### JdbcTemplate 相关

- JdbcTemplate 类：[src/main/java/com/github/microwind/springwind/jdbc/JdbcTemplate.java](../../src/main/java/com/github/microwind/springwind/jdbc/JdbcTemplate.java)
- RowMapper 接口：[src/main/java/com/github/microwind/springwind/jdbc/RowMapper.java](../../src/main/java/com/github/microwind/springwind/jdbc/RowMapper.java)

### HikariCP 连接池

- 官方文档：https://github.com/brettwooldridge/HikariCP

### MySQL 相关

- 官方文档：https://dev.mysql.com/doc/

## 🚀 扩展和优化建议

1. **添加缓存**：使用 Redis 缓存频繁查询的用户信息
2. **添加分页**：在 findAll() 中支持分页查询
3. **添加排序**：支持按不同字段排序
4. **密码加密**：使用 BCrypt 等加密算法
5. **事务管理**：添加事务支持确保数据一致性
6. **参数验证**：添加 Bean Validation 框架
7. **异常处理**：自定义异常和全局异常处理
8. **日志记录**：添加详细的日志记录
9. **性能优化**：添加数据库查询索引
10. **API 文档**：集成 Swagger 自动生成 API 文档

## 📞 技术支持

如有问题，请：

1. 查看 [DATABASE_OPERATIONS.md](DATABASE_OPERATIONS.md) 详细文档
2. 检查项目中的示例代码
3. 查看错误日志获取诊断信息
4. 确保所有依赖和配置都正确

---

**最后更新：** 2024 年 1 月
