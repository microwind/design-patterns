# User Demo - 学生信息管理系统

这是一个基于SpringWind框架的简单学生信息管理系统示例，用于演示SpringWind框架的核心功能。

## 项目说明

本项目是SpringWind框架的示例项目，展示了框架的以下核心能力：

- **IoC容器**: 通过注解管理Bean的生命周期
- **依赖注入**: 使用@Autowired自动注入依赖
- **组件扫描**: 自动扫描@Controller、@Service、@Repository注解的类
- **分层架构**: Controller -> Service -> Dao 的标准三层架构

## 项目结构

```
user-demo/
├── pom.xml
└── src/main/java/com/github/microwind/userdemo/
    ├── UserDemoApplication.java  # 启动类
    ├── controller/               # 控制器层
    │   ├── AuthController.java   # 用户登录
    │   ├── StudentController.java # 学生信息查询
    │   └── ClassController.java  # 班级信息查询
    ├── service/                  # 服务层
    │   ├── StudentService.java
    │   └── ClassService.java
    ├── dao/                      # 数据访问层
    │   ├── StudentDao.java
    │   └── ClassDao.java
    └── model/                    # 数据模型
        ├── Student.java
        └── ClassInfo.java
```

## 功能列表

1. **用户登录** (`/auth/login`)
   - 简单的用户名密码验证
   - 演示Service层的业务逻辑调用

2. **获取学生详细信息** (`/student/detail`)
   - 根据ID查询学生信息
   - 演示完整的Controller -> Service -> Dao调用链

3. **班级列表** (`/class/list`)
   - 获取所有班级信息
   - 演示List数据的处理

## 快速开始

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
