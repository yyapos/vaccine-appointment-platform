package com.vaccine.vaccine.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vaccine.common.result.Result;
import com.vaccine.vaccine.entity.Vaccine;
import com.vaccine.vaccine.service.VaccineService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/vaccine")
@RequiredArgsConstructor
public class VaccineController {

    private final VaccineService vaccineService;
    /*库存预警*/
    @GetMapping("/stock-warning")
    public Result<Map<String, Long>> getStockWarning(@RequestParam("lowStockThreshold") long lowStockThreshold,
                                                       @RequestParam("outOfStockThreshold") long outOfStockThreshold) {
            LambdaQueryWrapper<Vaccine> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Vaccine::getDeleted, 0)
                    .gt(Vaccine::getStock, lowStockThreshold);
        long sufficient  = vaccineService.count(queryWrapper);
        LambdaQueryWrapper<Vaccine> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(Vaccine::getDeleted, 0)
                .gt(Vaccine::getStock, outOfStockThreshold)
                .le(Vaccine::getStock, lowStockThreshold);
        long lowCount = vaccineService.count(queryWrapper1);
        LambdaQueryWrapper<Vaccine> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.eq(Vaccine::getDeleted, 0)
                .le(Vaccine::getStock, outOfStockThreshold);
                long outOfStock = vaccineService.count(queryWrapper2);
                Map<String, Long> result = new HashMap<>();
                result.put("sufficientStock", sufficient);
                result.put("lowStock", lowCount);
                result.put("outOfStock", outOfStock);
                return Result.success(result);
    }
    /**
     * 获取疫苗总数（统计接口）
     */
    @GetMapping("/count")
    public Result<Long> getVaccineCount() {
        LambdaQueryWrapper<Vaccine> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Vaccine::getDeleted, 0).eq(Vaccine::getStatus,1);
        long count = vaccineService.count(queryWrapper);
        return Result.success(count);
    }

    /**
     * 获取疫苗库存统计（统计接口）
     */
    @GetMapping("/stock-stats")
    public Result<Map<String, Long>> getStockStats() {
        return Result.success(vaccineService.getStockStats());
    }

    /**
     * 获取所有疫苗列表
     */
    @GetMapping("/list")
    public Result<List<Vaccine>> getAllVaccines() {
        List<Vaccine> vaccines = vaccineService.getAllVaccines();
        return Result.success(vaccines);
    }

    /**
     * 分页查询疫苗列表（管理员接口）
     */
    @GetMapping("/page")
    @com.vaccine.common.annotation.RequireAdmin
    public Result<Map<String, Object>> getVaccinePage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String manufacturer,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String createTimeStart,
            @RequestParam(required = false) String createTimeEnd) {
        return Result.success(vaccineService.getVaccinePage(page, pageSize, name, manufacturer, status, createTimeStart, createTimeEnd));
    }

    /**
     * 根据ID获取疫苗信息
     */
    @GetMapping("/{id}")
    public Result<Vaccine> getVaccineById(@PathVariable Long id) {
        Vaccine vaccine = vaccineService.getById(id);
        if (vaccine == null) {
            return Result.error("疫苗信息不存在");
        }
        return Result.success(vaccine);
    }

    /**
     * 根据编码获取疫苗信息
     */
    @GetMapping("/code/{code}")
    public Result<Vaccine> getVaccineByCode(@PathVariable String code) {
        Vaccine vaccine = vaccineService.getByCode(code);
        if (vaccine == null) {
            return Result.error("疫苗信息不存在");
        }
        return Result.success(vaccine);
    }

    /**
     * 搜索疫苗（管理员接口）
     */
    @GetMapping("/search")
    @com.vaccine.common.annotation.RequireAdmin
    public Result<List<Vaccine>> searchVaccines(@RequestParam String name) {
        List<Vaccine> vaccines = vaccineService.searchByName(name);
        return Result.success(vaccines);
    }

    /**
     * 添加疫苗（管理员接口）
     */
    @PostMapping("/add")
    @com.vaccine.common.annotation.RequireAdmin
    public Result<Vaccine> addVaccine(@RequestBody Vaccine vaccine) {
        vaccine.setStatus(1);
        vaccine.setStock(0);
        Vaccine added = vaccineService.save(vaccine) ? vaccine : null;
        return added != null ? Result.success(added) : Result.error("添加失败");
    }

    /**
     * 更新疫苗信息（管理员接口）
     */
    @PutMapping("/update")
    @com.vaccine.common.annotation.RequireAdmin
    public Result<String> updateVaccine(@RequestBody Vaccine vaccine) {
        boolean success = vaccineService.updateById(vaccine);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 更新疫苗库存（管理员接口）
     */
    @PutMapping("/stock")
    @com.vaccine.common.annotation.RequireAdmin
    public Result<String> updateStock(@RequestBody Map<String, Object> request) {
        Long vaccineId = Long.valueOf(request.get("vaccineId").toString());
        Integer quantity = Integer.valueOf(request.get("quantity").toString());
        boolean success = vaccineService.updateStock(vaccineId, quantity);
        return success ? Result.success("库存更新成功") : Result.error("库存更新失败");
    }

    /**
     * 删除疫苗（管理员接口）
     */
    @DeleteMapping("/{id}")
    @com.vaccine.common.annotation.RequireAdmin
    public Result<String> deleteVaccine(@PathVariable Long id) {
        boolean success = vaccineService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}