package com.monkey.monkeycourse.rabbitmq;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.alibaba.nacos.shaded.com.google.gson.internal.LinkedTreeMap;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.monkey.monkeyUtils.constants.AliPayTradeStatusEnum;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.mapper.OrderInformationMapper;
import com.monkey.monkeyUtils.mapper.OrderLogMapper;
import com.monkey.monkeyUtils.mapper.RabbitmqErrorLogMapper;
import com.monkey.monkeyUtils.pojo.OrderInformation;
import com.monkey.monkeyUtils.pojo.OrderLog;
import com.monkey.monkeyUtils.pojo.RabbitmqErrorLog;
import com.monkey.monkeycourse.mapper.*;
import com.monkey.monkeycourse.pojo.*;
import com.monkey.monkeycourse.service.CoursePayService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.Date;
import java.util.HashMap;

import static com.monkey.monkeyUtils.util.DateUtils.*;

/**
 * @author: wusihao
 * @date: 2023/8/20 17:09
 * @version: 1.0
 * @description:
 */
@Slf4j
@Component
public class RabbitmqReceiverMessage {

    @Resource
    private CourseVideoBarrageMapper courseVideoBarrageMapper;
    @Resource
    private RabbitmqErrorLogMapper rabbitmqErrorLogMapper;

    @Resource
    private OrderLogMapper orderLogMapper;

    @Resource
    private CoursePayService coursePayService;

    @Resource
    private OrderInformationMapper orderInformationMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private CourseCommentMapper courseCommentMapper;
    @Resource
    private CourseMapper courseMapper;
    @Resource
    private CourseCommentLikeMapper courseCommentLikeMapper;
    @Resource
    private CourseEvaluateMapper courseEvaluateMapper;

    // 插入课程弹幕队列
    @RabbitListener(queues = RabbitmqQueueName.COURSE_VIDEO_BARRAGE_QUEUE)
    public void receiverCourseBarrageMessage(Message message, Channel channel) {
        log.info("课程弹幕队列");
        try {
            byte[] body = message.getBody();
            CourseVideoBarrage courseVideoBarrage = JSONObject.parseObject(body, CourseVideoBarrage.class);
            courseVideoBarrageMapper.insert(courseVideoBarrage);
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
        }
    }


    // 课程弹幕死信队列
    @RabbitListener(queues = RabbitmqQueueName.COURSE_VIDEO_BARRAGE_DLX_QUEUE)
    public void receiverCourseBarrageErrorMessage(Message message, Channel channel) {
        log.info("课程弹幕死信队列");
        try {
            byte[] body = message.getBody();
            CourseVideoBarrage courseVideoBarrage = JSONObject.parseObject(body, CourseVideoBarrage.class);
            courseVideoBarrageMapper.insert(courseVideoBarrage);
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
        }
    }

    // 接收课程支付记录队列
    @RabbitListener(queues = RabbitmqQueueName.COURSE_PAY_LOG_QUEUE)
    public void receiverCoursePayRecords(Message message, Channel channel) {
        log.info("课程支付日志队列");
        try {
            byte[] body = message.getBody();
            OrderLog orderLog = JSONObject.parseObject(body, OrderLog.class);
            orderLogMapper.insert(orderLog);
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
        }
    }

    // 接收课程支付记录死信队列
    @RabbitListener(queues = RabbitmqQueueName.COURCE_PAY_LOG_DLX_QUEUE)
    public void receiverCoursePayDlxRecords(Message message, Channel channel) {
        log.info("课程支付日志死信队列");
        try {
            byte[] body = message.getBody();
            OrderLog orderLog = JSONObject.parseObject(body, OrderLog.class);
            orderLogMapper.insert(orderLog);
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
        }
    }

