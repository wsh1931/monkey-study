package com.monkey.monkeycourse.service.impl;

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
import com.monkey.monkeyUtils.constants.ExceptionEnum;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.pojo.OrderInformation;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycourse.constant.TipConstant;
import com.monkey.monkeycourse.mapper.CourseBuyMapper;
import com.monkey.monkeycourse.mapper.CourseMapper;
import com.monkey.monkeyUtils.mapper.OrderInformationMapper;
import com.monkey.monkeycourse.pojo.Course;
import com.monkey.monkeycourse.pojo.CourseBuy;
import com.monkey.monkeycourse.rabbitmq.EventConstant;
import com.monkey.monkeycourse.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeycourse.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeycourse.service.CoursePayService;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import com.monkey.monkeyUtils.mapper.UserMapper;
import com.monkey.monkeyUtils.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import static com.monkey.monkeyUtils.util.DateUtils.*;
import static com.monkey.monkeycourse.util.CommonMethods.getTwoFloatBySixFloat;
import static com.monkey.monkeycourse.util.ConstantPropertiesUtlis.*;

/**
 * @author: wusihao
 * @date: 2023/8/23 15:06
 * @version: 1.0
 * @description:
 */
@Service
@Slf4j
public class CoursePayServiceImpl implements CoursePayService {
    @Resource
    private CourseMapper courseMapper;
    @Resource
    private UserMapper userMapper;

    @Resource
    private AlipayClient alipayClient;
    @Resource
    private OrderInformationMapper orderInformationMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private CourseBuyMapper courseBuyMapper;

    private final ReentrantLock reentrantLock = new ReentrantLock();

    /**
     * 通过课程id得到课程信息
     *
     * @param courseId 课程id
     * @param userId 当前登录用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/23 15:16
     */
    @Override
    public R getCourseInfoByCourseId(long courseId, long userId) {
        Course course = courseMapper.selectById(courseId);
        Float price = course.getCoursePrice();
        Float discount = course.getDiscount();
        if (discount != null) {
//                    courseCardVo.setPrice(String.valueOf(price * discount * 0.1));
            // 截取小数点后两位
            String str = String.valueOf(price * discount * 0.1);
            int index = str.indexOf('.');
            if (index != -1 && index + 3 <= str.length()) {
                course.setDiscountPrice(str.substring(0, index + 3));
            } else {
                course.setDiscountPrice(str);
            }
        } else {
            course.setDiscountPrice(String.valueOf(price));
        }

        // 判断该用户是否注册QQ邮箱
        User user = userMapper.selectById(userId);
        String email = user.getEmail();
        if (email == null || "".equals(email)) {
            course.setHasEmail(CommonEnum.NOT_REGISTER_EMAIL.getCode());
        } else {
            course.setEmail(email);
            course.setHasEmail(CommonEnum.ALREADY_REGISTER_EMAIL.getCode());
        }

        return R.ok(course);
    }

    /**
     * 统一下单并支付页面接口
     * <p>
     * 支付宝开放平台接收request请求对象后
     * 会为开发者生成一个html形式的form表单，包含自动提交的脚本
     * 将form表单字符串返回给前端程序，前端程序会自动调用提交表单的脚本，进行表单的提交
     * 此时表单会自动提交到action属性所指向的支付宝开放平台中，为用户展示一个支付页面
     *
     * @param courseId          课程id
     * @param isSelectChargeWay 支付方式
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/24 14:02
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public R tradePagePay(long courseId, Integer isSelectChargeWay) {
        // 判断用户是否支付该课程
        boolean isPay = judgeUserIsPayed(courseId);
        if (isPay) {
            return R.error(TipConstant.userAlreadyPayed);
        }
        try {
            // 查找已存在并且还未支付的课程, 否则建立一个课程订单返回
            OrderInformation orderInformation = getCourseOrderStatus(courseId, isSelectChargeWay);

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
            bizContent.put("total_amount", getTwoFloatBySixFloat(orderInformation.getOrderMoney()));
            //订单标题，不可使用特殊符号
            bizContent.put("subject", orderInformation.getTitle());
            //电脑网站支付场景固定传值FAST_INSTANT_TRADE_PAY
            bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");

//            // 设置订单过期时间(一天)
//            bizContent.put("time_expire", format(addDateDays(new Date(), 1), DATE_TIME_PATTERN));

            /******可选参数******/

            //// 商品明细信息，按需传入
            //JSONArray goodsDetail = new JSONArray();
            //JSONObject goods1 = new JSONObject();
            //goods1.put("goods_id", "goodsNo1");
            //goods1.put("goods_name", "子商品1");
            //goods1.put("quantity", 1);
            //goods1.put("price", 0.01);
            //goodsDetail.add(goods1);
            //bizContent.put("goods_detail", goodsDetail);

            //// 扩展信息，按需传入
            //JSONObject extendParams = new JSONObject();
            //extendParams.put("sys_service_provider_id", "2088511833207846");
            //bizContent.put("extend_params", extendParams);

            request.setBizContent(bizContent.toString());

