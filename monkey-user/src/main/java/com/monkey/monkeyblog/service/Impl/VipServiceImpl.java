package com.monkey.monkeyblog.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConstants;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.constants.CommonConstant;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.exception.ExceptionEnum;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.mapper.OrderInformationMapper;
import com.monkey.monkeyUtils.pojo.OrderInformation;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyblog.constant.TipConstant;
import com.monkey.monkeyblog.constant.UrlEnum;
import com.monkey.monkeyblog.mapper.VipBuyMapper;
import com.monkey.monkeyblog.mapper.VipPriceMapper;
import com.monkey.monkeyblog.mapper.VipPrivilegeMapper;
import com.monkey.monkeyblog.pojo.VipBuy;
import com.monkey.monkeyblog.pojo.VipPrice;
import com.monkey.monkeyblog.pojo.VipPrivilege;
import com.monkey.monkeyblog.rabbitmq.EventConstant;
import com.monkey.monkeyblog.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeyblog.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeyblog.service.VipService;
import com.monkey.monkeyblog.util.ConstantPropertiesUtlis;
import com.monkey.spring_security.mapper.UserMapper;
import com.monkey.spring_security.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import static com.monkey.monkeyUtils.util.DateUtils.DATE_TIME_PATTERN;
import static com.monkey.monkeyUtils.util.DateUtils.stringToDate;
import static com.monkey.monkeyblog.util.ConstantPropertiesUtlis.*;

/**
 * @author: wusihao
 * @date: 2023/10/31 10:03
 * @version: 1.0
 * @description:
 */
@Service
@Slf4j
public class VipServiceImpl implements VipService {
    @Resource
    private VipPriceMapper vipPriceMapper;
    @Resource
    private VipPrivilegeMapper vipPrivilegeMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private OrderInformationMapper orderInformationMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private AlipayClient alipayClient;
    @Resource
    private VipBuyMapper vipBuyMapper;
    private final ReentrantLock reentrantLock = new ReentrantLock();
    /**
     * 查询会员价格列表
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/31 10:08
     */
    @Override
    public R queryVipPrice() {
        QueryWrapper<VipPrice> vipPriceQueryWrapper = new QueryWrapper<>();
        vipPriceQueryWrapper.orderByAsc("sort");
        return R.ok(vipPriceMapper.selectList(vipPriceQueryWrapper));
    }

    /**
     * 查询会员专属特权列表
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/31 10:32
     */
    @Override
    public R queryVipPrivilegeList() {
        QueryWrapper<VipPrivilege> vipPrivilegeQueryWrapper = new QueryWrapper<>();
        vipPrivilegeQueryWrapper.orderByAsc("sort");
        return R.ok(vipPrivilegeMapper.selectList(vipPrivilegeQueryWrapper));
    }

    /**
     * 提交vip订单
     *
     * @param payWay 支付方式
     * @param monkey 支付金额
     * @param selectVipPriceId 选择vip价格类型
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/31 11:13
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R submitVipOrder(Long userId, Integer payWay, Integer monkey, Integer selectVipPriceId) {
        try {
            // 查找已存在并且还未支付的订单, 否则建立一个vip订单返回
            OrderInformation orderInformation = judgeIsExistOrder(selectVipPriceId, userId, payWay);
            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            //异步接收地址，仅支持http/https，公网可访问
            request.setNotifyUrl(ConstantPropertiesUtlis.NOTIFY_URL);
            //同步跳转地址，仅支持http/https
            // 设置请求成功后的跳转地址
            request.setReturnUrl(ConstantPropertiesUtlis.ALIPAY_RETURN_URL + orderInformation.getId());
            /******必传参数******/
            JSONObject bizContent = new JSONObject();
            //商户订单号，商家自定义，保持唯一性
            bizContent.put("out_trade_no", orderInformation.getId());
            //支付金额，最小值0.01元
            bizContent.put("total_amount", String.format("%.1f", orderInformation.getOrderMoney()));
            //订单标题，不可使用特殊符号
            bizContent.put("subject", orderInformation.getTitle());
            //电脑网站支付场景固定传值FAST_INSTANT_TRADE_PAY
            bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");

            request.setBizContent(bizContent.toString());

