<template>
    <div class="MonkeyManageViews-container">
        <el-row>
            <el-col :span="upholdLeftCol">
                <el-menu 
                :router="true"
                :default-active="defaultActive" 
                class="el-menu-vertical-demo" 
                :collapse="isCollapse"
                background-color="#545c64"
                text-color="#fff"
                @select="handleSelect"
                active-text-color="#ffd04b">
                <div v-if="!isCollapse" class="manage-title">花果山社区管理中心</div>

                <el-submenu index="user">
                    <template slot="title">
                        <i class="el-icon-location"></i>
                        <span slot="title" >用户</span>
                    </template>
                    <el-menu-item-group>
                    <el-menu-item index="manage_user" :route="`/community/manage/${communityId}/userManage?event=to_community_manage`">
                        <i class="el-icon-switch-button"></i>
                        <span>用户管理</span>
                    </el-menu-item>
                    <el-menu-item index="role_manage" :route="`/community/manage/${communityId}/roleManage?event=to_community_manage`">
                        <i class="el-icon-switch-button"></i>
                        <span>角色管理</span>
                    </el-menu-item>
                    <el-menu-item index="user_application" :route="`/community/manage/${communityId}/userApplication?event=to_community_manage`">
                        <i class="el-icon-switch-button"></i>
                        <span>加入申请</span>
                    </el-menu-item>
                    </el-menu-item-group>
                </el-submenu>

                <el-submenu index="content">
                    <template slot="title">
                        <i class="el-icon-location"></i>
                        <span slot="title">内容</span>
                    </template>
                    <el-menu-item-group>
                    <el-menu-item index="content_manage" :route="`/community/manage/${communityId}/contentManage?event=to_community_manage`">
                        <i class="el-icon-switch-button"></i>
                        <span>内容管理</span>
                    </el-menu-item>
                    <el-menu-item index="content_inclusion" :route="`/community/manage/${communityId}/contentInclusion?event=to_community_manage`">
                        <i class="el-icon-switch-button"></i>
                        <span>内容收录</span>
                    </el-menu-item>
                    </el-menu-item-group>
                </el-submenu>

                <el-submenu index="function">
                    <template slot="title">
                        <i class="el-icon-location"></i>
                        <span slot="title">功能配置</span>
                    </template>
                    <el-menu-item-group>
                    <el-menu-item index="channel_manage" :route="`/community/manage/${communityId}/channelManage?event=to_community_manage`">
                        <i class="el-icon-switch-button"></i>
                        <span>频道管理</span>
                    </el-menu-item>
                    <el-menu-item index="information_manage" :route="`/community/manage/${communityId}/informationManage?event=to_community_manage`">
                        <i class="el-icon-switch-button"></i>
                        <span>信息管理</span>
                    </el-menu-item>
                    </el-menu-item-group>
                </el-submenu>

                <el-submenu index="administrator">
                    <template slot="title">
                        <i class="el-icon-location"></i>
                        <span slot="title">管理员</span>
                    </template>
                    <el-menu-item-group>
                    <el-menu-item index="administrator_config" :route="`/community/manage/${communityId}/administratorConfig?event=to_community_manage`">
                        <i class="el-icon-switch-button"></i>
                        <span>管理员配置</span>
                    </el-menu-item>
                    </el-menu-item-group>
                </el-submenu>
                </el-menu>
            </el-col>
            <el-col :span="unHoldRightCol" class="change-style">
                <div class="right-top">
                    <span v-if="isCollapse" @click="isCollapse = false" class="el-icon-s-unfold fold-or-unfold"></span>
                    <span v-if="!isCollapse" @click="isCollapse = true" class="el-icon-s-fold fold-or-unfold"></span>
                    <el-breadcrumb separator-class="el-icon-arrow-right" class="breadcrumb">
                        <el-breadcrumb-item 
                        v-for="breadcrumb in breadcrumbList" 
                        :key="breadcrumb.name">{{ breadcrumb.name }}</el-breadcrumb-item>
                    </el-breadcrumb>

                <el-dropdown class="community-img" @command="handleCommend">
                    <span class="el-dropdown-link">
                        更多操作
                    </span>
                    <el-dropdown-menu slot="dropdown" style="vertical-align: middle;">
                        <el-dropdown-item command="communityViews">
                            <i class="el-icon-house"></i>
                            <span>社区主页</span>
                        </el-dropdown-item>
                        <el-dropdown-item command="communityDetail">
                            <i class="el-icon-time"></i>
                            <span>访问社区</span>
                        </el-dropdown-item>
                        <el-dropdown-item command="swapCommunity">
                            <i class="el-icon-refresh"></i>
                            <span>切换社区</span>
                        </el-dropdown-item>
                    </el-dropdown-menu>
                </el-dropdown>
                <!-- 切换社区集合 -->
                        <el-dialog
                        :visible.sync="isShowSelectCommunity"
                        width="860px"
                        style="padding: 0;"
                        :before-close="closeCommunitySelect()">
                            <div class="community-title">请选择您需要登录的社区</div>
                            <div style="margin-top: 20px;">
                                <div 
                                class="manage-card" 
                                v-for="userManageCommunity in userManageCommunityList" 
                                :key="userManageCommunity.id"
                                @click="toCommunityManageViews(userManageCommunity.id)">
                                    <img class="manage-img" :src="userManageCommunity.photo" alt="">
                                    <span style="display: inline-block; vertical-align: middle;">
                                        <span class="community-name">{{ userManageCommunity.name }}</span>
                                        <br>
                                        <span class="member-count el-icon-user">&nbsp;{{ userManageCommunity.memberCount }}</span>
                                        <span class="article-count el-icon-notebook-2">&nbsp;{{ userManageCommunity.articleCount }}</span>
                                    </span>
                                </div>

                                <PagiNation
                                small
                                style="text-align: right; margin-top: 10px;"
                                :totals="communityTotals"
                                :currentPage="communityCurrentPage" 
                                :pageSize="communityPageSize" 
                                @handleCurrentChange = "communityHandleCurrentChange"
                                @handleSizeChange="communityHandleSizeChange"/>
                            </div>
                            <span slot="footer" class="dialog-footer">
                                <el-button @click="isShowSelectCommunity = false">取 消</el-button>
                                <el-button type="primary" @click="isShowSelectCommunity = false">确 定</el-button>
                            </span>
                        </el-dialog>
                </div>
                <!-- 内容区导航栏 -->
                <router-view
                class="router-style"
                style="margin: 10px 0;"/>
                
            </el-col>
        </el-row>
    </div>
