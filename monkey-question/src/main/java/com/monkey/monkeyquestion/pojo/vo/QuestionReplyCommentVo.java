package com.monkey.monkeyquestion.pojo.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionReplyCommentVo {
    private Long id;
    private Long userId;
    private Long questionReplyId;
    private String content;
    private Long replyId;
    private Long parentId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createTime;

    // 评论用户姓名
    private String commentUserName;
    // 评论用户头像
    private String commentUserPhoto;
    // 回复用户姓名
    private String replyUserName;
    // 回复用户头像
    private String replyUserPhoto;
    // 问答评论数
    private Long replyCommentSum;
    // 是否展示该评论输入框
    private Boolean showInput;
    // 问答回复内容
    private String questionReplyContent;
    // 子评论列表
    List<QuestionReplyCommentVo> downComment = new ArrayList<>();
}
