package com.monkey.monkeybackend.Service.Impl.Blog;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeybackend.Mapper.Blog.ArticleMapper;
import com.monkey.monkeybackend.Mapper.Blog.LabelMapper;
import com.monkey.monkeybackend.Mapper.User.UserCollectMapper;
import com.monkey.monkeybackend.Mapper.User.UserLikeMapper;
import com.monkey.monkeybackend.Pojo.Article;
import com.monkey.monkeybackend.Pojo.Label;
import com.monkey.monkeybackend.Pojo.Vo.ArticleVo;
import com.monkey.monkeybackend.Pojo.user.User;
import com.monkey.monkeybackend.Pojo.user.UserCollect;
import com.monkey.monkeybackend.Pojo.user.UserLike;
import com.monkey.monkeybackend.Service.Blog.ArticleService;
import com.monkey.monkeybackend.config.SpringSecurity.UserDetailsImpl;
import com.monkey.monkeybackend.utils.result.ResultStatus;
import com.monkey.monkeybackend.utils.result.ResultVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private UserLikeMapper userLikeMapper;

    @Autowired
    private UserCollectMapper userCollectMapper;

    // 通过标签id得到文章内容
    @Override
    public ResultVO getArticleContentByLabelId(String labelId) {
        List<Article> articleList;

        if (!"-1".equals(labelId)) {
            QueryWrapper<Article> articleQueryWrapper = new QueryWrapper<>();
            articleQueryWrapper.eq("label_id", Long.parseLong(labelId));
            articleQueryWrapper.orderByDesc("create_time");
            articleList = articleMapper.selectList(articleQueryWrapper);
        } else {
            QueryWrapper<Article> articleQueryWrapper = new QueryWrapper<>();
            articleQueryWrapper.orderByDesc("create_time");
            articleList = articleMapper.selectList(articleQueryWrapper);
        }


        return new ResultVO(ResultStatus.OK, null, articleList);
    }

    // 博客主页分页实现
    @Override
    public ResultVO pagination(Integer currentPage, Integer pageSize, Long labelId) {
        Page page = new Page<>(currentPage, pageSize);
        QueryWrapper<Article> articleQueryWrapper = new QueryWrapper<>();
        if (labelId != -1L) {
            articleQueryWrapper.eq("label_id", labelId);
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

                articleVoList.add(temp);
            }
            selectPage.setRecords(articleVoList);
            return new ResultVO(ResultStatus.OK, null, selectPage);
        } else {
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
        return new ResultVO(ResultStatus.OK, null, articleVo);
    }
}