    /**
     * 查询一小时后的订单状态
     * 根据订单号调用支付宝查单金额接口，核实订单状态
     * 如果订单未创建。则更新商户端订单状态
     * 如何订单未支付，则调用关闭订单接口，并更新订单状态
     * 如果订单已支付，则更新订单状态，记录支付日志（可能是因为支付宝支付成功请求发送失败，或内网穿透失败）
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/28 17:36
     */
    // 查询一小时后的订单状态
    @RabbitListener(queues = RabbitmqQueueName.ORDER_EXPIRE_QUEUE)
    public void receiverOrderRecords(Message message, Channel channel) {
        log.info("课程订单延迟队列");
        try {
            byte[] body = message.getBody();
            OrderInformation orderInformation = JSONObject.parseObject(body, OrderInformation.class);

            Long orderInformationId = orderInformation.getId();
            log.info("查询一小时后的订单状态：==> orderInformation = {}", orderInformation);
            String result = coursePayService.queryOrder(orderInformationId);
            if (result == null) {
                log.warn("订单未创建： ==> {}", orderInformationId);
            } else {
                // 否则解析阿里云的相应结果
                Gson gson = new Gson();
                HashMap<String, LinkedTreeMap> hashMap = gson.fromJson(result, HashMap.class);
                LinkedTreeMap alipayTradeQueryResponse = hashMap.get("alipay_trade_query_response");
                String tradeStatus = (String) alipayTradeQueryResponse.get("trade_status");
                if (AliPayTradeStatusEnum.WAIT_BUYER_PAY.getStatus().equals(tradeStatus)) {
                    log.warn("课程模块核实订单未支付 ===> {}", orderInformationId);

                    // 若未支付，调用阿里云关单接口
                    coursePayService.closeOrder(orderInformationId);
                    // 更新订单状态。
                    orderInformation.setOrderStatus(CommonEnum.EXCEED_TIME_AlREADY_CLOSE.getMsg());
                    orderInformationMapper.updateById(orderInformation);
                } else if (AliPayTradeStatusEnum.TRADE_SUCCESS.getStatus().equals(tradeStatus)) {
                    // 如果订单已支付，则更新订单状态（可能是因为支付宝支付成功请求发送失败，或内网穿透失败）
                    log.info("课程模块核实订单已支付 ==> {}", orderInformationId);
                    OrderInformation information = orderInformationMapper.selectById(orderInformationId);
                    String orderStatus = information.getOrderStatus();

                    if (CommonEnum.NOT_PAY_FEE.getMsg().equals(orderStatus)) {
                        // 更新订单接口
                        information.setOrderStatus(CommonEnum.WAIT_EVALUATE.getMsg());
                        orderInformationMapper.updateById(information);

                        // 记录日志
                        String sendPayDate = (String) alipayTradeQueryResponse.get("send_pay_date");
                        Date payTime = stringToDate(sendPayDate, DATE_TIME_PATTERN);

                        String totalAmount = (String) alipayTradeQueryResponse.get("total_amount");
                        String outTradeNo = (String) alipayTradeQueryResponse.get("out_trade_no");
                        String tradeStatus1 = (String) alipayTradeQueryResponse.get("trade_status");
                        String tradeNo = (String) alipayTradeQueryResponse.get("trade_no");
                        String dataJson = JSONObject.toJSONString(alipayTradeQueryResponse);
                        OrderLog orderLog = new OrderLog();
                        orderLog.setId(Long.parseLong(outTradeNo));

                        orderLog.setTradeStatus(tradeStatus1);
                        orderLog.setTradeType(CommonEnum.COMPUTER_PAY.getMsg());
                        orderLog.setPayType(CommonEnum.ALIPAY.getMsg());

                        orderLog.setNoticeParams(dataJson);

                        orderLog.setTransactionId(tradeNo);
                        orderLog.setCreateTime(new Date());
                        orderLog.setPayTime(payTime);
                        orderLog.setPayMoney(Float.parseFloat(totalAmount));

                        byte[] bytes = JSONObject.toJSONString(orderLog).getBytes();
                        Message message1 = new Message(bytes);
                        // 通过消息队列更新日志
                        rabbitTemplate.convertAndSend(RabbitmqExchangeName.COURSE_DIRECT_EXCHANGE,
                                RabbitmqRoutingName.PAY_LOG_ROUTING, message1);
                    }
                }
            }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
        }

    }

