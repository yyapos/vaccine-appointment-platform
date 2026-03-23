package com.vaccine.appointment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vaccine.appointment.entity.VaccinationRecord;
import com.vaccine.appointment.feign.ChildClient;
import com.vaccine.appointment.feign.VaccineClient;
import com.vaccine.appointment.mapper.VaccinationRecordMapper;
import com.vaccine.appointment.service.VaccinationRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VaccinationRecordServiceImpl extends ServiceImpl<VaccinationRecordMapper, VaccinationRecord> implements VaccinationRecordService {

    private final ChildClient childClient;
    private final VaccineClient vaccineClient;

    public VaccinationRecordServiceImpl(ChildClient childClient, VaccineClient vaccineClient) {
        this.childClient = childClient;
        this.vaccineClient = vaccineClient;
    }

    @Override
    public List<VaccinationRecord> getRecordsByChildId(Long childId) {
        LambdaQueryWrapper<VaccinationRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VaccinationRecord::getChildId, childId);
        wrapper.orderByDesc(VaccinationRecord::getScheduledDate);
        return this.list(wrapper);
    }

    @Override
    @Transactional
    public VaccinationRecord createAppointment(VaccinationRecord record) {
        // 验证儿童是否存在
        try {
            childClient.getChildById(record.getChildId());
        } catch (Exception e) {
            throw new RuntimeException("儿童信息不存在");
        }
        // 验证疫苗是否存在
        try {
            vaccineClient.getVaccineById(record.getVaccineId());
        } catch (Exception e) {
            throw new RuntimeException("疫苗信息不存在");
        }
        // 如果没有设置状态，默认为待审核
        if (record.getStatus() == null) {
            record.setStatus(3);
        }
        this.save(record);
        return record;
    }

    @Override
    @Transactional
    public boolean cancelAppointment(Long id) {
        VaccinationRecord record = this.getById(id);
        if (record == null) {
            return false;
        }
        if (record.getStatus() == 1) {
            throw new RuntimeException("已完成接种的预约不能取消");
        }
        record.setStatus(2);
        return this.updateById(record);
    }

    @Override
    @Transactional
    public boolean completeVaccination(Long id, String batchNumber, String vaccinationSite) {
        VaccinationRecord record = this.getById(id);
        if (record == null) {
            return false;
        }
        if (record.getStatus() == 1) {
            return false;
        }
        if (record.getStatus() == 2) {
            throw new RuntimeException("已取消的预约不能完成接种");
        }
        record.setStatus(1);
        record.setVaccinatedDate(LocalDateTime.now());
        record.setBatchNumber(batchNumber);
        record.setVaccinationSite(vaccinationSite);
        return this.updateById(record);
    }

    @Override
    public List<VaccinationRecord> getPendingReviewAppointments() {
        LambdaQueryWrapper<VaccinationRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VaccinationRecord::getStatus, 3); // 待审核
        wrapper.orderByAsc(VaccinationRecord::getScheduledDate);
        return this.list(wrapper);
    }

    @Override
    @Transactional
    public boolean approveAppointment(Long id) {
        VaccinationRecord record = this.getById(id);
        if (record == null) {
            return false;
        }
        if (record.getStatus() != 3) {
            throw new RuntimeException("只能审核待审核的预约");
        }
        record.setStatus(0); // 审核通过，改为未接种状态
        return this.updateById(record);
    }

    @Override
    @Transactional
    public boolean rejectAppointment(Long id, String reason) {
        VaccinationRecord record = this.getById(id);
        if (record == null) {
            return false;
        }
        if (record.getStatus() != 3) {
            throw new RuntimeException("只能审核待审核的预约");
        }
        record.setStatus(2); // 审核拒绝，改为已取消状态
        record.setNotes(reason); // 记录拒绝原因
        return this.updateById(record);
    }

    @Override
    public Integer getAppointmentCountByVaccine(Long vaccineId) {
        LambdaQueryWrapper<VaccinationRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VaccinationRecord::getVaccineId, vaccineId);
        wrapper.eq(VaccinationRecord::getStatus, 0);
        return Math.toIntExact(this.count(wrapper));
    }

    @Override
    public Map<String, Object> getAppointmentPage(Integer page, Integer pageSize, Integer status, Long childId) {
        Page<VaccinationRecord> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<VaccinationRecord> wrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        if (status != null) {
            wrapper.eq(VaccinationRecord::getStatus, status);
        }
        if (childId != null) {
            wrapper.eq(VaccinationRecord::getChildId, childId);
        }
        
        wrapper.orderByDesc(VaccinationRecord::getScheduledDate);
        
        Page<VaccinationRecord> resultPage = this.page(pageParam, wrapper);
        
        Map<String, Object> data = new HashMap<>();
        data.put("records", resultPage.getRecords());
        data.put("total", resultPage.getTotal());
        data.put("current", resultPage.getCurrent());
        data.put("size", resultPage.getSize());
        data.put("pages", resultPage.getPages());
        
        return data;
    }
}