package com.github.microwind.userdemo.controller;

import com.github.microwind.springwind.annotation.Controller;
import com.github.microwind.springwind.annotation.RequestMapping;
import com.github.microwind.springwind.annotation.Autowired;
import com.github.microwind.userdemo.model.ClassInfo;
import com.github.microwind.userdemo.service.ClassService;
import java.util.List;

/**
 * 班级控制器 - 处理班级信息查询
 */
@Controller
@RequestMapping("/class")
public class ClassController {

    @Autowired
    private ClassService classService;

    @RequestMapping("/list")
    public String getList() {
        List<ClassInfo> classes = classService.getAllClasses();
        System.out.println("班级列表：");
        for (ClassInfo classInfo : classes) {
            System.out.println("  " + classInfo);
        }
        return "班级列表：共 " + classes.size() + " 个班级";
    }
}
