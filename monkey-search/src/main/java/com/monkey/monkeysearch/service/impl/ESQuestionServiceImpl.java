package com.monkey.monkeysearch.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.Buckets;
import co.elastic.clients.elasticsearch._types.aggregations.LongTermsAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.LongTermsBucket;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeysearch.constant.IndexConstant;
import com.monkey.monkeysearch.constant.SearchExceptionEnum;
import com.monkey.monkeysearch.constant.SearchTypeEnum;
import com.monkey.monkeysearch.feign.SearchToQuestionFeign;
import com.monkey.monkeysearch.pojo.Achievement;
import com.monkey.monkeysearch.pojo.ESAllIndex;
import com.monkey.monkeysearch.pojo.ESCourseIndex;
import com.monkey.monkeysearch.pojo.ESQuestionIndex;
import com.monkey.monkeysearch.service.ESQuestionService;
import com.monkey.monkeysearch.util.ESCommonMethods;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: wusihao
 * @date: 2023/11/9 16:08
 * @version: 1.0
 * @description:
 */
@Service
@Slf4j
public class ESQuestionServiceImpl implements ESQuestionService {
    @Resource
    private SearchToQuestionFeign searchToQuestionFeign;
    @Resource
    private ElasticsearchClient elasticsearchClient;
    /**
     * 将问答数据库中所有数据存入elasticsearch问答文档中
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 16:15
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R insertQuestionDocument() {
        try {
            R r = searchToQuestionFeign.queryAllQuestion();
            List<ESQuestionIndex> questionIndexList = (List<ESQuestionIndex>)r.getData(new TypeReference<List<ESQuestionIndex>>(){});
            BulkRequest.Builder buliBuilder = new BulkRequest.Builder();
            questionIndexList.stream().forEach(question -> {
                buliBuilder.operations(op -> op
                                .index(idx -> idx
                                        .index(IndexConstant.question)
                                        .id(String.valueOf(question.getId()))
                                        .document(question)));
            });

            BulkResponse response = elasticsearchClient.bulk(buliBuilder.build());
            if (response.errors()) {
                log.error("Bulk had errors");
                for (BulkResponseItem item: response.items()) {
                    if (item.error() != null) {
                        log.error(item.error().reason());
                    }
                }
                throw new MonkeyBlogException(SearchExceptionEnum.BULK_INSERT_QUESTION.getCode(), SearchExceptionEnum.BULK_INSERT_QUESTION.getMsg());
            }

            // 批量插入所有索引中
            bulkAllIndex(questionIndexList);
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 批量插入所有索引中
     *
     * @param questionIndexList 问答索引集合
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/15 17:53
     */
    private void bulkAllIndex(List<ESQuestionIndex> questionIndexList) {
        try {
            log.info("批量插入全部索引问答模块");
            BulkRequest.Builder br = new BulkRequest.Builder();
            questionIndexList.stream().forEach(question -> {
                ESAllIndex esAllIndex = new ESAllIndex();
                BeanUtils.copyProperties(question, esAllIndex);
                esAllIndex.setCommentCount(question.getReplyCount());
                esAllIndex.setType(SearchTypeEnum.QUESTION.getCode());
                esAllIndex.setAssociationId(String.valueOf(question.getId()));
                br.operations(op -> op
                        .index(idx -> idx
                                .index(IndexConstant.all)
                                .document(esAllIndex)));
            });

            BulkResponse bulk = elasticsearchClient.bulk(br.build());
            if (bulk.errors()) {
                throw new MonkeyBlogException(SearchExceptionEnum.BULK_QUESTION_ALL.getCode(), SearchExceptionEnum.BULK_QUESTION_ALL.getMsg());
            }
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }

    }


