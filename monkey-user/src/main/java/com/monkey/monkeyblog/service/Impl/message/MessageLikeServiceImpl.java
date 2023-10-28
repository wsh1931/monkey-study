package com.monkey.monkeyblog.service.Impl.message;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.constants.MessageEnum;
import com.monkey.monkeyUtils.exception.ExceptionEnum;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.mapper.MessageLikeMapper;
import com.monkey.monkeyUtils.pojo.MessageCommentReply;
import com.monkey.monkeyUtils.pojo.MessageLike;
import com.monkey.monkeyUtils.pojo.vo.MessageVo;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyblog.feign.*;
import com.monkey.monkeyblog.rabbitmq.EventConstant;
import com.monkey.monkeyblog.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeyblog.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeyblog.service.message.MessageLikeService;
import com.monkey.spring_security.mapper.UserMapper;
import com.monkey.spring_security.pojo.User;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/10/28 14:14
 * @version: 1.0
 * @description:
 */
@Service
public class MessageLikeServiceImpl implements MessageLikeService {
    @Resource
    private MessageLikeMapper messageLikeMapper;
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
    private RabbitTemplate rabbitTemplate;
    @Resource
    private UserMapper userMapper;
    @Override
    public R queryLikeMessage(long userId, Long currentPage, Integer pageSize) {
        QueryWrapper<MessageLike> messageLikeQueryWrapper = new QueryWrapper<>();
        messageLikeQueryWrapper.eq("recipient_id", userId);
        messageLikeQueryWrapper.orderByDesc("create_time");
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = messageLikeMapper.selectPage(page, messageLikeQueryWrapper);
        List<MessageLike> messageLikeList = selectPage.getRecords();
        // 最终返回集合
        List<MessageVo> messageVoList = new ArrayList<>();
        // 查询到的未读消息id集合
        List<Long> messageIdList = new ArrayList<>(messageLikeList.size());
        for (MessageLike messageLike : messageLikeList) {
            if (messageLike.getIsRead().equals(CommonEnum.MESSAGE_NOT_READ.getCode())) {
                messageIdList.add(messageLike.getId());
            }
            MessageVo messageVo = new MessageVo();
            BeanUtils.copyProperties(messageLike, messageVo);
            Integer isComment = messageVo.getIsComment();
            Long associationId = messageVo.getAssociationId();
            // 得到回复人信息
            Long senderId = messageVo.getSenderId();
            User user = userMapper.selectById(senderId);
            messageVo.setSenderName(user.getUsername());
            messageVo.setSenderHeadImg(user.getPhoto());
            Long commentId = messageVo.getCommentId();

            // 得到回复内容信息, 并判断用户是否点赞此内容
            Integer type = messageVo.getType();
            MessageEnum messageEnum = MessageEnum.getMessageEnum(type);
            switch (messageEnum) {
                case ARTICLE_MESSAGE:
                    // 查询文章内容
                    JSONObject article = null;
                    if (isComment.equals(CommonEnum.MESSAGE_LIKE_IS_CONTENT.getCode())) {
                        R resultArticle = userToArticleFeignService.queryArticleById(associationId);
                        if (resultArticle.getCode() != R.SUCCESS) {
                            throw new MonkeyBlogException(R.Error, resultArticle.getMsg());
                        }

                        article = (JSONObject) resultArticle.getData(new TypeReference<JSONObject>(){});
                    } else if (isComment.equals(CommonEnum.MESSAGE_LIKE_IS_COMMENT.getCode())) {
                        R resultArticle = userToArticleFeignService.queryArticleAndCommentById(associationId, commentId);
                        if (resultArticle.getCode() != R.SUCCESS) {
                            throw new MonkeyBlogException(R.Error, resultArticle.getMsg());
                        }

                        article = (JSONObject) resultArticle.getData(new TypeReference<JSONObject>(){});
                    }

                    if (article != null) {
                        messageVo.setContentTitle(article.getString("title"));
                    }
                    if (article != null) {
                        messageVo.setContentPicture(article.getString("picture"));
                    }
                    messageVo.setSendType(CommonEnum.ARTICLE_MESSAGE.getMsg());
                    break;
                case QUESTION_MESSAGE:
                    // 查询问答内容
                    JSONObject question = null;
                    if (isComment.equals(CommonEnum.MESSAGE_LIKE_IS_CONTENT.getCode())) {
                        R resultQuestion = userToQuestionFeignService.queryQuestionById(associationId);
                        if (resultQuestion.getCode() != R.SUCCESS) {
                            throw new MonkeyBlogException(R.Error, resultQuestion.getMsg());
                        }

                        question = (JSONObject) resultQuestion.getData(new TypeReference<JSONObject>(){});
                    } else if (isComment.equals(CommonEnum.MESSAGE_LIKE_IS_COMMENT.getCode())) {
                        R resultQuestion = userToQuestionFeignService.queryQuestionAndCommentById(associationId, commentId);
                        if (resultQuestion.getCode() != R.SUCCESS) {
                            throw new MonkeyBlogException(R.Error, resultQuestion.getMsg());
                        }

                        question = (JSONObject) resultQuestion.getData(new TypeReference<JSONObject>(){});
                    }

                    if (question != null) {
                        messageVo.setContentTitle(question.getString("title"));
                    }
                    if (question != null) {
                        messageVo.setContentPicture(question.getString("picture"));
                    }
                    messageVo.setSendType(CommonEnum.QUESTION_MESSAGE.getMsg());
                    break;
                case COMMUNITY_ARTICLE_MESSAGE:
                    // 查询社区文章内容
                    JSONObject communityArticle = null;
                    if (isComment.equals(CommonEnum.MESSAGE_LIKE_IS_CONTENT.getCode())) {
                        R resultCommunityArticle = userToCommunityFeignService.queryCommunityArticleById(associationId);
                        if (resultCommunityArticle.getCode() != R.SUCCESS) {
                            throw new MonkeyBlogException(R.Error, resultCommunityArticle.getMsg());
                        }

                        communityArticle = (JSONObject) resultCommunityArticle.getData(new TypeReference<JSONObject>(){});
                    } else if (isComment.equals(CommonEnum.MESSAGE_LIKE_IS_COMMENT.getCode())) {
                        R resultCommunityArticle = userToCommunityFeignService.queryCommunityArticleAndCommentById(associationId, commentId);
                        if (resultCommunityArticle.getCode() != R.SUCCESS) {
                            throw new MonkeyBlogException(R.Error, resultCommunityArticle.getMsg());
                        }

                        communityArticle = (JSONObject) resultCommunityArticle.getData(new TypeReference<JSONObject>(){});
                    }

                    if (communityArticle != null) {
                        messageVo.setContentTitle(communityArticle.getString("title"));
                    }
                    if (communityArticle != null) {
                        messageVo.setContentPicture(communityArticle.getString("picture"));
                    }
                    messageVo.setSendType(CommonEnum.COMMUNITY_ARTICLE_MESSAGE.getMsg());
                    break;
                case RESOURCE_MESSAGE:
                    // 查询资源内容
                    JSONObject resource = null;
                    if (isComment.equals(CommonEnum.MESSAGE_LIKE_IS_CONTENT.getCode())) {
                        R resultResource = userToResourceFeignService.queryResourceById(associationId);
                        if (resultResource.getCode() != R.SUCCESS) {
                            throw new MonkeyBlogException(R.Error, resultResource.getMsg());
                        }

                        resource = (JSONObject) resultResource.getData(new TypeReference<JSONObject>(){});
                    } else if (isComment.equals(CommonEnum.MESSAGE_LIKE_IS_COMMENT.getCode())) {
                        R resultResource = userToResourceFeignService.queryResourceAndCommentById(associationId, commentId);
                        if (resultResource.getCode() != R.SUCCESS) {
                            throw new MonkeyBlogException(R.Error, resultResource.getMsg());
                        }

                        resource = (JSONObject) resultResource.getData(new TypeReference<JSONObject>(){});
                    }

                    if (resource != null) {
                        messageVo.setContentTitle(resource.getString("title"));
                    }
                    if (resource != null) {
                        messageVo.setContentPicture(resource.getString("picture"));
                    }
                    messageVo.setSendType(CommonEnum.RESOURCE_MESSAGE.getMsg());
                    break;
                case COURSE_MESSAGE:
                    // 查询课程内容
                    JSONObject course = null;

                    if (isComment.equals(CommonEnum.MESSAGE_LIKE_IS_CONTENT.getCode())) {
                        R resultCourse = userToCourseFeignService.queryCourseById(associationId);
                        if (resultCourse.getCode() != R.SUCCESS) {
                            throw new MonkeyBlogException(R.Error, resultCourse.getMsg());
                        }

                        course = (JSONObject) resultCourse.getData(new TypeReference<JSONObject>(){});
                    } else if (isComment.equals(CommonEnum.MESSAGE_LIKE_IS_COMMENT.getCode())) {
                        R resultCourse = userToCourseFeignService.queryCourseAndCommentById(associationId, commentId);
                        if (resultCourse.getCode() != R.SUCCESS) {
                            throw new MonkeyBlogException(R.Error, resultCourse.getMsg());
                        }

                        course = (JSONObject) resultCourse.getData(new TypeReference<JSONObject>(){});
                    }

                    if (course != null) {
                        messageVo.setContentTitle(course.getString("title"));
                    }
                    if (course != null) {
                        messageVo.setContentPicture(course.getString("picture"));
                    }
                    messageVo.setSendType(CommonEnum.COURSE_MESSAGE.getMsg());
                    break;
            }

            // 得到消息类型
            messageVoList.add(messageVo);
        }

        selectPage.setRecords(messageVoList);
        // 把查到的未读消息置为已读消息
        if (messageIdList.size() > 0) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("event", EventConstant.updateLikeMessageAlready);
            jsonObject.put("messageIdList", JSONObject.toJSONString(messageIdList));
            Message message = new Message(jsonObject.toJSONString().getBytes());
            rabbitTemplate.convertAndSend(RabbitmqExchangeName.userUpdateDirectExchange,
                    RabbitmqRoutingName.userUpdateRouting, message);
        }
        return R.ok(selectPage);
    }

    /**
     * 删除点赞消息
     *
     * @param messageId 消息id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/28 17:08
     */
    @Override
    public R deleteLikeMessage(Long messageId) {
        return R.ok(messageLikeMapper.deleteById(messageId));
    }

    /**
     * 将所有点赞消息置为已读
     *
     * @param userId 用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/28 17:18
     */
    @Override
    public R updateAllLikeAlready(String userId) {
        if (userId == null || "".equals(userId)) {
            return R.error(ExceptionEnum.NOT_LOGIN.getMsg());
        }

        UpdateWrapper<MessageLike> messageLikeUpdateWrapper = new UpdateWrapper<>();
        messageLikeUpdateWrapper.eq("recipient_id", userId);
        messageLikeUpdateWrapper.set("is_read", CommonEnum.MESSAGE_IS_READ.getCode());
        messageLikeMapper.update(null, messageLikeUpdateWrapper);
        return R.ok();
    }

    /**
     * 删除所有点赞已读消息
     *
     * @param userId 用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/28 17:18
     */
    @Override
    public R deleteAllLikeMessageOfAlreadyRead(String userId) {
        if (userId == null || "".equals(userId)) {
            return R.error(ExceptionEnum.NOT_LOGIN.getMsg());
        }
        QueryWrapper<MessageLike> messageLikeQueryWrapper = new QueryWrapper<>();
        messageLikeQueryWrapper.eq("recipient_id", userId);
        messageLikeQueryWrapper.eq("is_read", CommonEnum.MESSAGE_IS_READ.getCode());
        messageLikeMapper.delete(messageLikeQueryWrapper);

        return R.ok();
    }
}
