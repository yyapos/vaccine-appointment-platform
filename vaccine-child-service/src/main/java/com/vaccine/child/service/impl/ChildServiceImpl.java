package com.vaccine.child.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vaccine.child.entity.Child;
import com.vaccine.child.mapper.ChildMapper;
import com.vaccine.child.service.ChildService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChildServiceImpl extends ServiceImpl<ChildMapper, Child> implements ChildService {

    @Override
    public Map<String, Object> getChildPage(Integer page, Integer pageSize, String name, Long parentId, String createTimeStart, String createTimeEnd) {
        Page<Child> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Child> wrapper = new LambdaQueryWrapper<>();

        // 添加查询条件
        if (name != null && !name.isEmpty()) {
            wrapper.like(Child::getName, name);
        }
        if (parentId != null) {
            wrapper.eq(Child::getParentId, parentId);
        }

        // 添加时间过滤
        if (createTimeStart != null && !createTimeStart.isEmpty()) {
            wrapper.ge(Child::getCreateTime, java.time.LocalDateTime.parse(createTimeStart + "T00:00:00"));
        }
        if (createTimeEnd != null && !createTimeEnd.isEmpty()) {
            wrapper.le(Child::getCreateTime, java.time.LocalDateTime.parse(createTimeEnd + "T23:59:59"));
        }

        wrapper.orderByDesc(Child::getCreateTime);

        Page<Child> resultPage = this.page(pageParam, wrapper);

        Map<String, Object> data = new HashMap<>();
        data.put("records", resultPage.getRecords());
        data.put("total", resultPage.getTotal());
        data.put("current", resultPage.getCurrent());
        data.put("size", resultPage.getSize());
        data.put("pages", resultPage.getPages());

        return data;
    }

    @Override
    public List<Child> getChildrenByParentId(Long parentId) {
        LambdaQueryWrapper<Child> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Child::getParentId, parentId);
        wrapper.orderByDesc(Child::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public Child addChild(Child child) {
        this.save(child);
        return child;
    }

    @Override
    public boolean updateChild(Child child) {
        return this.updateById(child);
    }

    @Override
    public boolean deleteChild(Long id) {
        return this.removeById(id);
    }
}