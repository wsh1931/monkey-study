package com.monkey.monkeyarticle.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.exception.ExceptionEnum;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.pojo.UserFansVo;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.result.ResultStatus;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyUtils.mapper.LabelMapper;
import com.monkey.monkeyUtils.pojo.Label;
import com.monkey.monkeyarticle.feign.ArticleToUserFeignService;
import com.monkey.monkeyarticle.mapper.*;
import com.monkey.monkeyarticle.pojo.*;
import com.monkey.monkeyarticle.pojo.vo.ArticleCommentVo;
import com.monkey.monkeyarticle.service.CheckArticleService;
import com.monkey.spring_security.mapper.UserMapper;
import com.monkey.spring_security.pojo.User;
import com.monkey.spring_security.user.UserDetailsImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

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
    private ArticleCommentMapper articleCommentMapper;

    @Autowired
    private ArticleCommentLikeMapper commentLikeMapper;

    @Autowired
    private ArticleToUserFeignService articleToUserFeignService;



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
            if (label.getLevel() == 2) {
                res.add(label.getLabelName());
            }
        }
        return new ResultVO(ResultStatus.OK, null, res);
    }

    // 通过文章id得到作者信息
    @Override
    public ResultVO getAuthorInfoByArticleId(Long articleId, String fansId) {
        Article article = articleMapper.selectById(articleId);
        Long userId = article.getUserId();
        Map<String, String> map = new HashMap<>();
        map.put("userId", String.valueOf(userId));
        map.put("nowUserId", fansId);
        ResultVO userInformationByUserId = articleToUserFeignService.getUserInformationByUserId(map);
        return userInformationByUserId;
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
        R userFansByUserAndAuthorConnect = articleToUserFeignService.getUserFansByUserAndAuthorConnect(userId, fansId);
        if (userFansByUserAndAuthorConnect.getCode() != R.SUCCESS) {
            throw new MonkeyBlogException(userFansByUserAndAuthorConnect.getCode(), userFansByUserAndAuthorConnect.getMsg());
        }
        UserFansVo userFansVo = (UserFansVo)userFansByUserAndAuthorConnect.getData(new TypeReference<UserFansVo>() {});
        if (userFansVo != null) {
            R deleteUserFansById = articleToUserFeignService.deleteUserFansById(userFansVo.getId());
            if (deleteUserFansById.getCode() != R.SUCCESS) {
                throw new MonkeyBlogException(deleteUserFansById.getCode(), deleteUserFansById.getMsg());
            }

            Integer deleteById = (Integer) deleteUserFansById.getData(new TypeReference<Integer>(){});
            if (deleteById > 0) {
                return new ResultVO(ResultStatus.OK, "取消关注作者成功。", null);
            } else {
                return new ResultVO(ResultStatus.NO, "取消关注作者失败。", null);
            }
        } else {
            UserFansVo userFanVo = new UserFansVo();
            userFanVo.setUserId(userId);
            userFanVo.setFansId(fansId);
            userFanVo.setCreateTime(new Date());
            R addUserFans = articleToUserFeignService.addUserFans(userFanVo);
            if (addUserFans.getCode() != R.SUCCESS) {
                throw new MonkeyBlogException(ExceptionEnum.ADD_USERFANS_FAIL.getCode(),  ExceptionEnum.ADD_USERFANS_FAIL.getMsg());
            }

            Integer insert = (Integer) addUserFans.getData(new TypeReference<Integer>(){});

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

            articleCommentVo.setCommentLikeSum(articleComment.getLikeSum());

            // 判断该用户对于这个评论是否点赞
            if (isLikeUserId != null && !"".equals(isLikeUserId)) {
                QueryWrapper<ArticleCommentLike> articleCommentQueryWrapper = new QueryWrapper<>();
                articleCommentQueryWrapper.eq("article_id", articleId);
                articleCommentQueryWrapper.eq("comment_id", articleCommentVo.getId());
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
                articleCommentVo1.setCommentLikeSum(articleComment.getLikeSum());

                // 判断该用户对于这个评论是否点赞
                if (isLikeUserId != null && !"".equals(isLikeUserId)) {
                    QueryWrapper<ArticleCommentLike> articleCommentQueryWrapper = new QueryWrapper<>();
                    articleCommentQueryWrapper.eq("article_id", articleId);
                    articleCommentQueryWrapper.eq("comment_id", articleCommentVo1.getId());
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
            // 文章评论数加 + 1
            Article article = articleMapper.selectById(articleId);
            article.setCommentCount(article.getCommentCount() + 1);
            articleMapper.updateById(article);
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
                // 文章评论点赞数 - 1
                ArticleComment articleComment = articleCommentMapper.selectById(commentId);
                articleComment.setLikeSum(articleComment.getLikeSum() - 1);
                articleCommentMapper.updateById(articleComment);
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
                // 文章评论点赞数 + 1
                ArticleComment articleComment = articleCommentMapper.selectById(commentId);
                articleComment.setLikeSum(articleComment.getLikeSum() + 1);
                articleCommentMapper.updateById(articleComment);
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
            // 文章评论数 + 1
            Article article = articleMapper.selectById(articleId);
            article.setCommentCount(article.getCommentCount() + 1);
            articleMapper.updateById(article);
            return new ResultVO(ResultStatus.OK, null, null);
        } else {
            return new ResultVO(ResultStatus.NO, null, null);
        }
    }
}
