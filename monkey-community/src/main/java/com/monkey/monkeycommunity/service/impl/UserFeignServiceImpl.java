package com.monkey.monkeycommunity.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.constant.CommunityEnum;
import com.monkey.monkeycommunity.mapper.CommunityArticleCommentMapper;
import com.monkey.monkeycommunity.mapper.CommunityArticleLikeMapper;
import com.monkey.monkeycommunity.mapper.CommunityArticleMapper;
import com.monkey.monkeycommunity.pojo.CommunityArticle;
import com.monkey.monkeycommunity.pojo.CommunityArticleComment;
import com.monkey.monkeycommunity.pojo.CommunityArticleLike;
import com.monkey.monkeycommunity.rabbitmq.EventConstant;
import com.monkey.monkeycommunity.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeycommunity.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeycommunity.service.UserFeignService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author: wusihao
 * @date: 2023/9/24 14:31
 * @version: 1.0
 * @description:
 */
@Service
public class UserFeignServiceImpl implements UserFeignService {
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private CommunityArticleMapper communityArticleMapper;
    @Resource
    private CommunityArticleLikeMapper communityArticleLikeMapper;
    @Resource
    private CommunityArticleCommentMapper communityArticleCommentMapper;
    /**
     * 社区文章收藏数 + 1
     *
     * @param communityArticleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/24 14:33
     */
    @Override
    public void communityArticleCollectAddOne(Long communityArticleId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.communityArticleCollectCountAddOne);
        jsonObject.put("communityArticleId", communityArticleId);
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.communityUpdateDirectExchange,
                RabbitmqRoutingName.communityUpdateRouting, message);
    }

    /**
     * 社区文章收藏数 - 1
     *
     * @param communityArticleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/24 14:33
     */
    @Override
    public void communityArticleCollectSubOne(Long communityArticleId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.communityArticleCollectCountSubOne);
        jsonObject.put("communityArticleId", communityArticleId);
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.communityUpdateDirectExchange,
                RabbitmqRoutingName.communityUpdateRouting, message);
    }

    /**
     * 通过社区文章id得到社区文章信息
     *
     * @param communityArticleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/26 10:34
     */
    @Override
    public R queryCommunityArticleById(Long communityArticleId) {
        JSONObject jsonObject = new JSONObject();
        CommunityArticle communityArticle = communityArticleMapper.selectById(communityArticleId);
        jsonObject.put("picture", communityArticle.getPicture());
        jsonObject.put("title", communityArticle.getTitle());
        jsonObject.put("viewCount", communityArticle.getViewCount());
        jsonObject.put("collectCount", communityArticle.getCollectCount());
        jsonObject.put("commentCount", communityArticle.getCommentCount());
        jsonObject.put("brief", communityArticle.getBrief());
        return R.ok(jsonObject);
    }

    /**
     * 通过社区文章id和评论id得到社区文章信息
     *
     * @param communityArticleId 社区文章id
     * @param commentId 评论id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/28 15:12
     */
    @Override
    public R queryCommunityArticleAndCommentById(Long communityArticleId, Long commentId) {
        JSONObject jsonObject = new JSONObject();
        CommunityArticle communityArticle = communityArticleMapper.selectById(communityArticleId);
        jsonObject.put("picture", communityArticle.getPicture());
        jsonObject.put("contentTitle", communityArticle.getTitle());
        CommunityArticleComment comment = communityArticleCommentMapper.selectById(commentId);
        jsonObject.put("title", comment.getContent());
        return R.ok(jsonObject);
    }

    /**
     * 通过社区文章id得到社区文章作者id
     *
     * @param communityArticleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/29 21:24
     */
    @Override
    public Long queryCommunityArticleAuthorById(Long communityArticleId) {
        CommunityArticle communityArticle = communityArticleMapper.selectById(communityArticleId);
        return communityArticle.getUserId();
    }
}
