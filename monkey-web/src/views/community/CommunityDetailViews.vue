<template>
    <div class="MonkeyWebCommunityDetailViews-container">
        <el-row style="display: flex;">
            <el-col :span="4" class="left-aside">
                <div style="padding: 20px 20px 10px 20px;">
                        <el-input v-model="queryCommunity" style="position: relative;" placeholder="搜索社区">
                            <template v-slot:suffix>
                                <i class="el-icon-search"></i>
                            </template>
                        </el-input>
                    <div class="divider"></div>

                    <div class="to-community-view">社区首页 <span style="float: right;">></span></div>
                </div>

                <div style="padding: 0 10px;">
                    <el-row>
                        <el-row class="my-add-community">
                            <div @click="myAddCommunity()">
                                <el-col :span="2">
                                    <span v-if="showCommunity == '1'" class="el-icon-caret-bottom">
                                    </span>
                                    <span v-else class="el-icon-caret-right"></span>
                                </el-col>
                                <el-col :span="22">
                                    <span class="el-icon-folder-opened">&nbsp;我加入的社区&nbsp;
                                        <span style="color: gray;">1</span>
                                    </span>
                                </el-col>   
                            </div>
                        </el-row>
                        <!-- 展示我加入的社区 -->
                            <el-row v-show="showCommunity == '1'" class="show-community-data">
                                <el-col :span="2">
                                    <img class="left-img" src="https://ts2.cn.mm.bing.net/th?id=OIP-C.th6FlIUU3ebI8pl4ccYHZgHaEo&w=316&h=197&c=8&rs=1&qlt=90&o=6&dpr=1.3&pid=3.1&rm=2" alt="">
                                </el-col>
                                <el-col :span="20" class="left-content">
                                    啥也不会hh啥也不会hh啥也不会hh啥也不会hh啥也不会hh啥也不会hh
                                </el-col>
                            </el-row>
                            <el-row v-if="showCommunity == '1'" class="show-community-data">
                                <el-col :span="2">
                                    <img class="left-img" src="https://ts2.cn.mm.bing.net/th?id=OIP-C.th6FlIUU3ebI8pl4ccYHZgHaEo&w=316&h=197&c=8&rs=1&qlt=90&o=6&dpr=1.3&pid=3.1&rm=2" alt="">
                                </el-col>
                                <el-col :span="20" class="left-content">
                                    啥也不会hh啥也不会hh啥也不会hh啥也不会hh啥也不会hh啥也不会hh
                                </el-col>
                            </el-row>
                            <el-row v-if="showCommunity == '1'" class="show-community-data">
                                <el-col :span="2">
                                    <img class="left-img" src="https://ts2.cn.mm.bing.net/th?id=OIP-C.th6FlIUU3ebI8pl4ccYHZgHaEo&w=316&h=197&c=8&rs=1&qlt=90&o=6&dpr=1.3&pid=3.1&rm=2" alt="">
                                </el-col>
                                <el-col :span="20" class="left-content">
                                    啥也不会hh啥也不会hh啥也不会hh啥也不会hh啥也不会hh啥也不会hh
                                </el-col>
                            </el-row>
                            <el-row v-if="showCommunity == '1'" class="show-community-data">
                                <el-col :span="2">
                                    <img class="left-img" src="https://ts2.cn.mm.bing.net/th?id=OIP-C.th6FlIUU3ebI8pl4ccYHZgHaEo&w=316&h=197&c=8&rs=1&qlt=90&o=6&dpr=1.3&pid=3.1&rm=2" alt="">
                                </el-col>
                                <el-col :span="20" class="left-content">
                                    啥也不会hh啥也不会hh啥也不会hh啥也不会hh啥也不会hh啥也不会hh
                                </el-col>
                            </el-row>
                    </el-row>

                    <el-row>
                        <el-row class="my-add-community">
                            <div @click="myManageCommunity()">
                                <el-col :span="2">
                                    <span v-if="showCommunity == '2'" class="el-icon-caret-bottom">
                                    </span>
                                    <span v-else class="el-icon-caret-right"></span>
                                </el-col>
                                <el-col :span="22">
                                    <span class="el-icon-folder-opened">&nbsp;我管理的社区&nbsp;
                                        <span style="color: gray;">1</span>
                                    </span>
                                </el-col>   
                            </div>
                        </el-row>
                        <!-- 展示我加入的社区 -->
                            <el-row v-if="showCommunity == '2'" class="show-community-data">
                                <el-col :span="2">
                                    <img class="left-img" src="https://ts2.cn.mm.bing.net/th?id=OIP-C.th6FlIUU3ebI8pl4ccYHZgHaEo&w=316&h=197&c=8&rs=1&qlt=90&o=6&dpr=1.3&pid=3.1&rm=2" alt="">
                                </el-col>
                                <el-col :span="20" class="left-content">
                                    啥也不会hh啥也不会hh啥也不会hh啥也不会hh啥也不会hh啥也不会hh
                                </el-col>
                            </el-row>
                    </el-row>

                    <el-row>
                        <el-row class="my-add-community">
                            <div @click="recommend()">
                                <el-col :span="2">
                                    <span v-if="showCommunity == '3'" class="el-icon-caret-bottom">
                                    </span>
                                    <span v-else class="el-icon-caret-right"></span>
                                </el-col>
                                <el-col :span="22">
                                    <span class="el-icon-folder-opened">&nbsp;官方推荐&nbsp;
                                        <span style="color: gray;">1</span></span>
                                </el-col>   
                            </div>
                        </el-row>
                        <!-- 展示我加入的社区 -->
                            <el-row v-if="showCommunity == '3'" class="show-community-data">
                                <el-col :span="2">
                                    <img class="left-img" src="https://ts2.cn.mm.bing.net/th?id=OIP-C.th6FlIUU3ebI8pl4ccYHZgHaEo&w=316&h=197&c=8&rs=1&qlt=90&o=6&dpr=1.3&pid=3.1&rm=2" alt="">
                                </el-col>
                                <el-col :span="20" class="left-content">
                                    啥也不会hh啥也不会hh啥也不会hh啥也不会hh啥也不会hh啥也不会hh
                                </el-col>
                            </el-row>
                    </el-row>


                </div>
            </el-col>
            <el-col :span="15" class="center-content">
                <div style="background-color: #fff; padding: 20px;">
                    <div class="article-content" @click="publishCommunityArticle(communityId)">
                        <span class="article-content-font">请您编写文章内容</span>
                        <el-button type="primary" class="publish-article" size="medium">发布文章</el-button>
                    </div>
                </div>

                <div class="community-channel">
                    <div style="font-size: 14px;">社区频道 7</div>
                    <div>
                        <el-tag class="study-channel" size="medium">标签一</el-tag>
                    </div>
                </div>

                <div class="community-content">
                    <el-input v-model="searchArticle" class="searchArticle"  placeholder="搜索社区内容">
                            <template v-slot:suffix>
                                <i class="el-icon-search"></i>
                            </template>
                        </el-input>
                    <el-tabs v-model="activeName" @tab-click="handleClick">
                        <el-tab-pane label="最新" name="latest">用户管理</el-tab-pane>
                        <el-tab-pane label="最热" name="second">配置管理</el-tab-pane>
                        <el-tab-pane label="评分" name="third">角色管理</el-tab-pane>
                        <el-tab-pane label="阅读量" name="fourth">定时任务补偿</el-tab-pane>
                        <el-tab-pane label="精选" name="fourth">定时任务补偿</el-tab-pane>
                        <el-tab-pane label="置顶" name="fourth">定时任务补偿</el-tab-pane>
                        <el-tab-pane label="点赞" name="fourth">定时任务补偿</el-tab-pane>
                        <el-tab-pane label="收藏" name="fourth">定时任务补偿</el-tab-pane>
                        <el-tab-pane label="我的文章" name="fourth">定时任务补偿</el-tab-pane>
                        
                        <CommunityDetailCard
                        :communityArticleList="communityArticleList"/>
                        <PagiNation
                        style="text-align: right;"
                        :totals="totals"
                        :currentPage="currentPage" 
                        :pageSize="pageSize" 
                        @handleCurrentChange = "handleCurrentChange"
                        @handleSizeChange="handleSizeChange"/>
                    </el-tabs>
                </div>
            </el-col>
            <el-col :span="5" style="display: flex; background-color: #fff;">
                <CommunityInfo
                style="padding: 10px;"/>
            </el-col>
        </el-row>
    </div>
