<template>
    <div class="MonkeyWebUserHomeCourse-container">
        <div class="course-card" 
        @click="toCourseDetailViews(course.id)"
        v-for="course in courseList" 
        :key="course.id"
        @mouseover="course.isHover = '1'"
        @mouseleave="course.isHover = '0'">
            <el-row style="margin-bottom: 10px;">
                <el-col :span="4">
                    <div class="img-border">
                        <img class="course-img" :src="course.picture" alt="">
                        <div class="formTypeName">{{ course.formTypeName }}</div>
                    </div>
                </el-col>
                <el-col :span="20">
                    <div >
                        <span class="course-name">{{ course.title }}</span>
                        <span class="create-time">发布于：{{ getTimeFormat(course.createTime) }}</span>
                    </div>
                    <div class="course-brief">{{ course.description }}</div>
                    <div style="position: relative;">
                        <span class="iconfont icon-yanjing operate-common">&nbsp;游览&nbsp;{{ getFormatNumber(course.viewCount) }}</span>
                        <span class="el-icon-user operate-common">&nbsp;学习&nbsp;{{ getFormatNumber(course.studyCount) }}</span>
                        <span class="el-icon-collection operate-common">&nbsp;小节&nbsp;{{ getFormatNumber(course.sectionCount) }}</span>
                        <span class="iconfont icon-danmu operate-common">&nbsp;弹幕&nbsp;{{ getFormatNumber(course.barrageCount) }}</span>
                        <span class="iconfont icon-shoucang operate-common">&nbsp;收藏&nbsp;{{ getFormatNumber(course.collectCount) }}</span>
                        <span class="iconfont icon-pinglun operate-common">&nbsp;评论&nbsp;{{ getFormatNumber(course.commentCount) }}</span>
                        <span class="iconfont icon-score operate-common">&nbsp;评分&nbsp;{{ course.score }}</span>
                        <div
                        v-if="course.isHover == '1' && course.userId == $store.state.user.id" 
                        @mouseover="course.isMoreHover = '1'"
                        @mouseleave="course.isMoreHover = '0'"
                        class="hover el-icon-more-outline">
                            <div v-if="course.isMoreHover == '1'" class="more-hover">
                                <div @click.stop="toEditCourseViews(course.id)" class="common-hover">编辑</div>
                                <div @click.stop="deleteCourse(course)" class="common-hover">删除</div>
                            </div>
                        </div>
                    </div>
                </el-col>
            </el-row>

            <div class="divisor"></div>
        </div>


        <div
        v-if="courseList == null || courseList == '' || courseList == [] || courseList.length <= 0"
        style="text-align: center;" >
            <el-empty description="暂无数据"></el-empty>
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
import { getFormatNumber } from '@/assets/js/NumberMethod';
import { getTimeFormat } from '@/assets/js/DateMethod';
export default {
    name: 'MonkeyWebUserHomeCourse',
    components: {
        PagiNation
    },
    data() {
        return {
            userId: "",
            currentPage: 1,
            pageSize: 10,
            totals: 0,
            courseList: [],
            userHomeCourseUrl: "http://localhost:80/monkey-course/user/home",
        };
    },

    created() {
        this.userId = this.$route.params.userId;
        this.queryCourseByUserId(this.userId);
    },

    methods: {
        getFormatNumber(val) {
            return getFormatNumber(val);
        },
        getTimeFormat(val) {
            return getTimeFormat(val);
        },
        // 删除课程
        deleteCourse(course) {
            this.$modal.msgWarning("此功能暂未开放, 请期待教师管理系统")
            return;
            this.$modal.confirm(`"确定删除 ${course.title} 课程?"`)
                .then(() => {
                    const vue = this;
                    $.ajax({
                        url: vue.userHomeCourseUrl + "/deleteCourse",
                        type: "delete",
                        headers: {
                            Authorization: "Bearer " + store.state.user.token,
                        },
                        data: {
                            courseId: course.id,
                        },
                        success(response) {
                            if (response.code == vue.ResultStatus.SUCCESS) {
                                vue.queryCourseByUserId(this.userId);
                                vue.$modal.msgSuccess(response.msg);
                            } else {
                                vue.$modal.msgError(response.msg);
                            }
                        }
                    })
                }).catch(() => {})
            
        },
        // 前往编辑课程界面
        toEditCourseViews(courseId) {
            this.$modal.msgWarning("此功能暂未开放, 请期待教师管理系统")
            return;
            this.$router.push({
                name: "course_edit",
                params: {
                    courseId
                }
            })
        },
        // 前往课程详情页面
        toCourseDetailViews(courseId) {
            const { href } = this.$router.resolve({
                name: "course_detail",
                params: {
                    courseId
                }
            })

            window.open(href, "_blank")
        },
        // 通过用户id查询课程集合
        queryCourseByUserId(userId) {
            const vue = this;
            $.ajax({
                url: vue.userHomeCourseUrl + "/queryCourseByUserId",
                type: "get",
                data: {
                    userId,
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.courseList = response.data.records;
                        console.log(vue.courseList)
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
            this.queryCourseByUserId(this.userId);
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            this.queryCourseByUserId(this.userId);
        },
        getFormatNumber(val) {
            return getFormatNumber(val);
        }
    },
};
</script>

<style scoped>
.create-time {
    font-size: 14px;
    color: gray;
    vertical-align: middle;
}
.course-name:hover {
    color: #409EFF;
}
.common-hover:hover {
    color: #409EFF;
}
.common-hover:nth-child(n + 1) {
    margin-bottom: 10px;
}

.common-hover:last-child {
    margin-bottom: 0;
}
.more-hover {
    position: absolute;
    bottom: 20px;
    right: -20px;
    width: 50px;
    font-size: 14px;
    padding: 10px;
    background-color: #fff;
    text-align: center;
    box-shadow: 0 0 5px 0 black;
    animation: slide-out 0.4s linear;
}

@keyframes slide-out {
    0% {
        opacity: 0;
    }
    100% {
        overflow: 1;
    }
}

.hover {
    position: absolute;
    right: 0;
    font-size: 24px;
    animation: slide-out 0.4s linear;
}
.formTypeName {
    position: absolute;
    top: 0px;
    left: 0;
    background-image: linear-gradient(to right, #92fe9d 0%, #00c9ff 100%);
    color: white;
    padding: 1px 5px;
    font-size: 14px;
}
.divisor {
    background-color: rgba(0, 0, 0, 0.1);
    width: 100%;
    height: 1px;
}
.course-card {
    cursor: pointer;
    margin-bottom: 10px;
}
.operate-common {
    margin-right: 10px;
    font-size: 14px;
    color: gray;
}
.course-name {
    display: inline-block;
    max-width: 590px;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    margin-bottom: 4px;
    vertical-align: middle;
    margin-right: 10px;
}

.course-brief {
    display: -webkit-box;
    height: 75px;
    color: gray;
    font-size: 14px;
    overflow: hidden;
    -webkit-line-clamp: 4;
    -webkit-box-orient: vertical;
    text-overflow: ellipsis;
    white-space: normal;
    vertical-align: middle;
}
.img-border {
    overflow: hidden; 
    display: inline-block;
    position: relative;
}
.course-img {
    width: 130px;
    height: 120px;
    cursor: pointer;
    transition: 0.4s linear all;
}
.course-img:hover {
    transform: scale(1.2);
}
@keyframes slide-out {
    0% {
        opacity: 0;
    }
    100% {
        opacity: 1;
    }
}
.MonkeyWebUserHomeCourse-container {
    background-color: #fff;
    padding: 20px;
    animation: show-out 0.4s linear;
}
</style>