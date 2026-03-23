package com.vaccine.user.controller;

import com.vaccine.common.entity.AppointmentSettings;
import com.vaccine.common.entity.BasicSettings;
import com.vaccine.common.entity.ReminderSettings;
import com.vaccine.common.result.Result;
import com.vaccine.user.service.AppointmentSettingsService;
import com.vaccine.user.service.BasicSettingsService;
import com.vaccine.user.service.ReminderSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 系统设置控制器
 */
@RestController
@RequestMapping("/settings")
@RequiredArgsConstructor
public class SettingsController {
    
    private final BasicSettingsService basicSettingsService;
    private final AppointmentSettingsService appointmentSettingsService;
    private final ReminderSettingsService reminderSettingsService;
    
    /**
     * 获取基本设置
     */
    @GetMapping("/basic")
    public Result<BasicSettings> getBasicSettings() {
        try {
            BasicSettings settings = basicSettingsService.getSettings();
            if (settings == null) {
                // 如果不存在，创建默认设置
                settings = new BasicSettings();
                settings.setSystemName("社区儿童疫苗接种管理系统");
                settings.setSystemDescription("为社区儿童提供疫苗接种预约和管理的平台");
                settings.setAdminEmail("2927824043@example.com");
                settings.setContactPhone("15512725316");
                basicSettingsService.save(settings);
                settings = basicSettingsService.getById(settings.getId());
            }
            return Result.success(settings);
        } catch (Exception e) {
            return Result.error("获取基本设置失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新基本设置
     */
    @PutMapping("/basic")
    public Result<String> updateBasicSettings(@RequestBody BasicSettings settings) {
        try {
            boolean success = basicSettingsService.updateSettings(settings);
            return success ? Result.success("更新成功") : Result.error("更新失败");
        } catch (Exception e) {
            return Result.error("更新失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取预约设置
     */
    @GetMapping("/appointment")
    public Result<AppointmentSettings> getAppointmentSettings() {
        try {
            AppointmentSettings settings = appointmentSettingsService.getSettings();
            if (settings == null) {
                // 如果不存在，创建默认设置
                settings = new AppointmentSettings();
                settings.setAdvanceDays(7);
                settings.setMaxDailyAppointments(100);
                settings.setStartTime("08:00");
                settings.setEndTime("17:00");
                settings.setEnabled(1);
                appointmentSettingsService.save(settings);
                settings = appointmentSettingsService.getById(settings.getId());
            }
            return Result.success(settings);
        } catch (Exception e) {
            return Result.error("获取预约设置失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新预约设置
     */
    @PutMapping("/appointment")
    public Result<String> updateAppointmentSettings(@RequestBody AppointmentSettings settings) {
        try {
            boolean success = appointmentSettingsService.updateSettings(settings);
            return success ? Result.success("更新成功") : Result.error("更新失败");
        } catch (Exception e) {
            return Result.error("更新失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取提醒设置
     */
    @GetMapping("/reminder")
    public Result<ReminderSettings> getReminderSettings() {
        try {
            ReminderSettings settings = reminderSettingsService.getSettings();
            if (settings == null) {
                // 如果不存在，创建默认设置
                settings = new ReminderSettings();
                settings.setAppointmentReminder(1);
                settings.setVaccinationReminder(1);
                settings.setSmsReminder(0);
                settings.setReminderTime("09:00");
                settings.setReminderDaysBefore("3,1");
                settings.setEnabled(1);
                reminderSettingsService.save(settings);
                settings = reminderSettingsService.getById(settings.getId());
            }
            return Result.success(settings);
        } catch (Exception e) {
            return Result.error("获取提醒设置失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新提醒设置
     */
    @PutMapping("/reminder")
    public Result<String> updateReminderSettings(@RequestBody ReminderSettings settings) {
        try {
            boolean success = reminderSettingsService.updateSettings(settings);
            return success ? Result.success("更新成功") : Result.error("更新失败");
        } catch (Exception e) {
            return Result.error("更新失败: " + e.getMessage());
        }
    }
}