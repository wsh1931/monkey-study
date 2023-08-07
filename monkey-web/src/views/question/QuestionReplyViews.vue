<template>
    <div class="QuestionReply-container" 
    style="
    width: 1400px;  
    margin: 10px auto;">
    <CollectCard v-if="showCollect"
    :associateId="associateId"
    :showCollect="showCollect"
    :collectType="collectType"
    :collectTitle="collectTitle"
    @closeCollect="closeCollect"/>
    <el-container>
        <el-aside width="150px" style="margin-left: 10px;">
            <el-badge type="info" 
                :value="questionInformation.userLikeCount" 
                :max="99" 
                class="item" 
                style="margin-top: 10px; 
                position: fixed;">
                    <el-button v-if="questionInformation.isLike == '0'" 
                    @click="userLikeQuestion(questionInformation.id)"  
                    size="small" 
                    class="hover"
                    round>
                    <span class="iconfont icon-dianzan"></span>
                    赞</el-button>
                    <el-button v-else @click="userLikeQuestion(questionInformation.id)"  
                    size="small"
                    style="background-color: lightgreen" 
                    class="hover"
                    round >
                    <span class="iconfont icon-dianzan"></span>
                    赞</el-button>
            </el-badge>
                
            <el-badge type="info" 
                :max="99" 
                class="item" 
                style="margin-top: 55px; position: fixed;">
                <el-button @click="userCancelQuestion(questionInformation.id)" 
                size="small"  class="hover" round>
                <span class="iconfont icon-cai"></span>
                踩</el-button>
            </el-badge>

            <el-badge type="info" 
                :max="99" 
                :value="questionInformation.userCollectCount" 
                class="item" 
                style="margin-top: 100px; position: fixed;">
                    <el-button v-if="questionInformation.isCollect == '0'"
                    @click="userCollectQuestion(questionInformation.id, questionInformation.title)" 
                    size="small" 
                    class="hover"
                        round>
                        <span class="iconfont icon-shoucang"></span>
                        收藏</el-button>
                    <el-button v-else @click="userCollectQuestion(questionInformation.id, questionInformation.title)"
                    size="small" 
                    class="hover"
                    style="background-color: lightblue" round>
                        <span class="iconfont icon-shoucang"></span>
                    已收藏</el-button>
            </el-badge>

            
        </el-aside>
        <el-main style="background-color: #FFFFFF; ">
            <el-row>
                <el-row>
                    <el-tag style="margin: 5px;" type="primary" v-for="label in labelList" :key="label.id">{{ label.labelName }}</el-tag>
                </el-row>
                <el-row >
                    <h2>{{ questionInformation.title }}</h2>
                </el-row>
                
                <el-row>
                    <el-col :span="4" class="el-icon-view information ellipsis">
                        游览 {{ questionInformation.visit }}
                    </el-col>
                    <el-col :span="4" style=" margin-top: 8px;" class="hover reportColor el-icon-warning-outline">
                        举报
                    </el-col>
                    <el-col :span="9" style="color: gray; margin-top: 8px;">
                        <span class="el-icon-time"></span> {{ questionInformation.updateTime | formatDate }}
                    </el-col>
                </el-row>
                <el-row>
                    <vue-markdown 
                        :source="questionInformation.profile" 
                        :highlight="true"
                        :html="true"
                        :xhtmlOut="true">
                    </vue-markdown>
                </el-row>
            </el-row>
            <el-row>
                <QuestionReplyCard
                @getAuthorInfoByQuestionId = "getAuthorInfoByQuestionId"/>
            </el-row>
        </el-main>
        <el-aside width="400px" style="padding-left: 10px;">
            <UserInfoCard
            :userInformation="authorInformation"
            @getAuthorInfoByArticleOrQuestionId="getAuthorInfoByQuestionId"
            :articleOrQuestionId="questionId"/>
        </el-aside>
    </el-container>
        
    </div>
</template>

<script>

 import $ from "jquery"
 import UserInfoCard from '@/components/user/UserInfoCard.vue';
 import store from '@/store';
 import QuestionReplyCard from "@/components/question/QuestionReplyCard.vue";
