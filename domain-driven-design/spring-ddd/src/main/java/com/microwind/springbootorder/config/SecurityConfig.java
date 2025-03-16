package com.microwind.springbootorder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 关键修复点1：使用新式 lambda DSL 配置
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF（REST API 场景推荐）
                .csrf(AbstractHttpConfigurer::disable)
                // 关键修复点2：替换为 authorizeHttpRequests
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").authenticated()
                        .anyRequest().permitAll()
                )
                // 关键修复点3：正确导入 withDefaults
                .httpBasic(withDefaults());

        return http.build();
    }

    // 关键修复点4：显式声明密码编码器
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.builder()
                .username("admin")
                // 使用编码器兼容处理（即使使用 {noop} 也需要编码器存在）
                .password(passwordEncoder.encode("123456"))
                .roles("USER", "ADMIN")
                .build());
        return manager;
    }
}