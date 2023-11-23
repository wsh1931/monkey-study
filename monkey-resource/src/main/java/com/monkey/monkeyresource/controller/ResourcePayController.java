package com.monkey.monkeyresource.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyresource.service.ResourcePayService;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author: wusihao
 * @date: 2023/10/20 9:08
 * @version: 1.0
 * @description:
 */
@Api(tags = "资源支付接口")
@RestController
@RequestMapping("/monkey-resource/pay")
public class ResourcePayController {
    @Resource
    private ResourcePayService resourcePayService;
    
    @ApiOperation("得到资源基本信息")
    @GetMapping("/queryResourceInfo")
    public R queryResourceInfo(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId) {
        long userId = Long.parseLong(JwtUtil.getUserId());
        return resourcePayService.queryResourceInfo(userId, resourceId);
    }

    @ApiOperation("提交资源订单")
    @PostMapping("/submitResourceOrder")
    public R submitResourceOrder(@RequestParam("resourceId") @ApiParam("资源id") Long resourceId,
                                 @RequestParam("payWay") @ApiParam("支付方式") Integer payWay) {
        long userId = Long.parseLong(JwtUtil.getUserId());
        return resourcePayService.submitResourceOrder(userId, resourceId, payWay);
    }

    // 下单支付后执行的接口, 支付宝以 POST 形式发送请求
    @PostMapping("/finishPayNotice")
    public String finishPayNotice(@RequestParam Map<String, String> data)  {
        /**
         * 在进行异步通知交互时，如果支付宝收到的应答不是 success ，
         * 支付宝会认为通知失败，会通过一定的策略定期重新发起通知。
         * 如果返回failure则支付宝会认为此次交易失败，不断发送给重测通知
         */
        return resourcePayService.finishPayNotice(data);
    }
}
