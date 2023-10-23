<template>
    <div class="MonkeyWebCommunityViews-container">
        <!-- <SearchCommunity
        v-if="inputFocus"
        @closeLabelWindow="closeLabelWindow"
        @selectTwoLabel="selectTwoLabel"/> -->
        <!-- 头部 -->
        <el-row>
            <el-col :span="17" style="height:260px">
                <el-carousel trigger="click" style="overflow: hidden;" height="260px">
                    <el-carousel-item v-for="item in 4" :key="item">
                        <img class="carousel-img" src="https://tse2-mm.cn.bing.net/th/id/OIP-C.Lf2u2dPln44gRiIC7IR3IwHaEK?w=318&h=180&c=7&r=0&o=5&dpr=1.3&pid=1.7" alt="">
                    </el-carousel-item>
                </el-carousel>
            </el-col>
            <el-col :span="7" style="padding-left: 20px; height: 260px;">
                <el-row class="create-community">
                    <div @click="toCreateCommunityViews()" style="padding: 15px;">
                        <span class="iconfont icon-shequ create-community-icon">&nbsp;&nbsp;</span>
                        <span class="create-community-content">创建社区</span>
                        <span class="arrow">></span>
                    </div>
                </el-row>

                <el-row class="school-community">
                    <div @click="toCommunityRank()" style="padding: 15px;">
                        <span class="iconfont icon-xuexiao create-community-icon">&nbsp;&nbsp;</span>
                        <span class="create-community-content">社区排行</span>
                        <span class="arrow">></span>
                    </div>
                </el-row>

                <el-row class="notice-community">
                    <div @click="toCommunityNoticeViews()" style="padding: 15px;">
                        <span class="iconfont icon-gonggao create-community-icon">&nbsp;&nbsp;</span>
                        <span class="create-community-content">社区通知</span>
                        <span class="arrow">></span>
                    </div>
                </el-row>

                <el-row class="manage-community">
                    <div @click="queryUserManageCommunity()" style="padding: 15px;">
                        <span class="iconfont icon-guanli create-community-icon">&nbsp;&nbsp;</span>
                        <span class="create-community-content">社区管理</span>
                        <span class="arrow">></span>
                    </div>
                </el-row>

                <el-dialog
                :visible.sync="isShowSelectCommunity"
                width="860px"
                style="padding: 0;"
                :before-close="closeCommunitySelect()">
                    <div class="manage-title">请选择您需要登录的社区</div>
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
                
            </el-col>
        </el-row>

        <!-- 中间内容 -->
        <el-row style="margin-top: 50px;">    
            <el-col :span="17">
                <el-tabs v-model="activeName" @tab-click="handleTagClick(activeName)">
                    <el-tab-pane label="热度最高" name="latestHire"></el-tab-pane>
                    <el-tab-pane label="最新发布" name="latestPublish"></el-tab-pane>
                    <el-tab-pane label="收藏最多" name="collect"></el-tab-pane>
                    <el-tab-pane label="游览最多" name="view"></el-tab-pane>
                    <el-tab-pane label="回复最多" name="reply"></el-tab-pane>
                    <el-tab-pane label="点赞最多" name="like"></el-tab-pane>
                    <el-tab-pane label="与我有关" name="withMe"></el-tab-pane>
                    <CommunityCard
                    :communityArticleList="communityArticleList"/>
                    <PagiNation
                    style="text-align: right;"
                    :totals="totals"
                    :currentPage="currentPage" 
                    :pageSize="pageSize" 
                    @handleCurrentChange = "handleCurrentChange"
                    @handleSizeChange="handleSizeChange"/>
                </el-tabs>
                
            </el-col>
            <el-col :span="7" style="padding: 0 20px;">
                <!-- <el-row >
                    <el-input 
                    @keydown.native="searchArticle(communityName, dynamicTags)"
                    type="text"
                    placeholder="输入想要搜索的社区名"
                    v-model="communityName">
                    
                    <div slot="append" class="search">
                        <el-button @click="searchArticle(communityName, dynamicTags)" size="mini"><i class="el-icon-search"> 搜索</i></el-button>
                        
                    </div>
                    <template slot="prepend">
                        <el-button 
                            type="primary" 
                            class="el-icon-circle-plus-outline add-label" 
                            @click="inputFocusShow(communityName, dynamicTags)"
                            size="mini">&nbsp;标签
                        </el-button>
                    </template>
                </el-input>
                    
                </el-row>
                    <el-row>
                        <el-tag
                            class="inner-tag"
                            :key="tag"
                            v-for="tag in dynamicTags"
                            closable
                            :disable-transitions="false"
                            @close="handleClose(tag)">
                            {{tag.name}}
                        </el-tag>
                    </el-row> -->

                    <el-tabs v-model="activeNameAside" class="inner-tabs" @tab-click="innerHandleClick()" style="margin-top: 10px;">
                        
                        <el-tab-pane label="热门社区" name="hire"></el-tab-pane>
                        <el-tab-pane label="最新社区" name="latest"></el-tab-pane>
                        <el-tab-pane label="我的社区" name="myCommunity"></el-tab-pane>
                        <el-row style="margin-bottom: 10px;" v-for="community in communityList" :key="community.id">
                            <el-col :span="4" style="overflow: hidden;">
                                <img class="aside-img" :src="community.photo" alt="" @click="toCommunityDetailViews(community.id)" >
                            </el-col>
                            <el-col :span="15" style="padding-left: 10px;">
                                <el-row 
                                    class="community-name-aside">
                                    <div @click="toCommunityDetailViews(community.id)">{{ community.name }}</div>
                                </el-row>

                                <el-row class="member-aside">
                                    {{ community.memberCount }} 成员
                                </el-row>
                            </el-col>
                            <el-col :span="5">
                                <el-button 
                                @click="toCommunityManageViews(community.id)"
                                v-if="activeNameAside != 'myCommunity' && community.isManager == '1'" 
                                size="mini" 
                                class="button-aside">管理</el-button>
                                <el-button 
                                v-else-if="activeNameAside != 'myCommunity' && community.isAdd == '0'" 
                                size="mini" 
                                disabled
                                class="button-aside">审核中</el-button>
                                <el-button v-else-if="activeNameAside != 'myCommunity' && community.isAdd == '1'" 
                                size="mini" 
                                class="button-aside"
                                @click="turnOutCommunity(community.id)">退出</el-button>
                                <el-button 
                                v-else-if="activeNameAside != 'myCommunity'" 
                                size="mini" 
                                class="button-aside"
                                @click="applicationAddCommunity(community)">加入</el-button>
                            </el-col>
                        </el-row>
                    </el-tabs>
            </el-col>
        </el-row>
    </div>
