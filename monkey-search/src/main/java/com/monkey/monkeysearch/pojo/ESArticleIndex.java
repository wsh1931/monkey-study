package com.monkey.monkeysearch.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/11/6 16:37
 * @version: 1.0
 * @description:
 */
@Data
@ApiModel("elasticsearch文章索引类")
public class ESArticleIndex {
    private String id;
    private List<String> labelName;
    private Long userId;
    private String username;
    private String userHeadImg;
    private String userBrief;
    private String title;
    private String profile;
    private String content;
    private String photo;
    private String viewCount;
    private String likeCount;
    private String collectCount;
    private String commentCount;
    private Date createTime;

}
