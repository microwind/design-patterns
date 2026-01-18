package com.github.microwind.userdemo.model;

/**
 * 用户实体类
 */
public class User {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String wechat;
    private String address;
    private Long createdTime;
    private Long updatedTime;

    public User() {
    }

    public User(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.createdTime = System.currentTimeMillis();
        this.updatedTime = System.currentTimeMillis();
    }

    public User(String name, String email, String phone, String wechat, String address) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.wechat = wechat;
        this.address = address;
        this.createdTime = System.currentTimeMillis();
        this.updatedTime = System.currentTimeMillis();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWechat() {
        return wechat;
    }

    public String getAddress() {
        return address;
    }

    public Long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

    public Long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Long updatedTime) {
        this.updatedTime = updatedTime;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", wechat ='" + wechat + '\'' +
                ", address ='" + address + '\'' +
                ", createdTime=" + createdTime +
                ", updatedTime=" + updatedTime +
                '}';
    }
}
