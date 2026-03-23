package com.vaccine.notify.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotifyMessage {
    private Long childId;
    private String childName;
    private String vaccineName;
    private String scheduledDate;
    private String phone;
    private String email;
    private String notifyType; // APPOINTMENT, VACCINATION
    private String message;
}