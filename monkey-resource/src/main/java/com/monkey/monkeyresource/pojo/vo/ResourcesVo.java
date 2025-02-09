package com.monkey.monkeyresource.pojo.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.monkey.monkeyresource.constant.ResourcesEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/10/13 9:41
 * @version: 1.0
 * @description:
 */
@Data
public class ResourcesVo {
    /**
     * 主键id
     */
    private Long id;
    /**
     * 发布用户id
     */
    private Long userId;
    private String username;
    private String headImg;
    /**
     * 形式类型id
     */
    private Long formTypeId;

    private String formTypeName;
    /**
     * 资源地址
     */
    private String url;
    /**
     * 资源类型
     */
    private String type;

    private String typeUrl;
    /**
     * 资源名称
     */
    private String name;
    /**
     * 资源描述
     */
    private String description;
    /**
     * 资源游览数
     */
    private Long viewCount;
    /**
     * 资源总评分
     */
    private BigDecimal score;
    /**
     * 评分人数
     */
    private Integer scoreCount;
    /**
     * 评论人数
     */
    private Integer commentCount;
    /**
     * 收藏人数
     */
    private Integer collectCount;

    private Integer downCount;
    /**
     * 点赞数
     */
    private Integer likeCount;
    /**
     * 是否精选(0表示不精选，1表示精选)
     */
    private Integer isCuration;
    private Integer status;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;

    private String price;
    private String originPrice;

    private Integer buyCount;

    private Integer resourcesCount;

    private List<String> resourceLabel = new ArrayList<>();

    private String resourceClassificationName;

    private String email;
    private Integer hasEmail;

    private Integer isHover = ResourcesEnum.NOT_HOVER.getCode();
    private Integer isMoreHover = ResourcesEnum.NOT_HOVER.getCode();
}
