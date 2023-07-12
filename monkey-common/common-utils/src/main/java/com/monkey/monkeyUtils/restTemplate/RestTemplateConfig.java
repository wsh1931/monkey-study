package com.monkey.monkeyUtils.restTemplate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    @Bean // 若需要用到RestTemplate这是固定用法, 可以在注解autoWrite下使用
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
