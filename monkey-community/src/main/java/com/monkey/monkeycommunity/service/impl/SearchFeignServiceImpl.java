package com.monkey.monkeycommunity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.mapper.CommunityArticleMapper;
import com.monkey.monkeycommunity.mapper.CommunityMapper;
import com.monkey.monkeycommunity.pojo.Community;
import com.monkey.monkeycommunity.pojo.CommunityArticle;
import com.monkey.monkeycommunity.service.SearchFeignService;
import com.monkey.spring_security.mapper.UserMapper;
import com.monkey.spring_security.pojo.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/11/11 16:33
 * @version: 1.0
 * @description:
 */
@Service
public class SearchFeignServiceImpl implements SearchFeignService {
    @Resource
    private CommunityArticleMapper communityArticleMapper;
    @Resource
    private CommunityMapper communityMapper;
    @Resource
    private UserMapper userMapper;
    /**
     * 查询所有社区文章数据
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/11 16:39
     */
    @Override
    public R queryAllCommunityArticle() {
        QueryWrapper<CommunityArticle> communityArticleQueryWrapper = new QueryWrapper<>();
        communityArticleQueryWrapper.eq("status", CommonEnum.SUCCESS.getCode());
        List<CommunityArticle> articles = communityArticleMapper.selectList(communityArticleQueryWrapper);
        articles.parallelStream().forEach(f -> {
            // 得到文章作者信息
            User user = userMapper.selectById(f.getUserId());
            f.setUsername(user.getUsername());
            f.setUserHeadImg(user.getPhoto());
            f.setUserBrief(user.getBrief());

            // 得到社区信息
            Long communityId = f.getCommunityId();
            Community community = communityMapper.selectById(communityId);
            f.setCommunityName(community.getName());

        });
        return R.ok(articles);
    }
}
