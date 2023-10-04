package com.monkey.monkeycommunity.service.manage;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.pojo.CommunityUserApplication;

import java.util.List;

public interface UserApplicationService {
    // 查询用户申请列表
    R queryUserApplicationList(Long communityId, Long currentPage, Integer pageSize);


    // 拒绝用户加入社区
    R batchRefuseUserApplication(List<CommunityUserApplication> communityUserApplicationList, long nowUserId);

    // 批量通过用户申请
    R batchPassUserApplication(long nowUserId, List<CommunityUserApplication> communityUserApplicationList);

    // 查询已通过申请列表
    R querySuccessApplicationList(Long communityId, Long currentPage, Integer pageSize);

    // 查询已拒绝申请列表
    R queryRefuseApplicationList(Long communityId, Long currentPage, Integer pageSize);

    // 批量删除申请记录
    R batchDeleteUserApplication(List<CommunityUserApplication> communityUserApplicationList);

    // 删除全部拒绝用户申请记录
    R deleteAllRefuseRecord(Long communityId);

    // 删除全部已通过用户申请记录
    R deleteAllSuccessRecord(Long communityId);
}
