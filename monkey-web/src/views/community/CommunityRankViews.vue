<template>
    <div class="MonkeyWebCommunityRankViews-container">
        <el-tabs v-model="activeName" @tab-click="handleClick()">
            <el-tab-pane label="游览数" name="viewCount"></el-tab-pane>
            <el-tab-pane label="成员数" name="memberCount"></el-tab-pane>
            <el-tab-pane label="文章数" name="articleCount"></el-tab-pane>
            <el-tab-pane label="点赞人数" name="likeCount"></el-tab-pane>
            <el-tab-pane label="评论人数" name="commentCount"></el-tab-pane>
            <el-tab-pane label="评分人数" name="scoreCount"></el-tab-pane>
            <el-tab-pane label="收藏人数" name="collectCount"></el-tab-pane>
            <CommunityRankCard
            :communityList="communityList"/>
        </el-tabs>
    </div>
</template>

<script>
import $ from "jquery"
import PagiNation from '@/components/pagination/PagiNation.vue';
import CommunityRankCard from '@/components/community/CommunityRankCard.vue';
export default {
    name: 'MonkeyWebCommunityRankViews',
    components: {
        CommunityRankCard,
        PagiNation
    },
    data() {
        return {
            activeName: "viewCount",
            // 0表示游览，1表示成员以此类推
            status: '0',
            communityRankUrl: "http://localhost:80/monkey-community/rank",
            // 社区文章列表
            communityList: [],
        };
    },

    created() {
        this.queryCommunityViewCount();
    },

    methods: {
        handleClick() {
            this.pageSize = 10;
            this.currentPage = 1;
            if (this.activeName == 'viewCount') {
                this.queryCommunityViewCount();
            } else if (this.activeName == 'memberCount') {
                this.queryCommunityMemberCount();
            } else if (this.activeName == 'articleCount') {
                this.queryCommunityArticleCount();
            } else if (this.activeName == 'likeCount') {
                this.queryCommunityLikeCount();
            } else if (this.activeName == 'commentCount') {
                this.queryCommunityCommentCount();
            } else if (this.activeName == 'scoreCount') {
                this.queryCommunityScoreCount();
            } else if (this.activeName == 'collectCount') {
                this.queryCommunityCollectCount();
            }
        },
        // 查询社区游览数排行
        queryCommunityViewCount() {
            const vue = this;
            $.ajax({
                url: vue.communityRankUrl + "/queryCommunityViewCount",
                type: "get",
                success(response) {
                    if (response.code == '200') {
                        vue.communityList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询社区成员数排行
        queryCommunityMemberCount() {
            const vue = this;
            $.ajax({
                url: vue.communityRankUrl + "/queryCommunityMemberCount",
                type: "get",
                success(response) {
                    if (response.code == '200') {
                        vue.communityList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询社区文章数排行
        queryCommunityArticleCount() {
            const vue = this;
            $.ajax({
                url: vue.communityRankUrl + "/queryCommunityArticleCount",
                type: "get",
                success(response) {
                    if (response.code == '200') {
                        vue.communityList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询社区点赞数排行
        queryCommunityLikeCount() {
            const vue = this;
            $.ajax({
                url: vue.communityRankUrl + "/queryCommunityLikeCount",
                type: "get",
                success(response) {
                    if (response.code == '200') {
                        vue.communityList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询社区评论数排行
        queryCommunityCommentCount() {
            const vue = this;
            $.ajax({
                url: vue.communityRankUrl + "/queryCommunityCommentCount",
                type: "get",
                success(response) {
                    if (response.code == '200') {
                        vue.communityList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询社区评分数数排行
        queryCommunityScoreCount() {
            const vue = this;
            $.ajax({
                url: vue.communityRankUrl + "/queryCommunityScoreCount",
                type: "get",
                success(response) {
                    if (response.code == '200') {
                        vue.communityList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询社区收藏数排行
        queryCommunityCollectCount() {
            const vue = this;
            $.ajax({
                url: vue.communityRankUrl + "/queryCommunityCollectCount",
                type: "get",
                success(response) {
                    if (response.code == '200') {
                        vue.communityList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
    },
};
</script>

<style scoped>
.MonkeyWebCommunityRankViews-container {
    width: 900px;
    margin: 0 auto;
    padding: 20px;
    background-color: #fff;
}
</style>