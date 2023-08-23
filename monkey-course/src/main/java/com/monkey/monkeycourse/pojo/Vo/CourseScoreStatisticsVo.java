package com.monkey.monkeycourse.pojo.Vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: wusihao
 * @date: 2023/8/22 13:59
 * @version: 1.0
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseScoreStatisticsVo {

    // 特别推荐人数
    @TableField(exist = false)
    private Long extremeRecommend = 0L;

    // 力推人数
    @TableField(exist = false)
    private Long pushRecommend = 0L;

    // 推荐人数
    @TableField(exist = false)
    private Long recommend = 0L;

    // 一般推荐
    @TableField(exist = false)
    private Long mediumRecommend = 0L;

    // 不推荐
    @TableField(exist = false)
    private Long notRecommend = 0L;
}
