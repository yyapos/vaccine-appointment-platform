package com.vaccine.appointment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vaccine.appointment.entity.VaccinationRecord;

import java.util.List;
import java.util.Map;

public interface VaccinationRecordService extends IService<VaccinationRecord> {

    /**
     * 获取儿童的所有接种记录
     */
    List<VaccinationRecord> getRecordsByChildId(Long childId);

    /**
     * 创建预约
     */
    VaccinationRecord createAppointment(VaccinationRecord record);

    /**
     * 取消预约
     */
    boolean cancelAppointment(Long id);

    /**
     * 完成接种
     */
    boolean completeVaccination(Long id, String batchNumber, String vaccinationSite);

    /**
     * 获取待审核预约列表
     */
    List<VaccinationRecord> getPendingReviewAppointments();

    /**
     * 审核通过
     */
    boolean approveAppointment(Long id);

    /**
     * 审核拒绝
     */
    boolean rejectAppointment(Long id, String reason);

    /**
     * 获取疫苗的已预约数量
     */
    Integer getAppointmentCountByVaccine(Long vaccineId);

    /**
     * 分页查询预约
     */
    Map<String, Object> getAppointmentPage(Integer page, Integer pageSize, Integer status, Long childId);
}