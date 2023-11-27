package com.monkey.monkeysearch.service;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeysearch.pojo.ESCommunityIndex;

public interface CommunityFeignService {
    // 社区成员数 + 1
    R communityMemberAddOne(Long communityId);
    // 社区成员数 - 1
    R communityMemberSubOne(Long communityId);

    // 社区文章数 + 1
    R communityArticleAddOne(Long communityId);

    // 社区文章数 - 1
    R communityArticleSubOne(Long communityId);

    // 创建社区
    R createCommunity(ESCommunityIndex esCommunityIndex);

    // 删除社区
    R deleteCommunity(Long communityId);
}
