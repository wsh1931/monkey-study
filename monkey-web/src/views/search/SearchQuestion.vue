<template>
    <div class="MonkeyWebSearchQuestion-container">
        <el-tabs v-model="activeName" @tab-click="handleClick" >
            <el-tab-pane label="综合" name="comprehensive"></el-tab-pane>
            <el-tab-pane label="最新" name="latest"></el-tab-pane>
            <el-tab-pane label="最热" name="hire"></el-tab-pane>
            <el-tab-pane label="游览" name="view"></el-tab-pane>
            <el-tab-pane label="点赞" name="like"></el-tab-pane>
            <el-tab-pane label="收藏" name="collect"></el-tab-pane>
            <el-tab-pane label="回复" name="reply"></el-tab-pane>

            <div
            class="infinite-list" 
            v-infinite-scroll="loadData" 
            infinite-scroll-distance="30">
                <div 
                @click="toQuestionReply(question.id)"
                v-for="question in questionList" :key="question.id" 
                style="cursor: pointer;">
                    <el-row style="margin-bottom: 5px;">
                        <el-col :span="1">
                            <img @click="toUserViews(question.userId)" class="user-headImg" :src="question.userHeadImg" alt="">
                        </el-col>
                        <el-col :span="23" class="card-right">
                            <img class="question-picture" :src="question.photo" alt="">
                            <div class="font-class">
                                <span @click="toUserViews(question.userId)" class="username">{{ question.username }}</span>
                                <span class="publishTime">{{ getTimeFormat(question.createTime) }}</span>
                            </div>
                            <div class="brief">{{ question.userBrief }}</div>
                        </el-col>
                    </el-row>

                    <div class="question-title" v-html="question.title">
                    </div>

                    <div class="question-content" v-html="question.profile">
                    </div>
                    <!-- <div style="margin-bottom: 10px;">
                        <el-tag type="info" size="mini">标签三</el-tag>
                    </div> -->

                    <div style="vertical-align: middle;">
                        <span class="iconfont icon-yanjing operate-common">&nbsp;{{ getFormatNumber(question.viewCount) }}</span>
                        <span class="iconfont icon-dianzan operate-common">&nbsp;{{ getFormatNumber(question.likeCount) }}</span>
                        <span class="iconfont icon-shoucang operate-common">&nbsp;{{ getFormatNumber(question.collectCount) }}</span>
                        <span class="iconfont icon-pinglun operate-common">&nbsp;{{ getFormatNumber(question.replyCount) }}</span>
                        <el-tag 
                        type="info" 
                        size="small" 
                        style="margin-right: 10px;"
                        v-for="label in question.labelName" :key="label"
                        v-html="label"></el-tag>

                    </div>
                    <div class="divisor"></div>
                </div>
            </div>

        <div
        v-if="questionList == null || questionList == '' || questionList == [] || questionList.length <= 0"
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
    name: 'MonkeyWebSearchQuestion',
    data() {
        return {
            isScroll: true,
            activeName: "comprehensive",
            currentPage: 1,
            pageSize: 10,
            // 关键词搜索字段
            keyword: this.$route.query.keyword,
            // 问答集合
            questionList: [],
            searchQuestionUrl: "http://localhost:80/monkey-search/question",
            questionUrl: "http://localhost:80/monkey-question/question",
        };
    },

    mounted() {
        
    },

    methods: {
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
        questionViewCountAddOne(questionId) {
            const vue = this;
            $.ajax({
                url: vue.questionUrl + "/questionViewCountAddOne",
                type: "get",
                data: {
                    questionId
                },
                success(response) {
                    if (response.code != vue.ResultStatus.SUCCESS) {
                        vue.$modal.msgError(response.msg);
                    }
                },
                
            })
        },
        // 跳转至问答回复界面
        toQuestionReply(questionId) {
             // 问答游览数 + 1
            const { href } = this.$router.resolve({
                name: "question_reply",
                params: {
                    questionId
                }
            })

            this.questionViewCountAddOne(questionId);
            window.open(href, '_black');
        },
        handleClick() {
            this.isScroll = false;
            this.currentPage = 1;
            this.questionList = [];
            if (this.activeName == 'comprehensive') {
                    this.queryComprehensiveQuestion();
                } else if (this.activeName == "latest") {
                    this.queryLatestQuestion();
                } else if (this.activeName == "hire") {
                    this.queryHireQuestion();
                } else if (this.activeName == "view") {
                    this.queryViewQuestion();
                } else if (this.activeName == "like") {
                    this.queryLikeQuestion();
                } else if (this.activeName == "collect") {
                    this.queryCollectQuestion();
                } else if (this.activeName == "reply") {
                    this.queryReplyQuestion();
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
                if (this.activeName == 'comprehensive') {
                    this.queryComprehensiveQuestion();
                } else if (this.activeName == "latest") {
                    this.queryLatestQuestion();
                } else if (this.activeName == "hire") {
                    this.queryHireQuestion();
                } else if (this.activeName == "view") {
                    this.queryViewQuestion();
                } else if (this.activeName == "like") {
                    this.queryLikeQuestion();
                } else if (this.activeName == "collect") {
                    this.queryCollectQuestion();
                } else if (this.activeName == "reply") {
                    this.queryReplyQuestion();
                }
            }
        },
        // 查询回复最多问答列表
        queryReplyQuestion() {
            const vue = this;
            $.ajax({
                url: vue.searchQuestionUrl + "/queryReplyQuestion",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data;
                        for (let i = 0; i < data.length; i++) {
                            vue.questionList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询收藏数最多问答列表
        queryCollectQuestion() {
            const vue = this;
            $.ajax({
                url: vue.searchQuestionUrl + "/queryCollectQuestion",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data;
                        for (let i = 0; i < data.length; i++) {
                            vue.questionList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询点赞数最多问答列表
        queryLikeQuestion() {
            const vue = this;
            $.ajax({
                url: vue.searchQuestionUrl + "/queryLikeQuestion",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data;
                        for (let i = 0; i < data.length; i++) {
                            vue.questionList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询游览数最多问答列表
        queryViewQuestion() {
            const vue = this;
            $.ajax({
                url: vue.searchQuestionUrl + "/queryViewQuestion",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data;
                        for (let i = 0; i < data.length; i++) {
                            vue.questionList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询最热问答列表
        queryHireQuestion() {
            const vue = this;
            $.ajax({
                url: vue.searchQuestionUrl + "/queryHireQuestion",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data;
                        for (let i = 0; i < data.length; i++) {
                            vue.questionList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询最新问答列表
        queryLatestQuestion() {
            const vue = this;
            $.ajax({
                url: vue.searchQuestionUrl + "/queryLatestQuestion",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data;
                        for (let i = 0; i < data.length; i++) {
                            vue.questionList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询综合问答列表
        queryComprehensiveQuestion() {
            const vue = this;
            $.ajax({
                url: vue.searchQuestionUrl + "/queryComprehensiveQuestion",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data;
                        for (let i = 0; i < data.length; i++) {
                            vue.questionList.push(data[i]);
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
.question-content {
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
.question-content:hover {
    opacity: 0.8;
}
.question-title {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 10px;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    max-width: 600px;
    transition: 0.2s linear all;
}
.question-title:hover {
    opacity: 0.8;
}
.question-picture {
    position: absolute;
    right: 10px;
    top: 0;
    height: 100px;
    width: 150px;
    border-radius: 0;
    transition: 0.2s linear all;
}

.question-picture:hover {
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
    margin-right: 10px;
    font-size: 14px;
    transition: 0.2s linear all;

}
.username:hover {
    color: #409EFF;
}
.MonkeyWebSearchQuestion-container {
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