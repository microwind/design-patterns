package com.github.microwind.springwind.util;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 类扫描工具类
 * 用于扫描指定包路径下的所有类
 */
public class ClassScanner {
    
    /**
     * 扫描指定包下的所有类
     * @param basePackage 基础包路径
     * @return 扫描到的所有类的列表
     */
    public static List<Class<?>> scanClasses(String basePackage) {
        List<Class<?>> classes = new ArrayList<>();
        String path = basePackage.replace('.', '/');
        
        try {
            // 获取类加载器
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            URL resource = classLoader.getResource(path);
            if (resource != null) {
                // 递归扫描目录
                File directory = new File(resource.getFile());
                if (directory.exists()) {
                    scanDirectory(directory, basePackage, classes);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("扫描类路径失败: " + basePackage, e);
        }
        
        return classes;
    }
    
    /**
     * 递归扫描目录
     * @param directory 目录文件
     * @param packageName 包名
     * @param classes 类列表
     */
    private static void scanDirectory(File directory, String packageName, List<Class<?>> classes) {
        File[] files = directory.listFiles();
        if (files == null) return;
        
        // 遍历目录下的所有文件
        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + file.getName(), classes);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().replace(".class", "");
                try {
                    classes.add(Class.forName(className));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}