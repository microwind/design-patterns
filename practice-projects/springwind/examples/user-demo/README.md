# User Demo - SpringWind 框架教程示例

这是一个**基于 SpringWind 框架的教程示例项目**，**模拟了 Spring MVC 框架**的核心功能。SpringWind 是一个轻量级的 Java Web 框架，实现了类似 Spring 的 IoC 容器、依赖注入、MVC 模式等核心特性。

## 关于 SpringWind 框架

SpringWind 是一个教学性质的轻量级框架，旨在帮助开发者理解 Spring 框架的核心原理。它实现了：

- **IoC 容器**：类似 Spring 的依赖注入容器
- **MVC 模式**：模拟 Spring MVC 的请求处理机制
- **注解驱动**：使用 `@Controller`、`@Service`、`@Repository` 等注解
- **依赖注入**：通过 `@Autowired` 实现自动装配
- **组件扫描**：自动发现和注册 Bean
- **请求映射**：类似 `@RequestMapping` 的 URL 映射机制
- **数据库操作**：提供类似 Spring JdbcTemplate 的数据库访问层

## 项目简介

本项目包含两个模块，展示了 SpringWind 框架的不同应用场景：

### 模块 1：学生信息管理（内存存储）
- **Student/Class**: 原有的学生信息管理系统
- 使用内存存储数据
- 演示基本的 MVC 架构和依赖注入

### 模块 2：用户管理系统（数据库操作）
- **User**: 新增的用户管理系统
- 使用 MySQL 数据库存储
- 使用 JdbcTemplate 进行数据库 CRUD 操作
- 展示完整的分层架构和数据持久化

## 项目说明

本项目是 SpringWind 框架的示例项目，展示了框架的以下核心能力：

- **IoC 容器**：通过注解管理 Bean 的生命周期
- **依赖注入**：使用 `@Autowired` 自动注入依赖
- **组件扫描**：自动扫描 `@Controller`、`@Service`、`@Repository` 注解的类
- **MVC 模式**：模拟 Spring MVC 的 Controller -> Service -> Dao 三层架构
- **JSON 响应**：所有 API 接口统一返回 JSON 格式数据
- **ApiResponse 封装**：提供优雅的响应构建工具类，简化代码
- **PageResult 分页**：独立的分页结果对象，便于扩展和维护
- **数据库操作**：使用 JdbcTemplate 进行数据库 CRUD 操作
- **连接池管理**：HikariCP 高性能连接池配置

## 项目结构

```
user-demo/
├── pom.xml
├── init-db.sql                              # 数据库初始化脚本
├── DATABASE_OPERATIONS.md                   # 数据库操作详细文档
├── QUICK_START.md                           # 快速开始指南
├── test-user-api.sh                         # API 自动化测试脚本
├── src/main/
│   ├── java/com/github/microwind/userdemo/
│   │   ├── UserDemoApplication.java         # 启动类
│   │   ├── config/                          # 配置层
│   │   │   └── DataSourceConfig.java        # 数据源和 JdbcTemplate 配置
│   │   ├── controller/                      # 控制器层
│   │   │   ├── AuthController.java          # 用户登录（原有）
│   │   │   ├── StudentController.java       # 学生信息查询（原有）
│   │   │   ├── ClassController.java         # 班级信息查询（原有）
│   │   │   ├── IndexController.java         # 首页控制器
│   │   │   └── UserController.java          # 用户管理（新增，数据库）
│   │   ├── service/                         # 服务层
│   │   │   ├── StudentService.java          # 学生服务（原有）
│   │   │   ├── ClassService.java            # 班级服务（原有）
│   │   │   └── UserService.java             # 用户服务（新增，数据库）
│   │   ├── dao/                             # 数据访问层
│   │   │   ├── StudentDao.java              # 学生 DAO（原有，内存）
│   │   │   ├── ClassDao.java                # 班级 DAO（原有，内存）
│   │   │   └── UserDao.java                 # 用户 DAO（新增，数据库）
│   │   ├── model/                           # 数据模型
│   │   │   ├── Student.java                 # 学生实体（原有）
│   │   │   ├── ClassInfo.java               # 班级实体（原有）
│   │   │   └── User.java                    # 用户实体（新增）
│   │   └── utils/                           # 工具类
│   │       ├── ApiResponse.java             # API 响应封装（优雅的响应构建）
│   │       ├── PageResult.java              # 分页结果封装（独立的分页对象）
│   │       └── JsonUtil.java                # JSON 工具类
│   └── resources/
│       └── application.properties            # 应用配置（新增）
└── target/                                  # 编译输出
```

