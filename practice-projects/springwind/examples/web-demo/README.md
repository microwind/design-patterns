# Web Demo - 企业网站内容管理系统

这是一个基于SpringWind框架的企业网站内容管理系统示例，用于演示SpringWind框架在实际项目中的应用。

## 项目说明

本项目模拟"春风公司"的企业网站，包含前台展示和后台管理功能。展示了SpringWind框架的以下特性：

- **IoC容器和依赖注入**: 自动管理各层组件的依赖关系
- **分层架构**: Controller -> Service -> Dao 的经典MVC架构
- **RESTful API**: 完整的 CRUD 操作支持
- **JSON响应**: 统一的 JSON 格式响应
- **业务场景**: 真实的内容管理系统业务逻辑
- **数据操作**: 栏目和文章的增删改查

## 项目结构

```
web-demo/
├── pom.xml
└── src/main/java/com/github/microwind/webdemo/
    ├── WebDemoApplication.java  # 启动类
    ├── controller/              # 控制器层
    │   ├── HomeController.java       # 首页
    │   ├── ProductController.java    # 产品中心
    │   ├── NewsController.java       # 新闻资讯
    │   ├── AdminController.java      # 后台管理
    │   └── ArticleController.java    # 文章CRUD接口
    ├── service/                 # 服务层
    │   ├── ColumnService.java
    │   └── ArticleService.java
    ├── dao/                     # 数据访问层
    │   ├── ColumnDao.java
    │   └── ArticleDao.java
    ├── model/                   # 数据模型
    │   ├── Column.java          # 栏目
    │   └── Article.java         # 文章
    └── utils/                   # 工具类
        ├── ResponseUtils.java   # 响应工具类（来自user-demo）
        ├── RequestUtils.java    # 请求工具类
        └── ResponseBody.java    # 统一响应体
```

## 新增功能特性

✅ **完整的 CRUD 操作**（创建、读取、更新、删除）
✅ **RESTful API 设计**
✅ **JSON 格式响应**
✅ **本地内存数据库模拟**
✅ **支持中文数据处理**
✅ **LocalDateTime 日期时间支持**
✅ **统一的错误处理**

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

#### 模式2: Web模式（推荐）

启动嵌入式Tomcat服务器，通过浏览器或API工具访问：

```bash
# 进入web-demo目录
cd examples/web-demo

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

服务器启动后，访问 `http://localhost:8080`

## API 接口文档

### 1. 首页接口

#### 获取首页数据
```
GET /home/index
```

响应示例：
```json
{
  "status": 200,
  "message": "首页数据获取成功",
  "data": {
    "companyName": "春风公司",
    "description": "春风公司成立于2010年，是一家专注于云计算和大数据领域的创新型企业。",
    "recentArticles": [...]
  }
}
```

### 2. 文章管理接口（完整CRUD）

#### 获取所有文章
```
GET /article/list
```

#### 获取文章详情
```
GET /article/detail/{id}
```
示例：`GET /article/detail/1`

#### 根据栏目获取文章
```
GET /article/column/{columnId}
```
示例：`GET /article/column/1`

#### 创建文章
```
POST /article/create
Content-Type: application/json

{
  "title": "文章标题",
  "content": "文章内容",
  "columnId": 1,
  "author": "作者姓名"
}
```

#### 更新文章
```
PUT /article/update
Content-Type: application/json

{
  "id": 1,
  "title": "新标题",
  "content": "新内容",
  "columnId": 1,
  "author": "作者姓名"
}
```

#### 删除文章
```
DELETE /article/delete/{id}
```
示例：`DELETE /article/delete/1`

### 3. 产品接口

#### 获取产品列表
```
GET /product/list
```

返回栏目ID为1的所有产品文章，JSON格式。

### 4. 新闻接口

#### 获取新闻列表
```
GET /news/list
```

返回栏目ID为2的所有新闻文章，JSON格式。

### 5. 管理接口

#### 获取所有栏目
```
GET /admin/columns
```

#### 创建栏目
```
POST /admin/createColumn
Content-Type: application/json

{
  "name": "栏目名称",
  "description": "栏目描述",
  "sort": 1
}
```

#### 发布文章
```
POST /admin/publishArticle
Content-Type: application/json

{
  "title": "文章标题",
  "content": "文章内容",
  "columnId": 2,
  "author": "作者姓名"
}
```

## 测试示例

### 使用 curl 测试

#### 1. 获取文章列表
```bash
curl http://localhost:8080/article/list
```

