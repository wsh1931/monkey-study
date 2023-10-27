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
import com.monkey.monkeyUtils.mapper.MessageCommentReplyMapper;
import com.monkey.monkeyUtils.pojo.MessageCommentReply;
import com.monkey.monkeyUtils.pojo.vo.MessageCommentReplyVo;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyblog.feign.*;
import com.monkey.monkeyblog.rabbitmq.EventConstant;
import com.monkey.monkeyblog.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeyblog.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeyblog.service.message.CommentReplyService;
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
 * @date: 2023/10/25 17:13
 * @version: 1.0
 * @description:
 */
@Service
public class CommentReplyServiceImpl implements CommentReplyService {
    @Resource
    private MessageCommentReplyMapper messageCommentReplyMapper;
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
    private RabbitTemplate rabbitTemplate;
    /**
     * 查询评论回复消息
     *
     * @param userId 当前登录用户id
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/25 17:25
     */
    @Override
    public R queryCommentReplyMessage(long userId, Long currentPage, Integer pageSize) {
        QueryWrapper<MessageCommentReply> messageCommentReplyQueryWrapper = new QueryWrapper<>();
        messageCommentReplyQueryWrapper.eq("recipient_id", userId);
        messageCommentReplyQueryWrapper.orderByDesc("create_time");
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = messageCommentReplyMapper.selectPage(page, messageCommentReplyQueryWrapper);
        List<MessageCommentReply> messageCommentReplyList = selectPage.getRecords();
        // 最终返回集合
        List<MessageCommentReplyVo> messageCommentReplyVoList = new ArrayList<>();
        // 查询到的未读消息id集合
        List<Long> messageIdList = new ArrayList<>(messageCommentReplyList.size());
        for (MessageCommentReply messageCommentReply : messageCommentReplyList) {
            if (messageCommentReply.getIsRead().equals(CommonEnum.MESSAGE_NOT_READ.getCode())) {
                messageIdList.add(messageCommentReply.getId());
            }
            MessageCommentReplyVo messageCommentReplyVo = new MessageCommentReplyVo();
            BeanUtils.copyProperties(messageCommentReply, messageCommentReplyVo);
            Long associationId = messageCommentReplyVo.getAssociationId();
            // 得到回复人信息
            Long senderId = messageCommentReplyVo.getSenderId();
            User user = userMapper.selectById(senderId);
            messageCommentReplyVo.setSenderName(user.getUsername());
            messageCommentReplyVo.setSenderHeadImg(user.getPhoto());

            // 得到回复内容信息, 并判断用户是否点赞此内容
            Integer type = messageCommentReplyVo.getType();
            MessageEnum messageEnum = MessageEnum.getMessageEnum(type);
            switch (messageEnum) {
                case ARTICLE_MESSAGE:
                    // 查询文章内容
                    R resultArticle = userToArticleFeignService.queryArticleById(associationId);
                    if (resultArticle.getCode() != R.SUCCESS) {
                        throw new MonkeyBlogException(R.Error, resultArticle.getMsg());
                    }

                    JSONObject article = (JSONObject) resultArticle.getData(new TypeReference<JSONObject>(){});
                    messageCommentReplyVo.setContentTitle(article.getString("title"));
                    messageCommentReplyVo.setContentPicture(article.getString("picture"));
                    messageCommentReplyVo.setSendType(CommonEnum.ARTICLE_MESSAGE.getMsg());
                    break;
                case QUESTION_MESSAGE:
                    // 查询问答内容
                    R resultQuestion = userToQuestionFeignService.queryQuestionById(associationId);
                    if (resultQuestion.getCode() != R.SUCCESS) {
                        throw new MonkeyBlogException(R.Error, resultQuestion.getMsg());
                    }

                    JSONObject question = (JSONObject) resultQuestion.getData(new TypeReference<JSONObject>(){});
                    messageCommentReplyVo.setContentTitle(question.getString("title"));
                    messageCommentReplyVo.setContentPicture(question.getString("picture"));
                    messageCommentReplyVo.setSendType(CommonEnum.QUESTION_MESSAGE.getMsg());
                    break;
                case COMMUNITY_ARTICLE_MESSAGE:
                    // 查询社区文章内容
                    R resultCommunityArticle = userToCommunityFeignService.queryCommunityArticleById(associationId);
                    if (resultCommunityArticle.getCode() != R.SUCCESS) {
                        throw new MonkeyBlogException(R.Error, resultCommunityArticle.getMsg());
                    }

                    JSONObject communityArticle = (JSONObject) resultCommunityArticle.getData(new TypeReference<JSONObject>(){});
                    messageCommentReplyVo.setContentTitle(communityArticle.getString("title"));
                    messageCommentReplyVo.setContentPicture(communityArticle.getString("picture"));
                    messageCommentReplyVo.setSendType(CommonEnum.COMMUNITY_ARTICLE_MESSAGE.getMsg());
                    break;
                case RESOURCE_MESSAGE:
                    // 查询资源内容
                    R resultResource = userToResourceFeignService.queryResourceById(associationId);
                    if (resultResource.getCode() != R.SUCCESS) {
                        throw new MonkeyBlogException(R.Error, resultResource.getMsg());
                    }

                    JSONObject resource = (JSONObject) resultResource.getData(new TypeReference<JSONObject>(){});
                    messageCommentReplyVo.setContentTitle(resource.getString("title"));
                    messageCommentReplyVo.setContentPicture(resource.getString("picture"));
                    messageCommentReplyVo.setSendType(CommonEnum.RESOURCE_MESSAGE.getMsg());
                    break;
                case COURSE_MESSAGE:
                    // 查询课程内容
                    R resultCourse = userToCourseFeignService.queryCourseById(associationId);
                    if (resultCourse.getCode() != R.SUCCESS) {
                        throw new MonkeyBlogException(R.Error, resultCourse.getMsg());
                    }

                    JSONObject course = (JSONObject) resultCourse.getData(new TypeReference<JSONObject>(){});
                    messageCommentReplyVo.setContentTitle(course.getString("title"));
                    messageCommentReplyVo.setContentPicture(course.getString("picture"));
                    messageCommentReplyVo.setSendType(CommonEnum.COURSE_MESSAGE.getMsg());
                    break;
            }

            // 得到消息类型
            messageCommentReplyVoList.add(messageCommentReplyVo);
        }

        selectPage.setRecords(messageCommentReplyVoList);
        // 把查到的未读消息置为已读消息
        if (messageIdList.size() > 0) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("event", EventConstant.updateCommentReplyMessageAlready);
            jsonObject.put("messageIdList", JSONObject.toJSONString(messageIdList));
            Message message = new Message(jsonObject.toJSONString().getBytes());
            rabbitTemplate.convertAndSend(RabbitmqExchangeName.userUpdateDirectExchange,
                    RabbitmqRoutingName.userUpdateRouting, message);
        }

