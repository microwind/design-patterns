package com.github.microwind.springwind;

import com.github.microwind.springwind.annotation.Autowired;
import com.github.microwind.springwind.annotation.Component;
import com.github.microwind.springwind.annotation.Service;
import com.github.microwind.springwind.core.SpringWindApplicationContext;
import org.junit.Test;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;

/**
 * IoC容器功能测试
 */
public class IoCTest {

  @Component
  public static class UserRepository {
    private int initializationCount = 0;
    private int destructionCount = 0;

    public String findUserById(Long id) {
      return "User{id=" + id + ", name='John Doe'}";
    }

    @PostConstruct
    public void init() {
      initializationCount++;
      System.out.println("UserRepository 初始化完成，初始化次数: " + initializationCount);
    }

    @PreDestroy
    public void destroy() {
      destructionCount++;
      System.out.println("UserRepository 销毁完成，销毁次数: " + destructionCount);
    }

    public int getInitializationCount() {
      return initializationCount;
    }

    public int getDestructionCount() {
      return destructionCount;
    }
  }

  @Service
  public static class UserService {

    @Autowired
    private UserRepository userRepository;

    private String serviceName = "DefaultUserService";

    public String getUserInfo(Long userId) {
      return userRepository.findUserById(userId) + " processed by " + serviceName;
    }

    public UserRepository getUserRepository() {
      return userRepository;
    }

    public void setServiceName(String serviceName) {
      this.serviceName = serviceName;
    }

    @PostConstruct
    public void initializeService() {
      System.out.println("UserService 初始化完成");
    }
  }

  @Component("orderService")
  public static class OrderService {

    @Autowired
    private UserService userService;

    public String createOrder(Long userId, String product) {
      String userInfo = userService.getUserInfo(userId);
      return "Order for: " + userInfo + ", Product: " + product;
    }

    @PostConstruct
    public void init() {
      System.out.println("OrderService 初始化完成");
    }
  }

  @Test
  public void testBasicIoC() {
    System.out.println("=== 测试基础IoC功能 ===");

    SpringWindApplicationContext context = new SpringWindApplicationContext(IoCTest.class);

    // 测试按名称获取Bean
    UserService userService = (UserService) context.getBean("userService");
    assert userService != null : "按名称获取Bean失败";
    System.out.println("按名称获取Bean成功");

    // 测试按类型获取Bean
    UserService userServiceByType = context.getBean(UserService.class);
    assert userServiceByType != null : "按类型获取Bean失败";
    System.out.println("按类型获取Bean成功");

    // 测试依赖注入
    assert userService.getUserRepository() != null : "依赖注入失败";
    System.out.println("依赖注入成功");

    // 测试业务方法
    String userInfo = userService.getUserInfo(1L);
    System.out.println("业务方法调用: " + userInfo);

    // 测试自定义Bean名称
    OrderService orderService = (OrderService) context.getBean("orderService");
    assert orderService != null : "自定义Bean名称失败";
    String orderInfo = orderService.createOrder(1L, "MacBook Pro");
    System.out.println("自定义Bean名称测试: " + orderInfo);

    context.close();
    System.out.println("=== 基础IoC测试完成 ===\n");
  }

  @Test
  public void testBeanLifecycle() {
    System.out.println("=== 测试Bean生命周期 ===");

    SpringWindApplicationContext context = new SpringWindApplicationContext(IoCTest.class);

    UserRepository repository = context.getBean(UserRepository.class);

    // 验证初始化方法被调用
    assert repository.getInitializationCount() == 1 : "@PostConstruct 方法未正确调用";
    System.out.println("@PostConstruct 验证成功");

    context.close();

    // 注意：在测试环境中，@PreDestroy 可能不会立即执行
    System.out.println("Bean生命周期测试完成\n");
  }

  @Test
  public void testGetBeansWithAnnotation() {
    System.out.println("=== 测试注解扫描功能 ===");

    SpringWindApplicationContext context = new SpringWindApplicationContext(IoCTest.class);

    // 获取所有带有@Service注解的Bean
//    var serviceBeans = context.getBeansWithAnnotation(Service.class);
    Map<String, Object> serviceBeans = context.getBeansWithAnnotation(Service.class);
    assert !serviceBeans.isEmpty() : "注解扫描失败";
    System.out.println("找到 " + serviceBeans.size() + " 个@Service注解的Bean");

    // 获取所有Bean名称
    String[] beanNames = context.getBeanDefinitionNames();
    System.out.println("容器中总Bean数量: " + beanNames.length);
    for (String name : beanNames) {
      System.out.println("  - " + name);
    }

    context.close();
    System.out.println("=== 注解扫描测试完成 ===\n");
  }
}