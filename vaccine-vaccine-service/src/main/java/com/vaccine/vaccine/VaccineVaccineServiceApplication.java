package com.vaccine.vaccine;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan({"com.vaccine.vaccine.mapper", "com.vaccine.common.mapper"})
@ComponentScan(basePackages = {"com.vaccine.vaccine", "com.vaccine.common"})
public class VaccineVaccineServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VaccineVaccineServiceApplication.class, args);
        System.out.println("========================================");
        System.out.println("疫苗管理服务启动成功！");
        System.out.println("端口: 8084");
        System.out.println("========================================");
    }
}