package com.vaccine.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan({"com.vaccine.user.mapper", "com.vaccine.common.mapper"})
@ComponentScan(basePackages = {"com.vaccine.user", "com.vaccine.common"})
public class VaccineUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VaccineUserServiceApplication.class, args);
        System.out.println("========================================");
        System.out.println("用户管理服务启动成功！");
        System.out.println("端口: 8082");
        System.out.println("========================================");
    }
}