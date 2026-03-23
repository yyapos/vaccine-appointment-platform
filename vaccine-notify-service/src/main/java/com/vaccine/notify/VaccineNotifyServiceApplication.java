package com.vaccine.notify;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableScheduling
@MapperScan({"com.vaccine.notify.mapper", "com.vaccine.common.mapper"})
@ComponentScan(basePackages = {"com.vaccine.notify", "com.vaccine.common"})
public class VaccineNotifyServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VaccineNotifyServiceApplication.class, args);
        log.info("========================================");
        log.info("通知提醒服务启动成功！");
        log.info("端口: 8086");
        log.info("定时任务已启用");
        log.info("========================================");
    }
}