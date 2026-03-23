package com.vaccine.appointment.feign;

import com.vaccine.common.entity.AppointmentSettings;
import com.vaccine.common.entity.ReminderSettings;
import com.vaccine.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 设置服务Feign客户端
 */
@FeignClient(name = "vaccine-user-service")
public interface SettingsClient {
    
    /**
     * 获取预约设置
     */
    @GetMapping("/settings/appointment")
    Result<AppointmentSettings> getAppointmentSettings();
    
    /**
     * 获取提醒设置
     */
    @GetMapping("/settings/reminder")
    Result<ReminderSettings> getReminderSettings();
}