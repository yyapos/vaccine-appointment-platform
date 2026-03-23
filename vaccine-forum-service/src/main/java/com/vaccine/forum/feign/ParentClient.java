package com.vaccine.forum.feign;

import com.vaccine.common.result.Result;
import com.vaccine.parent.entity.Parent;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 家长服务Feign客户端
 */
@FeignClient(name = "vaccine-parent-service")
public interface ParentClient {

    /**
     * 根据ID获取家长信息
     */
    @GetMapping("/parent/{id}")
    Result<Parent> getParentById(@PathVariable("id") Long id);
}