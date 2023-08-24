package com.monkey.monkeycourse.service.impl;

import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycourse.mapper.CourseMapper;
import com.monkey.monkeycourse.pojo.Course;
import com.monkey.monkeycourse.service.CourseBuyService;
import com.monkey.spring_security.mapper.UserMapper;
import com.monkey.spring_security.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: wusihao
 * @date: 2023/8/23 15:06
 * @version: 1.0
 * @description:
 */
@Service
public class CourseBuyServiceImpl implements CourseBuyService {
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 通过课程id得到课程信息
     *
     * @param courseId 课程id
     * @param userId 当前登录用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/23 15:16
     */
    @Override
    public R getCourseInfoByCourseId(long courseId, long userId) {
        Course course = courseMapper.selectById(courseId);
        Float price = course.getCoursePrice();
        Float discount = course.getDiscount();
        if (discount != null) {
//                    courseCardVo.setPrice(String.valueOf(price * discount * 0.1));
            // 截取小数点后两位
            String str = String.valueOf(price * discount * 0.1);
            int index = str.indexOf('.');
            if (index != -1 && index + 3 <= str.length()) {
                course.setDiscountPrice(str.substring(0, index + 3));
            } else {
                course.setDiscountPrice(str);
            }
        } else {
            course.setDiscountPrice(String.valueOf(price));
        }

        // 判断该用户是否注册QQ邮箱
        User user = userMapper.selectById(userId);
        String email = user.getEmail();
        if (email == null || "".equals(email)) {
            course.setHasEmail(CommonEnum.NOT_REGISTER_EMAIL.getCode());
        } else {
            course.setEmail(email);
            course.setHasEmail(CommonEnum.ALREADY_REGISTER_EMAIL.getCode());
        }

        return R.ok(course);
    }
}
