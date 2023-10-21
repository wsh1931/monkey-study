package com.monkey.monkeyresource.service.impl;

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
import com.monkey.monkeyresource.constant.FileTypeEnum;
import com.monkey.monkeyresource.constant.ResourcesEnum;
import com.monkey.monkeyresource.constant.TipConstant;
import com.monkey.monkeyresource.mapper.ResourceBuyMapper;
import com.monkey.monkeyresource.mapper.ResourceChargeMapper;
import com.monkey.monkeyresource.mapper.ResourceConnectMapper;
import com.monkey.monkeyresource.mapper.ResourcesMapper;
import com.monkey.monkeyresource.pojo.ResourceBuy;
import com.monkey.monkeyresource.pojo.ResourceCharge;
import com.monkey.monkeyresource.pojo.ResourceConnect;
import com.monkey.monkeyresource.pojo.Resources;
import com.monkey.monkeyresource.pojo.vo.ResourcesVo;
import com.monkey.monkeyresource.rabbitmq.EventConstant;
import com.monkey.monkeyresource.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeyresource.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeyresource.service.ResourcePayService;
import com.monkey.spring_security.mapper.UserMapper;
import com.monkey.spring_security.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import static com.monkey.monkeyUtils.util.DateUtils.DATE_TIME_PATTERN;
import static com.monkey.monkeyUtils.util.DateUtils.stringToDate;
import static com.monkey.monkeyresource.util.ConstantPropertiesUtlis.*;

/**
 * @author: wusihao
 * @date: 2023/10/20 9:08
 * @version: 1.0
 * @description:
 */
@Slf4j
@Service
public class ResourcePayServiceImpl implements ResourcePayService {
    @Resource
    private ResourcesMapper resourcesMapper;
    @Resource
    private ResourceConnectMapper resourceConnectMapper;
    @Resource
    private ResourceChargeMapper resourceChargeMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private ResourceBuyMapper resourceBuyMapper;
    @Resource
    private OrderInformationMapper orderInformationMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private AlipayClient alipayClient;

    private final ReentrantLock reentrantLock = new ReentrantLock();
    /**
     * 得到资源基本信息
     *
     * @param userId 当前登录用户id
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/20 9:24
     */
    @Override
    public R queryResourceInfo(long userId, Long resourceId) {
        Resources resources = resourcesMapper.selectById(resourceId);
        //得到资源类型
        QueryWrapper<ResourceConnect> resourceConnectQueryWrapper = new QueryWrapper();
        resourceConnectQueryWrapper.eq("resource_id", resourceId);
        resourceConnectQueryWrapper.eq("level", CommonEnum.LABEL_LEVEL_ONE.getCode());
        ResourceConnect resourceConnect = resourceConnectMapper.selectOne(resourceConnectQueryWrapper);

        ResourcesVo resourcesVo = new ResourcesVo();
        BeanUtils.copyProperties(resources, resourcesVo);
        resourcesVo.setTypeUrl(FileTypeEnum.getFileUrlByFileType(resourceConnect.getType()).getUrl());
        Integer status = resources.getStatus();

        if (!status.equals(ResourcesEnum.SUCCESS.getCode())) {
            return R.error(TipConstant.resourceNoAccessApproval);
        }
        QueryWrapper<ResourceCharge> resourceChargeQueryWrapper = new QueryWrapper<>();
        resourceChargeQueryWrapper.eq("resource_id", resourceId);
        ResourceCharge resourceCharge = resourceChargeMapper.selectOne(resourceChargeQueryWrapper);

        Integer isDiscount = resourceCharge.getIsDiscount();
        if (ResourcesEnum.IS_DISCOUNT.getCode().equals(isDiscount)) {
            Float discount = resourceCharge.getDiscount();
            Float price = resourceCharge.getPrice();
            Float resourcePrice = discount * price;
            String format = String.format("%.2f", resourcePrice);
            resourcesVo.setPrice(format);
            resourcesVo.setOriginPrice(String.format("%.2f", price));
        } else {
            resourcesVo.setOriginPrice(String.valueOf(resourceCharge.getPrice()));
            resourcesVo.setPrice(String.valueOf(resourceCharge.getPrice()));
        }

        // 判断用户是否绑定QQ邮箱
        User user = userMapper.selectById(userId);
        String email = user.getEmail();
        if (email == null || "".equals(email)) {
            resourcesVo.setHasEmail(CommonEnum.NOT_REGISTER_EMAIL.getCode());
        } else {
            resourcesVo.setEmail(email);
            resourcesVo.setHasEmail(CommonEnum.ALREADY_REGISTER_EMAIL.getCode());
        }
        return R.ok(resourcesVo);
    }

