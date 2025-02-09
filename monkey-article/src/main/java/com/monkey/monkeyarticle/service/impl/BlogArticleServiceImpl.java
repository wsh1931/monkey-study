package com.monkey.monkeyarticle.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeyUtils.constants.CollectEnum;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.mapper.CollectContentConnectMapper;
import com.monkey.monkeyUtils.pojo.CollectContentConnect;
import com.monkey.monkeyUtils.redis.RedisKeyAndTimeEnum;
import com.monkey.monkeyUtils.result.ResultStatus;
import com.monkey.monkeyUtils.result.ResultVO;


import com.monkey.monkeyarticle.constant.ArticleEnum;
import com.monkey.monkeyarticle.mapper.ArticleLabelMapper;
import com.monkey.monkeyarticle.mapper.ArticleLikeMapper;
import com.monkey.monkeyarticle.mapper.ArticleMapper;
import com.monkey.monkeyarticle.pojo.Article;
import com.monkey.monkeyarticle.pojo.ArticleLabel;
import com.monkey.monkeyarticle.pojo.ArticleLike;
import com.monkey.monkeyarticle.pojo.vo.ArticleVo;
import com.monkey.monkeyarticle.rabbitmq.EventConstant;
import com.monkey.monkeyarticle.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeyarticle.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeyarticle.service.BlogArticleService;
import io.swagger.models.auth.In;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Service
public class BlogArticleServiceImpl implements BlogArticleService {
    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private ArticleLikeMapper articleLikeMapper;

    @Resource
    private CollectContentConnectMapper collectContentConnectMapper;

    @Resource
    private ArticleLabelMapper articleLabelMapper;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private RabbitTemplate rabbitTemplate;
    // 通过标签id得到文章内容
    @Override
    public ResultVO getArticleContentByLabelId(String labelId) {
        List<Article> articleList = new ArrayList<>();

        if (!"-1".equals(labelId)) {
            QueryWrapper<ArticleLabel> articleQueryWrapper = new QueryWrapper<>();
            articleQueryWrapper.eq("label_id", Long.parseLong(labelId));
            List<ArticleLabel> articleLabelList = articleLabelMapper.selectList(articleQueryWrapper);
            for (ArticleLabel articleLabel : articleLabelList) {
                Long articleId = articleLabel.getArticleId();
                Article articleSelectById = articleMapper.selectById(articleId);
                articleList.add(articleSelectById);

                Collections.sort(articleList, (o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime()));
            }
        } else {
            QueryWrapper<Article> articleQueryWrapper = new QueryWrapper<>();
            articleQueryWrapper.orderByDesc("create_time");
            articleList = articleMapper.selectList(articleQueryWrapper);

        }

        return new ResultVO(ResultStatus.OK, null, articleList);
    }

