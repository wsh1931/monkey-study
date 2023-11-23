package com.monkey.monkeycommunity.tasks;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeycommunity.constant.CommunityEnum;
import com.monkey.monkeycommunity.constant.CommunityOfficeEnum;
import com.monkey.monkeycommunity.mapper.CommunityArticleMapper;
import com.monkey.monkeycommunity.mapper.CommunityMapper;
import com.monkey.monkeycommunity.pojo.Community;
import com.monkey.monkeycommunity.pojo.CommunityArticle;
import com.monkey.monkeycommunity.redis.RedisKeyAndExpireEnum;
import com.monkey.monkeyUtils.mapper.UserMapper;
import com.monkey.monkeyUtils.pojo.User;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/9/25 10:54
 * @version: 1.0
 * @description: 定时任务
 */
@Component
public class ScheduledTasks {
    @Resource
    private UserMapper userMapper;
    @Resource
    private CommunityMapper communityMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private CommunityArticleMapper communityArticleMapper;

    /**
     * 每天凌晨4点执行社区排行任务
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/25 11:22
     */
    @Scheduled(cron = "0 0 4 * * ?")
//    @Scheduled(cron = "0 35 9 * * ?")
    public void updateCommunityRank() {
        QueryWrapper<Community> communityQueryWrapper = new QueryWrapper<>();
        communityQueryWrapper.select("id", "user_id", "name", "description", "photo",
                "article_count", "member_count", "create_time");
        List<Community> communityList = communityMapper.selectList(communityQueryWrapper);
        // 获取社区文章游览数, 文章数，点赞人数，评论人数，评分人数，收藏人数
        for (Community community : communityList) {
            Long communityId = community.getId();
            // 出去官方社区
            if (communityId.equals(CommunityOfficeEnum.COMMUNITY_OFFICE_ID.getCode())) {
                continue;
            }
            // 得到作者信息
            Long userId = community.getUserId();
            User user = userMapper.selectById(userId);
            community.setUsername(user.getUsername());
            community.setHeadImg(user.getPhoto());


            QueryWrapper<CommunityArticle> communityArticleQueryWrapper = new QueryWrapper<>();
            communityArticleQueryWrapper.eq("community_id", communityId);
            communityArticleQueryWrapper.eq("status", CommunityEnum.APPROVE_EXAMINE.getCode());
            List<CommunityArticle> communityArticleList = communityArticleMapper.selectList(communityArticleQueryWrapper);
            if (communityArticleList == null || communityArticleList.size() == 0) {
                continue;
            }

            long viewCount = 0;
            long likeCount = 0;
            long commentCount = 0;
            long scoreCount = 0;
            long collectCount = 0;
            for (CommunityArticle communityArticle : communityArticleList) {
                viewCount += communityArticle.getViewCount();
                likeCount += communityArticle.getLikeCount();
                commentCount += communityArticle.getCommentCount();
                scoreCount += communityArticle.getScoreCount();
                collectCount += communityArticle.getCollectCount();
            }


            community.setViewCount(viewCount);
            community.setLikeCount(likeCount);
            community.setCommentCount(commentCount);
            community.setScoreCount(scoreCount);
            community.setCollectCount(collectCount);
        }

        // 利用redis更新排行榜

        // 更新社区游览人数排行榜
        this.updateCommunityViewCountSortRank(communityList);
        // 更新社区成员数排行榜
        this.updateCommunityMemberCountSortRank(communityList);
        // 更新社区文章数排行榜
        this.updateCommunityArticleCountSortRank(communityList);
        // 更新社区点赞人数排行榜
        this.updateCommunityLikeCountSortRank(communityList);
        // 更新社区评论人数排行榜
        this.updateCommunityCommentCountSortRank(communityList);
        // 更新社区评分人数排行榜
        this.updateCommunityScoreCountSortRank(communityList);
        // 更新社区收藏人数排行榜
        this.updateCommunityCollectCountSortRank(communityList);
    }

