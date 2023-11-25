package com.monkey.monkeysearch.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetResponse;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeysearch.constant.IndexConstant;
import com.monkey.monkeysearch.pojo.ESUserIndex;
import com.monkey.monkeysearch.service.UserHomeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/11/23 22:41
 * @version: 1.0
 * @description:
 */
@Service
@Slf4j
public class UserHomeServiceImpl implements UserHomeService {
    @Resource
    private ElasticsearchClient elasticsearchClient;
    /**
     * 查询用户成就
     *
     * @param userId 用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/23 21:21
     */
    @Override
    public R queryUserAchievement(String userId) {
        try {
            GetResponse<ESUserIndex> response = elasticsearchClient.get(get -> get
                    .index(IndexConstant.user)
                    .id(userId)
                    .sourceIncludes("opusCount", "collectCount", "likeCount", "viewCount"), ESUserIndex.class);


            ESUserIndex source = response.source();
            return R.ok(source);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }
}
