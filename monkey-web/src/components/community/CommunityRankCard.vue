<template>
    <div class="MonkeyWebCommunityRankCard-container">
        <el-row v-for="(community, index) in communityList" :key="community.id">
            <!-- 卡片顶部 -->
            <el-row class="top">
                <img @click="toUserView(community.userId)" class="community-card-img" :src="community.headImg" alt="">
                <span @click="toUserView(community.userId)" class="username">{{ community.username }}</span>
                <span class="time-top">&nbsp;&nbsp;|&nbsp;&nbsp;{{ community.createTime }} </span>
                <span class="rank">{{ index + 1 }}</span>
            </el-row>

            <!-- 卡片内容 -->
            <el-row style="margin-top: 10px;">
                <el-col :span="5" style="overflow: hidden;">
                    <img @click="toCommunityDetailView(community.id)" class="content-card-img" :src="community.photo" alt="">
                </el-col>
                <el-col :span="19" style="padding-left: 10px;">
                    <el-row class="community-title"> 
                        <div @click="toCommunityDetailView(community.id)">
                            {{ community.name }}
                        </div>
                    </el-row>
                    <el-row class="community-content">
                        {{ community.description }}
                    </el-row>

                    <el-row>
                        <span class="el-icon-view view">&nbsp;游览&nbsp;{{ community.viewCount }}</span>
                        <span class="el-icon-user view">&nbsp;成员&nbsp;{{ community.memberCount }}</span>
                        <span class="el-icon-notebook-1 view">&nbsp;文章&nbsp;{{ community.articleCount }}</span>
                        <span class="iconfont icon-dianzan view">&nbsp;点赞&nbsp;{{ community.likeCount }}</span>
                        <span class="iconfont icon-pinglun view">&nbsp;评论&nbsp;{{ community.commentCount }}</span>
                        <span class="iconfont icon-score view">&nbsp;评分数&nbsp;{{ community.scoreCount }}</span>
                        <span class="iconfont icon-shoucang view">&nbsp;收藏&nbsp;{{ community.collectCount }}</span>
                    </el-row>
                </el-col>
            </el-row>

            <el-row class="divider"></el-row>
        </el-row>
    </div>
</template>

<script>
export default {
    name: 'MonkeyWebCommunityRankCard',
    props: ['communityList'],
    data() {
        return {
            
        };
    },

    methods: {
        // 前往社区详情界面
        toCommunityDetailView(communityId) {
            const { href } = this.$router.resolve({
                name: "community_detail",
                params: {
                    communityId,
                }
            }) 

            window.open(href, "_blank");
        },
        // 前往用户主页
        toUserView(userId) {
            const { href } = this.$router.resolve({
                name: "user_home",
                params: {
                    userId,
                }
            }) 

            window.open(href, "_blank");
        }
    },
};
</script>

<style scoped>
.rank {
    float: right;
    color: white;
    padding: 0 10px;
    font-size: 24px;
    font-weight: bolder;
    border-radius: 50%;
    background-color: #F56C6C;
}

@keyframes slide-up {
    0% {
        opacity: 0;
    }
    100% {
        opacity: 1;
    }
}
.MonkeyWebCommunityRankCard-container {
    animation: slide-up 0.2s linear;
}
.divider {
    margin: 20px;
    background-color: gray;
    height: 0.5px;
    opacity: 0.5;
}

.view {
    font-size: 14px;
    margin-right: 32px;
}
.community-content {
    min-height: 60px;
    line-height: 24px;
    font-size: 15px;
    font-weight: 400;
    margin-bottom: 14px;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    overflow: hidden;
    padding-top: 10px;
}
.community-title {
    cursor: pointer;
    font-size: 16px;
    font-weight: 700;
    color: #222226;
    line-height: 24px;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
    -webkit-margin-before: 0;
    margin-block-start: 0;
    -webkit-margin-after: 0;
    margin-block-end: 0;
}
.community-title:hover {
    opacity: 0.5;
}
.content-card-img {
    width: 100%;
    cursor: pointer;
    height: 120px;
    transition: 0.4s linear all;
}
.content-card-img:hover {
    transform: scale(1.4);
}
.time-top {
    display: inline-block;
    font-size: 14px;
    height: 32px;
    line-height: 32px;
    font-weight: 500;
    color: #555666;
    vertical-align: middle;
}
.top {
    height: 34px;
    line-height: 34px;
    overflow: hidden;
    position: relative;
}
.username {
    display: inline-block;
    font-size: 14px;
    height: 32px;
    line-height: 32px;
    font-weight: 500;
    color: #555666;
    margin-left: 8px;
    max-width: 600px;
    overflow: hidden;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
    text-overflow: ellipsis;
    white-space: nowrap;
    vertical-align: middle;
    cursor: pointer;
}
.username:hover {
    opacity: 0.5;
}
.community-card-img {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    vertical-align: top;
    border: 1px solid #f2f2f2;
    cursor: pointer;
}
.community-card-img:hover {
    opacity: 0.5;
    cursor: pointer;
}
</style>