    // 课程模块rabbitmq删除队列
    @RabbitListener(queues = RabbitmqQueueName.courseDeleteQueue)
    public void receiverDeleteQueue(Message message) {
        try {
            JSONObject data = JSONObject.parseObject(message.getBody(), JSONObject.class);
            String event = data.getString("event");
            log.info("课程模块rabbitmq删除队列：event ==> {}", event);
            if (event.equals(EventConstant.deleteCourseComment)) {
                Long courseCommentId = data.getLong("courseCommentId");
                Long parentId = data.getLong("parentId");
                Long courseId = data.getLong("courseId");
                deleteCourseComment(courseCommentId, parentId, courseId);
            } else if (EventConstant.deleteCourseVideoBarrage.equals(event)) {
                // 删除视频弹幕
                Long barrageId = data.getLong("barrageId");
                courseVideoBarrageMapper.deleteById(barrageId);
            }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
        }
    }

    // 课程模块rabbitmq死信删除队列
    @RabbitListener(queues = RabbitmqQueueName.courseDeleteDlxQueue)
    public void receiverDlxDeleteQueue(Message message) {
        try {
            JSONObject data = JSONObject.parseObject(message.getBody(), JSONObject.class);
            String event = data.getString("event");
            log.info("课程模块rabbitmq死信删除队列：event ==> {}", event);
            if (event.equals(EventConstant.deleteCourseComment)) {
                Long courseCommentId = data.getLong("courseCommentId");
                Long parentId = data.getLong("parentId");
                Long courseId = data.getLong("courseId");
                deleteCourseComment(courseCommentId, parentId, courseId);
            } else if (EventConstant.deleteCourseVideoBarrage.equals(event)) {
                // 删除视频弹幕
                Long barrageId = data.getLong("barrageId");
                courseVideoBarrageMapper.deleteById(barrageId);
            }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
        }
    }

    // 课程模块rabbitmq更新队列
    @RabbitListener(queues = RabbitmqQueueName.courseUpdateQueue)
    public void receiverUpdateQueue(Message message) {
        try {
            JSONObject data = JSONObject.parseObject(message.getBody(), JSONObject.class);
            String event = data.getString("event");
            log.info("课程模块rabbitmq更新队列：event ==> {}", event);
            if (EventConstant.courseCommentCountAddOne.equals(event)) {
                // 课程评论数 + 1
                Long courseId = data.getLong("courseId");
                courseCommentCountAddOne(courseId);
            } else if (EventConstant.updateCourseCurationComment.equals(event)) {
                // 更新精选课程评论
                CourseComment courseComment = JSONObject.parseObject(data.getString("courseComment"), CourseComment.class);
                updateCourseCurationComment(courseComment);
            } else if (EventConstant.courseViewCountAddOne.equals(event)) {
                // 课程游览数 + 1
                Long courseId = data.getLong("courseId");
                courseViewCountAddOne(courseId);
            } else if (EventConstant.courseStudyPeopleAddOne.equals(event)) {
                // 课程学习人数 + 1;
                Long courseId = data.getLong("courseId");
                courseStudyPeopleAddOne(courseId);
            } else if (EventConstant.updatePayOrder.equals(event)) {
                // 更新支付订单
                OrderInformation orderInformation = JSONObject.parseObject(data.getString("orderInformation"), OrderInformation.class);
                orderInformationMapper.updateById(orderInformation);
            } else if (EventConstant.courseViewCountSubOne.equals(event)) {
                // 课程游览数 - 1
                Long courseId = data.getLong("courseId");
                courseViewCountSubOne(courseId);
            }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
        }
    }


