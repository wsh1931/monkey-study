package com.monkey.monkeycommunity.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.constant.CommunityRoleEnum;
import com.monkey.monkeycommunity.mapper.CommunityArticleMapper;
import com.monkey.monkeycommunity.mapper.CommunityUserManageMapper;
import com.monkey.monkeycommunity.mapper.CommunityUserRoleConnectMapper;
import com.monkey.monkeycommunity.pojo.CommunityUserManage;
import com.monkey.monkeycommunity.pojo.CommunityUserRoleConnect;
import com.monkey.monkeycommunity.rabbitmq.EventConstant;
import com.monkey.monkeycommunity.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeycommunity.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeycommunity.service.CommunityDetailCardService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/9/11 10:17
 * @version: 1.0
 * @description:
 */
@Service
public class CommunityDetailCardServiceImpl implements CommunityDetailCardService {
    @Resource
    private CommunityUserRoleConnectMapper communityUserRoleConnectMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private CommunityUserManageMapper communityUserManageMapper;
    /**
     * 判断是否有显示隐藏框的权力
     *
     * @param communityId 社区id
     * @param userId 当前登录用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/11 10:22
     */
    @Override
    public R judgePower(Long communityId, String userId) {
        if (userId == null || "".equals(userId)) {
            return R.ok(false);
        }

        // 判断是否是管理员
        Boolean isManagement = judgeIsManager(communityId, userId);
        if (isManagement) {
            return R.ok(true);
        }

        return R.ok(false);
    }

    /**
     * 判断当前登录用户是否是管理员
     *
     * @param communityId 社区id
     * @param userId 当前登录用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/11 10:59
     */
    private Boolean judgeIsManager(Long communityId, String userId) {
        if (userId == null || "".equals(userId)) {
            return false;
        }
        QueryWrapper<CommunityUserManage> communityUserManageQueryWrapper = new QueryWrapper<>();
        communityUserManageQueryWrapper.eq("community_id", communityId);
        communityUserManageQueryWrapper.eq("user_id", userId);
        Long selectCount = communityUserManageMapper.selectCount(communityUserManageQueryWrapper);
        if (selectCount > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 删除社区文章
     *
     * @param articleId   文章id
     * @param communityId 社区id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/11 10:52
     */
    @Override
    public R deleteArticle(Long articleId, Long communityId) {
        // 删除社区文章
        JSONObject object = new JSONObject();
        object.put("event", EventConstant.deleteCommunityArticle);
        object.put("communityArticleId", articleId);
        object.put("communityId", communityId);
        Message messageDelete = new Message(object.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.communityDeleteDirectExchange,
                RabbitmqRoutingName.communityDeleteRouting, messageDelete);
        return R.ok();
    }

    /**
     * 将文章设置为精选内容
     *
     * @param articleId 文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/11 11:23
     */
    @Override
    public R setExcellentArticle(Long articleId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.communityArticleExcellent);
        jsonObject.put("communityArticleId", articleId);
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.communityUpdateDirectExchange,
                RabbitmqRoutingName.communityUpdateRouting, message);
        return R.ok();
    }

    /**
     * 取消文章精选
     *
     * @param articleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/24 16:02
     */
    @Override
    public R cancelExcellentArticle(Long articleId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.cancelCommunityArticleExcellent);
        jsonObject.put("communityArticleId", articleId);
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.communityUpdateDirectExchange,
                RabbitmqRoutingName.communityUpdateRouting, message);
        return R.ok();
    }

    /**
     * 将文章设置为置顶内容
     *
     * @param articleId 文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/11 11:30
     */
    @Override
    public R setTopArticle(Long articleId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.communityArticleTop);
        jsonObject.put("communityArticleId", articleId);
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.communityUpdateDirectExchange,
                RabbitmqRoutingName.communityUpdateRouting, message);
        return R.ok();
    }

    /**
     * 取消文章置顶
     *
     * @param articleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/24 16:02
     */
    @Override
    public R cancelTopArticle(Long articleId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.cancelCommunityArticleTop);
        jsonObject.put("communityArticleId", articleId);
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.communityUpdateDirectExchange,
                RabbitmqRoutingName.communityUpdateRouting, message);
        return R.ok();
    }

}
