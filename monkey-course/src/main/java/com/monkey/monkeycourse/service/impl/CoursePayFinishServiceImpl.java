package com.monkey.monkeycourse.service.impl;

import com.monkey.monkeyUtils.mapper.OrderInformationMapper;
import com.monkey.monkeyUtils.pojo.OrderInformation;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycourse.service.CoursePayFinishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/8/29 17:51
 * @version: 1.0
 * @description:
 */
@Service
public class CoursePayFinishServiceImpl implements CoursePayFinishService {
    @Resource
    private OrderInformationMapper orderInformationMapper;
    /**
     * 通过订单id得到订单信息
     *
     * @param orderId 订单id
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/29 18:12
     */
    @Override
    public R queryOrderInfoByOrderId(long orderId) {
        return R.ok(orderInformationMapper.selectById(orderId));
    }
}
