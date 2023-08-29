<template>
    <div class="MonkeyWebCourseVideoPlayViews-container">
        <el-row style="padding: 20px; background-color: #FFFFFF;;">
            <el-col :span="17">
                <el-row class="course-title">
                    {{ courseInfo.title }}
                </el-row>
                <el-row style="margin: 10px 0;">
                    <span class="el-icon-video-play base-info">&nbsp;{{ courseInfo.viewCount }}</span>
                    &nbsp;
                    <span class="iconfont icon-danmu base-info">&nbsp;{{ courseInfo.barrageCount }}</span>
                    &nbsp;
                    <span class="base-info">{{ courseInfo.createTime }}</span>
                </el-row>
            
                <CourseVideo
                @getCourseInfoByCourseId="getCourseInfoByCourseId"
                :barrageListTotal="barrageList"
                v-if="isFinish"
                :courseInfo="courseInfo"
                :videoInfo="selectedCourseVideo"/>
                

                <el-row class="divisor"></el-row>
                
                <el-row>
                    <el-row >
                        <el-col :span="12" class="user-evaluate">
                            用户评价
                        </el-col>
                        <el-col :span="12" class="user-evaluate-right">
                            <span @click="beforePage()" class="el-icon-arrow-left user-evaluate-arrow"></span>
                            <span style="padding-bottom: 2px;">{{ currentPage }}</span>
                            <span @click="afterPage()" class="el-icon-arrow-right user-evaluate-arrow"></span>
                        </el-col>
                    </el-row>

                    <el-row>
                        <el-col :span="8">
                            <CourseRate 
                            :courseInfo="courseInfo"/>
                        </el-col>
                        <el-col :span="15" style="margin-left: 10px;">
                            <UserRate
                            @updateTotal="updateTotal"
                            :currentPage="currentPage"
                            :pageSize="pageSize"
                            :total="total"/>
                        </el-col>
                    </el-row>

                    <el-row>
                        <CourseComment/>
                    </el-row>
                </el-row>
                
            </el-col>
            <el-col :span="7" style="padding: 0 0 0 10px;">
            <el-row>
                <el-col :span="4">
                    <img @click="toUserViews(userInformation.id)" class="user-img" :src="userInformation.photo" alt="">
                </el-col>
                <el-col :span="20">
                    <!-- 作者信息 -->
                    <el-row class="username">{{ userInformation.username }}</el-row>
                    <el-row class="user-profile">{{ userInformation.brief }}</el-row>
                    <el-row>
                        <el-col :span="12" v-if="isFans == '0'">
                            <el-button 
                            size="mini"
                            type="primary hover-button" 
                            icon="el-icon-plus" 
                            @click="likeAuthor(userInformation.id)" 
                            style="width: 90%;" 
                            round>
                            关注</el-button>
                        </el-col>
                        <el-col :span="12" v-else>
                            <el-button type="danger hover-button" 
                            icon="el-icon-delete"  
                            size="mini"
                            @click="likeAuthor(userInformation.id)" 
                            style="width: 90%;" 
                            round>
                            取消关注</el-button>
                        </el-col>
                        <el-col :span="12">
                            <el-button @click="WebSocketChat(userInformation.id)"  
                            type="success hover-button" 
                            style="width: 90%;" 
                            size="mini"
                            round
                            icon="el-icon-chat-line-round"
                            >私信</el-button>
                        </el-col>
                    </el-row>
                </el-col>
            </el-row>

            <!-- 课程目录 -->
            <el-tabs v-model="activeName" class="el-tabs">
                <el-tab-pane  name="directory">
                    <span slot="label"><i class="el-icon-s-orderInformation"></i>&nbsp;课程目录</span>
                    <el-row 
                    :class="['el-tabs-content', 'hover-barrage-list', {selectedDirectory: contentSelected == courseDirectory.id}]" 
                    v-for="(courseDirectory, index) in courseDirectoryList" 
                    :key="courseDirectory.id">
                        <div @click="getVideoUrl(courseDirectory)">
                            <el-col :span="2">P{{ index + 1 }}</el-col>
                            <el-col v-if="courseDirectory.isFree == '0' || isAuthority == '1'" :span="19" class="barrage-title">{{ courseDirectory.title }}</el-col>
                            <el-col v-if="isAuthority == '0' && courseDirectory.isFree == '1'" :span="15" class="barrage-title">{{ courseDirectory.title }}</el-col>
                            <el-col v-if="isAuthority == '0' && courseDirectory.isFree == '1'" :span="4" class="formTypeName">{{ formTypeName }}</el-col>
                            <el-col :span="3" style="text-align: right;">{{courseDirectory.videoTime}}</el-col>
                        </div>
                    </el-row>      
                </el-tab-pane>

                <!-- 弹幕列表 -->
                <el-tab-pane name="barrageList">
                    <span slot="label"><i class="el-icon-tickets"></i>&nbsp;弹幕列表</span>
                    <table class="table-style">
                        <tr style="text-align: left;">
                            <th>时间</th>
                            <th>弹幕内容</th>
                            <th>发送时间</th>
                        </tr>
                        <tr 
                        v-for="barrage in barrageList" :key="barrage.id"
                        class="hover-barrage-list"
                        @mouseover="selectedBarrageId = barrage.id"
                        @mouseleave="selectedBarrageId = ''">
                            <td>{{ barrage.formatVideoTime }}</td>
                            <td>{{ barrage.content }}</td>
                            <!-- 当鼠标悬浮的时候显示举报 -->
                            <td v-if="selectedBarrageId == barrage.id && barrage.userId != $store.state.user.id">
                                <el-button type="danger" size="mini" plain  class="accusation">举报</el-button>
                            </td>
                            <td v-else-if="selectedBarrageId == barrage.id && barrage.userId == $store.state.user.id">
                                <el-button type="danger" 
                                size="mini" plain  
                                @click="cancelBarrage(barrage.id)"
                                class="accusation">
                                撤回</el-button>
                            </td>
                            <td v-else>
                                {{ barrage.formatCreateTime }}
                            </td>
                        </tr>
                    </table>
                </el-tab-pane>
            </el-tabs>
                <!-- 相关课程 -->
                <el-row class="connect-course">
                    <el-row class="connect-course-background"></el-row>
                    <el-row class="recomment-course-font" >热门</el-row>
                    <el-row 
                    v-for="courseFire in courseFireList" :key="courseFire.id"
                    class="recommend-card" >
                    <div  @click="toCourseDetail(courseFire.id)">
                        <el-col :span="7">
                            <img style="width: 90px; height: 60px;"  :src="courseFire.picture" alt="">
                        </el-col>
                        <el-col :span="17">
                            <el-row>
                            <el-row class="section-title">
                                {{ courseFire.title }}
                            </el-row>
                            <el-row style="padding: 5px;">
                                <span class="course-section">{{ courseFire.sectionCount }} 节</span>
                                <span class="section-teacher-name">{{ courseFire.username }}</span>
                            </el-row>
                            </el-row>
                        </el-col>
                    </div>
                    </el-row>
                </el-row>
                <!-- 该老师的其他课程 -->
                <el-row class="connect-course">
                    <el-row class="connect-course-background" style="background-color: #409EFF;"></el-row>
                    <el-row class="recomment-course-font" >作者</el-row>
                    <el-row 
                    v-for="teacherOther in teacherOtherList" :key="teacherOther.id"
                    class="recommend-card" >
                    <div  @click="toCourseDetail(teacherOther.id)">
                        <el-col :span="7">
                            <img style="width: 90px; height: 60px;"  :src="teacherOther.picture" alt="">
                        </el-col>
                        <el-col :span="17">
                            <el-row>
                            <el-row class="section-title">
                                {{ teacherOther.title }}
                            </el-row>
                            <el-row style="padding: 5px;">
                                <span class="course-section">{{ teacherOther.sectionCount }} 节</span>
                                <span class="section-teacher-name">{{ teacherOther.username }}</span>
                            </el-row>
                            </el-row>
                        </el-col>
                    </div>
                    </el-row>
                </el-row>
            </el-col>
        </el-row>
    </div>
