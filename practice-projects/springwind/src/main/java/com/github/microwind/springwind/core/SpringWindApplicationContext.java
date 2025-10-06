package com.github.microwind.springwind.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.github.microwind.springwind.annotation.Autowired;
import com.github.microwind.springwind.annotation.Component;
import com.github.microwind.springwind.annotation.Controller;
import com.github.microwind.springwind.annotation.Repository;
import com.github.microwind.springwind.annotation.Service;
import com.github.microwind.springwind.exception.BeanCreationException;
import com.github.microwind.springwind.exception.BeanDefinitionException;
import com.github.microwind.springwind.exception.BeanNotFoundException;
import com.github.microwind.springwind.exception.CircularDependencyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SpringWind核心容器类 - IoC容器实现
 */
public class SpringWindApplicationContext {
    private static final Logger logger = LoggerFactory.getLogger(SpringWindApplicationContext.class);

    // Bean定义映射表（使用ConcurrentHashMap提升并发性能）
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    // 单例Bean映射表（一级缓存）
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>();
    // 早期单例Bean映射表（二级缓存，用于解决循环依赖）
    private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>();
    // 正在创建的Bean集合（用于检测循环依赖）
    private final Set<String> singletonsCurrentlyInCreation = Collections.newSetFromMap(new ConcurrentHashMap<>());
    // Bean后处理器列表
    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();
    // 构造器缓存（性能优化）
    private final Map<Class<?>, Constructor<?>> constructorCache = new ConcurrentHashMap<>();

    public SpringWindApplicationContext(Class<?> configClass) {
        // 扫描包路径下的组件
        scanComponents(configClass);
        // 创建单例Bean
        createSingletonBeans();
        // 依赖注入
        dependencyInjection();
        // 执行初始化方法
        invokeInitMethods();
    }

    /**
     * 扫描组件并注册Bean定义
     */
    private void scanComponents(Class<?> configClass) {
        // 参数校验
        if (configClass == null) {
            throw new IllegalArgumentException("配置类不能为null");
        }

        // 获取配置的包扫描路径
        String basePackage = configClass.getPackage().getName();
        logger.info("开始扫描包: {}", basePackage);

        try {
            // 扫描类路径
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            String resourcePath = basePackage.replace('.', '/');
            java.net.URL resource = classLoader.getResource(resourcePath);

            if (resource == null) {
                logger.warn("无法找到包资源: {}", basePackage);
                return;
            }

            URI uri = resource.toURI();
            Path path = Paths.get(uri);

            // 使用 try-with-resources 确保流被正确关闭
            try (java.util.stream.Stream<Path> walk = Files.walk(path)) {
                walk.filter(p -> p.toString().endsWith(".class"))
                        .forEach(p -> {
                            try {
                                // 生成类全限定名
                                Path relativePath = path.relativize(p);
                                String className = relativePath.toString()
                                        .replace(".class", "")
                                        .replace('/', '.');
                                className = basePackage + "." + className;

                                Class<?> clazz = Class.forName(className);

                                // 检查是否有@Component或其派生注解
                                if (clazz.isAnnotationPresent(Component.class) ||
                                        clazz.isAnnotationPresent(Controller.class) ||
                                        clazz.isAnnotationPresent(Service.class) ||
                                        clazz.isAnnotationPresent(Repository.class)) {

                                    registerBeanDefinition(clazz);
                                    logger.debug("注册Bean: {}", clazz.getSimpleName());
                                }

                                // 注册BeanPostProcessor
                                if (BeanPostProcessor.class.isAssignableFrom(clazz) &&
                                    !clazz.isInterface()) {
                                    try {
                                        BeanPostProcessor processor = (BeanPostProcessor) clazz.getDeclaredConstructor().newInstance();
                                        beanPostProcessors.add(processor);
                                        logger.debug("注册BeanPostProcessor: {}", clazz.getSimpleName());
                                    } catch (NoSuchMethodException e) {
                                        logger.warn("BeanPostProcessor {} 必须有无参构造函数", clazz.getName());
                                    }
                                }

                            } catch (ClassNotFoundException e) {
                                logger.warn("无法加载类: {}", e.getMessage());
                            } catch (Exception e) {
                                logger.error("处理类时出错: {}", e.getMessage(), e);
                            }
                        });
            }
            logger.info("包扫描完成，共注册 {} 个Bean", beanDefinitionMap.size());
        } catch (URISyntaxException e) {
            throw new BeanDefinitionException("无效的URI语法: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new BeanDefinitionException("扫描组件失败: " + e.getMessage(), e);
        }
    }

    /**
     * 注册Bean定义
     * @param clazz Bean类
     */
    private void registerBeanDefinition(Class<?> clazz) {
        String beanName = getBeanName(clazz);
        BeanDefinition beanDefinition = new BeanDefinition(beanName, clazz);

        // 设置初始化方法和销毁方法
        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(javax.annotation.PostConstruct.class)) {
                beanDefinition.setInitMethod(method);
            }
            if (method.isAnnotationPresent(javax.annotation.PreDestroy.class)) {
                beanDefinition.setDestroyMethod(method);
            }
        }

