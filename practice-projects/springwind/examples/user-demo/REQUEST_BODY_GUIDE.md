# @RequestBody 注解使用指南

## 概述

SpringWind 框架现已支持 `@RequestBody` 注解，用于自动解析 HTTP 请求体内容。这消除了手动读取和解析请求体的需要，使控制器代码更加简洁和优雅。

## 新增功能

### 1. @RequestBody 注解

**路径**: `com.github.microwind.springwind.annotation.RequestBody`

**功能**:
- 自动读取 HTTP 请求体
- 支持 JSON 自动解析
- 支持多种参数类型

**属性**:
- `required` - 是否必填（默认 true）

### 2. HttpRequestUtil 工具类

**路径**: `com.github.microwind.springwind.web.HttpRequestUtil`

**功能**:
- 读取请求体内容
- 获取客户端 IP 地址
- 判断是否为 AJAX 请求
- 判断是否为 JSON 请求

### 3. JsonUtil 工具类（框架层）

**路径**: `com.github.microwind.springwind.web.JsonUtil`

**功能**:
- JSON 字符串解析为 Map
- JSON 字符串解析为指定对象
- 对象序列化为 JSON 字符串

## 支持的参数类型

### 1. Map<String, Object>

自动将 JSON 解析为 Map：

```java
@PostMapping("/user")
@ResponseBody
public ViewResult create(@RequestBody Map<String, Object> data) {
    String name = (String) data.get("name");
    String email = (String) data.get("email");
    // ... 处理逻辑
}
```

**请求示例**:
```bash
curl -X POST http://localhost:8080/user \
  -H "Content-Type: application/json" \
  -d '{"name":"admin","email":"admin@example.com"}'
```

### 2. String

获取原始请求体字符串：

```java
@PostMapping("/webhook")
@ResponseBody
public ViewResult handleWebhook(@RequestBody String rawData) {
    // rawData 包含原始 JSON 字符串
    System.out.println("Received: " + rawData);
    // ... 处理逻辑
}
```

### 3. 自定义对象

自动将 JSON 解析为自定义 POJO：

```java
@PostMapping("/user")
@ResponseBody
public ViewResult create(@RequestBody User user) {
    // user 对象已自动填充
    System.out.println("Name: " + user.getName());
    System.out.println("Email: " + user.getEmail());
    // ... 处理逻辑
}
```

**请求示例**:
```bash
curl -X POST http://localhost:8080/user \
  -H "Content-Type: application/json" \
  -d '{"name":"admin","email":"admin@example.com","phone":"13800138000"}'
```

## 使用示例

### 完整控制器示例

```java
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    // 创建用户 - 使用 Map
    @PostMapping("")
    @ResponseBody
    public ViewResult create(@RequestBody Map<String, Object> data) {
        String name = (String) data.get("name");
        String email = (String) data.get("email");
        String phone = (String) data.get("phone");

        User user = new User(name, email, phone);
        userService.createUser(user);
        return ApiResponse.success(user, "创建成功");
    }

    // 更新用户 - 结合路径参数
    @PutMapping("/{id}")
    @ResponseBody
    public ViewResult update(
            @PathVariable("id") Long id,
            @RequestBody Map<String, Object> data) {

        User user = userService.getUserById(id);
        if (user == null) {
            return ApiResponse.notFound("用户不存在");
        }

        String email = (String) data.get("email");
        if (email != null) {
            user.setEmail(email);
        }

        userService.updateUser(user);
        return ApiResponse.success(user, "更新成功");
    }

    // 登录 - 使用 Map
    @PostMapping("/login")
    @ResponseBody
    public ViewResult login(@RequestBody Map<String, Object> credentials) {
        String username = (String) credentials.get("username");
        String password = (String) credentials.get("password");

        boolean success = userService.validateLogin(username, password);
        if (success) {
            return ApiResponse.success("登录成功");
        } else {
            return ApiResponse.unauthorized("用户名或密码错误");
        }
    }
}
```

## 与旧方式对比

### 旧方式（手动读取请求体）

