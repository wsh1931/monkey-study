package com.monkey.monkeysearch.pojo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/11/15 16:04
 * @version: 1.0
 * @description:
 */
@Data
public class ESAllIndex {
    private List<String> labelName;
    private Long userId;
    private String username;
    private String userHeadImg;
    private String userBrief;
    private Long communityId;
    private String communityName;
    private String title;
    private String profile;
    private String content;
    private String photo;
    private Long viewCount;
    private Integer collectCount;
    private Integer commentCount;
    private Integer type;
    private String associationId;
    private Integer likeCount;
    private Date createTime;
    private String formTypeName;
}
