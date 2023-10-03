package com.monkey.monkeycommunity.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.constant.CommunityEnum;
import com.monkey.monkeycommunity.constant.CommunityRoleEnum;
import com.monkey.monkeycommunity.rabbitmq.EventConstant;
import com.monkey.monkeycommunity.mapper.*;
import com.monkey.monkeycommunity.pojo.*;
import com.monkey.monkeycommunity.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeycommunity.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeycommunity.service.CommunityService;
import com.monkey.spring_security.mapper.UserMapper;
import com.monkey.spring_security.pojo.User;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/9/1 17:37
 * @version: 1.0
 * @description:
 */
@Service
public class CommunityServiceImpl implements CommunityService {
    @Resource
    private CommunityClassificationLabelMapper communityClassificationLabelMapper;
    @Resource
    private CommunityArticleMapper communityArticleMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private CommunityChannelMapper communityChannelMapper;
    @Resource
    private CommunityMapper communityMapper;
    @Resource
    private CommunityLabelConnectMapper communityLabelConnectMapper;
    @Resource
    private CommunityUserRoleConnectMapper communityUserRoleConnectMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private CommunityUserApplicationMapper communityUserApplicationMapper;
    @Resource
    private CommunityUserManageMapper communityUserManageMapper;
    /**
     * 得到一级标签
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/1 17:44
     */
    @Override
    public R getOneLevelLabelList() {
        QueryWrapper<CommunityClassificationLabel> communityClassificationAttributeQueryWrapper = new QueryWrapper<>();
        communityClassificationAttributeQueryWrapper.eq("level", CommunityEnum.ONE_LEVEL_LABEL.getCode());
        communityClassificationAttributeQueryWrapper.orderByAsc("sort");
        communityClassificationAttributeQueryWrapper.orderByDesc("create_time");
        return R.ok(communityClassificationLabelMapper.selectList(communityClassificationAttributeQueryWrapper));
    }

    /**
     * 通过一级标签id得到二级标签列表
     *
     * @param labelOneId 一级标签id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/1 17:59
     */
    @Override
    public R getTwoLabelListByOneLabelId(long labelOneId) {
        QueryWrapper<CommunityClassificationLabel> communityClassificationAttributeQueryWrapper = new QueryWrapper<>();
        communityClassificationAttributeQueryWrapper.eq("parent_id", labelOneId);
        communityClassificationAttributeQueryWrapper.orderByAsc("sort");
        communityClassificationAttributeQueryWrapper.orderByDesc("create_time");
        return R.ok(communityClassificationLabelMapper.selectList(communityClassificationAttributeQueryWrapper));
    }

    /**
     * 得到与我有关社区文章列表
     *
     * @param userId 当前登录用户id
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/2 17:37
     */
    @Override
    public R queryWithMeArticleList(long userId, int currentPage, int pageSize) {
        QueryWrapper<CommunityArticle> communityArticleQueryWrapper = new QueryWrapper<>();
        communityArticleQueryWrapper.eq("user_id", userId);
        communityArticleQueryWrapper.eq("status", CommonEnum.SUCCESS.getCode());
        communityArticleQueryWrapper.orderByDesc("create_time");
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = communityArticleMapper.selectPage(page, communityArticleQueryWrapper);
        List<CommunityArticle> records = selectPage.getRecords();

        // 得到communityArticleListVo集合
        List<CommunityArticle> communityArticleList = getCommunityArticle(records);

        selectPage.setRecords(communityArticleList);
        return R.ok(selectPage);
    }

    /**
     * 得到点赞最多文章列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/2 19:34
     */
    @Override
    public R queryLikeArticleList(int currentPage, int pageSize) {
        QueryWrapper<CommunityArticle> communityArticleQueryWrapper = new QueryWrapper<>();
        communityArticleQueryWrapper.orderByDesc("like_count");
        communityArticleQueryWrapper.orderByDesc("create_time");
        communityArticleQueryWrapper.eq("status", CommonEnum.SUCCESS.getCode());
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = communityArticleMapper.selectPage(page, communityArticleQueryWrapper);
        List<CommunityArticle> records = selectPage.getRecords();

        // 得到communityArticleListVo集合
        List<CommunityArticle> communityArticleList = getCommunityArticle(records);

        selectPage.setRecords(communityArticleList);
        return R.ok(selectPage);
    }

