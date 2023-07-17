package com.monkey.monkeygateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.monkey"})
//@EnableEurekaClient
public class MonkeyGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(MonkeyGatewayApplication.class, args);
    }

}
