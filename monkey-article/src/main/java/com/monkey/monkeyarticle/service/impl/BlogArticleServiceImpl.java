package com.monkey.monkeyarticle.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeyUtils.redis.RedisTimeConstant;
import com.monkey.monkeyUtils.redis.RedisKeyConstant;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.result.ResultStatus;
import com.monkey.monkeyUtils.result.ResultVO;


import com.monkey.monkeyarticle.mapper.ArticleCollectMapper;
import com.monkey.monkeyarticle.mapper.ArticleLabelMapper;
import com.monkey.monkeyarticle.mapper.ArticleLikeMapper;
import com.monkey.monkeyarticle.mapper.ArticleMapper;
import com.monkey.monkeyarticle.pojo.Article;
import com.monkey.monkeyarticle.pojo.ArticleCollect;
import com.monkey.monkeyarticle.pojo.ArticleLabel;
import com.monkey.monkeyarticle.pojo.ArticleLike;
import com.monkey.monkeyarticle.pojo.vo.ArticleVo;
import com.monkey.monkeyarticle.service.BlogArticleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;


@Service
public class BlogArticleServiceImpl implements BlogArticleService {
    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleLikeMapper articleLikeMapper;

    @Autowired
    private ArticleCollectMapper articleCollectMapper;

    @Autowired
    private ArticleLabelMapper articleLabelMapper;
    @Autowired
    private RedisTemplate redisTemplate;
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
                articleQueryWrapper.in("id", ids);

