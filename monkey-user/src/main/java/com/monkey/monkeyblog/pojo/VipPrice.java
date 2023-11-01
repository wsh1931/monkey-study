package com.monkey.monkeyblog.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.models.auth.In;
import lombok.Data;

import java.util.Date;

/**
 * @author: wusihao
 * @date: 2023/10/31 9:45
 * @version: 1.0
 * @description:
 */
@Data
public class VipPrice {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer day;
    private Integer price;
    private Integer sort;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createTime;
}
