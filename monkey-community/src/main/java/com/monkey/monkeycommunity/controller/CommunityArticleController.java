package com.monkey.monkeycommunity.controller;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.mapper.CommunityArticleTaskReplyMapper;
import com.monkey.monkeycommunity.pojo.CommunityArticleTaskReply;
import com.monkey.monkeycommunity.pojo.CommunityArticleVoteItem;
import com.monkey.monkeycommunity.service.CommunityArticleService;
import com.monkey.spring_security.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/9/18 9:45
 * @version: 1.0
 * @description:
 */
@Api(tags = "社区文章界面接口类")
@RestController
@RequestMapping("/monkey-community/article")
public class CommunityArticleController {
    @Resource
    private CommunityArticleService communityArticleService;


    @ApiOperation("查询社区文章基本信息")
    @GetMapping("/queryArticleBaseInfo")
    public R queryArticleBaseInfo(@RequestParam("communityArticleId") @ApiParam("社区文章id")Long communityArticleId) {
        return communityArticleService.queryArticleBaseInfo(communityArticleId);
    }

    @ApiOperation("社区文章评分")
    @PostMapping("/communityArticle/score")
    public R communityArticleScore(@RequestParam("communityArticleId") @ApiParam("社区文章id") Long communityArticleId,
                                   @RequestParam("articleScore") @ApiParam("文章分数") Integer articleScore,
                                   @RequestParam("communityId") @ApiParam("社区id") Long communityId) {
        long userId = Long.parseLong(JwtUtil.getUserId());
        return communityArticleService.communityArticleScore(communityId, communityArticleId, articleScore, userId);
    }

    @ApiOperation("得到文章评分内容")
    @GetMapping("/queryCommunityArticle/score")
    public R queryCommunityArticleScore(@RequestParam("communityArticleId") @ApiParam("社区文章id")Long communityArticleId) {
        return communityArticleService.queryCommunityArticleScore(communityArticleId);
    }

    @ApiOperation("查询文章投票信息")
    @GetMapping("/queryArticleVoteInfo")
    public R queryArticleVoteInfo(@RequestParam("communityArticleId") @ApiParam("社区文章id")Long communityArticleId) {
        String userId = JwtUtil.getUserId();
        return communityArticleService.queryArticleVoteInfo(userId, communityArticleId);
    }

    @ApiOperation("提交社区文章投票")
    @PostMapping("/submitVote")
    public R submitVote(@RequestParam("communityArticleVoteId") @ApiParam("社区文章投票id")Long communityArticleVoteId,
                        @RequestParam("communityArticleVoteItems") @ApiParam("社区文章投票选项内容") String communityArticleVoteItemsStr) {
        List<CommunityArticleVoteItem> communityArticleVoteItemList = JSONObject.parseArray(communityArticleVoteItemsStr, CommunityArticleVoteItem.class);
        long userId = Long.parseLong(JwtUtil.getUserId());
        return communityArticleService.submitVote(userId, communityArticleVoteId, communityArticleVoteItemList);
    }

    @ApiOperation("判断当前登录用户是否能看到任务")
    @GetMapping("/judgeIsShowTask")
    public R judgeIsShowTask(@RequestParam("communityArticleId")Long communityArticleId) {
        long userId = Long.parseLong(JwtUtil.getUserId());
        return communityArticleService.judgeIsShowTask(userId, communityArticleId);
    }

    @ApiOperation("得到任务信息并判断当前任务是否过期")
    @GetMapping("/queryTaskInfo/judgeIsExpire")
    public R queryTaskInfoAndJudgeIsExpire(@RequestParam("communityArticleId") Long communityArticleId,
                                           @RequestParam("currentPage") @ApiParam("当前页")Integer currentPage,
                                           @RequestParam("pageSize") @ApiParam("每页数据量")Integer pageSize) {
        long userId = Long.parseLong(JwtUtil.getUserId());
        return communityArticleService.queryTaskInfoAndJudgeIsExpire(userId, communityArticleId, currentPage, pageSize);
    }

