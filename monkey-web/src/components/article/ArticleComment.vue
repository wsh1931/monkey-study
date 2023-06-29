<template>
    <div class="ArticleComment">
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
                    @click="Login()"
                    style="background-color: #FC5531; margin-top: 5px; margin-right: 5px;"
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
                    @click="publishComment($store.state.user.id, articleId)">发表评论</el-button>
            </el-row>
        </el-row>
        <el-row v-for="commentOne in commentInformation" :key="commentOne.id" style="margin-left: 10px; margin-top: 10px;">
            <el-col :span="3">
                <img class="comment-img" :src="commentOne.userNamePhoto" alt="">
            </el-col>
            <el-col :span="21">
                <el-row>
                    <el-row>
                        <el-col :span="12" class="el-col-omit">
                            {{ commentOne.userName }}
                        </el-col>
                        <el-col class="comment-col-one" :span="5" style="font-size: 6px; color: rgba(0, 0, 0, 0.5); margin-top: 2px;">
                            {{ commentOne.commentTime | formatDate }}
                        </el-col>
                        <el-col :span="3"
                         class="el-icon-chat-line-round el-col-one-reply" 
                         >
                         <span @click="commentOne.showInput = true">回复</span>
                             
                        </el-col>
                        <el-col :span="4" class="userLike">
                            <div v-if="commentOne.isLike == '0'"
                             class="el-icon-caret-top"
                              @click="commentLike($store.state.user.id, articleId, commentOne.id)">
                              赞 {{ commentOne.commentLikeSum }}
                            </div>
                            <div v-else class="el-icon-caret-top"
                             style="color: lightblue;" 
                             @click="commentLike($store.state.user.id, articleId,commentOne.id)">
                             赞 {{ commentOne.commentLikeSum }}
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
                        style="background-color: #FC5531;margin-top: 5px;margin-right: 5px;"
                        round @click="replyComment(commentOne.id, $store.state.user.id, commentOne.articleCommentContent)">
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
                            
                            <el-col :span="12" >
                                <el-row >
                                    <el-col :span="10"  
                                    class="el-col-userName-two">
                                    {{ commentTwo.userName }} 
                                </el-col>
                                    <el-col :span="14"
                                    class="el-col-userName-two">
                                        回复: {{ commentTwo.replyName }}</el-col>
                                </el-row>
                             </el-col>

                            <el-col class="comment-col-one" :span="5" style="font-size: 6px; color: rgba(0, 0, 0, 0.5); margin-top: 2px;">
                                {{ commentTwo.commentTime | formatDate }}
                            </el-col>

                            <el-col :span="3"
                                class="el-icon-chat-line-round el-col-one-reply">
                                <span @click="commentTwo.showInput = true">回复</span>
                                
                            </el-col>

                            <el-col :span="4" class="userLike">
                                <div v-if="commentTwo.isLike == '0'"
                                 class="el-icon-caret-top"
                                  @click="commentLike($store.state.user.id,articleId, commentTwo.id)">
                                  赞 {{ commentTwo.commentLikeSum }}
                                </div>
                                <div v-else style="color: lightblue;"
                                 class="el-icon-caret-top"
                                  @click="commentLike($store.state.user.id, articleId,commentTwo.id)">
                                  赞 {{ commentTwo.commentLikeSum }}
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
                                    style="background-color: #FC5531;margin-top: 5px;margin-right: 5px;"
                                    round @click="replyComment(commentOne.id, $store.state.user.id, commentTwo.articleCommentContent)">
                                        回复
                                    </el-button>
                                </el-row>
                            </el-row>
                        </el-row>
                    </el-col>
                </el-row>
            </el-col>
        </el-row>
    </div>
</template>

<script>
import $ from "jquery"
import store from "@/store"
 export default {
    name: "ArticleComment",
    data() {
        return {
            commentInformation: [],
            articleId: "",
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
        // 发表评论之前要先登录
        Login() {
            this.$router.push({
                name: "login"
            })
        },
        // 评论点赞功能实习
        commentLike(userId, articleId,commentId) {
            const vue = this;
            $.ajax({
                url: "http://localhost:4000/check/article/commentLike",
                type: "post",
                data: {
                    userId,
                    articleId,
                    commentId
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token
                },
                success(response) {
                    if (response.code == '10000') {
                        vue.getCommentInformationByArticleId(articleId)
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg)
                    }
                },
                error() {
                    vue.$modal.msgError("认证失败，无法访问系统资源");
                }
            })
        },
         // 回复发布
         replyComment(commentId, replyId, replyContent) {
            const vue = this;
            $.ajax({
                url: "http://localhost:4000/check/article/replyComment",
                type: "post",
                data:{
                    commentId,
                    replyId,
                    replyContent
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token
                },
                success(response) {
                    if (response.code == "10000") {
                        vue.getCommentInformationByArticleId(vue.articleId);
                        vue.$modal.msgSuccess("回复评论成功");
                    } else {
                        vue.$modal.msgError("发生未知错误");
                    }
                },
                error() {
                    vue.$modal.msgError("认证失败，无法访问系统资源");
                }
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
        publishComment(userId, articleId) {
            const vue = this;
            $.ajax({
                url: "http://localhost:4000/check/article/publishComment",
                type: "get",
                data: {
                    userId,
                    articleId,
                    content: vue.commentInformation.content,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == "10000") {
                        vue.commentInformation.content = "";
                        vue.getCommentInformationByArticleId(articleId);
                        vue.$modal.msgSuccess("发表评论成功");
                        
                    } else {
                        vue.$modal.msgError("发生未知错误");
                    }
                },
                error() {
                    vue.$modal.msgError("认证失败，无法访问系统资源。");
                }
            })
        },
        // 通过文章id查询该文章评论信息
        getCommentInformationByArticleId(articleId) {
            const vue = this;
            $.ajax({
                url: "http://localhost:4000/check/article/getCommentInformationByArticleId",
                type: "get",
                data: {
                    articleId,
                    userId: store.state.user.id,
                },
                success(response) {
                    if (response.code == "10000") {
                        vue.commentInformation = response.data;
                    } else {
                        vue.$modal.msgError("加载用户评论失败");
                    }
                },
                error() {
                    vue.$modal.msgError("加载用户评论失败");
                }
            })
        },
    }
 }
</script>

<style scoped>
.el-col-userName-two {
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden; 
    font-size: 7px; color: 
    rgba(0, 0, 0, 0.5);
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