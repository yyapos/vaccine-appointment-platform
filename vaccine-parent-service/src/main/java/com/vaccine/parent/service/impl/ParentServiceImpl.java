package com.vaccine.parent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vaccine.parent.entity.Parent;
import com.vaccine.parent.mapper.ParentMapper;
import com.vaccine.parent.service.ParentService;
import com.vaccine.parent.util.WeChatUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ParentServiceImpl extends ServiceImpl<ParentMapper, Parent> implements ParentService {

    private final WeChatUtil weChatUtil;

    @Override
    public Parent getParentByOpenid(String openid) {
        LambdaQueryWrapper<Parent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Parent::getOpenid, openid);
        wrapper.eq(Parent::getDeleted, 0);
        return this.getOne(wrapper);
    }

    @Override
    public Parent wxLogin(Map<String, Object> wxInfo) {
        String openid = (String) wxInfo.get("openid");
        String avatar = (String) wxInfo.get("avatar");
        String nickname = (String) wxInfo.get("nickname");
        String phoneCode = (String) wxInfo.get("phoneCode");
        Integer gender = (Integer) wxInfo.get("gender");
        String city = (String) wxInfo.get("city");

        // 查询用户
        Parent parent = this.getOne(new LambdaQueryWrapper<Parent>()
                .eq(Parent::getOpenid, openid));

        if (parent == null) {
            parent = new Parent();
            parent.setOpenid(openid);
            parent.setAvatar(avatar);
            parent.setNickname(nickname);
            parent.setGender(gender);
            parent.setCity(city);
            parent.setStatus(1);
            parent.setDeleted(0);

            // 手机号
            if (phoneCode != null && !phoneCode.isEmpty()) {
                Map<String, String> phoneMap = weChatUtil.getPhoneNumber(phoneCode);
                if (phoneMap.containsKey("phoneNumber")) {
                    parent.setPhone(phoneMap.get("phoneNumber"));
                }
            }

            this.save(parent);
        } else {
            // 更新信息
            parent.setAvatar(avatar);
            parent.setNickname(nickname);
            parent.setGender(gender);
            parent.setCity(city);
            
            // 如果提供了手机号code，则更新手机号
            if (phoneCode != null && !phoneCode.isEmpty()) {
                Map<String, String> phoneMap = weChatUtil.getPhoneNumber(phoneCode);
                if (phoneMap.containsKey("phoneNumber")) {
                    parent.setPhone(phoneMap.get("phoneNumber"));
                }
            }
            
            this.updateById(parent);
        }

        return parent;
    }
    @Override
    public Map<String, Object> getParentPage(Integer page, Integer pageSize, String nickname, String phone, Integer status) {
        Page<Parent> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Parent> wrapper = new LambdaQueryWrapper<>();
        
        if (nickname != null && !nickname.isEmpty()) {
            wrapper.like(Parent::getNickname, nickname);
        }
        if (phone != null && !phone.isEmpty()) {
            wrapper.like(Parent::getPhone, phone);
        }
        if (status !=null) {
            wrapper.eq(Parent::getStatus, status);
        }
        
        wrapper.orderByDesc(Parent::getCreateTime);
        
        IPage<Parent> resultPage = this.page(pageParam, wrapper);
        
        Map<String, Object> data = new HashMap<>();
        data.put("records", resultPage.getRecords());
        data.put("total", resultPage.getTotal());
        data.put("current", resultPage.getCurrent());
        data.put("size", resultPage.getSize());
        data.put("pages", resultPage.getPages());
        
        return data;
    }

    @Override
    public boolean updateParentStatus(Long id, Integer status) {
        Parent parent = this.getById(id);
        if (parent == null) {
            throw new RuntimeException("家长不存在");
        }
        parent.setStatus(status);
        return this.updateById(parent);
    }

    @Override
    public boolean deleteParent(Long id) {
        return this.removeById(id);
    }
}