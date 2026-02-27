# API 测试用例文档

本文档列出为 User 和 Order API 编写的完整测试用例，包括创建、读取、更新和删除（CRUD）操作，以及特殊业务逻辑测试。

## 项目信息

- **测试框架**: JUnit 5 + Mockito + Spring Test
- **测试类型**: WebMvcTest（单元测试，Mock Service 层）
- **测试文件位置**:
  - User Controller 测试: `src/test/java/com/github/microwind/springboot4ddd/interfaces/controller/user/UserControllerTest.java`
  - Order Controller 测试: `src/test/java/com/github/microwind/springboot4ddd/interfaces/controller/order/OrderControllerTest.java`

---

## User API 测试用例

### 1. CREATE - 创建用户

#### 1.1 创建用户 - 成功
- **测试方法**: `testCreateUser_Success`
- **请求**: `POST /api/users`
- **请求体**:
  ```json
  {
    "name": "testuser",
    "email": "test@example.com",
    "phone": "13800138000",
    "wechat": "test_wechat",
    "address": "Beijing"
  }
  ```
- **预期结果**:
  - 状态码: 200
  - 返回用户ID、名称、邮箱等完整信息
- **验证**:
  - UserService.createUser() 被调用 1 次

#### 1.2 创建用户 - 用户名已存在
- **测试方法**: `testCreateUser_NameExists`
- **请求**: `POST /api/users`
- **请求体**: 用户名 = "existinguser"（已存在）
- **预期结果**:
  - 状态码: 400 Bad Request
- **业务规则**: 用户名必须唯一

#### 1.3 创建用户 - 邮箱格式不正确
- **测试方法**: `testCreateUser_InvalidEmail`
- **请求**: `POST /api/users`
- **请求体**: email = "invalid-email"
- **预期结果**:
  - 状态码: 400 Bad Request
- **业务规则**: 邮箱必须符合 @Email 格式

#### 1.4 创建用户 - 手机号格式不正确
- **测试方法**: `testCreateUser_InvalidPhone`
- **请求**: `POST /api/users`
- **请求体**: phone = "12345"
- **预期结果**:
  - 状态码: 400 Bad Request
- **业务规则**: 手机号必须符合中国手机号格式 (1[3-9]\\d{9})

---

### 2. READ - 读取用户

#### 2.1 获取所有用户 - 成功
- **测试方法**: `testGetAllUsers_Success`
- **请求**: `GET /api/users`
- **预期结果**:
  - 状态码: 200
  - 返回用户列表（数组）
  - 包含 id、name、email 等字段
- **验证**:
  - UserService.getAllUsers() 被调用 1 次

#### 2.2 分页查询用户 - 成功
- **测试方法**: `testGetUsersByPage_Success`
- **请求**: `GET /api/users/page?page=1&size=10`
- **预期结果**:
  - 状态码: 200
  - 返回分页结果，包含 content、totalElements、totalPages 等字段
- **分页参数**:
  - page: 从 1 开始
  - size: 每页条数（>0）
  - sort: 排序字段（可选）

#### 2.3 根据 ID 获取用户 - 成功
- **测试方法**: `testGetUserById_Success`
- **请求**: `GET /api/users/{id}`
  - 示例: `GET /api/users/1`
- **预期结果**:
  - 状态码: 200
  - 返回指定 ID 的用户详情

#### 2.4 根据 ID 获取用户 - 用户不存在
- **测试方法**: `testGetUserById_NotFound`
- **请求**: `GET /api/users/{id}`
  - 示例: `GET /api/users/999`（不存在的 ID）
- **预期结果**:
  - 状态码: 500 Internal Server Error

#### 2.5 根据用户名获取用户 - 成功
- **测试方法**: `testGetUserByName_Success`
- **请求**: `GET /api/users/name/{name}`
  - 示例: `GET /api/users/name/testuser`
- **预期结果**:
  - 状态码: 200
  - 返回匹配用户名的用户信息

---

### 3. UPDATE - 更新用户

