package com.vaccine.notify.controller;

import com.vaccine.common.result.Result;
import com.vaccine.common.util.JwtUtil;
import com.vaccine.notify.dto.NotifyMessage;
import com.vaccine.notify.dto.NotifyResponse;
import com.vaccine.notify.service.NotifyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/notify")
@RequiredArgsConstructor
public class NotifyController {

    private final NotifyService notifyService;
    private final JwtUtil jwtUtil;

    @PostMapping("/appointment/reminder")
    public Result<NotifyResponse> sendAppointmentReminder(@RequestBody NotifyMessage message, 
                                                           @RequestHeader("Authorization") String token) {
        try {
            log.info("发送预约提醒: {}", message);
            NotifyResponse response = notifyService.sendAppointmentReminder(
                message.getChildId(),
                message.getChildName(),
                message.getVaccineName(),
                message.getScheduledDate(),
                message.getPhone()
            );
            return Result.success(response);
        } catch (Exception e) {
            log.error("发送预约提醒失败", e);
            return Result.error("发送预约提醒失败: " + e.getMessage());
        }
    }

    @PostMapping("/vaccination/reminder")
    public Result<NotifyResponse> sendVaccinationReminder(@RequestBody NotifyMessage message,
                                                           @RequestHeader("Authorization") String token) {
        try {
            log.info("发送接种提醒: {}", message);
            NotifyResponse response = notifyService.sendVaccinationReminder(
                message.getChildId(),
                message.getChildName(),
                message.getVaccineName(),
                message.getScheduledDate(),
                message.getPhone()
            );
            return Result.success(response);
        } catch (Exception e) {
            log.error("发送接种提醒失败", e);
            return Result.error("发送接种提醒失败: " + e.getMessage());
        }
    }

    @PostMapping("/batch/reminder")
    public Result<NotifyResponse> sendBatchReminders(@RequestBody List<NotifyMessage> messages,
                                                     @RequestHeader("Authorization") String token) {
        try {
            log.info("批量发送提醒: {} 条", messages.size());
            NotifyResponse response = notifyService.sendBatchAppointmentReminders(messages);
            return Result.success(response);
        } catch (Exception e) {
            log.error("批量发送提醒失败", e);
            return Result.error("批量发送提醒失败: " + e.getMessage());
        }
    }

    @PostMapping("/sms")
    public Result<NotifyResponse> sendSms(@RequestParam String phone,
                                          @RequestParam String message,
                                          @RequestHeader("Authorization") String token) {
        try {
            log.info("发送短信: {}", message);
            NotifyResponse response = notifyService.sendSmsNotification(phone, message);
            return Result.success(response);
        } catch (Exception e) {
            log.error("发送短信失败", e);
            return Result.error("发送短信失败: " + e.getMessage());
        }
    }

    @PostMapping("/email")
    public Result<NotifyResponse> sendEmail(@RequestParam String email,
                                            @RequestParam String subject,
                                            @RequestParam String message,
                                            @RequestHeader("Authorization") String token) {
        try {
            log.info("发送邮件: {}", subject);
            NotifyResponse response = notifyService.sendEmailNotification(email, subject, message);
            return Result.success(response);
        } catch (Exception e) {
            log.error("发送邮件失败", e);
            return Result.error("发送邮件失败: " + e.getMessage());
        }
    }
}