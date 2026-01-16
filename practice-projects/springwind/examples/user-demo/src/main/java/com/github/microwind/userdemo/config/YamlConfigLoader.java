package com.github.microwind.userdemo.config;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

/**
 * YAML 配置文件加载器
 * 统一管理 application.yml 配置文件的读取和解析
 * 
 * 使用示例：
 * YamlConfigLoader loader = YamlConfigLoader.getInstance();
 * String jdbcUrl = loader.getString("user.datasource.jdbc-url", "默认值");
 * int maxPoolSize = loader.getInt("user.datasource.maximum-pool-size", 10);
 */
public class YamlConfigLoader {
    
    private static final String CONFIG_FILE = "application.yml";
    private static final YamlConfigLoader INSTANCE = new YamlConfigLoader();
    private final Map<String, Object> configMap;

    /**
     * 私有构造函数（单例模式）
     */
    private YamlConfigLoader() {
        this.configMap = loadConfig();
    }

    /**
     * 获取单例实例
     */
    public static YamlConfigLoader getInstance() {
        return INSTANCE;
    }

    /**
     * 从 application.yml 加载配置
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> loadConfig() {
        try {
            Yaml yaml = new Yaml();
            InputStream inputStream = YamlConfigLoader.class.getClassLoader()
                    .getResourceAsStream(CONFIG_FILE);
            
            if (inputStream == null) {
                System.err.println("找不到配置文件 " + CONFIG_FILE);
                System.err.println("   应用将使用硬编码的默认配置值");
                return Map.of();
            }
            
            Map<String, Object> config = yaml.load(inputStream);
            System.out.println("成功加载配置文件: " + CONFIG_FILE);
            return config != null ? config : Map.of();
            
        } catch (Exception e) {
            System.err.println("读取配置文件失败: " + e.getMessage());
            System.err.println("   应用将使用硬编码的默认配置值");
            return Map.of();
        }
    }

    /**
     * 从配置中获取字符串值
     * 
     * @param path 配置路径，例如 "user.datasource.jdbc-url"
     * @param defaultValue 如果配置不存在，返回的默认值
     * @return 配置值或默认值
     */
    public String getString(String path, String defaultValue) {
        Object value = getValueByPath(path);
        return value != null ? value.toString() : defaultValue;
    }

    /**
     * 从配置中获取整数值
     * 
     * @param path 配置路径
     * @param defaultValue 默认值
     * @return 配置值或默认值
     */
    public int getInt(String path, int defaultValue) {
        try {
            Object value = getValueByPath(path);
            if (value == null) {
                return defaultValue;
            }
            if (value instanceof Integer) {
                return (Integer) value;
            }
            return Integer.parseInt(value.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 从配置中获取长整数值
     * 
     * @param path 配置路径
     * @param defaultValue 默认值
     * @return 配置值或默认值
     */
    public long getLong(String path, long defaultValue) {
        try {
            Object value = getValueByPath(path);
            if (value == null) {
                return defaultValue;
            }
            if (value instanceof Long) {
                return (Long) value;
            }
            return Long.parseLong(value.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 从配置中获取布尔值
     * 
     * @param path 配置路径
     * @param defaultValue 默认值
     * @return 配置值或默认值
     */
    public boolean getBoolean(String path, boolean defaultValue) {
        try {
            Object value = getValueByPath(path);
            if (value == null) {
                return defaultValue;
            }
            if (value instanceof Boolean) {
                return (Boolean) value;
            }
            String strValue = value.toString().toLowerCase();
            return "true".equals(strValue) || "yes".equals(strValue) || "1".equals(strValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 获取嵌套的配置对象
     * 
     * @param path 配置路径，例如 "user.datasource"
     * @return 配置对象或null
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getMap(String path) {
        Object value = getValueByPath(path);
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }
        return null;
    }

    /**
     * 根据路径获取配置值
     * 路径格式：user.datasource.jdbc-url（用 . 分隔）
     * 
     * @param path 配置路径
     * @return 配置值或null
     */
    @SuppressWarnings("unchecked")
    private Object getValueByPath(String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }
        
        try {
            String[] keys = path.split("\\.");
            Object current = configMap;
            
            for (String key : keys) {
                if (current instanceof Map) {
                    current = ((Map<String, Object>) current).get(key);
                    if (current == null) {
                        return null;
                    }
                } else {
                    return null;
                }
            }
            
            return current;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 检查配置是否存在
     * 
     * @param path 配置路径
     * @return 如果配置存在返回true，否则返回false
     */
    public boolean containsKey(String path) {
        return getValueByPath(path) != null;
    }

    /**
     * 打印所有配置信息（用于调试）
     */
    public void printConfig() {
        System.out.println("\n========== 应用配置信息 ==========");
        printConfigMap("", configMap);
        System.out.println("==================================\n");
    }

    /**
     * 递归打印配置映射
     */
    @SuppressWarnings("unchecked")
    private void printConfigMap(String prefix, Object obj) {
        if (obj instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) obj;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
                Object value = entry.getValue();
                
                if (value instanceof Map) {
                    printConfigMap(key, value);
                } else {
                    System.out.println(key + " = " + value);
                }
            }
        }
    }

    /**
     * 获取原始配置映射（高级用法）
     */
    public Map<String, Object> getRawConfig() {
        return configMap;
    }
}
