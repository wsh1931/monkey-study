package com.monkey.monkeycourse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeycourse.constant.FormTypeEnum;
import com.monkey.monkeyUtils.mapper.LabelMapper;
import com.monkey.monkeyUtils.pojo.Label;
import com.monkey.monkeyUtils.pojo.LabelVo;
import com.monkey.monkeyUtils.redis.RedisKeyAndTimeEnum;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.result.ResultStatus;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeycourse.constant.CourseEnum;
import com.monkey.monkeycourse.mapper.*;
import com.monkey.monkeycourse.pojo.*;
import com.monkey.monkeycourse.pojo.Vo.CourseCardVo;
import com.monkey.monkeycourse.service.CourseService;
import com.monkey.spring_security.mapper.UserMapper;
import com.monkey.spring_security.pojo.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: wusihao
 * @date: 2023/7/15 20:44
 * @version: 1.0
 * @description:
 */
@Service
public class CourseServiceImpl implements CourseService {
    @Autowired
    private LabelMapper labelMapper;
    @Autowired
    private CourseLabelMapper courseLabelMapper;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private FormTypeMapper formTypeMapper;

    @Autowired
    private CourseVideoMapper courseVideoMapper;

    @Autowired
    private CourseStudentMapper courseStudentMapper;
    @Autowired
    private UserMapper userMapper;

    // 得到一级标签列表
    @Override
    public ResultVO getOneLabelList() {
        String redisKey = RedisKeyAndTimeEnum.COURSE_ONE_LABEL_List.getKeyName();
        Integer timeUnit = RedisKeyAndTimeEnum.COURSE_ONE_LABEL_List.getTimeUnit();
        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
            return new ResultVO(ResultStatus.OK, null, redisTemplate.opsForList().range(redisKey, 0, -1));
        }
        QueryWrapper<Label> labelQueryWrapper = new QueryWrapper<>();
        labelQueryWrapper.eq("level", CommonEnum.LABEL_LEVEL_ONE.getCode());
        List<Label> labelList = labelMapper.selectList(labelQueryWrapper);
        List<LabelVo> labelVoList = new ArrayList<>();
        for (Label label : labelList) {
            LabelVo labelVo = new LabelVo();
            BeanUtils.copyProperties(label, labelVo);
            labelVo.setSelected(false);
            labelVoList.add(labelVo);
        }

        LabelVo labelVo = new LabelVo();
        labelVo.setId(-1L);
        labelVo.setLabelName("全部");
        labelVoList.add(0, labelVo);

