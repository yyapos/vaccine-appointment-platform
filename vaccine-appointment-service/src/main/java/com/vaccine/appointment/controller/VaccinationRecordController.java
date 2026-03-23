package com.vaccine.appointment.controller;

import com.vaccine.appointment.entity.VaccinationRecord;
import com.vaccine.appointment.service.VaccinationRecordService;
import com.vaccine.common.result.Result;
import com.vaccine.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/vaccination-record")
@RequiredArgsConstructor
public class VaccinationRecordController {

    private final VaccinationRecordService vaccinationRecordService;
    private final JwtUtil jwtUtil;

    @GetMapping("/count")
    public Result<Long> getRecordCount() {
        return Result.success(vaccinationRecordService.count());
    }

    @GetMapping("/child/{childId}")
    public Result<List<VaccinationRecord>> getRecordsByChildId(@RequestHeader("Authorization") String token, @PathVariable Long childId) {
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error("未授权，请先登录");
        }
        List<VaccinationRecord> records = vaccinationRecordService.getRecordsByChildId(childId);
        return Result.success(records);
    }

    @GetMapping("/page")
    @com.vaccine.common.annotation.RequireAdmin
    public Result<Map<String, Object>> getRecordPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long childId) {
        return Result.success(vaccinationRecordService.getAppointmentPage(page, pageSize, status, childId));
    }

    @PutMapping("/complete")
    @com.vaccine.common.annotation.RequireAdmin
    public Result<String> completeVaccination(@RequestBody Map<String, Object> request) {
        Long id = Long.valueOf(request.get("id").toString());
        String batchNumber = (String) request.get("batchNumber");
        String vaccinationSite = (String) request.get("vaccinationSite");
        try {
            boolean success = vaccinationRecordService.completeVaccination(id, batchNumber, vaccinationSite);
            return success ? Result.success("接种完成") : Result.error("操作失败");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<VaccinationRecord> getRecordById(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error("未授权，请先登录");
        }
        VaccinationRecord record = vaccinationRecordService.getById(id);
        if (record == null) {
            return Result.error("记录不存在");
        }
        return Result.success(record);
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
}