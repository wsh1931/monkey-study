package com.monkey.monkeyarticle.authority;

import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import com.monkey.monkeyarticle.mapper.ArticleMapper;
import com.monkey.monkeyarticle.pojo.Article;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/11/30 9:08
 * @version: 1.0
 * @description:
 */
@Component
public class ArticleCuscomAuthority {
    @Resource
    private ArticleMapper articleMapper;
    /**
     * 判断文章作者是否是当前登录用户
     *
     * @param articleId 文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/30 9:09
     */
    public boolean judgeIsAuthor(Long articleId) {
        Article article = articleMapper.selectById(articleId);
        Long articleUserId = article.getUserId();
        Long userId = Long.parseLong(JwtUtil.getUserId());
        return userId.equals(articleUserId);
    }
}
