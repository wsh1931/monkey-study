<template>
    <div class="MonkeyWebUserAttention-container">
        <div
        class="infinite-list" 
        v-infinite-scroll="loadData" 
        infinite-scroll-distance="30">
        <div
            @click="toContentViews(messageAttention)"
            v-for="messageAttention in messageAttentionList" 
            :key="messageAttention.id" 
            style="cursor: pointer;">
                <el-row class="like-card">
                    <el-col :span="1">
                        <div class="user-headImg-out">
                            <img
                            @click.stop="toUserViews(messageAttention.senderId)"
                            class="user-headImg" 
                            :src="messageAttention.senderHeadImg" alt="">
                        </div>
                    </el-col>
                    <el-col :span="23" class="right-content">
                        <div 
                        @click.stop="toUserViews(messageAttention.senderId)" 
                        class="username">{{ messageAttention.senderName }}</div>
                        <div class="time-content">
                            <span class="like-time">{{ getTimeFormat(messageAttention.createTime) }}</span>
                            <span style="margin-right: 5px;">关注了我</span>
                            <el-tooltip 
                            class="item" 
                            effect="dark" 
                            content="删除消息" 
                            placement="right">
                                <span 
                                @click.stop="deleteAttentionMessage(messageAttention.id, index)" 
                                class="el-icon-delete all-already"></span>
                            </el-tooltip>
                        </div>
                    </el-col>
                </el-row>
            </div>

        <div
        style="text-align: center;" 
        v-if="messageAttentionList == null 
        || messageAttentionList == '' 
        || messageAttentionList == []
        || messageAttentionList.length == 0"
        >
            <el-empty description="暂无消息"></el-empty>
        </div>
        </div>
    </div>
</template>

<script>
import $ from 'jquery'
import store from '@/store';
import { getTimeFormat } from '@/assets/js/DateMethod';
export default {
    name: 'MonkeyWebUserAttention',
    data() {
        return {
            // 消息关注集合
            messageAttentionList: [],
            // 分页参数
            currentPage: 1,
            pageSize: 10,
            // 在数据完成之后是否触发滚动
            isScroll: true,
            messageAttentionUrl: "http://localhost/monkey-user/message/attention",
        };
    },

    mounted() {
        
    },

    methods: {
        loadData() {
            if (this.isScroll) {
                this.isScroll = false;
                this.queryAttentionMessage();
            }
        },
        // 父组件调用的查询评论回复消息
        queryAttentionMessageOfParent() {
            this.currentPage = 1;
            const vue = this;
            $.ajax({
                url: vue.messageAttentionUrl + "/queryAttentionMessage",
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
                        vue.messageAttentionList = records;
                        vue.$emit("queryNoCheckAttentionCount");
                    } else { 
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询关注消息集合
        queryAttentionMessage() {
            const vue = this;
            $.ajax({
                url: vue.messageAttentionUrl + "/queryAttentionMessage",
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
                            vue.messageAttentionList.push(records[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                        vue.$emit("queryNoCheckAttentionCount");
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
        // 删除关注消息
        deleteAttentionMessage(messageId, idx) {
            const vue = this;
            $.ajax({
                url: vue.messageAttentionUrl + "/deleteAttentionMessage",
                type: "delete",
                data: {
                    messageId
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.messageAttentionList.splice(idx, 1);
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
                }
            })

            window.open(href, "_blank")
        },
        getTimeFormat(val) {
            return getTimeFormat(val);
        }
    },
};
</script>

<style scoped>
.all-already:hover {
    color: #409EFF;
    cursor: pointer;
}
.infinite-list {
    overflow:auto; 
    max-height: calc(100vh - 71px);
    border-radius: 10px;
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
@keyframes slide-out {
    0% {
        opacity: 0;
    }
    100% {
        opacity: 1;
    }
}
.MonkeyWebUserAttention-container {
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
</style>