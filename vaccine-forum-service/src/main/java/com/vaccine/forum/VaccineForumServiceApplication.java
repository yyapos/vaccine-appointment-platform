package com.vaccine.forum;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.vaccine.forum.feign")
@MapperScan({"com.vaccine.forum.mapper", "com.vaccine.common.mapper"})
@ComponentScan(basePackages = {"com.vaccine.forum", "com.vaccine.common"})
public class VaccineForumServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VaccineForumServiceApplication.class, args);
        System.out.println("========================================");
        System.out.println("论坛服务启动成功！");
        System.out.println("端口: 8089");
        System.out.println("========================================");
    }
}