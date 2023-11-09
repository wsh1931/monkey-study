package com.monkey.monkeygateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: wusihao
 * @date: 2023/7/15 10:52
 * @version: 1.0
 * @description:
 */
@Configuration
public class RouteConfig {
    /**
     * 代码的路由  和yml不冲突  都可以用
     * 如果你的uri后面给了一个访问地址 和匹配地址相同 那么就不会再凭借
     * 使用配置类不可配置成负载均衡的形式比如lb://service.application.name
     * @param builder
     * @return
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return  builder.routes()
                .route("monkey-article",r->r.path("/monkey-article/**").uri("http://localhost:4100"))
                .route("monkey-course",r->r.path("/monkey-course/**").uri("http://localhost:4600"))
                .route("monkey-websocket-user",r->r.path("/websocket/chat/**").uri("ws://localhost:4500"))
                .route("monkey-question",r->r.path("/monkey-question/**").uri("http://localhost:4300"))
                .route("monkey-service",r->r.path("/monkey-service/**").uri("http://localhost:4400"))
                .route("monkey-user",r->r.path("/monkey-user/**").uri("http://localhost:4500"))
                .route("monkey-websocket-course", r -> r.path("/websocket/barrage/**").uri("ws://localhost:4600"))
                .route("monkey-community", r->r.path("/monkey-community/**").uri("http://localhost:4200"))
                .route("monkey-resource", r->r.path("/monkey-resource/**").uri("http://localhost:4700"))
                .route("monkey-search", r->r.path("/monkey-search/**").uri("http://localhost:4800"))
                .build();
    }
}
