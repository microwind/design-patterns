package com.microwind.knife.application.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.ArrayList;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "dedicated")
@PropertySource(value = "classpath:dedicated-ip-ranges.yml", factory = YamlPropertySourceFactory.class)
public class DedicatedIPConfig {
    private List<IPRange> ipRanges = new ArrayList<>();

    @Data
    public static class IPRange {
        private String cidr;
        private String alias;
        private String province;
        private String city;
    }
}