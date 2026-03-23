package com.vaccine.forum.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vaccine.forum.entity.ForumLike;
import com.vaccine.forum.mapper.ForumLikeMapper;
import com.vaccine.forum.service.ForumCommentService;
import com.vaccine.forum.service.ForumLikeService;
import com.vaccine.forum.service.ForumPostService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ForumLikeServiceImpl extends ServiceImpl<ForumLikeMapper, ForumLike> implements ForumLikeService {

    private final ForumPostService forumPostService;
    private final ForumCommentService forumCommentService;

    public ForumLikeServiceImpl(ForumPostService forumPostService, ForumCommentService forumCommentService) {
        this.forumPostService = forumPostService;
        this.forumCommentService = forumCommentService;
    }

    @Override
    public boolean hasLiked(Long userId, String targetType, Long targetId) {
        LambdaQueryWrapper<ForumLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ForumLike::getUserId, userId);
        wrapper.eq(ForumLike::getTargetType, targetType);
        wrapper.eq(ForumLike::getTargetId, targetId);
        return this.count(wrapper) > 0;
    }

    @Override
    @Transactional
    public boolean toggleLike(Long userId, String targetType, Long targetId) {
        if (hasLiked(userId, targetType, targetId)) {
            // 取消点赞
            LambdaQueryWrapper<ForumLike> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ForumLike::getUserId, userId);
            wrapper.eq(ForumLike::getTargetType, targetType);
            wrapper.eq(ForumLike::getTargetId, targetId);
            this.remove(wrapper);

            // 减少点赞数
            if ("post".equals(targetType)) {
                forumPostService.decrementLikeCount(targetId);
            } else if ("comment".equals(targetType)) {
                forumCommentService.decrementLikeCount(targetId);
            }
            return false;
        } else {
            // 点赞
            ForumLike like = new ForumLike();
            like.setUserId(userId);
            like.setTargetType(targetType);
            like.setTargetId(targetId);
            this.save(like);

            // 增加点赞数
            if ("post".equals(targetType)) {
                forumPostService.incrementLikeCount(targetId);
            } else if ("comment".equals(targetType)) {
                forumCommentService.incrementLikeCount(targetId);
            }
            return true;
        }
    }
}