#### 2. 获取文章详情
```bash
curl http://localhost:8080/article/detail/1
```

#### 3. 创建新文章
```bash
curl -X POST http://localhost:8080/article/create \
  -H "Content-Type: application/json" \
  -d '{
    "title": "测试文章",
    "content": "这是一篇测试文章的内容",
    "columnId": 1,
    "author": "测试作者"
  }'
```

#### 4. 更新文章
```bash
curl -X PUT http://localhost:8080/article/update \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "title": "更新后的标题",
    "content": "更新后的内容",
    "columnId": 1,
    "author": "作者"
  }'
```

#### 5. 删除文章
```bash
curl -X DELETE http://localhost:8080/article/delete/1
```

## 响应格式

所有接口统一使用以下 JSON 响应格式：

### 成功响应
```json
{
  "status": 200,
  "message": "操作成功消息",
  "data": {
    // 响应数据
  }
}
```

### 错误响应
```json
{
  "status": 404,
  "message": "错误消息",
  "data": null
}
```

## 数据模型

### Article（文章）
```json
{
  "id": 1,
  "title": "文章标题",
  "content": "文章内容",
  "columnId": 1,
  "author": "作者",
  "publishTime": "2025-10-18T10:30:00"
}
```

### Column（栏目）
```json
{
  "id": 1,
  "name": "栏目名称",
  "description": "栏目描述",
  "sort": 1,
  "createTime": "2025-10-18T10:30:00"
}
```

## 技术要点

### 1. 内存数据存储

```java
@Repository
public class ArticleDao {
    private List<Article> articles = new ArrayList<>();
    // 使用内存List模拟数据库，支持完整CRUD

    public boolean delete(Long id) {
        return articles.removeIf(a -> a.getId().equals(id));
    }
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

    public boolean deleteArticle(Long id) {
        return articleDao.delete(id);
    }
}
```

### 3. RESTful 控制器

```java
@Controller
@RequestMapping("/article")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @RequestMapping("/list")
    public void list(HttpServletRequest request, HttpServletResponse response) {
        List<Article> articles = articleService.getAllArticles();
        ResponseUtils.sendJsonResponse(response, 200, "获取成功", articles, null);
    }
}
```

### 4. 统一响应工具

```java
// 来自 user-demo 的 ResponseUtils
ResponseUtils.sendJsonResponse(response, HttpServletResponse.SC_OK,
    "操作成功", data, null);

ResponseUtils.sendJsonError(response, HttpServletResponse.SC_NOT_FOUND,
    "资源不存在", null);
```

## 技术栈

- SpringWind 框架（自研轻量级 IoC 和 MVC 框架）
- Embedded Tomcat 11
- Jackson 2.18.2（JSON 序列化，支持 LocalDateTime）
- Jakarta Servlet API 6.1
- Java 17

## 注意事项

1. 本项目使用内存数据存储，重启服务后数据会重置
2. 所有接口支持中文数据
3. LocalDateTime 格式化为 ISO 8601 标准格式
4. 所有 POST/PUT/DELETE 请求需要设置 `Content-Type: application/json`
5. ResponseUtils 和 ResponseBody 来自 user-demo 项目

## Web模式特性

- **嵌入式Tomcat**: 无需外部Web服务器，开箱即用
- **DispatcherServlet**: SpringWind的前端控制器自动处理请求分发
- **请求映射**: 通过@RequestMapping注解自动映射URL到Controller方法
- **JSON响应**: 自动序列化对象为JSON格式
- **RESTful风格**: 清晰的URL结构，符合Web开发规范

## 业务场景

本示例模拟了一个完整的企业网站场景：

1. **前台用户视角**：浏览首页、产品、新闻
2. **后台管理员视角**：管理栏目、发布内容
3. **API调用者视角**：通过RESTful API进行CRUD操作
4. **数据流转**：从Dao -> Service -> Controller的完整流程

## 扩展建议

扩展此示例，可以考虑：

1. ✅ ~~添加JSON响应支持~~ （已完成）
2. ✅ ~~添加完整的CRUD操作~~ （已完成）
3. 添加数据库支持（MySQL/H2）
4. 添加用户认证和权限管理
5. 添加日志记录（使用AOP）
6. 添加文章搜索和分页功能
7. 添加文件上传功能
8. 添加缓存支持

## 相关文档

- [SpringWind框架文档](../../README.md)
- [User Demo示例](../user-demo/README.md)
- [SpringWind注解说明](../../src/main/java/com/github/microwind/springwind/annotation/)
