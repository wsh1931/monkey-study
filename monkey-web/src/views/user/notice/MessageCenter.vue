<template>
    <div class="MonkeyWebMessageCenter-container">
        <el-row class="container" style="height: 100%;">
            <el-col :span="4" class="stable-nav">
                <el-menu
                router
                style="height: 100%;"
                    :default-active="defaultActive"
                    class="el-menu-vertical-demo"
                    text-color="black"
                    active-text-color="#409EFF">
                    <div style="padding: 10px 10px 5px 10px;">
                        <span class="navigation-title">消息中心</span>
                        <el-tooltip class="item" effect="dark" content="将所有消息置为已读" placement="right">
                        <span class="iconfont icon-yidu all-already"></span>
                        </el-tooltip>

                        <el-tooltip class="item" effect="dark" content="删除所有已读消息" placement="right">
                            <span class="el-icon-delete all-already"></span>
                        </el-tooltip>
                    </div>
                    
                    <el-menu-item index="/message/comment" class="el-menu-item">
                        <i class="iconfont icon-pinglun">&nbsp;</i>
                        <span slot="title">评论和@&nbsp;</span>
                        <el-tooltip 
                        slot="title" 
                        class="item" 
                        effect="dark" 
                        content="将所有评论回复消息置为已读" 
                        placement="top">
                            <span 
                            class="iconfont icon-yidu"
                            @click="updateAllCommentReplyAlready()"></span>
                        </el-tooltip>
                        <el-tooltip 
                        slot="title" 
                        class="item" 
                        effect="dark" 
                        content="删除所有评论回复已读消息" 
                        placement="top">
                            <span 
                            class="el-icon-delete"
                            @click="deleteAllCommentMessageOfAlreadyRead()"></span>
                        </el-tooltip>
                        
                        <el-badge v-if="unCheckCommentCount != 0" slot="title" :value="unCheckCommentCount" :max="99" class="item">
                        </el-badge>
                    </el-menu-item>
                    <el-menu-item index="/message/like" class="el-menu-item">
                        <i class="iconfont icon-dianzan">&nbsp;</i>
                        <span slot="title">新增点赞&nbsp;</span>
                        <el-tooltip 
                        slot="title" 
                        class="item" 
                        effect="dark" 
                        content="将所有点赞消息置为已读" 
                        placement="top">
                            <span 
                            class="iconfont icon-yidu"
                            @click="updateAllLikeAlready()"></span>
                        </el-tooltip>
                        <el-tooltip 
                        slot="title" 
                        class="item" 
                        effect="dark" 
                        content="删除所有点赞已读消息" 
                        placement="top">
                            <span 
                            class="el-icon-delete"
                            @click="deleteAllLikeMessageOfAlreadyRead()"></span>
                        </el-tooltip>
                        <el-badge v-if="unCheckLikeCount != 0" slot="title" :value="unCheckLikeCount" :max="99" class="item">
                        </el-badge>
                    </el-menu-item>
                    <el-menu-item index="/message/collect" class="el-menu-item">
                        <i class="iconfont icon-shoucang">&nbsp;</i>
                        <span slot="title">新增收藏&nbsp;</span>
                        <el-tooltip 
                        slot="title" 
                        class="item" 
                        effect="dark" 
                        content="将所有收藏消息置为已读" 
                        placement="top">
                            <span 
                            class="iconfont icon-yidu"
                            @click="updateAllCollectAlready()"></span>
                        </el-tooltip>
                        <el-tooltip 
                        slot="title" 
                        class="item" 
                        effect="dark" 
                        content="删除所有收藏已读消息" 
                        placement="top">
                            <span 
                            class="el-icon-delete"
                            @click="deleteAllCollectMessageOfAlreadyRead()"></span>
                        </el-tooltip>
                        <el-badge v-if="unCheckCollectCount != 0" slot="title" :value="unCheckCollectCount" :max="99" class="item">
                        </el-badge>
                    </el-menu-item>
                    <el-menu-item index="/message/attention" class="el-menu-item">
                        <i class="iconfont icon-31guanzhu">&nbsp;</i>
                        <span slot="title">新增关注&nbsp;</span>
                        <el-tooltip 
                        slot="title" 
                        class="item" 
                        effect="dark" 
                        content="将所有关注消息置为已读" 
                        placement="top">
                            <span 
                            class="iconfont icon-yidu"
                            @click="updateAllAttentionAlready()"></span>
                        </el-tooltip>
                        <el-tooltip 
                        slot="title" 
                        class="item" 
                        effect="dark" 
                        content="删除所有关注已读消息" 
                        placement="top">
                            <span 
                            class="el-icon-delete"
                            @click="deleteAllAttentionMessageOfAlreadyRead()"></span>
                        </el-tooltip>
                        <el-badge v-if="unCheckAttentionCount != 0" slot="title" :value="unCheckAttentionCount" :max="99" class="item">
                        </el-badge>
                    </el-menu-item>
                    <el-menu-item index="/message/system" class="el-menu-item">
                        <i class="iconfont icon-gonggao">&nbsp;</i>
                        <span slot="title">官方通知</span>
                    </el-menu-item>
                </el-menu>
            </el-col>
            <el-col :span="20" style="padding-left: 10px;">
                <router-view 
                @queryNoCheckCommentCount="queryNoCheckCommentCount" 
                ref="childrenComponent"
                @queryNoCheckLikeCount="queryNoCheckLikeCount"
                @queryNoCheckCollectCount="queryNoCheckCollectCount"
                @queryNoCheckAttentionCount="queryNoCheckAttentionCount"></router-view>
            </el-col>
        </el-row>
    </div>
