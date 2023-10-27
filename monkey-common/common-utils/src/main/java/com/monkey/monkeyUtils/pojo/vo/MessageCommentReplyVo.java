package com.monkey.monkeyUtils.pojo.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * @author: wusihao
 * @date: 2023/10/26 10:03
 * @version: 1.0
 * @description:
 */
@Data
public class MessageCommentReplyVo {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 发送人id
     */
    private Long senderId;
    /**
     * 接收人id
     */
    private Long recipientId;
    private Integer isComment;
    /**
     * 关联id(0表示文章，1表示问答，2表示课程，3表示社区文章, 4表示资源)
     */
    private Long associationId;
    /**
     * 评论类型(0表示文章，1表示问答，2表示课程，3表示社区文章, 4表示资源)
     */
    private Integer type;
    /**
     * 发送内容
     */
    private String sendContent;
    /**
     * 是否已读消息(0表示未读，1表示已读)
     */
    private Integer isRead;
    /**
     * 评论时间
     */
    private Date createTime;

    private Integer isLike;
    private String senderName;
    private String senderHeadImg;
    private String senderContent;
    private String contentTitle;
    private String contentPicture;
    private String sendType;
}
