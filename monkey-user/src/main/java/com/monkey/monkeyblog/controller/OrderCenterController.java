package com.monkey.monkeyblog.controller;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.pojo.OrderInformation;
import com.monkey.monkeyUtils.rabbitmq.RabbitmqExpireTime;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyblog.service.OrderCenterService;
import com.monkey.spring_security.JwtUtil;
import io.swagger.models.auth.In;
import lombok.Data;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.awt.image.RasterFormatException;
import java.util.Map;

/**
 * @author: wusihao
 * @date: 2023/8/27 10:38
 * @version: 1.0
 * @description: 订单中心
 */
@RestController
@RequestMapping("/monkey-user/order/center")
public class OrderCenterController {
    @Autowired
    private OrderCenterService orderCenterService;

    // 得到订单类型的数量（全部，已付款，未付款，待评价）
    @GetMapping("/getOrderTypeNumber")
    public R getOrderTypeNumber() {
        long userId = Long.parseLong(JwtUtil.getUserId());
        return orderCenterService.getOrderTypeNumber(userId);
    }

    // 得到全部订单列表
    @GetMapping("/getAllOrderList")
    public R getAllOrderList(@RequestParam Map<String, String> data) {
        int currentPage = Integer.parseInt(data.get("currentPage"));
        int pageSize = Integer.parseInt(data.get("pageSize"));
        Long userId = Long.parseLong(JwtUtil.getUserId());
        return orderCenterService.getAllOrderList(userId, currentPage, pageSize);
    }

    // 得到已完成订单列表
    @GetMapping("/getAlreadyFinishedOrderList")
    public R getAlreadyFinishedOrderList(@RequestParam Map<String, String> data) {
        int currentPage = Integer.parseInt(data.get("currentPage"));
        int pageSize = Integer.parseInt(data.get("pageSize"));
        Long userId = Long.parseLong(JwtUtil.getUserId());
        return orderCenterService.getAlreadyFinishedOrderList(userId, currentPage, pageSize);
    }
    // 得到待评价订单列表
    @GetMapping("/getWaitEvaluateOrderList")
    public R getWaitEvaluateOrderList(@RequestParam Map<String, String> data) {
        int currentPage = Integer.parseInt(data.get("currentPage"));
        int pageSize = Integer.parseInt(data.get("pageSize"));
        Long userId = Long.parseLong(JwtUtil.getUserId());
        return orderCenterService.getWaitEvaluateOrderList(userId, currentPage, pageSize);
    }

    // 得到待付款订单列表
    @GetMapping("/getWaitPayOrderList")
    public R getWaitPayOrderList(@RequestParam Map<String, String> data) {
        int currentPage = Integer.parseInt(data.get("currentPage"));
        int pageSize = Integer.parseInt(data.get("pageSize"));
        Long userId = Long.parseLong(JwtUtil.getUserId());
        return orderCenterService.getWaitPayOrderList(userId, currentPage, pageSize);
    }

    // 删除订单记录
    @DeleteMapping("/deleteOrderRecord")
    public R deleteOrderRecord(@RequestParam Map<String, String> data) {
        long orderInformationId = Long.parseLong(data.get("orderInformationId"));
        return orderCenterService.deleteOrderRecord(orderInformationId);
    }

    // 用户取消订单实现
    @DeleteMapping("/cancelOrder")
    public R cancelOrder(@RequestParam Map<String, String> data) {
        long orderInformationId = Long.parseLong(data.get("orderInformationId"));
        return orderCenterService.cancelOrder(orderInformationId);
    }

    // 得到用户已取消订单列表
    @GetMapping("/getUserCanceledOrderList")
    public R getUserCanceledOrderList(@RequestParam Map<String, String> data) {
        int currentPage = Integer.parseInt(data.get("currentPage"));
        int pageSize = Integer.parseInt(data.get("pageSize"));
        Long userId = Long.parseLong(JwtUtil.getUserId());
        return orderCenterService.getUserCanceledOrderList(userId, currentPage, pageSize);
    }

    // 得到超时已关闭订单列表
    @GetMapping("/getExceedTimeAlreadyCloseOrderList")
    public R getExceedTimeAlreadyCloseOrderList(@RequestParam Map<String, String> data) {
        int currentPage = Integer.parseInt(data.get("currentPage"));
        int pageSize = Integer.parseInt(data.get("pageSize"));
        Long userId = Long.parseLong(JwtUtil.getUserId());
        return orderCenterService.getExceedTimeAlreadyCloseOrderList(userId, currentPage, pageSize);
    }

    // 申请阿里云支付退款实现
    @PostMapping("/orderRefund")
    public R applicationRefund(@RequestParam("orderInformation") String orderInformationStr, @RequestParam("reason") String reason) {
        OrderInformation orderInformation = JSONObject.parseObject(orderInformationStr, OrderInformation.class);
        return orderCenterService.orderRefund(orderInformation, reason);
    }

    // 得到退款成功订单列表
    @GetMapping("/getRefundSuccessOrderList")
    public R getRefundOrderList(@RequestParam Map<String, String> data) {
        int currentPage = Integer.parseInt(data.get("currentPage"));
        int pageSize = Integer.parseInt(data.get("pageSize"));
        Long userId = Long.parseLong(JwtUtil.getUserId());
        return orderCenterService.getRefundSuccessOrderList(userId, currentPage, pageSize);
    }

    // 得到退款失败订单列表
    @GetMapping("/getRefundFailOrderList")
    public R getRefundFailOrderList(@RequestParam Map<String, String> data) {
        int currentPage = Integer.parseInt(data.get("currentPage"));
        int pageSize = Integer.parseInt(data.get("pageSize"));
        Long userId = Long.parseLong(JwtUtil.getUserId());
        return orderCenterService.getRefundFailOrderList(userId, currentPage, pageSize);
    }

    // 退款查询接口
    @GetMapping("/refundQuery/{orderInformationId}")
    public R refundQuery(@PathVariable Long orderInformationId) {
        return orderCenterService.refundQuery(orderInformationId);
    }

    // 通过账单类型和日期获取账单Url
    @GetMapping("/queryTradeBill")
    public R queryTradeBill(@RequestParam("billDate") String billDate,
                            @RequestParam("billType")String billType) {
        return orderCenterService.queryTradeBill(billDate, billType);
    }
}
