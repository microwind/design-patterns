# retry-pattern (Java)

## 模块说明

重试模式的 Java 实现。使用 BooleanSupplier 函数式接口作为重试策略，Java 16+ record 作为返回值。

## 设计模式应用

- **策略模式**：operation 作为 BooleanSupplier 传入，可以是任何返回 boolean 的 lambda。
- **模板方法模式**：retry 定义了循环调用的骨架。

## 代码结构

```
src/
  RetryPattern.java  — retry 静态方法 + ScriptedOperation + RetryResult record
test/
  Test.java          — 验证成功重试和最终失败
```

## 与实际工程对比

| 维度 | 本示例 | Resilience4j / Spring Retry |
|---|---|---|
| 退避 | 无 | 指数退避 + 抖动 |
| 异常分类 | 无 | 可重试/不可重试异常 |
| 异步 | 同步 | CompletableFuture |

## 测试验证

```bash
cd microservice-architecture/retry-pattern/java
javac src/RetryPattern.java test/Test.java
java test.Test
```
