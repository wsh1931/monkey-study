package com.monkey.monkeysearch.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.Buckets;
import co.elastic.clients.elasticsearch._types.aggregations.LongTermsAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.LongTermsBucket;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperationBase;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.util.CommonMethods;
import com.monkey.monkeysearch.constant.IndexConstant;
import com.monkey.monkeysearch.constant.SearchTypeEnum;
import com.monkey.monkeysearch.pojo.Achievement;
import com.monkey.monkeysearch.pojo.ESArticleIndex;
import com.monkey.monkeysearch.pojo.ESCommunityArticleIndex;
import com.monkey.monkeysearch.pojo.ESCommunityIndex;
import com.monkey.monkeysearch.service.CommunityFeignService;
import com.monkey.monkeysearch.util.ESCommonMethods;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.create.table.Index;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    /**
     * 删除社区
     *
     * @param communityId 社区id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/25 17:37
     */
    @Override
    public R deleteCommunity(Long communityId) {
        try {
            // 删除社区
            deleteCommunityByCommunityId(communityId);


            // 查询该社区文章每个用户对应的的点赞数，游览数，收藏数总和
            SearchResponse<ESCommunityArticleIndex>  response =  queryCommunityArticleAchievement(communityId);
            // 得到该社区文章每个用户对应的的点赞数，游览数，收藏数总和
            Map<Achievement, Long> communityArticle = ESCommonMethods.getAchievement(response);


            // 得到社区文章id
            List<String> communityArticleIdList = getCommunityArticleIdList(response);

            if (communityArticleIdList.size() > 0) {
                // 批量删除社区文章
                bulkDeleteCommunityArticle(communityArticleIdList);

                // 批量减去用户对应的游览数 点赞数，收藏数
                ESCommonMethods.bulkSubUserAchievement(communityArticle, elasticsearchClient);

            }
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * elasticsearch删除社区
     *
     * @param communityId 社区id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/26 20:45
     */
    private void deleteCommunityByCommunityId(Long communityId) {
        try {
            log.info("elasticsearch删除社区, communityId == > {}", communityId);
            elasticsearchClient.delete(delete -> delete
                    .id(String.valueOf(communityId))
                    .index(IndexConstant.community));

            log.info("删除全部索引表中的社区信息 ==> communityId = {}", communityId);
            elasticsearchClient.deleteByQuery(delete -> delete
                    .index(IndexConstant.all)
                    .query(query -> query
                            .match(match -> match
                                    .field("type")
                                    .query(SearchTypeEnum.COMMUNITY.getCode())
                                    .field("associationId")
                                    .query(String.valueOf(communityId)))));
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 得到社区文章id集合
     *
     * @param response elasticsearch查找到的社区文章信息
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/26 20:43
     */
    private List<String> getCommunityArticleIdList(SearchResponse<ESCommunityArticleIndex> response) {
        try {
            log.info("得到社区文章id集合");
            List<String> communityArticleIdList = new ArrayList<>();
            response.hits().hits().stream().forEach(hit -> {
                ESCommunityArticleIndex source = hit.source();
                if (source != null) {
                    communityArticleIdList.add(source.getId());
                }
            });

            return communityArticleIdList;
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询该社区文章每个用户对应的的点赞数，游览数，收藏数总和
     *
     * @param communityId 社区id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/26 20:39
     */
    private SearchResponse<ESCommunityArticleIndex> queryCommunityArticleAchievement(Long communityId) {
        try {
            log.info("查询该社区文章每个用户对应的的点赞数，游览数，收藏数总和");
            SearchResponse<ESCommunityArticleIndex> response = elasticsearchClient.search(search -> search
                            .index(IndexConstant.communityArticle)
                            .query(query -> query
                                    .match(match -> match
                                            .field("communityId")
                                            .query(String.valueOf(communityId))))
                            .aggregations("userId", ag -> ag.terms(term -> term
                                            .field("userId"))
                                    .aggregations("likeCount", aggregation -> aggregation
                                            .sum(sum -> sum
                                                    .field("likeCount")))
                                    .aggregations("viewCount", aggregation -> aggregation
                                            .sum(sum -> sum
                                                    .field("viewCount")))
                                    .aggregations("CollectCount", aggregation -> aggregation
                                            .sum(sum -> sum
                                                    .field("CollectCount")))
                                    .aggregations("CommentCount", aggregation -> aggregation
                                            .sum(sum -> sum
                                                    .field("CommentCount"))))
                    , ESCommunityArticleIndex.class);

            return response;
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }

    }



    /**
     * 批量删除社区文章
     *
     * @param communityArticleIdList 社区文章id集合
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/26 20:32
     */
    private void bulkDeleteCommunityArticle(List<String> communityArticleIdList) {
        try {
            log.info("批量删除社区文章");
            BulkRequest.Builder deleteCommunityArticle = new BulkRequest.Builder();
            communityArticleIdList.stream().forEach(communityArticleId -> {
                deleteCommunityArticle.operations(op -> op
                        .delete(delete -> delete
                                .id(communityArticleId)
                                .index(IndexConstant.communityArticle)));
            });
            elasticsearchClient.bulk(deleteCommunityArticle.build());

            log.info("批量删除全部索引表中的社区文章信息");
            communityArticleIdList.stream().forEach(communityArticleId -> {
                try {
                    elasticsearchClient.deleteByQuery(delete -> delete
                            .index(IndexConstant.all)
                            .query(query -> query
                                    .match(match -> match
                                            .field("type")
                                            .query(SearchTypeEnum.COMMUNITY_ARTICLE.getCode())
                                            .field("associationId")
                                            .query(communityArticleId))
                            ));

                } catch (IOException e) {
                    throw new MonkeyBlogException(R.Error, e.getMessage());
                }
            });
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }
}
