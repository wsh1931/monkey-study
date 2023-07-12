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
 * @date: 2023/7/12 17:32
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoursePrice {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long courseId;
    private Float coursePrice;
    private Float discount;
}
