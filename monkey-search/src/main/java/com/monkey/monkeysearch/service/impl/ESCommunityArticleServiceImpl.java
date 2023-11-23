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
import com.monkey.monkeysearch.feign.SearchToCommunityFeign;
import com.monkey.monkeysearch.pojo.ESAllIndex;
import com.monkey.monkeysearch.pojo.ESCommunityArticleIndex;
import com.monkey.monkeysearch.service.ESCommunityArticleService;
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
 * @date: 2023/11/11 16:30
 * @version: 1.0
 * @description:
 */
@Service
@Slf4j
public class ESCommunityArticleServiceImpl implements ESCommunityArticleService {
    @Resource
    private SearchToCommunityFeign searchToCommunityFeign;
    @Resource
    private ElasticsearchClient elasticsearchClient;

    /**
     * 将社区文章数据库中所有数据存入elasticsearch社区文章文档中
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/11 11:18
     */
    @Override
    public R insertCommunityArticleDocument() {
        try {
            // 查询所有社区文章
            R result = searchToCommunityFeign.queryAllCommunityArticle();

            List<ESCommunityArticleIndex> esCommunityArticleIndexList = (List<ESCommunityArticleIndex>) result.getData(new TypeReference<List<ESCommunityArticleIndex>>(){});

            // 将社区文章批量插入elasticsearch
            BulkRequest.Builder br = new BulkRequest.Builder();
            esCommunityArticleIndexList.stream().forEach(communityArticle -> {
                br.operations(op -> op
                        .index(idx -> idx
                                .index(IndexConstant.communityArticle)
                                .id(communityArticle.getId())
                                .document(communityArticle)));
            });

            BulkResponse response = elasticsearchClient.bulk(br.build());
            if (response.errors()) {
                log.error("Bulk had errors");
                for (BulkResponseItem item: response.items()) {
                    if (item.error() != null) {
                        log.error(item.error().reason());
                    }
                }
                throw new MonkeyBlogException(SearchExceptionEnum.BULK_INSERT_COMMUNITY_ARTICLE.getCode(), SearchExceptionEnum.BULK_INSERT_COMMUNITY_ARTICLE.getMsg());
            }

            this.insertAllCommunityArticleIndex(esCommunityArticleIndexList);
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 批量插入全部索引社区文章模块
     *
     * @param esCommunityArticleIndexList 社区文章索引集合
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/16 9:30
     */
    private void insertAllCommunityArticleIndex(List<ESCommunityArticleIndex> esCommunityArticleIndexList) {
        try {
            log.info("批量插入全部索引社区文章模块");
            BulkRequest.Builder br = new BulkRequest.Builder();
            esCommunityArticleIndexList.stream().forEach(communityArticle -> {
                ESAllIndex esAllIndex = new ESAllIndex();
                BeanUtils.copyProperties(communityArticle, esAllIndex);
                esAllIndex.setProfile(communityArticle.getBrief());
                esAllIndex.setPhoto(communityArticle.getPicture());
                esAllIndex.setType(SearchTypeEnum.COMMUNITY_ARTICLE.getCode());
                esAllIndex.setViewCount(Long.valueOf(communityArticle.getViewCount()));
                esAllIndex.setAssociationId(communityArticle.getId());
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
     * 查询综合社区文章列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 9:57
     */
    @Override
    public R queryComprehensiveCommunityArticle(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("title",new HighlightField.Builder().build());
            highlightFieldMap.put("brief",new HighlightField.Builder().build());
            SearchResponse<ESCommunityArticleIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.communityArticle)
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
                                    .order(SortOrder.Desc))), ESCommunityArticleIndex.class);

            // 设置搜索结果高亮
            List<ESCommunityArticleIndex> esCommunityArticleIndexList = setHighlight(response);

            return R.ok(esCommunityArticleIndexList);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询评论最多社区文章列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:45
     */
    @Override
    public R queryCommentCommunityArticle(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("title",new HighlightField.Builder().build());
            highlightFieldMap.put("brief",new HighlightField.Builder().build());
            SearchResponse<ESCommunityArticleIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.communityArticle)
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
                                    .order(SortOrder.Desc))), ESCommunityArticleIndex.class);

            // 设置搜索结果高亮
            List<ESCommunityArticleIndex> esCommunityArticleIndexList = setHighlight(response);

