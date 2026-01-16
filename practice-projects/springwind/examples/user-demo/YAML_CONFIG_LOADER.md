# YamlConfigLoader 使用指南

## 概述

`YamlConfigLoader` 是一个独立的 YAML 配置文件加载器工具类，为整个应用提供统一的配置管理。

**特点：**
- ✅ 单例模式，全局唯一实例
- ✅ 支持多种数据类型获取（字符串、整数、长整数、布尔值）
- ✅ 支持嵌套配置路径访问
- ✅ 配置不存在时自动返回默认值
- ✅ 容错机制，配置文件丢失时应用仍可启动

## 文件位置

```
examples/user-demo/src/main/java/com/github/microwind/userdemo/config/YamlConfigLoader.java
```

## 使用示例

### 1. 基本使用

```java
// 获取单例实例
YamlConfigLoader loader = YamlConfigLoader.getInstance();

// 获取字符串配置
String jdbcUrl = loader.getString("user.datasource.jdbc-url", "默认URL");

// 获取整数配置
int maxPoolSize = loader.getInt("user.datasource.maximum-pool-size", 10);

// 获取长整数配置
long timeout = loader.getLong("user.datasource.connection-timeout", 30000);

// 获取布尔值配置
boolean autoCommit = loader.getBoolean("app.autocommit", true);
```

### 2. 在配置类中使用

```java
@Configuration
public class MyConfig {
    
    @Bean
    public SomeService someService() {
        YamlConfigLoader config = YamlConfigLoader.getInstance();
        
        String param1 = config.getString("my.service.param1", "默认值");
        int param2 = config.getInt("my.service.param2", 100);
        
        return new SomeService(param1, param2);
    }
}
```

### 3. 检查配置是否存在

```java
YamlConfigLoader config = YamlConfigLoader.getInstance();

if (config.containsKey("user.datasource.jdbc-url")) {
    String url = config.getString("user.datasource.jdbc-url", "");
    // 处理存在的配置
}
```

### 4. 获取配置对象

```java
YamlConfigLoader config = YamlConfigLoader.getInstance();

// 获取嵌套的配置映射
Map<String, Object> datasourceConfig = config.getMap("user.datasource");
if (datasourceConfig != null) {
    for (String key : datasourceConfig.keySet()) {
        System.out.println(key + " = " + datasourceConfig.get(key));
    }
}
```

### 5. 打印所有配置（调试）

```java
YamlConfigLoader config = YamlConfigLoader.getInstance();
config.printConfig();
```

输出示例：
```
========== 应用配置信息 ==========
app.name = user-demo
app.version = 1.0.0
user.datasource.jdbc-url = jdbc:mysql://localhost:3306/frog?...
user.datasource.username = frog_admin
user.datasource.maximum-pool-size = 10
==================================
```

## application.yml 配置文件

配置文件位置：`src/main/resources/application.yml`

```yaml
app:
  name: user-demo
  version: 1.0.0

user:
  datasource:
    jdbc-url: jdbc:mysql://localhost:3306/frog?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC
    username: frog_admin
    password: frog798
    driver-class-name: com.mysql.cj.jdbc.Driver
    maximum-pool-size: 10
    minimum-idle: 5
    connection-timeout: 30000
    idle-timeout: 600000
    max-lifetime: 1800000
```

## 路径语法

使用 **点号（.）** 分隔的路径来访问嵌套配置：

```
顶层配置          → "key"
一层嵌套           → "parent.child"
多层嵌套           → "parent.child.grandchild"
```

**示例：**
```yaml
user:
  datasource:
    jdbc-url: jdbc:mysql://...
```

对应的访问路径：`user.datasource.jdbc-url`

## API 列表

### getString(path, defaultValue)
获取字符串值

```java
String value = loader.getString("app.name", "unknown");
```

### getInt(path, defaultValue)
获取整数值

```java
int value = loader.getInt("user.datasource.maximum-pool-size", 10);
```

### getLong(path, defaultValue)
获取长整数值

```java
long value = loader.getLong("app.timeout", 30000L);
```

### getBoolean(path, defaultValue)
获取布尔值

支持的真值：`true`, `yes`, `1`
支持的假值：`false`, `no`, `0`

```java
boolean value = loader.getBoolean("app.debug", false);
```

### getMap(path)
获取配置映射对象

```java
Map<String, Object> config = loader.getMap("user.datasource");
```

### containsKey(path)
检查配置是否存在

```java
boolean exists = loader.containsKey("user.datasource.jdbc-url");
```

### printConfig()
打印所有配置信息（用于调试）

```java
loader.printConfig();
```

### getRawConfig()
获取原始配置映射（高级用法）

```java
Map<String, Object> allConfig = loader.getRawConfig();
```

## 错误处理

`YamlConfigLoader` 提供了完善的容错机制：

1. **配置文件不存在** → 返回默认值，应用继续启动
2. **配置路径不存在** → 返回提供的默认值
3. **类型转换失败** → 返回默认值，不抛异常

这样确保了配置缺失不会导致应用崩溃。

## 最佳实践

1. **统一使用 getInstance()**
   ```java
   YamlConfigLoader config = YamlConfigLoader.getInstance();
   ```
   不要创建多个实例，始终使用单例。

2. **提供合理的默认值**
   ```java
   int timeout = loader.getInt("app.timeout", 30000); // 30秒默认
   ```
   应该提供有意义的默认值，而不是 null。

3. **在配置类中使用**
   在 `@Configuration` 标记的配置类中读取配置，而不是在业务代码中。

4. **避免重复读取**
   ```java
   // ✅ 好：在初始化时读取一次
   YamlConfigLoader config = YamlConfigLoader.getInstance();
   String value = config.getString("path", "default");
   
   // ❌ 差：每次都读取
   for (int i = 0; i < 1000; i++) {
       String value = YamlConfigLoader.getInstance().getString("path", "default");
   }
   ```

5. **记录配置值**
   在应用启动时打印重要的配置信息，方便排查问题：
   ```java
   loader.printConfig();
   ```

## 扩展其他配置类

如果你需要为其他服务创建配置，可以按照 `DataSourceConfig` 的模式：

```java
@Configuration
public class RedisConfig {
    
    @Bean
    public RedisClient redisClient() {
        YamlConfigLoader config = YamlConfigLoader.getInstance();
        
        String host = config.getString("redis.host", "localhost");
        int port = config.getInt("redis.port", 6379);
        
        return new RedisClient(host, port);
    }
}
```

然后在 `application.yml` 中添加对应的配置：

```yaml
redis:
  host: localhost
  port: 6379
  password: your-password
  timeout: 10000
```

## 常见问题

**Q: 为什么使用单例模式？**
A: 确保全应用只加载一次配置文件，提高性能，避免重复解析。

**Q: 可以热更新配置吗？**
A: 当前版本不支持热更新。如需支持，可以添加文件监听机制。

**Q: 支持环境变量覆盖吗？**
A: 当前版本不支持。可以在 `getConfigValue` 方法中添加环保变量查询逻辑。

**Q: 配置文件编码问题？**
A: `application.yml` 应该使用 UTF-8 编码，放在 `src/main/resources` 目录下。