</template>

<script>
import $ from 'jquery'
import store from '@/store';
export default {
    name: 'MonkeyWebMessageCenter',

    data() {
        return {
            defaultActive: "",
            // 未查看评论数
            unCheckCommentCount: 0,
            // 未查看点赞数
            unCheckLikeCount: 0,
            // 未查看收藏数
            unCheckCollectCount: 0,
            // 未查看关注消息数
            unCheckAttentionCount: 0,
            messageCenterUrl: "http://localhost/monkey-user/message/center",
            commentReplyUrl: "http://localhost/monkey-user/message/comment/reply",
            messageLikeUrl: "http://localhost/monkey-user/message/like",
            messageCollectUrl: "http://localhost/monkey-user/message/collect",
            messageAttentionUrl: "http://localhost/monkey-user/message/attention",
        };
    },
    created() {
        this.defaultActive = this.$route.fullPath;
    },

    methods: {
        // 删除所有关注已读消息
        deleteAllAttentionMessageOfAlreadyRead() {
            const vue = this;
            $.ajax({
                url: vue.messageAttentionUrl + "/deleteAllAttentionMessageOfAlreadyRead",
                type: "delete",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        this.unCheckAttentionCount = 0;
                        vue.$refs.childrenComponent.queryAttentionMessageOfParent();
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 将所有关注消息置为已读
        updateAllAttentionAlready() {
            const vue = this;
            $.ajax({
                url: vue.messageAttentionUrl + "/updateAllAttentionAlready",
                type: "put",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.unCheckAttentionCount = 0;
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询未查看消息关注数
        queryNoCheckAttentionCount() {
            const vue = this;
            $.ajax({
                url: vue.messageCenterUrl + "/queryNoCheckAttentionCount",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.unCheckAttentionCount = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 删除所有收藏已读消息
        deleteAllCollectMessageOfAlreadyRead() {
            const vue = this;
            $.ajax({
                url: vue.messageCollectUrl + "/deleteAllCollectMessageOfAlreadyRead",
                type: "delete",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        this.unCheckCollectCount = 0;
                        vue.$refs.childrenComponent.queryCollectMessageOfParent();
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 将所有收藏消息置为已读
        updateAllCollectAlready() {
            const vue = this;
            $.ajax({
                url: vue.messageCollectUrl + "/updateAllCollectAlready",
                type: "put",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.unCheckCollectCount = 0;
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询未查看消息收藏数
        queryNoCheckCollectCount() {
            const vue = this;
            $.ajax({
                url: vue.messageCenterUrl + "/queryNoCheckCollectCount",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.unCheckCollectCount = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询未查看消息点赞数
        queryNoCheckLikeCount() {
            const vue = this;
            $.ajax({
                url: vue.messageCenterUrl + "/queryNoCheckLikeCount",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.unCheckLikeCount = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 删除所有评论回复已读消息
        deleteAllCommentMessageOfAlreadyRead() {
            const vue = this;
            $.ajax({
                url: vue.commentReplyUrl + "/deleteAllCommentMessageOfAlreadyRead",
                type: "delete",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        this.unCheckCommentCount = 0;
                        vue.$refs.childrenComponent.queryCommentReplyMessageOfParent();
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 将所有评论回复置为已读
        updateAllCommentReplyAlready() {
            const vue = this;
            $.ajax({
                url: vue.commentReplyUrl + "/updateAllCommentReplyAlready",
                type: "put",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.unCheckCommentCount = 0;
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 删除所有点赞已读消息
        deleteAllLikeMessageOfAlreadyRead() {
            const vue = this;
            $.ajax({
                url: vue.messageLikeUrl + "/deleteAllLikeMessageOfAlreadyRead",
                type: "delete",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        this.unCheckLikeCount = 0;
                        vue.$refs.childrenComponent.queryLikeMessageOfParent();
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 将所有点赞消息置为已读
        updateAllLikeAlready() {
            const vue = this;
            $.ajax({
                url: vue.messageLikeUrl + "/updateAllLikeAlready",
                type: "put",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.unCheckLikeCount = 0;
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
                        vue.unCheckCommentCount = response.data
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
.container {
    height: 100%;
}
.stable-nav {
    height: 100%;
    overflow-y: auto;
}

:hover::-webkit-scrollbar-thumb:vertical {
    background-color: rgba(0, 0, 0, 0.1);
    border-radius: 6px;
    outline: 2px solid #fff;
    outline-offset: -2px;
    border: 2px solid #fff;
}

.all-already {
    cursor: pointer;
}
.all-already:hover {
    color: #409EFF;
}
.navigation-title {
    font-weight: 600;
    margin-right: 5px;
    text-align: center;
}
.el-menu-item {
    text-align: center;
    border-radius: 10px;
    font-size: 16px;
    vertical-align: middle;
}
.el-menu-item:hover {
    background-color: rgba(0, 0, 0, 0.1);
}
::v-deep .el-menu-vertical-demo {
    border-radius: 10px;
    text-align: center;
    padding: 0 10px;
    font-size: 16px;
}

.MonkeyWebMessageCenter-container {
    margin: 0 auto;
    margin-top: 10px;
    width: 1100px;
    height: calc(100vh - 72px);
}
</style>