</template>

<script>
import $ from "jquery"
import store from "@/store"
import CourseComment from '@/components/course/CourseComment.vue'
import UserRate from '@/components/course/UserRate.vue'
import CourseRate from '@/components/course/CourseRate'
import CourseVideo from '@/components/course/CourseVideo'
export default {
    name: 'MonkeyWebCourseVideoPlayViews',
    components: {
        CourseVideo,
        CourseRate,
        UserRate,
        CourseComment
    },
    data() {
        return {
            // 作者基本信息
            userInformation: {},
            // 悬浮弹幕列表id
            selectedBarrageId: "",
            // 弹幕列表
            barrageList: [],
            // 当前选择的是哪个目录
            contentSelected: "",
            // 父组件是否取得数据完成后将数据传递给子组件
            isFinish: false,
            // 选择的课程视频
            selectedCourseVideo: {},
            // 课程类型
            formTypeName: "",
            // 用户是否有权限查看该课(0表示权限不足，1表示有权限)
            isAuthority: 0,
            // 课程播放url
            coursePlayUrl: "http://localhost/monkey-course/video/player",
            videoUrl: "http://localhost/monkey-service/aliyun/video/getVideoPlayUrl",
            checkArticleUrl: "http://localhost:80/monkey-article/check",
            courseInfo: {},
            // 课程目录列表
            courseDirectoryList: [],
            // 当前课程id
            courseId: "",
            isHover: false,
            // 首先选中的列表
            activeName: "directory",
            // 测试
            isFans: '0',
            // 文章作者登录id
            userId: "",        
            // 热门课程列表
            courseFireList: [],
            // 该老师的其他课程列表
            teacherOtherList: [],
            // 用户评价当前页
            currentPage: 1,
            // 用户评价每页数据量
            pageSize: 2,
            // 用户评价总数据量
            total: 0,
	}
    },
    created() {
        this.courseId = this.$route.params.courseId;
        this.getCourseInfoByCourseId(this.courseId);
        this.getCourseDirectoryByCourseId(this.courseId);
        this.getFireCourseList(this.courseId);
    },
    mounted() {
        window.addEventListener("load", function () {
            this.getUserInfo(this.userId);
            this.judgeIsFans(this.userId);
            this.getTeacherOtherCourse(this.userId);
        }.bind(this));
        
    },
    methods: {
        updateTotal(value) {
            this.total = value;
        },
        // 前往用户评价前一页
        beforePage() {
            const current = this.currentPage - 1;
            if (current <= 0) {
                this.$modal.msgError("超过选择范围")
                return;
            }

            this.currentPage = current;
        },

        // 前往用户评价后一页
        afterPage() {
            const current = this.currentPage + 1;
            if (current * this.pageSize > this.total) {
                this.$modal.msgError("超过选择范围")
                return;
            }
            this.currentPage = current;
        },
        toCourseDetail(courseId) {
            const { href }  = this.$router.resolve({
                name: "course_detail",
                params: {
                    courseId
                },
            });
            window.open(href, '_blank')
        },
        // 查询热门课程列表
        getFireCourseList(courseId) {
            const vue = this;
            $.ajax({
                url: vue.coursePlayUrl + "/getFireCourseList",
                type: "get",
                data: {
                    courseId
                },
                success(response) {
                    if (response.code == '200') {
                        vue.courseFireList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 得到该老师的其他课程
        getTeacherOtherCourse(teacherId) {
            const vue = this;
            $.ajax({
                url: vue.coursePlayUrl + "/getTeacherOtherCourse",
                type: "get",
                data: {
                    teacherId
                },
                success(response) {
                    if (response.code == '200') {
                        vue.teacherOtherList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 跳至用户聊天界面
        WebSocketChat(receiverId) {
            const { href } = this.$router.resolve({
                name: "webSocket_chat",
                params: {
                    receiverId
                },
            })

            window.open(href, "_black");
        },
        // 关注作者
        likeAuthor(userId) {
            const vue = this;
            $.ajax({
                url: vue.checkArticleUrl + "/likeAuthor",
                type: "get",
                data: {
                    userId
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token
                },
                success(response) {
                    if (response.code == "200") {
                        vue.$modal.msgSuccess(response.msg);
                        vue.judgeIsFans(userId);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })            
        },
        // 跳到用户主页
        toUserViews(userId) {
            const { href } = this.$router.resolve({
                name: "user_home",
                params: {
                    userId
                }
            })

            window.open(href, '_black');
        },
        // 判断当前登录用户是否是作者粉丝
        judgeIsFans(userId) {
            const vue = this;
            $.ajax({
                url: vue.coursePlayUrl + "/judgeIsFans",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    userId
                },
                success(response) {
                    if (response.code == '200') {
                        vue.isFans = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 得到课程发布者基本信息, 并判断当前登录用户是否关注他
        getUserInfo(userId) {
            const vue = this;
            $.ajax({
                url: vue.coursePlayUrl + "/getUserInfo",
                type: "get",
                data: {
                    userId
                },
                success(response) {
                    if (response.code == '200') {
                        vue.userInformation = response.data
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 撤回2分钟之内的弹幕
        cancelBarrage(barrageId) {
            const vue = this;
            $.ajax({
                url: vue.coursePlayUrl + "/cancelBarrage",
                type: "put",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    barrageId,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.getBarrageListByCourseVideoId(vue.contentSelected);
                    } else {
                        vue.$modal.msgError(response.msg)
                    }
                }
            })
        },
        // 通过视频id得到弹幕列表, 并判断该弹幕是否时自己发送的
        getBarrageListByCourseVideoId(courseVideoId) {
            const vue = this;
            $.ajax({
                url: vue.coursePlayUrl + "/getBarrageListByCourseVideoId",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token
                },
                data: {
                    courseVideoId,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.barrageList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 通过视频视频资源id得到视频地址
        getCourseUrlByCourseSourceId(courseVideo) {
            const vue = this;
            $.ajax({
                url: vue.videoUrl,
                type: "get",
                data: {
                    videoSourceId: courseVideo.videoSourceId
                },
                success(response) {
                    if (response.code == '200') {
                        courseVideo.courseVideoUrl = response.data;
                        vue.isFinish = true;
                        vue.selectedCourseVideo = courseVideo;
                        vue.getBarrageListByCourseVideoId(courseVideo.id);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 得到视频地址
        getVideoUrl(courseVideo) {
            
            if (courseVideo.isFree == '1' && this.isAuthority == '0' && this.courseInfo.userId != store.state.user.id) {
                // 权限不足跳往页面缴费
                // 得到课程类型
                let formType = this.courseInfo.formTypeId;
                if (formType == '2') {
                    // 跳往开通会员课界面
                    this.$router.push({
                        name: "vip"
                    })
                } else if (formType == '3') {
                    // 跳往收费界面
                    this.$router.push({
                        name: "course_pay",
                        params: {
                            courseId: this.courseId,
                        }
                    })
                }
                return;
            }
            this.isFinish = false;
            this.contentSelected = courseVideo.id;
            // 否则通过视频视频资源id得到视频地址
            this.getCourseUrlByCourseSourceId(courseVideo);
        },
        // 通过课程id得到课程目录列表
        getCourseDirectoryByCourseId(courseId) {
            const vue = this;
            $.ajax({
                url: vue.coursePlayUrl + "/getCourseDirectoryByCourseId",
                type: "get",
                data: {
                    courseId,
                    userId: store.state.user.token
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.courseDirectoryList = response.data.courseVideoList;
                        vue.formTypeName = response.data.formTypeName;
                        vue.isAuthority = response.data.isAuthority;

                        if (vue.courseDirectoryList != null && vue.courseDirectoryList.length > 0) {
                            vue.contentSelected = vue.courseDirectoryList[0].id
                            vue.getVideoUrl(vue.courseDirectoryList[0]);
                        }
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 通过课程id得到课程基本信息
        getCourseInfoByCourseId(courseId) {
            const vue = this;
            $.ajax({
                url: vue.coursePlayUrl + "/getCourseInfoByCourseId",
                type: "get",
                data: {
                    courseId
                },
                success(response) {
                    if (response.code == '200') {
                        vue.courseInfo = response.data;
                        vue.userId = vue.courseInfo.userId;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        
        handleKeyDownBarrage(e) {
            if (e.keyCode === 13 && !e.ctrlKey) {
                // Enter，换行

                this.content += '\n';
            } else if (e.keyCode === 13 && e.ctrlKey) {
                // Ctrl + Enter，发送消息
                this.publishCourseComment(this.content, this.courseId);
            }
        },
    },

    
}
</script>

<style scoped >
.selectedDirectory {
    background-color: white;
    color: #409EFF;
}
.formTypeName {
    font-size: 12px;
    text-align: center;
    color: white;
    background-image: linear-gradient(to right, #f9d423 0%, #ff4e50 100%);
    opacity: 0.8;
}
.accusation {
    position: absolute;
    padding: 5px;
    top: 20%;
    width: 30%;
    animation: showAccusation 0.4s linear;
}
@keyframes showAccusation {
    0% {
        opacity: 0;
    }
    100% {
        opacity: 1;
    }
}
.hover-barrage-list {
    padding: 0 10px;
    position: relative;
    animation: showAccusation 0.4s linear;
}
.hover-barrage-list:hover {
    background-color: white;
    cursor: pointer;
}
.connect-course {
    position: relative;
    padding: 10px;
    border-radius: 10px;
    overflow: hidden;
    margin-top: 10px;
    min-height: 50px;
    background-color: #EEF2F5;
}
.connect-course-background {
    position: absolute;
    background-color: red;
    color: white;
    top: -29px;
    left: -29px;
    height: 60px;
    width: 60px;
    text-align: center;
    line-height: 25px;
    transform: rotate(-45deg);
}
.recomment-course-font {
    position: absolute;
    color: white;
    top: 4px;
    left: 0px;
    font-size: 14px;
    transform: rotate(-45deg);
}
.section-teacher-name {
    padding-left: 10px;
    font-size: 14px;
    opacity: 0.7;
}
.course-section::after {
    content: "\2022";
    margin-left: 10px;
    position: relative;
    top: -4px;
    color: gray;
}
.section-title {
    display: block;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
    text-align: left;
    opacity: 0.8;
    padding: 5px;
}
.recommend-card {
    padding: 10px;
    transition: 0.2s linear all;
}
.recommend-card:hover {
    cursor: pointer;
    background-image: linear-gradient(to top, #d5d4d0 0%, #d5d4d0 1%, #eeeeec 31%, #efeeec 75%, #e9e9e7 100%);
    opacity: 0.5;
}
.table-style {
    width: 100%; 
    text-align: left; 
    border-collapse: collapse; 
    table-layout:fixed
}
th {
    font-size: 12px;
    
}
td {
    font-size: 14px;
    padding: 10px 0;
}
th:nth-child(1) {
    width: 11%;
}
th:nth-child(2) {
    width: 55%;
    padding-left: 10px;
}
th:nth-child(3) {
    width: 34%;
}
td:nth-child(2) {
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
    padding: 0 10px;
}

.barrage-title {
    font-size: 14px;
    display: block;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
    text-align: left;
}
.el-tabs-content:nth-child(n + 1) {
    margin-top: 10px;
}
.el-tabs-content {
    font-size: 14px;
    padding: 5px 5px 5px 5px;
    border-radius: 5px;
}
.el-tabs {
    background-color: #EEF2F5;
    border-radius: 10px;
    margin-top: 13px;
    height: 65vh;
    /* 设置滚动条 */
    overflow-y: auto;
    padding-left: 10px;
    padding-right: 10px;
}
.user-profile {
    color: rgba(0, 0, 0, 0.5);
    font-size: 12px;
    display: block;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
    text-align: left;
    padding: 2px 0px;
}
.username {
    color: #FF6699;
    font-size: 14px;
}
.user-img {
    cursor: pointer;
    width: 50px;
    height: 50px;
    border-radius: 50%;
}
@keyframes take-in {
    0% {
        opacity: 0;
    }
    100% {
        opacity: 1;
    }
}
.barrage-speed-number {
    position: absolute;
    top: 21vh;
    right: 3vw;
    font-size: 14px;
}
.barrage-font-size-number {
    position: absolute;
    top: 16.2vh;
    right: 3vw;
    font-size: 14px;
}
.show-area-number {
    position: absolute;
    top: 11.2vh;
    right: 3vw;
    font-size: 14px;
}
.no-transparent-number {
    position: absolute;
    top: 6.6vh;
    right: 3vw;
    font-size: 14px;
}
.no-transparent-number::after {
    content: "%";
}
.show-area {
    position: absolute;
    top: 11vh;
    left: 3vw;
    font-size: 14px;
}
.barrage-font-size {
    position: absolute;
    top: 16vh;
    left: 3vw;
    font-size: 14px;
}
.barrage-speed {
    position: absolute;
    top: 21vh;
    left: 3vw;
    font-size: 14px;
}

.no-transparent {
    position:absolute;
    top: 6.6vh;
    left: 3vw;
    font-size: 14px;
}
.block {
    width: 80%;
    
}
.user-evaluate-arrow {
    font-size: 35px; 
    cursor: pointer;
    color: gray;
}
.user-evaluate-arrow:hover {
    color: black;
}
.user-evaluate-right {
    padding-top: 5px;
    display: flex;
    justify-content: right;
    font-size: 30px;
    color: gray;
}
.divisor {
    background-color: gray;
    opacity: 0.3;
    height: 1px;
    margin: 20px 0px;
}
.user-evaluate {
    font-size: 30px;
    font-weight: 600;
}


.course-title {
    font-size: 20px;
    font-weight: 600;
}
.base-info {
    font-size: 14px;
    opacity: 0.7;
    margin-top: 10px;
}
.MonkeyWebCourseVideoPlayViews-container {
    background-color: #FFFFFF;
    width: 90vw;
    height: 300px;
    margin: 10px auto;
}

</style>