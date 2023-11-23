package com.monkey.monkeysearch.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.alibaba.fastjson.TypeReference;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeysearch.constant.IndexConstant;
import com.monkey.monkeysearch.constant.SearchExceptionEnum;
import com.monkey.monkeysearch.constant.SearchTypeEnum;
import com.monkey.monkeysearch.feign.SearchToArticleFeign;
import com.monkey.monkeysearch.pojo.ESAllIndex;
import com.monkey.monkeysearch.pojo.ESArticleIndex;
import com.monkey.monkeysearch.service.ESArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author: wusihao
 * @date: 2023/11/7 16:29
 * @version: 1.0
 * @description:
 */
@Slf4j
@Service
public class ESArticleServiceImpl implements ESArticleService {
    @Resource
    private SearchToArticleFeign searchToArticleFeign;
    @Resource
    private ElasticsearchClient elasticsearchClient;

    /**
     * 将文章数据库中所有数据存入elasticsearch文章文档中
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/7 17:00
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R insertArticleDocument() {
        try {
            // 查询所有文章
            R result = searchToArticleFeign.queryAllArticle();

            List<ESArticleIndex> esArticleIndexList = (List<ESArticleIndex>) result.getData(new TypeReference<List<ESArticleIndex>>(){});

             // 将文章批量插入elasticsearch
            BulkRequest.Builder br = new BulkRequest.Builder();
            esArticleIndexList.stream().forEach(article -> {
                br.operations(op -> op
                        .index(idx -> idx
                                .index(IndexConstant.article)
                                .id(article.getId())
                                .document(article)));
            });

            BulkResponse response = elasticsearchClient.bulk(br.build());
            if (response.errors()) {
                log.error("Bulk had errors");
                for (BulkResponseItem item: response.items()) {
                    if (item.error() != null) {
                        log.error(item.error().reason());
                    }
                }
                throw new MonkeyBlogException(SearchExceptionEnum.BULK_INSERT_ARTICLE.getCode(), SearchExceptionEnum.BULK_INSERT_ARTICLE.getMsg());
            }


            // 批量插入全部搜索索引
            bulkInsertAllIndex(esArticleIndexList);

            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 批量插入全部搜索索引
     *
     * @param esArticleIndexList 文章实体类
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/15 17:39
     */
    private void bulkInsertAllIndex(List<ESArticleIndex> esArticleIndexList) {
        try {
            log.info("批量插入全部索引文章模块");
            BulkRequest.Builder br = new BulkRequest.Builder();
            esArticleIndexList.stream().forEach(article -> {
                ESAllIndex esAllIndex = new ESAllIndex();
                BeanUtils.copyProperties(article, esAllIndex);
                esAllIndex.setType(SearchTypeEnum.ARTICLE.getCode());
                esAllIndex.setAssociationId(article.getId());
                br.operations(op -> op
                        .index(idx -> idx
                                .index(IndexConstant.all)
                                .document(esAllIndex)));
            });

            BulkResponse bulk = elasticsearchClient.bulk(br.build());
            if (bulk.errors()) {
                throw new MonkeyBlogException(SearchExceptionEnum.BULK_ARTICLE_ALL.getCode(), SearchExceptionEnum.BULK_ARTICLE_ALL.getMsg());
            }
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询综合文章列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 9:57
     */
    @Override
    public R queryComprehensiveArticle(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("title",new HighlightField.Builder().build());
            highlightFieldMap.put("profile",new HighlightField.Builder().build());
            highlightFieldMap.put("labelName", new HighlightField.Builder().build());
            SearchResponse<ESArticleIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.article)
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
                                    .field("commentCount")
                                    .order(SortOrder.Desc)
                                    .field("viewCount")
                                    .order(SortOrder.Desc)
                                    .field("createTime")
                                    .order(SortOrder.Desc))), ESArticleIndex.class);

            // 设置搜索结果高亮
            List<ESArticleIndex> esArticleIndexList = setHighlight(response);

            return R.ok(esArticleIndexList);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询回复最多文章列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:45
     */
    @Override
    public R queryReplyArticle(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("title",new HighlightField.Builder().build());
            highlightFieldMap.put("profile",new HighlightField.Builder().build());
            highlightFieldMap.put("labelName", new HighlightField.Builder().build());
            SearchResponse<ESArticleIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.article)
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
                                    .order(SortOrder.Desc))), ESArticleIndex.class);

            // 设置搜索结果高亮
            List<ESArticleIndex> esArticleIndexList = setHighlight(response);

            return R.ok(esArticleIndexList);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询收藏数最多文章列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:51
     */
    @Override
    public R queryCollectArticle(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("title",new HighlightField.Builder().build());
            highlightFieldMap.put("profile",new HighlightField.Builder().build());
            highlightFieldMap.put("labelName", new HighlightField.Builder().build());
            SearchResponse<ESArticleIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.article)
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
                                    .order(SortOrder.Desc))), ESArticleIndex.class);

            // 设置搜索结果高亮
            List<ESArticleIndex> esArticleIndexList = setHighlight(response);

            return R.ok(esArticleIndexList);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询点赞数最多文章列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:52
     */
    @Override
    public R queryLikeArticle(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("title",new HighlightField.Builder().build());
            highlightFieldMap.put("profile",new HighlightField.Builder().build());
            highlightFieldMap.put("labelName", new HighlightField.Builder().build());
            SearchResponse<ESArticleIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.article)
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
                                    .order(SortOrder.Desc))), ESArticleIndex.class);

            // 设置搜索结果高亮
            List<ESArticleIndex> esArticleIndexList = setHighlight(response);

            return R.ok(esArticleIndexList);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询游览数最多文章列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:52
     */
    @Override
    public R queryViewArticle(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("title",new HighlightField.Builder().build());
            highlightFieldMap.put("profile",new HighlightField.Builder().build());
            highlightFieldMap.put("labelName", new HighlightField.Builder().build());
            SearchResponse<ESArticleIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.article)
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
                                    .order(SortOrder.Desc))), ESArticleIndex.class);

            // 设置搜索结果高亮
            List<ESArticleIndex> esArticleIndexList = setHighlight(response);

            return R.ok(esArticleIndexList);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询最热文章列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:53
     */
    @Override
    public R queryHireArticle(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("title",new HighlightField.Builder().build());
            highlightFieldMap.put("profile",new HighlightField.Builder().build());
            highlightFieldMap.put("labelName", new HighlightField.Builder().build());
            SearchResponse<ESArticleIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.article)
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
                                    .field("commentCount")
                                    .field("collectCount")
                                    .order(SortOrder.Desc)
                                    .field("createTime")
                                    .order(SortOrder.Desc))), ESArticleIndex.class);

            // 设置搜索结果高亮
            List<ESArticleIndex> esArticleIndexList = setHighlight(response);

            return R.ok(esArticleIndexList);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询最新文章列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:54
     */
    @Override
    public R queryLatestArticle(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("title",new HighlightField.Builder().build());
            highlightFieldMap.put("profile",new HighlightField.Builder().build());
            highlightFieldMap.put("labelName", new HighlightField.Builder().build());
            SearchResponse<ESArticleIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.article)
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
                                    .order(SortOrder.Desc))), ESArticleIndex.class);

            // 设置搜索结果高亮
            List<ESArticleIndex> esArticleIndexList = setHighlight(response);

            return R.ok(esArticleIndexList);
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
    private List<ESArticleIndex> setHighlight(SearchResponse<ESArticleIndex> response) {
        List<Hit<ESArticleIndex>> hits = response.hits().hits();
        List<ESArticleIndex> esArticleIndexList = new ArrayList<>();
        for (Hit<ESArticleIndex> hit : hits) {
            Map<String, List<String>> highlight = hit.highlight();
            ESArticleIndex source = hit.source();
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
            esArticleIndexList.add(source);
        }

        return esArticleIndexList;
    }
}
