<template>
    <div class="MonkeyWebUserHomeQuestion-container">
        <el-tabs 
        v-model="activeName" 
        @tab-click="handleClick" 
        class="el-tabs" 
        type="card" 
        style="vertical-align: middle; padding: 20px;">
                    <el-tab-pane name="publish">
                        <span slot="label">
                            <i class="iconfont icon-fabuzhuanjiawenzhang">&nbsp;</i>
                            <span>发布的问题</span>
                        </span>
                    </el-tab-pane>
                    <el-tab-pane name="reply">
                        <span slot="label">
                            <i class="iconfont icon-pinglun">&nbsp;</i>
                            <span>回答的问题</span>
                        </span>
                    </el-tab-pane>

                    <div 
                    @mouseover="question.isHover = '1'"
                        @mouseleave="question.isHover = '0'"
                    class="question-card"
                    v-for="question in questionList"
                    :key="question.id">
                        <div class="question-title">{{ question.title }}</div>
                        <div class="question-brief">{{ question.profile }}</div>
                        <div>
                        <span class="iconfont icon-yanjing operate-common">&nbsp;游览&nbsp;{{ getFormatNumber(question.viewCount) }}</span>
                        <span class="iconfont icon-yanjing operate-common">&nbsp;点赞&nbsp;{{ getFormatNumber(question.likeCount) }}</span>
                        <span class="iconfont icon-shoucang operate-common">&nbsp;收藏&nbsp;{{ getFormatNumber(question.collectCount) }}</span>
                        <span class="iconfont icon-pinglun operate-common">&nbsp;回复&nbsp;{{ getFormatNumber(question.replyCount) }}</span>
                        <span class="operate-common">发表于：{{ getTimeFormat(question.createTime) }}</span>
                        <div
                        v-if="question.isHover == '1' && question.userId == $store.state.user.id" 
                        @mouseover="question.isMoreHover = '1'"
                        @mouseleave="question.isMoreHover = '0'"
                        class="hover el-icon-more-outline">
                            <div v-if="question.isMoreHover == '1'" class="more-hover">
                                <div @click.stop="toQuestionEditViews(question.id)" class="common-hover">编辑</div>
                                <div @click.stop="deleteQuestion(question)" class="common-hover">删除</div>
                            </div>
                        </div>
                        </div>
                        
                    </div>
                </el-tabs>
                <div
                v-if="questionList == null || questionList == '' || questionList == [] || questionList.length <= 0"
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
    name: 'MonkeyWebUserHomeQuestion',
    components: {
        PagiNation
    },
    data() {
        return {
            activeName: "publish",
            userId: "",
            currentPage: 1,
            pageSize: 10,
            totals: 0,
            questionList: [],
            userHomeQuestionUrl: "http://localhost:80/monkey-question/user/home",
        };
    },

    created() {
        this.userId = this.$route.params.userId;
        if (this.activeName == "publish") {
            this.queryPublishQuestion(this.userId);
        } else if (this.activeName == "reply") {
            this.queryReplyQuestion(this.userId);
        }
    },

    methods: {
        // 删除问答
        deleteQuestion(question) {
            this.$modal.confirm(`您确定要删除 ${question.title} 问答？`)
                .then(() => {
                    const vue = this;
                    $.ajax({
                        url: vue.userHomeQuestionUrl + "/deleteQuestion",
                        type: "delete",
                        data: {
                            questionId: question.id
                        },
                        headers: {
                            Authorization: "Bearer " + store.state.user.token,
                        },
                        success(response) {
                            if (response.code == vue.ResultStatus.SUCCESS) {
                                vue.$modal.msgSuccess(response.msg);
                                if (vue.activeName == "publish") {
                                    vue.queryPublishQuestion(vue.userId);
                                } else if (vue.activeName == "reply") {
                                    vue.queryReplyQuestion(vue.userId);
                                }
                            } else {
                                vue.$modal.msgError(response.msg);
                            }
                        }
                    })
                }).catch(() => { });
            
        },
        toQuestionEditViews(questionId) {
            this.$router.push({
                name: "question_edit",
                params: {
                    questionId,
                }
            })
        },
        handleSizeChange(val) {
            this.pageSize = val;
            this.currentPage = 1;
            if (this.activeName == "publish") {
            this.queryPublishQuestion(this.userId);
        } else if (this.activeName == "reply") {
            this.queryReplyQuestion(this.userId);
        }
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            if (this.activeName == "publish") {
                this.queryPublishQuestion(this.userId);
            } else if (this.activeName == "reply") {
                this.queryReplyQuestion(this.userId);
            }
        },
        // 通过用户id查询用户回复问答
        queryReplyQuestion(userId) {
            const vue = this;
            $.ajax({
                url: vue.userHomeQuestionUrl + "/queryReplyQuestion",
                type: "get",
                data: {
                    userId,
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.questionList = response.data.records;
                        vue.totals = response.data.total;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
         // 通过用户id查询用户发布问答
        queryPublishQuestion(userId) {
            const vue = this;
            $.ajax({
                url: vue.userHomeQuestionUrl + "/queryPublishQuestion",
                type: "get",
                data: {
                    userId,
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.questionList = response.data.records;
                        vue.totals = response.data.total;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        handleClick() {
            this.currentPage = 1;
            this.pageSize = 10;
            if (this.activeName == "publish") {
                this.queryPublishQuestion(this.userId);
            } else if (this.activeName == "reply") {
                this.queryReplyQuestion(this.userId);
            }
        },
        getFormatNumber(val) {
            return getFormatNumber(val);
        },
        getTimeFormat(val) {
            return getTimeFormat(val);
        },
    },
};
</script>

<style scoped>
.MonkeyWebUserHomeQuestion-container {
    background-color: #fff;
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
.hover {
    position: absolute;
    right: 30px;
    font-size: 24px;
    animation: slide-out 0.4s linear;
}
.question-brief {
    display: -webkit-box;
    height: 35px;
    color: gray;
    font-size: 14px;
    overflow: hidden;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    text-overflow: ellipsis;
    white-space: normal;
    vertical-align: middle;
}
.question-title {
    display: inline-block;
    max-width: 590px;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    margin-bottom: 4px;
    vertical-align: middle;
    margin-right: 10px;
}
.question-card {
    padding: 20px;
    background-color: #fff;
    cursor: pointer;
}
.operate-common {
    margin-right: 10px;
    font-size: 14px;
    color: gray;
}
::v-deep .el-tabs__header {
    margin: 0;
}
</style>