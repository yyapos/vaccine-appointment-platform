package com.vaccine.notify.feign;

import com.vaccine.common.result.Result;
import com.vaccine.parent.entity.Parent;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "vaccine-parent-service")
public interface ParentClient {

    @GetMapping("/parent/{id}")
    Result<Parent> getParentById(@PathVariable Long id);
}