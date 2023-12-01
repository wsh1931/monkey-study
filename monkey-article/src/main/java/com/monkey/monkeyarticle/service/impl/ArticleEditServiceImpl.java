package com.monkey.monkeyarticle.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.constants.MessageEnum;
import com.monkey.monkeyUtils.constants.ReportCommentEnum;
import com.monkey.monkeyUtils.constants.ReportContentEnum;
import com.monkey.monkeyUtils.mapper.*;
import com.monkey.monkeyUtils.pojo.*;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import com.monkey.monkeyarticle.constant.ArticleEnum;
import com.monkey.monkeyarticle.feign.ArticleToSearchFeignService;
import com.monkey.monkeyarticle.mapper.*;
import com.monkey.monkeyarticle.pojo.*;
import com.monkey.monkeyarticle.service.ArticleEditService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/11/30 8:46
 * @version: 1.0
 * @description:
 */
@Service
public class ArticleEditServiceImpl implements ArticleEditService {
    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private LabelMapper labelMapper;
    @Resource
    private ArticleLabelMapper articleLabelMapper;
    @Resource
    private ArticleToSearchFeignService articleToSearchFeignService;
    /**
     * 通过文章id查询文章信息
     *
     * @param articleId 文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/30 8:52
     */
    @Override
    public R queryArticleInfoById(Long articleId) {
        Article article = articleMapper.selectById(articleId);
        LambdaQueryWrapper<ArticleLabel> articleLabelLambdaQueryWrapper = new LambdaQueryWrapper<>();
        articleLabelLambdaQueryWrapper.eq(ArticleLabel::getArticleId, articleId);
        articleLabelLambdaQueryWrapper.select(ArticleLabel::getLabelId);
        List<Object> labelIdList = articleLabelMapper.selectObjs(articleLabelLambdaQueryWrapper);
        if (labelIdList != null && labelIdList.size() > 0) {
            LambdaQueryWrapper<Label> labelLambdaQueryWrapper = new LambdaQueryWrapper<>();
            labelLambdaQueryWrapper.in(Label::getId, labelIdList);
            List<Label> labelList = labelMapper.selectList(labelLambdaQueryWrapper);
            article.setLabelList(labelList);
        }
        return R.ok(article);
    }

    /**
     * 删除数据库中的图片
     *
     * @param articleId 文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/30 9:12
     */
    @Override
    public R deleteArticlePicture(Long articleId) {
        LambdaUpdateWrapper<Article> articleLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        articleLambdaUpdateWrapper.eq(Article::getId, articleId);
        articleLambdaUpdateWrapper.set(Article::getPhoto, null);
        articleLambdaUpdateWrapper.set(Article::getUpdateUser, JwtUtil.getUserId());
        articleLambdaUpdateWrapper.set(Article::getUpdateTime, new Date());
        articleMapper.update(null, articleLambdaUpdateWrapper);

        Article article = new Article();
        article.setPhoto(null);
        article.setId(articleId);
        articleToSearchFeignService.updateArticle(JSONObject.toJSONString(article));
        return R.ok();
    }

    /**
     * 更新文章图片
     *
     * @param photo 上传图片地址
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/30 10:15
     */
    @Override
    public R uploadArticlePicture(String photo, Long articleId) {
        LambdaUpdateWrapper<Article> articleLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        articleLambdaUpdateWrapper.eq(Article::getId, articleId);
        articleLambdaUpdateWrapper.set(Article::getPhoto, photo);
        String userId = JwtUtil.getUserId();
        articleLambdaUpdateWrapper.set(Article::getUpdateUser, userId);
        articleLambdaUpdateWrapper.set(Article::getUpdateTime, new Date());
        articleMapper.update(null, articleLambdaUpdateWrapper);

        Article article = new Article();
        article.setPhoto(photo);
        article.setId(articleId);
        article.setUserId(Long.parseLong(userId));
        articleToSearchFeignService.updateArticle(JSONObject.toJSONString(article));
        return R.ok();
    }

    /**
     * 更新文章
     *
     * @param article 文字实体类
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/30 10:29
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R updateArticle(Article article) {
        // 更新文章信息
        LambdaUpdateWrapper<Article> articleLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        Long articleId = article.getId();
        articleLambdaUpdateWrapper.eq(Article::getId, articleId);
        article.setUpdateUser(Long.parseLong(JwtUtil.getUserId()));
        article.setUpdateTime(new Date());
        article.setStatus(ArticleEnum.REVIEWING.getCode());
        articleMapper.update(article, articleLambdaUpdateWrapper);

        // 更新文章标签信息
        List<Label> labelList = article.getLabelList();
        LambdaQueryWrapper<ArticleLabel> labelLambdaQueryWrapper = new LambdaQueryWrapper<>();
        labelLambdaQueryWrapper.eq(ArticleLabel::getArticleId, articleId);
        articleLabelMapper.delete(labelLambdaQueryWrapper);
        List<String> labelName = new ArrayList<>(labelList.size());
        labelList.forEach(label -> {
            labelName.add(label.getLabelName());
            ArticleLabel articleLabel = new ArticleLabel();
            articleLabel.setArticleId(articleId);
            articleLabel.setLabelId(label.getId());
            articleLabelMapper.insert(articleLabel);
        });

        article.setLabelName(labelName);
        articleToSearchFeignService.updateArticle(JSONObject.toJSONString(article));
        return R.ok();
    }


}
