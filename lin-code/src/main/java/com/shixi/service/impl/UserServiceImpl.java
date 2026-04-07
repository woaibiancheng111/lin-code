package com.shixi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shixi.common.BusinessException;
import com.shixi.common.ErrorCode;
import com.shixi.dto.LoginDTO;
import com.shixi.dto.LoginVO;
import com.shixi.dto.RegisterDTO;
import com.shixi.dto.UserVO;
import com.shixi.entity.User;
import com.shixi.mapper.UserMapper;
import com.shixi.mapper.UserRoleMapper;
import com.shixi.security.JwtUtils;
import com.shixi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户服务实现
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final JwtUtils jwtUtils;

    @Override
    public void register(RegisterDTO dto) {
        // 检查用户名是否存在
        User existUser = getByUsername(dto.getUsername());
        if (existUser != null) {
            throw new BusinessException(ErrorCode.USER_EXIST);
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(BCrypt.hashpw(dto.getPassword()));
        user.setNickname(dto.getNickname());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setStatus(1);
        userMapper.insert(user);
    }

    @Override
    public LoginVO login(LoginDTO dto) {
        // 查询用户
        User user = getByUsername(dto.getUsername());
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        
        // 验证密码
        if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.USER_PASSWORD_ERROR);
        }
        
        // 检查状态
        if (user.getStatus() != 1) {
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }
        
        // 生成token
        String token = jwtUtils.generateToken(user.getId());
        
        // 构建响应
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUser(getUserById(user.getId()));
        return vo;
    }

    @Override
    public User getByUsername(String username) {
        return userMapper.selectOne(
            new LambdaQueryWrapper<User>().eq(User::getUsername, username)
        );
    }

    @Override
    public UserVO getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return null;
        }
        
        UserVO vo = new UserVO();
        BeanUtil.copyProperties(user, vo);
        
        // 查询角色
        List<String> roles = userRoleMapper.selectRoleCodesByUserId(id);
        vo.setRoles(roles);
        
        return vo;
    }

    @Override
    public User getCurrentUser() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return user;
    }

    @Override
    public Long getCurrentUserId() {
        return jwtUtils.getCurrentUserId();
    }
}
