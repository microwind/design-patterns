# User Demo - 数据库操作示例 - 文件索引

快速导航：找到您需要的文件和文档

## 📁 文件结构导航

```
examples/user-demo/
│
├── 📄 核心配置文件
│   ├── pom.xml                           # Maven 依赖配置 (已更新 - 添加 MySQL 和 HikariCP)
│   └── README.md                         # 项目说明 (已更新)
│
├── 📚 文档文件
│   ├── QUICK_START.md                    # ⭐ 快速开始指南 (推荐新用户首先阅读)
│   ├── DATABASE_OPERATIONS.md            # 详细的数据库操作文档
│   ├── IMPLEMENTATION_SUMMARY.md         # 项目实现总结
│   └── FILE_INDEX.md                     # 本文件
│
├── 🗄️ 数据库配置
│   ├── init-db.sql                       # 数据库初始化脚本 (创建表和插入测试数据)
│   └── src/main/resources/
│       └── application.properties        # 应用配置文件 (数据库连接参数)
│
├── 🧪 测试文件
│   └── test-user-api.sh                  # API 自动化测试脚本
│
└── 💻 源代码
    └── src/main/java/com/github/microwind/userdemo/
        │
        ├── 🎯 配置层 (config/)
        │   └── DataSourceConfig.java     # 数据源和 JdbcTemplate 配置
        │
        ├── 🎮 控制层 (controller/)
        │   ├── UserController.java       # ⭐ 用户管理 REST API [新增]
        │   ├── AuthController.java       # 用户认证 (原有)
        │   ├── StudentController.java    # 学生管理 (原有)
        │   └── ClassController.java      # 班级管理 (原有)
        │
        ├── 📦 业务层 (service/)
        │   ├── UserService.java          # ⭐ 用户业务逻辑 [新增]
        │   ├── StudentService.java       # 学生业务逻辑 (原有)
        │   └── ClassService.java         # 班级业务逻辑 (原有)
        │
        ├── 🗂️ 数据访问层 (dao/)
        │   ├── UserDao.java              # ⭐ 用户数据访问 (使用 JdbcTemplate) [新增]
        │   ├── StudentDao.java           # 学生数据访问 (内存存储，原有)
        │   └── ClassDao.java             # 班级数据访问 (内存存储，原有)
        │
        ├── 🏛️ 数据模型 (model/)
        │   ├── User.java                 # ⭐ 用户实体类 [新增]
        │   ├── Student.java              # 学生实体类 (原有)
        │   └── ClassInfo.java            # 班级实体类 (原有)
        │
        └── 🚀 应用入口
            └── UserDemoApplication.java  # 应用启动类 (已更新)
```

## 🎯 按用途查找文件

### 🚀 快速开始

1. **第一次使用？** 
   - 阅读 [QUICK_START.md](QUICK_START.md)
   - 按照步骤初始化数据库和启动应用

2. **需要数据库脚本？**
   - 查看 [init-db.sql](init-db.sql)

3. **想测试 API？**
   - 使用 [test-user-api.sh](test-user-api.sh)

### 💻 学习代码

#### 入门级 (推荐顺序)

1. **数据模型** → [User.java](src/main/java/com/github/microwind/userdemo/model/User.java)
   - 了解用户实体结构

2. **数据访问** → [UserDao.java](src/main/java/com/github/microwind/userdemo/dao/UserDao.java)
   - 学习如何使用 JdbcTemplate 进行 CRUD 操作
   - 查看 SQL 语句编写

3. **业务逻辑** → [UserService.java](src/main/java/com/github/microwind/userdemo/service/UserService.java)
   - 了解业务规则实现

4. **API 接口** → [UserController.java](src/main/java/com/github/microwind/userdemo/controller/UserController.java)
   - 学习如何编写 REST API
   - 了解请求处理流程

#### 配置级

- **[DataSourceConfig.java](src/main/java/com/github/microwind/userdemo/config/DataSourceConfig.java)**
  - 数据源和 JdbcTemplate 配置
  - HikariCP 连接池配置

- **[application.properties](src/main/resources/application.properties)**
  - 应用级配置

- **[pom.xml](pom.xml)**
  - Maven 依赖管理

### 📖 文档查阅

