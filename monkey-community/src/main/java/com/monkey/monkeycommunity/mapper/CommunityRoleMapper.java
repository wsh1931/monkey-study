package com.monkey.monkeycommunity.mapper;



import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.monkey.monkeycommunity.pojo.CommunityRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 社区角色表(community_role)

 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-08-31 16:47:20
 */
@Mapper
public interface CommunityRoleMapper extends BaseMapper<CommunityRole> {
	
}
