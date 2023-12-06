package com.monkey.monkeyquestion.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyquestion.service.QuestionReplyService;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@Api(tags = "问答回复接口")
@RestController
@RequestMapping("/monkey-question/reply")
public class QusetionReplyController{

    @Resource
    private QuestionReplyService questionReplyService;

    @ApiOperation("通过问答id得到作者Vo信息")
    @GetMapping("/getAuthorVoInfoByQuestionId")
    public ResultVO getAuthorVoInfoByQuestionId(@RequestParam("questionId") @ApiParam("问答id") Long questionId) {
        String fansId = JwtUtil.getUserId();
        return questionReplyService.getAuthorVoInfoByQuestionId(questionId, fansId);
    }

    @ApiOperation("通过问答id得到问答信息")
    @GetMapping("/getQuestionInfoByQuestionId")
    public ResultVO getQuestionInfoByQuestionId(@RequestParam("questionId") @ApiParam("问答id") Long questionId) {
        String userId = JwtUtil.getUserId();
        return questionReplyService.getQuestionInfoByQuestionId(questionId, userId);
    }

    @ApiOperation("通过问答id得到问答标签名")
    @GetMapping("/getQuestionLabelNameByQuestionId")
    public ResultVO getQuestionLabelNameByQuestionId(@RequestParam("questionId") @ApiParam("问答id") Long questionId) {
        return questionReplyService.getQuestionLabelNameByQuestionId(questionId);
    }

    @ApiOperation("通过问答id得到问答回复列表")
    @GetMapping("/getQuestionReplyListByQuestionId")
    public ResultVO getQuestionReplyListByQuestionId(@RequestParam("questionId") @ApiParam("问答id") Long questionId,
                                                     @RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                                     @RequestParam("pageSize") @ApiParam("每页数据量") Long pageSize) {
        String fansId = JwtUtil.getUserId();
        return questionReplyService.getQuestionReplyListByQuestionId(questionId, fansId, currentPage, pageSize);
    }


    @ApiOperation("用户问答点赞实现")
    @PostMapping("/userLikeQuestion")
    public ResultVO userLikeQuestion(@RequestParam("questionId") @ApiParam("问答id") Long questionId,
                                     @RequestParam("recipientId") @ApiParam("接收人id") Long recipientId) {
        long userId = Long.parseLong(JwtUtil.getUserId());
        return questionReplyService.userLikeQuestion(questionId, userId, recipientId);
    }

    @ApiOperation("用户问答取消点赞实现")
    @PostMapping("/userCancelLikeQuestion")
    public ResultVO userCancelLikeQuestion(@RequestParam("questionId") @ApiParam("问答id") Long questionId) {
        long userId = Long.parseLong(JwtUtil.getUserId());
        return questionReplyService.userCancelLikeQuestion(questionId, userId);
    }


    @ApiOperation("通过问答回复id得到文章评论信息")
    @GetMapping("/getQuestionCommentByQuestionReplyId")
    public ResultVO getQuestionCommentByQuestionReplyId(@RequestParam("questionReplyId") @ApiParam("问答回复id") Long questionReplyId,
                                                        @RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                                        @RequestParam("pageSize") @ApiParam("每页数据量") Long pageSize) {
        return questionReplyService.getQuestionCommentByQuestionReplyId(questionReplyId, currentPage, pageSize);
    }

    @ApiOperation("发表问答回复")
    @PostMapping("/publishReply")
    public R publishReply(@RequestParam("questionId") @ApiParam("问答id") Long questionId,
                          @RequestParam("replyContent") @ApiParam("回复内容") String replyContent) {
        Long userId = Long.parseLong(JwtUtil.getUserId());
        return questionReplyService.publishReply(questionId, userId, replyContent);
    }

    @ApiOperation("判断问答是否存在")
    @GetMapping("/judgeQuestionIsExist")
    public R judgeQuestionIsExist(@RequestParam("questionId") @ApiParam("问答id") Long questionId) {
        return questionReplyService.judgeQuestionIsExist(questionId);
    }
}
