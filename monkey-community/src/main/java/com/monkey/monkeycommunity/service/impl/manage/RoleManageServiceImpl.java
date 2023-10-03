package com.monkey.monkeycommunity.service.impl.manage;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.constant.CommunityEnum;
import com.monkey.monkeycommunity.constant.CommunityRoleEnum;
import com.monkey.monkeycommunity.constant.TipConstant;
import com.monkey.monkeycommunity.mapper.CommunityRoleMapper;
import com.monkey.monkeycommunity.mapper.CommunityUserRoleConnectMapper;
import com.monkey.monkeycommunity.pojo.CommunityRole;
import com.monkey.monkeycommunity.pojo.CommunityUserRoleConnect;
import com.monkey.monkeycommunity.pojo.vo.DownNameVo;
import com.monkey.monkeycommunity.rabbitmq.EventConstant;
import com.monkey.monkeycommunity.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeycommunity.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeycommunity.service.manage.RoleManageService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/10/2 11:38
 * @version: 1.0
 * @description:
 */
@Service
public class RoleManageServiceImpl implements RoleManageService{
    @Resource
    private CommunityRoleMapper communityRoleMapper;
    @Resource
    private CommunityUserRoleConnectMapper communityUserRoleConnectMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;
    /**
     * 查询角色管理列表
     *
     * @param communityId 社区id
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/2 11:41
     */
    @Override
    public R queryRoleManageList(Long communityId, Long currentPage, Integer pageSize) {
        QueryWrapper<CommunityRole> communityRoleQueryWrapper = new QueryWrapper<>();
        communityRoleQueryWrapper.eq("community_id", communityId);
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = communityRoleMapper.selectPage(page, communityRoleQueryWrapper);
        List<CommunityRole> communityRoleList = selectPage.getRecords();
        // 得到下设头衔转化
        for (CommunityRole communityRole : communityRoleList) {

            // 得到该角色id对应的用户人数
            Long roleId = communityRole.getId();
            QueryWrapper<CommunityUserRoleConnect> communityUserRoleConnectQueryWrapper = new QueryWrapper<>();
            communityUserRoleConnectQueryWrapper.eq("role_id", roleId);
            Long selectCount = communityUserRoleConnectMapper.selectCount(communityUserRoleConnectQueryWrapper);
            communityRole.setUserCount(selectCount);

            String roleDownName = communityRole.getDownName();
            if (roleDownName != null) {
                String []downName = communityRole.getDownName().split(",");
                List<DownNameVo> downNameList = new ArrayList<>();
                for (String s : downName) {
                    if (s == null || "".equals(s)) {
                        continue;
                    }
                    DownNameVo downNameVo = new DownNameVo();
                    downNameVo.setDownName(s);
                    downNameVo.setIsPreserve(CommunityEnum.DOWN_NAME_IS_PRESERVE.getCode());
                    downNameList.add(downNameVo);
                }
                communityRole.setDownNameVoList(downNameList);
            }
        }

        selectPage.setRecords(communityRoleList);
        return R.ok(selectPage);
    }

    /**
     * 保存下设头衔
     *
     * @param roleId 角色id
     * @param downName 下设头衔名称
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/2 15:19
     */
    @Override
    public R preserveDownName(Long roleId, String downName) {
        // 判断下设名称是否重复
        CommunityRole communityRole = communityRoleMapper.selectById(roleId);
        String roleName = communityRole.getDownName();
        List<String> downNameList = new ArrayList<>();
        if (roleName != null) {
            String[] split = communityRole.getDownName().split(",");
            downNameList = new ArrayList<>(Arrays.asList(split));
            for (String s : downNameList) {
                if (s.equals(downName)) {
                    return R.error(TipConstant.downNameNotRepeat);
                }
            }
        }

        // 插入下设头衔
        downNameList.add(downName);

        StringBuilder res = new StringBuilder();
        int size = downNameList.size();
        for (int i = 0; i < size; i ++ ) {
            if (i != size - 1) {
                res.append(downNameList.get(i)).append(',');
            } else {
                res.append(downNameList.get(i));
            }
        }
        UpdateWrapper<CommunityRole> communityRoleUpdateWrapper = new UpdateWrapper<>();
        communityRoleUpdateWrapper.eq("id", roleId);
        communityRoleUpdateWrapper.set("down_name", res.toString());
        communityRoleMapper.update(null, communityRoleUpdateWrapper);
        return R.ok();
    }