| 文档 | 适用场景 | 详细程度 |
|------|---------|---------|
| [QUICK_START.md](QUICK_START.md) | 快速开始 | ⭐⭐⭐⭐⭐ |
| [DATABASE_OPERATIONS.md](DATABASE_OPERATIONS.md) | 深入学习 | ⭐⭐⭐⭐ |
| [IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md) | 项目总结 | ⭐⭐⭐ |
| [README.md](README.md) | 项目概览 | ⭐⭐ |

### 🧪 测试相关

- **[test-user-api.sh](test-user-api.sh)**
  - 自动化测试脚本
  - 11 个测试用例
  - 无需手动输入

### 🗄️ 数据库相关

- **[init-db.sql](init-db.sql)**
  - 创建 user 表
  - 插入初始数据

- **[application.properties](src/main/resources/application.properties)**
  - 数据库连接配置
  - 连接池参数

## 📊 功能对应的代码位置

### 用户列表查询

| 组件 | 文件 | 方法 | 说明 |
|------|------|------|------|
| 控制层 | UserController.java | `list()` | GET /user/list |
| 业务层 | UserService.java | `getAllUsers()` | 获取所有用户 |
| 数据层 | UserDao.java | `findAll()` | 执行 SELECT 查询 |
| 模型 | User.java | - | 用户实体 |

### 用户创建

| 组件 | 文件 | 方法 |
|------|------|------|
| 控制层 | UserController.java | `create()` |
| 业务层 | UserService.java | `createUser()` |
| 数据层 | UserDao.java | `create()` |

### 用户更新

| 组件 | 文件 | 方法 |
|------|------|------|
| 控制层 | UserController.java | `update()` |
| 业务层 | UserService.java | `updateUser()` |
| 数据层 | UserDao.java | `update()` |

### 用户删除

| 组件 | 文件 | 方法 |
|------|------|------|
| 控制层 | UserController.java | `delete()` |
| 业务层 | UserService.java | `deleteUser()` |
| 数据层 | UserDao.java | `delete()` |

### 用户查询

| 组件 | 文件 | 方法 |
|------|------|------|
| 控制层 | UserController.java | `getById()`, `getByUsername()` |
| 业务层 | UserService.java | `getUserById()`, `getUserByUsername()` |
| 数据层 | UserDao.java | `findById()`, `findByUsername()` |

### 用户登录

| 组件 | 文件 | 方法 |
|------|------|------|
| 控制层 | UserController.java | `login()` |
| 业务层 | UserService.java | `validateLogin()` |
| 数据层 | UserDao.java | `findByUsername()` |

## 🔗 关键代码片段位置

### JdbcTemplate 的使用

#### 查询单个对象
```java
// 文件: UserDao.java, 方法: findById()
jdbcTemplate.queryForObject(sql, (rs, rowNum) -> { ... }, id)
```

#### 查询列表
```java
// 文件: UserDao.java, 方法: findAll()
jdbcTemplate.query(sql, (rs, rowNum) -> { ... })
```

#### 执行更新
```java
// 文件: UserDao.java, 方法: create(), update(), delete()
jdbcTemplate.update(sql, param1, param2, ...)
```

### Springwind 注解的使用

- `@Configuration` - [DataSourceConfig.java](src/main/java/com/github/microwind/userdemo/config/DataSourceConfig.java)
- `@Bean` - [DataSourceConfig.java](src/main/java/com/github/microwind/userdemo/config/DataSourceConfig.java)
- `@Controller` - [UserController.java](src/main/java/com/github/microwind/userdemo/controller/UserController.java)
- `@Service` - [UserService.java](src/main/java/com/github/microwind/userdemo/service/UserService.java)
- `@Repository` - [UserDao.java](src/main/java/com/github/microwind/userdemo/dao/UserDao.java)
- `@Autowired` - 所有业务类
- `@RequestMapping` - [UserController.java](src/main/java/com/github/microwind/userdemo/controller/UserController.java)
- `@ResponseBody` - [UserController.java](src/main/java/com/github/microwind/userdemo/controller/UserController.java)

## 🎓 学习路线

### 第 1 天：快速入门
1. 阅读 [QUICK_START.md](QUICK_START.md)
2. 按步骤初始化数据库
3. 启动应用并访问 API

