package com.monkey.monkeyUtils.constants;

/**
 * @author: wusihao
 * @date: 2023/11/21 10:00
 * @version: 1.0
 * @description: 社区管理权限常量
 */
public enum CommunityManageMenuEnum {
    USER_MANAGE("community:user:manage:communityId = ",
            "用户管理",
            "/community/manage/userManage",
            "UserManage",
            "iconfont icon-yonghuguanli"),

    ROLE_MANAGE("community:role:manage:communityId = ",
            "角色管理",
            "/community/manage/roleManage",
            "RoleManage",
            "iconfont icon-guanliyuan_jiaoseguanli"),

    USER_APPLICATION("community:user:application:communityId = ",
            "加入申请",
            "/community/manage/userApplication",
            "AddApplication",
            "iconfont icon-shenqingquanxian"),

    CONTENT_MANAGE("community:content:manage:communityId = ",
            "内容管理",
            "/community/manage/contentManage",
            "ContentManageViews",
            "iconfont icon-neirongguanli"),

    CHANNEL_MANAGE("community:channel:manage:communityId = ",
            "频道管理",
            "/community/manage/channelManage",
            "ChannelManage",
            "iconfont icon-pindaoguanli"),

    INFORMATION_MANAGE("community:information:manage:communityId = ",
            "信息管理",
            "/community/manage/informationManage",
            "CommunityInfoManage",
            "iconfont icon-icon_xinyong_xianxing_jijin-"),

    ADMINISTRATOR_CONFIG("community:administrator:config:communityId = ",
            "管理员配置",
            "/community/manage/administratorConfig",
            "AdminConfig",
            "iconfont icon-guanliyuan_guanliyuanliebiao"),

    ;

    private String perms;
    private String menuName;
    private String routerPath;
    private String component;
    private String menuIcon;

    CommunityManageMenuEnum(String perms, String menuName, String routerPath, String component, String menuIcon) {
        this.perms = perms;
        this.menuName = menuName;
        this.routerPath = routerPath;
        this.component = component;
        this.menuIcon = menuIcon;
    }

    public String getPerms() {
        return perms;
    }

    public void setPerms(String perms) {
        this.perms = perms;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getRouterPath() {
        return routerPath;
    }

    public void setRouterPath(String routerPath) {
        this.routerPath = routerPath;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getMenuIcon() {
        return menuIcon;
    }

    public void setMenuIcon(String menuIcon) {
        this.menuIcon = menuIcon;
    }
}
