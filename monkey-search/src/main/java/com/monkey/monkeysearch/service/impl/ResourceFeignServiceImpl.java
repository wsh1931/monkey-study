package com.monkey.monkeysearch.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetResponse;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeysearch.constant.IndexConstant;
import com.monkey.monkeysearch.constant.SearchTypeEnum;
import com.monkey.monkeysearch.pojo.ESResourceIndex;
import com.monkey.monkeysearch.pojo.ESUserIndex;
import com.monkey.monkeysearch.service.ResourceFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: wusihao
 * @date: 2023/11/13 8:13
 * @version: 1.0
 * @description:
 */
@Slf4j
@Service
public class ResourceFeignServiceImpl implements ResourceFeignService {
    @Resource
    private ElasticsearchClient elasticsearchClient;

    /**
     * 资源游览数 + 1
     *
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/10 10:12
     */
    @Override
    public R resourceViewAddOne(Long resourceId) {
        try {
            log.info("elasticsearch资源游览数 + 1, resourceId = {}", resourceId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.resource)
                    .id(String.valueOf(resourceId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.viewCount += 1"))), ESResourceIndex.class);

            log.info("elasticsearch全部索引资源游览数 + 1 --> resourceId = {}", resourceId);
            elasticsearchClient.updateByQuery(update -> update
                    .index(IndexConstant.all)
                    .query(query -> query
                            .match(match -> match
                                    .field("type")
                                    .query(SearchTypeEnum.RESOURCE.getCode())
                                    .field("associationId")
                                    .query(resourceId)))
                    .script(script -> script
                            .inline(inline -> inline
                                    .source("ctx._source.viewCount += 1"))));
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 资源评论数 + 1
     *
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/10 16:46
     */
    @Override
    public R resourceCommentCountAdd(Long resourceId) {
        try {
            log.info("elasticsearch资源评论数 + 1, resourceId = {}", resourceId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.resource)
                    .id(String.valueOf(resourceId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.commentCount += 1"))), ESResourceIndex.class);

            log.info("elasticsearch全部索引资源评论数 + 1 --> resourceId = {}", resourceId);
            elasticsearchClient.updateByQuery(update -> update
                    .index(IndexConstant.all)
                    .query(query -> query
                            .match(match -> match
                                    .field("type")
                                    .query(SearchTypeEnum.RESOURCE.getCode())
                                    .field("associationId")
                                    .query(resourceId)))
                    .script(script -> script
                            .inline(inline -> inline
                                    .source("ctx._source.commentCount += 1"))));
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 资源评论数减去对应值
     *
     * @param resourceId 资源id
     * @param sum 减去的对应值
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/11 14:49
     */
    @Override
    public R resourceCommentSub(Long resourceId, Long sum) {
        try {
            log.info("elasticsearch资源评论人数 - {}, resourceId = {}", sum, resourceId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.resource)
                    .id(String.valueOf(resourceId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.commentCount -=" + sum))), ESResourceIndex.class);

            log.info("elasticsearch全部索引资源评论数 - {} --> resourceId = {}", sum, resourceId);
            elasticsearchClient.updateByQuery(update -> update
                    .index(IndexConstant.all)
                    .query(query -> query
                            .match(match -> match
                                    .field("type")
                                    .query(SearchTypeEnum.RESOURCE.getCode())
                                    .field("associationId")
                                    .query(resourceId)))
                    .script(script -> script
                            .inline(inline -> inline
                                    .source("ctx._source.commentCount -=" + sum))));
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 插入资源索引
     *
     * @param esResourceIndex 资源索引
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/13 8:34
     */
    @Override
    public R insertResourceIndex(ESResourceIndex esResourceIndex) {
        try {
            elasticsearchClient.create(create -> create
                    .index(IndexConstant.resource)
                    .id(esResourceIndex.getId())
                    .document(esResourceIndex));

            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 删除资源文档
     *
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/13 8:36
     */
    @Override
    public R deleteResourceIndex(Long resourceId) {
        try {
            log.info("删除elasticsearch资源文档 resourceId == > {}", resourceId);
            // 查出资源信息
            GetResponse<ESResourceIndex> resourceIndexGetResponse = elasticsearchClient.get(get -> get
                    .index(IndexConstant.resource)
                    .id(String.valueOf(resourceId))
                    .sourceIncludes("viewCount", "likeCount", "collectCount", "userId"), ESResourceIndex.class);
            ESResourceIndex resourceIndex = resourceIndexGetResponse.source();
            elasticsearchClient.delete(delete -> delete
                    .index(IndexConstant.resource)
                    .id(String.valueOf(resourceId)));

            // 删除全部索引表中的资源信息
            deleteAllResource(resourceId);
            // 减去用户游览，点赞，收藏数
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.user)
                    .id(String.valueOf(resourceIndex.getUserId()))
                    .script(script -> script
                            .inline(inline -> inline
                                    .source("ctx._source.likeCount -= " + resourceIndex.getLikeCount())
                                    .source("ctx._source.viewCount -=" + resourceIndex.getViewCount())
                                    .source("ctx._source.commentCount -=" + resourceIndex.getCommentCount())
                                    .source("ctx._source.collectCount -= " + resourceIndex.getCollectCount())
                                    .source("ctx._source.opusCount -= 1")
                            )), ESUserIndex.class);

            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 删除全部索引表中的资源信息
     *
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/26 21:52
     */
    private void deleteAllResource(Long resourceId) {
        try {
            log.info("删除全部索引表中的资源信息 resourceId == > {}", resourceId);
            elasticsearchClient.deleteByQuery(delete -> delete
                    .index(IndexConstant.all)
                    .query(query -> query
                            .match(match -> match
                                    .field("type")
                                    .query(SearchTypeEnum.RESOURCE.getCode())
                                    .field("associationId")
                                    .query(String.valueOf(resourceId)))));
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 资源点赞人数 - 1
     *
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/13 8:50
     */
    @Override
    public R resourceLikeCountSubOne(Long resourceId) {
        try {
            log.info("elasticsearch资源点赞数 - 1, resourceId = {}", resourceId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.resource)
                    .id(String.valueOf(resourceId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.likeCount -= 1"))), ESResourceIndex.class);

            log.info("elasticsearch全部索引资源点赞数 - 1 --> resourceId = {}", resourceId);
            elasticsearchClient.updateByQuery(update -> update
                    .index(IndexConstant.all)
                    .query(query -> query
                            .match(match -> match
                                    .field("type")
                                    .query(SearchTypeEnum.RESOURCE.getCode())
                                    .field("associationId")
                                    .query(resourceId)))
                    .script(script -> script
                            .inline(inline -> inline
                                    .source("ctx._source.likeCount -= 1"))));
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 资源点赞人数 + 1
     *
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/13 8:50
     */
    @Override
    public R resourceLikeCountAddOne(Long resourceId) {
        try {
            log.info("elasticsearch资源点赞数 + 1, resourceId = {}", resourceId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.resource)
                    .id(String.valueOf(resourceId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.likeCount += 1"))), ESResourceIndex.class);

            log.info("elasticsearch全部索引资源点赞数 + 1 --> resourceId = {}", resourceId);
            elasticsearchClient.updateByQuery(update -> update
                    .index(IndexConstant.all)
                    .query(query -> query
                            .match(match -> match
                                    .field("type")
                                    .query(SearchTypeEnum.RESOURCE.getCode())
                                    .field("associationId")
                                    .query(resourceId)))
                    .script(script -> script
                            .inline(inline -> inline
                                    .source("ctx._source.likeCount += 1"))));
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 资源收藏数 + 1
     *
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/10 16:51
     */
    @Override
    public R resourceCollectCountAddOne(Long resourceId) {
        try {
            log.info("elasticsearch资源收藏数 + 1, resourceId = {}", resourceId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.resource)
                    .id(String.valueOf(resourceId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.collectCount += 1"))), ESResourceIndex.class);

            log.info("elasticsearch全部索引资源收藏数 + 1 --> resourceId = {}", resourceId);
            elasticsearchClient.updateByQuery(update -> update
                    .index(IndexConstant.all)
                    .query(query -> query
                            .match(match -> match
                                    .field("type")
                                    .query(SearchTypeEnum.RESOURCE.getCode())
                                    .field("associationId")
                                    .query(resourceId)))
                    .script(script -> script
                            .inline(inline -> inline
                                    .source("ctx._source.collectCount += 1"))));
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 资源收藏数 - 1
     *
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/10 16:51
     */
    @Override
    public R resourceCollectCountSubOne(Long resourceId) {
        try {
            log.info("elasticsearch资源收藏数 - 1, resourceId = {}", resourceId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.resource)
                    .id(String.valueOf(resourceId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.collectCount -= 1"))), ESResourceIndex.class);

            log.info("elasticsearch全部索引资源收藏数 - 1 --> resourceId = {}", resourceId);
            elasticsearchClient.updateByQuery(update -> update
                    .index(IndexConstant.all)
                    .query(query -> query
                            .match(match -> match
                                    .field("type")
                                    .query(SearchTypeEnum.RESOURCE.getCode())
                                    .field("associationId")
                                    .query(resourceId)))
                    .script(script -> script
                            .inline(inline -> inline
                                    .source("ctx._source.collectCount -= 1"))));
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 资源下载人数 + 1
     *
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/10 16:47
     */
    @Override
    public R resourceDownCountAddOne(Long resourceId) {
        try {
            log.info("elasticsearch资源学习人数 + 1, resourceId = {}", resourceId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.resource)
                    .id(String.valueOf(resourceId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.downCount += 1"))), ESResourceIndex.class);
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 资源购买人数 + 1
     *
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/10 16:48
     */
    @Override
    public R resourceBuyCountAddOne(Long resourceId) {
        try {
            log.info("elasticsearch资源下载人数 - 1, resourceId = {}", resourceId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.resource)
                    .id(String.valueOf(resourceId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.buyCount += 1"))), ESResourceIndex.class);
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 资源购买人数 - 1
     *
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/10 16:48
     */
    @Override
    public R resourceBuyCountSubOne(Long resourceId) {
        try {
            log.info("elasticsearch资源下载人数 - 1, resourceId = {}", resourceId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.resource)
                    .id(String.valueOf(resourceId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.buyCount -= 1"))), ESResourceIndex.class);
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 更新资源评分
     *
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/10 16:48
     */
    @Override
    public R updateResourceScore(Long resourceId, Float score) {
        try {
            Map<String, Float> map = new HashMap<>();
            map.put("score", score);
            log.info("elasticsearch更新资源评分, resourceId = {}", resourceId);
            elasticsearchClient.update(update -> update
                            .index(IndexConstant.resource)
                            .id(String.valueOf(resourceId))
                            .doc(map)
                    , ESResourceIndex.class);
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }
}
