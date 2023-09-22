package com.monkey.monkeyblog.rabbitmq;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.mapper.CollectContentMapper;
import com.monkey.monkeyUtils.mapper.OrderInformationMapper;
import com.monkey.monkeyUtils.mapper.RabbitmqErrorLogMapper;
import com.monkey.monkeyUtils.mapper.RefundInformationMapper;
import com.monkey.monkeyUtils.pojo.CollectContent;
import com.monkey.monkeyUtils.pojo.OrderInformation;
import com.monkey.monkeyUtils.pojo.RabbitmqErrorLog;
import com.monkey.monkeyUtils.pojo.RefundInformation;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.result.ResultStatus;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyblog.feign.UserToArticleFeignService;
import com.monkey.monkeyblog.feign.UserToCourseFeignService;
import com.monkey.monkeyblog.feign.UserToQuestionFeignService;
import com.monkey.monkeyblog.mapper.EmailCodeMapper;
import com.monkey.monkeyblog.mapper.RecentVisitUserhomeMapper;
import com.monkey.monkeyblog.pojo.EmailCode;
import com.monkey.monkeyblog.pojo.RecentVisitUserhome;
import com.monkey.monkeyblog.pojo.Vo.EmailCodeVo;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;

@Slf4j
@Component
public class RabbitmqReceiveMessage {

    @Resource
    private EmailCodeMapper emailCodeMapper;
    @Resource
    private RabbitmqErrorLogMapper rabbitmqErrorLogMapper;
    @Resource
    private OrderInformationMapper orderInformationMapper;
    @Resource
    private RefundInformationMapper refundInformationMapper;
    @Resource
    private CollectContentMapper collectContentMapper;
    @Resource
    private UserToArticleFeignService userToArticleFeignService;
    @Resource
    private UserToQuestionFeignService userToQuestionFeignService;
    @Resource
    private UserToCourseFeignService userToCourseFeignService;
    @Resource
    private RecentVisitUserhomeMapper recentVisitUserhomeMapper;

