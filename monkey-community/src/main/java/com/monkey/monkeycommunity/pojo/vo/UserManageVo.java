package com.monkey.monkeycommunity.pojo.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeycommunity.constant.CommunityEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author: wusihao
 * @date: 2023/9/30 11:37
 * @version: 1.0
 * @description: 用户管理Vo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserManageVo {
    @ColumnWidth(13)
    @ExcelProperty("用户id")
    @ApiModelProperty("用户id")
    private Long id;

    @ColumnWidth(25)
    @ExcelProperty("用户名")
    @ApiModelProperty("用户名")
    private String username;

    @ExcelIgnore
    @ApiModelProperty("用户头像")
    private String headImg;

    @ApiModelProperty("角色id")
    @ExcelIgnore
    public Long roleId;

    @ColumnWidth(25)
    @ExcelProperty("角色名称")
    @ApiModelProperty("用户角色名称")
    private String roleName;

    @ColumnWidth(18)
    @ExcelProperty
    @ApiModelProperty("加入日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createTime;

    @ExcelIgnore
    @ApiModelProperty("是否有权利移出该角色")
    public Integer isAuthorization;

}
