package com.monkey.monkeyUtils.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVo {
    private Long id;
    private String password;
    private String username;
    private String job;
    private String photo;
    private String brief;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date registerTime;
    private Integer isDeleted;
    private String email;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Shanghai")
    private String birthday;
    private String phone;
    // 工作单位
    private String jobUnit;

    // 用户游览数，用户所有文章游览总和
    private Long visit;
    // 粉丝数
    private Long fans;
    // 是否已经关注该作者
    private Long isFans;
    // 关注数
    private Long concern;
    // 用户所发表的文章数
    private Long articleSum;
    // 用户获得的收藏数
    private Long userCollect;
    // 用户获得到点赞数
    private Long likeSum;
    // 用户获得的评论数
    private Long commentSum;
    // 用户提问数
    private Long questionSum;
    // 用户的收藏数
    private Long collect;
}
