# 异步任务和邮件发送功能

本项目集成了Spring定时任务和邮件发送功能，作为脚手架示例，展示如何实现订单超时监控。

## 功能说明

### 1. 定时任务 - 订单超时监控

**功能描述**：
- 每小时自动扫描订单表（orders）
- 检测创建超过2小时仍未支付的订单（状态为PENDING）
- 发送HTML格式的提醒邮件给管理员

**实现文件**：
- `application/scheduled/OrderScanScheduledTask.java` - 定时任务实现类

**执行频率**：
- 默认每小时整点执行一次（cron: `0 0 * * * ?`）
- 可根据需要修改`@Scheduled`注解中的cron表达式

**修改执行频率示例**：
```java
// 每30分钟执行一次
@Scheduled(cron = "0 */30 * * * ?")

// 每天凌晨2点执行
@Scheduled(cron = "0 0 2 * * ?")

// 每5分钟执行一次（测试用）
@Scheduled(cron = "0 */5 * * * ?")
```

### 2. 邮件发送服务

**功能描述**：
- 支持发送纯文本邮件和HTML格式邮件
- 自动配置QQ邮箱SMTP服务器
- 简单易用的API接口

**实现文件**：
- `infrastructure/notification/EmailService.java` - 邮件服务类

**使用示例**：
```java
@Autowired
private EmailService emailService;

// 发送简单文本邮件
emailService.sendSimpleEmail("user@example.com", "测试邮件", "这是邮件内容");

// 发送HTML格式邮件
emailService.sendHtmlEmail("user@example.com", "HTML邮件", "<h1>这是HTML内容</h1>");
```

## 配置说明

### 1. Maven依赖

已在`pom.xml`中添加邮件支持：
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

### 2. 启用定时任务

在主应用类中已添加`@EnableScheduling`注解：
```java
@SpringBootApplication
@EnableScheduling
public class Application {
    // ...
}
```

### 3. 邮件配置

#### application.yaml（全局配置）
```yaml
spring:
  mail:
    host: smtp.qq.com
    port: 587
    protocol: smtp
    default-encoding: UTF-8
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
            required: true
```

#### application-dev.yaml（开发环境配置）
```yaml
spring:
  mail:
    username: 12262529@qq.com
    password: your-qq-mail-authorization-code  # 需要替换为实际的QQ邮箱授权码
```

**重要提示**：
1. `password` 字段需要填写QQ邮箱的**授权码**，不是QQ密码
2. 获取QQ邮箱授权码步骤：
   - 登录QQ邮箱 → 设置 → 账户
   - 找到"POP3/IMAP/SMTP/Exchange/CardDAV/CalDAV服务"
   - 开启"POP3/SMTP服务"或"IMAP/SMTP服务"
   - 发送短信验证后，会显示授权码
   - 将授权码填入配置文件的password字段

### 4. 修改管理员邮箱

在`OrderScanScheduledTask.java`中修改管理员邮箱地址：
```java
private static final String ADMIN_EMAIL = "your-email@qq.com";
```

### 5. 修改超时时间

在`OrderScanScheduledTask.java`中修改超时小时数：
```java
private static final int TIMEOUT_HOURS = 2;  // 默认2小时，可修改为其他值
```

## 新增的Repository方法

为支持订单超时查询，在Repository层添加了新方法：

**OrderJdbcRepository.java**：
```java
@Query("SELECT * FROM orders WHERE status = :status AND created_at < :beforeTime ORDER BY created_at ASC")
List<Order> findByStatusAndCreatedAtBefore(@Param("status") String status, @Param("beforeTime") LocalDateTime beforeTime);
```

**使用示例**：
```java
LocalDateTime twoHoursAgo = LocalDateTime.now().minusHours(2);
List<Order> unpaidOrders = orderRepository.findByStatusAndCreatedAtBefore("PENDING", twoHoursAgo);
```

## 项目结构

```
src/main/java/com/github/microwind/springboot4ddd/
├── application/
│   └── scheduled/
│       └── OrderScanScheduledTask.java          # 订单扫描定时任务
├── infrastructure/
│   └── notification/
│       └── EmailService.java                    # 邮件发送服务
├── domain/
│   └── repository/
│       └── order/
│           └── OrderRepository.java             # 添加了超时查询方法
└── infrastructure/
    └── repository/
        ├── jdbc/
        │   └── OrderJdbcRepository.java         # 添加了超时查询SQL
        └── order/
            └── OrderRepositoryImpl.java         # 实现了超时查询方法
```

## 测试建议

1. **测试邮件发送**：
   - 先配置好邮箱授权码
   - 修改定时任务频率为每分钟一次：`@Scheduled(cron = "0 * * * * ?")`
   - 创建测试订单，设置创建时间为3小时前
   - 等待定时任务执行，检查是否收到邮件

2. **测试定时任务**：
   - 查看日志输出，确认定时任务正常执行
   - 检查数据库中的订单状态和创建时间
   - 验证邮件内容是否正确

## 注意事项

1. **邮箱安全**：
   - 不要将真实的邮箱密码或授权码提交到代码仓库
   - 建议使用环境变量或配置中心管理敏感信息
   - 生产环境应使用专门的邮件发送服务或企业邮箱

2. **定时任务性能**：
   - 如果订单量很大，考虑添加分页查询
   - 可以添加缓存机制，避免重复发送邮件
   - 建议在非高峰期执行定时任务

3. **邮件发送频率**：
   - QQ邮箱有发送频率限制，避免短时间内发送大量邮件
   - 建议合并多个订单信息到一封邮件中

4. **扩展建议**：
   - 可以添加短信通知功能
   - 可以添加钉钉/企业微信机器人通知
   - 可以记录通知历史，避免重复通知

## 相关技术文档

- [Spring Boot邮件发送](https://docs.spring.io/spring-boot/reference/io/email.html)
- [Spring定时任务](https://docs.spring.io/spring-framework/reference/integration/scheduling.html)
- [Cron表达式说明](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/scheduling/support/CronExpression.html)
