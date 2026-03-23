package com.vaccine.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vaccine.common.entity.AppointmentSettings;
import org.apache.ibatis.annotations.Mapper;

/**
 * 预约设置Mapper接口
 */
@Mapper
public interface AppointmentSettingsMapper extends BaseMapper<AppointmentSettings> {
}