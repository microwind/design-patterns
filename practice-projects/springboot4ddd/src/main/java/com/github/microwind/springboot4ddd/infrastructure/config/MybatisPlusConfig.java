package com.github.microwind.springboot4ddd.infrastructure.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.github.microwind.springboot4ddd.infrastructure.repository.mybatisplus.OrderMybatisPlusMapper;
import com.github.microwind.springboot4ddd.infrastructure.repository.mybatisplus.OrderMybatisPlusRepositoryImpl;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.sql.DataSource;

/**
 * MyBatis Plus 配置类
 * 手动配置 MyBatis Plus 以兼容 Spring Boot 4.x
 *
 * @author jarry
 * @since 1.0.0
 */
@Configuration
@MapperScan(basePackages = "com.github.microwind.springboot4ddd.infrastructure.repository.mybatisplus")
public class MybatisPlusConfig {

    /**
     * 配置 SqlSessionFactory
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource, MybatisPlusInterceptor mybatisPlusInterceptor) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setPlugins(mybatisPlusInterceptor);

        // 配置 MyBatis 属性
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setLogImpl(org.apache.ibatis.logging.slf4j.Slf4jImpl.class);
        sqlSessionFactoryBean.setConfiguration(configuration);

        // 设置 mapper XML 文件位置
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath*:/mapper/**/*.xml"));

        return sqlSessionFactoryBean.getObject();
    }

    /**
     * 配置 SqlSessionTemplate
     */
    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    /**
     * 配置MyBatis Plus拦截器
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // MyBatis Plus 3.5.x 版本分页已内置支持，无需显式配置分页拦截器
        // 分页使用示例：Page<Order> page = new Page<>(1, 10);
        // orderRepository.selectPage(page, null);
        return interceptor;
    }

    /**
     * 显式注册 OrderMybatisPlusRepositoryImpl bean
     * 以解决 Spring 无法自动注册 @Mapper 接口依赖的问题
     */
    @Bean
    public OrderMybatisPlusRepositoryImpl orderMybatisPlusRepositoryImpl(OrderMybatisPlusMapper orderMybatisPlusMapper) {
        return new OrderMybatisPlusRepositoryImpl(orderMybatisPlusMapper);
    }
}
