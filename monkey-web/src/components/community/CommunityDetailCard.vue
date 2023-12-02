<template>
    <div class="MonkeyWebCommunityDetailCard-container">
        <el-row v-for="(article, index) in communityArticleList" :key="article.id">
            <!-- 卡片顶部 -->
            <el-row class="top">
                <img @click="toUserViews($store.state.user.id)" class="community-card-img" :src="article.userHeadImg" alt="">
                <span @click="toUserViews($store.state.user.id)" class="username">{{ article.username }}</span>
                <span @click="toCommunityDetailViews(article.communityId)" class="community-top">|&nbsp;&nbsp;&nbsp;来自: {{ article.communityName }}</span>
                <span class="time-top"> | {{ article.createTime }} </span>
                <span class="label" style="text-align: right;">
                    <span v-if="article.isExcellent == '1'">&nbsp;&nbsp;</span>
                    <span class="excellent" v-if="article.isExcellent == '1'">精选</span>
                    <span v-if="article.isExcellent == '1'">&nbsp;&nbsp;</span>
                    <span class="isTop" v-if="article.isTop == '1'">置顶</span>
                </span>
                
            </el-row>

            <!-- 卡片内容 -->
            <el-row style="margin-top: 10px;">
                <el-col :span="5" style="overflow: hidden;">
                    <img @click="toCommunityArticleViews(communityId, article.id)" class="content-card-img" :src="article.picture" alt="">
                </el-col>
                <el-col :span="19" style="padding-left: 10px;">
                    <el-row class="article-title">
                        <div @click="toCommunityArticleViews(communityId, article.id)">
                            {{ article.title }}
                        </div>
                    </el-row>
                    <el-row class="article-content">
                        {{ article.brief }}
                    </el-row>

                    <el-row>
                        <span class="el-icon-view view">&nbsp;游览&nbsp;{{ article.viewCount }}</span>
                        <span class="iconfont icon-dianzan view">&nbsp;点赞&nbsp;{{ article.likeCount }}</span>
                        <span class="iconfont icon-shoucang view">&nbsp;收藏&nbsp;{{ article.collectCount }}</span>
                        <span class="iconfont icon-pinglun view">&nbsp;回复&nbsp;{{ article.replyCount }}</span>
                        <span class="iconfont icon-zhuanfa view share">&nbsp;分享</span>
                        <span 
                        class="el-icon-more view more" 
                        @mouseover.stop="article.isMoreHover = '1'" 
                        @mouseleave="article.isMoreHover = '0'">
                            <div class="more-background" v-if="article.isMoreHover == '1' && isPower">
                                <div class="more-background-content" @click="deleteArticle(article.id, index)">移除</div>
                                <div class="more-background-content" @click="setExcellentArticle(article.id, index)" v-if="article.isExcellent == '0'">精选</div>
                                <div class="more-background-content" @click="cancelExcellentArticle(article.id, index)" v-if="article.isExcellent == '1'">取消精选</div>
                                <div class="more-background-content" @click="setTopArticle(article.id, index)" v-if="article.isTop == '0'">置顶</div>
                                <div class="more-background-content" @click="cancelTopArticle(article.id, index)" v-if="article.isTop == '1'">取消置顶</div>
                            </div>
                        </span>
                    </el-row>
                </el-col>
            </el-row>

            <el-row class="divider"></el-row>
        </el-row>
    </div>
</template>

