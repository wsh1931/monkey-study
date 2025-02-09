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
import com.monkey.monkeysearch.feign.SearchToCourseFeign;
import com.monkey.monkeysearch.pojo.*;
import com.monkey.monkeysearch.service.ESCourseService;
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
 * @date: 2023/11/11 11:17
 * @version: 1.0
 * @description:
 */
@Service
@Slf4j
public class ESCourseServiceImpl implements ESCourseService {
    @Resource
    private SearchToCourseFeign searchToCourseFeign;
    @Resource
    private ElasticsearchClient elasticsearchClient;

    /**
     * 将课程数据库中所有数据存入elasticsearch课程文档中
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/11 11:18
     */
    @Override
    public R insertCourseDocument() {
        try {
            // 查询所有课程
            R result = searchToCourseFeign.queryAllCourse();

            List<ESCourseIndex> esCourseIndexList = (List<ESCourseIndex>) result.getData(new TypeReference<List<ESCourseIndex>>(){});

            // 将课程批量插入elasticsearch
            BulkRequest.Builder br = new BulkRequest.Builder();
            esCourseIndexList.stream().forEach(course -> {
                br.operations(op -> op
                        .index(idx -> idx
                                .index(IndexConstant.course)
                                .id(course.getId())
                                .document(course)));
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

            this.insertAllCounse(esCourseIndexList);
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 批量插入全部索引课程模块
     *
     * @param esCourseIndexList 课程索引集合
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/16 9:21
     */
    private void insertAllCounse(List<ESCourseIndex> esCourseIndexList) {
        try {
            log.info("批量插入全部索引课程模块");
            BulkRequest.Builder br = new BulkRequest.Builder();
            esCourseIndexList.stream().forEach(course -> {
                ESAllIndex esAllIndex = new ESAllIndex();
                BeanUtils.copyProperties(course, esAllIndex);
                esAllIndex.setProfile(course.getIntroduce());
                esAllIndex.setPhoto(course.getPicture());
                esAllIndex.setType(SearchTypeEnum.COURSE.getCode());
                esAllIndex.setAssociationId(course.getId());
                esAllIndex.setCommentCount(Math.toIntExact(course.getCommentCount()));
                esAllIndex.setCollectCount(Math.toIntExact(course.getCollectCount()));
                esAllIndex.setCommentCount(Math.toIntExact(course.getCommentCount()));
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
     * 查询综合课程列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 9:57
     */
    @Override
    public R queryComprehensiveCourse(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("title",new HighlightField.Builder().build());
            highlightFieldMap.put("introduce",new HighlightField.Builder().build());
            highlightFieldMap.put("labelName", new HighlightField.Builder().build());
            SearchResponse<ESCourseIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.course)
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
                                    .field("studyCount")
                                    .order(SortOrder.Desc)
                                    .field("collectCount")
                                    .order(SortOrder.Desc)
                                    .field("commentCount")
                                    .order(SortOrder.Desc)
                                    .field("viewCount")
                                    .order(SortOrder.Desc)
                                    .field("createTime")
                                    .order(SortOrder.Desc))), ESCourseIndex.class);

            // 设置搜索结果高亮
            List<ESCourseIndex> esCourseIndexList = setHighlight(response);
            long totals = response.hits().total().value();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("esCourseIndexList", esCourseIndexList);
            jsonObject.put("totals", totals);
            return R.ok(jsonObject);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询评论最多课程列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:45
     */
    @Override
    public R queryCommentCourse(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("title",new HighlightField.Builder().build());
            highlightFieldMap.put("introduce",new HighlightField.Builder().build());
            highlightFieldMap.put("labelName", new HighlightField.Builder().build());
            SearchResponse<ESCourseIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.course)
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
                                    .order(SortOrder.Desc))), ESCourseIndex.class);

            // 设置搜索结果高亮
            List<ESCourseIndex> esCourseIndexList = setHighlight(response);
            long totals = response.hits().total().value();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("esCourseIndexList", esCourseIndexList);
            jsonObject.put("totals", totals);
            return R.ok(jsonObject);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询收藏数最多课程列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:51
     */
    @Override
    public R queryCollectCourse(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("title",new HighlightField.Builder().build());
            highlightFieldMap.put("introduce",new HighlightField.Builder().build());
            highlightFieldMap.put("labelName", new HighlightField.Builder().build());
            SearchResponse<ESCourseIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.course)
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
                                    .order(SortOrder.Desc))), ESCourseIndex.class);

            // 设置搜索结果高亮
            List<ESCourseIndex> esCourseIndexList = setHighlight(response);
            long totals = response.hits().total().value();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("esCourseIndexList", esCourseIndexList);
            jsonObject.put("totals", totals);
            return R.ok(jsonObject);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }
    

    /**
     * 查询游览数最多课程列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:52
     */
    @Override
    public R queryViewCourse(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("title",new HighlightField.Builder().build());
            highlightFieldMap.put("introduce",new HighlightField.Builder().build());
            highlightFieldMap.put("labelName", new HighlightField.Builder().build());
            SearchResponse<ESCourseIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.course)
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
                                    .order(SortOrder.Desc))), ESCourseIndex.class);

            // 设置搜索结果高亮
            List<ESCourseIndex> esCourseIndexList = setHighlight(response);
            long totals = response.hits().total().value();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("esCourseIndexList", esCourseIndexList);
            jsonObject.put("totals", totals);
            return R.ok(jsonObject);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询最热课程列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:53
     */
    @Override
    public R queryHireCourse(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("title",new HighlightField.Builder().build());
            highlightFieldMap.put("introduce",new HighlightField.Builder().build());
            highlightFieldMap.put("labelName", new HighlightField.Builder().build());
            SearchResponse<ESCourseIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.course)
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
                                    .field("studyCount")
                                    .order(SortOrder.Desc)
                                    .field("viewCount")
                                    .order(SortOrder.Desc)
                                    .order(SortOrder.Desc)
                                    .field("commentCount")
                                    .field("collectCount")
                                    .order(SortOrder.Desc)
                                    .field("createTime")
                                    .order(SortOrder.Desc))), ESCourseIndex.class);

            // 设置搜索结果高亮
            List<ESCourseIndex> esCourseIndexList = setHighlight(response);
            long totals = response.hits().total().value();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("esCourseIndexList", esCourseIndexList);
            jsonObject.put("totals", totals);
            return R.ok(jsonObject);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询最新课程列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:54
     */
    @Override
    public R queryLatestCourse(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("title",new HighlightField.Builder().build());
            highlightFieldMap.put("introduce",new HighlightField.Builder().build());
            highlightFieldMap.put("labelName", new HighlightField.Builder().build());
            SearchResponse<ESCourseIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.course)
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
                                    .order(SortOrder.Desc))), ESCourseIndex.class);

            // 设置搜索结果高亮
            List<ESCourseIndex> esCourseIndexList = setHighlight(response);
            long totals = response.hits().total().value();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("esCourseIndexList", esCourseIndexList);
            jsonObject.put("totals", totals);
            return R.ok(jsonObject);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询学习人数最多课程列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:52
     */
    @Override
    public R queryStudyCourse(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("title",new HighlightField.Builder().build());
            highlightFieldMap.put("introduce",new HighlightField.Builder().build());
            highlightFieldMap.put("labelName", new HighlightField.Builder().build());
            SearchResponse<ESCourseIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.course)
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
                                    .field("studyCount")
                                    .order(SortOrder.Desc))), ESCourseIndex.class);

            // 设置搜索结果高亮
            List<ESCourseIndex> esCourseIndexList = setHighlight(response);
            long totals = response.hits().total().value();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("esCourseIndexList", esCourseIndexList);
            jsonObject.put("totals", totals);
            return R.ok(jsonObject);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询所有课程文档
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/26 20:59
     */
    @Override
    public R queryAllCourseDocument() {
        try {
            log.info("查询所有课程文档");
            SearchResponse<ESCourseIndex> response = elasticsearchClient.search(search -> search
                    .index(IndexConstant.course)
                    .query(query -> query
                            .matchAll(all -> all)), ESCourseIndex.class);
            List<ESCourseIndex> esCourseIndexList = new ArrayList<>();
            List<Hit<ESCourseIndex>> hits = response.hits().hits();
            for (Hit<ESCourseIndex> hit : hits) {
                ESCourseIndex source = hit.source();
                esCourseIndexList.add(source);
            }

            return R.ok(esCourseIndexList);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 删除所有课程文档
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/26 20:59
     */
    @Override
    public R deleteAllCourseDocument() {
        try {
            // 得到该课程每个用户对应的的点赞数，游览数，收藏数总和
            SearchResponse<ESCourseIndex> esCourseIndexSearchResponse = ESCommonMethods.queryAllUserAchievement(
                    IndexConstant.course, elasticsearchClient, ESCourseIndex.class);
            Map<Achievement, Long> course = ESCommonMethods.getAchievement(esCourseIndexSearchResponse);

            // 批量减去用户对应的游览数 点赞数，收藏数
            ESCommonMethods.bulkSubUserAchievement(course, elasticsearchClient);
            log.info("删除所有课程文档");
            elasticsearchClient.deleteByQuery(delete -> delete
                    .index(IndexConstant.course)
                    .query(query -> query
                            .matchAll(matchAll -> matchAll)));

            log.info("删除全部索引中的课程文档");
            elasticsearchClient.deleteByQuery(delete -> delete
                    .index(IndexConstant.all)
                    .query(query -> query
                            .match(match -> match
                                    .field("type")
                                    .query(SearchTypeEnum.COURSE.getCode()))));
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询评分降序课程列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:52
     */
    @Override
    public R queryScoreCourse(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("title",new HighlightField.Builder().build());
            highlightFieldMap.put("introduce",new HighlightField.Builder().build());
            highlightFieldMap.put("labelName", new HighlightField.Builder().build());
            SearchResponse<ESCourseIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.course)
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
                                    .order(SortOrder.Desc))), ESCourseIndex.class);

            // 设置搜索结果高亮
            List<ESCourseIndex> esCourseIndexList = setHighlight(response);
            long totals = response.hits().total().value();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("esCourseIndexList", esCourseIndexList);
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
    private List<ESCourseIndex> setHighlight(SearchResponse<ESCourseIndex> response) {
        List<Hit<ESCourseIndex>> hits = response.hits().hits();
        List<ESCourseIndex> esCourseIndexList = new ArrayList<>();
        for (Hit<ESCourseIndex> hit : hits) {
            Map<String, List<String>> highlight = hit.highlight();
            ESCourseIndex source = hit.source();
            // 高亮赋值
            if (source != null) {
                if (highlight.get("title") != null) {
                    source.setTitle(highlight.get("title").get(0));
                }

                if (highlight.get("introduce") != null) {
                    source.setIntroduce(highlight.get("introduce").get(0));
                }

                if (highlight.get("labelName") != null) {
                    List<String> list = highlight.get("labelName");
                    source.setLabelName(list);
                }
            }
            esCourseIndexList.add(source);
        }

        return esCourseIndexList;
    }
}
