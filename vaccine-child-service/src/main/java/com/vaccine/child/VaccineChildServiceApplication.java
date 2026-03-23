package com.vaccine.child;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan({"com.vaccine.child.mapper", "com.vaccine.common.mapper"})
@ComponentScan(basePackages = {"com.vaccine.child", "com.vaccine.common"})
public class VaccineChildServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VaccineChildServiceApplication.class, args);
        System.out.println("========================================");
        System.out.println("儿童管理服务启动成功！");
        System.out.println("端口: 8083");
        System.out.println("========================================");
    }
}