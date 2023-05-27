package com.monkey.monkeybackend.Mapper.User;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.monkey.monkeybackend.Pojo.user.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
