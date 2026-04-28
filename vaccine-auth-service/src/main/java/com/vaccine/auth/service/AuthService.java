package com.vaccine.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vaccine.auth.entity.User;
import com.vaccine.auth.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserMapper userMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 根据用户名查询用户
     */
    public User getUserByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        wrapper.eq(User::getDeleted, 0);
        return userMapper.selectOne(wrapper);
    }

    /**
     * 验证用户状态
     */
    public void validateUser(User user) throws UsernameNotFoundException {
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        if (user.getStatus() != 1) {
            throw new UsernameNotFoundException("账号已被禁用");
        }
    }

    /**
     * 验证用户名和密码（明文验证）
     */
    public boolean validateUser(String username, String password) {
        User user = getUserByUsername(username);
        if (user == null) {
            log.info("用户不存在");
            return false;
        }
        if (user.getStatus() != 1) {
            log.info("账户禁用");
            return false;
        }
        // 明文密码验证
        return password.equals(user.getPassword());
    }

    /**
     * 保存 token 到 Redis
     */
    public void saveToken(String username, String token) {
        String key = "token:" + username;
        // token 有效期 24 小时
        redisTemplate.opsForValue().set(key, token, 24, TimeUnit.HOURS);
    }

    /**
     * 从 Redis 获取 token
     */
    public String getToken(String username) {
        String key = "token:" + username;
        Object token = redisTemplate.opsForValue().get(key);
        return token != null ? token.toString() : null;
    }

    /**
     * 删除 token（登出）
     */
    public void removeToken(String username) {
        String key = "token:" + username;
        redisTemplate.delete(key);
    }
}