    // 博客主页得到所有文章以及分页功能实现
    @Override
    public ResultVO getArticlePagination(Integer currentPage, Integer pageSize, Long labelId, String userId) {
        Page page = new Page<>(currentPage, pageSize);
        if (labelId != -1L) {
            QueryWrapper<ArticleLabel> articleLabelQueryWrapper = new QueryWrapper<>();
            articleLabelQueryWrapper.eq("label_id", labelId);
            List<ArticleLabel> articleLabelList = articleLabelMapper.selectList(articleLabelQueryWrapper);
            List<Long> ids = new ArrayList<>();
            for (ArticleLabel articleLabel : articleLabelList) {
                Long articleId = articleLabel.getArticleId();
                ids.add(articleId);
            }

            // selectPage方法查询到的数据不可为空
            if (ids.size() > 0) {
                QueryWrapper<Article> articleQueryWrapper = new QueryWrapper<>();
                articleQueryWrapper.eq("status", CommonEnum.SUCCESS.getCode());
                articleQueryWrapper.in("id", ids);

                Page selectPage = articleMapper.selectPage(page, articleQueryWrapper);
                List<Article> records = selectPage.getRecords();
                List<ArticleVo> articleVoList = new ArrayList<>();
                for (Article article : records) {
                    ArticleVo temp = new ArticleVo();
                    BeanUtils.copyProperties(article, temp);
                    // 查询文章点赞数
                    Long articleId = article.getId();

                    temp.setLikeSum(article.getLikeCount());

                    // 查询文章收藏数
                    temp.setCollect(article.getCollectCount());

                    // 判断用户是否点赞/收藏该文章
                    if (userId != null || !userId.equals("")) {
                        QueryWrapper<CollectContentConnect> collectContentConnectQueryWrapper = new QueryWrapper<>();
                        collectContentConnectQueryWrapper.eq("type", CollectEnum.COLLECT_ARTICLE.getCode());
                        collectContentConnectQueryWrapper.eq("associate_id", articleId);
                        collectContentConnectQueryWrapper.eq( "user_id", userId);
                        Integer isCollect = Math.toIntExact(collectContentConnectMapper.selectCount(collectContentConnectQueryWrapper));
                        temp.setIsCollect(isCollect);
                        QueryWrapper<ArticleLike> userLikeQueryWrapper = new QueryWrapper<>();
                        userLikeQueryWrapper.eq("article_id", articleId);
                        userLikeQueryWrapper.eq("user_id", userId);
                        Integer isLike = Math.toIntExact(articleLikeMapper.selectCount(userLikeQueryWrapper));
                        temp.setIsLike(isLike);
                    } else {
                        temp.setIsCollect(ArticleEnum.NOT_COLLECT_ARTICLE.getCode());
                        temp.setIsLike(ArticleEnum.NOT_LIKE_ARTICLE.getCode());
                    }

                    articleVoList.add(temp);
                }

                // 按创建时间降序排序
                Collections.sort(articleVoList, (o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime()));
                selectPage.setRecords(articleVoList);
                return new ResultVO(ResultStatus.OK, null, selectPage);
            } else {
                return new ResultVO(ResultStatus.OK, null, null);
            }
        } else {
            QueryWrapper<Article> articleQueryWrapper = new QueryWrapper<>();
            articleQueryWrapper.orderByDesc("create_time");
            articleQueryWrapper.eq("status", CommonEnum.SUCCESS.getCode());
            Page selectPage = articleMapper.selectPage(page, articleQueryWrapper);
            List<Article> records = selectPage.getRecords();
            List<ArticleVo> articleVoList = new ArrayList<>();
            for (Article article : records) {
                ArticleVo temp = new ArticleVo();
                BeanUtils.copyProperties(article, temp);
                // 查询文章点赞数
                Long articleId = article.getId();
                temp.setLikeSum(article.getLikeCount());

                // 查询文章收藏数

                temp.setCollect(article.getCollectCount());

                // 判断用户是否点赞/收藏该文章
                if (userId != null || !userId.equals("")) {
                    QueryWrapper<CollectContentConnect> collectContentConnectQueryWrapper = new QueryWrapper<>();
                    collectContentConnectQueryWrapper.eq("type", CollectEnum.COLLECT_ARTICLE.getCode());
                    collectContentConnectQueryWrapper.eq("associate_id", articleId);
                    collectContentConnectQueryWrapper.eq( "user_id", userId);
                    Integer isCollect = Math.toIntExact(collectContentConnectMapper.selectCount(collectContentConnectQueryWrapper));
                    temp.setIsCollect(isCollect);
                    QueryWrapper<ArticleLike> userLikeQueryWrapper = new QueryWrapper<>();
                    userLikeQueryWrapper.eq("article_id", articleId);
                    userLikeQueryWrapper.eq("user_id", userId);
                    Integer isLike = Math.toIntExact(articleLikeMapper.selectCount(userLikeQueryWrapper));
                    temp.setIsLike(isLike);
                } else {
                    temp.setIsCollect(ArticleEnum.NOT_COLLECT_ARTICLE.getCode());
                    temp.setIsLike(ArticleEnum.NOT_LIKE_ARTICLE.getCode());
                }
                articleVoList.add(temp);
                // 根据点击的分页数得到当前页信息
            }

            selectPage.setRecords(articleVoList);
            return new ResultVO(ResultStatus.OK, null, selectPage);
        }
    }

