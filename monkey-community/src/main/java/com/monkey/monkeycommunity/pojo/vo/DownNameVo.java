package com.monkey.monkeycommunity.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: wusihao
 * @date: 2023/10/2 11:47
 * @version: 1.0
 * @description: 下设头衔
 */
@Data
public class DownNameVo {
    @ApiModelProperty("头衔名称")
    private String downName;
    @ApiModelProperty("头衔是否保存")
    private Integer isPreserve;
}
