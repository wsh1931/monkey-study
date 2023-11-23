package com.monkey.monkeycommunity.service.impl.manage;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.MapUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeyUtils.excel.ExcelSheetNameConstant;
import com.monkey.monkeyUtils.excel.ExcelTableNameConstant;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.constant.CommunityEnum;
import com.monkey.monkeycommunity.constant.TipConstant;
import com.monkey.monkeycommunity.mapper.CommunityRoleConnectMapper;
import com.monkey.monkeycommunity.mapper.CommunityUserRoleConnectMapper;
import com.monkey.monkeycommunity.mapper.CommunityRoleMapper;
import com.monkey.monkeycommunity.mapper.CommunityUserInviteMapper;
import com.monkey.monkeycommunity.pojo.CommunityRole;
import com.monkey.monkeycommunity.pojo.CommunityRoleConnect;
import com.monkey.monkeycommunity.pojo.CommunityUserRoleConnect;
import com.monkey.monkeycommunity.pojo.CommunityUserInvite;
import com.monkey.monkeycommunity.pojo.vo.UserManageVo;
import com.monkey.monkeycommunity.rabbitmq.EventConstant;
import com.monkey.monkeycommunity.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeycommunity.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeycommunity.service.manage.UserManageService;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import com.monkey.monkeyUtils.mapper.UserMapper;
import com.monkey.monkeyUtils.pojo.User;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: wusihao
 * @date: 2023/9/30 11:30
 * @version: 1.0
 * @description:
 */
@Service
public class UserManageServiceImpl implements UserManageService {
    @Resource
    private CommunityUserRoleConnectMapper communityUserRoleConnectMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private CommunityRoleMapper communityRoleMapper;
    @Resource
    private CommunityUserInviteMapper communityUserInviteMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private CommunityRoleConnectMapper communityRoleConnectMapper;
    /**
     * 查询用户信息集合
     *
     * @param communityId 社区id
     * @param pageSize 每页数据量
     * @param currentPage 当前页
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/30 11:34
     */
    @Override
    public R queryUserInfoList(Long communityId, Long currentPage, Integer pageSize) {
        QueryWrapper<CommunityUserRoleConnect> communityRoleConnectQueryWrapper = new QueryWrapper<>();
        communityRoleConnectQueryWrapper.eq("community_id", communityId);
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = communityUserRoleConnectMapper.selectPage(page, communityRoleConnectQueryWrapper);
        JSONObject data =  getUserManageList(selectPage);
        return R.ok(data);
    }

    /**
     * 查询社区角色列表
     *
     * @param communityId 社区id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/30 14:44
     */
    @Override
    public R queryRoleList(Long communityId) {
        QueryWrapper<CommunityRoleConnect> communityRoleConnectQueryWrapper = new QueryWrapper<>();
        communityRoleConnectQueryWrapper.eq("community_id", communityId);
        communityRoleConnectQueryWrapper.select("role_id");
        List<CommunityRoleConnect> communityUserRoleConnectList = communityRoleConnectMapper.selectList(communityRoleConnectQueryWrapper);
        List<Long> roleIdList = new ArrayList<>();
        for (CommunityRoleConnect communityRoleConnect : communityUserRoleConnectList) {
            roleIdList.add(communityRoleConnect.getRoleId());
        }
        QueryWrapper<CommunityRole> communityRoleQueryWrapper = new QueryWrapper<>();
        communityRoleQueryWrapper.in("id", roleIdList);
        communityRoleQueryWrapper.select("id", "role_name");
        List<CommunityRole> communityRoleList = communityRoleMapper.selectList(communityRoleQueryWrapper);
        return R.ok(communityRoleList);
    }

    /**
     * 更新用户角色
     *
     * @param userId 用户id
     * @param roleId 角色id
     * @param communityId 社区id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/30 15:07
     */
    @Override
    public R updateUserRole(Long userId, Long roleId, Long communityId) {
        UpdateWrapper<CommunityUserRoleConnect> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("community_id", communityId);
        updateWrapper.eq("role_id", roleId);
        updateWrapper.set("role_id", roleId);
        communityUserRoleConnectMapper.update(null, updateWrapper);
        return R.ok();
    }