    /**
     * 得到回复最多文章列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/2 19:51
     */
    @Override
    public R queryReplyArticleList(int currentPage, int pageSize) {
        QueryWrapper<CommunityArticle> communityArticleQueryWrapper = new QueryWrapper<>();
        communityArticleQueryWrapper.orderByDesc("comment_count");
        communityArticleQueryWrapper.orderByDesc("create_time");
        communityArticleQueryWrapper.eq("status", CommonEnum.SUCCESS.getCode());
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = communityArticleMapper.selectPage(page, communityArticleQueryWrapper);
        List<CommunityArticle> records = selectPage.getRecords();

        // 得到communityArticleListVo集合
        List<CommunityArticle> communityArticleList = getCommunityArticle(records);

        selectPage.setRecords(communityArticleList);
        return R.ok(selectPage);
    }

    /**
     * 得到游览最多文章列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/2 19:52
     */
    @Override
    public R queryViewArticleList(int currentPage, int pageSize) {
        QueryWrapper<CommunityArticle> communityArticleQueryWrapper = new QueryWrapper<>();
        communityArticleQueryWrapper.orderByDesc("view_count");
        communityArticleQueryWrapper.orderByDesc("create_time");
        communityArticleQueryWrapper.eq("status", CommonEnum.SUCCESS.getCode());
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = communityArticleMapper.selectPage(page, communityArticleQueryWrapper);
        List<CommunityArticle> records = selectPage.getRecords();

        // 得到communityArticleListVo集合
        List<CommunityArticle> communityArticleList = getCommunityArticle(records);

        selectPage.setRecords(communityArticleList);
        return R.ok(selectPage);
    }

    /**
     * 得到收藏最高文章列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/2 19:52
     */
    @Override
    public R queryCollectArticleList(int currentPage, int pageSize) {
        QueryWrapper<CommunityArticle> communityArticleQueryWrapper = new QueryWrapper<>();
        communityArticleQueryWrapper.orderByDesc("collect_count");
        communityArticleQueryWrapper.orderByDesc("create_time");
        communityArticleQueryWrapper.eq("status", CommonEnum.SUCCESS.getCode());
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = communityArticleMapper.selectPage(page, communityArticleQueryWrapper);
        List<CommunityArticle> records = selectPage.getRecords();

        // 得到communityArticleListVo集合
        List<CommunityArticle> communityArticleList = getCommunityArticle(records);

        selectPage.setRecords(communityArticleList);
        return R.ok(selectPage);
    }

    /**
     * 得到最热文章列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/2 19:55
     */
    @Override
    public R queryHireArticleList(int currentPage, int pageSize) {
        QueryWrapper<CommunityArticle> communityArticleQueryWrapper = new QueryWrapper<>();
        communityArticleQueryWrapper.orderByDesc("score");
        communityArticleQueryWrapper.orderByDesc("view_count");
        communityArticleQueryWrapper.orderByDesc("like_count");
        communityArticleQueryWrapper.orderByDesc("comment_count");
        communityArticleQueryWrapper.orderByDesc("collect_count");
        communityArticleQueryWrapper.orderByDesc("score_count");
        communityArticleQueryWrapper.orderByDesc("create_time");
        communityArticleQueryWrapper.eq("status", CommonEnum.SUCCESS.getCode());
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = communityArticleMapper.selectPage(page, communityArticleQueryWrapper);
        List<CommunityArticle> records = selectPage.getRecords();

        // 得到communityArticleListVo集合
        List<CommunityArticle> communityArticleList = getCommunityArticle(records);

        selectPage.setRecords(communityArticleList);
        return R.ok(selectPage);
    }

    /**
     * 得到最新文章列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/2 19:56
     */
    @Override
    public R queryLatestArticleList(int currentPage, int pageSize) {
        QueryWrapper<CommunityArticle> communityArticleQueryWrapper = new QueryWrapper<>();
        communityArticleQueryWrapper.orderByDesc("create_time");
        communityArticleQueryWrapper.eq("status", CommonEnum.SUCCESS.getCode());
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = communityArticleMapper.selectPage(page, communityArticleQueryWrapper);
        List<CommunityArticle> records = selectPage.getRecords();

        // 得到communityArticleListVo集合
        List<CommunityArticle> communityArticleList = getCommunityArticle(records);

        selectPage.setRecords(communityArticleList);
        return R.ok(selectPage);
    }

