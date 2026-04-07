package com.shixi.service;

import com.shixi.dto.LoginDTO;
import com.shixi.dto.LoginVO;
import com.shixi.dto.RegisterDTO;
import com.shixi.dto.UserVO;
import com.shixi.entity.User;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户注册
     */
    void register(RegisterDTO dto);
    
    /**
     * 用户登录
     */
    LoginVO login(LoginDTO dto);
    
    /**
     * 根据用户名查询用户
     */
    User getByUsername(String username);
    
    /**
     * 根据ID查询用户
     */
    UserVO getUserById(Long id);
    
    /**
     * 获取当前登录用户
     */
    User getCurrentUser();
    
    /**
     * 获取当前登录用户ID
     */
    Long getCurrentUserId();
}
