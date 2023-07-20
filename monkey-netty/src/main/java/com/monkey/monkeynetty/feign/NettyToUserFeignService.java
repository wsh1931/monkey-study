package com.monkey.monkeynetty.feign;

import com.monkey.monkeyUtils.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(value = "monkey-user", contextId = "netty-to-user")
public interface NettyToUserFeignService {

    // 通过fans_id和user_id判断当前登录用户是否是对方粉丝
    @GetMapping("/monkey-user/feign/judgeLoginUserAndAuthorConnect")
     R judgeLoginUserAndAuthorConnect(@RequestParam Long userId,@RequestParam Long fansId);

    @GetMapping("/monkey-user/feign/getUserInfoByUserId/{userId}")
    R getUserInfoByUserId(@PathVariable Long userId);
}
