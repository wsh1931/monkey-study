package com.monkey.monkeysearch.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.alibaba.fastjson.TypeReference;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeysearch.constant.IndexConstant;
import com.monkey.monkeysearch.constant.SearchExceptionEnum;
import com.monkey.monkeysearch.constant.SearchTypeEnum;
import com.monkey.monkeysearch.feign.*;
import com.monkey.monkeysearch.pojo.ESResourceIndex;
import com.monkey.monkeysearch.pojo.ESUserIndex;
import com.monkey.monkeysearch.pojo.vo.ESUserIndexVo;
import com.monkey.monkeysearch.service.ESUserService;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import com.monkey.monkeyUtils.mapper.UserMapper;
import com.monkey.monkeyUtils.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: wusihao
 * @date: 2023/11/14 9:34
 * @version: 1.0
 * @description:
 */
@Service
@Slf4j
public class ESUserServiceImpl implements ESUserService {
    @Resource
    private SearchToUserFeign searchToUserFeign;
    @Resource
    private ElasticsearchClient elasticsearchClient;

    @Resource
    private SearchToArticleFeign searchToArticleFeign;
    @Resource
    private SearchToQuestionFeign searchToQuestionFeign;
    @Resource
    private SearchToCommunityFeign searchToCommunityFeign;
    @Resource
    private SearchToCourseFeign searchToCourseFeign;
    @Resource
    private SearchToResourceFeign searchToResourceFeign;
    @Resource
    private UserMapper userMapper;

