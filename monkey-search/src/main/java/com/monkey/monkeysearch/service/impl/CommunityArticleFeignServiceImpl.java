package com.monkey.monkeysearch.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeysearch.constant.IndexConstant;
import com.monkey.monkeysearch.constant.SearchTypeEnum;
import com.monkey.monkeysearch.pojo.ESCommunityArticleIndex;
import com.monkey.monkeysearch.service.CommunityArticleFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: wusihao
 * @date: 2023/11/11 17:29
 * @version: 1.0
 * @description:
 */
@Service
@Slf4j
public class CommunityArticleFeignServiceImpl implements CommunityArticleFeignService {
    @Resource
    private ElasticsearchClient elasticsearchClient;

    /**
     * 社区文章游览数 + 1
     *
     * @param communityArticleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/10 10:12
     */
    @Override
    public R communityArticleViewAddOne(Long communityArticleId) {
        try {
            log.info("elasticsearch社区文章游览数 + 1, communityArticleId = {}", communityArticleId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.communityArticle)
                    .id(String.valueOf(communityArticleId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.viewCount += 1"))), ESCommunityArticleIndex.class);

            log.info("elasticsearch全部索引社区文章游览数 + 1 --> communityArticleId = {}", communityArticleId);
            elasticsearchClient.updateByQuery(update -> update
                    .index(IndexConstant.all)
                    .query(query -> query
                            .match(match -> match
                                    .field("type")
                                    .query(SearchTypeEnum.COMMUNITY_ARTICLE.getCode())
                                    .field("associationId")
                                    .query(communityArticleId)))
                    .script(script -> script
                            .inline(inline -> inline
                                    .source("ctx._source.viewCount += 1"))));
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 社区文章评论数 + 1
     *
     * @param communityArticleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/10 16:46
     */
    @Override
    public R communityArticleCommentCountAdd(Long communityArticleId) {
        try {
            log.info("elasticsearch社区文章回复数 + 1, communityArticleId = {}", communityArticleId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.communityArticle)
                    .id(String.valueOf(communityArticleId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.commentCount += 1"))), ESCommunityArticleIndex.class);

            log.info("elasticsearch全部索引社区文章评论数 + 1 --> communityArticleId = {}", communityArticleId);
            elasticsearchClient.updateByQuery(update -> update
                    .index(IndexConstant.all)
                    .query(query -> query
                            .match(match -> match
                                    .field("type")
                                    .query(SearchTypeEnum.COMMUNITY_ARTICLE.getCode())
                                    .field("associationId")
                                    .query(communityArticleId)))
                    .script(script -> script
                            .inline(inline -> inline
                                    .source("ctx._source.commentCount += 1"))));
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 社区文章评论数减去对应值
     *
     * @param communityArticleId 社区文章id
     * @param sum 减去的对应值
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/11 14:49
     */
    @Override
    public R communityArticleCommentSub(Long communityArticleId, Long sum) {
        try {
            log.info("elasticsearch社区文章点赞人数 - {}, communityArticleId = {}", sum, communityArticleId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.communityArticle)
                    .id(String.valueOf(communityArticleId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.commentCount -=" + sum))), ESCommunityArticleIndex.class);

            log.info("elasticsearch全部索引社区文章评论数 - {} --> communityArticleId = {}", sum, communityArticleId);
            elasticsearchClient.updateByQuery(update -> update
                    .index(IndexConstant.all)
                    .query(query -> query
                            .match(match -> match
                                    .field("type")
                                    .query(SearchTypeEnum.COMMUNITY_ARTICLE.getCode())
                                    .field("associationId")
                                    .query(communityArticleId)))
                    .script(script -> script
                            .inline(inline -> inline
                                    .source("ctx._source.commentCount -= " + sum))));
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 社区文章收藏数 + 1
     *
     * @param communityArticleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/10 16:51
     */
    @Override
    public R communityArticleCollectCountAddOne(Long communityArticleId) {
        try {
            log.info("elasticsearch社区文章收藏数 + 1, communityArticleId = {}", communityArticleId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.communityArticle)
                    .id(String.valueOf(communityArticleId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.collectCount += 1"))), ESCommunityArticleIndex.class);

            log.info("elasticsearch全部索引社区文章收藏数 + 1 --> communityArticleId = {}", communityArticleId);
            elasticsearchClient.updateByQuery(update -> update
                    .index(IndexConstant.all)
                    .query(query -> query
                            .match(match -> match
                                    .field("type")
                                    .query(SearchTypeEnum.COMMUNITY_ARTICLE.getCode())
                                    .field("associationId")
                                    .query(communityArticleId)))
                    .script(script -> script
                            .inline(inline -> inline
                                    .source("ctx._source.collectCount += 1"))));
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 社区文章收藏数 - 1
     *
     * @param communityArticleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/10 16:51
     */
    @Override
    public R communityArticleCollectCountSubOne(Long communityArticleId) {
        try {
            log.info("elasticsearch社区文章收藏数 - 1, communityArticleId = {}", communityArticleId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.communityArticle)
                    .id(String.valueOf(communityArticleId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.collectCount -= 1"))), ESCommunityArticleIndex.class);

            log.info("elasticsearch全部索引社区文章收藏数 - 1 --> communityArticleId = {}", communityArticleId);
            elasticsearchClient.updateByQuery(update -> update
                    .index(IndexConstant.all)
                    .query(query -> query
                            .match(match -> match
                                    .field("type")
                                    .query(SearchTypeEnum.COMMUNITY_ARTICLE.getCode())
                                    .field("associationId")
                                    .query(communityArticleId)))
                    .script(script -> script
                            .inline(inline -> inline
                                    .source("ctx._source.collectCount -= 1"))));
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 社区文章点赞人数 + 1
     *
     * @param communityArticleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/10 16:47
     */
    @Override
    public R communityArticleLikeCountAddOne(Long communityArticleId) {
        try {
            log.info("elasticsearch社区文章点赞人数 + 1, communityArticleId = {}", communityArticleId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.communityArticle)
                    .id(String.valueOf(communityArticleId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.likeCount += 1"))), ESCommunityArticleIndex.class);

            log.info("elasticsearch全部索引社区文章点赞数 + 1 --> communityArticleId = {}", communityArticleId);
            elasticsearchClient.updateByQuery(update -> update
                    .index(IndexConstant.all)
                    .query(query -> query
                            .match(match -> match
                                    .field("type")
                                    .query(SearchTypeEnum.COMMUNITY_ARTICLE.getCode())
                                    .field("associationId")
                                    .query(communityArticleId)))
                    .script(script -> script
                            .inline(inline -> inline
                                    .source("ctx._source.likeCount += 1"))));
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 社区文章点赞人数 - 1
     *
     * @param communityArticleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/10 16:48
     */
    @Override
    public R communityArticleLikeCountSubOne(Long communityArticleId) {
        try {
            log.info("elasticsearch社区文章点赞人数 - 1, communityArticleId = {}", communityArticleId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.communityArticle)
                    .id(String.valueOf(communityArticleId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.likeCount -= 1"))), ESCommunityArticleIndex.class);

            log.info("elasticsearch全部索引社区文章点赞数 - 1 --> communityArticleId = {}", communityArticleId);
            elasticsearchClient.updateByQuery(update -> update
                    .index(IndexConstant.all)
                    .query(query -> query
                            .match(match -> match
                                    .field("type")
                                    .query(SearchTypeEnum.COMMUNITY_ARTICLE.getCode())
                                    .field("associationId")
                                    .query(communityArticleId)))
                    .script(script -> script
                            .inline(inline -> inline
                                    .source("ctx._source.likeCount -= 1"))));
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 更新社区文章评分
     *
     * @param communityArticleId 社区文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/10 16:48
     */
    @Override
    public R updateCommunityArticleScore(Long communityArticleId, Float score) {
        try {
            Map<String, Float> map = new HashMap<>();
            map.put("score", score);
            log.info("elasticsearch社区文章点赞人数 - 1, communityArticleId = {}", communityArticleId);
            elasticsearchClient.update(update -> update
                            .index(IndexConstant.communityArticle)
                            .id(String.valueOf(communityArticleId))
                            .doc(map)
                    , ESCommunityArticleIndex.class);
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }
}
