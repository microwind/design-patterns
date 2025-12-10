package com.microwind.knife.domain.ip;

import lombok.Data;

@Data
public class IPRegion {
    private String province;
    private String city;
    private String district;
    private String country;
    private String ip;

    public IPRegion(String province, String city, String district, String country, String ip) {
        this.province = province;
        this.city = city;
        this.district = district;
        this.country = country;
        this.ip = ip;
    }

    public IPRegion() {
        // 默认构造函数
    }
}