package com.vaccine.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vaccine.common.entity.AppointmentSettings;
import com.vaccine.common.mapper.AppointmentSettingsMapper;
import com.vaccine.user.service.AppointmentSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 预约设置服务实现类
 */
@Service
@RequiredArgsConstructor
public class AppointmentSettingsServiceImpl extends ServiceImpl<AppointmentSettingsMapper, AppointmentSettings> implements AppointmentSettingsService {
    
    @Override
    public AppointmentSettings getSettings() {
        // 预约设置只有一条记录，ID为1
        return this.getById(1L);
    }
    
    @Override
    public boolean updateSettings(AppointmentSettings settings) {
        // 确保ID为1
        settings.setId(1L);
        return this.updateById(settings);
    }
}