package com.microwind.knife.interfaces.controllers;

import com.microwind.knife.application.services.ip.IPLocationService;
import com.microwind.knife.common.ApiResponse;
import com.microwind.knife.domain.ip.IPRegion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ip")
public class IPController {

    @Autowired
    private IPLocationService ipLocationService;

    /**
     * 查询 IP 地理位置
     * 示例：GET /api/ip/location?ip=172.1.1.1
     */
    @GetMapping("/location")
    public ApiResponse<IPRegion> getLocation(@RequestParam String ip) {
        return ipLocationService.getIPLocation(ip);
    }
}
