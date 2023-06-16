package com.monkey.monkeyblog.pojo.Vo.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

// 聊天用户信息列表
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserChatVo {
    private Long id;
    private Long senderId; // 回复人id
    private String senderPhoto; // 聊天人头像
    private String senderName; // 聊天人姓名
    private String senderBrief; // 聊天人简介
    private String receiverName; // 回复人姓名
    private Long receiverId; // 回复者id
    private String receiverPhoto; // 回复人头像
    private String receiverBrief; // 回复人简介
    private String lastContent; // 最后聊天内容
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date LastCreateTime; // 最后聊天时间
    private Long isLike; // 当前用户是否关注回复人(0表示未关注，1表示已关注)
    private String direction; // 聊天人的方向
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")

    private Date createTime; // 每条记录聊天时间
    private String content; // 每条聊天内容
}
