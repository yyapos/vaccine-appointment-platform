package com.vaccine.notify.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NotifyResponse {
    private Boolean success;
    private String message;
    private String notifyId;

    public NotifyResponse(Boolean success, String message) {
        this.success = success;
        this.message = message;
        this.notifyId = null;
    }

    public NotifyResponse(Boolean success, String message, String notifyId) {
        this.success = success;
        this.message = message;
        this.notifyId = notifyId;
    }
}