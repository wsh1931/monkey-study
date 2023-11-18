<template>
    <div class="MonkeyWebSearchResource-container">
        <el-tabs v-model="activeName" @tab-click="handleClick" >
            <el-tab-pane label="综合" name="comprehensive"></el-tab-pane>
            <el-tab-pane label="最新" name="latest"></el-tab-pane>
            <el-tab-pane label="最热" name="hire"></el-tab-pane>
            <el-tab-pane label="下载" name="down"></el-tab-pane>
            <el-tab-pane label="购买" name="buy"></el-tab-pane>
            <el-tab-pane label="游览" name="view"></el-tab-pane>
            <el-tab-pane label="收藏" name="collect"></el-tab-pane>
            <el-tab-pane label="评论" name="comment"></el-tab-pane>
            <el-tab-pane label="评分" name="score"></el-tab-pane>
            <el-tab-pane label="点赞" name="like"></el-tab-pane>
            <div
            class="infinite-list" 
            v-infinite-scroll="loadData" 
            infinite-scroll-distance="30">
                <div 
                @click="toResourceComment(resource.id)"
                v-for="resource in resourceList" :key="resource.id" 
                style="cursor: pointer;">
                    <el-row style="margin-bottom: 5px;">
                        <el-col :span="1">
                            <img @click="toUserViews(resource.userId)" class="user-headImg" :src="resource.userHeadImg" alt="">
                        </el-col>
                        <el-col :span="23" class="card-right">
                            <img class="resource-picture" :src="resource.typeUrl" alt="">
                            <div class="font-class">
                                <span @click="toUserViews(resource.userId)" class="username">{{ resource.username }}</span>
                                <span class="publishTime">{{ getTimeFormat(resource.createTime) }}</span>
                                <span v-if="resource.formTypeName ==  formType.getMsg(1)" class="formType">{{ resource.formTypeName }}</span>
                                <span v-else-if="resource.formTypeName ==  formType.getMsg(3)" class="formType-fee">{{ resource.formTypeName }}</span>
                                <span v-else-if="resource.formTypeName ==  formType.getMsg(2)" class="formType-fee">{{ resource.formTypeName }}</span>
                                <span v-else class="formType">{{ resource.formTypeName }}</span>
                            </div>
                            <div class="brief">{{ resource.userBrief }}</div>
                        </el-col>
                    </el-row>
                    <div style="margin-bottom: 5px;">
                        <el-tag 
                        type="info" 
                        size="small" 
                        style="margin-right: 10px;"
                        v-for="label in resource.resourceLabelName" :key="label"
                        v-html="label"></el-tag>
                        <el-tag 
                        type="info" 
                        size="small" 
                        style="margin-right: 10px;"
                        v-for="label in resource.resourceClassificationName" :key="label"
                        v-html="label"></el-tag>
                    </div>
                    <div class="resource-title" v-html="resource.name">
                    </div>

                    <div class="resource-content" v-html="resource.description">
                    </div>

                    <div style="vertical-align: middle;">
                        <span class="iconfont icon-yanjing operate-common">&nbsp;{{ getFormatNumber(resource.viewCount) }}</span>
                        <span class="el-icon-download operate-common">&nbsp;{{ getFormatNumber(resource.downCount) }}</span>
                        <span class="operate-common">￥&nbsp;{{ getFormatNumber(resource.buyCount) }}</span>
                        <span class="iconfont icon-dianzan operate-common">&nbsp;{{ getFormatNumber(resource.likeCount) }}</span>
                        <span class="iconfont icon-shoucang operate-common">&nbsp;{{ getFormatNumber(resource.collectCount) }}</span>
                        <span class="iconfont icon-pinglun operate-common">&nbsp;{{ getFormatNumber(resource.commentCount) }}</span>
                        <span class="iconfont icon-shoucang operate-common">&nbsp;{{ resource.score }}</span>
                        

                    </div>
                    <div class="divisor"></div>
                </div>
            </div>

        <div
        v-if="resourceList == null || resourceList == '' || resourceList == [] || resourceList.length <= 0"
        style="text-align: center;" >
            <el-empty description="暂无数据"></el-empty>
        </div>
        </el-tabs>
    </div>
</template>

