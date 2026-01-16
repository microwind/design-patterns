package com.github.microwind.userdemo.service;

import com.github.microwind.springwind.annotation.Service;
import com.github.microwind.springwind.annotation.Autowired;
import com.github.microwind.userdemo.dao.UserDao;
import com.github.microwind.userdemo.model.User;
import com.github.microwind.userdemo.exception.BusinessException;
import com.github.microwind.userdemo.exception.DuplicateKeyException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

/**
 * 用户业务逻辑层
 */
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    /**
     * 创建用户
     */
    public boolean createUser(User user) {
        try {
            // 验证用户名是否已存在
            User existing = userDao.findByUsername(user.getName());
            if (existing != null) {
                throw new DuplicateKeyException("用户名已存在");
            }

            int result = userDao.create(user);
            return result > 0;
        } catch (DuplicateKeyException e) {
            // 重新抛出业务异常
            throw e;
        } catch (Exception e) {
            // 捕获数据库异常并转换为友好的错误消息
            Throwable cause = e.getCause();
            while (cause != null) {
                if (cause instanceof SQLIntegrityConstraintViolationException) {
                    String message = cause.getMessage();

                    // 解析具体的约束违反类型
                    if (message.contains("Duplicate entry") && message.contains("unique_email")) {
                        throw new DuplicateKeyException("邮箱已被使用", cause);
                    } else if (message.contains("Duplicate entry") && message.contains("unique_phone")) {
                        throw new DuplicateKeyException("手机号已被使用", cause);
                    } else if (message.contains("Duplicate entry")) {
                        throw new DuplicateKeyException("数据重复，违反唯一约束", cause);
                    }
                }
                cause = cause.getCause();
            }

            // 其他未知异常
            throw new BusinessException(500, "创建用户失败: " + e.getMessage(), e);
        }
    }

    /**
     * 更新用户
     */
    public boolean updateUser(User user) {
        try {
            int result = userDao.update(user);
            return result > 0;
        } catch (Exception e) {
            // 捕获数据库异常
            Throwable cause = e.getCause();
            while (cause != null) {
                if (cause instanceof SQLIntegrityConstraintViolationException) {
                    String message = cause.getMessage();

                    if (message.contains("Duplicate entry") && message.contains("unique_email")) {
                        throw new DuplicateKeyException("邮箱已被使用", cause);
                    } else if (message.contains("Duplicate entry") && message.contains("unique_phone")) {
                        throw new DuplicateKeyException("手机号已被使用", cause);
                    } else if (message.contains("Duplicate entry")) {
                        throw new DuplicateKeyException("数据重复，违反唯一约束", cause);
                    }
                }
                cause = cause.getCause();
            }

            throw new BusinessException(500, "更新用户失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据 ID 获取用户
     */
    public User getUserById(Long id) {
        return userDao.findById(id);
    }

    /**
     * 根据用户名获取用户
     */
    public User getUserByUsername(String name) {
        return userDao.findByUsername(name);
    }

    /**
     * 获取所有用户
     */
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    /**
     * 删除用户
     */
    public boolean deleteUser(Long id) {
        int result = userDao.delete(id);
        return result > 0;
    }

    /**
     * 根据用户名删除用户
     */
    public boolean deleteUserByUsername(String name) {
        int result = userDao.deleteByUsername(name);
        return result > 0;
    }

    /**
     * 获取用户总数
     */
    public Long getUserCount() {
        return userDao.count();
    }

    /**
     * 验证用户登录
     */
    public boolean validateLogin(String name, String password) {
        User user = userDao.findByUsername(name);
        return user != null;
    }

    /**
     * 分页查询用户
     * @param page 页码，从1开始
     * @param pageSize 每页大小
     * @return 用户列表
     */
    public List<User> getUsersByPage(int page, int pageSize) {
        return userDao.findByPage(page, pageSize);
    }
}
