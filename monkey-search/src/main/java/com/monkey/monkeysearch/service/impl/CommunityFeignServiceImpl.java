package com.monkey.monkeysearch.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeysearch.constant.IndexConstant;
import com.monkey.monkeysearch.pojo.ESArticleIndex;
import com.monkey.monkeysearch.pojo.ESCommunityIndex;
import com.monkey.monkeysearch.service.CommunityFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/11/12 14:24
 * @version: 1.0
 * @description:
 */
@Slf4j
@Service
public class CommunityFeignServiceImpl implements CommunityFeignService {
    @Resource
    private ElasticsearchClient elasticsearchClient;
    /**
     * 社区成员数 + 1
     *
     * @param communityId 社区id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/12 14:32
     */
    @Override
    public R communityMemberAddOne(Long communityId) {
        try {
            log.info("elasticsearch社区成员数 + 1, communityId = {}", communityId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.community)
                    .id(String.valueOf(communityId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.memberCount += 1"))), ESCommunityIndex.class);
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 社区成员数 - 1
     *
     * @param communityId 社区id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/12 14:32
     */
    @Override
    public R communityMemberSubOne(Long communityId) {
        try {
            log.info("elasticsearch社区成员数 - 1, communityId = {}", communityId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.community)
                    .id(String.valueOf(communityId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.memberCount -= 1"))), ESCommunityIndex.class);
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }
    
    /**
     * 社区文章数 + 1
     *
     * @param communityId 社区id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/12 14:32
     */
    @Override
    public R communityArticleAddOne(Long communityId) {
        try {
            log.info("elasticsearch社区文章 + 1, communityId = {}", communityId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.community)
                    .id(String.valueOf(communityId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.articleCount += 1"))), ESCommunityIndex.class);
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 社区文章数 - 1
     *
     * @param communityId 社区id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/12 14:32
     */
    @Override
    public R communityArticleSubOne(Long communityId) {
        try {
            log.info("elasticsearch社区文章 - 1, communityId = {}", communityId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.community)
                    .id(String.valueOf(communityId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.articleCount -= 1"))), ESCommunityIndex.class);
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 创建社区
     *
     * @param esCommunityIndex 社区索引类
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/12 14:32
     */
    @Override
    public R createCommunity(ESCommunityIndex esCommunityIndex) {
        try {
            log.info("elasticsearch创建社区, community = {}", esCommunityIndex);
            elasticsearchClient.create(create -> create
                    .id(esCommunityIndex.getId())
                    .index(IndexConstant.community)
                    .document(esCommunityIndex));
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }
}
