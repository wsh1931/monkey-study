package com.monkey.monkeysearch.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeysearch.constant.IndexConstant;
import com.monkey.monkeysearch.constant.SearchTypeEnum;
import com.monkey.monkeysearch.pojo.ESAllIndex;
import com.monkey.monkeysearch.pojo.ESCommunityArticleIndex;
import com.monkey.monkeysearch.pojo.ESCommunityIndex;
import com.monkey.monkeysearch.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: wusihao
 * @date: 2023/11/16 9:49
 * @version: 1.0
 * @description:
 */
@Service
@Slf4j
public class ESAllServiceImpl implements ESAllService {
    @Resource
    private ElasticsearchClient elasticsearchClient;
    @Resource
    private ESArticleService esArticleService;
    @Resource
    private ESCourseService esCourseService;
    @Resource
    private ESQuestionService esQuestionService;
    @Resource
    private ESCommunityService esCommunityService;
    @Resource
    private ESResourceService esResourceService;
    @Resource
    private ESUserService esUserService;
    /**
     * 查询综合全部列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 9:57
     */
    @Override
    public R queryComprehensiveAll(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("title",new HighlightField.Builder().build());
            highlightFieldMap.put("profile",new HighlightField.Builder().build());
            highlightFieldMap.put("labelName", new HighlightField.Builder().build());
            SearchResponse<ESAllIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.all)
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
                                    .order(SortOrder.Desc))), ESAllIndex.class);

            // 设置搜索结果高亮
            List<ESAllIndex> esAllIndexList = setHighlight(response);
            long totals = response.hits().total().value();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("esAllIndexList", esAllIndexList);
            jsonObject.put("totals", totals);
            return R.ok(jsonObject);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询评论最多全部列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:45
     */
    @Override
    public R queryCommentAll(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("title",new HighlightField.Builder().build());
            highlightFieldMap.put("profile",new HighlightField.Builder().build());
            highlightFieldMap.put("labelName", new HighlightField.Builder().build());
            SearchResponse<ESAllIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.all)
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
                                    .order(SortOrder.Desc))), ESAllIndex.class);

            // 设置搜索结果高亮
            List<ESAllIndex> esAllIndexList = setHighlight(response);
            long totals = response.hits().total().value();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("esAllIndexList", esAllIndexList);
            jsonObject.put("totals", totals);
            return R.ok(jsonObject);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询收藏数最多全部列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:51
     */
    @Override
    public R queryCollectAll(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("title",new HighlightField.Builder().build());
            highlightFieldMap.put("profile",new HighlightField.Builder().build());
            highlightFieldMap.put("labelName", new HighlightField.Builder().build());
            SearchResponse<ESAllIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.all)
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
                                    .order(SortOrder.Desc))), ESAllIndex.class);

            // 设置搜索结果高亮
            List<ESAllIndex> esAllIndexList = setHighlight(response);
            long totals = response.hits().total().value();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("esAllIndexList", esAllIndexList);
            jsonObject.put("totals", totals);
            return R.ok(jsonObject);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询点赞数最多全部列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:52
     */
    @Override
    public R queryLikeAll(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("title",new HighlightField.Builder().build());
            highlightFieldMap.put("profile",new HighlightField.Builder().build());
            highlightFieldMap.put("labelName", new HighlightField.Builder().build());
            SearchResponse<ESAllIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.all)
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
                                    .order(SortOrder.Desc))), ESAllIndex.class);

            // 设置搜索结果高亮
            List<ESAllIndex> esAllIndexList = setHighlight(response);
            long totals = response.hits().total().value();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("esAllIndexList", esAllIndexList);
            jsonObject.put("totals", totals);
            return R.ok(jsonObject);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询游览数最多全部列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:52
     */
    @Override
    public R queryViewAll(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("title",new HighlightField.Builder().build());
            highlightFieldMap.put("profile",new HighlightField.Builder().build());
            highlightFieldMap.put("labelName", new HighlightField.Builder().build());
            SearchResponse<ESAllIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.all)
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
                                    .order(SortOrder.Desc))), ESAllIndex.class);

            // 设置搜索结果高亮
            List<ESAllIndex> esAllIndexList = setHighlight(response);
            long totals = response.hits().total().value();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("esAllIndexList", esAllIndexList);
            jsonObject.put("totals", totals);
            return R.ok(jsonObject);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询最热全部列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:53
     */
    @Override
    public R queryHireAll(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("title",new HighlightField.Builder().build());
            highlightFieldMap.put("profile",new HighlightField.Builder().build());
            highlightFieldMap.put("labelName", new HighlightField.Builder().build());
            SearchResponse<ESAllIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.all)
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
                                    .order(SortOrder.Desc))), ESAllIndex.class);

            // 设置搜索结果高亮
            List<ESAllIndex> esAllIndexList = setHighlight(response);
            long totals = response.hits().total().value();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("esAllIndexList", esAllIndexList);
            jsonObject.put("totals", totals);
            return R.ok(jsonObject);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询最新全部列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:54
     */
    @Override
    public R queryLatestAll(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("title",new HighlightField.Builder().build());
            highlightFieldMap.put("profile",new HighlightField.Builder().build());
            highlightFieldMap.put("labelName", new HighlightField.Builder().build());
            SearchResponse<ESAllIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.all)
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
                                    .order(SortOrder.Desc))), ESAllIndex.class);

            // 设置搜索结果高亮
            List<ESAllIndex> esAllIndexList = setHighlight(response);
            long totals = response.hits().total().value();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("esAllIndexList", esAllIndexList);
            jsonObject.put("totals", totals);
            return R.ok(jsonObject);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询所有文档
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/26 20:48
     */
    @Override
    public R queryAllDocument() {
        try {
            log.info("查询所有文档");
            SearchResponse<ESAllIndex> response = elasticsearchClient.search(search -> search
                    .index(IndexConstant.all)
                    .query(query -> query
                            .matchAll(all -> all)), ESAllIndex.class);
            List<ESAllIndex> allIndexList = new ArrayList<>();
            List<Hit<ESAllIndex>> hits = response.hits().hits();
            for (Hit<ESAllIndex> hit : hits) {
                ESAllIndex source = hit.source();
                allIndexList.add(source);
            }

            return R.ok(allIndexList);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }

    }

    /**
     * 删除所有文档
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/26 20:51
     */
    @Override
    public R deleteAllDocument() {
        try {
            esArticleService.deleteArticleDocument();
            esQuestionService.deleteAllQuestionDocument();
            esCourseService.deleteAllCourseDocument();
            esCommunityService.deleteCommunityDocument();
            esResourceService.deleteAllResourceDocument();
            esUserService.deleteAllUserDocument();
            log.info("删除所有文档");
            elasticsearchClient.deleteByQuery(delete -> delete
                    .index(IndexConstant.all)
                    .query(query -> query
                            .matchAll(matchAll -> matchAll)));

            log.info("删除全部索引中文档");
            elasticsearchClient.deleteByQuery(delete -> delete
                    .index(IndexConstant.all)
                    .query(query -> query
                            .matchAll(all -> all)));
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
    private List<ESAllIndex> setHighlight(SearchResponse<ESAllIndex> response) {
        List<Hit<ESAllIndex>> hits = response.hits().hits();
        List<ESAllIndex> esAllIndexList = new ArrayList<>();
        for (Hit<ESAllIndex> hit : hits) {
            Map<String, List<String>> highlight = hit.highlight();
            ESAllIndex source = hit.source();
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
            esAllIndexList.add(source);
        }

        return esAllIndexList;
    }
}
