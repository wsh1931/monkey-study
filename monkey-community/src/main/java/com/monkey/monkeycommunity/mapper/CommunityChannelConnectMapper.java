package com.monkey.monkeycommunity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.monkey.monkeycommunity.pojo.CommunityChannelConnect;
import org.apache.ibatis.annotations.Mapper;

/**
 * 社区频道关系表(community_channel_connect)
 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-08-31 16:47:20
 */
@Mapper
public interface CommunityChannelConnectMapper extends BaseMapper<CommunityChannelConnect> {
	
}
