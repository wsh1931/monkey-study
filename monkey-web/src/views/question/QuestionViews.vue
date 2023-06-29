<template>
    <div class="QuestionView-container"
        style="display: flex; 
        justify-content: center;
        align-items: center; 
        width: 1200px; 
        margin: 10px auto;"
        >
        <el-container>
        <el-main style="background-color: #FFFFFF;">
            <el-tabs v-model="tabName">
                <el-tab-pane label="最新" style="padding: 5px;" name="latest">
                    <QuestionCard
                    :questionList="questionList"/>

                </el-tab-pane>
                <el-tab-pane label="最热" style="padding: 5px;" name="hottest">
                    <QuestionCard
                    :questionList="questionList"/>
                </el-tab-pane>
                <el-tab-pane label="等你来答" style="padding: 5px;" name="wait">
                    <QuestionCard
                    :questionList="questionList"/>
                </el-tab-pane>
                <el-tab-pane label="为你推荐" style="padding: 5px;" name="recommend">
                    未完成
                </el-tab-pane>
                <PagiNation
                    :totals="totals"
                    :currentPage="currentPage" 
                    :pageSize="pageSize" 
                    @handleCurrentChange = "handleCurrentChange"
                    @handleSizeChange="handleSizeChange"/>
            </el-tabs>
        </el-main>
        <el-aside width="300px" style="background-color: #EEEEEE; margin-left: 10px;">
            <el-row style="margin: 10px 10px;">
                <el-button icon="el-icon-question" @click="toPublishQuestion()" type="success">提问</el-button>
            </el-row>
            <el-row>
            <div style="margin: 10px 10px;">
                <el-row>热门回答</el-row>
                <el-row style="margin-top: 10px;" v-for="rightQuestion in rightHottestList" :key="rightQuestion.id">
                    <div @click="toQuestionReply(rightQuestion.id)">
                        <el-col :span="4" style="background-color: #FEF0F0; color: #F9926C; width: 25px; height: 25px; text-align: center;">{{ rightQuestion.sort }}</el-col>
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
            this.currentPage = 1
            if (val == "latest") {
                this.getLastQuestionList();
            } else if (val == "hottest") {
                this.getHottestQuestionList();
            } else if (val == "wait") {
                this.getWaitYouQuestionList();
            } else if (val == "recommend") {
                this.currentPage = 1;
                this.totals = 0;
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
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            if (this.tabName == "latest") {
                this.getLastQuestionList();
            } else if (this.tabName == "hottest") {
                this.getHottestQuestionList();
            } else if (this.tabName == "wait") {
                this.getWaitYouQuestionList();
            } else if (this.tabName == "recommend") {
                console.log(val);
            }
        },
        // 跳转至问答回复界面
        toQuestionReply(questionId) {
            this.$router.push({
                name: "question_reply",
                params: {
                    questionId
                }
            })
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
                url: "http://localhost:4002/question/getRightHottestQuestionList",
                type: "get",
                success(response) {
                    if (response.code == '10000') {
                        vue.rightHottestList = response.data;
                    } else {
                        vue.$modal.msgError("发生未知错误，查找热门回答失败");
                    }
                },
                error() {
                    vue.$modal.msgError("发生未知错误，查找热门回答失败");
                }
            })
        },
        // 得到等你来答列表
        getWaitYouQuestionList() {
            const vue = this;
            $.ajax({
                url: "http://localhost:4002/question/getWaitYouQuestionList",
                type: "get",
                data: {
                    userId: store.state.user.id,
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize
                },
                success(response) {
                    if (response.code == '10000') {
                        vue.totals = response.data.total;
                        vue.questionList = response.data.records;
                    } else {
                        vue.$modal.msgError("发生未知错误，查找等你来答文章失败");
                    }
                },
                error() {
                    vue.$modal.msgError("认证失败，无法访问系统资源");
                }
            })
        },
        // 得到最热文章列表
        getHottestQuestionList() {
            const vue = this;
            $.ajax({
                url: "http://localhost:4002/question/getHottestQuestionList",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize
                },
                success(response) {
                    if (response.code == '10000') {
                        vue.totals = response.data.total;
                        vue.questionList = response.data.records;
                    } else {
                        vue.$modal.msgError("发生未知错误，查找最热文章失败");
                    }
                },
                error() {
                    vue.$modal.msgError("认证失败，无法访问系统资源");
                }
            })
        },
        // 得到最新问答列表
        getLastQuestionList() {
            const vue = this;
            $.ajax({
                url: "http://localhost:4002/question/getLatestQuestionList",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                },
                success(response) {
                    if (response.code == '10000') {
                        vue.totals = response.data.total;
                        vue.questionList = response.data.records;
                    } else {
                        vue.$modal.msgError("发生未知错误，查询问答列表失败");
                    }
                },
                error() {
                    vue.$modal.msgError("发生未知错误，查询问答列表失败");
                }
            })
        },
    }
 }
</script>

<style scoped>
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