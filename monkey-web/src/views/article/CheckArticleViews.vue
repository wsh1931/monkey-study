<template>
<div class="checkArticle-container">
    <ReportContent
    v-if="showReportContent"
    @reportContent="reportContent"
    :reportContentType="reportContentType"
    :reportContentAssociationId="reportContentAssociationId"
    @closeReportContent="closeReportContent"/>
    <CollectCard v-if="showCollect"
    :associateId="associateId"
    :showCollect="showCollect"
    :collectType="collectType"
    :collectTitle="collectTitle"
    @closeCollect="closeCollect"/>
    <el-container style="margin-top: 10px;">
        <el-aside width="140px" style="padding-left: 33px;">
            
                <el-badge type="info" 
                    :value="articleInformation.likeSum" 
                    :max="99" 
                    class="item" 
                    style="margin-top: 10px; 
                    position: fixed;">
                        <el-button v-if="articleInformation.isLike == '0'" 
                        @click="userClickPraise(articleInformation)"  
                        size="small" 
                        class="hover "
                        round><span class="iconfont icon-dianzan"></span> 赞</el-button>
                        <el-button v-else @click="userClickPraise(articleInformation)"  
                        size="small"
                        style="background-color: lightgreen"
                        class="hover iconfont icon-dianzan"
                        round >赞</el-button>
                </el-badge>
                
                <el-badge type="info" 
                    :max="99" 
                    class="item" 
                    style="margin-top: 55px; position: fixed;">
                    <el-button @click="userClickOppose(articleInformation)" 
                    size="small" class="hover" round><span class="iconfont icon-cai"></span>&nbsp;踩</el-button>
                </el-badge>

                <el-badge type="info" 
                    :max="99" 
                    :value="articleInformation.collect" 
                    class="item" 
                    style="margin-top: 100px; position: fixed;">
                        <el-button v-if="articleInformation.isCollect == '0'"
                        @click="userCollect(articleInformation.id, articleInformation.title)" 
                        size="small" 
                        class="hover"
                        icon="el-icon-collection" round>收藏</el-button>
                        <el-button v-else @click="userCollect(articleInformation.id, articleInformation.title)"
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

                <el-button 
                style="margin-top: 200px; position: fixed;"
                    size="small"
                    round
                    @click="reportContent(articleInformation.id)" 
                    icon="el-icon-warning-outline"
                    class="hover">
                    举报
                    </el-button>
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
                            <div class="el-icon-view preview"> 
                                游览 {{ getFormatNumber(articleInformation.viewCount) }}</div>
                        </el-col>
                        <el-col :span="6" class="el-icon-time updateTime">
                            {{ getTimeFormat(articleInformation.updateTime) }}
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
                    <vue-markdown 
                    :source="articleInformation.content" 
                    :highlight="true"
                    :html="true"
                    :xhtmlOut="true"></vue-markdown>
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
import ReportContent from '@/components/report/ReportContent'
import { getTimeFormat } from '@/assets/js/DateMethod';
import { getFormatNumber } from '@/assets/js/NumberMethod';
import $ from "jquery"
import VueMarkdown from 'vue-markdown';
import store from "@/store"
import UserInfoCard from "@/components/user/UserInfoCard.vue"
import ArticleComment from '@/components/article/ArticleComment'
import CollectCard from "@/components/collect/CollectCard.vue";

export default {
    name: "CheckArticleViews",

    components: {
        UserInfoCard,
        ArticleComment,
        VueMarkdown,
        CollectCard,
        ReportContent
    },
    data() {
        return {
            // 举报类型(0表示文章，1表示问答，2表示课程, 3表示社区，4表示社区文章, 5表示资源)
            reportContentType: this.reportContentType.article,
            // 举报关联id
            reportContentAssociationId: "0",
            // 显示举报内容框
            showReportContent: false,
             // 收藏类型
            collectType: 0,
            // 收藏标题
            collectTitle: "",
            // 收藏关联id
            associateId: "",
            // 是否展示收藏页面
            showCollect: false,
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
            checkArticleUrl: "http://localhost:80/monkey-article/check",
            blogArticleUrl: "http://localhost:80/monkey-article/blog",
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
        closeReportContent() {
            this.showReportContent = false;
        },
        reportContent(resourceId) {
            this.showReportContent = true;
            this.reportContentAssociationId = resourceId;
        },
        getFormatNumber(val) {
            return getFormatNumber(val);
        },
        getTimeFormat(val) {
            return getTimeFormat(val);
        },
        closeCollect(status) {
            this.showCollect = status
            this.getArticleInformationByArticleId(this.articleId)
        },
        // 关闭评论界面之前，更新文章作者信息
        handleClose() {
            this.drawer = false;
            this.getAuthorInfoByArticleId(this.articleId);
        },
        // 通过文章id查询该文章评论信息
        openDrawer() {
            this.drawer = true;
        },
        // 通过文章id得到作者信息
        getAuthorInfoByArticleId(articleId) {
            const vue = this;
            $.ajax({
                url: vue.checkArticleUrl + "/getAuthorInfoByArticleId",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token
                },
                data: {
                    articleId,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.userInformation = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
            
        },
        // 用户收藏文章
        userCollect(articleId, title) {
            this.associateId = articleId;
            this.showCollect = true;
            this.collectTitle = title;
        },
        // 用户不喜欢
        userClickOppose(article) {
            const vue = this;
                $.ajax({
                url: vue.blogArticleUrl + "/userClickOppose",
                type: "get",
                data: {
                    articleId: article.id,
                    authorId: article.userId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        article.isLike = '0';
                        article.likeSum--;
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
              
                })
            
            
        },
        // 用户点赞
        userClickPraise(article) {
            const vue = this;
                $.ajax({
                url: vue.blogArticleUrl + "/userClickPraise",
                type: "get",
                data: {
                    articleId: article.id,
                    recipientId: article.userId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        if (article.isLike == '0') {
                            article.isLike = '1';
                            article.likeSum++;
                        } else if (article.isLike == '1') {
                            article.isLike = '0';
                            article.likeSum--;
                        }
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                    
                },
                })
        },
        
        // 通过文章id得到文章标签信息
        getArticleLabelInfoByArticleId(articleId) {
            const vue = this;
            $.ajax({
                url: vue.checkArticleUrl + "/getArticleLabelInfoByArticleId",
                type: "get",
                data: {
                    articleId,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.labelList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                    
                },
            })
        },
        // 通过文章id得到文章信息
        getArticleInformationByArticleId(articleId) {
            const vue = this;
                $.ajax({
                url: vue.blogArticleUrl + "/getArticleInformationByArticleId",
                    type: "get",
                    headers: {
                    Authorization: "Bearer" + store.state.user.token
                    },
                    data: {
                        articleId, 
                    },
                    
                    success(response) {
                        if (response.code == vue.ResultStatus.SUCCESS) {
                            vue.articleInformation = response.data
                        } else {
                            vue.$modal.msgError(response.msg);
                        }
                        
                    },
            })
        }
    }
}
</script>

<style scoped>
.updateTime {
    font-size: 13.5px; 
    color: rgba(0, 0, 0, 0.5); 
    margin-top: 14px;
}
.preview {
    font-size: 13.5px; 
    margin-top: 14px; 
    color: #409EFF;
}
.checkArticle-container {
    width: 1400px; 
    margin: 10px auto;
}
.bottom{
    position: relative;
    z-index: 1;
}
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