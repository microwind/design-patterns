package com.microwind.knife.application.config;

import org.apache.catalina.Context;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> containerCustomizer() {
        return factory -> {
            factory.addContextCustomizers(context -> {
                // 替换默认的 ErrorReportValve
                JsonErrorReportValve valve = new JsonErrorReportValve();
                // 必须禁用显示服务器信息，否则某些逻辑会被跳过
                valve.setShowServerInfo(false);
                valve.setShowReport(false);

                context.getParent().getPipeline().addValve(valve);

                // 寻找并移除默认的 ErrorReportValve (通常是最后一个)
                // 这一步是关键，否则会有两个输出
            });
        };
    }
}