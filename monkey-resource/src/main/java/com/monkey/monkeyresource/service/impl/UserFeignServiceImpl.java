package com.monkey.monkeyresource.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyresource.mapper.ResourceBuyMapper;
import com.monkey.monkeyresource.pojo.ResourceBuy;
import com.monkey.monkeyresource.service.UserFeignService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/10/22 16:26
 * @version: 1.0
 * @description:
 */
@Service
public class UserFeignServiceImpl implements UserFeignService {

    @Resource
    private ResourceBuyMapper resourceBuyMapper;
    /**
     * 删除用户购买资源记录
     *
     * @param userId 用户id
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/22 16:32
     */
    @Override
    public R deleteUserBuyResource(Long userId, Long resourceId) {
        QueryWrapper<ResourceBuy> resourceBuyQueryWrapper = new QueryWrapper<>();
        resourceBuyQueryWrapper.eq("user_id", userId);
        resourceBuyQueryWrapper.eq("resource_id", resourceId);
        int delete = resourceBuyMapper.delete(resourceBuyQueryWrapper);
        return R.ok(delete);
    }
}
