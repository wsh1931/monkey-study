package com.monkey.monkeyblog.service.Impl.message;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.constants.ExceptionEnum;
import com.monkey.monkeyUtils.mapper.MessageAttentionMapper;
import com.monkey.monkeyUtils.pojo.MessageAttention;
import com.monkey.monkeyUtils.pojo.vo.MessageVo;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyblog.rabbitmq.EventConstant;
import com.monkey.monkeyblog.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeyblog.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeyblog.service.message.MessageAttentionService;
import com.monkey.monkeyUtils.mapper.UserMapper;
import com.monkey.monkeyUtils.pojo.User;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/10/30 8:12
 * @version: 1.0
 * @description:
 */
@Service
public class MessageAttentionServiceImpl implements MessageAttentionService {
    @Resource
    private MessageAttentionMapper messageAttentionMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 查询关注消息集合
     *
     * @param userId 用户id
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/30 8:17
     */
    @Override
    public R queryAttentionMessage(long userId, Long currentPage, Integer pageSize) {
        QueryWrapper<MessageAttention> messageAttentionQueryWrapper = new QueryWrapper<>();
        messageAttentionQueryWrapper.eq("recipient_id", userId);
        messageAttentionQueryWrapper.orderByDesc("create_time");
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = messageAttentionMapper.selectPage(page, messageAttentionQueryWrapper);
        List<MessageAttention> messageAttentionList = selectPage.getRecords();
        // 最终返回集合
        List<MessageVo> messageVoList = new ArrayList<>();
        // 查询到的未读消息id集合
        List<Long> messageIdList = new ArrayList<>(messageAttentionList.size());
        for (MessageAttention messageAttention : messageAttentionList) {
            if (messageAttention.getIsRead().equals(CommonEnum.MESSAGE_NOT_READ.getCode())) {
                messageIdList.add(messageAttention.getId());
            }
            MessageVo messageVo = new MessageVo();
            BeanUtils.copyProperties(messageAttention, messageVo);
            // 得到回复人信息
            Long senderId = messageVo.getSenderId();
            User user = userMapper.selectById(senderId);
            messageVo.setSenderName(user.getUsername());
            messageVo.setSenderHeadImg(user.getPhoto());

            messageVoList.add(messageVo);
        }

        selectPage.setRecords(messageVoList);
        // 把查到的未读消息置为已读消息
        if (messageIdList.size() > 0) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("event", EventConstant.updateAttentionMessageAlready);
            jsonObject.put("messageIdList", JSONObject.toJSONString(messageIdList));
            Message message = new Message(jsonObject.toJSONString().getBytes());
            rabbitTemplate.convertAndSend(RabbitmqExchangeName.userUpdateDirectExchange,
                    RabbitmqRoutingName.userUpdateRouting, message);
        }
        return R.ok(selectPage);
    }

    /**
     * 删除关注消息
     *
     * @param messageId 消息id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/30 8:31
     */
    @Override
    public R deleteAttentionMessage(Long messageId) {
        return R.ok(messageAttentionMapper.deleteById(messageId));
    }

    /**
     * 将所有关注消息置为已读
     *
     * @param userId 用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/30 8:31
     */
    @Override
    public R updateAllAttentionAlready(String userId) {
        if (userId == null || "".equals(userId)) {
            return R.error(ExceptionEnum.NOT_LOGIN.getMsg());
        }

        UpdateWrapper<MessageAttention> messageAttentionUpdateWrapper = new UpdateWrapper<>();
        messageAttentionUpdateWrapper.eq("recipient_id", userId);
        messageAttentionUpdateWrapper.set("is_read", CommonEnum.MESSAGE_IS_READ.getCode());
        messageAttentionMapper.update(null, messageAttentionUpdateWrapper);
        return R.ok();
    }

    /**
     * 删除所有关注已读消息
     *
     * @param userId 用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/30 8:32
     */
    @Override
    public R deleteAllAttentionMessageOfAlreadyRead(String userId) {
        if (userId == null || "".equals(userId)) {
            return R.error(ExceptionEnum.NOT_LOGIN.getMsg());
        }
        QueryWrapper<MessageAttention> messageAttentionQueryWrapper = new QueryWrapper<>();
        messageAttentionQueryWrapper.eq("recipient_id", userId);
        messageAttentionQueryWrapper.eq("is_read", CommonEnum.MESSAGE_IS_READ.getCode());
        messageAttentionMapper.delete(messageAttentionQueryWrapper);

        return R.ok();
    }
}
