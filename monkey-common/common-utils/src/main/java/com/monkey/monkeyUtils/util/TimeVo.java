package com.monkey.monkeyUtils.util;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yjc
 * @create 2023-03-23 16:28
 */
@Data
public class TimeVo {

    @ApiModelProperty("小时数")
    private Integer hours;

    @ApiModelProperty("天数")
    private Integer days;

    @ApiModelProperty("分钟数")
    private Integer minutes;
}
