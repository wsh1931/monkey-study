package com.monkey.monkeyblog.controller;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.pojo.OrderInformation;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyblog.service.OrderCenterService;
import com.monkey.spring_security.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author: wusihao
 * @date: 2023/8/27 10:38
 * @version: 1.0
 * @description: 订单中心
 */
@Api(tags = "用户订单接口")
@RestController
@RequestMapping("/monkey-user/order/center")
public class OrderCenterController {
    @Resource
    private OrderCenterService orderCenterService;

    @ApiOperation("得到订单类型的数量（全部，已付款，未付款，待评价）")
    @GetMapping("/getOrderTypeNumber")
    public R getOrderTypeNumber() {
        long userId = Long.parseLong(JwtUtil.getUserId());
        return orderCenterService.getOrderTypeNumber(userId);
    }

    @ApiOperation("得到全部订单列表")
    @GetMapping("/getAllOrderList")
    public R getAllOrderList(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                            @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize) {
        Long userId = Long.parseLong(JwtUtil.getUserId());
        return orderCenterService.getAllOrderList(userId, currentPage, pageSize);
    }

    @ApiOperation("得到已完成订单列表")
    @GetMapping("/getAlreadyFinishedOrderList")
    public R getAlreadyFinishedOrderList(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                                         @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize) {
        Long userId = Long.parseLong(JwtUtil.getUserId());
        return orderCenterService.getAlreadyFinishedOrderList(userId, currentPage, pageSize);
    }

    @ApiOperation("得到待评价订单列表")
    @GetMapping("/getWaitEvaluateOrderList")
    public R getWaitEvaluateOrderList(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                                      @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize) {
        Long userId = Long.parseLong(JwtUtil.getUserId());
        return orderCenterService.getWaitEvaluateOrderList(userId, currentPage, pageSize);
    }

    @ApiOperation("得到待付款订单列表")
    @GetMapping("/getWaitPayOrderList")
    public R getWaitPayOrderList(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                                 @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize) {
        Long userId = Long.parseLong(JwtUtil.getUserId());
        return orderCenterService.getWaitPayOrderList(userId, currentPage, pageSize);
    }

    @ApiOperation("删除订单记录")
    @DeleteMapping("/deleteOrderRecord")
    public R deleteOrderRecord(@RequestParam("orderInformationId") @ApiParam("订单id") Long orderInformationId) {
        return orderCenterService.deleteOrderRecord(orderInformationId);
    }

    @ApiOperation("用户取消订单实现")
    @DeleteMapping("/cancelOrder")
    public R cancelOrder(@RequestParam("orderInformationId") @ApiParam("订单id") Long orderInformationId) {
        return orderCenterService.cancelOrder(orderInformationId);
    }

    @ApiOperation("得到用户已取消订单列表")
    @GetMapping("/getUserCanceledOrderList")
    public R getUserCanceledOrderList(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                                      @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize) {
        Long userId = Long.parseLong(JwtUtil.getUserId());
        return orderCenterService.getUserCanceledOrderList(userId, currentPage, pageSize);
    }

    @ApiOperation("得到超时已关闭订单列表")
    @GetMapping("/getExceedTimeAlreadyCloseOrderList")
    public R getExceedTimeAlreadyCloseOrderList(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                                                @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize) {
        Long userId = Long.parseLong(JwtUtil.getUserId());
        return orderCenterService.getExceedTimeAlreadyCloseOrderList(userId, currentPage, pageSize);
    }

    @ApiOperation("申请阿里云支付退款实现")
    @PostMapping("/orderRefund")
    public R applicationRefund(@RequestParam("orderInformation") @ApiParam("订单信息") String orderInformationStr,
                               @RequestParam("reason") @ApiParam("退款原因") String reason) {
        OrderInformation orderInformation = JSONObject.parseObject(orderInformationStr, OrderInformation.class);
        return orderCenterService.orderRefund(orderInformation, reason);
    }

    @ApiOperation("得到退款成功订单列表")
    @GetMapping("/getRefundSuccessOrderList")
    public R getRefundOrderList(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                                @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize) {
        Long userId = Long.parseLong(JwtUtil.getUserId());
        return orderCenterService.getRefundSuccessOrderList(userId, currentPage, pageSize);
    }

    @ApiOperation("得到退款失败订单列表")
    @GetMapping("/getRefundFailOrderList")
    public R getRefundFailOrderList(@RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                                    @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize) {
        Long userId = Long.parseLong(JwtUtil.getUserId());
        return orderCenterService.getRefundFailOrderList(userId, currentPage, pageSize);
    }

    @ApiOperation("退款查询接口")
    @GetMapping("/refundQuery/{orderInformationId}")
    public R refundQuery(@PathVariable @ApiParam("订单id") Long orderInformationId) {
        return orderCenterService.refundQuery(orderInformationId);
    }

    @ApiOperation("通过账单类型和日期获取账单Url")
    @GetMapping("/queryTradeBill")
    public R queryTradeBill(@RequestParam("billDate") @ApiParam("日期") String billDate,
                            @RequestParam("billType") @ApiParam("账单类型") String billType) {
        return orderCenterService.queryTradeBill(billDate, billType);
    }
}
