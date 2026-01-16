# 数据库错误处理实现总结

## 概述

成功实现了数据库约束违反错误的友好响应处理，将底层的 SQL 异常转换为用户友好的错误消息。

## 实现的功能

### 1. 自定义异常类

#### BusinessException (业务异常基类)
- 路径：`src/main/java/com/github/microwind/userdemo/exception/BusinessException.java`
- 包含 HTTP 状态码和错误消息
- 所有业务异常的父类

#### DuplicateKeyException (数据重复异常)
- 路径：`src/main/java/com/github/microwind/userdemo/exception/DuplicateKeyException.java`
- 继承自 `BusinessException`
- 默认状态码：400 Bad Request
- 用于处理唯一约束违反

### 2. 服务层异常处理

#### UserService 修改
- 路径：`src/main/java/com/github/microwind/userdemo/service/UserService.java`
- 在 `createUser()` 和 `updateUser()` 方法中添加异常处理
- 捕获 `SQLIntegrityConstraintViolationException`
- 解析错误消息，识别具体的约束类型：
  - `unique_email` → "邮箱已被使用"
  - `unique_phone` → "手机号已被使用"
  - 其他 → "数据重复，违反唯一约束"

#### 异常处理流程
```java
try {
    // 数据库操作
} catch (DuplicateKeyException e) {
    // 重新抛出业务异常
    throw e;
} catch (Exception e) {
    // 遍历异常链
    Throwable cause = e.getCause();
    while (cause != null) {
        if (cause instanceof SQLIntegrityConstraintViolationException) {
            String message = cause.getMessage();
            // 解析具体约束类型并抛出对应异常
            if (message.contains("Duplicate entry") && message.contains("unique_email")) {
                throw new DuplicateKeyException("邮箱已被使用", cause);
            }
            // ... 其他约束检查
        }
        cause = cause.getCause();
    }
    // 未知异常
    throw new BusinessException(500, "创建用户失败: " + e.getMessage(), e);
}
```

### 3. 控制器层异常处理

#### UserController 修改
- 路径：`src/main/java/com/github/microwind/userdemo/controller/UserController.java`
- 在 `create()` 和 `update()` 方法中添加异常捕获
- 返回适当的 HTTP 状态码和错误消息

```java
try {
    // 业务逻辑
    userService.createUser(user);
    return ApiResponse.success(user, "创建用户成功");
} catch (DuplicateKeyException e) {
    return ApiResponse.badRequest(e.getMessage());
} catch (BusinessException e) {
    return ApiResponse.failure(e.getCode(), e.getMessage());
} catch (Exception e) {
    return ApiResponse.failure("创建用户失败: " + e.getMessage());
}
```

### 4. 数据库约束

#### 已添加的唯一约束
- `unique_email` - 邮箱唯一约束
- `unique_phone` - 手机号唯一约束
- `unique_name` - 用户名唯一约束（应用层检查）

#### SQL 脚本
创建了 `create-users-table.sql` 包含完整的表结构定义。

## 测试验证

### 测试脚本
- 文件：`test-database-errors.sh`
- 测试场景：
  1. ✅ 创建用户成功
  2. ✅ 重复用户名返回 400 错误
  3. ✅ 重复邮箱返回 400 错误
  4. ✅ 重复手机号返回 400 错误
  5. ✅ 创建第二个用户成功
  6. ✅ 更新用户时邮箱冲突返回 400 错误

### 测试结果

所有测试通过！错误响应示例：

```json
{
  "code": 400,
  "message": "邮箱已被使用"
}
```

```json
{
  "code": 400,
  "message": "用户名已存在"
}
```

```json
{
  "code": 400,
  "message": "手机号已被使用"
}
```

## 优势

1. **用户友好**：将技术性错误消息转换为易懂的中文提示
2. **一致性**：统一的错误响应格式
3. **可扩展**：易于添加新的约束类型检查
4. **标准化**：使用标准的 HTTP 状态码
5. **可维护**：异常处理逻辑集中在服务层

## 使用方法

### 运行测试
```bash
chmod +x test-database-errors.sh
./test-database-errors.sh
```

### 手动测试

#### 创建用户
```bash
curl -X POST http://localhost:8080/user \
  -H "Content-Type: application/json" \
  -d '{"name":"testuser","email":"test@example.com","phone":"13800138000"}'
```

#### 测试重复邮箱
```bash
curl -X POST http://localhost:8080/user \
  -H "Content-Type: application/json" \
  -d '{"name":"anotheruser","email":"test@example.com","phone":"13800138001"}'
```

预期响应：
```json
{
  "code": 400,
  "message": "邮箱已被使用"
}
```

## 技术栈

- **异常处理**：Java try-catch, 异常链遍历
- **数据库**：MySQL 唯一约束
- **框架**：SpringWind 自研框架
- **序列化**：Jackson
- **测试**：Bash Shell 脚本

## 注意事项

1. 用户名的唯一性在应用层检查（`UserService.createUser()` 中的 `findByUsername()`）
2. 邮箱和手机号的唯一性由数据库约束保证
3. 数据库需要先添加唯一约束才能生效
4. 异常消息解析依赖于 MySQL 的错误消息格式

## 相关文件

- `src/main/java/com/github/microwind/userdemo/exception/BusinessException.java`
- `src/main/java/com/github/microwind/userdemo/exception/DuplicateKeyException.java`
- `src/main/java/com/github/microwind/userdemo/service/UserService.java`
- `src/main/java/com/github/microwind/userdemo/controller/UserController.java`
- `test-database-errors.sh`
- `create-users-table.sql`

## 总结

成功实现了数据库约束违反错误的友好处理机制，提升了 API 的用户体验。所有测试用例通过，系统能够正确识别并返回友好的错误消息。
