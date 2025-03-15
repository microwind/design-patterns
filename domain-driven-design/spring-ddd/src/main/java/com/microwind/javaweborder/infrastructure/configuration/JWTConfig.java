package com.microwind.javaweborder.infrastructure.configuration;

public class JWTConfig {
    private String secret;
    private String expiresIn;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
    }
}
