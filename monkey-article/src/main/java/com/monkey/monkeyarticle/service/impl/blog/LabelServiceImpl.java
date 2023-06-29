package com.monkey.monkeyarticle.service.impl.blog;

import com.monkey.monkeyUtils.redis.RedisTimeConstant;
import com.monkey.monkeyUtils.redis.RedisUrlConstant;
import com.monkey.monkeyUtils.result.ResultStatus;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyUtils.mapper.LabelMapper;
import com.monkey.monkeyUtils.pojo.Label;
import com.monkey.monkeyarticle.service.blog.LabelService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class LabelServiceImpl implements LabelService {
    @Autowired
    private LabelMapper labelMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public ResultVO getLabelList() {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(RedisUrlConstant.LABEL_LIST))) {
            return new ResultVO(ResultStatus.OK, null, redisTemplate.opsForList().range(RedisUrlConstant.LABEL_LIST, 0, -1));
        } else {
            List<Label> labels = labelMapper.selectList(null);
            redisTemplate.opsForList().rightPushAll(RedisUrlConstant.LABEL_LIST, labels);
            redisTemplate.expire(RedisUrlConstant.LABEL_LIST, RedisTimeConstant.LABEL_EXPIRE_TIME, TimeUnit.MINUTES);
            return new ResultVO(ResultStatus.OK, null, labels);
        }
    }
}
