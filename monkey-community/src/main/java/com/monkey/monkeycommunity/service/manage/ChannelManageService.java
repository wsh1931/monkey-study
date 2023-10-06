package com.monkey.monkeycommunity.service.manage;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.pojo.CommunityChannel;

public interface ChannelManageService {
    // 查询社区频道管理列表
    R queryChannelManageList(Long communityId, Long currentPage, Integer pageSize, String channelName);

    // 更新是否支持前端展示
    R updateSupportShow(Long channelId, Integer supportShow);

    // 更新是否支持用户发表文章
    R updateSupportUserPublish(Long channelId, Integer supportUserPublish);

    // 更新是否支持管理员修改
    R updateSupportManageModify(Long channelId, Integer supportManageModify);

    // 提交频道编辑
    R submitChannelEdit(CommunityChannel communityChannel);

    // 删除频道
    R deleteChannel(Long channelId);

    // 新增频道
    R addChannel(Long communityId, String channelName, Long sort);
}
