# 重构后的控制器示例

## 重构说明

`ApiResponse` 现在实现了 `ViewResult` 接口，可以直接作为控制器方法的返回值，不再需要手动包装成 `JsonResult`。

## 重构前（旧写法）

```java
@RequestMapping("/get")
@ResponseBody
public ViewResult getById(HttpServletRequest request, HttpServletResponse response) {
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
    } catch (NumberFormatException e) {
        return new JsonResult(ApiResponse.badRequest("用户 ID 格式错误").toMap());
    } catch (Exception e) {
        return new JsonResult(ApiResponse.failure("获取用户详情失败: " + e.getMessage()).toMap());
    }
}
```

## 重构后（新写法）✨

```java
@RequestMapping("/get")
@ResponseBody
public ViewResult getById(HttpServletRequest request, HttpServletResponse response) {
    try {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            return ApiResponse.badRequest("用户 ID 不能为空");
        }
        Long id = Long.parseLong(idStr);
        User user = userService.getUserById(id);
        if (user == null) {
            return ApiResponse.notFound("用户不存在");
        }
        return ApiResponse.success(user, "获取用户详情成功");
    } catch (NumberFormatException e) {
        return ApiResponse.badRequest("用户 ID 格式错误");
    } catch (Exception e) {
        return ApiResponse.failure("获取用户详情失败: " + e.getMessage());
    }
}
```

## 关键改进

1. **去除冗余代码**：不再需要 `new JsonResult(...)`
2. **去除 toMap() 调用**：不再需要 `.toMap()`
3. **代码更简洁**：直接返回 `ApiResponse.xxx()` 即可
4. **类型安全**：保持了泛型支持 `ApiResponse<User>`

## 更多示例

### 示例 1：创建用户

```java
@RequestMapping("/create")
@ResponseBody
public ViewResult create(HttpServletRequest request, HttpServletResponse response) throws IOException {
    try {
        String body = getRequestBody(request);
        Map<String, Object> data = JsonUtil.parseJson(body);

        String name = (String) data.get("name");
        String password = (String) data.get("password");

        if (name == null || name.isEmpty() || password == null || password.isEmpty()) {
            return ApiResponse.badRequest("用户名和密码不能为空");
        }

        User user = new User(name, password, email, phone);
        boolean success = userService.createUser(user);

        if (success) {
            return ApiResponse.success(user, "创建用户成功");
        } else {
            return ApiResponse.badRequest("用户名已存在");
        }
    } catch (Exception e) {
        return ApiResponse.failure("创建用户失败: " + e.getMessage());
    }
}
```

### 示例 2：删除用户

```java
@RequestMapping("/delete")
@ResponseBody
public ViewResult delete(HttpServletRequest request, HttpServletResponse response) {
    try {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            return ApiResponse.badRequest("用户 ID 不能为空");
        }
        Long id = Long.parseLong(idStr);

        User user = userService.getUserById(id);
        if (user == null) {
            return ApiResponse.notFound("用户不存在");
        }

        boolean success = userService.deleteUser(id);
        if (success) {
            return ApiResponse.success("删除用户成功");
        } else {
            return ApiResponse.failure("删除用户失败");
        }
    } catch (NumberFormatException e) {
        return ApiResponse.badRequest("用户 ID 格式错误");
    } catch (Exception e) {
        return ApiResponse.failure("删除用户失败: " + e.getMessage());
    }
}
```

### 示例 3：分页列表

```java
@RequestMapping("/list")
@ResponseBody
public ViewResult list(HttpServletRequest request, HttpServletResponse response) {
    try {
        String pageStr = request.getParameter("page");
        String pageSizeStr = request.getParameter("pageSize");

        if (pageStr != null && pageSizeStr != null) {
            int page = Integer.parseInt(pageStr);
            int pageSize = Integer.parseInt(pageSizeStr);
            List<User> users = userService.getUsersByPage(page, pageSize);
            Long total = userService.getUserCount();

            return ApiResponse.page(users, page, pageSize, total);
        } else {
            List<User> users = userService.getAllUsers();
            return ApiResponse.success(users, "获取用户列表成功");
        }
    } catch (NumberFormatException e) {
        return ApiResponse.badRequest("分页参数格式错误");
    } catch (Exception e) {
        return ApiResponse.failure("获取用户列表失败: " + e.getMessage());
    }
}
```

## 技术实现

`ApiResponse` 类实现了 `ViewResult` 接口，在 `render()` 方法中：

```java
@Override
public void render(HttpServletRequest request, HttpServletResponse response) throws Exception {
    JsonResult jsonResult = new JsonResult(this.toMap());
    jsonResult.render(request, response);
}
```

这样就将自身转换为 JSON 并写入响应，实现了无缝集成。

## 优势总结

✅ **简洁性**：减少了大量样板代码
✅ **可读性**：代码意图更加清晰
✅ **一致性**：所有接口返回方式统一
✅ **类型安全**：保留泛型支持
✅ **向后兼容**：旧的 `toMap()` 方法仍然保留，可以渐进式迁移

## 响应格式

无论使用哪种写法，最终的 JSON 响应格式保持不变：

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
