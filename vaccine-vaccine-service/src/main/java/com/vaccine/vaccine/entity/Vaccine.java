package com.vaccine.vaccine.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.vaccine.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("vaccine")
public class Vaccine extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String code;

    private String manufacturer;

    private Integer doseNumber;

    private Integer intervalDays;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;

    private Integer stock;

    private String unit;

    private java.math.BigDecimal price;

    private String category;

    /**
     * 可接种年龄（岁，整数，用于简化预约和提醒逻辑）
     * 例如：0表示出生时，1表示1岁，2表示2岁
     */
    @com.baomidou.mybatisplus.annotation.TableField("target_age")
    private Integer targetAge;

    private String contraindications;

    private String adverseReactions;

    private String precautions;

    private String description;

    private Integer status;
}