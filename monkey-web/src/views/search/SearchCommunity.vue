<template>
    <div class="MonkeyWebSearchCommunity-container">
        <el-tabs v-model="activeName" @tab-click="handleClick" >
            <el-tab-pane label="综合" name="comprehensive"></el-tab-pane>
            <el-tab-pane label="最新" name="latest"></el-tab-pane>
            <el-tab-pane label="最热" name="hire"></el-tab-pane>
            <el-tab-pane label="成员数" name="member"></el-tab-pane>
            <el-tab-pane label="文章数" name="article"></el-tab-pane>
            <div
            class="infinite-list" 
            v-infinite-scroll="loadData" 
            infinite-scroll-distance="30">
                <div 
                @click="toCommunityDetailViews(community.id)"
                v-for="community in communityList" :key="community.id" 
                style="cursor: pointer; position: relative;">
                    <img class="community-picture" :src="community.photo" alt="">
                    <div class="community-title" v-html="community.name">
                    </div>

                    <div class="community-content" v-html="community.description">
                    </div>
                    <div style="margin-bottom: 5px;">
                        <el-tag 
                        type="info"  
                        size="small" 
                        v-html="community.attributeLabelName"
                        style="margin-right: 10px;"></el-tag>
                        <el-tag 
                        type="info"  
                        size="small" 
                        v-html="community.classificationName"
                        style="margin-right: 10px;"></el-tag>
                        <el-tag 
                        type="info" 
                        size="small" 
                        style="margin-right: 10px;"
                        v-for="label in community.contentLabelName" :key="label"
                        v-html="label"></el-tag>
                    </div>
                    <div style="vertical-align: middle;">
                        <span class="iconfont icon-wenzhang operate-common">&nbsp;{{ getFormatNumber(community.articleCount) }}</span>
                        <span class="el-icon-user operate-common">&nbsp;{{ getFormatNumber(community.memberCount) }}</span>
                        
                    <span class="publishTime">{{ getTimeFormat(community.createTime) }}</span>
                    </div>
                    <div class="divisor"></div>
                </div>
            </div>

        <div
        v-if="communityList == null || communityList == '' || communityList == [] || communityList.length <= 0"
        style="text-align: center;" >
            <el-empty description="暂无数据"></el-empty>
        </div>
        </el-tabs>
    </div>
</template>