    // 课程模块rabbitmq死信更新队列
    @RabbitListener(queues = RabbitmqQueueName.courseUpdateDlxQueue)
    public void receiverDlxUpdateQueue(Message message) {
        try {
            JSONObject data = JSONObject.parseObject(message.getBody(), JSONObject.class);
            String event = data.getString("event");
            log.info("课程模块rabbitmq死信更新队列：event ==> {}", event);
            if (EventConstant.courseCommentCountAddOne.equals(event)) {
                // 课程评论数 + 1
                Long courseId = data.getLong("courseId");
                courseCommentCountAddOne(courseId);
            } else if (EventConstant.updateCourseCurationComment.equals(event)) {
                // 更新精选课程评论
                CourseComment courseComment = JSONObject.parseObject(data.getString("courseComment"), CourseComment.class);
                updateCourseCurationComment(courseComment);
            } else if (EventConstant.courseViewCountAddOne.equals(event)) {
                // 课程游览数 + 1
                Long courseId = data.getLong("courseId");
                courseViewCountAddOne(courseId);
            } else if (EventConstant.courseStudyPeopleAddOne.equals(event)) {
                // 课程学习人数 + 1;
                Long courseId = data.getLong("courseId");
                courseStudyPeopleAddOne(courseId);
            } else if (EventConstant.updatePayOrder.equals(event)) {
                // 更新支付订单
                OrderInformation orderInformation = JSONObject.parseObject(data.getString("orderInformation"), OrderInformation.class);
                orderInformationMapper.updateById(orderInformation);
            } else if (EventConstant.courseViewCountSubOne.equals(event)) {
                // 课程游览数 - 1
                Long courseId = data.getLong("courseId");
                courseViewCountSubOne(courseId);
            }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
        }
    }


    // 课程模块rabbitmq插入队列
    @RabbitListener(queues = RabbitmqQueueName.courseInsertQueue)
    public void receiverInsertQueue(Message message) {
        try {
            JSONObject data = JSONObject.parseObject(message.getBody(), JSONObject.class);
            String event = data.getString("event");
            log.info("课程模块rabbitmq插入队列：event ==> {}", event);
            if (EventConstant.courseCommentLike.equals(event)) {
                Long courseCommentId = data.getLong("courseCommentId");
                Long userId = data.getLong("userId");
                courseCommentLike(courseCommentId, userId);
            } else if (EventConstant.insertCourseEvaluate.equals(event)) {
                CourseEvaluate courseEvaluate = JSONObject.parseObject(data.getString("courseEvaluate"), CourseEvaluate.class);
                courseEvaluateMapper.insert(courseEvaluate);
            } else if (EventConstant.insertCoursePayLog.equals(event)) {
                HashMap<String, String> hashMap = JSONObject.parseObject(data.getString("data"), new TypeReference<HashMap<String, String>>() {});
                insertCoursePayLog(hashMap);
            }

        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
        }
    }


    // 课程模块rabbitmq死信插入队列
    @RabbitListener(queues = RabbitmqQueueName.courseInsertDlxQueue)
    public void receiverDlxInsertQueue(Message message) {
        try {
            JSONObject data = JSONObject.parseObject(message.getBody(), JSONObject.class);
            String event = data.getString("event");
            log.info("课程模块rabbitmq死信插入队列：event ==> {}", event);
            if (EventConstant.courseCommentLike.equals(event)) {
                Long courseCommentId = data.getLong("courseCommentId");
                Long userId = data.getLong("userId");
                courseCommentLike(courseCommentId, userId);
            } else if (EventConstant.insertCourseEvaluate.equals(event)) {
                CourseEvaluate courseEvaluate = JSONObject.parseObject(data.getString("courseEvaluate"), CourseEvaluate.class);
                courseEvaluateMapper.insert(courseEvaluate);
            } else if (EventConstant.insertCoursePayLog.equals(event)) {
                HashMap<String, String> hashMap = JSONObject.parseObject(data.getString("data"), new TypeReference<HashMap<String, String>>() {});
                insertCoursePayLog(hashMap);

            }

        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
        }
    }

