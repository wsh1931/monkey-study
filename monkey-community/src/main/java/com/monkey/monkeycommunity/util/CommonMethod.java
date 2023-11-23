package com.monkey.monkeycommunity.util;

import com.monkey.monkeyUtils.constants.CommunityManageMenuEnum;
import com.monkey.monkeyUtils.mapper.CommunityManageMenuConnectMapper;
import com.monkey.monkeyUtils.mapper.CommunityManageMenuMapper;
import com.monkey.monkeyUtils.pojo.CommunityManageMenu;
import com.monkey.monkeyUtils.pojo.CommunityManageMenuConnect;
import com.monkey.monkeyUtils.redis.RedisKeyAndTimeEnum;
import com.monkey.monkeyUtils.springsecurity.AuthorityMethods;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.text.DecimalFormat;
import java.util.Date;

/**
 * @author: wusihao
 * @date: 2023/9/19 7:57
 * @version: 1.0
 * @description: 公共方法
 */
public class CommonMethod {
    /**
     * 将一个double类型的变量转化为一个百分比的数 比如21.12转化为21%
     *
     * @param number 待转化的数
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/19 7:58
     */
    public static Integer doubleToRate(Double number) {
        number *= 100;
        // 创建 DecimalFormat 对象，并设置格式为两位小数
        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        // 使用 DecimalFormat 对象对 double 值进行格式化
        String formattedNumber = decimalFormat.format(number);
        return (int)Double.parseDouble(formattedNumber);
    }

    /**
     * 将double转化为保留一位小数的字符串
     *
     * @param number 待转化的数
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/19 8:21
     */
    public static String doubleToOne(double number) {
        // 创建 DecimalFormat 对象，并设置格式为两位小数
        DecimalFormat decimalFormat = new DecimalFormat("0.0");

        // 使用 DecimalFormat 对象对 double 值进行格式化
        String formattedNumber = decimalFormat.format(number);
        return formattedNumber;
    }

    /**
     * 插入社区具体权限功能
     *
     * @param communityManageId 社区管理表id
     * @param communityId 社区id
     * @param menuEnum 权限枚举类
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/22 8:41
     */
    public static void insertDetailMenu(Long communityManageId, Long communityId,
                                        CommunityManageMenuEnum menuEnum,
                                        CommunityManageMenuMapper communityManageMenuMapper,
                                        CommunityManageMenuConnectMapper communityManageMenuConnectMapper) {
        CommunityManageMenu communityManageMenu = new CommunityManageMenu();
        communityManageMenu.setPerms(menuEnum.getPerms() + communityId);
        communityManageMenu.setMenuName(menuEnum.getMenuName());
        communityManageMenu.setComponent(menuEnum.getComponent());
        communityManageMenu.setIcon(menuEnum.getMenuIcon());
        String administratorRouter = menuEnum.getRouterPath();
        administratorRouter = getRealPath(administratorRouter, communityId);
        communityManageMenu.setPath(administratorRouter);
        communityManageMenu.setCreateTime(new Date());
        communityManageMenuMapper.insert(communityManageMenu);

        CommunityManageMenuConnect adminManageConnect = new CommunityManageMenuConnect();
        adminManageConnect.setCommunityManageId(communityManageId);
        adminManageConnect.setCommunityManageMenuId(communityManageMenu.getId());
        communityManageMenuConnectMapper.insert(adminManageConnect);
    }

    /**
     * 插入社区具体权限功能
     *
     * @param communityManageId 社区管理表id
     * @param communityId 社区id
     * @param menuEnum 权限枚举类
     * @param userId 添加权限的用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/22 8:41
     */
    public static void insertDetailMenu(Long communityManageId, Long communityId,
                                        CommunityManageMenuEnum menuEnum,
                                        String userId,
                                        CommunityManageMenuMapper communityManageMenuMapper,
                                        CommunityManageMenuConnectMapper communityManageMenuConnectMapper,
                                        StringRedisTemplate stringRedisTemplate) {
        CommunityManageMenu communityManageMenu = new CommunityManageMenu();
        communityManageMenu.setPerms(menuEnum.getPerms() + communityId);
        communityManageMenu.setMenuName(menuEnum.getMenuName());
        communityManageMenu.setComponent(menuEnum.getComponent());
        communityManageMenu.setIcon(menuEnum.getMenuIcon());
        communityManageMenu.setCreateTime(new Date());
        String administratorRouter = menuEnum.getRouterPath();
        administratorRouter = getRealPath(administratorRouter, communityId);
        communityManageMenu.setPath(administratorRouter);
        String nowUserId = JwtUtil.getUserId();
        if (nowUserId != null && !"".equals(nowUserId)) {
            communityManageMenu.setCreateUser(Long.parseLong(nowUserId));
        }
        communityManageMenu.setCreateTime(new Date());
        communityManageMenuMapper.insert(communityManageMenu);

        CommunityManageMenuConnect adminManageConnect = new CommunityManageMenuConnect();
        adminManageConnect.setCommunityManageId(communityManageId);
        adminManageConnect.setCommunityManageMenuId(communityManageMenu.getId());
        communityManageMenuConnectMapper.insert(adminManageConnect);

        AuthorityMethods.addUserAuthority(userId,
                menuEnum.getPerms() + communityId,
                stringRedisTemplate);
    }

