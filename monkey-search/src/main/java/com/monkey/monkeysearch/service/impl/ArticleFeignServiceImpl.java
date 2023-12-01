package com.monkey.monkeysearch.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetResponse;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeysearch.constant.IndexConstant;
import com.monkey.monkeysearch.constant.SearchTypeEnum;
import com.monkey.monkeysearch.pojo.ESAllIndex;
import com.monkey.monkeysearch.pojo.ESArticleIndex;
import com.monkey.monkeysearch.pojo.ESUserIndex;
import com.monkey.monkeysearch.service.ArticleFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/11/11 9:03
 * @version: 1.0
 * @description:
 */
@Service
@Slf4j
public class ArticleFeignServiceImpl implements ArticleFeignService {
    @Resource
    private ElasticsearchClient elasticsearchClient;

    /**
     * 文章游览数 + 1
     *
     * @param articleId 文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/10 10:12
     */
    @Override
    public R articleViewAddOne(Long articleId) {
        try {
            log.info("elasticsearch文章游览数 + 1, articleId = {}", articleId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.article)
                    .id(String.valueOf(articleId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.viewCount += 1"))), ESArticleIndex.class);


            log.info("elasticsearch全部索引文章功能游览数 + 1 --> articleId = {}", articleId);
            elasticsearchClient.updateByQuery(update -> update
                    .index(IndexConstant.all)
                    .query(query -> query
                            .match(match -> match
                                    .field("type")
                                    .query(SearchTypeEnum.ARTICLE.getCode())
                                    .field("associationId")
                                    .query(articleId)))
                    .script(script -> script
                            .inline(inline -> inline
                                    .source("ctx._source.viewCount += 1"))));
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 文章评论数 + 1
     *
     * @param articleId 文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/10 16:46
     */
    @Override
    public R articleCommentCountAdd(Long articleId) {
        try {
            log.info("elasticsearch文章回复数 + 1, articleId = {}", articleId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.article)
                    .id(String.valueOf(articleId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.commentCount += 1"))), ESArticleIndex.class);

            log.info("elasticsearch全部索引文章功能评论数 + 1 --> articleId = {}", articleId);
            elasticsearchClient.updateByQuery(update -> update
                    .index(IndexConstant.all)
                    .query(query -> query
                            .match(match -> match
                                    .field("type")
                                    .query(SearchTypeEnum.ARTICLE.getCode())
                                    .field("associationId")
                                    .query(articleId)))
                    .script(script -> script
                            .inline(inline -> inline
                                    .source("ctx._source.commentCount += 1"))));
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 文章点赞数 + 1
     *
     * @param articleId 文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/10 16:47
     */
    @Override
    public R articleLikeCountAddOne(Long articleId) {
        try {
            log.info("elasticsearch文章点赞数 + 1, articleId = {}", articleId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.article)
                    .id(String.valueOf(articleId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.likeCount += 1"))), ESArticleIndex.class);

            log.info("elasticsearch全部索引文章功能点赞数 + 1 --> articleId = {}", articleId);
            elasticsearchClient.updateByQuery(update -> update
                    .index(IndexConstant.all)
                    .query(query -> query
                            .match(match -> match
                                    .field("type")
                                    .query(SearchTypeEnum.ARTICLE.getCode())
                                    .field("associationId")
                                    .query(articleId)))
                    .script(script -> script
                            .inline(inline -> inline
                                    .source("ctx._source.likeCount += 1"))));
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 文章点赞数 - 1
     *
     * @param articleId 文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/10 16:48
     */
    @Override
    public R articleLikeCountSubOne(Long articleId) {
        try {
            log.info("elasticsearch文章点赞数 - 1, articleId = {}", articleId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.article)
                    .id(String.valueOf(articleId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.likeCount -= 1"))), ESArticleIndex.class);

            log.info("elasticsearch全部索引文章功能点赞数 - 1 --> articleId = {}", articleId);
            elasticsearchClient.updateByQuery(update -> update
                    .index(IndexConstant.all)
                    .query(query -> query
                            .match(match -> match
                                    .field("type")
                                    .query(SearchTypeEnum.ARTICLE.getCode())
                                    .field("associationId")
                                    .query(articleId)))
                    .script(script -> script
                            .inline(inline -> inline
                                    .source("ctx._source.likeCount -= 1"))));
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 文章收藏数 + 1
     *
     * @param articleId 文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/10 16:51
     */
    @Override
    public R articleCollectCountAddOne(Long articleId) {
        try {
            log.info("elasticsearch文章收藏数 + 1, articleId = {}", articleId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.article)
                    .id(String.valueOf(articleId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.collectCount += 1"))), ESArticleIndex.class);

            log.info("elasticsearch全部索引文章功能收藏数 + 1 --> articleId = {}", articleId);
            elasticsearchClient.updateByQuery(update -> update
                    .index(IndexConstant.all)
                    .query(query -> query
                            .match(match -> match
                                    .field("type")
                                    .query(SearchTypeEnum.ARTICLE.getCode())
                                    .field("associationId")
                                    .query(articleId)))
                    .script(script -> script
                            .inline(inline -> inline
                                    .source("ctx._source.collectCount += 1"))));
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 文章收藏数 - 1
     *
     * @param articleId 文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/10 16:51
     */
    @Override
    public R articleCollectCountSubOne(Long articleId) {
        try {
            log.info("elasticsearch文章收藏数 - 1, articleId = {}", articleId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.article)
                    .id(String.valueOf(articleId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.collectCount -= 1"))), ESArticleIndex.class);

            log.info("elasticsearch全部索引文章功能收藏数 - 1 --> articleId = {}", articleId);
            elasticsearchClient.updateByQuery(update -> update
                    .index(IndexConstant.all)
                    .query(query -> query
                            .match(match -> match
                                    .field("type")
                                    .query(SearchTypeEnum.ARTICLE.getCode())
                                    .field("associationId")
                                    .query(articleId)))
                    .script(script -> script
                            .inline(inline -> inline
                                    .source("ctx._source.collectCount -= 1"))));
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 更新文章实体类
     *
     * @param esArticleIndex ESArticle实体类
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/30 11:21
     */
    @Override
    public R updateArticle(ESArticleIndex esArticleIndex) {
        try {
            log.info("更新elasticsearch文章实体类 ==> {}", esArticleIndex);
            elasticsearchClient.update(update -> update
                            .index(IndexConstant.article)
                            .id(esArticleIndex.getId())
                            .doc(esArticleIndex)
            , ESArticleIndex.class);

            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 删除elasticsearch文章
     *
     * @param articleId 文章id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/30 22:17
     */
    @Override
    public R deleteArticle(String articleId) {
        try {
            log.info("删除elasticsearch文章 articleId ==> {}", articleId);
            // 得到文章点赞游览收藏数
            GetResponse<ESArticleIndex> esArticleIndexGetResponse = elasticsearchClient.get(get -> get
                    .index(IndexConstant.article)
                    .id(articleId)
                    .sourceIncludes("viewCount", "likeCount", "collectCount", "userId"), ESArticleIndex.class);
            ESArticleIndex esArticleIndex = esArticleIndexGetResponse.source();

            // 删除文章
            elasticsearchClient.delete(delete -> delete
                    .id(articleId)
                    .index(IndexConstant.article));

            // 删除全部索引中的用户文章
            elasticsearchClient.deleteByQuery(delete -> delete
                    .index(IndexConstant.all)
                    .query(query -> query
                            .match(match -> match
                                    .field("associationId")
                                    .query(articleId)
                                    .field("type")
                                    .query(SearchTypeEnum.ARTICLE.getCode()))));


            Long userId = esArticleIndex.getUserId();
            // 减去用户所对应的点赞游览收藏数
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.user)
                    .id(String.valueOf(userId))
                            .script(script -> script
                                    .inline(inline -> inline
                                            .source("ctx._source.opusCount -= 1")
                                            .source("ctx._source.viewCount -= " + esArticleIndex.getViewCount())
                                            .source("ctx._source.likeCount -= " + esArticleIndex.getLikeCount())
                                            .source("ctx._source.collectCount -= " + esArticleIndex.getCollectCount())))
                    , ESUserIndex.class);

            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }
}
