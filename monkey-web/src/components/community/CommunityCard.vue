<template>
    <div class="MonkeyWebCommunityCard-container">
        <!-- 卡片顶部 -->
        <el-row class="top">
            <img class="community-card-img" src="https://ts3.cn.mm.bing.net/th?id=OIP-C.bVb769JBdzVZYuksxZ2Y-AHaEo&w=316&h=197&c=8&rs=1&qlt=90&o=6&dpr=1.3&pid=3.1&rm=2" alt="">
            <span class="username">用户名用户名用户名用户名用户名用户名用户名用户名用户名用户名</span>
            <span class="community-top">|&nbsp;&nbsp;&nbsp;来自: 社区社区社区社区社区社区社区社区社区社区社区社区社区社区社区社区社区社区</span>
            <span class="time-top" style="text-align: right;">2023-05-23 16:03:35</span>
        </el-row>

        <!-- 卡片内容 -->
        <el-row style="margin-top: 10px;">
            <el-col :span="5" style="overflow: hidden;">
                <img class="content-card-img" src="https://ts2.cn.mm.bing.net/th?id=OIP-C.1zMo-77F0KNEbklTzOledQHaEo&w=316&h=197&c=8&rs=1&qlt=90&o=6&dpr=1.3&pid=3.1&rm=2" alt="">
            </el-col>
            <el-col :span="19" style="padding-left: 10px;">
                <el-row class="article-title">文章标题文章标题文章标题文章标题文文章标题文章标题文章标题文章标题文章标题文章标题文章标题文章标题文章标题章标题文章标题文章标题文章标题文章标题</el-row>
                <el-row class="article-content">
                    文章内容文章内容文章内容文章内容文章内容文章内容文章内容文章内容文章内容文章内容文章内容文章内容文章内容文章内容文章内容文章内容文章内容文章内容文章内容文章内容文章内容文章内容文章内容文章内容文章内容文章内容文章内容文章内容文章内容文章内容文章内容文章内容文章内容文章内容文章内容文章内容文章内容文章内容文章内容
                </el-row>

                <el-row>
                    <span class="el-icon-view view">&nbsp;游览&nbsp;2</span>
                    <span class="iconfont icon-pingfen view">&nbsp;评分&nbsp;2</span>
                    <span class="iconfont icon-pinglun view">&nbsp;回复&nbsp;2</span>
                    <span class="iconfont icon-zhuanfa view">&nbsp;分享&nbsp;2</span>
                    <span class="channel-content">标标标标标标标标标标标标标标标标</span>
                    <span class="el-icon-menu channel">&nbsp;频道&nbsp;&nbsp;</span>
                    
                </el-row>
            </el-col>
        </el-row>

        <el-row class="divider"></el-row>
    </div>
</template>

<script>
export default {
    name: 'MonkeyWebCommunityCard',

    data() {
        return {
            
        };
    },

    mounted() {
        
    },

    methods: {
        
    },
};
</script>

<style scoped>
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
.community-card-img {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    vertical-align: top;
    border: 1px solid #f2f2f2;
    cursor: pointer;
}

</style>