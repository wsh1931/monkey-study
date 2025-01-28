package com.monkey.monkeygateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.monkey"})
//@EnableEurekaClient
public class MonkeyGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(MonkeyGatewayApplication.class, args);
    }


    //注入bean
//    @Bean
//    public KeyResolver ipKeyResolver() {
//        return new KeyResolver() {
//            @Override
//            public Mono<String> resolve(ServerWebExchange exchange) {
//                /**
//                 * 限流条件：
//                 * 1.用户ip地址（桶是私有）
//                 * 2.用户用户名（桶是私有）
//                 * 3.微服的路径(桶是共享的)
//                 */
//                //根据用户的id做为条件限流
//                return Mono.just(exchange.getRequest().getRemoteAddress().getHostName());
//            }
//        };
//    }
}
