<template>
    <div class="blogview-container" >
        <div style=" width: 1200px;">
            <el-row style="padding-left: 21px;" :gutter="40">
                <el-col :span="17" style="width: 847.5px;">
                    <CarouselMapVue
                    class="bottom"
                    :articleSortList="articleSortList"/>
                </el-col>
                <el-col :span="7" style="padding: 0;">
                    <ArticlePicture
                    :article="articleSortList[0]"/>
                    <ArticlePicture
                    :article="articleSortList[1]"/>
                </el-col>
            </el-row>
        </div>
                <el-container >
                        <!-- 左边信息框 -->
                        <el-container >
                            <el-main>
                            <ArticleCard :articleInformation="articleInformation" 
                            @pagination="pagination"    
                            :labelId="labelId"
                            :currentPage="currentPage" 
                            :pageSize="pageSize"/>
                            </el-main>
                            <PagiNation :totals="totals" 
                            :currentPage="currentPage" 
                            :pageSize="pageSize" 
                            @handleCurrentChange = "handleCurrentChange"
                            @handleSizeChange="handleSizeChange"/>
                            
                    <el-footer>
                    </el-footer>
                        </el-container>
                        <el-aside class="el-aside">
                            <el-row >
                                <el-card class="box-card gradient-class right-slide" >
                                    <div slot="header" class="clearfix">
                                        <span class="article-class iconfont icon-fenlei" >文章分类</span>
                                    </div>
                                    <div class="animated-buttons">
                                    <el-button @click="pagination(label.id)" 
                                    v-for="label in labelInformation" :key="label.id"  
                                    class="button-bubble hover" 
                                    size="small">{{label.labelName}}</el-button>
                                    </div>
                                </el-card>
                            </el-row>

                            <el-row>
                                <el-card class="box-card gradient-fire right-slide">
                                    <div slot="header" class="clearfix">
                                        <span class="el-icon-medal-1 fire-article iconfont icon-yushouhuore">最近热帖</span>
                                    </div>
                                    <el-row v-for="fireArticle in fireArticleRecently" :key="fireArticle.id" style="cursor: pointer;">
                                        <div class="fireArticleLink" @click="clickFireArticleRecently(fireArticle.id)">
                                                <div class="ellipsis hover" style="margin-top: 5px;">
                                                    {{fireArticle.profile}}
                                                </div>
                                        </div>
                                    </el-row>
                                </el-card>
                            </el-row>
                        </el-aside>
                </el-container>
            </div>
</template>

