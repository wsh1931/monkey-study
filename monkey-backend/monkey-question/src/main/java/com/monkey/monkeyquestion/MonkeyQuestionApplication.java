package com.monkey.monkeyquestion;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.monkey"})
@MapperScan(basePackages = {"com.monkey.monkeyquestion.mapper", "com.monkey.spring_security.mapper"})
public class MonkeyQuestionApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonkeyQuestionApplication.class, args);
    }

}
