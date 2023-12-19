package com.monkey.monkeyblog.feign;

import com.monkey.monkeyUtils.result.R;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@FeignClient(value = "monkey-question", contextId = "user-to-question")
public interface UserToQuestionFeignService {

    // 得到问答一周发表数
    @GetMapping("/monkey-question/user/feign/queryQuestionCountRecentlyWeek")
    R queryQuestionCountRecentlyWeek(@RequestParam("userId") @ApiParam("用户id") String userId);


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

    // 问答收藏数 + 1
    @PutMapping("/monkey-question/user/feign/addQuestionCollectSum/{questionId}")
    R addQuestionCollectSum(@PathVariable Long questionId);

    // 问答收藏数 - 1
    @PutMapping("/monkey-question/user/feign/subQuestionCollectSum/{questionId}")
    R subQuestionCollectSum(@PathVariable Long questionId,
                         @RequestParam("createTime")Date createTime);

    @GetMapping("/monkey-question/user/feign/queryQuestionById/{questionId}")
    R queryQuestionById(@PathVariable Long questionId);

    @GetMapping("/monkey-question/user/feign/queryQuestionAndCommentById")
    R queryQuestionAndCommentById(@RequestParam("questionId") @ApiParam("问答id") Long questionId,
                                         @RequestParam("commentId") @ApiParam("评论id") Long commentId);

    @GetMapping("/monkey-question/user/feign/queryQuestionAuthorById")
    Long queryQuestionAuthorById(@RequestParam("associationId") @ApiParam("问答id") Long questionId);

    @GetMapping("/monkey-question/user/feign/queryQuestionAndReplyById")
    R queryQuestionAndReplyById(@RequestParam("questionId") @ApiParam("问答id") Long questionId,
                                       @RequestParam("questionReplyId") @ApiParam("问答回复id") Long questionReplyId);
}
