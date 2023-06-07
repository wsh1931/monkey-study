package com.monkey.monkeyblog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

// componenet的扫描包会默认覆盖SpringBootApplication的扫描包
@SpringBootApplication
@ComponentScan(basePackages = {"com.monkey"})
@MapperScan(basePackages = {"com.monkey.monkeyblog.mapper", "com.monkey.spring_security.mapper"})
public class MonkeyBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonkeyBackendApplication.class, args);
    }

}
