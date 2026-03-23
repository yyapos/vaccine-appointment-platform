package com.vaccine.notify.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vaccine.notify.entity.Reminder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ReminderService extends IService<Reminder> {

    /**
     * 获取用户的提醒列表
     */
    List<Reminder> getRemindersByUserId(Long userId);

    /**
     * 获取未读提醒数量
     */
    Long getUnreadCount(Long userId);

    /**
     * 标记提醒为已读
     */
    boolean markAsRead(Long reminderId);

    /**
     * 批量标记提醒为已读
     */
    boolean markAsReadBatch(Long userId);

    /**
     * 获取提醒统计
     */
    Map<String, Object> getReminderStatistics();

    /**
     * 清理过期提醒
     */
    int cleanExpiredReminders(int days);
}