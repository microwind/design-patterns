# Web Demo - 企业网站内容管理系统

这是一个基于SpringWind框架的企业网站内容管理系统示例，用于演示SpringWind框架在实际项目中的应用。

## 项目说明

本项目模拟"春风公司"的企业网站，包含前台展示和后台管理功能。展示了SpringWind框架的以下特性：

- **IoC容器和依赖注入**: 自动管理各层组件的依赖关系
- **分层架构**: Controller -> Service -> Dao 的经典MVC架构
- **业务场景**: 真实的内容管理系统业务逻辑
- **数据操作**: 栏目和文章的增删改查

## 项目结构

```
web-demo/
├── pom.xml
└── src/main/java/com/github/microwind/webdemo/
    ├── WebDemoApplication.java  # 启动类
    ├── controller/              # 控制器层
    │   ├── HomeController.java     # 首页
    │   ├── ProductController.java  # 产品中心
    │   ├── NewsController.java     # 新闻资讯
    │   └── AdminController.java    # 后台管理
    ├── service/                 # 服务层
    │   ├── ColumnService.java
    │   └── ArticleService.java
    ├── dao/                     # 数据访问层
    │   ├── ColumnDao.java
    │   └── ArticleDao.java
    └── model/                   # 数据模型
        ├── Column.java          # 栏目
        └── Article.java         # 文章
```

## 功能列表

### 前台展示

1. **首页** (`/home/index`)
   - 显示公司简介
   - 展示最新资讯
   - 公司介绍和欢迎信息

2. **产品中心** (`/product/list`)
   - 展示所有产品信息
   - 产品详细介绍
   - 来自数据库的产品内容

3. **新闻资讯** (`/news/list`)
   - 展示公司新闻和行业动态
   - 包含发布时间和作者信息
   - 来自数据库的新闻内容

### 后台管理

4. **栏目管理** (`/admin/columns`)
   - 查看所有栏目
   - 创建新栏目 (`/admin/createColumn`)
   - 栏目配置和排序

5. **内容管理** (`/admin/publishArticle`)
   - 发布新文章
   - 文章管理
   - 内容分类

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
# 进入web-demo目录
cd examples/web-demo

# 编译项目
mvn clean compile

# 运行示例（控制台模式）
mvn exec:java
```

#### 模式2: Web模式

启动嵌入式Tomcat服务器，通过浏览器访问：

```bash
# 进入web-demo目录
cd examples/web-demo

# 编译项目
mvn clean compile

# 启动Web服务器
mvn exec:java -Dexec.args="--web"
```

服务器启动后，访问以下URL：

**前台页面：**
- http://localhost:8080/home/index - 春风公司首页
- http://localhost:8080/product/list - 产品中心
- http://localhost:8080/news/list - 新闻资讯

**后台管理：**
- http://localhost:8080/admin/columns - 查看栏目列表
- http://localhost:8080/admin/createColumn - 创建新栏目
- http://localhost:8080/admin/publishArticle - 发布新文章

## 控制台模式输出

运行后将看到以下输出：

```
========== 春风公司企业网站 ==========
启动SpringWind应用...

SpringWind容器启动成功！

========== 测试1: 访问首页 ==========
=== 欢迎访问春风公司官网 ===
公司简介：春风公司成立于2010年，是一家专注于云计算和大数据领域的创新型企业。
我们致力于为客户提供优质的技术服务和解决方案。

最新资讯：
  - 智能云服务平台
  - 大数据分析系统
  - 春风公司荣获年度创新企业奖

========== 测试2: 访问产品中心 ==========
=== 产品中心 ===
我们的产品：

【智能云服务平台】
  春风公司推出的企业级云服务平台，提供稳定可靠的云计算服务。

【大数据分析系统】
  帮助企业深度挖掘数据价值的专业分析系统。

========== 测试3: 访问新闻资讯 ==========
=== 新闻资讯 ===
最新资讯：

【春风公司荣获年度创新企业奖】
  作者：新闻中心 | 发布时间：2025-10-07T...
  在2024年度科技创新大会上，春风公司荣获年度创新企业奖。
...

========== 演示完成 ==========
```

## 技术要点

### 1. 内存数据存储

```java
@Repository
public class ArticleDao {
    private List<Article> articles = new ArrayList<>();
    // 使用内存List模拟数据库
}
```

### 2. 业务逻辑封装

```java
@Service
public class ArticleService {
    @Autowired
    private ArticleDao articleDao;

    public List<Article> getArticlesByColumnId(Long columnId) {
        return articleDao.findByColumnId(columnId);
    }
}
```

### 3. 控制器处理

```java
@Controller
@RequestMapping("/news")
public class NewsController {
    @Autowired
    private ArticleService articleService;

    @RequestMapping("/list")
    public String list() {
        List<Article> newsList = articleService.getArticlesByColumnId(2L);
        // 处理业务逻辑
    }
}
```

## 数据模型

### 栏目（Column）
- id: 栏目ID
- name: 栏目名称
- description: 栏目描述
- sort: 排序
- createTime: 创建时间

### 文章（Article）
- id: 文章ID
- title: 标题
- content: 内容
- columnId: 所属栏目
- author: 作者
- publishTime: 发布时间

## 代码简洁性

本示例遵循"最简洁"原则：

- 使用内存数据，无需数据库和配置文件
- 业务逻辑简单明了，专注框架功能展示
- 标准三层架构，清晰的职责划分
- 真实的业务场景，易于理解和扩展
- 支持Web和控制台两种运行模式

## Web模式特性

- **嵌入式Tomcat**: 无需外部Web服务器，开箱即用
- **DispatcherServlet**: SpringWind的前端控制器自动处理请求分发
- **请求映射**: 通过@RequestMapping注解自动映射URL到Controller方法
- **文本响应**: Controller方法返回的字符串直接作为响应内容
- **RESTful风格**: 清晰的URL结构，符合Web开发规范

## 业务场景

本示例模拟了一个完整的企业网站场景：

1. **前台用户视角**：浏览首页、产品、新闻
2. **后台管理员视角**：管理栏目、发布内容
3. **数据流转**：从Dao -> Service -> Controller的完整流程

## 扩展建议

如需扩展此示例，可以考虑：

1. 添加数据库支持（MySQL/H2）
2. 添加用户认证和权限管理
3. 添加日志记录（使用AOP）
4. 添加JSON响应支持（返回Map对象）
5. 添加文章搜索和分页功能

## 相关文档

- [SpringWind框架文档](../../README.md)
- [User Demo示例](../user-demo/README.md)
- [SpringWind注解说明](../../src/main/java/com/github/microwind/springwind/annotation/)
