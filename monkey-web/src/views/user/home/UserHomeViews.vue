<template>
    <div class="UserHome-container">
        <div class="header">
            <img class="background" src="https://monkey-blog.oss-cn-beijing.aliyuncs.com/user/background/user-background.jpg" alt="">
            <div class="userInfo">
                <img class="user-headImg" :src="userInfo.photo" alt="">
                <div class="username">
                    <div class="name">{{ userInfo.username }}</div>
                    <div class="user-brief">{{ userInfo.brief }}</div>
                </div>
                <div class="user-tag">
                    <el-tag 
                    v-if="userInfo.sex == '0'"
                    class="common-tag"
                    effect="dark" 
                    type="info" 
                    size="small" 
                    round>男</el-tag>
                    <el-tag 
                    v-if="userInfo.sex == '1'"
                    class="common-tag"
                    effect="dark" 
                    type="info" 
                    size="small" 
                    round>女</el-tag>
                    <el-tag 
                    v-if="userInfo.jobUnit != null && userInfo.jobUnit != ''"
                    class="common-tag"
                    effect="dark" 
                    type="info" 
                    size="small" 
                    round>{{ userInfo.jobUnit }}</el-tag>
                    <el-tag 
                    v-if="userInfo.job != null && userInfo.job != ''"
                    class="common-tag"
                    effect="dark" 
                    type="info" 
                    size="small" 
                    round>{{ userInfo.job }}</el-tag>
                    <el-tag 
                    class="common-tag"
                    effect="dark" 
                    type="info" 
                    size="small" 
                    round>IP属地：江西省</el-tag>
                    <el-tag 
                    v-if="userInfo.birthday != null && userInfo.birthday != ''"
                    class="common-tag"
                    effect="dark" 
                    type="info" 
                    size="small" 
                    round>出生日期：{{ formatDate(userInfo.birthday) }}</el-tag>
                    <el-tag 
                    class="common-tag"
                    effect="dark" 
                    type="info" 
                    size="small" 
                    round>注册时间：{{ formatDate(userInfo.registerTime) }}</el-tag>
                    <el-tag 
                    v-if="userInfo == '1'"
                    class="common-tag"
                    effect="dark" 
                    type="info" 
                    size="small" 
                    round>vip</el-tag>
                </div>
            </div>
            <div class="function-button">
                <el-button 
                class="el-icon-user" 
                size="small" 
                round>&nbsp;编辑资料</el-button>
                <el-button 
                class="el-icon-setting" 
                size="small" 
                round>&nbsp;内容管理</el-button>
            </div>
        </div>

        <div class="nav-class">
            <el-tabs v-model="activeName" @tab-click="handleChangeTag">
                <el-tab-pane name="resource">
                    <span slot="label">
                        <i class="iconfont icon-ziyuan">&nbsp;</i>
                        资源</span>
                </el-tab-pane>
                <el-tab-pane label="社区" name="community">
                    <span slot="label">
                        <i class="iconfont icon-shequ">&nbsp;</i>
                        社区</span>
                </el-tab-pane>
                <el-tab-pane label="社区文章" name="community_article">
                    <span slot="label">
                        <i class="iconfont iconfont icon-shequ1">&nbsp;</i>
                        社区文章</span>
                </el-tab-pane>
                <el-tab-pane label="课程" name="course">
                    <span slot="label">
                        <i class="iconfont icon-kecheng-">&nbsp;</i>
                        课程</span>
                </el-tab-pane>
                <el-tab-pane label="问答" name="question">
                    <span slot="label">
                        <i class="iconfont icon-tiwenquestion">&nbsp;</i>
                        问答</span>
                </el-tab-pane>
                <el-tab-pane label="博客" name="article">
                    <span slot="label">
                        <i class="iconfont icon-wenzhang">&nbsp;</i>
                        博客</span>
                </el-tab-pane>
                <el-tab-pane label="关注" name="concern">
                    <span slot="label">
                        <i class="el-icon-user">&nbsp;</i>
                        关注</span>
                </el-tab-pane>
                <el-tab-pane label="粉丝" name="fans">
                    <span slot="label">
                        <i class="el-icon-user">&nbsp;</i>
                        粉丝</span>
                </el-tab-pane>

                <el-tab-pane label="收藏" name="collect">
                    <span slot="label">
                        <i class="iconfont icon-shoucang">&nbsp;</i>
                        收藏</span>
                </el-tab-pane>
                
            </el-tabs>
            <div class="nav-right">
                <span class="common-right">原文&nbsp;({{ getFormatNumber(userAchievement.opusCount) }})</span>
                <span class="common-right">游览&nbsp;({{ getFormatNumber(userAchievement.viewCount) }})</span>
                <span class="common-right">获赞&nbsp;({{ getFormatNumber(userAchievement.likeCount) }})</span>
                <span class="common-right">收藏&nbsp;({{ getFormatNumber(userAchievement.collectCount) }})</span>
            </div>
        </div>

        <div class="nav-content-class">
            <el-row>
                <el-col :span="17">
                    <router-view class="show-out"></router-view>
                </el-col>
                <el-col :span="7" style="padding-left: 10px;">
                    <div class="userBaseInfo">
                        <div class="userInfo-common">
                            <span>用户编号</span>
                            <span class="userInfo-right-common">{{ userInfo.id }}</span>
                        </div>
                        <div class="userInfo-common">
                            <span>用户类型</span>
                            <span v-if="userInfo.isVip == '1'" class="userInfo-right-common">vip用户</span>
                            <span v-if="userInfo.isVip == '0'" class="userInfo-right-common">普通用户</span>
                        </div>
                        <div class="userInfo-common">
                            <span>注册时间</span>
                            <span class="userInfo-right-common">{{ formatDate(userInfo.registerTime) }}</span>
                        </div>
                        <div class="userInfo-common">
                            <span>关注用户</span>
                            <el-button 
                            @click="concernUser(userInfo)"
                            v-if="userInfo.isFans == '0'" 
                            class="userInfo-right-common" 
                            size="mini">关注</el-button>
                            <el-button 
                            @click="cancelConcernUser(userInfo)"
                            v-if="userInfo.isFans == '1'" 
                            class="userInfo-right-common" 
                            size="mini">取消关注</el-button>
                        </div>
                    </div>

                    <div class="latest-visit">
                        <div class="latest-visit-sum">最近访客</div>
                        <div class="latest-user" v-for="latest in latestList" :key="latest.id">
                            <el-tooltip class="item" effect="dark" :content="formatDate(latest.registerTime)" placement="top">
                                <div @click="toUserViews(latest.id)">
                                    <img class="latest-user-headImg" :src="latest.photo" alt="">
                                    <div class="latest-username">
                                        {{ latest.username }}
                                    </div>
                                </div>
                            </el-tooltip>
                        </div>
                        <div
                        v-if="latestList == null || latestList == '' || latestList == [] || latestList.length <= 0"
                        style="text-align: center;" >
                            <el-empty description="暂无访客"></el-empty>
                        </div>
                    </div>
                </el-col>
            </el-row>
        </div>
    </div>
