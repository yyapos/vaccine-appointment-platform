package com.vaccine.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan({"com.vaccine.auth.mapper", "com.vaccine.common.mapper"})
@ComponentScan(basePackages = {"com.vaccine.auth", "com.vaccine.common"})
public class VaccineAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(VaccineAuthApplication.class, args);
        System.out.println("========================================");
        System.out.println("认证授权服务启动成功！");
        System.out.println("端口: 8088");
        System.out.println("========================================");
    }
}