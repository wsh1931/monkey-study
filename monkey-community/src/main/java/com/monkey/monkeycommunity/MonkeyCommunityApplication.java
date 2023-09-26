package com.monkey.monkeycommunity;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

// componenet的扫描包会默认覆盖SpringBootApplication的扫描包
@SpringBootApplication
//@EnableEurekaClient
@EnableFeignClients
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.monkey"})
// 引入springboot的定时任务
@EnableScheduling
@MapperScan(basePackages = {"com.monkey.monkeycommunity.mapper", "com.monkey.spring_security.mapper", "com.monkey.label.mapper", "com.monkey.monkeyUtils.mapper"})
public class MonkeyCommunityApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonkeyCommunityApplication.class, args);
    }

}
