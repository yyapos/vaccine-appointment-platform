package com.vaccine.appointment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.vaccine.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("vaccination_record")
public class VaccinationRecord extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long childId;

    private Long vaccineId;

    private Integer doseNumber;

    private Long appointmentId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate scheduledDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime vaccinatedDate;

    private String vaccinationSite;

    private String batchNumber;

    private Long batchId;

    private Long vaccinatorId;

    private Integer status;

    private String reaction;

    private Integer reactionLevel;

    private String notes;
}