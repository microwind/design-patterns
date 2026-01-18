# @RequestBody 注解实现总结

## 实现内容

成功将 `UserController` 中的 `getRequestBody()` 方法重构为通用的 `@RequestBody` 注解支持，使控制器代码更加简洁和优雅。

## 新增文件

### 1. 框架层（SpringWind）

#### 注解
- **`@RequestBody`** - `src/main/java/com/github/microwind/springwind/annotation/RequestBody.java`
  - 用于标注控制器方法参数
  - 支持 `required` 属性（默认 true）

#### 工具类
- **`HttpRequestUtil`** - `src/main/java/com/github/microwind/springwind/web/HttpRequestUtil.java`
  - `getRequestBody()` - 读取请求体
  - `getClientIp()` - 获取客户端 IP
  - `isAjaxRequest()` - 判断是否为 AJAX 请求
  - `isJsonRequest()` - 判断是否为 JSON 请求

- **`JsonUtil`** - `src/main/java/com/github/microwind/springwind/web/JsonUtil.java`
  - `parseToMap()` - JSON 解析为 Map
  - `parseToObject()` - JSON 解析为对象
  - `toJson()` - 对象序列化为 JSON

### 2. 框架依赖

在 `pom.xml` 中添加了 Jackson 依赖：
```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.16.0</version>
</dependency>
```

### 3. 修改的文件

#### DispatcherServlet
- 在 `resolveMethodParameters()` 方法中添加 @RequestBody 支持
- 支持三种参数类型：
  - `String` - 返回原始字符串
  - `Map<String, Object>` - 解析为 Map
  - 自定义对象 - 解析为 POJO

#### UserController
- 移除了 `getRequestBody()` 私有方法
- 移除了 `HttpServletRequest` 和 `IOException` 依赖
- 所有需要请求体的方法改用 `@RequestBody Map<String, Object> data` 参数
- 方法签名更简洁：
  ```java
  // 旧方式
  public ViewResult create(HttpServletRequest request) throws IOException

  // 新方式
  public ViewResult create(@RequestBody Map<String, Object> data)
  ```

### 4. 文档
- **`REQUEST_BODY_GUIDE.md`** - 详细使用指南

## 代码对比

### 旧方式
```java
@PostMapping("")
@ResponseBody
public ViewResult create(HttpServletRequest request) throws IOException {
    String body = getRequestBody(request);
    Map<String, Object> data = JsonUtil.parseJson(body);

    String name = (String) data.get("name");
    String email = (String) data.get("email");
    // ...
}

private String getRequestBody(HttpServletRequest request) throws IOException {
    StringBuilder sb = new StringBuilder();
    byte[] buffer = new byte[1024];
    int len;
    while ((len = request.getInputStream().read(buffer)) != -1) {
        sb.append(new String(buffer, 0, len, "UTF-8"));
    }
    return sb.toString();
}
```

### 新方式
```java
@PostMapping("")
@ResponseBody
public ViewResult create(@RequestBody Map<String, Object> data) {
    String name = (String) data.get("name");
    String email = (String) data.get("email");
    // ...
}
```

**代码减少**: ~60%

## 测试验证

### 1. 框架测试
```bash
mvn test
```
**结果**: ✅ 所有 21 个测试通过

### 2. 功能测试

#### 创建用户
```bash
curl -X POST http://localhost:8080/user \
  -H "Content-Type: application/json" \
  -d '{"name":"test","email":"test@example.com","phone":"13900139000"}'
```
**结果**: ✅ 成功创建，返回用户 ID

#### 更新用户
```bash
curl -X PUT http://localhost:8080/user/172 \
  -H "Content-Type: application/json" \
  -d '{"email":"updated@example.com"}'
```
**结果**: ✅ 成功更新

#### 用户登录
```bash
curl -X POST http://localhost:8080/user/login \
  -H "Content-Type: application/json" \
  -d '{"name":"test","password":"123456"}'
```
**结果**: ✅ 正常登录

### 3. 数据库错误处理测试
```bash
./test-database-errors.sh
```
**结果**: ✅ 所有约束错误测试通过

## 优势

1. **代码简洁** - 减少约 60% 的请求体处理代码
2. **自动解析** - 框架自动处理 JSON 解析
3. **类型安全** - 支持多种参数类型
4. **统一处理** - 错误处理集中在框架层
5. **易于维护** - 消除重复代码
6. **向后兼容** - 不影响现有功能

## 支持的使用场景

### 1. Map 参数
```java
public ViewResult create(@RequestBody Map<String, Object> data)
```

### 2. 自定义对象
```java
public ViewResult create(@RequestBody User user)
```

### 3. 原始字符串
```java
public ViewResult webhook(@RequestBody String rawData)
```

### 4. 结合其他注解
```java
public ViewResult update(
    @PathVariable("id") Long id,
    @RequestBody Map<String, Object> data,
    @RequestParam(value = "notify", required = false) Boolean notify)
```

### 5. 可选请求体
```java
public ViewResult process(@RequestBody(required = false) Map<String, Object> data)
```

## 技术栈

- **JSON 处理**: Jackson 2.16.0
- **请求体读取**: BufferedReader + UTF-8
- **参数解析**: Java 反射 + 注解处理
- **异常处理**: 统一转换为友好错误消息

## 相关文件清单

### 框架层
- `/src/main/java/com/github/microwind/springwind/annotation/RequestBody.java`
- `/src/main/java/com/github/microwind/springwind/web/HttpRequestUtil.java`
- `/src/main/java/com/github/microwind/springwind/web/JsonUtil.java`
- `/src/main/java/com/github/microwind/springwind/web/DispatcherServlet.java`
- `/pom.xml`

### 应用层
- `/examples/user-demo/src/main/java/com/github/microwind/userdemo/controller/UserController.java`
- `/examples/user-demo/REQUEST_BODY_GUIDE.md`

## 总结

成功实现了 `@RequestBody` 注解支持，使 SpringWind 框架的 MVC 功能更加完善和易用。这个改进：

- ✅ 消除了控制器中的重复代码
- ✅ 提供了统一的请求体处理机制
- ✅ 增强了框架的易用性
- ✅ 保持了向后兼容性
- ✅ 所有测试通过

现在 SpringWind 框架已经支持：
- ✅ @Controller 和 @Service
- ✅ @Autowired 依赖注入
- ✅ @RequestMapping 及其变体（@GetMapping, @PostMapping, @PutMapping, @DeleteMapping）
- ✅ @PathVariable 路径参数
- ✅ @RequestParam 查询参数
- ✅ **@RequestBody 请求体解析（新增）**
- ✅ @ResponseBody JSON 响应
- ✅ ViewResult 视图渲染