</template>

<script>
import $ from 'jquery'
import store from '@/store';
export default {
    name: 'MonkeyManageViews',

    data() {
        return {
            // 刷新后默认激活导航
            defaultActive: "",
            // 面包屑集合
            breadcrumbList: [],
            // 是否折叠菜单
            isCollapse: false,
            // 社区id
            communityId: "",
            // 左侧列宽度
            upholdLeftCol: 4,
            // 右侧列宽度
            unHoldRightCol: 20,
            // 是否显示社区集合
            isShowSelectCommunity: false,
            communityUrl: "http://localhost:80/monkey-community/community",
            // 选择社区分页
            communityCurrentPage: 1,
            communityPageSize: 10,
            communityTotals: 0,
            // 用户管理的社区集合
            userManageCommunityList: [],
        };
    },

    created() {
        this.communityId = this.$route.params.communityId;
        this.defaultActive = this.$route.name;
        const name = this.$route.name;
        if (name == 'manage_user') {
            this.breadcrumbList.push({ name: "用户" });
        } else if (name == 'role_manage') {
            this.breadcrumbList.push({ name: "用户" });
        } else if (name == 'user_application') {
            this.breadcrumbList.push({ name: "用户" });
        } else if (name == 'content_manage') {
            this.breadcrumbList.push({ name: "内容" });
        } else if (name == 'content_inclusion') {
            this.breadcrumbList.push({ name: "内容" });
        } else if (name == 'channel_manage') {
            this.breadcrumbList.push({name: "功能配置"});
        } else if (name == 'information_manage') {
            this.breadcrumbList.push({name: "功能配置"});
        } else if (name == 'administrator_config') {
            this.breadcrumbList.push({ name: "管理员" });
        }
        this.breadcrumbList.push({ name: this.$route.meta.title });
    },

    watch: {
        $route() {
            this.defaultActive = this.$route.name;
        },
        isCollapse(newVal) {
            if (newVal) {
                this.upholdLeftCol = 1;
                this.unHoldRightCol = 23;
            } else {
                this.upholdLeftCol = 4;
                this.unHoldRightCol = 20;
            }
        },
        unHoldRightCol(newVal) {
            return newVal
        },
    },

    methods: {
        communityHandleSizeChange(val) {
            this.communityPageSize = val;
            this.queryUserManageCommunity();
        },
        communityHandleCurrentChange(val) {
            this.communityCurrentPage = val;
            this.queryUserManageCommunity();
        },
        // 前往社区管理页面
        toCommunityManageViews(communityId) {
            const vue = this;
            const { href } = this.$router.push({
                name: "manage_user",
                params: {
                    communityId,
                },
                query: {
                    event: "to_community_manage",
                }
            })

            window.open(href, "_blank")
        },
        // 关闭社区选择
        closeCommunitySelect() {
            return true;
        },
        // 查询用户管理的社区集合
        queryUserManageCommunity() {
            const vue = this;
            $.ajax({
                url: vue.communityUrl + "/queryUserManageCommunity",
                type: "get",
                data: {
                    currentPage: vue.communityCurrentPage,
                    pageSize: vue.communityPageSize,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.isShowSelectCommunity = true
                        vue.communityTotals = response.data.total;
                        vue.userManageCommunityList = response.data.records;
                    } else {
                        vue.$modal.msgError(response.msg)
                    }
                }
            })
        },
        handleCommend(command) {
            if (command == "communityViews") {
                this.toCommunityViews();
                // 前往社区主页
            } else if (command == 'communityDetail') {
                // 前往社区详情
                this.toCommunityDetailViews(this.communityId);
            } else if (command == "swapCommunity") {
                // 切换社区
                this.queryUserManageCommunity();
            }
        },
        // 前往社区详情
        toCommunityDetailViews(communityId) {
            this.$router.push({
                name: "community_detail",
                params: {
                    communityId
                },
            })
        },
        // 前往社区主页
        toCommunityViews() {
            this.$router.push({
                name: "community",
            })
        },
        // 点击首页触发事件
        handleSelect(index, path) {
            this.breadcrumbList = [];
            if (path[0] == 'user') {
                this.breadcrumbList.push({ name: "用户" });
                if (path[1] == 'manage_user') {
                    this.breadcrumbList.push({ name: "用户管理" });
                } else if (path[1] == 'role_manage') {
                    this.breadcrumbList.push({ name: "角色管理" });
                } else if (path[1] == 'user_application') {
                    this.breadcrumbList.push({ name: "用户申请" });
                }
            } else if (path[0] == 'content') {
                this.breadcrumbList.push({ name: "内容" });
                if (path[1] == 'content_manage') {
                    this.breadcrumbList.push({ name: "内容管理" });
                } else if (path[1] == 'content_inclusion') {
                    this.breadcrumbList.push({ name: "内容收录" });
                }
            } else if (path[0] == 'function') {
                this.breadcrumbList.push({name: "功能配置"});
                if (path[1] == 'channel_manage') {
                    this.breadcrumbList.push({ name: "频道管理" });
                } else if (path[1] == 'information_manage') {
                    this.breadcrumbList.push({ name: "信息管理" });
                }
            } else if (path[0] == 'administrator') {
                this.breadcrumbList.push({ name: "管理员" });
                if (path[1] == 'administrator_config') {
                    this.breadcrumbList.push({ name: "管理员配置" });
                }
            }
        }
    },
};
</script>

