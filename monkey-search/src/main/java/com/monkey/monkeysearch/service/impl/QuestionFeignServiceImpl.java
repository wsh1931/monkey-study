package com.monkey.monkeysearch.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.elasticsearch.core.scripts_painless_execute.PainlessContextSetup;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeysearch.constant.IndexConstant;
import com.monkey.monkeysearch.pojo.ESQuestionIndex;
import com.monkey.monkeysearch.service.QuestionFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import co.elastic.clients.elasticsearch._types.Script;

import javax.annotation.Resource;
import java.util.Collections;

/**
 * @author: wusihao
 * @date: 2023/11/10 10:08
 * @version: 1.0
 * @description:
 */
@Slf4j
@Service
public class QuestionFeignServiceImpl implements QuestionFeignService {

    @Resource
    private ElasticsearchClient elasticsearchClient;

    /**
     * 问答游览数 + 1
     *
     * @param questionId 问答id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/10 10:12
     */
    @Override
    public R questionViewAddOne(Long questionId) {
        try {
            log.info("elasticsearch问答游览数 + 1, questionId = {}", questionId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.question)
                    .id(String.valueOf(questionId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.viewCount += 1"))), ESQuestionIndex.class);
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 问答回复数 + 1
     *
     * @param questionId 问答id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/10 16:46
     */
    @Override
    public R questionReplyCountAdd(Long questionId) {
        try {
            log.info("elasticsearch问答回复数 + 1, questionId = {}", questionId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.question)
                    .id(String.valueOf(questionId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.replyCount += 1"))), ESQuestionIndex.class);
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 问答点赞数 + 1
     *
     * @param questionId 问答id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/10 16:47
     */
    @Override
    public R questionLikeCountAddOne(Long questionId) {
        try {
            log.info("elasticsearch问答点赞数 + 1, questionId = {}", questionId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.question)
                    .id(String.valueOf(questionId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.likeCount += 1"))), ESQuestionIndex.class);
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 问答点赞数 - 1
     *
     * @param questionId 问答id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/10 16:48
     */
    @Override
    public R questionLikeCountSubOne(Long questionId) {
        try {
            log.info("elasticsearch问答点赞数 - 1, questionId = {}", questionId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.question)
                    .id(String.valueOf(questionId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.likeCount -= 1"))), ESQuestionIndex.class);
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 问答收藏数 + 1
     *
     * @param questionId 问答id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/10 16:51
     */
    @Override
    public R questionCollectCountAddOne(Long questionId) {
        try {
            log.info("elasticsearch问答收藏数 + 1, questionId = {}", questionId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.question)
                    .id(String.valueOf(questionId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.collectCount += 1"))), ESQuestionIndex.class);
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 问答收藏数 - 1
     *
     * @param questionId 问答id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/10 16:51
     */
    @Override
    public R questionCollectCountSubOne(Long questionId) {
        try {
            log.info("elasticsearch问答收藏数 - 1, questionId = {}", questionId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.question)
                    .id(String.valueOf(questionId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.collectCount -= 1"))), ESQuestionIndex.class);
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 发布问答
     *
     * @param esQuestionIndex 问答索引类
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/12 15:48
     */
    @Override
    public R publishQuestion(ESQuestionIndex esQuestionIndex) {
        try {
            elasticsearchClient.create(create -> create
                    .id(String.valueOf(esQuestionIndex.getId()))
                    .index(IndexConstant.question)
                    .document(esQuestionIndex));
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }
}
