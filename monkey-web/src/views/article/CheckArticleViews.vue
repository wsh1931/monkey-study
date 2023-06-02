<template>
<div id="checkArticle-container" style="display: flex; justify-content: center;align-items: center;">
    <el-container style="margin-top: 10px;">
        <el-aside width="140px" style="padding-left: 33px; ">
            
                <el-badge type="info" 
                    :value="articleInformation.likeSum" 
                    :max="99" 
                    class="item" 
                    style="margin-top: 10px; 
                    position: fixed;">
                        <el-button v-if="articleInformation.isLike == '0'" 
                        @click="userClickPraise(articleInformation.id)"  
                        size="small" 
                        icon="el-icon-caret-top" 
                        round>赞</el-button>
                        <el-button v-else @click="userClickPraise(articleInformation.id)"  
                        size="small" icon="el-icon-caret-top" 
                        style="background-color: lightgreen" 
                        round >赞</el-button>
                </el-badge>
                
                <el-badge type="info" 
                    :max="99" 
                    class="item" 
                    style="margin-top: 55px; position: fixed;">
                    <el-button @click="userClickOppose(articleInformation.id)" 
                    size="small" icon="el-icon-caret-bottom" round>踩</el-button>
                </el-badge>

                <el-badge type="info" 
                    :max="99" 
                    :value="articleInformation.collect" 
                    class="item" 
                    style="margin-top: 100px; position: fixed;">
                        <el-button v-if="articleInformation.isCollect == '0'"
                        @click="userCollect(articleInformation.id)" 
                        size="small" 
                        icon="el-icon-collection" round>收藏</el-button>
                        <el-button v-else @click="userCollect(articleInformation.id)"
                        size="small" 
                        icon="el-icon-collection" 
                        style="background-color: lightblue" round>已收藏</el-button>
                </el-badge>
                <el-badge 
                    :max="99"
                    :value="articleInformation.commentSum"
                    style="margin-top: 150px; position: fixed;">
                    <el-button 
                    size="small"
                    round
                    @click="getCommentInformationByArticleId(articleId)"
                    icon="el-icon-s-comment">
                    评论
                    </el-button>
                </el-badge>
        </el-aside>
        <el-container>
        
        <el-main style="padding: 2px;">
            <el-row>
                <el-card class="box-card" style="width: 100%;">
                    
                <div slot="header" class="clearfix" style="font-size: 20px; font-weight: bold;">
                    {{ articleInformation.title }}
                </div>
                <el-row>

                    <el-row style="padding-left: 30px;">
                        <el-col :span="3">
                            <div style="font-size: 13.5px; margin-top: 14px; color: #409EFF;" class="el-icon-view"> 
                                游览 {{ articleInformation.visit }}</div>
                        </el-col>
                        <!-- TODO -->
                        <el-col :span="2" class="el-icon-warning-outline" style="font-size: 13.5px; margin-top: 14px; color: #409EFF;">
                             举报</el-col>
                        <el-col :span="6" class="el-icon-time" style="font-size: 13.5px; color: rgba(0, 0, 0, 0.5); margin-top: 14px;">
                            {{ articleInformation.createTime }}
                        </el-col>
                        
                        <el-col :span="13" style="margin-top: 8px">
                        <el-tag
                        style="margin-right: 5px;"
                            v-for="label in labelList"
                            :key="label"
                            type="info"
                            effect="dark">
                            {{ label }}
                        </el-tag> 
                        </el-col>
                    </el-row>
                    
                </el-row >
                <el-row style="margin-top: 10px">
                    <mavon-editor
                    class="markdown"
                    :value="articleInformation.content"
                    :subfield="false"
                    defaultOpen="preview"
                    :toolbarsFlag="false"
                    :editable="false"
                    :scrollStyle="true"
                    style="min-height:20px"
                ></mavon-editor>
                </el-row>
                <el-row>
                    <el-card style="margin-top: 10px;"></el-card>
            </el-row>
            </el-card>
            </el-row>
            
        </el-main>
        
            
        </el-container>
        <el-aside width="400px" style="margin-left: 10px;">
            <el-card style="position: fixed; padding: 0; width: 350px;">
                <el-row>
                    <!-- TODO点击跳转到个人主页 -->
                    <el-col :span="8">
                        <img :src="userInformation.photo" @click="personHome()" style="cursor: pointer;">
                    </el-col>
                    <el-col :span="16" style="font-size: 24px; font-weight: 600;">
                        {{ userInformation.username }}
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="4">
                        <el-row >
                            {{ userInformation.visit }}
                        </el-row>
                        <el-row>
                            游览    
                        </el-row>
                    </el-col>
                    <el-col :span="4">
                        <el-row>
                            {{ userInformation.fans }}
                        </el-row>
                        <el-row>
                            粉丝
                        </el-row>
                    </el-col>
                    <el-col :span="4">
                        <el-row>
                            {{ userInformation.concern }}
                        </el-row>
                        <el-row>
                            关注
                        </el-row>
                    </el-col>
                    <el-col :span="4">
                        <el-row>
                            {{ userInformation.articleSum }}
                        </el-row>
                        <el-row>
                            文章
                        </el-row>
                    </el-col>
                    <el-col :span="4">
                        <el-row>
                            {{ userInformation.userCollect }}
                        </el-row>
                        <el-row>
                            收藏
                        </el-row>
                    </el-col>
                    <el-col :span="4">
                        <el-row>
                            {{ userInformation.likeSum }}
                        </el-row>
                        <el-row>
                            点赞
                        </el-row>
                    </el-col>
                </el-row>
                <el-row style="margin-top: 5px;">
                    <el-col :span="11">
                        <el-button v-if="userInformation.isFans == '0'" @click="likeAuthor(userInformation.id)" icon="el-icon-user-solid" round style="width: 100%;" type="primary">关注</el-button>
                        <el-button v-else @click="likeAuthor(userInformation.id)" icon="el-icon-user-solid" round style="width: 100%;" type="danger">取消关注</el-button>
                    </el-col>
                    <!-- 聊天界面实现 -->
                    <el-col :span="11" style="margin-left: 5px;">
                        <el-button icon="el-icon-chat-line-round" round style="width: 100%;" type="success">聊天</el-button>
                    </el-col>
                </el-row>
            </el-card>
        </el-aside>
        
    </el-container>
    
        <!-- 用户评论界面 -->
        <el-drawer
        title="用户评论"
        :visible.sync="drawer"
        direction="rtl"
        :before-close="handleClose">
        <el-row style="margin-left: 10px; margin-top: 10px;">
            <el-row v-if="$store.state.user.is_login">
                <!-- 跳转到个人主页 -->
                <el-col :span="3">
                    <img 
                        class="comment-img"
                        @click="toPersonViews()"
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
                             @click="commentLike($store.state.user.id, articleId, commentOne.id)">
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
                            v-model="replyContent"
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
                        round @click="replyComment(commentOne.id, $store.state.user.id, replyContent)">
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
                                  @click="commentLike($store.state.user.id, articleId, commentTwo.id)">
                                  赞 {{ commentTwo.commentLikeSum }}
                                </div>
                                <div v-else style="color: lightblue;"
                                 class="el-icon-caret-top"
                                  @click="commentLike($store.state.user.id, articleId, commentTwo.id)">
                                  赞 {{ commentTwo.commentLikeSum }}
                                </div>
                            </el-col>
                        </el-row>
                        <el-row style="width: 91%;">
                        <el-row>{{ commentTwo.content }}</el-row>
                            <el-row v-if="commentTwo.showInput">
                                <el-row>
                                    <el-input
                                        v-model="replyContent"
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
                                    round @click="replyComment(commentOne.id, $store.state.user.id, replyContent)">
                                        回复
                                    </el-button>
                                </el-row>
                            </el-row>
                        </el-row>
                    </el-col>
                </el-row>
            </el-col>
        </el-row>
        </el-drawer>
