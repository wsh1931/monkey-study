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
import com.monkey.monkeyUtils.util.DateUtils;
import com.monkey.monkeyblog.feign.*;
import com.monkey.monkeyblog.pojo.Vo.CollectVo;
import com.monkey.monkeyblog.service.create.CreateHomeService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.xml.ws.RequestWrapper;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/12/14 10:13
 * @version: 1.0
 * @description:
 */
@Service
public class CreateHomeServiceImpl implements CreateHomeService {
    @Resource
    private UserOpusStatisticsMapper userOpusStatisticsMapper;
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
    /**
     * 得到用户一年中所发表的文章数
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/14 10:17
     */
    @Override
    public R queryUserOpusCountInYear() {
        // 得到今年第一天
        String currentYearFirstDay = DateUtils.getCurrentYearFirstDay(DateUtils.DATE_PATTERN);
        // 得到当前日期
        String nowDate = DateUtils.getNowDate(DateUtils.DATE_PATTERN);
        LambdaQueryWrapper<UserOpusStatistics> userOpusStatisticsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userOpusStatisticsLambdaQueryWrapper.ge(UserOpusStatistics::getCreateTime, currentYearFirstDay)
                .le(UserOpusStatistics::getCreateTime, nowDate);
        userOpusStatisticsLambdaQueryWrapper.orderByAsc(UserOpusStatistics::getCreateTime);
        userOpusStatisticsLambdaQueryWrapper.eq(UserOpusStatistics::getAuthorId, JwtUtil.getUserId());
        userOpusStatisticsLambdaQueryWrapper.select(UserOpusStatistics::getOpusCount, UserOpusStatistics::getCreateTime);
        List<UserOpusStatistics> userOpusStatistics = userOpusStatisticsMapper.selectList(userOpusStatisticsLambdaQueryWrapper);
        return R.ok(userOpusStatistics);
    }

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
}
