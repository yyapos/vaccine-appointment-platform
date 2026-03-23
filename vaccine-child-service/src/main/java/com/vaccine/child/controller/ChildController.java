package com.vaccine.child.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vaccine.common.result.Result;
import com.vaccine.common.util.JwtUtil;
import com.vaccine.child.entity.Child;
import com.vaccine.child.service.ChildService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/child")
@RequiredArgsConstructor
public class ChildController {

    private final ChildService childService;
    private final JwtUtil jwtUtil;

    /**
     * 获取儿童总数（统计接口）
     */
    @GetMapping("/count")
    public Result<Long> getChildCount() {

        LambdaQueryWrapper<Child> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Child::getStatus,1)
                .eq(Child::getDeleted,0);
        long count = childService.count(queryWrapper);
        return Result.success(count);
    }

    /**
     * 分页查询儿童列表（管理员接口）
     */
    @GetMapping("/page")
    @com.vaccine.common.annotation.RequireAdmin
    public Result<Map<String, Object>> getChildPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long parentId,
            @RequestParam(required = false) String createTimeStart,
            @RequestParam(required = false) String createTimeEnd) {
        Map<String, Object> data = childService.getChildPage(page, pageSize, name, parentId, createTimeStart, createTimeEnd);
        return Result.success(data);
    }

    /**
     * 获取当前用户的儿童列表
     */
    @GetMapping("/list")
    public Result<List<Child>> getChildren(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token != null) {
            Long parentId = getUserIdFromToken(token);
            if (parentId != null) {
                List<Child> children = childService.getChildrenByParentId(parentId);
                return Result.success(children);
            }
        }
        return Result.error("未授权，请先登录");
    }

    /**
     * 获取所有儿童列表（定时任务使用，无需登录）
     */
    @GetMapping("/all")
    public Result<List<Child>> getAllChildren() {
        LambdaQueryWrapper<Child> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Child::getStatus, 1)
                .eq(Child::getDeleted, 0);
        List<Child> children = childService.list(queryWrapper);
        return Result.success(children);
    }

    /**
     * 根据ID获取儿童信息
     */
    @GetMapping("/{id}")
    public Result<Child> getChildById(@PathVariable Long id) {
        Child child = childService.getById(id);
        if (child == null) {
            return Result.error("儿童信息不存在");
        }
        return Result.success(child);
    }

    /**
     * 添加儿童
     */
    @PostMapping("/add")
    public Result<Child> addChild(@RequestHeader(value = "Authorization", required = false) String token, @RequestBody Child child) {
        if (token != null) {
            Long parentId = getUserIdFromToken(token);
            if (parentId != null) {
                child.setParentId(parentId);
                child.setStatus(1);
                // 如果前端没有传递isKeyChild，则设置为0
                if (child.getIsKeyChild() == null) {
                    child.setIsKeyChild(0);
                }
                Child added = childService.addChild(child);
                return Result.success(added);
            }
        }
        return Result.error("未授权，请先登录");
    }

    /**
     * 更新儿童信息
     */
    @PutMapping("/update")
    public Result<String> updateChild(@RequestHeader(value = "Authorization", required = false) String token, @RequestBody Child child) {
        if (token != null) {
            Long parentId = getUserIdFromToken(token);
            if (parentId != null) {
                // 验证儿童是否属于当前用户
                Child existing = childService.getById(child.getId());
                if (existing == null) {
                    return Result.error("儿童信息不存在");
                }
                if (!existing.getParentId().equals(parentId)) {
                    return Result.error("无权修改此儿童信息");
                }
                boolean success = childService.updateChild(child);
                return success ? Result.success("更新成功") : Result.error("更新失败");
            }
        }
        return Result.error("未授权，请先登录");
    }

    /**
     * 删除儿童
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteChild(@RequestHeader(value = "Authorization", required = false) String token, @PathVariable Long id) {
        if (token != null) {
            Long parentId = getUserIdFromToken(token);
            if (parentId != null) {
                // 验证儿童是否属于当前用户
                Child child = childService.getById(id);
                if (child == null) {
                    return Result.error("儿童信息不存在");
                }
                if (!child.getParentId().equals(parentId)) {
                    return Result.error("无权删除此儿童信息");
                }
                boolean success = childService.deleteChild(id);
                return success ? Result.success("删除成功") : Result.error("删除失败");
            }
        }
        return Result.error("未授权，请先登录");
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