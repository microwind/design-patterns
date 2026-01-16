# User JSON 对象返回和分页查询示例

## 功能说明

✅ **User 对象 JSON 返回** - 完整的用户对象序列化
✅ **分页查询** - 支持翻页，返回完整分页信息

---

## 快速测试

### 1. 单个用户 JSON 对象

```bash
# 创建用户
curl -X POST http://localhost:8080/user \
  -H "Content-Type: application/json" \
  -d '{"name":"admin","email":"admin@example.com","phone":"13800138000"}'

# 获取用户
curl http://localhost:8080/user/1 | python3 -m json.tool
```

**响应（格式化后）：**
```json
{
    "code": 200,
    "message": "获取用户详情成功",
    "data": {
        "id": 1,
        "name": "admin",
        "email": "admin@example.com",
        "phone": "13800138000",
        "createdTime": 1705401234567,
        "updatedTime": 1705401234567
    }
}
```

### 2. 分页查询

```bash
# 第1页，每页5条
curl "http://localhost:8080/user?page=1&pageSize=5" | python3 -m json.tool
```

**响应（格式化后）：**
```json
{
    "code": 200,
    "message": "获取列表成功",
    "data": {
        "list": [
            {
                "id": 1,
                "name": "user1",
                "email": "user1@example.com",
                "phone": "13800138001",
                "createdTime": 1705401234567,
                "updatedTime": 1705401234567
            },
            {
                "id": 2,
                "name": "user2",
                "email": "user2@example.com",
                "phone": "13800138002",
                "createdTime": 1705401234568,
                "updatedTime": 1705401234568
            }
        ],
        "page": 1,
        "pageSize": 5,
        "total": 15,
        "totalPages": 3,
        "hasNext": true,
        "hasPrevious": false
    }
}
```

---

## 核心改进

### 1. ApiResponse.toMap() 使用 Jackson

```java
public Map<String, Object> toMap() {
    Map<String, Object> map = new HashMap<>();
    map.put("code", code);
    map.put("message", message);
    if (data != null) {
        // 使用 Jackson 确保对象正确序列化为 JSON
        String json = JsonUtil.toJson(data);
        Object parsedData = JsonUtil.parseJson(json, Object.class);
        map.put("data", parsedData);
    }
    return map;
}
```

**效果：**
- User 对象 → `{"id":1,"name":"admin",...}`
- List<User> → `[{"id":1,...},{"id":2,...}]`
- PageResult → `{"list":[...],"page":1,"total":100,...}`

### 2. JsonResult 支持递归序列化

```java
private String toJson(Object obj) {
    if (obj instanceof Map) return mapToJson((Map<?, ?>) obj);
    if (obj instanceof Collection) return collectionToJson((Collection<?>) obj);
    if (obj.getClass().isArray()) return arrayToJson(obj);
    // ... 其他类型处理
}
```

**支持：**
- ✅ 嵌套 Map
- ✅ 嵌套 List/Set
- ✅ 数组（Object[], int[], long[]）
- ✅ 基本类型和包装类

---

## 分页 API 使用

### Controller 代码

```java
@GetMapping("")
@ResponseBody
public ViewResult list(
    @RequestParam(value = "page", required = false) Integer page,
    @RequestParam(value = "pageSize", required = false) Integer pageSize) {

    if (page != null && pageSize != null) {
        // 分页查询
        List<User> users = userService.getUsersByPage(page, pageSize);
        Long total = userService.getUserCount();
        return ApiResponse.page(users, page, pageSize, total);
    } else {
        // 查询所有
        List<User> users = userService.getAllUsers();
        return ApiResponse.success(users);
    }
}
```

### 前端调用示例（JavaScript）

```javascript
// 获取第1页，每页10条
fetch('/user?page=1&pageSize=10')
  .then(res => res.json())
  .then(response => {
    console.log('状态码:', response.code);
    console.log('消息:', response.message);
    console.log('数据:', response.data.list);
    console.log('总数:', response.data.total);
    console.log('总页数:', response.data.totalPages);
    console.log('是否有下一页:', response.data.hasNext);
  });
```

---

## 测试脚本

运行完整测试：
```bash
./test-json-pagination.sh
```

测试内容：
1. ✅ 创建15个测试用户
2. ✅ 获取单个用户 - 验证 JSON 对象
3. ✅ 获取所有用户 - 验证 JSON 数组
4. ✅ 分页查询（第1、2、3页）- 验证分页数据
5. ✅ 验证分页字段完整性
6. ✅ 根据用户名查询
7. ✅ 获取用户总数

---

## 响应格式对比

### 之前（可能有问题）
```json
{
  "code": 200,
  "message": "成功",
  "data": "User{id=1, name='admin'}"  // ❌ toString() 字符串
}
```

### 现在（正确的 JSON）
```json
{
  "code": 200,
  "message": "成功",
  "data": {                            // ✅ JSON 对象
    "id": 1,
    "name": "admin",
    "email": "admin@example.com",
    "phone": "13800138000",
    "createdTime": 1705401234567,
    "updatedTime": 1705401234567
  }
}
```

---

## 完成！

现在你的 User API 支持：
- ✅ 完整的 JSON 对象返回（不是 toString）
- ✅ 分页查询，包含完整分页信息
- ✅ RESTful 路径参数
- ✅ 所有测试通过
