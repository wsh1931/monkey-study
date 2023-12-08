package com.monkey.monkeyblog.service.Impl.center;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.constants.HistoryViewEnum;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.mapper.HistoryCommentMapper;
import com.monkey.monkeyUtils.mapper.HistoryContentMapper;
import com.monkey.monkeyUtils.mapper.HistoryLikeMapper;
import com.monkey.monkeyUtils.mapper.UserMapper;
import com.monkey.monkeyUtils.pojo.HistoryComment;
import com.monkey.monkeyUtils.pojo.HistoryContent;
import com.monkey.monkeyUtils.pojo.User;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import com.monkey.monkeyblog.constant.UserEnum;
import com.monkey.monkeyblog.feign.*;
import com.monkey.monkeyblog.pojo.Vo.HistoryCommentVo;
import com.monkey.monkeyblog.pojo.Vo.HistoryContentVo;
import com.monkey.monkeyblog.service.center.UserCenterHistoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/12/7 16:25
 * @version: 1.0
 * @description:
 */
@Service
public class UserCenterHistoryServiceImpl implements UserCenterHistoryService {
    @Resource
    private HistoryContentMapper historyContentMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserToArticleFeignService userToArticleFeignService;
    @Resource
    private UserToQuestionFeignService userToQuestionFeignService;
    @Resource
    private UserToCourseFeignService userToCourseFeignService;
    @Resource
    private UserToCommunityFeignService userToCommunityFeignService;
    @Resource
    private UserToResourceFeignService userToResourceFeignService;
    @Resource
    private HistoryCommentMapper historyCommentMapper;
    @Resource
    private HistoryLikeMapper historyLikeMapper;

    /**
     * 查询历史内容集合
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/7 16:34
     */
    @Override
    public R queryHistoryContent(Long currentPage, Integer pageSize) {
        String userId = JwtUtil.getUserId();
        QueryWrapper<HistoryContent> historyContentQueryWrapper = new QueryWrapper<>();
        historyContentQueryWrapper.groupBy("associate_id", "type", "author_id");
        historyContentQueryWrapper.select("max(create_time) as createTime",
                "type",
                "associate_id",
                "author_id");
        historyContentQueryWrapper.eq("user_id", userId);
        historyContentQueryWrapper.orderByDesc("createTime");
        Page page = new Page<>(currentPage, pageSize);
        Page historyContentPage = historyContentMapper.selectPage(page, historyContentQueryWrapper);

        List<HistoryContent> historyContentList = historyContentPage.getRecords();
        // 最终返回集合
        List<HistoryContentVo> historyContentVoList = new ArrayList<>();
        historyContentList.forEach(historyContent -> {
            HistoryContentVo historyContentVo = new HistoryContentVo();
            // 得到作者信息
            Long authorId = historyContent.getAuthorId();
            User user = userMapper.selectById(authorId);
            historyContentVo.setAuthorName(user.getUsername());
            historyContentVo.setAuthorHeadImg(user.getPhoto());
            historyContentVo.setAuthorId(authorId);

            // 得到文章内容信息
            Integer type = historyContent.getType();
            HistoryViewEnum historyViewEnum = HistoryViewEnum.getHistoryViewEnum(type);
            historyContentVo.setTypeName(historyViewEnum.getMsg());
            historyContentVo.setType(historyContent.getType());
            Long associateId = historyContent.getAssociateId();
            historyContentVo.setAssociateId(associateId);
            R result = null;
            switch (historyViewEnum) {
                case ARTICLE:
                    result = userToArticleFeignService.queryArticleById(associateId);
                    break;
                case QUESTION:
                    result = userToQuestionFeignService.queryQuestionById(associateId);
                    break;
                case RESOURCE:
                    result = userToResourceFeignService.queryResourceById(associateId);
                    break;
                case COMMUNITY_ARTICLE:
                    result = userToCommunityFeignService.queryCommunityArticleById(associateId);
                    break;
                case COURSE:
                    result = userToCourseFeignService.queryCourseById(associateId);
                    break;
            }

            if (result != null) {
                int code = result.getCode();
                if (code != R.SUCCESS) {
                    throw new MonkeyBlogException(R.Error, result.getMsg());
                }

                JSONObject jsonObject = (JSONObject) result.getData(new TypeReference<JSONObject>(){});
                String title = jsonObject.getString("title");
                String picture = jsonObject.getString("picture");
                historyContentVo.setTitle(title);
                historyContentVo.setPicture(picture);
            }

            historyContentVo.setCreateTime(historyContent.getCreateTime());

            historyContentVoList.add(historyContentVo);
        });

        historyContentPage.setRecords(historyContentVoList);
        return R.ok(historyContentPage);
    }

