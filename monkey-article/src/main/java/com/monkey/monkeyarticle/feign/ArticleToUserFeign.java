package com.monkey.monkeyarticle.feign;

import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyarticle.feign.hystrix.ArticleCustomerRentFeignHystrix;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Component
// contextId确保多个feign接口使用@FeignClient注解调用同一个名称的微服务时， 启动不会报异常
// 发生雪崩后会调用CustomerRentFeignHystrix方法中的回调
@FeignClient(value = "monkey-user", contextId = "monkey-article", fallback = ArticleCustomerRentFeignHystrix.class)
public interface ArticleToUserFeign {
    // 通过用户id得到用户vo信息
    @GetMapping("/user/center/home/getUserInformationByUserId")
    public ResultVO getUserInformationByUserId(@RequestParam Map<String, String> data);
}
