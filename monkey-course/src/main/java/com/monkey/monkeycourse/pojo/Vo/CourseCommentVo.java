package com.monkey.monkeycourse.pojo.Vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/8/7 16:43
 * @version: 1.0
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseCommentVo {
    private Long id;

    private Long courseId;
    private String content;
    private Long parentId;

    // 回复内容
    private String replyContent;

    // 评论时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date commentTime;
    // 评论者id
    private Long senderId;
    // 评论者姓名
    private String senderName;
    // 评论者头像
    private String senderPhoto;
    // 回复者id
    private Long replyId;
    // 回复者姓名
    private String replyName;
    // 回复者头像
    private String replyPhoto;
    // 评论点赞数
    private Long commentLikeSum;
    // 该用户对该评论是否已点赞
    private Long isLike;

    // 是否精选该评论(0表示不精选，1表示精选)
    private Integer isCuration;
    // 课程评论内容
    private String courseCommentContent;
    // 除了一级评论剩下的评论
    List<CourseCommentVo> downComment = new ArrayList<>();
    // 该评论下方是否展示输入框
    private Integer showInput;
    // 输入框内容
    private String inputContent;
    // 输入框默认展示内容
    private String placeholderContent;
    // 悬浮展示更多样式(0表示不展示，1表示展示)
    private Integer isShowMore;
    // 展示更多内容
    private Integer isShowMoreContent;

    // 判断是否为当前登录用户发表的评论(0表示不属于，1表示属于)
    private Integer commentIsOfLoginUser;
}
