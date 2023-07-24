package com.monkey.monkeycourse.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author: wusihao
 * @date: 2023/7/22 17:05
 * @version: 1.0
 * @description: 教师岗位表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeacherPost {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long teacherId;
    private Long postId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date updateTime;
}
