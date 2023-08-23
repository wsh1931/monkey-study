package com.monkey.monkeycourse.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeyUtils.constants.CommonConstant;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.constants.FormTypeEnum;
import com.monkey.monkeyUtils.exception.ExceptionEnum;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.redis.RedisKeyAndTimeEnum;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.util.DateUtils;
import com.monkey.monkeycourse.constant.CourseEnum;
import com.monkey.monkeycourse.feign.CourseToUserFengnService;
import com.monkey.monkeycourse.mapper.*;
import com.monkey.monkeycourse.pojo.*;
import com.monkey.monkeycourse.pojo.Vo.CourseScoreStatisticsVo;
import com.monkey.monkeycourse.service.CourseVideoPlayerService;
import com.monkey.spring_security.mapper.UserMapper;
import com.monkey.spring_security.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.monkey.monkeyUtils.util.DateSelfUtils.*;
import static com.monkey.monkeyUtils.util.DateUtils.format;

/**
 * @author: wusihao
 * @date: 2023/8/17 17:26
 * @version: 1.0
 * @description:
 */
@Service
public class CourseVideoPlayerServiceImpl implements CourseVideoPlayerService {
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private CourseVideoMapper courseVideoMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CourseStudentMapper courseStudentMapper;
    @Autowired
    private CourseVideoBarrageMapper courseVideoBarrageMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private CourseScoreMapper courseScoreMapper;

    @Autowired
    private CourseToUserFengnService courseToUserFengnService;
    /**
     * 通过课程id得到课程基本信息
     *
     * @param courseId 课程id
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/17 17:29
     */
    @Override
    public R getCourseInfoByCourseId(long courseId) {
        return R.ok(courseMapper.selectById(courseId));
    }

