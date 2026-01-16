# 用户管理系统 - Springwind 数据库操作示例

这是一个完整的数据库操作示例，演示了如何在 Springwind 框架中使用 JdbcTemplate 进行数据库操作。

## 项目结构

```
examples/user-demo/
├── src/main/
│   ├── java/com/github/microwind/userdemo/
│   │   ├── config/
│   │   │   └── DataSourceConfig.java          # 数据源和 JdbcTemplate 配置
│   │   ├── model/
│   │   │   ├── Student.java                   # 学生实体（原有）
│   │   │   ├── ClassInfo.java                 # 班级实体（原有）
│   │   │   └── User.java                      # 用户实体（新增）
│   │   ├── dao/
│   │   │   ├── StudentDao.java                # 学生数据访问层（原有）
│   │   │   ├── ClassDao.java                  # 班级数据访问层（原有）
│   │   │   └── UserDao.java                   # 用户数据访问层（新增）
│   │   ├── service/
│   │   │   └── UserService.java               # 用户业务逻辑层（新增）
│   │   ├── controller/
│   │   │   ├── AuthController.java            # 认证控制器（原有）
│   │   │   ├── StudentController.java         # 学生控制器（原有）
│   │   │   ├── ClassController.java           # 班级控制器（原有）
│   │   │   └── UserController.java            # 用户控制器（新增）
│   │   └── UserDemoApplication.java
│   └── resources/
│       └── application.properties              # 应用配置文件
├── init-db.sql                                 # 数据库初始化脚本
└── pom.xml                                     # Maven 配置文件
```

## 数据库配置

### 1. 创建数据库和用户表

首先，在 MySQL 中创建 `frog` 数据库，然后执行 `init-db.sql` 脚本创建 `user` 表：

```bash
# 使用以下命令导入 SQL 脚本
mysql -u root -p frog < init-db.sql
```

或者手动执行：

```sql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS frog CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE frog;

-- 创建用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(100) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    status INT DEFAULT 1 COMMENT '状态 1:激活 0:禁用',
    created_time BIGINT NOT NULL COMMENT '创建时间',
    updated_time BIGINT NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX idx_username (username),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 插入测试数据
INSERT INTO user (username, password, email, phone, status, created_time, updated_time) VALUES
('admin', '123456', 'admin@example.com', '13800138000', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
('user1', 'password1', 'user1@example.com', '13800138001', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
('user2', 'password2', 'user2@example.com', '13800138002', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);
```

### 2. 数据库连接信息

配置文件位置：`src/main/resources/application.properties`

```properties
# 数据库配置
user:datasource:jdbc-url=jdbc:mysql://localhost:3306/frog?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC
user:datasource:username=frog_admin
user:datasource:password=frog798
user:datasource:driver-class-name=com.mysql.cj.jdbc.Driver
```

如果您使用不同的数据库用户名/密码，请在 `DataSourceConfig.java` 中修改以下代码：

```java
config.setJdbcUrl("你的数据库 URL");
config.setUsername("你的用户名");
config.setPassword("你的密码");
```

## 架构设计

### 1. Model (模型层)

**User.java** - 用户实体类

```java
public class User {
    private Long id;              // 用户ID
    private String username;      // 用户名
    private String password;      // 密码
    private String email;         // 邮箱
    private String phone;         // 手机号
    private Integer status;       // 状态（1: 激活, 0: 禁用）
    private Long createdTime;     // 创建时间
    private Long updatedTime;     // 更新时间
}
```

### 2. DAO (数据访问层)

**UserDao.java** - 使用 JdbcTemplate 进行数据库操作

主要方法：
- `create(User user)` - 创建用户
- `findById(Long id)` - 根据 ID 查询用户
- `findByUsername(String username)` - 根据用户名查询用户
- `findAll()` - 查询所有用户
- `update(User user)` - 更新用户
- `delete(Long id)` - 删除用户
- `deleteByUsername(String username)` - 根据用户名删除用户
- `count()` - 获取用户总数
- `findActiveUsers()` - 查询激活的用户

### 3. Service (业务逻辑层)

**UserService.java** - 业务逻辑处理

主要方法：
- `createUser(User user)` - 创建用户（包含重复检查）
- `getUserById(Long id)` - 获取用户详情
- `getAllUsers()` - 获取所有用户
- `updateUser(User user)` - 更新用户
- `deleteUser(Long id)` - 删除用户
- `validateLogin(String username, String password)` - 验证登录
- `changePassword(Long userId, String oldPassword, String newPassword)` - 修改密码

### 4. Controller (控制层)

**UserController.java** - REST API 接口

## API 接口文档

### 1. 获取用户列表

```
GET /user/list

响应:
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
        },
        ...
    ]
}
```

### 2. 获取激活的用户列表

```
GET /user/active

响应:
{
    "code": 200,
    "message": "获取激活用户列表成功",
    "data": [...]
}
```

