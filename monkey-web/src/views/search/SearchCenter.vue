<template>
    <div class="MonkeyWebSearchCenter-container">
        <el-row>
            <el-col :span="18">
                <el-tabs v-model="activeName" @tab-click="handleClick" class="el-tabs">
                    <el-tab-pane name="search_all">
                        <span slot="label">
                            <i class="iconfont icon-quanbu">&nbsp;</i>
                            <span>全站</span>
                        </span>
                    </el-tab-pane>
                    <el-tab-pane name="search_resource">
                        <span slot="label">
                            <i class="iconfont icon-ziyuan">&nbsp;</i>
                            <span>资源</span>
                        </span>
                    </el-tab-pane>
                    <el-tab-pane label="社区文章" name="search_community_article">
                        <span slot="label">
                            <i class="iconfont iconfont icon-shequ1">&nbsp;</i>
                            <span>社区文章</span>
                        </span>
                    </el-tab-pane>
                    <el-tab-pane label="课程" name="search_course">
                        <span slot="label">
                            <i class="iconfont icon-kecheng-">&nbsp;</i>
                            <span>课程</span>
                        </span>
                    </el-tab-pane>
                    <el-tab-pane name="search_question">
                        <span slot="label">
                            <i class="iconfont icon-tiwenquestion">&nbsp;</i>
                            <span>问答</span>
                        </span>
                    </el-tab-pane>
                    <el-tab-pane  name="search_article">
                        <span slot="label">
                            <i class="iconfont icon-wenzhang">&nbsp;</i>
                            <span>文章</span>
                        </span>
                    </el-tab-pane>
                </el-tabs>
                <router-view></router-view>
            </el-col>
            <el-col :span="6">

            </el-col>
        </el-row>
    </div>
</template>

<script>
export default {
    name: 'MonkeyWebSearchCenter',

    data() {
        return {
            activeName: "first",
        };
    },

    created() {
        this.activeName = this.$route.name;
    },

    methods: {
        toSearchAllViews() {
            this.$router.push({
                name: "search_all",
                query: {
                    keyword: this.$route.query.keyword,
                }
            })
        },
        toSearchResourceViews() {
            this.$router.push({
                name: "search_resource",
                query: {
                    keyword: this.$route.query.keyword,
                }
            })
        },
        toSearchCommunityArticleViews() {
            this.$router.push({
                name: "search_community_article",
                query: {
                    keyword: this.$route.query.keyword,
                }
            })
        },
        toSearchCourseViews() {
            this.$router.push({
                name: "search_course",
                query: {
                    keyword: this.$route.query.keyword,
                }
            })
        },
        toSearchQuestionViews() {
            this.$router.push({
                name: "search_question",
                query: {
                    keyword: this.$route.query.keyword,
                }
            })
        },
        toSearchArticleViews() {
            this.$router.push({
                name: "search_article",
                query: {
                    keyword: this.$route.query.keyword,
                }
            })
        },
        handleClick() {
            if (this.activeName == 'search_all') {
                this.toSearchAllViews();
            } else if (this.activeName == 'search_resource') {
                this.toSearchResourceViews();
            } else if (this.activeName == 'search_community_article') {
                this.toSearchCommunityArticleViews();
            } else if (this.activeName == 'search_course') {
                this.toSearchCourseViews();
            } else if (this.activeName == 'search_question') {
                this.toSearchQuestionViews();
            } else if (this.activeName == 'search_article') {
                this.toSearchArticleViews();
            }
        }
    },
};
</script>

<style scoped>
.el-tabs {
    background-color: #fff;
    padding: 0 20px;
}
.MonkeyWebSearchCenter-container {
    margin: 10px auto;
    width: 1100px;
}
</style>