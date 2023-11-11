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
 * @date: 2023/11/11 10:29
 * @version: 1.0
 * @description:
 */
@Data
public class ESCourseIndex {
    private String id;
    private Long userId;
    private String username;
    private String userHeadImg;
    private String userBrief;
    private List<String> labelName;
    private String title;
    private String introduce;
    private String picture;
    private Long viewCount;
    private Long collectCount;
    private Long commentCount;
    private Long sectionCount;
    private Long studyCount;
    private String formTypeName;
    private Float score;
    private Date createTime;

}
