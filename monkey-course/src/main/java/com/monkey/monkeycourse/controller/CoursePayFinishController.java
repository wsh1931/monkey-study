package com.monkey.monkeycourse.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycourse.service.CoursePayFinishService;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author: wusihao
 * @date: 2023/8/29 17:50
 * @version: 1.0
 * @description:
 */
@RestController
@RequestMapping("/monkey-course/pay/finish")
public class CoursePayFinishController {
    @Resource
    private CoursePayFinishService coursePayFinishService;

    // 通过订单id得到订单信息
    @GetMapping("/queryOrderInfoByOrderId")
    public R queryOrderInfoByOrderId(@RequestParam Map<String, String> data){
        long orderId = Long.parseLong(data.get("orderId"));
        return coursePayFinishService.queryOrderInfoByOrderId(orderId);
    }
}
