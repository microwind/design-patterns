package com.github.microwind.springwind.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.github.microwind.springwind.annotation.Autowired;
import com.github.microwind.springwind.annotation.Bean;
import com.github.microwind.springwind.annotation.Component;
import com.github.microwind.springwind.annotation.Configuration;
import com.github.microwind.springwind.annotation.Controller;
import com.github.microwind.springwind.annotation.Repository;
import com.github.microwind.springwind.annotation.Service;
import com.github.microwind.springwind.annotation.Aspect;
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
    // 三级缓存：ObjectFactory，用于延迟生成早期引用（支持AOP）
    private final Map<String, ObjectFactory<?>> singletonFactories = new ConcurrentHashMap<>();
    // 正在创建的Bean集合（用于检测循环依赖）
    private final Set<String> singletonsCurrentlyInCreation = Collections.newSetFromMap(new ConcurrentHashMap<>());
    // Bean后处理器列表
    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();
    // 构造器缓存（性能优化）
    private final Map<Class<?>, Constructor<?>> constructorCache = new ConcurrentHashMap<>();
    // @Configuration 类实例（用于 @Bean 方法调用）
    private final Map<Class<?>, Object> configurationInstances = new ConcurrentHashMap<>();
    // @Bean 方法定义（类 -> 方法列表）
    private final Map<Class<?>, List<Method>> beanMethods = new ConcurrentHashMap<>();

    // 构造函数只保存配置类
    private final Class<?> configClass;
    
    /**
     * 兼容性构造函数：自动调用refresh()
     * @param configClass 配置类
     */
    public SpringWindApplicationContext(Class<?> configClass) {
        this.configClass = configClass;
        refresh();
    }
    
    /**
     * 新的构造函数：只保存配置类，需要手动调用refresh()
     * @param configClass 配置类
     * @param autoRefresh 是否自动刷新
     */
    public SpringWindApplicationContext(Class<?> configClass, boolean autoRefresh) {
        this.configClass = configClass;
        if (autoRefresh) {
            refresh();
        }
    }

    // Spring风格刷新方法
    public void refresh() {
        scanComponents(configClass);      // 扫描组件，注册BeanDefinition
        createSingletonBeans();           // 创建单例Bean
        dependencyInjection();            // 注入依赖
        invokeInitMethods();              // 执行@PostConstruct
    }

    /**
     * 扫描组件并注册Bean定义
     */
    private void scanComponents(Class<?> configClass) {
        if (configClass == null) {
            throw new IllegalArgumentException("配置类不能为null");
        }

        String basePackage = configClass.getPackage().getName();
        logger.info("开始扫描包: {}", basePackage);

        try {
            String resourcePath = basePackage.replace('.', '/');

            // 尝试多个类加载器，确保在 Web 环境下也能正常工作
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            java.net.URL resource = classLoader != null ? classLoader.getResource(resourcePath) : null;

            // 如果线程上下文类加载器找不到，尝试使用配置类的类加载器
            if (resource == null) {
                classLoader = configClass.getClassLoader();
                resource = classLoader.getResource(resourcePath);
            }

            // 如果还是找不到，尝试使用当前类的类加载器
            if (resource == null) {
                classLoader = SpringWindApplicationContext.class.getClassLoader();
                resource = classLoader.getResource(resourcePath);
            }

            if (resource == null) {
                logger.warn("无法找到包资源: {} (已尝试多个类加载器)", basePackage);
                return;
            }

            logger.debug("使用类加载器: {}, 找到资源: {}", classLoader.getClass().getName(), resource);

            String protocol = resource.getProtocol();

            // 1. 运行在普通文件系统中（IDE 运行或 classes 目录）
            if ("file".equals(protocol)) {
                Path path = Paths.get(resource.toURI());
                Files.walk(path)
                        .filter(p -> p.toString().endsWith(".class"))
                        .forEach(p -> processClassFile(basePackage, path, p));
            }
            // 2. 运行在 jar 包中
            else if ("jar".equals(protocol)) {
                String path = resource.getPath();
                String jarPath = path.substring(path.indexOf("file:"), path.indexOf("!"));
                try (JarFile jarFile = new JarFile(new File(new URI(jarPath)))) {
                    Enumeration<JarEntry> entries = jarFile.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        if (entry.getName().startsWith(resourcePath) && entry.getName().endsWith(".class")) {
                            String className = entry.getName()
                                    .replace("/", ".")
                                    .replace(".class", "");
                            processClassName(className);
                        }
                    }
                }
            }
            // 3. 其他协议（不常见）
            else {
                logger.warn("不支持的资源协议: {}", protocol);
            }

            logger.info("包扫描完成，共注册 {} 个Bean", beanDefinitionMap.size());
        } catch (Exception e) {
            throw new BeanDefinitionException("扫描组件失败: " + e.getMessage(), e);
        }
    }

    /**
     * 处理文件系统下的类文件
     */
    private void processClassFile(String basePackage, Path rootPath, Path classFilePath) {
        try {
            Path relativePath = rootPath.relativize(classFilePath);
            String className = relativePath.toString()
                    .replace(".class", "")
                    .replace('/', '.')
                    .replace('\\', '.');
            className = basePackage + "." + className;
            processClassName(className);
        } catch (Exception e) {
            logger.error("处理类文件出错: {}", classFilePath, e);
        }
    }

    /**
     * 根据类名加载并注册Bean
     */
    private void processClassName(String className) {
        try {
            Class<?> clazz = Class.forName(className);

            if (clazz.isAnnotationPresent(Component.class) ||
                    clazz.isAnnotationPresent(Controller.class) ||
                    clazz.isAnnotationPresent(Service.class) ||
                    clazz.isAnnotationPresent(Repository.class) ||
                    clazz.isAnnotationPresent(Aspect.class)) {
                registerBeanDefinition(clazz);
                logger.debug("注册Bean: {}", clazz.getSimpleName());
            }

            // @Configuration 类处理
            if (clazz.isAnnotationPresent(Configuration.class)) {
                registerConfigurationClass(clazz);
                logger.debug("注册Configuration类: {}", clazz.getSimpleName());
            }

            // BeanPostProcessor 注册
            if (BeanPostProcessor.class.isAssignableFrom(clazz) && !clazz.isInterface()) {
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
        } catch (Throwable e) {
            logger.error("处理类时出错: {}", className, e);
        }
    }

    /**
     * 注册Bean定义
     * 
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
     * 注册@Configuration类及其@Bean方法
     * 
     * @param configClass Configuration类
     */
    private void registerConfigurationClass(Class<?> configClass) {
        try {
            // 创建配置类实例
            Object configInstance = configClass.getDeclaredConstructor().newInstance();
            configurationInstances.put(configClass, configInstance);

            // 扫描@Bean方法
            List<Method> beanMethodList = new ArrayList<>();
            for (Method method : configClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Bean.class)) {
                    method.setAccessible(true);
                    beanMethodList.add(method);
                    
                    // 注册@Bean方法返回的Bean定义
                    registerBeanMethodDefinition(method, configClass);
                }
            }
            beanMethods.put(configClass, beanMethodList);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            logger.error("无法创建Configuration实例: {}", configClass.getName(), e);
            throw new BeanCreationException(configClass.getSimpleName(), 
                    "创建Configuration实例失败: " + e.getMessage(), e);
        }
    }

    /**
     * 注册@Bean方法定义
     * 
     * @param method 被@Bean标注的方法
     * @param configClass Configuration类
     */
    private void registerBeanMethodDefinition(Method method, Class<?> configClass) {
        Bean beanAnnotation = method.getAnnotation(Bean.class);
        
        // 获取bean名称，优先使用@Bean注解中的value，否则使用方法名
        String beanName = beanAnnotation.value();
        if (beanName == null || beanName.isEmpty()) {
            beanName = method.getName();
        }
        
        // 创建bean定义，记录方法返回类型和所在的配置类
        BeanMethodDefinition beanMethodDef = new BeanMethodDefinition(beanName, method.getReturnType());
        beanMethodDef.setMethod(method);
        beanMethodDef.setConfigClass(configClass);
        
        beanDefinitionMap.put(beanName, beanMethodDef);
        logger.debug("注册@Bean方法: {}.{}()", configClass.getSimpleName(), method.getName());
    }

    /**
     * 获取Bean名称
     * 
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
        } else if (clazz.isAnnotationPresent(Aspect.class)) {
            value = clazz.getAnnotation(Aspect.class).value();
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
                // 通过getBean方法创建，它会自动处理依赖注入
                getBean(beanDefinition.getBeanName());
            }
        }
    }

    /**
     * 创建Bean实例（使用缓存的构造器优化性能）
     * 
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
     * 调用@Bean方法创建Bean实例，支持构造函数参数注入
     * 
     * @param methodDef @Bean方法定义
     * @return Bean实例
     */
    private Object invokeBeanMethod(BeanMethodDefinition methodDef) {
        try {
            Method method = methodDef.getMethod();
            Object configInstance = configurationInstances.get(methodDef.getConfigClass());
            
            if (configInstance == null) {
                throw new BeanCreationException(methodDef.getBeanName(),
                        "配置类实例不存在: " + methodDef.getConfigClass().getName());
            }
            
            // 获取方法的参数类型
            Class<?>[] parameterTypes = method.getParameterTypes();
            
            if (parameterTypes.length == 0) {
                // 无参数方法，直接调用
                return method.invoke(configInstance);
            } else {
                // 有参数方法，需要依赖注入
                Object[] parameters = new Object[parameterTypes.length];
                for (int i = 0; i < parameterTypes.length; i++) {
                    Class<?> paramType = parameterTypes[i];
                    Object paramBean = getBean(paramType);
                    if (paramBean == null) {
                        throw new BeanNotFoundException(paramType);
                    }
                    parameters[i] = paramBean;
                }
                return method.invoke(configInstance, parameters);
            }
        } catch (InvocationTargetException e) {
            throw new BeanCreationException(methodDef.getBeanName(),
                    "@Bean方法执行失败: " + e.getTargetException().getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new BeanCreationException(methodDef.getBeanName(),
                    "无法访问@Bean方法: " + e.getMessage(), e);
        }
    }

    /**
     * 依赖注入（在创建所有Bean后执行）
     * 此方法只对普通的@Component/@Service/@Repository/@Controller类执行field注入
     * @Bean方法产生的Bean已在invokeBeanMethod中完成了构造参数注入
     */
    private void dependencyInjection() {
        for (BeanDefinition beanDefinition : beanDefinitionMap.values()) {
            // 只对非@Bean方法产生的Bean执行字段注入
            if (!(beanDefinition instanceof BeanMethodDefinition)) {
                Object bean = getBean(beanDefinition.getBeanName());
                doDependencyInjection(bean);
            }
        }
    }

    /**
     * 执行依赖注入（字段注入）
     * 
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
     * 注：Bean初始化已经在getBean()方法中执行过了，此方法为备用
     */
    private void invokeInitMethods() {
        // 由于getBean()已经在创建Bean时执行了初始化和BeanPostProcessor处理
        // 此方法现已不需要额外处理，保留以兼容原有框架设计
        // 如果后续需要延迟初始化，可以在此实现
    }

    /**
     * BeanPostProcessor前置处理：在Bean初始化方法（@PostConstruct）执行前调用
     * 
     * @param bean     Bean实例
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
     * 
     * @param bean     Bean实例
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
     * 
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
     * 
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

        // 原型scope，每次都新建
        if (!"singleton".equals(beanDefinition.getScope())) {
            Object bean;
            if (beanDefinition instanceof BeanMethodDefinition) {
                BeanMethodDefinition methodDef = (BeanMethodDefinition) beanDefinition;
                bean = invokeBeanMethod(methodDef);
            } else {
                bean = createBeanInstance(beanDefinition.getBeanClass());
                doDependencyInjection(bean);
            }
            return bean;
        }

        // 1. 首先从一级缓存获取完全初始化好的Bean
        Object bean = singletonObjects.get(beanName);
        if (bean != null) {
            return bean;
        }

        // 2. 检查循环依赖：如果当前Bean正在创建中
        if (singletonsCurrentlyInCreation.contains(beanName)) {
            // 先从二级缓存获取早期对象
            bean = earlySingletonObjects.get(beanName);
            if (bean != null) {
                return bean;
            }

            // 如果二级缓存没有，尝试从三级缓存获取ObjectFactory
            ObjectFactory<?> factory = singletonFactories.get(beanName);
            if (factory != null) {
                // 调用ObjectFactory获取早期对象，并放入二级缓存
                bean = factory.getObject();
                earlySingletonObjects.put(beanName, bean);
                singletonFactories.remove(beanName);
                return bean;
            }

            // 如果三级缓存也没有，说明循环依赖无法解决
            throw new CircularDependencyException(beanName, new HashSet<>(singletonsCurrentlyInCreation));
        }

        // 标记为正在创建
        singletonsCurrentlyInCreation.add(beanName);

        try {
            // 1. 实例化
            if (beanDefinition instanceof BeanMethodDefinition) {
                BeanMethodDefinition methodDef = (BeanMethodDefinition) beanDefinition;
                bean = invokeBeanMethod(methodDef);
            } else {
                bean = createBeanInstance(beanDefinition.getBeanClass());
            }

            // 2. 放入三级缓存（关键：放入一个ObjectFactory，支持AOP提前创建代理）
            final Object rawBean = bean;
            singletonFactories.put(beanName, new ObjectFactory<Object>() {
                @Override
                public Object getObject() {
                    // 获取早期引用，支持AOP提前代理
                    return getEarlyBeanReference(beanName, rawBean);
                }
            });

            // 3. 依赖注入（字段注入）← 这里可能发生循环依赖
            if (!(beanDefinition instanceof BeanMethodDefinition)) {
                doDependencyInjection(bean);
            }

            // 4. 前置初始化处理器
            bean = applyBeanPostProcessorsBeforeInitialization(bean, beanName);

            // 5. 执行 @PostConstruct
            if (beanDefinition.getInitMethod() != null) {
                try {
                    beanDefinition.getInitMethod().invoke(bean);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new BeanCreationException(beanName, "执行初始化方法失败", e);
                }
            }

            // 6. 后置处理器（AOP通常在这里发生）
            bean = applyBeanPostProcessorsAfterInitialization(bean, beanName);

            // 7. 放入一级缓存（完全初始化好的Bean）
            singletonObjects.put(beanName, bean);

            // 8. 清理二级缓存和三级缓存
            earlySingletonObjects.remove(beanName);
            singletonFactories.remove(beanName);

            logger.debug("创建单例Bean: {}", beanName);
            return bean;
        } finally {
            // 无论成功与否，都从正在创建集合中移除
            singletonsCurrentlyInCreation.remove(beanName);
            // 如果创建失败，清理可能残留的缓存
            if (!singletonObjects.containsKey(beanName)) {
                singletonFactories.remove(beanName);
                earlySingletonObjects.remove(beanName);
            }
        }
    }

    /**
     * 获取早期Bean引用（支持SmartInstantiationAwareBeanPostProcessor提前创建代理）
     * @param beanName Bean名称
     * @param bean 原始Bean实例
     * @return 早期引用（可能是代理对象）
     */
    protected Object getEarlyBeanReference(String beanName, Object bean) {
        Object exposedObject = bean;
        
        // 遍历所有BeanPostProcessor
        for (BeanPostProcessor bp : beanPostProcessors) {
            if (bp instanceof SmartInstantiationAwareBeanPostProcessor) {
                SmartInstantiationAwareBeanPostProcessor ibp = (SmartInstantiationAwareBeanPostProcessor) bp;
                // 获取早期引用，SmartInstantiationAwareBeanPostProcessor可以在这里创建代理
                exposedObject = ibp.getEarlyBeanReference(exposedObject, beanName);
                if (exposedObject == null) {
                    return null;
                }
            }
        }
        return exposedObject;
    }

    /**
     * 根据类型获取Bean（添加参数校验）
     * 
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
                Object bean = getBean(beanDefinition.getBeanName());
                return requiredType.cast(bean);
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
                    logger.error("执行销毁方法失败: {}", beanDefinition.getBeanName(), e);
                }
            }
        }
        singletonObjects.clear();
        earlySingletonObjects.clear();
        singletonFactories.clear();
        beanDefinitionMap.clear();
        logger.info("容器已关闭");
    }
}