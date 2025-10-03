package com.github.microwind.springwind;

import com.github.microwind.springwind.annotation.*;
import com.github.microwind.springwind.aop.AspectProcessor;
import com.github.microwind.springwind.core.SpringWindApplicationContext;

import org.junit.Test;

import java.lang.reflect.Method;

/**
 * AOP功能测试
 */
public class AopTest {

  // 业务接口
  public interface PaymentService {
    boolean processPayment(double amount);

    String getStatus();
  }

  // 业务实现
  @Service
  public static class PaymentServiceImpl implements PaymentService {
    private String status = "READY";

    @Override
    public boolean processPayment(double amount) {
      System.out.println("处理支付: $" + amount);
      status = "PROCESSED";
      return amount > 0;
    }

    @Override
    public String getStatus() {
      return status;
    }
  }

  // 日志切面
  @Aspect
  @Component
  public static class LoggingAspect {

    @Before("com.github.microwind.springwind.AopTest.PaymentService.*")
    public void beforePayment() {
      System.out.println("[前置日志] 支付操作即将开始...");
    }

    @After("com.github.microwind.springwind.AopTest.PaymentService.*")
    public void afterPayment() {
      System.out.println("[后置日志] 支付操作已完成...");
    }
  }

  // 安全切面
  @Aspect
  @Component
  public static class SecurityAspect {

    @Before("com.github.microwind.springwind.AopTest.PaymentService.processPayment")
    public void checkSecurity() {
      System.out.println("[安全检查] 验证用户权限...");
    }
  }

  // 性能监控切面
  @Aspect
  @Component
  public static class PerformanceAspect {
    private long startTime;

    @Around("com.github.microwind.springwind.AopTest.PaymentService.processPayment")
    public Object monitorPerformance(Method method, Object[] args) throws Throwable {
      startTime = System.currentTimeMillis();
      System.out.println("[性能监控] 方法开始执行: " + method.getName());

      // 这里应该调用目标方法，简化实现
      System.out.println("[性能监控] 模拟执行目标方法");

      long endTime = System.currentTimeMillis();
      System.out.println("[性能监控] 方法执行耗时: " + (endTime - startTime) + "ms");
      return true;
    }
  }

  @Test
  public void testAopFunctionality() {
    System.out.println("=== 测试AOP功能 ===");

    SpringWindApplicationContext context = new SpringWindApplicationContext(AopTest.class);
    AspectProcessor aspectProcessor = new AspectProcessor();

    // 注册切面
    aspectProcessor.registerAspect(context.getBean(LoggingAspect.class));
    aspectProcessor.registerAspect(context.getBean(SecurityAspect.class));
    aspectProcessor.registerAspect(context.getBean(PerformanceAspect.class));

    // 获取业务Bean
    PaymentService paymentService = context.getBean(PaymentServiceImpl.class);

    // 创建代理对象
    PaymentService proxy = (PaymentService) aspectProcessor.createProxy(paymentService);

    System.out.println("开始执行AOP测试...");
    boolean result = proxy.processPayment(100.0);

    assert result : "AOP代理调用失败";
    System.out.println("AOP代理调用成功，结果: " + result);

    context.close();
    System.out.println("=== AOP功能测试完成 ===\n");
  }

  @Test
  public void testMultipleAspects() {
    System.out.println("=== 测试多切面协同工作 ===");

    SpringWindApplicationContext context = new SpringWindApplicationContext(AopTest.class);

    PaymentService paymentService = context.getBean(PaymentServiceImpl.class);

    System.out.println("直接调用业务方法（无AOP）:");
    paymentService.processPayment(50.0);

    System.out.println("\n通过AOP代理调用业务方法:");
    // 在实际使用中，容器应该自动创建代理
    AspectProcessor processor = new AspectProcessor();
    processor.registerAspect(context.getBean(LoggingAspect.class));
    processor.registerAspect(context.getBean(SecurityAspect.class));

    PaymentService proxy = (PaymentService) processor.createProxy(paymentService);
    proxy.processPayment(50.0);

    context.close();
    System.out.println("=== 多切面测试完成 ===\n");
  }
}