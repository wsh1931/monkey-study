package com.monkey.monkeyblog.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyblog.service.OrderPayFinishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/8/29 17:50
 * @version: 1.0
 * @description:
 */
@Api(tags = "课程订单支付完成接口")
@RestController
@RequestMapping("/monkey-user/pay/finish")
public class OrderPayFinishController {
    @Resource
    private OrderPayFinishService coursePayFinishService;

    @ApiOperation("通过订单id得到订单信息")
    @GetMapping("/queryOrderInfoByOrderId")
    public R queryOrderInfoByOrderId(@RequestParam("orderId") @ApiParam("订单id") Long orderId){
        return coursePayFinishService.queryOrderInfoByOrderId(orderId);
    }
}
