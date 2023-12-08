package com.monkey.monkeysearch.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
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
import com.monkey.monkeysearch.feign.SearchToResourceFeign;
import com.monkey.monkeysearch.pojo.Achievement;
import com.monkey.monkeysearch.pojo.ESAllIndex;
import com.monkey.monkeysearch.pojo.ESQuestionIndex;
import com.monkey.monkeysearch.pojo.ESResourceIndex;
import com.monkey.monkeysearch.service.ESResourceService;
import com.monkey.monkeysearch.util.ESCommonMethods;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: wusihao
 * @date: 2023/11/12 16:53
 * @version: 1.0
 * @description:
 */
@Service
@Slf4j
public class ESResourceServiceImpl implements ESResourceService {
    @Resource
    private SearchToResourceFeign searchToResourceFeign;
    @Resource
    private ElasticsearchClient elasticsearchClient;

    /**
     * 将资源数据库中所有数据存入elasticsearch资源文档中
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/11 11:18
     */
    @Override
    public R insertResourceDocument() {
        try {
            // 查询所有资源
            R result = searchToResourceFeign.queryAllResource();

            List<ESResourceIndex> esResourceIndexList = (List<ESResourceIndex>) result.getData(new TypeReference<List<ESResourceIndex>>(){});

            // 将资源批量插入elasticsearch
            BulkRequest.Builder br = new BulkRequest.Builder();
            esResourceIndexList.stream().forEach(resource -> {
                br.operations(op -> op
                        .index(idx -> idx
                                .index(IndexConstant.resource)
                                .id(resource.getId())
                                .document(resource)));
            });

            BulkResponse response = elasticsearchClient.bulk(br.build());
            if (response.errors()) {
                log.error("Bulk had errors");
                for (BulkResponseItem item: response.items()) {
                    if (item.error() != null) {
                        log.error(item.error().reason());
                    }
                }
                throw new MonkeyBlogException(SearchExceptionEnum.BULK_INSERT_COURSE.getCode(), SearchExceptionEnum.BULK_INSERT_COURSE.getMsg());
            }

            this.insertResourceAll(esResourceIndexList);
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 批量插入全部索引资源模块
     *
     * @param esResourceIndexList 资源索引集合
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/16 9:33
     */
    private void insertResourceAll(List<ESResourceIndex> esResourceIndexList) {
        try {
            log.info("批量插入全部索引资源模块");
            BulkRequest.Builder br = new BulkRequest.Builder();
            esResourceIndexList.stream().forEach(resource -> {
                ESAllIndex esAllIndex = new ESAllIndex();
                BeanUtils.copyProperties(resource, esAllIndex);
                esAllIndex.setTitle(resource.getName());
                esAllIndex.setProfile(resource.getDescription());
                esAllIndex.setPhoto(resource.getTypeUrl());
                esAllIndex.setLabelName(resource.getResourceLabelName());
                esAllIndex.setType(SearchTypeEnum.RESOURCE.getCode());
                esAllIndex.setAssociationId(resource.getId());

                br.operations(op -> op
                        .index(idx -> idx
                                .index(IndexConstant.all)
                                .document(esAllIndex)));
            });

            BulkResponse bulk = elasticsearchClient.bulk(br.build());
            if (bulk.errors()) {
                throw new MonkeyBlogException(SearchExceptionEnum.BULK_COURSE_ALL.getCode(), SearchExceptionEnum.BULK_COURSE_ALL.getMsg());
            }
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询综合资源列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 9:57
     */
    @Override
    public R queryComprehensiveResource(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("name",new HighlightField.Builder().build());
            highlightFieldMap.put("description",new HighlightField.Builder().build());
            highlightFieldMap.put("resourceLabelName", new HighlightField.Builder().build());
            highlightFieldMap.put("resourceClassificationName", new HighlightField.Builder().build());
            SearchResponse<ESResourceIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.resource)
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
                                    .field("downCount")
                                    .order(SortOrder.Desc)
                                    .field("buyCount")
                                    .order(SortOrder.Desc)
                                    .field("likeCount")
                                    .order(SortOrder.Desc)
                                    .field("collectCount")
                                    .order(SortOrder.Desc)
                                    .field("commentCount")
                                    .order(SortOrder.Desc)
                                    .field("viewCount")
                                    .order(SortOrder.Desc)
                                    .field("createTime")
                                    .order(SortOrder.Desc))), ESResourceIndex.class);

            // 设置搜索结果高亮
            List<ESResourceIndex> esResourceIndexList = setHighlight(response);
            long totals = response.hits().total().value();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("esResourceIndexList", esResourceIndexList);
            jsonObject.put("totals", totals);
            return R.ok(jsonObject);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询评论最多资源列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:45
     */
    @Override
    public R queryCommentResource(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("name",new HighlightField.Builder().build());
            highlightFieldMap.put("description",new HighlightField.Builder().build());
            highlightFieldMap.put("resourceLabelName", new HighlightField.Builder().build());
            highlightFieldMap.put("resourceClassificationName", new HighlightField.Builder().build());
            SearchResponse<ESResourceIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.resource)
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
                                    .field("commentCount")
                                    .order(SortOrder.Desc))), ESResourceIndex.class);

            // 设置搜索结果高亮
            List<ESResourceIndex> esResourceIndexList = setHighlight(response);
            long totals = response.hits().total().value();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("esResourceIndexList", esResourceIndexList);
            jsonObject.put("totals", totals);
            return R.ok(jsonObject);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询收藏数最多资源列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:51
     */
    @Override
    public R queryCollectResource(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("name",new HighlightField.Builder().build());
            highlightFieldMap.put("description",new HighlightField.Builder().build());
            highlightFieldMap.put("resourceLabelName", new HighlightField.Builder().build());
            highlightFieldMap.put("resourceClassificationName", new HighlightField.Builder().build());
            SearchResponse<ESResourceIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.resource)
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
                                    .order(SortOrder.Desc))), ESResourceIndex.class);

            // 设置搜索结果高亮
            List<ESResourceIndex> esResourceIndexList = setHighlight(response);
            long totals = response.hits().total().value();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("esResourceIndexList", esResourceIndexList);
            jsonObject.put("totals", totals);
            return R.ok(jsonObject);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }


    /**
     * 查询游览数最多资源列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:52
     */
    @Override
    public R queryViewResource(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("name",new HighlightField.Builder().build());
            highlightFieldMap.put("description",new HighlightField.Builder().build());
            highlightFieldMap.put("resourceLabelName", new HighlightField.Builder().build());
            highlightFieldMap.put("resourceClassificationName", new HighlightField.Builder().build());
            SearchResponse<ESResourceIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.resource)
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
                                    .order(SortOrder.Desc))), ESResourceIndex.class);

            // 设置搜索结果高亮
            List<ESResourceIndex> esResourceIndexList = setHighlight(response);
            long totals = response.hits().total().value();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("esResourceIndexList", esResourceIndexList);
            jsonObject.put("totals", totals);
            return R.ok(jsonObject);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询最热资源列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:53
     */
    @Override
    public R queryHireResource(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("name",new HighlightField.Builder().build());
            highlightFieldMap.put("description",new HighlightField.Builder().build());
            highlightFieldMap.put("resourceLabelName", new HighlightField.Builder().build());
            highlightFieldMap.put("resourceClassificationName", new HighlightField.Builder().build());
            SearchResponse<ESResourceIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.resource)
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
                                    .field("downCount")
                                    .order(SortOrder.Desc)
                                    .field("buyCount")
                                    .order(SortOrder.Desc)
                                    .field("viewCount")
                                    .order(SortOrder.Desc)
                                    .order(SortOrder.Desc)
                                    .field("commentCount")
                                    .field("collectCount")
                                    .order(SortOrder.Desc)
                                    .field("createTime")
                                    .order(SortOrder.Desc))), ESResourceIndex.class);

            // 设置搜索结果高亮
            List<ESResourceIndex> esResourceIndexList = setHighlight(response);
            long totals = response.hits().total().value();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("esResourceIndexList", esResourceIndexList);
            jsonObject.put("totals", totals);
            return R.ok(jsonObject);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询最新资源列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:54
     */
    @Override
    public R queryLatestResource(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("name",new HighlightField.Builder().build());
            highlightFieldMap.put("description",new HighlightField.Builder().build());
            highlightFieldMap.put("resourceLabelName", new HighlightField.Builder().build());
            highlightFieldMap.put("resourceClassificationName", new HighlightField.Builder().build());
            SearchResponse<ESResourceIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.resource)
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
                                    .order(SortOrder.Desc))), ESResourceIndex.class);

            // 设置搜索结果高亮
            List<ESResourceIndex> esResourceIndexList = setHighlight(response);
            long totals = response.hits().total().value();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("esResourceIndexList", esResourceIndexList);
            jsonObject.put("totals", totals);
            return R.ok(jsonObject);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询下载数最多资源列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:52
     */
    @Override
    public R queryDownResource(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("name",new HighlightField.Builder().build());
            highlightFieldMap.put("description",new HighlightField.Builder().build());
            highlightFieldMap.put("resourceLabelName", new HighlightField.Builder().build());
            highlightFieldMap.put("resourceClassificationName", new HighlightField.Builder().build());
            SearchResponse<ESResourceIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.resource)
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
                                    .field("downCount")
                                    .order(SortOrder.Desc))), ESResourceIndex.class);

            // 设置搜索结果高亮
            List<ESResourceIndex> esResourceIndexList = setHighlight(response);
            long totals = response.hits().total().value();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("esResourceIndexList", esResourceIndexList);
            jsonObject.put("totals", totals);
            return R.ok(jsonObject);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询购买数最多资源列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:52
     */
    @Override
    public R queryBuyResource(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("name",new HighlightField.Builder().build());
            highlightFieldMap.put("description",new HighlightField.Builder().build());
            highlightFieldMap.put("resourceLabelName", new HighlightField.Builder().build());
            highlightFieldMap.put("resourceClassificationName", new HighlightField.Builder().build());
            SearchResponse<ESResourceIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.resource)
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
                                    .field("buyCount")
                                    .order(SortOrder.Desc))), ESResourceIndex.class);

            // 设置搜索结果高亮
            List<ESResourceIndex> esResourceIndexList = setHighlight(response);
            long totals = response.hits().total().value();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("esResourceIndexList", esResourceIndexList);
            jsonObject.put("totals", totals);
            return R.ok(jsonObject);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询资源评分最多的资源列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 搜素关键字
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/13 10:29
     */
    @Override
    public R queryScoreResource(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("name",new HighlightField.Builder().build());
            highlightFieldMap.put("description",new HighlightField.Builder().build());
            highlightFieldMap.put("resourceLabelName", new HighlightField.Builder().build());
            highlightFieldMap.put("resourceClassificationName", new HighlightField.Builder().build());
            SearchResponse<ESResourceIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.resource)
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
                                    .field("score")
                                    .order(SortOrder.Desc))), ESResourceIndex.class);

            // 设置搜索结果高亮
            List<ESResourceIndex> esResourceIndexList = setHighlight(response);
            long totals = response.hits().total().value();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("esResourceIndexList", esResourceIndexList);
            jsonObject.put("totals", totals);
            return R.ok(jsonObject);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询所有资源文档
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/26 21:02
     */
    @Override
    public R queryAllResourceDocument() {
        try {
            log.info("查询所有资源文档");
            SearchResponse<ESResourceIndex> response = elasticsearchClient.search(search -> search
                    .index(IndexConstant.resource)
                    .query(query -> query
                            .matchAll(all -> all)), ESResourceIndex.class);
            List<ESResourceIndex> esResourceIndexList = new ArrayList<>();
            List<Hit<ESResourceIndex>> hits = response.hits().hits();
            for (Hit<ESResourceIndex> hit : hits) {
                ESResourceIndex source = hit.source();
                esResourceIndexList.add(source);
            }

            return R.ok(esResourceIndexList);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 删除所有资源文档
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/26 21:03
     */
    @Override
    public R deleteAllResourceDocument() {
        try {
            // 得到该资源每个用户对应的的点赞数，游览数，收藏数总和
            SearchResponse<ESResourceIndex> esResourceIndexSearchResponse = ESCommonMethods.queryAllUserAchievement(
                    IndexConstant.resource, elasticsearchClient, ESResourceIndex.class);
            Map<Achievement, Long> resource = ESCommonMethods.getAchievement(esResourceIndexSearchResponse);

            // 批量减去用户对应的游览数 点赞数，收藏数
            ESCommonMethods.bulkSubUserAchievement(resource, elasticsearchClient);
            log.info("删除所有资源文档");
            elasticsearchClient.deleteByQuery(delete -> delete
                    .index(IndexConstant.resource)
                    .query(query -> query
                            .matchAll(matchAll -> matchAll)));

            log.info("删除全部索引中的资源文档");
            elasticsearchClient.deleteByQuery(delete -> delete
                    .index(IndexConstant.all)
                    .query(query -> query
                            .match(match -> match
                                    .field("type")
                                    .query(SearchTypeEnum.RESOURCE.getCode()))));
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询点赞数最多资源列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:52
     */
    @Override
    public R queryLikeResource(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("name",new HighlightField.Builder().build());
            highlightFieldMap.put("introduce",new HighlightField.Builder().build());
            highlightFieldMap.put("resourceLabelName", new HighlightField.Builder().build());
            highlightFieldMap.put("resourceClassificationName", new HighlightField.Builder().build());
            SearchResponse<ESResourceIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.resource)
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
                                    .order(SortOrder.Desc))), ESResourceIndex.class);

            // 设置搜索结果高亮
            List<ESResourceIndex> esResourceIndexList = setHighlight(response);
            long totals = response.hits().total().value();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("esResourceIndexList", esResourceIndexList);
            jsonObject.put("totals", totals);
            return R.ok(jsonObject);
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
    private List<ESResourceIndex> setHighlight(SearchResponse<ESResourceIndex> response) {
        List<Hit<ESResourceIndex>> hits = response.hits().hits();
        List<ESResourceIndex> esResourceIndexList = new ArrayList<>();
        for (Hit<ESResourceIndex> hit : hits) {
            Map<String, List<String>> highlight = hit.highlight();
            ESResourceIndex source = hit.source();
            // 高亮赋值
            if (source != null) {
                if (highlight.get("name") != null) {
                    source.setName(highlight.get("name").get(0));
                }

                if (highlight.get("description") != null) {
                    source.setDescription(highlight.get("description").get(0));
                }

                if (highlight.get("resourceClassificationName") != null) {
                    List<String> list = highlight.get("resourceClassificationName");
                    source.setResourceClassificationName(list);
                }

                if (highlight.get("resourceLabelName") != null) {
                    List<String> list = highlight.get("resourceLabelName");
                    source.setResourceLabelName(list);
                }
            }
            esResourceIndexList.add(source);
        }

        return esResourceIndexList;
    }
}
