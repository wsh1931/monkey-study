package com.monkey.monkeyresource.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import com.monkey.monkeyUtils.util.DateSelfUtils;
import com.monkey.monkeyUtils.util.DateUtils;
import com.monkey.monkeyresource.constant.FileTypeEnum;
import com.monkey.monkeyresource.mapper.*;
import com.monkey.monkeyresource.pojo.*;
import com.monkey.monkeyresource.rabbitmq.EventConstant;
import com.monkey.monkeyresource.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeyresource.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeyresource.service.UserFeignService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author: wusihao
 * @date: 2023/10/22 16:26
 * @version: 1.0
 * @description:
 */
@Service
public class UserFeignServiceImpl implements UserFeignService {

    @Resource
    private ResourceBuyMapper resourceBuyMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private ResourcesMapper resourcesMapper;
    @Resource
    private ResourceCommentMapper resourceCommentMapper;
    @Resource
    private ResourceStatisticsMapper resourceStatisticsMapper;
    /**
     * 删除用户购买资源记录
     *
     * @param userId 用户id
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/22 16:32
     */
    @Override
    public R deleteUserBuyResource(Long userId, Long resourceId, Float money, Date payTime) {
        QueryWrapper<ResourceBuy> resourceBuyQueryWrapper = new QueryWrapper<>();
        resourceBuyQueryWrapper.eq("user_id", userId);
        resourceBuyQueryWrapper.eq("resource_id", resourceId);
        int delete = resourceBuyMapper.delete(resourceBuyQueryWrapper);

        // 资源用户购买数 - 1
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.resourceBuyCountSubOne);
        jsonObject.put("resourceId", resourceId);
        jsonObject.put("money", money);
        jsonObject.put("payTime", payTime);
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.resourceUpdateDirectExchange,
                RabbitmqRoutingName.resourceUpdateRouting, message);
        return R.ok(delete);
    }

    /**
     * 资源收藏数 + 1
     *
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/24 14:29
     */
    @Override
    public R resourceCollectCountAddOne(Long resourceId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.resourceCollectCountAddOne);
        jsonObject.put("resourceId", resourceId);
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.resourceUpdateDirectExchange,
                RabbitmqRoutingName.resourceUpdateRouting, message);
        return R.ok();
    }

    /**
     * 资源收藏数 - 1
     *
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/24 14:29
     */
    @Override
    public R resourceCollectCountSubOne(Long resourceId, Date createTime) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.resourceCollectCountSubOne);
        jsonObject.put("resourceId", resourceId);
        jsonObject.put("createTime", createTime);
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.resourceUpdateDirectExchange,
                RabbitmqRoutingName.resourceUpdateRouting, message);
        return R.ok();
    }

    /**
     * 通过资源id得到资源信息
     *
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/26 10:41
     */
    @Override
    public R queryResourceById(Long resourceId) {
        Resources resources = resourcesMapper.selectById(resourceId);
        JSONObject jsonObject = new JSONObject();
        String type = resources.getType();
        FileTypeEnum fileUrlByFileType = FileTypeEnum.getFileUrlByFileType(type);
        // 得到资源类型
        jsonObject.put("picture", fileUrlByFileType.getUrl());
        jsonObject.put("title", resources.getName());
        jsonObject.put("viewCount", resources.getViewCount());
        jsonObject.put("collectCount", resources.getCollectCount());
        jsonObject.put("commentCount", resources.getCommentCount());
        jsonObject.put("brief", resources.getDescription());
        return R.ok(jsonObject);
    }

    /**
     * 通过资源id,和评论id得到资源信息
     *
     * @param resourceId 资源id
     * @param commentId 评论id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/28 15:24
     */
    @Override
    public R queryResourceAndCommentById(Long resourceId, Long commentId) {
        Resources resources = resourcesMapper.selectById(resourceId);
        JSONObject jsonObject = new JSONObject();
        String type = resources.getType();
        FileTypeEnum fileUrlByFileType = FileTypeEnum.getFileUrlByFileType(type);
        // 得到资源类型
        jsonObject.put("picture", fileUrlByFileType.getUrl());
        ResourceComment resourceComment = resourceCommentMapper.selectById(commentId);
        jsonObject.put("title", resourceComment.getContent());
        return R.ok(jsonObject);
    }

    /**
     * 通过资源id得到资源作者id
     *
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/29 21:30
     */
    @Override
    public Long queryResourceAuthorById(Long resourceId) {
        Resources resources = resourcesMapper.selectById(resourceId);
        return resources.getUserId();
    }

    /**
     * 通过资源，评论id得到资源信息
     *
     * @param resourceId 资源id
     * @param commentId 评论id
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/8 15:22
     */
    @Override
    public R queryResourceAndCommentInfoById(Long resourceId, Long commentId) {
        JSONObject jsonObject = new JSONObject();
        Resources resources = resourcesMapper.selectById(resourceId);
        jsonObject.put("picture", FileTypeEnum.getFileUrlByFileType(resources.getUrl()).getUrl());
        jsonObject.put("contentTitle", resources.getName());
        ResourceComment resourceComment = resourceCommentMapper.selectById(commentId);
        jsonObject.put("title", resourceComment.getContent());
        return R.ok(jsonObject);
    }

    /**
     * 得到资源一周发表数
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/18 21:19
     */
    @Override
    public R queryResourceCountRecentlyWeek(String userId) {
        // 一周内所有日期资源
        List<Long> questionCount = new ArrayList<>();
        // 得到一周前数据
        Date before = DateUtils.addDateDays(new Date(), -6);
        Date now = new Date();
        // 得到一周内所有日期
        List<Date> beenTwoDayAllDate = DateSelfUtils.getBeenTwoDayAllDate(before, now);
        beenTwoDayAllDate.forEach(time -> {
            LambdaQueryWrapper<ResourceStatistics> resourceStatisticsLambdaQueryWrapper = new LambdaQueryWrapper<>();
            resourceStatisticsLambdaQueryWrapper.eq(ResourceStatistics::getUserId, userId);
            resourceStatisticsLambdaQueryWrapper.eq(ResourceStatistics::getCreateTime, time);
            Long selectCount = resourceStatisticsMapper.selectCount(resourceStatisticsLambdaQueryWrapper);
            questionCount.add(selectCount);
        });

        return R.ok(questionCount);
    }
}
