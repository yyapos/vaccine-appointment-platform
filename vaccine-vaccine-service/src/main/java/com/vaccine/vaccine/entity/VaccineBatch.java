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
@TableName("vaccine_batch")
public class VaccineBatch extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long vaccineId;

    private String batchNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate productionDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;

    private Integer quantity;

    private Integer usedQuantity;

    private Integer availableQuantity;

    private String supplier;

    private String traceCode;

    private Long siteId;

    private Integer status;

    private String remark;
}