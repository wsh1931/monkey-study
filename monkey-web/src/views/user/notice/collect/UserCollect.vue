<template>
    <div class="MonkeyWebUserCollect-container">
        <div
        class="infinite-list" 
        v-infinite-scroll="loadData" 
        infinite-scroll-distance="30">
            <div 
            @click="toContentViews(messageCollect)"
            v-for="messageCollect in messageCollectList" 
            :key="messageCollect.id" 
            style="cursor: pointer;">
                <el-row class="like-card">
                    <el-col :span="1">
                        <div class="user-headImg-out">
                            <img
                            @click.stop="toUserViews(messageCollect.senderId)"
                            class="user-headImg" 
                            :src="messageCollect.senderHeadImg" alt="">
                        </div>
                    </el-col>
                    <el-col :span="23" class="right-content">
                        <img class="comment-picture" :src="messageCollect.contentPicture" alt="">
                        <div 
                        @click.stop="toUserViews(messageCollect.senderId)" 
                        class="username">{{ messageCollect.senderName }}</div>
                        <div class="time-content">
                            <span class="like-time">{{ getTimeFormat(messageCollect.createTime) }}</span>
                            <span style="margin-right: 5px;">收藏了我的{{ messageCollect.sendType }}</span>
                            <el-tooltip 
                            class="item" 
                            effect="dark" 
                            content="删除消息" 
                            placement="right">
                                <span 
                                @click.stop="deleteCollectMessage(messageCollect.id, index)" 
                                class="el-icon-delete all-already"></span>
                            </el-tooltip>
                        </div>
                        <div class="content">| {{ messageCollect.sendType }}：{{ messageCollect.contentTitle }}</div>
                    </el-col>
                </el-row>
            </div>
        </div>

        <div
        style="text-align: center;" 
        v-if="messageCollectList == null 
        || messageCollectList == '' 
        || messageCollectList == []
        || messageCollectList.length == 0"
        >
            <el-empty description="暂无消息"></el-empty>
        </div>
    </div>
</template>

<script>
import $ from 'jquery'
import store from '@/store';
import { getTimeFormat } from '@/assets/js/DateMethod';
export default {
    name: 'MonkeyWebUserCollect',

    data() {
        return {
            // 分页参数
            currentPage: 1,
            pageSize: 10,
            // 在数据完成之后是否触发滚动
            isScroll: true,
            messageCollectList: [],
            communityArticleUrl: "http://localhost:80/monkey-community/article",
            messageCollectUrl: "http://localhost/monkey-user/message/collect",
        };
    },

    methods: {
        // 父组件调用的查询评论回复消息
        queryCollectMessageOfParent() {
            this.currentPage = 1;
            const vue = this;
            $.ajax({
                url: vue.messageCollectUrl + "/queryCollectMessage",
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
                        const records = response.data.records;
                        vue.messageCollectList = records;
                        vue.$emit("queryNoCheckCollectCount");
                    } else { 
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询收藏消息集合
        queryCollectMessage() {
            const vue = this;
            $.ajax({
                url: vue.messageCollectUrl + "/queryCollectMessage",
                type: 'get',
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const records = response.data.records;
                        for (let i = 0; i < records.length; i++) {
                            vue.messageCollectList.push(records[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                        vue.$emit("queryNoCheckCollectCount");
                    }
                }
            })
        },
        // 前往社区文章页面
        toCommunityArticleViews(communityArticleId) {
            const vue = this;
            $.ajax({
                url: vue.communityArticleUrl + "/queryCommunityIdByArticleId",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    communityArticleId,
                },
                success(response) {
                    if (response.code == '200') {
                        const { href } = vue.$router.resolve({
                            name: "community_article",
                            params: {
                                communityArticleId,
                                communityId: response.data,
                            }
                        })
                        window.open(href, "_blank");
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 前往内容页面
        toContentViews(messageCollect) {
            if (messageCollect.type == this.messageType.article) {
                // 前往文章详情页面
                const { href } = this.$router.resolve({
                    name: "check_article",
                    params: {
                        articleId: messageCollect.associationId,
                    }
                })

                window.open(href, "_blank");
            } else if (messageCollect.type == this.messageType.question) {
                // 前往问答详情页面
                const { href } = this.$router.resolve({
                    name: "question_reply",
                    params: {
                        questionId: messageCollect.associationId,
                    }
                })

                window.open(href, "_blank");
            } else if (messageCollect.type == this.messageType.course) {
                // 前往课程详情页面
                const { href } = this.$router.resolve({
                    name: "course_detail",
                    params: {
                        courseId: messageCollect.associationId,
                    }
                })

                window.open(href, "_blank");
            } else if (messageCollect.type == this.messageType.communityArticle) {
                // 前往社区文章详情页面
                this.toCommunityArticleViews(messageCollect.associationId)
            } else if (messageCollect.type == this.messageType.resource) {
                // 前往资源详情页面
                const { href } = this.$router.resolve({
                    name: "resource_detail",
                    params: {
                        resourceId: messageCollect.associationId,
                    }
                })

                window.open(href, "_blank");
            }
        },
        // 删除点赞消息
        deleteCollectMessage(messageId, idx) {
            const vue = this;
            $.ajax({
                url: vue.messageCollectUrl + "/deleteCollectMessage",
                type: "delete",
                data: {
                    messageId
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.messageCollectList.splice(idx, 1);
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
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
        loadData() {
            if (this.isScroll) {
                this.isScroll = false;
                this.queryCollectMessage();
            }
        },
        getTimeFormat(val) {
            return getTimeFormat(val);
        }
    },
};
</script>

<style scoped>
@keyframes slide-out {
    0% {
        opacity: 0;
    }
    100% {
        opacity: 1;
    }
}
.MonkeyWebUserCollect-container {
    animation: slide-out 0.4s linear;
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
.all-already:hover {
    color: #409EFF;
    cursor: pointer;
}
.comment-picture {
    position: absolute;
    right: 0;
    top: 0;
    height: 80px;
    width: 160px;
    transition: 0.4s linear all;
}
.comment-picture:hover {
    transform: scale(1.1);
    cursor: pointer;
}
.infinite-list {
    overflow:auto; 
    max-height: calc(100vh - 71px);
    border-radius: 10px;
}
.content {
    color: gray;
    font-size: 15px;
    vertical-align: middle;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden; 
}
.time-content {
    font-size: 14px;
    color: gray;
    margin-bottom: 10px;
}
.like-time {
    margin-right: 10px;
}
.username {
    font-weight: 550;
    margin-bottom: 5px;
    vertical-align: middle;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden; 
}
.username:hover {
    color: #409EFF;
    cursor: pointer;
}
.right-content {
    padding-left: 20px;
    position: relative;
}
.user-headImg-out {
    display: inline-block;
    overflow: hidden; 
    border-radius: 50%; 
    text-align: center; 
    height: 50px; 
    width: 50px; 
}
.like-card {
    background-color: #fff;
    padding: 20px;
    margin-bottom: 10px;
    border-radius: 10px;
}
.user-headImg {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    transition: 0.2s linear all;
}
.user-headImg:hover {
    cursor: pointer;
    transform: scale(1.5);
}
</style>