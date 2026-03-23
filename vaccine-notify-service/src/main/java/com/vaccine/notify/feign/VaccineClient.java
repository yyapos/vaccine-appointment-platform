package com.vaccine.notify.feign;

import com.vaccine.vaccine.entity.Vaccine;
import com.vaccine.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 疫苗服务Feign客户端
 */
@FeignClient(name = "vaccine-vaccine-service", path = "/vaccine")
public interface VaccineClient {

    /**
     * 根据ID获取疫苗信息
     */
    @GetMapping("/{id}")
    Result<Vaccine> getVaccineById(@PathVariable("id") Long id);

    /**
     * 获取所有疫苗列表（定时任务使用）
     */
    @GetMapping("/list")
    Result<List<Vaccine>> getAllVaccines();
}