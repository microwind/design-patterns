package com.github.microwind.springboot4ddd.infrastructure.cache;

import com.github.microwind.springboot4ddd.interfaces.vo.user.UserResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * SimpleCacheService 单元测试
 * 覆盖缓存命中、未命中、Redis 故障降级三种核心场景
 *
 * @author jarry
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class SimpleCacheServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private SimpleCacheService simpleCacheService;

    private static final String TEST_KEY = "user:123";
    private static final Duration TEST_TTL = Duration.ofMinutes(30);

    private UserResponse testUser() {
        return UserResponse.builder()
                .id(123L)
                .name("testuser")
                .email("test@example.com")
                .build();
    }

    @Test
    void getOrSet_缓存命中_直接返回不查数据库() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(TEST_KEY)).thenReturn(testUser());

        UserResponse result = simpleCacheService.getOrSet(
                TEST_KEY, UserResponse.class, TEST_TTL, () -> fail("不应调用数据源"));

        assertNotNull(result);
        assertEquals(123L, result.getId());
        verify(valueOperations, never()).set(anyString(), any(), any(Duration.class));
    }

    @Test
    void getOrSet_缓存未命中_查数据库并回填缓存() {
        UserResponse user = testUser();
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(TEST_KEY)).thenReturn(null);

        UserResponse result = simpleCacheService.getOrSet(
                TEST_KEY, UserResponse.class, TEST_TTL, () -> user);

        assertNotNull(result);
        assertEquals(123L, result.getId());
        verify(valueOperations).set(TEST_KEY, user, TEST_TTL);
    }

    @Test
    void getOrSet_数据源返回null_不回填缓存() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(TEST_KEY)).thenReturn(null);

        UserResponse result = simpleCacheService.getOrSet(
                TEST_KEY, UserResponse.class, TEST_TTL, () -> null);

        assertNull(result);
        verify(valueOperations, never()).set(anyString(), any(), any(Duration.class));
    }

    @Test
    void getOrSet_Redis故障_降级走数据源() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(TEST_KEY)).thenThrow(new RuntimeException("Redis连接失败"));

        UserResponse result = simpleCacheService.getOrSet(
                TEST_KEY, UserResponse.class, TEST_TTL, () -> testUser());

        assertNotNull(result);
        assertEquals(123L, result.getId());
    }

    @Test
    void delete_正常删除() {
        when(redisTemplate.delete(TEST_KEY)).thenReturn(true);

        simpleCacheService.delete(TEST_KEY);

        verify(redisTemplate).delete(TEST_KEY);
    }

    @Test
    void delete_Redis故障_不抛异常() {
        when(redisTemplate.delete(TEST_KEY)).thenThrow(new RuntimeException("Redis连接失败"));

        assertDoesNotThrow(() -> simpleCacheService.delete(TEST_KEY));
    }
}
