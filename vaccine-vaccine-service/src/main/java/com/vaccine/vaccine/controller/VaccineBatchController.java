package com.vaccine.vaccine.controller;

import com.vaccine.common.result.Result;
import com.vaccine.vaccine.entity.VaccineBatch;
import com.vaccine.vaccine.service.VaccineBatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/vaccine-batch")
@RequiredArgsConstructor
public class VaccineBatchController {

    private final VaccineBatchService vaccineBatchService;

    /**
     * 创建疫苗批次（管理员接口）
     */
    @PostMapping("/create")
    @com.vaccine.common.annotation.RequireAdmin
    public Result<VaccineBatch> createBatch(@RequestBody VaccineBatch batch) {
        try {
            VaccineBatch created = vaccineBatchService.createBatch(batch);
            return Result.success(created);
        } catch (Exception e) {
            log.error("创建疫苗批次失败", e);
            return Result.error("创建失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询批次列表（管理员接口）
     */
    @GetMapping("/page")
    @com.vaccine.common.annotation.RequireAdmin
    public Result<Map<String, Object>> getBatchPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long vaccineId,
            @RequestParam(required = false) Long siteId,
            @RequestParam(required = false) Integer status) {
        return Result.success(vaccineBatchService.getBatchPage(page, pageSize, vaccineId, siteId, status));
    }

    /**
     * 更新批次使用数量（管理员接口）
     */
    @PutMapping("/update-quantity")
    @com.vaccine.common.annotation.RequireAdmin
    public Result<String> updateUsedQuantity(@RequestBody Map<String, Object> request) {
        Long batchId = Long.valueOf(request.get("batchId").toString());
        Integer quantity = Integer.valueOf(request.get("quantity").toString());
        boolean success = vaccineBatchService.updateUsedQuantity(batchId, quantity);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 获取即将过期的批次（管理员接口）
     */
    @GetMapping("/expiring")
    @com.vaccine.common.annotation.RequireAdmin
    public Result<List<VaccineBatch>> getExpiringBatches(
            @RequestParam(defaultValue = "30") Integer daysBeforeExpiry) {
        try {
            List<VaccineBatch> batches = vaccineBatchService.getExpiringBatches(daysBeforeExpiry);
            return Result.success(batches);
        } catch (Exception e) {
            log.error("获取即将过期批次失败", e);
            return Result.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 获取已过期的批次（管理员接口）
     */
    @GetMapping("/expired")
    @com.vaccine.common.annotation.RequireAdmin
    public Result<List<VaccineBatch>> getExpiredBatches() {
        try {
            List<VaccineBatch> batches = vaccineBatchService.getExpiredBatches();
            return Result.success(batches);
        } catch (Exception e) {
            log.error("获取已过期批次失败", e);
            return Result.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 获取库存不足的批次（管理员接口）
     */
    @GetMapping("/low-stock")
    @com.vaccine.common.annotation.RequireAdmin
    public Result<List<VaccineBatch>> getLowStockBatches(
            @RequestParam(defaultValue = "10") Integer threshold) {
        try {
            List<VaccineBatch> batches = vaccineBatchService.getLowStockBatches(threshold);
            return Result.success(batches);
        } catch (Exception e) {
            log.error("获取库存不足批次失败", e);
            return Result.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 根据追溯码查询批次（管理员接口）
     */
    @GetMapping("/trace/{traceCode}")
    @com.vaccine.common.annotation.RequireAdmin
    public Result<VaccineBatch> getBatchByTraceCode(@PathVariable String traceCode) {
        try {
            VaccineBatch batch = vaccineBatchService.getBatchByTraceCode(traceCode);
            if (batch == null) {
                return Result.error("批次不存在");
            }
            return Result.success(batch);
        } catch (Exception e) {
            log.error("根据追溯码查询批次失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 分配批次到站点（管理员接口）
     */
    @PostMapping("/allocate")
    @com.vaccine.common.annotation.RequireAdmin
    public Result<String> allocateBatchToSite(@RequestBody Map<String, Object> request) {
        try {
            Long batchId = Long.valueOf(request.get("batchId").toString());
            Long siteId = Long.valueOf(request.get("siteId").toString());
            Integer quantity = Integer.valueOf(request.get("quantity").toString());
            
            boolean success = vaccineBatchService.allocateBatchToSite(batchId, siteId, quantity);
            return success ? Result.success("分配成功") : Result.error("分配失败");
        } catch (Exception e) {
            log.error("分配批次失败", e);
            return Result.error("分配失败: " + e.getMessage());
        }
    }

    /**
     * 标记过期批次（管理员接口）
     */
    @PostMapping("/mark-expired")
    @com.vaccine.common.annotation.RequireAdmin
    public Result<Map<String, Object>> markExpiredBatches() {
        try {
            int count = vaccineBatchService.markExpiredBatches();
            Map<String, Object> result = Map.of("count", count);
            return Result.success(result);
        } catch (Exception e) {
            log.error("标记过期批次失败", e);
            return Result.error("操作失败: " + e.getMessage());
        }
    }

    /**
     * 获取批次统计信息（管理员接口）
     */
    @GetMapping("/statistics")
    @com.vaccine.common.annotation.RequireAdmin
    public Result<Map<String, Object>> getBatchStatistics() {
        try {
            Map<String, Object> stats = vaccineBatchService.getBatchStatistics();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取批次统计失败", e);
            return Result.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取批次详情（管理员接口）
     */
    @GetMapping("/{id}")
    @com.vaccine.common.annotation.RequireAdmin
    public Result<VaccineBatch> getBatchById(@PathVariable Long id) {
        try {
            VaccineBatch batch = vaccineBatchService.getById(id);
            if (batch == null) {
                return Result.error("批次不存在");
            }
            return Result.success(batch);
        } catch (Exception e) {
            log.error("获取批次详情失败", e);
            return Result.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 更新批次信息（管理员接口）
     */
    @PutMapping("/update")
    @com.vaccine.common.annotation.RequireAdmin
    public Result<String> updateBatch(@RequestBody VaccineBatch batch) {
        try {
            boolean success = vaccineBatchService.updateById(batch);
            return success ? Result.success("更新成功") : Result.error("更新失败");
        } catch (Exception e) {
            log.error("更新批次失败", e);
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除批次（管理员接口）
     */
    @DeleteMapping("/{id}")
    @com.vaccine.common.annotation.RequireAdmin
    public Result<String> deleteBatch(@PathVariable Long id) {
        try {
            boolean success = vaccineBatchService.removeById(id);
            return success ? Result.success("删除成功") : Result.error("删除失败");
        } catch (Exception e) {
            log.error("删除批次失败", e);
            return Result.error("删除失败: " + e.getMessage());
        }
    }
}