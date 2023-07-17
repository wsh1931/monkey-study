package com.monkey.monkeyquestion;

import feign.Logger;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

//@EnableEurekaClient
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.monkey"})
@MapperScan(basePackages = {"com.monkey.monkeyquestion.mapper", "com.monkey.spring_security.mapper", "com.monkey.monkeyUtils.mapper"})
public class  MonkeyQuestionApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonkeyQuestionApplication.class, args);
    }

    // 设置fegin的日志，FULL是打印所有信息
    @Bean
    public Logger.Level questionLevel() {
        return Logger.Level.FULL;
    }
}
