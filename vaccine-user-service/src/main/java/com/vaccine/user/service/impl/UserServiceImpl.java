package com.vaccine.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vaccine.common.result.Result;
import com.vaccine.user.entity.User;
import com.vaccine.user.mapper.UserMapper;
import com.vaccine.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User login(String username, String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        wrapper.eq(User::getPassword, password);
        return this.getOne(wrapper);
    }

    @Override
    public User getUserByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return this.getOne(wrapper);
    }

    @Override
    public User createUser(User user) {
        // 检查用户名是否已存在
        if (getUserByUsername(user.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        // 设置默认状态
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        this.save(user);
        return user;
    }

    @Override
    public boolean updateUserByAdmin(User user) {
        User existingUser = this.getById(user.getId());
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }
        // 不允许修改用户名
        if (user.getUsername() != null && !user.getUsername().equals(existingUser.getUsername())) {
            throw new RuntimeException("不允许修改用户名");
        }
        // 更新允许修改的字段
        if (user.getRealName() != null) {
            existingUser.setRealName(user.getRealName());
        }
        if (user.getPhone() != null) {
            existingUser.setPhone(user.getPhone());
        }
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        if (user.getAvatar() != null) {
            existingUser.setAvatar(user.getAvatar());
        }
        if (user.getStatus() != null) {
            existingUser.setStatus(user.getStatus());
        }
        if (user.getPassword() != null) {
            existingUser.setPassword(user.getPassword());
        }
        return this.updateById(existingUser);
    }

    @Override
    public boolean updateUserStatus(Long userId, Integer status) {
        User user = this.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setStatus(status);
        return this.updateById(user);
    }

    @Override
    public boolean deleteUser(Long userId) {
        // 检查是否为超级管理员
        User user = this.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return this.removeById(userId);
    }

    @Override
    public Result<Map<String, Object>> getUserPage(Integer page, Integer pageSize, String username, Integer status, String createTimeStart, String createTimeEnd) {
        Page<User> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        // 添加查询条件
        if (username != null && !username.isEmpty()) {
            wrapper.like(User::getUsername, username);
        }

        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }

        // 添加时间过滤
        if (createTimeStart != null && !createTimeStart.isEmpty()) {
            wrapper.ge(User::getCreateTime, java.time.LocalDateTime.parse(createTimeStart + "T00:00:00"));
        }
        if (createTimeEnd != null && !createTimeEnd.isEmpty()) {
            wrapper.le(User::getCreateTime, java.time.LocalDateTime.parse(createTimeEnd + "T23:59:59"));
        }

        wrapper.orderByDesc(User::getCreateTime);

        Page<User> resultPage = this.page(pageParam, wrapper);

        // 清除密码
        resultPage.getRecords().forEach(user -> user.setPassword(null));

        Map<String, Object> data = new HashMap<>();
        data.put("records", resultPage.getRecords());
        data.put("total", resultPage.getTotal());
        data.put("current", resultPage.getCurrent());
        data.put("size", resultPage.getSize());
        data.put("pages", resultPage.getPages());
        
        return Result.success(data);
    }
}