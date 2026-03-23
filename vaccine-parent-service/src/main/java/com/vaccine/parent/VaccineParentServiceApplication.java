package com.vaccine.parent;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan({"com.vaccine.parent.mapper", "com.vaccine.common.mapper"})
@ComponentScan(basePackages = {"com.vaccine.parent", "com.vaccine.common"})
public class VaccineParentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VaccineParentServiceApplication.class, args);
        System.out.println("========================================");
        System.out.println("家长管理服务启动成功！");
        System.out.println("端口: 8090");
        System.out.println("========================================");
    }
}