package com.vaccine.forum.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vaccine.forum.entity.ForumAnnouncement;
import com.vaccine.forum.mapper.ForumAnnouncementMapper;
import com.vaccine.forum.service.ForumAnnouncementService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ForumAnnouncementServiceImpl extends ServiceImpl<ForumAnnouncementMapper, ForumAnnouncement> implements ForumAnnouncementService {

    @Override
    public List<ForumAnnouncement> getActiveAnnouncements() {
        LambdaQueryWrapper<ForumAnnouncement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ForumAnnouncement::getStatus, 1);
        wrapper.and(w -> w.isNull(ForumAnnouncement::getStartTime)
                .or().le(ForumAnnouncement::getStartTime, LocalDateTime.now()));
        wrapper.and(w -> w.isNull(ForumAnnouncement::getEndTime)
                .or().ge(ForumAnnouncement::getEndTime, LocalDateTime.now()));
        wrapper.orderByDesc(ForumAnnouncement::getPriority, ForumAnnouncement::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public List<ForumAnnouncement> getPopupAnnouncements() {
        LambdaQueryWrapper<ForumAnnouncement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ForumAnnouncement::getStatus, 1);
        wrapper.eq(ForumAnnouncement::getIsPopup, 1);
        wrapper.and(w -> w.isNull(ForumAnnouncement::getStartTime)
                .or().le(ForumAnnouncement::getStartTime, LocalDateTime.now()));
        wrapper.and(w -> w.isNull(ForumAnnouncement::getEndTime)
                .or().ge(ForumAnnouncement::getEndTime, LocalDateTime.now()));
        wrapper.orderByDesc(ForumAnnouncement::getPriority, ForumAnnouncement::getCreateTime);
        return this.list(wrapper);
    }
}