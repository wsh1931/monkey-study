<template>
    <div class="ArticleComment">
        <ReportComment
        v-if="showReportComment"
        @reportComment="reportComment"
        :reportCommentType="reportCommentType"
        :reportCommentAssociationId="reportCommentAssociationId"
        @closeReportComment="closeReportComment"/>
        <el-row style="margin-left: 10px; margin-top: 10px;">
            <el-row v-if="$store.state.user.is_login">
                <el-col :span="3">
                    <img 
                        class="comment-img"
                        @click="toPersonViews($store.state.user.id)"
                       :src="$store.state.user.photo" alt="">
                </el-col>
                <el-col :span="21" >
                    <el-input
                        type="textarea"
                        :autosize="{ minRows: 5 , maxRows: 5}"
                        placeholder="期待您的优质评论"
                        v-model="commentInformation.content"
                        :show-word-limit="true"
                        minlength="1"
                        maxlength="1000">
                    </el-input>
                </el-col>
            </el-row>

            <el-row v-else>
                    <el-input
                        type="textarea"
                        :autosize="{ minRows: 5 , maxRows: 5}"
                        placeholder="登录之后才能发表评论"
                        v-model="commentInformation.content"
                        :show-word-limit="true"
                        minlength="1"
                        maxlength="1000">
                    </el-input>
                    <el-row style="text-align: right;">
                <el-button 
                    size="mini"
                    type="danger"
                    class="comment-reply"
                    @click="Login()"
                    round>
                    登录
                </el-button>
            </el-row>
            </el-row>

            <el-row style="text-align: right;" v-if="$store.state.user.is_login">
                <el-button size="mini"
                    type="danger"
                    style="background-color: #FC5531;
                    margin-top: 5px;
                    margin-right: 5px;"
                    round
                    @click="publishComment(articleId)">发表评论</el-button>
            </el-row>
        </el-row>
        <el-row v-for="commentOne in commentInformation" :key="commentOne.id" style="margin-left: 10px; margin-top: 10px;">
            <el-col :span="3">
                <img class="comment-img" :src="commentOne.userNamePhoto" alt="">
            </el-col>
            <el-col :span="21">
                <el-row>
                    <el-row>
                        <el-col :span="8" class="el-col-omit">
                            {{ commentOne.userName }}
                        </el-col>
                        <el-col class="comment-col-one comment-time" :span="4">
                            {{ getTimeFormat(commentOne.commentTime) }}
                        </el-col>
                        <el-col :span="3"
                         class="el-icon-chat-line-round el-col-one-reply" 
                         >
                         <span @click="commentOne.showInput = true">回复</span>
                             
                        </el-col>
                        <el-col :span="3" class="userLike">
                            <div v-if="commentOne.isLike == '0'"
                            class="el-icon-caret-top"
                            @click="commentLike(articleId, commentOne, 1)">
                            赞 {{ commentOne.commentLikeSum }}
                            </div>
                            <div v-else class="el-icon-caret-top"
                            style="color: lightblue;" 
                            @click="commentLike(articleId,commentOne, 1)">
                            赞 {{ commentOne.commentLikeSum }}
                            </div>
                        </el-col>
                         <el-col :span="3" class="userLike">
                            <div
                            class="el-icon-warning-outline"
                            @click="reportComment(commentOne.id)" >
                            举报
                            </div>
                        </el-col>
                    </el-row>
                    
                </el-row>
                <el-row style="width: 85%;">
                    {{ commentOne.content }}
                </el-row>
                <el-row v-if="commentOne.showInput">
                    <el-row>
                        <el-input
                            v-model="commentOne.articleCommentContent"
                            type="textarea"
                            :autosize="{ minRows: 5 , maxRows: 5}"
                            placeholder="期待您的精彩回复"
                            :show-word-limit="true"
                            minlength="1"
                            maxlength="1000">
                        </el-input>
                    </el-row>
                    <el-row style="text-align: right;">
                        <el-button
                        size="mini"
                        type="danger"
                        class="comment-reply"
                        round @click="replyComment(commentOne.id, commentOne.articleCommentContent)">
                            回复
                        </el-button>
                    </el-row>
                </el-row>

                <el-row v-for="commentTwo in commentOne.downComment" :key="commentTwo.id">
                    <el-col :span="2">
                        <img class="comment-img-two" :src="commentTwo.userNamePhoto" alt="">
                    </el-col>
                    <el-col :span="22">
                        <el-row >
                            
                            <el-col :span="9" >
                                <el-row >
                                    <span 
                                    class="el-col-userName-two">
                                    {{ commentTwo.userName }}
                                    </span>
                                    <span
                                    class="el-col-userName-two">
                                        回复: {{ commentTwo.replyName }}
                                    </span>
                                </el-row>
                             </el-col>

                            <el-col class="comment-col-one comment-time" :span="4">
                                {{ getTimeFormat(commentTwo.commentTime) }}
                            </el-col>

                            <el-col :span="3"
                                class="el-icon-chat-line-round el-col-one-reply">
                                <span @click="commentTwo.showInput = true">回复</span>
                                
                            </el-col>

                            <el-col :span="4" class="userLike">
                                <div v-if="commentTwo.isLike == '0'"
                                class="el-icon-caret-top"
                                @click="commentLike(articleId, commentTwo, 2)">
                                赞 {{ commentTwo.commentLikeSum }}
                                </div>
                                <div v-else style="color: lightblue;"
                                class="el-icon-caret-top"
                                @click="commentLike(articleId,commentTwo, 2)">
                                赞 {{ commentTwo.commentLikeSum }}
                                </div>
                            </el-col>

                            <el-col :span="4" class="userLike">
                                <div
                                class="el-icon-warning-outline"
                                @click="reportComment(commentTwo.id)">
                                举报
                                </div>
                            </el-col>
                        </el-row>
                        <el-row style="width: 91%;">
                        <el-row>{{ commentTwo.content }}</el-row>
                            <el-row v-if="commentTwo.showInput">
                                <el-row>
                                    <el-input
                                        v-model="commentTwo.articleCommentContent"
                                        type="textarea"
                                        :autosize="{ minRows: 5 , maxRows: 5}"
                                        placeholder="期待您的精彩回复"
                                        :show-word-limit="true"
                                        minlength="1"
                                        maxlength="1000">
                                    </el-input>
                                </el-row>
                                <el-row style="text-align: right;">
                                    <el-button
                                    size="mini"
                                    type="danger"
                                    class="comment-reply"
                                    round @click="replyComment(commentOne.id, commentTwo.articleCommentContent)">
                                        回复
                                    </el-button>
                                </el-row>
                            </el-row>
                        </el-row>
                    </el-col>
                </el-row>
            </el-col>
        </el-row>

        <PagiNation
            style="text-align: right; margin-top: 10px;"
            :totals="totals"
            :currentPage="currentPage" 
            :pageSize="pageSize" 
            @handleCurrentChange = "handleCurrentChange"
            @handleSizeChange="handleSizeChange"/>
    </div>
