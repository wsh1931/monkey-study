package com.monkey.monkeyarticle.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeyUtils.constants.*;
import com.monkey.monkeyUtils.mapper.*;
import com.monkey.monkeyUtils.pojo.*;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyarticle.feign.ArticleToSearchFeignService;
import com.monkey.monkeyarticle.mapper.*;
import com.monkey.monkeyarticle.pojo.*;
import com.monkey.monkeyarticle.service.UserHomeArticleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/11/29 16:34
 * @version: 1.0
 * @description:
 */
@Service
public class UserHomeArticleServiceImpl implements UserHomeArticleService {
    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private ArticleCommentMapper articleCommentMapper;
    @Resource
    private ArticleCommentLikeMapper articleCommentLikeMapper;
    @Resource
    private ArticleLikeMapper articleLikeMapper;
    @Resource
    private MessageCommentReplyMapper messageCommentReplyMapper;
    @Resource
    private MessageLikeMapper messageLikeMapper;
    @Resource
    private MessageCollectMapper messageCollectMapper;
    @Resource
    private ReportContentMapper reportContentMapper;
    @Resource private ReportCommentMapper reportCommentMapper;
    @Resource
    private ArticleLabelMapper articleLabelMapper;
    @Resource
    private ArticleToSearchFeignService articleToSearchFeignService;

    @Resource
    private CollectContentConnectMapper collectContentConnectMapper;
    @Resource
    private HistoryCommentMapper historyCommentMapper;
    @Resource
    private HistoryContentMapper historyContentMapper;
    @Resource
    private HistoryLikeMapper historyLikeMapper;
    /**
     * 通过用户id查询文章集合
     *
     * @param userId 用户id
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/29 16:38
     */
    @Override
    public R queryArticleByUserId(Long userId, Long currentPage, Integer pageSize) {
        LambdaQueryWrapper<Article> articleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        articleLambdaQueryWrapper.eq(Article::getStatus, CommonEnum.SUCCESS.getCode());
        articleLambdaQueryWrapper.eq(Article::getUserId, userId);
        articleLambdaQueryWrapper.orderByDesc(Article::getCreateTime);
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = articleMapper.selectPage(page, articleLambdaQueryWrapper);
        return R.ok(selectPage);
    }

