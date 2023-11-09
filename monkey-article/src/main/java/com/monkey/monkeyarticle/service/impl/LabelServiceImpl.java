package com.monkey.monkeyarticle.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.redis.RedisKeyAndTimeEnum;
import com.monkey.monkeyUtils.result.ResultStatus;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyUtils.mapper.LabelMapper;
import com.monkey.monkeyUtils.pojo.Label;
import com.monkey.monkeyarticle.service.LabelService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class LabelServiceImpl implements LabelService {
    @Resource
    private LabelMapper labelMapper;
    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public ResultVO getLabelList() {
        String redisKey = RedisKeyAndTimeEnum.ARTICLE_LABEL_LIST.getKeyName();
        Integer timeUnit = RedisKeyAndTimeEnum.ARTICLE_LABEL_LIST.getTimeUnit();
        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
            return new ResultVO(ResultStatus.OK, null, redisTemplate.opsForList().range(redisKey, 0, -1));
        } else {
            QueryWrapper<Label> labelQueryWrapper = new QueryWrapper<>();
            labelQueryWrapper.eq("level", 2);
            List<Label> labels = labelMapper.selectList(labelQueryWrapper);
            if (labels != null && labels.size() > 0) {
                redisTemplate.opsForList().rightPushAll(redisKey, labels);
                redisTemplate.expire(redisKey, timeUnit, TimeUnit.MINUTES);
            }

            return new ResultVO(ResultStatus.OK, null, labels);
        }
    }
}