    /**
     * 查询综合问答列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 9:57
     */
    @Override
    public R queryComprehensiveQuestion(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("title",new HighlightField.Builder().build());
            highlightFieldMap.put("profile",new HighlightField.Builder().build());
            highlightFieldMap.put("labelName", new HighlightField.Builder().build());
            SearchResponse<ESQuestionIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.question)
                    .query(q -> q
                            .match(m -> m
                                    .field("togetherSearch")
                                    .query(keyword)))
                    .highlight(h -> h
                            .fields(highlightFieldMap)
                            .preTags("<em style='color: red'>")
                            .postTags("</em>")
                            .requireFieldMatch(false))
                    .from(location)
                    .size(pageSize)
                    .sort(sort -> sort
                            .field(f -> f
                                    .field("likeCount")
                                    .order(SortOrder.Desc)
                                    .field("collectCount")
                                    .order(SortOrder.Desc)
                                    .field("replyCount")
                                    .order(SortOrder.Desc)
                                    .field("viewCount")
                                    .order(SortOrder.Desc)
                                    .field("createTime")
                                    .order(SortOrder.Desc))), ESQuestionIndex.class);

            // 设置搜索结果高亮
            List<ESQuestionIndex> esQuestionIndexList = setHighlight(response);
            long totals = response.hits().total().value();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("esQuestionIndexList", esQuestionIndexList);
            jsonObject.put("totals", totals);
            return R.ok(jsonObject);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询回复最多问答列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:45
     */
    @Override
    public R queryReplyQuestion(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("title",new HighlightField.Builder().build());
            highlightFieldMap.put("profile",new HighlightField.Builder().build());
            highlightFieldMap.put("labelName", new HighlightField.Builder().build());
            SearchResponse<ESQuestionIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.question)
                    .query(q -> q
                            .match(m -> m
                                    .field("togetherSearch")
                                    .query(keyword)))
                    .highlight(h -> h
                            .fields(highlightFieldMap)
                            .preTags("<em style='color: red'>")
                            .postTags("</em>")
                            .requireFieldMatch(false))
                    .from(location)
                    .size(pageSize)
                    .sort(sort -> sort
                            .field(f -> f
                                    .field("replyCount")
                                    .order(SortOrder.Desc))), ESQuestionIndex.class);

            // 设置搜索结果高亮
            List<ESQuestionIndex> esQuestionIndexList = setHighlight(response);
            long totals = response.hits().total().value();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("esQuestionIndexList", esQuestionIndexList);
            jsonObject.put("totals", totals);
            return R.ok(jsonObject);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询收藏数最多问答列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:51
     */
    @Override
    public R queryCollectQuestion(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("title",new HighlightField.Builder().build());
            highlightFieldMap.put("profile",new HighlightField.Builder().build());
            highlightFieldMap.put("labelName", new HighlightField.Builder().build());
            SearchResponse<ESQuestionIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.question)
                    .query(q -> q
                            .match(m -> m
                                    .field("togetherSearch")
                                    .query(keyword)))
                    .highlight(h -> h
                            .fields(highlightFieldMap)
                            .preTags("<em style='color: red'>")
                            .postTags("</em>")
                            .requireFieldMatch(false))
                    .from(location)
                    .size(pageSize)
                    .sort(sort -> sort
                            .field(f -> f
                                    .field("collectCount")
                                    .order(SortOrder.Desc))), ESQuestionIndex.class);

            // 设置搜索结果高亮
            List<ESQuestionIndex> esQuestionIndexList = setHighlight(response);
            long totals = response.hits().total().value();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("esQuestionIndexList", esQuestionIndexList);
            jsonObject.put("totals", totals);
            return R.ok(jsonObject);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询点赞数最多问答列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:52
     */
    @Override
    public R queryLikeQuestion(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("title",new HighlightField.Builder().build());
            highlightFieldMap.put("profile",new HighlightField.Builder().build());
            highlightFieldMap.put("labelName", new HighlightField.Builder().build());
            SearchResponse<ESQuestionIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.question)
                    .query(q -> q
                            .match(m -> m
                                    .field("togetherSearch")
                                    .query(keyword)))
                    .highlight(h -> h
                            .fields(highlightFieldMap)
                            .preTags("<em style='color: red'>")
                            .postTags("</em>")
                            .requireFieldMatch(false))
                    .from(location)
                    .size(pageSize)
                    .sort(sort -> sort
                            .field(f -> f
                                    .field("likeCount")
                                    .order(SortOrder.Desc))), ESQuestionIndex.class);

            // 设置搜索结果高亮
            List<ESQuestionIndex> esQuestionIndexList = setHighlight(response);
            long totals = response.hits().total().value();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("esQuestionIndexList", esQuestionIndexList);
            jsonObject.put("totals", totals);
            return R.ok(jsonObject);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询游览数最多问答列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:52
     */
    @Override
    public R queryViewQuestion(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("title",new HighlightField.Builder().build());
            highlightFieldMap.put("profile",new HighlightField.Builder().build());
            highlightFieldMap.put("labelName", new HighlightField.Builder().build());
            SearchResponse<ESQuestionIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.question)
                    .query(q -> q
                            .match(m -> m
                                    .field("togetherSearch")
                                    .query(keyword)))
                    .highlight(h -> h
                            .fields(highlightFieldMap)
                            .preTags("<em style='color: red'>")
                            .postTags("</em>")
                            .requireFieldMatch(false))
                    .from(location)
                    .size(pageSize)
                    .sort(sort -> sort
                            .field(f -> f
                                    .field("viewCount")
                                    .order(SortOrder.Desc))), ESQuestionIndex.class);

            // 设置搜索结果高亮
            List<ESQuestionIndex> esQuestionIndexList = setHighlight(response);
            long totals = response.hits().total().value();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("esQuestionIndexList", esQuestionIndexList);
            jsonObject.put("totals", totals);
            return R.ok(jsonObject);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询最热问答列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:53
     */
    @Override
    public R queryHireQuestion(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("title",new HighlightField.Builder().build());
            highlightFieldMap.put("profile",new HighlightField.Builder().build());
            highlightFieldMap.put("labelName", new HighlightField.Builder().build());
            SearchResponse<ESQuestionIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.question)
                    .query(q -> q
                            .match(m -> m
                                    .field("togetherSearch")
                                    .query(keyword)))
                    .highlight(h -> h
                            .fields(highlightFieldMap)
                            .preTags("<em style='color: red'>")
                            .postTags("</em>")
                            .requireFieldMatch(false))
                    .from(location)
                    .size(pageSize)
                    .sort(sort -> sort
                            .field(f -> f
                                    .field("viewCount")
                                    .order(SortOrder.Desc)
                                    .field("likeCount")
                                    .order(SortOrder.Desc)
                                    .order(SortOrder.Desc)
                                    .field("replyCount")
                                    .field("collectCount")
                                    .order(SortOrder.Desc)
                                    .field("createTime")
                                    .order(SortOrder.Desc))), ESQuestionIndex.class);

            // 设置搜索结果高亮
            List<ESQuestionIndex> esQuestionIndexList = setHighlight(response);
            long totals = response.hits().total().value();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("esQuestionIndexList", esQuestionIndexList);
            jsonObject.put("totals", totals);
            return R.ok(jsonObject);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询最新问答列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:54
     */
    @Override
    public R queryLatestQuestion(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("title",new HighlightField.Builder().build());
            highlightFieldMap.put("profile",new HighlightField.Builder().build());
            highlightFieldMap.put("labelName", new HighlightField.Builder().build());
            SearchResponse<ESQuestionIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.question)
                    .query(q -> q
                            .match(m -> m
                                    .field("togetherSearch")
                                    .query(keyword)))
                    .highlight(h -> h
                            .fields(highlightFieldMap)
                            .preTags("<em style='color: red'>")
                            .postTags("</em>")
                            .requireFieldMatch(false))
                    .from(location)
                    .size(pageSize)
                    .sort(sort -> sort
                            .field(f -> f
                                    .field("createTime")
                                    .order(SortOrder.Desc))), ESQuestionIndex.class);

            // 设置搜索结果高亮
            List<ESQuestionIndex> esQuestionIndexList = setHighlight(response);
            long totals = response.hits().total().value();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("esQuestionIndexList", esQuestionIndexList);
            jsonObject.put("totals", totals);
            return R.ok(jsonObject);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询所有问答文档
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/26 21:01
     */
    @Override
    public R queryAllQuestionDocument() {
        try {
            log.info("查询所有问答文档");
            SearchResponse<ESQuestionIndex> response = elasticsearchClient.search(search -> search
                    .index(IndexConstant.question)
                    .query(query -> query
                            .matchAll(all -> all)), ESQuestionIndex.class);
            List<ESQuestionIndex> esQuestionIndexList = new ArrayList<>();
            List<Hit<ESQuestionIndex>> hits = response.hits().hits();
            for (Hit<ESQuestionIndex> hit : hits) {
                ESQuestionIndex source = hit.source();
                esQuestionIndexList.add(source);
            }

            return R.ok(esQuestionIndexList);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 删除所有问答文档
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/26 21:01
     */
    @Override
    public R deleteAllQuestionDocument() {
        try {
            // 得到该问答每个用户对应的的点赞数，游览数，收藏数总和
            SearchResponse<ESQuestionIndex> esQuestionIndexSearchResponse = ESCommonMethods.queryAllUserAchievement(IndexConstant.question
            , elasticsearchClient, ESQuestionIndex.class);
            Map<Achievement, Long> question = ESCommonMethods.getAchievement(esQuestionIndexSearchResponse);

            // 批量减去用户对应的游览数 点赞数，收藏数
            ESCommonMethods.bulkSubUserAchievement(question, elasticsearchClient);

            log.info("删除所有问答文档");
            elasticsearchClient.deleteByQuery(delete -> delete
                    .index(IndexConstant.question)
                    .query(query -> query
                            .matchAll(matchAll -> matchAll)));

            log.info("删除全部索引中的问答文档");
            elasticsearchClient.deleteByQuery(delete -> delete
                    .index(IndexConstant.all)
                    .query(query -> query
                            .match(match -> match
                                    .field("type")
                                    .query(SearchTypeEnum.QUESTION.getCode()))));

            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 设置搜索结果高亮
     *
     * @param response 搜索返回字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:49
     */
    private List<ESQuestionIndex> setHighlight(SearchResponse<ESQuestionIndex> response) {
        List<Hit<ESQuestionIndex>> hits = response.hits().hits();
        List<ESQuestionIndex> esQuestionIndexList = new ArrayList<>();
        for (Hit<ESQuestionIndex> hit : hits) {
            Map<String, List<String>> highlight = hit.highlight();
            ESQuestionIndex source = hit.source();
            // 高亮赋值
            if (source != null) {
                if (highlight.get("title") != null) {
                    source.setTitle(highlight.get("title").get(0));
                }

                if (highlight.get("profile") != null) {
                    source.setProfile(highlight.get("profile").get(0));
                }

                if (highlight.get("labelName") != null) {
                    List<String> list = highlight.get("labelName");
                    source.setLabelName(list);
                }
            }
            esQuestionIndexList.add(source);
        }

        return esQuestionIndexList;
    }
}