### 第 2 天：代码学习
1. 查看 [User.java](src/main/java/com/github/microwind/userdemo/model/User.java) - 数据模型
2. 查看 [UserDao.java](src/main/java/com/github/microwind/userdemo/dao/UserDao.java) - CRUD 操作
3. 查看 [UserService.java](src/main/java/com/github/microwind/userdemo/service/UserService.java) - 业务逻辑
4. 查看 [UserController.java](src/main/java/com/github/microwind/userdemo/controller/UserController.java) - API 接口

### 第 3 天：深入学习
1. 阅读 [DATABASE_OPERATIONS.md](DATABASE_OPERATIONS.md) 完整文档
2. 修改代码进行扩展
3. 添加新功能（如分页、排序）

## 📞 常见问题快速链接

| 问题 | 位置 |
|------|------|
| 如何启动应用？ | [QUICK_START.md - 快速开始](QUICK_START.md#第三步启动应用) |
| 数据库如何初始化？ | [QUICK_START.md - 数据库配置](QUICK_START.md#1-创建数据库和用户表) |
| API 有哪些？ | [DATABASE_OPERATIONS.md - API 文档](DATABASE_OPERATIONS.md#api-接口文档) |
| 如何测试 API？ | [test-user-api.sh](test-user-api.sh) |
| JdbcTemplate 怎么用？ | [DATABASE_OPERATIONS.md - JdbcTemplate](DATABASE_OPERATIONS.md#使用-jdbctemplate-的示例代码) |
| 连接不上数据库？ | [QUICK_START.md - 常见问题](QUICK_START.md#-常见问题解决) |

## 🏗️ 架构相关文件

**分层架构图和说明** → [QUICK_START.md - 项目架构](QUICK_START.md#-项目架构)

**详细的架构设计** → [DATABASE_OPERATIONS.md - 架构设计](DATABASE_OPERATIONS.md#架构设计)

## 📋 新增文件清单

### 新增的 8 个 Java 文件
- ✅ [DataSourceConfig.java](src/main/java/com/github/microwind/userdemo/config/DataSourceConfig.java)
- ✅ [User.java](src/main/java/com/github/microwind/userdemo/model/User.java)
- ✅ [UserDao.java](src/main/java/com/github/microwind/userdemo/dao/UserDao.java)
- ✅ [UserService.java](src/main/java/com/github/microwind/userdemo/service/UserService.java)
- ✅ [UserController.java](src/main/java/com/github/microwind/userdemo/controller/UserController.java)

### 新增的 4 个文档文件
- ✅ [DATABASE_OPERATIONS.md](DATABASE_OPERATIONS.md)
- ✅ [QUICK_START.md](QUICK_START.md)
- ✅ [IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md)
- ✅ [FILE_INDEX.md](FILE_INDEX.md) - 本文件

### 新增的 2 个配置文件
- ✅ [init-db.sql](init-db.sql)
- ✅ [application.properties](src/main/resources/application.properties)

### 新增的 1 个脚本文件
- ✅ [test-user-api.sh](test-user-api.sh)

### 修改的文件
- 📝 [pom.xml](pom.xml) - 添加 MySQL 和 HikariCP 依赖
- 📝 [README.md](README.md) - 更新项目说明
- 📝 [UserDemoApplication.java](src/main/java/com/github/microwind/userdemo/UserDemoApplication.java) - 添加 User 模块支持

## ⭐ 推荐阅读顺序

1. 本文件 (FILE_INDEX.md) - 您现在正在阅读
2. [QUICK_START.md](QUICK_START.md) - 快速开始指南
3. [User.java](src/main/java/com/github/microwind/userdemo/model/User.java) - 数据模型
4. [UserDao.java](src/main/java/com/github/microwind/userdemo/dao/UserDao.java) - 数据访问
5. [UserController.java](src/main/java/com/github/microwind/userdemo/controller/UserController.java) - REST API
6. [DATABASE_OPERATIONS.md](DATABASE_OPERATIONS.md) - 详细文档
7. [test-user-api.sh](test-user-api.sh) - 测试示例

---

**文档版本**: 1.0  
**最后更新**: 2024 年 1 月 14 日  
**状态**: ✅ 完成
