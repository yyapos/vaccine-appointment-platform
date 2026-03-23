package com.vaccine.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vaccine.common.entity.BasicSettings;

/**
 * 基本设置服务接口
 */
public interface BasicSettingsService extends IService<BasicSettings> {
    
    /**
     * 获取基本设置
     */
    BasicSettings getSettings();
    
    /**
     * 更新基本设置
     */
    boolean updateSettings(BasicSettings settings);
}