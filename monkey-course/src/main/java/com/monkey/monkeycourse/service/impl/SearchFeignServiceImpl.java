package com.monkey.monkeycourse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.constants.FormTypeEnum;
import com.monkey.monkeyUtils.mapper.LabelMapper;
import com.monkey.monkeyUtils.pojo.Label;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycourse.mapper.CourseLabelMapper;
import com.monkey.monkeycourse.mapper.CourseMapper;
import com.monkey.monkeycourse.pojo.Course;
import com.monkey.monkeycourse.pojo.CourseLabel;
import com.monkey.monkeycourse.service.SearchFeignService;
import com.monkey.spring_security.mapper.UserMapper;
import com.monkey.spring_security.pojo.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: wusihao
 * @date: 2023/11/11 10:57
 * @version: 1.0
 * @description:
 */
@Service
public class SearchFeignServiceImpl implements SearchFeignService {
    @Resource
    private CourseMapper courseMapper;
    @Resource
    private CourseLabelMapper courseLabelMapper;
    @Resource
    private LabelMapper labelMapper;
    @Resource
    private UserMapper userMapper;
    /**
     * 查询所有课程
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/11 11:00
     */
    @Override
    public R queryAllCourse() {
        List<Course> courseList = courseMapper.selectList(null);
        courseList.parallelStream().forEach(course -> {
            // 得到课程标签
            Long courseId = course.getId();
            QueryWrapper<CourseLabel> courseLabelQueryWrapper = new QueryWrapper<>();
            courseLabelQueryWrapper.eq("course_id", courseId);
            courseLabelQueryWrapper.select("label_id");
            List<Object> labelIdList = courseLabelMapper.selectObjs(courseLabelQueryWrapper);
            if (labelIdList != null && labelIdList.size() > 0) {
                QueryWrapper<Label> labelQueryWrapper = new QueryWrapper<>();
                labelQueryWrapper.in("id", labelIdList);
                List<Label> labelList = labelMapper.selectList(labelQueryWrapper);
                List<String> labelName = new ArrayList<>(labelList.size());
                for (Label label : labelList) {
                    labelName.add(label.getLabelName());
                }

                course.setLabelName(labelName);
            }

            // 得到课程作者信息
            Long userId = course.getUserId();
            User user = userMapper.selectById(userId);
            course.setUsername(user.getUsername());
            course.setUserBrief(user.getBrief());
            course.setUserHeadImg(user.getPhoto());

            // 得到形式类型名称
            Long formTypeId = course.getFormTypeId();
            FormTypeEnum formTypeEnum = FormTypeEnum.getFormTypeEnum(formTypeId);
            course.setFormTypeName(formTypeEnum.getMsg());
        });

        return R.ok(courseList);
    }

    /**
     * 得到所有用户所有课程，点赞，收藏，游览数
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/14 10:56
     */
    @Override
    public R queryAllUserCourseInfo() {
        QueryWrapper<Course> articleQueryWrapper = new QueryWrapper<>();
        articleQueryWrapper.groupBy("user_id");
        articleQueryWrapper.select("user_id",
                "sum(collect_count) as collectCount",
                "sum(view_count) as viewCount",
                "count(*) as opusCount"
        );
        List<Map<String, Object>> maps = courseMapper.selectMaps(articleQueryWrapper);
        return R.ok(maps);
    }
}
