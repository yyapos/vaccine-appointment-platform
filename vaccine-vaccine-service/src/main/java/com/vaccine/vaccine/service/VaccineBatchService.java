package com.vaccine.vaccine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vaccine.vaccine.entity.VaccineBatch;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface VaccineBatchService extends IService<VaccineBatch> {

    /**
     * 创建疫苗批次
     */
    VaccineBatch createBatch(VaccineBatch batch);

    /**
     * 更新批次使用数量
     */
    boolean updateUsedQuantity(Long batchId, Integer quantity);

    /**
     * 分页查询批次列表
     */
    Map<String, Object> getBatchPage(Integer page, Integer pageSize, Long vaccineId, Long siteId, Integer status);

    /**
     * 获取疫苗的可用批次
     */
    VaccineBatch getAvailableBatch(Long vaccineId, Long siteId);

    /**
     * 检查即将过期的批次
     */
    List<VaccineBatch> getExpiringBatches(int daysBeforeExpiry);

    /**
     * 检查已过期的批次
     */
    List<VaccineBatch> getExpiredBatches();

    /**
     * 检查库存不足的批次
     */
    List<VaccineBatch> getLowStockBatches(int threshold);

    /**
     * 根据追溯码查询批次
     */
    VaccineBatch getBatchByTraceCode(String traceCode);

    /**
     * 分配批次到站点
     */
    boolean allocateBatchToSite(Long batchId, Long siteId, Integer quantity);

    /**
     * 自动标记过期批次
     */
    int markExpiredBatches();

    /**
     * 获取批次统计信息
     */
    Map<String, Object> getBatchStatistics();
}