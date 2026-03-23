package com.vaccine.notify.feign;

import com.vaccine.child.entity.Child;
import com.vaccine.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 儿童服务Feign客户端
 */
@FeignClient(name = "vaccine-child-service", path = "/child")
public interface ChildClient {

    /**
     * 根据ID获取儿童信息
     */
    @GetMapping("/{id}")
    Result<Child> getChildById(@PathVariable("id") Long id);

    /**
     * 获取所有儿童列表（定时任务使用）
     */
    @GetMapping("/all")
    Result<List<Child>> getAllChildren();
}