    /**
     * 提交编辑角色信息
     *
     * @param communityRole 社区角色实体类
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/2 16:53
     */
    @Override
    public R submitEditRole(CommunityRole communityRole) {
        Long roleId = communityRole.getId();
        String roleName = communityRole.getRoleName();
        Long communityId = communityRole.getCommunityId();
        // 判断角色名称是否重复
        QueryWrapper<CommunityRole> communityRoleQueryWrapper = new QueryWrapper<>();
        communityRoleQueryWrapper.eq("community_id", communityId);
        communityRoleQueryWrapper.ne("id", roleId);
        communityRoleQueryWrapper.eq("role_name", roleName);
        Long selectCount = communityRoleMapper.selectCount(communityRoleQueryWrapper);
        if (selectCount > 0) {
            return R.error(TipConstant.roleNameNotRepeat);
        }

        UpdateWrapper<CommunityRole> communityRoleUpdateWrapper = new UpdateWrapper<>();
        communityRoleUpdateWrapper.eq("id", communityRole.getId());
        communityRoleUpdateWrapper.set("promotion_condition", communityRole.getPromotionCondition());
        communityRoleUpdateWrapper.set("related_benefit", communityRole.getRelatedBenefit());
        communityRoleUpdateWrapper.set("role_name", communityRole.getRoleName());
        communityRoleMapper.update(null, communityRoleUpdateWrapper);
        return R.ok();
    }

    /**
     * 删除下设头衔
     *
     * @param downName 要删除的下拉头衔名称
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/2 17:01
     */
    @Override
    public R deleteDownName(String downName, Long roleId) {
        CommunityRole communityRole = communityRoleMapper.selectById(roleId);
        String[] split = communityRole.getDownName().split(",");
        StringBuilder res = new StringBuilder();
        for (String s : split) {
            if (!s.equals(downName)) {
                res.append(s).append(',');
            }
        }

        int length = res.length();
        if (length >= 1) {
            if (res.charAt(length - 1) == ',') {
                res.deleteCharAt(length - 1);
            }
        }

        communityRole.setDownName(res.toString());
        communityRoleMapper.updateById(communityRole);
        return R.ok();
    }

    /**
     * 删除用户角色
     *
     * @param roleId 角色id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/2 17:15
     */
    @Override
    public R deleteRole(Long roleId, Long communityId) {
        // 将有该角色的用户更新为社区员工, 并删除此记录
        JSONObject data = new JSONObject();
        data.put("event", EventConstant.updateUserConnectRole);
        data.put("roleId", roleId);
        data.put("communityId", communityId);
        Message message = new Message(data.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.communityUpdateDirectExchange,
                RabbitmqRoutingName.communityUpdateRouting, message);
        return R.ok(null, TipConstant.operateSuccess);
    }

    /**
     * 新增社区角色
     *
     * @param communityRole 社区角色实体类
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/3 10:02
     */
    @Override
    public R addRole(CommunityRole communityRole, Long communityId) {
        // 判断社区角色名称是否重复
        String roleName = communityRole.getRoleName();
        QueryWrapper<CommunityRole> communityRoleQueryWrapper = new QueryWrapper<>();
        communityRoleQueryWrapper.eq("community_id", communityId);
        communityRoleQueryWrapper.eq("role_name", roleName);
        Long selectCount = communityRoleMapper.selectCount(communityRoleQueryWrapper);
        if (selectCount > 0) {
            return R.error(TipConstant.communityRoleNameRepeat);
        }
        JSONObject data = new JSONObject();
        data.put("event", EventConstant.addCommunityRole);
        data.put("communityId", communityId);
        data.put("communityRole", JSONObject.toJSONString(communityRole));
        Message message = new Message(data.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.communityInsertDirectExchange,
                RabbitmqRoutingName.communityInsertRouting, message);
        return R.ok(null, TipConstant.operateSuccess);
    }
}
