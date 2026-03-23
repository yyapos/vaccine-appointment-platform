package com.vaccine.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vaccine.common.entity.ReminderSettings;
import org.apache.ibatis.annotations.Mapper;

/**
 * 提醒设置Mapper接口
 */
@Mapper
public interface ReminderSettingsMapper extends BaseMapper<ReminderSettings> {
}