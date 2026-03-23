package com.vaccine.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 基本设置实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("basic_settings")
public class BasicSettings extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 系统名称
     */
    private String systemName;

    /**
     * 系统描述
     */
    private String systemDescription;

    /**
     * 管理员邮箱
     */
    private String adminEmail;

    /**
     * 联系电话
     */
    private String contactPhone;
}