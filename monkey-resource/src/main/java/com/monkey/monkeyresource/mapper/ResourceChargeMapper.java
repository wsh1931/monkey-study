package com.monkey.monkeyresource.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.monkey.monkeyresource.pojo.ResourceCharge;
import org.apache.ibatis.annotations.Mapper;

/**
 * 资源收费表
 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-10-09 09:35:26
 */
@Mapper
public interface ResourceChargeMapper extends BaseMapper<ResourceCharge> {
	
}
