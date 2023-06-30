package com.monkey.monkeynetty;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
@SpringBootApplication
@ComponentScan(basePackages = {"com.monkey"})
@MapperScan(basePackages = { "com.monkey.spring_security.mapper", "com.monkey.monkeyUtils.mapper", "com.monkey.monkeynetty.mapper"})
public class MonkeyNettyApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonkeyNettyApplication.class, args);
    }

}
