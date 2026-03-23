package com.vaccine.forum.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vaccine.forum.entity.ForumLike;

public interface ForumLikeService extends IService<ForumLike> {

    /**
     * 检查是否已点赞
     */
    boolean hasLiked(Long userId, String targetType, Long targetId);

    /**
     * 点赞或取消点赞
     */
    boolean toggleLike(Long userId, String targetType, Long targetId);
}