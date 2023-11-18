package com.monkey.monkeysearch.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeysearch.constant.IndexConstant;
import com.monkey.monkeysearch.constant.SearchExceptionEnum;
import com.monkey.monkeysearch.constant.SearchTypeEnum;
import com.monkey.monkeysearch.mapper.SearchHistoryMapper;
import com.monkey.monkeysearch.pojo.*;
import com.monkey.monkeysearch.rabbitmq.EventConstant;
import com.monkey.monkeysearch.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeysearch.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeysearch.service.SearchCenterService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author: wusihao
 * @date: 2023/11/17 20:24
 * @version: 1.0
 * @description:
 */
@Service
public class SearchCenterServiceImpl implements SearchCenterService {
    @Resource
    private ElasticsearchClient elasticsearchClient;
    @Resource
    private SearchHistoryMapper searchHistoryMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;
    /**
     * 查找相关搜索
     *
     * @param keyWord 搜索关键字
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/17 20:27
     */
    @Override
    public R queryRelatedSearch(String keyWord, Integer searchType) {
        SearchTypeEnum searchTypeByCode = SearchTypeEnum.getSearchTypeByCode(searchType);
        switch (searchTypeByCode) {
            case ARTICLE:
                return this.queryRelatedArticle(keyWord);
            case COURSE:
                return this.queryRelatedCourse(keyWord);
            case COMMUNITY_ARTICLE:
                return this.queryRelatedCommunityArticle(keyWord);
            case RESOURCE:
                return this.queryRelatedResource(keyWord);
            case COMMUNITY:
                return this.queryRelatedCommunity(keyWord);
            case USER:
                return this.queryRelatedUser(keyWord);
            case ALL:
                return this.queryRelatedAll(keyWord);
            case QUESTION:
                return this.queryRelatedQuestion(keyWord);
        }
        return R.error(SearchExceptionEnum.SEARCH_TYPE_ERROR.getCode(), SearchExceptionEnum.SEARCH_TYPE_ERROR.getMsg());
    }

    /**
     * 查询该登录用户最近搜索信息
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/17 22:20
     */
    @Override
    public R queryHistorySearch(String userId) {
        if (userId == null || "".equals(userId)) {
            return R.ok();
        }

        QueryWrapper<SearchHistory> searchHistoryQueryWrapper = new QueryWrapper<>();
        searchHistoryQueryWrapper.eq("user_id", userId);
        searchHistoryQueryWrapper.last("limit 10");
        searchHistoryQueryWrapper.orderByDesc("create_time");
        List<SearchHistory> searchHistories = searchHistoryMapper.selectList(searchHistoryQueryWrapper);
        return R.ok(searchHistories);
    }

