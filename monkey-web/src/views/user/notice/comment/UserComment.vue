<template>
    <div class="MonkeyWebUserComment-container">
        <el-row 
        class="infinite-list" 
        v-infinite-scroll="loadData" 
        infinite-scroll-distance="30">
        <div 
        @click="toContentViews(commentReply)"
        v-for="(commentReply, index) in commentReplyList" 
        :key="commentReply.id"  
        class="infinite-list-item user-comment-card">
            <el-row>
                <el-col :span="1">
                    <div class="reply-user-headImg-out">
                        <img 
                        @click.stop="toUserViews(commentReply.senderId)" 
                        class="reply-user-headImg" 
                        :src="commentReply.senderHeadImg" alt="">
                    </div>
                </el-col>
                <el-col :span="23" class="user-comment-card-right">
                    <img class="comment-picture" :src="commentReply.contentPicture" alt="">
                    <div @click.stop="toUserViews(commentReply.senderId)" class="user-name">{{ commentReply.senderName }}</div>
                    <div class="comment-time-des">
                        <span class="comment-time">{{ getTimeFormat(commentReply.createTime)}}</span>
                        <span 
                        v-if="commentReply.isComment == '0'" 
                        style="margin-right: 10px;">评论了我的{{ commentReply.sendType }}</span>
                        <span 
                        v-if="commentReply.isComment == '1'" 
                        style="margin-right: 10px;">回复了我的评论&nbsp;({{ commentReply.sendType }})</span>
                        <el-tooltip 
                        class="item" 
                        effect="dark" 
                        content="删除消息" 
                        placement="right">
                            <span 
                            @click.stop="deleteCommentReply(commentReply.id, index)" 
                            class="el-icon-delete all-already"></span>
                        </el-tooltip>
                    </div>
                    <div class="comment-content">{{ commentReply.sendContent }}</div>
                    <div 
                    v-if="commentReply.isComment == '0'" 
                    class="comment-title">|&nbsp;原文: {{ commentReply.contentTitle }}</div>

                    <div 
                    v-if="commentReply.isComment == '1'" 
                    class="comment-title">|&nbsp;回复: {{ commentReply.contentTitle }}</div>
                </el-col>
            </el-row>
        </div>
        </el-row>
        <el-row 
        style="text-align: center;" 
        v-if="commentReplyList == null 
        || commentReplyList == '' 
        || commentReplyList == []
        || commentReplyList.length == 0"
        >
            <el-empty description="暂无消息"></el-empty>
        </el-row>
    </div>
</template>

