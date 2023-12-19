package com.monkey.monkeyblog.service.Impl.create;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.monkey.monkeyUtils.constants.CollectEnum;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.mapper.CollectContentConnectMapper;
import com.monkey.monkeyUtils.mapper.UserOpusStatisticsMapper;
import com.monkey.monkeyUtils.pojo.CollectContentConnect;
import com.monkey.monkeyUtils.pojo.UserOpusStatistics;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import com.monkey.monkeyUtils.util.DateSelfUtils;
import com.monkey.monkeyUtils.util.DateUtils;
import com.monkey.monkeyblog.feign.*;
import com.monkey.monkeyblog.pojo.Vo.CollectVo;
import com.monkey.monkeyblog.service.create.CreateHomeService;
import io.swagger.models.auth.In;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author: wusihao
 * @date: 2023/12/14 10:13
 * @version: 1.0
 * @description:
 */
@Service
public class CreateHomeServiceImpl implements CreateHomeService {

    @Resource
    private CollectContentConnectMapper collectContentConnectMapper;
    @Resource
    private UserToArticleFeignService userToArticleFeignService;
    @Resource
    private UserToQuestionFeignService userToQuestionFeignService;
    @Resource
    private UserToCourseFeignService userToCourseFeignService;
    @Resource
    private UserToCommunityFeignService userToCommunityFeignService;
    @Resource
    private UserToResourceFeignService userToResourceFeignService;
    @Resource
    private UserOpusStatisticsMapper userOpusStatisticsMapper;

    /**
     * 查询用户近期收藏信息
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/14 11:47
     */
    @Override
    public R queryRecentlyCollect() {
        LambdaQueryWrapper<CollectContentConnect> collectContentConnectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        collectContentConnectLambdaQueryWrapper.eq(CollectContentConnect::getUserId, JwtUtil.getUserId());
        collectContentConnectLambdaQueryWrapper.orderByDesc(CollectContentConnect::getCreateTime);
        collectContentConnectLambdaQueryWrapper.last("limit 3");
        // 最终返回集合
        List<CollectVo> collectVoList = new ArrayList<>();
        List<CollectContentConnect> collectContentConnectList = collectContentConnectMapper.selectList(collectContentConnectLambdaQueryWrapper);
        for (CollectContentConnect collectContentConnect : collectContentConnectList) {
            CollectVo collectVo = new CollectVo();
            BeanUtils.copyProperties(collectContentConnect, collectVo);
            Integer type = collectContentConnect.getType();
            Long associateId = collectContentConnect.getAssociateId();
            CollectEnum collectEnum = CollectEnum.getCollectEnum(type);
            R result = null;
            switch (collectEnum) {
                case COLLECT_ARTICLE:
                    result = userToArticleFeignService.queryArticleById(associateId);
                    break;
                case COLLECT_QUESTION:
                    result = userToQuestionFeignService.queryQuestionById(associateId);
                    break;
                case COLLECT_COMMUNITY_ARTICLE:
                    result = userToCommunityFeignService.queryCommunityArticleById(associateId);
                    break;
                case COLLECT_COURSE:
                    result = userToCourseFeignService.queryCourseById(associateId);
                    break;
                case COLLECT_RESOURCE:
                    result = userToResourceFeignService.queryResourceById(associateId);
                    break;
            }

            if (result != null) {
                if (result.getCode() != R.SUCCESS) {
                    throw new MonkeyBlogException(R.Error, result.getMsg());
                }

                JSONObject jsonObject = (JSONObject)result.getData(new TypeReference<JSONObject>(){});
                Long viewCount = jsonObject.getLong("viewCount");
                Integer commentCount = jsonObject.getInteger("commentCount");
                Integer collectCount = jsonObject.getInteger("collectCount");
                String brief = jsonObject.getString("brief");
                collectVo.setViewCount(viewCount);
                collectVo.setCommentCount(commentCount);
                collectVo.setCollectCount(collectCount);
                collectVo.setBrief(brief);

                // 得到收藏类型名称
                collectVo.setTypename(collectEnum.getMsg());
                collectVoList.add(collectVo);
            }
        }
        return R.ok(collectVoList);
    }

    /**
     * 查询用户近一周原文数
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/18 10:56
     */
    @Override
    public R queryUserOpusInfoRecentWeek() {
        String userId = JwtUtil.getUserId();
        // 一周内所有日期文章
        // 得到一周前数据
        Date before = DateUtils.addDateDays(new Date(), -6);
        Date now = new Date();
        // 得到一周内所有日期
        List<Integer> articleCount = new ArrayList<>();
        List<Integer> questionCount = new ArrayList<>();
        List<Integer> courseCount = new ArrayList<>();
        List<Integer> communityArticleCount = new ArrayList<>();
        List<Integer> resourceCount = new ArrayList<>();
        List<Date> beenTwoDayAllDate = DateSelfUtils.getBeenTwoDayAllDate(before, now);
        beenTwoDayAllDate.forEach(date -> {
            LambdaQueryWrapper<UserOpusStatistics> userOpusStatisticsLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userOpusStatisticsLambdaQueryWrapper.eq(UserOpusStatistics::getCreateTime, DateUtils.format(date));
            userOpusStatisticsLambdaQueryWrapper.eq(UserOpusStatistics::getUserId, userId);
            UserOpusStatistics userOpusStatistics1 = userOpusStatisticsMapper.selectOne(userOpusStatisticsLambdaQueryWrapper);
            if (userOpusStatistics1 == null) {
                articleCount.add(0);
                questionCount.add(0);
                courseCount.add(0);
                communityArticleCount.add(0);
                resourceCount.add(0);
            } else {
                articleCount.add(userOpusStatistics1.getArticleCount());
                questionCount.add(userOpusStatistics1.getQuestionCount());
                courseCount.add(userOpusStatistics1.getCourseCount());
                communityArticleCount.add(userOpusStatistics1.getCommunityArticleCount());
                resourceCount.add(userOpusStatistics1.getResourceCount());
            }
        });

        List<String> dates = new ArrayList<>();
        beenTwoDayAllDate.forEach(time -> dates.add(DateUtils.format(time)));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("articleCountList", articleCount);
        jsonObject.put("questionCountList", questionCount);
        jsonObject.put("courseCountList", courseCount);
        jsonObject.put("communityArticleCountList", communityArticleCount);
        jsonObject.put("resourceCountList", resourceCount);
        jsonObject.put("dateList", dates);

        return R.ok(jsonObject);
    }
}