## 功能列表

### 原有功能（内存存储）

1. **用户登录** (`/auth/login`)
   - 简单的用户名密码验证
   - 演示Service层的业务逻辑调用

2. **获取学生详细信息** (`/student/detail`)
   - 根据ID查询学生信息
   - 演示完整的Controller -> Service -> Dao调用链

3. **班级列表** (`/class/list`)
   - 获取所有班级信息
   - 演示List数据的处理

### 新增功能（数据库操作 - JdbcTemplate）

#### 用户管理 API - 完整的 CRUD 操作

**特性说明**：
- ✅ **统一 JSON 响应**：所有接口返回标准的 JSON 格式
- ✅ **实体数据返回**：直接返回 User 实体对象
- ✅ **分页支持**：列表接口支持分页查询，返回分页元数据
- ✅ **ApiResponse 封装**：使用优雅的 ApiResponse 工具类构建响应，代码更简洁
- ✅ **PageResult 对象**：独立的分页结果对象，提供丰富的分页信息和便捷方法

**接口列表**：

1. **获取用户列表**
   - `GET /user/list` - 获取所有用户
   - `GET /user/list?page=1&pageSize=10` - 分页查询用户
   - 返回格式（分页）：
     ```json
     {
       "code": 200,
       "message": "获取用户列表成功",
       "data": {
         "list": [...],       // 用户列表
         "page": 1,           // 当前页码
         "pageSize": 10,      // 每页大小
         "total": 100,        // 总记录数
         "totalPages": 10     // 总页数
       }
     }
     ```

2. **获取单个用户**
   - `GET /user/get?id=1` - 根据 ID 获取用户
   - `GET /user/get-by-name?name=admin` - 根据用户名获取用户
   - 返回格式：
     ```json
     {
       "code": 200,
       "message": "获取用户详情成功",
       "data": {
         "id": 1,
         "name": "admin",
         "email": "admin@example.com",
         "phone": "13800138000",
         "createdTime": 1234567890000,
         "updatedTime": 1234567890000
       }
     }
     ```

3. **其他操作**
   - `GET /user/count` - 获取用户总数
   - `POST /user/create` - 创建用户
   - `POST /user/update` - 更新用户
   - `POST /user/delete?id=1` - 删除用户
   - `POST /user/login` - 用户登录验证

## 快速开始

### 数据库操作示例

如果您只对数据库操作感兴趣，请查看详细指南：

- **快速开始**: [QUICK_START.md](QUICK_START.md)
- **详细文档**: [DATABASE_OPERATIONS.md](DATABASE_OPERATIONS.md)

基本步骤：

1. 创建 MySQL 数据库和用户
2. 执行 `init-db.sql` 初始化表和数据
3. 修改 `DataSourceConfig.java` 中的数据库连接信息（如需要）
4. 启动应用：`mvn exec:java -Dexec.args="--web"`
5. 访问 API：`http://localhost:8080/user/list`

### 原有示例

继续按照以下方式运行原有的学生管理示例：


### 前置条件

- JDK 17+
- Maven 3.6+
- SpringWind框架已安装到本地Maven仓库

### 安装SpringWind框架

```bash
# 在springwind项目根目录执行
cd ../..
mvn clean install
```

### 运行示例

本项目支持两种运行模式：

#### 模式1: 控制台模式 (默认)

直接在控制台运行，查看框架功能演示：