<style scoped>
.article-count {
    display: inline-block;
    margin-left: 30px;
}
.member-count {
    display: inline-block;
    margin-left: 10px;
}
.community-name {
    display: inline-block;
    margin-bottom: 20px;
    margin-left: 10px;
    overflow: hidden;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
    text-overflow: ellipsis;
    white-space: nowrap;
    vertical-align: middle;
    max-width: 150px;
}
.manage-img {
    display: inline-block;
    width: 60px;
    height: 60px;
    border-radius: 50%;
    vertical-align: top;
}
.manage-card:hover {
    box-shadow: 0 0 5px 0 rgba(0, 0, 0, 0.5);
    cursor: pointer;
}
.manage-card:nth-child(3n + 3) {
    margin-right: 0;
}
.manage-card:nth-child(n + 2) {
    margin-bottom: 20px;
}
.manage-card {
    display: inline-block;
    padding: 10px;
    box-shadow: 0 0 1px 0 rgba(0, 0, 0, 0.5);
    width: 240px;
    margin-right: 20px;
    transition: 0.3s linear all;
}
.community-title {
    font-size: 20px;
    text-align: center;
}
.router-style {
    animation: turn-out 0.4s linear;
}
@keyframes turn-out {
    0% {
        opacity: 0;
    }
    100% {
        opacity: 1;
    }
}
.change-style {
    height: 90vh;
    overflow-y: auto;
}
.el-menu-vertical-demo {
    width: 100%;
    height: 100%;
}
::v-deep .el-menu-item-group__title {
    padding: 0 !important;
}
.manage-title {
    color: white;
    font-weight: 600;
    text-align: center;
    vertical-align: middle;
    padding: 10px;
}
.el-dropdown-link {
    cursor: pointer;
    color: #409EFF;
    font-size: 12px;
}
.el-icon-arrow-down {
    font-size: 12px;
}
.community-img {
    position: absolute;
    right: 20px;
    top: 5px;
    display: inline-block;
}
.breadcrumb {
    display: inline-block;
    margin-left: 10px;
    font-size: 12px;
    vertical-align: middle;
}
.fold-or-unfold:hover {
    color: #409EFF;
}
.right-top {
    position: relative;
    padding: 5px 10px;
    background-color: #fff;
    vertical-align: middle;
    transition: 0.3s linear all;
}
.right-top:hover {
    box-shadow: 0 0 5px rgba(0, 0, 0, 0.5);
}
.fold-or-unfold {
    font-size: 16px;
    cursor: pointer;
    vertical-align: middle;
}
.el-menu-vertical-demo:not(.el-menu--collapse) {
    width: 100%;
    height: 91vh;
}
.MonkeyManageViews-container {
    width: 100%;
    margin: 0 auto;
}
</style>