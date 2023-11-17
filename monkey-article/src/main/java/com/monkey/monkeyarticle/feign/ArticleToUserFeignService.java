package com.monkey.monkeyarticle.feign;

import com.monkey.monkeyUtils.pojo.vo.UserFansVo;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.result.ResultVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// contextId确保多个feign接口使用@FeignClient注解调用同一个名称的微服务时， 启动不会报异常
// 发生雪崩后会调用CustomerRentFeignHystrix方法中的回调
//@FeignClient(value = "monkey-user", contextId = "article-to-user", fallback = ArticleCustomerRentFeignServiceHystrix.class)
@FeignClient(value = "monkey-user", contextId = "article-to-user")
public interface ArticleToUserFeignService {
    // 通过用户id得到用户vo信息
    @GetMapping("/monkey-user/user/center/home/getUserInformationByUserId")
    ResultVO getUserInformationByUserId(@RequestParam Map<String, String> data);

    @GetMapping("/monkey-user/feign/judgeLoginUserAndAuthorConnect")
    R judgeLoginUserAndAuthorConnect(@RequestParam Long userId, @RequestParam  Long fansId);

    // 得到userFans通过userId, 和fansId
    @GetMapping("/monkey-user/feign/getUserFansByUserAndAuthorConnect")
    R getUserFansByUserAndAuthorConnect(@RequestParam Long userId, @RequestParam Long fansId);

    // 通过id删除userFans
    @DeleteMapping("/monkey-user/feign/deleteUserFans")
    R deleteUserFans(@RequestBody UserFansVo userFansVo);

    // 插入userFans
    @PostMapping("/monkey-user/feign/add/UserFans")
    R addUserFans(@RequestBody UserFansVo userFansVo);
}
