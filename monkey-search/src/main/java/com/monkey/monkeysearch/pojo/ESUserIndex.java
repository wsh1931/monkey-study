package com.monkey.monkeysearch.pojo;

import lombok.Data;

import java.util.Date;

/**
 * @author: wusihao
 * @date: 2023/11/14 9:48
 * @version: 1.0
 * @description:
 */
@Data
public class ESUserIndex {
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
}
