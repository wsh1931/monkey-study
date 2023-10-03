package com.monkey.monkeycommunity.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.MapUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.excel.ExcelSheetNameConstant;
import com.monkey.monkeyUtils.excel.ExcelTableNameConstant;
import com.monkey.monkeyUtils.mapper.CollectContentConnectMapper;
import com.monkey.monkeyUtils.pojo.CollectContentConnect;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.util.DateSelfUtils;
import com.monkey.monkeycommunity.constant.CommunityEnum;
import com.monkey.monkeycommunity.constant.CommunityRoleEnum;
import com.monkey.monkeycommunity.mapper.*;
import com.monkey.monkeycommunity.pojo.*;
import com.monkey.monkeycommunity.rabbitmq.EventConstant;
import com.monkey.monkeycommunity.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeycommunity.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeycommunity.service.CommunityArticleService;
import com.monkey.monkeycommunity.util.CommonMethod;
import com.monkey.spring_security.mapper.UserMapper;
import com.monkey.spring_security.pojo.User;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author: wusihao
 * @date: 2023/9/18 9:46
 * @version: 1.0
 * @description:
 */
@Service
public class CommunityArticleServiceImpl implements CommunityArticleService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private CommunityArticleMapper communityArticleMapper;

    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private CommunityArticleScoreMapper communityArticleScoreMapper;
    @Resource
    private CommunityArticleVoteMapper communityArticleVoteMapper;
    @Resource
    private CommunityArticleVoteItemMapper communityArticleVoteItemMapper;
    @Resource
    private CommunityArticleVoteUserMapper communityArticleVoteUserMapper;
    @Resource
    private CommunityArticleTaskMapper communityArticleTaskMapper;
    @Resource
    private CommunityArticleTaskReplyMapper communityArticleTaskReplyMapper;
    @Resource
    private CommunityArticleLikeMapper communityArticleLikeMapper;
    @Resource
    private CollectContentConnectMapper collectContentConnectMapper;
    @Resource
    private CommunityUserRoleConnectMapper communityUserRoleConnectMapper;
    @Resource
    private CommunityChannelMapper communityChannelMapper;
    @Resource
    private CommunityUserManageMapper communityUserManageMapper;
    /**
     * 查询社区文章基本信息
     *
     * @param communityArticleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/18 9:55
     */
    @Override
    public R queryArticleBaseInfo(Long communityArticleId) {
        CommunityArticle communityArticle = communityArticleMapper.selectById(communityArticleId);
        Long userId = communityArticle.getUserId();
        User user = userMapper.selectById(userId);
        communityArticle.setUsername(user.getUsername());
        communityArticle.setUserHeadImg(user.getPhoto());
        return R.ok(communityArticle);
    }

    /**
     * 社区文章评分
     *
     * @param communityId
     * @param communityArticleId 社区文章id
     * @param articleScore       文章评分
     * @param userId             评价者id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/18 10:30
     */
    @Override
    public R communityArticleScore(Long communityId, Long communityArticleId, Integer articleScore, long userId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.communityArticleScore);
        jsonObject.put("communityArticleId", communityArticleId);
        jsonObject.put("articleScore", articleScore);
        jsonObject.put("userId", userId);
        jsonObject.put("communityId", communityId);
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.communityInsertDirectExchange,
                RabbitmqRoutingName.communityInsertRouting, message);
        return R.ok();
    }

    /**
     * 得到文章评分内容
     *
     * @param communityArticleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/18 16:59
     */
    @Override
    public R queryCommunityArticleScore(Long communityArticleId) {
        QueryWrapper<CommunityArticleScore> communityArticleScoreQueryWrapper = new QueryWrapper<>();
        communityArticleScoreQueryWrapper.eq("community_article_id", communityArticleId);
        communityArticleScoreQueryWrapper.select("score");
        List<CommunityArticleScore> communityArticleScores = communityArticleScoreMapper.selectList(communityArticleScoreQueryWrapper);
        if (communityArticleScores == null) {
            return R.ok();
        }
        int oneStar = 0;
        int towStar = 0;
        int threeStar = 0;
        int fourStar = 0;
        int fiveStar = 0;
        int scoreCount = 0;
        long scoreTotal = 0L;
        for (CommunityArticleScore communityArticleScore : communityArticleScores) {
            Integer score = communityArticleScore.getScore();
            scoreCount ++ ;
            scoreTotal += score;
            switch (score) {
                case 1:
                    oneStar ++ ;
                    break;
                case 2:
                    towStar ++ ;
                    break;
                case 3:
                    threeStar ++ ;
                    break;
                case 4:
                    fourStar ++ ;
                    break;
                case 5:
                    fiveStar ++ ;
            }
        }

        if (scoreCount == 0) {
            return R.ok();
        }
        JSONObject data = new JSONObject();
        data.put("oneStar", CommonMethod.doubleToRate((double) oneStar / scoreCount));
        data.put("twoStar", CommonMethod.doubleToRate((double)towStar / scoreCount));
        data.put("threeStar", CommonMethod.doubleToRate((double)threeStar / scoreCount));
        data.put("fourStar", CommonMethod.doubleToRate((double)fourStar / scoreCount));
        data.put("fiveStar", CommonMethod.doubleToRate((double)fiveStar / scoreCount));
        data.put("scoreCount", scoreCount);
        data.put("totalScore", CommonMethod.doubleToOne((double)scoreTotal / scoreCount));
        return R.ok(data);
    }

    /**
     * 查询文章投票信息
     *
     * @param userId 当前登录用户id
     * @param communityArticleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/19 9:02
     */
    @Override
    public R queryArticleVoteInfo(String userId, Long communityArticleId) {
        // 得到投票基本信息
        QueryWrapper<CommunityArticleVote> communityArticleVoteQueryWrapper = new QueryWrapper<>();
        communityArticleVoteQueryWrapper.eq("community_article_id", communityArticleId);
        CommunityArticleVote communityArticleVote = communityArticleVoteMapper.selectOne(communityArticleVoteQueryWrapper);
        // 判断任务是否过期
        Date voteDuration = communityArticleVote.getVoteDuration();
        if (DateSelfUtils.judgeNowTimeAndAssignment(voteDuration)) {
            communityArticleVote.setIsOverdue(CommunityEnum.IS_OVERDUE.getCode());
        } else {
            communityArticleVote.setIsOverdue(CommunityEnum.NOT_OVERDUE.getCode());
        }

        // 得到文章选项投票表
        QueryWrapper<CommunityArticleVoteItem> communityArticleVoteItemQueryWrapper = new QueryWrapper<>();
        communityArticleVoteItemQueryWrapper.eq("community_article_vote_id", communityArticleVote.getId());
        List<CommunityArticleVoteItem> communityArticleVoteItemList = communityArticleVoteItemMapper.selectList(communityArticleVoteItemQueryWrapper);
        communityArticleVote.setCommunityArticleVoteItemList(communityArticleVoteItemList);

        // 判断用户是否已参加投票
        if (userId != null && !"".equals(userId)) {
            for (CommunityArticleVoteItem communityArticleVoteItem : communityArticleVoteItemList) {
                Long communityArticleVoteItemId = communityArticleVoteItem.getId();
                QueryWrapper<CommunityArticleVoteUser> communityArticleVoteUserQueryWrapper = new QueryWrapper<>();
                communityArticleVoteUserQueryWrapper.eq("community_article_vote_item_id", communityArticleVoteItemId);
                communityArticleVoteUserQueryWrapper.eq("user_id", userId);
                Long selectCount = communityArticleVoteUserMapper.selectCount(communityArticleVoteUserQueryWrapper);
                if (selectCount > 0) {
                    communityArticleVote.setIsVote(CommunityEnum.IS_PARTICIPATE_VOTE.getCode());
                    communityArticleVoteItem.setIsSelected(CommunityEnum.IS_PARTICIPATE_VOTE.getCode());

                    if (communityArticleVote.getVoteKind().equals(CommunityEnum.RADIO.getCode())) {
                        break;
                    }
                }
            }
        }

        return R.ok(communityArticleVote);
    }

    /**
     * 提交社区文章投票
     *
     * @param userId 当前登录用户id
     * @param communityArticleVoteId 社区文章投票id
     * @param communityArticleVoteItemList 社区文章投票选项集合
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/19 17:21
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R submitVote(long userId, Long communityArticleVoteId, List<CommunityArticleVoteItem> communityArticleVoteItemList) {
        Date date = new Date();
        for (CommunityArticleVoteItem communityArticleVoteItem : communityArticleVoteItemList) {
            Integer isSelected = communityArticleVoteItem.getIsSelected();
            if (isSelected.equals(CommonEnum.IS_SELECTED.getCode())) {
                // 被选中, 加入数据库
                CommunityArticleVoteUser communityArticleVoteUser = new CommunityArticleVoteUser();
                communityArticleVoteUser.setUserId(userId);
                communityArticleVoteUser.setCommunityArticleVoteItemId(communityArticleVoteItem.getId());
                communityArticleVoteUser.setCreateTime(date);
                communityArticleVoteUserMapper.insert(communityArticleVoteUser);
            }
        }

        // 文章投票人数 + 1
        JSONObject data = new JSONObject();
        data.put("event", EventConstant.communityArticleVotePeopleAddOne);
        data.put("communityArticleVoteId", communityArticleVoteId);
        Message message = new Message(data.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.communityUpdateDirectExchange,
                RabbitmqRoutingName.communityUpdateRouting, message);
        return R.ok();
    }

    /**
     * 判断当前登录用户是否能看到任务
     *
     * @param userId 当前登录用户id
     * @param communityArticleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/20 14:19
     */
    @Override
    public R judgeIsShowTask(String userId, Long communityArticleId) {
        QueryWrapper<CommunityArticleTask> communityArticleTaskQueryWrapper = new QueryWrapper<>();
        communityArticleTaskQueryWrapper.eq("community_article_id", communityArticleId);
        CommunityArticleTask communityArticleTask = communityArticleTaskMapper.selectOne(communityArticleTaskQueryWrapper);
        Integer isPublic = communityArticleTask.getIsPublic();
        if (CommunityEnum.IS_PUBLIC.getCode().equals(isPublic)) {
            return R.ok(CommunityEnum.IS_PUBLIC.getCode());
        }

        if (userId == null || "".equals(userId)) {
            return R.ok(CommunityEnum.NOT_PUBLIC.getCode());
        }
        long parseLong = Long.parseLong(userId);
        // 否则不公开，判断用户是否被邀请
        String[] userIds = communityArticleTask.getUserIds().split(",");
        for (String s : userIds) {
            if (parseLong == Long.parseLong(s)) {
                return R.ok(CommunityEnum.IS_PUBLIC.getCode());
            }
        }

        return R.ok(CommunityEnum.NOT_PUBLIC.getCode());
    }

    /**
     * 得到任务信息并判断当前任务是否过期
     *
     * @param communityArticleId 社区文章id
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/20 14:50
     */
    @Override
    public R queryTaskInfoAndJudgeIsExpire(Long communityArticleId, Integer currentPage, Integer pageSize) {
        QueryWrapper<CommunityArticleTask> communityArticleTaskQueryWrapper = new QueryWrapper<>();
        communityArticleTaskQueryWrapper.eq("community_article_id", communityArticleId);
        CommunityArticleTask communityArticleTask = communityArticleTaskMapper.selectOne(communityArticleTaskQueryWrapper);
        Long communityArticleTaskId = communityArticleTask.getId();
        Date endTime = communityArticleTask.getEndTime();
        // 判断该任务是否过期
        if (DateSelfUtils.judgeNowTimeAndAssignment(endTime)) {
            communityArticleTask.setIsOverDue(CommunityEnum.IS_OVERDUE.getCode());
        } else {
            communityArticleTask.setIsOverDue(CommunityEnum.NOT_OVERDUE.getCode());
        }

        int replyCount = 0;
        QueryWrapper<CommunityArticleTaskReply> communityArticleTaskReplyQueryWrapper = new QueryWrapper<>();
        communityArticleTaskReplyQueryWrapper.eq("community_article_task_id", communityArticleTaskId);
        communityArticleTaskReplyQueryWrapper.select("user_id, MAX(create_time) as max_create_time");
        communityArticleTaskReplyQueryWrapper.groupBy("user_id");
        communityArticleTaskReplyQueryWrapper.orderByDesc("max_create_time");
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = communityArticleTaskReplyMapper.selectPage(page, communityArticleTaskReplyQueryWrapper);
        List<CommunityArticleTaskReply> communityArticleTaskReplyList = selectPage.getRecords();
        for (CommunityArticleTaskReply communityArticleTaskReply : communityArticleTaskReplyList) {
            Long replyUserId = communityArticleTaskReply.getUserId();
                // 得到用户提交次数
                QueryWrapper<CommunityArticleTaskReply> replyQueryWrapper = new QueryWrapper<>();
                replyQueryWrapper.eq("community_article_task_id", communityArticleTaskId);
                replyQueryWrapper.eq("user_id", replyUserId);
                replyQueryWrapper.orderByDesc("create_time");
                List<CommunityArticleTaskReply> communityArticleTaskReplies = communityArticleTaskReplyMapper.selectList(replyQueryWrapper);
                int size = communityArticleTaskReplies.size();
                if (communityArticleTaskReplies != null && size > 0) {
                    communityArticleTaskReply.setSubmitCount(size);

                    CommunityArticleTaskReply articleTaskReply = communityArticleTaskReplies.get(0);
                    BeanUtils.copyProperties(articleTaskReply, communityArticleTaskReply);
                }

                communityArticleTaskReply.setSubmitCount(size);
                replyCount ++ ;
                User user = userMapper.selectById(replyUserId);
                communityArticleTaskReply.setUsername(user.getUsername());
                communityArticleTaskReply.setHeadImg(user.getPhoto());
        }
        selectPage.setRecords(communityArticleTaskReplyList);
        communityArticleTask.setPage(selectPage);

        // 得到未提交人数
        String[] userIds = communityArticleTask.getUserIds().split(",");
        int length = userIds.length;
        communityArticleTask.setNotSubmitCount(length - replyCount);
        // 提交人数
        communityArticleTask.setSubmitCount(replyCount);

        // 得到完成率
        communityArticleTask.setFinish((int)((double)replyCount / length * 100));

        return R.ok(communityArticleTask);
    }

    /**
     * 用户提交文章任务
     *
     * @param communityArticleTaskId 社区文章任务id
     * @param replyContent 回复内容
     * @param userId 当前登录用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/20 17:09
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R submitTask(Long communityArticleTaskId, String replyContent, long userId) {
        CommunityArticleTaskReply communityArticleTaskReply = new CommunityArticleTaskReply();
        communityArticleTaskReply.setUserId(userId);
        communityArticleTaskReply.setReplyContent(replyContent);
        Date date = new Date();
        communityArticleTaskReply.setCreateTime(date);
        communityArticleTaskReply.setUpdateTime(date);
        communityArticleTaskReply.setCommunityArticleTaskId(communityArticleTaskId);
        communityArticleTaskReplyMapper.insert(communityArticleTaskReply);

        // 回复人数 + 1
        JSONObject data = new JSONObject();
        data.put("event", EventConstant.communityArticleTaskReplyCountAddOne);
        data.put("communityArticleTaskId", communityArticleTaskId);
        Message message = new Message(data.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.communityUpdateDirectExchange,
                RabbitmqRoutingName.communityUpdateRouting, message);
        return R.ok();
    }

    /**
     * 查询未提交任务成员列表
     *
     * @param communityArticleTaskId        社区任务成员id
     * @param communityArticleTaskReplyList 已提交用户集合
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/21 8:18
     */
    @Override
    public R queryNoSubmitTaskPeople(Long communityArticleTaskId, List<CommunityArticleTaskReply> communityArticleTaskReplyList) {
        // 得到所有任务指派的用户集合
        CommunityArticleTask communityArticleTask = communityArticleTaskMapper.selectById(communityArticleTaskId);
        String userIds[] = communityArticleTask.getUserIds().split(",");
        Set<Long> allUserList = new HashSet<>(userIds.length);
        for (String userId : userIds) {
            allUserList.add(Long.parseLong(userId));
        }

        // 得到未提交用户集合
        for (CommunityArticleTaskReply communityArticleTaskReply : communityArticleTaskReplyList) {
            Long userId = communityArticleTaskReply.getUserId();
            allUserList.remove(userId);
        }

        // 通过未提交用户集合id得到用户信息
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("id", allUserList);
        List<User> noSubmitUser = userMapper.selectList(userQueryWrapper);
        return R.ok(noSubmitUser);
    }

    /**
     * 查询当前登录用户对该文章的评分
     *
     * @param userId 当前登录用户id
     * @param communityArticleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/21 9:08
     */
    @Override
    public R queryUserArticleScore(String userId, Long communityArticleId) {
        if (userId == null || "".equals(userId)) {
            return R.ok(0);
        }
        QueryWrapper<CommunityArticleScore> communityArticleScoreQueryWrapper = new QueryWrapper<>();
        communityArticleScoreQueryWrapper.eq("user_id", userId);
        communityArticleScoreQueryWrapper.eq("community_article_id", communityArticleId);
        communityArticleScoreQueryWrapper.select("score");
        CommunityArticleScore communityArticleScore = communityArticleScoreMapper.selectOne(communityArticleScoreQueryWrapper);
        if (communityArticleScore == null || communityArticleScore.getScore() == null) {
            return R.ok(0);
        }
        return R.ok(communityArticleScore.getScore());
    }

    /**
     * 修改社区文章任务内容
     *
     * @param replyContent 回复内容
     * @param communityArticleTaskReplyId 社区文章任务回复id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/21 14:29
     */
    @Override
    public R confirmUpdate(String replyContent, Long communityArticleTaskReplyId) {
        UpdateWrapper<CommunityArticleTaskReply> communityArticleTaskReplyUpdateWrapper = new UpdateWrapper<>();
        communityArticleTaskReplyUpdateWrapper.eq("id", communityArticleTaskReplyId);
        communityArticleTaskReplyUpdateWrapper.set("reply_content", replyContent);
        communityArticleTaskReplyUpdateWrapper.set("update_time", new Date());
        communityArticleTaskReplyMapper.update(null, communityArticleTaskReplyUpdateWrapper);
        return R.ok();
    }

    /**
     * 查询任务提交历史记录
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param communityArticleTaskId 社区文章任务id
     * @param userId 当前登录用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/21 16:06
     */
    @Override
    public R queryTaskSubmitHistoryRecords(Integer currentPage, Integer pageSize, Long communityArticleTaskId, Long userId) {
        QueryWrapper<CommunityArticleTaskReply> communityArticleTaskReplyQueryWrapper = new QueryWrapper<>();
        communityArticleTaskReplyQueryWrapper.eq("community_article_task_id", communityArticleTaskId);
        communityArticleTaskReplyQueryWrapper.eq("user_id", userId);
        communityArticleTaskReplyQueryWrapper.orderByDesc("create_time");
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = communityArticleTaskReplyMapper.selectPage(page, communityArticleTaskReplyQueryWrapper);
        return R.ok(selectPage);
    }

    /**
     * 删除任务历史记录
     *
     * @param communityArticleTaskReplyId 社区文章任务回复id
     * @param communityArticleTaskId 社区文章任务id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/21 16:40
     */
    @Override
    public R deleteTaskHistoryRecord(Long communityArticleTaskReplyId, Long communityArticleTaskId) {
        JSONObject data = new JSONObject();
        data.put("event", EventConstant.deleteCommunityArticleTaskReply);
        data.put("communityArticleTaskReplyId", communityArticleTaskReplyId);
        data.put("communityArticleTaskId", communityArticleTaskId);
        Message message = new Message(data.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.communityDeleteDirectExchange,
                RabbitmqRoutingName.communityDeleteRouting, message);
        return R.ok();
    }

    /**
     * 导出提交成员数据至excel
     *
     * @param communityArticleTaskReplyList 导出的数据对象
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/22 11:24
     */
    @Override
    public void exportDataToExcel(List<CommunityArticleTaskReply> communityArticleTaskReplyList, HttpServletResponse response) throws IOException {
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode(ExcelTableNameConstant.communityArticleTaskSubmitInfo, "UTF-8")
                    .replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            // 这里需要设置不关闭流
            EasyExcel.write(response.getOutputStream(), CommunityArticleTaskReply.class)
                    .autoCloseStream(Boolean.FALSE)
                    .sheet(ExcelSheetNameConstant.sheet1)
                    .doWrite(communityArticleTaskReplyList);
        } catch (Exception e) {
            // 重置response
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.setStatus(R.Error);
            Map<String, Object> map = MapUtils.newHashMap();
            map.put("status", "failure");
            map.put("message", "下载文件失败, 错误原因为 ==> " + e.getMessage());
            response.getWriter().write(JSON.toJSONString(map));
        }
    }

    /**
     * 判断当前登录用户是否点赞该文章
     *
     * @param userId 当前登录用户id
     * @param communityArticleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/24 10:03
     */
    @Override
    public R judgeIsLikeArticle(String userId, Long communityArticleId) {
        if (userId == null || "".equals(userId)) {
            return R.ok(CommunityEnum.NOT_LIKE.getCode());
        }

        QueryWrapper<CommunityArticleLike> communityArticleLikeQueryWrapper = new QueryWrapper<>();
        communityArticleLikeQueryWrapper.eq("user_id", userId);
        communityArticleLikeQueryWrapper.eq("community_article_id", communityArticleId);
        Long selectCount = communityArticleLikeMapper.selectCount(communityArticleLikeQueryWrapper);
        if (selectCount > 0) {
            return R.ok(CommunityEnum.IS_LIKE.getCode());
        } else {
            return R.ok(CommunityEnum.NOT_LIKE.getCode());
        }
    }

    /**
     * 点赞文章
     *
     * @param userId 当前登录用户id
     * @param communityArticleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/24 10:17
     */
    @Override
    public R articleLike(long userId, Long communityArticleId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.communityArticleLike);
        jsonObject.put("userId", userId);
        jsonObject.put("communityArticleId", communityArticleId);
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.communityInsertDirectExchange,
                RabbitmqRoutingName.communityInsertRouting, message);
        return R.ok();
    }

    /**
     * 取消点赞文章
     *
     * @param userId 当前登录用户id
     * @param communityArticleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/24 10:18
     */
    @Override
    public R cancelArticleLike(long userId, Long communityArticleId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.communityArticleCancelLike);
        jsonObject.put("userId", userId);
        jsonObject.put("communityArticleId", communityArticleId);
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.communityInsertDirectExchange,
                RabbitmqRoutingName.communityInsertRouting, message);
        return R.ok();
    }

    /**
     * 判断当前登录用户是否收藏此社区文章
     *
     * @param userId 当前登录用户id
     * @param communityArticleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/24 14:01
     */
    @Override
    public R judgeIsCollectArticle(String userId, Long communityArticleId) {
        if (userId == null || "".equals(userId)) {
            return R.ok(CommonEnum.UNCOLLECT.getCode());
        }

        QueryWrapper<CollectContentConnect> collectContentConnectQueryWrapper = new QueryWrapper<>();
        collectContentConnectQueryWrapper.eq("user_id", userId);
        collectContentConnectQueryWrapper.eq("type", CommonEnum.COLLECT_COMMUNITY_ARTICLE.getCode());
        collectContentConnectQueryWrapper.eq("associate_id", communityArticleId);
        Long selectCount = collectContentConnectMapper.selectCount(collectContentConnectQueryWrapper);
        if (selectCount > 0) {
            return R.ok(CommonEnum.COLLECT.getCode());
        } else {
            return R.ok(CommonEnum.UNCOLLECT.getCode());
        }
    }

    /**
     * 判断当前登录用户是否是该文章的作者或者管理员
     *
     * @param userId             当前登录用户id
     * @param communityId          社区id
     * @param communityArticleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/23 9:19
     */
    @Override
    public R judgeIsAuthorOrManager(String userId, Long communityId, Long communityArticleId) {
        JSONObject jsonObject = new JSONObject();
        if (userId == null || "".equals(userId)) {
            jsonObject.put("isAuthor", CommunityEnum.NOT_AUTHOR.getCode());
            jsonObject.put("isManager", CommunityEnum.NOT_MANAGER.getCode());
            return R.ok(jsonObject);
        }

        // 判断是否是文章作者
        CommunityArticle communityArticle = communityArticleMapper.selectById(communityArticleId);
        Long articleUserId = communityArticle.getUserId();
        if (articleUserId.equals(Long.parseLong(userId))) {
            jsonObject.put("isAuthor", CommunityEnum.IS_AUTHOR.getCode());
        } else {
            jsonObject.put("isAuthor", CommunityEnum.NOT_AUTHOR.getCode());
        }

        // 判断是否是社区管理员
        QueryWrapper<CommunityUserManage> communityUserManageQueryWrapper = new QueryWrapper<>();
        communityUserManageQueryWrapper.eq("community_id", communityId);
        communityUserManageQueryWrapper.eq("user_id", userId);
        Long selectCount = communityUserManageMapper.selectCount(communityUserManageQueryWrapper);
        if (selectCount > 0) {
            jsonObject.put("isManager", CommunityEnum.IS_MANAGER.getCode());
        } else {
            jsonObject.put("isManager", CommunityEnum.NOT_MANAGER.getCode());
        }

        return R.ok(jsonObject);
    }

    /**
     * 查询社区文章频道名称
     *
     * @param communityArticleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/24 16:34
     */
    @Override
    public R queryCommunityArticleChannelName(Long communityArticleId) {
        CommunityArticle communityArticle = communityArticleMapper.selectById(communityArticleId);
        Long channelId = communityArticle.getChannelId();
        CommunityChannel communityChannel = communityChannelMapper.selectById(channelId);
        return R.ok(communityChannel.getChannelName());
    }

    /**
     * 修改社区文章频道
     *
     * @param channelId 频道id
     * @param communityArticleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/24 16:44
     */
    @Override
    public R updateCommunityArticleChannel(Long channelId, Long communityArticleId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.updateCommunityArticleChannel);
        jsonObject.put("channelId", channelId);
        jsonObject.put("communityArticleId", communityArticleId);
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.communityUpdateDirectExchange,
                RabbitmqRoutingName.communityUpdateRouting, message);

        return R.ok();
    }
}
