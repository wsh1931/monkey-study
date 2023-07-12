<template>
    <div class="QuestionReply-container" 
    style="
    width: 1400px;  
    margin: 10px auto;">
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
                        icon="el-icon-caret-top" 
                        class="hover"
                        round>赞</el-button>
                        <el-button v-else @click="userLikeQuestion(questionInformation.id)"  
                        size="small" icon="el-icon-caret-top" 
                        style="background-color: lightgreen" 
                        class="hover"
                        round >赞</el-button>
                </el-badge>
                
                <el-badge type="info" 
                    :max="99" 
                    class="item" 
                    style="margin-top: 55px; position: fixed;">
                    <el-button @click="userCancelQuestion(questionInformation.id)" 
                    size="small" icon="el-icon-caret-bottom" class="hover" round>踩</el-button>
                </el-badge>

                <el-badge type="info" 
                    :max="99" 
                    :value="questionInformation.userCollectCount" 
                    class="item" 
                    style="margin-top: 100px; position: fixed;">
                        <el-button v-if="questionInformation.isCollect == '0'"
                        @click="userCollectQuestion(questionInformation.id)" 
                        size="small" 
                        class="hover"
                        icon="el-icon-collection" round>收藏</el-button>
                        <el-button v-else @click="userCollectQuestion(questionInformation.id)"
                        size="small" 
                        class="hover"
                        icon="el-icon-collection" 
                        style="background-color: lightblue" round>已收藏</el-button>
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
                    <mavon-editor
                    style="margin-top: 10px;"
                    class="markdown"
                    :value="questionInformation.profile"
                    :subfield="false"
                    defaultOpen="preview"
                    :toolbarsFlag="false"
                    :editable="false"
                    :scrollStyle="true">
                    </mavon-editor>
                </el-row>
            </el-row>
            <el-row>
                <el-row  style="background-color: #FFFFFF; margin-top: 10px;">
                    <span id="PointerIcon" class="el-icon-chat-round" style="margin-right: 5px;"></span>问答回复({{ questionInformation.replyCount }})
                </el-row>
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
 import { mavonEditor } from 'mavon-editor'
 import QuestionReplyCard from "@/components/question/QuestionReplyCard.vue";
 import 'mavon-editor/dist/css/index.css'


 export default {
    name: "QuestionReply",
    components: {
        UserInfoCard,
        mavonEditor,
        QuestionReplyCard,
    },
    data() {
        return {
            questionReplyUrl: "http://localhost:4300/question/reply",
            checkArticleUrl: "http://localhost:4100/check/article",
            // 问答id
            questionId: "",
            authorInformation: {},
            currentPage: 1,
            pageSize: 10,
            totals: 0,
            questionInformation: [],
            labelList: [],
            questionReplyList: [],
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
        this.questionId = this.$route.params.questionId;
        this.getAuthorInfoByQuestionId(this.questionId);
        this.getQuestionInfoByQuestionId(this.questionId);
        this.getQuestionLabelNameByQuestionId(this.questionId);
    },
    methods: {
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
                    if (response.code == '10000') {
                        vue.$modal.msgSuccess(response.msg);
                        vue.getQuestionInfoByQuestionId(vue.questionId);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
                error() {
                    vue.$modal.msgError("认证失败，无法访问系统资源")
                }
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
                    if (response.code == '10000') {
                        vue.$modal.msgSuccess(response.msg);
                        vue.getQuestionInfoByQuestionId(vue.questionId);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
                error() {
                    vue.$modal.msgError("认证失败，无法访问系统资源")
                }
            })
        },
        // 用户收藏功能实现
        userCollectQuestion(questionId) {
            const vue = this;
            $.ajax({
                url: vue.questionReplyUrl + "/userCollectQuestion",
                type: "post",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    questionId,
                    userId: store.state.user.id,
                },
                success(response) {
                    if (response.code == '10000') {
                        vue.$modal.msgSuccess(response.msg);
                        vue.getQuestionInfoByQuestionId(vue.questionId);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
                error() {
                    vue.$modal.msgError("认证失败，无法访问系统资源")
                }
            })
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
                    if (response.code == '10000') {
                        vue.$modal.msgSuccess(response.msg);
                        vue.getQuestionInfoByQuestionId(vue.questionId);
                    } else {
                        vue.$modal.msgError("发生未知错误，关注该问题失败");
                    }
                },
                error() {
                    vue.$modal.msgError("认证失败，无法访问系统资源");
                } 
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
                    if (response.code == "10000") {
                        vue.$modal.msgSuccess(response.msg);
                        vue.getQuestionReplyListByQuestionId(vue.questionId);
                        vue.getAuthorInfoByQuestionId(vue.questionId);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
                error() {
                    vue.$modal.msgError("认证失败，无法访问系统资源。");
                }
            })            
        },
        // 通过问答id查询问答回复列表
        getQuestionReplyListByQuestionId(questionId) {
            const vue = this;
            setTimeout(() => {
                $.ajax({
                url: vue.questionReplyUrl + "/getQuestionReplyListByQuestionId",
                type: "get",
                data: {
                    questionId,
                    fansId: store.state.user.id,
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                },
                success(response) {
                    if (response.code == '10000') {
                        vue.questionReplyList = response.data.records;
                        vue.totals = response.data.total;
                    } else {
                        vue.$modal.msgError("发送未知错误，查询问答回复失败");
                    }
                },
                error() {
                    vue.$modal.msgError("发送未知错误，查询问答回复失败");
                }
            })
            }, 1)
           
        },
        // 通过问答id得到作者信息
        getAuthorInfoByQuestionId(questionId) {
            const vue = this;
            setTimeout(() => {
                $.ajax({
                url: vue.questionReplyUrl + "/getAuthorVoInfoByQuestionId",
                type: "get",
                data: {
                    questionId,
                    fansId: store.state.user.id
                },
                success(response) {
                    if (response.code == '10000') {
                        vue.authorInformation = response.data;
                        console.log(vue.authorInformation)
                    } else {
                        vue.$modal.msgError("发生未知错误，得到作者信息失败");
                    }
                },
                error() {
                    vue.$modal.msgError("发生未知错误，得到作者信息失败");
                }
                })
            }, 1)
            
        },
        // 通过问答id得到问答信息
        getQuestionInfoByQuestionId(questionId) {
            const vue = this;
            setTimeout(() => {
                $.ajax({
                url: vue.questionReplyUrl + "/getQuestionInfoByQuestionId",
                type: "get",
                data: {
                    questionId,
                    userId: store.state.user.id
                },
                success(response) {
                    if (response.code == '10000') {
                        vue.questionInformation = response.data;
                    } else {
                        vue.$modal.msgError("发送未知错误，加载问答信息失败");
                    }
                },
                error() {
                    vue.$modal.msgError("发送未知错误，得到问答信息失败");
                }
            })
            }, 100)
            
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
                    if (response.code == '10000') {
                        vue.labelList = response.data;
                    } else {
                        vue.$modal.msgError("发送未知错误，查找问答标签失败");
                    }
                },
                error() {
                    vue.$modal.msgError("发送未知错误，查找问答标签失败");
                }
            })
        },
    }
 }
</script>

<style scoped>




.reportColor {
    color: gray;
}
#PointerIcon {
    transform:rotate(270deg)
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