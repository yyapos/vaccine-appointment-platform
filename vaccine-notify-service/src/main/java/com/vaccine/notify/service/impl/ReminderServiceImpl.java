package com.vaccine.notify.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vaccine.notify.entity.Reminder;
import com.vaccine.notify.mapper.ReminderMapper;
import com.vaccine.notify.service.ReminderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ReminderServiceImpl extends ServiceImpl<ReminderMapper, Reminder> implements ReminderService {

    @Override
    public List<Reminder> getRemindersByUserId(Long userId) {
        LambdaQueryWrapper<Reminder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reminder::getUserId, userId);
        wrapper.eq(Reminder::getStatus, 1); // 只返回已发送但未读的提醒
        wrapper.orderByDesc(Reminder::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public Long getUnreadCount(Long userId) {
        LambdaQueryWrapper<Reminder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reminder::getUserId, userId);
        wrapper.eq(Reminder::getStatus, 1); // 1:已发送但未读
        return this.count(wrapper);
    }

    @Override
    public boolean markAsRead(Long reminderId) {
        Reminder reminder = this.getById(reminderId);
        if (reminder == null) {
            log.warn("提醒不存在: reminderId={}", reminderId);
            return false;
        }

        reminder.setStatus(2); // 2:已读
        reminder.setUpdateTime(LocalDateTime.now());

        return this.updateById(reminder);
    }

    @Override
    public boolean markAsReadBatch(Long userId) {
        LambdaQueryWrapper<Reminder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reminder::getUserId, userId);
        wrapper.eq(Reminder::getStatus, 1); // 查询已发送但未读的提醒

        List<Reminder> unreadReminders = this.list(wrapper);
        for (Reminder reminder : unreadReminders) {
            reminder.setStatus(2); // 2:已读
            reminder.setUpdateTime(LocalDateTime.now());
        }

        return this.updateBatchById(unreadReminders);
    }

    @Override
    public Map<String, Object> getReminderStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // 总提醒数
        Long totalReminders = this.count();
        stats.put("totalReminders", totalReminders);
        
        // 未读提醒数
        Long unreadReminders = this.lambdaQuery()
            .eq(Reminder::getStatus, 0)
            .count();
        stats.put("unreadReminders", unreadReminders);
        
        // 今日提醒数
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime todayEnd = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        Long todayReminders = this.lambdaQuery()
            .between(Reminder::getCreateTime, todayStart, todayEnd)
            .count();
        stats.put("todayReminders", todayReminders);
        
        // 按类型统计
        Map<String, Long> typeStats = new HashMap<>();
        typeStats.put("即将到期", this.lambdaQuery().eq(Reminder::getReminderType, "即将到期").count());
        typeStats.put("逾期", this.lambdaQuery().eq(Reminder::getReminderType, "逾期").count());
        typeStats.put("预约提醒", this.lambdaQuery().eq(Reminder::getReminderType, "预约提醒").count());
        typeStats.put("系统通知", this.lambdaQuery().eq(Reminder::getReminderType, "系统通知").count());
        stats.put("typeStats", typeStats);
        
        return stats;
    }

    @Override
    public int cleanExpiredReminders(int days) {
        LocalDateTime expireTime = LocalDateTime.now().minusDays(days);
        
        LambdaQueryWrapper<Reminder> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(Reminder::getCreateTime, expireTime);
        
        List<Reminder> expiredReminders = this.list(wrapper);
        int count = expiredReminders.size();
        
        if (count > 0) {
            this.removeByIds(expiredReminders.stream().map(Reminder::getId).toList());
            log.info("清理过期提醒: count={}", count);
        }
        
        return count;
    }
}