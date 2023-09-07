package com.monkey.spring_security.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.monkey.monkeyUtils.constants.CommonEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/*
* 用户信息表
* */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer sex;
    private String password;
    private String username;
    private String job;
    private String photo;
    private String brief;
    private String phone;
    private String email;
    private String birthday;
    private Integer isVip;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date vipExpirationTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date registerTime;
    private String jobUnit;
    private Integer isDeleted;

    @TableField(exist = false)
    public Integer isFans;

    @TableField(exist = false)
    public Boolean selected = false;

    @TableField(exist = false)
    public Long communityRoleId;
}