```bash
# 进入user-demo目录
cd examples/user-demo

# 编译项目
mvn clean compile

# 运行示例（控制台模式）
mvn exec:java
```

#### 模式2: Web模式

启动嵌入式Tomcat服务器，通过浏览器访问：

```bash
# 进入user-demo目录
cd examples/user-demo

# 编译项目
mvn clean compile
# 依赖打包到同一个 jar 中
# mvn clean package assembly:single

# 启动Web服务器
mvn exec:java -Dexec.args="--web"
* 运行模式：
* 1. Web模式：java -jar xxx.jar --web (启动嵌入式Tomcat)
  $ java -jar ./target/springwind-user-demo-1.0-SNAPSHOT.jar --web
* 2. 控制台模式：java -jar xxx.jar (直接在控制台运行)
```

服务器启动后，访问以下URL：

- http://localhost:8080/auth/login - 用户登录
- http://localhost:8080/student/detail - 学生详细信息
- http://localhost:8080/class/list - 班级列表

## 控制台模式输出

运行后将看到以下输出：

```
========== 学生信息管理系统 ==========
启动SpringWind应用...

SpringWind容器启动成功！

========== 测试1: 用户登录 ==========
登录成功！

========== 测试2: 获取学生详细信息 ==========
学生详情：Student{id=1, name='张三', age=20, classId='CS101', email='zhangsan@example.com'}

========== 测试3: 获取班级列表 ==========
班级列表：
  ClassInfo{id='CS101', name='计算机科学101班', grade='2023', studentCount=30}
  ClassInfo{id='CS102', name='计算机科学102班', grade='2023', studentCount=28}
  ClassInfo{id='SE101', name='软件工程101班', grade='2023', studentCount=32}

========== 演示完成 ==========
```

## 技术要点

### 1. SpringWind 框架与 Spring MVC 的对应关系

| Spring MVC | SpringWind | 说明 |
|------------|-----------|------|
| `@Controller` | `@Controller` | 标记控制器类 |
| `@Service` | `@Service` | 标记服务类 |
| `@Repository` | `@Repository` | 标记数据访问类 |
| `@Autowired` | `@Autowired` | 依赖注入 |
| `@RequestMapping` | `@RequestMapping` | URL 请求映射 |
| `@ResponseBody` | `@ResponseBody` | JSON 响应 |
| `DispatcherServlet` | `DispatcherServlet` | 前端控制器 |
| `JdbcTemplate` | `JdbcTemplate` | 数据库操作模板 |
| `ApplicationContext` | `ApplicationContext` | IoC 容器 |

### 2. 依赖注入示例

```java
@Controller
public class StudentController {
    @Autowired
    private StudentService studentService;  // 自动注入
}
```

### 3. 组件注解

- `@Controller`: 标记控制器类
- `@Service`: 标记服务类
- `@Repository`: 标记数据访问类

### 4. 请求映射

```java
@RequestMapping("/student")
public class StudentController {
    @RequestMapping("/detail")
    public String getDetail() { ... }
}
```

### 5. JSON 响应示例

SpringWind 框架通过 `@ResponseBody` 注解和 `JsonResult` 实现 JSON 响应。本项目进一步封装了 `ApiResponse` 工具类，提供更优雅的 API 响应方式：

#### ApiResponse 工具类

`ApiResponse` 提供了丰富的静态方法来创建各种响应：

```java
// 成功响应（带数据）
ApiResponse.success(user, "获取用户成功")

// 成功响应（默认消息）
ApiResponse.success(user)

// 分页响应
ApiResponse.page(users, page, pageSize, total)

// 失败响应
ApiResponse.failure("操作失败")
ApiResponse.badRequest("参数错误")       // 400
ApiResponse.notFound("资源不存在")       // 404
ApiResponse.unauthorized("未授权")      // 401
```

#### Controller 中的使用示例