            // 执行请求调用支付宝接口
            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
            if(response.isSuccess()){
                return R.ok(response.getBody());
            } else {
                throw new MonkeyBlogException(R.Error, CommonConstant.createTradePayFail);
            }
        } catch (AlipayApiException e) {
            log.error("创建交易支付失败：==》 {}", e.getMessage());
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 判断用户是否已支付该课程
     *
     * @param courseId 课程id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/14 9:55
     */
    private boolean judgeUserIsPayed(long courseId) {
        QueryWrapper<CourseBuy> courseBuyQueryWrapper = new QueryWrapper<>();
        courseBuyQueryWrapper.eq("course_id", courseId);
        courseBuyQueryWrapper.eq("user_id", Long.parseLong(JwtUtil.getUserId()));
        Long selectCount = courseBuyMapper.selectCount(courseBuyQueryWrapper);
        if (selectCount > 0) {
            return true;
        }

        return false;
    }

    /**
     * 下单支付后执行的接口, 支付宝以 POST 形式发送请求
     *
     * @param data 支付宝传来的参数
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/25 10:52
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String finishPayNotice(Map<String, String> data) {
        try {
            // 进行异步通知的验签
            //调用SDK验证签名
            boolean signVerified = AlipaySignature.rsaCheckV1(data,
                    ALIPAY_PUBLIC_KEY,
                    AlipayConstants.CHARSET_UTF8, AlipayConstants.SIGN_TYPE_RSA2);
            if(!signVerified){
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

                    // 课程学习人数 + 1
                    Long courseId = orderInformation.getAssociationId();
                    JSONObject object = new JSONObject();
                    object.put("event", EventConstant.courseStudyPeopleAddOne);
                    object.put("courseId", courseId);
                    Message message = new Message(object.toJSONString().getBytes());
                    rabbitTemplate.convertAndSend(RabbitmqExchangeName.courseUpdateDirectExchange,
                            RabbitmqRoutingName.courseUpdateRouting, message);

                    // 插入课程购买人员
                    CourseBuy courseBuy = new CourseBuy();
                    courseBuy.setCourseId(courseId);
                    courseBuy.setUserId(orderInformation.getUserId());
                    courseBuy.setCreateTime(new Date());
                    courseBuyMapper.insert(courseBuy);
                }  finally {
                    reentrantLock.unlock();
                }
            }


        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
        return "success";
    }


    // 记录支付日志
    public void recordPayLog(Map<String, String> data) {
        // 通过消息队列插入支付日志
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.insertCoursePayLog);
        jsonObject.put("data", JSONObject.toJSONString(data));
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.courseInsertDirectExchange,
                RabbitmqRoutingName.courseInsertRouting, message);
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
     * @date 2023/8/25 11:09
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
     * 查找已存在并且还未支付的课程, 否则建立一个课程订单返回
     *
     * @param courseId 课程id
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/24 14:53
     */
    private OrderInformation getCourseOrderStatus(long courseId, Integer isSelectChargeWay) {
        long userId = Long.parseLong(JwtUtil.getUserId());
        QueryWrapper<OrderInformation> orderQueryWrapper = new QueryWrapper<>();
        orderQueryWrapper.eq("user_id", userId);
        orderQueryWrapper.eq("association_id", courseId);
        orderQueryWrapper.eq("order_type", CommonEnum.COURSE_ORDER.getMsg());
        orderQueryWrapper.eq("order_status", CommonEnum.NOT_PAY_FEE.getMsg());
        OrderInformation orderInformation = orderInformationMapper.selectOne(orderQueryWrapper);
        if (orderInformation != null) {
            // 说明该订单未支付， 返回支付订单
            return orderInformation;
        } else {
            Course course = courseMapper.selectById(courseId);
            // 说明该订单不存在，新建立一个订单
            OrderInformation newOrderInformation = new OrderInformation();
            newOrderInformation.setAssociationId(courseId);
            newOrderInformation.setOrderType(CommonEnum.COURSE_ORDER.getMsg());
            newOrderInformation.setOrderStatus(CommonEnum.NOT_PAY_FEE.getMsg());
            newOrderInformation.setUserId(userId);
            if (CommonEnum.ALIPAY.getCode().equals(isSelectChargeWay)) {
                newOrderInformation.setPayWay(CommonEnum.ALIPAY.getMsg());
            } else if (CommonEnum.WE_CHAT_PAY.getCode().equals(isSelectChargeWay)) {
                newOrderInformation.setPayWay(CommonEnum.WE_CHAT_PAY.getMsg());
            }
            newOrderInformation.setOrderMoney(Float.parseFloat(getTwoFloatBySixFloat((float) (course.getCoursePrice() * course.getDiscount() * 0.1))));
            newOrderInformation.setTitle(course.getTitle());
            newOrderInformation.setCreateTime(new Date());
            newOrderInformation.setPicture(course.getPicture());
            orderInformationMapper.insert(newOrderInformation);

            // 将信息发送给rabbitmq并用死信交换机设置过期时间, 超过过期时间后查询订单状态
            byte[] bytes = JSONObject.toJSONBytes(newOrderInformation);
            Message message = new Message(bytes);
            rabbitTemplate.convertAndSend(RabbitmqExchangeName.COURSE_DIRECT_EXCHANGE, RabbitmqRoutingName.ORDER_ROUTING, message);

            return newOrderInformation;
        }
    }

    /**
     * 主动查询阿里云订单
     *
     * @param orderInformationId 顶单信息id
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/28 10:28
     */
    @Override
    public String queryOrder(Long orderInformationId) {
        try {
            log.info("课程查单接口调用： ==> {}", orderInformationId);

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
     * 调用支付宝提供的统一交易关闭接口
     *
     * @param orderInformationId 订单id
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/28 9:24
     */
    public void closeOrder(long orderInformationId) {
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
}
