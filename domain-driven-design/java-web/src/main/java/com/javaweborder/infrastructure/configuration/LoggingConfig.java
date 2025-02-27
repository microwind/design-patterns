package com.javaweborder.infrastructure.configuration;

public class LoggingConfig {
    private String level;
    private String file;

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
