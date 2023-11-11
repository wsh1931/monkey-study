package com.monkey.monkeysearch.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/11/11 15:54
 * @version: 1.0
 * @description:
 */
@Data
public class ESCommunityArticleIndex {
    private String id;
    private Long userId;
    private Long communityId;
    private String communityName;
    private String username;
    private String userHeadImg;
    private String userBrief;
    private String title;
    private String brief;
    private String content;
    private String picture;
    private Float score;
    private Integer likeCount;
    private Integer viewCount;
    private Integer collectCount;
    private Integer commentCount;
    private Date createTime;
}
