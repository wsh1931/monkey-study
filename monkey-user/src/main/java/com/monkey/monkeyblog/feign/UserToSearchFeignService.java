package com.monkey.monkeyblog.feign;

import com.monkey.monkeyUtils.result.R;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "monkey-search", contextId = "user-to-search")
public interface UserToSearchFeignService {

    // 用户粉丝数 + 1
    @PutMapping("/monkey-search/user/feign/userFansCountAddOne")
    R userFansCountAddOne(@RequestParam("userId") @ApiParam("用户id") Long userId);

    // 用户粉丝数 - 1
    @PutMapping("/monkey-search/user/feign/userFansCountSubOne")
    R userFansCountSubOne(@RequestParam("userId") @ApiParam("用户id") Long userId);
}
