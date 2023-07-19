<template>
    <div class="blogview-container" style="margin: 0 auto; width: 1200px;">
        <div style=" width: 1200px;">
            <el-row style="padding-left: 21px;" :gutter="40">
                <el-col :span="17" style="width: 847.5px;">
                    <CarouselMapVue
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
                        <el-aside style=" margin-top: 10px;" width="350px">
                            <el-row >
                                <el-card class="box-card" >
                                    <div slot="header" class="clearfix">
                                        <span class="el-icon-price-tag" style="font-size: 24px; font-weight: 600; font-style: italic;">文章分类</span>
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
                                <el-card class="box-card">
                                    <div slot="header" class="clearfix">
                                        <span class="el-icon-medal-1" style="font-size: 24px; font-weight: 600; font-style: italic;">最近热帖</span>
                                    </div>
                                    <el-row v-for="fireArticle in fireArticleRecently" :key="fireArticle.id">
                                        <div class="fireArticleLink">
                                            <router-link :to="{name: 'check_article', params: {articleId: fireArticle.id}}">
                                                <div class="ellipsis hover" style="margin-top: 5px;">
                                                    {{fireArticle.profile}}
                                                </div>
                                            </router-link>
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
        ArticlePicture
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
                    if (response.code == '10000') {
                        vue.articleSortList = response.data;
                    } else {
                        vue.$modal.msgError("发生未知错误，查询轮播图失败");
                    }
                },
                error() {
                    vue.$modal.msgError("发生未知错误，查询轮播图失败");
                }
            })
        },
        // 点击最近热帖跳到相应的界面
        clickFireArticleRecently(articleId) {
            this.$router.push({
                name: "check_article",
                params: {
                    articleId,
                }
            })
        },

        
        // 查询最近热帖
        getFireArticleRecently() {
            const vue = this;
            $.ajax({
                url: vue.blogArticleUrl + "/fireRecently", 
                type: "get",
                success(response) {
                    if (response.code == "10000") {
                        vue.fireArticleRecently = response.data
                    } else {
                        vue.$modal.msgError(response.msg)
                    }
                },
                error(response) {
                    console.log(response);
                    vue.$modal.msgError(response.msg)
                }
            })
        },
      handleSizeChange(val) {
        this.pageSize = val;
      },
      handleCurrentChange(val) {
        this.currentPage = val;
        this.pagination(this.labelId)
      },
      // 分页函数, 得到文章内容
      pagination(labelId) {
        const vue = this;
        vue.labelId = labelId;
        setTimeout(() => {
            $.ajax({
            url: vue.blogArticleUrl + "/getArticlePagination",
            type: "get",
            data: {
                currentPage: vue.currentPage,
                pageSize: vue.pageSize,
                labelId,
                userId: store.state.user.id
            },
            success(response) {
                if (response.code == "10000") {
                    
                    if (response.data != null) {
                        vue.articleInformation = response.data.records
                         vue.totals = response.data.total;
                    } else {
                        vue.articleInformation = [];
                        vue.totals = 0;
                    }
                } else {
                    vue.$modal.msgError("发生未知错误。");
                }
            },
            erorr() {
                vue.$modal.msgError("发生未知错误。");
            }
        })
        }, 1)
        
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
                if (response.code == '10000') {
                    vue.articleInformation = response.data;
                } else {
                    vue.$modal.msgError("发生未知错误，加载文章内容失败")
                }
            },
            error() {
                vue.$modal.msgError("发生未知错误，加载文章内容失败")
            }
        })
    },
    // 得到标签列表
    getLabelList() {
        const vue = this;
        $.ajax({
            url: vue.blogLabelUrl + "/getLabelList",
            type: "get",
            success(response) {
                if (response.code == "10000") {
                    vue.labelInformation = response.data;
                } else {
                    vue.$modal.msgError("加载标签错误。")
                }
            },
            error() {
                vue.$modal.msgError("加载标签错误。")
            }
        })
    },
    }
}
</script>

<style scoped >
.ellipsis {
    color: gray;
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
  content: "";
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