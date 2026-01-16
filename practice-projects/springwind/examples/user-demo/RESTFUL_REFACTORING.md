# RESTful API 重构完成文档

## 概述

已完成两个主要重构：
1. **ApiResponse 直接返回** - 不再需要手动包装成 `JsonResult`
2. **RESTful 路径参数支持** - 支持 `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping` 和路径参数 `{id}`

---

## 一、ApiResponse 重构

### 改动说明

`ApiResponse` 现在实现了 `ViewResult` 接口，可以直接作为控制器返回值。

### 重构前（旧写法）

```java
@RequestMapping("/get")
@ResponseBody
public ViewResult getById(HttpServletRequest request, HttpServletResponse response) {
    String idStr = request.getParameter("id");
    User user = userService.getUserById(Long.parseLong(idStr));
    return new JsonResult(ApiResponse.success(user).toMap());  // ❌ 冗余
}
```

### 重构后（新写法）✨

```java
@GetMapping("/{id}")
@ResponseBody
public ViewResult getById(@PathVariable("id") Long id) {
    User user = userService.getUserById(id);
    return ApiResponse.success(user);  // ✅ 简洁
}
```

---

## 二、RESTful 路径参数支持

### 新增功能

#### 1. 新增注解

- `@GetMapping` - GET 请求映射
- `@PostMapping` - POST 请求映射
- `@PutMapping` - PUT 请求映射
- `@DeleteMapping` - DELETE 请求映射

#### 2. 路径参数

支持在路径中使用 `{变量名}` 占位符，通过 `@PathVariable` 注解注入到方法参数。

**示例：**

```java
@GetMapping("/user/{id}")
@ResponseBody
public ViewResult getById(@PathVariable("id") Long id) {
    // id 会自动从路径中提取，如 /user/123 -> id=123
    User user = userService.getUserById(id);
    if (user == null) {
        return ApiResponse.notFound("用户不存在");
    }
    return ApiResponse.success(user);
}
```

#### 3. 多个路径参数

```java
@GetMapping("/user/{userId}/order/{orderId}")
@ResponseBody
public ViewResult getOrder(
    @PathVariable("userId") Long userId,
    @PathVariable("orderId") Long orderId) {
    // ...
}
```

#### 4. 路径参数 + 查询参数

```java
@GetMapping("/user/{id}")
@ResponseBody
public ViewResult getById(
    @PathVariable("id") Long id,
    @RequestParam(value = "detail", required = false) Boolean detail) {
    // 路径参数: /user/123
    // 查询参数: ?detail=true
    // 完整URL: /user/123?detail=true
}
```

---

## 三、重构后的 UserController

### 完整的 RESTful API 接口

| HTTP 方法 | 路径 | 说明 |
|----------|------|------|
| GET | `/user` | 获取用户列表 |
| GET | `/user?page=1&pageSize=10` | 分页获取用户列表 |
| GET | `/user/{id}` | 根据 ID 获取用户详情 |
| GET | `/user/name/{username}` | 根据用户名获取用户 |
| GET | `/user/count` | 获取用户总数 |
| POST | `/user` | 创建用户 |
| PUT | `/user/{id}` | 更新用户 |
| DELETE | `/user/{id}` | 删除用户 |
| POST | `/user/login` | 用户登录 |

### 代码示例

```java
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    // 获取用户详情（路径参数）
    @GetMapping("/{id}")
    @ResponseBody
    public ViewResult getById(@PathVariable("id") Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ApiResponse.notFound("用户不存在");
        }
        return ApiResponse.success(user);
    }

    // 创建用户
    @PostMapping("")
    @ResponseBody
    public ViewResult create(HttpServletRequest request) throws IOException {
        String body = getRequestBody(request);
        Map<String, Object> data = JsonUtil.parseJson(body);

        String name = (String) data.get("name");
        String password = (String) data.get("password");

        if (name == null || password == null) {
            return ApiResponse.badRequest("用户名和密码不能为空");
        }

        User user = new User(name, password, email, phone);
        boolean success = userService.createUser(user);

        if (success) {
            return ApiResponse.success(user, "创建用户成功");
        } else {
            return ApiResponse.badRequest("用户名已存在");
        }
    }

    // 更新用户（路径参数 + 请求体）
    @PutMapping("/{id}")
    @ResponseBody
    public ViewResult update(@PathVariable("id") Long id, HttpServletRequest request) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ApiResponse.notFound("用户不存在");
        }

        // 更新逻辑...
        userService.updateUser(user);
        return ApiResponse.success(user, "更新用户成功");
    }

    // 删除用户（路径参数）
    @DeleteMapping("/{id}")
    @ResponseBody
    public ViewResult delete(@PathVariable("id") Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ApiResponse.notFound("用户不存在");
        }

        userService.deleteUser(id);
        return ApiResponse.success("删除用户成功");
    }
}
```

---

## 四、底层实现

### 1. PathMatcher（路径匹配器）

新增 `PathMatcher` 类，用于解析路径模式并提取路径变量。

```java
PathMatcher matcher = new PathMatcher("/user/{id}");
matcher.matches("/user/123");  // true
Map<String, String> vars = matcher.extractPathVariables("/user/123");
// vars = {"id": "123"}
```

### 2. DispatcherServlet 改动

