package com.github.microwind.springboot4ddd.interfaces.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.microwind.springboot4ddd.application.service.user.UserService;
import com.github.microwind.springboot4ddd.interfaces.vo.user.CreateUserRequest;
import com.github.microwind.springboot4ddd.interfaces.vo.user.UpdateUserRequest;
import com.github.microwind.springboot4ddd.interfaces.vo.user.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * User Controller API 单元测试 - CRUD 基本操作
 *
 * @author jarry
 * @since 1.0.0
 */
@DisplayName("User Controller API 测试")
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userService)).build();
    }

    // ==================== CREATE 测试 ====================

    @Test
    @DisplayName("创建用户 - 成功")
    void testCreateUser_Success() throws Exception {
        CreateUserRequest request = CreateUserRequest.builder()
                .name("testuser")
                .email("test@example.com")
                .phone("13800138000")
                .wechat("test_wechat")
                .address("Beijing")
                .build();

        UserResponse response = UserResponse.builder()
                .id(1L)
                .name("testuser")
                .email("test@example.com")
                .phone("13800138000")
                .wechat("test_wechat")
                .address("Beijing")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("testuser"));

        verify(userService, times(1)).createUser(any(CreateUserRequest.class));
    }

    // ==================== READ 测试 ====================

    @Test
    @DisplayName("获取所有用户 - 成功")
    void testGetAllUsers_Success() throws Exception {
        List<UserResponse> users = Arrays.asList(
                UserResponse.builder().id(1L).name("user1").email("user1@example.com").build(),
                UserResponse.builder().id(2L).name("user2").email("user2@example.com").build()
        );

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].name").value("user1"))
                .andExpect(jsonPath("$.data[1].name").value("user2"));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @DisplayName("根据 ID 获取用户 - 成功")
    void testGetUserById_Success() throws Exception {
        long userId = 1L;
        UserResponse userResponse = UserResponse.builder()
                .id(userId)
                .name("testuser")
                .email("test@example.com")
                .phone("13800138000")
                .build();

        when(userService.getUserById(userId)).thenReturn(userResponse);

        mockMvc.perform(get("/api/users/{id}", userId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(userId))
                .andExpect(jsonPath("$.data.name").value("testuser"));

        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    @DisplayName("根据用户名获取用户 - 成功")
    void testGetUserByName_Success() throws Exception {
        String userName = "testuser";
        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .name(userName)
                .email("test@example.com")
                .phone("13800138000")
                .build();

        when(userService.getUserByName(userName)).thenReturn(userResponse);

        mockMvc.perform(get("/api/users/name/{name}", userName)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value(userName));

        verify(userService, times(1)).getUserByName(userName);
    }

    // ==================== UPDATE 测试 ====================

    @Test
    @DisplayName("更新用户 - 成功")
    void testUpdateUser_Success() throws Exception {
        long userId = 1L;
        UpdateUserRequest request = UpdateUserRequest.builder()
                .email("updated@example.com")
                .phone("13900139000")
                .address("Shanghai")
                .build();

        UserResponse response = UserResponse.builder()
                .id(userId)
                .name("testuser")
                .email("updated@example.com")
                .phone("13900139000")
                .address("Shanghai")
                .build();

        when(userService.updateUser(eq(userId), any(UpdateUserRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/users/{id}", userId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(userId))
                .andExpect(jsonPath("$.data.email").value("updated@example.com"));

        verify(userService, times(1)).updateUser(eq(userId), any(UpdateUserRequest.class));
    }

    // ==================== DELETE 测试 ====================

    @Test
    @DisplayName("删除用户 - 成功")
    void testDeleteUser_Success() throws Exception {
        long userId = 1L;
        doNothing().when(userService).deleteUser(userId);

        mockMvc.perform(delete("/api/users/{id}", userId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(userService, times(1)).deleteUser(userId);
    }
}
