<template>
    <div class="MonkeyWebUserHomeArticle-container">
        <div class="article-card" 
        @click="toArticleDetailViews(article.id)"
        v-for="article in articleList" 
        :key="article.id"
        @mouseover="article.isHover = '1'"
        @mouseleave="article.isHover = '0'">
            <el-row style="margin-bottom: 10px;">
                <el-col :span="4">
                    <div class="img-border">
                        <img class="article-img" :src="article.photo" alt="">
                    </div>
                </el-col>
                <el-col :span="20">
                    <div >
                        <span class="article-title">{{ article.title }}</span>
                        <span class="create-time">&nbsp;| 发表于：{{ getTimeFormat(article.createTime) }}</span>
                    </div>
                    <div class="article-brief">{{ article.brief }}</div>
                    <div style="position: relative;">
                        <span class="iconfont icon-yanjing operate-common">&nbsp;游览&nbsp;{{ getFormatNumber(article.viewCount) }}</span>
                        <span class="iconfont icon-dianzan operate-common">&nbsp;点赞&nbsp;{{ getFormatNumber(article.likeCount) }}</span>
                        <span class="iconfont icon-shoucang operate-common">&nbsp;收藏&nbsp;{{ getFormatNumber(article.collectCount) }}</span>
                        <span class="iconfont icon-pinglun operate-common">&nbsp;评论&nbsp;{{ getFormatNumber(article.commentCount) }}</span>
                    <div
                        v-if="article.isHover == '1' && article.userId == $store.state.user.id" 
                        @mouseover="article.isMoreHover = '1'"
                        @mouseleave="article.isMoreHover = '0'"
                        class="hover el-icon-more-outline">
                            <div v-if="article.isMoreHover == '1'" class="more-hover">
                                <div 
                                @click.stop="toArticleEditViews(article.id)" 
                                class="common-hover">编辑</div>
                                <div @click.stop="deleteArticle(article)" class="common-hover">删除</div>
                            </div>
                        </div>
                    </div>
                </el-col>
            </el-row>
        </div>


        <div
        v-if="articleList == null || articleList == '' || articleList == [] || articleList.length <= 0"
        style="text-align: center;" >
            <el-empty description="暂无数据"></el-empty>
        </div>
        <PagiNation
        style="text-align: right;"
            :totals="totals"
            :currentPage="currentPage" 
            :pageSize="pageSize" 
            @handleCurrentChange = "handleCurrentChange"
            @handleSizeChange="handleSizeChange"/>
    </div>
</template>

<script>
import $ from 'jquery'
import store from '@/store';
import PagiNation from '@/components/pagination/PagiNation.vue';
import { getFormatNumber } from '@/assets/js/NumberMethod';
import { getTimeFormat } from '@/assets/js/DateMethod';
export default {
    name: 'MonkeyWebUserHomeArticle',

    data() {
        return {
            userId: "",
            currentPage: 1,
            pageSize: 10,
            totals: 0,
            articleList: [],
            userHomeArticleUrl: "http://localhost:80/monkey-article/user/home",
        };
    },

    created() {
        this.userId = this.$route.params.userId;
        this.queryArticleByUserId(this.userId);
    },

    methods: {
        getTimeFormat(val) {
            return getTimeFormat(val);
        },
        // 删除文章
        deleteArticle(article) {
            this.$modal.confirm(`"您确定要删除 ${article.title} 文章?"`)
                .then(() => {
                const vue = this;
                $.ajax({
                    url: vue.userHomeArticleUrl + "/deleteArticle",
                    type: "delete",
                    headers: {
                        Authorization: "Bearer " + store.state.user.token,
                    },
                    data: {
                        articleId: article.id,
                    },
                    success(response) {
                        if (response.code == vue.ResultStatus.SUCCESS) {
                            vue.queryArticleByUserId(vue.userId);
                            vue.$modal.msgSuccess(response.msg);
                        } else {
                            vue.$modal.msgError(response.msg);
                        }
                    }
                })
            }).catch(() => {})
            
        },
        // 前往编辑文章界面
        toArticleEditViews(articleId) {
            this.$router.push({
                name: "article_edit",
                params: {
                    articleId,
                },
            })
        },
        // 前往文章详情页面
        toArticleDetailViews(articleId) {
            const { href } = this.$router.resolve({
                name: "check_article",
                params: {
                    articleId,
                }
            })

            window.open(href, "_blank")
        },
        // 通过用户id查询文章集合
        queryArticleByUserId(userId) {
            const vue = this;
            $.ajax({
                url: vue.userHomeArticleUrl + "/queryArticleByUserId",
                type: "get",
                data: {
                    userId,
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.articleList = response.data.records;
                        vue.totals = response.data.total;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        handleSizeChange(val) {
            this.pageSize = val;
            this.currentPage = 1;
            this.queryArticleByUserId(this.userId);
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            this.queryArticleByUserId(this.userId);
        },
        getFormatNumber(val) {
            return getFormatNumber(val);
        }
    },
};
</script>

<style scoped>
.create-time {
    font-size: 14px;
    color: gray;
    vertical-align: middle;
}
.article-title:hover {
    color: #409EFF;
}
.common-hover:hover {
    color: #409EFF;
}
.common-hover:nth-child(n + 1) {
    margin-bottom: 10px;
}

.common-hover:last-child {
    margin-bottom: 0;
}
.more-hover {
    position: absolute;
    bottom: 20px;
    right: -20px;
    width: 50px;
    font-size: 14px;
    padding: 10px;
    background-color: #fff;
    text-align: center;
    box-shadow: 0 0 5px 0 black;
    animation: slide-out 0.4s linear;
}

@keyframes slide-out {
    0% {
        opacity: 0;
    }
    100% {
        overflow: 1;
    }
}

.hover {
    position: absolute;
    right: 0;
    font-size: 24px;
    animation: slide-out 0.4s linear;
}
.article-card {
    cursor: pointer;
    transition: 0.4s linear all;
    padding: 20px;
}
.article-card:hover {
    box-shadow: 0 0 10px 0 #00f2fe;
}
.operate-common {
    margin-right: 10px;
    font-size: 14px;
    color: gray;
}
.article-title {
    display: inline-block;
    max-width: 740px;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    margin-bottom: 4px;
    vertical-align: middle;
    margin-right: 10px;
}

.article-brief {
    display: -webkit-box;
    height: 75px;
    color: gray;
    font-size: 14px;
    overflow: hidden;
    -webkit-line-clamp: 4;
    -webkit-box-orient: vertical;
    text-overflow: ellipsis;
    white-space: normal;
    vertical-align: middle;
    max-width: 740px;
}
.img-border {
    overflow: hidden; 
    display: inline-block;
    position: relative;
}
.article-img {
    width: 130px;
    height: 120px;
    cursor: pointer;
    transition: 0.4s linear all;
}
.article-img:hover {
    transform: scale(1.2);
}
.MonkeyWebUserHomeArticle-container {
    background-color: #fff;
    padding: 20px;
    animation: slide-out 0.4s linear;
}
</style>