        if (labelVoList != null && labelVoList.size() > 0) {
            redisTemplate.opsForList().rightPushAll(redisKey, labelVoList);
            redisTemplate.expire(redisKey, timeUnit, TimeUnit.HOURS);
            return new ResultVO(ResultStatus.OK, null, labelVoList);
        }
        return new ResultVO(ResultStatus.OK, null, "");
    }

    // 通过一级标签id得到二级标签
    @Override
    public ResultVO getTwoLabelListByOneLabelId(Long oneLabelId) {
        if (oneLabelId == -1) {
            return new ResultVO(ResultStatus.OK, null, "");
        }
        String redisKey = RedisKeyAndTimeEnum.COURSE_TWO_LABEL_List.getKeyName() + oneLabelId;
        Integer timeUnit = RedisKeyAndTimeEnum.COURSE_TWO_LABEL_List.getTimeUnit();
        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
            return new ResultVO(ResultStatus.OK, null, redisTemplate.opsForList().range(redisKey, 0, -1));
        }
        QueryWrapper<Label> labelQueryWrapper = new QueryWrapper<>();
        labelQueryWrapper.eq("parent_id", oneLabelId);
        labelQueryWrapper.eq("level", CommonEnum.LABEL_LEVEL_TWO.getCode());
        List<Label> labelList = labelMapper.selectList(labelQueryWrapper);

        List<LabelVo> labelVoList = new ArrayList<>();
        for (Label label : labelList) {
            LabelVo labelVo = new LabelVo();
            BeanUtils.copyProperties(label, labelVo);
            labelVo.setSelected(false);
            labelVoList.add(labelVo);
        }

        LabelVo labelVo = new LabelVo();
        labelVo.setId(-1L);
        labelVo.setLabelName("全部");
        labelVoList.add(0, labelVo);

        if (labelVoList != null && labelVoList.size() > 0) {
            redisTemplate.opsForList().rightPushAll(redisKey, labelVoList);
            redisTemplate.expire(redisKey, timeUnit, TimeUnit.HOURS);
            return new ResultVO(ResultStatus.OK, null, labelVoList);
        }
        return new ResultVO(ResultStatus.OK, null, "");
    }

    // 通过二级标签分页查询文章列表
    @Override
    public ResultVO getCourseListByTwoLabelId(long formTypeId, long twoLabelId, long currentPage, long pageSize) {

        QueryWrapper<CourseLabel> courseLabelQueryWrapper  = new QueryWrapper<>();
        courseLabelQueryWrapper.eq("label_id", twoLabelId);
        List<CourseLabel> courseLabelList = courseLabelMapper.selectList(courseLabelQueryWrapper);
        // 判断形式类型是否符合
        List<Long> courseIdList = new ArrayList<>();
        for (CourseLabel courseLabel : courseLabelList) {
            Long courseId = courseLabel.getCourseId();
            Course course = courseMapper.selectById(courseId);
            if (!course.getFormTypeId().equals(formTypeId)) {
                continue;
            }
            courseIdList.add(course.getId());
        }

        if (courseLabelList.size() > 0) {
            QueryWrapper<Course> courseQueryWrapper = new QueryWrapper<>();
            courseQueryWrapper.in("id", courseIdList);
            courseQueryWrapper.orderByDesc("create_time");
            Page page = new Page<>(currentPage, pageSize);
            Page<Course> selectPage = courseMapper.selectPage(page, courseQueryWrapper);
            return new ResultVO(ResultStatus.OK, null, selectPage);
        }

        return new ResultVO(ResultStatus.OK, null, "");
    }

    // 得到形式类型集合
    @Override
    public R getFormTypeList() {
        QueryWrapper<FormType> formTypeQueryWrapper = new QueryWrapper<>();
        formTypeQueryWrapper.orderByAsc("sort");
        List<FormType> formTypeList = formTypeMapper.selectList(formTypeQueryWrapper);
        return R.ok(formTypeList);
    }

    /**
     * 得到课程列表
     *
     * @param formTypeId 形式id
     * @param oneLabelId 一级标签id
     * @param twoLabelId 二级标签id
     * @param currentPage 当前页
     * @param pageSize 页面大小
     * @return {@link null}
     * @author wusihao
     * @date 2023/7/28 10:51
     */
    public List<CourseCardVo> getCourseList(long formTypeId, long oneLabelId, long twoLabelId, long currentPage, long pageSize) {
        QueryWrapper<Course> courseQueryWrapper = new QueryWrapper<>();
        List<Course> courseList = new ArrayList<>();
        // 若一级标签类型为全部
        if (oneLabelId == -1) {
            courseQueryWrapper.eq(formTypeId != FormTypeEnum.FORM_TYPE_ALL.getCode(), "form_type_id", formTypeId);
            courseList = courseMapper.selectList(courseQueryWrapper);
        } else {
            // 若二级标签类型为全部
            if (twoLabelId == -1) {
                // 查询有关一标签下的所有文章
                QueryWrapper<Label> labelQueryWrapper = new QueryWrapper<>();
                labelQueryWrapper.eq("parent_id", oneLabelId);
                List<Label> labelList = labelMapper.selectList(labelQueryWrapper);
                for (Label label : labelList) {
                    // 通过标签id得到课程id
                    Long labelId = label.getId();
                    QueryWrapper<CourseLabel> courseLabelQueryWrapper = new QueryWrapper<>();
                    courseLabelQueryWrapper.eq("label_id", labelId);
                    List<CourseLabel> courseLabelList = courseLabelMapper.selectList(courseLabelQueryWrapper);
                    for (CourseLabel courseLabel : courseLabelList) {
                        Long courseId = courseLabel.getCourseId();
                        Course course = courseMapper.selectById(courseId);
                        // 若形式相同
                        if (formTypeId == FormTypeEnum.FORM_TYPE_ALL.getCode()) {
                            courseList.add(course);
                        } else if (course.getFormTypeId().equals(formTypeId)) {
                            courseList.add(course);
                        }
                    }
                }
            } else {
                // 说明二级标签不为全部，找到该课程所对应的标签
                QueryWrapper<CourseLabel> courseLabelQueryWrapper = new QueryWrapper<>();
                courseLabelQueryWrapper.eq("label_id", twoLabelId);
                List<CourseLabel> courseLabelList = courseLabelMapper.selectList(courseLabelQueryWrapper);
                for (CourseLabel courseLabel : courseLabelList) {
                    Long courseId = courseLabel.getCourseId();
                    Course course = courseMapper.selectById(courseId);
                    // 若形式相同
                    if (formTypeId == FormTypeEnum.FORM_TYPE_ALL.getCode()) {
                        courseList.add(course);
                    } else if (course.getFormTypeId().equals(formTypeId)) {
                        courseList.add(course);
                    }

                }
            }
        }

        // 将课程列表转化为vo类返回
        List<CourseCardVo> courseCardVoList = new ArrayList<>();
        for (Course course : courseList) {
            CourseCardVo courseCardVo = new CourseCardVo();
            Long courseId = course.getId();
            // 得到课程基本信息
            courseCardVo.setCreateTime(course.getCreateTime());
            courseCardVo.setPicture(course.getPicture());
            courseCardVo.setId(course.getId());
            courseCardVo.setTitle(course.getTitle());
            Long courseFormTypeId = course.getFormTypeId();
            FormTypeEnum formTypeEnum = FormTypeEnum.getFormTypeEnum(courseFormTypeId);
            courseCardVo.setCourseFormType(formTypeEnum.getMsg());

            // 得到课程价格
            if (formTypeId != FormTypeEnum.FORM_TYPE_FREE.getCode()) {
                courseCardVo.setIsFree(CourseEnum.COURSE_UNFREE.getCode());
                Float price = course.getCoursePrice();
                Float discount = course.getDiscount();
                if (discount != null) {
//                    courseCardVo.setPrice(String.valueOf(price * discount * 0.1));
                    // 截取小数点后两位
                    String str = String.valueOf(price * discount * 0.1);
                    int index = str.indexOf('.');
                    if (index != -1 && index + 3 <= str.length()) {
                        courseCardVo.setPrice(str.substring(0, index + 3));
                    } else {
                        courseCardVo.setPrice(str);
                    }
                } else {
                    courseCardVo.setPrice(String.valueOf(price));
                }
            } else {
                courseCardVo.setIsFree(CourseEnum.COURSE_FREE.getCode());
                courseCardVo.setPrice(String.valueOf(-1));
            }


            // 得到用户简介
            Long userId = course.getUserId();
            User user = userMapper.selectById(userId);
            courseCardVo.setUserProfile(user.getBrief());

            // 得到课程小节总数
            QueryWrapper<CourseVideo> courseVideoQueryWrapper = new QueryWrapper<>();
            courseVideoQueryWrapper.eq("course_id", courseId);
            courseCardVo.setSum(courseVideoMapper.selectCount(courseVideoQueryWrapper));

            // 得到课程学习人数
            QueryWrapper<CourseStudent> courseStudentQueryWrapper = new QueryWrapper<>();
            courseStudentQueryWrapper.eq("course_id", courseId);
            courseCardVo.setStudySum(courseStudentMapper.selectCount(courseStudentQueryWrapper));

            courseCardVoList.add(courseCardVo);
        }

        // 进行分页
        // 分页后的数据
        List<CourseCardVo> res = new ArrayList<>();
        int len = courseCardVoList.size();
        long page = (currentPage - 1) * pageSize;
        for (long i = page; i < len && i < page + pageSize; i ++ ) {
            res.add(courseCardVoList.get((int)i));
        }

        return res;
    }

    /**
     * 通过形式id和一级标签id, 二级标签id得到热门课程列表
     *
     * @param formTypeId 形式id
     * @param oneLabelId 一级标签id
     * @param twoLabelId 二级标签id
     * @param currentPage 当前页
     * @param pageSize 页面大小
     * @return {@link null}
     * @author wusihao
     * @date 2023/7/27 11:15
     */
    @Override
    public R getFireCourseListByOneLabelAndTowLabelAndFormId(long formTypeId, long oneLabelId, long twoLabelId, long currentPage, long pageSize) {
        List<CourseCardVo> courseList = getCourseList(formTypeId, oneLabelId, twoLabelId, currentPage, pageSize);
        // 按学习人数降序排序
        courseList.sort((a, b) -> Long.compare(b.getStudySum(), a.getStudySum()));
        return R.ok(courseList);
    }

    /**
     * 通过形式id和一级标签id, 二级标签id得到所有最新课程列表
     *
     * @param formTypeId 形式id
     * @param oneLabelId 一级标签id
     * @param twoLabelId 二级标签id
     * @param currentPage 当前页
     * @param pageSize 页面大小
     * @return {@link null}
     * @author wusihao
     * @date 2023/7/28 10:53
     */
    @Override
    public R getLastlyCourseListByOneLabelAndTowLabelAndFormId(long formTypeId, long oneLabelId, long twoLabelId, long currentPage, long pageSize) {
        List<CourseCardVo> courseList = getCourseList(formTypeId, oneLabelId, twoLabelId, currentPage, pageSize);
        // 通过发布课程时间降序
        courseList.sort((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()));
        return R.ok(courseList);
    }

    /**
     * 通过形式id和一级标签id, 二级标签id得到升序价格列表
     *
     * @param formTypeId 形式id
     * @param oneLabelId 一级标签id
     * @param twoLabelId 二级标签id
     * @param currentPage 当前页
     * @param pageSize 页面大小
     * @return {@link null}
     * @author wusihao
     * @date 2023/7/28 10:54
     */
    @Override
    public R getAscPriceCourseListByOneLabelAndTowLabelAndFormId(long formTypeId, long oneLabelId, long twoLabelId, long currentPage, long pageSize) {
        List<CourseCardVo> courseList = getCourseList(formTypeId, oneLabelId, twoLabelId, currentPage, pageSize);
        // 通过发布课程价格升序
        courseList.sort(Comparator.comparing(CourseCardVo::getPrice));
        return R.ok(courseList);
    }

    /**
     *  通过形式id和一级标签id, 二级标签id得到降序价格列表
     *
     * @param formTypeId 形式id
     * @param oneLabelId 一级标签id
     * @param twoLabelId 二级标签id
     * @param currentPage 当前页
     * @param pageSize 页面大小
     * @return {@link null}
     * @author wusihao
     * @date 2023/7/28 10:54
     */
    @Override
    public R getDescPriceCourseListByOneLabelAndTowLabelAndFormId(long formTypeId, long oneLabelId, long twoLabelId, long currentPage, long pageSize) {
        List<CourseCardVo> courseList = getCourseList(formTypeId, oneLabelId, twoLabelId, currentPage, pageSize);
        // 通过发布课程价格降序
        courseList.sort(Comparator.comparing(CourseCardVo::getPrice).reversed());
        return R.ok(courseList);
    }
}