    /**
     * 将搜索信息插入历史搜索
     *
     * @param userId 搜索用户id
     * @param keyword 搜素关键字
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/18 11:27
     */
    @Override
    public R insertHistorySearch(String userId, String keyword) {
        if (userId == null || "".equals(userId)) {
            return R.ok();
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.insertSearchHistory);
        jsonObject.put("userId", userId);
        jsonObject.put("keyword", keyword);
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.searchInsertDirectExchange,
                RabbitmqRoutingName.searchInsertRouting, message);
        return R.ok();
    }

    class Search {
        private String value;
        private String label;

        public void setLabel(String label) {
            this.label = label;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public String getValue() {
            return value;
        }

        public Search(String value, String label) {
            this.value = value;
            this.label = label;
        }
    }

    /**
     * 通过拼音/中文搜索标题信息
     *
     * @param keyword 搜索关键字
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/18 14:47
     */
    @Override
    public R searchTitleByEnglishOrChina(String keyword) {
        try {
            SearchResponse<ESAllIndex> response = elasticsearchClient.search(search -> search
                    .index(IndexConstant.all)
                    .query(query -> query
                            .match(match -> match
                                    .field("title")
                                    .query(keyword)))
                    .from(0)
                    .size(20)
                    .source(source -> source
                            .filter(filter -> filter
                                    .includes("title"))), ESAllIndex.class);

            List<Search> searches = new ArrayList<>();
            List<Hit<ESAllIndex>> hits = response.hits().hits();
            Set<String> set = new HashSet<>(hits.size());
            for (Hit<ESAllIndex> esAllIndexHit : hits) {
                ESAllIndex source = esAllIndexHit.source();
                String title = source.getTitle();
                if (!set.contains(title)) {
                    set.add(title);
                    searches.add(new Search(title, title));
                }
            }

            int len = searches.size();
            return R.ok(searches.subList(0, Math.min(10, len)));
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 得到所有相关标题集合
     *
     * @param keyWord 搜索关键字
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/17 22:01
     */
    private R queryRelatedAll(String keyWord) {
        try {
            SearchResponse<ESAllIndex> response = elasticsearchClient.search(search -> search
                    .index(IndexConstant.all)
                    .query(query -> query
                            .match(match -> match
                                    .field("title")
                                    .query(keyWord)))
                    .from(0)
                    .size(30)
                    .source(source -> source
                            .filter(filter -> filter
                                    .includes("title"))), ESAllIndex.class)
                    ;

            List<ESAllIndex> esAllIndexList = new ArrayList<>();
            List<Hit<ESAllIndex>> hits = response.hits().hits();
            Set<String> set = new HashSet<>(hits.size());
            hits.forEach(hit -> {
                ESAllIndex source = hit.source();
                String title = source.getTitle();
                if (!set.contains(title)) {
                    set.add(title);
                    esAllIndexList.add(source);
                }
            });

            int len = esAllIndexList.size();
            return R.ok(esAllIndexList.subList(0, Math.min(10, len)));
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 得到相关用户标签集合
     *
     * @param keyWord 搜索关键字
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/17 22:00
     */
    private R queryRelatedUser(String keyWord) {
        try {
            SearchResponse<ESUserIndex> response = elasticsearchClient.search(search -> search
                    .index(IndexConstant.user)
                    .query(query -> query
                            .match(match -> match
                                    .field("username")
                                    .query(keyWord)))
                    .from(0)
                    .size(30)
                    .source(source -> source
                            .filter(filter -> filter
                                    .includes("username"))), ESUserIndex.class);

            List<ESUserIndex> esUserIndexList = new ArrayList<>();
            List<Hit<ESUserIndex>> hits = response.hits().hits();

            Set<String> set = new HashSet<>(hits.size());
            hits.forEach(hit -> {
                ESUserIndex source = hit.source();
                String username = source.getUsername();
                if (!set.contains(username)) {
                    set.add(username);
                    esUserIndexList.add(source);
                }
            });

            int len = esUserIndexList.size();
            return R.ok(esUserIndexList.subList(0, Math.min(10, len)));
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询相关社区标题集合
     *
     * @param keyWord 搜索关键字
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/17 21:59
     */
    private R queryRelatedCommunity(String keyWord) {
        try {
            SearchResponse<ESCommunityIndex> response = elasticsearchClient.search(search -> search
                    .index(IndexConstant.community)
                    .query(query -> query
                            .match(match -> match
                                    .field("name")
                                    .query(keyWord)))
                    .from(0)
                    .size(30)
                    .source(source -> source
                            .filter(filter -> filter
                                    .includes("name"))), ESCommunityIndex.class);

            List<ESCommunityIndex> esCommunityIndexList = new ArrayList<>();

            List<Hit<ESCommunityIndex>> hits = response.hits().hits();
            Set<String> set = new HashSet<>(hits.size());
            hits.forEach(hit -> {
                ESCommunityIndex source = hit.source();
                String name = source.getName();
                if (!set.contains(name)) {
                    set.add(name);
                    esCommunityIndexList.add(source);
                }
            });

            int len = esCommunityIndexList.size();
            return R.ok(esCommunityIndexList.subList(0, Math.min(10, len)));
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询相关资源标签集合
     *
     * @param keyWord
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/17 21:58
     */
    private R queryRelatedResource(String keyWord) {
        try {
            SearchResponse<ESResourceIndex> response = elasticsearchClient.search(search -> search
                    .index(IndexConstant.resource)
                    .query(query -> query
                            .match(match -> match
                                    .field("title")
                                    .query(keyWord)))
                    .from(0)
                    .size(30)
                    .source(source -> source
                            .filter(filter -> filter
                                    .includes("title"))), ESResourceIndex.class);

            List<ESResourceIndex> esResourceIndexList = new ArrayList<>();
            List<Hit<ESResourceIndex>> hits = response.hits().hits();
            Set<String> set = new HashSet<>(hits.size());
            hits.forEach(hit -> {
                ESResourceIndex source = hit.source();
                String name = source.getName();
                if (!set.contains(name)) {
                    set.add(name);
                    esResourceIndexList.add(source);
                }
            });

            int len = esResourceIndexList.size();
            return R.ok(esResourceIndexList.subList(0, Math.min(10, len)));
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询社区文章相关标题集合
     *
     * @param keyWord 搜索关键字
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/17 21:54
     */
    private R queryRelatedCommunityArticle(String keyWord) {
        try {
            SearchResponse<ESCommunityArticleIndex> response = elasticsearchClient.search(search -> search
                    .index(IndexConstant.communityArticle)
                    .query(query -> query
                            .match(match -> match
                                    .field("title")
                                    .query(keyWord)))
                    .from(0)
                    .size(30)
                    .source(source -> source
                            .filter(filter -> filter
                                    .includes("title"))), ESCommunityArticleIndex.class);

            List<ESCommunityArticleIndex> esCommunityArticleIndexList = new ArrayList<>();
            List<Hit<ESCommunityArticleIndex>> hits = response.hits().hits();
            Set<String> set = new HashSet<>(hits.size());
            hits.forEach(hit -> {
                ESCommunityArticleIndex source = hit.source();
                String title = source.getTitle();
                if (!set.contains(title)) {
                    set.add(title);
                    esCommunityArticleIndexList.add(source);
                }
            });

            int len = esCommunityArticleIndexList.size();
            return R.ok(esCommunityArticleIndexList.subList(0, Math.min(10, len)));
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询课程相关标签集合
     *
     * @param keyWord 搜索关键字
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/17 21:51
     */
    private R queryRelatedCourse(String keyWord) {
        try {
            SearchResponse<ESCourseIndex> response = elasticsearchClient.search(search -> search
                    .index(IndexConstant.course)
                    .query(query -> query
                            .match(match -> match
                                    .field("title")
                                    .query(keyWord)))
                    .from(0)
                    .size(30)
                    .source(source -> source
                            .filter(filter -> filter
                                    .includes("title"))), ESCourseIndex.class);

            List<ESCourseIndex> esCourseIndexList = new ArrayList<>();
            List<Hit<ESCourseIndex>> hits = response.hits().hits();
            Set<String> set = new HashSet<>(hits.size());
            hits.forEach(hit -> {
                ESCourseIndex source = hit.source();
                String title = source.getTitle();
                if (!set.contains(title)) {
                    set.add(title);
                    esCourseIndexList.add(source);
                }
            });

            int len = esCourseIndexList.size();
            return R.ok(esCourseIndexList.subList(0, Math.min(10, len)));
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询问答相关标签集合
     *
     * @param keyWord 关键字
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/17 21:50
     */
    private R queryRelatedQuestion(String keyWord) {
        try {
            SearchResponse<ESQuestionIndex> response = elasticsearchClient.search(search -> search
                    .index(IndexConstant.question)
                    .query(query -> query
                            .match(match -> match
                                    .field("title")
                                    .query(keyWord)))
                    .from(0)
                    .size(30)
                    .source(source -> source
                            .filter(filter -> filter
                                    .includes("title"))), ESQuestionIndex.class);

            List<ESQuestionIndex> esQuestionIndexList = new ArrayList<>();
            List<Hit<ESQuestionIndex>> hits = response.hits().hits();
            Set<String> set = new HashSet<>(hits.size());
            hits.forEach(hit -> {
                ESQuestionIndex source = hit.source();
                String title = source.getTitle();
                if (!set.contains(title)) {
                    set.add(title);
                    esQuestionIndexList.add(source);
                }
            });

            int len = esQuestionIndexList.size();
            return R.ok(esQuestionIndexList.subList(0, Math.min(10, len)));
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 搜索文章相关标题集合
     *
     * @param keyWord 关键字
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/17 21:47
     */
    private R queryRelatedArticle(String keyWord) {
        try {
            SearchResponse<ESArticleIndex> response = elasticsearchClient.search(search -> search
                    .index(IndexConstant.article)
                    .query(query -> query
                            .match(match -> match
                                    .field("title")
                                    .query(keyWord)))
                    .from(0)
                    .size(30)
                    .source(source -> source
                            .filter(filter -> filter
                                    .includes("title"))), ESArticleIndex.class);

            List<ESArticleIndex> esArticleIndexList = new ArrayList<>();
            List<Hit<ESArticleIndex>> hits = response.hits().hits();
            Set<String> set = new HashSet<>(hits.size());
            hits.forEach(hit -> {
                ESArticleIndex source = hit.source();
                String title = source.getTitle();
                if (!set.contains(title)) {
                    set.add(title);
                    esArticleIndexList.add(source);
                }
            });

            int len = esArticleIndexList.size();
            return R.ok(esArticleIndexList.subList(0, Math.min(10, len)));
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

}
