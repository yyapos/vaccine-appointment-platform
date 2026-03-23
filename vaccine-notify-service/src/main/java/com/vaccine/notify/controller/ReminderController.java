package com.vaccine.notify.controller;

import com.vaccine.common.result.Result;
import com.vaccine.common.util.JwtUtil;
import com.vaccine.notify.entity.Reminder;
import com.vaccine.notify.service.ReminderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/reminder")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService reminderService;
    private final JwtUtil jwtUtil;

    /**
     * 获取当前用户的提醒列表
     */
    @GetMapping("/list")
    public Result<List<Reminder>> getReminders(@RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return Result.error("未授权，请先登录");
            }
            
            List<Reminder> reminders = reminderService.getRemindersByUserId(userId);
            return Result.success(reminders);
        } catch (Exception e) {
            log.error("获取提醒列表失败", e);
            return Result.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 获取未读提醒数量
     */
    @GetMapping("/unread-count")
    public Result<Long> getUnreadCount(@RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return Result.error("未授权，请先登录");
            }
            
            Long count = reminderService.getUnreadCount(userId);
            return Result.success(count);
        } catch (Exception e) {
            log.error("获取未读提醒数量失败", e);
            return Result.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 标记提醒为已读
     */
    @PutMapping("/read/{id}")
    public Result<String> markAsRead(@RequestHeader("Authorization") String token,
                                    @PathVariable Long id) {
        try {
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return Result.error("未授权，请先登录");
            }
            
            boolean success = reminderService.markAsRead(id);
            return success ? Result.success("标记成功") : Result.error("标记失败");
        } catch (Exception e) {
            log.error("标记提醒失败", e);
            return Result.error("标记失败: " + e.getMessage());
        }
    }

    /**
     * 批量标记提醒为已读
     */
    @PutMapping("/read-all")
    public Result<String> markAsReadBatch(@RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return Result.error("未授权，请先登录");
            }
            
            boolean success = reminderService.markAsReadBatch(userId);
            return success ? Result.success("标记成功") : Result.error("标记失败");
        } catch (Exception e) {
            log.error("批量标记提醒失败", e);
            return Result.error("标记失败: " + e.getMessage());
        }
    }

    /**
     * 获取提醒统计（管理员接口）
     */
    @GetMapping("/statistics")
    @com.vaccine.common.annotation.RequireAdmin
    public Result<Map<String, Object>> getStatistics() {
        try {
            Map<String, Object> stats = reminderService.getReminderStatistics();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取提醒统计失败", e);
            return Result.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 清理过期提醒（管理员接口）
     */
    @PostMapping("/clean")
    @com.vaccine.common.annotation.RequireAdmin
    public Result<Map<String, Object>> cleanExpiredReminders(@RequestParam(defaultValue = "90") Integer days) {
        try {
            int count = reminderService.cleanExpiredReminders(days);
            Map<String, Object> result = Map.of("count", count);
            return Result.success(result);
        } catch (Exception e) {
            log.error("清理过期提醒失败", e);
            return Result.error("清理失败: " + e.getMessage());
        }
    }

    /**
     * 删除提醒
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteReminder(@RequestHeader("Authorization") String token,
                                       @PathVariable Long id) {
        try {
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return Result.error("未授权，请先登录");
            }
            
            boolean success = reminderService.removeById(id);
            return success ? Result.success("删除成功") : Result.error("删除失败");
        } catch (Exception e) {
            log.error("删除提醒失败", e);
            return Result.error("删除失败: " + e.getMessage());
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