    /**
     * 插入支付日志
     *
     * @param data 数据
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/16 15:39
     */
    private void insertCoursePayLog(HashMap<String, String> data) {
        String gmtCreate = data.get("gmt_create");
        String gmtPayment = data.get("gmt_payment");
        Date payTime = stringToDate(gmtPayment, DATE_TIME_PATTERN);
        Date createTime = stringToDate(gmtCreate, DATE_TIME_PATTERN);

        String totalAmount = data.get("total_amount");
        String outTradeNo = data.get("out_trade_no");
        String tradeStatus = data.get("trade_status");
        String tradeNo = data.get("trade_no");
        String dataJson = JSONObject.toJSONString(data);
        OrderLog orderLog = new OrderLog();
        orderLog.setCreateTime(createTime);
        orderLog.setPayTime(payTime);

        orderLog.setPayMoney(Float.parseFloat(totalAmount));

        orderLog.setId(Long.parseLong(outTradeNo));

        orderLog.setTradeStatus(tradeStatus);
        orderLog.setTradeType(CommonEnum.COMPUTER_PAY.getMsg());
        orderLog.setPayType(CommonEnum.ALIPAY.getMsg());

        orderLog.setNoticeParams(dataJson);

        orderLog.setTransactionId(tradeNo);

        orderLog.setOrderType(CommonEnum.COURSE_ORDER.getMsg());

        orderLogMapper.insert(orderLog);
    }

    /**
     * 课程游览数 - 1
     *
     * @param courseId 课程id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/16 16:14
     */
    private void courseViewCountSubOne(Long courseId) {
        UpdateWrapper<Course> courseUpdateWrapper = new UpdateWrapper<>();
        courseUpdateWrapper.eq("id", courseId);
        courseUpdateWrapper.setSql("view_count = view_count - 1");
        courseMapper.update(null , courseUpdateWrapper);
    }

    /**
     * 课程学习人数 + 1
     *
     * @param courseId 课程id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/16 15:44
     */
    private void courseStudyPeopleAddOne(Long courseId) {
        UpdateWrapper<Course> courseUpdateWrapper = Wrappers.update();
        courseUpdateWrapper.eq("id", courseId).setSql("study_count = study_count + 1");
        courseMapper.update(null, courseUpdateWrapper);
    }

    /**
     * 课程游览数 + 1
     *
     * @param courseId 课程id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/16 15:10
     */
    private void courseViewCountAddOne(Long courseId) {
        UpdateWrapper<Course> courseUpdateWrapper = new UpdateWrapper<>();
        courseUpdateWrapper.eq("id", courseId);
        courseUpdateWrapper.setSql("view_count = view_count + 1");
        courseMapper.update(null , courseUpdateWrapper);
    }


    /**
     * 更新精选课程评论
     *
     * @param courseComment 课程评论类
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/16 15:00
     */
    private void updateCourseCurationComment(CourseComment courseComment) {
        UpdateWrapper<CourseComment> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", courseComment.getId());
        courseCommentMapper.update(courseComment, updateWrapper);
    }

    /**
     * 课程评论点赞
     *
     * @param courseCommentId 课程评论id
     * @param userId 当前登录用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/16 14:40
     */
    @Transactional(rollbackFor = Exception.class)
    public void courseCommentLike(Long courseCommentId, Long userId) {
        QueryWrapper<CourseCommentLike> courseCommentLikeQueryWrapper = new QueryWrapper<>();
        courseCommentLikeQueryWrapper.eq("user_id", userId);
        courseCommentLikeQueryWrapper.eq("course_comment_id", courseCommentId);
        CourseCommentLike courseCommentLike = courseCommentLikeMapper.selectOne(courseCommentLikeQueryWrapper);
        if (courseCommentLike == null) {
            // 加入文章评论表
            CourseCommentLike courseCommentLike1 = new CourseCommentLike();
            courseCommentLike1.setUserId(userId);
            courseCommentLike1.setCourseCommentId(courseCommentId);
            courseCommentLike1.setCreateTime(new Date());
            courseCommentLikeMapper.insert(courseCommentLike1);
            // 文章评论点赞数 + 1
            UpdateWrapper<CourseComment> courseCommentUpdateWrapper = new UpdateWrapper<>();
            courseCommentUpdateWrapper.eq("id", courseCommentId);
            courseCommentUpdateWrapper.setSql("like_count = like_count + 1");
            courseCommentMapper.update(null, courseCommentUpdateWrapper);
        } else {
            // 从文章评论表点赞表中删除
            courseCommentLikeMapper.deleteById(courseCommentLike);
            // 点赞数 - 1
            UpdateWrapper<CourseComment> courseCommentUpdateWrapper = new UpdateWrapper<>();
            courseCommentUpdateWrapper.eq("id", courseCommentId);
            courseCommentUpdateWrapper.setSql("like_count = like_count - 1");
            courseCommentMapper.update(null, courseCommentUpdateWrapper);
        }
    }

