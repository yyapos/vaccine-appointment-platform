package com.vaccine.parent.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vaccine.common.result.Result;
import com.vaccine.common.util.JwtUtil;
import com.vaccine.parent.entity.Parent;
import com.vaccine.parent.service.ParentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/parent")
@RequiredArgsConstructor
public class ParentController {
    private final ParentService parentService;
    private final JwtUtil jwtUtil;
    private final com.vaccine.parent.util.WeChatUtil weChatUtil;

    /**
     * 获取家长总数（统计接口）
     */
    @GetMapping("/count")
    public Result<Long> getParentCount() {

        LambdaQueryWrapper<Parent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Parent::getDeleted, 0);
        wrapper.eq(Parent::getStatus, 1);
        long count = parentService.count(wrapper);

        return Result.success(count);
    }

    /**
     * 微信登录
     */
    @PostMapping("/wxlogin")
    public Result<Map<String, Object>> wxLogin(@RequestBody Map<String, Object> wxInfo) {

        try {
            String code = (String) wxInfo.get("code");
            if (code == null || code.isEmpty()) {
                return Result.error("微信登录code不能为空");
            }

            // 1. 通过code换取openid
            Map<String, String> sessionInfo = weChatUtil.code2Session(code);

            if (sessionInfo.containsKey("error")) {
                return Result.error("微信登录失败: " + sessionInfo.get("error"));
            }

            String openid = sessionInfo.get("openid");
            wxInfo.put("openid", openid);
            if (sessionInfo.containsKey("unionid")) {
                wxInfo.put("unionid", sessionInfo.get("unionid"));
            }

            // 2. 登录或注册家长
            Parent parent = parentService.wxLogin(wxInfo);
            String token = jwtUtil.generateToken(parent.getNickname(), parent.getId(), "PARENT");
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("data", parent);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("登录失败: " + e.getMessage());
        }
    }

    /**
     * 解析手机号
     */
    @PostMapping("/wxlogin/phone")
    public Result<Map<String, String>> getPhoneNumber(@RequestBody Map<String, String> request) {
        try {
            String code = request.get("code");
            if (code == null || code.isEmpty()) {
                return Result.error("手机号授权code不能为空");
            }

            // 调用微信接口解析手机号
            Map<String, String> phoneInfo = weChatUtil.getPhoneNumber(code);
            
            if (phoneInfo.containsKey("error")) {
                return Result.error("解析手机号失败: " + phoneInfo.get("error"));
            }

            return Result.success(phoneInfo);
        } catch (Exception e) {
            return Result.error("解析手机号失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前登录家长信息
     */
    @GetMapping("/info")
    public Result<Parent> getParentInfo(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(token);
            Parent parent = parentService.getById(userId);
            if (parent == null) {
                return Result.error("用户不存在");
            }
            return Result.success(parent);
        } catch (Exception e) {
            return Result.error("验证失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询家长列表（管理员接口）
     */
    @GetMapping("/page")
    public Result<Map<String, Object>> getParentPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Integer status) {
        return Result.success(parentService.getParentPage(page, pageSize, nickname, phone, status));
    }

    /**
     * 根据ID获取家长信息（管理员接口）
     */
    @GetMapping("/{id}")
    public Result<Parent> getParentById(@PathVariable Long id) {
        Parent parent = parentService.getById(id);
        if (parent == null) {
            return Result.error("家长不存在");
        }
        return Result.success(parent);
    }

    /**
     * 更新家长信息（管理员接口）
     */
    @PutMapping("/update")
    public Result<String> updateParent(@RequestBody Parent parent) {
        try {
            boolean success = parentService.updateById(parent);
            return success ? Result.success("更新成功") : Result.error("更新失败");
        } catch (Exception e) {
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 禁用家长（管理员接口）
     */
    @PutMapping("/disable/{id}")
    public Result<String> disableParent(@PathVariable Long id) {
        try {
            boolean success = parentService.updateParentStatus(id, 0);
            return success ? Result.success("家长已禁用") : Result.error("操作失败");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 启用家长（管理员接口）
     */
    @PutMapping("/enable/{id}")
    public Result<String> enableParent(@PathVariable Long id) {
        try {
            boolean success = parentService.updateParentStatus(id, 1);
            return success ? Result.success("家长已启用") : Result.error("操作失败");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除家长（管理员接口）
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteParent(@PathVariable Long id) {
        try {
            boolean success = parentService.deleteParent(id);
            return success ? Result.success("删除成功") : Result.error("删除失败");
        } catch (Exception e) {
            return Result.error("删除失败: " + e.getMessage());
        }
    }
}