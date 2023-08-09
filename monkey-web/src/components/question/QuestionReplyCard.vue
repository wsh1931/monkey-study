<template>
    <div class="QuestionReplyCard-container">
        <el-button 
                class="el-icon-edit write-reply" @click="showWriterReply = true"> 写回答</el-button>
        <el-row v-if="showWriterReply" style="margin-bottom: 30px;">
            <mavon-editor
            v-model="replyContent" 
            :toolbars="toolbars"
            :translate="true"
            defaultOpen="edit"
            placeholder="期待您精彩的回复"
            style="min-height: 400px; z-index: 1;"
            :navigation="false"
            :subfield="false"
            :scrollStyl="true"
            @keydown.native="handleKeyDown($event)"
            ></mavon-editor>
            <el-button type="primary" size="small" class="publish-comment-button" @click="publishReply(questionId)">发表回复</el-button>
            <el-row class="publish-comment-indicate">按下Enter换行，Ctrl+Enter发表回复</el-row>
        </el-row>
        <span id="PointerIcon" class="el-icon-chat-round" style="margin-right: 5px;"></span>问答回复({{ questionReplyList.length }})
        <el-row style="margin-top: 10px;" v-for="questionReply in questionReplyList" :key="questionReply.id">
            <el-row>
                <el-col :span="2">
                    <img 
                    @click="toPersonHome(questionReply.userId)"
                    class="hover userPhoto img-one" 
                    :src="questionReply.userPhoto" 
                    alt="">
                </el-col>
                <el-col :span="19" style="margin-left: -15px;" >
                    <el-row style="font-weight: 600;">{{ questionReply.username }}</el-row>
                    <el-row class="fontColor">{{ questionReply.userBrief }}</el-row>
                </el-col>
                <el-col :span="3">
                    <el-button v-if="questionReply.isFans == '0'"
                        @click="likeAuthor(questionReply.userId)"
                        icon="el-icon-plus"
                        plain 
                        class="about"
                        size="small">
                        关注</el-button>
                    <el-button v-else @click="likeAuthor(questionReply.userId)" 
                        icon="el-icon-delete" 
                        plain 
                        size="small"
                        class="cancel-about"
                        >取消关注
                    </el-button>
                </el-col>
            </el-row>
            <el-row style="margin: 10px; font-weight: 600px;">
                {{ questionReply.content }}
            </el-row>
            <el-row class="fontColor">
                发布于 {{ questionReply.updateTime }}
            </el-row>
            <el-row>
                <el-row style="display: flex; justify-content: right; align-items: center;">
                    <el-row class="hover fontColor" style="margin-right: 10px;">
                        <span class="el-icon-chat-line-square" id="PointerIcon"></span>
                        <span v-if="!questionReply.showComment" @click="getQuestionCommentByQuestionReplyId(questionReply.id, true)">查看评论({{ questionReply.articleCommentCount }})</span>
                        <span v-else @click="openQuestionReplyComment(questionReply.id)">收起评论</span>
                    </el-row>
                    <el-row class="hover fontColor">
                        <span class="el-icon-warning-outline"></span>
                        <span style="margin-right: 30px;">举报</span>
                    </el-row>
                </el-row>
                <!-- 文章评论信息 -->
                <el-row style="margin-top: 10px;" v-if="questionReply.showComment">
                    <el-card class="box-card">
                        <div slot="header" class="clearfix">
                        <span>{{ questionCommentCount }} 条评论</span>
                    </div>
                        <el-row style="margin-left: 10px; margin-top: 10px;">
                            <el-row v-if="$store.state.user.is_login">
                                <el-col :span="2">
                                    <img 
                                        class="comment-img"
                                        @click="toPersonViews($store.state.user.id)"
                                    :src="$store.state.user.photo" alt="">
                                </el-col>
                                <el-col :span="22" >
                                    <el-input
                                        type="textarea"
                                        :autosize="{ minRows: 5 , maxRows: 5}"
                                        placeholder="期待您的优质评论"
                                        v-model="questionReply.commentContent"
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
                                        v-model="questionReply.commentContent"
                                        :show-word-limit="true"
                                        minlength="1"
                                        maxlength="1000">
                                    </el-input>
                                    <el-row style="text-align: right;">
                                <el-button 
                                    size="mini"
                                    type="danger"
                                    @click="Login()"
                                    class="login"
                                    round>
                                    登录
                                </el-button>
                            </el-row>
                            </el-row>

                            <el-row style="text-align: right;" v-if="$store.state.user.is_login">
                                <el-button size="mini"
                                    style="background-color: #0461CF;
                                    color: #FFFFFF;
                                    margin-top: 5px;
                                    margin-right: 5px;"
                                    round
                                    @click="publishQuestionComment($store.state.user.id, questionReply.id, questionReply.commentContent)">发表评论</el-button>
                            </el-row>
                        </el-row>
                        <el-row v-for="commentOne in questionCommentList" :key="commentOne.id" style="margin-left: 10px; margin-top: 10px;">
                            <el-col :span="2">
                                <img class="comment-img" :src="commentOne.commentUserPhoto" alt="" style="margin-top: 5px;">
                            </el-col>
                            <el-col :span="22">
                                    <el-row>
                                        <el-col :span="16" style="font-weight: 600; font-size: 14px;" class="el-col-omit">
                                            {{ commentOne.commentUserName }}
                                        </el-col>
                                            
                                        <el-col :span="2" class="el-col-one-reply" >
                                            <span @click="commentOne.showInput = true">
                                                <span class="el-icon-chat-line-round" id="PointerIcon"></span>回复
                                            </span>
                                            
                                        </el-col>
                                        <el-col :span="6" class="comment-one-createTime">
                                            {{ commentOne.createTime }}
                                        </el-col>
                                    </el-row>
                                    
                                <el-row style="font-weight: 600;">
                                    {{ commentOne.content }}
                                </el-row>
                                <el-row v-if="commentOne.showInput">
                                    <el-row>
                                        <el-input
                                        style="margin-top: 5px;"
                                            v-model="commentOne.questionReplyContent"
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
                                        class="reply"
                                        round @click="questionReplyComment(commentOne.id, $store.state.user.id, commentOne.questionReplyContent, questionReply.id)">
                                            回复
                                        </el-button>
                                    </el-row>
                                </el-row>

                                <el-row v-for="commentTwo in commentOne.downComment" :key="commentTwo.id" style="width: 95%; margin-top: 10px;">
                                    <el-col :span="1">
                                        <img class="comment-img-two" :src="commentTwo.commentUserPhoto" alt="">
                                    </el-col>
                                    <el-col :span="22" style="margin-left: 10px;">
                                        <el-row >
                                            <el-col :span="15"  
                                                class="el-col-userName-two">
                                                {{ commentTwo.commentUserName }} 回复: {{ commentTwo.replyUserName }}
                                            </el-col>
                                            <el-col :span="2"
                                                class="el-col-one-reply">
                                                <span @click="commentTwo.showInput = true">
                                                    <span class="el-icon-chat-line-round" id="PointerIcon"></span>回复
                                                </span>
                                                
                                            </el-col>
                                            <el-col :span="7" class="comment-two-createTime">
                                                {{ commentTwo.createTime }}
                                            </el-col>   

                                        </el-row>
                                        <el-row style="font-weight: 600;">{{ commentTwo.content }}</el-row>
                                            <el-row v-if="commentTwo.showInput">
                                                <el-row>
                                                    <el-input
                                                    style="margin-top: 10px;"
                                                        v-model="commentTwo.questionReplyContent"
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
                                                    class="reply"
                                                    round @click="questionReplyComment(commentOne.id, $store.state.user.id, commentTwo.questionReplyContent, questionReply.id)">
                                                        回复
                                                    </el-button>
                                                </el-row>
                                            </el-row>
                                    </el-col>
                                </el-row>
                            </el-col>
                        </el-row>
                    </el-card>
                    </el-row>
            </el-row>
            <el-row class="divider"></el-row>
        </el-row>

        <PagiNation
        :totals="totals"
        :currentPage="currentPage" 
        :pageSize="pageSize" 
        @handleCurrentChange = "handleCurrentChange"
        @handleSizeChange="handleSizeChange"/>
                
    </div>
