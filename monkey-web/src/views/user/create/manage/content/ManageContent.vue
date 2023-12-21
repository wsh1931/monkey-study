<template>
    <div class="MonkeyWebManageContent-container">
        <el-tabs v-model="activeName" @tab-click="handleClick">
            <el-tab-pane label="资源" name="resource"></el-tab-pane>
            <el-tab-pane label="课程" name="course"></el-tab-pane>
            <el-tab-pane label="问答" name="question"></el-tab-pane>
            <el-tab-pane label="文章" name="article"></el-tab-pane>
            <el-tab-pane label="社区文章" name="communityArticle"></el-tab-pane>
            <router-view></router-view>
        </el-tabs>
    </div>
</template>

<script>
export default {
    name: 'MonkeyWebManageContent',

    data() {
        return {
            activeName: "resource",
        };
    },

    created() {
        this.updateActive();
    },

    methods: {
        updateActive() {
            if (this.activeName == "resource") {
                this.$router.push({
                    name: "manage_resource_content"
                })
            } else if (this.activeName == "course") {
                this.$router.push({
                    name: "manage_course_content"
                })
            } else if (this.activeName == "question") {
                this.$router.push({
                    name: "manage_question_content"
                })
            } else if (this.activeName == "article") {
                this.$router.push({
                    name: "manage_article_content"
                })
            } else if (this.activeName == "communityArticle") {
                this.$router.push({
                    name: "manage_community_article_content"
                })
            }
        },
        handleClick() {
            this.updateActive();
        }
    },
};
</script>

<style scoped>
::v-deep .el-tabs__nav-wrap::after {
    content: none;
    position: absolute;
    left: 0;
    width: 100%;
    height: 0;
    background-color: #fff;
    z-index: 1;
}
.MonkeyWebManageContent-container {
    padding: 10px 20px 0 20px;
    background-color: #fff;
}
</style>