```java
@Controller
@RequestMapping("/user")
public class UserController {

    @RequestMapping("/get")
    @ResponseBody
    public ViewResult getById(HttpServletRequest request) {
        try {
            String idStr = request.getParameter("id");
            if (idStr == null || idStr.isEmpty()) {
                return new JsonResult(ApiResponse.badRequest("用户 ID 不能为空").toMap());
            }
            Long id = Long.parseLong(idStr);
            User user = userService.getUserById(id);
            if (user == null) {
                return new JsonResult(ApiResponse.notFound("用户不存在").toMap());
            }
            return new JsonResult(ApiResponse.success(user, "获取用户详情成功").toMap());
        } catch (Exception e) {
            return new JsonResult(ApiResponse.failure("获取用户失败: " + e.getMessage()).toMap());
        }
    }
}
```

**代码对比**：

传统方式（手动构建 Map）：
```java
Map<String, Object> result = new HashMap<>();
result.put("code", 200);
result.put("message", "获取用户成功");
result.put("data", user);
return new JsonResult(result);
```

使用 ApiResponse（简洁优雅）：
```java
return new JsonResult(ApiResponse.success(user, "获取用户成功").toMap());
```

### 6. 分页查询示例

本项目使用独立的 `PageResult` 对象封装分页数据，使分页功能更加清晰和易于扩展。

#### PageResult 分页结果对象

`PageResult` 是一个泛型类，提供了完整的分页信息：

**核心字段**：
- `list`: 数据列表
- `page`: 当前页码（从1开始）
- `pageSize`: 每页大小
- `total`: 总记录数
- `totalPages`: 总页数

**扩展字段**：
- `hasPrevious`: 是否有上一页
- `hasNext`: 是否有下一页
- `isFirst`: 是否是第一页
- `isLast`: 是否是最后一页

**便捷方法**：
- `getNextPage()`: 获取下一页页码
- `getPreviousPage()`: 获取上一页页码
- `getStartIndex()`: 获取当前页起始索引
- `getEndIndex()`: 获取当前页结束索引

#### 使用方式

**方式 1：使用 ApiResponse.page() 自动创建**（推荐）

```java
@RequestMapping("/list")
@ResponseBody
public ViewResult list(HttpServletRequest request) {
    int page = Integer.parseInt(request.getParameter("page"));
    int pageSize = Integer.parseInt(request.getParameter("pageSize"));

    List<User> users = userService.getUsersByPage(page, pageSize);
    Long total = userService.getUserCount();

    // ApiResponse.page() 会自动创建 PageResult 对象
    return new JsonResult(ApiResponse.page(users, page, pageSize, total).toMap());
}
```

**方式 2：手动创建 PageResult 对象**

```java
// 创建分页结果
PageResult<User> pageResult = PageResult.of(users, page, pageSize, total);

// 使用分页结果创建响应
return new JsonResult(ApiResponse.page(pageResult).toMap());
```

**方式 3：创建空的分页结果**

```java
// 当没有数据时，返回空的分页结果
PageResult<User> emptyPage = PageResult.empty(page, pageSize);
return new JsonResult(ApiResponse.page(emptyPage).toMap());
```

**返回的分页数据格式**：

```json
{
  "code": 200,
  "message": "获取列表成功",
  "data": {
    "list": [...],           // 用户列表
    "page": 2,               // 当前页码
    "pageSize": 10,          // 每页大小
    "total": 100,            // 总记录数
    "totalPages": 10,        // 总页数
    "hasPrevious": true,     // 是否有上一页
    "hasNext": true,         // 是否有下一页
    "first": false,          // 是否是第一页
    "last": false            // 是否是最后一页
  }
}
```

#### PageResult 的优势

1. **类型安全**：泛型支持，编译时类型检查
2. **功能丰富**：提供了丰富的分页状态和便捷方法
3. **易于扩展**：可以轻松添加新的分页相关字段或方法
4. **代码清晰**：分页逻辑集中在一个类中，易于维护
5. **统一标准**：所有分页接口返回统一的分页格式

#### 扩展示例

如果需要添加更多分页功能，只需在 `PageResult` 类中扩展：