        beanDefinitionMap.put(beanName, beanDefinition);
    }

    /**
     * 获取Bean名称
     * @param clazz Bean类
     * @return Bean名称
     */
    private String getBeanName(Class<?> clazz) {
        String value = "";
        if (clazz.isAnnotationPresent(Component.class)) {
            value = clazz.getAnnotation(Component.class).value();
        } else if (clazz.isAnnotationPresent(Controller.class)) {
            value = clazz.getAnnotation(Controller.class).value();
        } else if (clazz.isAnnotationPresent(Service.class)) {
            value = clazz.getAnnotation(Service.class).value();
        } else if (clazz.isAnnotationPresent(Repository.class)) {
            value = clazz.getAnnotation(Repository.class).value();
        }

        if (value.isEmpty()) {
            String className = clazz.getSimpleName();
            return Character.toLowerCase(className.charAt(0)) + className.substring(1);
        }
        return value;
    }

    /**
     * 创建单例Bean实例（支持循环依赖检测）
     */
    private void createSingletonBeans() {
        for (BeanDefinition beanDefinition : beanDefinitionMap.values()) {
            if ("singleton".equals(beanDefinition.getScope())) {
                String beanName = beanDefinition.getBeanName();

                // 检测循环依赖
                if (singletonsCurrentlyInCreation.contains(beanName)) {
                    throw new CircularDependencyException(beanName, singletonsCurrentlyInCreation);
                }

                // 标记为正在创建
                singletonsCurrentlyInCreation.add(beanName);

                try {
                    Object bean = createBeanInstance(beanDefinition.getBeanClass());
                    // 将早期Bean放入二级缓存（用于解决循环依赖）
                    earlySingletonObjects.put(beanName, bean);

                    // 执行BeanPostProcessor前置处理
                    bean = applyBeanPostProcessorsBeforeInitialization(bean, beanName);
                    beanDefinition.setBeanInstance(bean);
                    singletonObjects.put(beanName, bean);

                    logger.debug("创建单例Bean: {}", beanName);
                } finally {
                    // 创建完成，移出正在创建集合
                    singletonsCurrentlyInCreation.remove(beanName);
                    // 移出早期缓存
                    earlySingletonObjects.remove(beanName);
                }
            }
        }
    }

    /**
     * 创建Bean实例（使用缓存的构造器优化性能）
     * @param clazz Bean类
     * @return Bean实例
     */
    private Object createBeanInstance(Class<?> clazz) {
        try {
            // 从缓存获取构造器，如果没有则创建并缓存
            Constructor<?> constructor = constructorCache.computeIfAbsent(clazz, c -> {
                try {
                    Constructor<?> ctor = c.getDeclaredConstructor();
                    ctor.setAccessible(true);
                    return ctor;
                } catch (NoSuchMethodException e) {
                    throw new BeanCreationException(c.getSimpleName(),
                        "没有找到无参构造函数", e);
                }
            });

            return constructor.newInstance();
        } catch (InstantiationException e) {
            throw new BeanCreationException(clazz.getSimpleName(),
                "无法实例化Bean（可能是抽象类或接口）", e);
        } catch (IllegalAccessException e) {
            throw new BeanCreationException(clazz.getSimpleName(),
                "无法访问构造函数", e);
        } catch (InvocationTargetException e) {
            throw new BeanCreationException(clazz.getSimpleName(),
                "构造函数执行失败: " + e.getTargetException().getMessage(), e);
        }
    }

    /**
     * 创建Bean实例
     * @param beanDefinition Bean定义
     * @return Bean实例
     */
    private Object createBeanInstance(BeanDefinition beanDefinition) {
        try {
            // 使用 getDeclaredConstructor().newInstance() 替代过时的 newInstance()
            return beanDefinition.getBeanClass().getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException |
                 NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException("创建Bean实例失败: " + beanDefinition.getBeanName(), e);
        }
    }

    /**
     * 依赖注入
     */
    private void dependencyInjection() {
        for (BeanDefinition beanDefinition : beanDefinitionMap.values()) {
            Object bean = getBean(beanDefinition.getBeanName());
            doDependencyInjection(bean);
        }
    }

    /**
     * 执行依赖注入（字段注入）
     * @param bean Bean实例
     */
    private void doDependencyInjection(Object bean) {
        if (bean == null) {
            return;
        }

        Class<?> clazz = bean.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                try {
                    field.setAccessible(true);
                    Object dependency = getBean(field.getType());
                    if (dependency != null) {
                        field.set(bean, dependency);
                        logger.debug("注入依赖: {} -> {}", clazz.getSimpleName(), field.getName());
                    } else {
                        Autowired autowired = field.getAnnotation(Autowired.class);
                        // 如果required=true且依赖为null，抛出异常
                        if (autowired.required()) {
                            throw new BeanNotFoundException(field.getType());
                        }
                        logger.warn("未找到依赖 {} 的Bean (required=false)", field.getType().getSimpleName());
                    }
                } catch (IllegalAccessException e) {
                    throw new BeanCreationException(clazz.getSimpleName(),
                        "无法注入字段: " + field.getName(), e);
                }
            }
        }
    }

    /**
     * 执行初始化方法（@PostConstruct标注的方法）
     */
    private void invokeInitMethods() {
        for (BeanDefinition beanDefinition : beanDefinitionMap.values()) {
            if (beanDefinition.getInitMethod() != null) {
                try {
                    Object bean = getBean(beanDefinition.getBeanName());
                    // 调用@PostConstruct标注的初始化方法
                    beanDefinition.getInitMethod().invoke(bean);
                    // 执行BeanPostProcessor后置处理（初始化后）
                    bean = applyBeanPostProcessorsAfterInitialization(bean, beanDefinition.getBeanName());
                    // 更新处理后的Bean实例到定义和单例缓存
                    beanDefinition.setBeanInstance(bean);
                    singletonObjects.put(beanDefinition.getBeanName(), bean);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    System.err.println("Error invoking init method for bean: " + beanDefinition.getBeanName());
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * BeanPostProcessor前置处理：在Bean初始化方法（@PostConstruct）执行前调用
     * @param bean Bean实例
     * @param beanName Bean名称
     * @return 处理后的Bean实例（可能被包装）
     */
    private Object applyBeanPostProcessorsBeforeInitialization(Object bean, String beanName) {
        Object result = bean;
        for (BeanPostProcessor processor : beanPostProcessors) {
            result = processor.postProcessBeforeInitialization(result, beanName);
            if (result == null) {
                return result;
            }
        }
        return result;
    }

    /**
     * BeanPostProcessor后置处理：在Bean初始化方法（@PostConstruct）执行后调用
     * @param bean Bean实例
     * @param beanName Bean名称
     * @return 处理后的Bean实例（可能被包装，如AOP代理）
     */
    private Object applyBeanPostProcessorsAfterInitialization(Object bean, String beanName) {
        Object result = bean;
        for (BeanPostProcessor processor : beanPostProcessors) {
            result = processor.postProcessAfterInitialization(result, beanName);
            if (result == null) {
                return result;
            }
        }
        return result;
    }

    /**
     * 获取所有带有指定注解的Bean实例
     * 参考Spring框架的ApplicationContext.getBeansWithAnnotation方法
     *
     * @param annotationType 注解类型
     * @return Bean名称与实例的映射表
     */
    public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) {
        Map<String, Object> result = new HashMap<>();

        // 遍历所有Bean定义
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();
            Class<?> beanClass = beanDefinition.getBeanClass();

            // 检查类上是否有指定注解
            if (beanClass.isAnnotationPresent(annotationType)) {
                Object beanInstance = getBean(beanName);
                if (beanInstance != null) {
                    result.put(beanName, beanInstance);
                }
            }
        }

        return result;
    }

    /**
     * 获取所有Bean名称（辅助方法）
     */
    public String[] getBeanDefinitionNames() {
        return beanDefinitionMap.keySet().toArray(new String[0]);
    }

    /**
     * 根据类型获取所有Bean实例（辅助方法）
     * @param type Bean类型
     * @return Bean名称与实例的映射表
     */
    public <T> Map<String, T> getBeansOfType(Class<T> type) {
        Map<String, T> result = new HashMap<>();

        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();

            if (type.isAssignableFrom(beanDefinition.getBeanClass())) {
                Object beanInstance = getBean(beanName);
                if (beanInstance != null) {
                    result.put(beanName, type.cast(beanInstance));
                }
            }
        }

        return result;
    }

    /**
     * 获取Bean实例（添加参数校验）
     * @param beanName Bean名称
     * @return Bean实例
     */
    public Object getBean(String beanName) {
        // 参数校验
        if (beanName == null || beanName.trim().isEmpty()) {
            throw new IllegalArgumentException("Bean名称不能为空");
        }

        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            throw new BeanNotFoundException(beanName);
        }

        if ("singleton".equals(beanDefinition.getScope())) {
            return singletonObjects.get(beanName);
        } else {
            // 原型模式，每次创建新实例
            Object bean = createBeanInstance(beanDefinition.getBeanClass());
            doDependencyInjection(bean);
            return bean;
        }
    }

    /**
     * 根据类型获取Bean（添加参数校验）
     * @param requiredType Bean类型
     * @return Bean实例
     */
    public <T> T getBean(Class<T> requiredType) {
        // 参数校验
        if (requiredType == null) {
            throw new IllegalArgumentException("Bean类型不能为null");
        }

        for (BeanDefinition beanDefinition : beanDefinitionMap.values()) {
            if (requiredType.isAssignableFrom(beanDefinition.getBeanClass())) {
                return (T) getBean(beanDefinition.getBeanName());
            }
        }

        // 未找到Bean，返回null（调用方需要处理）
        logger.debug("未找到类型为 {} 的Bean", requiredType.getName());
        return null;
    }

    /**
     * 关闭容器，执行销毁方法（@PreDestroy标注的方法）
     */
    public void close() {
        for (BeanDefinition beanDefinition : beanDefinitionMap.values()) {
            if (beanDefinition.getDestroyMethod() != null) {
                try {
                    Object bean = getBean(beanDefinition.getBeanName());
                    beanDefinition.getDestroyMethod().invoke(bean);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    System.err.println("Error invoking destroy method for bean: " + beanDefinition.getBeanName());
                    e.printStackTrace();
                }
            }
        }
        singletonObjects.clear();
        beanDefinitionMap.clear();
    }
}