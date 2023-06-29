package com.monkey.monkeyblog.service.user.center;

import com.monkey.monkeyUtils.result.ResultVO;

public interface UserHomeService {

    // 通过用户id查询用户信息
    ResultVO getUserInformationByUserId(Long userId, String nowUserId1);

    // 将访问者信息加入用户游览信息列表
    ResultVO recentlyView(Long userId, Long reviewId);

    // 通过用户id得到最近来访用户信息
    ResultVO getRecentlyUserInfoByUserId(Long userId);

    // 通过用户id得到用户所发表的所有文章分类数
    ResultVO getUserArticleClassficationCountByuserId(Long userId);

    // 通过用户id得到文章列表
    ResultVO getArticleListByUserId(Long currentPage, Long pageSize, Long labelId, String userId);

    // 通过用户id得到用户粉丝列表
    ResultVO getFansListByUserId(Integer currentPage, Integer pageSize, Long userId, String nowUserId);

    // 通过用户id得到关注列表
    ResultVO getConcernListByUserId(Integer currentPage, Integer pageSize, Long userId, String nowUserId);

    // 通过用户id得到用户收藏文章列表
    ResultVO getUserCollectArticleListByUserId(Integer currentPage, Integer pageSize, Long userId, String nowUserId);

    // 提交编辑资料之后更新用户信息
    ResultVO updateInformation(String userInformation1);

    // 通过用户id得到文章提问列表
    ResultVO getQuestionListByUserId(Long userId, Long currentPage, Long pageSize);
}
