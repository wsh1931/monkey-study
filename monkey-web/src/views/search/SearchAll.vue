<template>
    <div class="MonkeyWebSearchAll-container">
        <el-tabs v-model="activeName" @tab-click="handleClick" >
            <el-tab-pane label="综合" name="comprehensive"></el-tab-pane>
            <el-tab-pane label="最新" name="latest"></el-tab-pane>
            <el-tab-pane label="最热" name="hire"></el-tab-pane>
            <el-tab-pane label="游览" name="view"></el-tab-pane>
            <el-tab-pane label="点赞" name="like"></el-tab-pane>
            <el-tab-pane label="收藏" name="collect"></el-tab-pane>
            <el-tab-pane label="评论" name="comment"></el-tab-pane>

            <div
            class="infinite-list" 
            v-infinite-scroll="loadData" 
            infinite-scroll-distance="30">
                <div 
                @click="toAllViews(all.type, all.associationId, all.communityId)"
                v-for="all in allList" :key="all.id" 
                style="cursor: pointer;">
                    <el-row style="margin-bottom: 5px;">
                        <el-col :span="1">
                            <img @click="toUserViews(all.userId)" class="user-headImg" :src="all.userHeadImg" alt="">
                        </el-col>
                        <el-col :span="23" class="card-right">
                            <img class="all-picture" :src="all.photo" alt="">
                            <div class="font-class">
                                <span @click="toUserViews(all.userId)" class="username">{{ all.username }}</span>
                                <span class="publishTime">{{ getTimeFormat(all.createTime) }}</span>
                                <span 
                                @click="toCommunityDetailViews(all.communityId)" 
                                class="community" 
                                v-if="all.type == SearchType.COMMUNITY_ARTICLE">来自：{{ all.communityName }}</span>
                                <span v-if="all.formTypeName != null && all.formTypeName != ''">
                                    <span v-if="all.formTypeName ==  formType.getMsg(1)" class="formType">{{ all.formTypeName }}</span>
                                    <span v-else-if="all.formTypeName ==  formType.getMsg(3)" class="formType-fee">{{ all.formTypeName }}</span>
                                    <span v-else-if="all.formTypeName ==  formType.getMsg(2)" class="formType-fee">{{ all.formTypeName }}</span>
                                    <span v-else class="formType">{{ all.formTypeName }}</span>
                                </span>
                                <span class="type">{{ SearchType.getMsg(all.type) }}</span>
                            </div>
                            <div class="brief">{{ all.userBrief }}</div>
                        </el-col>
                    </el-row>

                    <div class="all-title" v-html="all.title">
                    </div>

                    <div class="all-content" v-html="all.profile">
                    </div>
                    <!-- <div style="margin-bottom: 10px;">
                        <el-tag type="info" size="mini">标签三</el-tag>
                    </div> -->

                    <div style="vertical-align: middle;">
                        <span class="iconfont icon-yanjing operate-common">&nbsp;{{ getFormatNumber(all.viewCount) }}</span>
                        <span v-if="all.type != SearchType.COURSE" class="iconfont icon-dianzan operate-common">&nbsp;{{ getFormatNumber(all.likeCount) }}</span>
                        <span class="iconfont icon-shoucang operate-common">&nbsp;{{ getFormatNumber(all.collectCount) }}</span>
                        <span class="iconfont icon-pinglun operate-common">&nbsp;{{ getFormatNumber(all.commentCount) }}</span>
                        <el-tag 
                        v-if="all.type != SearchType.COMMUNITY_ARTICLE"
                        type="info" 
                        size="small" 
                        style="margin-right: 10px;"
                        v-for="label in all.labelName" :key="label"
                        v-html="label"></el-tag>

                    </div>
                    <div class="divisor"></div>
                </div>
            </div>

        <div
        v-if="allList == null || allList == '' || allList == [] || allList.length <= 0"
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
    name: 'MonkeyWebSearchAll',

    data() {
        return {
            isScroll: true,
            activeName: "comprehensive",
            currentPage: 1,
            pageSize: 10,
            // 关键词搜索字段
            keyword: this.$route.query.keyword,
            // 所有集合
            allList: [],
            searchAllUrl: "http://localhost:80/monkey-search/all",
        };
    },

    watch: {
        '$route.query.keyword'(newVal, oldVal) {
            this.keyword = newVal;
            this.allList = [];
            this.currentPage = 1;
            if (this.activeName == 'comprehensive') {
                    this.queryComprehensiveAll();
                } else if (this.activeName == "latest") {
                    this.queryLatestAll();
                } else if (this.activeName == "hire") {
                    this.queryHireAll();
                } else if (this.activeName == "view") {
                    this.queryViewAll();
                } else if (this.activeName == "like") {
                    this.queryLikeAll();
                } else if (this.activeName == "collect") {
                    this.queryCollectAll();
                } else if (this.activeName == "comment") {
                    this.queryCommentAll();
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
        toAllViews(type, associationId, communityId) {
            if (type == this.SearchType.ARTICLE) {
                // 前往文章页面
                const { href } = this.$router.resolve({
                    name: "check_article",
                    params: {
                        articleId: associationId,
                    }
                })
                window.open(href, '_black')
            } else if (type == this.SearchType.QUESTION) {
                // 前往问答页面
                const { href } = this.$router.resolve({
                    name: "question_reply",
                    params: {
                        questionId: associationId,
                    }
                })
                window.open(href, '_black')
            } else if (type == this.SearchType.COURSE) {
                // 前往课程页面
                const { href } = this.$router.resolve({
                    name: "course_detail",
                    params: {
                        courseId: associationId,
                    }
                })
                window.open(href, '_black')
            } else if (type == this.SearchType.COMMUNITY_ARTICLE) {
                // 前往社区文章页面
                const { href } = this.$router.resolve({
                    name: "community_article",
                    params: {
                        communityArticleId: associationId,
                        communityId
                    }
                })
                window.open(href, '_black')
            } else if (type == this.SearchType.RESOURCE) {
                // 前往资源页面
                const { href } = this.$router.resolve({
                    name: "resource_detail",
                    params: {
                        resourceId: associationId,
                    }
                })
                window.open(href, '_black')
            }
        },
        handleClick() {
            this.isScroll = false;
            this.currentPage = 1;
            this.allList = [];
            if (this.activeName == 'comprehensive') {
                    this.queryComprehensiveAll();
                } else if (this.activeName == "latest") {
                    this.queryLatestAll();
                } else if (this.activeName == "hire") {
                    this.queryHireAll();
                } else if (this.activeName == "view") {
                    this.queryViewAll();
                } else if (this.activeName == "like") {
                    this.queryLikeAll();
                } else if (this.activeName == "collect") {
                    this.queryCollectAll();
                } else if (this.activeName == "comment") {
                    this.queryCommentAll();
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
                    this.queryComprehensiveAll();
                } else if (this.activeName == "latest") {
                    this.queryLatestAll();
                } else if (this.activeName == "hire") {
                    this.queryHireAll();
                } else if (this.activeName == "view") {
                    this.queryViewAll();
                } else if (this.activeName == "like") {
                    this.queryLikeAll();
                } else if (this.activeName == "collect") {
                    this.queryCollectAll();
                } else if (this.activeName == "comment") {
                    this.queryCommentAll();
                }
            }
        },
        // 查询回复最多所有列表
        queryCommentAll() {
            const vue = this;
            $.ajax({
                url: vue.searchAllUrl + "/queryCommentAll",
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
                            vue.allList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询收藏数最多所有列表
        queryCollectAll() {
            const vue = this;
            $.ajax({
                url: vue.searchAllUrl + "/queryCollectAll",
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
                            vue.allList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询点赞数最多所有列表
        queryLikeAll() {
            const vue = this;
            $.ajax({
                url: vue.searchAllUrl + "/queryLikeAll",
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
                            vue.allList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询游览数最多所有列表
        queryViewAll() {
            const vue = this;
            $.ajax({
                url: vue.searchAllUrl + "/queryViewAll",
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
                            vue.allList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询最热所有列表
        queryHireAll() {
            const vue = this;
            $.ajax({
                url: vue.searchAllUrl + "/queryHireAll",
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
                            vue.allList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询最新所有列表
        queryLatestAll() {
            const vue = this;
            $.ajax({
                url: vue.searchAllUrl + "/queryLatestAll",
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
                            vue.allList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询综合所有列表
        queryComprehensiveAll() {
            const vue = this;
            $.ajax({
                url: vue.searchAllUrl + "/queryComprehensiveAll",
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
                            vue.allList.push(data[i]);
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
.formType-fee {
    background-image: linear-gradient(to right, #f9d423 0%, #ff4e50 100%);
    font-size: 12px;
    text-align: center;
    color: white;
    padding: 2px 5px;
    margin-right: 10px;
}
.formType {
    background-image: linear-gradient(to right, #92fe9d 0%, #00c9ff 100%);
    font-size: 12px;
    text-align: center;
    color: white;
    padding: 2px 5px;
    margin-right: 10px;
}
.type {
    background-image: linear-gradient(to right, #4facfe 0%, #00f2fe 100%);
    font-size: 12px;
    text-align: center;
    color: white;
    padding: 2px 5px;
    margin-right: 10px;
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
    margin-right: 16px;
    font-size: 16px;
    color: gray;
}
.font-class {
    margin-bottom: 2px;
}
.all-content {
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
.all-content:hover {
    opacity: 0.8;
}
.all-title {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 10px;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    max-width: 600px;
    transition: 0.2s linear all;
}
.all-title:hover {
    opacity: 0.8;
}
.all-picture {
    position: absolute;
    right: 10px;
    top: 0;
    height: 100px;
    width: 150px;
    border-radius: 0;
    transition: 0.2s linear all;
}

.all-picture:hover {
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
    vertical-align: middle;
    font-size: 14px;
    color: gray;
    margin-right: 10px;
}
.community {
    vertical-align: middle;
    font-size: 14px;
    color: gray;
    margin-right: 10px;
    transition: 0.2s linear all;
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
    vertical-align: middle;
}
.username:hover {
    color: #409EFF;
}
.MonkeyWebSearchAll-container {
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