    // 得到最近热帖
    @Override
    public ResultVO getRecentlyFireArticle() {
        String redisKey = RedisKeyAndTimeEnum.RECENT_FIRE_ARTICLE.getKeyName();
        Integer timeUnit = RedisKeyAndTimeEnum.RECENT_FIRE_ARTICLE.getTimeUnit();
        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
            return new ResultVO(ResultStatus.OK, null, redisTemplate.opsForList().range(redisKey, 0, -1));
        } else {
            QueryWrapper<Article> articleQueryWrapper = new QueryWrapper<>();
            articleQueryWrapper.orderByDesc("view_count");
            articleQueryWrapper.eq("status", CommonEnum.SUCCESS.getCode());
            articleQueryWrapper.orderByDesc("collect_count");
            articleQueryWrapper.orderByDesc("like_count");
            articleQueryWrapper.orderByDesc("comment_count");
            articleQueryWrapper.last("limit 10");
            List<Article> articleList = articleMapper.selectList(articleQueryWrapper);
            if (articleList != null && articleList.size() > 0) {
                redisTemplate.opsForList().rightPushAll(redisKey, articleList);
                redisTemplate.expire(redisKey, timeUnit, TimeUnit.DAYS);
                return new ResultVO(ResultStatus.OK, null, articleList);
            }
            return new ResultVO(ResultStatus.OK, null, "");
        }
    }


    // 用户点赞功能实现
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO userClickPraise(Long articleId, Long userId, Long recipientId) {
        QueryWrapper<ArticleLike> userLikeQueryWrapper = new QueryWrapper<>();
        userLikeQueryWrapper.eq("user_id", userId);
        userLikeQueryWrapper.eq("article_id", articleId);
        ArticleLike selectOne = articleLikeMapper.selectOne(userLikeQueryWrapper);
        if (selectOne != null) {
            return new ResultVO(ResultStatus.NO, "不可重复点赞", null);
        }
        ArticleLike articleLike = new ArticleLike();
        articleLike.setArticleId(articleId);
        articleLike.setUserId(userId);
        articleLike.setCreateTime(new Date());

        int insert = articleLikeMapper.insert(articleLike);

        if (insert > 0) {
            // 文章点赞数 + 1;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("event", EventConstant.articleLikeCountAddOne);
            jsonObject.put("articleId", articleId);
            jsonObject.put("userId", userId);
            jsonObject.put("authorId", recipientId);
            Message message = new Message(jsonObject.toJSONString().getBytes());
            rabbitTemplate.convertAndSend(RabbitmqExchangeName.articleUpdateDirectExchange,
                    RabbitmqRoutingName.articleUpdateRouting, message);

            // 加入点赞消息原文表
            JSONObject data = new JSONObject();
            data.put("event", EventConstant.insertArticleLikeContentMessage);
            data.put("associationId", articleId);
            data.put("senderId", userId);
            data.put("recipientId", recipientId);
            Message message1 = new Message(data.toJSONString().getBytes());
            rabbitTemplate.convertAndSend(RabbitmqExchangeName.articleInsertDirectExchange,
                    RabbitmqRoutingName.articleInsertRouting, message1);
            return new ResultVO(ResultStatus.OK, "点赞成功", null);
        }
        return new ResultVO(ResultStatus.NO, "点赞失败", null);
    }

    // 用户取消点赞
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO userClickOppose(Long articleId, Long userId, Long authorId) {
        QueryWrapper<ArticleLike> userLikeQueryWrapper = new QueryWrapper<>();
        userLikeQueryWrapper.eq("user_id", userId);
        userLikeQueryWrapper.eq("article_id", articleId);
        ArticleLike articleLike = articleLikeMapper.selectOne(userLikeQueryWrapper);
        if (articleLike == null) {
            return new ResultVO(ResultStatus.NO, "您还未对该文章点赞。", null);
        } else {
            int deleteById = articleLikeMapper.deleteById(articleLike);
            if (deleteById > 0) {
                // 文章点赞数 - 1
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("event", EventConstant.articleLikeCountSubOne);
                jsonObject.put("articleId", articleId);
                jsonObject.put("userId", userId);
                jsonObject.put("authorId", authorId);
                Message message = new Message(jsonObject.toJSONString().getBytes());
                rabbitTemplate.convertAndSend(RabbitmqExchangeName.articleUpdateDirectExchange,
                        RabbitmqRoutingName.articleUpdateRouting, message);
                return new ResultVO(ResultStatus.OK, "取消点赞成功", null);
            } else {
                return new ResultVO(ResultStatus.NO, "取消失败", null);
            }
        }
    }


    // 通过文章id得到文章信息
    @Override
    public ResultVO getArticleInformationByArticleId(Long articleId, String userId) {

        ArticleVo articleVo = new ArticleVo();
        Article article = articleMapper.selectById(articleId);
        BeanUtils.copyProperties(article, articleVo);

        // 得到文章点赞，收藏数目
        articleVo.setCollect(article.getCollectCount());
        articleVo.setLikeSum(article.getLikeCount());
        articleVo.setViewCount(article.getViewCount());

        // 判断用户是否点赞/收藏该文章
        if (userId != null || !userId.equals("")) {
            QueryWrapper<ArticleLike> userLikeQueryWrapper = new QueryWrapper<>();
            userLikeQueryWrapper.eq("article_id", articleId);
            QueryWrapper<CollectContentConnect> collectContentConnectQueryWrapper = new QueryWrapper<>();
            collectContentConnectQueryWrapper.eq("associate_id", articleId);
            collectContentConnectQueryWrapper.eq("type", CollectEnum.COLLECT_ARTICLE.getCode());
            collectContentConnectQueryWrapper.eq( "user_id", userId);
            Integer isCollect = Math.toIntExact(collectContentConnectMapper.selectCount(collectContentConnectQueryWrapper));
            articleVo.setIsCollect(isCollect);
            userLikeQueryWrapper.eq("user_id", userId);
            Integer isLike = Math.toIntExact(articleLikeMapper.selectCount(userLikeQueryWrapper));
            articleVo.setIsLike(isLike);
        } else {
            articleVo.setIsCollect(ArticleEnum.NOT_COLLECT_ARTICLE.getCode());
            articleVo.setIsLike(ArticleEnum.NOT_LIKE_ARTICLE.getCode());
        }
        return new ResultVO(ResultStatus.OK, null, articleVo);
    }

    /**
     * 安排字段得到文章列表
     * @return {@link ResultVO}
     * @author wusihao
     * @date 2023/7/10 15:37
     */
    @Override
    public ResultVO getArticleListBySort() {
        QueryWrapper<Article> articleQueryWrapper = new QueryWrapper<>();
        articleQueryWrapper.eq("status", CommonEnum.SUCCESS.getCode());
        articleQueryWrapper.orderByAsc("sort");
        List<Article> articleList = articleMapper.selectList(articleQueryWrapper);
        return new ResultVO(ResultStatus.OK, null, articleList);
    }

}
