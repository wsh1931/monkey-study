package com.monkey.monkeyresource;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.monkey"})
@MapperScan(basePackages = {"com.monkey.monkeyresource.mapper", "com.monkey.spring_security.mapper", "com.monkey.monkeyUtils.mapper"})
public class MonkeyResourceApplication {
	public static void main(String[] args) {
		SpringApplication.run(MonkeyResourceApplication.class, args);
	}

}
