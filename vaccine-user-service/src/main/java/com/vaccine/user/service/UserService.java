package com.vaccine.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vaccine.common.result.Result;
import com.vaccine.user.entity.User;

import java.util.Map;

public interface UserService extends IService<User> {

    User login(String username, String password);

    User getUserByUsername(String username);

    /**
     * 创建用户（管理员）
     */
    User createUser(User user);

    /**
     * 更新用户信息（管理员）
     */
    boolean updateUserByAdmin(User user);

    /**
     * 更新用户状态（管理员）
     */
    boolean updateUserStatus(Long userId, Integer status);

    /**
     * 删除用户（管理员）
     */
    boolean deleteUser(Long userId);

    // 分页查询用户列表（管理员）
    Result<Map<String, Object>> getUserPage(Integer page, Integer pageSize, String username, Integer status, String createTimeStart, String createTimeEnd);
}