package com.vaccine.vaccine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vaccine.vaccine.entity.Vaccine;
import com.vaccine.vaccine.mapper.VaccineMapper;
import com.vaccine.vaccine.service.VaccineService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VaccineServiceImpl extends ServiceImpl<VaccineMapper, Vaccine> implements VaccineService {

    @Override
    public List<Vaccine> getAllVaccines() {
        LambdaQueryWrapper<Vaccine> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Vaccine::getStatus, 1);
        wrapper.orderByDesc(Vaccine::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public List<Vaccine> searchByName(String name) {
        LambdaQueryWrapper<Vaccine> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Vaccine::getName, name);
        wrapper.eq(Vaccine::getStatus, 1);
        wrapper.orderByDesc(Vaccine::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public Vaccine getByCode(String code) {
        LambdaQueryWrapper<Vaccine> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Vaccine::getCode, code);
        wrapper.eq(Vaccine::getStatus, 1);
        return this.getOne(wrapper);
    }

    @Override
    public boolean updateStock(Long vaccineId, Integer quantity) {
        Vaccine vaccine = this.getById(vaccineId);
        if (vaccine == null) {
            return false;
        }
        vaccine.setStock(vaccine.getStock() + quantity);
        return this.updateById(vaccine);
    }

    @Override
    public Map<String, Object> getVaccinePage(Integer page, Integer pageSize, String name, String manufacturer, Integer status, String createTimeStart, String createTimeEnd) {
        Page<Vaccine> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Vaccine> wrapper = new LambdaQueryWrapper<>();

        // 添加查询条件
        if (name != null && !name.isEmpty()) {
            wrapper.like(Vaccine::getName, name);
        }
        if (manufacturer != null && !manufacturer.isEmpty()) {
            wrapper.like(Vaccine::getManufacturer, manufacturer);
        }
        if (status != null) {
            wrapper.eq(Vaccine::getStatus, status);
        }

        // 添加时间过滤
        if (createTimeStart != null && !createTimeStart.isEmpty()) {
            wrapper.ge(Vaccine::getCreateTime, java.time.LocalDateTime.parse(createTimeStart + "T00:00:00"));
        }
        if (createTimeEnd != null && !createTimeEnd.isEmpty()) {
            wrapper.le(Vaccine::getCreateTime, java.time.LocalDateTime.parse(createTimeEnd + "T23:59:59"));
        }

        wrapper.orderByDesc(Vaccine::getCreateTime);

        Page<Vaccine> resultPage = this.page(pageParam, wrapper);

        Map<String, Object> data = new HashMap<>();
        data.put("records", resultPage.getRecords());
        data.put("total", resultPage.getTotal());
        data.put("current", resultPage.getCurrent());
        data.put("size", resultPage.getSize());
        data.put("pages", resultPage.getPages());

        return data;
    }

    @Override
    public Map<String, Long> getStockStats() {
        Map<String, Long> stats = new HashMap<>();
    
        // 从系统设置获取阈值（暂时使用默认值）
        // 后续可以从数据库配置表读取
        int outOfStockThreshold = 10;
        int lowStockThreshold = 20;
    
        // 获取所有启用的疫苗
        LambdaQueryWrapper<Vaccine> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Vaccine::getStatus, 1);
        List<Vaccine> vaccines = this.list(wrapper);
    
        // 统计库存情况
        long sufficient = 0;  // 充足：>= lowStockThreshold
        long low = 0;         // 紧张：>= outOfStockThreshold 且 < lowStockThreshold
        long outOfStock = 0;  // 缺货：< outOfStockThreshold
    
        for (Vaccine vaccine : vaccines) {
            int stock = vaccine.getStock() != null ? vaccine.getStock() : 0;
            if (stock >= lowStockThreshold) {
                sufficient++;
            } else if (stock >= outOfStockThreshold) {
                low++;
            } else {
                outOfStock++;
            }
        }
    
        stats.put("sufficientStock", sufficient);
        stats.put("lowStock", low);
        stats.put("outOfStock", outOfStock);
    
        return stats;
    }}