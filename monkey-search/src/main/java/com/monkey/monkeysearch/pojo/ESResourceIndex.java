package com.monkey.monkeysearch.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/11/12 16:40
 * @version: 1.0
 * @description: elasticsearch资源索引
 */
@Data
public class ESResourceIndex {
    private String id;
    private Long userId;
    private String username;
    private String userHeadImg;
    private String userBrief;
    private String name;
    private String typeUrl;
    private String description;
    private List<String> resourceClassificationName = new ArrayList<>();
    private List<String> resourceLabelName = new ArrayList<>();
    private String formTypeName;
    private BigDecimal score;
    private Long viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer collectCount;
    private Integer downCount;
    private Integer buyCount;
    private Date createTime;
}