#### 3.1 更新用户 - 成功
- **测试方法**: `testUpdateUser_Success`
- **请求**: `PUT /api/users/{id}`
- **请求体**:
  ```json
  {
    "name": "updateduser",
    "email": "updated@example.com",
    "phone": "13900139000",
    "wechat": "updated_wechat",
    "address": "Shanghai"
  }
  ```
- **预期结果**:
  - 状态码: 200
  - 返回更新后的用户信息

#### 3.2 更新用户 - 用户不存在
- **测试方法**: `testUpdateUser_NotFound`
- **请求**: `PUT /api/users/999`
- **预期结果**:
  - 状态码: 500 Internal Server Error

#### 3.3 更新用户 - 邮箱已被使用
- **测试方法**: `testUpdateUser_EmailInUse`
- **请求**: `PUT /api/users/{id}`
- **请求体**: email = "existing@example.com"（已被其他用户使用）
- **预期结果**:
  - 状态码: 400 Bad Request
- **业务规则**: 邮箱必须唯一

---

### 4. DELETE - 删除用户

#### 4.1 删除用户 - 成功
- **测试方法**: `testDeleteUser_Success`
- **请求**: `DELETE /api/users/{id}`
  - 示例: `DELETE /api/users/1`
- **预期结果**:
  - 状态码: 200
  - message: "删除成功"
- **验证**:
  - UserService.deleteUser() 被调用 1 次

#### 4.2 删除用户 - 用户不存在
- **测试方法**: `testDeleteUser_NotFound`
- **请求**: `DELETE /api/users/999`
- **预期结果**:
  - 状态码: 500 Internal Server Error

---

## Order API 测试用例

### 1. CREATE - 创建订单

#### 1.1 创建订单 - 成功
- **测试方法**: `testCreateOrder_Success`
- **请求**: `POST /api/orders/create`
- **请求体**:
  ```json
  {
    "userId": 1,
    "totalAmount": 99.99
  }
  ```
- **预期结果**:
  - 状态码: 200
  - 返回订单 ID、订单号、用户 ID、金额、状态等信息
  - 初始状态: "PENDING"（待支付）
- **验证**:
  - OrderService.createOrder() 被调用 1 次
- **注意**: 订单号自动生成（格式: ORD + 时间戳）

#### 1.2 创建订单 - 用户不存在
- **测试方法**: `testCreateOrder_UserNotFound`
- **请求**: `POST /api/orders/create`
- **请求体**: userId = 999（不存在的用户）
- **预期结果**:
  - 状态码: 500 Internal Server Error

#### 1.3 创建订单 - 金额必须大于0
- **测试方法**: `testCreateOrder_InvalidAmount`
- **请求**: `POST /api/orders/create`
- **请求体**: totalAmount = -10.00
- **预期结果**:
  - 状态码: 400 Bad Request
- **业务规则**: 订单金额必须为正数

---

### 2. READ - 读取订单

#### 2.1 获取订单详情 - 成功
- **测试方法**: `testGetOrderDetail_Success`
- **请求**: `GET /api/orders/{id}`
  - 示例: `GET /api/orders/1`
- **预期结果**:
  - 状态码: 200
  - 返回订单详情（不包含用户信息）
  - 包含状态描述（statusDesc: "待支付"）
- **返回字段**: id, orderNo, userId, totalAmount, status, statusDesc, createdAt, updatedAt

#### 2.2 获取订单详情 - 订单不存在
- **测试方法**: `testGetOrderDetail_NotFound`
- **请求**: `GET /api/orders/999`
- **预期结果**:
  - 状态码: 500 Internal Server Error

#### 2.3 获取用户订单列表 - 成功
- **测试方法**: `testGetUserOrderList_Success`
- **请求**: `GET /api/orders/user/{userId}`
  - 示例: `GET /api/orders/user/1`
- **预期结果**:
  - 状态码: 200
  - 返回用户的所有订单（含用户信息）
  - 每条包含订单信息和用户名、用户电话
- **特点**:
  - 跨库查询（订单来自 PostgreSQL，用户来自 MySQL）
  - 显示用户信息（userName, userPhone）

