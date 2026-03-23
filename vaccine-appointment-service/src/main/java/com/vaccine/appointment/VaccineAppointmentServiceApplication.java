package com.vaccine.appointment;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan({"com.vaccine.appointment.mapper", "com.vaccine.common.mapper"})
@ComponentScan(basePackages = {"com.vaccine.appointment", "com.vaccine.common"})
public class VaccineAppointmentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VaccineAppointmentServiceApplication.class, args);
        System.out.println("========================================");
        System.out.println("预约管理服务启动成功！");
        System.out.println("端口: 8085");
        System.out.println("========================================");
    }
}