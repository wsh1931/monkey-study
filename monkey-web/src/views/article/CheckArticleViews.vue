<template>
<div id="checkArticle-container" style="display: flex; justify-content: center;align-items: center;">
    <el-container>
        <el-container>\
        <el-main style="padding-left: 170px;">
            <el-row>
                <el-card class="box-card" style="width: 100%;">
                <div slot="header" class="clearfix">
                    <span>{{ articleInformation.title }}</span>
                </div>
                <el-row>
                    {{ articleInformation.content }}
                </el-row>
                
            </el-card>
            </el-row>
            
        </el-main>
        
            <el-footer style="padding-left: 170px;">用户评论</el-footer>
        </el-container>
        <el-aside width="200px" style="padding-right: 110px; margin-top: 20px;">
            
        </el-aside>
        
    </el-container>
</div>
</template>

<script>
import $ from "jquery"
export default {
    name: "CheckArticleViews",
    data() {
        return {
            // 文章id
            articleId: "",
            // 文章信息
            articleInformation: "",
            token: this.$store.state.user.token,
        }
    },

    created() {
        this.articleId = this.$route.params.articleId;
        this.getArticleInformationByArticleId(this.articleId);
    },
    methods: {
        // 通过文章id得到文章信息
        getArticleInformationByArticleId(articleId) {
            const vue = this;
            $.ajax({
                url: "http://localhost:4000/blog/article/getArticleInformationByArticleId",
                type: "get",
                data: {
                    articleId
                },
                success(response) {
                    vue.articleInformation = response.data
                },
                error() {
                    vue.$modal.msgError("加载文章失败，请重试。")
                }
            })
        }
    }
}
</script>

<style scoped>
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