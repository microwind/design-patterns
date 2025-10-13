# SpringWind Framework - 更新日志

## [1.1.0] - 2025-10-06

### 新增功能 ✨
- **日志系统**: 集成SLF4J + Logback日志框架，替换所有System.out.println
- **CGLIB代理**: AOP模块支持CGLIB代理，可代理没有接口的类
- **自定义异常**: 添加BeanCreationException、BeanNotFoundException、BeanDefinitionException、CircularDependencyException

### 重要修复 🐛
- **循环依赖检测**: IoC容器添加二级缓存和循环依赖检测机制
- **JDBC资源泄漏**: 修复资源关闭逻辑，每个资源独立try-catch避免关闭失败影响其他资源
- **参数校验**: 在关键方法添加参数非空校验，提升代码健壮性

### 性能优化 ⚡
- **构造器缓存**: IoC容器使用ConcurrentHashMap缓存构造器，避免重复反射
- **正则缓存**: AOP切面匹配使用Pattern缓存，避免重复编译正则表达式
- **并发优化**: Bean定义和单例对象使用ConcurrentHashMap提升并发性能

### 代码改进 📝
- **异常处理**: 使用具体异常类型替换通用Exception，提供更详细的错误信息
- **日志输出**: 所有核心模块添加DEBUG/INFO/ERROR级别日志
- **注解增强**: @Autowired添加required属性支持可选依赖注入

### 技术升级 🔧
- **Java版本**: 升级至Java 17
- **依赖管理**:
  - SLF4J 2.0.9
  - Logback 1.4.11
  - CGLIB 3.3.0

### 测试验证 ✅
- 所有测试通过 (21/21)
- 编译成功，无错误

---

## [1.0.0] - 2025-10-01

### 初始版本
- IoC容器基础功能
- AOP切面支持（JDK动态代理）
- MVC框架实现
- JDBC模板封装
