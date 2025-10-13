package com.github.microwind.userdemo.model;

/**
 * 班级信息实体类
 */
public class ClassInfo {
    private String id;
    private String name;
    private String grade;
    private Integer studentCount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public Integer getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(Integer studentCount) {
        this.studentCount = studentCount;
    }

    @Override
    public String toString() {
        return "ClassInfo{id='" + id + "', name='" + name + "', grade='" + grade
               + "', studentCount=" + studentCount + "}";
    }
}
