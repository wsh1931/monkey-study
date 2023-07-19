package com.monkey.monkeyUtils.feign;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author: wusihao
 * @date: 2023/7/18 20:17
 * @version: 1.0
 * @description:
 */
@Component
public class FeignLoggerConfig {
    // feign日志信息配置
    @Bean
    public Logger.Level articleLevel() {
        return Logger.Level.FULL;
    }
}