    /**
     * 提交资源订单
     *
     * @param userId 当前登录用户id
     * @param resourceId 资源id
     * @param payWay 支付方式
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/20 15:32
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R submitResourceOrder(long userId, Long resourceId, Integer payWay) {
        try {
            // 判断用户是否支付该资源
            boolean isPay = judgeUserIsPayResource(resourceId, userId);
            if (isPay) {
                log.error("用户已支付该课程");
                return R.error(TipConstant.isPayResource);
            }
            // 查找已存在并且还未支付的资源, 否则建立一个课程订单返回
            OrderInformation orderInformation = judgeIsExistOrder(resourceId, userId, payWay);
            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            //异步接收地址，仅支持http/https，公网可访问
            request.setNotifyUrl(NOTIFY_URL);
            //同步跳转地址，仅支持http/https
            // 设置请求成功后的跳转地址
            request.setReturnUrl(ALIPAY_RETURN_URL + orderInformation.getId());
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
     * 主动查询阿里云订单
     *
     * @param orderInformationId 顶单信息id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/21 11:51
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
     * @date 2023/10/21 15:24
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
     * @param data 支付宝返回的数据
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/21 16:01
     */
    @Override
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

                    orderInformation.setOrderStatus(CommonEnum.WAIT_EVALUATE.getMsg());
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


                    // 资源学习人数 + 1
                    Long resourceId = orderInformation.getAssociationId();
                    JSONObject object = new JSONObject();
                    object.put("event", EventConstant.resourceBuyCountAddOne);
                    object.put("resourceId", resourceId);
                    Message message = new Message(object.toJSONString().getBytes());
                    rabbitTemplate.convertAndSend(RabbitmqExchangeName.resourceUpdateDirectExchange,
                            RabbitmqRoutingName.resourceUpdateRouting, message);

