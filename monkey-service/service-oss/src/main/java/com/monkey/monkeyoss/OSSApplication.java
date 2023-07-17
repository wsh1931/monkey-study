package com.monkey.monkeyoss;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@EnableDiscoveryClient // 开启nacos服务
@SpringBootApplication// 不扫描数据库文件
@ComponentScan(basePackages = {"com.monkey.monkeyoss", "com.monkey.spring_security"})
@MapperScan(basePackages = {"com.monkey.spring_security.mapper"})
//@EnableEurekaClient // 开启客户端功能
public class OSSApplication {
    public static void main(String[] args) {
        SpringApplication.run(OSSApplication.class, args);
    }
}
