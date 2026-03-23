package com.vaccine.vaccine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vaccine.vaccine.entity.Vaccine;

import java.util.List;
import java.util.Map;

public interface VaccineService extends IService<Vaccine> {

    /**
     * 获取所有疫苗列表
     */
    List<Vaccine> getAllVaccines();

    /**
     * 根据名称搜索疫苗
     */
    List<Vaccine> searchByName(String name);

    /**
     * 根据编码搜索疫苗
     */
    Vaccine getByCode(String code);

    /**
     * 更新疫苗库存
     */
    boolean updateStock(Long vaccineId, Integer quantity);

    /**
     * 分页查询疫苗
     */
    Map<String, Object> getVaccinePage(Integer page, Integer pageSize, String name, String manufacturer, Integer status, String createTimeStart, String createTimeEnd);

    /**
     * 获取疫苗库存统计
     */
    Map<String, Long> getStockStats();
}