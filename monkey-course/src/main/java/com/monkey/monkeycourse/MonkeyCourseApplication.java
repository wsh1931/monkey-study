package com.monkey.monkeycourse;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableEurekaClient
@ComponentScan(basePackages = {"com.monkey"})
@MapperScan(basePackages = {"com.monkey.monkeycourse.mapper", "com.monkey.spring_security.mapper", "com.monkey.monkeyUtils.mapper"})
public class MonkeyCourseApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonkeyCourseApplication.class, args);
    }

}
