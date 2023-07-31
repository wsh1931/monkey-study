package com.monkey.monkeyblog.feign;

import com.monkey.monkeyUtils.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "monkey-question", contextId = "user-to-question")
public interface UserToQuestionFeignService {
    // 通过用户id得到问答列表
    @GetMapping("/monkey-question/user/feign/getQuestionListByQuestionId/{userId}")
    R getQuestionListByQuestionId(@PathVariable Long userId);

    // 通过用户id得到用户提问数
    @GetMapping("/monkey-question/user/feign/getUserQuestionCountByUserId/{userId}")
    R getUserQuestionCountByUserId(@PathVariable Long userId);

    // 通过用户id得到文章分页提问列表
    @GetMapping("/monkey-question/user/feign/getQuestionListByUserId/")
    R getQuestionListByUserId(@RequestParam("userId") Long userId,
                                     @RequestParam("currentPage") Long currentPage,
                                     @RequestParam("pageSize") Long pageSize);
}
