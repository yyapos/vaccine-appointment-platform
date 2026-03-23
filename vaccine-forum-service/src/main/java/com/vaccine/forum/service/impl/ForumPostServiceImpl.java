package com.vaccine.forum.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vaccine.common.result.Result;
import com.vaccine.forum.entity.ForumPost;
import com.vaccine.forum.feign.ParentClient;
import com.vaccine.forum.mapper.ForumPostMapper;
import com.vaccine.forum.service.ForumPostService;
import com.vaccine.forum.vo.PostVO;
import com.vaccine.parent.entity.Parent;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ForumPostServiceImpl extends ServiceImpl<ForumPostMapper, ForumPost> implements ForumPostService {

    private final ParentClient parentClient;

    public ForumPostServiceImpl(ParentClient parentClient) {
        this.parentClient = parentClient;
    }

    @Override
    public List<ForumPost> getPostList(Integer isTop, String keyword, String sortBy) {
        LambdaQueryWrapper<ForumPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ForumPost::getStatus, 1);
        if (isTop != null && isTop == 1) {
            wrapper.eq(ForumPost::getIsTop, 1);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(ForumPost::getTitle, keyword)
                    .or()
                    .like(ForumPost::getContent, keyword));
        }

        // 根据排序方式排序
        if ("hot".equals(sortBy)) {
            // 最热：按点赞数降序，再按时间降序
            wrapper.orderByDesc(ForumPost::getIsTop, ForumPost::getLikeCount, ForumPost::getCreateTime);
        } else {
            // 最新（默认）：按创建时间降序
            wrapper.orderByDesc(ForumPost::getIsTop, ForumPost::getCreateTime);
        }

        return this.list(wrapper);
    }

    @Override
    public List<PostVO> getPostListVO(Integer isTop, String keyword, String sortBy, Long currentUserId) {
        // 查询帖子列表
        List<ForumPost> posts = getPostList(isTop, keyword, sortBy);

        if (posts.isEmpty()) {
            return new java.util.ArrayList<>();
        }

        // 获取所有用户ID
        List<Long> userIds = posts.stream()
                .map(ForumPost::getUserId)
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

        // 组装PostVO列表
        return posts.stream().map(post -> {
            PostVO vo = new PostVO();
            vo.setId(post.getId());
            vo.setUserId(post.getUserId());
            vo.setUserType(post.getUserType());
            vo.setTitle(post.getTitle());
            vo.setContent(post.getContent());
            vo.setImages(post.getImages());
            vo.setTags(post.getTags());
            vo.setViewCount(post.getViewCount());
            vo.setLikeCount(post.getLikeCount());
            vo.setCommentCount(post.getCommentCount());
            vo.setIsTop(post.getIsTop());
            vo.setIsEssence(post.getIsEssence());
            vo.setStatus(post.getStatus());
            vo.setCreateTime(post.getCreateTime());
            vo.setUpdateTime(post.getUpdateTime());

            // 设置用户信息
            Parent user = userMap.get(post.getUserId());
            if (user != null) {
                vo.setNickname(user.getNickname() != null ? user.getNickname() : "匿名用户");
                vo.setUserAvatar(user.getAvatar() != null ? user.getAvatar() : "");
            } else {
                vo.setNickname("匿名用户");
                vo.setUserAvatar("");
            }

            // 判断是否可以删除
            vo.setCanDelete(currentUserId != null && currentUserId.equals(post.getUserId()));

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
    public Map<String, Object> getPostPage(Integer page, Integer pageSize, String title, Integer status) {
        Page<ForumPost> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<ForumPost> wrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        if (title != null && !title.isEmpty()) {
            wrapper.like(ForumPost::getTitle, title);
        }
        if (status != null) {
            wrapper.eq(ForumPost::getStatus, status);
        }
        
        wrapper.orderByDesc(ForumPost::getIsTop, ForumPost::getCreateTime);
        
        Page<ForumPost> resultPage = this.page(pageParam, wrapper);
        
        Map<String, Object> data = new HashMap<>();
        data.put("records", resultPage.getRecords());
        data.put("total", resultPage.getTotal());
        data.put("current", resultPage.getCurrent());
        data.put("size", resultPage.getSize());
        data.put("pages", resultPage.getPages());
        
        return data;
    }

    @Override
    public void incrementViewCount(Long postId) {
        ForumPost post = this.getById(postId);
        if (post != null) {
            post.setViewCount(post.getViewCount() + 1);
            this.updateById(post);
        }
    }

    @Override
    public void incrementLikeCount(Long postId) {
        ForumPost post = this.getById(postId);
        if (post != null) {
            post.setLikeCount(post.getLikeCount() + 1);
            this.updateById(post);
        }
    }

    @Override
    public void decrementLikeCount(Long postId) {
        ForumPost post = this.getById(postId);
        if (post != null && post.getLikeCount() > 0) {
            post.setLikeCount(post.getLikeCount() - 1);
            this.updateById(post);
        }
    }

    @Override
    public void incrementCommentCount(Long postId) {
        ForumPost post = this.getById(postId);
        if (post != null) {
            post.setCommentCount(post.getCommentCount() + 1);
            this.updateById(post);
        }
    }

    @Override
    public void decrementCommentCount(Long postId) {
        ForumPost post = this.getById(postId);
        if (post != null && post.getCommentCount() > 0) {
            post.setCommentCount(post.getCommentCount() - 1);
            this.updateById(post);
        }
    }

    @Override
    public PostVO getPostDetailVO(Long postId, Long currentUserId) {
        ForumPost post = this.getById(postId);
        if (post == null) {
            return null;
        }

        PostVO vo = new PostVO();
        vo.setId(post.getId());
        vo.setUserId(post.getUserId());
        vo.setUserType(post.getUserType());
        vo.setTitle(post.getTitle());
        vo.setContent(post.getContent());
        vo.setImages(post.getImages());
        vo.setTags(post.getTags());
        vo.setViewCount(post.getViewCount());
        vo.setLikeCount(post.getLikeCount());
        vo.setCommentCount(post.getCommentCount());
        vo.setIsTop(post.getIsTop());
        vo.setIsEssence(post.getIsEssence());
        vo.setStatus(post.getStatus());
        vo.setCreateTime(post.getCreateTime());
        vo.setUpdateTime(post.getUpdateTime());

        // 获取用户信息
        try {
            Result<Parent> result = parentClient.getParentById(post.getUserId());
            if (result != null && result.getCode() == 200 && result.getData() != null) {
                Parent user = result.getData();
                vo.setNickname(user.getNickname() != null ? user.getNickname() : "匿名用户");
                vo.setUserAvatar(user.getAvatar() != null ? user.getAvatar() : "");
            } else {
                vo.setNickname("匿名用户");
                vo.setUserAvatar("");
            }
        } catch (Exception e) {
            vo.setNickname("匿名用户");
            vo.setUserAvatar("");
        }

        // 判断是否可以删除
        vo.setCanDelete(currentUserId != null && currentUserId.equals(post.getUserId()));

        return vo;
    }
}