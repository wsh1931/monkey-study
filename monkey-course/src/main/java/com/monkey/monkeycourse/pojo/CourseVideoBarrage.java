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
 * @description:
 * @date: 2023/7/12 17:38
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseVideoBarrage {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long courseVideoId;
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createTime;
}
