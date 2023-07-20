package com.monkey.monkeyblog.service.Impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.constants.ExceptionEnum;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.pojo.Vo.UserFansVo;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyblog.mapper.UserFansMapper;
import com.monkey.monkeyblog.pojo.UserFans;
import com.monkey.monkeyblog.service.UserFeignService;
import com.monkey.spring_security.mapper.UserMapper;
import com.monkey.spring_security.pojo.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: wusihao
 * @date: 2023/7/20 15:45
 * @version: 1.0
 * @description:
 */
@Service
public class UserFeignServiceImpl implements UserFeignService {
    @Autowired
    private UserFansMapper userFansMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public R judgeLoginUserAndAuthorConnect(long userId, long fansId) {
        // 判断发送者是否关注了接收者
        QueryWrapper<UserFans> userFansQueryWrapper = new QueryWrapper<>();
        userFansQueryWrapper.eq("fans_id", fansId);
        userFansQueryWrapper.eq("user_id", userId);
        Long selectCount = userFansMapper.selectCount(userFansQueryWrapper);
        return R.ok(selectCount);
    }

    // 通过用户id得到用户信息
    @Override
    public R getUserInfoByUserId(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new MonkeyBlogException(ExceptionEnum.USER_NOT_EXIST.getCode(), ExceptionEnum.USER_NOT_EXIST.getMsg());
        }
        return R.ok(user);
    }

    // 得到userFans通过userId, 和fansId
    @Override
    public R getUserFansByUserAndAuthorConnect(Long userId, Long fansId) {
        // 判断发送者是否关注了接收者
        QueryWrapper<UserFans> userFansQueryWrapper = new QueryWrapper<>();
        userFansQueryWrapper.eq("fans_id", fansId);
        userFansQueryWrapper.eq("user_id", userId);
        UserFans userFans = userFansMapper.selectOne(userFansQueryWrapper);
        return R.ok(userFans);
    }

    // 通过id删除userFans
    @Override
    public R deleteUserFansById(Long userFansId) {
        int deleteById = userFansMapper.deleteById(userFansId);
        if (deleteById > 0) {
            return R.ok(deleteById);
        }
        throw new MonkeyBlogException(ExceptionEnum.Delete_USERFANS_FAIL.getCode(), ExceptionEnum.Delete_USERFANS_FAIL.getMsg());
    }

    // 插入userFans
    @Override
    public R addUserFans(UserFansVo userFansVo) {
        UserFans userFans = new UserFans();
        BeanUtils.copyProperties(userFansVo, userFans);
        int insert = userFansMapper.insert(userFans);
        if (insert > 0) {
            return R.ok(insert);
        }
        throw new MonkeyBlogException(ExceptionEnum.Delete_USERFANS_FAIL.getCode(), ExceptionEnum.ADD_USERFANS_FAIL.getMsg());
    }
}
