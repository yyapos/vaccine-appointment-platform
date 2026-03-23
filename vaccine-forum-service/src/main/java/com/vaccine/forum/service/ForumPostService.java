package com.vaccine.forum.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vaccine.forum.entity.ForumPost;
import com.vaccine.forum.vo.PostVO;

import java.util.List;
import java.util.Map;

public interface ForumPostService extends IService<ForumPost> {

    /**
     * 获取帖子列表
     */
    List<ForumPost> getPostList(Integer isTop, String keyword, String sortBy);

    /**
     * 获取帖子列表（包含用户信息）
     */
    List<PostVO> getPostListVO(Integer isTop, String keyword, String sortBy, Long currentUserId);

    /**
     * 分页查询帖子列表
     */
    Map<String, Object> getPostPage(Integer page, Integer pageSize, String title, Integer status);

    /**
     * 获取帖子详情（包含用户信息）
     */
    PostVO getPostDetailVO(Long postId, Long currentUserId);

    /**
     * 增加浏览次数
     */
    void incrementViewCount(Long postId);

    /**
     * 增加点赞数
     */
    void incrementLikeCount(Long postId);

    /**
     * 减少点赞数
     */
    void decrementLikeCount(Long postId);

    /**
     * 增加评论数
     */
    void incrementCommentCount(Long postId);

    /**
     * 减少评论数
     */
    void decrementCommentCount(Long postId);
}