<script>
import $ from 'jquery'
import { getTimeFormat } from '@/assets/js/DateMethod';
import { getFormatNumber } from '@/assets/js/NumberMethod';
export default {
    name: 'MonkeyWebSearchCommunity',
    data() {
        return {
            isScroll: true,
            activeName: "comprehensive",
            currentPage: 1,
            pageSize: 10,
            // 关键词搜索字段
            keyword: this.$route.query.keyword,
            // 社区集合
            communityList: [],
            searchCommunityUrl: "http://localhost:80/monkey-search/community",
            communityUrl: "http://localhost:80/monkey-community/article",
        };
    },

    watch: {
        '$route.query.keyword'(newVal, oldVal) {
            this.keyword = newVal;
            this.currentPage = 1;
            this.communityList = [];
            if (this.activeName == 'comprehensive') {
                    this.queryComprehensiveCommunity();
                } else if (this.activeName == "latest") {
                    this.queryLatestCommunity();
                } else if (this.activeName == "hire") {
                    this.queryHireCommunity();
                } else if (this.activeName == "article") {
                    this.queryArticleCommunity();
                } else if (this.activeName == "member") {
                    this.queryMemberCommunity();
                } else if (this.activeName == "collect") {
                    this.queryCollectCommunity();
                } else if (this.activeName == "comment") {
                    this.queryCommentCommunity();
                } else if (this.activeName == 'member') {
                    this.queryMemberCommunity();
                } else if (this.activeName == 'score') {
                    this.queryScoreCommunity();
                }
        }
    },

    methods: {
        // 前往社区详情页面
        toCommunityDetailViews(communityId) {
            const { href } = this.$router.resolve({
                name: "community_detail",
                params: {
                    communityId
                }
            })

            window.open(href, "_blank")
        },
        // 前往用户主页
        toUserViews(userId) {
            const { href } = this.$router.resolve({
                name: "user_home",
                params: {
                    userId
                },
            })

            window.open(href, "_blank")
        },
        handleClick() {
            this.isScroll = false;
            this.currentPage = 1;
            this.communityList = [];
            if (this.activeName == 'comprehensive') {
                    this.queryComprehensiveCommunity();
                } else if (this.activeName == "latest") {
                    this.queryLatestCommunity();
                } else if (this.activeName == "hire") {
                    this.queryHireCommunity();
                } else if (this.activeName == "article") {
                    this.queryArticleCommunity();
                } else if (this.activeName == "member") {
                    this.queryMemberCommunity();
                } else if (this.activeName == "collect") {
                    this.queryCollectCommunity();
                } else if (this.activeName == "comment") {
                    this.queryCommentCommunity();
                } else if (this.activeName == 'member') {
                    this.queryMemberCommunity();
                } else if (this.activeName == 'score') {
                    this.queryScoreCommunity();
                }
        },
        getFormatNumber(val) {
            return getFormatNumber(val);
        },
        getTimeFormat(val) {
            return getTimeFormat(val);
        },
        loadData() {
            if (this.isScroll) {
                this.isScroll = false;
                if (this.activeName == 'comprehensive') {
                    this.queryComprehensiveCommunity();
                } else if (this.activeName == "latest") {
                    this.queryLatestCommunity();
                } else if (this.activeName == "hire") {
                    this.queryHireCommunity();
                } else if (this.activeName == "article") {
                    this.queryArticleCommunity();
                } else if (this.activeName == "member") {
                    this.queryMemberCommunity();
                } else if (this.activeName == "collect") {
                    this.queryCollectCommunity();
                } else if (this.activeName == "comment") {
                    this.queryCommentCommunity();
                } else if (this.activeName == 'score') {
                    this.queryScoreCommunity();
                }
            }
        },
        // 查询成员数最多社区列表
        queryMemberCommunity() {
            const vue = this;
            $.ajax({
                url: vue.searchCommunityUrl + "/queryMemberCommunity",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data;
                        for (let i = 0; i < data.length; i++) {
                            vue.communityList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询游览数最多社区列表
        queryArticleCommunity() {
            const vue = this;
            $.ajax({
                url: vue.searchCommunityUrl + "/queryArticleCommunity",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data;
                        for (let i = 0; i < data.length; i++) {
                            vue.communityList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询最热社区列表
        queryHireCommunity() {
            const vue = this;
            $.ajax({
                url: vue.searchCommunityUrl + "/queryHireCommunity",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data;
                        for (let i = 0; i < data.length; i++) {
                            vue.communityList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询最新社区列表
        queryLatestCommunity() {
            const vue = this;
            $.ajax({
                url: vue.searchCommunityUrl + "/queryLatestCommunity",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data;
                        for (let i = 0; i < data.length; i++) {
                            vue.communityList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询综合社区列表
        queryComprehensiveCommunity() {
            const vue = this;
            $.ajax({
                url: vue.searchCommunityUrl + "/queryComprehensiveCommunity",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data;
                        for (let i = 0; i < data.length; i++) {
                            vue.communityList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
    },
};
</script>

<style scoped>
.community-picture {
    position: absolute;
    right: 10px;
    top: 0;
    height: 100px;
    width: 150px;
    border-radius: 0;
    transition: 0.2s linear all;
}

.community-picture:hover {
    opacity: 0.8;
}
.divisor {
    background-color: rgba(0, 0, 0, 0.1);
    height: 1px;
    width: 100%;
    margin: 10px 0;
}
@keyframes slide-out {
    0% {
        opacity: 0;
    }
    100% {
        opacity: 1;
    }
}
::-webkit-scrollbar {
    width: 10px;
    background-color: #fff;
}

:hover ::-webkit-scrollbar-track-piece {
    background-color: #fff;
    border-radius: 6px;
}

:hover::-webkit-scrollbar-thumb:hover {
    background-color: rgba(0, 0, 0, 0.1);
}

:hover::-webkit-scrollbar-thumb:vertical {
    background-color: rgba(0, 0, 0, 0.1);
    border-radius: 6px;
    outline: 2px solid #fff;
    outline-offset: -2px;
    border: 2px solid #fff;
    
    transition: 0.4s linear all;
}
.operate-common {
    margin-right: 20px;
    font-size: 16px;
    color: gray;
}
.font-class {
    margin-bottom: 2px;
}
.community-content {
    display: inline-block;
    font-size: 14px;
    color: gray;
    margin-bottom: 10px;
    overflow: hidden;
    max-width: 600px;
    text-overflow: ellipsis;
    display: -webkit-box;
    /* 设置省略行 */
    -webkit-line-clamp: 2; 
    -webkit-box-orient: vertical;  
    transition: 0.2s linear all;
}
.community-content:hover {
    opacity: 0.8;
}
.community-title {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 10px;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    max-width: 600px;
    transition: 0.2s linear all;
    display: inline-block;
}
.community-title:hover {
    opacity: 0.8;
}

.card-right {
    padding-left: 15px;
    position: relative;
}
.brief {
    font-size: 14px;
    color: gray;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    max-width: 543px;
}
.publishTime {
    font-size: 14px;
    color: gray;
    margin-right: 10px;
}
.username {
    display: inline-block;
    margin-right: 10px;
    font-size: 14px;
    transition: 0.2s linear all;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    max-width: 450px;
}
.username:hover {
    color: #409EFF;
}
.MonkeyWebSearchCommunity-container {
    vertical-align: middle;
    animation: slide-out 0.4s linear;
}
.user-headImg {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    transition: 0.2s linear all;
}
.user-headImg:hover {
    opacity: 0.8;
    cursor: pointer;
}
::v-deep .el-tabs__header {
    margin: 0 0 10px 0;
}
.infinite-list {
    overflow:auto; 
    max-height: calc(100vh - 150px);
    border-radius: 10px;
}
::v-deep .el-tabs__nav-wrap::after {
    content: none;
    position: absolute;
    left: 0;
    width: 100%;
    height: 0;
    background-color: #fff;
    z-index: 1;
}
</style>