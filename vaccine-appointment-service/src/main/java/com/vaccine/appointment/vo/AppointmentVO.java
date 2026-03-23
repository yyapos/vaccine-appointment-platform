package com.vaccine.appointment.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AppointmentVO {
    // 基础预约信息
    private Long id;
    private Long childId;
    private Long vaccineId;
    private Integer doseNumber;
    private String timeSlot;
    private Integer status;
    private String cancelReason;
    private String notes;
    private String statusText;

    // 时间信息
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate appointmentDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    // 扩展信息 - 关联查询儿童
    private String childName;  // 儿童姓名
    private Integer childGender;  // 儿童性别
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate childBirthDate;  // 儿童出生日期
    private Integer isKeyChild;  // 是否重点儿童

    // 扩展信息 - 关联查询疫苗
    private String vaccineName;  // 疫苗名称
    private String manufacturer;  // 生产厂家

    // 扩展信息 - 关联查询家长
    private Long parentId;  // 家长ID
    private String parentPhone;  // 家长电话
    private String parentName;  // 家长姓名

    // 审核相关
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime auditTime;  // 审核时间
    private String remark;  // 备注
    private String rejectReason;  // 拒绝原因
    private String auditOpinion;  // 审核意见

    // 接种信息（管理员填写）
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate vaccinatedDate;  // 实际接种时间
    private String vaccinationSite;  // 接种地点
    private String batchNumber;  // 疫苗批号
    private String doctor;  // 接种医生
    private String reaction;  // 不良反应
}