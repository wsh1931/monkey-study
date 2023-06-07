package com.monkey.monkeyblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.monkey.monkeyblog.pojo.ChatHistory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatHistoryMapper extends BaseMapper<ChatHistory> {
}
