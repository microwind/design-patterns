# User Demo - 学生信息管理系统

这是一个基于SpringWind框架的综合示例项目，包含内存存储和数据库操作两个模块：
- **Student/Class**: 原有的学生信息管理系统（内存存储）
- **User**: 新增的用户管理系统（数据库存储，使用 JdbcTemplate）

## 项目说明

本项目是SpringWind框架的示例项目，展示了框架的以下核心能力：

- **IoC容器**: 通过注解管理Bean的生命周期
- **依赖注入**: 使用@Autowired自动注入依赖
- **组件扫描**: 自动扫描@Controller、@Service、@Repository注解的类
- **分层架构**: Controller -> Service -> Dao 的标准三层架构
- **数据库操作**: 使用 JdbcTemplate 进行数据库 CRUD 操作
- **连接池管理**: HikariCP 高性能连接池配置

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
│   │   │   └── UserController.java          # 用户管理（新增，数据库）
│   │   ├── service/                         # 服务层
│   │   │   ├── StudentService.java          # 学生服务（原有）
│   │   │   ├── ClassService.java            # 班级服务（原有）
│   │   │   └── UserService.java             # 用户服务（新增，数据库）
│   │   ├── dao/                             # 数据访问层
│   │   │   ├── StudentDao.java              # 学生 DAO（原有，内存）
│   │   │   ├── ClassDao.java                # 班级 DAO（原有，内存）
│   │   │   └── UserDao.java                 # 用户 DAO（新增，数据库）
│   │   └── model/                           # 数据模型
│   │       ├── Student.java                 # 学生实体（原有）
│   │       ├── ClassInfo.java               # 班级实体（原有）
│   │       └── User.java                    # 用户实体（新增）
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

1. **用户管理** - 完整的 CRUD 操作
   - `GET /user/list` - 获取所有用户
   - `GET /user/get?id=1` - 根据 ID 获取用户
   - `GET /user/getByUsername?username=admin` - 根据用户名获取用户
   - `GET /user/active` - 获取激活的用户
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

### 1. 依赖注入示例

```java
@Controller
public class StudentController {
    @Autowired
    private StudentService studentService;  // 自动注入
}
```

### 2. 组件注解

- `@Controller`: 标记控制器类
- `@Service`: 标记服务类
- `@Repository`: 标记数据访问类

### 3. 请求映射

```java
@RequestMapping("/student")
public class StudentController {
    @RequestMapping("/detail")
    public String getDetail() { ... }
}
```

## 代码简洁性

本示例遵循"最简洁"原则：

- 使用内存数据，无需数据库配置
- 精简的业务逻辑，专注于框架功能演示
- 标准的三层架构，清晰易懂
- 每个类职责单一，代码量控制在最少
- 支持Web和控制台两种运行模式

## Web模式特性

- **嵌入式Tomcat**: 无需外部Web服务器，开箱即用
- **DispatcherServlet**: SpringWind的前端控制器自动处理请求分发
- **请求映射**: 通过@RequestMapping注解自动映射URL到Controller方法
- **文本响应**: Controller方法返回的字符串直接作为响应内容

## 扩展建议

如需扩展此示例，可以考虑：

1. 添加数据库支持（使用SpringWind的JdbcTemplate）
2. 添加AOP切面（如日志、事务）
3. 添加更多业务功能
4. 添加JSON响应支持（返回Map对象）

## 相关文档

- [SpringWind框架文档](../../README.md)
- [Web Demo示例](../web-demo/README.md)
- [SpringWind IoC容器](../../src/main/java/com/github/microwind/springwind/core/)
- [SpringWind注解说明](../../src/main/java/com/github/microwind/springwind/annotation/)
