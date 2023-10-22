package com.monkey.monkeyblog.service.Impl;

import com.monkey.monkeyUtils.mapper.OrderInformationMapper;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyblog.service.OrderPayFinishService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/8/29 17:51
 * @version: 1.0
 * @description:
 */
@Service
public class OrderPayFinishServiceImpl implements OrderPayFinishService {
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