    /**
     * 得到真实路由路径
     *
     * @param routerPath 原路由路径
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/21 10:53
     */
    private static String getRealPath(String routerPath, Long communityId) {
        String substring = routerPath.substring(1);
        String []split = substring.split("/");
        StringBuilder res = new StringBuilder("/");
        res.append(split[0]);
        res.append("/");
        res.append(split[1]);
        res.append("/");
        res.append(communityId);
        res.append("/");
        res.append(split[2]);

        return res.toString();
    }

    /**
     * 插入社区管理员权限
     *
     * @param communityManageId 社区管理id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/22 8:55
     */
    public static void insertCommunityManagerMenu(Long communityManageId,
                                                  Long communityId,
                                                  String userId,
                                                  CommunityManageMenuMapper communityManageMenuMapper,
                                                  CommunityManageMenuConnectMapper communityManageMenuConnectMapper,
                                                  StringRedisTemplate stringRedisTemplate) {
        // 插入用户管理权限表
        CommonMethod.insertDetailMenu(communityManageId,
                communityId,
                CommunityManageMenuEnum.USER_MANAGE,
                userId,
                communityManageMenuMapper,
                communityManageMenuConnectMapper,
                stringRedisTemplate);

        // 插入角色管理权限表
        CommonMethod.insertDetailMenu(communityManageId,
                communityId,
                CommunityManageMenuEnum.ROLE_MANAGE,
                userId,
                communityManageMenuMapper,
                communityManageMenuConnectMapper,
                stringRedisTemplate);

        // 插入加入申请权限表
        CommonMethod.insertDetailMenu(communityManageId,
                communityId,
                CommunityManageMenuEnum.USER_APPLICATION,
                userId,
                communityManageMenuMapper,
                communityManageMenuConnectMapper,
                stringRedisTemplate);

        // 插入内容管理权限表
        CommonMethod.insertDetailMenu(communityManageId,
                communityId,
                CommunityManageMenuEnum.CONTENT_MANAGE,
                userId,
                communityManageMenuMapper,
                communityManageMenuConnectMapper,
                stringRedisTemplate);

        // 插入频道权限表
        CommonMethod.insertDetailMenu(communityManageId,
                communityId,
                CommunityManageMenuEnum.CHANNEL_MANAGE,
                userId,
                communityManageMenuMapper,
                communityManageMenuConnectMapper,
                stringRedisTemplate);

        // 插入信息管理权限表
        CommonMethod.insertDetailMenu(communityManageId,
                communityId,
                CommunityManageMenuEnum.INFORMATION_MANAGE,
                userId,
                communityManageMenuMapper,
                communityManageMenuConnectMapper,
                stringRedisTemplate);
    }

    /**
     * 插入社区主管理员权限
     *
     * @param communityManageId 社区管理id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/22 8:55
     */
    public static void insertCommunityManagerMenu(Long communityManageId,
                                                  Long communityId,
                                                  CommunityManageMenuMapper communityManageMenuMapper,
                                                  CommunityManageMenuConnectMapper communityManageMenuConnectMapper) {
        // 插入用户管理权限表
        CommonMethod.insertDetailMenu(communityManageId,
                communityId,
                CommunityManageMenuEnum.USER_MANAGE,
                communityManageMenuMapper,
                communityManageMenuConnectMapper);

        // 插入角色管理权限表
        CommonMethod.insertDetailMenu(communityManageId,
                communityId,
                CommunityManageMenuEnum.ROLE_MANAGE,
                communityManageMenuMapper,
                communityManageMenuConnectMapper);

        // 插入加入申请权限表
        CommonMethod.insertDetailMenu(communityManageId,
                communityId,
                CommunityManageMenuEnum.USER_APPLICATION,
                communityManageMenuMapper,
                communityManageMenuConnectMapper);

        // 插入内容管理权限表
        CommonMethod.insertDetailMenu(communityManageId,
                communityId,
                CommunityManageMenuEnum.CONTENT_MANAGE,
                communityManageMenuMapper,
                communityManageMenuConnectMapper);

        // 插入频道权限表
        CommonMethod.insertDetailMenu(communityManageId,
                communityId,
                CommunityManageMenuEnum.CHANNEL_MANAGE,
                communityManageMenuMapper,
                communityManageMenuConnectMapper);

        // 插入信息管理权限表
        CommonMethod.insertDetailMenu(communityManageId,
                communityId,
                CommunityManageMenuEnum.INFORMATION_MANAGE,
                communityManageMenuMapper,
                communityManageMenuConnectMapper);
    }
}
