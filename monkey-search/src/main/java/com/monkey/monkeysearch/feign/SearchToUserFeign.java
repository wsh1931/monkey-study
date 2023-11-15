package com.monkey.monkeysearch.feign;

import com.monkey.monkeyUtils.result.R;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "monkey-user", contextId = "search-to-user")
public interface SearchToUserFeign {

    // 得到所有用户粉丝信息
    @GetMapping("/monkey-user/search/feign/queryAllUserFansInfo")
    R queryAllUserFansInfo();

    // 判断当前登录用户是否是作者粉丝
    @GetMapping("/monkey-user/search/feign/judgeIsFans")
    R judgeIsFans(@RequestParam("userId") @ApiParam("当前登录用户id") String userId,
                         @RequestParam("authorId") @ApiParam("作者id") String authorId);
}
