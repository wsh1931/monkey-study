package com.monkey.monkeyblog.service.Impl.blog;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeyUtils.result.ResultStatus;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyblog.mapper.article.ArticleLabelMapper;
import com.monkey.monkeyblog.mapper.article.ArticleMapper;
import com.monkey.monkeyblog.mapper.user.UserCollectMapper;
import com.monkey.monkeyblog.mapper.user.UserLikeMapper;
import com.monkey.monkeyblog.pojo.article.Article;
import com.monkey.monkeyblog.pojo.Vo.ArticleVo;
import com.monkey.monkeyblog.pojo.article.ArticleLabel;
import com.monkey.monkeyblog.pojo.user.UserCollect;
import com.monkey.monkeyblog.pojo.user.UserLike;
import com.monkey.monkeyblog.service.blog.BlogArticleService;

import com.monkey.spring_security.pojo.user.User;
import com.monkey.spring_security.user.UserDetailsImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BlogArticleServiceImpl implements BlogArticleService {
    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private UserLikeMapper userLikeMapper;

    @Autowired
    private UserCollectMapper userCollectMapper;

    @Autowired
    private ArticleLabelMapper articleLabelMapper;

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

    // 博客主页分页实现
    @Override
    public ResultVO pagination(Integer currentPage, Integer pageSize, Long labelId, String userId) {
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
                    QueryWrapper<UserLike> userLikeQueryWrapper = new QueryWrapper<>();
                    userLikeQueryWrapper.eq("article_id", articleId);
                    Long userLikeSum = userLikeMapper.selectCount(userLikeQueryWrapper);


                    // 查询文章收藏数
                    QueryWrapper<UserCollect> userCollectQueryWrapper = new QueryWrapper<>();
                    userCollectQueryWrapper.eq("article_id", articleId);
                    Long collect = userCollectMapper.selectCount(userCollectQueryWrapper);
                    temp.setCollect(collect);
                    temp.setLikeSum(userLikeSum);

                    // 判断用户是否点赞/收藏该文章
                    if (userId != null || !userId.equals("")) {
                        userCollectQueryWrapper.eq( "user_id", userId);
                        Long isCollect = userCollectMapper.selectCount(userCollectQueryWrapper);
                        temp.setIsCollect(isCollect);
                        userLikeQueryWrapper.eq("user_id", userId);
                        Long isLike = userLikeMapper.selectCount(userLikeQueryWrapper);
                        temp.setIsLike(isLike);
                    } else {
                        temp.setIsCollect(0L);
                        temp.setIsLike(0L);
                    }

                    articleVoList.add(temp);
                }

                // 按创建世界降序排序
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
                QueryWrapper<UserLike> userLikeQueryWrapper = new QueryWrapper<>();
                userLikeQueryWrapper.eq("article_id", articleId);
                Long userLikeSum = userLikeMapper.selectCount(userLikeQueryWrapper);

                // 查询文章收藏数
                QueryWrapper<UserCollect> userCollectQueryWrapper = new QueryWrapper<>();
                userCollectQueryWrapper.eq("article_id", articleId);
                Long collect = userCollectMapper.selectCount(userCollectQueryWrapper);
                temp.setCollect(collect);
                temp.setLikeSum(userLikeSum);

                // 判断用户是否点赞/收藏该文章
                if (userId != null || !userId.equals("")) {
                    userCollectQueryWrapper.eq( "user_id", userId);
                    Long isCollect = userCollectMapper.selectCount(userCollectQueryWrapper);
                    temp.setIsCollect(isCollect);
                    userLikeQueryWrapper.eq("user_id", userId);
                    Long isLike = userLikeMapper.selectCount(userLikeQueryWrapper);
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
        QueryWrapper<Article> articleQueryWrapper = new QueryWrapper<>();
        articleQueryWrapper.orderByDesc("visit");
        articleQueryWrapper.orderByDesc("likes");
        articleQueryWrapper.last("limit 10");
        List<Article> articleList = articleMapper.selectList(articleQueryWrapper);
        return new ResultVO(ResultStatus.OK, null, articleList);
    }

    // 用户点赞功能实现
    @Override
    public ResultVO userClickPraise(Map<String, String> data) {
        try {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

            UserDetailsImpl userDetails = (UserDetailsImpl) usernamePasswordAuthenticationToken.getPrincipal();
            User user = userDetails.getUser();
        } catch (Exception e) {
            return new ResultVO(ResultStatus.NO, "您的token已过期，请退出重新登录。", null);
        }
        long articleId = Long.parseLong(data.get("articleId"));
        long userId = Long.parseLong(data.get("userId"));
        QueryWrapper<UserLike> userLikeQueryWrapper = new QueryWrapper<>();
        userLikeQueryWrapper.eq("user_id", userId);
        userLikeQueryWrapper.eq("article_id", articleId);
        UserLike selectOne = userLikeMapper.selectOne(userLikeQueryWrapper);
        if (selectOne != null) {
            return new ResultVO(ResultStatus.NO, "不可重复点赞", null);
        }
        UserLike userLike = new UserLike();
        userLike.setArticleId(articleId);
        userLike.setUserId(userId);
        userLike.setCreateTime(new Date());
        int insert = userLikeMapper.insert(userLike);
        if (insert > 0) {
            return new ResultVO(ResultStatus.OK, null, null);
        }
        return new ResultVO(ResultStatus.NO, "点赞失败", null);
    }

    // 用户取消点赞
    @Override
    public ResultVO userClickOppose(Map<String, String> data) {
        long userId = Long.parseLong(data.get("userId"));
        long articleId = Long.parseLong(data.get("articleId"));
        QueryWrapper<UserLike> userLikeQueryWrapper = new QueryWrapper<>();
        userLikeQueryWrapper.eq("user_id", userId);
        userLikeQueryWrapper.eq("article_id", articleId);
        UserLike userLike = userLikeMapper.selectOne(userLikeQueryWrapper);
        if (userLike == null) {
            return new ResultVO(ResultStatus.NO, "您还未对该文章点赞。", null);
        } else {
            int deleteById = userLikeMapper.deleteById(userLike);
            if (deleteById > 0) {
                return new ResultVO(ResultStatus.OK, "取消点赞成功", null);
            } else {
                return new ResultVO(ResultStatus.NO, "取消失败", null);
            }
        }
    }

    // 用户收藏文章
    @Override
    public ResultVO userCollect(Map<String, String> data) {
        long userId = Long.parseLong(data.get("userId"));
        long articleId = Long.parseLong(data.get("articleId"));
        QueryWrapper<UserCollect> userCollectQueryWrapper = new QueryWrapper<>();
        userCollectQueryWrapper.eq("user_id", userId);
        userCollectQueryWrapper.eq("article_id", articleId);
        UserCollect userCollect = userCollectMapper.selectOne(userCollectQueryWrapper);
        if(userCollect != null) {
            int deleteById = userCollectMapper.deleteById(userCollect);
            if (deleteById > 0) {
                return new ResultVO(ResultStatus.OK, "您已取消收藏", null);
            } else {
                return new ResultVO(ResultStatus.NO, "取消失败", null);
            }
        } else {
            UserCollect userCollect1 = new UserCollect();
            userCollect1.setArticleId(articleId);
            userCollect1.setUserId(userId);
            userCollect1.setCreateTime(new Date());
            int insert = userCollectMapper.insert(userCollect1);
            if (insert > 0) {
                return new ResultVO(ResultStatus.OK, "收藏成功", null);
            } else {
                return new ResultVO(ResultStatus.NO, "收藏失败", null);
            }
        }
    }

    // 通过文章id得到文章信息
    @Override
    public ResultVO getArticleInformationByArticleId(Map<String, String> data) {
        long articleId = Long.parseLong(data.get("articleId"));
        String userId = data.get("userId");
        ArticleVo articleVo = new ArticleVo();
        Article article = articleMapper.selectById(articleId);
        BeanUtils.copyProperties(article, articleVo);

        // 得到文章点赞，收藏数目
        QueryWrapper<UserLike> userLikeQueryWrapper = new QueryWrapper<>();
        userLikeQueryWrapper.eq("article_id", articleId);
        Long countLike = userLikeMapper.selectCount(userLikeQueryWrapper);
        QueryWrapper<UserCollect> userCollectQueryWrapper = new QueryWrapper<>();
        userCollectQueryWrapper.eq("article_id", articleId);
        Long countCollect = userCollectMapper.selectCount(userCollectQueryWrapper);
        articleVo.setCollect(countCollect);
        articleVo.setLikeSum(countLike);

        // 判断用户是否点赞/收藏该文章
        if (userId != null || !userId.equals("")) {
            userCollectQueryWrapper.eq( "user_id", userId);
            Long isCollect = userCollectMapper.selectCount(userCollectQueryWrapper);
            articleVo.setIsCollect(isCollect);
            userLikeQueryWrapper.eq("user_id", userId);
            Long isLike = userLikeMapper.selectCount(userLikeQueryWrapper);
            articleVo.setIsLike(isLike);
        } else {
            articleVo.setIsCollect(0L);
            articleVo.setIsLike(0L);
        }
        return new ResultVO(ResultStatus.OK, null, articleVo);
    }
}
