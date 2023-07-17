package com.monkey.monkeygateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

/**
 * 自定义请求限制的
 */
//@Configuration
public class RequestLimitConfig {

    // 针对某一个接口 ip来限流  /doLogin    每一个ip 10s只能访问3次
    @Bean
    @Primary // 主候选的
    public KeyResolver ipKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getHeaders().getHost().getHostString());
    }

    // 针对这个路径来限制  /doLogin
    // api 就是 接口  外面一般把gateway    api网关  新一代网关
    @Bean
    public KeyResolver apiKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getPath().value());
    }


}
