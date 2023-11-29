package com.monkey.monkeyquestion.feign;

import com.monkey.monkeyUtils.result.R;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    // 发布问答
    @PostMapping("/monkey-search/question/feign/publishQuestion")
    R publishQuestion(@RequestParam("questionStr") @ApiParam("问答实体类字符串") String questionStr);

    @DeleteMapping("/monkey-search/question/feign/deleteQuestion")
    R deleteQuestion(@RequestParam("questionId") @ApiParam("问答id") String questionId);

    // 用户游览数 + 1
    @PutMapping("/monkey-search/user/feign/userViewAddOne")
    R userViewAddOne(@RequestParam("userId") @ApiParam("用户id") Long userId);

    // 用户作品数 + 1
    @PutMapping("/monkey-search/user/feign/userOpusCountAddOne")
    R userOpusCountAddOne(@RequestParam("userId") @ApiParam("用户id") Long userId);


    // 用户作品数 - 1
    @PutMapping("/monkey-search/user/feign/userOpusCountSubOne")
    R userOpusCountSubOne(@RequestParam("userId") @ApiParam("用户id") Long userId);

    // 用户点赞数 + 1
    @PutMapping("/monkey-search/user/feign/userLikeCountAddOne")
    R userLikeCountAddOne(@RequestParam("userId") @ApiParam("用户id") Long userId);

    // 用户点赞数 - 1
    @PutMapping("/monkey-search/user/feign/userLikeCountSubOne")
    R userLikeCountSubOne(@RequestParam("userId") @ApiParam("用户id") Long userId);

    // 用户收藏数 + 1
    @PutMapping("/monkey-search/user/feign/userCollectCountAddOne")
    R userCollectCountAddOne(@RequestParam("userId") @ApiParam("用户id") Long userId);

    // 用户收藏数 - 1
    @PutMapping("/monkey-search/user/feign/userCollectCountSubOne")
    R userCollectCountSubOne(@RequestParam("userId") @ApiParam("用户id") Long userId);
}
