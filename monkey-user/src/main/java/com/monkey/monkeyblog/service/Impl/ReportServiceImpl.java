package com.monkey.monkeyblog.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.mapper.ReportTypeMapper;
import com.monkey.monkeyUtils.pojo.ReportType;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyblog.constant.UserEnum;
import com.monkey.monkeyblog.rabbitmq.EventConstant;
import com.monkey.monkeyblog.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeyblog.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeyblog.redis.RedisKeyAndExpireEnum;
import com.monkey.monkeyblog.service.ReportService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: wusihao
 * @date: 2023/11/1 16:24
 * @version: 1.0
 * @description:
 */
@Service
public class ReportServiceImpl implements ReportService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private ReportTypeMapper reportTypeMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;
    /**
     * 查询一级举报类型集合
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/1 16:32
     */
    @Override
    public R queryOneReportType() {
        String redisKey = RedisKeyAndExpireEnum.ONE_REPORT_TYPE.getKeyName();
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisKey))) {
            return R.ok(JSONObject.parse(stringRedisTemplate.opsForValue().get(redisKey)));
        }

        QueryWrapper<ReportType> reportTypeQueryWrapper = new QueryWrapper<>();
        reportTypeQueryWrapper.orderByAsc("sort");
        reportTypeQueryWrapper.eq("level", UserEnum.ONE_REPORT_TYPE.getCode());
        List<ReportType> reportTypes = reportTypeMapper.selectList(reportTypeQueryWrapper);

        stringRedisTemplate.opsForValue().set(redisKey, JSONObject.toJSONString(reportTypes));
        stringRedisTemplate.expire(redisKey, RedisKeyAndExpireEnum.ONE_REPORT_TYPE.getTimeUnit(), TimeUnit.DAYS);
        return R.ok(reportTypes);
    }

    /**
     * 查询二级举报类型集合
     *
     * @param oneReportTypeId 一级举报类型id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/1 17:01
     */
    @Override
    public R queryTwoReportType(Long oneReportTypeId) {
        String redisKey = RedisKeyAndExpireEnum.TWO_REPORT_TYPE.getKeyName() + oneReportTypeId;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisKey))) {
            return R.ok(JSONObject.parse(stringRedisTemplate.opsForValue().get(redisKey)));
        }

        QueryWrapper<ReportType> reportTypeQueryWrapper = new QueryWrapper<>();
        reportTypeQueryWrapper.eq("parent_id", oneReportTypeId);
        reportTypeQueryWrapper.orderByAsc("sort");
        List<ReportType> reportTypes = reportTypeMapper.selectList(reportTypeQueryWrapper);
        stringRedisTemplate.opsForValue().set(redisKey, JSONObject.toJSONString(reportTypes));
        stringRedisTemplate.expire(redisKey, RedisKeyAndExpireEnum.TWO_REPORT_TYPE.getTimeUnit(), TimeUnit.DAYS);
        return R.ok(reportTypes);
    }

    /**
     * 提交举报内容类型
     *
     * @param userId 登录用户id
     * @param oneReportTypeId 一级举报类型id
     * @param twoReportTypeId 二级举报类型id
     * @param reportDetail 举报内容
     * @param reportContentType 举报类型
     * @param reportContentAssociationId 举报关联id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/1 17:54
     */
    @Override
    public R submitReportContentType(long userId,
                                     Long oneReportTypeId,
                                     String twoReportTypeId,
                                     String reportDetail,
                                     Integer reportContentType,
                                     Long reportContentAssociationId) {
        JSONObject data = new JSONObject();
        data.put("event", EventConstant.insertReportContent);
        data.put("oneReportTypeId", oneReportTypeId);
        data.put("twoReportTypeId", twoReportTypeId);
        data.put("reportDetail", reportDetail);
        data.put("reportContentType", reportContentType);
        data.put("reportContentAssociationId", reportContentAssociationId);
        data.put("userId", userId);
        Message message = new Message(data.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.userInsertDirectExchange,
                RabbitmqRoutingName.userInsertRouting, message);
        return R.ok();
    }

    /**
     * 提交举报评论类型
     *
     * @param userId 登录用户id
     * @param oneReportTypeId 一级举报类型id
     * @param twoReportTypeId 二级举报类型id
     * @param reportDetail 举报内容
     * @param reportCommentType 举报类型
     * @param reportCommentAssociationId 举报关联id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/2 18:22
     */
    @Override
    public R submitReportCommentType(long userId,
                                     Long oneReportTypeId,
                                     String twoReportTypeId,
                                     String reportDetail,
                                     Integer reportCommentType,
                                     Long reportCommentAssociationId) {
        System.out.println(reportCommentAssociationId);
        JSONObject data = new JSONObject();
        data.put("event", EventConstant.insertReportComment);
        data.put("oneReportTypeId", oneReportTypeId);
        data.put("twoReportTypeId", twoReportTypeId);
        data.put("reportDetail", reportDetail);
        data.put("reportCommentType", reportCommentType);
        data.put("reportCommentAssociationId", reportCommentAssociationId);
        data.put("userId", userId);
        Message message = new Message(data.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.userInsertDirectExchange,
                RabbitmqRoutingName.userInsertRouting, message);
        return R.ok();
    }
}