    /**
     * 通过搜索字段得到一级标签
     *
     * @param search 模糊搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/2 20:34
     */
    @Override
    public R queryOneLabel(String search) {
        QueryWrapper<CommunityClassificationLabel> communityClassificationLabelQueryWrapper = new QueryWrapper<>();
        communityClassificationLabelQueryWrapper.like("name", search);
        communityClassificationLabelQueryWrapper.eq("level", CommunityEnum.ONE_LEVEL_LABEL.getCode());
        communityClassificationLabelQueryWrapper.orderByAsc("sort");
        communityClassificationLabelQueryWrapper.orderByDesc("create_time");
        return R.ok(communityClassificationLabelMapper.selectList(communityClassificationLabelQueryWrapper));
    }

    /**
     * 通过条件搜索文章
     *
     * @param communityName 社区名
     * @param communityClassificationLabelList 标签实体类
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/2 20:51
     */
    @Override
    public R searchArticle(String communityName, List<CommunityClassificationLabel> communityClassificationLabelList, int currentPage, int pageSize) {
        Page page = new Page<>(currentPage, pageSize);
        QueryWrapper<CommunityLabelConnect> communityLabelConnectQueryWrapper = new QueryWrapper<>();
        List<Long> list = new ArrayList<>();
        for (CommunityClassificationLabel communityClassificationLabel : communityClassificationLabelList) {
            list.add(communityClassificationLabel.getId());
        }
        communityLabelConnectQueryWrapper.in("community_classification_label_id", list);
        communityLabelConnectQueryWrapper.orderByDesc("create_time");
        Page selectPage = communityLabelConnectMapper.selectPage(page, communityLabelConnectQueryWrapper);
        List<CommunityLabelConnect> communityLabelConnectList = selectPage.getRecords();
        List<Community> communities = new ArrayList<>();
        for (CommunityLabelConnect communityLabelConnect : communityLabelConnectList) {
            Long communityId = communityLabelConnect.getCommunityId();
            Community community = communityMapper.selectById(communityId);
            communities.add(community);
        }

        selectPage.setRecords(communities);

        return R.ok(selectPage);
    }

    /**
     * 查询热门社区列表
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/2 21:30
     */
    @Override
    public R queryHireCommunityList(String userId) {
        QueryWrapper<Community> communityQueryWrapper = new QueryWrapper<>();
        communityQueryWrapper.orderByDesc("article_count");
        communityQueryWrapper.orderByDesc("member_count");
        communityQueryWrapper.orderByDesc("create_time");
        communityQueryWrapper.last("limit 10");
        List<Community> communityList = communityMapper.selectList(communityQueryWrapper);
        if (userId != null && !"".equals(userId)) {
            // 判断该用户是否加入该社区
            for (Community community : communityList) {
                Long communityId = community.getId();
                QueryWrapper<CommunityUserRoleConnect> communityRoleConnectQueryWrapper = new QueryWrapper<>();
                communityRoleConnectQueryWrapper.eq("community_id", communityId);
                communityRoleConnectQueryWrapper.eq("user_id", userId);
                CommunityUserRoleConnect communityUserRoleConnect = communityUserRoleConnectMapper.selectOne(communityRoleConnectQueryWrapper);
                if (communityUserRoleConnect != null) {
                    community.setIsAdd(CommunityEnum.APPROVE_EXAMINE.getCode());
                } else {
                    // 判断用户是否申请加入社区
                    QueryWrapper<CommunityUserApplication> communityUserApplicationQueryWrapper = new QueryWrapper<>();
                    communityUserApplicationQueryWrapper.eq("community_id", communityId);
                    communityUserApplicationQueryWrapper.eq("user_id", userId);
                    communityUserApplicationQueryWrapper.select("status");
                    CommunityUserApplication communityUserApplication = communityUserApplicationMapper.selectOne(communityUserApplicationQueryWrapper);
                    if (communityUserApplication == null) {
                        community.setIsAdd(CommunityEnum.NOT_APPLICATION.getCode());
                    } else {
                        community.setIsAdd(communityUserApplication.getStatus());
                    }
                }
            }
        }
        return R.ok(communityList);

    }