#### 2.4 分页查询用户订单 - 成功
- **测试方法**: `testGetUserOrderListByPage_Success`
- **请求**: `GET /api/orders/user/{userId}/page?page=1&size=10`
  - 示例: `GET /api/orders/user/1/page?page=1&size=10`
- **预期结果**:
  - 状态码: 200
  - 返回分页结果（content, totalElements, totalPages 等）

#### 2.5 获取所有订单列表 - 成功
- **测试方法**: `testGetAllOrderList_Success`
- **请求**: `GET /api/orders/list`
- **预期结果**:
  - 状态码: 200
  - 返回系统中所有订单（含用户信息）

#### 2.6 分页查询所有订单 - 成功
- **测试方法**: `testGetAllOrderListByPage_Success`
- **请求**: `GET /api/orders/page?page=1&size=10`
- **预期结果**:
  - 状态码: 200
  - 返回分页结果

---

### 3. UPDATE - 订单状态变更

#### 3.1 支付订单 - 成功
- **测试方法**: `testPayOrder_Success`
- **请求**: `POST /api/orders/{id}/pay`
  - 示例: `POST /api/orders/1/pay`
- **预期结果**:
  - 状态码: 200
  - 订单状态变更为 "PAID"（已支付）
- **特点**:
  - 状态转移: PENDING → PAID
  - 需要签名验证（@RequireSign）
- **验证**:
  - OrderService.payOrder() 被调用 1 次

#### 3.2 支付订单 - 订单不存在
- **测试方法**: `testPayOrder_NotFound`
- **请求**: `POST /api/orders/999/pay`
- **预期结果**:
  - 状态码: 500 Internal Server Error

#### 3.3 支付订单 - 订单已支付
- **测试方法**: `testPayOrder_AlreadyPaid`
- **请求**: `POST /api/orders/1/pay`（订单已支付）
- **预期结果**:
  - 状态码: 400 Bad Request
- **业务规则**: 已支付订单不能重复支付

#### 3.4 取消订单 - 成功
- **测试方法**: `testCancelOrder_Success`
- **请求**: `POST /api/orders/{id}/cancel`
  - 示例: `POST /api/orders/1/cancel`
- **预期结果**:
  - 状态码: 200
  - 订单状态变更为 "CANCELLED"（已取消）
- **状态转移**: PENDING → CANCELLED
- **需要签名验证**: @RequireSign

#### 3.5 取消订单 - 已支付订单不能取消
- **测试方法**: `testCancelOrder_AlreadyPaid`
- **请求**: `POST /api/orders/1/cancel`（订单状态为 PAID）
- **预期结果**:
  - 状态码: 400 Bad Request
- **业务规则**: 只有 PENDING 状态的订单能取消

#### 3.6 完成订单 - 成功
- **测试方法**: `testCompleteOrder_Success`
- **请求**: `POST /api/orders/{id}/complete`
  - 示例: `POST /api/orders/1/complete`
- **预期结果**:
  - 状态码: 200
  - 订单状态变更为 "COMPLETED"（已完成）
- **状态转移**: PAID → COMPLETED
- **需要签名验证**: @RequireSign

#### 3.7 完成订单 - 未支付订单不能完成
- **测试方法**: `testCompleteOrder_NotPaid`
- **请求**: `POST /api/orders/1/complete`（订单状态为 PENDING）
- **预期结果**:
  - 状态码: 400 Bad Request
- **业务规则**: 只有 PAID 状态的订单能完成

---

### 4. DELETE - 删除订单

#### 4.1 删除订单 - 成功
- **测试方法**: `testDeleteOrder_Success`
- **请求**: `DELETE /api/orders/{id}`
  - 示例: `DELETE /api/orders/1`
- **预期结果**:
  - 状态码: 200
  - message: "删除成功"
- **需要签名验证**: @RequireSign
- **验证**:
  - OrderService.deleteOrder() 被调用 1 次

#### 4.2 删除订单 - 订单不存在
- **测试方法**: `testDeleteOrder_NotFound`
- **请求**: `DELETE /api/orders/999`
- **预期结果**:
  - 状态码: 500 Internal Server Error