</div>
</template>

<script>
import $ from "jquery"
import { mavonEditor } from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'
import store from "@/store"

export default {
    name: "CheckArticleViews",

    components: {
        mavonEditor
    },
    data() {
        return {
            drawer: false,
            // 文章id
            articleId: "",
            // 评论回复内容
            replyContent: "",
            // 文章信息
            articleInformation: [],
            userInformation: [],
            labelList: [],
            commentInformation: [],
            userId: "",
            token: this.$store.state.user.token,
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
        this.articleId = this.$route.params.articleId;
        this.getArticleInformationByArticleId(this.articleId);
        this.getArticleLabelInfoByArticleId(this.articleId);
        this.getAuthorInfoByArticleId(this.articleId);
        this.addAtricleVisit(this.articleId);
    },
    methods: {
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
                        vue.replyContent = "";
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
        // 点击回复展示输入框
        showInput(temp) {
            console.log(temp)
        },
        // 评论点赞功能实习
        commentLike(userId, articleId, commentId) {
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
                        vue.getCommentInformationByArticleId(vue.articleId)
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
        // 关闭评论界面之前，更新文章作者信息
        handleClose() {
            this.drawer = false;
            this.getArticleInformationByArticleId(this.articleId);
            this.getAuthorInfoByArticleId(this.articleId);
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
                        vue.getCommentInformationByArticleId(vue.articleId);
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
        // 发表评论之前要先登录
        Login() {
            this.$router.push({
                name: "login"
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
                        vue.drawer = true;
                        vue.commentInformation = response.data;
                        console.log(vue.commentInformation)
                    } else {
                        vue.$modal.msgError("加载用户评论失败");
                    }
                },
                error() {
                    vue.$modal.msgError("加载用户评论失败");
                }
            })
        },
        // 关注作者
        likeAuthor(userId) {
            const vue = this;
            $.ajax({
                url: "http://localhost:4000/check/article/likeAuthor",
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
                        vue.getAuthorInfoByArticleId(vue.articleId);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
                error() {
                    vue.$modal.msgError("认证失败，无法访问系统资源。");
                }
            })            
        },
        // 跳转到用户主页
        personHome() {
            
        },
        // 进入这个页面后文章游览数加一
        addAtricleVisit(articleId) {
            $.ajax({
                url: "http://localhost:4000/check/article/addAtricleVisit",
                type: "post",
                data: {
                    articleId,
                },
            })
        },
        // 通过文章id得到作者信息
        getAuthorInfoByArticleId(articleId) {
            const vue = this;
            setTimeout(() => {
                $.ajax({
                url: "http://localhost:4000/check/article/getAuthorInfoByArticleId",
                type: "get",
                data: {
                    articleId,
                    userId: store.state.user.id,
                },
                success(response) {
                    if (response.code == "10000") {
                        vue.userInformation = response.data;
                    } else {
                        vue.$modal.msgError("作者信息加载失败")
                    }
                },
                error() {
                    vue.$modal.msgError("作者信息加载失败");
                }
            })
            })
            
        },
        // 用户收藏文章
        userCollect(articleId) {
            const vue = this;
            const token = store.state.user.token;
            if (token == null || token == "") {
                vue.$modal.msgError("请先登录");
            } else {

                $.ajax({
                url: "http://localhost:4000/blog/article/userCollect",
                type: "get",
                data: {
                    articleId,
                    userId: store.state.user.id,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == "10000") {
                        vue.$modal.msgSuccess(response.msg);
                        vue.getArticleInformationByArticleId(vue.articleId)
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
                error() {
                    vue.$modal.msgError("认证失败，无法访问系统资源。");
                    }
                })
            }
            
        },
        // 用户不喜欢
        userClickOppose(articleId) {
            const vue = this;
            const token = store.state.user.token;
            if (token == null || token == "") {
                vue.$modal.msgError("请先登录");
            } else {
                $.ajax({
                url: "http://localhost:4000/blog/article/userClickOppose",
                type: "get",
                data: {
                    articleId,
                    userId: store.state.user.id,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == "10000") {
                        vue.$modal.msgSuccess(response.msg);
                        vue.getArticleInformationByArticleId(vue.articleId)
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
                error() {
                    vue.$modal.msgError("认证失败，无法访问系统资源。");
                    }
                })
            }
            
        },
        // 用户点赞
        userClickPraise(articleId) {
            const vue = this;
            const token = store.state.user.token;
            if (token == null || token == "") {
                vue.$modal.msgError("请先登录");
            } else {
                $.ajax({
                url: "http://localhost:4000/blog/article/userClickPraise",
                type: "get",
                data: {
                    articleId,
                    userId: store.state.user.id,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == "10000") {
                        vue.$modal.msgSuccess("点赞成功");
                        vue.getArticleInformationByArticleId(vue.articleId)
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                    
                },
                error() {
                    vue.$modal.msgError("认证失败，无法访问系统资源。");
                    }
                })
            }
            
        },
        
        // 通过文章id得到文章标签信息
        getArticleLabelInfoByArticleId(articleId) {
            const vue = this;
            $.ajax({
                url: "http://localhost:4000/check/article/getArticleLabelInfoByArticleId",
                type: "get",
                data: {
                    articleId,
                },
                success(response) {
                    vue.labelList = response.data;
                },
                error() {
                    vue.$modal.msgError("文章标签加载失败");
                }
            })
        },
        // 通过文章id得到文章信息
        getArticleInformationByArticleId(articleId) {
            const vue = this;
            setTimeout(() => {
                $.ajax({
                url: "http://localhost:4000/blog/article/getArticleInformationByArticleId",
                type: "get",
                data: {
                    articleId,
                    userId: store.state.user.id,   
                },
                
                success(response) {
                    vue.articleInformation = response.data
                },
                error() {
                    vue.$modal.msgError("加载文章失败，请重试。")
                }
            })
        }, 1)   
        }
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
.el-col-omit {
     width: 10rem;
     font-size: 15px;
     white-space: nowrap;
     text-overflow: ellipsis;
     overflow: hidden;
     color: rgba(0, 0, 0, 0.5);
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
.userLike {
    cursor: pointer;
    font-size: 7px;
    margin-top: 2px;
    color: rgba(0, 0, 0, 0.5);
}

.userLike:hover {
    color: lightblue ;
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
  .text {
    font-size: 14px;
  }

  .item {
    margin-bottom: 18px;
  }

  .clearfix:before,
  .clearfix:after {
    display: table;
    content: "";
  }
  .clearfix:after {
    clear: both
  }

  .box-card {
    width: 480px;
  }
</style>