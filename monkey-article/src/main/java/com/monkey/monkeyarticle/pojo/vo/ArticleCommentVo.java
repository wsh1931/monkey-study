package com.monkey.monkeyarticle.pojo.vo;

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
public class ArticleCommentVo {
    private Long id;
    private Long userId;
    private Long articleId;
    private String content;
    private Long parentId;
    private Long replyId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date commentTime;

    // 评论者姓名
    private String userName;
    // 评论者头像
    private String userNamePhoto;
    // 回复者姓名
    private String replyName;
    // 回复者头像
    private String replyNamePhoto;
    // 评论点赞数
    private Long commentLikeSum;
    // 该用户对该评论是否已点赞
    private Long isLike;
    // 该评论下方是否展示输入框
    private Boolean showInput;
    // 文章评论内容
    private String articleCommentContent;
    // 除了一级评论剩下的评论
    List<ArticleCommentVo> downComment = new ArrayList<>();
}
