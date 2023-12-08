package com.monkey.monkeyUtils.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.monkey.monkeyUtils.pojo.HistoryLike;
import org.apache.ibatis.annotations.Mapper;

/**
 * 历史点赞表
 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-12-06 17:24:07
 */
@Mapper
public interface HistoryLikeMapper extends BaseMapper<HistoryLike> {
	
}
