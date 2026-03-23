package com.vaccine.forum.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vaccine.forum.entity.ForumAnnouncement;

import java.util.List;

public interface ForumAnnouncementService extends IService<ForumAnnouncement> {

    /**
     * 获取启用的公告列表（按优先级排序）
     */
    List<ForumAnnouncement> getActiveAnnouncements();

    /**
     * 获取需要弹窗显示的公告
     */
    List<ForumAnnouncement> getPopupAnnouncements();
}