            return R.ok(esCommunityArticleIndexList);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询收藏数最多社区文章列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:51
     */
    @Override
    public R queryCollectCommunityArticle(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("title",new HighlightField.Builder().build());
            highlightFieldMap.put("brief",new HighlightField.Builder().build());
            SearchResponse<ESCommunityArticleIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.communityArticle)
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
                                    .order(SortOrder.Desc))), ESCommunityArticleIndex.class);

            // 设置搜索结果高亮
            List<ESCommunityArticleIndex> esCommunityArticleIndexList = setHighlight(response);

            return R.ok(esCommunityArticleIndexList);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }


    /**
     * 查询游览数最多社区文章列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:52
     */
    @Override
    public R queryViewCommunityArticle(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("title",new HighlightField.Builder().build());
            highlightFieldMap.put("brief",new HighlightField.Builder().build());
            SearchResponse<ESCommunityArticleIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.communityArticle)
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
                                    .order(SortOrder.Desc))), ESCommunityArticleIndex.class);

            // 设置搜索结果高亮
            List<ESCommunityArticleIndex> esCommunityArticleIndexList = setHighlight(response);

            return R.ok(esCommunityArticleIndexList);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询最热社区文章列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:53
     */
    @Override
    public R queryHireCommunityArticle(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("title",new HighlightField.Builder().build());
            highlightFieldMap.put("brief",new HighlightField.Builder().build());
            SearchResponse<ESCommunityArticleIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.communityArticle)
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
                                    .field("viewCount")
                                    .order(SortOrder.Desc)
                                    .order(SortOrder.Desc)
                                    .field("commentCount")
                                    .field("collectCount")
                                    .order(SortOrder.Desc)
                                    .field("createTime")
                                    .order(SortOrder.Desc))), ESCommunityArticleIndex.class);

            // 设置搜索结果高亮
            List<ESCommunityArticleIndex> esCommunityArticleIndexList = setHighlight(response);

            return R.ok(esCommunityArticleIndexList);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询最新社区文章列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:54
     */
    @Override
    public R queryLatestCommunityArticle(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("title",new HighlightField.Builder().build());
            highlightFieldMap.put("brief",new HighlightField.Builder().build());
            SearchResponse<ESCommunityArticleIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.communityArticle)
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
                                    .order(SortOrder.Desc))), ESCommunityArticleIndex.class);

            // 设置搜索结果高亮
            List<ESCommunityArticleIndex> esCommunityArticleIndexList = setHighlight(response);

            return R.ok(esCommunityArticleIndexList);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询点赞人数最多社区文章列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:52
     */
    @Override
    public R queryLikeCommunityArticle(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("title",new HighlightField.Builder().build());
            highlightFieldMap.put("brief",new HighlightField.Builder().build());
            SearchResponse<ESCommunityArticleIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.communityArticle)
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
                                    .order(SortOrder.Desc))), ESCommunityArticleIndex.class);

            // 设置搜索结果高亮
            List<ESCommunityArticleIndex> esCommunityArticleIndexList = setHighlight(response);

            return R.ok(esCommunityArticleIndexList);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询评分降序社区文章列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:52
     */
    @Override
    public R queryScoreCommunityArticle(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("title",new HighlightField.Builder().build());
            highlightFieldMap.put("brief",new HighlightField.Builder().build());
            SearchResponse<ESCommunityArticleIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.communityArticle)
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
                                    .order(SortOrder.Desc))), ESCommunityArticleIndex.class);

            // 设置搜索结果高亮
            List<ESCommunityArticleIndex> esCommunityArticleIndexList = setHighlight(response);

            return R.ok(esCommunityArticleIndexList);
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
    private List<ESCommunityArticleIndex> setHighlight(SearchResponse<ESCommunityArticleIndex> response) {
        List<Hit<ESCommunityArticleIndex>> hits = response.hits().hits();
        List<ESCommunityArticleIndex> esCommunityArticleIndexList = new ArrayList<>();
        for (Hit<ESCommunityArticleIndex> hit : hits) {
            Map<String, List<String>> highlight = hit.highlight();
            ESCommunityArticleIndex source = hit.source();
            // 高亮赋值
            if (source != null) {
                if (highlight.get("title") != null) {
                    source.setTitle(highlight.get("title").get(0));
                }

                if (highlight.get("brief") != null) {
                    source.setBrief(highlight.get("brief").get(0));
                }
            }
            esCommunityArticleIndexList.add(source);
        }

        return esCommunityArticleIndexList;
    }
}
