<template>
    <div class="MonkeyWebUserHomeCommunityArticle-container">
        <div class="communityArticle-card" 
        @click="toCommunityArticleDetailViews(communityArticle.id, communityArticle.communityId)"
        v-for="communityArticle in communityArticleList" 
        :key="communityArticle.id"
        @mouseover="communityArticle.isHover = '1'"
        @mouseleave="communityArticle.isHover = '0'">
            <el-row style="margin-bottom: 10px;">
                <el-col :span="4">
                    <div class="img-border">
                        <img class="communityArticle-img" :src="communityArticle.picture" alt="">
                        <div v-if="communityArticle == '1'" class="formTypeName">精选</div>
                    </div>
                </el-col>
                <el-col :span="20" style="padding-left: 10px;">
                    <div>
                        <span class="communityArticle-title">{{ communityArticle.title }}</span>
                        <span 
                        @click="toCommunityDetailViews(communityArticle.communityId)"
                        class="communityArticle-name">来自于：{{ getTimeFormat(communityArticle.communityName) }}</span>
                        <span class="create-time">&nbsp;| 发表于：{{ getTimeFormat(communityArticle.createTime) }}</span>
                    </div>
                    <div class="communityArticle-brief">{{ communityArticle.brief }}</div>
                    <div style="position: relative;">
                        <span class="iconfont icon-yanjing operate-common">&nbsp;游览&nbsp;{{ getFormatNumber(communityArticle.viewCount) }}</span>
                        <span class="iconfont icon-dianzan operate-common">&nbsp;点赞&nbsp;{{ getFormatNumber(communityArticle.likeCount) }}</span>
                        <span class="iconfont icon-shoucang operate-common">&nbsp;收藏&nbsp;{{ getFormatNumber(communityArticle.collectCount) }}</span>
                        <span class="iconfont icon-pinglun operate-common">&nbsp;评论&nbsp;{{ getFormatNumber(communityArticle.commentCount) }}</span>
                        <span class="iconfont icon-shoucang operate-common">&nbsp;评分&nbsp;{{ communityArticle.score }}</span>
                    <div
                        v-if="communityArticle.isHover == '1' && communityArticle.userId == $store.state.user.id" 
                        @mouseover="communityArticle.isMoreHover = '1'"
                        @mouseleave="communityArticle.isMoreHover = '0'"
                        class="hover el-icon-more-outline">
                            <div v-if="communityArticle.isMoreHover == '1'" class="more-hover">
                                <div 
                                @click.stop="toCommunityArticleEditViews(communityArticle.id, communityArticle.communityId)" 
                                class="common-hover">编辑</div>
                                <div @click.stop="deleteCommunityArticle(communityArticle)" class="common-hover">删除</div>
                            </div>
                        </div>
                    </div>
                </el-col>
            </el-row>

            <div class="divisor"></div>
        </div>


        <div
        v-if="communityArticleList == null || communityArticleList == '' || communityArticleList == [] || communityArticleList.length <= 0"
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
    name: 'MonkeyWebUserHomeCommunityArticleArticle',
    components: {
        PagiNation
    },
    data() {
        return {
            userId: "",
            currentPage: 1,
            pageSize: 10,
            totals: 0,
            communityArticleList: [],
            userHomeCommunityArticleUrl: "http://localhost:80/monkey-community/user/home/community/article",
        };
    },

    created() {
        this.userId = this.$route.params.userId;
        this.queryCommunityArticleByUserId(this.userId);
    },

    methods: {
        getTimeFormat(val) {
            return getTimeFormat(val);
        },
        // 删除社区文章
        deleteCommunityArticle(communityArticle) {
            this.$modal.confirm(`"您确定要删除 ${communityArticle.title} 社区文章?"`)
                .then(() => {
                const vue = this;
                $.ajax({
                    url: vue.userHomeCommunityArticleUrl + "/deleteCommunityArticle",
                    type: "delete",
                    headers: {
                        Authorization: "Bearer " + store.state.user.token,
                    },
                    data: {
                        communityArticleId: communityArticle.id,
                        communityId: communityArticle.communityId,
                    },
                    success(response) {
                        if (response.code == vue.ResultStatus.SUCCESS) {
                            vue.queryCommunityArticleByUserId(vue.userId);
                            vue.$modal.msgSuccess(response.msg);
                        } else {
                            vue.$modal.msgError(response.msg);
                        }
                    }
                })
            }).catch(() => {})
            
        },
        // 前往编辑社区文章界面
        toCommunityArticleEditViews(communityArticleId, communityId) {
            this.$router.push({
                name: "community_article_edit",
                params: {
                    communityId,
                    communityArticleId,
                },
            })
        },
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
        // 前往社区文章详情页面
        toCommunityArticleDetailViews(communityArticleId, communityId) {
            const { href } = this.$router.resolve({
                name: "community_article",
                params: {
                    communityArticleId,
                    communityId
                }
            })

            window.open(href, "_blank")
        },
        // 通过用户id查询社区文章集合
        queryCommunityArticleByUserId(userId) {
            const vue = this;
            $.ajax({
                url: vue.userHomeCommunityArticleUrl + "/queryCommunityArticleByUserId",
                type: "get",
                data: {
                    userId,
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
        handleSizeChange(val) {
            this.pageSize = val;
            this.currentPage = 1;
            this.queryCommunityArticleByUserId(this.userId);
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            this.queryCommunityArticleByUserId(this.userId);
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
.communityArticle-title:hover {
    color: #409EFF;
}
.communityArticle-name:hover {
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
.formTypeName {
    position: absolute;
    top: 0px;
    left: 0px;
    background-image: linear-gradient(to right, #f9d423 0%, #ff4e50 100%);
    color: white;
    padding: 1px 5px;
    font-size: 14px;
}
.divisor {
    background-color: rgba(0, 0, 0, 0.1);
    width: 100%;
    height: 1px;
}
.communityArticle-card {
    cursor: pointer;
    transition: 0.4s linear all;
    padding: 20px;
}
.communityArticle-card:hover {
    box-shadow: 0 0 10px 0 #00f2fe;
}
.operate-common {
    margin-right: 10px;
    font-size: 14px;
    color: gray;
}
.communityArticle-title {
    display: inline-block;
    max-width: 280px;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    margin-bottom: 4px;
    vertical-align: middle;
    margin-right: 10px;
}
.communityArticle-name {
    display: inline-block;
    max-width: 290px;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    margin-bottom: 4px;
    vertical-align: middle;
    margin-right: 10px;
}

.communityArticle-brief {
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
}
.img-border {
    overflow: hidden; 
    display: inline-block;
    position: relative;
}
.communityArticle-img {
    width: 130px;
    height: 120px;
    cursor: pointer;
    transition: 0.4s linear all;
}
.communityArticle-img:hover {
    transform: scale(1.2);
}
.MonkeyWebUserHomeCommunityArticle-container {
    background-color: #fff;
    padding: 20px;
    animation: slide-out 0.4s linear;
}
</style>