                Page selectPage = articleMapper.selectPage(page, articleQueryWrapper);
                List<Article> records = selectPage.getRecords();
                List<ArticleVo> articleVoList = new ArrayList<>();
                for (Article article : records) {
                    ArticleVo temp = new ArticleVo();
                    BeanUtils.copyProperties(article, temp);
                    // 查询文章点赞数
                    Long articleId = article.getId();
                    QueryWrapper<ArticleLike> userLikeQueryWrapper = new QueryWrapper<>();
                    userLikeQueryWrapper.eq("article_id", articleId);
                    Long userLikeSum = articleLikeMapper.selectCount(userLikeQueryWrapper);


                    // 查询文章收藏数
                    QueryWrapper<ArticleCollect> userCollectQueryWrapper = new QueryWrapper<>();
                    userCollectQueryWrapper.eq("article_id", articleId);
                    Long collect = articleCollectMapper.selectCount(userCollectQueryWrapper);
                    temp.setCollect(collect);
                    temp.setLikeSum(userLikeSum);

                    // 判断用户是否点赞/收藏该文章
                    if (userId != null || !userId.equals("")) {
                        userCollectQueryWrapper.eq( "user_id", userId);
                        Long isCollect = articleCollectMapper.selectCount(userCollectQueryWrapper);
                        temp.setIsCollect(isCollect);
                        userLikeQueryWrapper.eq("user_id", userId);
                        Long isLike = articleLikeMapper.selectCount(userLikeQueryWrapper);
                        temp.setIsLike(isLike);
                    } else {
                        temp.setIsCollect(0L);
                        temp.setIsLike(0L);
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
            Page selectPage = articleMapper.selectPage(page, articleQueryWrapper);
            List<Article> records = selectPage.getRecords();
            List<ArticleVo> articleVoList = new ArrayList<>();
            for (Article article : records) {
                ArticleVo temp = new ArticleVo();
                BeanUtils.copyProperties(article, temp);
                // 查询文章点赞数
                Long articleId = article.getId();
                QueryWrapper<ArticleLike> userLikeQueryWrapper = new QueryWrapper<>();
                userLikeQueryWrapper.eq("article_id", articleId);
                Long userLikeSum = articleLikeMapper.selectCount(userLikeQueryWrapper);

                // 查询文章收藏数
                QueryWrapper<ArticleCollect> userCollectQueryWrapper = new QueryWrapper<>();
                userCollectQueryWrapper.eq("article_id", articleId);
                Long collect = articleCollectMapper.selectCount(userCollectQueryWrapper);
                temp.setCollect(collect);
                temp.setLikeSum(userLikeSum);

                // 判断用户是否点赞/收藏该文章
                if (userId != null || !userId.equals("")) {
                    userCollectQueryWrapper.eq( "user_id", userId);
                    Long isCollect = articleCollectMapper.selectCount(userCollectQueryWrapper);
                    temp.setIsCollect(isCollect);
                    userLikeQueryWrapper.eq("user_id", userId);
                    Long isLike = articleLikeMapper.selectCount(userLikeQueryWrapper);
                    temp.setIsLike(isLike);
                } else {
                    temp.setIsCollect(0L);
                    temp.setIsLike(0L);
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
        if (Boolean.TRUE.equals(redisTemplate.hasKey(RedisKeyConstant.FIRE_RECENTLY))) {
            return new ResultVO(ResultStatus.OK, null, redisTemplate.opsForList().range(RedisKeyConstant.FIRE_RECENTLY, 0, -1));
        } else {
            QueryWrapper<Article> articleQueryWrapper = new QueryWrapper<>();
            articleQueryWrapper.orderByDesc("visit");
            articleQueryWrapper.orderByDesc("likes");
            articleQueryWrapper.last("limit 10");
            List<Article> articleList = articleMapper.selectList(articleQueryWrapper);
            redisTemplate.opsForList().rightPushAll(RedisKeyConstant.FIRE_RECENTLY, articleList);
            redisTemplate.expire(RedisKeyConstant.FIRE_RECENTLY, RedisTimeConstant.FIRE_RECENTLY_EXPIRE_TIME, TimeUnit.DAYS);
            return new ResultVO(ResultStatus.OK, null, articleList);
        }
    }

    // todo 利用消息队列给接收者发送信息
    // 用户点赞功能实现
    @Override
    public ResultVO userClickPraise(Long articleId, Long userId) {
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
            return new ResultVO(ResultStatus.OK, "点赞成功", null);
        }
        return new ResultVO(ResultStatus.NO, "点赞失败", null);
    }

    // 用户取消点赞
    @Override
    public ResultVO userClickOppose(Long articleId, Long userId) {

        QueryWrapper<ArticleLike> userLikeQueryWrapper = new QueryWrapper<>();
        userLikeQueryWrapper.eq("user_id", userId);
        userLikeQueryWrapper.eq("article_id", articleId);
        ArticleLike articleLike = articleLikeMapper.selectOne(userLikeQueryWrapper);
        if (articleLike == null) {
            return new ResultVO(ResultStatus.NO, "您还未对该文章点赞。", null);
        } else {
            int deleteById = articleLikeMapper.deleteById(articleLike);
            if (deleteById > 0) {
                return new ResultVO(ResultStatus.OK, "取消点赞成功", null);
            } else {
                return new ResultVO(ResultStatus.NO, "取消失败", null);
            }
        }
    }

    // 用户收藏文章
    @Override
    public ResultVO userCollect(Long articleId, Long userId) {

        QueryWrapper<ArticleCollect> userCollectQueryWrapper = new QueryWrapper<>();
        userCollectQueryWrapper.eq("user_id", userId);
        userCollectQueryWrapper.eq("article_id", articleId);
        ArticleCollect articleCollect = articleCollectMapper.selectOne(userCollectQueryWrapper);
        if(articleCollect != null) {
            int deleteById = articleCollectMapper.deleteById(articleCollect);
            if (deleteById > 0) {
                return new ResultVO(ResultStatus.OK, "您已取消收藏", null);
            } else {
                return new ResultVO(ResultStatus.NO, "取消失败", null);
            }
        } else {
            ArticleCollect articleCollect1 = new ArticleCollect();
            articleCollect1.setArticleId(articleId);
            articleCollect1.setUserId(userId);
            articleCollect1.setCreateTime(new Date());
            int insert = articleCollectMapper.insert(articleCollect1);
            if (insert > 0) {
                return new ResultVO(ResultStatus.OK, "收藏成功", null);
            } else {
                return new ResultVO(ResultStatus.NO, "收藏失败", null);
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
        QueryWrapper<ArticleLike> userLikeQueryWrapper = new QueryWrapper<>();
        userLikeQueryWrapper.eq("article_id", articleId);
        Long countLike = articleLikeMapper.selectCount(userLikeQueryWrapper);
        QueryWrapper<ArticleCollect> userCollectQueryWrapper = new QueryWrapper<>();
        userCollectQueryWrapper.eq("article_id", articleId);
        Long countCollect = articleCollectMapper.selectCount(userCollectQueryWrapper);
        articleVo.setCollect(countCollect);
        articleVo.setLikeSum(countLike);

        // 判断用户是否点赞/收藏该文章
        if (userId != null || !userId.equals("")) {
            userCollectQueryWrapper.eq( "user_id", userId);
            Long isCollect = articleCollectMapper.selectCount(userCollectQueryWrapper);
            articleVo.setIsCollect(isCollect);
            userLikeQueryWrapper.eq("user_id", userId);
            Long isLike = articleLikeMapper.selectCount(userLikeQueryWrapper);
            articleVo.setIsLike(isLike);
        } else {
            articleVo.setIsCollect(0L);
            articleVo.setIsLike(0L);
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
        articleQueryWrapper.orderByAsc("sort");
        List<Article> articleList = articleMapper.selectList(articleQueryWrapper);
        return new ResultVO(ResultStatus.OK, null, articleList);
    }

}
