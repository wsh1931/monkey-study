<template>
    <div class="MonkeyWebSearchCommunityArticle-container">
        <el-tabs v-model="activeName" @tab-click="handleClick" >
            <el-tab-pane label="综合" name="comprehensive"></el-tab-pane>
            <el-tab-pane label="最新" name="latest"></el-tab-pane>
            <el-tab-pane label="最热" name="hire"></el-tab-pane>
            <el-tab-pane label="点赞" name="like"></el-tab-pane>
            <el-tab-pane label="游览" name="view"></el-tab-pane>
            <el-tab-pane label="收藏" name="collect"></el-tab-pane>
            <el-tab-pane label="评论" name="comment"></el-tab-pane>
            <el-tab-pane label="评分" name="score"></el-tab-pane>
            <div
            class="infinite-list" 
            v-infinite-scroll="loadData" 
            infinite-scroll-distance="30">
                <div 
                @click="toCommunityArticleComment(communityArticle.id, communityArticle.communityId)"
                v-for="communityArticle in communityArticleList" :key="communityArticle.id" 
                style="cursor: pointer;">
                    <el-row style="margin-bottom: 5px;">
                        <el-col :span="1">
                            <img @click="toUserViews(communityArticle.userId)" class="user-headImg" :src="communityArticle.userHeadImg" alt="">
                        </el-col>
                        <el-col :span="23" class="card-right">
                            <img class="communityArticle-picture" :src="communityArticle.picture" alt="">
                            <div class="font-class">
                                <span @click="toUserViews(communityArticle.userId)" class="username">{{ communityArticle.username }}</span>
                                <span class="publishTime">{{ getTimeFormat(communityArticle.createTime) }}</span>
                                <span 
                                class="community" 
                                @click="toCommunityDetailViews(communityArticle.communityId)">来自：{{ communityArticle.communityName }}</span>
                            </div>
                            <div class="brief">{{ communityArticle.userBrief }}</div>
                        </el-col>
                    </el-row>

                    <div class="communityArticle-title" v-html="communityArticle.title">
                    </div>

                    <div class="communityArticle-content" v-html="communityArticle.brief">
                    </div>
                    <div style="vertical-align: middle;">
                        <span class="iconfont icon-yanjing operate-common">&nbsp;{{ getFormatNumber(communityArticle.viewCount) }}</span>
                        <span class="iconfont icon-dianzan operate-common">&nbsp;{{ getFormatNumber(communityArticle.likeCount) }}</span>
                        <span class="iconfont icon-shoucang operate-common">&nbsp;{{ getFormatNumber(communityArticle.collectCount) }}</span>
                        <span class="iconfont icon-pinglun operate-common">&nbsp;{{ getFormatNumber(communityArticle.commentCount) }}</span>
                        <span class="iconfont icon-shoucang operate-common">&nbsp;{{ communityArticle.score }}</span>
                        <el-tag 
                        type="info" 
                        size="small" 
                        style="margin-right: 10px;"
                        v-for="label in communityArticle.labelName" :key="label"
                        v-html="label"></el-tag>

                    </div>
                    <div class="divisor"></div>
                </div>
            </div>

        <div
        v-if="communityArticleList == null || communityArticleList == '' || communityArticleList == [] || communityArticleList.length <= 0"
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
    name: 'MonkeyWebSearchCommunityArticle',

    data() {
        return {
            isScroll: true,
            activeName: "comprehensive",
            currentPage: 1,
            pageSize: 10,
            totals: 0,
            // 关键词搜索字段
            keyword: this.$route.query.keyword,
            // 社区文章集合
            communityArticleList: [],
            searchCommunityArticleUrl: "http://localhost:80/monkey-search/community/article",
            communityArticleUrl: "http://localhost:80/monkey-community/article",
        };
    },
    watch: {
        '$route.query.keyword'(newVal, oldVal) {
            this.keyword = newVal;
            this.currentPage = 1;
            this.communityArticleList = [];
            if (this.activeName == 'comprehensive') {
                    this.queryComprehensiveCommunityArticle();
                } else if (this.activeName == "latest") {
                    this.queryLatestCommunityArticle();
                } else if (this.activeName == "hire") {
                    this.queryHireCommunityArticle();
                } else if (this.activeName == "view") {
                    this.queryViewCommunityArticle();
                } else if (this.activeName == "like") {
                    this.queryLikeCommunityArticle();
                } else if (this.activeName == "collect") {
                    this.queryCollectCommunityArticle();
                } else if (this.activeName == "comment") {
                    this.queryCommentCommunityArticle();
                } else if (this.activeName == 'like') {
                    this.queryLikeCommunityArticle();
                } else if (this.activeName == 'score') {
                    this.queryScoreCommunityArticle();
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
        // 跳转至社区文章详情界面
        toCommunityArticleComment(communityArticleId, communityId) {
            const { href } = this.$router.resolve({
                name: "community_article",
                params: {
                    communityArticleId,
                    communityId,
                }
            })
            window.open(href, "_blank");
        },
        handleClick() {
            this.isScroll = false;
            this.currentPage = 1;
            this.communityArticleList = [];
            if (this.activeName == 'comprehensive') {
                    this.queryComprehensiveCommunityArticle();
                } else if (this.activeName == "latest") {
                    this.queryLatestCommunityArticle();
                } else if (this.activeName == "hire") {
                    this.queryHireCommunityArticle();
                } else if (this.activeName == "view") {
                    this.queryViewCommunityArticle();
                } else if (this.activeName == "like") {
                    this.queryLikeCommunityArticle();
                } else if (this.activeName == "collect") {
                    this.queryCollectCommunityArticle();
                } else if (this.activeName == "comment") {
                    this.queryCommentCommunityArticle();
                } else if (this.activeName == 'like') {
                    this.queryLikeCommunityArticle();
                } else if (this.activeName == 'score') {
                    this.queryScoreCommunityArticle();
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
                if ((this.currentPage - 1) * this.pageSize > this.totals) {
                    this.$modal.msgWarning("没有更多了");
                    return false;
                }
                if (this.activeName == 'comprehensive') {
                    this.queryComprehensiveCommunityArticle();
                } else if (this.activeName == "latest") {
                    this.queryLatestCommunityArticle();
                } else if (this.activeName == "hire") {
                    this.queryHireCommunityArticle();
                } else if (this.activeName == "view") {
                    this.queryViewCommunityArticle();
                } else if (this.activeName == "like") {
                    this.queryLikeCommunityArticle();
                } else if (this.activeName == "collect") {
                    this.queryCollectCommunityArticle();
                } else if (this.activeName == "comment") {
                    this.queryCommentCommunityArticle();
                } else if (this.activeName == 'score') {
                    this.queryScoreCommunityArticle();
                }
            }
        },
        // 查询社区文章评分最高的社区文章列表
        queryScoreCommunityArticle() {
            const vue = this;
            $.ajax({
                url: vue.searchCommunityArticleUrl + "/queryScoreCommunityArticle",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data.esCommunityArticleIndexList;
                        vue.totals = response.data.totals;
                        for (let i = 0; i < data.length; i++) {
                            vue.communityArticleList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询学习人数最多社区文章列表
        queryLikeCommunityArticle() {
            const vue = this;
            $.ajax({
                url: vue.searchCommunityArticleUrl + "/queryLikeCommunityArticle",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data.esCommunityArticleIndexList;
                        vue.totals = response.data.totals;
                        for (let i = 0; i < data.length; i++) {
                            vue.communityArticleList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询评论最多社区文章列表
        queryCommentCommunityArticle() {
            const vue = this;
            $.ajax({
                url: vue.searchCommunityArticleUrl + "/queryCommentCommunityArticle",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data.esCommunityArticleIndexList;
                        vue.totals = response.data.totals;
                        for (let i = 0; i < data.length; i++) {
                            vue.communityArticleList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询收藏数最多社区文章列表
        queryCollectCommunityArticle() {
            const vue = this;
            $.ajax({
                url: vue.searchCommunityArticleUrl + "/queryCollectCommunityArticle",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data.esCommunityArticleIndexList;
                        vue.totals = response.data.totals;
                        for (let i = 0; i < data.length; i++) {
                            vue.communityArticleList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询点赞数最多社区文章列表
        queryLikeCommunityArticle() {
            const vue = this;
            $.ajax({
                url: vue.searchCommunityArticleUrl + "/queryLikeCommunityArticle",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data.esCommunityArticleIndexList;
                        vue.totals = response.data.totals;
                        for (let i = 0; i < data.length; i++) {
                            vue.communityArticleList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询游览数最多社区文章列表
        queryViewCommunityArticle() {
            const vue = this;
            $.ajax({
                url: vue.searchCommunityArticleUrl + "/queryViewCommunityArticle",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data.esCommunityArticleIndexList;
                        vue.totals = response.data.totals;
                        for (let i = 0; i < data.length; i++) {
                            vue.communityArticleList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询最热社区文章列表
        queryHireCommunityArticle() {
            const vue = this;
            $.ajax({
                url: vue.searchCommunityArticleUrl + "/queryHireCommunityArticle",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data.esCommunityArticleIndexList;
                        vue.totals = response.data.totals;
                        for (let i = 0; i < data.length; i++) {
                            vue.communityArticleList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询最新社区文章列表
        queryLatestCommunityArticle() {
            const vue = this;
            $.ajax({
                url: vue.searchCommunityArticleUrl + "/queryLatestCommunityArticle",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data.esCommunityArticleIndexList;
                        vue.totals = response.data.totals;
                        for (let i = 0; i < data.length; i++) {
                            vue.communityArticleList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询综合社区文章列表
        queryComprehensiveCommunityArticle() {
            const vue = this;
            $.ajax({
                url: vue.searchCommunityArticleUrl + "/queryComprehensiveCommunityArticle",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data.esCommunityArticleIndexList;
                        vue.totals = response.data.totals;
                        for (let i = 0; i < data.length; i++) {
                            vue.communityArticleList.push(data[i]);
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
    margin-right: 16px;
    font-size: 16px;
    color: gray;
}
.font-class {
    margin-bottom: 2px;
}
.communityArticle-content {
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
.communityArticle-content:hover {
    opacity: 0.8;
}
.communityArticle-title {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 10px;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    max-width: 600px;
    transition: 0.2s linear all;
}
.communityArticle-title:hover {
    opacity: 0.8;
}
.communityArticle-picture {
    position: absolute;
    right: 10px;
    top: 0;
    height: 100px;
    width: 150px;
    border-radius: 0;
    transition: 0.2s linear all;
}

.communityArticle-picture:hover {
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
.community {
    margin-right: 10px;
    font-size: 14px;
    transition: 0.2s linear all;
    color: gray;
}
.community:hover {
    color: #409EFF;
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
.MonkeyWebSearchCommunityArticle-container {
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