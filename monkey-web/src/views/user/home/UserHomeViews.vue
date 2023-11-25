<template>
    <div class="UserHome-container">
        <div class="header">
            <img class="background" src="https://pic.netbian.com/uploads/allimg/220905/232801-166239168154a8.jpg" alt="">
            <div class="userInfo">
                <img class="user-headImg" src="https://cdn.luogu.com.cn/upload/usericon/657442.png" alt="">
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
                <el-tab-pane label="课程" name="course">
                    <span slot="label">
                        <i class="iconfont icon-kecheng-">&nbsp;</i>
                        课程</span>
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
                <el-col :span="18">
                    <router-view></router-view>
                </el-col>
                <el-col :span="6">
                    
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
            userHomeUrl: "http://localhost/monkey-user/user/home",
            esUserUrl: "http://localhost/monkey-search/user/home"
        }
    },
    filters: {
        formatDate: value => {
            if (!value) return '';
            // 转换成 Date 对象
            const date = new Date(value);
            // 格式化输出
            const year = date.getFullYear();
            const month = ('0' + (date.getMonth() + 1)).slice(-2);
            const day = ('0' + date.getDate()).slice(-2);
            return `${year}-${month}-${day}`;
        }
    },
    created() {
        this.userId = this.$route.params.userId;
        this.initChildrenRouter();
        this.queryUserAchievement(this.userId);
        this.queryUserInfo(this.userId);
    },


    methods: {
        // 查询用户成就
        queryUserAchievement(userId) {
            const vue = this;
            $.ajax({
                url: vue.esUserUrl + "/queryUserAchievement",
                type: "get",
                data: {
                    userId,
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
    widows: 80px;
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
}
</style>