package com.vaccine.appointment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vaccine.appointment.entity.Appointment;
import com.vaccine.appointment.vo.AppointmentVO;

import java.util.List;
import java.util.Map;

public interface AppointmentService extends IService<Appointment> {

    /**
     * 创建预约
     */
    com.vaccine.appointment.vo.AppointmentVO createAppointment(Appointment appointment);

    /**
     * 取消预约
     */
    boolean cancelAppointment(Long id, String reason);

    /**
     * 审核通过
     */
    boolean approveAppointment(Long id);

    /**
     * 审核拒绝
     */
    boolean rejectAppointment(Long id, String reason);

    /**
     * 完成预约
     */
    boolean completeAppointment(Long id);

    /**
     * 获取儿童的预约列表
     */
    List<AppointmentVO> getAppointmentsByChildId(Long childId);

    /**
     * 获取待审核预约列表
     */
    List<Appointment> getPendingReviewAppointments();

    /**
     * 分页查询预约列表
     */
    Map<String, Object> getAppointmentPage(Integer page, Integer pageSize, Integer status, Long childId, String childName, String vaccineName, String createTimeStart, String createTimeEnd);
}