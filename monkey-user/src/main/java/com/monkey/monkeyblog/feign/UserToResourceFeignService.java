package com.monkey.monkeyblog.feign;

import com.monkey.monkeyUtils.result.R;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author: wusihao
 * @date: 2023/10/22 16:14
 * @version: 1.0
 * @description:
 */
@FeignClient(value = "monkey-resource", contextId = "user-to-resource")
public interface UserToResourceFeignService {

    @DeleteMapping("/monkey-resource/user/feign/deleteUserBuyResource")
    R deleteUserBuyResource(@RequestParam("userId") @ApiParam("用户id") Long userId,
                                   @RequestParam("resourceId") @ApiParam("资源id") Long resourceId);
}
