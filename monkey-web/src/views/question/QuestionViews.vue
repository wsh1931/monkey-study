<template>
    <div class="QuestionView-container">
        <el-container>
        <el-main style="background-color: #FFFFFF;">
            <el-tabs v-model="tabName">
                <el-tab-pane label="最新问答" style="padding: 10px;" name="latest">
                    <QuestionCard
                    :questionList="questionList"/>

                </el-tab-pane>
                <el-tab-pane label="最热问答" style="padding: 10px;" name="hottest">
                    <QuestionCard
                    :questionList="questionList"/>
                </el-tab-pane>
                <el-tab-pane label="等你来答" style="padding: 10px;" name="wait">
                    <QuestionCard
                    :questionList="questionList"/>
                </el-tab-pane>
                <PagiNation
                    :totals="totals"
                    :currentPage="currentPage" 
                    :pageSize="pageSize" 
                    @handleCurrentChange = "handleCurrentChange"
                    @handleSizeChange="handleSizeChange"/>
            </el-tabs>
        </el-main>
        <el-aside class="el-aside">
            <el-row style="margin: 10px 10px;">
                <el-button icon="el-icon-question" @click="toPublishQuestion()" type="success">提问</el-button>
            </el-row>
            <el-row>
            <div style="margin: 10px 10px;">
                <el-row>热门回答</el-row>
                <el-row style="margin-top: 10px;" v-for="rightQuestion in rightHottestList" :key="rightQuestion.id">
                    <div @click="toQuestionReply(rightQuestion.id)">
                        <el-col :span="4" class="sort">{{ rightQuestion.sort }}</el-col>
                        <el-col class="ellipsis hover" style="margin-left: 2px; font-size: 14px;" :span="20">{{ rightQuestion.title }}</el-col>
                    </div>
                </el-row>
            </div>
        </el-row>
        </el-aside>
        </el-container>
    </div>
</template>

<script>
import $ from "jquery"
import QuestionCard from '@/components/question/QuestionCard'
import PagiNation from "@/components/pagination/PagiNation.vue"
import store from "@/store"

 export default {
    name: 'QuestionionView',
    components: {
        QuestionCard,
        PagiNation,
    },
    data() {
        return {
            questionUrl: "http://localhost:80/monkey-question/question",
            tabName: "latest",
            currentPage: 1,
            pageSize: 10,
            totals: 0,
            questionList: [],
            rightHottestList: [],
        }
    },
    watch: {
        tabName(val) {
            this.currentPage = 1;
            this.pageSize = 10;
            if (val == "latest") {
                this.getLastQuestionList();
            } else if (val == "hottest") {
                this.getHottestQuestionList();
            } else if (val == "wait") {
                this.getWaitYouQuestionList();
            }
        }
    },
    created() {
        this.getLastQuestionList();
        this.getRightHottestQuestionList();
    },
    methods: {
        handleSizeChange(val) {
            this.pageSize = val;
            if (this.tabName == "latest") {
                this.getLastQuestionList();
            } else if (this.tabName == "hottest") {
                this.getHottestQuestionList();
            } else if (this.tabName == "wait") {
                this.getWaitYouQuestionList();
            }
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            if (this.tabName == "latest") {
                this.getLastQuestionList();
            } else if (this.tabName == "hottest") {
                this.getHottestQuestionList();
            } else if (this.tabName == "wait") {
                this.getWaitYouQuestionList();
            }
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
        // 点击提问，跳转至发布提问界面
        toPublishQuestion() {
            this.$router.push({
                name: "publish_question"
            })
        },
        // 得到右侧热门回答列表
        getRightHottestQuestionList() {
            const vue = this;
            $.ajax({
                url: vue.questionUrl + "/getRightHottestQuestionList",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.rightHottestList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
        },
        // 得到等你来答列表
        getWaitYouQuestionList() {
            const vue = this;
            $.ajax({
                url: vue.questionUrl + "/getWaitYouQuestionList",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.totals = response.data.total;
                        vue.questionList = response.data.records;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
        },
        // 得到最热文章列表
        getHottestQuestionList() {
            const vue = this;
            $.ajax({
                url: vue.questionUrl + "/getHottestQuestionList",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.totals = response.data.total;
                        vue.questionList = response.data.records;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
        },
        // 得到最新问答列表
        getLastQuestionList() {
            const vue = this;
            $.ajax({
                url: vue.questionUrl + "/getLatestQuestionList",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.totals = response.data.total;
                        vue.questionList = response.data.records;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
        },
    }
 }
</script>

<style scoped>
.sort {
    background-color: #FEF0F0; 
    color: #F9926C;
    width: 25px; 
    height: 25px; 
    text-align: center;
}
.el-aside {
    width: 300px;
    background-color: #EEEEEE; 
    margin-left: 10px;
}
.QuestionView-container {
    display: flex; 
    justify-content: center;
    align-items: center; 
    width: 1200px; 
    margin: 10px auto;
}
.hover:hover {
    color: #409EFF;
    background-color: #EEEEEE;
    cursor: pointer;
}
.ellipsis {
    color: gray;
  display: block;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  text-align: left;
}
</style>