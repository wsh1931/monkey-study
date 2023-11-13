package com.monkey.monkeysearch.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeysearch.constant.IndexConstant;
import com.monkey.monkeysearch.pojo.ESCourseIndex;
import com.monkey.monkeysearch.service.CourseFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: wusihao
 * @date: 2023/11/11 14:32
 * @version: 1.0
 * @description:
 */
@Service
@Slf4j
public class CourseFeignServiceImpl implements CourseFeignService {
    @Resource
    private ElasticsearchClient elasticsearchClient;

    /**
     * 课程游览数 + 1
     *
     * @param courseId 课程id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/10 10:12
     */
    @Override
    public R courseViewAddOne(Long courseId) {
        try {
            log.info("elasticsearch课程游览数 + 1, courseId = {}", courseId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.course)
                    .id(String.valueOf(courseId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.viewCount += 1"))), ESCourseIndex.class);
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 课程评论数 + 1
     *
     * @param courseId 课程id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/10 16:46
     */
    @Override
    public R courseCommentCountAdd(Long courseId) {
        try {
            log.info("elasticsearch课程回复数 + 1, courseId = {}", courseId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.course)
                    .id(String.valueOf(courseId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.commentCount += 1"))), ESCourseIndex.class);
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 课程评论数减去对应值
     *
     * @param courseId 课程id
     * @param sum 减去的对应值
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/11 14:49
     */
    @Override
    public R courseCommentSub(Long courseId, Long sum) {
        try {
            log.info("elasticsearch课程评论人数 - {}, courseId = {}", sum, courseId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.course)
                    .id(String.valueOf(courseId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.commentCount -=" + sum))), ESCourseIndex.class);
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 课程收藏数 + 1
     *
     * @param courseId 课程id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/10 16:51
     */
    @Override
    public R courseCollectCountAddOne(Long courseId) {
        try {
            log.info("elasticsearch课程收藏数 + 1, courseId = {}", courseId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.course)
                    .id(String.valueOf(courseId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.collectCount += 1"))), ESCourseIndex.class);
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 课程收藏数 - 1
     *
     * @param courseId 课程id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/10 16:51
     */
    @Override
    public R courseCollectCountSubOne(Long courseId) {
        try {
            log.info("elasticsearch课程收藏数 - 1, courseId = {}", courseId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.course)
                    .id(String.valueOf(courseId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.collectCount -= 1"))), ESCourseIndex.class);
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 课程学习人数 + 1
     *
     * @param courseId 课程id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/10 16:47
     */
    @Override
    public R courseStudyCountAddOne(Long courseId) {
        try {
            log.info("elasticsearch课程学习人数 + 1, courseId = {}", courseId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.course)
                    .id(String.valueOf(courseId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.studyCount += 1"))), ESCourseIndex.class);
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 课程学习人数 - 1
     *
     * @param courseId 课程id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/10 16:48
     */
    @Override
    public R courseStudyCountSubOne(Long courseId) {
        try {
            log.info("elasticsearch课程学习人数 - 1, courseId = {}", courseId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.course)
                    .id(String.valueOf(courseId))
                    .script(script -> script
                            .inline(inline -> inline
                                    .lang("painless")
                                    .source("ctx._source.studyCount -= 1"))), ESCourseIndex.class);
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 更新课程评分
     *
     * @param courseId 课程id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/10 16:48
     */
    @Override
    public R updateCourseScore(Long courseId, Float score) {
        try {
            Map<String, Float> map = new HashMap<>();
            map.put("score", score);
            log.info("elasticsearch课程学习人数 - 1, courseId = {}", courseId);
            elasticsearchClient.update(update -> update
                    .index(IndexConstant.course)
                    .id(String.valueOf(courseId))
                    .doc(map)
                    , ESCourseIndex.class);
            return R.ok();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }
}
