package com.monkey.monkeyquestion.feign;

import com.monkey.monkeyUtils.result.R;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "monkey-search", contextId = "question-to-search")
public interface QuestionToSearchFeignService {
    // 问答游览数 + 1
    @PutMapping("/monkey-search/question/feign/questionViewAddOne")
    R questionViewAddOne(@RequestParam("questionId") @ApiParam("问答id") Long questionId);

    // 问答回复数 + 1
    @PutMapping("/monkey-search/question/feign/questionReplyCountAdd")
    R questionReplyCountAdd(@RequestParam("questionId") @ApiParam("问答id") Long questionId);

    // 问答点赞数 + 1
    @PutMapping("/monkey-search/question/feign/questionLikeCountAddOne")
    R questionLikeCountAddOne(@RequestParam("questionId") @ApiParam("问答id") Long questionId);

    // 问答点赞数 - 1
    @PutMapping("/monkey-search/question/feign/questionLikeCountSubOne")
    R questionLikeCountSubOne(@RequestParam("questionId") @ApiParam("问答id") Long questionId);

    // 问答收藏数 + 1
    @PutMapping("/monkey-search/question/feign/questionCollectCountAddOne")
    R questionCollectCountAddOne(@RequestParam("questionId") @ApiParam("问答id") Long questionId);

    // 问答收藏数 - 1
    @PutMapping("/monkey-search/question/feign/questionCollectCountSubOne")
    R questionCollectCountSubOne(@RequestParam("questionId") @ApiParam("问答id") Long questionId);
}
