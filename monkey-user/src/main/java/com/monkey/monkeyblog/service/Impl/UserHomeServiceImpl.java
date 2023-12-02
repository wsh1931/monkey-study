package com.monkey.monkeyblog.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.mapper.CollectContentConnectMapper;
import com.monkey.monkeyUtils.pojo.CollectContentConnect;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.result.ResultStatus;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyUtils.mapper.LabelMapper;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
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
import com.monkey.monkeyblog.util.UserCommonMethods;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserHomeServiceImpl implements UserHomeService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserFansMapper userFansMapper;
    @Resource
    private RecentVisitUserhomeMapper recentVisitUserhomeMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;
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
        User user = userMapper.selectById(userId);
        String nowUserId = JwtUtil.getUserId();
        if (nowUserId != null && !"".equals(nowUserId)) {
            // 判断当前登录用户是否关注
            int isFans = UserCommonMethods.judgeIsFans(userId, nowUserId, userFansMapper);
            user.setIsFans(isFans);
        }

        return R.ok(user);
    }

    /**
     * 查询最近访客列表
     *
     * @param userId 访问的作者id
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/2 9:34
     */
    @Override
    public R queryLatestVisit(String userId) {
        QueryWrapper<RecentVisitUserhome> recentVisitUserhomeQueryWrapper = new QueryWrapper<>();
        recentVisitUserhomeQueryWrapper.eq("be_visit_id", userId);
        recentVisitUserhomeQueryWrapper.groupBy("visit_id");
        recentVisitUserhomeQueryWrapper.select("max(create_Time) as createTime", "visit_id");
        recentVisitUserhomeQueryWrapper.orderByDesc("createTime");
        recentVisitUserhomeQueryWrapper.last("limit 15");

        List<RecentVisitUserhome> recentVisitUserhomes = recentVisitUserhomeMapper.selectList(recentVisitUserhomeQueryWrapper);
        Map<Long, Date> map = new HashMap<>();

        if (recentVisitUserhomes != null && recentVisitUserhomes.size() > 0) {
            // 保存每个访问用户所对应的访问时间
            recentVisitUserhomes.forEach(recentVisitUserhome -> {
                map.put(recentVisitUserhome.getVisitId(), recentVisitUserhome.getCreateTime());
            });

            List<Long> visitUserIdList = recentVisitUserhomes
                    .stream()
                    .mapToLong(m -> m.getVisitId()).boxed().collect(Collectors.toList());
                // 得到所有访问用户信息
                LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
                userLambdaQueryWrapper.in(User::getId, visitUserIdList);
                userLambdaQueryWrapper.select(User::getId, User::getUsername, User::getPhoto, User::getRegisterTime, User::getId);
                List<User> userList = userMapper.selectList(userLambdaQueryWrapper);
                // 更新每个用户所对应的访问时间
                userList.forEach(user -> {
                    user.setRegisterTime(map.get(user.getId()));
                });

                // 上面查出的用户不是按userId的顺序来查询的，所以要再次排序
                userList.sort((a, b) -> b.getRegisterTime().compareTo(a.getRegisterTime()));
                return R.ok(userList);
        }

        return R.ok();
    }

    /**
     * 将用户插入最近访问表
     *
     * @param userId 被访问的用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/2 10:46
     */
    @Override
    public R addToRecentUserVisit(String userId) {
        String nowUserId = JwtUtil.getUserId();
        if (nowUserId == null || "".equals(nowUserId)) {
            return R.ok();
        }

        if (userId == null || "".equals(userId)) {
            return R.ok();
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.insertUserRecentlyView);
        jsonObject.put("reviewId", nowUserId);
        jsonObject.put("userId", userId);
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.userInsertDirectExchange,
                RabbitmqRoutingName.userInsertRouting, message);
        return R.ok();
    }
}
