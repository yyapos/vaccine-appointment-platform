package com.vaccine.child.entity.health;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("child_growth_record")
public class ChildGrowthRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long childId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate recordDate;

    private Integer ageMonths;

    private BigDecimal height;

    private BigDecimal weight;

    private BigDecimal headCircumference;

    private BigDecimal chestCircumference;

    private BigDecimal heightScore;

    private BigDecimal weightScore;

    private String nutritionStatus;

    private String developmentMilestone;

    private String notes;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String createBy;

    private String updateBy;

    private Integer deleted;
}