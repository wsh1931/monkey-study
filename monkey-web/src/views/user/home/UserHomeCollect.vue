<template>
    <div class="MonkeyWebUserHomeCollect-container">
        <div class="collect-card" v-for="collectContent in collectContentList" :key="collectContent.id">
            <div class="collect-name">{{ collectContent.name }}</div>
            <div class="collect-medium">
                <span class="collect-common">{{ collectContent.collectCount }} 内容</span>
                <span class="collect-common">{{ getTimeFormat("2023-07-12 17:08:28")}}</span>
                <span 
                class="collect-common collect-operate" 
                v-if="collectContent.isShow == '0'" 
                @click="queryCollectContentDetail(collectContent)">
                    <span>展开&nbsp;</span>
                    <i class="el-icon-arrow-down"></i>
                </span>
                <span 
                class="collect-common collect-operate" 
                v-if="collectContent.isShow == '1'" 
                @click="collectContent.isShow = '0'">
                    <span>收起&nbsp;</span>
                    <i class="el-icon-arrow-up"></i>
                </span>
            </div>
            <div class="display-content" v-if="collectContent.isShow == '1'">
                <div 
                @click="toCollectContentViews(collectContentDetail)"
                v-for="collectContentDetail in collectContentDetailList" 
                :key="collectContentDetail.id" 
                style="margin-bottom: 5px;">
                    <el-tag size="mini" style="margin-right: 5px;">{{ collectContentDetail.typename }}</el-tag>
                    <span class="collect-content-name">{{ collectContentDetail.title }}</span>
                </div>
            </div>
        </div>

        <PagiNation
        style="text-align: right;"
            :totals="totals"
            :currentPage="currentPage" 
            :pageSize="pageSize" 
            @handleCurrentChange = "handleCurrentChange"
            @handleSizeChange="handleSizeChange"/>
    </div>
</template>

