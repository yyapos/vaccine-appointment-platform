package com.vaccine.notify.feign;

import com.vaccine.appointment.entity.Appointment;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 预约服务Feign客户端
 */
@FeignClient(name = "vaccine-appointment-service", path = "/appointment/internal")
public interface AppointmentClient {

    /**
     * 根据条件查询预约列表
     */
    @GetMapping("/list")
    List<Appointment> listAppointments(
        @RequestParam(value = "status", required = false) Integer status,
        @RequestParam(value = "childId", required = false) Long childId,
        @RequestParam(value = "vaccineId", required = false) Long vaccineId,
        @RequestParam(value = "appointmentDate", required = false) String appointmentDate
    );

    /**
     * 统计符合条件的预约数量
     */
    @PostMapping("/count")
    Long countAppointments(@RequestBody AppointmentQueryRequest request);

    /**
     * 预约查询请求对象
     */
    class AppointmentQueryRequest {
        private Integer status;
        private Long childId;
        private Long vaccineId;

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public Long getChildId() {
            return childId;
        }

        public void setChildId(Long childId) {
            this.childId = childId;
        }

        public Long getVaccineId() {
            return vaccineId;
        }

        public void setVaccineId(Long vaccineId) {
            this.vaccineId = vaccineId;
        }
    }
}