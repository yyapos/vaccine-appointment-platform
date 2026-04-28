package com.vaccine.appointment.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vaccine.appointment.entity.Appointment;
import com.vaccine.appointment.service.AppointmentService;
import com.vaccine.common.result.Result;
import com.vaccine.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/appointment")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final JwtUtil jwtUtil;

    /**
     * 获取预约总数（统计接口）
     */
    @GetMapping("/count")
    public Result<Long> getAppointmentCount() {
        LambdaQueryWrapper<Appointment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Appointment::getDeleted, 0);
        long count = appointmentService.count(queryWrapper);
        return Result.success(count);
    }
    //获取预约待审核总数
    @GetMapping("/count/pending-review-count")
    public Result<Long> getPendingReviewCount() {
        LambdaQueryWrapper<Appointment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Appointment::getStatus, 0);
        long count = appointmentService.count(queryWrapper);
        return Result.success(count);
    }
    //获取预约已通过总数
    @GetMapping("/count/approved-count")
    public Result<Long> getApprovedCount() {
        LambdaQueryWrapper<Appointment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Appointment::getStatus, 1);
        long count = appointmentService.count(queryWrapper);
        return Result.success(count);
    }
    //获取预约未通过总数
    @GetMapping("/count/rejected-count")
    public Result<Long> getRejectedCount() {
        LambdaQueryWrapper<Appointment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Appointment::getStatus, 2);
        long count = appointmentService.count(queryWrapper);
        return Result.success(count);
    }
    //获取预约已完成总数
    @GetMapping("/count/completed-count")
    public Result<Long> getCompletedCount() {
        LambdaQueryWrapper<Appointment> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(Appointment::getStatus, 3);
        long count = appointmentService.count(queryWrapper);
        return Result.success(count);
    }
    //获取预约已取消总数
    @GetMapping("/count/cancelled-count")
    public Result<Long> getCancelledCount() {
        LambdaQueryWrapper<Appointment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Appointment::getStatus, 4);
        long count = appointmentService.count(queryWrapper);
        return Result.success(count);
    }
    /**
     * 获取儿童的所有预约
     */
    @GetMapping("/child/{childId}")
    public Result<List<com.vaccine.appointment.vo.AppointmentVO>> getAppointmentsByChildId(@RequestHeader("Authorization") String token, @PathVariable Long childId) {
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error("未授权，请先登录");
        }
        List<com.vaccine.appointment.vo.AppointmentVO> appointments = appointmentService.getAppointmentsByChildId(childId);
        return Result.success(appointments);
    }

    /**
     * 创建预约
     */
    @PostMapping("/create")
    public Result<com.vaccine.appointment.vo.AppointmentVO> createAppointment(@RequestHeader("Authorization") String token, @RequestBody Appointment appointment) {
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error("未授权，请先登录");
        }
        try {
            com.vaccine.appointment.vo.AppointmentVO created = appointmentService.createAppointment(appointment);
            return Result.success(created);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 取消预约
     */
    @PutMapping("/cancel/{id}")
    public Result<String> cancelAppointment(@RequestHeader("Authorization") String token, @PathVariable Long id, @RequestBody Map<String, String> request) {
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error("未授权，请先登录");
        }
        try {
            String reason = request.get("reason");
            boolean success = appointmentService.cancelAppointment(id, userId, reason);
            return success ? Result.success("取消成功") : Result.error("取消失败");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取待审核预约列表（管理员接口）
     */
    @GetMapping("/pending-review")
    @com.vaccine.common.annotation.RequireAdmin
    public Result<List<Appointment>> getPendingReviewAppointments() {
        List<Appointment> appointments = appointmentService.getPendingReviewAppointments();
        return Result.success(appointments);
    }

    /**
     * 分页查询预约列表（管理员接口）
     */
    @GetMapping("/page")
    @com.vaccine.common.annotation.RequireAdmin
    public Result<Map<String, Object>> getAppointmentPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long childId,
            @RequestParam(required = false) String childName,
            @RequestParam(required = false) String vaccineName,
            @RequestParam(required = false) String createTimeStart,
            @RequestParam(required = false) String createTimeEnd) {
        return Result.success(appointmentService.getAppointmentPage(page, pageSize, status, childId, childName, vaccineName, createTimeStart, createTimeEnd));
    }

    /**
     * 审核通过（管理员接口）
     */
    @PutMapping("/approve/{id}")
    @com.vaccine.common.annotation.RequireAdmin
    public Result<String> approveAppointment(@PathVariable Long id) {
        try {
            boolean success = appointmentService.approveAppointment(id);
            return success ? Result.success("审核通过") : Result.error("操作失败");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 审核拒绝（管理员接口）
     */
    @PutMapping("/reject/{id}")
    @com.vaccine.common.annotation.RequireAdmin
    public Result<String> rejectAppointment(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String reason = request.get("reason");
        try {
            boolean success = appointmentService.rejectAppointment(id, reason);
            return success ? Result.success("已拒绝预约") : Result.error("操作失败");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 完成预约（管理员接口）
     */
    @PutMapping("/complete/{id}")
    @com.vaccine.common.annotation.RequireAdmin
    public Result<String> completeAppointment(@PathVariable Long id) {
        try {
            boolean success = appointmentService.completeAppointment(id);
            return success ? Result.success("预约已完成") : Result.error("操作失败");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取预约详情
     */
    @GetMapping("/{id}")
    public Result<Appointment> getAppointmentById(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error("未授权，请先登录");
        }
        Appointment appointment = appointmentService.getById(id);
        if (appointment == null) {
            return Result.error("预约不存在");
        }
        return Result.success(appointment);
    }

    /**
     * 从 Token 中获取用户 ID
     */
    private Long getUserIdFromToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }
        String jwtToken = token.substring(7);
        try {
            return jwtUtil.getUserIdFromToken(jwtToken);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 内部接口：根据条件查询预约列表（供其他服务调用）
     */
    @GetMapping("/internal/list")
    public List<Appointment> listAppointments(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long childId,
            @RequestParam(required = false) Long vaccineId,
            @RequestParam(required = false) String appointmentDate) {
        LambdaQueryWrapper<Appointment> queryWrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            queryWrapper.eq(Appointment::getStatus, status);
        }
        if (childId != null) {
            queryWrapper.eq(Appointment::getChildId, childId);
        }
        if (vaccineId != null) {
            queryWrapper.eq(Appointment::getVaccineId, vaccineId);
        }
        if (appointmentDate != null) {
            queryWrapper.ge(Appointment::getAppointmentDate, appointmentDate);
        }
        queryWrapper.eq(Appointment::getDeleted, 0);
        return appointmentService.list(queryWrapper);
    }

    /**
     * 内部接口：统计符合条件的预约数量（供其他服务调用）
     */
    @PostMapping("/internal/count")
    public Long countAppointments(@RequestBody AppointmentQueryRequest request) {
        LambdaQueryWrapper<Appointment> queryWrapper = new LambdaQueryWrapper<>();
        if (request.getStatus() != null) {
            queryWrapper.eq(Appointment::getStatus, request.getStatus());
        }
        if (request.getChildId() != null) {
            queryWrapper.eq(Appointment::getChildId, request.getChildId());
        }
        if (request.getVaccineId() != null) {
            queryWrapper.eq(Appointment::getVaccineId, request.getVaccineId());
        }
        queryWrapper.eq(Appointment::getDeleted, 0);
        return appointmentService.count(queryWrapper);
    }

    /**
     * 预约查询请求对象
     */
    public static class AppointmentQueryRequest {
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