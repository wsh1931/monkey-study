package com.monkey.monkeysearch.pojo;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/11/12 10:01
 * @version: 1.0
 * @description: 社区索引类
 */
@Data
@ToString
public class ESCommunityIndex {
    private String id;
    private String name;
    private String description;
    private String photo;
    private String classificationName;
    private String attributeLabelName;
    private List<String> contentLabelName = new ArrayList<>();
    private Long memberCount;
    private Long articleCount;
    private Date createTime;
}