    /**
     * 查询最新社区列表
     *
     * @param userId 当前登录用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/2 21:34
     */
    @Override
    public R queryLatestCommunityList(String userId) {
        QueryWrapper<Community> communityQueryWrapper = new QueryWrapper<>();
        communityQueryWrapper.orderByDesc("create_time");
        communityQueryWrapper.last("limit 10");
        List<Community> communityList = communityMapper.selectList(communityQueryWrapper);

        if (userId != null && !"".equals(userId)) {
            // 判断该用户是否加入该社区
            for (Community community : communityList) {
                Long communityId = community.getId();
                QueryWrapper<CommunityUserRoleConnect> communityRoleConnectQueryWrapper = new QueryWrapper<>();
                communityRoleConnectQueryWrapper.eq("community_id", communityId);
                communityRoleConnectQueryWrapper.eq("user_id", userId);
                CommunityUserRoleConnect communityUserRoleConnect = communityUserRoleConnectMapper.selectOne(communityRoleConnectQueryWrapper);
                if (communityUserRoleConnect != null) {
                    community.setIsAdd(CommunityEnum.APPROVE_EXAMINE.getCode());
                } else {
                    // 判断用户是否申请加入社区
                    QueryWrapper<CommunityUserApplication> communityUserApplicationQueryWrapper = new QueryWrapper<>();
                    communityUserApplicationQueryWrapper.eq("community_id", communityId);
                    communityUserApplicationQueryWrapper.eq("user_id", userId);
                    communityUserApplicationQueryWrapper.select("status");
                    CommunityUserApplication communityUserApplication = communityUserApplicationMapper.selectOne(communityUserApplicationQueryWrapper);
                    if (communityUserApplication == null) {
                        community.setIsAdd(CommunityEnum.NOT_APPLICATION.getCode());
                    } else {
                        community.setIsAdd(communityUserApplication.getStatus());
                    }
                }
            }
        }
        return R.ok(communityList);
    }

    /**
     * 查询我的社区列表
     *
     * @param userId 当前登录用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/2 21:49
     */
    @Override
    public R queryWithMeCommunityList(long userId) {
        QueryWrapper<CommunityUserRoleConnect> communityRoleConnectQueryWrapper = new QueryWrapper<>();
        communityRoleConnectQueryWrapper.eq("user_id", userId);
        communityRoleConnectQueryWrapper.orderByDesc("create_time");
        List<CommunityUserRoleConnect> communityUserRoleConnectList = communityUserRoleConnectMapper.selectList(communityRoleConnectQueryWrapper);
        List<Community> communityList = new ArrayList<>();
        for (CommunityUserRoleConnect communityUserRoleConnect : communityUserRoleConnectList) {
            Long communityId = communityUserRoleConnect.getCommunityId();
            Community community = communityMapper.selectById(communityId);
            communityList.add(community);
        }
        return R.ok(communityList);
    }

    /**
     * 加入社区实现
     *
     * @param userId    当前登录用户id
     * @param community 社区id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/3 12:00
     */
    @Override
    public R  applicationAddCommunity(long userId, Community community) {
        Long communityId = community.getId();
        // 判断该社区加入状态
        Integer enterWay = community.getEnterWay();
        Date date = new Date();
        if (enterWay.equals(CommunityEnum.NO_RESTRAIN.getCode())) {
            // 无限制，直接加入
            CommunityUserRoleConnect communityUserRoleConnect = new CommunityUserRoleConnect();
            communityUserRoleConnect.setCommunityId(communityId);
            communityUserRoleConnect.setRoleId(CommunityRoleEnum.MEMBER.getCode());
            communityUserRoleConnect.setUserId(userId);
            communityUserRoleConnect.setCreateTime(date);
            communityUserRoleConnect.setUpdateTime(date);
            communityUserRoleConnectMapper.insert(communityUserRoleConnect);

            // 社区成员数 + 1
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("event", EventConstant.communityMemberCountAddOne);
            jsonObject.put("communityId", communityId);
            Message message = new Message(jsonObject.toJSONString().getBytes());
            rabbitTemplate.convertAndSend(RabbitmqExchangeName.communityUpdateDirectExchange,
                    RabbitmqRoutingName.communityUpdateRouting, message);
        } else {
            // 有限制加入用户申请表
            JSONObject data = new JSONObject();
            data.put("event", EventConstant.addCommunityUserApplication);
            data.put("communityId", communityId);
            data.put("userId", userId);
            Message message = new Message(data.toJSONString().getBytes());
            rabbitTemplate.convertAndSend(RabbitmqExchangeName.communityInsertDirectExchange,
                    RabbitmqRoutingName.communityInsertRouting, message);
        }


        return R.ok();
    }

