package com.monkey.monkeyUtils.pojo.Vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author: wusihao
 * @description:
 * @date: 2023/7/11 17:20
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LabelVo {
    private Long id;
    private String labelName;
    private Long parentId;
    private Long level;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createTime;
    private boolean selected;
}