        return R.ok(selectPage);
    }

    /**
     * 删除评论回复消息
     *
     * @param commentReplyId 评论回复id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/27 16:24
     */
    @Override
    public R deleteCommentReply(Long commentReplyId) {
        return R.ok(messageCommentReplyMapper.deleteById(commentReplyId));
    }

    /**
     * 将所有评论回复置为已读
     *
     * @param userId 用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/27 16:30
     */
    @Override
    public R updateAllCommentReplyAlready(String userId) {
        if (userId == null || "".equals(userId)) {
            return R.error(ExceptionEnum.NOT_LOGIN.getMsg());
        }

        UpdateWrapper<MessageCommentReply> messageCommentReplyUpdateWrapper = new UpdateWrapper<>();
        messageCommentReplyUpdateWrapper.eq("recipient_id", userId);
        messageCommentReplyUpdateWrapper.set("is_read", CommonEnum.MESSAGE_IS_READ.getCode());
        messageCommentReplyMapper.update(null, messageCommentReplyUpdateWrapper);
        return R.ok();
    }

    /**
     * 删除所有评论回复已读消息
     *
     * @param userId 用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/27 16:44
     */
    @Override
    public R deleteAllCommentMessageOfAlreadyRead(String userId) {
        QueryWrapper<MessageCommentReply> messageCommentReplyQueryWrapper = new QueryWrapper<>();
        messageCommentReplyQueryWrapper.eq("recipient_id", userId);
        messageCommentReplyQueryWrapper.eq("is_read", CommonEnum.MESSAGE_IS_READ.getCode());
        messageCommentReplyMapper.delete(messageCommentReplyQueryWrapper);
        return R.ok();
    }
}