</template>

<script>
import ReportComment from '@/components/report/ReportComment'
import $ from "jquery"
import store from "@/store"
import PagiNation from '../pagination/PagiNation.vue';
import { getTimeFormat } from "@/assets/js/DateMethod";
 export default {
    name: "ArticleComment",
    components: {
        PagiNation,
        ReportComment
    },
    data() {
        return {
            // 举报类型(0表示文章，1表示问答，2表示课程, 3表示社区，4表示社区文章, 5表示资源)
            reportCommentType: this.reportCommentTypes.article,
            // 举报关联id
            reportCommentAssociationId: "0",
            // 显示举报内容框
            showReportComment: false,
            // 评论分页
            currentPage: 1,
            pageSize: 10,
            totals: 0,
            commentInformation: [],
            articleId: "",
            checkArticleUrl: "http://localhost:80/monkey-article/check",
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
        this.articleId = this.$route.params.articleId
        this.getCommentInformationByArticleId(this.articleId);
    },
    methods: {
        closeReportComment() {
            this.showReportComment = false;
        },
        reportComment(commentId) {
            this.showReportComment = true;
            this.reportCommentAssociationId = commentId;
        },
        getTimeFormat(val) {
            return getTimeFormat(val);
        },
        handleSizeChange(val) {
            this.pageSize = val;
            this.getCommentInformationByArticleId(this.articleId);
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            this.getCommentInformationByArticleId(this.articleId);
        },
        // 发表评论之前要先登录
        Login() {
            this.$router.push({
                name: "login"
            })
        },
        // 评论点赞功能实习
        commentLike(articleId, comment, type) {
            let recipientId;
            if (type == '1') {
                recipientId = comment.userId;
            } else if (type == '2') {
                recipientId = comment.replyId;
            }
            const vue = this;
            $.ajax({
                url: vue.checkArticleUrl + "/commentLike",
                type: "post",
                data: {
                    articleId,
                    commentId: comment.id,
                    recipientId
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        if (comment.isLike == '0') {
                            comment.commentLikeSum++;
                            comment.isLike = '1'
                        } else if (comment.isLike == '1') {
                            comment.isLike = '0';
                            comment.commentLikeSum--;
                        }
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
        },
         // 回复发布
        replyComment(commentId,replyContent) {
            const vue = this;
            $.ajax({
                url: vue.checkArticleUrl + "/replyComment",
                type: "post",
                data:{
                    commentId,
                    replyContent
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.getCommentInformationByArticleId(vue.articleId);
                        vue.$modal.msgSuccess("回复评论成功");
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
        },
        // 跳往个人主页
        toPersonViews(userId) {
            this.$router.push({
                name: "user_home",
                params: {
                    userId
                }
            })
        },
        // 发表评论
        publishComment(articleId) {
            const vue = this;
            $.ajax({
                url: vue.checkArticleUrl + "/publishComment",
                type: "get",
                data: {
                    articleId,
                    content: vue.commentInformation.content,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.commentInformation.content = "";
                        vue.getCommentInformationByArticleId(articleId);
                        vue.$modal.msgSuccess("发表评论成功");
                        
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
        },
        // 通过文章id查询该文章评论信息
        getCommentInformationByArticleId(articleId) {
            const vue = this;
            $.ajax({
                url: vue.checkArticleUrl + "/getCommentInformationByArticleId",
                type: "get",
                data: {
                    articleId,
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.commentInformation = response.data.records;
                        vue.totals = response.data.total;
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
.comment-time {
    font-size: 6px; 
    color: rgba(0, 0, 0, 0.5); 
    margin-top: 2px;
}
.comment-reply {
    background-color: #FC5531;
    margin-top: 5px;
    margin-right: 5px;
}
.el-col-userName-two {
    display: inline-block;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden; 
    font-size: 7px; color: 
    rgba(0, 0, 0, 0.5);
    margin-right: 5px;
    max-width: 55px;
}
.comment-img-two {
    width: 30px;
    height: 30px;
    border-radius: 50%;
    cursor: pointer;
}
  .comment-img {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    cursor: pointer;
  }
  .userLike {
    cursor: pointer;
    font-size: 7px;
    margin-top: 2px;
    color: rgba(0, 0, 0, 0.5);
}

.userLike:hover {
    color: lightblue ;
}
  
.el-col-one-reply {
    font-size: 6px;
    color: rgba(0, 0, 0, 0.5);
    margin-top: 5px; 
    cursor: pointer;
}

.el-col-one-reply:hover {
    color: lightblue;
}
.el-col-omit {
    width: 10rem;
    font-size: 15px;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    color: rgba(0, 0, 0, 0.5);
}
</style>