package com.vaccine.forum.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.vaccine.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("forum_post")
public class ForumPost extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Integer userType;  // 用户类型 1:家长

    private String title;

    private String content;

    private String images;

    private String tags;

    private Integer viewCount;

    private Integer likeCount;

    private Integer commentCount;

    private Integer isTop;

    private Integer isEssence;

    private Integer status;
}