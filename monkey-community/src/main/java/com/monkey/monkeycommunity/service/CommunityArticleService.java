package com.monkey.monkeycommunity.service;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.pojo.CommunityArticleTaskReply;
import com.monkey.monkeycommunity.pojo.CommunityArticleVoteItem;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface CommunityArticleService {
    // 查询社区文章基本信息
    R queryArticleBaseInfo(Long communityArticleId);

    // 社区文章评分
    R communityArticleScore(Long communityId, Long communityArticleId, Integer articleScore, long userId);

    // 得到文章评分内容
    R queryCommunityArticleScore(Long communityArticleId);

    // 查询文章投票信息
    R queryArticleVoteInfo(String userId, Long communityArticleId);

    // 提交社区文章投票
    R submitVote(long userId, Long communityArticleVoteId, List<CommunityArticleVoteItem> communityArticleVoteItemList);

    // 判断当前登录用户是否能看到任务
    R judgeIsShowTask(String userId, Long communityArticleId);

    // 得到任务信息并判断当前任务是否过期
    R queryTaskInfoAndJudgeIsExpire(Long communityArticleId, Integer currentPage, Integer pageSize);

    // 用户提交文章任务
    R submitTask(Long communityArticleTaskId, String replyContent, long userId);

    // 查询未提交任务成员列表
    R queryNoSubmitTaskPeople(Long communityArticleTaskId, List<CommunityArticleTaskReply> communityArticleTaskReplyList);

    // 查询当前登录用户对该文章的评分
    R queryUserArticleScore(String userId, Long communityArticleId);

    // 修改社区文章任务内容
    R confirmUpdate(String replyContent, Long communityArticleTaskReplyId);

    // 查询任务提交历史记录
    R queryTaskSubmitHistoryRecords(Integer currentPage, Integer pageSize, Long communityArticleTaskId, Long userId);

    // 删除任务历史记录
    R deleteTaskHistoryRecord(Long communityArticleTaskReplyId, Long communityArticleTaskId);

    // 导出提交成员数据至excel
    void exportDataToExcel(List<CommunityArticleTaskReply> communityArticleTaskReplyList, HttpServletResponse response) throws IOException;
}
