package com.vaccine.user.controller;

import com.vaccine.common.result.Result;
import com.vaccine.common.util.JwtUtil;
import com.vaccine.user.entity.User;
import com.vaccine.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    /**
     * 获取用户总数（统计接口）
     */
    @GetMapping("/count")
    public Result<Long> getUserCount() {
        return Result.success(userService.count());
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/info")
    public Result<User> getUserInfo(@RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error("未授权，请先登录");
        }
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword(null); // 不返回密码
        return Result.success(user);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public Result<String> updateUser(@RequestHeader("Authorization") String token, @RequestBody Map<String, Object> updateData) {
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error("未授权，请先登录");
        }
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        // 更新允许修改的字段
        if (updateData.containsKey("realName")) {
            user.setRealName((String) updateData.get("realName"));
        }
        if (updateData.containsKey("phone")) {
            user.setPhone((String) updateData.get("phone"));
        }
        if (updateData.containsKey("email")) {
            user.setEmail((String) updateData.get("email"));
        }
        if (updateData.containsKey("avatar")) {
            user.setAvatar((String) updateData.get("avatar"));
        }
        boolean success = userService.updateById(user);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 分页查询用户列表（管理员接口）
     */
    @GetMapping("/page")
    public Result<Map<String, Object>> getUserPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String createTimeStart,
            @RequestParam(required = false) String createTimeEnd) {
        return userService.getUserPage(page, pageSize, username,  status, createTimeStart, createTimeEnd);
    }

    /**
     * 获取所有用户列表（管理员接口）
     */
    @GetMapping("/list")
    public Result<java.util.List<User>> getAllUsers() {
        java.util.List<User> users = userService.list();
        users.forEach(user -> user.setPassword(null));
        return Result.success(users);
    }

    /**
     * 根据用户名获取用户信息
     */
    @GetMapping("/username/{username}")
    public Result<User> getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    /**
     * 根据ID获取用户信息（管理员接口）
     */
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    /**
     * 创建用户（管理员接口）
     * 注意：后台管理系统只能创建管理员角色，家长通过微信小程序端创建
     */
    @PostMapping("/create")
    public Result<User> createUser(@RequestBody User user) {
        // 强制设置为管理员角色
        try {
            User created = userService.createUser(user);
            created.setPassword(null);
            return Result.success(created);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新用户信息（管理员接口）
     */
    @PutMapping("/admin/update")
    public Result<String> updateUserByAdmin(@RequestBody User user) {
        try {
            boolean success = userService.updateUserByAdmin(user);
            return success ? Result.success("更新成功") : Result.error("更新失败");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 禁用用户（管理员接口）
     */
    @PutMapping("/disable/{id}")
    public Result<String> disableUser(@PathVariable Long id) {
        try {
            boolean success = userService.updateUserStatus(id, 0);
            return success ? Result.success("用户已禁用") : Result.error("操作失败");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 启用用户（管理员接口）
     */
    @PutMapping("/enable/{id}")
    public Result<String> enableUser(@PathVariable Long id) {
        try {
            boolean success = userService.updateUserStatus(id, 1);
            return success ? Result.success("用户已启用") : Result.error("操作失败");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除用户（管理员接口）
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteUser(@PathVariable Long id) {
        try {
            boolean success = userService.deleteUser(id);
            return success ? Result.success("删除成功") : Result.error("删除失败");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 从 Token 中获取用户 ID
     */
    private Long getUserIdFromToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }
        String jwtToken = token.substring(7);
        try {
            return jwtUtil.getUserIdFromToken(jwtToken);
        } catch (Exception e) {
            return null;
        }
    }
}