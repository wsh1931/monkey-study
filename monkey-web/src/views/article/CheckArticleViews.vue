<template>
<div id="checkArticle-container" style="width: 1400px; margin: 10px auto;">
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
                        class="hover"
                        round>赞</el-button>
                        <el-button v-else @click="userClickPraise(articleInformation.id)"  
                        size="small" icon="el-icon-caret-top" 
                        style="background-color: lightgreen" 
                        class="hover"
                        round >赞</el-button>
                </el-badge>
                
                <el-badge type="info" 
                    :max="99" 
                    class="item" 
                    style="margin-top: 55px; position: fixed;">
                    <el-button @click="userClickOppose(articleInformation.id)" 
                    size="small" icon="el-icon-caret-bottom" class="hover" round>踩</el-button>
                </el-badge>

                <el-badge type="info" 
                    :max="99" 
                    :value="articleInformation.collect" 
                    class="item" 
                    style="margin-top: 100px; position: fixed;">
                        <el-button v-if="articleInformation.isCollect == '0'"
                        @click="userCollect(articleInformation.id)" 
                        size="small" 
                        class="hover"
                        icon="el-icon-collection" round>收藏</el-button>
                        <el-button v-else @click="userCollect(articleInformation.id)"
                        size="small" 
                        class="hover"
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
                    @click="openDrawer()"
                    icon="el-icon-s-comment"
                    class="hover">
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
                        <!-- TODO举报功能 -->
                        <el-col :span="2" class="el-icon-warning-outline" style="font-size: 13.5px; margin-top: 14px; color: #409EFF;">
                             举报</el-col>
                        <el-col :span="6" class="el-icon-time" style="font-size: 13.5px; color: rgba(0, 0, 0, 0.5); margin-top: 14px;">
                            {{ articleInformation.updateTime }}
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
                    :scrollStyle="true">
                    </mavon-editor>
                </el-row>
            </el-card>
            </el-row>
            
        </el-main>
        
            
        </el-container>
        <el-aside width="400px" style="margin-left: 10px;">
            <UserInfoCard
            :userInformation="userInformation"
            @getAuthorInfoByArticleOrQuestionId="getAuthorInfoByArticleId"
            :articleOrQuestionId="articleId"/>
        </el-aside>
        
    </el-container>
    
        <!-- 用户评论界面 -->
        <el-drawer
        title="用户评论"
        :visible.sync="drawer"
        direction="rtl"
        :before-close="handleClose">
        <ArticleComment/>
        </el-drawer>
    </div>
</template>

<script>
import $ from "jquery"
import { mavonEditor } from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'
import store from "@/store"
import UserInfoCard from "@/components/user/UserInfoCard.vue"
import ArticleComment from '@/components/article/ArticleComment'

export default {
    name: "CheckArticleViews",

    components: {
        mavonEditor,
        UserInfoCard,
        ArticleComment
    },
    data() {
        return {
            toc: "",  //目录Markdown文本
            drawer: false,
            // 文章id
            articleId: "",
            // 评论回复内容
            replyContent: "",
            // 文章信息
            articleInformation: [],
            userInformation: [],
            labelList: [],
            checkArticleUrl: "http://localhost:4100/check/article",
            blogArticleUrl: "http://localhost:4100/blog/article",
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
    },
    methods: {
        // 关闭评论界面之前，更新文章作者信息
        handleClose() {
            this.drawer = false;
            this.getArticleInformationByArticleId(this.articleId);
            this.getAuthorInfoByArticleId(this.articleId);
        },
        // 通过文章id查询该文章评论信息
        openDrawer() {
            this.drawer = true;
        },
        // 通过文章id得到作者信息
        getAuthorInfoByArticleId(articleId) {
            const vue = this;
            setTimeout(() => {
                $.ajax({
                url: vue.checkArticleUrl + "/getAuthorInfoByArticleId",
                type: "get",
                data: {
                    articleId,
                    userId: store.state.user.id
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
                url: vue.blogArticleUrl + "userCollect",
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
                url: vue.blogArticleUrl + "userClickOppose",
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
                url: vue.blogArticleUrl + "userClickPraise",
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
        
        // 通过文章id得到文章标签信息
        getArticleLabelInfoByArticleId(articleId) {
            const vue = this;
            $.ajax({
                url: vue.checkArticleUrl + "getArticleLabelInfoByArticleId",
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
                url: vue.blogArticleUrl + "getArticleInformationByArticleId",
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
.hover:hover {
    transition: 0.5s ease;
    transform: scale(1.07) translate3d(0,0,0);
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