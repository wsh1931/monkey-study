package com.monkey.monkeyblog.feign;

import com.monkey.monkeyUtils.result.R;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @author: wusihao
 * @date: 2023/10/22 16:14
 * @version: 1.0
 * @description:
 */
@FeignClient(value = "monkey-resource", contextId = "user-to-resource")
public interface UserToResourceFeignService {

    // 得到资源一周发表数
    @GetMapping("/monkey-resource/user/feign/queryResourceCountRecentlyWeek")
    R queryResourceCountRecentlyWeek(@RequestParam("userId") @ApiParam("用户id") String userId);

    @DeleteMapping("/monkey-resource/user/feign/deleteUserBuyResource")
    R deleteUserBuyResource(@RequestParam("userId") @ApiParam("用户id") Long userId,
                            @RequestParam("resourceId") @ApiParam("资源id") Long resourceId,
                            @RequestParam("money") @ApiParam("订单金额") Float money,
                            @RequestParam("payTime")@ApiParam("付款时间")Date payTime);

    @PutMapping("/monkey-resource/user/feign/resourceCollectCountAddOne/{resourceId}")
    R resourceCollectCountAddOne(@PathVariable @ApiParam("资源id") Long resourceId);

    @PutMapping("/monkey-resource/user/feign/resourceCollectCountSubOne/{resourceId}")
    R resourceCollectCountSubOne(@PathVariable @ApiParam("资源id") Long resourceId,
                                 @RequestParam("createTime") @ApiParam("收藏资源事件") Date createTime);

    @GetMapping("/monkey-resource/user/feign/queryResourceById/{resourceId}")
    R queryResourceById(@PathVariable @ApiParam("资源id") Long resourceId);

    @GetMapping("/monkey-resource/user/feign/queryResourceAndCommentById")
    R queryResourceAndCommentById(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId,
                                         @RequestParam("commentId") @ApiParam("评论id") Long commentId);

    @GetMapping("/monkey-resource/user/feign/queryResourceAuthorById")
    Long queryResourceAuthorById(@RequestParam("associationId") @ApiParam("资源id") Long resourceId);

    // 通过资源，评论id得到资源评论信息
    @GetMapping("/monkey-resource/user/feign/queryResourceAndCommentInfoById")
    R queryResourceAndCommentInfoById(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId,
                                             @RequestParam("commentId") @ApiParam("评论id") Long commentId);
}
