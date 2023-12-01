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
import com.monkey.monkeysearch.pojo.Achievement;
import com.monkey.monkeysearch.pojo.ESArticleIndex;
import com.monkey.monkeysearch.pojo.ESCommunityArticleIndex;
import com.monkey.monkeysearch.pojo.ESCommunityIndex;
import com.monkey.monkeysearch.service.ESCommunityService;
import com.monkey.monkeysearch.util.ESCommonMethods;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: wusihao
 * @date: 2023/11/12 10:51
 * @version: 1.0
 * @description:
 */
@Service
@Slf4j
public class ESCommunityServiceImpl implements ESCommunityService {
    @Resource
    private SearchToCommunityFeign searchToCommunityFeign;
    @Resource
    private ElasticsearchClient elasticsearchClient;

    /**
     * 将社区数据库中所有数据存入elasticsearch社区文档中
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/11 11:18
     */
    @Override
    public R insertCommunityDocument() {
        try {
            // 查询所有社区
            R result = searchToCommunityFeign.queryAllCommunity();
            List<ESCommunityIndex> esCommunityIndexList = (List<ESCommunityIndex>) result.getData(new TypeReference<List<ESCommunityIndex>>(){});

            // 将社区批量插入elasticsearch
            BulkRequest.Builder br = new BulkRequest.Builder();
            esCommunityIndexList.stream().forEach(community -> {
                br.operations(op -> op
                        .index(idx -> idx
                                .index(IndexConstant.community)
                                .id(community.getId())
                                .document(community)));
            });

            BulkResponse response = elasticsearchClient.bulk(br.build());
            if (response.errors()) {
                log.error("Bulk had errors");
                for (BulkResponseItem item: response.items()) {
                    if (item.error() != null) {
                        log.error(item.error().reason());
                    }
                }
                throw new MonkeyBlogException(SearchExceptionEnum.BULK_INSERT_COMMUNITY.getCode(), SearchExceptionEnum.BULK_INSERT_COMMUNITY.getMsg());
            }

            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询综合社区列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 9:57
     */
    @Override
    public R queryComprehensiveCommunity(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("name",new HighlightField.Builder().build());
            highlightFieldMap.put("description",new HighlightField.Builder().build());
            highlightFieldMap.put("classificationName",new HighlightField.Builder().build());
            highlightFieldMap.put("attributeLabelName",new HighlightField.Builder().build());
            highlightFieldMap.put("contentLabelName",new HighlightField.Builder().build());
            SearchResponse<ESCommunityIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.community)
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
                                    .field("memberCount")
                                    .order(SortOrder.Desc)
                                    .field("articleCount")
                                    .order(SortOrder.Desc)
                                    .field("createTime")
                                    .order(SortOrder.Desc))), ESCommunityIndex.class);

            // 设置搜索结果高亮
            List<ESCommunityIndex> esCommunityIndexList = setHighlight(response);

            return R.ok(esCommunityIndexList);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询最热社区列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:53
     */
    @Override
    public R queryHireCommunity(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("name",new HighlightField.Builder().build());
            highlightFieldMap.put("description",new HighlightField.Builder().build());
            highlightFieldMap.put("classificationName",new HighlightField.Builder().build());
            highlightFieldMap.put("attributeLabelName",new HighlightField.Builder().build());
            highlightFieldMap.put("contentLabelName",new HighlightField.Builder().build());
            SearchResponse<ESCommunityIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.community)
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
                                    .field("memberCount")
                                    .order(SortOrder.Desc)
                                    .field("articleCount")
                                    .order(SortOrder.Desc)
                                    .order(SortOrder.Desc))), ESCommunityIndex.class);

            // 设置搜索结果高亮
            List<ESCommunityIndex> esCommunityIndexList = setHighlight(response);

            return R.ok(esCommunityIndexList);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询最新社区列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:54
     */
    @Override
    public R queryLatestCommunity(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("name",new HighlightField.Builder().build());
            highlightFieldMap.put("description",new HighlightField.Builder().build());
            highlightFieldMap.put("classificationName",new HighlightField.Builder().build());
            highlightFieldMap.put("attributeLabelName",new HighlightField.Builder().build());
            highlightFieldMap.put("contentLabelName",new HighlightField.Builder().build());
            SearchResponse<ESCommunityIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.community)
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
                                    .order(SortOrder.Desc))), ESCommunityIndex.class);

            // 设置搜索结果高亮
            List<ESCommunityIndex> esCommunityIndexList = setHighlight(response);

            return R.ok(esCommunityIndexList);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询社区成员最多社区列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:52
     */
    @Override
    public R queryMemberCommunity(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("name",new HighlightField.Builder().build());
            highlightFieldMap.put("description",new HighlightField.Builder().build());
            highlightFieldMap.put("classificationName",new HighlightField.Builder().build());
            highlightFieldMap.put("attributeLabelName",new HighlightField.Builder().build());
            highlightFieldMap.put("contentLabelName",new HighlightField.Builder().build());
            SearchResponse<ESCommunityIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.community)
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
                                    .field("memberCount")
                                    .order(SortOrder.Desc))), ESCommunityIndex.class);

            // 设置搜索结果高亮
            List<ESCommunityIndex> esCommunityIndexList = setHighlight(response);

            return R.ok(esCommunityIndexList);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询社区文章最多社区列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:52
     */
    @Override
    public R queryArticleCommunity(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("name",new HighlightField.Builder().build());
            highlightFieldMap.put("description",new HighlightField.Builder().build());
            highlightFieldMap.put("classificationName",new HighlightField.Builder().build());
            highlightFieldMap.put("attributeLabelName",new HighlightField.Builder().build());
            highlightFieldMap.put("contentLabelName",new HighlightField.Builder().build());
            SearchResponse<ESCommunityIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.community)
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
                                    .field("articleCount")
                                    .order(SortOrder.Desc))), ESCommunityIndex.class);

            // 设置搜索结果高亮
            List<ESCommunityIndex> esCommunityIndexList = setHighlight(response);

            return R.ok(esCommunityIndexList);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询所有社区文档
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/26 20:56
     */
    @Override
    public R queryCommunityDocument() {
        try {
            log.info("查询所有社区文档");
            SearchResponse<ESCommunityIndex> response = elasticsearchClient.search(search -> search
                    .index(IndexConstant.community)
                    .query(query -> query
                            .matchAll(all -> all)), ESCommunityIndex.class);
            List<ESCommunityIndex> communityIndexList = new ArrayList<>();
            List<Hit<ESCommunityIndex>> hits = response.hits().hits();
            for (Hit<ESCommunityIndex> hit : hits) {
                ESCommunityIndex source = hit.source();
                communityIndexList.add(source);
            }

            return R.ok(communityIndexList);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 删除所有社区文档
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/26 20:57
     */
    @Override
    public R deleteCommunityDocument() {
        try {
            log.info("删除所有社区文档");
            elasticsearchClient.deleteByQuery(delete -> delete
                    .index(IndexConstant.community)
                    .query(query -> query
                            .matchAll(matchAll -> matchAll)));

            // 查询该社区文章每个用户对应的的点赞数，游览数，收藏数总和
            SearchResponse<ESCommunityArticleIndex>  response =  ESCommonMethods.queryAllUserAchievement(
                    IndexConstant.communityArticle, elasticsearchClient, ESCommunityArticleIndex.class
            );
            // 得到该社区文章每个用户对应的的点赞数，游览数，收藏数总和
            Map<Achievement, Long> communityArticle = ESCommonMethods.getAchievement(response);

            // 得到社区文章id
            List<String> communityArticleIdList = ESCommonMethods.getCommunityArticleIdList(response);

            if (communityArticleIdList.size() > 0) {
                // 通过社区文章id批量删除社区文章
                ESCommonMethods.bulkDeleteCommunityArticle(communityArticleIdList, elasticsearchClient);
                // 批量减去用户对应的游览数 点赞数，收藏数
                ESCommonMethods.bulkSubUserAchievement(communityArticle, elasticsearchClient);
            }

            log.info("删除全部索引中的社区文档");
            elasticsearchClient.deleteByQuery(delete -> delete
                    .index(IndexConstant.all)
                    .query(query -> query
                            .match(match -> match
                                    .field("type")
                                    .query(SearchTypeEnum.COMMUNITY.getCode()))));
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
    private List<ESCommunityIndex> setHighlight(SearchResponse<ESCommunityIndex> response) {
        List<Hit<ESCommunityIndex>> hits = response.hits().hits();
        List<ESCommunityIndex> esCommunityIndexList = new ArrayList<>();
        for (Hit<ESCommunityIndex> hit : hits) {
            Map<String, List<String>> highlight = hit.highlight();
            ESCommunityIndex source = hit.source();
            // 高亮赋值
            if (source != null) {
                if (highlight.get("name") != null) {
                    source.setName(highlight.get("name").get(0));
                }

                if (highlight.get("description") != null) {
                    source.setDescription(highlight.get("description").get(0));
                }

                if (highlight.get("classificationName") != null) {
                    source.setClassificationName(highlight.get("classificationName").get(0));
                }

                if (highlight.get("contentLabelName") != null) {
                    source.setContentLabelName(highlight.get("contentLabelName"));
                }

                if (highlight.get("attributeLabelName") != null) {
                    source.setAttributeLabelName(highlight.get("attributeLabelName").get(0));
                }

            }
            esCommunityIndexList.add(source);
        }

        return esCommunityIndexList;
    }
}
