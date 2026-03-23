package com.vaccine.forum.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vaccine.forum.entity.ForumComment;
import com.vaccine.forum.vo.CommentVO;

import java.util.List;

public interface ForumCommentService extends IService<ForumComment> {

    /**
     * 根据帖子ID获取评论列表（包含用户信息）
     */
    List<CommentVO> getCommentsByPostId(Long postId, Long currentUserId);

    /**
     * 增加评论点赞数
     */
    void incrementLikeCount(Long commentId);

    /**
     * 减少评论点赞数
     */
    void decrementLikeCount(Long commentId);
}