    /**
     * 退出社区实现
     *
     * @param userId 当前用户登录id
     * @param communityId 社区id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/3 12:13
     */
    @Override
    public R turnOutCommunity(long userId, Long communityId) {
        QueryWrapper<CommunityUserRoleConnect> communityRoleConnectQueryWrapper = new QueryWrapper<>();
        communityRoleConnectQueryWrapper.eq("user_id", userId);
        communityRoleConnectQueryWrapper.eq("community_id", communityId);
        communityUserRoleConnectMapper.delete(communityRoleConnectQueryWrapper);

        // 社区成员数 - 1
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.getCommunityMemberCountSubOne);
        jsonObject.put("communityId", communityId);
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.communityUpdateDirectExchange,
                RabbitmqRoutingName.communityUpdateRouting, message);
        return R.ok();
    }

    /**
     * 得到communityArticleListVo集合
     *
     * @param communityArticleList 集合类
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/2 17:47
     */
    @Override
    public List<CommunityArticle> getCommunityArticle(List<CommunityArticle> communityArticleList) {
        for (CommunityArticle communityArticle : communityArticleList) {
            // 得到用户信息
            Long userId = communityArticle.getUserId();
            User user = userMapper.selectById(userId);
            communityArticle.setUsername(user.getUsername());
            communityArticle.setUserHeadImg(user.getPhoto());

            // 得到社区信息
            Long communityId = communityArticle.getCommunityId();
            Community community = communityMapper.selectById(communityId);
            communityArticle.setCommunityName(community.getName());

            // 得到频道信息
            Long channelId = communityArticle.getChannelId();
            CommunityChannel communityChannel = communityChannelMapper.selectById(channelId);
            communityArticle.setChannelName(communityChannel.getChannelName());
        }

        return communityArticleList;
    }

    /**
     * 社区文章游览数 + 1
     *
     * @param communityArticleId 社区文章 id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/20 9:15
     */
    @Override
    public R communityArticleViewCountAddOne(Long communityArticleId) {
        JSONObject data = new JSONObject();
        data.put("event", EventConstant.communityArticleViewCountAddOne);
        data.put("communityArticleId", communityArticleId);
        Message message = new Message(data.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.communityUpdateDirectExchange,
                RabbitmqRoutingName.communityUpdateRouting, message);
        return R.ok();
    }

    /**
     * 查询用户管理社区集合
     *
     * @param userId 当前登录用户id
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/27 8:49
     */
    @Override
    public R queryUserManageCommunity(String userId, Long currentPage, Integer pageSize) {
        if (userId == null || "".equals(userId)) {
            return R.ok(new Page<>(currentPage, pageSize));
        }

        Page page = new Page<>(currentPage, pageSize);
        QueryWrapper<CommunityUserManage> communityUserManageQueryWrapper = new QueryWrapper<>();
        communityUserManageQueryWrapper.eq("user_id", userId);
        communityUserManageQueryWrapper.select("community_id");

        Page selectPage = communityUserManageMapper.selectPage(page, communityUserManageQueryWrapper);
        List<CommunityUserManage> records = selectPage.getRecords();
        List<Long> communityIdList = new ArrayList<>(records.size());
        for (CommunityUserManage communityUserManage : records) {
            communityIdList.add(communityUserManage.getCommunityId());
        }
        QueryWrapper<Community> communityQueryWrapper = new QueryWrapper<>();

        communityQueryWrapper.in("id", communityIdList);
        List<Community> communityList = communityMapper.selectList(communityQueryWrapper);
        selectPage.setRecords(communityList);
        return R.ok(selectPage);
    }
}