### 3. 根据 ID 获取用户详情

```
GET /user/get?id=1

响应:
{
    "code": 200,
    "message": "获取用户详情成功",
    "data": {
        "id": 1,
        "username": "admin",
        "email": "admin@example.com",
        ...
    }
}
```

### 4. 根据用户名获取用户

```
GET /user/getByUsername?username=admin

响应:
{
    "code": 200,
    "message": "获取用户详情成功",
    "data": {...}
}
```

### 5. 获取用户总数

```
GET /user/count

响应:
{
    "code": 200,
    "message": "获取用户总数成功",
    "data": 3
}
```

### 6. 创建用户

```
POST /user/create

请求体:
{
    "username": "newuser",
    "password": "password123",
    "email": "newuser@example.com",
    "phone": "13800138888"
}

响应:
{
    "code": 201,
    "message": "创建用户成功"
}
```

### 7. 更新用户

```
POST /user/update

请求体:
{
    "id": 1,
    "username": "admin",
    "password": "newpassword",
    "email": "admin@newemail.com",
    "phone": "13800138000",
    "status": 1
}

响应:
{
    "code": 200,
    "message": "更新用户成功"
}
```

### 8. 删除用户

```
POST /user/delete?id=1

响应:
{
    "code": 200,
    "message": "删除用户成功"
}
```

### 9. 用户登录

```
POST /user/login

请求体:
{
    "username": "admin",
    "password": "123456"
}

响应（成功）:
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

响应（失败）:
{
    "code": 401,
    "message": "用户名或密码错误"
}
```

## 使用 JdbcTemplate 的示例代码

### 查询单个对象

```java
public User findById(Long id) {
    String sql = "SELECT id, username, password, email, phone, status, created_time, updated_time FROM user WHERE id = ?";
    return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        // ... 其他字段赋值
        return user;
    }, id);
}
```

### 查询列表

```java
public List<User> findAll() {
    String sql = "SELECT id, username, password, email, phone, status, created_time, updated_time FROM user";
    return jdbcTemplate.query(sql, (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getLong("id"));
        // ... 其他字段赋值
        return user;
    });
}
```

### 执行更新

```java
public int create(User user) {
    String sql = "INSERT INTO user (username, password, email, phone, status, created_time, updated_time) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";
    return jdbcTemplate.update(sql,
            user.getUsername(),
            user.getPassword(),
            user.getEmail(),
            user.getPhone(),
            user.getStatus(),
            user.getCreatedTime(),
            user.getUpdatedTime());
}
```

## 运行项目

### 1. Web 模式

```bash
# 编译和打包
mvn clean package

# 运行（启动 Web 服务器）
java -jar target/springwind-user-demo-*.jar --web

# 或使用 Maven 插件
mvn exec:java -Dexec.args="--web"
```

访问 API：`http://localhost:8080/user/list`

### 2. 控制台模式

```bash
# 运行应用（控制台模式）
java -jar target/springwind-user-demo-*.jar

# 或使用 Maven 插件
mvn exec:java
```

## 技术栈

- **框架**: Springwind IoC 框架
- **Web 容器**: 嵌入式 Tomcat 11.0.11
- **数据库**: MySQL 8.0+
- **连接池**: HikariCP 5.1.0
- **数据库驱动**: MySQL Connector/J 8.0.33
- **JSON 处理**: Jackson 2.16.0
- **Java 版本**: 17+

## 注意事项

1. **数据库连接**: 确保 MySQL 数据库已启动，且数据库用户名和密码与配置匹配
2. **连接池**: 使用 HikariCP 作为连接池实现，具有高性能和稳定性
3. **事务管理**: 当前示例未使用事务管理，实际项目中建议添加事务支持
4. **密码安全**: 示例中使用明文密码，实际项目中应该使用密码加密
5. **错误处理**: DAO 层将 SQLException 包装为 RuntimeException，实际项目中可根据需要定制

## 扩展建议

1. 添加事务管理支持
2. 添加密码加密功能（如 BCrypt）
3. 添加用户角色和权限管理
4. 添加分页查询功能
5. 添加日期字段优化（使用 LocalDateTime 替代时间戳）
6. 添加数据验证和业务规则检查
7. 集成缓存（Redis）来提升查询性能

## 相关文件

- [User 实体类](src/main/java/com/github/microwind/userdemo/model/User.java)
- [UserDao 数据访问层](src/main/java/com/github/microwind/userdemo/dao/UserDao.java)
- [UserService 业务逻辑层](src/main/java/com/github/microwind/userdemo/service/UserService.java)
- [UserController 控制层](src/main/java/com/github/microwind/userdemo/controller/UserController.java)
- [DataSourceConfig 数据源配置](src/main/java/com/github/microwind/userdemo/config/DataSourceConfig.java)
- [数据库初始化脚本](init-db.sql)