</template>

<script>
import CommunityInfo from '@/components/community/CommunityInfo.vue';
import PagiNation from '@/components/pagination/PagiNation.vue';
import CommunityDetailCard from '@/components/community/CommunityDetailCard'
import $ from 'jquery'
import store from '@/store';
export default {
    name: 'MonkeyWebCommunityDetailViews',
    components: {
        CommunityDetailCard,
        PagiNation,
        CommunityInfo
    },
    data() {
        return {
            // 我加入的社区数
            myAddCommunityCount: "",
            // 我管理的社区数


            // 定义动态css对象 和 滚动条隐藏控制变量
            scrollBoxStyle: {
                "--backgroundColor": "rgba(187,255,170,0)",
            },
            scrollHideFlag: false,
            // 搜索文章内容
            searchArticle: "",
            // 选中的频道id
            selectedChannelId: "",
            // 社区id
            communityId: "",
            // 通过社区名搜索社区字段
            queryCommunity: "",
            // 点击展示的社区, 1为我加入的社区，2为我管理的社区，3为官方推荐
            showCommunity: "",
            currentPage: 1,
            pageSize: 10,
            totals: 0,
            // 社区文章列表
            communityArticleList: [],
            communityUrl: "http://localhost:80/monkey-community/community",
            communityDetailUrl: "http://localhost:80/monkey-community/community/detail",
        };
    },
    created() {
        this.communityId = this.$route.params.communityId;
        this.queryLatestArticleList();
    },
    methods: {
        // 得到我加入的社区数量
        queryMyAddCommunityCount() {
            const vue = this;
            $.ajax({
                url: vue.communityDetailUrl + '/queryMyAddCommunityCount',
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.myAddCommunityCount = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 得到我管理的社区数
        queryMyManageCommunityCount() {

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
                    if (response.code == '200') {
                        vue.communityArticleList = response.data.records;
                        vue.totals = response.data.total;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 发表社区文章
        publishCommunityArticle(communityId) {
            const { href } = this.$router.resolve({
                name: "publish_community_article",
                params: {
                    communityId,
                },
            })

        window.open(href, "_blank");
        },
        // 我加入的社区
        myAddCommunity() {
            if (this.showCommunity != 1) {
                this.showCommunity = 1;
            } else {
                this.showCommunity = -1;
            }
        },
        // 我管理的社区
        myManageCommunity() {
            if (this.showCommunity != 2) {
                this.showCommunity = 2;
            } else {
                this.showCommunity = -1;
            }
            
        },
        // 推荐社区
        recommend() {
            if (this.showCommunity != 3) {
                this.showCommunity = 3;
            } else {
                this.showCommunity = -1;
            }
            
        }
    },
};
</script>

<style scoped>

.searchArticle {
    position: absolute; 
    width: 22%; 
    right: 0; 
    z-index: 5;
}
.search-function {
    position: absolute;
    right: 10px;
    top: 22px;
    width: 24%;
    z-index: 2;
}
.community-channel {
    background-color: #fff; 
    margin-top: 10px; 
    padding: 20px;
}
.community-content {
    position: relative;
    background-color: #fff; 
    margin-top: 10px; 
    padding: 20px;
    height: 56vh;
    overflow-y: auto;
}
.study-channel {
    margin-right: 10px;
    margin-top: 10px;
}
.study-channel:hover {
    background-color: #409EFF;
    color: #fff;
    cursor: pointer;
}
.center-content {
    margin-right: 2px;
    position: relative;
}
.publish-article {
    position: absolute;
    right: 10px;
    background-color: #66B1FF;
    border-radius: 20px;
    top: 12px;
}
.article-content-font {
    color: #777888;
}
.article-content {
    padding: 15px;
    height: 26px;
    line-height: 26px;
    background-color: rgba(232,232,237,.8);
    border-radius: 8px;
    position: relative;
    cursor: pointer;
}
.article-content:hover {
    .publish-article {
        background-color: #409EFF;
    }
}
.left-content {
    line-height: 20px;
    padding-left: 10px;
    font-size: 14px;
    overflow: hidden;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
    text-overflow: ellipsis;
    white-space: nowrap;
    vertical-align: middle;
}
.show-community-data {
    font-size: 14px;
    padding: 5px 0px 5px 20px;
    width: 100%;
    margin-bottom: 10px;
    margin-top: 10px;
}
.show-community-data:hover {
    cursor: pointer;
    background-color: #F5F6F7;
}
.left-img {
    height: 20px;
    width: 20px;
    cursor: pointer;
}
@keyframes slow-arrow {
    0% {
        opacity: 0;
    } 
    100% {
        opacity: 1;
    }
}
.el-icon-caret-right {
    animation: slow-arrow 0.4s linear;
}
.el-icon-folder-opened {
    animation: slow-arrow 0.4s linear;
}
.selected-community-label {
    cursor: pointer;
    background-color: #F5F6F7;
}
.my-add-community {
    font-size: 14px;
    padding: 5px 10px 5px 10px;
    width: 100%;
}
.my-add-community:hover {
    cursor: pointer;
    background-color: #F5F6F7;
}
.to-community-view {
    font-size: 20px;
    cursor: pointer;
}
.to-community-view:hover {
    opacity: 0.5;
}
.left-aside {
    position: relative;
    background-color: #fff;
    margin-right: 2px;
    max-width: 100%;
    height: 90vh;
    overflow-y: auto;
}
.divider {
    height: 1px;
    background: #e8e8ed;
    margin: 20px 0;
}
.MonkeyWebCommunityDetailViews-container {
    margin: 0 auto;
    width: 1400px;
}
.el-icon-search {
    position: absolute;
    top: 30%;
    right: 0;
    font-size: 16px;
}
.el-icon-search:hover {
    cursor: pointer;
    color: #409EFF;
}
    
</style>