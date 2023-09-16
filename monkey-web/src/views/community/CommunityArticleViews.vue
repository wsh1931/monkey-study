<template>
    <div class="MonkeyWebCommunityArticleViews-container">
        <el-row>
            <el-col :span="18">
                <div class="article-content">
                    <div class="article-title">文章标题</div>
                    <div style="margin: 10px 0 0 0; padding: 0 20px;">
                        <span>
                            <img class="article-picture" src="https://ts3.cn.mm.bing.net/th?id=OIP-C.a_2XJOKnYxsnGU-tYazCWwHaF7&w=279&h=223&c=8&rs=1&qlt=90&o=6&pid=3.1&rm=2" alt="">
                            <span class="username-author" style="max-width: 600px;">啥也不会hh啥也不会hh啥也不会hh啥也不会hh啥也不会hh啥也不会hh啥也不会hh啥也不会hh</span>
                            <span class="username-author">|&nbsp;&nbsp;&nbsp;2023-09-13 16:18:28</span>
                        </span>
                    </div>

                    <div style="padding: 0 20px;">
                        <VueMarkdown 
                        class="markdown-content"
                        :source="content" 
                        :highlight="true"
                        :html="true"
                        :xhtmlOut="true">
                        </VueMarkdown>
                    </div>

                    <!-- 文章评分 -->
                <div class="header-score">
                    <span class="score-description">给本帖评分</span>
                    <el-rate
                        class="score"
                        v-model="articleScore"
                        :texts=texts
                        show-text
                        :colors="colors"
                        text-color="#FC5531">
                    </el-rate>
                </div>
                </div>

                

                <!-- 文章投票 -->
                    <div class="header">
                            <div>
                                <span class="veto-status">[审核中]</span>
                                <span class="veto-name">啊啊啊啊啊啊啊啊啊啊啊啊</span>
                                <span class="select-method">单选</span>
                            </div>
                            <div class="select-content">
                                <span>选项内容1</span>
                                <span style="float: right;">0</span>
                            </div>
                            <div class="select-content">
                                <span>选项内容2</span>
                                <span style="float: right;">0</span>
                            </div>
                            <el-button size="" class="veto-button">确定投票</el-button>

                            <div class="discuss-people">
                                <span style="margin-right: 10px;">0 人已参与讨论</span>
                                <span>于2023-09-14 结束</span>
                            </div>
                    </div>

                    <!-- 文章任务 -->
                    <div class="article-task">
                        <div class="article-task-title">
                            <span class="task-submit-people">1&nbsp;人已提交</span>
                            <span style="float: right;">
                                <span class="task-end-time">任务截止：2023-09-30 23:59:59</span>
                                <span class="task-end-time">完成率100%</span>
                                <span class="no-submit-people">
                                    <el-dropdown click="channel-content" size="small">
                                    <el-button type="text" size="mini">
                                        未提交&nbsp;1人&nbsp;
                                    </el-button>
                                    <el-dropdown-menu slot="dropdown">
                                    <el-dropdown-item>黄金糕</el-dropdown-item>
                                    <el-dropdown-item>狮子头</el-dropdown-item>
                                    <el-dropdown-item>螺蛳粉</el-dropdown-item>
                                    <el-dropdown-item disabled>双皮奶</el-dropdown-item>
                                    <el-dropdown-item divided>蚵仔煎</el-dropdown-item>
                                    </el-dropdown-menu>
                                    </el-dropdown>
                                </span>
                                <el-button class="button-task" size="mini" type="primary">提交任务</el-button>
                                <el-button type="success" class="button-task el-icon-download" size="mini">导出</el-button>
                            </span>
                        </div>

                        <!-- 任务内容信息 -->
                        <div>
                             <el-table
                                :data="tableData"
                                style="width: 100%;">
                                <el-table-column
                                align="left"
                                label="用户信息"
                                width="180">
                                <template slot-scope="scope">
                                    <img class="user-headImg" src="https://ts4.cn.mm.bing.net/th?id=OIP-C.Fx6P4aoVRKGGircQ7tgtggHaEo&w=316&h=197&c=8&rs=1&qlt=90&o=6&pid=3.1&rm=2" alt="">
                                    <span class="username">啥也不会hh啥也不会hh啥也不会hh啥也不会hh啥也不会hh</span>
                                </template>
                                </el-table-column>

                                <el-table-column
                                align="left"
                                label="提交内容"
                                width="180">
                                <template slot-scope="scope">
                                    <span class="submit-content">提交内容提交内容</span>
                                </template>
                                </el-table-column>

                                <el-table-column
                                align="left"
                                label="首次提交"
                                width="190">
                                    <template slot-scope="scope">
                                        <i class="el-icon-time"></i>
                                        <span style="margin-left: 10px">2023-09-27 23:59:59</span>
                                    </template>
                                </el-table-column>

                                <el-table-column
                                align="left"
                                label="最近提交"
                                width="190">
                                    <template slot-scope="scope">
                                        <i class="el-icon-time"></i>
                                        <span style="margin-left: 10px">2023-09-27 23:59:59</span>
                                    </template>
                                </el-table-column>
                                
                                <el-table-column 
                                label="操作"
                                align="center">
                                <template slot-scope="scope">
                                    <el-button
                                    size="mini"
                                    type="text"
                                    @click="handleEdit(scope.$index, scope.row)">查看提交内容</el-button>
                                    <el-button
                                    size="mini"
                                    type="text"
                                    style="color: orange;"
                                    @click="handleEdit(scope.$index, scope.row)">历史提交记录</el-button>
                                </template>
                                </el-table-column>
                            </el-table>
                        </div>
                    </div>



                    <div class="function">
                        <div class="position">
                            <span style="font-size: 14px;">
                                <i class="el-icon-view" style="vertical-align: middle;"></i>
                                游览&nbsp;7
                            </span>
                            <span class="iconfont icon-dianzan">&nbsp;点赞</span>
                            <span class="iconfont icon-pinglun">&nbsp;回复</span>
                            <span class="iconfont icon-shoucang">&nbsp;收藏</span>
                            <span class="iconfont icon-zhuanfa">&nbsp;转发</span>
                            <span 
                            @mouseover="isHoverMore = true"
                            @mouseleave="isHoverMore = false"
                            class="el-icon-more view more" >
                                <div class="more-background" v-if="isHoverMore">
                                    <div class="more-background-content" @click="deleteArticle(article.id, index)">移除</div>
                                    <div class="more-background-content" @click="setExcellentArticle(article.id, index)">精选</div>
                                    <div class="more-background-content" @click="setExcellentArticle(article.id, index)">取消精选</div>
                                    <div class="more-background-content" @click="setTopArticle(article.id, index)">置顶</div>
                                    <div class="more-background-content" @click="setTopArticle(article.id, index)">取消置顶</div>
                                </div>
                            </span>
                            <span class="channel">
                                <i class="el-icon-menu"></i>
                                <span>频道&nbsp;</span>

                                <el-dropdown click="channel-content" size="small">
                                <el-button type="primary" size="mini" class="channel-button">
                                    学习打卡
                                </el-button>
                                <el-dropdown-menu slot="dropdown">
                                <el-dropdown-item>黄金糕</el-dropdown-item>
                                <el-dropdown-item>狮子头</el-dropdown-item>
                                <el-dropdown-item>螺蛳粉</el-dropdown-item>
                                <el-dropdown-item disabled>双皮奶</el-dropdown-item>
                                <el-dropdown-item divided>蚵仔煎</el-dropdown-item>
                                </el-dropdown-menu>
                                </el-dropdown>
                            </span>

                            <el-button type="primary" size="mini" class="write-reply el-icon-edit" >写回复</el-button>
                        </div>
                    </div>

                    <div style="margin-top: 10px;">
                        <CommunityArticleComment
                        style="z-index: 10;"/>
                    </div>
            </el-col>
            <el-col :span="6" style="padding: 0 10px;">
                <CommunityInfo
                :isShowScore="isShowScore"/>
            </el-col>
        </el-row>
        
    </div>
