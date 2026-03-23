package com.vaccine.child.controller;

import com.vaccine.child.entity.health.ChildHealthRecord;
import com.vaccine.child.entity.health.ChildGrowthRecord;
import com.vaccine.child.entity.health.VaccinationContraindication;
import com.vaccine.common.result.Result;
import com.vaccine.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/child-health")
@RequiredArgsConstructor
public class ChildHealthController {

    private final JwtUtil jwtUtil;

    /**
     * 添加健康档案
     */
    @PostMapping("/record/add")
    public Result<ChildHealthRecord> addHealthRecord(@RequestHeader("Authorization") String token,
                                                     @RequestBody ChildHealthRecord record) {
        try {
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return Result.error("未授权，请先登录");
            }
            
            record.setCreateTime(java.time.LocalDateTime.now());
            record.setUpdateTime(java.time.LocalDateTime.now());
            record.setDeleted(0);
            
            // 这里应该调用服务保存数据
            // 暂时返回成功
            
            return Result.success(record);
        } catch (Exception e) {
            log.error("添加健康档案失败", e);
            return Result.error("添加失败: " + e.getMessage());
        }
    }

    /**
     * 获取儿童健康档案列表
     */
    @GetMapping("/record/list/{childId}")
    public Result<List<ChildHealthRecord>> getHealthRecords(@RequestHeader("Authorization") String token,
                                                            @PathVariable Long childId) {
        try {
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return Result.error("未授权，请先登录");
            }
            
            // 这里应该调用服务查询数据
            // 暂时返回空列表
            
            return Result.success(List.of());
        } catch (Exception e) {
            log.error("获取健康档案失败", e);
            return Result.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 添加生长发育记录
     */
    @PostMapping("/growth/add")
    public Result<ChildGrowthRecord> addGrowthRecord(@RequestHeader("Authorization") String token,
                                                     @RequestBody ChildGrowthRecord record) {
        try {
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return Result.error("未授权，请先登录");
            }
            
            record.setCreateTime(java.time.LocalDateTime.now());
            record.setUpdateTime(java.time.LocalDateTime.now());
            record.setDeleted(0);
            
            // 这里应该调用服务保存数据
            // 暂时返回成功
            
            return Result.success(record);
        } catch (Exception e) {
            log.error("添加生长发育记录失败", e);
            return Result.error("添加失败: " + e.getMessage());
        }
    }

    /**
     * 获取儿童生长发育记录列表
     */
    @GetMapping("/growth/list/{childId}")
    public Result<List<ChildGrowthRecord>> getGrowthRecords(@RequestHeader("Authorization") String token,
                                                            @PathVariable Long childId) {
        try {
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return Result.error("未授权，请先登录");
            }
            
            // 这里应该调用服务查询数据
            // 暂时返回空列表
            
            return Result.success(List.of());
        } catch (Exception e) {
            log.error("获取生长发育记录失败", e);
            return Result.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 添加接种禁忌
     */
    @PostMapping("/contraindication/add")
    public Result<VaccinationContraindication> addContraindication(@RequestHeader("Authorization") String token,
                                                                   @RequestBody VaccinationContraindication contraindication) {
        try {
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return Result.error("未授权，请先登录");
            }
            
            contraindication.setCreateTime(java.time.LocalDateTime.now());
            contraindication.setUpdateTime(java.time.LocalDateTime.now());
            contraindication.setDeleted(0);
            contraindication.setIsActive(1);
            
            // 这里应该调用服务保存数据
            // 暂时返回成功
            
            return Result.success(contraindication);
        } catch (Exception e) {
            log.error("添加接种禁忌失败", e);
            return Result.error("添加失败: " + e.getMessage());
        }
    }

    /**
     * 获取儿童接种禁忌列表
     */
    @GetMapping("/contraindication/list/{childId}")
    public Result<List<VaccinationContraindication>> getContraindications(@RequestHeader("Authorization") String token,
                                                                          @PathVariable Long childId) {
        try {
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return Result.error("未授权，请先登录");
            }
            
            // 这里应该调用服务查询数据
            // 暂时返回空列表
            
            return Result.success(List.of());
        } catch (Exception e) {
            log.error("获取接种禁忌失败", e);
            return Result.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 检查儿童是否有接种禁忌
     */
    @GetMapping("/contraindication/check/{childId}/{vaccineId}")
    public Result<Map<String, Object>> checkContraindication(@RequestHeader("Authorization") String token,
                                                              @PathVariable Long childId,
                                                              @PathVariable Long vaccineId) {
        try {
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return Result.error("未授权，请先登录");
            }
            
            // 这里应该调用服务检查禁忌
            // 暂时返回无禁忌
            
            Map<String, Object> result = Map.of(
                "hasContraindication", false,
                "contraindications", List.of()
            );
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("检查接种禁忌失败", e);
            return Result.error("检查失败: " + e.getMessage());
        }
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