    /**
     * 通过课程id得到课程目录
     *
     * @param courseId 课程id
     * @param userId
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/18 9:48
     */
    @Override
    public R getCourseDirectoryByCourseId(long courseId, String userId) {
        // 得到课程目录(单位/天) 形式为 "courseDirectory: " + "courseId = {}" + "userId = {}"
        String redisKey = "courseDirectory: " + "courseId = " + courseId + " userId = " + userId;
        if (redisTemplate.hasKey(redisKey)) {
            return R.ok(redisTemplate.opsForValue().get(redisKey));
        }
        QueryWrapper<CourseVideo> courseVideoQueryWrapper = new QueryWrapper<>();
        courseVideoQueryWrapper.eq("course_id", courseId);
        courseVideoQueryWrapper.orderByAsc("sort");
        List<CourseVideo> courseVideoList = courseVideoMapper.selectList(courseVideoQueryWrapper);
        if (courseVideoList != null && courseVideoList.size() > 0) {
            courseVideoList.get(0).setIsSelected(CommonEnum.IS_SELECTED.getCode());
        }
        // 判断当前登录用户对于当前课程是否免费观看
        Course course = courseMapper.selectById(courseId);
        Long formTypeId = course.getFormTypeId();
        // 是否免费看该课程，0表示有权限，1表示收费
        Integer isAuthority = CommonEnum.IS_AUTHORITY.getCode();
        // 形式类型名
        String formTypeName = "";
        if (formTypeId.equals(FormTypeEnum.FORM_TYPE_VIP.getCode())) {
            // 会员免费课
            // 判断当前登录用户是否是会员
            formTypeName = FormTypeEnum.FORM_TYPE_VIP.getMsg();
            if (userId != null && !"".equals(userId)) {
                User user = userMapper.selectById(userId);
                if (!user.getIsVip().equals(CommonEnum.IS_VIP.getCode())) {
                    // 说明不是vip
                    isAuthority = CommonEnum.NOT_AUTHORITY.getCode();
                } else {
                    // 如果是vip判断它的vip是否过期
                    Date vipExpirationTime = user.getVipExpirationTime();
                    if (judgeNowTimeAndAssignment(vipExpirationTime)) {
                        isAuthority = CommonEnum.NOT_AUTHORITY.getCode();
                    }
                }
            } else {
                isAuthority = CommonEnum.NOT_AUTHORITY.getCode();
            }
        } else if (formTypeId.equals(FormTypeEnum.FORM_TYPE_TOLL.getCode())) {
            // 收费课
            // 判断当前用户是否交费
            formTypeName = FormTypeEnum.FORM_TYPE_TOLL.getMsg();
            if (userId != null && !"".equals(userId)) {
                QueryWrapper<CourseStudent> courseStudentQueryWrapper = new QueryWrapper<>();
                courseStudentQueryWrapper.eq("user_id", userId);
                courseStudentQueryWrapper.eq("course_id", courseId);
                Long selectCount = courseStudentMapper.selectCount(courseStudentQueryWrapper);
                if (selectCount <= 0) {
                    isAuthority = CommonEnum.NOT_AUTHORITY.getCode();
                }
            } else {
                isAuthority = CommonEnum.NOT_AUTHORITY.getCode();
            }
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isAuthority", isAuthority);
        jsonObject.put("formTypeName", formTypeName);
        jsonObject.put("courseVideoList", courseVideoList);

        redisTemplate.opsForValue().set(redisKey, jsonObject);
        redisTemplate.expire(redisKey, RedisKeyAndTimeEnum.COURSE_DIRECTORY.getTimeUnit(), TimeUnit.DAYS);
        return R.ok(jsonObject);
    }

    /**
     * 用过课程视频id得到课程弹幕列表, 并格式化发送时间
     *
     * @param userId 用户id
     * @param courseVideoId 课程视频id
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/19 14:47
     */
    @Override
    public R getBarrageListByCourseVideoId(String userId, long courseVideoId) {
        QueryWrapper<CourseVideoBarrage> courseVideoBarrageQueryWrapper = new QueryWrapper<>();
        courseVideoBarrageQueryWrapper.eq("course_video_id", courseVideoId);
        courseVideoBarrageQueryWrapper.orderByDesc("create_time");
        // 获取当年的第一天
        Calendar calendar = Calendar.getInstance();
        Date currentFirstOfYear = getCurrentFirstOfYear(calendar);
        courseVideoBarrageQueryWrapper.between("create_time", currentFirstOfYear, new Date());
        List<CourseVideoBarrage> courseVideoBarrageList = courseVideoBarrageMapper.selectList(courseVideoBarrageQueryWrapper);

        // 格式化发送时间
        for (CourseVideoBarrage courseVideoBarrage : courseVideoBarrageList) {
            Date createTime = courseVideoBarrage.getCreateTime();
            String localDate = format(createTime, DateUtils.DATE_TIME_PATTERN);
            courseVideoBarrage.setFormatCreateTime(localDate.substring(5));
            courseVideoBarrage.setFormatVideoTime(getSpecialFormatBySeconds(courseVideoBarrage.getCourseVideoTime()));
        }
        return R.ok(courseVideoBarrageList);
    }

    /**
     * 撤回2分钟内的弹幕
     *
     * @param barrageId 弹幕id
     * @param userId 当前登录用户od
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/21 16:34
     */
    @Override
    public R cancelBarrage(long barrageId, String userId) {
        try {
            CourseVideoBarrage courseVideoBarrage = courseVideoBarrageMapper.selectById(barrageId);
            Date createTime = courseVideoBarrage.getCreateTime();
            // 得到两个日期相隔的分钟数
            int senconds = Math.abs(getSecondsBetweenDates(createTime, new Date()));
            if (senconds > CommonConstant.courseBarrageCancelTime) {
                return R.error(R.Error, ExceptionEnum.COURSE_BARRAGE_EXPIRE.getMsg());
            }
            // 删除该数据
            int deleteById = courseVideoBarrageMapper.deleteById(barrageId);
            if (deleteById > 0) {
                return R.ok();
            } else {
                return R.error();
            }
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 得到课程评价信息
     *
     * @param courseId 课程id
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/22 9:50
     */
    @Override
    public R getCourseScoreInfo(long courseId) {
        // 通过课程id得到课程评分表
        QueryWrapper<CourseScore> courseScoreQueryWrapper = new QueryWrapper<>();
        courseScoreQueryWrapper.eq("course_id", courseId);
        // 最终返回类型
        CourseScoreStatisticsVo courseScoreStatisticsVo = new CourseScoreStatisticsVo();
        List<CourseScore> courseScoreList = courseScoreMapper.selectList(courseScoreQueryWrapper);
        for (CourseScore courseScore : courseScoreList) {
            int score = courseScore.getCourseScore();
            if (score == CourseEnum.EXTREME_RECOMMEND.getCode()) {
                // 特别推荐
                courseScoreStatisticsVo.setExtremeRecommend(courseScoreStatisticsVo.getExtremeRecommend() + 1);
            } else if (score == CourseEnum.PUSH_RECOMMEND.getCode()) {
                // 力推
                courseScoreStatisticsVo.setPushRecommend(courseScoreStatisticsVo.getPushRecommend() + 1);
            } else if (score == CourseEnum.RECOMMEND.getCode()) {
                // 推荐
                courseScoreStatisticsVo.setRecommend(courseScoreStatisticsVo.getRecommend() + 1);
            } else if (score == CourseEnum.MEDIUM_RECOMMEND.getCode()) {
                // 一般推荐
                courseScoreStatisticsVo.setMediumRecommend(courseScoreStatisticsVo.getMediumRecommend() + 1);
            } else if (score == CourseEnum.NOT_RECOMMEND.getCode()) {
                // 不推荐
                courseScoreStatisticsVo.setNotRecommend(courseScoreStatisticsVo.getNotRecommend() + 1);
            }
        }

        return R.ok(courseScoreStatisticsVo);
    }

    /**
     * 得到评价用户集合
     *
     * @param courseId 课程id
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/22 13:56
     */
    @Override
    public R getCourseScoreUserList(long courseId, int currentPage, int pageSize) {
        QueryWrapper<CourseScore> courseScoreQueryWrapper = new QueryWrapper<>();
        courseScoreQueryWrapper.eq("course_id", courseId);
        courseScoreQueryWrapper.orderByDesc("course_score");
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = courseScoreMapper.selectPage(page, courseScoreQueryWrapper);
        List<CourseScore> courseScoreList = (List<CourseScore>)selectPage.getRecords();
        for (CourseScore courseScore : courseScoreList) {
            Long userId = courseScore.getUserId();
            User user = userMapper.selectById(userId);
            courseScore.setHeadImage(user.getPhoto());
            courseScore.setUsername(user.getUsername());

            // 得到标签集合列表
            List<String> labelList = new ArrayList<>();
            String[] split = courseScore.getCourseScoreLabelName().split(",");
            for (String s : split) {
                labelList.add(s);
            }

            courseScore.setLabelList(labelList);
        }

        selectPage.setRecords(courseScoreList);
        return R.ok(selectPage);
    }

    /**
     * 得到课程作者基本信息并判断当前登录用户是否关注他
     *
     * @param userId 文章作者id
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/22 15:25
     */
    @Override
    public R getUserInfo(long userId) {
        User user = userMapper.selectById(userId);
        return R.ok(user);
    }

    /**
     * 判断当前登录用户是否是作者粉丝
     *
     * @param userId 作者id
     * @param nowUserId 当前用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/22 16:18
     */
    @Override
    public R judgeIsFans(long userId, String nowUserId) {
        if (nowUserId != null && !"".equals(nowUserId)) {
            R r = courseToUserFengnService.judgeLoginUserAndAuthorConnect(userId, Long.parseLong(nowUserId));
            if (r.getCode() != R.SUCCESS) {
                throw new MonkeyBlogException(r.getCode(), r.getMsg());
            }

            return R.ok((int)r.getData(new TypeReference<Integer>(){}));
        } else {
            return R.ok(CommonEnum.NOT_FANS.getCode());
        }
    }

    /**
     * 得到最热课程列表
     *
     * @param courseId 课程id
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/22 17:06
     */
    @Override
    public R getFireCourseList(long courseId) {
        QueryWrapper<Course> courseQueryWrapper = new QueryWrapper<>();
        courseQueryWrapper.orderByDesc("view_count");
        courseQueryWrapper.orderByDesc("study_count");
        courseQueryWrapper.orderByDesc("collect_count");
        courseQueryWrapper.orderByDesc("comment_count");
        courseQueryWrapper.orderByDesc("barrage_count");
        List<Course> courseList = courseMapper.selectList(courseQueryWrapper);
        for (Course course : courseList) {
            Long userId = course.getUserId();
            User user = userMapper.selectById(userId);
            course.setUsername(user.getUsername());
        }
        return R.ok(courseList);
    }

    /**
     * 得到该作者的其他课程
     *
     * @param teacherId 教师id
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/22 17:14
     */
    @Override
    public R getTeacherOtherCourse(long teacherId) {
        QueryWrapper<Course> courseQueryWrapper = new QueryWrapper<>();
        courseQueryWrapper.eq("user_id", teacherId);
        courseQueryWrapper.orderByDesc("update_time");
        List<Course> courseList = courseMapper.selectList(courseQueryWrapper);
        for (Course course : courseList) {
            Long userId = course.getUserId();
            User user = userMapper.selectById(userId);
            course.setUsername(user.getUsername());
        }
        return R.ok(courseList);
    }
}
