package com.monkey.monkeyblog.service.Impl.check;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.result.ResultStatus;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyblog.mapper.CommentLikeMapper;
import com.monkey.monkeyblog.mapper.CommentMapper;
import com.monkey.monkeyblog.mapper.article.ArticleLabelMapper;
import com.monkey.monkeyblog.mapper.article.ArticleMapper;
import com.monkey.monkeyblog.mapper.LabelMapper;
import com.monkey.monkeyblog.mapper.user.UserCollectMapper;
import com.monkey.monkeyblog.mapper.user.UserFansMapper;
import com.monkey.monkeyblog.mapper.user.UserLikeMapper;
import com.monkey.monkeyblog.pojo.CommentLike;
import com.monkey.monkeyblog.pojo.Comment;
import com.monkey.monkeyblog.pojo.Label;
import com.monkey.monkeyblog.pojo.Vo.CommentVo;
import com.monkey.monkeyblog.pojo.Vo.UserVo;
import com.monkey.monkeyblog.pojo.article.Article;
import com.monkey.monkeyblog.pojo.article.ArticleLabel;
import com.monkey.monkeyblog.pojo.user.UserCollect;
import com.monkey.monkeyblog.pojo.user.UserFans;
import com.monkey.monkeyblog.pojo.user.UserLike;
import com.monkey.monkeyblog.service.check.CheckArticleService;
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
import java.util.Map;

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
    private UserCollectMapper userCollectMapper;

    @Autowired
    private UserFansMapper userFansMapper;

    @Autowired
    private UserLikeMapper userLikeMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CommentLikeMapper commentLikeMapper;

    // 通过文章id查询文章标签信息
    @Override
    public ResultVO getArticleLabelInfoByArticleId(Map<String, String> data) {
        long articleId = Long.parseLong(data.get("articleId"));
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
    public ResultVO getAuthorInfoByArticleId(Map<String, String> data) {
        long articleId = Long.parseLong(data.get("articleId"));

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
            QueryWrapper<UserLike> userLikeQueryWrapper = new QueryWrapper<>();
            userLikeQueryWrapper.eq("article_id", article1Id);
            userLike += userLikeMapper.selectCount(userLikeQueryWrapper);
            QueryWrapper<UserCollect> userCollectQueryWrapper = new QueryWrapper<>();
            userCollectQueryWrapper.eq("article_id", article1Id);
            userCollect += userCollectMapper.selectCount(userCollectQueryWrapper);
        }
        userVo.setVisit(userVisit);
        userVo.setLikeSum(userLike);
        userVo.setUserCollect(userCollect);

        // 得到用户粉丝数
        QueryWrapper<UserFans> userFansQueryWrapper = new QueryWrapper<>();
        userFansQueryWrapper.eq("user_id", userId);
        Long fansSum = userFansMapper.selectCount(userFansQueryWrapper);
        userVo.setFans(fansSum);


        // 得到用户关注数
        QueryWrapper<UserFans> userFansQueryWrapper1 = new QueryWrapper<>();
        userFansQueryWrapper1.eq("fans_id", userId);
        userVo.setConcern(userFansMapper.selectCount(userFansQueryWrapper1));

        String fansId = data.get("userId");
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
    public ResultVO addArticleVisit(Map<String, String> data) {
        long articleId = Long.parseLong(data.get("articleId"));
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
    public ResultVO likeAuthor(Map<String, String> data) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl authenticationTokenPrincipal = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = authenticationTokenPrincipal.getUser();
        Long fansId = user.getId(); // 粉丝id
        long userId = Long.parseLong(data.get("userId")); // 被关注者id
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
    public ResultVO getCommentInformationByArticleId(Map<String, String> data) {
        long articleId = Long.parseLong(data.get("articleId"));
        String isLikeUserId = data.get("userId");
        // 通过文章id查询一级评论信息
        QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
        commentQueryWrapper.eq("article_id", articleId);
        commentQueryWrapper.eq("parent_id", 0);
        List<Comment> commentList = commentMapper.selectList(commentQueryWrapper);
        List<CommentVo> commentOne = new ArrayList<>();
        for (Comment comment : commentList) {
            CommentVo commentVo = new CommentVo();
            BeanUtils.copyProperties(comment,  commentVo);
            // 通过评论者和回复者id得到评论者回复者姓名
            Long userId = commentVo.getUserId();
            Long replyId = commentVo.getReplyId();
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.eq("id", userId);
            User user = userMapper.selectOne(userQueryWrapper);
            commentVo.setUserName(user.getUsername());
            commentVo.setUserNamePhoto(user.getPhoto());
            commentVo.setShowInput(false);
            if (replyId != null) {
                QueryWrapper<User> userQueryWrapper1 = new QueryWrapper<>();
                userQueryWrapper1.eq("id", replyId);
                User user1 = userMapper.selectOne(userQueryWrapper1);
                commentVo.setReplyName(user1.getUsername());
                commentVo.setReplyNamePhoto(user1.getPhoto());
            }

            // 得到该评论点赞数
            QueryWrapper<CommentLike> articleCommentQueryWrapper = new QueryWrapper<>();
            articleCommentQueryWrapper.eq("article_id", articleId);
            articleCommentQueryWrapper.eq("comment_id", commentVo.getId());
            Long selectCount = commentLikeMapper.selectCount(articleCommentQueryWrapper);
            commentVo.setCommentLikeSum(selectCount);

            // 判断该用户对于这个评论是否点赞
            if (isLikeUserId != null && !"".equals(isLikeUserId)) {
                articleCommentQueryWrapper.eq("user_id", isLikeUserId);
                Long aLong = commentLikeMapper.selectCount(articleCommentQueryWrapper);
                commentVo.setIsLike(aLong);
            } else {
                commentVo.setIsLike(0L);
            }


            commentOne.add(commentVo);
        }

        // 通过一级评论信息找到2，3级评论信息
        for (CommentVo commentVo : commentOne) {
            Long oneId = commentVo.getId();
            QueryWrapper<Comment> commentQueryWrapper1 = new QueryWrapper<>();
            commentQueryWrapper1.eq("parent_id", oneId);
            List<Comment> comments = commentMapper.selectList(commentQueryWrapper1);
            // 通过其评论id和回复人id得到评论人姓名，和回复人姓名
            List<CommentVo> commentVoList = new ArrayList<>();
            for (Comment comment : comments) {
                CommentVo commentVo1 =new CommentVo();
                BeanUtils.copyProperties(comment, commentVo1);
                Long userId = commentVo1.getUserId();
                Long replyId = commentVo1.getReplyId();
                QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
                userQueryWrapper.eq("id", userId);
                User user = userMapper.selectOne(userQueryWrapper);
                commentVo1.setUserNamePhoto(user.getPhoto());
                commentVo1.setUserName(user.getUsername());
                QueryWrapper<User> userQueryWrapper1 = new QueryWrapper<>();
                userQueryWrapper1.eq("id", replyId);
                User user1 = userMapper.selectOne(userQueryWrapper1);
                commentVo1.setReplyName(user1.getUsername());
                commentVo1.setReplyNamePhoto(user1.getPhoto());

                // 得到该评论点赞数
                QueryWrapper<CommentLike> articleCommentQueryWrapper = new QueryWrapper<>();
                articleCommentQueryWrapper.eq("article_id", articleId);
                articleCommentQueryWrapper.eq("comment_id", commentVo1.getId());
                Long selectCount = commentLikeMapper.selectCount(articleCommentQueryWrapper);
                commentVo1.setCommentLikeSum(selectCount);

                // 判断该用户对于这个评论是否点赞
                if (isLikeUserId != null && !"".equals(isLikeUserId)) {
                    articleCommentQueryWrapper.eq("user_id", isLikeUserId);
                    Long aLong = commentLikeMapper.selectCount(articleCommentQueryWrapper);
                    commentVo1.setIsLike(aLong);
                } else {
                    commentVo1.setIsLike(0L);
                }

                commentVo1.setShowInput(false);
                commentVoList.add(commentVo1);
            }
            commentVo.setDownComment(commentVoList);
        }
        return new ResultVO(ResultStatus.OK, null, commentOne);
    }

    // 发布评论
    @Override
    public ResultVO publishComment(Map<String, String> data) {
        long userId = Long.parseLong(data.get("userId"));
        long articleId = Long.parseLong(data.get("articleId"));
        String content = data.get("content");
        Comment comment = new Comment();
        comment.setArticleId(articleId);
        comment.setUserId(userId);
        comment.setContent(content);
        comment.setCommentTime(new Date());
        int insert = commentMapper.insert(comment);
        if (insert > 0) {
            return new ResultVO(ResultStatus.OK, null, null);
        } else {
            return new ResultVO(ResultStatus.NO, null, null);
        }
    }

    // 评论点赞功能实现
    @Override
    public ResultVO commentLike(Map<String, String> data) {
        long userId = Long.parseLong(data.get("userId"));
        long articleId = Long.parseLong(data.get("articleId"));
        long commentId = Long.parseLong(data.get("commentId"));
        QueryWrapper<CommentLike> commentLikeQueryWrapper = new QueryWrapper<>();
        commentLikeQueryWrapper.eq("user_id", userId);
        commentLikeQueryWrapper.eq("article_id", articleId);
        commentLikeQueryWrapper.eq("comment_id", commentId);
        CommentLike selectOne = commentLikeMapper.selectOne(commentLikeQueryWrapper);
        if (selectOne != null) {
            int deleteById = commentLikeMapper.deleteById(selectOne);
            if (deleteById > 0) {
                return new ResultVO(ResultStatus.OK, "取消点赞成功", null);
            } else {
                return new ResultVO(ResultStatus.NO, "取消点赞失败", null);
            }
        } else {
            CommentLike commentLike = new CommentLike();
            commentLike.setUserId(userId);
            commentLike.setCommentId(commentId);
            commentLike.setArticleId(articleId);
            commentLike.setCreateTime(new Date());
            int insert = commentLikeMapper.insert(commentLike);
            if (insert > 0) {
                return new ResultVO(ResultStatus.OK, "点赞成功", null);
            } else {
                return new ResultVO(ResultStatus.NO, "点赞失败", null);
            }
        }
    }

    // 评论回复功能实现
    @Override
    public ResultVO replyComment(Map<String, String> data) {
        long commentId = Long.parseLong(data.get("commentId"));
        long replyId = Long.parseLong(data.get("replyId"));
        String replyContent = data.get("replyContent");
        Comment selectById = commentMapper.selectById(commentId);

        Comment comment = new Comment();
        comment.setUserId(selectById.getUserId());
        comment.setReplyId(replyId);
        comment.setArticleId(selectById.getArticleId());
        comment.setContent(replyContent);
        comment.setCommentTime(new Date());
        comment.setParentId(commentId);
        int insert = commentMapper.insert(comment);
        if (insert > 0) {
            return new ResultVO(ResultStatus.OK, null, null);
        } else {
            return new ResultVO(ResultStatus.NO, null, null);
        }
    }
}