    /**
     * 将用户数据库中所有数据存入elasticsearch用户文档中
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/7 17:00
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R insertUserDocument() {
        try {
            // 查询所有用户信息
            List<ESUserIndex> esUserIndexList = queryAllUserInfo();
            // 将用户批量插入elasticsearch
            BulkRequest.Builder br = new BulkRequest.Builder();
            esUserIndexList.stream().forEach(user -> {
                br.operations(op -> op
                        .index(idx -> idx
                                .index(IndexConstant.user)
                                .id(user.getId())
                                .document(user)));
            });

            BulkResponse response = elasticsearchClient.bulk(br.build());
            if (response.errors()) {
                throw new MonkeyBlogException(SearchExceptionEnum.BULK_INSERT_ARTICLE.getCode(), SearchExceptionEnum.BULK_INSERT_ARTICLE.getMsg());
            }

            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询所有用户点赞，游览，收藏，粉丝总和
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/14 11:29
     */
    private List<ESUserIndex> queryAllUserInfo() {
            // 得到所有文章，点赞，收藏，游览数
            R articleResult = searchToArticleFeign.queryAllUserArticleInfo();
            if (articleResult.getCode() != R.SUCCESS) {
                throw new MonkeyBlogException(articleResult.getCode(), articleResult.getMsg());
            }
            List<Map<String, Object>> articleInfo = (List<Map<String, Object>>)articleResult.getData(new TypeReference<List<Map<String, Object>>>(){});

            // 得到所有问答，点赞，收藏，游览数
            R qeustionResult = searchToQuestionFeign.queryAllUserQuestionInfo();
            if (qeustionResult.getCode() != R.SUCCESS) {
                throw new MonkeyBlogException(qeustionResult.getCode(), qeustionResult.getMsg());
            }
            List<Map<String, Object>> questionInfo = (List<Map<String, Object>>)qeustionResult.getData(new TypeReference<List<Map<String, Object>>>(){});

            // 得到所有课程，点赞，收藏，游览数
            R courseResult = searchToCourseFeign.queryAllUserCourseInfo();
            if (courseResult.getCode() != R.SUCCESS) {
                throw new MonkeyBlogException(courseResult.getCode(), courseResult.getMsg());
            }
            List<Map<String, Object>> courseInfo = (List<Map<String, Object>>)courseResult.getData(new TypeReference<List<Map<String, Object>>>(){});

            // 得到所有社区文章点赞，收藏，游览数
            R communityArticleResult = searchToCommunityFeign.queryAllUserCommunityArticleInfo();
            if (communityArticleResult.getCode() != R.SUCCESS) {
                throw new MonkeyBlogException(communityArticleResult.getCode(), communityArticleResult.getMsg());
            }
            List<Map<String, Object>> communityArticleInfo = (List<Map<String, Object>>)communityArticleResult.getData(new TypeReference<List<Map<String, Object>>>(){});

            // 得到所有资源点赞，收藏，游览数
            R resourceResult = searchToResourceFeign.queryAllUserResourceInfo();
            if (resourceResult.getCode() != R.SUCCESS) {
                throw new MonkeyBlogException(resourceResult.getCode(), resourceResult.getMsg());
            }
            List<Map<String, Object>> resourceInfo = (List<Map<String, Object>>)resourceResult.getData(new TypeReference<List<Map<String, Object>>>(){});

            // 得到所有用户粉丝, 关注信息
            R userFansResult = searchToUserFeign.queryAllUserFansInfo();
            if (userFansResult.getCode() != R.SUCCESS) {
                throw new MonkeyBlogException(userFansResult.getCode(), userFansResult.getMsg());
            }
            List<Map<String, Object>> userFansInfo = (List<Map<String, Object>>)userFansResult.getData(new TypeReference<List<Map<String, Object>>>(){});

            // 存取每个用户最终的点赞，收藏游览数
            Map<String, ESUserIndex> userInfo = new HashMap<>();

            // 全部信息
            List<Map<String, Object>> totalInfo = new ArrayList<>();
            totalInfo.addAll(articleInfo);
            totalInfo.addAll(questionInfo);
            totalInfo.addAll(courseInfo);
            totalInfo.addAll(communityArticleInfo);
            totalInfo.addAll(resourceInfo);
            totalInfo.addAll(userFansInfo);
            totalInfo.parallelStream().forEach(f -> {
                String userId = f.get("user_id").toString();
                // 先找出用户信息
                ESUserIndex esUserIndex = null;
                if (!userInfo.containsKey(userId)) {
                    User user = userMapper.selectById(userId);
                    esUserIndex = new ESUserIndex();
                    esUserIndex.setId(userId);
                    esUserIndex.setUsername(user.getUsername());
                    esUserIndex.setUserBrief(user.getBrief());
                    esUserIndex.setUserHeadImg(user.getPhoto());
                    esUserIndex.setOpusCount(0);
                    esUserIndex.setCollectCount(0);
                    esUserIndex.setViewCount(0L);
                    esUserIndex.setLikeCount(0);
                    esUserIndex.setFansCount(0);
                    esUserIndex.setConnectCount(0);
                    esUserIndex.setCreateTime(user.getRegisterTime());
                } else {
                    esUserIndex = userInfo.get(userId);
                }
//                // 添加用户记录
                for (Map.Entry<String, Object> map : f.entrySet()) {
                    String key = map.getKey();
                    String value = map.getValue().toString();
                    if ("opusCount".equals(key)) {
                        esUserIndex.setOpusCount(esUserIndex.getOpusCount() + Integer.parseInt(value));
                    } else if ("collectCount".equals(key)) {
                        esUserIndex.setCollectCount(esUserIndex.getCollectCount() + Integer.parseInt(value));
                    } else if ("likeCount".equals(key)) {
                        esUserIndex.setLikeCount(esUserIndex.getLikeCount() + Integer.parseInt(value));
                    } else if ("fansCount".equals(key)) {
                        esUserIndex.setFansCount(esUserIndex.getFansCount() + Integer.parseInt(value));
                    } else if ("viewCount".equals(key)) {
                        esUserIndex.setViewCount(esUserIndex.getViewCount() + Long.parseLong(value));
                    } else if ("connectCount".equals(key)) {
                        esUserIndex.setConnectCount(esUserIndex.getConnectCount() + Integer.parseInt(value));
                    }
                }

                userInfo.put(userId, esUserIndex);
            });

            // 得到用户集合
        List<ESUserIndex> collect = userInfo.values().stream().collect(Collectors.toList());
        return collect;
    }