    /**
     * 删除文章
     *
     * @param articleId 文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/30 21:15
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R deleteArticle(String articleId) {
        // 删除文章
        articleMapper.deleteById(articleId);
        // 删除文章标签关系表
        LambdaQueryWrapper<ArticleLabel> articleLabelLambdaQueryWrapper = new LambdaQueryWrapper<>();
        articleLabelLambdaQueryWrapper.eq(ArticleLabel::getArticleId, articleId);
        articleLabelMapper.delete(articleLabelLambdaQueryWrapper);

        // 查询文章评论id集合
        LambdaQueryWrapper<ArticleComment> articleCommentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        articleCommentLambdaQueryWrapper.eq(ArticleComment::getArticleId, articleId);
        articleCommentLambdaQueryWrapper.select(ArticleComment::getId);
        List<Object> articleCommentIdList = articleCommentMapper.selectObjs(articleCommentLambdaQueryWrapper);
        // 删除文章评论表
        LambdaQueryWrapper<ArticleComment> deleteArticleCommentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        deleteArticleCommentLambdaQueryWrapper.eq(ArticleComment::getArticleId, articleId);
        articleCommentMapper.delete(deleteArticleCommentLambdaQueryWrapper);

        // 删除文章评论点赞表
        LambdaQueryWrapper<ArticleCommentLike> articleCommentLikeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        articleCommentLikeLambdaQueryWrapper.eq(ArticleCommentLike::getArticleId, articleId);
        articleCommentLikeMapper.delete(articleCommentLikeLambdaQueryWrapper);

        // 删除文章点赞
        LambdaQueryWrapper<ArticleLike> articleLikeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        articleLikeLambdaQueryWrapper.eq(ArticleLike::getArticleId, articleId);
        articleLikeMapper.delete(articleLikeLambdaQueryWrapper);

        // 删除文章游览历史表
        LambdaQueryWrapper<HistoryContent> historyContentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        historyContentLambdaQueryWrapper.eq(HistoryContent::getAssociateId, articleId);
        historyContentLambdaQueryWrapper.eq(HistoryContent::getType, HistoryViewEnum.ARTICLE.getCode());
        historyContentMapper.delete(historyContentLambdaQueryWrapper);

        // 删除文章历史点赞表
        LambdaQueryWrapper<HistoryLike> historyLikeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        historyLikeLambdaQueryWrapper.eq(HistoryLike::getType, HistoryViewEnum.ARTICLE.getCode());
        historyLikeLambdaQueryWrapper.eq(HistoryLike::getAssociateId, articleId);
        historyLikeMapper.delete(historyLikeLambdaQueryWrapper);

        if (articleCommentIdList != null && articleCommentIdList.size() > 0) {
            // 删除消息回复评论表
            LambdaQueryWrapper<MessageCommentReply> messageCommentReplyLambdaQueryWrapper = new LambdaQueryWrapper<>();
            messageCommentReplyLambdaQueryWrapper.eq(MessageCommentReply::getAssociationId, articleId);
            messageCommentReplyLambdaQueryWrapper.in(MessageCommentReply::getCommentId, articleCommentIdList);
            messageCommentReplyLambdaQueryWrapper.eq(MessageCommentReply::getType, ReportCommentEnum.ARTICLE_REPORT.getCode());
            messageCommentReplyMapper.delete(messageCommentReplyLambdaQueryWrapper);

            // 删除举报评论表
            LambdaQueryWrapper<ReportComment> reportCommentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            reportCommentLambdaQueryWrapper.eq(ReportComment::getType, ReportCommentEnum.ARTICLE_REPORT.getCode());
            reportCommentLambdaQueryWrapper.in(ReportComment::getAssociateId, articleCommentIdList);
            reportCommentMapper.delete(reportCommentLambdaQueryWrapper);

            // 删除消息评论点赞表
            LambdaQueryWrapper<MessageLike> messageCommentLikeLambdaQueryWrapper = new LambdaQueryWrapper<>();
            messageCommentLikeLambdaQueryWrapper.eq(MessageLike::getAssociationId, articleId);
            messageCommentLikeLambdaQueryWrapper.eq(MessageLike::getType, MessageEnum.ARTICLE_MESSAGE.getCode());
            messageCommentLikeLambdaQueryWrapper.in(MessageLike::getCommentId, articleCommentIdList);
            messageCommentLikeLambdaQueryWrapper.eq(MessageLike::getIsComment, CommonEnum.MESSAGE_LIKE_IS_COMMENT.getCode());
            messageLikeMapper.delete(messageCommentLikeLambdaQueryWrapper);

            // 删除文章历史评论表
            LambdaQueryWrapper<HistoryComment> historyCommentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            historyCommentLambdaQueryWrapper.eq(HistoryComment::getAssociateId, articleId);
            historyCommentLambdaQueryWrapper.eq(HistoryComment::getType, HistoryViewEnum.ARTICLE.getCode());
            historyCommentMapper.delete(historyCommentLambdaQueryWrapper);
        }

        // 删除消息点赞表
        LambdaQueryWrapper<MessageLike> messageLikeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        messageLikeLambdaQueryWrapper.eq(MessageLike::getAssociationId, articleId);
        messageLikeLambdaQueryWrapper.eq(MessageLike::getType, MessageEnum.ARTICLE_MESSAGE.getCode());
        messageLikeLambdaQueryWrapper.eq(MessageLike::getIsComment, CommonEnum.MESSAGE_LIKE_IS_CONTENT.getCode());
        messageLikeMapper.delete(messageLikeLambdaQueryWrapper);

        // 删除消息收藏表
        LambdaQueryWrapper<MessageCollect> messageCollectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        messageCollectLambdaQueryWrapper.eq(MessageCollect::getAssociationId, articleId);
        messageCollectLambdaQueryWrapper.eq(MessageCollect::getType, MessageEnum.ARTICLE_MESSAGE.getCode());
        messageCollectMapper.delete(messageCollectLambdaQueryWrapper);

        // 删除举报内容表
        LambdaQueryWrapper<ReportContent> reportContentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        reportContentLambdaQueryWrapper.eq(ReportContent::getAssociateId, articleId);
        reportContentLambdaQueryWrapper.eq(ReportContent::getType, ReportContentEnum.ARTICLE_REPORT.getCode());
        reportContentMapper.delete(reportContentLambdaQueryWrapper);

        // 删除收藏目录关系表
        LambdaQueryWrapper<CollectContentConnect> collectContentConnectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        collectContentConnectLambdaQueryWrapper.eq(CollectContentConnect::getType, CollectEnum.COLLECT_ARTICLE.getCode())
                .eq(CollectContentConnect::getAssociateId, articleId);
        collectContentConnectMapper.delete(collectContentConnectLambdaQueryWrapper);


        // 删除elasticsearch文章信息
        articleToSearchFeignService.deleteArticle(articleId);
        return R.ok();
    }
}
