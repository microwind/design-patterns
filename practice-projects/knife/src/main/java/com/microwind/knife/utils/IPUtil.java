package com.microwind.knife.utils;

import org.apache.commons.net.util.SubnetUtils;
import org.springframework.stereotype.Component;

@Component
public class IPUtil {

    // 检查是否内网IP
    public boolean isPrivateIP(String ip) {
        if (ip.startsWith("10.")) return true;
        if (ip.startsWith("172.")) {
            String[] parts = ip.split("\\.");
            if (parts.length >= 2) {
                int second = Integer.parseInt(parts[1]);
                return second >= 16 && second <= 31;
            }
        }
        if (ip.startsWith("192.168.")) return true;
        return false;
    }

    // 根据IP地址是否在某个IP段内
    public boolean isInRange(String ip, String cidr) {
        try {
            SubnetUtils utils = new SubnetUtils(cidr);
            utils.setInclusiveHostCount(true); // 包含网络地址和广播地址
            return utils.getInfo().isInRange(ip);
        } catch (Exception e) {
            System.err.println("CIDR格式错误: " + cidr + ", 错误: " + e.getMessage());
            return false;
        }
    }
}