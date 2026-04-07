package com.shixi.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.shixi.BaseTest;
import com.shixi.common.BusinessException;
import com.shixi.common.ErrorCode;
import com.shixi.dto.LoginDTO;
import com.shixi.dto.LoginVO;
import com.shixi.dto.RegisterDTO;
import com.shixi.dto.UserVO;
import com.shixi.entity.User;
import com.shixi.mapper.UserMapper;
import com.shixi.security.JwtUtils;
import com.shixi.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户服务单元测试
 */
@DisplayName("用户服务测试")
class UserServiceImplTest extends BaseTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    @DisplayName("注册成功")
    void register_Success() {
        // given
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("newuser");
        dto.setPassword("password123");
        dto.setNickname("新用户");
        dto.setEmail("new@test.com");

        // when
        userService.register(dto);

        // then
        User savedUser = userService.getByUsername("newuser");
        assertNotNull(savedUser);
        assertEquals("newuser", savedUser.getUsername());
        assertEquals("新用户", savedUser.getNickname());
        assertEquals(1, savedUser.getStatus());
        assertTrue(BCrypt.checkpw("password123", savedUser.getPassword()));
    }

    @Test
    @DisplayName("注册失败-用户名已存在")
    void register_UsernameExist() {
        // given
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("testuser"); // 已存在的用户名
        dto.setPassword("password123");

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.register(dto));
        assertEquals(ErrorCode.USER_EXIST.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("登录成功")
    void login_Success() {
        // given
        // 先注册用户，确保密码哈希正确
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("logintestuser");
        registerDTO.setPassword("test123");
        registerDTO.setNickname("登录测试用户");
        userService.register(registerDTO);

        LoginDTO dto = new LoginDTO();
        dto.setUsername("logintestuser");
        dto.setPassword("test123");

        // when
        LoginVO result = userService.login(dto);

        // then
        assertNotNull(result);
        assertNotNull(result.getToken());
        assertNotNull(result.getUser());
        assertEquals("logintestuser", result.getUser().getUsername());
        assertTrue(jwtUtils.validateToken(result.getToken()));
    }

    @Test
    @DisplayName("登录失败-用户不存在")
    void login_UserNotFound() {
        // given
        LoginDTO dto = new LoginDTO();
        dto.setUsername("notexist");
        dto.setPassword("password123");

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.login(dto));
        assertEquals(ErrorCode.USER_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("登录失败-密码错误")
    void login_WrongPassword() {
        // given
        LoginDTO dto = new LoginDTO();
        dto.setUsername("testuser");
        dto.setPassword("wrongpassword");

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.login(dto));
        assertEquals(ErrorCode.USER_PASSWORD_ERROR.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("登录失败-用户已禁用")
    void login_UserDisabled() {
        // given
        // 先注册一个用户，然后手动禁用
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("disabletestuser");
        registerDTO.setPassword("test123");
        registerDTO.setNickname("待禁用用户");
        userService.register(registerDTO);
        
        // 获取用户并禁用
        User user = userService.getByUsername("disabletestuser");
        user.setStatus(0); // 禁用
        userMapper.updateById(user);

        LoginDTO dto = new LoginDTO();
        dto.setUsername("disabletestuser");
        dto.setPassword("test123");

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.login(dto));
        assertEquals(ErrorCode.USER_DISABLED.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("根据用户名查询用户-存在")
    void getByUsername_Exists() {
        // given - 先注册用户
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("querytestuser");
        registerDTO.setPassword("test123");
        registerDTO.setNickname("查询测试用户");
        userService.register(registerDTO);
        
        // when
        User user = userService.getByUsername("querytestuser");

        // then
        assertNotNull(user);
        assertEquals("querytestuser", user.getUsername());
    }

    @Test
    @DisplayName("根据用户名查询用户-不存在")
    void getByUsername_NotExists() {
        // when
        User user = userService.getByUsername("notexist");

        // then
        assertNull(user);
    }

    @Test
    @DisplayName("根据ID查询用户信息")
    void getUserById_Success() {
        // given - 先注册用户
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("infotestuser");
        registerDTO.setPassword("test123");
        registerDTO.setNickname("信息查询用户");
        userService.register(registerDTO);
        
        User user = userService.getByUsername("infotestuser");
        
        // when
        UserVO userVO = userService.getUserById(user.getId());

        // then
        assertNotNull(userVO);
        assertEquals("infotestuser", userVO.getUsername());
        assertNotNull(userVO.getRoles());
    }

    @Test
    @DisplayName("根据ID查询用户-不存在返回null")
    void getUserById_NotExists() {
        // when
        UserVO userVO = userService.getUserById(9999L);

        // then
        assertNull(userVO);
    }
}
