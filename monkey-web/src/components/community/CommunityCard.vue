<template>
    <div class="MonkeyWebCommunityCard-container">
        <el-row v-for="article in communityArticleList" :key="article.id">
            <!-- 卡片顶部 -->
            <el-row class="top">
                <img @click="toUserViews($store.state.user.id)" class="community-card-img" :src="article.userHeadImg" alt="">
                <span @click="toUserViews($store.state.user.id)" class="username">{{ article.username }}</span>
                <span @click="toCommunityViews()" class="community-top">|&nbsp;&nbsp;&nbsp;来自: {{ article.communityName }}</span>
                <span class="time-top" style="text-align: right;">{{ article.createTime }}</span>
            </el-row>

            <!-- 卡片内容 -->
            <el-row style="margin-top: 10px;">
                <el-col :span="5" style="overflow: hidden;">
                    <img class="content-card-img" :src="article.userHeadImg" alt="">
                </el-col>
                <el-col :span="19" style="padding-left: 10px;">
                    <el-row class="article-title">{{ article.title }}</el-row>
                    <el-row class="article-content">
                        {{ article.brief }}
                    </el-row>

                    <el-row>
                        <span class="el-icon-view view">&nbsp;游览&nbsp;{{ article.viewCount }}</span>
                        <span class="el-icon-view view">&nbsp;点赞&nbsp;{{ article.likeCount }}</span>
                        <span class="iconfont icon-shoucang view">&nbsp;收藏&nbsp;{{ article.collectCount }}</span>
                        <span class="iconfont icon-pinglun view">&nbsp;回复&nbsp;{{ article.replyCount }}</span>
                        <span class="iconfont icon-zhuanfa view">&nbsp;分享</span>
                        <span class="channel-content">{{ article.channelName }}</span>
                        <span class="el-icon-menu channel">&nbsp;频道&nbsp;&nbsp;</span>
                        
                    </el-row>
                </el-col>
            </el-row>

            <el-row class="divider"></el-row>
        </el-row>
    </div>
</template>

<script>
export default {
    name: 'MonkeyWebCommunityCard',
    props: ['communityArticleList'],
    data() {
        return {
            
        };
    },

    mounted() {
        
    },

    methods: {
        // 前往用户主页
        toUserViews(userId) {
            const { href } = this.$router.resolve({
                name: "user_home",
                params: {
                    userId
                }
            })

            window.open(href, "_blank")
        },
        // 前往社区主页
        toCommunityViews() {
            
        }
    },
};
</script>

<style scoped>
@keyframes slide-up {
    0% {
        opacity: 0;
    }
    100% {
        opacity: 1;
    }
}
.MonkeyWebCommunityCard-container {
    animation: slide-up 0.2s linear;
}
.divider {
    margin: 20px;
    background-color: gray;
    height: 0.5px;
    opacity: 0.5;
}
.channel-content:hover {
    opacity: 0.5;
}
.channel-content {
    float: right;
    padding: 2px 10px;
    background-color: rgb(75, 180, 199);
    color: #fff;
    max-width: 100px;
    overflow: hidden;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
    text-overflow: ellipsis;
    white-space: nowrap;
    vertical-align: middle;
    cursor: pointer;
}
.channel {
    float: right;
    font-size: 14px;
    line-height: 24.8px;
}
.view {
    font-size: 14px;
    margin-right: 32px;
}
.article-content {
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
.article-title {
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
.article-title:hover {
    opacity: 0.5;
}
.content-card-img {
    width: 100%;
    cursor: pointer;
    transition: 0.4s linear all;
}
.content-card-img:hover {
    transform: scale(1.4);
}
.time-top {
    float: right;
    font-size: 14px;
    height: 32px;
    line-height: 32px;
    font-weight: 500;
}
.community-top {
    display: inline-block;
    font-size: 14px;
    height: 32px;
    line-height: 32px;
    font-weight: 500;
    color: #555666;
    margin-left: 8px;
    max-width: 300px;
    overflow: hidden;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
    text-overflow: ellipsis;
    white-space: nowrap;
    vertical-align: middle;
}
.community-top:hover {
    cursor: pointer;
    opacity: 0.5;
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
    max-width: 160px;
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
    
}

</style>