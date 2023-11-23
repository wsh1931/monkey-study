package com.monkey.monkeycommunity.service.impl.manage;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.constant.CommunityEnum;
import com.monkey.monkeycommunity.constant.CommunityRoleEnum;
import com.monkey.monkeycommunity.constant.TipConstant;
import com.monkey.monkeycommunity.constant.TryLockTimeEnum;
import com.monkey.monkeycommunity.mapper.CommunityRoleMapper;
import com.monkey.monkeycommunity.mapper.CommunityUserApplicationMapper;
import com.monkey.monkeycommunity.mapper.CommunityUserRoleConnectMapper;
import com.monkey.monkeycommunity.pojo.CommunityRole;
import com.monkey.monkeycommunity.pojo.CommunityUserApplication;
import com.monkey.monkeycommunity.pojo.CommunityUserRoleConnect;
import com.monkey.monkeycommunity.rabbitmq.EventConstant;
import com.monkey.monkeycommunity.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeycommunity.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeycommunity.service.manage.UserApplicationService;
import com.monkey.monkeyUtils.mapper.UserMapper;
import com.monkey.monkeyUtils.pojo.User;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;

/**
 * @author: wusihao
 * @date: 2023/10/3 14:30
 * @version: 1.0
 * @description:
 */
@Service
public class UserApplicationServiceImpl implements UserApplicationService {
    @Resource
    private CommunityUserApplicationMapper communityUserApplicationMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private CommunityUserRoleConnectMapper communityUserRoleConnectMapper;

    @Resource
    private CommunityRoleMapper communityRoleMapper;
    private static final ReentrantLock reentrantLock = new ReentrantLock();
    /**
     * 查询用户申请列表
     *
     * @param communityId 社区id
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/3 14:36
     */
    @Override
    public R queryUserApplicationList(Long communityId, Long currentPage, Integer pageSize) {
        QueryWrapper<CommunityUserApplication> communityUserApplicationQueryWrapper = new QueryWrapper<>();
        communityUserApplicationQueryWrapper.eq("community_id", communityId);
        communityUserApplicationQueryWrapper.eq("status", CommunityEnum.WAIT_APPROVAL.getCode());
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = communityUserApplicationMapper.selectPage(page, communityUserApplicationQueryWrapper);
        List<CommunityUserApplication> communityUserApplicationList = selectPage.getRecords();
        for (CommunityUserApplication communityUserApplication : communityUserApplicationList) {
            Long userId = communityUserApplication.getUserId();
            User user = userMapper.selectById(userId);
            communityUserApplication.setUsername(user.getUsername());
            communityUserApplication.setHeadImg(user.getPhoto());
            communityUserApplication.setBrief(user.getBrief());
        }

        selectPage.setRecords(communityUserApplicationList);
        return R.ok(selectPage);
    }

