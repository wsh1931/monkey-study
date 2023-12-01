package com.monkey.monkeysearch.pojo.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author: wusihao
 * @date: 2023/11/15 9:36
 * @version: 1.0
 * @description:
 */
@Data
public class ESUserIndexVo {
    private String id;
    private String username;
    private String userHeadImg;
    private String userBrief;
    private Long viewCount;
    private Integer likeCount;
    private Integer collectCount;
    private Integer opusCount;
    private Integer fansCount;
    private Integer connectCount;
    private Date createTime;
    private Integer isFans;
}
