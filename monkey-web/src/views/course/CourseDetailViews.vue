<template>
    <div class="MonkeyWebCourseDetail-container" style="z-index: 1;">
        <CollectCard v-if="showCollect"
            :associateId="associateId"
            :showCollect="showCollect"
            :collectType="collectType"
            :collectTitle="collectTitle"
            @closeCollect="closeCollect"/>
        <el-row class="all-background">
        <el-row class="top up-to-down">
            <el-col :span="16">
                <el-row class="ellipsis-title title">
                   {{ courseInformation.title }}
                </el-row>
                <el-row class="profile">
                    <el-col :span="4">
                        <el-row >
                            {{ courseInformation.collectCount }}
                        </el-row>
                        <el-row>
                            课程收藏数
                        </el-row>
                     <span class="divider-left-collect"></span>
                    </el-col>
                    <el-col :span="6">
                        <el-row>
                            {{ courseInformation.sectionCount }} 节
                        </el-row>
                        <el-row>
                            节数
                        </el-row>
                        <span class="divider-left"></span>
                    </el-col>
                    <el-col :span="6">
                        <el-row>
                            {{ courseInformation.studyCount }} 人
                        </el-row>
                        <el-row>
                            购买人数
                        </el-row>
                        <span class="divider-left"></span>
                    </el-col>
                    <el-col :span="6">
                        <el-row>
                            <el-rate
                                v-model="courseInformation.score"
                                disabled
                                show-score
                                text-color="#ff9900"
                                score-template="{value}">
                                </el-rate>
                        </el-row>
                        <el-row>
                            课程评分
                        </el-row>
                    </el-col>
                </el-row>
                <el-row style=" padding-bottom: 20px; margin-left: 30px;">
                    <el-col :span="6" v-if="courseInformation.isFree == '1' && isAuthority == '0'">
                    <el-button type="primary" class="free-view" @click="toCourseVideoPlay(courseId)" >
                        <i class="el-icon-view"></i> 
                        免费试看</el-button>
                    </el-col>
                    <el-col :span="6" v-if="isAuthority == '1'">
                    <el-button type="primary" class="free-view" @click="toCourseVideoPlay(courseId)" >
                        <i class="el-icon-view"></i> 
                        点击观看</el-button>
                    </el-col>
                    <el-col :span="6" v-if="courseInformation.isFree == '0'">
                        <el-button type="primary" class="free-view" @click="toCourseVideoPlay(courseId)">
                            <i class="el-icon-view"></i> 点击观看</el-button>
                        </el-col>
                    <el-col 
                    :span="6" 
                    v-if="courseInformation.isFree == '1' && 
                    courseInformation.formTypeId == '3' && 
                    courseInformation.userId != $store.state.user.id
                    && isAuthority == '0'">
                        <el-button @click="toCourseBuyViews(courseInformation.id)" type="danger" class="buy-view">{{ courseInformation.discountPrice }} 购买</el-button>
                    </el-col>
                    <el-col 
                    :span="6" 
                    v-if="courseInformation.isFree == '1' && 
                    courseInformation.formTypeId == '2' && 
                    courseInformation.userId != $store.state.user.id">
                        <el-button @click="toVipViews(courseInformation.id)" type="danger" class="buy-view">会员免费</el-button>
                    </el-col>
                    <el-col 
                    :span="4" 
                    class="discount" 
                    v-if="courseInformation.isDiscount == '1' && 
                    courseInformation.formTypeId == '3' && 
                    courseInformation.userId != $store.state.user.id">
                        课程{{ courseInformation.discount }}折
                    </el-col>
                    <el-col 
                    :span="6" 
                    class="original-price" 
                    v-if="courseInformation.isDiscount == '1' && 
                    courseInformation.formTypeId == '3' && 
                    courseInformation.userId != $store.state.user.id">
                        原价￥{{ courseInformation.coursePrice }}
                    </el-col>
                </el-row>
            </el-col>
            <el-col :span="6" style="float: right;">
                <el-row style="text-align: right;">
                    <span class="share-convert">
                        <span 
                        @click="userCollect(courseId, courseInformation.title)" 
                        class="iconfont icon-shoucang" 
                        v-if="isCollect == '0'">&nbsp;收藏 &nbsp;</span>

                        <span 
                        class="iconfont icon-shoucang"
                        @click="userCollect(courseId, courseInformation.title)" 
                        v-if="isCollect == '1'" 
                        style="color: lightgreen;">&nbsp;收藏 &nbsp;</span>
                        &nbsp;
                        <span class="iconfont icon-zhuanfa">&nbsp;转发</span>
                    </span>
                </el-row>
                <el-row class="background-teacher">
                    <el-col :span="4">
                        <img class="teacher-img" @click="toUserHome(userInformation.id)" :src="userInformation.photo" alt="">
                    </el-col>
                    <el-col :span="6" >
                        <el-row class="teacher-name ellipsis-name">&nbsp;{{ userInformation.username }}</el-row>
                        <div class="divider-right-name"></div>
                    </el-col>
                    <el-col :span="6" class="course-sum">
                        <el-row style="color: lightgreen;">{{ userInformation.concern }}</el-row>
                        <el-row style="color: white;">关注数</el-row>
                        <div class="divider-right"></div>
                    </el-col>
                    <el-col :span="6" class="course-sum">
                        <el-row style="color: lightgreen;">{{ userInformation.fans }}</el-row>
                        <el-row style="color: white;">粉丝数</el-row>
                    </el-col>
                </el-row>
                <el-row>
                    <el-row class="teacher-profile">用户简介</el-row>
                    <el-row class="teacher-profile-content">
                        {{ userInformation.brief }}
                    </el-row>
                </el-row>
            </el-col>
        </el-row>
        </el-row>
            <scrollactive class="my-nav anchor-point up-to-down" :offset="0" :scrollToOptions="{ behavior: 'smooth' }" style="z-index: 10000;" >
                <a class="nav-anchor showColor" href="#listshow1" @click="toAnchor(1)" >课程介绍
                    <span v-if="showIndex == '1'" class="underline"></span>
                </a>
                <a class="nav-anchor"  href="#listshow2" @click="toAnchor(2)" style="margin-left: 65px;">课程目录
                    <span v-if="showIndex == '2'" class="underline"></span>
                    </a>
                <a class="nav-anchor" href="#listshow3" @click="toAnchor(3)" style="margin-left: 65px; ">讨论留言
                    <span v-if="showIndex == '3'" class="underline"></span>
                    </a>
            </scrollactive>
        <el-row class="content down-to-up" style="padding: 30px;">
                <el-col  :span="17">
                    <el-row >
                        <el-card id="listshow1" style="position: relative; padding: 10px;" class="card">
                            <el-row class="will-harvest">你将收获</el-row>
                            <span class="background-content"></span>
                            <el-row>
                                <vue-markdown 
                                class="markdown-content"
                                :source="courseInformation.harvest" 
                                :highlight="true"
                                :html="true"
                                :xhtmlOut="true">
                                </vue-markdown>
                            </el-row>
                        </el-card>
                        <el-card style="margin-top: 10px; padding: 10px;" class="card">
                            <el-row class="will-harvest">适用人群</el-row>
                            <span class="background-content"></span>
                            <el-row>
                                <vue-markdown 
                                class="markdown-content"
                                :source="courseInformation.suitPeople" 
                                :highlight="true"
                                :html="true"
                                :xhtmlOut="true">
                                </vue-markdown>
                            </el-row>
                        </el-card>

                        <el-card class="card">
                            <el-row class="will-harvest">课程介绍</el-row>
                            <span class="background-content"></span>
                            <el-row>
                                <vue-markdown 
                                class="markdown-content"
                                :source="courseInformation.introduce" 
                                :highlight="true"
                                :html="true"
                                :xhtmlOut="true">
                                </vue-markdown>
                            </el-row>
                        </el-card>
                    </el-row>
                    <el-card class="card" id="listshow2">
                            <el-row class="will-harvest" style="margin-bottom: 10px;">课程目录</el-row>
                            <span class="background-content"></span>
                            <el-row class="course-directory-row" v-for="courseDirectory in courseDirectoryList" :key="courseDirectory.id">
                                <div @click="toCourseVideoOrChargeViews(courseDirectory)">
                                    <el-col 
                                        v-if="courseDirectory.isFree == '0' || $store.state.user.id == courseInformation.userId || isAuthority == '1'"
                                        class="el-icon-video-play directory" 
                                        :span="22">
                                        {{ courseDirectory.title }} 
                                        <span class="is-free">免费</span>
                                    </el-col>
                                    <el-col 
                                        v-else
                                        class="el-icon-video-play directory" 
                                        :span="22">
                                        {{ courseDirectory.title }} 
                                        <span class="not-free">{{ formTypeName }}</span>
                                    </el-col>
                                    <el-col class="play-time" :span="2">{{ courseDirectory.videoTime }}</el-col>
                                </div> 
                            </el-row>
                    </el-card>
                    
                    <course-comment id="listshow3" style="z-index: 1;"/>
                
                </el-col>
                <el-col :span="7">
                    <el-row class="commend-course">
                        <el-row class="recommend-course-background"></el-row>
                        <el-row class="recomment-course-font">推荐</el-row>
                        <el-row 
                        class="recommend-card" 
                        v-for="courseRecommend in courseRecommendList" :key="courseRecommend.id">
                        <div  @click="toCourseDetail(courseRecommend.id)">
                            <el-col :span="7">
                                <img style="width: 90px; height: 60px;"  :src="courseRecommend.picture" alt="">
                            </el-col>
                            <el-col :span="17">
                                <el-row>
                                <el-row class="section-title">
                                    {{ courseRecommend.title }}
                                </el-row>
                                <el-row style="padding: 5px;">
                                    <span class="course-section"> {{ courseRecommend.sectionCount }}节</span>
                                    <span class="section-teacher-name">{{ courseRecommend.teacherName }}</span>
                                </el-row>
                                </el-row>
                            </el-col>
                        </div>
                        </el-row>
                    </el-row>

                    <el-row class="connect-course">
                        <el-row class="connect-course-background"></el-row>
                        <el-row class="recomment-course-font" >热门</el-row>
                        <el-row 
                        class="recommend-card" 
                        v-for="connectCourse in connectCourseList" :key="connectCourse.id">
                        <div  @click="toCourseDetail(connectCourse.id)">
                            <el-col :span="7">
                                <img style="width: 90px; height: 60px;"  :src="connectCourse.picture" alt="">
                            </el-col>
                            <el-col :span="17">
                                <el-row>
                                <el-row class="section-title">
                                    {{ connectCourse.title }}
                                </el-row>
                                <el-row style="padding: 5px;">
                                    <span class="course-section"> {{ connectCourse.sectionCount }}节</span>
                                    <span class="section-teacher-name">{{ connectCourse.teacherName }}</span>
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
import CollectCard from '@/components/collect/CollectCard.vue';
import CourseComment from '@/components/course/CourseComment'
import $ from 'jquery'
import store from '@/store';
import VueMarkdown from 'vue-markdown';
export default {
    name: 'MonkeyWebCourseDetail',
    components: {
        VueMarkdown,
        CourseComment,
        CollectCard
    },
    data() {
        return {
            // 是否展示收藏夹
            showCollect: false,
             // 收藏类型
            collectType: 2,
            // 收藏标题
            collectTitle: "",
            // 收藏关联id
            associateId: "",
            courseInformation: {},
            showIndex: '1',
            // 当前页面的课程id
            courseId: "",
            // 是否贴在屏幕最上方
            isSticky: false,
            value: 3.7,
            content: "",
            // 选中的课程锚点，0表示课程介绍，1表示课程目录，2表示讨论留言
            anchor: 1,
            // 课程详细信息地址
            courseDetailUrl: "http://localhost/monkey-course/detail",
            // 课程播放url
            coursePlayUrl: "http://localhost/monkey-course/video/player",
            userId: store.state.user.id,
            // 当前登录用户是否收藏该文章(0表示未收藏，1表示已收藏)
            isCollect: 0,
            // 课程推荐列表
            courseRecommendList: [],
            // 相关课程列表
            connectCourseList: [],
            // 教师信息
            userInformation: {},
            // 课程形式类型名称
            formTypeName: "",
            // 用户是否有权限查看该课程 (0表示无权，1表示有权限)
            isAuthority: "",
            // 课程目录列表
            courseDirectoryList: [],
        };
    },
    watch: {
        $route() {
            this.courseId = this.$route.params.courseId;
            this.userId = store.state.user.id;
            this.getCourseInfoByCourseId(this.courseId);
            this.judgeIsCollect(this.courseId);
            this.getConnectCourseList(this.courseId);
            this.getUserInfoByCourseId(this.courseId);
            this.getCourseRecommendList();
            this.getCourseDirectoryByCourseId(this.courseId);
        },
    },
    created() {
        this.courseId = this.$route.params.courseId;
        this.userId = store.state.user.id;
        this.getCourseInfoByCourseId(this.courseId);
        this.judgeIsCollect(this.courseId);
        this.getConnectCourseList(this.courseId);
        this.getUserInfoByCourseId(this.courseId);
        this.getCourseRecommendList();
        this.getCourseDirectoryByCourseId(this.courseId);
    },
    mounted() {
        window.addEventListener('scroll', this.handleScroll);
    },
    methods: {
        // 前往课程播放界面或收费
        toCourseVideoOrChargeViews(courseDirectory) {
            if (courseDirectory.isFree == '1' && this.isAuthority == "0" && this.courseInformation.userId != store.state.user.id) {
                if (this.courseInformation.formTypeId == '2') {
                    this.$router.push({
                        name: "vip",
                    });
                } else if (this.courseInformation.formTypeId == '3') {
                    this.$router.push({
                        name: "course_pay",
                        params: {
                            courseId: this.courseId
                        }
                    })
                }

                return;
            }
            // 否则说明用户有权限，前往播放界面
            if (this.isAuthority == '1') {
                this.$router.push({
                    name: "course_video_play",
                    params: {
                        courseId: this.courseId
                    }
                })

                return;
            }
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
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.courseDirectoryList = response.data.courseVideoList;
                        vue.formTypeName = response.data.formTypeName;
                        vue.isAuthority = response.data.isAuthority;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 前往VIP界面
        toVipViews(courseId) {
            const { href } = this.$router.resolve({
                name: "vip",
            })

            window.open(href, "_black");
        },
        //调至用户购买界面
        toCourseBuyViews(courseId) {
            const { href } = this.$router.resolve({
                name: "course_pay",
                params: {
                    courseId
                }
            })

            window.open(href, "_black");
        },
        // 跳至用户主页
        toUserHome(userId) {
            const { href } = this.$router.resolve({
                name: "user_home",
                params: {
                    userId
                }
            })

            window.open(href, '_black');
        },
        // 跳转至课程播放界面
        toCourseVideoPlay(courseId) {
            const { href } = this.$router.resolve({
                name: "course_video_play",
                params: {
                    courseId,
                },
            })

            window.open(href, '_black');
        },
        // 跳转至课程详情页面
        toCourseDetail(courseId) {
            this.$router.push({
                name: "course_detail",
                params: {
                    courseId
                }
            })
        },
        // 通过课程id得到教师信息
        getUserInfoByCourseId(courseId) {
            const vue = this;
            $.ajax({
                url: vue.courseDetailUrl + "/getUserInfoByCourseId",
                type: "get",
                data: {
                    courseId
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.userInformation = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 得到相关课程列表
        getConnectCourseList(courseId) {
            const vue = this;
            $.ajax({
                url: vue.courseDetailUrl + "/getConnectCourseList",
                type: "get",
                data: {
                    courseId,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.connectCourseList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
                
            })
        },
        // 得到课程官方推荐列表
        getCourseRecommendList() {
            const vue = this;
            $.ajax({
                url: vue.courseDetailUrl + "/getCourseRecommendList",
                type: "get",
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.courseRecommendList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
                
            })
        },
        // 用户收藏课程
        userCollect(articleId, title) {
            this.associateId = articleId;
            this.showCollect = true;
            this.collectTitle = title;
        },
        closeCollect(status) {
            this.showCollect = status;
            this.judgeIsCollect(this.courseId);
        },
        // 判断当前登录用户是否收藏该课程
        
        judgeIsCollect(courseId) {
            const vue = this;
                $.ajax({
                    url: vue.courseDetailUrl + "/judgeIsCollect",
                    type: "get",
                    headers: {
                        Authorization: "Bearer " + store.state.user.token,
                    },
                    data: {
                        courseId,
                        // 在后端接
                        collectType: vue.collectType,
                    },
                    success(response) {
                        if (response.code == vue.ResultStatus.SUCCESS) {
                            vue.isCollect = response.data;
                        } else {
                        vue.$modal.msgError(response.msg);
                    }
                    },
                })
            },
            
        // 通过课程id得到课程信息
        getCourseInfoByCourseId(courseId) {
            const vue = this;
            $.ajax({
                url: vue.courseDetailUrl + '/getCourseInfoByCourseId',
                type: "get",
                data: {
                    courseId
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.courseInformation = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
        },
        // 跳转到对应的锚点
        toAnchor(num) {
            this.showIndex = num;
            const targetId = `#listshow${num}`;
            this.$scrollTo(targetId, 500);
            // var el = document.getElementById(`listshow${num}`);
            // this.$nextTick(function () {
            //     window.scrollTo({ "behavior": "smooth", "top": el.getBoundingClientRect().top + window.pageYOffset });
            // });
        },
        // 实现滑动到某个位置某个位置就显示下划线
        handleScroll() {
            const windowHeight = window.innerHeight;

            // 遍历元素列表，确定当前显示项的索引值
            for (let i = 1; i <= 3; i++) {
                const itemEl = document.getElementById(`listshow${i}`);

                if (!itemEl) {
                    continue; // 元素不存在，跳过本次循环
                }

                const rect = itemEl.getBoundingClientRect();
                const isVisible = rect.top >= 0 && rect.bottom <= windowHeight;

                if (isVisible) {
                    // 滚动到元素顶部时，更新showIndex
                    this.showIndex = i;
                    break;
                }
            }
        },
    },
    beforeDestroy() {
        window.removeEventListener('scroll', this.handleScroll);
    }
};
</script>

<style scoped>
.profile {
    color: white; 
    text-align: center; 
    padding: 20px 0px;
}
.card {
    position: relative; 
    margin-top: 10px; 
    padding: 10px;
}
.showColor {
    color: rebeccapurple;
    opacity: 1;
}
.up-to-down {
    animation:up-to-down  0.4s linear;
}

@keyframes up-to-down {
    0% {
        transform: translateY(-100px);
        opacity: 0;
    }
    60% {
        transform: translateY(-30px);
        opacity: 1;
    }
    80% {
        transform: translateY(20px);
    }
    100% {
        transform: translateY(0px);
    }
}

.down-to-up {
    animation:down-to-up  0.4s linear;
}

@keyframes down-to-up {
    0% {
        transform: translateY(100px);
        opacity: 0;
    }
    60% {
        transform: translateY(30px);
        opacity: 1;
    }
    80% {
        transform: translateY(-20px);
    }
    100% {
        transform: translateY(0px);
    }
}

.connect-course-background {
    position: absolute;
    background-color: #00984E;
    color: white;
    top: -29px;
    left: -29px;
    height: 60px;
    width: 60px;
    text-align: center;
    line-height: 25px;
    transform: rotate(-45deg);
}
.connect-course {
    background-color: #FFFFFF;
    position: relative;
    margin-left: 10px;
    padding: 10px;
    border-radius: 10px;
    overflow: hidden;
    margin-top: 10px;
    min-height: 50px;
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
.course-section {
    text-align: left;
    font-size: 14px;
    opacity: 0.7;

}
.recomment-course-font {
    position: absolute;
    color: white;
    top: 4px;
    left: 0px;
    font-size: 14px;
    transform: rotate(-45deg);
}
.recommend-course-background {
    position: absolute;
    background-color: #FFAA00;
    color: white;
    top: -29px;
    left: -29px;
    height: 60px;
    width: 60px;
    text-align: center;
    line-height: 25px;
    transform: rotate(-45deg);
}
.commend-course {
    background-color: #FFFFFF;
    position: relative;
    margin-left: 10px;
    padding: 10px;
    border-radius: 10px;
    overflow: hidden;
    min-height: 50px;
}
.sticky {
    position: fixed;
    margin-top: 10px;
}

.custom-input {
  border-color: #42b983;
  background-color: #f0f9eb;
  color: #555;
}

.custom-input:focus {
  border-color: #42b983;
  box-shadow: 0 0 0 2px rgba(66, 185, 131, 0.2);
}


.play-time {
     font-size: 12px; 
     line-height: 34px;
}
.course-directory-row {
    padding: 10px;
}
.course-directory-row:hover {
    color: #087df3;
    cursor: pointer;
    background-color: rgb(227, 240, 244);
    transition: 0.3s linear all;
}
.directory {
    font-size: 14px;
    padding: 10px;
}
.not-free {
    position: absolute;
    width: 30px;
    height: 14px;
    background-image: linear-gradient(to right, #f9d423 0%, #ff4e50 100%);
    margin-left: 10px;
    font-size: 12px;
    text-align: center;
    line-height: 14px;
    color: white;
}

.is-free {
    position: absolute;
    width: 30px;
    height: 14px;
    background-image: linear-gradient(to right, #92fe9d 0%, #00c9ff 100%);
    margin-left: 10px;
    font-size: 12px;
    text-align: center;
    line-height: 14px;
    color: white;
}

.not-free {
    position: absolute;
    width: 30px;
    height: 14px;
    background-image: linear-gradient(to right, #f9d423 0%, #ff4e50 100%);
    margin-left: 10px;
    font-size: 12px;
    text-align: center;
    line-height: 14px;
    color: white;
}
.card {
    border-radius: 20px;
    position: relative; 
}
.markdown-content {
    font-size: 14px;
    opacity: 0.8;
}
.background-content {
    position: absolute;
    background-color: #409EFF;
    width: 5px;
    height: 20px;
    top: 33px;
    left: 18px;
}
.will-harvest {
    top: 2px;
}
.content {
    margin: 10px auto; 
    width: 1400px;
}
.underline {
    width: 70px;
    height: 1px;
    background-color: black;
    position: absolute;
    top: 25px;
    left: -2px;
}
.nav-anchor:hover {
    cursor: pointer;
    color: black;
    opacity: 1;
}
.nav-anchor {
    opacity: 0.6;
    color: black;
    position: relative;
}
.anchor-point {
    position: sticky; 
    top: 0; 
    z-index: 1;
    line-height: 60px;
    background: white;
    height: 60px;
    text-align: center;
}
.teacher-profile-content {
    color: white;
    font-size: 14px;
    padding-top: 10px;
}
.teacher-profile {
    color: white;
    padding-top: 10px;
    font-size: 14px;
}
.divider-right-name {
    position: absolute; 
    height: 25px; 
    width: 1px; 
    margin-left: 80px;
    top: 20px;
    background-color: white;
}
.divider-right {
    position: absolute; 
    height: 25px; 
    width: 1px; 
    margin-left: 80px;
    top: 20px;
    background-color: white;
}
.course-sum {
    margin-top: 10px;
}
.teacher-name {
    margin-top: 20px;
    color: white;
}
.teacher-sex {
    margin-top: 10px;
    color: white;
}
.background-teacher {
    text-align: center;
    padding: 0;
    background-color: rgba(0, 0, 0, 0.2);
    border-radius: 15px;
}
.teacher-img {
    width: 55px; 
    height: 55px; 
    border-radius: 50%;
    padding-top: 3px;
    cursor: pointer;
}
.icon-zhuanfa:hover {
    cursor: pointer;
    color: #57c6e1;
}
.share-convert{
    color: white;
    font-size: 30px;
    padding: 5px;
}
.icon-shoucang:hover {
    cursor: pointer;
    color: #57c6e1;
}
.title {
    padding-left: 29px;
}
.original-price {
    margin-top: 12px;
    text-align: left;
    padding-left: 20px;
    color: #E5E9F0;
    text-decoration: line-through;
}
.discount {
    margin-top: 12px;
    text-align: center;
    color: white;
    height: 22px;
    font-size: 14px;
    padding-top: 1px;
    width: 110px;
    background-image: linear-gradient(to right, #eea2a2 0%, #bbc1bf 19%, #57c6e1 42%, #b49fda 79%, #7ac5d8 100%);
}
.buy-view::before {
    content: "￥";
}
.buy-view {
    height: 50px; 
    font-size: 20px; 
    font-weight: bolder;
    color: black;
}
.el-icon-view {
    font-size: 20px; 
    font-weight: bolder;
}
.free-view {
    width: 150px; 
    height: 50px; 
    font-size: 20px; 
    font-weight: bolder;
}
.divider-left-collect {
    position: absolute; 
    height: 25px; 
    width: 1px; 
    margin-left: 80px;
    top: 30px;
    background-color: white;
}

.divider-left {
    position: absolute; 
    height: 25px; 
    width: 1px; 
    margin-left: 115px;
    top: 30px;
    background-color: white;
}
.ellipsis-title {
    font-size: 27px;
    font-weight: bold;
    color: white;
    display: block;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
    text-align: left;
}
.ellipsis-name {
    display: block;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
    text-align: left;
}

.top {
    background-image: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    padding: 20px 40px;
    width: 1380px;
    margin: 0 auto;
  

}
.all-background {
    background-image: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    margin: 0 auto;
    height: 100%;
}
</style>