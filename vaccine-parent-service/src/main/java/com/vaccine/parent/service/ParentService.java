package com.vaccine.parent.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vaccine.parent.entity.Parent;

import java.util.Map;

public interface ParentService extends IService<Parent> {

    /**
     * 根据openid查询家长
     */
    Parent getParentByOpenid(String openid);

    /**
     * 微信登录
     */
    Parent wxLogin(Map<String, Object> wxInfo);

    /**
     * 分页查询家长列表（管理员接口）
     */
    Map<String, Object> getParentPage(Integer page, Integer pageSize, String nickname, String phone, Integer status);

    /**
     * 更新家长状态（管理员接口）
     */
    boolean updateParentStatus(Long id, Integer status);

    /**
     * 删除家长（管理员接口）
     */
    boolean deleteParent(Long id);
}