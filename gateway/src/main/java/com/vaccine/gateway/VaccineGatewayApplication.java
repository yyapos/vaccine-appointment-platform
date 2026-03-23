package com.vaccine.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
public class VaccineGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(VaccineGatewayApplication.class, args);
        System.out.println("========================================");
        System.out.println("社区儿童疫苗接种平台网关启动成功！");
        System.out.println("网关地址: http://localhost:9000");
        System.out.println("========================================");
    }
}