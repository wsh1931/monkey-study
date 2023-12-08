<template>
    <div class="MonkeyWebSearchCourse-container">
        <el-tabs v-model="activeName" @tab-click="handleClick" >
            <el-tab-pane label="综合" name="comprehensive"></el-tab-pane>
            <el-tab-pane label="最新" name="latest"></el-tab-pane>
            <el-tab-pane label="最热" name="hire"></el-tab-pane>
            <el-tab-pane label="游览" name="view"></el-tab-pane>
            <el-tab-pane label="收藏" name="collect"></el-tab-pane>
            <el-tab-pane label="评论" name="comment"></el-tab-pane>
            <el-tab-pane label="评分" name="score"></el-tab-pane>
            <el-tab-pane label="学习人数" name="study"></el-tab-pane>
            <div
            class="infinite-list" 
            v-infinite-scroll="loadData" 
            infinite-scroll-distance="30">
                <div 
                @click="toCourseComment(course.id)"
                v-for="course in courseList" :key="course.id" 
                style="cursor: pointer;">
                    <el-row style="margin-bottom: 5px;">
                        <el-col :span="1">
                            <img @click="toUserViews(course.userId)" class="user-headImg" :src="course.userHeadImg" alt="">
                        </el-col>
                        <el-col :span="23" class="card-right">
                            <img class="course-picture" :src="course.picture" alt="">
                            <div class="font-class">
                                <span @click="toUserViews(course.userId)" class="username">{{ course.username }}</span>
                                <span class="publishTime">{{ getTimeFormat(course.createTime) }}</span>
                                <span v-if="course.formTypeName ==  formType.getMsg(1)" class="formType">{{ course.formTypeName }}</span>
                                <span v-else-if="course.formTypeName ==  formType.getMsg(3)" class="formType-fee">{{ course.formTypeName }}</span>
                                <span v-else-if="course.formTypeName ==  formType.getMsg(2)" class="formType-fee">{{ course.formTypeName }}</span>
                                <span v-else class="formType">{{ course.formTypeName }}</span>
                            </div>
                            <div class="brief">{{ course.userBrief }}</div>
                        </el-col>
                    </el-row>

                    <div class="course-title" v-html="course.title">
                    </div>

                    <div class="course-content" v-html="course.introduce">
                    </div>
                    <!-- <div style="margin-bottom: 10px;">
                        <el-tag type="info" size="mini">标签三</el-tag>
                    </div> -->

                    <div style="vertical-align: middle;">
                        <span class="iconfont icon-yanjing operate-common">&nbsp;{{ getFormatNumber(course.viewCount) }}</span>
                        <span class="el-icon-user operate-common">&nbsp;{{ getFormatNumber(course.studyCount) }}</span>
                        <span class="iconfont icon-shoucang operate-common">&nbsp;{{ getFormatNumber(course.collectCount) }}</span>
                        <span class="iconfont icon-pinglun operate-common">&nbsp;{{ getFormatNumber(course.commentCount) }}</span>
                        <span class="iconfont icon-shoucang operate-common">&nbsp;{{ course.score }}</span>
                        <el-tag 
                        type="info" 
                        size="small" 
                        style="margin-right: 10px;"
                        v-for="label in course.labelName" :key="label"
                        v-html="label"></el-tag>

                    </div>
                    <div class="divisor"></div>
                </div>
            </div>

        <div
        v-if="courseList == null || courseList == '' || courseList == [] || courseList.length <= 0"
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
    name: 'MonkeyWebSearchCourse',

    data() {
        return {
            isScroll: true,
            activeName: "comprehensive",
            currentPage: 1,
            pageSize: 10,
            totals: 20,
            // 关键词搜索字段
            keyword: this.$route.query.keyword,
            // 课程集合
            courseList: [],
            searchCourseUrl: "http://localhost:80/monkey-search/course",
        };
    },

    watch: {
        '$route.query.keyword'(newVal, oldVal) {
            this.keyword = newVal;
            this.currentPage = 1;
            this.courseList = [];
            if (this.activeName == 'comprehensive') {
                    this.queryComprehensiveCourse();
                } else if (this.activeName == "latest") {
                    this.queryLatestCourse();
                } else if (this.activeName == "hire") {
                    this.queryHireCourse();
                } else if (this.activeName == "view") {
                    this.queryViewCourse();
                } else if (this.activeName == "study") {
                    this.queryStudyCourse();
                } else if (this.activeName == "collect") {
                    this.queryCollectCourse();
                } else if (this.activeName == "comment") {
                    this.queryCommentCourse();
                } else if (this.activeName == 'score') {
                    this.queryScoreCourse();
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
                },
            })

            window.open(href, "_blank")
        },
        // 跳转至课程详情界面
        toCourseComment(courseId) {
            const { href } = this.$router.resolve({
                name: "course_detail",
                params: {
                    courseId
                }
            })
            window.open(href, '_black');
        },
        handleClick() {
            this.isScroll = false;
            this.currentPage = 1;
            this.courseList = [];
            if (this.activeName == 'comprehensive') {
                    this.queryComprehensiveCourse();
                } else if (this.activeName == "latest") {
                    this.queryLatestCourse();
                } else if (this.activeName == "hire") {
                    this.queryHireCourse();
                } else if (this.activeName == "view") {
                    this.queryViewCourse();
                } else if (this.activeName == "study") {
                    this.queryStudyCourse();
                } else if (this.activeName == "collect") {
                    this.queryCollectCourse();
                } else if (this.activeName == "comment") {
                    this.queryCommentCourse();
                } else if (this.activeName == 'score') {
                    this.queryScoreCourse();
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
                if (this.currentPage * this.pageSize >= this.totals) {
                    this.$modal.msgWarning("没有更多了");
                    return false;
                }
                if (this.activeName == 'comprehensive') {
                    this.queryComprehensiveCourse();
                } else if (this.activeName == "latest") {
                    this.queryLatestCourse();
                } else if (this.activeName == "hire") {
                    this.queryHireCourse();
                } else if (this.activeName == "view") {
                    this.queryViewCourse();
                } else if (this.activeName == "study") {
                    this.queryStudyCourse();
                } else if (this.activeName == "collect") {
                    this.queryCollectCourse();
                } else if (this.activeName == "comment") {
                    this.queryCommentCourse();
                } else if (this.activeName == 'score') {
                    this.queryScoreCourse();
                }
            }
        },
        // 查询课程评分最高的课程列表
        queryScoreCourse() {
            const vue = this;
            $.ajax({
                url: vue.searchCourseUrl + "/queryScoreCourse",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data.esCourseIndexList;
                        vue.totals = response.data.totals;
                        for (let i = 0; i < data.length; i++) {
                            vue.courseList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询学习人数最多课程列表
        queryStudyCourse() {
            const vue = this;
            $.ajax({
                url: vue.searchCourseUrl + "/queryStudyCourse",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data.esCourseIndexList;
                        vue.totals = response.data.totals;
                        for (let i = 0; i < data.length; i++) {
                            vue.courseList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询评论最多课程列表
        queryCommentCourse() {
            const vue = this;
            $.ajax({
                url: vue.searchCourseUrl + "/queryCommentCourse",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data.esCourseIndexList;
                        vue.totals = response.data.totals;
                        for (let i = 0; i < data.length; i++) {
                            vue.courseList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询收藏数最多课程列表
        queryCollectCourse() {
            const vue = this;
            $.ajax({
                url: vue.searchCourseUrl + "/queryCollectCourse",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data.esCourseIndexList;
                        vue.totals = response.data.totals;
                        for (let i = 0; i < data.length; i++) {
                            vue.courseList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询点赞数最多课程列表
        queryStudyCourse() {
            const vue = this;
            $.ajax({
                url: vue.searchCourseUrl + "/queryStudyCourse",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data.esCourseIndexList;
                        vue.totals = response.data.totals;
                        for (let i = 0; i < data.length; i++) {
                            vue.courseList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询游览数最多课程列表
        queryViewCourse() {
            const vue = this;
            $.ajax({
                url: vue.searchCourseUrl + "/queryViewCourse",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data.esCourseIndexList;
                        vue.totals = response.data.totals;
                        for (let i = 0; i < data.length; i++) {
                            vue.courseList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询最热课程列表
        queryHireCourse() {
            const vue = this;
            $.ajax({
                url: vue.searchCourseUrl + "/queryHireCourse",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data.esCourseIndexList;
                        vue.totals = response.data.totals;
                        for (let i = 0; i < data.length; i++) {
                            vue.courseList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询最新课程列表
        queryLatestCourse() {
            const vue = this;
            $.ajax({
                url: vue.searchCourseUrl + "/queryLatestCourse",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data.esCourseIndexList;
                        vue.totals = response.data.totals;
                        for (let i = 0; i < data.length; i++) {
                            vue.courseList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询综合课程列表
        queryComprehensiveCourse() {
            const vue = this;
            $.ajax({
                url: vue.searchCourseUrl + "/queryComprehensiveCourse",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data.esCourseIndexList;
                        vue.totals = response.data.totals;
                        for (let i = 0; i < data.length; i++) {
                            vue.courseList.push(data[i]);
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
    margin-right: 16px;
    font-size: 16px;
    color: gray;
}
.font-class {
    margin-bottom: 2px;
}
.course-content {
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
.course-content:hover {
    opacity: 0.8;
}
.course-title {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 10px;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    max-width: 600px;
    transition: 0.2s linear all;
}
.course-title:hover {
    opacity: 0.8;
}
.course-picture {
    position: absolute;
    right: 10px;
    top: 0;
    height: 100px;
    width: 150px;
    border-radius: 0;
    transition: 0.2s linear all;
}

.course-picture:hover {
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
.MonkeyWebSearchCourse-container {
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