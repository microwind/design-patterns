package com.github.microwind.userdemo.service;

import com.github.microwind.springwind.annotation.Service;
import com.github.microwind.springwind.annotation.Autowired;
import com.github.microwind.userdemo.dao.ClassDao;
import com.github.microwind.userdemo.model.ClassInfo;
import java.util.List;

/**
 * 班级服务层
 */
@Service
public class ClassService {

    @Autowired
    private ClassDao classDao;

    public List<ClassInfo> getAllClasses() {
        return classDao.findAll();
    }

    public ClassInfo getClassById(String id) {
        return classDao.findById(id);
    }
}
