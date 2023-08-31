package com.monkey.monkeyblog.mapper;

import com.monkey.monkeyblog.pojo.UserMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户消息表
 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-08-31 16:47:20
 */
@Mapper
public interface UserMessageDao extends BaseMapper<UserMessage> {
	
}
