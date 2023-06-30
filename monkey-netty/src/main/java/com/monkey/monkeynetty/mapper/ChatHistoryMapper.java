package com.monkey.monkeynetty.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.monkey.monkeynetty.pojo.ChatHistory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatHistoryMapper extends BaseMapper<ChatHistory> {
}
