package com.monkey.monkeyarticle.service.impl.publish;

import com.monkey.monkeyUtils.result.ResultStatus;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyarticle.mapper.ArticleLabelMapper;
import com.monkey.monkeyarticle.mapper.ArticleMapper;
import com.monkey.monkeyarticle.pojo.Article;
import com.monkey.monkeyarticle.pojo.ArticleLabel;
import com.monkey.monkeyarticle.service.publish.PublishService;
import com.monkey.spring_security.pojo.user.User;
import com.monkey.spring_security.user.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class PublishServiceImpl implements PublishService {
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleLabelMapper articleLabelMapper;
    // 发布文章
    @Override
    public ResultVO publishArticle(Map<String, String> data) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl userDetails = (UserDetailsImpl) usernamePasswordAuthenticationToken.getPrincipal();
        User user = userDetails.getUser();
        Long userId = user.getId();
        String title = data.get("title");
        String labelId = data.get("labelId");
        labelId = labelId.substring(1, labelId.length() - 1);
        String[] labelIdList = labelId.split(",");
        String content = data.get("content");
        String profile = data.get("profile");
        String photo = data.get("photo");

        Article article = new Article();
        article.setContent(content);
        article.setUserId(userId);
        article.setTitle(title);
        article.setCreateTime(new Date());
        article.setProfile(profile);
        article.setPhoto(photo);
        article.setUpdateTime(new Date());
        articleMapper.insert(article);


        for (String label : labelIdList) {
            long labelid = Long.parseLong(label);
            ArticleLabel articleLabel = new ArticleLabel();
            articleLabel.setArticleId(article.getId());
            articleLabel.setLabelId(labelid);
            articleLabelMapper.insert(articleLabel);
        }
        return new ResultVO(ResultStatus.OK, null, null);
    }
}
