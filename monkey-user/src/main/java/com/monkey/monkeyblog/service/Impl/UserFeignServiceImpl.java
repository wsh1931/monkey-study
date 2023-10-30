package com.monkey.monkeyblog.service.Impl;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.exception.ExceptionEnum;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.pojo.vo.UserFansVo;
import com.monkey.monkeyUtils.pojo.vo.UserVo;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyblog.mapper.UserFansMapper;
import com.monkey.monkeyblog.pojo.UserFans;
import com.monkey.monkeyblog.rabbitmq.EventConstant;
import com.monkey.monkeyblog.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeyblog.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeyblog.service.UserFeignService;
import com.monkey.spring_security.mapper.UserMapper;
import com.monkey.spring_security.pojo.User;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/7/20 15:45
 * @version: 1.0
 * @description:
 */
@Service
public class UserFeignServiceImpl implements UserFeignService {
    @Resource
    private UserFansMapper userFansMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;

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
            // 插入关注消息表
            Long recipientId = userFansVo.getUserId();
            Long senderId = userFansVo.getFansId();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("event", EventConstant.insertConcernMessage);
            jsonObject.put("recipientId", recipientId);
            jsonObject.put("senderId", senderId);
            Message message = new Message(jsonObject.toJSONString().getBytes());
            rabbitTemplate.convertAndSend(RabbitmqExchangeName.userInsertDirectExchange,
                    RabbitmqRoutingName.userInsertRouting, message);
            return R.ok(insert);
        }

        throw new MonkeyBlogException(ExceptionEnum.Delete_USERFANS_FAIL.getCode(), ExceptionEnum.ADD_USERFANS_FAIL.getMsg());
    }

    /**
     * 通过用户id得到用户关注数和粉丝数
     *
     * @param userId 用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/13 21:11
     */
    @Override
    public R getUserConcernAndFansCountByUserId(Long userId) {
        UserVo userVo = new UserVo();
        QueryWrapper<UserFans> userFansQueryWrapper = new QueryWrapper<>();
        userFansQueryWrapper.eq("fans_id", userId);
        userVo.setFans(userFansMapper.selectCount(userFansQueryWrapper));

        QueryWrapper<UserFans> fansQueryWrapper = new QueryWrapper<>();
        fansQueryWrapper.eq("user_id", userId);
        userVo.setConcern(userFansMapper.selectCount(fansQueryWrapper));
        return R.ok(userVo);
    }
}