<script>
import $ from 'jquery'
import store from '@/store';
import { getTimeFormat } from '@/assets/js/DateMethod';
export default {
    name: 'MonkeyWebUserComment',
    data() {
        return {
            // 分页参数
            currentPage: 1,
            pageSize: 10,
            // 评论回复集合
            commentReplyList: [],
            // 在数据完成之后是否触发滚动
            isScroll: true,
            commentReplyUrl: "http://localhost/monkey-user/message/comment/reply",
            messageCenterUrl: "http://localhost/monkey-user/message/center",
            communityArticleUrl: "http://localhost:80/monkey-community/article",
        };
    },
    methods: {
        // 前往内容页面
        toContentViews(commentReply) {
            if (commentReply.type == this.messageType.article) {
                // 前往文章详情页面
                const { href } = this.$router.resolve({
                    name: "check_article",
                    params: {
                        articleId: commentReply.associationId,
                    }
                })

                window.open(href, "_blank");
            } else if (commentReply.type == this.messageType.question) {
                // 前往问答详情页面
                const { href } = this.$router.resolve({
                    name: "question_reply",
                    params: {
                        questionId: commentReply.associationId,
                    }
                })

                window.open(href, "_blank");
            } else if (commentReply.type == this.messageType.course) {
                // 前往课程详情页面
                const { href } = this.$router.resolve({
                    name: "course_detail",
                    params: {
                        courseId: commentReply.associationId,
                    }
                })

                window.open(href, "_blank");
            } else if (commentReply.type == this.messageType.communityArticle) {
                // 前往社区文章详情页面
                this.toCommunityArticleViews(commentReply.associationId)
            } else if (commentReply.type == this.messageType.resource) {
                // 前往资源详情页面
                const { href } = this.$router.resolve({
                    name: "resource_detail",
                    params: {
                        resourceId: commentReply.associationId,
                    }
                })

                window.open(href, "_blank");
            }
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
        // 前往用户主页
        toUserViews(userId) {
            const { href } = this.$router.resolve({
                name: "user_home",
                params: {
                    userId
                }
            })

            window.open(href, "_blank")
        },
        // 删除评论回复消息
        deleteCommentReply(commentReplyId, idx) {
            const vue = this;
            $.ajax({
                url: vue.commentReplyUrl + "/deleteCommentReply",
                type: "delete",
                data: {
                    commentReplyId
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.commentReplyList.splice(idx, 1);
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询未查看评论回复消息数
        queryNoCheckCommentCount() {
            const vue = this;
            $.ajax({
                url: vue.messageCenterUrl + "/queryNoCheckCommentCount",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.unCheckCommentCount = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        loadData() {
            if (this.isScroll) {
                this.isScroll = false;
                this.queryCommentReplyMessage();
            }
        },
        // 查询评论回复消息
        queryCommentReplyMessage() {
            const vue = this;
            $.ajax({
                url: vue.commentReplyUrl + "/queryCommentReplyMessage",
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
                        vue.currentPage++;
                        const records = response.data.records;
                        for (let i = 0; i < records.length; i++) {
                            vue.commentReplyList.push(records[i]);
                        }
                        vue.$emit("queryNoCheckCommentCount");
                        vue.isScroll = true;
                    } else { 
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 父组件调用的查询评论回复消息
        queryCommentReplyMessageOfParent() {
            this.currentPage = 1;
            const vue = this;
            $.ajax({
                url: vue.commentReplyUrl + "/queryCommentReplyMessage",
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
                        vue.commentReplyList = records;
                        vue.$emit("queryNoCheckCommentCount");
                    } else { 
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        getTimeFormat(val) {
            return getTimeFormat(val);
        }
    },
};
</script>

<style scoped>
.infinite-list {
    overflow:auto; 
    max-height: calc(100vh - 71px);
    border-radius: 10px;
}
@keyframes slide-out {
    0% {
        opacity: 0;
    }
    100% {
        opacity: 1;
    }
}
.MonkeyWebUserComment-container {
    height: 100%;
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
    height: 100px;
    width: 200px;
    transition: 0.4s linear all;
}
.comment-picture:hover {
    transform: scale(1.1);
}
.user-comment-card-right {
    padding-left: 20px;
    position: relative;
}

.comment-title {
    color: gray;
    margin-bottom: 10px;
    max-width: 600px;
    vertical-align: middle;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden; 
}
.comment-content {
    display: inline-block;
    font-weight: 600;
    margin-bottom: 10px;
    max-width: 600px;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden; 
    vertical-align: middle;
}
.comment-time-des {
    vertical-align: middle;
    font-size: 14px;
    color: gray;
    margin-bottom: 10px;
}
.user-name {
    margin-bottom: 5px;
    max-width: 600px;
    vertical-align: middle;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden; 
    vertical-align: middle;
}
.user-name:hover {
    cursor: pointer;
    color: #409EFF;
}
.comment-time {
    margin-right: 10px;
}
.reply-user-headImg-out {
    display: inline-block;
    overflow: hidden; 
    border-radius: 50%; 
    text-align: center; 
    height: 50px; 
    width: 50px; 
}
.reply-user-headImg:hover {
    cursor: pointer;
    transform: scale(1.5);
}
.reply-user-headImg {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    transition: 0.4s linear all;
}
.user-comment-card {
    padding: 20px;
    background-color: #fff;
    border-radius: 10px;
    box-shadow: 0 0 5px 0 rgba(0, 0, 0, 0.1); 
    margin-bottom: 10px;
    cursor: pointer;
}
</style>