<script>
import $ from 'jquery'
import { getTimeFormat } from '@/assets/js/DateMethod';
import { getFormatNumber } from '@/assets/js/NumberMethod';
export default {
    name: 'MonkeyWebSearchResource',

    data() {
        return {
            isScroll: true,
            activeName: "comprehensive",
            currentPage: 1,
            pageSize: 10,
            // 关键词搜索字段
            keyword: this.$route.query.keyword,
            // 资源集合
            resourceList: [],
            searchResourceUrl: "http://localhost:80/monkey-search/resource",
        };
    },

    watch: {
        '$route.query.keyword'(newVal, oldVal) {
            this.keyword = newVal;
            this.currentPage = 1;
            this.resourceList = [];
            if (this.activeName == 'comprehensive') {
                this.queryComprehensiveResource();
            } else if (this.activeName == "latest") {
                this.queryLatestResource();
            } else if (this.activeName == "hire") {
                this.queryHireResource();
            } else if (this.activeName == "down") {
                this.queryDownResource();
            } else if (this.activeName == "buy") {
                this.queryBuyResource();
            } else if (this.activeName == "view") {
                this.queryViewResource();
            } else if (this.activeName == "collect") {
                this.queryCollectResource();
            } else if (this.activeName == "comment") {
                this.queryCommentResource();
            } else if (this.activeName == 'study') {
                this.queryStudyResource();
            } else if (this.activeName == 'score') {
                this.queryScoreResource();
            } else if (this.activeName == 'like') {
                this.queryLikeResource();
            }
        }
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
        // 跳转至资源详情界面
        toResourceComment(resourceId) {
             // 资源游览数 + 1
            const { href } = this.$router.resolve({
                name: "resource_detail",
                params: {
                    resourceId
                }
            })

            window.open(href, '_black');
        },
        handleClick() {
            this.isScroll = false;
            this.currentPage = 1;
            this.resourceList = [];
            if (this.activeName == 'comprehensive') {
                this.queryComprehensiveResource();
            } else if (this.activeName == "latest") {
                this.queryLatestResource();
            } else if (this.activeName == "hire") {
                this.queryHireResource();
            } else if (this.activeName == "down") {
                this.queryDownResource();
            } else if (this.activeName == "buy") {
                this.queryBuyResource();
            } else if (this.activeName == "view") {
                this.queryViewResource();
            } else if (this.activeName == "collect") {
                this.queryCollectResource();
            } else if (this.activeName == "comment") {
                this.queryCommentResource();
            } else if (this.activeName == 'study') {
                this.queryStudyResource();
            } else if (this.activeName == 'score') {
                this.queryScoreResource();
            } else if (this.activeName == 'like') {
                this.queryLikeResource();
            }
        },
        getFormatNumber(val) {
            return getFormatNumber(val);
        },
        getTimeFormat(val) {
            return getTimeFormat(val);
        },
        loadData() {
            if (this.isScroll) {
                this.isScroll = false;
                if (this.activeName == 'comprehensive') {
                    this.queryComprehensiveResource();
                } else if (this.activeName == "latest") {
                    this.queryLatestResource();
                } else if (this.activeName == "hire") {
                    this.queryHireResource();
                } else if (this.activeName == "down") {
                    this.queryDownResource();
                } else if (this.activeName == "buy") {
                    this.queryBuyResource();
                } else if (this.activeName == "view") {
                    this.queryViewResource();
                } else if (this.activeName == "collect") {
                    this.queryCollectResource();
                } else if (this.activeName == "comment") {
                    this.queryCommentResource();
                } else if (this.activeName == 'study') {
                    this.queryStudyResource();
                } else if (this.activeName == 'score') {
                    this.queryScoreResource();
                } else if (this.activeName == 'like') {
                    this.queryLikeResource();
                }
            }
        },
        // 查询资源评分最高的资源列表
        queryScoreResource() {
            const vue = this;
            $.ajax({
                url: vue.searchResourceUrl + "/queryScoreResource",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data;
                        for (let i = 0; i < data.length; i++) {
                            vue.resourceList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询点赞人数最多资源列表
        queryLikeResource() {
            const vue = this;
            $.ajax({
                url: vue.searchResourceUrl + "/queryLikeResource",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data;
                        for (let i = 0; i < data.length; i++) {
                            vue.resourceList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询购买人数最多资源列表
        queryBuyResource() {
            const vue = this;
            $.ajax({
                url: vue.searchResourceUrl + "/queryBuyResource",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data;
                        for (let i = 0; i < data.length; i++) {
                            vue.resourceList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询下载人数最多资源列表
        queryDownResource() {
            const vue = this;
            $.ajax({
                url: vue.searchResourceUrl + "/queryDownResource",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data;
                        for (let i = 0; i < data.length; i++) {
                            vue.resourceList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询评论最多资源列表
        queryCommentResource() {
            const vue = this;
            $.ajax({
                url: vue.searchResourceUrl + "/queryCommentResource",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data;
                        for (let i = 0; i < data.length; i++) {
                            vue.resourceList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询收藏数最多资源列表
        queryCollectResource() {
            const vue = this;
            $.ajax({
                url: vue.searchResourceUrl + "/queryCollectResource",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data;
                        for (let i = 0; i < data.length; i++) {
                            vue.resourceList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询点赞数最多资源列表
        queryStudyResource() {
            const vue = this;
            $.ajax({
                url: vue.searchResourceUrl + "/queryStudyResource",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data;
                        for (let i = 0; i < data.length; i++) {
                            vue.resourceList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询游览数最多资源列表
        queryViewResource() {
            const vue = this;
            $.ajax({
                url: vue.searchResourceUrl + "/queryViewResource",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data;
                        for (let i = 0; i < data.length; i++) {
                            vue.resourceList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询最热资源列表
        queryHireResource() {
            const vue = this;
            $.ajax({
                url: vue.searchResourceUrl + "/queryHireResource",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data;
                        for (let i = 0; i < data.length; i++) {
                            vue.resourceList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询最新资源列表
        queryLatestResource() {
            const vue = this;
            $.ajax({
                url: vue.searchResourceUrl + "/queryLatestResource",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data;
                        for (let i = 0; i < data.length; i++) {
                            vue.resourceList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询综合资源列表
        queryComprehensiveResource() {
            const vue = this;
            $.ajax({
                url: vue.searchResourceUrl + "/queryComprehensiveResource",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data;
                        for (let i = 0; i < data.length; i++) {
                            vue.resourceList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
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
.formType-fee {
    background-image: linear-gradient(to right, #f9d423 0%, #ff4e50 100%);
    font-size: 12px;
    text-align: center;
    color: white;
    padding: 2px 5px;
}
.formType {
    background-image: linear-gradient(to right, #92fe9d 0%, #00c9ff 100%);
    font-size: 12px;
    text-align: center;
    color: white;
    padding: 2px 5px;
}
.divisor {
    background-color: rgba(0, 0, 0, 0.1);
    height: 1px;
    width: 100%;
    margin: 10px 0;
}
@keyframes slide-out {
    0% {
        opacity: 0;
    }
    100% {
        opacity: 1;
    }
}
::-webkit-scrollbar {
    width: 10px;
    background-color: #fff;
}

:hover ::-webkit-scrollbar-track-piece {
    background-color: #fff;
    border-radius: 6px;
}

:hover::-webkit-scrollbar-thumb:hover {
    background-color: rgba(0, 0, 0, 0.1);
}

:hover::-webkit-scrollbar-thumb:vertical {
    background-color: rgba(0, 0, 0, 0.1);
    border-radius: 6px;
    outline: 2px solid #fff;
    outline-offset: -2px;
    border: 2px solid #fff;
    
    transition: 0.4s linear all;
}
.operate-common {
    margin-right: 20px;
    font-size: 16px;
    color: gray;
}
.font-class {
    margin-bottom: 2px;
}
.resource-content {
    display: inline-block;
    font-size: 14px;
    color: gray;
    margin-bottom: 10px;
    overflow: hidden;
    max-width: 600px;
    text-overflow: ellipsis;
    display: -webkit-box;
    /* 设置省略行 */
    -webkit-line-clamp: 2; 
    -webkit-box-orient: vertical;  
    transition: 0.2s linear all;
}
.resource-content:hover {
    opacity: 0.8;
}
.resource-title {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 10px;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    max-width: 600px;
    transition: 0.2s linear all;
}
.resource-title:hover {
    opacity: 0.8;
}
.resource-picture {
    position: absolute;
    right: 10px;
    top: 0;
    height: 100px;
    width: 150px;
    border-radius: 0;
    transition: 0.2s linear all;
}

.resource-picture:hover {
    opacity: 0.8;
}
.card-right {
    padding-left: 15px;
    position: relative;
}
.brief {
    font-size: 14px;
    color: gray;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    max-width: 543px;
}
.publishTime {
    font-size: 14px;
    color: gray;
    margin-right: 10px;
}
.username {
    display: inline-block;
    margin-right: 10px;
    font-size: 14px;
    transition: 0.2s linear all;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    max-width: 450px;
}
.username:hover {
    color: #409EFF;
}
.MonkeyWebSearchResource-container {
    vertical-align: middle;
    animation: slide-out 0.4s linear;
}
.user-headImg {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    transition: 0.2s linear all;
}
.user-headImg:hover {
    opacity: 0.8;
    cursor: pointer;
}
::v-deep .el-tabs__header {
    margin: 0 0 10px 0;
}
.infinite-list {
    overflow:auto; 
    max-height: calc(100vh - 150px);
    border-radius: 10px;
}
::v-deep .el-tabs__nav-wrap::after {
    content: none;
    position: absolute;
    left: 0;
    width: 100%;
    height: 0;
    background-color: #fff;
    z-index: 1;
}
</style>