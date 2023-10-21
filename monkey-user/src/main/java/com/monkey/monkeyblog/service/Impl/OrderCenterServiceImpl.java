package com.monkey.monkeyblog.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.alibaba.nacos.shaded.com.google.gson.JsonObject;
import com.alibaba.nacos.shaded.com.google.gson.internal.LinkedTreeMap;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayDataDataserviceBillDownloadurlQueryRequest;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayDataDataserviceBillDownloadurlQueryResponse;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.mapper.OrderInformationMapper;
import com.monkey.monkeyUtils.mapper.RefundInformationMapper;
import com.monkey.monkeyUtils.pojo.OrderInformation;
import com.monkey.monkeyUtils.pojo.RefundInformation;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyblog.rabbitmq.EventConstant;
import com.monkey.monkeyblog.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeyblog.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeyblog.service.OrderCenterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * @author: wusihao
 * @date: 2023/8/27 10:39
 * @version: 1.0
 * @description:
 */
@Service
@Slf4j
public class OrderCenterServiceImpl implements OrderCenterService {
    @Resource
    private OrderInformationMapper orderInformationMapper;
    @Resource
    private AlipayClient alipayClient;
    @Resource
    private RefundInformationMapper refundInformationMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;
    /**
     * 得到订单类型的数量（全部，已付款，未付款，待评价）
     *
     * @param userId 当前登录用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/27 15:36
     */
    @Override
    public R getOrderTypeNumber(long userId) {
        long total = 0L;
        long finished = 0L;
        long unpay = 0L;
        long waitEvalute = 0L;
        long userCanceled = 0L;
        long exceedTimeAlreadyClose = 0L;
        long refundSuccess = 0L;
        long refundFail = 0L;
        QueryWrapper<OrderInformation> orderInformationQueryWrapper = new QueryWrapper<>();
        orderInformationQueryWrapper.eq("user_id", userId);
        List<OrderInformation> orderInformationList = orderInformationMapper.selectList(orderInformationQueryWrapper);
        for (OrderInformation orderInformation : orderInformationList) {
            total ++ ;
            String orderStatus = orderInformation.getOrderStatus();
            if (orderStatus.equals(CommonEnum.NOT_PAY_FEE.getMsg())) {
                unpay ++ ;
            } else if (orderStatus.equals(CommonEnum.WAIT_EVALUATE.getMsg())) {
                waitEvalute ++ ;
            } else if (orderStatus.equals(CommonEnum.ALREADY_FINISH.getMsg())) {
                finished ++ ;
            } else if (orderStatus.equals(CommonEnum.USER_CANCELED.getMsg())) {
                userCanceled ++ ;
            } else if (orderStatus.equals(CommonEnum.EXCEED_TIME_AlREADY_CLOSE.getMsg())) {
                exceedTimeAlreadyClose ++ ;
            } else if (orderStatus.equals(CommonEnum.REFUND_SUCCESS.getMsg())) {
                refundSuccess ++ ;
            } else if (orderStatus.equals(CommonEnum.REFUND_FAIL.getMsg())) {
                refundFail ++ ;
            }
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("total", total);
        jsonObject.put("finished", finished);
        jsonObject.put("unpaid", unpay);
        jsonObject.put("waitEvaluate", waitEvalute);
        jsonObject.put("userCanceled", userCanceled);
        jsonObject.put("exceedTimeAlreadyClose", exceedTimeAlreadyClose);
        jsonObject.put("refundSuccess", refundSuccess);
        jsonObject.put("refundFail", refundFail);
        return R.ok(jsonObject);
    }

    /**
     * 得到全部订单列表
     *
     * @param userId 当前登录用户id
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/27 16:45
     */
    @Override
    public R getAllOrderList(long userId, int currentPage, int pageSize) {
        QueryWrapper<OrderInformation> orderInformationQueryWrapper = new QueryWrapper<>();
        orderInformationQueryWrapper.eq("user_id", userId);
        Page page = new Page(currentPage, pageSize);
        Page selectPage = orderInformationMapper.selectPage(page, orderInformationQueryWrapper);
        return R.ok(selectPage);
    }

    /**
     * 得到已完成订单列表
     *
     * @param userId 当前登录用户id
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/27 20:18
     */
    @Override
    public R getAlreadyFinishedOrderList(Long userId, int currentPage, int pageSize) {
        QueryWrapper<OrderInformation> orderInformationQueryWrapper = new QueryWrapper<>();
        orderInformationQueryWrapper.eq("user_id", userId);
        orderInformationQueryWrapper.eq("order_status", CommonEnum.ALREADY_FINISH.getMsg());
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = orderInformationMapper.selectPage(page, orderInformationQueryWrapper);
        return R.ok(selectPage);
    }

    /**
     * 得到待评价订单列表
     *
     * @param userId 当前登录用户id
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/27 20:20
     */
    @Override
    public R getWaitEvaluateOrderList(Long userId, int currentPage, int pageSize) {
        QueryWrapper<OrderInformation> orderInformationQueryWrapper = new QueryWrapper<>();
        orderInformationQueryWrapper.eq("user_id", userId);
        orderInformationQueryWrapper.eq("order_status", CommonEnum.WAIT_EVALUATE.getMsg());
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = orderInformationMapper.selectPage(page, orderInformationQueryWrapper);
        return R.ok(selectPage);
    }

    /**
     * 得到待付款订单列表
     *
     * @param userId 当前登录用户id
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/27 20:21
     */
    @Override
    public R getWaitPayOrderList(Long userId, int currentPage, int pageSize) {
        QueryWrapper<OrderInformation> orderInformationQueryWrapper = new QueryWrapper<>();
        orderInformationQueryWrapper.eq("user_id", userId);
        orderInformationQueryWrapper.eq("order_status", CommonEnum.NOT_PAY_FEE.getMsg());
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = orderInformationMapper.selectPage(page, orderInformationQueryWrapper);
        return R.ok(selectPage);
    }

    /**
     * 删除订单记录
     *
     * @param orderInformationId 订单信息id
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/27 20:36
     */
    @Override
    public R deleteOrderRecord(long orderInformationId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.deleteOrderRecord);
        jsonObject.put("orderInformationId", orderInformationId);
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.userDeleteDirectExchange,
                RabbitmqRoutingName.userDeleteRouting, message);
        return R.ok();
    }

    /**
     * 用户取消订单实现
     *
     * @param orderInformationId 订单id
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/28 9:01
     */
    @Override
    public R cancelOrder(long orderInformationId) {
        // 调用支付宝提供的统一交易关闭接口
        this.closeOrder(orderInformationId);
        // 更新订单状态
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.updateOrderStatus);
        jsonObject.put("orderInformationId", orderInformationId);
        jsonObject.put("statusType", CommonEnum.USER_CANCELED.getMsg());
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.userUpdateDirectExchange,
                RabbitmqRoutingName.userUpdateRouting, message);
        return R.ok();
    }

    /**
     * 调用支付宝提供的统一交易关闭接口
     *
     * @param orderInformationId 订单id
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/28 9:24
     */
    private void closeOrder(long orderInformationId) {
        try {
            log.info("调用支付宝提供的统一交易关闭接口, 订单号 ===> {}", orderInformationId);
            AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
            JSONObject bizContent = new JSONObject();
            bizContent.put("out_trade_no", orderInformationId);
            request.setBizContent(bizContent.toString());
            AlipayTradeCloseResponse response = alipayClient.execute(request);
            if(response.isSuccess()){
                log.info("调用支付宝提供的统一交易关闭接口成功， 返回结果 ==》 {}", response.getBody());
            } else {
                // 说明远程支付宝没有创建订单
                log.error("调用支付宝提供的统一交易关闭接口失败， 返回结果 ==》 {}", response.getBody());
//                throw new MonkeyBlogException(R.Error, CommonConstant.cancelOrderFail);
            }
        } catch (AlipayApiException e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 得到用户已取消订单列表
     *
     * @param userId 当前登录用户id
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/28 9:15
     */
    @Override
    public R getUserCanceledOrderList(Long userId, int currentPage, int pageSize) {
        QueryWrapper<OrderInformation> orderInformationQueryWrapper = new QueryWrapper<>();
        orderInformationQueryWrapper.eq("user_id", userId);
        orderInformationQueryWrapper.eq("order_status", CommonEnum.USER_CANCELED.getMsg());
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = orderInformationMapper.selectPage(page, orderInformationQueryWrapper);
        return R.ok(selectPage);
    }

    /**
     * 得到超时已关闭订单列表
     *
     * @param userId 当前登录用户id
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/28 9:15
     */
    @Override
    public R getExceedTimeAlreadyCloseOrderList(Long userId, int currentPage, int pageSize) {
        QueryWrapper<OrderInformation> orderInformationQueryWrapper = new QueryWrapper<>();
        orderInformationQueryWrapper.eq("user_id", userId);
        orderInformationQueryWrapper.eq("order_status", CommonEnum.EXCEED_TIME_AlREADY_CLOSE.getMsg());
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = orderInformationMapper.selectPage(page, orderInformationQueryWrapper);
        return R.ok(selectPage);
    }


    /**
     * 申请阿里云支付退款实现
     *
     * @param orderInformation 订单号
     * @param reason           退款原因
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/28 20:14
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public R orderRefund(OrderInformation orderInformation, String reason) {
        try {
            AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
            JSONObject bizContent = new JSONObject();
            bizContent.put("out_trade_no", orderInformation.getId());
            bizContent.put("refund_amount", orderInformation.getOrderMoney());
            bizContent.put("refund_reason", reason);
            bizContent.put("out_request_no", orderInformation.getId());

            //// 返回参数选项，按需传入
            //JSONArray queryOptions = new JSONArray();
            //queryOptions.add("refund_detail_item_list");
            //bizContent.put("query_options", queryOptions);

            request.setBizContent(bizContent.toString());
            AlipayTradeRefundResponse response = alipayClient.execute(request);

            // 生成退款订单
            RefundInformation refundInformation = new RefundInformation();
            refundInformation.setOrderInformationId(orderInformation.getId());
            refundInformation.setTotalMoney(orderInformation.getOrderMoney());
            refundInformation.setRefund(orderInformation.getOrderMoney());
            refundInformation.setReason(reason);
            refundInformation.setContentReturn(response.getBody());
            refundInformation.setCreateTime(new Date());
            if(response.isSuccess()){
                log.info("退款成功, 返回结果 ==> {}", response.getBody());
                orderInformation.setOrderStatus(CommonEnum.REFUND_SUCCESS.getMsg());

                // 更新退款状态
                refundInformation.setRefundStatus(CommonEnum.REFUND_FAIL.getMsg());

            } else {
                log.error("退款失败， 返回结果 ==》 {}", response.getBody());
                orderInformation.setOrderStatus(CommonEnum.REFUND_FAIL.getMsg());

                // 更新退款状态
                refundInformation.setRefundStatus(CommonEnum.REFUND_SUCCESS.getMsg());
            }
            // 更新订单信息
            orderInformationMapper.updateById(orderInformation);
            // 插入退款订单信息
            JSONObject object = new JSONObject();
            object.put("event", EventConstant.updateOrderInformation);
            object.put("refundInformation", JSONObject.toJSONString(refundInformation));
            Message messageRefund = new Message(object.toJSONString().getBytes());
            rabbitTemplate.convertAndSend(RabbitmqExchangeName.userUpdateDirectExchange,
                    RabbitmqRoutingName.userUpdateRouting, messageRefund);
        } catch (Exception e) {
            log.error("退款异常 == > {}", orderInformation);
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
        return R.ok();
    }

    /**
     * 得到退款成功订单列表
     *
     * @param userId 当前登录用户id
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/28 21:14
     */
    @Override
    public R getRefundSuccessOrderList(Long userId, int currentPage, int pageSize) {
        QueryWrapper<OrderInformation> orderInformationQueryWrapper = new QueryWrapper<>();
        orderInformationQueryWrapper.eq("user_id", userId);
        orderInformationQueryWrapper.eq("order_status", CommonEnum.REFUND_SUCCESS.getMsg());
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = orderInformationMapper.selectPage(page, orderInformationQueryWrapper);
        return R.ok(selectPage);
    }

    /**
     * 得到退款失败订单列表
     *
     * @param userId 当前登录用户id
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/28 21:40
     */
    @Override
    public R getRefundFailOrderList(Long userId, int currentPage, int pageSize) {
        QueryWrapper<OrderInformation> orderInformationQueryWrapper = new QueryWrapper<>();
        orderInformationQueryWrapper.eq("user_id", userId);
        orderInformationQueryWrapper.eq("order_status", CommonEnum.REFUND_FAIL.getMsg());
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = orderInformationMapper.selectPage(page, orderInformationQueryWrapper);
        return R.ok(selectPage);
    }

    /**
     * 退款查询接口
     *
     * @param orderInformationId 订单id
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/29 11:06
     */
    @Override
    public R refundQuery(Long orderInformationId) {
        try {
            log.info("退款查询接口调用 ==> {}", orderInformationId);
            AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
            JSONObject bizContent = new JSONObject();
            bizContent.put("out_trade_no", orderInformationId);
            bizContent.put("out_request_no", orderInformationId);

            //// 返回参数选项，按需传入
            //JSONArray queryOptions = new JSONArray();
            //queryOptions.add("refund_detail_item_list");
            //bizContent.put("query_options", queryOptions);

            request.setBizContent(bizContent.toString());
            AlipayTradeFastpayRefundQueryResponse response = alipayClient.execute(request);
            if(response.isSuccess()){
                log.info("查询退款成功, 调用结果 ==> {}", response.getBody());

                return R.ok(response.getBody());
            } else {
                log.info("查询退款失败, 调用结果 ==> {}", response.getMsg());
                // 订单不存在
                return R.error();
            }
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 通过账单类型和日期获取账单Url
     *
     * @param billDate 账单日期
     * @param billType 账单类型
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/29 11:52
     */
    @Override
    public R queryTradeBill(String billDate, String billType) {
        try {
            AlipayDataDataserviceBillDownloadurlQueryRequest request = new AlipayDataDataserviceBillDownloadurlQueryRequest();
            JSONObject bizContent = new JSONObject();
            bizContent.put("bill_type", billType);
            bizContent.put("bill_date", billDate);
            request.setBizContent(bizContent.toString());
            AlipayDataDataserviceBillDownloadurlQueryResponse response = alipayClient.execute(request);
            if(response.isSuccess()){
                log.info("查询阿里云账单成功，返回结果 ==> {}", response.getBody());

                // 获取账单下载地址
                Gson gson = new Gson();
                HashMap<String, LinkedTreeMap> hashMap = gson.fromJson(response.getBody(), HashMap.class);
                LinkedTreeMap alipayTradeQueryResponse = hashMap.get("alipay_data_dataservice_bill_downloadurl_query_response");
                String billDownloadUrl = (String) alipayTradeQueryResponse.get("bill_download_url");
                return R.ok(billDownloadUrl);
            } else {
                log.info("查询阿里云账单失败，返回结果 ==> {}", response.getMsg());
                return R.error();
            }
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }
}