    /**
     * 拒绝用户加入社区
     *
     * @param communityUserApplicationList 社区用户申请实体类
     * @param nowUserId 当前登录用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/4 10:23
     */
    @Override
    public R batchRefuseUserApplication(List<CommunityUserApplication> communityUserApplicationList, long nowUserId) {
        try {
            boolean tryLock = reentrantLock.tryLock(TryLockTimeEnum.BATCH_DELETE_APPLICATION.getTimeUnit(), TimeUnit.SECONDS);
            if (!tryLock) {
                return R.error(R.Error, TipConstant.systemBusyWait);
            }
        } catch (InterruptedException e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
        try {
            // 判断用户是否已经通过申请
            for (CommunityUserApplication communityUserApplication : communityUserApplicationList) {
                UpdateWrapper<CommunityUserApplication> communityUserApplicationUpdateWrapper = new UpdateWrapper<>();
                communityUserApplicationUpdateWrapper.eq("community_id", communityUserApplication.getCommunityId());
                communityUserApplicationUpdateWrapper.eq("user_id", communityUserApplication.getUserId());
                communityUserApplicationUpdateWrapper.ne("status", CommunityEnum.WAIT_APPROVAL.getCode());
                Long selectCount = communityUserApplicationMapper.selectCount(communityUserApplicationUpdateWrapper);
                if (selectCount > 0) {
                    return R.error(communityUserApplication.getUsername() + TipConstant.applicationAlreadyProcess);
                }
            }
            for (CommunityUserApplication communityUserApplication : communityUserApplicationList) {
                UpdateWrapper<CommunityUserApplication> communityUserApplicationUpdateWrapper = new UpdateWrapper<>();
                communityUserApplicationUpdateWrapper.eq("community_id", communityUserApplication.getCommunityId());
                communityUserApplicationUpdateWrapper.eq("user_id", communityUserApplication.getUserId());
                communityUserApplicationUpdateWrapper.set("status", CommunityEnum.ALREADY_REFUSE.getCode());
                communityUserApplicationUpdateWrapper.set("approval_id", nowUserId);
                communityUserApplicationUpdateWrapper.set("update_time", new Date());
                communityUserApplicationMapper.update(null, communityUserApplicationUpdateWrapper);
            }
        } finally {
            reentrantLock.unlock();
        }
        return R.ok();
    }

    /**
     * 批量通过用户申请
     *
     * @param communityUserApplicationList 社区用户申请集合
     * @param nowUserId 当前登录用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/4 11:13
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R batchPassUserApplication(long nowUserId, List<CommunityUserApplication> communityUserApplicationList) {
        try {
            boolean tryLock = reentrantLock.tryLock(TryLockTimeEnum.BATCH_SUCCESS_APPLICATION.getTimeUnit(), TimeUnit.SECONDS);
            if (!tryLock) {
                return R.error(TipConstant.systemBusyWait);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // 批量通过用户申请

        try {
            // 判断用户是否在社区中
            for (CommunityUserApplication communityUserApplication : communityUserApplicationList) {
                Long userId = communityUserApplication.getUserId();
                Long communityId = communityUserApplication.getCommunityId();
                QueryWrapper<CommunityUserRoleConnect> communityUserRoleConnectQueryWrapper = new QueryWrapper<>();
                communityUserRoleConnectQueryWrapper.eq("user_id", userId);
                communityUserRoleConnectQueryWrapper.eq("community_id", communityId);
                Long selectCount = communityUserRoleConnectMapper.selectCount(communityUserRoleConnectQueryWrapper);
                if (selectCount > 0) {
                    return R.error(communityUserApplication.getUsername() + TipConstant.alreadyInCommunity);
                }
            }

            Date updateTime = new Date();
            for (CommunityUserApplication communityUserApplication : communityUserApplicationList) {
                // 更新社区用户申请表的状态
                Long userId = communityUserApplication.getUserId();
                Long communityId = communityUserApplication.getCommunityId();
                UpdateWrapper<CommunityUserApplication> communityUserApplicationUpdateWrapper = new UpdateWrapper<>();
                communityUserApplicationUpdateWrapper.eq("community_id", communityId);
                communityUserApplicationUpdateWrapper.eq("user_id", userId);
                communityUserApplicationUpdateWrapper.set("status", CommunityEnum.ALREADY_APPROVAL.getCode());
                communityUserApplicationUpdateWrapper.set("approval_id", nowUserId);
                communityUserApplicationUpdateWrapper.set("update_time", updateTime);
                communityUserApplicationMapper.update(null, communityUserApplicationUpdateWrapper);
                communityUserApplicationUpdateWrapper.set("update_time", updateTime);
                // 在社区角色表里面找到社区员工id
                QueryWrapper<CommunityRole> communityRoleQueryWrapper = new QueryWrapper<>();
                communityRoleQueryWrapper.eq("community_id", communityId);
                communityRoleQueryWrapper.eq("role_name", CommunityRoleEnum.MEMBER.getMsg());
                communityRoleQueryWrapper.select("id");
                CommunityRole communityRole = communityRoleMapper.selectOne(communityRoleQueryWrapper);
                Long roleId = communityRole.getId();
                // 插入社区用户角色关系表
                CommunityUserRoleConnect communityUserRoleConnect = new CommunityUserRoleConnect();
                communityUserRoleConnect.setCommunityId(communityId);
                communityUserRoleConnect.setUserId(userId);
                communityUserRoleConnect.setRoleId(roleId);
                communityUserRoleConnect.setCreateTime(updateTime);
                communityUserRoleConnect.setUpdateTime(updateTime);
                communityUserRoleConnectMapper.insert(communityUserRoleConnect);
            }
        } finally {
            reentrantLock.unlock();
        }
        return R.ok();
    }

    /**
     * 查询已通过申请列表
     *
     * @param communityId 社区id
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/4 11:56
     */
    @Override
    public R querySuccessApplicationList(Long communityId, Long currentPage, Integer pageSize) {
        QueryWrapper<CommunityUserApplication> communityUserApplicationQueryWrapper = new QueryWrapper<>();
        communityUserApplicationQueryWrapper.eq("community_id", communityId);
        communityUserApplicationQueryWrapper.eq("status", CommunityEnum.APPROVE_EXAMINE.getCode());
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = communityUserApplicationMapper.selectPage(page, communityUserApplicationQueryWrapper);
        List<CommunityUserApplication> communityUserApplicationList = selectPage.getRecords();

        List<CommunityUserApplication> communityUserApplications = getApplicationAndApprovalUserInfo(communityUserApplicationList);

        selectPage.setRecords(communityUserApplications);
        return R.ok(selectPage);
    }

    /**
     * 得到申请人和审批人用户信息
     *
     * @param communityUserApplicationList 需要转化的集合
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/4 14:19
     */
    private List<CommunityUserApplication> getApplicationAndApprovalUserInfo(List<CommunityUserApplication> communityUserApplicationList) {
        for (CommunityUserApplication communityUserApplication : communityUserApplicationList) {
            // 查询审核用户信息
            Long approvalId = communityUserApplication.getApprovalId();
            User approval = userMapper.selectById(approvalId);
            communityUserApplication.setApprovalHeadImg(approval.getPhoto());
            communityUserApplication.setApprovalUsername(approval.getUsername());

            // 查询申请用户信息
            Long userId = communityUserApplication.getUserId();
            User user = userMapper.selectById(userId);
            communityUserApplication.setUsername(user.getUsername());
            communityUserApplication.setHeadImg(user.getPhoto());
            communityUserApplication.setBrief(user.getBrief());
        }

        return communityUserApplicationList;
    }

    /**
     * 查询已拒绝申请列表
     *
     *
     * @param communityId 社区id
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/4 14:17
     */
    @Override
    public R queryRefuseApplicationList(Long communityId, Long currentPage, Integer pageSize) {
        QueryWrapper<CommunityUserApplication> communityUserApplicationQueryWrapper = new QueryWrapper<>();
        communityUserApplicationQueryWrapper.eq("community_id", communityId);
        communityUserApplicationQueryWrapper.eq("status", CommunityEnum.APPLICATION_FAIL.getCode());
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = communityUserApplicationMapper.selectPage(page, communityUserApplicationQueryWrapper);
        List<CommunityUserApplication> communityUserApplicationList = selectPage.getRecords();
        List<CommunityUserApplication> communityUserApplications = getApplicationAndApprovalUserInfo(communityUserApplicationList);
        selectPage.setRecords(communityUserApplications);
        return R.ok(selectPage);
    }

    /**
     * 批量删除申请记录
     *
     * @param communityUserApplicationList 社区用户申请集合
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/4 14:54
     */
    @Override
    public R batchDeleteUserApplication(List<CommunityUserApplication> communityUserApplicationList) {
        List<Long> idList = new ArrayList<>();
        for (CommunityUserApplication communityUserApplication : communityUserApplicationList) {
            idList.add(communityUserApplication.getId());
        }
        QueryWrapper<CommunityUserApplication> communityUserApplicationQueryWrapper = new QueryWrapper<>();
        communityUserApplicationQueryWrapper.in("id", idList);
        communityUserApplicationMapper.delete(communityUserApplicationQueryWrapper);
        return R.ok();
    }

    /**
     * 删除全部拒绝用户申请记录
     *
     * @param communityId 社区id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/4 15:15
     */
    @Override
    public R deleteAllRefuseRecord(Long communityId) {
        JSONObject data = new JSONObject();
        data.put("event", EventConstant.deleteAllRefuseApplicationRecords);
        data.put("communityId", communityId);
        Message message = new Message(data.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.communityDeleteDirectExchange,
                RabbitmqRoutingName.communityDeleteRouting, message);
        return R.ok(null, TipConstant.operateSuccess);
    }

    /**
     * 删除全部已通过用户申请记录
     *
     * @param communityId 社区id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/4 15:16
     */
    @Override
    public R deleteAllSuccessRecord(Long communityId) {
        JSONObject data = new JSONObject();
        data.put("event", EventConstant.deleteAllSuccessApplicationRecords);
        data.put("communityId", communityId);
        Message message = new Message(data.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.communityDeleteDirectExchange,
                RabbitmqRoutingName.communityDeleteRouting, message);
        return R.ok(null, TipConstant.operateSuccess);
    }
}
