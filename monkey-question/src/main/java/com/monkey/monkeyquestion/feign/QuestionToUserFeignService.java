package com.monkey.monkeyquestion.feign;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.result.ResultVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 问答模块调用文章模块
 *
 * @author wusihao
 * @date 2023/7/12 15:28
 * @value 提供者的应用名称
 */
// contextId确保多个feign接口使用@FeignClient注解调用同一个名称的微服务时， 启动不会报异常
//@FeignClient(value = "monkey-user", contextId = "question-to-user", fallback = QuestionCustomerRentFeignServiceHystrix.class)
@FeignClient(value = "monkey-user", contextId = "question-to-user")
public interface QuestionToUserFeignService {

    // 通过用户id得到用户vo信息
    @GetMapping("/monkey-user/user/center/home/getUserInformationByUserId")
    ResultVO getUserInformationByUserId(@RequestParam Map<String, String> data);

    // 通过fans_id和user_id判断当前登录用户是否是对方粉丝
    @GetMapping("/monkey-user/feign/judgeLoginUserAndAuthorConnect")
    R judgeLoginUserAndAuthorConnect(@RequestParam Long userId, @RequestParam Long fansId);
}