    /**
     * 清除用户历史内容
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/8 10:59
     */
    @Override
    public R clearHistoryContent() {
        String userId = JwtUtil.getUserId();
        LambdaQueryWrapper<HistoryContent> historyContentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        historyContentLambdaQueryWrapper.eq(HistoryContent::getUserId, userId);
        historyContentMapper.delete(historyContentLambdaQueryWrapper);
        return R.ok();
    }

    /**
     * 查询历史评论集合
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/8 14:42
     */
    @Override
    public R queryHistoryComment(Long currentPage, Integer pageSize) {
        String userId = JwtUtil.getUserId();
        QueryWrapper<HistoryComment> historyCommentQueryWrapper = new QueryWrapper<>();
        historyCommentQueryWrapper.groupBy("associate_id", "type", "author_id", "comment_id", "is_reply");
        historyCommentQueryWrapper.select(
                "max(create_time) as createTime",
                "type",
                "associate_id",
                "author_id",
                "comment_id",
                "is_reply");
        historyCommentQueryWrapper.eq("user_id", userId);
        historyCommentQueryWrapper.orderByDesc("createTime");
        Page page = new Page<>(currentPage, pageSize);
        Page historyCommentPage  = historyCommentMapper.selectPage(page, historyCommentQueryWrapper);

        List<HistoryComment> historyCommentList = historyCommentPage.getRecords();
        // 最终返回集合
        List<HistoryCommentVo> historyCommentVoList = new ArrayList<>();
        historyCommentList.forEach(historyComment -> {
            HistoryCommentVo historyCommentVo = new HistoryCommentVo();
            // 得到作者信息
            Long authorId = historyComment.getAuthorId();
            User user = userMapper.selectById(authorId);
            historyCommentVo.setAuthorName(user.getUsername());
            historyCommentVo.setAuthorHeadImg(user.getPhoto());
            historyCommentVo.setAuthorId(authorId);

            // 得到文章内容信息
            Integer type = historyComment.getType();
            HistoryViewEnum historyViewEnum = HistoryViewEnum.getHistoryViewEnum(type);
            historyCommentVo.setTypeName(historyViewEnum.getMsg());
            historyCommentVo.setType(historyComment.getType());
            Long associateId = historyComment.getAssociateId();
            Long commentId = historyComment.getCommentId();
            Integer isReply = historyComment.getIsReply();
            historyCommentVo.setAssociateId(associateId);
            historyCommentVo.setCommentId(commentId);
            historyCommentVo.setIsReply(isReply);
            R result = null;
            switch (historyViewEnum) {
                case ARTICLE:
                    result = userToArticleFeignService.queryArticleAndCommentById(associateId, commentId);
                    break;
                case QUESTION:
                    if (isReply.equals(CommonEnum.QUESTION_COMMENT.getCode())) {
                        result = userToQuestionFeignService.queryQuestionAndCommentById(associateId, commentId);
                    } else {
                        result = userToQuestionFeignService.queryQuestionAndReplyById(associateId, commentId);
                    }
                    break;
                case RESOURCE:
                    result = userToResourceFeignService.queryResourceAndCommentInfoById(associateId, commentId);
                    break;
                case COMMUNITY_ARTICLE:
                    result = userToCommunityFeignService.queryCommunityArticleAndCommentById(associateId, commentId);
                    break;
                case COURSE:
                    result = userToCourseFeignService.queryCourseAndCommentById(associateId, commentId);
                    break;
            }

            if (result != null) {
                int code = result.getCode();
                if (code != R.SUCCESS) {
                    throw new MonkeyBlogException(R.Error, result.getMsg());
                }

                JSONObject jsonObject = (JSONObject) result.getData(new TypeReference<JSONObject>(){});
                String title = jsonObject.getString("title");
                String picture = jsonObject.getString("picture");
                String contentTitle = jsonObject.getString("contentTitle");
                historyCommentVo.setCommentName(title);
                historyCommentVo.setPicture(picture);
                historyCommentVo.setTitle(contentTitle);
            }

            historyCommentVo.setCreateTime(historyComment.getCreateTime());

            historyCommentVoList.add(historyCommentVo);
        });

        historyCommentPage.setRecords(historyCommentVoList);
        return R.ok(historyCommentPage);
    }

    /**
     * 清除用户历史评论
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/8 16:03
     */
    @Override
    public R clearHistoryComment() {
        String userId = JwtUtil.getUserId();
        LambdaQueryWrapper<HistoryComment> historyCommentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        historyCommentLambdaQueryWrapper.eq(HistoryComment::getUserId, userId);
        historyCommentMapper.delete(historyCommentLambdaQueryWrapper);
        return R.ok();
    }
}
