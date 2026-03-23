package com.vaccine.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 预约设置实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("appointment_settings")
public class AppointmentSettings extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 预约提前天数
     */
    private Integer advanceDays;

    /**
     * 每天最大预约数
     */
    private Integer maxDailyAppointments;

    /**
     * 预约开始时间
     */
    private String startTime;

    /**
     * 预约结束时间
     */
    private String endTime;

    /**
     * 是否启用预约功能 0:禁用 1:启用
     */
    private Integer enabled;
}