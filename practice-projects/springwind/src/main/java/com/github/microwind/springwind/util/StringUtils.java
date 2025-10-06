package com.github.microwind.springwind.util;

/**
 * 字符串工具类
 * 提供常用的字符串操作功能
 */
public class StringUtils {
    
    /**
     * 判断字符串是否为空
     * @param str 待检查的字符串
     * @return 如果字符串为null或空字符串返回true，否则返回false
     */
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }
    
    /**
     * 判断字符串是否不为空
     * @param str 待检查的字符串
     * @return 如果字符串不为null且不为空字符串返回true，否则返回false
     */
    public static boolean isNotEmpty(CharSequence str) {
        return !isEmpty(str);
    }
    
    /**
     * 判断字符串是否为空白字符串
     * @param str 待检查的字符串
     * @return 如果字符串为null或只包含空白字符返回true，否则返回false
     */
    public static boolean isBlank(CharSequence str) {
        if (isEmpty(str)) {
            return true;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 判断字符串是否不为空白字符串
     * @param str 待检查的字符串
     * @return 如果字符串不为null且至少包含一个非空白字符返回true，否则返回false
     */
    public static boolean isNotBlank(CharSequence str) {
        return !isBlank(str);
    }
    
    /**
     * 去除字符串两端的空白字符
     * @param str 待处理的字符串
     * @return 去除空白后的字符串，如果输入为null则返回null
     */
    public static String trim(String str) {
        return str == null ? null : str.trim();
    }
    
    /**
     * 去除字符串两端空白字符，如果结果为空白字符串则返回null
     * @param str 待处理的字符串
     * @return 处理后的字符串
     */
    public static String trimToNull(String str) {
        String result = trim(str);
        return isBlank(result) ? null : result;
    }
    
    /**
     * 去除字符串两端空白字符，如果结果为空白字符串则返回空字符串
     * @param str 待处理的字符串
     * @return 处理后的字符串
     */
    public static String trimToEmpty(String str) {
        return str == null ? "" : str.trim();
    }
    
    /**
     * 将字符串首字母大写
     * @param str 待处理的字符串
     * @return 首字母大写的字符串
     */
    public static String capitalize(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
    
    /**
     * 将字符串首字母小写
     * @param str 待处理的字符串
     * @return 首字母小写的字符串
     */
    public static String uncapitalize(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }
    
    /**
     * 将下划线命名转换为驼峰命名
     * @param str 下划线命名的字符串
     * @return 驼峰命名的字符串
     */
    public static String underscoreToCamelCase(String str) {
        if (isEmpty(str)) {
            return str;
        }
        
        StringBuilder result = new StringBuilder();
        boolean nextUpperCase = false;
        
        for (int i = 0; i < str.length(); i++) {
            char currentChar = str.charAt(i);
            
            if (currentChar == '_') {
                nextUpperCase = true;
            } else {
                if (nextUpperCase) {
                    result.append(Character.toUpperCase(currentChar));
                    nextUpperCase = false;
                } else {
                    result.append(Character.toLowerCase(currentChar));
                }
            }
        }
        
        return result.toString();
    }
    
    /**
     * 将驼峰命名转换为下划线命名
     * @param str 驼峰命名的字符串
     * @return 下划线命名的字符串
     */
    public static String camelCaseToUnderscore(String str) {
        if (isEmpty(str)) {
            return str;
        }
        
        StringBuilder result = new StringBuilder();
        result.append(Character.toLowerCase(str.charAt(0)));
        
        for (int i = 1; i < str.length(); i++) {
            char currentChar = str.charAt(i);
            if (Character.isUpperCase(currentChar)) {
                result.append('_').append(Character.toLowerCase(currentChar));
            } else {
                result.append(currentChar);
            }
        }
        
        return result.toString();
    }
    
    /**
     * 检查字符串是否以指定前缀开头（忽略大小写）
     * @param str 待检查的字符串
     * @param prefix 前缀
     * @return 如果以指定前缀开头返回true
     */
    public static boolean startsWithIgnoreCase(String str, String prefix) {
        if (str == null || prefix == null) {
            return false;
        }
        if (str.startsWith(prefix)) {
            return true;
        }
        if (str.length() < prefix.length()) {
            return false;
        }
        return str.substring(0, prefix.length()).equalsIgnoreCase(prefix);
    }
    
    /**
     * 检查字符串是否以指定后缀结尾（忽略大小写）
     * @param str 待检查的字符串
     * @param suffix 后缀
     * @return 如果以指定后缀结尾返回true
     */
    public static boolean endsWithIgnoreCase(String str, String suffix) {
        if (str == null || suffix == null) {
            return false;
        }
        if (str.endsWith(suffix)) {
            return true;
        }
        if (str.length() < suffix.length()) {
            return false;
        }
        return str.substring(str.length() - suffix.length()).equalsIgnoreCase(suffix);
    }
    
    /**
     * 检查字符串是否包含指定子串（忽略大小写）
     * @param str 待检查的字符串
     * @param searchStr 要查找的子串
     * @return 如果包含指定子串返回true
     */
    public static boolean containsIgnoreCase(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }
        return str.toLowerCase().contains(searchStr.toLowerCase());
    }
    
    /**
     * 将数组元素用指定分隔符连接成字符串
     * @param array 数组
     * @param separator 分隔符
     * @return 连接后的字符串
     */
    public static String join(Object[] array, String separator) {
        if (array == null) {
            return null;
        }
        if (separator == null) {
            separator = "";
        }
        
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                result.append(separator);
            }
            if (array[i] != null) {
                result.append(array[i]);
            }
        }
        
        return result.toString();
    }
    
    /**
     * 将字符串重复指定次数
     * @param str 要重复的字符串
     * @param repeat 重复次数
     * @return 重复后的字符串
     */
    public static String repeat(String str, int repeat) {
        if (str == null) {
            return null;
        }
        if (repeat <= 0) {
            return "";
        }
        
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < repeat; i++) {
            result.append(str);
        }
        
        return result.toString();
    }
    
    /**
     * 从包名中提取简单类名
     * @param className 完整类名
     * @return 简单类名
     */
    public static String getSimpleClassName(String className) {
        if (isEmpty(className)) {
            return className;
        }
        
        int lastDotIndex = className.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return className;
        }
        
        return className.substring(lastDotIndex + 1);
    }
    
    /**
     * 从完整类名中提取包名
     * @param className 完整类名
     * @return 包名
     */
    public static String getPackageName(String className) {
        if (isEmpty(className)) {
            return "";
        }
        
        int lastDotIndex = className.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        
        return className.substring(0, lastDotIndex);
    }
    
    /**
     * 将路径中的点号替换为斜杠
     * @param packageName 包名
     * @return 文件系统路径
     */
    public static String dotToSlash(String packageName) {
        if (isEmpty(packageName)) {
            return packageName;
        }
        return packageName.replace('.', '/');
    }
    
    /**
     * 将路径中的斜杠替换为点号
     * @param path 文件路径
     * @return 包名格式的路径
     */
    public static String slashToDot(String path) {
        if (isEmpty(path)) {
            return path;
        }
        return path.replace('/', '.');
    }
    
    /**
     * 为Bean名称添加前缀（如果需要）
     * @param beanName Bean名称
     * @param prefix 前缀
     * @return 处理后的Bean名称
     */
    public static String addPrefixIfNeeded(String beanName, String prefix) {
        if (isEmpty(beanName) || isEmpty(prefix)) {
            return beanName;
        }
        
        if (beanName.startsWith(prefix)) {
            return beanName;
        }
        
        return prefix + beanName;
    }
    
    /**
     * 移除Bean名称的前缀（如果存在）
     * @param beanName Bean名称
     * @param prefix 前缀
     * @return 处理后的Bean名称
     */
    public static String removePrefixIfExists(String beanName, String prefix) {
        if (isEmpty(beanName) || isEmpty(prefix)) {
            return beanName;
        }
        
        if (beanName.startsWith(prefix)) {
            return beanName.substring(prefix.length());
        }
        
        return beanName;
    }
}