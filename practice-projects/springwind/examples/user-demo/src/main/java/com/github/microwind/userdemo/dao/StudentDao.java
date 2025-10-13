package com.github.microwind.userdemo.dao;

import com.github.microwind.springwind.annotation.Repository;
import com.github.microwind.userdemo.model.Student;
import java.util.HashMap;
import java.util.Map;

/**
 * 学生数据访问层
 */
@Repository
public class StudentDao {
    private Map<Long, Student> students = new HashMap<>();

    public StudentDao() {
        // 初始化测试数据
        initTestData();
    }

    private void initTestData() {
        Student s1 = new Student();
        s1.setId(1L);
        s1.setName("张三");
        s1.setAge(20);
        s1.setClassId("CS101");
        s1.setEmail("zhangsan@example.com");
        students.put(1L, s1);

        Student s2 = new Student();
        s2.setId(2L);
        s2.setName("李四");
        s2.setAge(21);
        s2.setClassId("CS101");
        s2.setEmail("lisi@example.com");
        students.put(2L, s2);
    }

    public Student findById(Long id) {
        return students.get(id);
    }

    public boolean validateLogin(String username, String password) {
        // 简单的登录验证逻辑
        return "admin".equals(username) && "123456".equals(password);
    }
}
