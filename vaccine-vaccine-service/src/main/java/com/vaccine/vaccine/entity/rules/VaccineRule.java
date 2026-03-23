package com.vaccine.vaccine.entity.rules;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.vaccine.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("vaccine_rule")
public class VaccineRule extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long vaccineId;

    private String vaccineName;

    private Integer totalDoses;

    private String targetAge;

    private Integer minAgeDays;

    private Integer maxAgeDays;

    private String intervalDays;

    private String scheduleDescription;

    private Integer priority;

    private Integer status;
}