# JSON 对象返回和分页查询说明

## 概述

已完成以下改进：
1. ✅ User 对象正确序列化为 JSON
2. ✅ 支持翻页查询，返回完整的分页信息
3. ✅ 支持嵌套对象和集合的 JSON 序列化

---

## 一、JSON 对象序列化

### 改进内容

#### 1. ApiResponse 改进
- 使用 Jackson 将对象序列化为 Map
- 确保嵌套对象、集合都能正确转换

```java
public Map<String, Object> toMap() {
    Map<String, Object> map = new HashMap<>();
    map.put("code", code);
    map.put("message", message);
    if (data != null) {
        // 使用 Jackson 确保对象正确序列化
        String json = JsonUtil.toJson(data);
        Object parsedData = JsonUtil.parseJson(json, Object.class);
        map.put("data", parsedData);
    }
    return map;
}
```

#### 2. JsonResult 改进
- 支持 Map、List、数组的递归序列化
- 正确处理嵌套结构

### 单个用户 JSON 响应示例

**请求：** `GET /user/1`

**响应：**
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

### 用户列表 JSON 响应示例

**请求：** `GET /user`

**响应：**
```json
{
  "code": 200,
  "message": "获取用户列表成功",
  "data": [
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
  ]
}
```

---

## 二、分页查询

### API 接口

**请求格式：** `GET /user?page={page}&pageSize={pageSize}`

**参数说明：**
- `page`: 页码，从 1 开始
- `pageSize`: 每页记录数

### 分页响应示例

**请求：** `GET /user?page=1&pageSize=10`

**响应：**
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
    "pageSize": 10,
    "total": 25,
    "totalPages": 3,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

### 分页字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| list | Array | 当前页的数据列表 |
| page | Number | 当前页码 |
| pageSize | Number | 每页大小 |
| total | Number | 总记录数 |
| totalPages | Number | 总页数 |
| hasNext | Boolean | 是否有下一页 |
| hasPrevious | Boolean | 是否有上一页 |

### 使用示例

```java
// 在 Controller 中
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
        return ApiResponse.success(users, "获取用户列表成功");
    }
}
```

---

## 三、测试

### 运行测试脚本

```bash
# 启动应用后，运行测试脚本
chmod +x test-json-pagination.sh
./test-json-pagination.sh
```

### 手动测试示例

```bash
# 1. 创建用户
curl -X POST http://localhost:8080/user \
  -H "Content-Type: application/json" \
  -d '{"name":"testuser","email":"test@example.com","phone":"13800138000"}'

# 响应：
# {
#   "code": 200,
#   "message": "创建用户成功",
#   "data": {
#     "id": 1,
#     "name": "testuser",
#     "email": "test@example.com",
#     "phone": "13800138000",
#     "createdTime": 1705401234567,
#     "updatedTime": 1705401234567
#   }
# }

# 2. 获取用户详情
curl http://localhost:8080/user/1

# 响应：包含完整 User 对象的 JSON

# 3. 分页查询
curl "http://localhost:8080/user?page=1&pageSize=5"

# 响应：包含分页信息和用户列表

# 4. 获取所有用户
curl http://localhost:8080/user

# 响应：包含所有用户的 JSON 数组
```

---

## 四、核心改进

### 1. ApiResponse 使用 Jackson 序列化

**优势：**
- ✅ 自动处理复杂对象
- ✅ 支持嵌套对象和集合
- ✅ 正确处理日期、枚举等特殊类型
- ✅ 遵循 JavaBean 规范（getter/setter）

### 2. JsonResult 递归序列化

**支持的数据类型：**
- ✅ 基本类型（String, Number, Boolean）
- ✅ Map（嵌套 Map）
- ✅ Collection（List, Set 等）
- ✅ 数组（Object[], int[], long[] 等）
- ✅ null 值

### 3. PageResult 完整分页信息

**包含字段：**
- ✅ list - 数据列表
- ✅ page - 当前页
- ✅ pageSize - 每页大小
- ✅ total - 总记录数
- ✅ totalPages - 总页数
- ✅ hasNext - 是否有下一页
- ✅ hasPrevious - 是否有上一页

---

## 五、完整的 API 列表

| HTTP 方法 | 路径 | 返回数据 | 说明 |
|----------|------|----------|------|
| GET | `/user` | `{ code, message, data: User[] }` | 获取所有用户 |
| GET | `/user?page=1&pageSize=10` | `{ code, message, data: PageResult }` | 分页获取用户 |
| GET | `/user/{id}` | `{ code, message, data: User }` | 获取单个用户 |
| GET | `/user/name/{username}` | `{ code, message, data: User }` | 根据用户名获取 |
| GET | `/user/count` | `{ code, message, data: Number }` | 获取用户总数 |
| POST | `/user` | `{ code, message, data: User }` | 创建用户 |
| PUT | `/user/{id}` | `{ code, message, data: User }` | 更新用户 |
| DELETE | `/user/{id}` | `{ code, message }` | 删除用户 |
| POST | `/user/login` | `{ code, message, data: User }` | 用户登录 |

---

## 六、注意事项

1. **User 对象必须有 getter/setter**
   - Jackson 通过 getter 方法序列化对象
   - 确保所有字段都有对应的 getter

2. **分页参数都是可选的**
   - 不提供分页参数时返回所有数据
   - 提供分页参数时返回分页数据

3. **响应格式统一**
   - 所有接口都返回 `{ code, message, data }` 格式
   - 便于前端统一处理

4. **错误处理**
   - 数据不存在返回 404
   - 参数错误返回 400
   - 服务器错误返回 500

---

## 完成！🎉

现在你的 API 支持：
- ✅ 完整的 User 对象 JSON 序列化
- ✅ 分页查询，包含完整的分页信息
- ✅ RESTful 风格的路径参数
- ✅ 统一的响应格式
- ✅ 类型安全的参数注入
