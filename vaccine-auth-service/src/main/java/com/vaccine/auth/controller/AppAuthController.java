//package com.vaccine.auth.controller;
//
//import com.vaccine.auth.entity.User;
//import com.vaccine.auth.service.AuthService;
//import com.vaccine.common.constant.RoleConstant;
//import com.vaccine.common.result.Result;
//import com.vaccine.common.util.JwtUtil;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * 家长小程序认证控制器
// * 只有家长角色可以登录
// */
//@RestController
//@RequestMapping("/app")
//@RequiredArgsConstructor
//public class AppAuthController {
//
//    private final AuthService authService;
//    private final JwtUtil jwtUtil;
//
//    /**
//     * 家长小程序登录
//     */
//    @PostMapping("/login")
//    public Result<Map<String, Object>> login(@RequestBody Map<String, String> request) {
//        try {
//            String username = request.get("username");
//            String password = request.get("password");
//
//            System.out.println("=== 家长小程序登录 ===");
//            System.out.println("用户名: " + username);
//
//            // 验证用户名和密码
//            if (!authService.validateUser(username, password)) {
//                System.out.println("用户名或密码错误");
//                return Result.error("用户名或密码错误");
//            }
//
//            // 获取用户信息
//            User loginUser = authService.getUserByUsername(username);
//
//            // 验证角色：只有家长可以登录小程序
//            if (!RoleConstant.PARENT.equals(loginUser.getRole())) {
//                System.out.println("权限不足，不是家长角色");
//                return Result.error("权限不足，只有家长可以登录小程序");
//            }
//
//            // 检查用户状态
//            if (loginUser.getStatus() == null || loginUser.getStatus() == 0) {
//                System.out.println("用户已被禁用");
//                return Result.error("用户已被禁用");
//            }
//
//            System.out.println("家长登录成功: " + loginUser.getRealName());
//
//            // 生成 JWT token（小程序可以使用较短的有效期）
//            String token = jwtUtil.generateToken(loginUser.getUsername(), loginUser.getId(), loginUser.getRole());
//            System.out.println("Token生成成功");
//
//            // 保存 token 到 Redis
//            authService.saveToken(loginUser.getUsername(), token);
//
//            // 返回结果
//            Map<String, Object> data = new HashMap<>();
//            data.put("token", token);
//
//            // 返回用户信息
//            Map<String, Object> userInfo = new HashMap<>();
//            userInfo.put("id", loginUser.getId());
//            userInfo.put("username", loginUser.getUsername());
//            userInfo.put("realName", loginUser.getRealName());
//            userInfo.put("phone", loginUser.getPhone());
//            userInfo.put("email", loginUser.getEmail());
//            userInfo.put("role", loginUser.getRole());
//            userInfo.put("avatar", loginUser.getAvatar());
//            data.put("user", userInfo);
//
//            return Result.success(data);
//        } catch (Exception e) {
//            System.out.println("登录失败: " + e.getMessage());
//            e.printStackTrace();
//            return Result.error("登录失败: " + e.getMessage());
//        }
//    }
//}