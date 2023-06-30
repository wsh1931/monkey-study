package com.monkey.monkeynetty.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/*
* WebSocketConfig的serverEndpointExporter方法是一个@Bean方法，用于创建并注册一个ServerEndpointExporter对象。
* 这个对象可以将标注有@ServerEndpoint注解的类注册为WebSocket端点，使其能够接收和处理WebSocket连接和消息。
* */
@Configuration
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
