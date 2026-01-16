package com.github.microwind.userdemo.controller;

import com.github.microwind.springwind.annotation.*;
import com.github.microwind.springwind.web.ViewResult;
import com.github.microwind.userdemo.exception.BusinessException;
import com.github.microwind.userdemo.exception.DuplicateKeyException;
import com.github.microwind.userdemo.service.UserService;
import com.github.microwind.userdemo.model.User;
import com.github.microwind.userdemo.utils.ApiResponse;
import com.github.microwind.userdemo.utils.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 用户控制器 - RESTful 风格
 * 使用 ApiResponse 直接返回，支持路径参数
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取所有用户列表（支持分页）
     * GET /user?page=1&pageSize=10
     * 不提供分页参数时返回所有用户
     */
    @GetMapping("")
    @ResponseBody
    public ViewResult list(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        try {
            // 如果提供了分页参数，则进行分页查询
            if (page != null && pageSize != null) {
                List<User> users = userService.getUsersByPage(page, pageSize);
                Long total = userService.getUserCount();
                return ApiResponse.page(users, page, pageSize, total);
            } else {
                // 不分页，返回所有用户
                List<User> users = userService.getAllUsers();
                return ApiResponse.success(users, "获取用户列表成功");
            }
        } catch (Exception e) {
            return ApiResponse.failure("获取用户列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据 ID 获取用户详情
     * GET /user/{id}
     */
    @GetMapping("/{id}")
    @ResponseBody
    public ViewResult getById(@PathVariable("id") Long id) {
        try {
            User user = userService.getUserById(id);
            if (user == null) {
                return ApiResponse.notFound("用户不存在");
            }
            return ApiResponse.success(user, "获取用户详情成功");
        } catch (Exception e) {
            return ApiResponse.failure("获取用户详情失败: " + e.getMessage());
        }
    }

    /**
     * 根据用户名获取用户
     * GET /user/name/{username}
     */
    @GetMapping("/name/{username}")
    @ResponseBody
    public ViewResult getByUsername(@PathVariable("username") String username) {
        try {
            if (username == null || username.isEmpty()) {
                return ApiResponse.badRequest("用户名不能为空");
            }
            User user = userService.getUserByUsername(username);
            if (user == null) {
                return ApiResponse.notFound("用户不存在");
            }
            return ApiResponse.success(user, "获取用户详情成功");
        } catch (Exception e) {
            return ApiResponse.failure("获取用户详情失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户总数
     * GET /user/count
     */
    @GetMapping("/count")
    @ResponseBody
    public ViewResult count() {
        try {
            Long count = userService.getUserCount();
            return ApiResponse.success(count, "获取用户总数成功");
        } catch (Exception e) {
            return ApiResponse.failure("获取用户总数失败: " + e.getMessage());
        }
    }

    /**
     * 创建用户
     * POST /user
     * Body: {"name":"admin","email":"admin@example.com","phone":"13800138000"}
     */
    @PostMapping("")
    @ResponseBody
    public ViewResult create(HttpServletRequest request) throws IOException {
        try {
            String body = getRequestBody(request);
            Map<String, Object> data = JsonUtil.parseJson(body);

            String name = (String) data.get("name");
            String email = (String) data.get("email");
            String phone = (String) data.get("phone");

            if (name == null || name.isEmpty()) {
                return ApiResponse.badRequest("用户名不能为空");
            }

            User user = new User(name, email, phone);
            userService.createUser(user);  // 可能抛出 DuplicateKeyException

            // 重新查询用户以获取生成的 ID
            User createdUser = userService.getUserByUsername(name);
            return ApiResponse.success(createdUser, "创建用户成功");

        } catch (DuplicateKeyException e) {
            // 捕获重复键异常，返回 400
            return ApiResponse.badRequest(e.getMessage());
        } catch (BusinessException e) {
            // 捕获业务异常，返回对应的状态码
            return ApiResponse.failure(e.getCode(), e.getMessage());
        } catch (Exception e) {
            return ApiResponse.failure("创建用户失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户
     * PUT /user/{id}
     * Body: {"name":"admin","email":"admin@example.com","phone":"13800138000"}
     */
    @PutMapping("/{id}")
    @ResponseBody
    public ViewResult update(@PathVariable("id") Long id, HttpServletRequest request) throws IOException {
        try {
            String body = getRequestBody(request);
            Map<String, Object> data = JsonUtil.parseJson(body);

            User user = userService.getUserById(id);
            if (user == null) {
                return ApiResponse.notFound("用户不存在");
            }

            String name = (String) data.get("name");
            String email = (String) data.get("email");
            String phone = (String) data.get("phone");

            if (name != null) user.setName(name);
            if (email != null) user.setEmail(email);
            if (phone != null) user.setPhone(phone);

            userService.updateUser(user);  // 可能抛出 DuplicateKeyException
            return ApiResponse.success(user, "更新用户成功");

        } catch (com.github.microwind.userdemo.exception.DuplicateKeyException e) {
            // 捕获重复键异常，返回 400
            return ApiResponse.badRequest(e.getMessage());
        } catch (com.github.microwind.userdemo.exception.BusinessException e) {
            // 捕获业务异常，返回对应的状态码
            return ApiResponse.failure(e.getCode(), e.getMessage());
        } catch (Exception e) {
            return ApiResponse.failure("更新用户失败: " + e.getMessage());
        }
    }

    /**
     * 删除用户
     * DELETE /user/{id}
     */
    @DeleteMapping("/{id}")
    @ResponseBody
    public ViewResult delete(@PathVariable("id") Long id) {
        try {
            User user = userService.getUserById(id);
            if (user == null) {
                return ApiResponse.notFound("用户不存在");
            }

            boolean success = userService.deleteUser(id);
            if (success) {
                return ApiResponse.success("删除用户成功");
            } else {
                return ApiResponse.failure("删除用户失败");
            }
        } catch (Exception e) {
            return ApiResponse.failure("删除用户失败: " + e.getMessage());
        }
    }

    /**
     * 用户登录
     * POST /user/login
     * Body: {"name":"admin","password":"123456"}
     */
    @PostMapping("/login")
    @ResponseBody
    public ViewResult login(HttpServletRequest request) throws IOException {
        try {
            String body = getRequestBody(request);
            Map<String, Object> data = JsonUtil.parseJson(body);

            String name = (String) data.get("name");
            String password = (String) data.get("password");

            if (name == null || name.isEmpty() ||
                password == null || password.isEmpty()) {
                return ApiResponse.badRequest("用户名和密码不能为空");
            }

            boolean success = userService.validateLogin(name, password);
            if (success) {
                User user = userService.getUserByUsername(name);
                return ApiResponse.success(user, "登录成功");
            } else {
                return ApiResponse.unauthorized("用户名或密码错误");
            }
        } catch (Exception e) {
            return ApiResponse.failure("登录失败: " + e.getMessage());
        }
    }

    /**
     * 获取请求体内容
     */
    private String getRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = request.getInputStream().read(buffer)) != -1) {
            sb.append(new String(buffer, 0, len, "UTF-8"));
        }
        return sb.toString();
    }
}
