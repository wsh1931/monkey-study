package com.monkey.monkeyUtils.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.monkey.monkeyUtils.pojo.log.HystrixErrorLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HystrixErrorLogMapper extends BaseMapper<HystrixErrorLog> {
}