- 将固定路径映射改为支持路径模式匹配
- 添加对 `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping` 的支持
- 在 `resolveMethodParameters` 中添加 `@PathVariable` 参数注入

### 3. HandlerMapping 改动

- 添加 `PathMatcher` 字段，用于存储路径模式
- 构造函数接收完整路径模式（如 `/user/{id}`）

---

## 五、测试

### 测试脚本

使用 `test-restful-api.sh` 脚本测试所有接口：

```bash
chmod +x test-restful-api.sh
./test-restful-api.sh
```

### 手动测试示例

```bash
# 1. 创建用户
curl -X POST http://localhost:8080/user \
  -H "Content-Type: application/json" \
  -d '{"name":"admin","password":"123456","email":"admin@example.com"}'

# 2. 获取用户（路径参数）
curl http://localhost:8080/user/1

# 3. 更新用户（路径参数）
curl -X PUT http://localhost:8080/user/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"admin2","email":"admin2@example.com"}'

# 4. 删除用户（路径参数）
curl -X DELETE http://localhost:8080/user/1

# 5. 根据用户名获取用户（路径参数）
curl http://localhost:8080/user/name/admin

# 6. 分页获取用户列表（查询参数）
curl http://localhost:8080/user?page=1&pageSize=10
```

---

## 六、优势总结

### ApiResponse 直接返回

✅ **代码量减少 50%** - 不再需要 `new JsonResult(ApiResponse.xxx().toMap())`
✅ **可读性提升** - 代码意图更清晰
✅ **类型安全** - 保留泛型支持
✅ **向后兼容** - 旧代码仍可使用

### RESTful 路径参数

✅ **符合 REST 规范** - 使用标准的 RESTful 设计
✅ **语义化 URL** - `/user/123` 比 `/user/get?id=123` 更直观
✅ **类型安全** - 路径参数自动转换为指定类型
✅ **Spring MVC 兼容** - 与 Spring Boot 风格一致

---

## 七、响应格式

所有接口返回统一的 JSON 格式：

### 成功响应

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "name": "admin",
    "email": "admin@example.com"
  }
}
```

### 错误响应

```json
{
  "code": 404,
  "message": "用户不存在"
}
```

### 分页响应

```json
{
  "code": 200,
  "message": "获取列表成功",
  "data": {
    "list": [...],
    "page": 1,
    "pageSize": 10,
    "total": 100,
    "totalPages": 10,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

---

## 八、注意事项

1. **路径参数匹配顺序**
   - 精确路径优先于模式路径
   - `/user/count` 应该在 `/user/{id}` 之前注册（当前按代码顺序）

2. **参数类型转换**
   - 支持自动转换：String, Integer, Long, Boolean, Double
   - 转换失败会抛出异常，返回 400 错误

3. **必填参数验证**
   - `@PathVariable` 默认 required=true
   - `@RequestParam` 可以设置 required=false 和 defaultValue

4. **HTTP 方法限制**
   - 必须使用正确的 HTTP 方法访问对应接口
   - GET /user/1 ✅
   - POST /user/1 ❌（会返回 404）

---

## 九、迁移指南

### 从旧代码迁移到新代码

1. **替换注解**
   ```java
   // 旧代码
   @RequestMapping(value = "/list", method = "GET")

   // 新代码
   @GetMapping("/list")
   ```

2. **移除 JsonResult 包装**
   ```java
   // 旧代码
   return new JsonResult(ApiResponse.success(user).toMap());

   // 新代码
   return ApiResponse.success(user);
   ```

3. **使用路径参数**
   ```java
   // 旧代码
   @RequestMapping("/get")
   public ViewResult getById(HttpServletRequest request) {
       String idStr = request.getParameter("id");
       Long id = Long.parseLong(idStr);
       // ...
   }

   // 新代码
   @GetMapping("/{id}")
   public ViewResult getById(@PathVariable("id") Long id) {
       // ...
   }
   ```

4. **使用 @RequestParam**
   ```java
   // 旧代码
   String pageStr = request.getParameter("page");
   int page = Integer.parseInt(pageStr);

   // 新代码
   @GetMapping("")
   public ViewResult list(
       @RequestParam(value = "page", required = false) Integer page) {
       // ...
   }
   ```

---

## 十、文件清单

### 新增文件

1. **注解文件**
   - `GetMapping.java` - GET 请求注解
   - `PostMapping.java` - POST 请求注解
   - `PutMapping.java` - PUT 请求注解
   - `DeleteMapping.java` - DELETE 请求注解

2. **工具类**
   - `PathMatcher.java` - 路径匹配和参数提取

3. **测试脚本**
   - `test-restful-api.sh` - RESTful API 测试脚本

### 修改文件

1. **核心文件**
   - `ApiResponse.java` - 实现 ViewResult 接口
   - `HandlerMapping.java` - 添加 PathMatcher 支持
   - `DispatcherServlet.java` - 支持新注解和路径参数

2. **控制器**
   - `UserController.java` - 完全重构为 RESTful 风格

---

## 完成！🎉

现在你的 SpringWind 框架已经支持：
- ✅ RESTful 风格的路径参数
- ✅ @GetMapping/@PostMapping/@PutMapping/@DeleteMapping
- ✅ ApiResponse 直接返回，无需手动包装
- ✅ 类型安全的参数注入
- ✅ 符合现代 Web 开发规范
