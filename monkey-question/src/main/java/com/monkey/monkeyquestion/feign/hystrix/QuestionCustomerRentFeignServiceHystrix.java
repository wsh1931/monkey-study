//package com.monkey.monkeyquestion.feign.hystrix;
//
//import com.monkey.monkeyUtils.mapper.HystrixErrorLogMapper;
//import com.monkey.monkeyUtils.pojo.log.HystrixErrorLog;
//import com.monkey.monkeyUtils.result.ResultStatus;
//import com.monkey.monkeyUtils.result.ResultVO;
//import com.monkey.monkeyquestion.feign.QuestionToUserFeignService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import java.util.Date;
//import java.util.Map;
//
///**
// * @author: wusihao
// * @description:
// * @date: 2023/7/13 10:27
// * @version: 1.0
// */
//@Component
//public class QuestionCustomerRentFeignServiceHystrix implements QuestionToUserFeignService {
//
//    @Value("${spring.application.name}")
//    private String springApplicationName;
//    @Autowired
//    private HystrixErrorLogMapper hystrixErrorLogMapper;
//    /**
//     * 发生雪崩后备选方案
//     *
//     * @param data {
//     *    userId: 需要查找的用户id信息
//     *    nowUserId: 当前登录的用户id
//     * }
//     *
//     * @return {@link null}
//     * @author wusihao
//     * @date 2023/7/13 9:44
//     */
//    @Override
//    public ResultVO getUserInformationByUserId(Map<String, String> data) {
//        String userId = data.get("userId");
//        String nowUserId = data.get("nowUserId");
//        String params = "userId = " + userId + " nowUserId = " + nowUserId;
//        HystrixErrorLog hystrixErrorLog = new HystrixErrorLog();
//        hystrixErrorLog.setMethodName("getUserInformationByUserId");
//        hystrixErrorLog.setServiceName(springApplicationName);
//        hystrixErrorLog.setParams(params);
//        hystrixErrorLog.setCreateTime(new Date());
//        int insert = hystrixErrorLogMapper.insert(hystrixErrorLog);
//        if (insert > 0) {
//            return new ResultVO(ResultStatus.NO,  springApplicationName + "服务发生错误，已加入数据库中", null);
//        } else {
//            return new ResultVO(ResultStatus.NO,  springApplicationName + "服务发生错误，加入数据库失败", null);
//        }
//    }
//}
