package com.vaccine.forum.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vaccine.common.result.Result;
import com.vaccine.forum.entity.ForumComment;
import com.vaccine.forum.feign.ParentClient;
import com.vaccine.forum.mapper.ForumCommentMapper;
import com.vaccine.forum.service.ForumCommentService;
import com.vaccine.forum.vo.CommentVO;
import com.vaccine.parent.entity.Parent;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ForumCommentServiceImpl extends ServiceImpl<ForumCommentMapper, ForumComment> implements ForumCommentService {

    private final ParentClient parentClient;

    public ForumCommentServiceImpl(ParentClient parentClient) {
        this.parentClient = parentClient;
    }

    @Override
    public List<CommentVO> getCommentsByPostId(Long postId, Long currentUserId) {
        // 查询评论列表
        LambdaQueryWrapper<ForumComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ForumComment::getPostId, postId);
        wrapper.eq(ForumComment::getStatus, 1);
        wrapper.orderByAsc(ForumComment::getCreateTime);
        List<ForumComment> comments = this.list(wrapper);

        if (comments.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取所有用户ID
        List<Long> userIds = comments.stream()
                .map(ForumComment::getUserId)
                .distinct()
                .collect(Collectors.toList());

        // 批量获取用户信息
        Map<Long, Parent> userMap = userIds.stream()
                .collect(Collectors.toMap(
                        userId -> userId,
                        userId -> {
                            try {
                                Result<Parent> result = parentClient.getParentById(userId);
                                if (result != null && result.getCode() == 200 && result.getData() != null) {
                                    return result.getData();
                                }
                            } catch (Exception e) {
                                // 忽略异常，返回默认值
                            }
                            return createDefaultParent(userId);
                        }
                ));

        // 组装CommentVO
        return comments.stream().map(comment -> {
            CommentVO vo = new CommentVO();
            vo.setId(comment.getId());
            vo.setPostId(comment.getPostId());
            vo.setUserId(comment.getUserId());
            vo.setParentId(comment.getParentId());
            vo.setReplyToUserId(comment.getReplyToUserId());
            vo.setContent(comment.getContent());
            vo.setLikeCount(comment.getLikeCount() != null ? comment.getLikeCount() : 0);
            vo.setCreateTime(comment.getCreateTime());

            // 设置用户信息
            Parent user = userMap.get(comment.getUserId());
            if (user != null) {
                vo.setNickname(user.getNickname() != null ? user.getNickname() : "匿名用户");
                vo.setUserAvatar(user.getAvatar() != null ? user.getAvatar() : "");
            } else {
                vo.setNickname("匿名用户");
                vo.setUserAvatar("");
            }

            // 判断是否可以删除
            vo.setCanDelete(currentUserId != null && currentUserId.equals(comment.getUserId()));

            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 创建默认家长信息
     */
    private Parent createDefaultParent(Long userId) {
        Parent parent = new Parent();
        parent.setId(userId);
        parent.setNickname("匿名用户");
        parent.setAvatar("");
        return parent;
    }

    @Override
    public void incrementLikeCount(Long commentId) {
        ForumComment comment = this.getById(commentId);
        if (comment != null) {
            comment.setLikeCount(comment.getLikeCount() + 1);
            this.updateById(comment);
        }
    }

    @Override
    public void decrementLikeCount(Long commentId) {
        ForumComment comment = this.getById(commentId);
        if (comment != null && comment.getLikeCount() > 0) {
            comment.setLikeCount(comment.getLikeCount() - 1);
            this.updateById(comment);
        }
    }
}