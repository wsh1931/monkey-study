<template>
    <div class="ArticleCard-container">
        <el-card  class="box-card hover-border" v-for="article in articleInformation" :key="article.id">
            <el-row :gutter="10">
                <el-col :span="6">
                        <img style="width: 100%;" :src="article.photo">
                </el-col>

                <el-col :span="18">
                    <el-row>
                        <el-col :span="15" style="text-align: left;">
                            <h3>{{ article.title }}</h3>
                        </el-col>
                        <el-col :span="9">
                            <h3 class="el-icon-time"> {{ article.updateTime | formatDate}}</h3>
                        </el-col>
                    </el-row>
                    <el-row>
                        <p class="text item" style="text-align: left;">
                        {{ article.profile }}
                    </p>
                    </el-row>
                <el-row>
                    <el-col :span="3" >
                        <el-button v-if="article.isLike == '0'" @click="userClickPraise(article.id)" type="text" icon="el-icon-caret-top" round >赞 {{ article.likeSum }}</el-button>
                        <el-button v-else @click="userClickPraise(article.id)" type="text" style="color: lightgreen;" icon="el-icon-caret-top" round >赞 {{ article.likeSum }}</el-button>
                    </el-col>
                        <el-col :span="3">
                        <el-button @click="userClickOppose(article.id)" type="text" icon="el-icon-caret-bottom" round>踩</el-button>
                        </el-col>
                    <el-col :span="4" >
                        <el-button v-if="article.isCollect == '0'" @click="userCollect(article.id)" type="text" icon="el-icon-collection" round>收藏 {{ article.collect }}</el-button>
                        <el-button v-else @click="userCollect(article.id)" type="text" icon="el-icon-collection" style="color: lightseagreen" round>已收藏 {{ article.collect }}</el-button>
                    </el-col>
                    <el-col :span="5">
                        <div style="font-size: 13.5px; margin-top: 14px; margin-left: 5px; color: #409EFF;" class="el-icon-view"> 游览 {{ article.visit }}</div>
                    </el-col>
                    <el-col :span="9">
                        <el-button style="float: right;" ref="childElement" class="animated-button" size="small" @click="checkArticle(article.id)">查看文章</el-button>
                    </el-col>
                    
                </el-row>
                </el-col>
            </el-row>
        </el-card>
    </div>
</template>

<script>
import store from '@/store';
import $ from "jquery"
export default {
    name: "ArticleCard",
    data() {
        return {
            blogArticleUrl: "http://localhost:80/monkey-article/blog",
            checkArticleUrl: "http://localhost:80/monkey-article/check",
        }
    },
    props: {
        articleInformation: Array,
        labelId: Number,
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
    
    methods: {
        // 用户点赞
        userClickPraise(articleId) {
            const vue = this;
            const token = store.state.user.token;
            if (token == null || token == "") {
                vue.$modal.msgError("请先登录");
            } else {
                $.ajax({
                url: vue.blogArticleUrl + "/userClickPraise",
                type: "get",
                data: {
                    articleId,
                    userId: store.state.user.id,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == "200") {
                        vue.$modal.msgSuccess("点赞成功");
                        vue.$emit("pagination", vue.$props.labelId);
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
        // 用户收藏文章
        userCollect(articleId) {
            const vue = this;
            const token = store.state.user.token;
            if (token == null || token == "") {
                vue.$modal.msgError("请先登录");
            } else {

                $.ajax({
                url: vue.blogArticleUrl + "/userCollect",
                type: "get",
                data: {
                    articleId,
                    userId: store.state.user.id,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == "200") {
                        vue.$modal.msgSuccess(response.msg);
                        vue.$emit("pagination", vue.$props.labelId)
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
                url: vue.blogArticleUrl + "/userClickOppose",
            
                type: "get",
                data: {
                    articleId,
                    userId: store.state.user.id,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == "200") {
                        vue.$modal.msgSuccess(response.msg);
                        vue.$emit("pagination", vue.$props.labelId)
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
        // 查看文章, 文章游览数加一且实现路由跳转
        checkArticle(articleId) {
            const vue = this;
            $.ajax({
                url: vue.checkArticleUrl + "/addAtricleVisit",
                type: "post",
                data: {
                    articleId,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.$router.push({
                        name: "check_article",
                        params: {
                            articleId,
                        }
                    })
                    } else {
                        vue.$modal.msgError("查看文章失败，发送未知错误");
                    }
                },
                error() {
                    vue.$modal.msgError("查看文章失败，发送未知错误");
                }
            })
        },
        
    },
}
</script>

<style scoped>

.text {
    font-size: 14px;
  }

  .item {
    margin-bottom: 18px;
  }

    .box-card {
    border-radius: 20px;
    margin-bottom: 10px;
    border-radius: 2px;
    border: 1px solid #dcdfe6;
    -webkit-box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
    box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
    }
    .hover-border:hover {
    box-shadow: 0 0 5px 3px lightblue;
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

img {
    width: 175px;
    height: 150px;
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
</style>