<script>
import store from '@/store';
import $ from 'jquery'
export default {
    name: 'MonkeyWebCommunityDetailCard',
    props: ['communityArticleList'],
    data() {
        return {
            communityId: "",
            // 是否有显示隐藏框的权力
            isPower: false,
            communityDetailCardUrl: "http://localhost:80/monkey-community/community/detail/card",
            communityUrl: "http://localhost:80/monkey-community/community",
        };
    },
    watch: {
        $route() {
            this.communityId = this.$route.params.communityId;
            this.judgePower(this.communityId);
        }
    },
    created() {
        this.communityId = this.$route.params.communityId;
        this.judgePower(this.communityId);
    },

    methods: {
        // 文章游览数 + 1
        communityArticleViewCountAddOne(communityArticleId) {
            const vue = this;
            $.ajax({
                url: vue.communityUrl + "/articleViewCount/addOne",
                type: "put",
                data: {
                    communityArticleId,
                },
                success(response) {
                    if (response.code != vue.ResultStatus.SUCCESS) {
                        vue.$modal.msgError(response.msg);
                    }
                },
                error(response) {
                    vue.$modal.msgError(response.msg);
                }
            })
        },
        // 前往社区文章界面
        toCommunityArticleViews(communityId, communityArticleId) {
            this.communityArticleViewCountAddOne(communityArticleId);
            const { href } = this.$router.resolve({
                name: "community_article",
                params: {
                    communityId,
                    communityArticleId
                }
            })
            window.open(href, "_blank")
        },
        // 设置文章置顶
        setTopArticle(articleId, index) {
            const vue = this;
            $.ajax({
                url: vue.communityDetailCardUrl + "/setTopArticle",
                type: "put",
                data: {
                    articleId,
                    communityId: vue.communityId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        let article = vue.communityArticleList[index];
                        article.isTop = '1';
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
                error(response) {
                    vue.$modal.msgError(response.msg);
                }
            })
        },
        // 取消文章置顶
        cancelTopArticle(articleId, index) {
            const vue = this;
            $.ajax({
                url: vue.communityDetailCardUrl + "/cancelTopArticle",
                type: "put",
                data: {
                    articleId,
                    communityId: vue.communityId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        let article = vue.communityArticleList[index];
                        article.isTop = '0';
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
                error(response) {
                    vue.$modal.msgError(response.msg);
                }
            })
        },
        // 设置文章为精选内容
        setExcellentArticle(articleId, index) {
            const vue = this;
            $.ajax({
                url: vue.communityDetailCardUrl + "/setExcellentArticle",
                type: "put",
                data: {
                    articleId,
                    communityId: vue.communityId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        let article = vue.communityArticleList[index];
                        article.isExcellent = '1';
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
                error(response) {
                    vue.$modal.msgError(response.msg);
                }
            })
        },
         // 取消文章为精选内容
        cancelExcellentArticle(articleId, index) {
            const vue = this;
            $.ajax({
                url: vue.communityDetailCardUrl + "/cancelExcellentArticle",
                type: "put",
                data: {
                    articleId,
                    communityId: vue.communityId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        let article = vue.communityArticleList[index];
                        article.isExcellent = '0';
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
                error(response) {
                    vue.$modal.msgError(response.msg);
                }
            })
        },
        // 删除文章
        deleteArticle(articleId, index) {
            const vue = this;
            $.ajax({
                url: vue.communityDetailCardUrl + "/deleteArticle",
                type: "delete",
                data: {
                    articleId,
                    communityId: vue.communityId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.communityArticleList.splice(index, 1);
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
                error(response) {
                    vue.$modal.msgError(response.msg);
                }
            })
        },
        // 判断是否有显示隐藏框的权力
        judgePower(communityId) {
            const vue = this;
            $.ajax({
                url: vue.communityDetailCardUrl + '/judgePower',
                type: "get",
                data: {
                    communityId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.isPower = response.data;
                    } else {
                        vue.isPower = false;
                    }
                }
            })
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
        // 前往社区详情页面主页
        toCommunityDetailViews(communityId) {
            const { href } = this.$router.resolve({
                name: "community_detail",
                params: {
                    communityId,
                },
            })

            window.open(href, "_blank");
        }
    },
};
</script>

<style scoped>
.more-background-content {
    animation: slide-out 0.4s linear;
}
@keyframes slide-out {
    0% {
        opacity: 0;
    }
    100% {
        opacity: 1;
    }
}

.more-background-content:nth-child(n + 2) {
    padding-top: 12px;
}
.more-background-content:hover {
    color: #409EFF;
    cursor: pointer;
}
.more-background {
    position: absolute;
    text-align: center;
    width: 60px;
    padding: 10px 16px;
    left: -30px;
    background-color: #fff;
    box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
    z-index: 1;
    cursor: auto;
}
.more {
    position: relative;
}
.more:hover {
    cursor: pointer;
}
.share:hover {
    color: #409EFF;
    cursor: pointer;
}
.isTop {
    display: inline;
    padding: 5px;
    width: 40px;
    border-radius: 2px;
    text-align: center;
    font-size: 12px;
    font-weight: 400;
    color: #fff;
    background-image: linear-gradient(120deg, #89f7fe 0%, #66a6ff 100%);
}
.excellent {
    display: inline;
    padding: 5px;
    width: 40px;
    background-image: linear-gradient(to right, #f88aab 0%, #f5df66 100%);
    border-radius: 2px;
    text-align: center;
    font-size: 12px;
    font-weight: 400;
    color: #fff;
}
@keyframes slide-up {
    0% {
        opacity: 0;
    }
    100% {
        opacity: 1;
    }
}
.MonkeyWebCommunityDetailCard-container {
    animation: slide-up 0.4s linear;
}
.divider {
    margin: 20px;
    background-color: gray;
    height: 0.5px;
    opacity: 0.5;
}
.channel-content:hover {
    opacity: 0.5;
}
.channel-content {
    float: right;
    padding: 2px 10px;
    background-color: rgb(75, 180, 199);
    color: #fff;
    max-width: 100px;
    overflow: hidden;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
    text-overflow: ellipsis;
    white-space: nowrap;
    vertical-align: middle;
    cursor: pointer;
}
.channel {
    float: right;
    font-size: 14px;
    line-height: 24.8px;
}
.view {
    font-size: 14px;
    margin-right: 32px;
}
.article-content {
    min-height: 60px;
    line-height: 24px;
    font-size: 15px;
    font-weight: 400;
    margin-bottom: 14px;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    overflow: hidden;
    padding-top: 10px;
}
.article-title {
    cursor: pointer;
    font-size: 16px;
    font-weight: 700;
    color: #222226;
    line-height: 24px;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
    -webkit-margin-before: 0;
    margin-block-start: 0;
    -webkit-margin-after: 0;
    margin-block-end: 0;
}
.article-title:hover {
    opacity: 0.5;
}
.content-card-img {
    width: 100%;
    cursor: pointer;
    height: 120px;
    transition: 0.4s linear all;
}
.content-card-img:hover {
    transform: scale(1.4);
}
.label {
    float: right;
    font-size: 14px;
    height: 32px;
    line-height: 32px;
    font-weight: 500;
}
.time-top {
    display: inline-block;
    font-size: 14px;
    height: 32px;
    line-height: 32px;
    font-weight: 500;
    color: #555666;
    margin-left: 8px;
    vertical-align: middle;
}
.community-top {
    display: inline-block;
    font-size: 14px;
    height: 32px;
    line-height: 32px;
    font-weight: 500;
    color: #555666;
    margin-left: 8px;
    max-width: 300px;
    overflow: hidden;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
    text-overflow: ellipsis;
    white-space: nowrap;
    vertical-align: middle;
}
.community-top:hover {
    cursor: pointer;
    opacity: 0.5;
}
.top {
    height: 34px;
    line-height: 34px;
    overflow: hidden;
    position: relative;
}
.username {
    display: inline-block;
    font-size: 14px;
    height: 32px;
    line-height: 32px;
    font-weight: 500;
    color: #555666;
    margin-left: 8px;
    max-width: 160px;
    overflow: hidden;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
    text-overflow: ellipsis;
    white-space: nowrap;
    vertical-align: middle;
    cursor: pointer;
}
.username:hover {
    opacity: 0.5;
}
.community-card-img {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    vertical-align: top;
    border: 1px solid #f2f2f2;
    cursor: pointer;
}
.community-card-img:hover {
    opacity: 0.5;
}
</style>