package com.vaccine.child.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vaccine.child.entity.Child;

import java.util.List;
import java.util.Map;

public interface ChildService extends IService<Child> {

    /**
     * 分页查询儿童列表（管理员接口）
     */
    Map<String, Object> getChildPage(Integer page, Integer pageSize, String name, Long parentId, String createTimeStart, String createTimeEnd);

    /**
     * 根据家长ID获取儿童列表
     */
    List<Child> getChildrenByParentId(Long parentId);

    /**
     * 添加儿童
     */
    Child addChild(Child child);

    /**
     * 更新儿童信息
     */
    boolean updateChild(Child child);

    /**
     * 删除儿童
     */
    boolean deleteChild(Long id);
}