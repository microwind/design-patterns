package com.github.microwind.userdemo.service;

import com.github.microwind.springwind.annotation.Service;
import com.github.microwind.springwind.annotation.Autowired;
import com.github.microwind.userdemo.dao.StudentDao;
import com.github.microwind.userdemo.model.Student;

/**
 * 学生服务层
 */
@Service
public class StudentService {

    @Autowired
    private StudentDao studentDao;

    public Student getStudentById(Long id) {
        return studentDao.findById(id);
    }

    public boolean login(String username, String password) {
        return studentDao.validateLogin(username, password);
    }
}