<script>
import $ from "jquery"
import store from "@/store";
import ArticleCard from "@/components/article/ArticleCard.vue";
import PagiNation from "@/components/pagination/PagiNation.vue";
import CarouselMapVue from '@/components/carousel/CarouselMap.vue';
import ArticlePicture from '@/components/article/ArticlePicture'
export default {
    name: "BlogView",
    components: {
        ArticleCard,
        PagiNation,
        CarouselMapVue,
        ArticlePicture,
    },
    data() {
        return {
            blogArticleUrl: "http://localhost:80/monkey-article/blog",
            blogLabelUrl: "http://localhost:80/monkey-article/blog/label",
            articleSortList: [], // 文章排序列表
            labelInformation: [], // 标签信息
            articleInformation: [], // 文章内容
            // 分页参数
            currentPage: 1,
            totals: 0,
            pageSize: 10,
            labelId: -1,
            // 最近热帖
            fireArticleRecently: [],
            
        }
    },
    watch: {
        $route() {
            window.location.reload(); //监测到路由发生跳转时刷新一次页面
        },
    },

    created() {
        this.getLabelList();
        this.pagination(this.labelId);
        this.getFireArticleRecently();
        this.getArticleListBySort();
    },

    methods: {
        // 按排序字段得到文章列表
        getArticleListBySort() {
            const vue = this;
            $.ajax({
                url: vue.blogArticleUrl + "/getArticleListBySort",
                type: "get",
                success(response) {
                    if (response.code == '200') {
                        vue.articleSortList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
        },
        // 点击最近热帖跳到相应的界面
        clickFireArticleRecently(articleId) {
            const { href } = this.$router.resolve({
                name: "check_article",
                params: {
                    articleId
                }
            })

            window.open(href, '_black')
        },

        
        // 查询最近热帖
        getFireArticleRecently() {
            const vue = this;
            $.ajax({
                url: vue.blogArticleUrl + "/fireRecently", 
                type: "get",
                success(response) {
                    if (response.code == "200") {
                        vue.fireArticleRecently = response.data
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
        },
        handleSizeChange(val) {
            this.pageSize = val;
            this.pagination(this.labelId)
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            this.pagination(this.labelId)
        },
        // 分页函数, 得到文章内容
        pagination(labelId) {
            const vue = this;
            vue.labelId = labelId;
                $.ajax({
                url: vue.blogArticleUrl + "/getArticlePagination",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    labelId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == "200") {
                        
                        if (response.data != null) {
                            vue.articleInformation = response.data.records
                            vue.totals = response.data.total;
                        } else {
                            vue.articleInformation = [];
                            vue.totals = 0;
                        }
                    } else {
                            vue.$modal.msgError(response.msg);
                        }
                },
            })
            },
        // 通过点击标得到文章内容
        getArticleByLabelName(labelId) {
            const vue = this;
            $.ajax({
                url: vue.blogArticleUrl + "/getArticleContentByLabelId",
                type: "get",
                data: {
                    labelId,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.articleInformation = response.data;
                    } else {
                            vue.$modal.msgError(response.msg);
                    }
                },
            })
        },
        // 得到标签列表
        getLabelList() {
            const vue = this;
            $.ajax({
                url: vue.blogLabelUrl + "/getLabelList",
                type: "get",
                success(response) {
                    if (response.code == "200") {
                        vue.labelInformation = response.data;
                    } else {
                            vue.$modal.msgError(response.msg);
                        }
                },
            })
        },
    }
}
</script>

<style scoped >
.el-aside {
    margin-top: 10px; 
    padding: 10px;
    width:350px;
}
.blogview-container {
    margin: 10px auto; 
    width: 1200px;
}
.right-slide {
    animation: right-in 0.6s linear;
}

@keyframes right-in {
    0% {
        opacity: 0;
        transform: translateX(100px);
        
    }
    100% {
        opacity: 1;
        transform: translateX(0);
        
    }
}
/* .gradient-class {
    background-image: linear-gradient(to top, #d5d4d0 0%, #d5d4d0 1%, #eeeeec 31%, #efeeec 75%, #e9e9e7 100%);
}

.gradient-fire {
    background-image: linear-gradient(to top, #d5d4d0 0%, #d5d4d0 1%, #eeeeec 31%, #efeeec 75%, #e9e9e7 100%);
} */


.article-class {
    padding: 3px;
    font-size: 24px; 
    font-weight: bold; 
    font-style: italic;
    /* text-shadow: 1px 1px 10px #409EFF; */
   background-image: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: transparent;
    background-clip: text;
    

    /* padding: 2px;
      font-size: 24px;
      font-weight: bold;
      font-style: italic;
    background-image: linear-gradient(to right, #00dbde 0%, #fc00ff 100%);
      color: transparent;
      background-clip: text;
      font-family: "方正手迹"; */
    
}

.fire-article {
    padding: 3px;
    font-size: 24px; 
    font-weight: 600; 
    font-style: italic;

    background-image: linear-gradient(to top, #f43b47 0%, #453a94 100%);
    background-clip: text;
    color: transparent;
    font-family: "方正手迹";
}
.bottom{
    position: relative;
    z-index: 1;
}
.ellipsis {
    color: black;
  display: block;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  text-align: left;
}
.hover:hover {
    transition: 0.5s ease;
    color: #409EFF;
    background-color: #EEEEEE;
}

.hover-border:hover {
    box-shadow: 0 0 5px 3px lightblue;
}

.box-card:hover {
    box-shadow: 0 0 20px 8px #409EFF;
     position: relative;
    top: -1px;
    transition: 0.5s linear all;
   
}

.box-card {
    border-radius: 20px;
    margin-top: 10px;
  border-radius: 2px;
  margin-bottom: 20px;
  border: 1px solid #dcdfe6;
  -webkit-box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
  box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
}
.animated-buttons {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-wrap: wrap;
}

.button-bubble {
  position: relative;
  border-radius: 20px;
  background-color: #e91e63;
  color: #fff;
  border: none;
  margin: 5px;
  box-shadow: none;
  cursor: pointer;
  z-index: 1;
  overflow: hidden;
}

.button-bubble:before {
  position: absolute;
  top: -50px;
  left: -50px;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.2);
  opacity: 0;
  z-index: -1;
  transform: scale(0);
  transition: all ease-in-out 0.8s;
}

.button-bubble:hover:before {
  opacity: 1;
  transform: scale(3);
}
</style>