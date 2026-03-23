package com.vaccine.appointment.feign;
import com.vaccine.child.entity.Child;
import com.vaccine.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "vaccine-child-service")
public interface ChildClient {

    @GetMapping("/child/{id}")
    Result<Child> getChildById(@PathVariable Long id);
}