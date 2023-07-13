package com.monkey.monkeyarticle;

import feign.Logger;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@ComponentScan(basePackages = {"com.monkey"})
@MapperScan(basePackages = {"com.monkey.monkeyarticle.mapper", "com.monkey.spring_security.mapper", "com.monkey.monkeyUtils.mapper"})
public class MonkeyArticleApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonkeyArticleApplication.class, args);
    }

   // feign日志信息配置
    @Bean
    public Logger.Level articleLevel() {
        return Logger.Level.FULL;
    }

}
