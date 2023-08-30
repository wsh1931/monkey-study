package com.monkey.monkeycourse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycourse.mapper.CourseEvaluateLabelMapper;
import com.monkey.monkeycourse.mapper.CourseEvaluateMapper;
import com.monkey.monkeycourse.pojo.CourseEvaluate;
import com.monkey.monkeycourse.pojo.CourseEvaluateLabel;
import com.monkey.monkeycourse.service.CourseEvaluateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/8/30 11:03
 * @version: 1.0
 * @description:
 */
@Service
public class CourseEvaluateServiceImpl implements CourseEvaluateService {
    @Autowired
    private CourseEvaluateLabelMapper courseEvaluateLabelMapper;
    @Autowired
    private CourseEvaluateMapper courseEvaluateMapper;
    /**
     * 得到评价标签列表
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/30 11:06
     */
    @Override
    public R queryEvaluateLabelList() {
        QueryWrapper<CourseEvaluateLabel> courseEvaluateLabelQueryWrapper = new QueryWrapper<>();
        courseEvaluateLabelQueryWrapper.orderByAsc("sort");
        courseEvaluateLabelQueryWrapper.orderByDesc("create_time");
        List<CourseEvaluateLabel> courseEvaluateLabelList = courseEvaluateLabelMapper.selectList(courseEvaluateLabelQueryWrapper);
        return R.ok(courseEvaluateLabelList);
    }

    /**
     * 提交评价
     *
     * @param userId  当前登录用户id
     * @param courseId 课程id
     * @param score 课程评分
     * @param evaluateContent 课程评论内容
     * @param selectedTags 选中的标签
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/30 11:44
     */
    @Override
    public R submitEvaluate(String userId, long courseId, int score, String evaluateContent, List<String> selectedTags) {
        StringBuilder tag = new StringBuilder();
        int len = selectedTags.size();
        for (int i = 0; i < len; i ++ ) {
            if (i == len - 1) {
                tag.append(selectedTags.get(i));
            } else {
                tag.append(selectedTags.get(i)).append(",");
            }
        }
        CourseEvaluate courseEvaluate = new CourseEvaluate();
        courseEvaluate.setCourseId(courseId);
        courseEvaluate.setCourseScoreLabelName(tag.toString());
        courseEvaluate.setUserId(Long.parseLong(userId));
        courseEvaluate.setCommentContent(evaluateContent);
        courseEvaluate.setCourseScore(score);
        courseEvaluate.setCreateTime(new Date());

        courseEvaluateMapper.insert(courseEvaluate);
        return R.ok();
    }
}
