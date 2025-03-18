package com.microwind.springbootorder.infrastructure.configuration;
// 基础设置配置文件示例
// 采用Springboot大量配置都在properties或者yaml文件，此文件可选
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfig implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("Database URL: " + url);
        System.out.println("Database Username: " +  username);
        System.out.println("Database Password: " + password);
    }
}