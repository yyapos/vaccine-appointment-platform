package com.vaccine.appointment.feign;

import com.vaccine.vaccine.entity.Vaccine;
import com.vaccine.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "vaccine-vaccine-service")
public interface VaccineClient {

    @GetMapping("/vaccine/{id}")
    Result<Vaccine> getVaccineById(@PathVariable Long id);
}