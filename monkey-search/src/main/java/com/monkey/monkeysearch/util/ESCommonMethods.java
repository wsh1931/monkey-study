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
import com.monkey.monkeysearch.pojo.Achievement;
import com.monkey.monkeysearch.pojo.ESQuestionIndex;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
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
}