    @ApiOperation("用户提交文章任务")
    @PostMapping("/submitTask")
    public R submitTask(@RequestParam("communityArticleTaskId") @ApiParam("社区文章任务id") Long communityArticleTaskId,
                        @RequestParam("replyContent") @ApiParam("回复内容")String replyContent) {
        long userId = Long.parseLong(JwtUtil.getUserId());
        return communityArticleService.submitTask(communityArticleTaskId, replyContent, userId);
    }

    @ApiOperation("查询未提交任务成员列表")
    @GetMapping("/queryNoSubmitTaskPeople")
    public R queryNoSubmitTaskPeople(@RequestParam("communityArticleTaskId") @ApiParam("社区文章任务id") Long communityArticleTaskId,
                                     @RequestParam("submitUserList") @ApiParam("提交用户集合") String submitUserListStr) {
        List<CommunityArticleTaskReply> communityArticleTaskReplyList = JSONObject.parseArray(submitUserListStr, CommunityArticleTaskReply.class);
        return communityArticleService.queryNoSubmitTaskPeople(communityArticleTaskId, communityArticleTaskReplyList);
    }

    @ApiOperation("查询当前登录用户对该文章的评分")
    @GetMapping("/queryUserArticleScore")
    public R queryUserArticleScore(@RequestParam("communityArticleId") @ApiParam("社区文章id") Long communityArticleId) {
        long userId = Long.parseLong(JwtUtil.getUserId());
        return communityArticleService.queryUserArticleScore(userId, communityArticleId);
    }

    @ApiOperation("修改社区文章任务内容")
    @PutMapping("/confirmUpdate")
    public R confirmUpdate(@RequestParam("replyContent") @ApiParam("修改回复内容") String replyContent,
                           @RequestParam("communityArticleTaskReplyId") @ApiParam("社区文章任务回复id")  Long communityArticleTaskReplyId) {
        return communityArticleService.confirmUpdate(replyContent, communityArticleTaskReplyId);
    }

    @ApiOperation("查询任务提交历史记录")
    @GetMapping("/queryTaskSubmit/historyRecords")
    public R queryTaskSubmitHistoryRecords(@RequestParam("currentPage") @ApiParam("当前页")Integer currentPage,
                                           @RequestParam("pageSize") @ApiParam("每页数据量")Integer pageSize,
                                           @RequestParam("communityArticleTaskId") @ApiParam("社区文章任务id") Long communityArticleTaskId,
                                           @RequestParam("userId") @ApiParam("回复用户id") Long userId) {
        return communityArticleService.queryTaskSubmitHistoryRecords(currentPage, pageSize, communityArticleTaskId, userId);
    }

    @ApiOperation("删除任务历史记录")
    @DeleteMapping("/delete/taskHistoryRecord")
    public R deleteTaskHistoryRecord(@RequestParam("communityArticleTaskReplyId") @ApiParam("社区文章任务回复id") Long communityArticleTaskReplyId,
                                     @RequestParam("communityArticleTaskId") @ApiParam("社区文章任务id") Long communityArticleTaskId) {
        return communityArticleService.deleteTaskHistoryRecord(communityArticleTaskReplyId, communityArticleTaskId);
    }

    @ApiOperation("导出提交成员数据至excel")
    @GetMapping("/exportDataToExcel")
    public void exportDataToExcel(@RequestParam("communityArticleTaskReplyStr") @ApiParam("社区文章任务回复集合字符串") String communityArticleTaskReplyStr,
                                  HttpServletResponse response) throws Exception{
        List<CommunityArticleTaskReply> communityArticleTaskReplyList = JSONObject.parseArray(communityArticleTaskReplyStr, CommunityArticleTaskReply.class);
        communityArticleService.exportDataToExcel(communityArticleTaskReplyList, response);
    }
}
