package com.vaccine.child.entity.health;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("vaccination_contraindication")
public class VaccinationContraindication {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long childId;

    private Long vaccineId;

    private String contraindicationType;

    private String contraindicationReason;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private String description;

    private String doctor;

    private String hospital;

    private Integer isActive;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String createBy;

    private String updateBy;

    private Integer deleted;
}