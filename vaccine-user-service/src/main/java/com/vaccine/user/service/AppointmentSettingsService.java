package com.vaccine.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vaccine.common.entity.AppointmentSettings;

/**
 * 预约设置服务接口
 */
public interface AppointmentSettingsService extends IService<AppointmentSettings> {
    
    /**
     * 获取预约设置
     */
    AppointmentSettings getSettings();
    
    /**
     * 更新预约设置
     */
    boolean updateSettings(AppointmentSettings settings);
}