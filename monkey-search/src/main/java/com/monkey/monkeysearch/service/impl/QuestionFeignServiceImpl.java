package com.monkey.monkeysearch.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.DeleteByQueryResponse;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.elasticsearch.core.scripts_painless_execute.PainlessContextSetup;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeysearch.constant.IndexConstant;
import com.monkey.monkeysearch.constant.SearchTypeEnum;
import com.monkey.monkeysearch.pojo.ESQuestionIndex;
import com.monkey.monkeysearch.pojo.ESUserIndex;
import com.monkey.monkeysearch.pojo.vo.ESUserIndexVo;
import com.monkey.monkeysearch.service.QuestionFeignService;
import io.swagger.models.auth.In;
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

            log.info("elasticsearch全部索引问答功能游览数 + 1 --> questionId = {}", questionId);
            elasticsearchClient.updateByQuery(update -> update
                    .index(IndexConstant.all)
                    .query(query -> query
                            .match(match -> match
                                    .field("type")
                                    .query(SearchTypeEnum.QUESTION.getCode())
                                    .field("associationId")
                                    .query(questionId)))
                    .script(script -> script
                            .inline(inline -> inline
                                    .source("ctx._source.viewCount += 1"))));
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

            log.info("elasticsearch全部索引问答功能回复数 + 1 --> questionId = {}", questionId);
            elasticsearchClient.updateByQuery(update -> update
                    .index(IndexConstant.all)
                    .query(query -> query
                            .match(match -> match
                                    .field("type")
                                    .query(SearchTypeEnum.QUESTION.getCode())
                                    .field("associationId")
                                    .query(questionId)))
                    .script(script -> script
                            .inline(inline -> inline
                                    .source("ctx._source.commentCount += 1"))));
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

            log.info("elasticsearch全部索引问答功能点赞数 + 1 --> questionId = {}", questionId);
            elasticsearchClient.updateByQuery(update -> update
                    .index(IndexConstant.all)
                    .query(query -> query
                            .match(match -> match
                                    .field("type")
                                    .query(SearchTypeEnum.QUESTION.getCode())
                                    .field("associationId")
                                    .query(questionId)))
                    .script(script -> script
                            .inline(inline -> inline
                                    .source("ctx._source.likeCount += 1"))));
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

            log.info("elasticsearch全部索引问答功能点赞数 - 1 --> questionId = {}", questionId);
            elasticsearchClient.updateByQuery(update -> update
                    .index(IndexConstant.all)
                    .query(query -> query
                            .match(match -> match
                                    .field("type")
                                    .query(SearchTypeEnum.QUESTION.getCode())
                                    .field("associationId")
                                    .query(questionId)))
                    .script(script -> script
                            .inline(inline -> inline
                                    .source("ctx._source.likeCount -= 1"))));
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

            log.info("elasticsearch全部索引问答功能收藏数 + 1 --> questionId = {}", questionId);
            elasticsearchClient.updateByQuery(update -> update
                    .index(IndexConstant.all)
                    .query(query -> query
                            .match(match -> match
                                    .field("type")
                                    .query(SearchTypeEnum.QUESTION.getCode())
                                    .field("associationId")
                                    .query(questionId)))
                    .script(script -> script
                            .inline(inline -> inline
                                    .source("ctx._source.collectCount += 1"))));
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

            log.info("elasticsearch全部索引问答功能收藏数 - 1 --> questionId = {}", questionId);
            elasticsearchClient.updateByQuery(update -> update
                    .index(IndexConstant.all)
                    .query(query -> query
                            .match(match -> match
                                    .field("type")
                                    .query(SearchTypeEnum.QUESTION.getCode())
                                    .field("associationId")
                                    .query(questionId)))
                    .script(script -> script
                            .inline(inline -> inline
                                    .source("ctx._source.collectCount -= 1"))));
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

    /**
     * 删除问答
     *
     * @param questionId 问答id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/29 9:19
     */
    @Override
    public R deleteQuestion(String questionId) {
        try {
            log.info("删除elasticsearch问答问答 ==> questionId = {}", questionId);
            // 得到问答收藏游览点赞数
            GetResponse<ESQuestionIndex> esQuestionIndexGetResponse = elasticsearchClient.get(get -> get
                    .index(IndexConstant.question)
                    .id(questionId)
                    .sourceIncludes("collectCount", "likeCount", "viewCount", "userId"), ESQuestionIndex.class);
            ESQuestionIndex esQuestionIndex = esQuestionIndexGetResponse.source();
            String userId = String.valueOf(esQuestionIndex.getUserId());
            // 删除问答问答
            elasticsearchClient.delete(delete -> delete
                    .index(IndexConstant.question)
                    .id(questionId));

            // 减去用户索引表中的收藏点赞，问答数
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.user)
                    .id(userId)
                    .script(script -> script
                            .inline(inline -> inline
                                    .source("ctx._source.collectCount -=" + esQuestionIndex.getCollectCount())
                                    .source("ctx._source.viewCount -=" + esQuestionIndex.getViewCount())
                                    .source("ctx._source.likeCount -=" + esQuestionIndex.getLikeCount())
                                    .source("ctx._source.opusCount -= 1"))), ESUserIndex.class);
            // 删除全部索引中的问答数据
            elasticsearchClient.deleteByQuery(delete -> delete
                    .index(IndexConstant.user)
                    .query(query -> query
                            .match(match -> match
                                    .field("associationId")
                                    .query(questionId)
                                    .field("type")
                                    .query(SearchTypeEnum.QUESTION.getCode()))));
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
        return null;
    }
}
