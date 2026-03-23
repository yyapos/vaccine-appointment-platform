package com.vaccine.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 提醒设置实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("reminder_settings")
public class ReminderSettings extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 是否启用预约提醒 0:禁用 1:启用
     */
    private Integer appointmentReminder;

    /**
     * 是否启用接种提醒 0:禁用 1:启用
     */
    private Integer vaccinationReminder;

    /**
     * 是否启用短信提醒 0:禁用 1:启用
     */
    private Integer smsReminder;

    /**
     * 提醒时间
     */
    private String reminderTime;

    /**
     * 提前提醒天数（逗号分隔）
     */
    private String reminderDaysBefore;

    /**
     * 是否启用提醒功能 0:禁用 1:启用
     */
    private Integer enabled;
}