#### 4.3 删除订单 - 已支付订单不能删除
- **测试方法**: `testDeleteOrder_AlreadyPaid`
- **请求**: `DELETE /api/orders/1`（订单状态为 PAID）
- **预期结果**:
  - 状态码: 400 Bad Request
- **业务规则**: 只有 PENDING 状态的订单能删除

---

### 5. 订单状态流转测试

#### 5.1 订单状态流转 - PENDING → PAID → COMPLETED
- **测试方法**: `testOrderStatusFlow_Success`
- **完整流程**:
  1. 创建订单 → 状态: PENDING
  2. 支付订单 → 状态: PAID
  3. 完成订单 → 状态: COMPLETED
- **预期结果**:
  - 每一步都返回 200 OK
  - 状态正确转移
- **业务流程验证**: 验证订单完整的生命周期

---

## 订单状态枚举说明

| 状态代码 | 状态名称 | 状态描述 | 可转移的状态 |
|---------|---------|---------|------------|
| PENDING | 待支付 | 订单创建后的初始状态 | PAID, CANCELLED |
| PAID | 已支付 | 用户支付后的状态 | COMPLETED |
| CANCELLED | 已取消 | 用户取消订单 | 无（终止状态） |
| COMPLETED | 已完成 | 订单完全完成 | 无（终止状态） |

---

## 测试参数说明

### 用户验证规则

| 字段 | 验证规则 | 示例 |
|------|---------|------|
| name | 3-20 字符，字母数字下划线 | testuser, user_01 |
| email | 有效的邮箱格式 | test@example.com |
| phone | 中国手机号 (1[3-9]\\d{9}) | 13800138000 |
| wechat | 可选字段 | test_wechat |
| address | 可选字段 | Beijing, Shanghai |

### 订单验证规则

| 字段 | 验证规则 | 示例 |
|------|---------|------|
| userId | 必填，必须存在 | 1, 2, 100 |
| totalAmount | 必填，必须 > 0 | 99.99, 199.99 |

---

## 运行测试

### 运行所有测试
```bash
mvn test
```

### 运行特定测试类
```bash
mvn test -Dtest=UserControllerTest
mvn test -Dtest=OrderControllerTest
```

### 运行特定测试方法
```bash
mvn test -Dtest=UserControllerTest#testCreateUser_Success
```

### 生成测试覆盖率报告
```bash
mvn clean test jacoco:report
```

---

## 测试统计

### User Controller 测试用例
- **CREATE**: 4 个测试用例
- **READ**: 5 个测试用例
- **UPDATE**: 3 个测试用例
- **DELETE**: 2 个测试用例
- **总计**: 14 个测试用例

### Order Controller 测试用例
- **CREATE**: 3 个测试用例
- **READ**: 6 个测试用例
- **UPDATE（状态变更）**: 7 个测试用例
- **DELETE**: 3 个测试用例
- **状态流转**: 1 个综合测试
- **总计**: 20 个测试用例

### 总体统计
- **总测试用例数**: 34 个
- **覆盖的 HTTP 方法**: GET, POST, PUT, DELETE
- **覆盖的业务场景**: CRUD + 状态转移 + 验证规则

---

## 注意事项

1. **Mock vs Integration**: 当前测试使用 `@WebMvcTest` + Mock，只测试 Controller 层。如需完整的集成测试，需要配置数据库。

2. **签名验证**: Order API 中的某些端点需要签名验证（@RequireSign）。当前测试未包含签名生成逻辑，实际测试时需要补充。

3. **数据库**: 测试环境未配置实际数据库，使用 Mock 数据。若需测试真实数据库操作，需要配置 H2 或 TestContainers。

4. **异常处理**: 测试验证了异常情况，确保 API 正确处理各种错误场景。

5. **参数验证**: 使用 JSR-303 注解验证，框架自动处理格式检查。

---

## 扩展建议

1. **Service 层测试**: 为 UserService 和 OrderService 添加单元测试
2. **Repository 层测试**: 为数据库操作添加集成测试
3. **事件测试**: 测试订单事件发布功能
4. **并发测试**: 添加多线程场景测试
5. **性能测试**: 添加大数据量场景测试
