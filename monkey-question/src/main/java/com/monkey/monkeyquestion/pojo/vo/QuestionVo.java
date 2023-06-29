package com.monkey.monkeyquestion.pojo.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
// 支持链式编程
@Accessors(chain = true)
public class QuestionVo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String title;
    private String profile;
    private Long visit;
    private Long labelId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date updateTime;

    private String userphoto; // 用户头像
    private String username; // 用户名
    private Long replyCount; // 用户回答数
    private Long userCollectCount; // 用户收藏数
    private Long sort; // 右侧热门回答序号数
    private Long userLikeCount; // 用户点赞数
    private Long isLike; // 用户是否收藏
    private Long isCollect; // 用户是否点赞

    private Long isConcern; // 该登录用户是否关注该问题
}
