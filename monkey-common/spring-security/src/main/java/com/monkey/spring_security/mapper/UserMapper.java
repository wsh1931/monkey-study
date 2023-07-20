package com.monkey.spring_security.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.monkey.spring_security.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