import VueMarkdown from 'vue-markdown';
import CollectCard from "@/components/collect/CollectCard.vue";

 export default {
    name: "QuestionReply",
    components: {
        UserInfoCard,
        QuestionReplyCard,
        VueMarkdown,
        CollectCard,
    },
    data() {
        return {
            questionReplyUrl: "http://localhost:80/monkey-question/reply",
            checkArticleUrl: "http://localhost:80/monkey-article/check",
            // 问答id
            questionId: "",
            authorInformation: {},
            currentPage: 1,
            pageSize: 10,
            totals: 0,
            questionInformation: [],
            labelList: [],
            questionReplyList: [],
            // 收藏类型
            collectType: 1,
            // 收藏标题
            collectTitle: "",
            // 收藏关联id
            associateId: "",
            // 是否展示收藏页面
            showCollect: false,
            // 写回答是否展示
            showWriterReply: false,
            userId: "",
        }
    },
    filters: {
        formatDate: value => {
        if (!value) return '';

        // 转换成 Date 对象
        const date = new Date(value);

        // 格式化输出
        const year = date.getFullYear();
        const month = ('0' + (date.getMonth() + 1)).slice(-2);
        const day = ('0' + date.getDate()).slice(-2);

        return `${year}-${month}-${day}`;
        }
    },
    created() {
        this.userId = store.state.user.id;
        this.questionId = this.$route.params.questionId;
        this.getAuthorInfoByQuestionId(this.questionId);
        this.getQuestionInfoByQuestionId(this.questionId);
        this.getQuestionLabelNameByQuestionId(this.questionId);
    },
    methods: {
        closeCollect(status) {
            this.showCollect = status
            this.getQuestionInfoByQuestionId(this.questionId);
        },
        // 用户点赞问答功能实现
        userLikeQuestion(questionId) {
            const vue = this;
            $.ajax({
                url: vue.questionReplyUrl + "/userLikeQuestion",
                type: "post",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    questionId,
                    userId: store.state.user.id,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.$modal.msgSuccess(response.msg);
                        vue.getQuestionInfoByQuestionId(vue.questionId);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
        },
        // 用户取消问答点赞功能实现
        userCancelQuestion(questionId) {
            const vue = this;
            $.ajax({
                url: vue.questionReplyUrl + "/userCancelLikeQuestion",
                type: "post",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    questionId,
                    userId: store.state.user.id,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.$modal.msgSuccess(response.msg);
                        vue.getQuestionInfoByQuestionId(vue.questionId);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },

            })
        },
        // 用户收藏功能实现
        userCollectQuestion(questionId, title) {
            this.associateId = questionId;
            this.showCollect = true;
            this.collectTitle = title;
        },
        // 当前登录用户收藏问答
        collectQuestion(questionId) {
            const vue = this;
            $.ajax({
                url: vue.questionReplyUrl + "/collectQuestion",
                type: "post",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    questionId,
                    userId: store.state.user.id,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.$modal.msgSuccess(response.msg);
                        vue.getQuestionInfoByQuestionId(vue.questionId);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
        },
        toPersonHome(userId) {
            this.$router.push({
                name: "user_home",
                params: {
                    userId
                }
            })
        },
        // 关注作者
        likeAuthor(userId) {
            const vue = this;
            $.ajax({
                url: vue.checkArticleUrl + "/likeAuthor",
                type: "get",
                data: {
                    userId
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token
                },
                success(response) {
                    if (response.code == "200") {
                        vue.$modal.msgSuccess(response.msg);
                        vue.getQuestionReplyListByQuestionId(vue.questionId);
                        vue.getAuthorInfoByQuestionId(vue.questionId);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })            
        },
        // 通过问答id查询问答回复列表
        getQuestionReplyListByQuestionId(questionId) {
            const vue = this;
                $.ajax({
                url: vue.questionReplyUrl + "/getQuestionReplyListByQuestionId",
                type: "get",
                data: {
                    questionId,
                    fansId: store.state.user.id,
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.questionReplyList = response.data.records;
                        vue.totals = response.data.total;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
           
        },
        // 通过问答id得到作者信息
        getAuthorInfoByQuestionId(questionId) {
            const vue = this;
                $.ajax({
                url: vue.questionReplyUrl + "/getAuthorVoInfoByQuestionId",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    questionId,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.authorInformation = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },

                })
            },
            
        // 通过问答id得到问答信息
        getQuestionInfoByQuestionId(questionId) {
            const vue = this;
                $.ajax({
                url: vue.questionReplyUrl + "/getQuestionInfoByQuestionId",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    questionId,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.questionInformation = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
            },

        // 通过问答id得到问答标签名
        getQuestionLabelNameByQuestionId(questionId) {
            const vue = this;
            $.ajax({
                url: vue.questionReplyUrl + "/getQuestionLabelNameByQuestionId",
                type: "get",
                data: {
                    questionId
                },
                success(response) {
                    if (response.code == '200') {
                        vue.labelList = response.data;
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

.reportColor {
    color: gray;
}
  .hover:hover {
    cursor: pointer;
    color: #409EFF;
}
  .ellipsis {
    color: gray;
  display: block;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  text-align: left;
}
  .information {
    margin-top: 12px; 
    color: rgba(0, 0, 0, 0.5);
     font-size: 14px;
}
</style>