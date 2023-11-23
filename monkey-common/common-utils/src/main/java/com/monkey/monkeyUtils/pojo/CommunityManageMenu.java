package com.monkey.monkeyUtils.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * @author: wusihao
 * @date: 2023/11/21 9:51
 * @version: 1.0
 * @description:
 */
@Data
public class CommunityManageMenu {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long parentId;
    /**
     * 菜单名
     */
    private String menuName;
    /**
     * 路由地址
     */
    private String path;
    /**
     * 组件路径
     */
    private String component;
    /**
     * 菜单状态（0显示 1隐藏）
     */
    private Integer visible;
    /**
     * 菜单状态（0正常 1停用）
     */
    private Integer status;
    /**
     * 权限标识
     */
    private String perms;
    /**
     * 菜单图标
     */
    private String icon;
    /**
     * 创建用户id
     */
    private Long createUser;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新用户id
     */
    private Long updateUser;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 备注
     */
    private String remark;
}