    /**
     * 删除用户角色
     *
     * @param userId 用户id
     * @param communityId 社区id
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/30 15:29
     */
    @Override
    public R deleteUserRole(Long userId, Long communityId) {
        QueryWrapper<CommunityUserRoleConnect> communityRoleConnectQueryWrapper = new QueryWrapper<>();
        communityRoleConnectQueryWrapper.eq("community_id", communityId);
        communityRoleConnectQueryWrapper.eq("user_id", userId);
        communityUserRoleConnectMapper.delete(communityRoleConnectQueryWrapper);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.getCommunityMemberCountSubOne);
        jsonObject.put("communityId", communityId);
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.communityUpdateDirectExchange,
                RabbitmqRoutingName.communityUpdateRouting, message);
        return R.ok();
    }

    /**
     * 模糊查询用户列表
     *
     * @param userId      用户名
     * @param roleId      角色名
     * @param communityId 社区id
     * @param currentPage
     * @param pageSize
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/30 16:16
     */
    @Override
    public R queryUserListByVague(String userId, String roleId, Long communityId, Long currentPage, Integer pageSize) {
        QueryWrapper<CommunityUserRoleConnect> communityRoleConnectQueryWrapper = new QueryWrapper<>();
        communityRoleConnectQueryWrapper.eq("community_id", communityId);
        if (roleId != null && !"".equals(roleId)) {
            communityRoleConnectQueryWrapper.eq("role_id", roleId);
        }
        if (userId != null && !"".equals(userId)) {
            communityRoleConnectQueryWrapper.eq("user_id", userId);
        }

        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = communityUserRoleConnectMapper.selectPage(page, communityRoleConnectQueryWrapper);
        JSONObject data =  getUserManageList(selectPage);

        return R.ok(data);
    }

    /**
     * 导出用户数据
     *
     * @param userManageVoList 用户集合
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/30 16:46
     */
    @Override
    public void exportData(List<UserManageVo> userManageVoList, HttpServletResponse response) throws Exception {
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode(ExcelTableNameConstant.userManageList, "UTF-8")
                    .replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            // 这里需要设置不关闭流
            EasyExcel.write(response.getOutputStream(), UserManageVo.class)
                    .autoCloseStream(Boolean.FALSE)
                    .sheet(ExcelSheetNameConstant.sheet1)
                    .doWrite(userManageVoList);
        } catch (Exception e) {
            // 重置response
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.setStatus(R.Error);
            Map<String, Object> map = MapUtils.newHashMap();
            map.put("status", "failure");
            map.put("message", "下载文件失败, 错误原因为 ==> " + e.getMessage());
            response.getWriter().write(JSON.toJSONString(map));
        }
    }

    /**
     * 邀请用户
     *
     * @param communityId 社区id
     * @param inviteUserId 被邀请的用户id
     * @param inviteRoleId 设定的用户角色
     * @param nowUserId 当前登录用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/1 9:19
     */
    @Override
    public R inviteUser(Long communityId, Long inviteUserId, Long inviteRoleId, Long nowUserId) {
        // 判断该用户是否存在
        User user = userMapper.selectById(inviteUserId);
        if (user == null) {
            return R.error(TipConstant.noExistUser);
        }
        // 判断该用户是否在社区中
        QueryWrapper<CommunityUserRoleConnect> communityRoleConnectQueryWrapper = new QueryWrapper<>();
        communityRoleConnectQueryWrapper.eq("community_id", communityId);
        communityRoleConnectQueryWrapper.eq("user_id", inviteUserId);
        Long selectCount = communityUserRoleConnectMapper.selectCount(communityRoleConnectQueryWrapper);
        if (selectCount > 0) {
            return R.error(TipConstant.alreadyInCommunity);
        }

        QueryWrapper<CommunityUserInvite> communityUserInviteQueryWrapper = new QueryWrapper<>();
        communityUserInviteQueryWrapper.eq("invite_id", inviteUserId);
        communityUserInviteQueryWrapper.eq("community_id", communityId);
        communityUserInviteQueryWrapper.select("status");
        CommunityUserInvite communityUserInvite = communityUserInviteMapper.selectOne(communityUserInviteQueryWrapper);
        if (communityUserInvite == null || communityUserInvite.getStatus().equals(CommunityEnum.ALREADY_REFUSE.getCode())
        || communityUserInvite.getStatus().equals(CommunityEnum.ALREADY_APPROVAL.getCode())) {
            // 邀请用户进入社区
            JSONObject data = new JSONObject();
            data.put("event", EventConstant.inviteUserEnterCommunity);
            data.put("communityId", communityId);
            data.put("inviteUserId", inviteUserId);
            data.put("inviteRoleId", inviteRoleId);
            data.put("nowUserId", nowUserId);
            Message message = new Message(data.toJSONString().getBytes());
            rabbitTemplate.convertAndSend(RabbitmqExchangeName.communityInsertDirectExchange,
                    RabbitmqRoutingName.communityInsertRouting, message);

            return R.ok();
        } else {
            return R.error(TipConstant.alreadyInvited);
        }
    }

    /**
     * 得到userManageVoList
     *
     * @param selectPage 分页类
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/30 16:28
     */
    private JSONObject getUserManageList(Page selectPage) {
        List<CommunityUserRoleConnect> records = selectPage.getRecords();
        long nowRoleId = 0;
        List<UserManageVo> userManageVoList = new ArrayList<>();
        for (CommunityUserRoleConnect communityUserRoleConnect : records) {
            UserManageVo userManageVo = new UserManageVo();

            Long userId = communityUserRoleConnect.getUserId();
            User user = userMapper.selectById(userId);
            userManageVo.setUsername(user.getUsername());
            userManageVo.setHeadImg(user.getPhoto());

            Long roleId = communityUserRoleConnect.getRoleId();
            CommunityRole communityRole = communityRoleMapper.selectById(roleId);
            if (communityRole != null) {
                userManageVo.setRoleName(communityRole.getRoleName());
                userManageVo.setRoleId(communityRole.getId());
            }


            userManageVo.setId(userId);
            userManageVo.setCreateTime(communityUserRoleConnect.getCreateTime());

            // 判断当前登录用户在该社区的角色
            long nowUserId = Long.parseLong(JwtUtil.getUserId());
            if (userId.equals(nowUserId)) {
                nowRoleId = roleId;
            }

            userManageVoList.add(userManageVo);
        }

        selectPage.setRecords(userManageVoList);

        JSONObject data = new JSONObject();
        data.put("roleId", nowRoleId);
        data.put("data", selectPage);
        return data;
    }
}