</template>

<script>
import CommunityArticleComment from '@/components/community/CommunityArticleComment.vue';
import VueMarkdown from 'vue-markdown';
import CommunityInfo from '@/components/community/CommunityInfo.vue';
export default {
    name: 'MonkeyWebCommunityArticleViews',
    components: {
        CommunityInfo,
        VueMarkdown,
        CommunityArticleComment
    },
    data() {
        
        return {
            // 是否展示内容评分
            isShowScore: true,
            // 是否悬浮在了隐藏更多上
            isHoverMore: false,
            content: "## 吴思豪",
            articleScore: '0',
            // 评分描述内容
            texts: ['锋芒小试，眼前一亮', '潜力巨大，未来可期', '持续贡献，值得关注', '成绩优异，大力学习', '贡献巨大，全力支持'],
             tableData: [{
                date: '2016-05-02',
                name: '王小虎',
                address: '上海市普陀区金沙江路 1518 弄'
                }, {
                date: '2016-05-04',
                name: '王小虎',
                address: '上海市普陀区金沙江路 1517 弄'
                }, {
                date: '2016-05-01',
                name: '王小虎',
                address: '上海市普陀区金沙江路 1519 弄'
                }, {
                date: '2016-05-03',
                name: '王小虎',
                address: '上海市普陀区金沙江路 1516 弄'
                }]
        };
    },

    mounted() {
        
    },

    methods: {
        handleEdit(index, row) {
            console.log(index, row);
        },
        handleDelete(index, row) {
            console.log(index, row);
        }
    },
};
</script>

