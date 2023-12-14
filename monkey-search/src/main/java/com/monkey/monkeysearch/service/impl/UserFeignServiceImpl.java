package com.monkey.monkeysearch.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetResponse;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.pojo.vo.UserVo;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import com.monkey.monkeysearch.constant.IndexConstant;
import com.monkey.monkeysearch.pojo.ESUserIndex;
import com.monkey.monkeysearch.service.UserFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/11/15 10:33
 * @version: 1.0
 * @description:
 */
@Service
@Slf4j
public class UserFeignServiceImpl implements UserFeignService {
    @Resource
    private ElasticsearchClient elasticsearchClient;

    /**
     * 用户游览数 + 1
     *
     * @param userId 用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/10 10:12
     */
    @Override
    public R userViewAddOne(Long userId) {
        try {
            log.info("elasticsearch用户游览数 + 1, userId = {}", userId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.user)
                    .id(String.valueOf(userId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.viewCount += 1"))), ESUserIndex.class);
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 用户作品数 + 1
     *
     * @param userId 用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/10 16:46
     */
    @Override
    public R userOpusCountAddOne(Long userId) {
        try {
            log.info("elasticsearch用户回复数 + 1, userId = {}", userId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.user)
                    .id(String.valueOf(userId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.opusCount += 1"))), ESUserIndex.class);
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 用户作品数 - 1
     *
     * @param userId 用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/10 16:46
     */
    @Override
    public R userOpusCountSubOne(Long userId) {
        try {
            log.info("elasticsearch用户回复数 + 1, userId = {}", userId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.user)
                    .id(String.valueOf(userId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.opusCount -= 1"))), ESUserIndex.class);
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 用户点赞数 + 1
     *
     * @param userId 用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/10 16:47
     */
    @Override
    public R userLikeCountAddOne(Long userId) {
        try {
            log.info("elasticsearch用户点赞数 + 1, userId = {}", userId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.user)
                    .id(String.valueOf(userId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.likeCount += 1"))), ESUserIndex.class);
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 用户点赞数 - 1
     *
     * @param userId 用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/10 16:48
     */
    @Override
    public R userLikeCountSubOne(Long userId) {
        try {
            log.info("elasticsearch用户点赞数 - 1, userId = {}", userId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.user)
                    .id(String.valueOf(userId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.likeCount -= 1"))), ESUserIndex.class);
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 用户收藏数 + 1
     *
     * @param userId 用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/10 16:51
     */
    @Override
    public R userCollectCountAddOne(Long userId) {
        try {
            log.info("elasticsearch用户收藏数 + 1, userId = {}", userId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.user)
                    .id(String.valueOf(userId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.collectCount += 1"))), ESUserIndex.class);
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 用户收藏数 - 1
     *
     * @param userId 用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/10 16:51
     */
    @Override
    public R userCollectCountSubOne(Long userId) {
        try {
            log.info("elasticsearch用户收藏数 - 1, userId = {}", userId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.user)
                    .id(String.valueOf(userId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.collectCount -= 1"))), ESUserIndex.class);
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 用户粉丝数 + 1
     *
     * @param userId 用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/17 8:55
     */
    @Override
    public R userFansCountAddOne(Long userId) {
        try {
            log.info("elasticsearch用户粉丝数 + 1, userId = {}", userId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.user)
                    .id(String.valueOf(userId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.fansCount += 1"))), ESUserIndex.class);
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 用户粉丝数 - 1
     *
     * @param userId 用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/17 8:56
     */
    @Override
    public R userFansCountSubOne(Long userId) {
        try {
            log.info("elasticsearch用户粉丝数 - 1, userId = {}", userId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.user)
                    .id(String.valueOf(userId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.fansCount -= 1"))), ESUserIndex.class);
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 得到作者信息
     *
     * @param authorId 作者id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/30 7:37
     */
    @Override
    public R getAuthorInfoById(Long authorId) {
        try {
            GetResponse<ESUserIndex> response = elasticsearchClient.get(get -> get
                    .index(IndexConstant.user)
                    .id(String.valueOf(authorId)), ESUserIndex.class);
            ESUserIndex source = response.source();

            UserVo userVo = new UserVo();
            userVo.setFans(source.getFansCount());
            userVo.setConcern(source.getConnectCount());
            userVo.setVisit(source.getViewCount());
            userVo.setLikeSum(source.getLikeCount());
            userVo.setConcern(source.getCollectCount());
            userVo.setId(Long.parseLong(source.getId()));
            userVo.setBrief(source.getUserBrief());
            userVo.setArticleSum(source.getOpusCount());
            userVo.setPhoto(source.getUserHeadImg());
            userVo.setUsername(source.getUsername());
            userVo.setUserCollect(source.getCollectCount());
            return R.ok(userVo);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }

    }

    /**
     * 查询用户原文数，游览数，点赞数，收藏数
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/14 9:33
     */
    @Override
    public R queryUserAchievement() {
        try {
            String userId = JwtUtil.getUserId();
            GetResponse<ESUserIndex> response = elasticsearchClient.get(get -> get
                    .index(IndexConstant.user)
                    .id(userId)
                    .sourceIncludes("opusCount",
                            "viewCount",
                                    "likeCount",
                            "collectCount",
                            "id"), ESUserIndex.class);
            ESUserIndex source = response.source();
            return R.ok(source);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }
}