```java
public class PageResult<T> {
    // 可以添加更多字段
    private int startRow;      // 起始行号
    private int endRow;        // 结束行号
    private boolean empty;     // 是否为空

    // 可以添加更多方法
    public boolean isEmpty() {
        return list == null || list.isEmpty();
    }

    public int getSize() {
        return list != null ? list.size() : 0;
    }
}
```

#### 前端使用示例

PageResult 提供的丰富字段让前端分页组件的实现变得简单：

```javascript
// 获取分页数据
fetch('/user/list?page=2&pageSize=10')
  .then(res => res.json())
  .then(response => {
    const pageData = response.data;

    // 渲染列表
    renderUserList(pageData.list);

    // 控制分页按钮
    prevButton.disabled = !pageData.hasPrevious;  // 禁用/启用上一页按钮
    nextButton.disabled = !pageData.hasNext;      // 禁用/启用下一页按钮

    // 显示分页信息
    pageInfo.textContent = `第 ${pageData.page}/${pageData.totalPages} 页，共 ${pageData.total} 条记录`;
  });
```

## 代码简洁性

本示例遵循"最简洁"原则：

- 使用内存数据和数据库两种方式，满足不同学习需求
- 精简的业务逻辑，专注于框架功能演示
- 标准的三层架构（Controller -> Service -> Dao），清晰易懂
- 每个类职责单一，代码量控制在最少
- 支持 Web 和控制台两种运行模式
- **统一的 JSON 响应格式**，易于前后端对接
- **ApiResponse 工具类**，一行代码构建响应，代码极其简洁
- **PageResult 分页对象**，独立封装分页逻辑，便于扩展和维护
- **完整的分页功能**，展示真实项目中的常见需求

## Web 模式特性

- **嵌入式 Tomcat**: 无需外部 Web 服务器，开箱即用
- **DispatcherServlet**: SpringWind 的前端控制器自动处理请求分发，模拟 Spring MVC
- **请求映射**: 通过 `@RequestMapping` 注解自动映射 URL 到 Controller 方法
- **JSON 响应**: Controller 方法返回 `JsonResult` 自动序列化为 JSON
- **实体返回**: 直接返回实体对象，框架自动转换为 JSON

## 与 Spring MVC 的对比

本项目通过 SpringWind 框架模拟了 Spring MVC 的核心功能：

| 功能特性 | Spring MVC | SpringWind | 本项目示例 |
|---------|-----------|-----------|-----------|
| IoC 容器 | ✅ | ✅ | UserController/UserService/UserDao |
| 依赖注入 | ✅ | ✅ | @Autowired 自动注入 |
| 请求映射 | ✅ | ✅ | @RequestMapping 注解 |
| JSON 响应 | ✅ | ✅ | JsonResult 返回值 |
| 数据库操作 | ✅ | ✅ | JdbcTemplate CRUD |
| 分页查询 | ✅ | ✅ | /user/list?page=1&pageSize=10 |
| 三层架构 | ✅ | ✅ | Controller/Service/Dao |

## 扩展建议

如需扩展此示例，可以考虑：

1. ✅ ~~添加数据库支持（使用SpringWind的JdbcTemplate）~~ - **已完成**
2. ✅ ~~添加 JSON 响应支持（返回实体对象）~~ - **已完成**
3. ✅ ~~添加分页查询功能~~ - **已完成**
4. ✅ ~~添加 ApiResponse 工具类封装响应~~ - **已完成**
5. ✅ ~~添加 PageResult 分页对象，独立封装分页逻辑~~ - **已完成**
6. 添加 AOP 切面（如日志、事务）
7. 添加更多业务功能（如用户权限、角色管理）
8. 添加参数验证和异常处理机制
9. 扩展 PageResult（如添加排序、筛选等功能）

## 相关文档

- [SpringWind框架文档](../../README.md)
- [Web Demo示例](../web-demo/README.md)
- [SpringWind IoC容器](../../src/main/java/com/github/microwind/springwind/core/)
- [SpringWind注解说明](../../src/main/java/com/github/microwind/springwind/annotation/)
