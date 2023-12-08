<template>
    <div
    v-loading="loading"
    class="MonkeyWebViewContent-container">
        <el-button 
        @click="clearHistoryContent()"
        size="mini" 
        class="clear-content-history">清除历史内容</el-button>
        <el-timeline class="infinite-list" 
        v-infinite-scroll="loadData" 
        infinite-scroll-distance="10"
        style="overflow:auto; max-height: calc(100vh - 175px);">
            <el-timeline-item 
            v-for="historyContent in historyContentList"
            :key="historyContent.id"
            :timestamp="historyContent.createTime" 
            placement="top" 
            class="infinite-list-item">
            <el-card
            style="cursor: pointer; margin-top: 20px;">
                <div @click.stop="toContentViews(historyContent)">
                    <el-row>
                        <el-col :span="4">
                            <div class="picture-card">
                                <img class="content-picture" :src="historyContent.picture" alt="">
                                <div class="content-type">{{ historyContent.typeName }}</div>
                            </div>
                        </el-col>
                        <el-col :span="20">
                            <div class="content-title">{{ historyContent.title }}</div>
                            <div>
                                <img 
                                @click="toUserViews(historyContent.authorId)" 
                                class="userHeadImg" 
                                :src="historyContent.authorHeadImg" alt="">
                                <span 
                                @click="toUserViews(historyContent.authorId)" 
                                class="username">{{ historyContent.authorName }}</span>
                            </div>
                        </el-col>
                    </el-row>
                </div>
            </el-card>
            </el-timeline-item>
        </el-timeline>

        <div
        v-if="historyContentList == null || historyContentList == '' || historyContentList == [] || historyContentList.length <= 0"
        style="text-align: center;" >
            <el-empty description="暂无数据"></el-empty>
        </div>
    </div>
</template>

<script>
import $ from 'jquery'
import store from '@/store';
export default {
    name: 'MonkeyWebViewContent',

    data() {
        return {
            loading: true,
            currentPage: 1,
            pageSize: 10,
            totals: 0,
            historyContentList: [],
            // 是否正在加载数据
            isLoadingData: false,
            historyViewUrl: "http://localhost:80/monkey-user/center/history",
        };
    },

    methods: {
        // 清除用户历史内容
        clearHistoryContent() {
            this.$modal.confirm("是否确实清除用户历史内容？")
            .then(() => {
                const vue = this;
                $.ajax({
                    url: vue.historyViewUrl + "/clearHistoryContent",
                    type: "delete",
                    headers: {
                        Authorization: "Bearer " + store.state.user.token,
                    },
                    success(response) {
                        if (response.code == vue.ResultStatus.SUCCESS) {
                            vue.historyContentList = [];
                            vue.$modal.msgSuccess(response.msg);
                        } else {
                            vue.$modal.msgError(response.msg);
                        }
                    }
                })
            }).catch(() => {});
            
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
        // 前往内容所对应的页面
        toContentViews(historyContent) {
            if (historyContent.type == this.historyView.ARTICLE) {
                // 前往文章页面
                const { href } = this.$router.resolve({
                    name: "check_article",
                    params: {
                        articleId: historyContent.associateId,
                    }
                })
                window.open(href, '_black')
            } else if (historyContent.type == this.historyView.QUESTION) {
                // 前往问答页面
                
                const { href } = this.$router.resolve({
                    name: "question_reply",
                    params: {
                        questionId: historyContent.associateId,
                    }
                })
                window.open(href, '_black')
            } else if (historyContent.type == this.historyView.COURSE) {
                // 前往课程页面
                const { href } = this.$router.resolve({
                    name: "course_detail",
                    params: {
                        courseId: historyContent.associateId,
                    }
                })
                window.open(href, '_black')
            } else if (historyContent.type == this.historyView.COMMUNITY_ARTICLE) {
                // 前往社区文章页面
                const { href } = this.$router.resolve({
                    name: "community_article",
                    params: {
                        communityArticleId: historyContent.associateId,
                        communityId
                    }
                })
                window.open(href, '_black')
            } else if (historyContent.type == this.historyView.RESOURCE) {
                // 前往资源页面
                const { href } = this.$router.resolve({
                    name: "resource_detail",
                    params: {
                        resourceId: historyContent.associateId,
                    }
                })
                window.open(href, '_black')
            }
        },
        loadData() {
            if (this.isLoadingData) {
                return false;
            }

            this.isLoadingData = true;
            this.queryHistoryContent();
        },
        // 查询历史内容集合
        queryHistoryContent() {
            if ((this.currentPage - 1) * this.pageSize > this.totals) {
                this.$modal.msgWarning("没有更多了");
                return false;
            }
            const vue = this;
            vue.loading = true;
            $.ajax({
                url: vue.historyViewUrl + "/queryHistoryContent",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.isLoadingData = false;
                        const record  = response.data.records;
                        vue.totals = response.data.total;
                        for (let i = 0; i < record.length; i ++ ) {
                            vue.historyContentList.push(record[i]);
                        }
                        vue.loading = false;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        }
    },
};
</script>

<style scoped>
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
}
.content-type {
    position: absolute;
    top: 0;
    left: 0;
    color: white;
    font-size: 12px;
    padding: 2px 5px;
    background-image: linear-gradient(to right, #4facfe 0%, #00f2fe 100%);
}
.clear-content-history {
    position: absolute;
    right: 0;
    top: 0px;
    z-index: 1501;
}
.username:hover {
    color: rgb(81, 230, 230);
}
.username {
    display: inline-block;
    white-space: nowrap;
    text-overflow: ellipsis;
    vertical-align: middle;
    overflow: hidden;
    max-width: 634px;
    transition: 0.2s linear all;
    cursor: pointer;
}
.userHeadImg:hover {
    opacity: 0.7;
}
.userHeadImg {
    width: 25px;
    height: 25px;
    border-radius: 50%;
    vertical-align: middle;
    cursor: pointer;
    transition: 0.2s linear all;
    margin-right: 10px;
}
.content-title:hover {
    color: #409EFF;
}
.content-title {
    font-size: 16px;
    font-weight: bold;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    cursor: pointer;
    transition: 0.2s linear all;
    margin-bottom: 5px;
}
.picture-card {
    display: inline-block;
    overflow: hidden;
}
.MonkeyWebViewContent-container {
    vertical-align: middle;
    position: relative;
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
.content-picture:hover {
    transform: scale(1.2);
}
.content-picture {
    width: 118px;
    height: 74px;
    vertical-align: middle;
    cursor: pointer;
    transition: 0.4s linear all;
}
ul {
    padding-inline-start: 0px;
}
</style>