</template>

<script>
import { mavonEditor } from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'
 import $ from "jquery"
 import store from "@/store";
 import PagiNation from "@/components/pagination/PagiNation.vue";
 export default {
    name: "QuestionReplyCard",
    components: {
        PagiNation,
        mavonEditor,
    },
    data() {
        return {
            // 回复内容
            replyContent: "",
            showWriterReply: false,
            questionReplyCommentUrl: "http://localhost:80/monkey-question/reply/comment",
            checkArticleUrl: "http://localhost:80/monkey-article/check",
            questionReplyUrl: "http://localhost:80/monkey-question/reply",
            questionId: "",
            questionReplyList:[],   
            currentPage: 1,
            pageSize: 10,
            totals: 0,
            userId: "",
            openComment: false,
            questionCommentList: [],
            // 发表评论内容
            commentContent: "",
            // 问答评论数
            questionCommentCount: 0,
            // 问答回复评论
            questionReplyContent: "",
            toolbars: {
                bold: true, // 粗体
                italic: true, // 斜体
                header: true, // 标题
                underline: true, // 下划线
                strikethrough: true, // 中划线
                mark: true, // 标记
                superscript: true, // 上角标
                subscript: true, // 下角标
                quote: true, // 引用
                ol: true, // 有序列表
                ul: true, // 无序列表
                link: true, // 链接
                imagelink: true, // 图片链接
                code: true, // code
                table: true, // 表格
                fullscreen: true, // 全屏编辑
                readmodel: true, // 沉浸式阅读
                help: true, // 帮助
                preview: true, // 预览
            },
        }
    },
    created() {
        this.userId = store.state.user.id;
        this.questionId = this.$route.params.questionId;
        this.getQuestionReplyListByQuestionId(this.questionId)
    },
    methods: {
        // 发表回复
        publishReply(questionId) {
            const vue = this;
            $.ajax({
                url: vue.questionReplyUrl + "/publishReply",
                type: "post",
                data: {
                    questionId,
                    replyContent: vue.replyContent
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response) {
                        if (response.code == '200') {
                            vue.getQuestionReplyListByQuestionId(questionId);
                            vue.$modal.msgSuccess(response.msg);
                            vue.showWriterReply = false;
                            vue.replyContent = "";
                        } else {
                            vue.$modal.msgError(response.msg);
                        }
                    }
                },
                
            })
        },
        handleKeyDown(e) {
            if (e.keyCode === 13 && !e.ctrlKey) {
                // Enter，换行
                this.replyContent += '\n';
                e.preventDefault();
            } else if (e.keyCode === 13 && e.ctrlKey) {
                // Ctrl + Enter，发送消息
                this.publishReply(this.questionId);
                e.preventDefault();
            }
        },
        // 问答评论回复功能实现
        questionReplyComment(parentId, replyId, questionReplyContent, questionReplyId) {
            const vue = this;
            $.ajax({
                url: vue.questionReplyCommentUrl + "/questionReplyComment",
                type: "post",
                data: {
                    parentId,
                    replyId,
                    questionReplyContent,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.$modal.msgSuccess("评论成功");
                        vue.getQuestionCommentByQuestionReplyId(questionReplyId);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
        },
        // 发表问答评论
        publishQuestionComment(userId, questionReplyId, content) {
            const vue = this;
            $.ajax({
                url: vue.questionReplyCommentUrl + "/publishQuestionComment",
                type: "post",
                data: {
                    userId,
                    questionReplyId,
                    commentContent: content,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token
                },
                success(response) {
                    if (response.code == '200') {
                        vue.commentContent = "";
                        vue.$modal.msgSuccess("发表评论成功");
                        vue.getQuestionCommentByQuestionReplyId(questionReplyId, false);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
        },
        // 打开/关闭问答回复评论
        openQuestionReplyComment(questionReplyId) {
            let len = this.questionReplyList.length;
            for (let i = 0; i < len; i ++ ) {
                if (this.questionReplyList[i].id == questionReplyId) {
                    this.questionReplyList[i].showComment = !this.questionReplyList[i].showComment;
                } else {
                    this.questionReplyList[i].showComment = false;
                }
            }
        },
        // 发表评论之前要先登录
        Login() {
            this.$router.push({
                name: "login"
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
        // 通过问答回复id得到文章评论信息
        getQuestionCommentByQuestionReplyId(questionReplyId, status) {
            const vue = this;
            $.ajax({
                url: vue.questionReplyUrl + "/getQuestionCommentByQuestionReplyId",
                type: "get",
                data: {
                    questionReplyId
                },
                success(response) {
                    if (response.code == '200') {
                        if (status) vue.openQuestionReplyComment(questionReplyId);
                        vue.questionCommentList = response.data.questionReplyCommentVoList;
                        vue.questionCommentCount = response.data.questionCommentCount;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
        },
        handleSizeChange(val) {
            this.pageSize = val;
        },
        handleCurrentChange(val) {
            this.currentPage = val;
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
                        vue.$emit("getAuthorInfoByQuestionId", vue.questionId);
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
                headers: {
                Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    questionId,
                    fansId: store.state.user.id,
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
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
    }
}
</script>

<style scoped>
.publish-comment-button {
    position: absolute;
    border-radius: 20px;
    top: 360px;
    text-align: center;
    left: 718px;
    z-index: 100002;
    width: 76px;
    height: 28px;
    line-height: 10px;
    
}
.publish-comment-indicate {
    position: absolute;
    top: 365px;
    left: 500px;
    z-index: 100002;
    font-size: 12px;
    opacity: 0.5;
}
.write-reply {
    position: fixed;
    top: 280px;
    right: 19vw;
    border-radius: 20px;
}
#PointerIcon {
    transform:rotate(270deg)
}
.img-one {
    width: 40px; 
    height: 40px; 
    border-radius: 50%;
}
.login {
    background-color: #FC5531; 
    margin-top: 5px;
    margin-right: 5px;
}
.comment-one-createTime {
    font-size: 14px; 
    font-weight: 600; 
    margin-top: 5px; 
    padding-left: 10px; 
    color: gray;
}
.comment-two-createTime {
    font-size: 14px; 
    font-weight: 600; 
    margin-top: 5px; 
    padding-left: 20px; 
    color: gray;
}
.reply {
    background-color: #0461CF;
    margin-top: 5px;
    margin-right: 5px; 
    color: #FFFFFF;
}
.divider {
    background-color: #EEEEEE;
    height: 1px; 
    margin: 10px;
}
.about {
    background-color: #E6F0FD; 
    color: #056DE8; 
    width: 80px; 
    display: flex; 
    justify-content: center; 
    align-items: center;
}
.cancel-about {
    color: lightcoral; 
    background-color: #FDE2E2; 
    width: 80px; 
    display: flex; 
    justify-content: center; 
    align-items: center;
}
#PointerIcon {
    transform:rotate(270deg)
}
.el-col-userName-two {
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden; 
    font-size: 14px; 
    font-weight: 600;
    
}
.el-col-one-reply {
    color: grey;
    font-size: 14px;
    font-weight: 600;
    margin-top: 5px; 
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

.el-col-one-reply:hover {
    color: #66B1FF;
}
.comment-img-two {
    width: 30px;
    height: 30px;
    border-radius: 50%;
    cursor: pointer;
}
.comment-img {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    cursor: pointer;
}
.userPhoto:hover {
    transition: 0.5s ease;
    transform: scale(1.07) translate3d(0,0,0);
}

.hover:hover {
    cursor: pointer;
    color: #409EFF;
}
.fontColor {
    color: rgba(0, 0, 0, 0.5);
    font-size: 14px;
}
#PointerIcon {
    transform:rotate(270deg)
}
</style>