<script>
import $ from 'jquery'
import store from '@/store';
import PagiNation from '@/components/pagination/PagiNation.vue';
import { getTimeFormat } from '@/assets/js/DateMethod';
export default {
    name: 'MonkeyWebUserHomeCollect',
    components: {
        PagiNation
    },
    data() {
        return {
            isFold: '0',
            currentPage: 1,
            pageSize: 10,
            totals: 0,
            userId: "",
            collectContentList: [],
            collectContentDetailList: [],
            userHomeCollectUrl: "http://localhost:80/monkey-user/home/collect",
            communityArticleUrl: "http://localhost:80/monkey-community/article",
            checkArticleUrl: "http://localhost:80/monkey-article/check",
            questionUrl: "http://localhost:80/monkey-question/question",
            // 课程详细信息地址
            courseDetailUrl: "http://localhost/monkey-course/detail",
            communityUrl: "http://localhost:80/monkey-community/community",
            resourceHomePageUrl: "http://localhost:80/monkey-resource/homePage",
        };
    },

    created() {
        this.userId = this.$route.params.userId;
        this.queryUserCollectContent(this.userId);
    },

    methods: {
        // 文章游览数 + 1
        articleViewCountAddOne(articleId) {
            const vue = this;
            $.ajax({
                url: vue.checkArticleUrl + "/addArticleVisit",
                type: "post",
                data: {
                    articleId,
                },
                success(response) {
                    if (response.code != vue.ResultStatus.SUCCESS) {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
        },
        resourceViewCountAddOne(resourceId) {
            const vue = this;
            $.ajax({
                url: vue.resourceHomePageUrl + "/resourceViewCountAddOne",
                type: "put",
                data: {
                    resourceId
                },
                success(response) {
                    if (response.code != vue.ResultStatus.SUCCESS) {
                        vue.$modal.msgError(response.msg);
                    } 
                }
            })
        },
        // 社区文章游览数 + 1
        communityArticleViewCountAddOne(communityArticleId) {
            const vue = this;
            $.ajax({
                url: vue.communityUrl + "/articleViewCount/addOne",
                type: "put",
                data: {
                    communityArticleId,
                },
                success(response) {
                    if (response.code != vue.ResultStatus.SUCCESS) {
                        vue.$modal.msgError(response.msg);
                    }
                },
                error(response) {
                    vue.$modal.msgError(response.msg);
                }
            })
        },
        // 课程游览数 + 1
        courseViewAdd(courseId) {
            const vue = this;
            $.ajax({
                url: vue.courseDetailUrl + "/courseViewAdd",
                type: "put",
                data: {
                    courseId
                },
                success(response) {
                    if (response.code != vue.ResultStatus.SUCCESS) {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        questionViewCountAddOne(questionId) {
            const vue = this;
            $.ajax({
                url: vue.questionUrl + "/questionViewCountAddOne",
                type: "get",
                data: {
                    questionId
                },
                success(response) {
                    if (response.code != vue.ResultStatus.SUCCESS) {
                        vue.$modal.msgError(response.msg);
                    }
                },
                
            })
        },
        // 前往收藏内容页面
        toCollectContentViews(collectContentDetail) {
            if (collectContentDetail.type == this.collectType.COLLECT_ARTICLE) {
                this.articleViewCountAddOne(collectContentDetail.associateId);
                const { href } = this.$router.resolve({
                    name: "check_article",
                    params: {
                        articleId: collectContentDetail.associateId,
                    }
                })
                window.open(href, "_black");

            } else if (collectContentDetail.type == this.collectType.COLLECT_QUESTION) {
                this.questionViewCountAddOne(collectContentDetail.associateId);
                const { href } = this.$router.resolve({
                    name: "question_reply",
                    params: {
                        questionId: collectContentDetail.associateId,
                    }
                })
                window.open(href, "_black");

            } else if (collectContentDetail.type == this.collectType.COLLECT_COURSE) {
                this.courseViewAdd(collectContentDetail.associateId);
                const { href } = this.$router.resolve({
                    name: "course_detail",
                    params: {
                        courseId: collectContentDetail.associateId,
                    }
                })
                window.open(href, "_black");

            } else if (collectContentDetail.type == this.collectType.COLLECT_COMMUNITY_ARTICLE) {
                this.communityArticleViewCountAddOne(collectContentDetail.associateId);
                this.toCommunityArticleViews(collectContentDetail.associateId);
                const { href } = this.$router.resolve({
                    name: "community_article",
                    params: {
                        communityArticleId: collectContentDetail.associateId,
                    }
                })
                window.open(href, "_black");
            } else if (collectContentDetail.type == this.collectType.COLLECT_RESOURCE) {
                this.resourceViewCountAddOne(collectContentDetail.associateId);
                const { href } = this.$router.resolve({
                    name: "resource_detail",
                    params: {
                        resourceId: collectContentDetail.associateId,
                    }
                    
                })
                window.open(href, "_black");
            }
        },
        // 前往社区文章页面
        toCommunityArticleViews(communityArticleId) {
            const vue = this;
            $.ajax({
                url: vue.communityArticleUrl + "/queryCommunityIdByArticleId",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    communityArticleId,
                },
                success(response) {
                    if (response.code == '200') {
                        const { href } = vue.$router.resolve({
                            name: "community_article",
                            params: {
                                communityArticleId,
                                communityId: response.data,
                            }
                        })
                        window.open(href, "_black");
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询收藏详细内容
        queryCollectContentDetail(collectContent) {
            const vue = this;
            $.ajax({
                url: vue.userHomeCollectUrl + "/queryCollectContentDetail",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    collectContentId: collectContent.id,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.collectContentDetailList = response.data;
                        collectContent.isShow = '1';
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询用户收藏目录信息
        queryUserCollectContent(userId) {
            const vue = this;
            $.ajax({
                url: vue.userHomeCollectUrl + "/queryUserCollectContent",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    userId,
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.collectContentList = response.data.records;
                        vue.totals = response.data.total;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        handleSizeChange(val) {
            this.pageSize = val;
            this.currentPage = 1;
            this.queryUserCollectContent(this.userId);
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            this.queryUserCollectContent(this.userId);
        },
        getTimeFormat(val) {
            return getTimeFormat(val);
        }
    },
};
</script>

<style scoped>
.collect-medium {
    margin-bottom: 5px;
}
.display-content {
    background-color: #F7F8F9;
    padding: 16px;
    animation: slide-out 0.2s linear;
}
.collect-operate {
    animation: slide-out 0.2s linear;
}
@keyframes slide-out {
    0% {
        opacity: 0.5;
    }
    100% {
        opacity: 1;
    }
}
.collect-common {
    font-size: 14px;
    color: gray;
    margin-right: 10px;
}
.collect-content-name:hover {
    color: #409EFF;
}
.collect-content-name {
    font-size: 14px;
    color: gray;
    margin-bottom: 5px;
    display: inline-block;
    max-width: 720px;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    vertical-align: middle;
    transition: 0.2s linear all;
}
.collect-name {
    font-size: 16px;
    margin-bottom: 5px;
    display: inline-block;
    max-width: 740px;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    vertical-align: middle;
}
.collect-card:hover {
    box-shadow: 0 0 10px 0 #00f2fe;
}
.collect-card {
    cursor: pointer;
    transition: 0.4s linear all;
    padding: 20px;
}
.MonkeyWebUserHomeCollect-container {
    padding: 20px;
    background-color: #fff;
    vertical-align: middle;
}
</style>