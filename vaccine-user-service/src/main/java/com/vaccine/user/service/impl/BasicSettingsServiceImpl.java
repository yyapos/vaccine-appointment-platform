package com.vaccine.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vaccine.common.entity.BasicSettings;
import com.vaccine.common.mapper.BasicSettingsMapper;
import com.vaccine.user.service.BasicSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 基本设置服务实现类
 */
@Service
@RequiredArgsConstructor
public class BasicSettingsServiceImpl extends ServiceImpl<BasicSettingsMapper, BasicSettings> implements BasicSettingsService {
    
    @Override
    public BasicSettings getSettings() {
        // 基本设置只有一条记录，ID为1
        return this.getById(1L);
    }
    
    @Override
    public boolean updateSettings(BasicSettings settings) {
        // 确保ID为1
        settings.setId(1L);
        return this.updateById(settings);
    }
}