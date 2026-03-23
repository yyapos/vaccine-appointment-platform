package com.vaccine.notify.feign;

import com.vaccine.common.entity.ReminderSettings;
import com.vaccine.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 设置服务Feign客户端
 */
@FeignClient(name = "vaccine-user-service", path = "/settings")
public interface SettingsClient {
    
    /**
     * 获取提醒设置
     */
    @GetMapping("/reminder")
    Result<ReminderSettings> getReminderSettings();
}