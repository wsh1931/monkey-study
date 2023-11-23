package com.monkey.monkeyblog.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.mapper.CollectContentConnectMapper;
import com.monkey.monkeyUtils.pojo.CollectContentConnect;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.result.ResultStatus;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyUtils.mapper.LabelMapper;
import com.monkey.monkeyblog.feign.UserToArticleFeignService;
import com.monkey.monkeyblog.feign.UserToQuestionFeignService;
import com.monkey.monkeyblog.mapper.UserFansMapper;
import com.monkey.monkeyUtils.pojo.Label;
import com.monkey.monkeyblog.pojo.UserFans;
import com.monkey.monkeyUtils.pojo.vo.UserVo;
import com.monkey.monkeyblog.mapper.RecentVisitUserhomeMapper;
import com.monkey.monkeyblog.pojo.Vo.*;
import com.monkey.monkeyblog.pojo.RecentVisitUserhome;
import com.monkey.monkeyblog.rabbitmq.EventConstant;
import com.monkey.monkeyblog.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeyblog.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeyblog.service.UserHomeService;

import com.monkey.monkeyUtils.mapper.UserMapper;
import com.monkey.monkeyUtils.pojo.User;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class UserHomeServiceImpl implements UserHomeService {
    @Resource
    private UserMapper userMapper;
    /**
     * 查询用户信息
     *
     * @param userId 用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/23 21:36
     */
    @Override
    public R queryUserAchievement(Long userId) {
        return R.ok(userMapper.selectById(userId));
    }
}
