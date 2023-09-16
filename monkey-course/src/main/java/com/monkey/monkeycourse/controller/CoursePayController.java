package com.monkey.monkeycourse.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycourse.service.CoursePayService;
import com.monkey.spring_security.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author: wusihao
 * @date: 2023/8/23 15:04
 * @version: 1.0
 * @description: 课程支付
 */
@Api(tags = "课程支付页面")
@RestController
@RequestMapping("/monkey-course/pay")
public class CoursePayController {
    @Resource
    private CoursePayService coursePayService;

    @ApiOperation("通过课程id得到课程信息")
    @GetMapping("/getCourseInfoByCourseId")
    public R getCourseInfoByCourseId(@RequestParam("courseId") @ApiParam("课程id") Long courseId) {
        long userId = Long.parseLong(JwtUtil.getUserId());
        return coursePayService.getCourseInfoByCourseId(courseId, userId);
    }

    // 统一下单并支付页面接口
    @PostMapping("/tradePagePay")
    @ApiOperation("统一下单并支付页面接口")
    public R tradePagePay(@RequestParam("courseId") @ApiParam("课程id") Long courseId) {
        return coursePayService.tradePagePay(courseId);
    }

    // 下单支付后执行的接口, 支付宝以 POST 形式发送请求
    @PostMapping("/finishPayNotice")
    public String finishPayNotice(@RequestParam Map<String, String> data)  {


        /**
         * 在进行异步通知交互时，如果支付宝收到的应答不是 success ，
         * 支付宝会认为通知失败，会通过一定的策略定期重新发起通知。
         * 如果返回failure则支付宝会认为此次交易失败，不断发送给重测通知
         */
        return coursePayService.finishPayNotice(data);
    }
}
