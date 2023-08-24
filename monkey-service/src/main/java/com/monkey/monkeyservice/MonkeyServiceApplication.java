package com.monkey.monkeyservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@EnableDiscoveryClient // 开启nacos服务
@SpringBootApplication// 不扫描数据库文件
@ComponentScan(basePackages = {"com.monkey.monkeyservice", "com.monkey.spring_security", "com.monkey.monkeyUtils"})
@MapperScan(basePackages = {"com.monkey.spring_security.mapper"})
//@EnableEurekaClient // 开启客户端功能
public class MonkeyServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MonkeyServiceApplication.class, args);
    }
}
