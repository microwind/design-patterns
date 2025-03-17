package com.microwind.springbootorder.config;

import com.microwind.springbootorder.domain.order.CustomOrderRepository;
import com.microwind.springbootorder.domain.order.CustomOrderRepositoryImpl;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderConfig {

    // 因为在不同的包，如没有指定全局配置，springboot默认不会自动扫描Impl为对应的实现类，需要手工指定
    // 在 @Configuration 中声明 CustomOrderRepositoryImpl是CustomOrderRepository的默认实现类
    @Bean
    public CustomOrderRepository customOrderRepository(EntityManager entityManager) {
        return new CustomOrderRepositoryImpl(entityManager);
    }
}