    /**
     * 查询综合用户列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 9:57
     */
    @Override
    public R queryComprehensiveUser(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("username",new HighlightField.Builder().build());
            highlightFieldMap.put("userBrief",new HighlightField.Builder().build());
            SearchResponse<ESUserIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.user)
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
                                    .field("viewCount")
                                    .order(SortOrder.Desc)
                                    .field("fansCount")
                                    .order(SortOrder.Desc)
                                    .field("createTime")
                                    .order(SortOrder.Desc))), ESUserIndex.class);

            // 设置搜索结果高亮
            List<ESUserIndex> esUserIndexList = setHighlight(response);
            // 判断当前登录用户是否是该搜索用户粉丝
             List<ESUserIndexVo> esUserIndexVoList = judgeIsFans(esUserIndexList);
            return R.ok(esUserIndexVoList);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 判断当前登录用户是否是该搜索用户粉丝
     *
     * @param esUserIndexList 实体类
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/15 9:39
     */
    private List<ESUserIndexVo> judgeIsFans(List<ESUserIndex> esUserIndexList) {
        List<ESUserIndexVo> esUserIndexVoList = new ArrayList<>();
        esUserIndexList.forEach(esUserIndex -> {
            ESUserIndexVo esUserIndexVo = new ESUserIndexVo();
            BeanUtils.copyProperties(esUserIndex, esUserIndexVo);
            String authorId = esUserIndex.getId();
            String userId = JwtUtil.getUserId();
            if (userId == null || "".equals(userId)) {
                esUserIndexVo.setIsFans(CommonEnum.NOT_FANS.getCode());
            } else {
                // 判断当前登录用户与作者是否是粉丝
                R result = searchToUserFeign.judgeIsFans(userId, authorId);
                if (result.getCode() != R.SUCCESS) {
                    throw new MonkeyBlogException(result.getCode(), result.getMsg());
                }

                Integer isFans = (Integer)result.getData(new TypeReference<Integer>(){});
                if (isFans > 0) {
                    esUserIndexVo.setIsFans(CommonEnum.IS_FANS.getCode());
                } else {
                    esUserIndexVo.setIsFans(CommonEnum.NOT_FANS.getCode());
                }
            }

            esUserIndexVoList.add(esUserIndexVo);
        });

        return esUserIndexVoList;
    }


    /**
     * 查询收藏数最多用户列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:51
     */
    @Override
    public R queryCollectUser(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("username",new HighlightField.Builder().build());
            highlightFieldMap.put("userBrief",new HighlightField.Builder().build());
            SearchResponse<ESUserIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.user)
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
                                    .order(SortOrder.Desc))), ESUserIndex.class);

            // 设置搜索结果高亮
            List<ESUserIndex> esUserIndexList = setHighlight(response);

            // 判断当前登录用户是否是该搜索用户粉丝
            List<ESUserIndexVo> esUserIndexVoList = judgeIsFans(esUserIndexList);
            return R.ok(esUserIndexVoList);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询点赞数最多用户列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:52
     */
    @Override
    public R queryLikeUser(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("username",new HighlightField.Builder().build());
            highlightFieldMap.put("userBrief",new HighlightField.Builder().build());
            SearchResponse<ESUserIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.user)
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
                                    .order(SortOrder.Desc))), ESUserIndex.class);

            // 设置搜索结果高亮
            List<ESUserIndex> esUserIndexList = setHighlight(response);

            // 判断当前登录用户是否是该搜索用户粉丝
            List<ESUserIndexVo> esUserIndexVoList = judgeIsFans(esUserIndexList);
            return R.ok(esUserIndexVoList);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询游览数最多用户列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:52
     */
    @Override
    public R queryViewUser(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("username",new HighlightField.Builder().build());
            highlightFieldMap.put("userBrief",new HighlightField.Builder().build());
            SearchResponse<ESUserIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.user)
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
                                    .order(SortOrder.Desc))), ESUserIndex.class);

            // 设置搜索结果高亮
            List<ESUserIndex> esUserIndexList = setHighlight(response);

            // 判断当前登录用户是否是该搜索用户粉丝
            List<ESUserIndexVo> esUserIndexVoList = judgeIsFans(esUserIndexList);
            return R.ok(esUserIndexVoList);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询最热用户列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:53
     */
    @Override
    public R queryHireUser(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("username",new HighlightField.Builder().build());
            highlightFieldMap.put("userBrief",new HighlightField.Builder().build());
            SearchResponse<ESUserIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.user)
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
                                    .field("collectCount")
                                    .order(SortOrder.Desc)
                                    .field("fansCount")
                                    .order(SortOrder.Desc)
                                    .field("createTime")
                                    .order(SortOrder.Desc))), ESUserIndex.class);

            // 设置搜索结果高亮
            List<ESUserIndex> esUserIndexList = setHighlight(response);

            // 判断当前登录用户是否是该搜索用户粉丝
            List<ESUserIndexVo> esUserIndexVoList = judgeIsFans(esUserIndexList);
            return R.ok(esUserIndexVoList);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询最新用户列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 14:54
     */
    @Override
    public R queryLatestUser(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("username",new HighlightField.Builder().build());
            highlightFieldMap.put("userBrief",new HighlightField.Builder().build());
            SearchResponse<ESUserIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.user)
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
                                    .order(SortOrder.Desc))), ESUserIndex.class);

            // 设置搜索结果高亮
            List<ESUserIndex> esUserIndexList = setHighlight(response);

            // 判断当前登录用户是否是该搜索用户粉丝
            List<ESUserIndexVo> esUserIndexVoList = judgeIsFans(esUserIndexList);
            return R.ok(esUserIndexVoList);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询作品数最多用户列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/14 9:51
     */
    @Override
    public R queryOpusUser(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("username",new HighlightField.Builder().build());
            highlightFieldMap.put("userBrief",new HighlightField.Builder().build());
            SearchResponse<ESUserIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.user)
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
                                    .field("opusCount")
                                    .order(SortOrder.Desc))), ESUserIndex.class);

            // 设置搜索结果高亮
            List<ESUserIndex> esUserIndexList = setHighlight(response);

            // 判断当前登录用户是否是该搜索用户粉丝
            List<ESUserIndexVo> esUserIndexVoList = judgeIsFans(esUserIndexList);
            return R.ok(esUserIndexVoList);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询粉丝数最多用户列表
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param keyword 关键词搜索字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/14 10:18
     */
    @Override
    public R queryFansUser(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Integer location = (currentPage - 1) * pageSize;
            Map<String, HighlightField> highlightFieldMap = new HashMap<>();
            highlightFieldMap.put("username",new HighlightField.Builder().build());
            highlightFieldMap.put("userBrief",new HighlightField.Builder().build());
            SearchResponse<ESUserIndex> response = elasticsearchClient.search(s -> s
                    .index(IndexConstant.user)
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
                                    .field("fansCount")
                                    .order(SortOrder.Desc))), ESUserIndex.class);

            // 设置搜索结果高亮
            List<ESUserIndex> esUserIndexList = setHighlight(response);

            // 判断当前登录用户是否是该搜索用户粉丝
            List<ESUserIndexVo> esUserIndexVoList = judgeIsFans(esUserIndexList);
            return R.ok(esUserIndexVoList);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询所有用户文档
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/26 21:04
     */
    @Override
    public R queryAllUserDocument() {
        try {
            log.info("查询所有用户文档");
            SearchResponse<ESUserIndex> response = elasticsearchClient.search(search -> search
                    .index(IndexConstant.user)
                    .query(query -> query
                            .matchAll(all -> all)), ESUserIndex.class);
            List<ESUserIndex> userIndexList = new ArrayList<>();
            List<Hit<ESUserIndex>> hits = response.hits().hits();
            for (Hit<ESUserIndex> hit : hits) {
                ESUserIndex source = hit.source();
                userIndexList.add(source);
            }

            return R.ok(userIndexList);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 删除所有用户文档
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/26 21:05
     */
    @Override
    public R deleteAllUserDocument() {
        try {
            log.info("删除所有资源文档");
            elasticsearchClient.deleteByQuery(delete -> delete
                    .index(IndexConstant.user)
                    .query(query -> query
                            .matchAll(matchAll -> matchAll)));

            log.info("删除全部索引中的用户文档");
            elasticsearchClient.deleteByQuery(delete -> delete
                    .index(IndexConstant.all)
                    .query(query -> query
                            .match(match -> match
                                    .field("type")
                                    .query(SearchTypeEnum.USER.getCode()))));
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
    private List<ESUserIndex> setHighlight(SearchResponse<ESUserIndex> response) {
        List<Hit<ESUserIndex>> hits = response.hits().hits();
        List<ESUserIndex> esUserIndexList = new ArrayList<>();
        for (Hit<ESUserIndex> hit : hits) {
            Map<String, List<String>> highlight = hit.highlight();
            ESUserIndex source = hit.source();
            // 高亮赋值
            if (source != null) {
                if (highlight.get("username") != null) {
                    source.setUsername(highlight.get("username").get(0));
                }

                if (highlight.get("userBrief") != null) {
                    source.setUserBrief(highlight.get("userBrief").get(0));
                }
            }
            esUserIndexList.add(source);
        }

        return esUserIndexList;
    }
}