</template>

<script>
// import SearchCommunity from '@/components/community/SearchCommunity'
import PagiNation from '@/components/pagination/PagiNation.vue';
import CommunityCard from '@/components/community/CommunityCard'
import $ from 'jquery'
import CarouselMap from '@/components/carousel/CarouselMap'
import store from '@/store';
export default {
    name: 'MonkeyWebCommunityViews',
    components: {
        CarouselMap,
        CommunityCard,
        PagiNation,
        // SearchCommunity
    },
    data() {
        return {
            // 是否显示选择社区对话框
            isShowSelectCommunity: false,
            communityUrl: "http://localhost:80/monkey-community/community",
            activeName: "latestHire",
            currentPage: 1,
            pageSize: 10,
            totals: 0,
            dynamicTags: [],
            // 输入框是否聚焦
            // inputFocus: false,
            // 想要搜索的社区名
            communityName: "",
            // 旁边标签选则
            activeNameAside: "hire",
            // 社区文章列表
            communityArticleList: [],
            // 社区列表
            communityList: [],
            // 选择社区分页
            communityCurrentPage: 1,
            communityPageSize: 10,
            communityTotals: 0,
            // 用户管理的社区集合
            userManageCommunityList: [],
        };
    },
    watch: {

    },
    mounted() {
        this.$watch("dynamicTags", (newVal) => {
            if (newVal.length > 0) {
                this.isShowConfirmSearch = true;
            } else {
                this.isShowConfirmSearch = false;
            }
        })
    },

    created() {
        this.queryHireArticleList();
        this.queryHireCommunityList();
    },

    methods: {
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
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.isShowSelectCommunity = true
                        vue.communityTotals = response.data.total;
                        vue.userManageCommunityList = response.data.records;
                    } else {
                        vue.$modal.msgError(response.msg)
                    }
                }
            })
        },
        // 关闭社区选择
        closeCommunitySelect() {
            return true;
        },
        // 前往社区排行界面
        toCommunityRank() {
            const { href } = this.$router.resolve({
                name: "community_rank",
            })

            window.open(href, "_blank")
        },
        // 前往社区详情页面主页
        toCommunityDetailViews(communityId) {
            const { href } = this.$router.resolve({
                name: "community_detail",
                params: {
                    communityId,
                },
            })

            window.open(href, "_blank");
        },
        // 加入社区功能实现
        applicationAddCommunity(community) {
            const vue = this;
            $.ajax({
                url: vue.communityUrl + '/applicationAddCommunity',
                type: "post",
                data: {
                    community: JSON.stringify(community)
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        if (vue.activeNameAside == 'hire') {
                            vue.queryHireCommunityList();
                        } else if (vue.activeNameAside == 'latest') {
                            vue.queryLatestCommunityList();
                        }

                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 退出社区功能实现
        turnOutCommunity(communityId) {
            const vue = this;
            $.ajax({
                url: vue.communityUrl + '/turnOutCommunity',
                type: "delete",
                data: {
                    communityId
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        if (vue.activeNameAside == 'hire') {
                            vue.queryHireCommunityList();
                        } else if (vue.activeNameAside == 'latest') {
                            vue.queryLatestCommunityList();
                        }

                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 内部导航切换
        innerHandleClick() {
            if (this.activeNameAside == 'hire') {
                this.queryHireCommunityList();
            } else if (this.activeNameAside == 'latest') {
                this.queryLatestCommunityList();
            } else if (this.activeNameAside == 'myCommunity') {
                this.queryWithMeCommunityList();
            }
        },
        // 查询热门社区列表
        queryHireCommunityList() {
            const vue = this;
            $.ajax({
                url: vue.communityUrl + "/queryHireCommunityList",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.communityList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询最新社区列表
        queryLatestCommunityList() {
            const vue = this;
            $.ajax({
                url: vue.communityUrl + "/queryLatestCommunityList",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.communityList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询我的社区列表
        queryWithMeCommunityList() {
            const vue = this;
            $.ajax({
                url: vue.communityUrl + "/queryWithMeCommunityList",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.communityList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 得到点赞最多文章列表
        queryLikeArticleList() {
            const vue = this;
            $.ajax({
                url: vue.communityUrl + "/queryLikeArticleList",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.communityArticleList = response.data.records;
                        vue.totals = response.data.total;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 得到回复最多文章列表
        queryReplyArticleList() {
            const vue = this;
            $.ajax({
                url: vue.communityUrl + "/queryReplyArticleList",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.communityArticleList = response.data.records;
                        vue.totals = response.data.total;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 得到游览最多文章列表
        queryViewArticleList() {
            const vue = this;
            $.ajax({
                url: vue.communityUrl + "/queryViewArticleList",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.communityArticleList = response.data.records;
                        vue.totals = response.data.total;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 得到收藏最高文章列表
        queryCollectArticleList() {
            const vue = this;
            $.ajax({
                url: vue.communityUrl + "/queryCollectArticleList",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.communityArticleList = response.data.records;
                        vue.totals = response.data.total;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 得到最热文章列表
        queryHireArticleList() {
            const vue = this;
            $.ajax({
                url: vue.communityUrl + "/queryHireArticleList",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.communityArticleList = response.data.records;
                        vue.totals = response.data.total;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 得到最新文章列表
        queryLatestArticleList() {
            const vue = this;
            $.ajax({
                url: vue.communityUrl + "/queryLatestArticleList",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.communityArticleList = response.data.records;
                        vue.totals = response.data.total;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 得到与我有关社区文章列表
        queryWithMeArticleList() {
            const vue = this;
            $.ajax({
                url: vue.communityUrl + "/queryWithMeArticleList",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.communityArticleList = response.data.records;
                        vue.totals = response.data.total;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 前往创建社区页面
        toCreateCommunityViews() {
            const { href } = this.$router.resolve({
                name: "community_create",
            })

            window.open(href, "_blank");
        },
        // 前往高校社区页面
        toCollegeViews() {
            
        },
        // 前往社区通知页面
        toCommunityNoticeViews() {
            const { href } = this.$router.resolve({
                name: "community_detail",
                params: {
                    communityId: -1,
                }
            })
            window.open(href, "_blank")
        },
        // 前往社区管理页面
        toCommunityManageViews(communityId) {
            const vue = this;
            const { href } = this.$router.resolve({
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
        // // 根据条件搜索文章
        // searchArticle(communityName, tabList) {
        //     const vue = this;
        //     $.ajax({
        //         url: vue.communityUrl + '/searchArticle',
        //         type: "get",
        //         data: {
        //             communityName,
        //             tabList: JSON.stringify(tabList),
        //         },
        //         success(response) {
        //             if (response.code == vue.ResultStatus.SUCCESS) {
        //                 vue.communityArticleList = response.data.records;
        //                 vue.totals = response.data.total;
        //             } else {
        //                 vue.$modal.msgError(response.msg);
        //             }
        //         }
        //     })
        // },
        // 选中了二级标签
        selectTwoLabel(twoLabel) {
            this.dynamicTags.push(twoLabel);
        },
        // 关闭标签选择框
        closeLabelWindow() {
            this.inputFocus = false;
        },
        inputFocusShow() {
            this.inputFocus = true;
        },
        handleClose(tag) {
            this.dynamicTags.splice(this.dynamicTags.indexOf(tag), 1);
        },
        handleTagClick(name) {
            this.currentPage = 1;
            this.pageSize = 10;
            if (name == 'withMe') {
                // 得到与我有关社区文章列表
                this.queryWithMeArticleList();
            } else if (name == "latestHire") {
                this.queryHireArticleList();
            } else if (name == "latestPublish") {
                this.queryLatestArticleList();
            } else if (name == "collect") {
                this.queryCollectArticleList();
            } else if (name == "view") {
                this.queryViewArticleList();
            } else if (name == "reply") {
                this.queryReplyArticleList();
            } else if (name == "like") {
                this.queryLikeArticleList();
            }
        },
        handleSizeChange(val) {
            this.pageSize = val;
            if (this.activeName == 'withMe') {
                // 得到与我有关社区文章列表
                this.queryWithMeArticleList();
            } else if (this.activeName == "latestHire") {
                this.queryHireArticleList();
            } else if (this.activeName == "latestPublish") {
                this.queryLatestArticleList();
            } else if (this.activeName == "collect") {
                this.queryCollectArticleList();
            } else if (this.activeName == "view") {
                this.queryViewArticleList();
            } else if (this.activeName == "reply") {
                this.queryReplyArticleList();
            } else if (this.activeName == "like") {
                this.queryLikeArticleList();
            }
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            if (this.activeName == 'withMe') {
                // 得到与我有关社区文章列表
                this.queryWithMeArticleList();
            } else if (this.activeName == "latestHire") {
                this.queryHireArticleList();
            } else if (this.activeName == "latestPublish") {
                this.queryLatestArticleList();
            } else if (this.activeName == "collect") {
                this.queryCollectArticleList();
            } else if (this.activeName == "view") {
                this.queryViewArticleList();
            } else if (this.activeName == "reply") {
                this.queryReplyArticleList();
            } else if (this.activeName == "like") {
                this.queryLikeArticleList();
            }
        },

        communityHandleSizeChange(val) {
            this.communityPageSize = val;
            this.queryUserManageCommunity();
        },
        communityHandleCurrentChange(val) {
            this.communityCurrentPage = val;
            this.queryUserManageCommunity();
        },
    },
};
</script>

<style scoped>
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

.article-count {
    display: inline-block;
    margin-left: 30px;
}
.member-count {
    display: inline-block;
    margin-left: 10px;
}
.manage-img {
    display: inline-block;
    width: 60px;
    height: 60px;
    border-radius: 50%;
    vertical-align: top;
}
.manage-title {
    font-size: 20px;
    text-align: center;
}
.inner-tabs {
    border: #E7EBF1 1px solid;
    padding: 20px;
}
.member-aside {
    font-size: 12px;
    font-weight: 400;
    color: #555666;
    line-height: 20px;
}
.button-aside {
    color: #555666;
    background-color: #fff;
    border-radius: 12px;
    margin-left: 12px;
    border: 1px solid #ccccd8;
    width: 48px;
    overflow: hidden;
    -ms-flex-negative: 0;
    flex-shrink: 0;
    padding: 0;
    outline: none;
    -ms-flex-item-align: center;
    align-self: center;
    height: 24px;
    font-size: 12px;
    font-weight: 400;
    margin-top: 10px;
}
.community-name-aside {
    font-size: 14px;
    font-weight: 400;
    color: #222226;
    line-height: 22px;
    max-width: 100%;
    overflow: hidden;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
    text-overflow: ellipsis;
    white-space: nowrap;
    vertical-align: middle;
    cursor: pointer;
}
.community-name-aside:hover {
    opacity: 0.5;
}
.aside-img {
    width: 100%;
    height: 44px;
    cursor: pointer;
    transition: 0.2s linear all;
}
.aside-img:hover {
    transform: scale(1.4);
}
.search {
    cursor: pointer;
}
.add-label {
    margin: 10px 0;
    margin-right: 10px;
}
.inner-tag {
    margin: 10px 0px;
    margin-right: 10px;
}
.before-input {
    width: 100%;
    height: 100%;
    border: none;
    outline: none;
    border-radius: 19px;
    background-color: transparent;
    cursor: pointer;
}
.before-input:hover {
    color: #409EFF;
}

.arrow {
    position: absolute;
    right: 10%;
}
.create-community-content {
    font-size: 18px;
}
.create-community-icon {
    display: inline-block;
    font-size: 20px;
}
.school-community {
    position: relative;
    cursor: pointer;
    border-radius: 10px;
    margin-top: 10px;
    transition: 0.2s linear all;
    background-image: linear-gradient(to right, #fbdd9d 0%, #d4f4f6 100%);
}

.school-community:hover {
    opacity: 0.5;
}


.notice-community {
    position: relative;
    cursor: pointer;
    border-radius: 10px;
    margin-top: 10px;
    transition: 0.2s linear all;
    background-image: linear-gradient(to right, #c4e9fa 100%, #fff1eb 0%);
}
.notice-community:hover {
    opacity: 0.5;
}
.manage-community {
    position: relative;
    cursor: pointer;
    border-radius: 10px;
    margin-top: 10px;
    transition: 0.2s linear all;
    background-image: linear-gradient(to right, #cd9cf2 0%, #f6f3ff 100%);
}
.manage-community:hover {
    opacity: 0.5;
}
.create-community {
    position: relative;
    cursor: pointer;
    border-radius: 10px;
    transition: 0.2s linear all;
    background-image: linear-gradient(to right, #96fbc4 0%, #cef8e0 100%);
}
.create-community:hover {
    opacity: 0.5;
}
.carousel-img {
    width: 100%;
    height: 100%;
    cursor: pointer;
    transition: 0.4s linear all;
}
.carousel-img:hover{
    transform: scale(1.4);
}
.el-carousel__item h3 {
    color: #475669;
    font-size: 14px;
    opacity: 0.75;
    line-height: 150px;
    margin: 0;
}

.el-carousel__item:nth-child(2n) {
    background-color: #99a9bf;
}

.el-carousel__item:nth-child(2n+1) {
    background-color: #d3dce6;
}
.MonkeyWebCommunityViews-container {
    width: 1280px;
    background-color: #FFFFFF;
    margin: 0 auto;
    padding: 20px;
}
</style>