<style scoped>
.submit-content {
    display: inline-block;
    max-width: 110px;
    overflow: hidden;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
    text-overflow: ellipsis;
    white-space: nowrap;
    vertical-align: middle;
}
.header-score {
    padding-left: 20px;
}
.evaluate-title {
    margin-bottom: 10px;
    font-weight: 600;
    font-size: 16px;
}
.article-content {
    background-color: #fff; 
    padding-bottom: 20px; 
    box-shadow: 0 2px 12px 2px rgba(0,0,0,0.1);
}
.header {
    background-color: #fff; 
    padding: 20px; 
    margin-top: 10px;
    box-shadow: 0 2px 12px 2px rgba(0,0,0,0.1);
}
.username {
    display: inline-block;
    margin-left: 10px;
    max-width: 110px;
    overflow: hidden;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
    text-overflow: ellipsis;
    white-space: nowrap;
    vertical-align: middle;
}
.username:hover {
    opacity: 0.5;
    cursor: pointer;
}
.user-headImg {
    height: 30px;
    width: 30px;
    vertical-align: middle;
    border-radius: 50%;
}
.user-headImg:hover {
    cursor: pointer;
    opacity: 0.5;
}
.no-submit-people {
    margin-right: 20px;
}
.task-end-time {
    font-size: 14px;
    opacity: 0.5;
    margin-right: 20px;
}
.task-submit-people {
    font-weight: 600;
    font-size: 16px;
}
.button-task {
    width: 0;
    height: 0;
    text-align: center;
    line-height: 26px;
    font-size: 14px;
    font-weight: 500;
    width: max-content;
    height: 28px;
    padding: 0 14px;
    border-radius: 16px;
}
.article-task {
    background-color: #fff;
    padding: 20px;
    margin-top: 10px;
    box-shadow: 0 2px 12px 2px rgba(0,0,0,0.1);
}
.write-reply {
    margin-left: 290px;
    border-radius: 20px;
    
}
.channel-button {
    text-overflow: ellipsis;
    padding: 0 8px;
    height: 20px;
    border-radius: 4px;
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    font-size: 12px;
    font-weight: 400;
    color: #fff;
    line-height: 20px;
    position: relative;
    -webkit-transition: all .2s;
    transition: all .2s;
}
.el-dropdown-link {
    cursor: pointer;
    color: #409EFF;
}
.el-icon-arrow-down {
    font-size: 12px;
}
.channel {
    margin-left: 20px;
    font-size: 14px;
}
.position {
    background-color: #F2F5F7;
    position: fixed;
    bottom: 0;
    padding: 10px 20px;
    z-index: 20000;
    box-shadow: 0 2px 12px 2px rgba(0,0,0,0.1);
}
.function {
    background-color: #fff;
    width: 100%;
    position: relative;
}
.more-background-content:nth-child(n + 2) {
    padding-top: 12px;
}
.more-background-content:hover {
    color: #409EFF;
    cursor: pointer;
}
.more-background-content {
    animation: slide-out 0.4s linear;
}
@keyframes slide-out {
    0% {
        opacity: 0;
    }
    100% {
        opacity: 1;
    }
}
.more-background {
    position: absolute;
    text-align: center;
    width: 60px;
    padding: 10px 16px;
    bottom: 16px;
    left: -40px;
    background-color: #fff;
    box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
    z-index: 1;
    cursor: auto;
}
.view {
    font-size: 14px;
    margin-left: 20px;
}
.more {
    position: relative;
}
.more:hover {
    cursor: pointer;
}
.icon-zhuanfa {
    margin-left: 20px;
    font-size: 14px;
}
.icon-zhuanfa:hover {
    color: #409EFF;
    cursor: pointer;
}
.icon-dianzan {
    margin-left: 20px;
    font-size: 14px;
}
.icon-dianzan:hover {
    color: #409EFF;
    cursor: pointer;
}
.icon-shoucang {
    margin-left: 20px;
    font-size: 14px;
}
.icon-shoucang:hover {
    color: #409EFF;
    cursor: pointer;
}
.icon-pinglun {
    margin-left: 20px;
    font-size: 14px;
}
.icon-pinglun:hover {
    color: #409EFF;
    cursor: pointer;
}
.score {
    margin-left: 10px;
    display: inline-block;
}
.score-description {
    color: #409EFF;
    font-size: 14px;
}
.veto-button {
    background-color: #F7F7FC;
    width: 100%;
    border: none;
}
.discuss-people {
    font-size: 14px; 
    opacity: 0.5; 
    margin: 10px 0;
}
.select-content {
    padding: 10px 20px;
    background-color: #F7F7FC;
    margin: 10px 0;
    font-size: 14px;
}
.select-content:hover {
    cursor: pointer;
    background-color: rgba(6, 127, 248, 0.1);
}
.select-method {
    font-size: 14px;
    opacity: 0.7;
    font-weight: 550;
}
.veto-name {
    font-size: 14px;
    margin-right: 10px;
    font-weight: 550;
}
.veto-status {
    font-weight: 550;
    font-size: 16px;
    margin-right: 10px;
}
.veto-card {
    background-color: #F7F7FC;
    padding: 20px;
}
.username-author {
    display: inline-block;
    vertical-align: middle;
    margin-left: 10px;
    font-size: 15px;
    opacity: 0.8;
    overflow: hidden;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
    text-overflow: ellipsis;
    white-space: nowrap;
    vertical-align: middle;
}
.username-author:hover {
    opacity: 0.5;
    cursor: pointer;
}
.article-picture {
    width: 40px;
    border-radius: 50%;
    vertical-align: middle;
}
.article-picture:hover {
    opacity: 0.5;
    cursor: pointer;
}
.article-title {
    font-weight: 600;
    font-size: 24px;
    padding: 20px 20px 0 20px;
}
.MonkeyWebCommunityArticleViews-container {
    margin: 10px auto;
    width: 1300px;
}

</style>