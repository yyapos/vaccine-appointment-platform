package com.vaccine.forum.controller;

import com.vaccine.common.result.Result;
import com.vaccine.common.util.JwtUtil;
import com.vaccine.forum.entity.*;
import com.vaccine.forum.service.*;
import com.vaccine.forum.vo.CommentVO;
import com.vaccine.forum.vo.PostVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/forum")
@RequiredArgsConstructor
public class ForumController {
    @Autowired
    private  ForumPostService forumPostService;
    @Autowired
    private ForumCommentService forumCommentService;
    @Autowired
    private ForumLikeService forumLikeService;
    @Autowired
    private ForumAnnouncementService forumAnnouncementService;
    @Autowired
    private JwtUtil jwtUtil;

    // ========================================
    // 帖子管理接口
    // ========================================

    /**
     * 获取帖子列表
     */
    @GetMapping("/posts")
    public Result<List<PostVO>> getPostList(
                                               @RequestParam(required = false) Integer isTop,
                                               @RequestParam(required = false) String keyword,
                                               @RequestParam(required = false, defaultValue = "latest") String sortBy,
                                               @RequestHeader(value = "Authorization", required = false) String token) {
        Long currentUserId = getUserIdFromToken(token);
        List<PostVO> posts = forumPostService.getPostListVO(isTop, keyword, sortBy, currentUserId);
        return Result.success(posts);
    }

