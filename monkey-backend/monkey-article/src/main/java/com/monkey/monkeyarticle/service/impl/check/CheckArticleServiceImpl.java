package com.monkey.monkeyarticle.service.impl.check;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.result.ResultStatus;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyUtils.mapper.LabelMapper;
import com.monkey.monkeyUtils.mapper.UserFansMapper;
import com.monkey.monkeyUtils.pojo.Label;
import com.monkey.monkeyUtils.pojo.user.UserFans;
import com.monkey.monkeyUtils.pojo.user.UserVo;
import com.monkey.monkeyarticle.mapper.*;
import com.monkey.monkeyarticle.pojo.*;
import com.monkey.monkeyarticle.pojo.vo.article.ArticleCommentVo;
import com.monkey.monkeyarticle.service.check.CheckArticleService;
import com.monkey.spring_security.mapper.user.UserMapper;
import com.monkey.spring_security.pojo.user.User;
import com.monkey.spring_security.user.UserDetailsImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CheckArticleServiceImpl implements CheckArticleService {
    @Autowired
    private ArticleLabelMapper articleLabelMapper;
    @Autowired
    private LabelMapper labelMapper;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ArticleCollectMapper articleCollectMapper;

    @Autowired
    private UserFansMapper userFansMapper;

    @Autowired
    private ArticleLikeMapper articleLikeMapper;

    @Autowired
    private ArticleCommentMapper articleCommentMapper;

    @Autowired
    private ArticleCommentLikeMapper commentLikeMapper;


    // 通过文章id查询文章标签信息
    @Override
    public ResultVO getArticleLabelInfoByArticleId(Long articleId) {
        QueryWrapper<ArticleLabel> articleLabelQueryWrapper = new QueryWrapper<>();
        articleLabelQueryWrapper.eq("article_id", articleId);
        List<ArticleLabel> articleLabelList = articleLabelMapper.selectList(articleLabelQueryWrapper);
        List<String> res = new ArrayList<>();
        for (ArticleLabel articleLabel : articleLabelList) {
            Long labelId = articleLabel.getLabelId();
            Label label = labelMapper.selectById(labelId);
            res.add(label.getLabelName());
        }
        return new ResultVO(ResultStatus.OK, null, res);
    }

    // 通过文章id得到作者信息
    @Override
    public ResultVO getAuthorInfoByArticleId(Long articleId, String fansId) {
        Article article = articleMapper.selectById(articleId);
        Long userId = article.getUserId();
        User user = userMapper.selectById(userId);
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user, userVo);

        // 得到用户游览数(所有文章游览数总和)
        QueryWrapper<Article> articleQueryWrapper = new QueryWrapper<>();
        articleQueryWrapper.eq("user_id", userId);
        List<Article> articleList = articleMapper.selectList(articleQueryWrapper);
        // 得到用户发表文章数, 用户点赞数, 用户收藏数
        userVo.setArticleSum((long) articleList.size());
        Long userVisit = 0L;
        Long userLike = 0L;
        Long userCollect = 0L;
        for (Article article1 : articleList) {
            userVisit += article1.getVisit();
            Long article1Id = article1.getId();
            QueryWrapper<ArticleLike> userLikeQueryWrapper = new QueryWrapper<>();
            userLikeQueryWrapper.eq("article_id", article1Id);
            userLike += articleLikeMapper.selectCount(userLikeQueryWrapper);
            QueryWrapper<ArticleCollect> userCollectQueryWrapper = new QueryWrapper<>();
            userCollectQueryWrapper.eq("article_id", article1Id);
            userCollect += articleCollectMapper.selectCount(userCollectQueryWrapper);
        }
        userVo.setVisit(userVisit);
        userVo.setLikeSum(userLike);
        userVo.setUserCollect(userCollect);

        QueryWrapper<ArticleCollect> articleCollectQueryWrapper = new QueryWrapper<>();
        articleCollectQueryWrapper.eq("user_id", userId);
        userVo.setCollect(articleCollectMapper.selectCount(articleCollectQueryWrapper));
        // 得到用户粉丝数
        QueryWrapper<UserFans> userFansQueryWrapper = new QueryWrapper<>();
        userFansQueryWrapper.eq("user_id", userId);
        Long fansSum = userFansMapper.selectCount(userFansQueryWrapper);
        userVo.setFans(fansSum);

        // 得到用户关注数
        QueryWrapper<UserFans> userFansQueryWrapper1 = new QueryWrapper<>();
        userFansQueryWrapper1.eq("fans_id", userId);
        userVo.setConcern(userFansMapper.selectCount(userFansQueryWrapper1));

        // 判断当前用户是否关注该文章作者
        if (fansId != null && !"".equals(fansId)) {
            QueryWrapper<UserFans> userFansQueryWrapper2 = new QueryWrapper<>();
            userFansQueryWrapper2.eq("user_id", userId);
            userFansQueryWrapper2.eq("fans_id", fansId);
            Long selectCount = userFansMapper.selectCount(userFansQueryWrapper2);
            userVo.setIsFans(selectCount);
        } else {
            userVo.setIsFans(0L);
        }
        return new ResultVO(ResultStatus.OK, null, userVo);

    }

    // 游览该文章，文章游览数加一
    @Override
    public ResultVO addArticleVisit(Long articleId) {
        Article article = articleMapper.selectById(articleId);
        article.setVisit(article.getVisit() + 1);
        int updateById = articleMapper.updateById(article);
        if (updateById > 0) {
            return new ResultVO(ResultStatus.OK, null, null);
        } else {
            return new ResultVO(ResultStatus.NO, null, null);
        }
    }

    // 关注作者
    @Override
    public ResultVO likeAuthor(Long userId) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl authenticationTokenPrincipal = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = authenticationTokenPrincipal.getUser();
        Long fansId = user.getId(); // 粉丝id
        QueryWrapper<UserFans> userFansQueryWrapper = new QueryWrapper<>();
        userFansQueryWrapper.eq("fans_id", fansId);
        userFansQueryWrapper.eq("user_id", userId);
        UserFans userFans = userFansMapper.selectOne(userFansQueryWrapper);
        if (userFans != null) {
            int deleteById = userFansMapper.deleteById(userFans);
            if (deleteById > 0) {
                return new ResultVO(ResultStatus.OK, "取消关注作者成功。", null);
            } else {
                return new ResultVO(ResultStatus.NO, "取消关注作者失败。", null);
            }
        } else {
            UserFans userFans1 = new UserFans();
            userFans1.setUserId(userId);
            userFans1.setFansId(fansId);
            userFans1.setCreateTime(new Date());
            int insert = userFansMapper.insert(userFans1);
            if (insert > 0) {
                return new ResultVO(ResultStatus.OK, "关注作者成功。", null);
            } else {
                return new ResultVO(ResultStatus.NO, "关注作者失败", null);
            }
        }
    }

    // 通过文章id查询文章评论信息
    @Override
    public ResultVO getCommentInformationByArticleId(Long articleId, String isLikeUserId) {
        // 通过文章id查询一级评论信息
        QueryWrapper<ArticleComment> commentQueryWrapper = new QueryWrapper<>();
        commentQueryWrapper.eq("article_id", articleId);
        commentQueryWrapper.eq("parent_id", 0);
        List<ArticleComment> articleCommentList = articleCommentMapper.selectList(commentQueryWrapper);
        List<ArticleCommentVo> commentOne = new ArrayList<>();
        for (ArticleComment articleComment : articleCommentList) {
            ArticleCommentVo articleCommentVo = new ArticleCommentVo();
            BeanUtils.copyProperties(articleComment, articleCommentVo);
            // 通过评论者和回复者id得到评论者回复者姓名
            Long userId = articleCommentVo.getUserId();
            Long replyId = articleCommentVo.getReplyId();
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.eq("id", userId);
            User user = userMapper.selectOne(userQueryWrapper);
            articleCommentVo.setUserName(user.getUsername());
            articleCommentVo.setUserNamePhoto(user.getPhoto());
            articleCommentVo.setShowInput(false);
            if (replyId != null) {
                QueryWrapper<User> userQueryWrapper1 = new QueryWrapper<>();
                userQueryWrapper1.eq("id", replyId);
                User user1 = userMapper.selectOne(userQueryWrapper1);
                articleCommentVo.setReplyName(user1.getUsername());
                articleCommentVo.setReplyNamePhoto(user1.getPhoto());
            }

            // 得到该评论点赞数
            QueryWrapper<ArticleCommentLike> articleCommentQueryWrapper = new QueryWrapper<>();
            articleCommentQueryWrapper.eq("article_id", articleId);
            articleCommentQueryWrapper.eq("comment_id", articleCommentVo.getId());
            Long selectCount = commentLikeMapper.selectCount(articleCommentQueryWrapper);
            articleCommentVo.setCommentLikeSum(selectCount);

            // 判断该用户对于这个评论是否点赞
            if (isLikeUserId != null && !"".equals(isLikeUserId)) {
                articleCommentQueryWrapper.eq("user_id", isLikeUserId);
                Long aLong = commentLikeMapper.selectCount(articleCommentQueryWrapper);
                articleCommentVo.setIsLike(aLong);
            } else {
                articleCommentVo.setIsLike(0L);
            }


            commentOne.add(articleCommentVo);
        }

        // 通过一级评论信息找到2，3级评论信息
        for (ArticleCommentVo articleCommentVo : commentOne) {
            Long oneId = articleCommentVo.getId();
            QueryWrapper<ArticleComment> commentQueryWrapper1 = new QueryWrapper<>();
            commentQueryWrapper1.eq("parent_id", oneId);
            List<ArticleComment> articleComments = articleCommentMapper.selectList(commentQueryWrapper1);
            // 通过其评论id和回复人id得到评论人姓名，和回复人姓名
            List<ArticleCommentVo> articleCommentVoList = new ArrayList<>();
            for (ArticleComment articleComment : articleComments) {
                ArticleCommentVo articleCommentVo1 =new ArticleCommentVo();
                BeanUtils.copyProperties(articleComment, articleCommentVo1);
                Long userId = articleCommentVo1.getUserId();
                Long replyId = articleCommentVo1.getReplyId();
                QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
                userQueryWrapper.eq("id", userId);
                User user = userMapper.selectOne(userQueryWrapper);
                articleCommentVo1.setUserNamePhoto(user.getPhoto());
                articleCommentVo1.setUserName(user.getUsername());
                QueryWrapper<User> userQueryWrapper1 = new QueryWrapper<>();
                userQueryWrapper1.eq("id", replyId);
                User user1 = userMapper.selectOne(userQueryWrapper1);
                articleCommentVo1.setReplyName(user1.getUsername());
                articleCommentVo1.setReplyNamePhoto(user1.getPhoto());

                // 得到该评论点赞数
                QueryWrapper<ArticleCommentLike> articleCommentQueryWrapper = new QueryWrapper<>();
                articleCommentQueryWrapper.eq("article_id", articleId);
                articleCommentQueryWrapper.eq("comment_id", articleCommentVo1.getId());
                Long selectCount = commentLikeMapper.selectCount(articleCommentQueryWrapper);
                articleCommentVo1.setCommentLikeSum(selectCount);

                // 判断该用户对于这个评论是否点赞
                if (isLikeUserId != null && !"".equals(isLikeUserId)) {
                    articleCommentQueryWrapper.eq("user_id", isLikeUserId);
                    Long aLong = commentLikeMapper.selectCount(articleCommentQueryWrapper);
                    articleCommentVo1.setIsLike(aLong);
                } else {
                    articleCommentVo1.setIsLike(0L);
                }

                articleCommentVo1.setShowInput(false);
                articleCommentVoList.add(articleCommentVo1);
            }
            articleCommentVo.setDownComment(articleCommentVoList);
        }
        return new ResultVO(ResultStatus.OK, null, commentOne);
    }

    // 发布评论
    @Override
    public ResultVO publishComment(Long userId, Long articleId, String content) {
        ArticleComment articleComment = new ArticleComment();
        articleComment.setArticleId(articleId);
        articleComment.setUserId(userId);
        articleComment.setContent(content);
        articleComment.setCommentTime(new Date());
        int insert = articleCommentMapper.insert(articleComment);
        if (insert > 0) {
            return new ResultVO(ResultStatus.OK, null, null);
        } else {
            return new ResultVO(ResultStatus.NO, null, null);
        }
    }

    // 评论点赞功能实现
    @Override
    public ResultVO commentLike(Long userId, Long articleId, Long commentId) {
        QueryWrapper<ArticleCommentLike> commentLikeQueryWrapper = new QueryWrapper<>();
        commentLikeQueryWrapper.eq("user_id", userId);
        commentLikeQueryWrapper.eq("article_id", articleId);
        commentLikeQueryWrapper.eq("comment_id", commentId);
        ArticleCommentLike selectOne = commentLikeMapper.selectOne(commentLikeQueryWrapper);
        if (selectOne != null) {
            int deleteById = commentLikeMapper.deleteById(selectOne);
            if (deleteById > 0) {
                return new ResultVO(ResultStatus.OK, "取消点赞成功", null);
            } else {
                return new ResultVO(ResultStatus.NO, "取消点赞失败", null);
            }
        } else {
            ArticleCommentLike articleCommentLike = new ArticleCommentLike();
            articleCommentLike.setUserId(userId);
            articleCommentLike.setCommentId(commentId);
            articleCommentLike.setArticleId(articleId);
            articleCommentLike.setCreateTime(new Date());
            int insert = commentLikeMapper.insert(articleCommentLike);
            if (insert > 0) {
                return new ResultVO(ResultStatus.OK, "点赞成功", null);
            } else {
                return new ResultVO(ResultStatus.NO, "点赞失败", null);
            }
        }
    }

    // 评论回复功能实现
    @Override
    public ResultVO  replyComment(Long commentId, Long replyId, String replyContent) {

        ArticleComment selectById = articleCommentMapper.selectById(commentId);
        Long articleId = selectById.getArticleId();
        ArticleComment articleComment = new ArticleComment();
        articleComment.setUserId(selectById.getUserId());
        articleComment.setReplyId(replyId);

        articleComment.setArticleId(articleId);
        articleComment.setContent(replyContent);
        articleComment.setCommentTime(new Date());
        articleComment.setParentId(commentId);
        int insert = articleCommentMapper.insert(articleComment);
        if (insert > 0) {
            return new ResultVO(ResultStatus.OK, null, null);
        } else {
            return new ResultVO(ResultStatus.NO, null, null);
        }
    }
}
