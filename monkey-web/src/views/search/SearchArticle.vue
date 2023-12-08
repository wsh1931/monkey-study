<template>
    <div class="MonkeyWebSearchArticle-container">
        <el-tabs v-model="activeName" @tab-click="handleClick" >
            <el-tab-pane label="综合" name="comprehensive"></el-tab-pane>
            <el-tab-pane label="最新" name="latest"></el-tab-pane>
            <el-tab-pane label="最热" name="hire"></el-tab-pane>
            <el-tab-pane label="游览" name="view"></el-tab-pane>
            <el-tab-pane label="点赞" name="like"></el-tab-pane>
            <el-tab-pane label="收藏" name="collect"></el-tab-pane>
            <el-tab-pane label="评论" name="reply"></el-tab-pane>

            <div
            class="infinite-list" 
            v-infinite-scroll="loadData" 
            infinite-scroll-distance="30">
                <div 
                @click="toArticleViews(article.id)"
                v-for="article in articleList" :key="article.id" 
                style="cursor: pointer;">
                    <el-row style="margin-bottom: 5px;">
                        <el-col :span="1">
                            <img @click="toUserViews(article.userId)" class="user-headImg" :src="article.userHeadImg" alt="">
                        </el-col>
                        <el-col :span="23" class="card-right">
                            <img class="article-picture" :src="article.photo" alt="">
                            <div class="font-class">
                                <span @click="toUserViews(article.userId)" class="username">{{ article.username }}</span>
                                <span class="publishTime">{{ getTimeFormat(article.createTime) }}</span>
                            </div>
                            <div class="brief">{{ article.userBrief }}</div>
                        </el-col>
                    </el-row>

                    <div class="article-title" v-html="article.title">
                    </div>

                    <div class="article-content" v-html="article.profile">
                    </div>
                    <!-- <div style="margin-bottom: 10px;">
                        <el-tag type="info" size="mini">标签三</el-tag>
                    </div> -->

                    <div style="vertical-align: middle;">
                        <span class="iconfont icon-yanjing operate-common">&nbsp;{{ getFormatNumber(article.viewCount) }}</span>
                        <span class="iconfont icon-dianzan operate-common">&nbsp;{{ getFormatNumber(article.likeCount) }}</span>
                        <span class="iconfont icon-shoucang operate-common">&nbsp;{{ getFormatNumber(article.collectCount) }}</span>
                        <span class="iconfont icon-pinglun operate-common">&nbsp;{{ getFormatNumber(article.commentCount) }}</span>
                        <el-tag 
                        type="info" 
                        size="small" 
                        style="margin-right: 10px;"
                        v-for="label in article.labelName" :key="label"
                        v-html="label"></el-tag>

                    </div>
                    <div class="divisor"></div>
                </div>
            </div>

        <div
        v-if="articleList == null || articleList == '' || articleList == [] || articleList.length <= 0"
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
    name: 'MonkeyWebSearchArticle',

    data() {
        return {
            isScroll: true,
            activeName: "comprehensive",
            currentPage: 1,
            pageSize: 10,
            totals: 0,
            // 关键词搜索字段
            keyword: this.$route.query.keyword,
            // 文章集合
            articleList: [],
            searchArticleUrl: "http://localhost:80/monkey-search/article",
            checkArticleUrl: "http://localhost:80/monkey-article/check",
        };
    },

    watch: {
        '$route.query.keyword'(newVal, oldVal) {
            this.keyword = newVal;
            this.currentPage = 1;
            this.articleList = [];
            if (this.activeName == 'comprehensive') {
                    this.queryComprehensiveArticle();
                } else if (this.activeName == "latest") {
                    this.queryLatestArticle();
                } else if (this.activeName == "hire") {
                    this.queryHireArticle();
                } else if (this.activeName == "view") {
                    this.queryViewArticle();
                } else if (this.activeName == "like") {
                    this.queryLikeArticle();
                } else if (this.activeName == "collect") {
                    this.queryCollectArticle();
                } else if (this.activeName == "reply") {
                    this.queryReplyArticle();
                }
        }
    },

    methods: {
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
        toArticleViews(articleId) {
            const { href } = this.$router.resolve({
                name: "check_article",
                params: {
                    articleId
                }
            })
            window.open(href, '_black')
        },
        handleClick() {
            this.isScroll = false;
            this.currentPage = 1;
            this.articleList = [];
            if (this.activeName == 'comprehensive') {
                    this.queryComprehensiveArticle();
                } else if (this.activeName == "latest") {
                    this.queryLatestArticle();
                } else if (this.activeName == "hire") {
                    this.queryHireArticle();
                } else if (this.activeName == "view") {
                    this.queryViewArticle();
                } else if (this.activeName == "like") {
                    this.queryLikeArticle();
                } else if (this.activeName == "collect") {
                    this.queryCollectArticle();
                } else if (this.activeName == "reply") {
                    this.queryReplyArticle();
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
                    this.queryComprehensiveArticle();
                } else if (this.activeName == "latest") {
                    this.queryLatestArticle();
                } else if (this.activeName == "hire") {
                    this.queryHireArticle();
                } else if (this.activeName == "view") {
                    this.queryViewArticle();
                } else if (this.activeName == "like") {
                    this.queryLikeArticle();
                } else if (this.activeName == "collect") {
                    this.queryCollectArticle();
                } else if (this.activeName == "reply") {
                    this.queryReplyArticle();
                }
            }
        },
        // 查询回复最多文章列表
        queryReplyArticle() {
            const vue = this;
            $.ajax({
                url: vue.searchArticleUrl + "/queryReplyArticle",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data.esArticleIndexList;
                        vue.totals = response.data.totals;
                        for (let i = 0; i < data.length; i++) {
                            vue.articleList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询收藏数最多文章列表
        queryCollectArticle() {
            const vue = this;
            $.ajax({
                url: vue.searchArticleUrl + "/queryCollectArticle",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data.esArticleIndexList;
                        vue.totals = response.data.totals;
                        for (let i = 0; i < data.length; i++) {
                            vue.articleList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询点赞数最多文章列表
        queryLikeArticle() {
            const vue = this;
            $.ajax({
                url: vue.searchArticleUrl + "/queryLikeArticle",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data.esArticleIndexList;
                        vue.totals = response.data.totals;
                        for (let i = 0; i < data.length; i++) {
                            vue.articleList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询游览数最多文章列表
        queryViewArticle() {
            const vue = this;
            $.ajax({
                url: vue.searchArticleUrl + "/queryViewArticle",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data.esArticleIndexList;
                        vue.totals = response.data.totals;
                        for (let i = 0; i < data.length; i++) {
                            vue.articleList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询最热文章列表
        queryHireArticle() {
            const vue = this;
            $.ajax({
                url: vue.searchArticleUrl + "/queryHireArticle",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data.esArticleIndexList;
                        vue.totals = response.data.totals;
                        for (let i = 0; i < data.length; i++) {
                            vue.articleList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询最新文章列表
        queryLatestArticle() {
            const vue = this;
            $.ajax({
                url: vue.searchArticleUrl + "/queryLatestArticle",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data.esArticleIndexList;
                        vue.totals = response.data.totals;
                        for (let i = 0; i < data.length; i++) {
                            vue.articleList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询综合文章列表
        queryComprehensiveArticle() {
            const vue = this;
            $.ajax({
                url: vue.searchArticleUrl + "/queryComprehensiveArticle",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data.esArticleIndexList;
                        vue.totals = response.data.totals;
                        for (let i = 0; i < data.length; i++) {
                            vue.articleList.push(data[i]);
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
.article-content {
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
.article-content:hover {
    opacity: 0.8;
}
.article-title {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 10px;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    max-width: 600px;
    transition: 0.2s linear all;
}
.article-title:hover {
    opacity: 0.8;
}
.article-picture {
    position: absolute;
    right: 10px;
    top: 0;
    height: 100px;
    width: 150px;
    border-radius: 0;
    transition: 0.2s linear all;
}

.article-picture:hover {
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
.MonkeyWebSearchArticle-container {
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