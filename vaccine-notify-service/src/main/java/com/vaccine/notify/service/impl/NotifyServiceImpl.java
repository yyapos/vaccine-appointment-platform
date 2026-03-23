package com.vaccine.notify.service.impl;

import com.vaccine.notify.dto.NotifyMessage;
import com.vaccine.notify.dto.NotifyResponse;
import com.vaccine.notify.service.NotifyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotifyServiceImpl implements NotifyService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public NotifyResponse sendAppointmentReminder(Long childId, String childName, String vaccineName, String scheduledDate, String phone) {
        String message = String.format("【疫苗预约提醒】您的孩子%s将于%s接种%s疫苗，请按时前往。", childName, scheduledDate, vaccineName);
        return sendSmsNotification(phone, message);
    }

    @Override
    public NotifyResponse sendVaccinationReminder(Long childId, String childName, String vaccineName, String scheduledDate, String phone) {
        String message = String.format("【接种提醒】您的孩子%s的%s疫苗接种日期是%s，请做好准备。", childName, vaccineName, scheduledDate);
        return sendSmsNotification(phone, message);
    }

    @Override
    public NotifyResponse sendBatchAppointmentReminders(List<NotifyMessage> messages) {
        int successCount = 0;
        for (NotifyMessage msg : messages) {
            NotifyResponse response;
            if ("APPOINTMENT".equals(msg.getNotifyType())) {
                response = sendAppointmentReminder(msg.getChildId(), msg.getChildName(), msg.getVaccineName(), msg.getScheduledDate(), msg.getPhone());
            } else {
                response = sendVaccinationReminder(msg.getChildId(), msg.getChildName(), msg.getVaccineName(), msg.getScheduledDate(), msg.getPhone());
            }
            if (response.getSuccess()) {
                successCount++;
            }
        }
        return new NotifyResponse(true, String.format("批量发送完成，成功%d/%d", successCount, messages.size()));
    }

    @Override
    public NotifyResponse sendSmsNotification(String phone, String message) {
        try {
            String notifyId = UUID.randomUUID().toString();
            log.info("发送短信通知 - 手机号: {}, 消息: {}", phone, message);
            
            String key = "notify:sms:" + notifyId;
            redisTemplate.opsForValue().set(key, message, 24 * 60 * 60);
            
            return new NotifyResponse(true, "短信发送成功", notifyId);
        } catch (Exception e) {
            log.error("发送短信失败", e);
            return new NotifyResponse(false, "短信发送失败: " + e.getMessage());
        }
    }

    @Override
    public NotifyResponse sendEmailNotification(String email, String subject, String message) {
        try {
            String notifyId = UUID.randomUUID().toString();
            log.info("发送邮件通知 - 邮箱: {}, 主题: {}", email, subject);
            
            String key = "notify:email:" + notifyId;
            redisTemplate.opsForValue().set(key, message, 24 * 60 * 60);
            
            return new NotifyResponse(true, "邮件发送成功", notifyId);
        } catch (Exception e) {
            log.error("发送邮件失败", e);
            return new NotifyResponse(false, "邮件发送失败: " + e.getMessage());
        }
    }
}