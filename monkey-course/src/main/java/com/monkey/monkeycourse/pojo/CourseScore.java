package com.monkey.monkeycourse.pojo;

import com.alibaba.fastjson2.util.RyuFloat;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.ws.BindingType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: wusihao
 * @description:
 * @date: 2023/7/12 17:41
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseScore {
    private Long id;
    private Long userId;
    private Long courseId;
    private Integer courseScore;
    private String commentContent;
    private String courseScoreLabelName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createTime;

    @TableField(exist = false)
    public String headImage;

    @TableField(exist = false)
    private String username;

    @TableField(exist = false)
    public List<String> labelList = new ArrayList<>();
}
