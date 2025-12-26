package com.microwind.knife.application.services.ip;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microwind.knife.application.config.DedicatedIPConfig;
import com.microwind.knife.common.ApiResponse;
import com.microwind.knife.domain.ip.IPRegion;
import com.microwind.knife.utils.IPUtil;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class IPLocationService {

    @Autowired
    private DedicatedIPConfig dedicatedIPConfig;

    @Autowired
    private IPUtil ipUtil;

    @Value("${public.ip.service.url:https://api.vore.top/api/IPdata}")
    private String publicIPServiceUrl;

    private final CloseableHttpClient httpClient = HttpClients.createDefault();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PreDestroy
    public void close() {
        try {
            httpClient.close();
        } catch (IOException ignored) {
        }
    }

    public ApiResponse<IPRegion> getIPLocation(String ip) {
        try {
            // 优先查专网
            Optional<IPRegion> region = checkDedicatedIP(ip);
            if (region.isPresent()) {
                log.info("IP {} 所属专网: {}", ip, region.get());
                return ApiResponse.success(region.get(), "专网IP查询成功");
            }

            // 专网IP查不到
            if (ipUtil.isPrivateIP(ip)) {
                return ApiResponse.success(new IPRegion(), "专网配置中查不到：" + ip);
            }

            // 否则走公网
            return queryPublicIP(ip);

        } catch (Exception e) {
            log.error("IP 查询异常: {}", e.getMessage(), e);
            return ApiResponse.failure(500, "服务器内部错误：" + e.getMessage());
        }
    }

    /**
     * 判断 Dedicated 专网 IP
     */
    private Optional<IPRegion> checkDedicatedIP(String ip) {
        if (dedicatedIPConfig.getIpRanges() == null) {
            return Optional.empty();
        }

        for (DedicatedIPConfig.IPRange range : dedicatedIPConfig.getIpRanges()) {
            try {
                if (ipUtil.isInRange(ip, range.getCidr())) {
                    IPRegion region = new IPRegion();
                    region.setProvince(range.getProvince());
                    region.setCity(range.getCity());
                    region.setCountry("中国");
                    region.setIp(ip);
                    return Optional.of(region);
                }
            } catch (Exception e) {
                log.warn("CIDR 解析失败: {} -> {}", range.getCidr(), e.getMessage());
            }
        }
        return Optional.empty();
    }

    /**
     * 公网 IP 查询
     */
    private ApiResponse<IPRegion> queryPublicIP(String ip) {

        String url = publicIPServiceUrl + "?ip=" + ip;
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("Accept", "application/json");

        try (var response = httpClient.execute(httpGet)) {

            int code = response.getCode();
            if (code != 200) {
                return ApiResponse.failure(code, "公网接口错误: " + code);
            }

            String body = EntityUtils.toString(response.getEntity());
            ApiResponse<?> result = objectMapper.readValue(body, ApiResponse.class);
            log.error("公网查询结果: {}", result.getData());
            // 关键修复：反序列化 data 为 Map
            Map<String, Object> data = objectMapper.convertValue(result.getData(), Map.class);

            IPRegion region = new IPRegion();
            region.setProvince((String) data.getOrDefault("province", ""));
            region.setCity((String) data.getOrDefault("city", ""));
            region.setDistrict((String) data.getOrDefault("district", ""));
            region.setCountry((String) data.getOrDefault("country", "中国"));
            region.setIp(ip);

            return ApiResponse.success(region, "公网IP查询成功");

        } catch (Exception e) {
            log.error("公网查询失败: {}", e.getMessage());
            return ApiResponse.failure(500, "公网API异常：" + e.getMessage());
        }
    }
}
