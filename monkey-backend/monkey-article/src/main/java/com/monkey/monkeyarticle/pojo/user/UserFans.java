package com.monkey.monkeyarticle.pojo.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

// 用户相互关注表
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFans {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long fansId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createTime;
}
