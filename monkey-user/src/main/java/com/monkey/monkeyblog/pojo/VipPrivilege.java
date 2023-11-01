package com.monkey.monkeyblog.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author: wusihao
 * @date: 2023/10/31 9:47
 * @version: 1.0
 * @description:
 */
@Data
public class VipPrivilege {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String content;
    private Integer sort;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createTime;
}
