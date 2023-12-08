<template>
    <div class="MonkeyWebHistoryView-container">
        <el-tabs v-model="activeName" @tab-click="handleClick">
            <el-tab-pane 
            @click="toContentHistoryViews()"
            label="内容" 
            name="user_center_history_content"></el-tab-pane>
            <el-tab-pane 
            @click="toCommentHistoryViews()"
            label="评论" 
            name="user_center_history_comment"></el-tab-pane>
            <el-tab-pane 
            @click="toLikeHistoryViews()"
            label="点赞" 
            name="user_center_history_like"></el-tab-pane>
            <router-view></router-view>
        </el-tabs>
    </div>
</template>

<script>
export default {
    name: 'MonkeyWebHistoryView',

    data() {
        return {
            activeName: "content",
        };
    },
    watch: {
        $route(val) {
            this.activeName = this.$route.val
        }
    },
    created() {
        this.activeName = this.$route.name
    },
    methods: {
        handleClick() {
            if (this.activeName == 'user_center_history_content') {
                this.toContentHistoryViews();
            } else if (this.activeName == "user_center_history_comment") {
                this.toCommentHistoryViews();
            } else if (this.activeName == 'user_center_history_like') {
                this.toLikeHistoryViews();
            }
        },
        // 前往内容历史页面
        toContentHistoryViews() {
            this.$router.push({
                name: "user_center_history_content"
            })
        },
        // 前往评论历史页面
        toCommentHistoryViews() {
            this.$router.push({
                name: "user_center_history_comment"
            })
        },
        // 前往点赞历史页面
        toLikeHistoryViews() {
            this.$router.push({
                name: "user_center_history_like"
            })
        }
    },
};
</script>

<style scoped>
.MonkeyWebHistoryView-container {
    background-color: #fff;
    padding: 20px;
}
</style>