package com.microwind.knife.interfaces.controllers;

import com.microwind.knife.application.services.ip.IPLocationService;
import com.microwind.knife.common.ApiResponse;
import com.microwind.knife.domain.ip.IPRegion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/ip")
public class IPController {

    @Autowired
    private IPLocationService ipLocationService;

    /**
     * 查询 IP 地理位置
     * 示例：GET /v1/ip/location?ip=1.2.3.4
     */
    @GetMapping("/location")
    public ApiResponse<IPRegion> getLocation(@RequestParam String ip) {
        return ipLocationService.getIPLocation(ip);
    }
}
