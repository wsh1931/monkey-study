package com.monkey.monkeyresource.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * @author: wusihao
 * @date: 2023/10/16 11:09
 * @version: 1.0
 * @description:
 */
@Data
public class ResourceBuy {
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 资源id
     */
    private Long resourceId;
    private Long userId;
    private Date createTime;
}
