package com.monkey.monkeyblog.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyblog.service.VipService;
import com.monkey.spring_security.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author: wusihao
 * @date: 2023/10/31 10:00
 * @version: 1.0
 * @description:
 */
@Api(tags = "开通vip接口")
@RestController
@RequestMapping("/monkey-user/vip")
public class VipController {
    @Resource
    private VipService vipService;

    @ApiOperation("查询会员价格列表")
    @GetMapping("/queryVipPrice")
    public R queryVipPrice() {
        return vipService.queryVipPrice();
    }

    @ApiOperation("查询会员专属特权列表")
    @GetMapping("/queryVipPrivilegeList")
    public R queryVipPrivilegeList() {
        return vipService.queryVipPrivilegeList();
    }

    @ApiOperation("判断用户是否为会员")
    @GetMapping("/judgeIsVip")
    public R judgeIsVip() {
        String userId = JwtUtil.getUserId();
        return vipService.judgeIsVip(userId);
    }

    @ApiOperation("提交vip订单")
    @PostMapping("/submitVipOrder")
    public R submitVipOrder(@RequestParam("payWay") @ApiParam("支付方式") Integer payWay,
                            @RequestParam("monkey") @ApiParam("支付金额") Integer monkey,
                            @RequestParam("selectVipPriceId") @ApiParam("选择套餐id") Integer selectVipPriceId) {
        long userId = Long.parseLong(JwtUtil.getUserId());
        return vipService.submitVipOrder(userId, payWay, monkey, selectVipPriceId);
    }

    // 下单支付后执行的接口, 支付宝以 POST 形式发送请求
    @PostMapping("/finishPayNotice")
    public String finishPayNotice(@RequestParam Map<String, String> data)  {
        /**
         * 在进行异步通知交互时，如果支付宝收到的应答不是 success ，
         * 支付宝会认为通知失败，会通过一定的策略定期重新发起通知。
         * 如果返回failure则支付宝会认为此次交易失败，不断发送给重测通知
         */
        return vipService.finishPayNotice(data);
    }
}
