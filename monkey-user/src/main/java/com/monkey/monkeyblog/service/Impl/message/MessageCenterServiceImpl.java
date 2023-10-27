package com.monkey.monkeyblog.service.Impl.message;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.mapper.MessageCommentReplyMapper;
import com.monkey.monkeyUtils.pojo.MessageCommentReply;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyblog.service.message.MessageCenterService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.management.Query;

/**
 * @author: wusihao
 * @date: 2023/10/27 11:04
 * @version: 1.0
 * @description:
 */
@Service
public class MessageCenterServiceImpl implements MessageCenterService {
    @Resource
    private MessageCommentReplyMapper messageCommentReplyMapper;

    /**
     * 查询未查看评论回复消息数
     *
     * @param userId 当前登录用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/27 11:08
     */
    @Override
    public R queryNoCheckCommentCount(String userId) {
        if (userId == null || "".equals(userId)) {
            return R.ok();
        }

        QueryWrapper<MessageCommentReply> messageCommentReplyQueryWrapper = new QueryWrapper<>();
        messageCommentReplyQueryWrapper.eq("recipient_id", userId);
        messageCommentReplyQueryWrapper.eq("is_read", CommonEnum.MESSAGE_NOT_READ.getCode());
        return R.ok(messageCommentReplyMapper.selectCount(messageCommentReplyQueryWrapper));
    }
}
