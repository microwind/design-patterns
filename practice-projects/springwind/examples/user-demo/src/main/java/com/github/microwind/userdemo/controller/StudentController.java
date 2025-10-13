package com.github.microwind.userdemo.controller;

import com.github.microwind.springwind.annotation.Controller;
import com.github.microwind.springwind.annotation.RequestMapping;
import com.github.microwind.springwind.annotation.Autowired;
import com.github.microwind.userdemo.model.Student;
import com.github.microwind.userdemo.service.StudentService;

/**
 * 学生控制器 - 处理学生信息查询
 */
@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @RequestMapping("/detail")
    public String getDetail() {
        Student student = studentService.getStudentById(1L);
        if (student != null) {
            System.out.println("学生详情：" + student);
            return "学生详情：" + student.toString();
        } else {
            return "未找到学生信息";
        }
    }
}
