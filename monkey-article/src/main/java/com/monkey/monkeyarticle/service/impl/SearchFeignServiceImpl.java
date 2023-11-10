package com.monkey.monkeyarticle.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.mapper.LabelMapper;
import com.monkey.monkeyUtils.pojo.Label;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyarticle.mapper.ArticleLabelMapper;
import com.monkey.monkeyarticle.mapper.ArticleMapper;
import com.monkey.monkeyarticle.pojo.Article;
import com.monkey.monkeyarticle.pojo.ArticleLabel;
import com.monkey.monkeyarticle.service.SearchFeignService;
import com.monkey.spring_security.mapper.UserMapper;
import com.monkey.spring_security.pojo.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/11/8 8:54
 * @version: 1.0
 * @description:
 */
@Service
public class SearchFeignServiceImpl implements SearchFeignService {
    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private ArticleLabelMapper articleLabelMapper;
    @Resource
    private LabelMapper labelMapper;
    @Resource
    private UserMapper userMapper;
    /**
     * 查询所有文章
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/8 8:59
     */
    @Override
    public R queryAllArticle() {
        QueryWrapper<Article> articleQueryWrapper = new QueryWrapper<>();
        articleQueryWrapper.eq("status", CommonEnum.SUCCESS.getCode());
        List<Article> articles = articleMapper.selectList(articleQueryWrapper);
        articles.parallelStream().forEach(f -> {
            Long articleId = f.getId();
            QueryWrapper<ArticleLabel> articleLabelQueryWrapper = new QueryWrapper<>();
            articleLabelQueryWrapper.eq("article_id", articleId);
            articleLabelQueryWrapper.select("label_id");
            List<Object> objects = articleLabelMapper.selectObjs(articleLabelQueryWrapper);
            if (objects.size() > 0) {
                List<String> label = new ArrayList<>(objects.size());
                QueryWrapper<Label> labelQueryWrapper = new QueryWrapper<>();
                labelQueryWrapper.in("id", objects);
                labelQueryWrapper.select("label_name");
                List<Label> labelList = labelMapper.selectList(labelQueryWrapper);
                for (Label label1 : labelList) {
                    label.add(label1.getLabelName());
                }

                f.setLabelName(label);
            }

            // 得到文章作者信息
            User user = userMapper.selectById(f.getUserId());
            f.setUsername(user.getUsername());
            f.setUserHeadImg(user.getPhoto());
            f.setUserBrief(user.getBrief());

        });
        return R.ok(articles);
    }
}
