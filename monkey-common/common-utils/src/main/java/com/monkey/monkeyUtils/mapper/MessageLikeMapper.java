package com.monkey.monkeyUtils.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.monkey.monkeyUtils.pojo.MessageLike;
import org.apache.ibatis.annotations.Mapper;

/**
 * 消息点赞表
 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-10-25 16:47:52
 */
@Mapper
public interface MessageLikeMapper extends BaseMapper<MessageLike> {
	
}
