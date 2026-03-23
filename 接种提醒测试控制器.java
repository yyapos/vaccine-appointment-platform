package com.vaccine.notify.controller;

import com.vaccine.notify.scheduled.VaccinationReminderScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 接种提醒测试控制器
 * 用于手动触发提醒功能测试
 */
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class ReminderTestController {

    private final VaccinationReminderScheduler reminderScheduler;

    /**
     * 手动触发提醒检查
     * POST /test/reminder/trigger
     */
    @PostMapping("/reminder/trigger")
    public ResponseEntity<Map<String, Object>> triggerReminder() {
        Map<String, Object> result = new HashMap<>();

        try {
            // 手动调用定时任务方法
            reminderScheduler.checkAllReminders();

            result.put("success", true);
            result.put("message", "提醒检查已触发，请查看控制台输出和数据库记录");
            result.put("timestamp", System.currentTimeMillis());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "提醒检查触发失败：" + e.getMessage());
            result.put("error", e.getClass().getSimpleName());
            result.put("timestamp", System.currentTimeMillis());
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 获取测试状态
     * GET /test/reminder/status
     */
    @PostMapping("/reminder/status")
    public ResponseEntity<Map<String, Object>> getTestStatus() {
        Map<String, Object> result = new HashMap<>();

        try {
            result.put("success", true);
            result.put("message", "提醒服务正常运行");
            result.put("service", "vaccine-notify-service");
            result.put("port", 8086);
            result.put("scheduler", "VaccinationReminderScheduler");
            result.put("cron", "0 0 1 * * ?"); // 每天凌晨1点执行
            result.put("timestamp", System.currentTimeMillis());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取状态失败：" + e.getMessage());
            result.put("timestamp", System.currentTimeMillis());
        }

        return ResponseEntity.ok(result);
    }
}