package com.monkey.monkeyblog.service.blog;

import com.monkey.monkeyUtils.result.ResultVO;

import java.util.Map;

public interface BlogArticleService {
    // 通过标签id得到文章内容
    ResultVO getArticleContentByLabelId(String labelId);

    // 博客主页分页实现
    ResultVO pagination(Integer currentPage, Integer pageSize, Long labelId, String userId);

    // 得到最近热帖
    ResultVO getRecentlyFireArticle();

    // 用户点赞
    ResultVO userClickPraise(Map<String, String> data);

    // 用户取消点赞
    ResultVO userClickOppose(Map<String, String> data);

    // 用户收藏文章
    ResultVO userCollect(Map<String, String> data);

    // 通过文章id得到文章信息
    ResultVO getArticleInformationByArticleId(Map<String, String> data);
}
