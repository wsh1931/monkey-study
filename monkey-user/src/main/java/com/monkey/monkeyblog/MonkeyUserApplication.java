package com.monkey.monkeyblog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

// componenet的扫描包会默认覆盖SpringBootApplication的扫描包
@SpringBootApplication
//@EnableEurekaClient
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.monkey"})
@MapperScan(basePackages = {"com.monkey.monkeyblog.mapper", "com.monkey.spring_security.mapper", "com.monkey.label.mapper", "com.monkey.monkeyarticle.mapper", "com.monkey.monkeyUtils.mapper"})
public class MonkeyUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonkeyUserApplication.class, args);
    }
}
