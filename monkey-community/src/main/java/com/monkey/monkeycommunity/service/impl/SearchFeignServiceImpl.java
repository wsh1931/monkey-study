package com.monkey.monkeycommunity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.mapper.*;
import com.monkey.monkeycommunity.pojo.*;
import com.monkey.monkeycommunity.service.SearchFeignService;
import com.monkey.spring_security.mapper.UserMapper;
import com.monkey.spring_security.pojo.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
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
    @Resource
    private CommunityLabelConnectMapper communityLabelConnectMapper;
    @Resource
    private CommunityClassificationLabelMapper communityClassificationLabelMapper;

    @Resource
    private CommunityAttributeMapper communityAttributeMapper;
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

    /**
     * 查询所有社区数据
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/12 10:25
     */
    @Override
    public R queryAllCommunity() {
        List<Community> communityList = communityMapper.selectList(null);
        communityList.parallelStream().forEach(community -> {
            Long communityId = community.getId();
            // 得到社区属性信息
            Long attributeLabelId = community.getAttributeLabelId();
            CommunityAttribute communityAttribute = communityAttributeMapper.selectById(attributeLabelId);
            community.setAttributeLabelName(communityAttribute.getName());

            // 得到社区分类标签
            Long classificationId = community.getClassificationId();
            CommunityClassificationLabel communityClassificationLabel = communityClassificationLabelMapper.selectById(classificationId);
            community.setClassificationName(communityClassificationLabel.getName());

            // 得到社区内容标签
            QueryWrapper<CommunityLabelConnect> communityLabelConnectQueryWrapper = new QueryWrapper<>();
            communityLabelConnectQueryWrapper.eq("community_id", communityId);
            communityLabelConnectQueryWrapper.select("community_classification_label_id");
            List<Object> labelIdList = communityLabelConnectMapper.selectObjs(communityLabelConnectQueryWrapper);
            if (labelIdList != null || labelIdList.size() > 0) {
                QueryWrapper<CommunityClassificationLabel> communityClassificationLabelQueryWrapper = new QueryWrapper<>();
                communityClassificationLabelQueryWrapper.in("id", labelIdList);
                List<CommunityClassificationLabel> communityClassificationLabels = communityClassificationLabelMapper.selectList(communityClassificationLabelQueryWrapper);
                List<String> contentLabelName = new ArrayList<>();
                for (CommunityClassificationLabel classificationLabel : communityClassificationLabels) {
                    contentLabelName.add(classificationLabel.getName());
                }
                community.setContentLabelName(contentLabelName);
            }
        });

        return R.ok(communityList);
    }
}
