package com.monkey.monkeycommunity.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.mapper.CommunityArticleMapper;
import com.monkey.monkeycommunity.mapper.CommunityMapper;
import com.monkey.monkeycommunity.pojo.Community;
import com.monkey.monkeycommunity.redis.RedisKeyAndExpireEnum;
import com.monkey.monkeycommunity.service.CommunityRankService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/9/25 10:22
 * @version: 1.0
 * @description:
 */
@Service
public class CommunityRankServiceImpl implements CommunityRankService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 查询社区游览数排行
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/25 10:27
     */
    @Override
    public R queryCommunityViewCount() {
        String redisKey = RedisKeyAndExpireEnum.COMMUNITY_VIEW_COUNT_RANK.getKeyName();
        String str = stringRedisTemplate.opsForValue().get(redisKey);
        List<Community> communityList = JSONObject.parseArray(str, Community.class);
        return R.ok(communityList);
    }

    /**
     * 查询社区成员数排行
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/26 9:39
     */
    @Override
    public R queryCommunityMemberCount() {
        String redisKey = RedisKeyAndExpireEnum.COMMUNITY_MEMBER_COUNT_RANK.getKeyName();
        String str = stringRedisTemplate.opsForValue().get(redisKey);
        List<Community> communityList = JSONObject.parseArray(str, Community.class);
        return R.ok(communityList);
    }

    /**
     * 查询社区文章数排行
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/26 9:39
     */
    @Override
    public R queryCommunityArticleCount() {
        String redisKey = RedisKeyAndExpireEnum.COMMUNITY_ARTICLE_COUNT_RANK.getKeyName();
        String str = stringRedisTemplate.opsForValue().get(redisKey);
        List<Community> communityList = JSONObject.parseArray(str, Community.class);
        return R.ok(communityList);
    }

    /**
     * 查询社区点赞数排行
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/26 9:39
     */
    @Override
    public R queryCommunityLikeCount() {
        String redisKey = RedisKeyAndExpireEnum.COMMUNITY_LIKE_COUNT_RANK.getKeyName();
        String str = stringRedisTemplate.opsForValue().get(redisKey);
        List<Community> communityList = JSONObject.parseArray(str, Community.class);
        return R.ok(communityList);
    }

    /**
     * 查询社区评论数排行
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/26 9:41
     */
    @Override
    public R queryCommunityCommentCount() {
        String redisKey = RedisKeyAndExpireEnum.COMMUNITY_COMMENT_COUNT_RANK.getKeyName();
        String str = stringRedisTemplate.opsForValue().get(redisKey);
        List<Community> communityList = JSONObject.parseArray(str, Community.class);
        return R.ok(communityList);
    }

    /**
     * 查询社区评分数排行
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/26 9:41
     */
    @Override
    public R queryCommunityScoreCount() {
        String redisKey = RedisKeyAndExpireEnum.COMMUNITY_SCORE_COUNT_RANK.getKeyName();
        String str = stringRedisTemplate.opsForValue().get(redisKey);
        List<Community> communityList = JSONObject.parseArray(str, Community.class);
        return R.ok(communityList);
    }

    /**
     * 查询社区收藏数排行
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/26 9:41
     */
    @Override
    public R queryCommunityCollectCount() {
        String redisKey = RedisKeyAndExpireEnum.COMMUNITY_COLLECT_COUNT_RANK.getKeyName();
        String str = stringRedisTemplate.opsForValue().get(redisKey);
        List<Community> communityList = JSONObject.parseArray(str, Community.class);
        return R.ok(communityList);
    }
}