    /**
     * 课程评论数 + 1
     *
     * @param courseId 课程id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/16 14:25
     */
    private void courseCommentCountAddOne(Long courseId) {
        UpdateWrapper<Course> courseUpdateWrapper = new UpdateWrapper<>();
        courseUpdateWrapper.eq("id", courseId);
        courseUpdateWrapper.setSql("comment_count = comment_count + 1");
        courseMapper.update(null, courseUpdateWrapper);
    }

    /**
     * 删除课程评论
     *
     * @param courseCommentId 课程评论id
     * @param parentId 课程评论父id
     * @param courseId 课程id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/16 11:45
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteCourseComment(Long courseCommentId, Long parentId, Long courseId) {
        if (parentId == (long)CommonEnum.ONE_LEVEL_COMMENT.getCode()) {
            // 说明为一级评论，需要删除它的全部子评论
            // 1首先删除它的全部子评论
            QueryWrapper<CourseComment> courseCommentQueryWrapper = new QueryWrapper<>();
            courseCommentQueryWrapper.eq("parent_id", courseCommentId );
            int delete = courseCommentMapper.delete(courseCommentQueryWrapper);
            // 删除本身
            int deleteById = courseCommentMapper.deleteById(courseCommentId);

            // 课程评论数减去 delete + deleteById
            UpdateWrapper<Course> courseUpdateWrapper = new UpdateWrapper<>();
            courseUpdateWrapper.eq("id", courseId);
            courseUpdateWrapper.setSql("comment_count = comment_count - " + (delete + deleteById));
            courseMapper.update(null, courseUpdateWrapper);
        } else {
            // 说明不为一级评论，只要删除当前评论即可
            courseCommentMapper.deleteById(courseCommentId);
            // 课程评论数 - 1
            UpdateWrapper<Course> courseUpdateWrapper = new UpdateWrapper<>();
            courseUpdateWrapper.eq("id", courseId);
            courseUpdateWrapper.setSql("comment_count = comment_count - 1");
            courseMapper.update(null, courseUpdateWrapper);
        }
    }

    /**
     * 将错误信息放入rabbitmq错误日志
     *
     * @param message rabbitmq的消息
     * @param e  错误的异常情况
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/12 7:57
     */
    private void addToRabbitmqErrorLog(Message message, Exception e) {
        MessageProperties messageProperties = message.getMessageProperties();
        String receivedRoutingKey = messageProperties.getReceivedRoutingKey();
        String receivedExchange = messageProperties.getReceivedExchange();
        JSONObject jsonObject = JSONObject.parseObject(message.getBody(), JSONObject.class);
        String event = jsonObject.getString("event");
        log.error("发送错误事件: event ==> {}, 错误原因为 ==> {}", event, e.getMessage());
        RabbitmqErrorLog rabbitmqErrorLog = new RabbitmqErrorLog();
        rabbitmqErrorLog.setContent(jsonObject.toJSONString());
        rabbitmqErrorLog.setRoutingKey(receivedRoutingKey);
        rabbitmqErrorLog.setExchange(receivedExchange);
        rabbitmqErrorLog.setCreateTime(new Date());
        rabbitmqErrorLog.setErrorCause(e.getMessage());
        rabbitmqErrorLogMapper.insert(rabbitmqErrorLog);
    }
}
