package com.vaccine.notify.service;

import com.vaccine.notify.dto.NotifyMessage;
import com.vaccine.notify.dto.NotifyResponse;
import java.util.List;

public interface NotifyService {
    
    NotifyResponse sendAppointmentReminder(Long childId, String childName, String vaccineName, String scheduledDate, String phone);
    
    NotifyResponse sendVaccinationReminder(Long childId, String childName, String vaccineName, String scheduledDate, String phone);
    
    NotifyResponse sendBatchAppointmentReminders(List<NotifyMessage> messages);
    
    NotifyResponse sendSmsNotification(String phone, String message);
    
    NotifyResponse sendEmailNotification(String email, String subject, String message);
}