    /**
     * 把发送验证码的邮件信息存入数据库
     *
     * @param message
     * @param channel
     * @return {@link null}
     * @author wusihao
     * @date 2023/7/6 9:06
     */
    @RabbitListener(queues = RabbitmqQueueName.EMAIL_CODE_QUEUE)
    public void receiveCodeEmailMessage(Message message, Channel channel) {
        String receivedExchange = "";
        String receivedRoutingKey = "";
        EmailCodeVo emailCodeVo = null;
        long deliveryTag = -1L;
        try {
            emailCodeVo = JSONObject.parseObject(message.getBody(), EmailCodeVo.class);
            receivedExchange = message.getMessageProperties().getReceivedExchange();
            receivedRoutingKey = message.getMessageProperties().getReceivedRoutingKey();
            // deliveryTag是通道内按顺序自增的
            deliveryTag = message.getMessageProperties().getDeliveryTag();

            log.info("receive_message = {} || emailCodeVo = {}", message, emailCodeVo);
            EmailCode emailCode = new EmailCode();
            BeanUtils.copyProperties(emailCodeVo, emailCode);
            int insert = emailCodeMapper.insert(emailCode);
            // 签收消息, 第二个参数启动非批量模式，是签收当前消息
            if (insert > 0) {
                // 签收成功, 自动删除消息
                log.info("已签收到消息：{}", emailCode);
                channel.basicAck(deliveryTag, false);
            } else {
                log.error("消息签收发送错误：{}", emailCodeVo);
            }
        } catch (Exception e) {
            // 发送异常，拒绝接收消息
            // 第一个参数，消息编号
            // 第二个参数，是否批量处理消息
            // 第三个消息是否重新把消息放入队列对头
            // channel.basicNack(deliveryTag, false, true);
            /*
            我们不使用上面的方法回传参数，因为上面的方法会将消息重新放入队头，
            而我们需要将消息重新放回队尾，并设置重传机制
            怎么做呢？？？
            只要把这个消息确认，然后利用rabittemplate重新发送一次消息就可以插入队尾了
            * */
            try {
                channel.basicAck(deliveryTag, false);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            Integer tryCount = emailCodeVo.getTryCount();
            if (tryCount < MessageReSendCount.REGISTER_EMAIL_CODE) {
                // 重传消息
                emailCodeVo.setTryCount(emailCodeVo.getTryCount() + 1);
                log.error("消息接收正在进行第：{}次重传", emailCodeVo.getTryCount());
                String str = JSONObject.toJSONString(emailCodeVo);
                log.error("str = {}", str);
                try {
                    channel.basicPublish(receivedExchange, receivedRoutingKey, null, str.getBytes());
                } catch (IOException ex) {
                    log.error("消息：{}重新发送失败", emailCodeVo);
                }
            } else {
                // 已超过重传次数，将消息放入错误日志中
                RabbitmqErrorLog rabbitmqErrorLog = new RabbitmqErrorLog();
                rabbitmqErrorLog.setTryCount(tryCount);
                rabbitmqErrorLog.setRoutingKey(receivedRoutingKey);
                rabbitmqErrorLog.setContent(JSONObject.toJSONString(emailCodeVo));
                rabbitmqErrorLog.setExchange(receivedExchange);
                rabbitmqErrorLog.setCreateTime(new Date());
                rabbitmqErrorLog.setErrorCause(e.toString());
                int insert = rabbitmqErrorLogMapper.insert(rabbitmqErrorLog);
                if (insert > 0) {
                    log.error("插入错误日志成功：{}", rabbitmqErrorLog);
                } else {
                    log.error("插入日志失败：{}", rabbitmqErrorLog);
                }
            }
        }
    }

    @RabbitListener(queues = RabbitmqQueueName.EMAIL_CODE_DLX_QUEUE)
    public void receiveDlxCodeEmailMessage(Message message, Channel channel) {
        String receivedExchange = "";
        String receivedRoutingKey = "";
        EmailCodeVo emailCodeVo = null;
        long deliveryTag = -1L;
        try {
            emailCodeVo = JSONObject.parseObject(message.getBody(), EmailCodeVo.class);
            receivedExchange = message.getMessageProperties().getReceivedExchange();
            receivedRoutingKey = message.getMessageProperties().getReceivedRoutingKey();
            // deliveryTag是通道内按顺序自增的
            deliveryTag = message.getMessageProperties().getDeliveryTag();

            log.info("receive_message = {} || emailCodeVo = {}", message, emailCodeVo);
            EmailCode emailCode = new EmailCode();
            BeanUtils.copyProperties(emailCodeVo, emailCode);
            int insert = emailCodeMapper.insert(emailCode);
            // 签收消息, 第二个参数启动非批量模式，是签收当前消息
            if (insert > 0) {
                // 签收成功, 自动删除消息
                log.info("已签收到消息：{}", emailCode);
                channel.basicAck(deliveryTag, false);
            } else {
                log.error("消息签收发送错误：{}", emailCodeVo);
            }
        } catch (Exception e) {
            // 发送异常，拒绝接收消息
            // 第一个参数，消息编号
            // 第二个参数，是否批量处理消息
            // 第三个消息是否重新把消息放入队列对头
            // channel.basicNack(deliveryTag, false, true);
            /*
            我们不使用上面的方法回传参数，因为上面的方法会将消息重新放入队头，
            而我们需要将消息重新放回队尾，并设置重传机制
            怎么做呢？？？
            只要把这个消息确认，然后利用rabittemplate重新发送一次消息就可以插入队尾了
            * */
            try {
                channel.basicAck(deliveryTag, false);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            Integer tryCount = emailCodeVo.getTryCount();
            if (tryCount < MessageReSendCount.REGISTER_EMAIL_CODE) {
                // 重传消息
                emailCodeVo.setTryCount(emailCodeVo.getTryCount() + 1);
                log.error("消息接收正在进行第：{}次重传", emailCodeVo.getTryCount());
                String str = JSONObject.toJSONString(emailCodeVo);
                log.error("str = {}", str);
                try {
                    channel.basicPublish(receivedExchange, receivedRoutingKey, null, str.getBytes());
                } catch (IOException ex) {
                    log.error("消息：{}重新发送失败", emailCodeVo);
                }
            } else {
                // 已超过重传次数，将消息放入错误日志中
                RabbitmqErrorLog rabbitmqErrorLog = new RabbitmqErrorLog();
                rabbitmqErrorLog.setTryCount(tryCount);
                rabbitmqErrorLog.setRoutingKey(receivedRoutingKey);
                rabbitmqErrorLog.setContent(JSONObject.toJSONString(emailCodeVo));
                rabbitmqErrorLog.setExchange(receivedExchange);
                rabbitmqErrorLog.setCreateTime(new Date());
                rabbitmqErrorLog.setErrorCause(e.toString());
                int insert = rabbitmqErrorLogMapper.insert(rabbitmqErrorLog);
                if (insert > 0) {
                    log.error("插入错误日志成功：{}", rabbitmqErrorLog);
                } else {
                    log.error("插入日志失败：{}", rabbitmqErrorLog);
                }
            }
        }
    }

    // 正常更新队列
    @RabbitListener(queues = RabbitmqQueueName.userUpdateQueue)
    public void receiverUpdateQueue(Message message) {
        try {
            byte[] body = message.getBody();
            JSONObject data = JSONObject.parseObject(body, JSONObject.class);
            String event = data.getString("event");
            log.info("用户正常更新队列时间 ==> event : {}", event);
            if (EventConstant.collectContentCountAddOne.equals(event)) {
                // 目录收藏数 + 1
                Long collectContentId = data.getLong("collectContentId");
                collectContentCountAddOne(collectContentId);
            } else if (EventConstant.collectContentCountSubOne.equals(event)) {
                // 目录收藏数 - 1
                Long collectContentId = data.getLong("collectContentId");
                collectContentCountSubOne(collectContentId);
            } else if (EventConstant.articleCollectCountAddOne.equals(event)) {
                // 文章收藏数 + 1
                Long associateId = data.getLong("associateId");
                articleCollectCountAddOne(associateId);
            } else if (EventConstant.articleCollectCountSubOne.equals(event)) {
                // 文章收藏数 - 1
                Long associateId = data.getLong("associateId");
                articleCollectCountSubOne(associateId);
            } else if (EventConstant.courseCollectCountAddOne.equals(event)) {
                // 课程收藏数 + 1
                Long associateId = data.getLong("associateId");
                courseCollectCountAddOne(associateId);
            } else if (EventConstant.courseCollectCountSubOne.equals(event)) {
                // 课程收藏数 - 1
                Long associateId = data.getLong("associateId");
                courseCollectCountSubOne(associateId);
            } else if (EventConstant.questionCollectCountAddOne.equals(event)) {
                // 问答收藏数 + 1
                Long associateId = data.getLong("associateId");
                questionCollectCountAddOne(associateId);
            } else if (EventConstant.questionCollectCountSubOne.equals(event)) {
                // 问答收藏数 - 1
                Long associateId = data.getLong("associateId");
                questionCollectCountSubOne(associateId);
            }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
        }
    }

    // 死信更新队列
    @RabbitListener(queues = RabbitmqQueueName.userUpdateDlxQueue)
    public void receiverUpdateDlxQueue(Message message) {
        try {
            byte[] body = message.getBody();
            JSONObject data = JSONObject.parseObject(body, JSONObject.class);
            String event = data.getString("event");
            log.info("用户死信更新队列时间 ==> event : {}", event);
            if (EventConstant.collectContentCountAddOne.equals(event)) {
                // 目录收藏数 + 1
                Long collectContentId = data.getLong("collectContentId");
                collectContentCountAddOne(collectContentId);
            } else if (EventConstant.collectContentCountSubOne.equals(event)) {
                // 目录收藏数 - 1
                Long collectContentId = data.getLong("collectContentId");
                collectContentCountSubOne(collectContentId);
            } else if (EventConstant.articleCollectCountAddOne.equals(event)) {
                // 文章收藏数 + 1
                Long associateId = data.getLong("associateId");
                articleCollectCountAddOne(associateId);
            } else if (EventConstant.articleCollectCountSubOne.equals(event)) {
                // 文章收藏数 - 1
                Long associateId = data.getLong("associateId");
                articleCollectCountSubOne(associateId);
            } else if (EventConstant.courseCollectCountAddOne.equals(event)) {
                // 课程收藏数 + 1
                Long associateId = data.getLong("associateId");
                courseCollectCountAddOne(associateId);
            } else if (EventConstant.courseCollectCountSubOne.equals(event)) {
                // 课程收藏数 - 1
                Long associateId = data.getLong("associateId");
                courseCollectCountSubOne(associateId);
            } else if (EventConstant.questionCollectCountAddOne.equals(event)) {
                // 问答收藏数 + 1
                Long associateId = data.getLong("associateId");
                questionCollectCountAddOne(associateId);
            } else if (EventConstant.questionCollectCountSubOne.equals(event)) {
                // 问答收藏数 - 1
                Long associateId = data.getLong("associateId");
                questionCollectCountSubOne(associateId);
            }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
        }
    }

    // 正常插入队列
    @RabbitListener(queues = RabbitmqQueueName.userInsertQueue)
    public void receiverInsertQueue(Message message) {
        try {
            byte[] body = message.getBody();
            JSONObject data = JSONObject.parseObject(body, JSONObject.class);
            String event = data.getString("event");
            log.info("用户正常插入队列 ==》 event：{}", event);
            if (EventConstant.insertRefundOrder.equals(event)) {
                // 记录退款信息
                RefundInformation refundInformation = JSONObject.parseObject(data.getString("refundInformation"), RefundInformation.class);
                refundInformationMapper.insert(refundInformation);
            } else if (EventConstant.insertUserRecentlyView.equals(event)) {
                // 记录最近用户游览
                Long userId = data.getLong("userId");
                Long reviewId = data.getLong("reviewId");
                insertUserRecentlyView(userId, reviewId);
            }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
        }
    }

    // 死信插入队列
    @RabbitListener(queues = RabbitmqQueueName.userInsertDlxQueue)
    public void receiverInsertDlxQueue(Message message) {
        try {
            byte[] body = message.getBody();
            JSONObject data = JSONObject.parseObject(body, JSONObject.class);
            String event = data.getString("event");
            log.info("用户死信插入队列 ==》 event：{}", event);
            if (EventConstant.insertRefundOrder.equals(event)) {
                // 记录退款信息
                RefundInformation refundInformation = JSONObject.parseObject(data.getString("refundInformation"), RefundInformation.class);
                refundInformationMapper.insert(refundInformation);
            } else if (EventConstant.insertUserRecentlyView.equals(event)) {
                // 记录最近用户游览
                Long userId = data.getLong("userId");
                Long reviewId = data.getLong("reviewId");
                insertUserRecentlyView(userId, reviewId);
            }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
        }
    }

    // 正常删除队列
    @RabbitListener(queues = RabbitmqQueueName.userDeleteQueue)
    public void receiverDeleteQueue(Message message) {
        try {
            JSONObject data = JSONObject.parseObject(message.getBody(), JSONObject.class);
            String event = data.getString("event");
            log.info("用户正常删除队列: event ==> {}", event);
            if (EventConstant.deleteOrderRecord.equals(event)) {
                Long orderInformationId = data.getLong("orderInformationId");
                orderInformationMapper.deleteById(orderInformationId);
            }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
        }
    }

    // 死信删除队列
    @RabbitListener(queues = RabbitmqQueueName.userDeleteDlxQueue)
    public void reveiverDeleteDlxQueue(Message message) {
        try {
            JSONObject data = JSONObject.parseObject(message.getBody(), JSONObject.class);
            String event = data.getString("event");
            log.info("用户死信删除队列: event ==> {}", event);
            if (EventConstant.deleteOrderRecord.equals(event)) {
                Long orderInformationId = data.getLong("orderInformationId");
                orderInformationMapper.deleteById(orderInformationId);
            }
        } catch (Exception e) {
            // 将错误信息放入rabbitmq日志
            addToRabbitmqErrorLog(message, e);
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
        byte[] body = message.getBody();
        JSONObject jsonObject = JSONObject.parseObject(body, JSONObject.class);
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

    /**
     * 更新订单状态
     *
     * @param orderInformationId 订单信息id
     * @param statusType 订单状态类型
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/17 17:05
     */
    private void updateOrderStatus(Long orderInformationId, String statusType) {
        UpdateWrapper<OrderInformation> orderInformationUpdateWrapper = new UpdateWrapper<>();
        orderInformationUpdateWrapper.eq("id", orderInformationId).set("order_status", statusType);
        orderInformationMapper.update(null, orderInformationUpdateWrapper);
    }

    /**
     * 记录最近用户游览
     *
     * @param userId 作者id
     * @param reviewId 当前登录用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/18 8:38
     */
    private void insertUserRecentlyView(Long userId, Long reviewId) {
        if (userId.equals(reviewId)) {
            return ;
        }
        RecentVisitUserhome recentVisitUserhome = new RecentVisitUserhome();
        recentVisitUserhome.setBeVisitId(userId);
        recentVisitUserhome.setVisitId(reviewId);
        recentVisitUserhome.setCreateTime(new Date());
        recentVisitUserhomeMapper.insert(recentVisitUserhome);
    }

    /**
     * 问答收藏数 - 1
     *
     * @param associateId 问答id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/17 17:55
     */
    private void questionCollectCountSubOne(Long associateId) {
        R resultQuestion = userToQuestionFeignService.subQurstionViewSum(associateId);
        if (resultQuestion.getCode() != R.SUCCESS) {
            throw new MonkeyBlogException(resultQuestion.getCode(), resultQuestion.getMsg());
        }
    }

    /**
     * 问答收藏数 + 1
     *
     * @param associateId 问答id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/17 17:55
     */
    private void questionCollectCountAddOne(Long associateId) {
        R resultQuestion = userToQuestionFeignService.addQurstionViewSum(associateId);
        if (resultQuestion.getCode() != R.SUCCESS) {
            throw new MonkeyBlogException(resultQuestion.getCode(), resultQuestion.getMsg());
        }
    }

    /**
     * 课程收藏数 - 1
     *
     * @param associateId 课程id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/17 17:55
     */
    private void courseCollectCountSubOne(Long associateId) {
        R resultCourse = userToCourseFeignService.subCourseViewSum(associateId);
        if (resultCourse.getCode() != R.SUCCESS) {
            throw new MonkeyBlogException(resultCourse.getCode(), resultCourse.getMsg());
        }
    }

    /**
     * 课程收藏数 + 1
     *
     * @param associateId 课程id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/17 17:55
     */
    private void courseCollectCountAddOne(Long associateId) {
        R resultCourse = userToCourseFeignService.addCourseViewSum(associateId);
        if (resultCourse.getCode() != R.SUCCESS) {
            throw new MonkeyBlogException(resultCourse.getCode(), resultCourse.getMsg());
        }
    }

    /**
     * 文章收藏数 + 1
     *
     * @param associateId 文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/17 17:50
     */
    private void articleCollectCountAddOne(Long associateId) {
        R resultArticle = userToArticleFeignService.addUpdateArticleInfo(associateId);
        if (resultArticle.getCode() != R.SUCCESS) {
            throw new MonkeyBlogException(resultArticle.getCode(), resultArticle.getMsg());
        }
    }

    /**
     * 文章收藏数 - 1
     *
     * @param associateId 文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/17 17:50
     */
    private void articleCollectCountSubOne(Long associateId) {
        R resultArticle = userToArticleFeignService.subUpdateArticleInfo(associateId);
        if (resultArticle.getCode() != R.SUCCESS) {
            throw new MonkeyBlogException(resultArticle.getCode(), resultArticle.getMsg());
        }
    }

    /**
     * 目录收藏数 - 1
     *
     * @param collectContentId 收藏目录id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/17 17:49
     */
    private void collectContentCountSubOne(Long collectContentId) {
        UpdateWrapper<CollectContent> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", collectContentId);
        updateWrapper.setSql("collect_count = collect_count - 1");
        collectContentMapper.update(null, updateWrapper);
    }

    /**
     * 目录收藏数 + 1
     *
     * @param collectContentId 收藏目录id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/17 17:46
     */
    private void collectContentCountAddOne(Long collectContentId) {
        UpdateWrapper<CollectContent> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", collectContentId);
        updateWrapper.setSql("collect_count = collect_count + 1");
        collectContentMapper.update(null, updateWrapper);
    }
}
