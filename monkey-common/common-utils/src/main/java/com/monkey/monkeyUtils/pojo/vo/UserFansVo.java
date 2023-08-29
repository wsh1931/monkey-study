package com.monkey.monkeyUtils.pojo.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

// 用户相互关注表
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFansVo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long fansId;
    private Date createTime;
}
