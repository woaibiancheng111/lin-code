package com.shixi.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JWT工具类单元测试
 */
@DisplayName("JWT工具类测试")
class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        // 通过反射设置配置值
        ReflectionTestUtils.setField(jwtUtils, "secret", 
                "test-jwt-secret-key-must-be-at-least-256-bits-long-for-hs256-algorithm");
        ReflectionTestUtils.setField(jwtUtils, "expiration", 3600000L); // 1小时
    }

    @Test
    @DisplayName("生成Token-成功")
    void generateToken_Success() {
        // given
        Long userId = 1L;

        // when
        String token = jwtUtils.generateToken(userId);

        // then
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    @DisplayName("从Token解析用户ID-成功")
    void getUserIdFromToken_Success() {
        // given
        Long userId = 123L;
        String token = jwtUtils.generateToken(userId);

        // when
        Long extractedUserId = jwtUtils.getUserIdFromToken(token);

        // then
        assertEquals(userId, extractedUserId);
    }

    @Test
    @DisplayName("验证Token-有效Token")
    void validateToken_ValidToken() {
        // given
        String token = jwtUtils.generateToken(1L);

        // when
        boolean isValid = jwtUtils.validateToken(token);

        // then
        assertTrue(isValid);
    }

    @Test
    @DisplayName("验证Token-无效Token")
    void validateToken_InvalidToken() {
        // given
        String invalidToken = "invalid.token.string";

        // when
        boolean isValid = jwtUtils.validateToken(invalidToken);

        // then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("验证Token-篡改后的Token")
    void validateToken_TamperedToken() {
        // given
        String validToken = jwtUtils.generateToken(1L);
        String tamperedToken = validToken + "tampered";

        // when
        boolean isValid = jwtUtils.validateToken(tamperedToken);

        // then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("生成不同用户的Token-应该不同")
    void generateToken_DifferentUsers() {
        // given
        String token1 = jwtUtils.generateToken(1L);
        String token2 = jwtUtils.generateToken(2L);

        // then
        assertNotEquals(token1, token2);
    }

    @Test
    @DisplayName("解析用户ID-错误的Token抛出异常")
    void getUserIdFromToken_InvalidToken() {
        // given
        String invalidToken = "invalid.token";

        // when & then
        assertThrows(Exception.class, () -> jwtUtils.getUserIdFromToken(invalidToken));
    }
}
