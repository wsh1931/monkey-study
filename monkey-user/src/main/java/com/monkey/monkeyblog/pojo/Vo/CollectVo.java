package com.monkey.monkeyblog.pojo.Vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.monkey.monkeyUtils.constants.CommonEnum;
import io.swagger.models.auth.In;
import lombok.Data;

import java.util.Date;

/**
 * @author: wusihao
 * @date: 2023/12/14 15:48
 * @version: 1.0
 * @description:
 */
@Data
public class CollectVo {
    private Long id;
    private Long collectContentId;
    private Long userId;
    private Long associateId;
    private Integer type;
    private String title;
    private String brief;
    private Long viewCount;
    private Integer commentCount;
    private Integer collectCount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createTime;

    private String typename;
}
