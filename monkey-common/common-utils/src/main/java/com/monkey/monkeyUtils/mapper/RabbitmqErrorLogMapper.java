package com.monkey.monkeyUtils.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.monkey.monkeyUtils.pojo.log.RabbitmqErrorLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RabbitmqErrorLogMapper extends BaseMapper<RabbitmqErrorLog> {
}
