package com.vaccine.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vaccine.common.entity.ReminderSettings;

/**
 * 提醒设置服务接口
 */
public interface ReminderSettingsService extends IService<ReminderSettings> {
    
    /**
     * 获取提醒设置
     */
    ReminderSettings getSettings();
    
    /**
     * 更新提醒设置
     */
    boolean updateSettings(ReminderSettings settings);
}