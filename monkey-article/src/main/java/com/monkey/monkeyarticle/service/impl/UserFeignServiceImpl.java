package com.monkey.monkeyarticle.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.mapper.CollectContentConnectMapper;
import com.monkey.monkeyUtils.pojo.CollectContentConnect;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyarticle.constant.ArticleEnum;
import com.monkey.monkeyarticle.mapper.ArticleCommentMapper;
import com.monkey.monkeyarticle.mapper.ArticleLabelMapper;
import com.monkey.monkeyarticle.mapper.ArticleLikeMapper;
import com.monkey.monkeyarticle.mapper.ArticleMapper;
import com.monkey.monkeyarticle.pojo.Article;
import com.monkey.monkeyarticle.pojo.ArticleComment;
import com.monkey.monkeyarticle.pojo.ArticleLabel;
import com.monkey.monkeyarticle.pojo.ArticleLike;
import com.monkey.monkeyarticle.pojo.vo.ArticleVo;
import com.monkey.monkeyarticle.rabbitmq.EventConstant;
import com.monkey.monkeyarticle.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeyarticle.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeyarticle.service.UserFeignService;
import io.swagger.models.auth.In;
import netscape.javascript.JSObject;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author: wusihao
 * @date: 2023/7/31 9:36
 * @version: 1.0
 * @description:
 */
@Service
public class UserFeignServiceImpl implements UserFeignService {
    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private ArticleLikeMapper articleLikeMapper;
    @Resource
    private ArticleCommentMapper articleCommentMapper;
    @Resource
    private ArticleLabelMapper articleLabelMapper;
    @Resource
    private CollectContentConnectMapper collectContentConnectMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;


    /**
     * 通过用户id得到用户发表文章信息
     *
     * @param userId 用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/7/31 9:39
     */
    @Override
    public R getUserArticleCountByUserId(Long userId) {
        QueryWrapper<Article> articleQueryWrapper = new QueryWrapper<>();
        articleQueryWrapper.eq("user_id", userId);
        articleQueryWrapper.eq("status", CommonEnum.SUCCESS.getCode());
        List<Article> articleList = articleMapper.selectList(articleQueryWrapper);
        return R.ok(articleList);
    }

    /**
     * 通过文章id得到文章点赞数
     *
     * @param articleId 文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/7/31 9:51
     */
    @Override
    public R getArticleLikeCountByArticleId(Long articleId) {
        QueryWrapper<ArticleLike> userLikeQueryWrapper = new QueryWrapper<>();
        userLikeQueryWrapper.eq("article_id", articleId);
        Long userLike = articleLikeMapper.selectCount(userLikeQueryWrapper);
        return R.ok(userLike);
    }

    /**
     * 通过文章id得到文章评论数
     *
     * @param articleId 文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/7/31 10:02
     */
    @Override
    public R getArticleCommentCountByArticleId(Long articleId) {
        QueryWrapper<ArticleComment> commentQueryWrapper = new QueryWrapper<>();
        commentQueryWrapper.eq("article_id", articleId);
        Long comment = articleCommentMapper.selectCount(commentQueryWrapper);
        return R.ok(comment);
    }

    /**
     * 通过标签id得到文章标签列表
     *
     * @param labelId 标签id
     * @return {@link null}
     * @author wusihao
     * @date 2023/7/31 10:56
     */
    @Override
    public R getArticleLabelListByLabelId(Long labelId) {
        QueryWrapper<ArticleLabel> articleLabelQueryWrapper = new QueryWrapper<>();
        articleLabelQueryWrapper.eq("label_id", labelId);
        List<ArticleLabel> articleLabelList = articleLabelMapper.selectList(articleLabelQueryWrapper);
        return R.ok(articleLabelList);
    }

    @Override
    public R getArticleLabelListByarticleId(Long articleId) {
        QueryWrapper<ArticleLabel> articleLabelQueryWrapper = new QueryWrapper<>();
        articleLabelQueryWrapper.eq("article_id", articleId);
        List<ArticleLabel> articleLabelList = articleLabelMapper.selectList(articleLabelQueryWrapper);
            return R.ok(articleLabelList);
    }