                    // 插入资源购买表
                    ResourceBuy resourceBuy = new ResourceBuy();
                    resourceBuy.setResourceId(resourceId);
                    resourceBuy.setUserId(orderInformation.getUserId());
                    resourceBuy.setCreateTime(new Date());
                    resourceBuyMapper.insert(resourceBuy);
                } finally {
                    reentrantLock.unlock();
                }
            }

            } catch(Exception e){
                throw new MonkeyBlogException(R.Error, e.getMessage());
            }
            return null;
        }

    // 记录支付日志
    private void recordPayLog(Map<String, String> data) {
        // 通过消息队列插入支付日志
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.insertPayUpdateSuccessLog);
        jsonObject.put("data", JSONObject.toJSONString(data));
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.resourceInsertDirectExchange,
                RabbitmqRoutingName.resourceInsertRouting, message);
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

    /**
     * 查找已存在并且还未支付的资源, 否则建立一个课程订单返回
     *
     * @param resourceId 资源id
     * @param userId 当前购买资源用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/21 9:01
     */
    private OrderInformation judgeIsExistOrder(Long resourceId, Long userId, Integer payWay) {
        QueryWrapper<OrderInformation> orderInformationQueryWrapper = new QueryWrapper<>();
        orderInformationQueryWrapper.eq("user_id", userId);
        orderInformationQueryWrapper.eq("association_id", resourceId);
        orderInformationQueryWrapper.eq("order_type", CommonEnum.RESOURCE_ORDER.getMsg());
        orderInformationQueryWrapper.eq("order_status", CommonEnum.NOT_PAY_FEE.getMsg());
        OrderInformation orderInformation = orderInformationMapper.selectOne(orderInformationQueryWrapper);
        if (orderInformation != null) {
            return orderInformation;
        }

        // 说明未支付的订单不存在，创建一个新订单
        OrderInformation newOrder = createOrder(resourceId, userId, payWay);

        return newOrder;
    }

    /**
     * 创建一个新订单
     *
     * @param resourceId 资源id
     * @param userId 用户id
     * @param payWay 支付方式
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/21 9:14
     */
    private OrderInformation createOrder(Long resourceId, Long userId, Integer payWay) {
        Resources resources = resourcesMapper.selectById(resourceId);
        OrderInformation orderInformation = new OrderInformation();
        orderInformation.setAssociationId(resourceId);
        orderInformation.setUserId(userId);
        orderInformation.setTitle(resources.getName());
        orderInformation.setOrderStatus(CommonEnum.NOT_PAY_FEE.getMsg());
        orderInformation.setOrderType(CommonEnum.RESOURCE_ORDER.getMsg());
        // 通过订单类型得到订单图片
        QueryWrapper<ResourceConnect> resourceConnectQueryWrapper = new QueryWrapper<>();
        resourceConnectQueryWrapper.eq("resource_id", resourceId);
        resourceConnectQueryWrapper.eq("level", CommonEnum.LABEL_LEVEL_TWO.getCode());
        resourceConnectQueryWrapper.select("type");
        ResourceConnect resourceConnect = resourceConnectMapper.selectOne(resourceConnectQueryWrapper);
        orderInformation.setPicture(FileTypeEnum.getFileUrlByFileType(resourceConnect.getType()).getUrl());
        if (payWay.equals(CommonEnum.WE_CHAT_PAY.getCode())) {
            orderInformation.setPayWay(CommonEnum.WE_CHAT_PAY.getMsg());
        } else if (payWay.equals(CommonEnum.ALIPAY.getCode())) {
            orderInformation.setPayWay(CommonEnum.ALIPAY.getMsg());
        } else {
            throw new MonkeyBlogException(R.Error, TipConstant.payTypeError);
        }
        // 得到订单付款金额
        QueryWrapper<ResourceCharge> resourceChargeQueryWrapper = new QueryWrapper<>();
        resourceChargeQueryWrapper.eq("resource_id", resourceId);
        ResourceCharge resourceCharge = resourceChargeMapper.selectOne(resourceChargeQueryWrapper);
        Integer isDiscount = resourceCharge.getIsDiscount();
        if (isDiscount.equals(ResourcesEnum.IS_DISCOUNT.getCode())) {
            orderInformation.setOrderMoney(Float.parseFloat(String.format("%.1f", resourceCharge.getPrice() * resourceCharge.getDiscount())));
        } else {
            orderInformation.setOrderMoney(Float.parseFloat(String.format("%.1f", resourceCharge.getPrice())));
        }

        orderInformation.setCreateTime(new Date());
        orderInformationMapper.insert(orderInformation);
        // 将信息发送给rabbitmq并用死信交换机设置过期时间, 超过过期时间后查询订单状态
        Message message = new Message(JSONObject.toJSONString(orderInformation).getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.resourceDirectExchange,
                RabbitmqRoutingName.resourceOrderRouting, message);

        return orderInformation;
    }

    /**
     * 判断用户是否支付该资源
     *
     * @param userId 用户id
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/20 15:37
     */
    private boolean judgeUserIsPayResource(Long resourceId, Long userId) {
        QueryWrapper<ResourceBuy> resourceBuyQueryWrapper = new QueryWrapper<>();
        resourceBuyQueryWrapper.eq("resource_id", resourceId);
        resourceBuyQueryWrapper.eq("user_id", userId);
        Long selectCount = resourceBuyMapper.selectCount(resourceBuyQueryWrapper);
        if (selectCount > 0) {
            return true;
        } else {
            return false;
        }
    }
}
