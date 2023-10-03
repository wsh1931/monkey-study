package com.monkey.monkeycommunity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.monkey.monkeycommunity.pojo.CommunityUserRoleConnect;
import org.apache.ibatis.annotations.Mapper;

/**
 * 社区角色关系表(community_role_connect)

 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-08-31 16:47:20
 */
@Mapper
public interface CommunityUserRoleConnectMapper extends BaseMapper<CommunityUserRoleConnect> {
	
}
