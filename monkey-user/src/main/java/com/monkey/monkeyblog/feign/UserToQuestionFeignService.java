package com.monkey.monkeyblog.feign;

import com.monkey.monkeyUtils.result.R;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
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

    // 问答游览数 + 1
    @PutMapping("/monkey-question/user/feign/addQurstionViewSum/{questionId}")
    R addQurstionViewSum(@PathVariable Long questionId);

    // 问答游览数 - 1
    @PutMapping("/monkey-question/user/feign/subQurstionViewSum/{questionId}")
    R subQurstionViewSum(@PathVariable Long questionId);

    @GetMapping("/monkey-question/user/feign/queryQuestionById/{questionId}")
    R queryQuestionById(@PathVariable Long questionId);

    @GetMapping("/monkey-question/user/feign/queryQuestionAndCommentById")
    R queryQuestionAndCommentById(@RequestParam("questionId") @ApiParam("问答id") Long questionId,
                                         @RequestParam("commentId") @ApiParam("评论id") Long commentId);

    @GetMapping("/monkey-question/user/feign/queryQuestionAuthorById")
    Long queryQuestionAuthorById(@RequestParam("associationId") @ApiParam("问答id") Long questionId);
}
