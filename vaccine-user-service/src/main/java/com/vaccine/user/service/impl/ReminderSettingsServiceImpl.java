package com.vaccine.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vaccine.common.entity.ReminderSettings;
import com.vaccine.common.mapper.ReminderSettingsMapper;
import com.vaccine.user.service.ReminderSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 提醒设置服务实现类
 */
@Service
@RequiredArgsConstructor
public class ReminderSettingsServiceImpl extends ServiceImpl<ReminderSettingsMapper, ReminderSettings> implements ReminderSettingsService {
    
    @Override
    public ReminderSettings getSettings() {
        // 提醒设置只有一条记录，ID为1
        return this.getById(1L);
    }
    
    @Override
    public boolean updateSettings(ReminderSettings settings) {
        // 确保ID为1
        settings.setId(1L);
        return this.updateById(settings);
    }
}