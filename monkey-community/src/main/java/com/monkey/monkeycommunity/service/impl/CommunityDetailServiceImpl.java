package com.monkey.monkeycommunity.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.constant.CommunityChannelEnum;
import com.monkey.monkeycommunity.constant.CommunityEnum;
import com.monkey.monkeycommunity.constant.CommunityRoleEnum;
import com.monkey.monkeycommunity.constant.RedisKeyAndExpireEnum;
import com.monkey.monkeycommunity.mapper.CommunityArticleMapper;
import com.monkey.monkeycommunity.mapper.CommunityMapper;
import com.monkey.monkeycommunity.mapper.CommunityRoleConnectMapper;
import com.monkey.monkeycommunity.pojo.Community;
import com.monkey.monkeycommunity.pojo.CommunityArticle;
import com.monkey.monkeycommunity.pojo.CommunityRoleConnect;
import com.monkey.monkeycommunity.service.CommunityService;
import com.monkey.monkeycommunity.service.CommunityDetailService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author: wusihao
 * @date: 2023/9/9 17:19
 * @version: 1.0
 * @description:
 */
@Service
public class CommunityDetailServiceImpl implements CommunityDetailService {
    @Resource
    private CommunityRoleConnectMapper communityRoleConnectMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private CommunityMapper communityMapper;
    @Resource
    private CommunityService communityService;
    @Resource
    private CommunityArticleMapper communityArticleMapper;
    /**
     * 得到我加入的社区数量
     *
     * @param userId 当前登录用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/10 8:35
     */
    @Override
    public R queryMyAddCommunityCount(String userId) {
        String redisKey = RedisKeyAndExpireEnum.MY_ADD_COMMUNITY_COUNT.getKeyName() + userId;
        if (userId != null && !"".equals(userId)) {
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisKey))) {
                return R.ok(stringRedisTemplate.opsForValue().get(redisKey));
            }
            QueryWrapper<CommunityRoleConnect> communityRoleConnectQueryWrapper = new QueryWrapper<>();
            communityRoleConnectQueryWrapper.eq("user_id", userId);
            communityRoleConnectQueryWrapper.eq("status", CommunityEnum.APPROVE_EXAMINE.getCode());
            Long selectCount = communityRoleConnectMapper.selectCount(communityRoleConnectQueryWrapper);

            stringRedisTemplate.opsForValue().set(redisKey, String.valueOf(selectCount));
            stringRedisTemplate.expire(redisKey, RedisKeyAndExpireEnum.MY_ADD_COMMUNITY_COUNT.getTimeUnit(), TimeUnit.DAYS);
            return R.ok(selectCount);
        }
        return R.ok();
    }

    /**
     * 得到我管理的社区数量
     *
     * @param userId 当前登录的用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/10 8:36
     */
    @Override
    public R queryMyManageCommunityCount(String userId) {
        String redisKey = RedisKeyAndExpireEnum.MY_MANAGE_COMMUNITY_COUNT.getKeyName() + userId;
        if (userId != null && !"".equals(userId)) {
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisKey))) {
                return R.ok(stringRedisTemplate.opsForValue().get(redisKey));
            }
            QueryWrapper<CommunityRoleConnect> communityRoleConnectQueryWrapper = new QueryWrapper<>();
            communityRoleConnectQueryWrapper.eq("user_id", userId);
            communityRoleConnectQueryWrapper.eq("role_id", CommunityRoleEnum.PRIMARY_ADMINISTRATOR.getCode())
                    .or()
                    .eq("role_id", CommunityRoleEnum.ADMINISTRATOR.getCode());

            Long selectCount = communityRoleConnectMapper.selectCount(communityRoleConnectQueryWrapper);

            stringRedisTemplate.opsForValue().set(redisKey, String.valueOf(selectCount));
            stringRedisTemplate.expire(redisKey, RedisKeyAndExpireEnum.MY_MANAGE_COMMUNITY_COUNT.getTimeUnit(), TimeUnit.DAYS);

            return R.ok(selectCount);
        }

        return R.ok();
    }

    /**
     * 得到官方推荐的社区数量
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/10 8:36
     */
    @Override
    public R queryRecommendCommunityCount() {
        String redisKey = RedisKeyAndExpireEnum.RECOMMEND_COMMUNITY_COUNT.getKeyName();
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisKey))) {
            String data = stringRedisTemplate.opsForValue().get(redisKey);
            return R.ok(data);
        }

        QueryWrapper<Community> communityQueryWrapper = new QueryWrapper<>();
        communityQueryWrapper.eq("is_recommend", CommunityEnum.IS_RECOMMEND.getCode());
        Long selectCount = communityMapper.selectCount(communityQueryWrapper);
        stringRedisTemplate.opsForValue().set(redisKey, String.valueOf(selectCount));

        if (selectCount > 0) {
            return R.ok(selectCount);
        } else {
            return R.ok();
        }
    }

    /**
     * 得到其他社区数
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/10 9:26
     */
    @Override
    public R queryOtherCommunityCount() {
        String redisKey = RedisKeyAndExpireEnum.OTHER_COMMUNITY_COUNT.getKeyName();
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisKey))) {
            return R.ok(JSONObject.parseArray(stringRedisTemplate.opsForValue().get(redisKey), Community.class));
        }

        Long selectCount = communityMapper.selectCount(null);
        stringRedisTemplate.opsForValue().set(redisKey, String.valueOf(selectCount));
        stringRedisTemplate.expire(redisKey, RedisKeyAndExpireEnum.OTHER_COMMUNITY_COUNT.getTimeUnit(), TimeUnit.DAYS);

        if (selectCount > 0) {
            return R.ok(selectCount);
        } else {
            return R.ok();
        }
    }

    /**
     * 查询我加入的社区集合
     *
     * @param userId 当前登录用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/10 9:55
     */
    @Override
    public R queryMyAddCommunityList(String userId) {
        String redisKey = RedisKeyAndExpireEnum.MY_ADD_COMMUNITY_LIST.getKeyName() + userId;
        if (userId != null && !"".equals(userId)) {
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisKey))) {
                return R.ok(JSONObject.parseArray(stringRedisTemplate.opsForValue().get(redisKey)));
            }
            
            QueryWrapper<CommunityRoleConnect> communityRoleConnectQueryWrapper = new QueryWrapper<>();
            communityRoleConnectQueryWrapper.eq("user_id", userId);
            communityRoleConnectQueryWrapper.select("community_id");
            List<Object> communityIdList = communityRoleConnectMapper.selectObjs(communityRoleConnectQueryWrapper);
            QueryWrapper<Community> communityQueryWrapper = new QueryWrapper<>();
            List<Long> collect = communityIdList.stream().map(obj -> (long) obj).collect(Collectors.toList());
            communityQueryWrapper.in("id", collect);
            communityQueryWrapper.select("id", "name", "photo");
            List<Community> communityList = communityMapper.selectList(communityQueryWrapper);

            stringRedisTemplate.opsForValue().set(redisKey, JSONObject.toJSONString(communityList));
            stringRedisTemplate.expire(redisKey, RedisKeyAndExpireEnum.MY_ADD_COMMUNITY_LIST.getTimeUnit(), TimeUnit.DAYS);
            return R.ok(communityList);
        }
        return R.ok();
    }

    /**
     * 查询我管理的社区集合
     *
     * @param userId 当前登录用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/10 10:09
     */
    @Override
    public R queryMyManegeCommunityList(String userId) {
        String redisKey = RedisKeyAndExpireEnum.MY_MANAGE_COMMUNITY_LIST.getKeyName() + userId;
        if (userId != null && !"".equals(userId)) {
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisKey))) {
                return R.ok(JSONObject.parseArray(stringRedisTemplate.opsForValue().get(redisKey)));
            }

            QueryWrapper<CommunityRoleConnect> communityRoleConnectQueryWrapper = new QueryWrapper<>();
            communityRoleConnectQueryWrapper.eq("user_id", userId);
            communityRoleConnectQueryWrapper.eq("role_id", CommunityRoleEnum.PRIMARY_ADMINISTRATOR.getCode())
                                            .or()
                                            .eq("role_id", CommunityRoleEnum.ADMINISTRATOR.getCode());
            communityRoleConnectQueryWrapper.select("community_id");
            List<Object> communityIdList = communityRoleConnectMapper.selectObjs(communityRoleConnectQueryWrapper);
            QueryWrapper<Community> communityQueryWrapper = new QueryWrapper<>();
            List<Long> collect = communityIdList.stream().map(obj -> (long) obj).collect(Collectors.toList());
            communityQueryWrapper.in("id", collect);
            communityQueryWrapper.select("id", "name", "photo");
            List<Community> communityList = communityMapper.selectList(communityQueryWrapper);

            stringRedisTemplate.opsForValue().set(redisKey, JSONObject.toJSONString(communityList));
            stringRedisTemplate.expire(redisKey, RedisKeyAndExpireEnum.MY_MANAGE_COMMUNITY_LIST.getTimeUnit(), TimeUnit.DAYS);

            return R.ok(communityList);
        }
        return R.ok();
    }

    /**
     * 查询官方推荐社区集合
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/10 10:17
     */
    @Override
    public R queryRecommendCommunityList() {
        String redisKey = RedisKeyAndExpireEnum.RECOMMEND_COMMUNITY_LIST.getKeyName();
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisKey))) {
            return R.ok(JSONObject.parseArray(stringRedisTemplate.opsForValue().get(redisKey)));
        }

        QueryWrapper<Community> communityQueryWrapper = new QueryWrapper<>();
        communityQueryWrapper.eq("is_recommend", CommunityEnum.IS_RECOMMEND.getCode());
        communityQueryWrapper.select("id", "name", "photo");
        List<Community> communityList = communityMapper.selectList(communityQueryWrapper);
        stringRedisTemplate.opsForValue().set(redisKey, JSONObject.toJSONString(communityList));
        return R.ok(communityList);
    }

    /**
     * 查询其他社区集合
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/10 10:23
     */
    @Override
    public R queryOtherCommunityListList() {
        String redisKey = RedisKeyAndExpireEnum.OTHER_COMMUNITY_LIST.getKeyName();
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisKey))) {
            return R.ok(JSONObject.parseArray(stringRedisTemplate.opsForValue().get(redisKey)));
        }

        QueryWrapper<Community> communityQueryWrapper = new QueryWrapper<>();
        communityQueryWrapper.select("id", "name", "photo");
        List<Community> communityList = communityMapper.selectList(communityQueryWrapper);
        stringRedisTemplate.opsForValue().set(redisKey, JSONObject.toJSONString(communityList));
        return R.ok(communityList);
    }

    /**
     * 通过社区名模糊搜索社区信息
     *
     * @param communityName 社区名称
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/10 15:42
     */
    @Override
    public R searchCommunityByCommunityName(String communityName) {
        QueryWrapper<Community> communityQueryWrapper = new QueryWrapper<>();
        communityQueryWrapper.like("name", communityName);
        communityQueryWrapper.select("id", "name", "photo");
        return R.ok(communityMapper.selectList(communityQueryWrapper));
    }

    /**
     * 得到最新文章列表
     *
     * @param currentPage 当前页
     * @param pageSize    每页数据量
     * @param communityId 社区id
     * @param channelId   频道id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/11 8:09
     */
    @Override
    public R queryLatestArticleListByChannelIdAndCommunityId(Long currentPage, Long pageSize, Long communityId, String channelName, String channelId) {
        QueryWrapper<CommunityArticle> communityArticleQueryWrapper = new QueryWrapper<>();
        communityArticleQueryWrapper.eq("community_id", communityId);
        communityArticleQueryWrapper.eq(!channelName.equals(CommunityChannelEnum.ALL.getChannelName()) && !"".equals(channelId),
                                        "channel_id",
                                             channelId);
        communityArticleQueryWrapper.orderByDesc("create_time");
        communityArticleQueryWrapper.eq("status", CommunityEnum.APPROVE_EXAMINE.getCode());
        Page selectPage = communityArticleMapper.selectPage(new Page<>(currentPage, pageSize), communityArticleQueryWrapper);
        List<CommunityArticle> records = selectPage.getRecords();
        List<CommunityArticle> communityArticle = communityService.getCommunityArticle(records);
        selectPage.setRecords(communityArticle);
        return R.ok(selectPage);
    }

    /**
     * 得到热文章列表
     *
     * @param currentPage 当前页
     * @param pageSize    每页数据量
     * @param communityId 社区id
     * @param channelName 频道名称
     * @param channelId 频道id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/11 8:10
     */
    @Override
    public R queryHottestArticleListByChannelIdAndCommunityId(Long currentPage, Long pageSize, Long communityId, String channelName, String channelId) {
        QueryWrapper<CommunityArticle> communityArticleQueryWrapper = new QueryWrapper<>();
        communityArticleQueryWrapper.eq("community_id", communityId);
        communityArticleQueryWrapper.eq(!channelName.equals(CommunityChannelEnum.ALL.getChannelName()) && !"".equals(channelId),
                "channel_id",
                channelId);
        communityArticleQueryWrapper.orderByDesc("view_count");
        communityArticleQueryWrapper.orderByDesc("collect_count");
        communityArticleQueryWrapper.orderByDesc("like_count");
        communityArticleQueryWrapper.orderByDesc("score_count");
        communityArticleQueryWrapper.eq("status", CommunityEnum.APPROVE_EXAMINE.getCode());
        Page selectPage = communityArticleMapper.selectPage(new Page<>(currentPage, pageSize), communityArticleQueryWrapper);
        List<CommunityArticle> records = selectPage.getRecords();
        List<CommunityArticle> communityArticle = communityService.getCommunityArticle(records);
        selectPage.setRecords(communityArticle);
        return R.ok(selectPage);
    }

    /**
     * 得到评分降序列表
     *
     * @param currentPage 当前页
     * @param pageSize    每页数据量
     * @param communityId 社区id
     * @param channelName 频道id
     * @param channelId 频道id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/11 8:10
     */
    @Override
    public R queryScoreArticleListByChannelIdAndCommunityId(Long currentPage, Long pageSize, Long communityId, String channelName, String channelId) {
        QueryWrapper<CommunityArticle> communityArticleQueryWrapper = new QueryWrapper<>();
        communityArticleQueryWrapper.eq("community_id", communityId);
        communityArticleQueryWrapper.eq(!channelName.equals(CommunityChannelEnum.ALL.getChannelName()) && !"".equals(channelId),
                "channel_id",
                channelId);
        communityArticleQueryWrapper.orderByDesc("score");
        communityArticleQueryWrapper.eq("status", CommunityEnum.APPROVE_EXAMINE.getCode());
        Page selectPage = communityArticleMapper.selectPage(new Page<>(currentPage, pageSize), communityArticleQueryWrapper);
        List<CommunityArticle> records = selectPage.getRecords();
        List<CommunityArticle> communityArticle = communityService.getCommunityArticle(records);
        selectPage.setRecords(communityArticle);
        return R.ok(selectPage);
    }

    /**
     * 得到阅读量文章列表
     *
     * @param currentPage 当前页
     * @param pageSize    每页数据量
     * @param communityId 社区id
     * @param channelName 频道id
     * @param channelId 频道id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/11 8:10
     */
    @Override
    public R queryViewsArticleListByChannelIdAndCommunityId(Long currentPage, Long pageSize, Long communityId, String channelName, String channelId) {
        QueryWrapper<CommunityArticle> communityArticleQueryWrapper = new QueryWrapper<>();
        communityArticleQueryWrapper.eq("community_id", communityId);
        communityArticleQueryWrapper.orderByDesc("view_count");
        communityArticleQueryWrapper.eq(!channelName.equals(CommunityChannelEnum.ALL.getChannelName()) && !"".equals(channelId),
                "channel_id",
                channelId);
        communityArticleQueryWrapper.eq("status", CommunityEnum.APPROVE_EXAMINE.getCode());
        Page selectPage = communityArticleMapper.selectPage(new Page<>(currentPage, pageSize), communityArticleQueryWrapper);
        List<CommunityArticle> records = selectPage.getRecords();
        List<CommunityArticle> communityArticle = communityService.getCommunityArticle(records);
        selectPage.setRecords(communityArticle);
        return R.ok(selectPage);
    }

    /**
     * 得到精选文章列表
     *
     * @param currentPage 当前页
     * @param pageSize    每页数据量
     * @param communityId 社区id
     * @param channelName 频道名称
     * @param channelId 频道id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/11 8:11
     */
    @Override
    public R queryExcellentArticleListByChannelIdAndCommunityId(Long currentPage, Long pageSize, Long communityId, String channelName, String channelId) {
        QueryWrapper<CommunityArticle> communityArticleQueryWrapper = new QueryWrapper<>();
        communityArticleQueryWrapper.eq("community_id", communityId);
        communityArticleQueryWrapper.eq(!channelName.equals(CommunityChannelEnum.ALL.getChannelName()) && !"".equals(channelId),
                "channel_id",
                channelId);
        communityArticleQueryWrapper.eq("status", CommunityEnum.APPROVE_EXAMINE.getCode());
        communityArticleQueryWrapper.eq("is_excellent", CommunityEnum.IS_EXCELLENT.getCode());
        Page selectPage = communityArticleMapper.selectPage(new Page<>(currentPage, pageSize), communityArticleQueryWrapper);
        List<CommunityArticle> records = selectPage.getRecords();
        List<CommunityArticle> communityArticle = communityService.getCommunityArticle(records);
        selectPage.setRecords(communityArticle);
        return R.ok(selectPage);
    }

    /**
     * 得到置顶文章列表
     *
     * @param currentPage 当前页
     * @param pageSize    每页数据量
     * @param communityId 社区名称
     * @param channelId 频道id
     * @return {@link null}
     * @paramchannelName 频道名称
     * @author wusihao
     * @date 2023/9/11 8:11
     */
    @Override
    public R queryTopArticleListByChannelIdAndCommunityId(Long currentPage, Long pageSize, Long communityId, String channelName, String channelId) {
        QueryWrapper<CommunityArticle> communityArticleQueryWrapper = new QueryWrapper<>();
        communityArticleQueryWrapper.eq("community_id", communityId);
        communityArticleQueryWrapper.eq(!channelName.equals(CommunityChannelEnum.ALL.getChannelName()) && !"".equals(channelId),
                "channel_id",
                channelId);
        communityArticleQueryWrapper.eq("is_top", CommunityEnum.IS_TOP.getCode());
        communityArticleQueryWrapper.eq("status", CommunityEnum.APPROVE_EXAMINE.getCode());
        Page selectPage = communityArticleMapper.selectPage(new Page<>(currentPage, pageSize), communityArticleQueryWrapper);
        List<CommunityArticle> records = selectPage.getRecords();
        List<CommunityArticle> communityArticle = communityService.getCommunityArticle(records);
        selectPage.setRecords(communityArticle);
        return R.ok(selectPage);
    }

    /**
     * 得到点赞排序文章列表
     *
     * @param currentPage 当前页
     * @param pageSize    每页数据量
     * @param communityId 社区id
     * @param channelName 频道名称
     * @param channelId 频道id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/11 8:11
     */
    @Override
    public R queryLikeArticleListByChannelIdAndCommunityId(Long currentPage, Long pageSize, Long communityId, String channelName, String channelId) {
        QueryWrapper<CommunityArticle> communityArticleQueryWrapper = new QueryWrapper<>();
        communityArticleQueryWrapper.eq("community_id", communityId);
        communityArticleQueryWrapper.eq(!channelName.equals(CommunityChannelEnum.ALL.getChannelName()) && !"".equals(channelId),
                "channel_id",
                channelId);
        communityArticleQueryWrapper.orderByDesc("like_count");
        communityArticleQueryWrapper.eq("status", CommunityEnum.APPROVE_EXAMINE.getCode());
        Page selectPage = communityArticleMapper.selectPage(new Page<>(currentPage, pageSize), communityArticleQueryWrapper);
        List<CommunityArticle> records = selectPage.getRecords();
        List<CommunityArticle> communityArticle = communityService.getCommunityArticle(records);
        selectPage.setRecords(communityArticle);
        return R.ok(selectPage);
    }

    /**
     * 得到收藏排序文章列表
     *
     * @param currentPage 当前页
     * @param pageSize    每页数据量
     * @param communityId 社区id
     * @param channelName 频道名称
     * @param channelId
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/11 8:11
     */
    @Override
    public R queryCollectArticleListByChannelIdAndCommunityId(Long currentPage, Long pageSize, Long communityId, String channelName, String channelId) {
        QueryWrapper<CommunityArticle> communityArticleQueryWrapper = new QueryWrapper<>();
        communityArticleQueryWrapper.eq("community_id", communityId);
        communityArticleQueryWrapper.eq(!channelName.equals(CommunityChannelEnum.ALL.getChannelName()) && !"".equals(channelId),
                "channel_id",
                channelId);
        communityArticleQueryWrapper.orderByDesc("collect_count");
        communityArticleQueryWrapper.eq("status", CommunityEnum.APPROVE_EXAMINE.getCode());
        Page selectPage = communityArticleMapper.selectPage(new Page<>(currentPage, pageSize), communityArticleQueryWrapper);
        List<CommunityArticle> records = selectPage.getRecords();
        List<CommunityArticle> communityArticle = communityService.getCommunityArticle(records);
        selectPage.setRecords(communityArticle);
        return R.ok(selectPage);
    }

    /**
     * 得到我的文章列表
     *
     * @param currentPage 当前页
     * @param pageSize    每页数据量
     * @param communityId 社区id
     * @param channelName 频道名称
     * @param userId      当前登录用户id
     * @param channelId
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/11 8:12
     */
    @Override
    public R queryWithMeCommunityListByCommunityIdAndChannelId(Long currentPage, Long pageSize, Long communityId, String channelName, Long userId, String channelId) {
        QueryWrapper<CommunityArticle> communityArticleQueryWrapper = new QueryWrapper<>();
        communityArticleQueryWrapper.eq("community_id", communityId);
        communityArticleQueryWrapper.eq(!channelName.equals(CommunityChannelEnum.ALL.getChannelName()) && !"".equals(channelId),
                "channel_id",
                channelId);
        communityArticleQueryWrapper.orderByDesc("is_top");
        communityArticleQueryWrapper.eq("user_id", userId);
        communityArticleQueryWrapper.eq("status", CommunityEnum.APPROVE_EXAMINE.getCode());
        Page selectPage = communityArticleMapper.selectPage(new Page<>(currentPage, pageSize), communityArticleQueryWrapper);
        List<CommunityArticle> records = selectPage.getRecords();
        List<CommunityArticle> communityArticle = communityService.getCommunityArticle(records);
        selectPage.setRecords(communityArticle);
        return R.ok(selectPage);
    }

    /**
     * 通过搜索字段模糊搜索文章标题
     *
     * @param title 模糊搜索标题字段
     * @param currentPage 当前页
     * @param pageSize    每页数据量
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/11 11:55
     */
    @Override
    public R searchArticleContent(String title, Long currentPage, Long pageSize) {
        return null;
    }
}
