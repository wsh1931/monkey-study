package com.monkey.monkeycommunity.pojo.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.monkey.monkeycommunity.pojo.CommunityClassificationLabel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/10/6 15:22
 * @version: 1.0
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommunityVo {
    /**
     * 主键id
     */
    private Long id;
    private String name;
    /**
     * 社区描述
     */
    private String description;
    /**
     * 社区头像
     */
    private String photo;
    /**
     * 社区分类
     */
    private Long classificationId;
    private String classification;
    /**
     * 评论前是否需要加入社区（0不需要，1需要）
     */
    private Integer isComment;
    /**
     * 属性标签
     */
    private String attributeLabel;
    private Long attributeLabelId;
    /**
     * 社区公告
     */
    private String notice;
    /**
     * 加入社区方式(0表示无限制，1表示管理员邀请，2表示定向邀请)
     */
    private Integer enterWay;

    @TableField(exist = false)
    private List<CommunityClassificationLabel> communityClassificationLabelList = new ArrayList<>();

}