    /**
     * 通过用户id得到文章分页列表
     *
     * @param currentPage 当前页
     * @param pageSize 页面大小
     * @param labelId 标签id
     * @param userId 用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/7/31 11:18
     */
    @Override
    public R getArticleListByUserId(Long currentPage, Long pageSize, Long labelId, String userId) {
        Page page = new Page<>(currentPage, pageSize);
        if (labelId != -1L) {
            QueryWrapper<ArticleLabel> articleLabelQueryWrapper = new QueryWrapper<>();
            articleLabelQueryWrapper.eq("label_id", labelId);
            articleLabelQueryWrapper.eq("status", CommonEnum.SUCCESS.getCode());
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
                articleQueryWrapper.eq("user_id", userId);

                Page selectPage = articleMapper.selectPage(page, articleQueryWrapper);
                List<Article> records = selectPage.getRecords();
                List<ArticleVo> articleVoList = new ArrayList<>();
                for (Article article : records) {
                    ArticleVo temp = new ArticleVo();
                    BeanUtils.copyProperties(article, temp);
                    Long articleId = article.getId();

                    temp.setCollect(article.getCollectCount());
                    temp.setLikeSum(article.getLikeCount());
                    QueryWrapper<CollectContentConnect> collectContentConnectQueryWrapper = new QueryWrapper<>();
                    collectContentConnectQueryWrapper.eq("associate_id", articleId);
                    collectContentConnectQueryWrapper.eq("type", CommonEnum.COLLECT_ARTICLE.getCode());
                    // 判断用户是否点赞/收藏该文章
                    if (userId != null || !userId.equals("")) {
                        collectContentConnectQueryWrapper.eq("user_id", userId);
                        Integer isCollect = Math.toIntExact(collectContentConnectMapper.selectCount(collectContentConnectQueryWrapper));
                        temp.setIsCollect(isCollect);
                        collectContentConnectQueryWrapper.eq("user_id", userId);
                        Integer isLike = Math.toIntExact(collectContentConnectMapper.selectCount(collectContentConnectQueryWrapper));
                        temp.setIsLike(isLike);
                    } else {
                        temp.setIsCollect(ArticleEnum.NOT_COLLECT_ARTICLE.getCode());
                        temp.setIsLike(ArticleEnum.NOT_COLLECT_ARTICLE.getCode());
                    }

                    articleVoList.add(temp);
                }

                // 按创建时间降序排序
                Collections.sort(articleVoList, (o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime()));
                selectPage.setRecords(articleVoList);
                return R.ok(selectPage);
            } else {
                return R.ok(null);
            }
        } else {
            QueryWrapper<Article> articleQueryWrapper = new QueryWrapper<>();
            articleQueryWrapper.orderByDesc("create_time");
            articleQueryWrapper.eq("status", CommonEnum.SUCCESS.getCode());
            articleQueryWrapper.eq("user_id", userId);
            Page selectPage = articleMapper.selectPage(page, articleQueryWrapper);
            List<Article> records = selectPage.getRecords();
            List<ArticleVo> articleVoList = new ArrayList<>();
            for (Article article : records) {
                ArticleVo temp = new ArticleVo();
                BeanUtils.copyProperties(article, temp);
                // 查询文章点赞数
                Long articleId = article.getId();
                temp.setCollect(article.getCollectCount());
                temp.setLikeSum(article.getLikeCount());

                QueryWrapper<CollectContentConnect> collectContentConnectQueryWrapper = new QueryWrapper<>();
                collectContentConnectQueryWrapper.eq("associate_id", articleId);
                collectContentConnectQueryWrapper.eq("type", CommonEnum.COLLECT_ARTICLE.getCode());
                // 判断用户是否点赞/收藏该文章
                if (userId != null || !userId.equals("")) {
                    collectContentConnectQueryWrapper.eq("user_id", userId);
                    Integer isCollect = Math.toIntExact(collectContentConnectMapper.selectCount(collectContentConnectQueryWrapper));
                    temp.setIsCollect(isCollect);
                    collectContentConnectQueryWrapper.eq("user_id", userId);
                    Integer isLike = Math.toIntExact(collectContentConnectMapper.selectCount(collectContentConnectQueryWrapper));
                    temp.setIsLike(isLike);
                } else {
                    temp.setIsCollect(ArticleEnum.NOT_COLLECT_ARTICLE.getCode());
                    temp.setIsLike(ArticleEnum.NOT_LIKE_ARTICLE.getCode());
                }
                articleVoList.add(temp);
                // 根据点击的分页数得到当前页信息
            }

            selectPage.setRecords(articleVoList);
            return R.ok(selectPage);
        }
    }

    /**
     * 更新文章信息，文章收藏数 + 1
     *
     * @param articleId 文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/5 14:53
     */
    @Override
    public R updateArticleInfo(Long articleId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.articleCollectCountAddOne);
        jsonObject.put("articleId", articleId);
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.articleUpdateDirectExchange,
                RabbitmqRoutingName.articleUpdateRouting, message);
        return R.ok(1);
    }

    /**
     * 更新文章信息, 文章收藏数 - 1
     *
     * @param articleId 文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/5 15:12
     */
    @Override
    public R subUpdateArticleInfo(Long articleId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.articleCollectCountSubOne);
        jsonObject.put("articleId", articleId);
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.articleUpdateDirectExchange,
                RabbitmqRoutingName.articleUpdateRouting, message);
        return R.ok(1);
    }

    /**
     * 通过文章id得到文章信息
     *
     * @param articleId 文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/26 10:29
     */
    @Override
    public R queryArticleById(Long articleId) {
        JSONObject jsonObject = new JSONObject();
        Article article = articleMapper.selectById(articleId);
        jsonObject.put("picture", article.getPhoto());
        jsonObject.put("title", article.getTitle());
        return R.ok(jsonObject);
    }

    /**
     * 通过文章id和评论id得到文章信息
     *
     * @param articleId 文章
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/28 15:09
     */
    @Override
    public R queryArticleAndCommentById(Long articleId, Long commentId) {
        JSONObject jsonObject = new JSONObject();
        Article article = articleMapper.selectById(articleId);
        jsonObject.put("picture", article.getPhoto());
        ArticleComment articleComment = articleCommentMapper.selectById(commentId);
        jsonObject.put("title", articleComment.getContent());
        return R.ok(jsonObject);
    }

    /**
     * 通过文章id得到文章名称
     *
     * @param articleId 文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/29 21:18
     */
    @Override
    public Long queryArticleAuthorById(Long articleId) {
        Article article = articleMapper.selectById(articleId);
        return article.getUserId();
    }


}
