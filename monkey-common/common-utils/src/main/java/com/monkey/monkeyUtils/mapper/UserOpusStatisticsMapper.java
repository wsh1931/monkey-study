package com.monkey.monkeyUtils.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.monkey.monkeyUtils.pojo.UserOpusStatistics;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户作品统计表
 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-12-09 11:16:20
 */
@Mapper
public interface UserOpusStatisticsMapper extends BaseMapper<UserOpusStatistics> {
	
}