            // 执行请求调用支付宝接口
            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
            if(response.isSuccess()){
                return R.ok(response.getBody());
            } else {
                throw new MonkeyBlogException(R.Error, CommonConstant.createTradePayFail);
            }

        } catch (Exception e) {
            log.error("创建交易支付失败：==》 {}", e.getMessage());
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查找已存在并且还未支付的订单, 否则建立一个vip订单返回
     * @param userId 当前登录用户id
     * @param payWay 支付方式
     * @param selectVipPriceId 选择vip订单id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/31 11:37
     */
    private OrderInformation judgeIsExistOrder(Integer selectVipPriceId, Long userId, Integer payWay) {
        QueryWrapper<OrderInformation> orderInformationQueryWrapper = new QueryWrapper<>();
        orderInformationQueryWrapper.eq("user_id", userId);
        orderInformationQueryWrapper.eq("association_id", selectVipPriceId);
        orderInformationQueryWrapper.eq("order_type", CommonEnum.USER_ORDER_VIP.getMsg());
        orderInformationQueryWrapper.eq("order_status", CommonEnum.NOT_PAY_FEE.getMsg());
        OrderInformation orderInformation = orderInformationMapper.selectOne(orderInformationQueryWrapper);
        if (orderInformation != null) {
            return orderInformation;
        }

        // 说明未支付的订单不存在，创建一个新订单
        OrderInformation newOrder = createOrder(selectVipPriceId, userId, payWay);

        return newOrder;
    }

    /**
     * 创建一个新订单
     *
     * @param selectVipPriceId vip价格订单id
     * @param userId 用户id
     * @param payWay 支付方式
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/31 11:40
     */
    private OrderInformation createOrder(Integer selectVipPriceId, Long userId, Integer payWay) {
        VipPrice vipPrice = vipPriceMapper.selectById(selectVipPriceId);
        OrderInformation orderInformation = new OrderInformation();
        orderInformation.setAssociationId(vipPrice.getId());
        orderInformation.setUserId(userId);
        orderInformation.setTitle(CommonEnum.USER_ORDER_VIP.getMsg());
        orderInformation.setOrderStatus(CommonEnum.NOT_PAY_FEE.getMsg());
        orderInformation.setOrderType(CommonEnum.USER_ORDER_VIP.getMsg());
        // 通过订单类型得到订单图片
        orderInformation.setPicture(UrlEnum.VIP_PICTURE.getUrl());
        if (payWay.equals(CommonEnum.WE_CHAT_PAY.getCode())) {
            orderInformation.setPayWay(CommonEnum.WE_CHAT_PAY.getMsg());
        } else if (payWay.equals(CommonEnum.ALIPAY.getCode())) {
            orderInformation.setPayWay(CommonEnum.ALIPAY.getMsg());
        } else {
            throw new MonkeyBlogException(R.Error, TipConstant.payTypeError);
        }
        // 得到订单付款金额
            orderInformation.setOrderMoney(Float.parseFloat(String.valueOf(vipPrice.getPrice())));

        orderInformation.setCreateTime(new Date());
        orderInformationMapper.insert(orderInformation);
        // 将信息发送给rabbitmq并用死信交换机设置过期时间, 超过过期时间后查询订单状态
        Message message = new Message(JSONObject.toJSONString(orderInformation).getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.userDirectExchange,
                RabbitmqRoutingName.userOrderRouting, message);

        return orderInformation;
    }

    /**
     * 判断用户是否为会员
     *
     * @param userId 用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/31 11:28
     */
    @Override
    public R judgeIsVip(String userId) {
        if (userId == null || "".equals(userId)) {
            return R.ok(CommonEnum.NOT_VIP.getCode());
        }
        User user = userMapper.selectById(userId);
        Integer isVip = user.getIsVip();
        if (isVip.equals(CommonEnum.NOT_VIP.getCode())) {
            return R.ok(CommonEnum.NOT_VIP.getCode());
        }

        return R.ok(CommonEnum.IS_VIP.getCode());
    }

    /**
     * 主动查询阿里支付订单信息
     *
     * @param orderInformationId 订单id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/31 17:12
     */
    @Override
    public String queryAliPayOrder(Long orderInformationId) {
        try {
            log.info("资源查单接口调用： ==> {}", orderInformationId);

            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            AlipayTradeQueryModel model = new AlipayTradeQueryModel();
            model.setOutTradeNo(String.valueOf(orderInformationId));
            request.setBizModel(model);
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            if(response.isSuccess()){
                log.info("主动查询阿里云订单接口成功， 返回结果 ==》 {}", response.getBody());
                return response.getBody();
            } else {
                // 说明远程支付宝没有创建订单
                log.error("订单不存在， 返回结果 ==》 {}", response.getBody());
                return null;
            }
        } catch (AlipayApiException e) {
            log.error("主动查询阿里云订单接口成功失败");
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 支付宝关单接口
     *
     * @param orderInformationId 订单id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/31 17:13
     */
    @Override
    public void closeAliPayOrder(Long orderInformationId) {
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
     * 下单支付后执行的接口, 支付宝以 POST 形式发送请求
     *
     * @param data 支付宝返回的方法
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/31 17:21
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String finishPayNotice(Map<String, String> data) {
        try {
            // 进行异步通知的验签
            //调用SDK验证签名
            boolean signVerified = AlipaySignature.rsaCheckV1(data,
                    ALIPAY_PUBLIC_KEY,
                    AlipayConstants.CHARSET_UTF8, AlipayConstants.SIGN_TYPE_RSA2);
            if (!signVerified) {
                // 验签失败则记录异常日志，并在response中返回failure.
                log.error("支付成功，异步通知验签失败");
                return "failure";
            }

            // 否则表示验签成功
            // 验签成功后，按照支付结果异步通知中的描述，
            // 对支付结果中的业务内容进行二次校验，
            // 二次校验
            JSONObject jsonObject = regainVerify(data);
            Boolean success = jsonObject.getBoolean("success");
            if (!success) {
                return "failure";
            }
            // 校验成功后在response中返回success并继续商户自身业务处理，校验失败返回failure
            // 支付成功，处理自己的业务要求
            // 防止两个订单同时进来，此时支付状态还未更新，造成两次记录支付日志
            if (reentrantLock.tryLock()) {
                try {
                    // 更新支付订单表
                    OrderInformation orderInformation = JSONObject.parseObject(jsonObject.getJSONObject("orderInformation").toJSONString(),
                            OrderInformation.class);
                    // 支付的幂等性：无论接口被调用多少次，以下业务只执行一次
                    String orderStatus = orderInformation.getOrderStatus();
                    if (!orderStatus.equals(CommonEnum.NOT_PAY_FEE.getMsg())) {
                        return "success";
                    }

                    orderInformation.setOrderStatus(CommonEnum.ALREADY_FINISH.getMsg());
                    String gmtCreate = data.get("gmt_create");
                    String gmtPayment = data.get("gmt_payment");
                    Date payTime = stringToDate(gmtPayment, DATE_TIME_PATTERN);
                    Date createTime = stringToDate(gmtCreate, DATE_TIME_PATTERN);
                    orderInformation.setSubmitTime(createTime);
                    orderInformation.setPayTime(payTime);

                    // 更新支付订单
                    orderInformationMapper.updateById(orderInformation);

                    // 记录支付日志
                    recordPayLog(data);

                    // 插入vip购买表
                    VipBuy vipBuy = new VipBuy();
                    vipBuy.setVipPriceId(orderInformation.getAssociationId());
                    vipBuy.setUserId(orderInformation.getUserId());
                    vipBuy.setCreateTime(new Date());
                    vipBuyMapper.insert(vipBuy);
                } finally {
                    reentrantLock.unlock();
                }
            }

        } catch(Exception e){
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
        return "success";
    }

    // 记录支付日志
    private void recordPayLog(Map<String, String> data) {
        // 通过消息队列插入支付日志
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.insertPayUpdateSuccessLog);
        jsonObject.put("data", JSONObject.toJSONString(data));
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.userInsertDirectExchange,
                RabbitmqRoutingName.userInsertRouting, message);
    }

    /**
     * 对订单进行二次校验
     * 需要严格按照如下描述校验通知数据的正确性：
     * 1. 商家需要验证该通知数据中的 out_trade_no 是否为商家系统中创建的订单号。
     * 2. 判断 total_amount 是否确实为该订单的实际金额（即商家订单创建时的金额）。
     * 3. 校验通知中的 seller_id（或者 seller_email) 是否为 out_trade_no 这笔单据的对应的操作方（有的时候，一个商家可能有多个 seller_id/seller_email）。
     * 4. 验证 app_id 是否为该商家本身。
     * @param data 支付宝传递过来的参数
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/21 16:09
     */
    private JSONObject regainVerify(Map<String, String> data) {
        // 1. 商家需要验证该通知数据中的 out_trade_no 是否为商家系统中创建的订单号。
        // 即该订单号在数据库中是否存在
        String outTradeNo = data.get("out_trade_no");
        OrderInformation orderInformation = orderInformationMapper.selectById(outTradeNo);
        // 最终返回集合
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("orderInformation", JSONObject.toJSONString(orderInformation));
        if (orderInformation == null) {
            log.error(ExceptionEnum.ORDER_NOT_EXIST.getMsg());
            jsonObject.put("success", "false");
            return jsonObject;
        }

        // 2. 判断 total_amount 是否确实为该订单的实际金额（即商家订单创建时的金额）。
        float totalAmount = Float.parseFloat(data.get("total_amount"));
        if (totalAmount != orderInformation.getOrderMoney()) {
            log.error(ExceptionEnum.MONKEY_VERITY_FAIL.getMsg());
            jsonObject.put("success", "false");
            return jsonObject;
        }

        // 3. 校验通知中的 seller_id（或者 seller_email) 是否为 out_trade_no 这笔单据的对应的操作方（有的时候，一个商家可能有多个 seller_id/seller_email）。
        // 防止金额支付给其他人
        String sellerId = data.get("seller_id");
        if (!SELLER_ID.equals(sellerId)) {
            log.error(ExceptionEnum.MERCHANT_PID_VERIFY_FAIL.getMsg());
            jsonObject.put("success", "false");
            return jsonObject;
        }

        // 4. 验证 app_id 是否为该商家本身。
        String appId = data.get("app_id");
        if (!APP_ID.equals(appId)) {
            log.error(ExceptionEnum.APP_ID_VERIFY_FAIL.getMsg());
            jsonObject.put("success", "false");
            return jsonObject;
        }

        // 当交易码为TRADE_SUCCESS支付宝才认定为买家付款成功
        String tradeStatus = data.get("trade_status");
        if (!CommonConstant.apliPayTradeSuccess.equals(tradeStatus)) {
            log.error(ExceptionEnum.PAY_FAIL.getMsg());
            jsonObject.put("success", "false");
            return jsonObject;
        }

        jsonObject.put("success", "true");
        return jsonObject;
    }
}