</template>

<script>
import $ from 'jquery'
import store from '@/store';
import { getFormatNumber } from '@/assets/js/NumberMethod';
import { formatDate } from '@/assets/js/DateMethod';
export default {
    name: "UserHome",
    data() {
        return {
            activeName: "resource",
            // 当前正在访问的用户空间id
            userId: "",
            // 用户成就信息
            userAchievement: {},
            // 用户基本信息
            userInfo: {},
            // 最近访客列表
            latestList: [],
            userHomeUrl: "http://localhost/monkey-user/user/home",
            esUserUrl: "http://localhost/monkey-search/user/home",
            userSearchUrl: "http://localhost:80/monkey-user/search"
        }
    },
    created() {
        this.userId = this.$route.params.userId;
        this.initChildrenRouter();
        this.queryUserAchievement(this.userId);
        this.queryUserInfo(this.userId);
        this.queryLatestVisit(this.userId);
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
        // 查询最近访客列表
        queryLatestVisit(userId) {
            const vue = this;
            $.ajax({
                url: vue.userHomeUrl + "/queryLatestVisit",
                type: "get",
                data: {
                    userId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.latestList = response.data;
                        console.log(vue.latestList)
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 关注用户
        concernUser(user) {
            const vue = this;
            $.ajax({
                url: vue.userSearchUrl + "/concernUser",
                type: "put",
                headers: {
                    Authorization: 'Bearer ' + store.state.user.token,
                },
                data: {
                    concernId: user.id
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.$modal.msgSuccess(response.msg);
                        user.isFans = '1';
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 取消关注用户
        cancelConcernUser(user) {
            const vue = this;
            $.ajax({
                url: vue.userSearchUrl + "/cancelConcernUser",
                type: "put",
                headers: {
                    Authorization: 'Bearer ' + store.state.user.token,
                },
                data: {
                    concernId: user.id
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.$modal.msgSuccess(response.msg);
                        user.isFans = '0';
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        formatDate(val) {
            return formatDate(val);
        },
        // 查询用户成就
        queryUserAchievement(userId) {
            const vue = this;
            $.ajax({
                url: vue.esUserUrl + "/queryUserAchievement",
                type: "get",
                data: {
                    userId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.userAchievement = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询用户信息
        queryUserInfo(userId) {
            const vue = this;
            $.ajax({
                url: vue.userHomeUrl + "/queryUserInfo",
                type: "get",
                data: {
                    userId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.userInfo = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 初始化子路由组件
        initChildrenRouter() {
            if (this.activeName == "resource") {
                this.$router.push({
                    name: "user_home_resource"
                })
            } else if (this.activeName == "course") {
                this.$router.push({
                    name: "user_home_course"
                })
            } else if (this.activeName == "community") {
                this.$router.push({
                    name: "user_home_community"
                })
            } else if (this.activeName == "community_article") {
                this.$router.push({
                    name: "user_home_community_article"
                })
            } else if (this.activeName == "question") {
                this.$router.push({
                    name: "user_home_question"
                })
            } else if (this.activeName == "article") {
                this.$router.push({
                    name: "user_home_article"
                })
            } else if (this.activeName == "concern") {
                this.$router.push({
                    name: "user_home_concern"
                })
            } else if (this.activeName == "fans") {
                this.$router.push({
                    name: "user_home_fans"
                })
            } else if (this.activeName == "collect") {
                this.$router.push({
                    name: "user_home_collect"
                })
            }
        },
        handleChangeTag() {
            if (this.activeName == "resource") {
                this.$router.push({
                    name: "user_home_resource"
                })
            } else if (this.activeName == "course") {
                this.$router.push({
                    name: "user_home_course"
                })
            } else if (this.activeName == "community") {
                this.$router.push({
                    name: "user_home_community"
                })
            } else if (this.activeName == "community_article") {
                this.$router.push({
                    name: "user_home_community_article"
                })
            } else if (this.activeName == "question") {
                this.$router.push({
                    name: "user_home_question"
                })
            } else if (this.activeName == "article") {
                this.$router.push({
                    name: "user_home_article"
                })
            } else if (this.activeName == "concern") {
                this.$router.push({
                    name: "user_home_concern"
                })
            } else if (this.activeName == "fans") {
                this.$router.push({
                    name: "user_home_fans"
                })
            } else if (this.activeName == "collect") {
                this.$router.push({
                    name: "user_home_collect"
                })
            }
        },
        getFormatNumber(val) {
            return getFormatNumber(val);
        },
        formatDate(val) {
            return formatDate(val);
        }
    }
}
</script>

<style scoped>
.latest-visit-sum {
    font-size: 14px;
    margin-bottom: 10px;
}
.latest-user:nth-child(5 * n) {
    margin-right: 0;

}
.latest-user {
    margin-right: 8px;
}
.latest-username {
    font-size: 14px;
    color: gray;
    white-space: nowrap;
    text-overflow: clip;
    overflow: hidden;
    margin-bottom: 4px;
    vertical-align: middle;
    text-align: center;
    cursor: pointer;
}
.latest-user-headImg {
    cursor: pointer;
    width: 52px;
    height: 52px;
    transition: 0.4s linear all;
}
.latest-user-headImg:hover {
    opacity: 0.7;
}
.latest-user {
    width: 52px;
    height: 50px;
    display: inline-block;
}
.latest-visit {
    padding: 20px;
    background-color: #fff;
}
.userInfo-right-common {
    position: absolute;
    right: 0;
    top: 0;
}
.userInfo-common:last-child {
    margin-bottom: 0;
}
.userInfo-common {
    position: relative;
    vertical-align: middle;
    margin-bottom: 14px;
    font-size: 14px;
    color: gray;
}
.userBaseInfo {
    background-color: #fff;
    padding: 20px;
    margin-bottom: 10px;
}
.show-out {
    animation: show-out 0.4s linear;
}
@keyframes slide-out {
    0% {
        opacity: 0;
    }
    100% {
        opacity: 1;
    }
}

.common-right {
    margin-right: 20px;
    font-size: 14px;
}
.nav-right {
    position: absolute;
    right: 10px;
    top: 10px;
    vertical-align: middle;
}
.nav-class {
    position: relative;
    background-color: #fff;
    padding: 0px 20px;
    margin-bottom: 20px;
}
::v-deep .el-tabs__header {
    margin: 0 0 10px 0;
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
.function-button {
    position: absolute;
    bottom: 10px;
    right: 20px;
}
.user-vip {
    display: inline-block;
    padding: 0 5px 1px 5px;
    text-align: center;
    background-image: linear-gradient(to right, #f9d423 0%, #ff4e50 100%);
    color: #fff;
    border-radius: 2px;
}
.common-tag {
    margin-right: 10px;
}
.user-tag {
    position: absolute;
    margin-top: 20px;
    width: 800px;
}
.name {
    max-width: 900px;
    overflow: hidden;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
    text-overflow: ellipsis;
    white-space: normal;
    vertical-align: middle;
    color: white;
    margin-bottom: 10px;
}
.user-brief {
    max-width: 900px;
    font-size: 14px;
    font-weight: 500;
    vertical-align: middle;
    color: white;
}
.username {
    display: inline-block;
    vertical-align: middle;
    padding-left: 10px;
    font-size: 18px;
    color: rgba(0, 0, 0);
    font-weight: 550;
    font-style: italic;
}
.userInfo {
    position: absolute;
    top: 30px;
    left: 20px;
}
.user-headImg {
    width: 80px;
    height: 80px;
    border-radius: 50%;
    vertical-align: middle;
}
.header {
    position: relative;
    height: 200px;
}
.background {
    position: absolute;
    width: 100%;
    height: 200px;
    background-repeat: no-repeat;
}
.UserHome-container {
    width: 1200px;
    margin: 20px auto;
    vertical-align: middle;
    animation: slide-out 0.4s linear;
}
</style>