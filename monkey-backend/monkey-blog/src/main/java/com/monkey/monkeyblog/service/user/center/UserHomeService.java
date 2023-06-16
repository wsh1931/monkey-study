package com.monkey.monkeyblog.service.user.center;

import com.monkey.monkeyUtils.result.ResultVO;

import java.util.Map;

public interface UserHomeService {

    // 通过用户id查询用户信息
    ResultVO getUserInformationByUserId(Map<String, String> data);

    // 将访问者信息加入用户游览信息列表
    ResultVO recentlyView(Map<String, String> data);

    // 通过用户id得到最近来访用户信息
    ResultVO getRecentlyUserInfoByUserId(Map<String, String> data);

    // 通过用户id得到用户所发表的所有文章分类数
    ResultVO getUserArticleClassficationCountByuserId(Map<String, String> data);

    // 通过用户id得到文章列表
    ResultVO getArticleListByUserId(Map<String, String> data);

    // 通过用户id得到用户粉丝列表
    ResultVO getFansListByUserId(Map<String, String> data);

    // 通过用户id得到关注列表
    ResultVO getConcernListByUserId(Map<String, String> data);

    // 通过用户id得到用户收藏文章列表
    ResultVO getUserCollectArticleListByUserId(Map<String, String> data);

    // 提交编辑资料之后更新用户信息
    ResultVO updateInformation(Map<String, String> data);

}
