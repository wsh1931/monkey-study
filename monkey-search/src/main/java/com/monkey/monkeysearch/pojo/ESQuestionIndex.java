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
 * @date: 2023/11/9 16:03
 * @version: 1.0
 * @description:
 */
@Data
public class ESQuestionIndex {
    private Long id;
    private Long userId;
    private String title;
    private Long viewCount;
    private Integer likeCount;
    private Integer collectCount;
    private Integer replyCount;
    private String profile;
    private Date createTime;
    private List<String> labelName;
    private String username;
    private String userHeadImg;
    private String userBrief;
    private String photo;
}
