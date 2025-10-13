package com.github.microwind.userdemo.dao;

import com.github.microwind.springwind.annotation.Repository;
import com.github.microwind.userdemo.model.ClassInfo;
import java.util.ArrayList;
import java.util.List;

/**
 * 班级数据访问层
 */
@Repository
public class ClassDao {
    private List<ClassInfo> classes = new ArrayList<>();

    public ClassDao() {
        // 初始化测试数据
        initTestData();
    }

    private void initTestData() {
        ClassInfo c1 = new ClassInfo();
        c1.setId("CS101");
        c1.setName("计算机科学101班");
        c1.setGrade("2023");
        c1.setStudentCount(30);
        classes.add(c1);

        ClassInfo c2 = new ClassInfo();
        c2.setId("CS102");
        c2.setName("计算机科学102班");
        c2.setGrade("2023");
        c2.setStudentCount(28);
        classes.add(c2);

        ClassInfo c3 = new ClassInfo();
        c3.setId("SE101");
        c3.setName("软件工程101班");
        c3.setGrade("2023");
        c3.setStudentCount(32);
        classes.add(c3);
    }

    public List<ClassInfo> findAll() {
        return new ArrayList<>(classes);
    }

    public ClassInfo findById(String id) {
        return classes.stream()
            .filter(c -> c.getId().equals(id))
            .findFirst()
            .orElse(null);
    }
}