```java
@PostMapping("")
@ResponseBody
public ViewResult create(HttpServletRequest request) throws IOException {
    // 手动读取请求体
    String body = getRequestBody(request);

    // 手动解析 JSON
    Map<String, Object> data = JsonUtil.parseJson(body);

    String name = (String) data.get("name");
    String email = (String) data.get("email");

    // ... 业务逻辑
}

// 需要自定义工具方法
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

### 新方式（使用 @RequestBody）

```java
@PostMapping("")
@ResponseBody
public ViewResult create(@RequestBody Map<String, Object> data) {
    String name = (String) data.get("name");
    String email = (String) data.get("email");

    // ... 业务逻辑
}
```

**优势**:
- ✅ 代码更简洁（减少 ~60% 代码）
- ✅ 不需要手动读取请求体
- ✅ 不需要手动解析 JSON
- ✅ 不需要处理 IOException
- ✅ 自动类型转换
- ✅ 统一的错误处理

## 高级用法

### 1. 可选请求体

```java
@PostMapping("/data")
@ResponseBody
public ViewResult process(@RequestBody(required = false) Map<String, Object> data) {
    if (data == null || data.isEmpty()) {
        // 处理空请求体的情况
        return ApiResponse.badRequest("请求体不能为空");
    }
    // ... 处理逻辑
}
```

### 2. 结合多种参数注解

```java
@PutMapping("/{id}")
@ResponseBody
public ViewResult update(
        @PathVariable("id") Long id,                    // 路径参数
        @RequestBody Map<String, Object> data,          // 请求体
        @RequestParam(value = "notify", required = false) Boolean notify) {  // 查询参数

    // 同时处理路径参数、请求体和查询参数
    User user = userService.getUserById(id);
    user.setEmail((String) data.get("email"));

    if (Boolean.TRUE.equals(notify)) {
        notificationService.sendUpdateNotification(user);
    }

    return ApiResponse.success(user);
}
```

**请求示例**:
```bash
curl -X PUT 'http://localhost:8080/user/123?notify=true' \
  -H "Content-Type: application/json" \
  -d '{"email":"newemail@example.com"}'
```

### 3. 使用自定义 DTO

```java
// 定义 DTO
public class CreateUserRequest {
    private String name;
    private String email;
    private String phone;
    // getters and setters
}

// 控制器方法
@PostMapping("")
@ResponseBody
public ViewResult create(@RequestBody CreateUserRequest request) {
    User user = new User(
        request.getName(),
        request.getEmail(),
        request.getPhone()
    );
    userService.createUser(user);
    return ApiResponse.success(user);
}
```

## 错误处理

框架会自动处理以下错误：

1. **请求体为空** (required=true 时)
   ```json
   {
     "code": 500,
     "message": "请求体不能为空"
   }
   ```

2. **JSON 解析失败**
   ```json
   {
     "code": 500,
     "message": "JSON parse failed: ..."
   }
   ```

3. **读取请求体失败**
   ```json
   {
     "code": 500,
     "message": "读取请求体失败: ..."
   }
   ```

## 框架依赖

SpringWind 框架现已包含 Jackson 依赖用于 JSON 处理：

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.16.0</version>
</dependency>
```

## 最佳实践

1. **优先使用 @RequestBody**
   - 对于接收 JSON 数据的 API，始终使用 @RequestBody
   - 避免手动读取和解析请求体

2. **选择合适的参数类型**
   - 简单场景使用 `Map<String, Object>`
   - 复杂场景使用自定义 DTO 类
   - 需要原始数据时使用 `String`

3. **结合其他注解**
   - 与 @PathVariable 结合处理 RESTful 路径
   - 与 @RequestParam 结合处理查询参数

4. **异常处理**
   - 在控制器方法中捕获业务异常
   - 返回友好的错误响应

## 相关文件

- **注解**: `src/main/java/com/github/microwind/springwind/annotation/RequestBody.java`
- **工具类**: `src/main/java/com/github/microwind/springwind/web/HttpRequestUtil.java`
- **JSON 工具**: `src/main/java/com/github/microwind/springwind/web/JsonUtil.java`
- **调度器**: `src/main/java/com/github/microwind/springwind/web/DispatcherServlet.java`
- **示例**: `examples/user-demo/src/main/java/com/github/microwind/userdemo/controller/UserController.java`

## 总结

`@RequestBody` 注解的引入极大地简化了控制器代码，使开发者能够专注于业务逻辑而不是底层的请求处理细节。这是 SpringWind 框架向更现代化、更易用迈进的重要一步。
