package com.monkey.monkeysearch.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeysearch.constant.IndexConstant;
import com.monkey.monkeysearch.pojo.ESResourceIndex;
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
     * 删除资源索引
     *
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/13 8:36
     */
    @Override
    public R deleteResourceIndex(Long resourceId) {
        try {
            elasticsearchClient.delete(delete -> delete
                    .index(IndexConstant.resource)
                    .id(String.valueOf(resourceId)));

            return R.ok();
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
