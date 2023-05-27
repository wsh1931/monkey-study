<template>
    <div class="blogview-container" style="text-align: center;overflow:auto">
                <el-container >
                        <!-- 左边信息框 -->
                        <el-container>
                            <el-main style="padding-left: 170px;">
                                <el-card  class="box-card" v-for="article in articleInformation" :key="article.id" @click="getLabelName(label.id)" shadow="hover">
                                        <el-row :gutter="10">
                                            <el-col :span="6">
                                                    <img :src="article.photo" alt="" >
                                            </el-col>

                                            <el-col :span="18">
                                                <el-row>
                                                    <el-col :span="18" style="text-align: left;">
                                                        <h3>{{ article.title }}</h3>
                                                    </el-col>
                                                    <el-col :span="6">
                                                        <h3>{{ article.createTime | formatDate}}</h3>
                                                        
                                                    </el-col>
                                                </el-row>
                                                <el-row>
                                                    <p class="text item" style="text-align: left;">
                                                    {{ article.profile }}
                                                </p>
                                                </el-row>
                                            <el-row>
                                                <el-col :span="4" >
                                                    <el-button @click="userClickPraise(article.id)" type="text" icon="el-icon-caret-top" round >赞 {{ article.likeSum }}</el-button>
                                                </el-col>
                                                    <el-col :span="4">
                                                    <el-button @click="userClickOppose(article.id)" type="text" icon="el-icon-caret-bottom" round>踩</el-button>
                                                    </el-col>
                                                <el-col :span="4">
                                                    <el-button @click="userCollect(article.id)" type="text" icon="el-icon-collection" round>收藏 {{ article.collect }}</el-button>
                                                </el-col>
                                                <el-col :span="4">
                                                    <div style="font-size: 13.5px; margin-top: 14px; color: #409EFF;" class="el-icon-view"> 游览 {{ article.visit }}</div>
                                                </el-col>
                                                <el-col :span="8">
                                                    <el-button style="float: right;" class="animated-button" size="small" @click="checkArticle(article.id)">查看文章</el-button>
                                                </el-col>
                                                
                                            </el-row>
                                            </el-col>
                                        </el-row>
                                    </el-card>

                            </el-main>

                            <!-- 分页功能 -->
                            <div class="block">
                                <el-pagination
                                @size-change="handleSizeChange"
                                @current-change="handleCurrentChange"
                                :current-page="currentPage"
                                :page-sizes="[10, 20, 50, 100]"
                                :page-size="pageSize"
                                layout="total, sizes, prev, pager, next, jumper"
                                :total="parseInt(totals)">
                                </el-pagination>
                            </div>
                            
                <el-footer>
                        footer
                </el-footer>
                        </el-container>
                        <el-aside style="padding-right: 110px; margin-top: 20px;" width="450px">
                            <el-row >
                                <el-card class="box-card" >
                                    <div slot="header" class="clearfix">
                                        <span class="el-icon-price-tag" style="font-size: 24px; font-weight: 600; font-style: italic;">文章分类</span>
                                    </div>
                                    <div class="animated-buttons">
                                    <el-button @click="pagination(currentPage, pageSize, label.id)" v-for="label in labelInformation" :key="label.id"  class="button-bubble" size="small">{{label.labelName}}</el-button>
                                    </div>
                                </el-card>
                            </el-row>

                            <el-row>
                                <el-card class="box-card">
                                    <div slot="header" class="clearfix">
                                        <span class="el-icon-medal-1" style="font-size: 24px; font-weight: 600; font-style: italic;">最近热帖</span>
                                    </div>
                                    <el-row @click="clickFireArticleRecently(fireArticle.id)" v-for="fireArticle in fireArticleRecently" :key="fireArticle.id">
                                        <div class="fireArticleLink">
                                            <router-link :to="{name: 'home'}">
                                                <div class="ellipsis">
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

export default {
    name: "BlogView",
    data() {
        return {
            activeName: 'second',
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
        this.getLabelList();
        this.pagination(this.currentPage, this.pageSize, this.labelId);
        this.getFireArticleRecently();
        
    },

    methods: {
        // 查看文章实现路由跳转
        checkArticle(articleId) {
            this.$router.push({
                name: "check_article",
                params: {
                    articleId,
                }
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
                        vue.pagination(vue.currentPage, vue.pageSize, vue.labelId);
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
                        vue.pagination(vue.currentPage, vue.pageSize, vue.labelId);
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
                        vue.pagination(vue.currentPage, vue.pageSize, vue.labelId);
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
        // 查询最近热帖
        getFireArticleRecently() {
            const vue = this;
            $.ajax({
                url: "http://localhost:4000/blog/article/fireRecently",
                type: "get",
                success(response) {
                    if (response.code == "10000") {
                        vue.fireArticleRecently = response.data
                    } else {
                        vue.$modal.msgError("发生未知错误")
                    }
                },
                error() {
                    vue.$modal.msgError("发生未知错误")
                }
            })
        },
        getLabelName(labelId) {
            this.labelId = labelId;
        },
        handleSizeChange(val) {
        console.log(`每页 ${val} 条`);
      },
      handleCurrentChange(val) {
        console.log(`当前页: ${val}`);
        this.pagination(`${val}`, this.pageSize, this.labelId)
      },
      // 分页函数, 得到文章内容
      pagination(currentPage, pageSize, labelId) {
        const vue = this;
        vue.labelId = labelId;
        $.ajax({
            url: "http://localhost:4000/blog/article/pagination",
            type: "get",
            data: {
                currentPage,
                pageSize,
                labelId

            },
            success(response) {
                if (response.code == "10000") {
                    vue.articleInformation = response.data.records
                    vue.totals = response.data.total
                } else {
                    vue.$modal.msgError("发生未知错误。");
                }
            },
            erorr() {
                vue.$modal.msgError("发生未知错误。");
            }
        })
      },
    // 通过点击标得到文章内容
    getArticleByLabelName(labelId) {
        const vue = this;
        $.ajax({
            url: "http://localhost:4000/blog/article/getArticleContentByLabelId",
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
            url: "http://localhost:4000/blog/label/getLabelList",
            type: "get",
            success(response) {
                if (response.code == "10000") {
                    vue.labelInformation = response.data.labelList;
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

.box-card {
    border-radius: 20px;
    margin-top: 10px;
  border-radius: 2px;
  margin-bottom: 20px;
  border: 1px solid #dcdfe6;
  -webkit-box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
  box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
}
.ellipsis {
    color: gray;
  display: block;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  text-align: left;
}
div.fireArticleLink {
    text-align: le;
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
.animated-button {
  display: inline-block;
  position: relative;
  overflow: hidden;
  background-color: #000;
  color: #fff;
  border: 0;
  font-size: 1.2em;
  letter-spacing: 1px;
  cursor: pointer;
}

.animated-button:after {
  content: "";
  position: absolute;
  width: 0;
  height: 0;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: #ffedbc;
  opacity: 0.75;
  border-radius: 100%;
}

.animated-button:hover:after {
  width: 250%;
  height: 250%;
  transition: all 0.5s ease-in-out;
}

.animated-button:hover {
  color: #000;
  background-color: #ffedbc;
}


img {
    width: 175px;
    height: 150px;
}
  .text {
    font-size: 14px;
  }

  .item {
    margin-bottom: 18px;
  }


  .el-tag{
    white-space: normal;
    height:auto;
    background-color: lightblue;
}

</style>