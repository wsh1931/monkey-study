package com.monkey.monkeysearch.util;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.Buckets;
import co.elastic.clients.elasticsearch._types.aggregations.LongTermsAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.LongTermsBucket;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeysearch.constant.IndexConstant;
import com.monkey.monkeysearch.constant.SearchTypeEnum;
import com.monkey.monkeysearch.pojo.Achievement;
import com.monkey.monkeysearch.pojo.ESCommunityArticleIndex;
import com.monkey.monkeysearch.pojo.ESQuestionIndex;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: wusihao
 * @date: 2023/11/29 11:18
 * @version: 1.0
 * @description: elasticsearch通过方法
 */
@Slf4j
public class ESCommonMethods {
    /**
     * 得到该每个用户对应的(成就)点赞数，游览数，收藏数总和
     *
     * @param esQuestionIndexSearchResponse elasticsearch中查询到的数据
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/29 11:15
     */
    public static <T> Map<Achievement, Long> getAchievement(SearchResponse<T> esQuestionIndexSearchResponse) {
        try {
            log.info("得到该每个用户对应的(成就)点赞数，游览数，收藏数总和");
            Map<String, Aggregate> aggregations = esQuestionIndexSearchResponse.aggregations();
            Map<Achievement, Long> question = new HashMap<>(aggregations.size());
            aggregations.entrySet().stream().forEach(aggregation -> {
                Aggregate aggregate = aggregation.getValue();
                LongTermsAggregate lterms = aggregate.lterms();
                Buckets<LongTermsBucket> buckets = lterms.buckets();
                List<LongTermsBucket> array = buckets.array();

                array.stream().forEach(arr -> {
                    long userId = arr.key();
                    Map<String, Aggregate> aggregateMap = arr.aggregations();

                    aggregateMap.entrySet().stream().forEach(arrMap -> {
                        String key = arrMap.getKey();
                        long value = BigDecimal.valueOf(arrMap.getValue().sum().value()).longValue();
                        question.put(new Achievement(userId, key), value);
                    });
                });
            });

            return question;
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 批量减去用户对应的成就（游览数 点赞数，收藏数等）
     *
     * @param achievement 每个用户对应的成就集合
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/26 20:35
     */
    public static void bulkSubUserAchievement(Map<Achievement, Long> achievement, ElasticsearchClient elasticsearchClient) {
        try {
            log.info("减去用户对应的游览数 点赞数，收藏数");
            BulkRequest.Builder userBuilder = new BulkRequest.Builder();
            achievement.entrySet().stream().forEach(article -> {
                Achievement key = article.getKey();
                Long userId = key.getUserId();
                String keyword = key.getKeyword();
                Long count = article.getValue();
                userBuilder.operations(op -> op
                        .update(update -> update
                                .index(IndexConstant.user)
                                .id(String.valueOf(userId))
                                .action(action -> action
                                        .script(script -> script
                                                .inline(inline -> inline
                                                        .source("ctx._source." + keyword + "-=" + count))))));
            });

            elasticsearchClient.bulk(userBuilder.build());
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 得到所有用户的点赞收藏游览数
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/29 11:13
     */
    public static <T> SearchResponse<T> queryAllUserAchievement(String index,
                                                                    ElasticsearchClient elasticsearchClient,
                                                                    Class<T> type) {
        try {
            log.info("得到问答所有的点赞收藏游览数 type ==> {}", index);
            SearchResponse<T> esQuestionIndexSearchResponse = elasticsearchClient.search(search -> search
                    .index(index)
                    .query(query -> query
                            .matchAll(all -> all))
                    .aggregations("userId", aggregations -> aggregations
                            .terms(terms -> terms
                                    .field("userId"))
                            .aggregations("viewCount", aggregation -> aggregation
                                    .sum(sum -> sum
                                            .field("viewCount")))
                            .aggregations("likeCount", aggregation -> aggregation
                                    .sum(sum -> sum
                                            .field("likeCount")))
                            .aggregations("collectCount", aggregation -> aggregation
                                    .sum(sum -> sum
                                            .field("collectCount")))
                    ), type);

            return esQuestionIndexSearchResponse;
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
    public static List<String> getCommunityArticleIdList(SearchResponse<ESCommunityArticleIndex> response) {
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
     * 批量删除社区文章
     *
     * @param communityArticleIdList 社区文章id集合
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/26 20:32
     */
    public static void bulkDeleteCommunityArticle(List<String> communityArticleIdList, ElasticsearchClient elasticsearchClient) {
        try {
            log.info("通过社区文章id批量删除社区文章 ==> {}", communityArticleIdList);
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
