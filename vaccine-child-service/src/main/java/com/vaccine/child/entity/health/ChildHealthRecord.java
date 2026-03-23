package com.vaccine.child.entity.health;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("child_health_record")
public class ChildHealthRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long childId;

    private String recordType;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate recordDate;

    private String hospital;

    private String doctor;

    private String symptoms;

    private String diagnosis;

    private String treatment;

    private String notes;

    private String attachments;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String createBy;

    private String updateBy;

    private Integer deleted;
}