    /**
     * 分页查询帖子列表（管理员接口）
     */
    @GetMapping("/posts/page")
    @com.vaccine.common.annotation.RequireAdmin
    public Result<Map<String, Object>> getPostPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer status) {
        Map<String, Object> data = forumPostService.getPostPage(page, pageSize, title, status);
        return Result.success(data);
    }

    /**
     * 获取帖子详情
     */
    @GetMapping("/posts/{id}")
    public Result<PostVO> getPostDetail(@PathVariable Long id,
                                        @RequestHeader(value = "Authorization", required = false) String token) {
        Long currentUserId = getUserIdFromToken(token);
        PostVO post = forumPostService.getPostDetailVO(id, currentUserId);
        if (post == null) {
            return Result.error("帖子不存在");
        }
        // 增加浏览次数
        forumPostService.incrementViewCount(id);
        return Result.success(post);
    }

    /**
     * 发布帖子
     */
    @PostMapping("/posts")
    public Result<ForumPost> createPost(@RequestHeader("Authorization") String token,
                                       @RequestBody ForumPost post) {
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error("未授权，请先登录");
        }
        post.setUserId(userId);
        post.setUserType(1);  // 设置用户类型为家长
        post.setViewCount(0);
        post.setLikeCount(0);
        post.setCommentCount(0);
        if (post.getIsTop() == null) {
            post.setIsTop(0);
        }
        if (post.getIsEssence() == null) {
            post.setIsEssence(0);
        }
        if (post.getStatus() == null) {
            post.setStatus(1);
        }
        forumPostService.save(post);
        return Result.success(post);
    }

    /**
     * 更新帖子
     */
    @PutMapping("/posts/{id}")
    public Result<String> updatePost(@RequestHeader("Authorization") String token,
                                     @PathVariable Long id,
                                     @RequestBody ForumPost post) {
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error("未授权，请先登录");
        }
        ForumPost existingPost = forumPostService.getById(id);
        if (existingPost == null) {
            return Result.error("帖子不存在");
        }
        if (!existingPost.getUserId().equals(userId)) {
            return Result.error("无权修改他人的帖子");
        }
        post.setId(id);
        forumPostService.updateById(post);
        return Result.success("更新成功");
    }

    /**
     * 删除帖子
     */
    @DeleteMapping("/posts/{id}")
    public Result<String> deletePost(@RequestHeader("Authorization") String token,
                                     @PathVariable Long id) {
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error("未授权，请先登录");
        }
        ForumPost post = forumPostService.getById(id);
        if (post == null) {
            return Result.error("帖子不存在");
        }
        if (!post.getUserId().equals(userId)) {
            return Result.error("无权删除他人的帖子");
        }
        forumPostService.removeById(id);
        return Result.success("删除成功");
    }

    // ========================================
    // 评论管理接口
    // ========================================

    /**
     * 获取帖子的评论列表
     */
    @GetMapping("/posts/{postId}/comments")
    public Result<List<CommentVO>> getComments(@PathVariable Long postId,
                                               @RequestHeader(value = "Authorization", required = false) String token) {
        Long currentUserId = getUserIdFromToken(token);
        List<CommentVO> comments = forumCommentService.getCommentsByPostId(postId, currentUserId);
        return Result.success(comments);
    }

    /**
     * 发表评论
     */
    @PostMapping("/comments")
    public Result<ForumComment> createComment(@RequestHeader("Authorization") String token,
                                             @RequestBody ForumComment comment) {
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error("未授权，请先登录");
        }
        comment.setUserId(userId);
        if (comment.getParentId() == null) {
            comment.setParentId(0L);
        }
        if (comment.getLikeCount() == null) {
            comment.setLikeCount(0);
        }
        if (comment.getStatus() == null) {
            comment.setStatus(1);
        }
        forumCommentService.save(comment);
        // 增加帖子的评论数
        forumPostService.incrementCommentCount(comment.getPostId());
        return Result.success(comment);
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/comments/{id}")
    public Result<String> deleteComment(@RequestHeader("Authorization") String token,
                                       @PathVariable Long id) {
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error("未授权，请先登录");
        }
        ForumComment comment = forumCommentService.getById(id);
        if (comment == null) {
            return Result.error("评论不存在");
        }
        if (!comment.getUserId().equals(userId)) {
            return Result.error("无权删除他人的评论");
        }
        forumCommentService.removeById(id);
        // 减少帖子的评论数
        forumPostService.decrementCommentCount(comment.getPostId());
        return Result.success("删除成功");
    }

    // ========================================
    // 点赞接口
    // ========================================

    /**
     * 点赞/取消点赞
     */
    @PostMapping("/like")
    public Result<Map<String, Object>> toggleLike(@RequestHeader("Authorization") String token,
                                                 @RequestBody Map<String, Object> request) {
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error("未授权，请先登录");
        }
        String targetType = (String) request.get("targetType");
        Long targetId = Long.valueOf(request.get("targetId").toString());

        boolean liked = forumLikeService.toggleLike(userId, targetType, targetId);

        Map<String, Object> result = new HashMap<>();
        result.put("liked", liked);
        return Result.success(result);
    }

    /**
     * 检查是否已点赞
     */
    @GetMapping("/like/check")
    public Result<Map<String, Object>> checkLike(@RequestHeader("Authorization") String token,
                                               @RequestParam String targetType,
                                               @RequestParam Long targetId) {
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error("未授权，请先登录");
        }
        boolean liked = forumLikeService.hasLiked(userId, targetType, targetId);

        Map<String, Object> result = new HashMap<>();
        result.put("liked", liked);
        return Result.success(result);
    }

    // ========================================
    // 公告接口
    // ========================================

    /**
     * 获取启用的公告列表
     */
    @GetMapping("/announcements")
    public Result<List<ForumAnnouncement>> getAnnouncements() {
        List<ForumAnnouncement> announcements = forumAnnouncementService.getActiveAnnouncements();
        return Result.success(announcements);
    }

    /**
     * 获取需要弹窗显示的公告
     */
    @GetMapping("/announcements/popup")
    public Result<List<ForumAnnouncement>> getPopupAnnouncements() {
        List<ForumAnnouncement> announcements = forumAnnouncementService.getPopupAnnouncements();
        return Result.success(announcements);
    }

    /**
     * 创建公告（管理员专用）
     */
    @PostMapping("/announcements")
    @com.vaccine.common.annotation.RequireAdmin
    public Result<ForumAnnouncement> createAnnouncement(@RequestBody ForumAnnouncement announcement) {
        if (announcement.getPriority() == null) {
            announcement.setPriority(0);
        }
        if (announcement.getIsPopup() == null) {
            announcement.setIsPopup(0);
        }
        if (announcement.getStatus() == null) {
            announcement.setStatus(1);
        }
        forumAnnouncementService.save(announcement);
        return Result.success(announcement);
    }

    /**
     * 更新公告（管理员专用）
     */
    @PutMapping("/announcements/{id}")
    @com.vaccine.common.annotation.RequireAdmin
    public Result<String> updateAnnouncement(@PathVariable Long id,
                                             @RequestBody ForumAnnouncement announcement) {
        announcement.setId(id);
        forumAnnouncementService.updateById(announcement);
        return Result.success("更新成功");
    }

    /**
     * 删除公告（管理员专用）
     */
    @DeleteMapping("/announcements/{id}")
    @com.vaccine.common.annotation.RequireAdmin
    public Result<String> deleteAnnouncement(@PathVariable Long id) {
        forumAnnouncementService.removeById(id);
        return Result.success("删除成功");
    }

    /**
     * 从 Token 中获取用户 ID
     */
    private Long getUserIdFromToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }
        String jwtToken = token.substring(7);
        try {
            return jwtUtil.getUserIdFromToken(jwtToken);
        } catch (Exception e) {
            return null;
        }
    }
}