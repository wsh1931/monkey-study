package com.monkey.monkeyblog.pojo.Vo.article;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleVo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private Long visit;
    private Long likes;
    private String profile;
    private String photo;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date updateTime;

    private Long commentSum;
    private Long likeSum; // 文章点赞数
    private Long isLike; // 用户是否已点赞(0表示未点赞，1表示已点赞)
    private Long collect; // 文章收藏数
    private Long isCollect; // 用户是否已收藏，0表示未收藏，1表示已收藏
}
