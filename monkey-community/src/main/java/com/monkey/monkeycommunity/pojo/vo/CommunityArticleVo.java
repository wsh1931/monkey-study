package com.monkey.monkeycommunity.pojo.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.monkey.monkeycommunity.pojo.CommunityArticleTask;
import com.monkey.monkeycommunity.pojo.CommunityArticleVote;
import com.monkey.spring_security.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/9/8 9:10
 * @version: 1.0
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommunityArticleVo {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 社区id
     */
    private Long communityId;
    /**
     * 频道id
     */
    private Long channelId;
    /**
     * title
     */
    private String title;
    private String brief;
    /**
     * 文章内容
     */
    private String content;
    /**
     * 文章头像
     */
    private String picture;
    /**
     * 是否以任务形式发布（0不是，1是）
     */
    private Integer isTask;
    /**
     * 是否已投票形式发布（0不是，1是）
     */
    private Integer isVote;
    /**
     * 文章点赞数
     */
    private Integer likeCount;
    /**
     * 文章游览数
     */
    private Integer viewCount;
    /**
     * 文章收藏数
     */
    private Integer collectCount;
    /**
     * 文章评分
     */
    private Float score;
    /**
     * 评分人数
     */
    private Integer scoreCount;
    private Integer commentCount;
    private	Integer status;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createTime;
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date updateTime;

    private List<User> communityMemberList = new ArrayList<>();

    private CommunityArticleTask communityArticleTask;

    private CommunityArticleVote communityArticleVote;
}