        /**
         * 更新社区收藏人数排行榜
         *
         * @param communityList 待排序社区集合
         * @return {@link null}
         * @author wusihao
         * @date 2023/9/26 9:33
         */
        private void updateCommunityCollectCountSortRank(List<Community> communityList) {
            String redisKey = RedisKeyAndExpireEnum.COMMUNITY_COLLECT_COUNT_RANK.getKeyName();
            communityList.sort(Comparator.comparingLong(Community::getCollectCount).reversed());
            stringRedisTemplate.opsForValue().set(redisKey, JSONObject.toJSONString(communityList));
        }

        /**
         * 更新社区评分人数排行榜
         *
         * @param communityList 待排序社区集合
         * @return {@link null}
         * @author wusihao
         * @date 2023/9/26 9:32
         */
        private void updateCommunityScoreCountSortRank(List<Community> communityList) {
            String redisKey = RedisKeyAndExpireEnum.COMMUNITY_SCORE_COUNT_RANK.getKeyName();
            communityList.sort(Comparator.comparingLong(Community::getScoreCount).reversed());
            stringRedisTemplate.opsForValue().set(redisKey, JSONObject.toJSONString(communityList));
        }

        /**
         * 更新社区评论人数排行榜
         *
         * @param communityList 待排序社区集合
         * @return {@link null}
         * @author wusihao
         * @date 2023/9/26 9:26
         */
        private void updateCommunityCommentCountSortRank(List<Community> communityList) {
            String redisKey = RedisKeyAndExpireEnum.COMMUNITY_COMMENT_COUNT_RANK.getKeyName();
            communityList.sort(Comparator.comparingLong(Community::getCommentCount).reversed());
            stringRedisTemplate.opsForValue().set(redisKey, JSONObject.toJSONString(communityList));
        }

        /**
         * 更新社区点赞人数排行榜
         *
         * @param communityList 待排序社区集合
         * @return {@link null}
         * @author wusihao
         * @date 2023/9/26 9:24
         */
        private void updateCommunityLikeCountSortRank(List<Community> communityList) {
            String redisKey = RedisKeyAndExpireEnum.COMMUNITY_LIKE_COUNT_RANK.getKeyName();
            communityList.sort(Comparator.comparingLong(Community::getLikeCount).reversed());
            stringRedisTemplate.opsForValue().set(redisKey, JSONObject.toJSONString(communityList));
        }

        /**
         * 更新社区文章数排行榜
         *
         * @param communityList 待排序社区集合
         * @return {@link null}
         * @author wusihao
         * @date 2023/9/26 9:20
         */
        private void updateCommunityArticleCountSortRank(List<Community> communityList) {
            String redisKey = RedisKeyAndExpireEnum.COMMUNITY_ARTICLE_COUNT_RANK.getKeyName();
            communityList.sort(Comparator.comparingLong(Community::getArticleCount).reversed());
            stringRedisTemplate.opsForValue().set(redisKey, JSONObject.toJSONString(communityList));
        }

        /**
         * 更新社区成员数排行榜
         *
         * @param communityList 待排序社区集合
         * @return {@link null}
         * @author wusihao
         * @date 2023/9/26 9:17
         */
        private void updateCommunityMemberCountSortRank(List<Community> communityList) {
            String redisKey = RedisKeyAndExpireEnum.COMMUNITY_MEMBER_COUNT_RANK.getKeyName();
            communityList.sort(Comparator.comparingLong(Community::getMemberCount).reversed());
            stringRedisTemplate.opsForValue().set(redisKey, JSONObject.toJSONString(communityList));
        }

        /**
         * 更新社区游览人数排行榜
         *
         * @param communityList 待排序社区集合
         * @return {@link null}
         * @author wusihao
         * @date 2023/9/26 9:06
         */
        private void updateCommunityViewCountSortRank(List<Community> communityList) {
            String redisKey = RedisKeyAndExpireEnum.COMMUNITY_VIEW_COUNT_RANK.getKeyName();
            communityList.sort(Comparator.comparingLong(Community::getViewCount).reversed());
            stringRedisTemplate.opsForValue().set(redisKey, JSONObject.toJSONString(communityList));
        }
    }
