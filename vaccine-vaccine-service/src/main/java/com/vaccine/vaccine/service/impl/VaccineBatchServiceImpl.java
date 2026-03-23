package com.vaccine.vaccine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vaccine.vaccine.entity.VaccineBatch;
import com.vaccine.vaccine.mapper.VaccineBatchMapper;
import com.vaccine.vaccine.service.VaccineBatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class VaccineBatchServiceImpl extends ServiceImpl<VaccineBatchMapper, VaccineBatch> implements VaccineBatchService {

    @Override
    public VaccineBatch createBatch(VaccineBatch batch) {
        batch.setUsedQuantity(0);
        batch.setAvailableQuantity(batch.getQuantity());
        batch.setStatus(1);
        // 生成追溯码
        if (batch.getTraceCode() == null || batch.getTraceCode().isEmpty()) {
            batch.setTraceCode(generateTraceCode(batch));
        }
        this.save(batch);
        log.info("创建疫苗批次成功: {}", batch.getBatchNumber());
        return batch;
    }

    @Override
    @Transactional
    public boolean updateUsedQuantity(Long batchId, Integer quantity) {
        VaccineBatch batch = this.getById(batchId);
        if (batch == null) {
            log.error("批次不存在: {}", batchId);
            return false;
        }
        
        int newUsedQuantity = batch.getUsedQuantity() + quantity;
        if (newUsedQuantity > batch.getQuantity()) {
            log.error("使用数量超过总库存: batchId={}, used={}, total={}", 
                batchId, newUsedQuantity, batch.getQuantity());
            return false;
        }
        
        batch.setUsedQuantity(newUsedQuantity);
        batch.setAvailableQuantity(batch.getQuantity() - newUsedQuantity);
        
        // 如果使用完，更新状态为已用完
        if (newUsedQuantity >= batch.getQuantity()) {
            batch.setStatus(3);
            log.info("批次已用完: {}", batch.getBatchNumber());
        }
        
        return this.updateById(batch);
    }

    @Override
    public Map<String, Object> getBatchPage(Integer page, Integer pageSize, Long vaccineId, Long siteId, Integer status) {
        Page<VaccineBatch> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<VaccineBatch> wrapper = new LambdaQueryWrapper<>();
        
        if (vaccineId != null) {
            wrapper.eq(VaccineBatch::getVaccineId, vaccineId);
        }
        if (siteId != null) {
            wrapper.eq(VaccineBatch::getSiteId, siteId);
        }
        if (status != null) {
            wrapper.eq(VaccineBatch::getStatus, status);
        }
        
        wrapper.orderByDesc(VaccineBatch::getCreateTime);
        
        Page<VaccineBatch> resultPage = this.page(pageParam, wrapper);
        
        Map<String, Object> data = new HashMap<>();
        data.put("records", resultPage.getRecords());
        data.put("total", resultPage.getTotal());
        data.put("current", resultPage.getCurrent());
        data.put("size", resultPage.getSize());
        data.put("pages", resultPage.getPages());
        
        return data;
    }

    @Override
    public VaccineBatch getAvailableBatch(Long vaccineId, Long siteId) {
        LambdaQueryWrapper<VaccineBatch> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VaccineBatch::getVaccineId, vaccineId);
        if (siteId != null) {
            wrapper.eq(VaccineBatch::getSiteId, siteId);
        }
        wrapper.eq(VaccineBatch::getStatus, 1);
        wrapper.gt(VaccineBatch::getAvailableQuantity, 0);
        wrapper.orderByAsc(VaccineBatch::getExpiryDate);
        wrapper.last("LIMIT 1");
        
        return this.getOne(wrapper);
    }

    @Override
    public List<VaccineBatch> getExpiringBatches(int daysBeforeExpiry) {
        LocalDate expiryDate = LocalDate.now().plusDays(daysBeforeExpiry);
        LambdaQueryWrapper<VaccineBatch> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VaccineBatch::getStatus, 1);
        wrapper.le(VaccineBatch::getExpiryDate, expiryDate);
        wrapper.ge(VaccineBatch::getExpiryDate, LocalDate.now());
        wrapper.gt(VaccineBatch::getAvailableQuantity, 0);
        wrapper.orderByAsc(VaccineBatch::getExpiryDate);
        
        return this.list(wrapper);
    }

    @Override
    public List<VaccineBatch> getExpiredBatches() {
        LambdaQueryWrapper<VaccineBatch> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VaccineBatch::getStatus, 1);
        wrapper.lt(VaccineBatch::getExpiryDate, LocalDate.now());
        
        return this.list(wrapper);
    }

    @Override
    public List<VaccineBatch> getLowStockBatches(int threshold) {
        LambdaQueryWrapper<VaccineBatch> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VaccineBatch::getStatus, 1);
        wrapper.le(VaccineBatch::getAvailableQuantity, threshold);
        wrapper.gt(VaccineBatch::getAvailableQuantity, 0);
        wrapper.orderByAsc(VaccineBatch::getAvailableQuantity);
        
        return this.list(wrapper);
    }

    @Override
    public VaccineBatch getBatchByTraceCode(String traceCode) {
        LambdaQueryWrapper<VaccineBatch> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VaccineBatch::getTraceCode, traceCode);
        return this.getOne(wrapper);
    }

    @Override
    @Transactional
    public boolean allocateBatchToSite(Long batchId, Long siteId, Integer quantity) {
        VaccineBatch batch = this.getById(batchId);
        if (batch == null) {
            log.error("批次不存在: {}", batchId);
            return false;
        }
        
        if (batch.getAvailableQuantity() < quantity) {
            log.error("可用库存不足: batchId={}, available={}, required={}", 
                batchId, batch.getAvailableQuantity(), quantity);
            return false;
        }
        
        batch.setSiteId(siteId);
        batch.setAvailableQuantity(batch.getAvailableQuantity() - quantity);
        
        return this.updateById(batch);
    }

    @Override
    @Transactional
    public int markExpiredBatches() {
        List<VaccineBatch> expiredBatches = getExpiredBatches();
        int count = 0;
        for (VaccineBatch batch : expiredBatches) {
            batch.setStatus(2);
            this.updateById(batch);
            count++;
            log.info("标记过期批次: {}", batch.getBatchNumber());
        }
        return count;
    }

    @Override
    public Map<String, Object> getBatchStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // 总批次数
        Long totalBatches = this.count();
        stats.put("totalBatches", totalBatches);
        
        // 可用批次数
        Long availableBatches = this.lambdaQuery()
            .eq(VaccineBatch::getStatus, 1)
            .gt(VaccineBatch::getAvailableQuantity, 0)
            .count();
        stats.put("availableBatches", availableBatches);
        
        // 即将过期批次数（30天内）
        Long expiringBatches = this.lambdaQuery()
            .eq(VaccineBatch::getStatus, 1)
            .le(VaccineBatch::getExpiryDate, LocalDate.now().plusDays(30))
            .gt(VaccineBatch::getAvailableQuantity, 0)
            .count();
        stats.put("expiringBatches", expiringBatches);
        
        // 库存不足批次数（少于10）
        Long lowStockBatches = this.lambdaQuery()
            .eq(VaccineBatch::getStatus, 1)
            .le(VaccineBatch::getAvailableQuantity, 10)
            .gt(VaccineBatch::getAvailableQuantity, 0)
            .count();
        stats.put("lowStockBatches", lowStockBatches);
        
        return stats;
    }

    /**
     * 生成追溯码
     */
    private String generateTraceCode(VaccineBatch batch) {
        String prefix = "VB";
        String vaccineId = String.format("%04d", batch.getVaccineId());
        String timestamp = String.valueOf(System.currentTimeMillis());
        return prefix + vaccineId + timestamp.substring(timestamp.length() - 8);
    }
}