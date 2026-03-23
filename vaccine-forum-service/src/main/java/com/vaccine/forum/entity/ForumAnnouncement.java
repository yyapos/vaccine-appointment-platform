package com.vaccine.forum.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.vaccine.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("forum_announcement")
public class ForumAnnouncement extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String content;

    private String type;

    private Integer priority;

    private Integer isPopup;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer status;
}