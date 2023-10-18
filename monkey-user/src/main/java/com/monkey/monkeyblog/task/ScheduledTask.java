package com.monkey.monkeyblog.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.spring_security.mapper.UserMapper;
import com.monkey.spring_security.pojo.User;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.monkey.monkeyUtils.util.DateSelfUtils.judgeNowTimeAndAssignment;

/**
 * @author: wusihao
 * @date: 2023/10/12 15:06
 * @version: 1.0
 * @description: 定时任务
 */
@Component
public class ScheduledTask {
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private UserMapper userMapper;
    /**
     * 判断用户VIP是否过期
     * 每隔 5 分钟执行
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/25 11:22
     */
    @Scheduled(fixedRate = 300000)
    public void judgeUserVipIsExpire() {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("is_vip", CommonEnum.IS_VIP.getCode());
        List<User> userList = userMapper.selectList(userQueryWrapper);
        List<Long> userIdList = new ArrayList<>();
        for (User user : userList) {
            Date vipExpirationTime = user.getVipExpirationTime();
            if (!judgeNowTimeAndAssignment(vipExpirationTime)) {
                // 说明用户vip已过期, 记录已过期用户vip集合
                userIdList.add(user.getId());
            }
        }

        // 更新用户信息，将用户VIP设置成已过期
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", userIdList);
        updateWrapper.set("is_vip", CommonEnum.NOT_VIP.getCode());
        userMapper.update(null, updateWrapper);
    }
}
