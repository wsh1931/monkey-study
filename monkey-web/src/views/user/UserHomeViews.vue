<template>
    <div class="UserHome-container last-wrapper" >
        <el-container>
            <el-header class="radial-gradient" style="background-color: #FFFFFF; height: 210px;">
                <el-row>
                    <el-col :span="4">
                        <img width="180px"
                        height="180px"
                        style="border-radius: 50%; margin-top: 10px;"
                        :src="userInformation.photo" alt="">
                    </el-col>
                    <el-col :span="20" style="margin-top: 10px;">
                        <el-row style="margin-top: 10px;">
                            <el-col  :span="16" style="color: rgba(0, 0, 0, 0.5);">用户名：{{ userInformation.username }}</el-col>
                            <el-button v-if="$store.state.user.id == userId" style="margin-left: 50px;" @click="dialogFormVisible = true" round icon="el-icon-edit">编辑资料</el-button>
                            <el-button v-if="$store.state.user.id == userId" :span="3" round icon="el-icon-s-management">管理博客</el-button>
                            <!-- 编辑资料弹框 -->
                            <UpdateUserInfoDialog
                            @dialogVisible="dialogVisible"
                            :dialogFormVisible="dialogFormVisible"
                            :formLabelWidth="formLabelWidth"
                            @updateForm="updateForm"
                            :userForm="userInformation"/>
                        </el-row > 
                        <el-row style="color: rgba(0, 0, 0, 0.5);">
                            注册时间：{{ userInformation.registerTime }}
                        </el-row>
                        
                        <el-row class="database">
                            学校/工作单位: {{ userInformation.jobUnit }}
                        </el-row>
                        <el-row class="database">
                            职业: {{ userInformation.job }}
                        </el-row>
                        <!-- todo 填写IP -->
                        <el-row class="database">
                            IP所在地: 江西省
                        </el-row>
                        <el-row class="database">
                            个人简介: {{ userInformation.brief }}
                        </el-row>
                       
                    </el-col>
                </el-row>
            </el-header >
            <el-container style="margin-top: 10px;">
                <el-aside class="gradient"  width="300px" style="background-color: #FFFFFF;">
                    <el-row style="text-align: center; margin-top: 10px;">
                        <el-col :span="8" class="hover">
                            <div @click="toConcernList('concern', userId)">
                                <el-row class="more-hide-one">
                                {{ userInformation.concern }}
                                </el-row>
                                <el-row>
                                    关注
                                </el-row>
                            </div>
                        </el-col>
                        <el-col :span="8" class="hover">
                            <div @click="toFansList('fans', userId)">
                                <el-row class="more-hide-one">
                               {{ userInformation.fans }}
                            </el-row>
                            <el-row>
                                粉丝
                            </el-row>
                            </div>
                            
                        </el-col>
                        <el-col :span="8">
                            <el-row>
                                {{ userInformation.visit }}
                            </el-row>
                            <el-row>
                                访问量
                            </el-row>
                        </el-col>
                    </el-row>
                    <el-row style="margin-top: 10px; margin-left: 6px;">
                        <el-col :span="12" v-if="userInformation.isFans == '0'">
                            <el-button 
                            type="primary hover-button" 
                            icon="el-icon-plus" 
                            @click="likeAuthor(userInformation.id)" 
                            style="width: 140px;" 
                            round>
                            关注</el-button>
                        </el-col>
                        <el-col :span="12" v-else>
                            <el-button type="danger hover-button" 
                            icon="el-icon-delete"  
                            @click="likeAuthor(userInformation.id)" 
                            style="width: 140px;" 
                            round>
                            取消关注</el-button>
                        </el-col>
                        <el-col :span="12">
                            <el-button @click="WebSocketChat(userInformation.id)"  
                            type="success hover-button" 
                            style="width: 140px;" 
                            round
                            icon="el-icon-chat-line-round"
                            >私信</el-button>
                        </el-col>
                    </el-row>
                    <el-divider></el-divider>
                    <el-row style="text-align: center; margin-bottom: 10px;">最近来访</el-row>
                    <el-row>
                        <el-row style="display: flex; flex-wrap: wrap;">
                            <span style="margin-left: 5.8px; width: 68px;" 
                            class="hover-recently"
                            v-for="visitUser in visitUserList" :key="visitUser.id">
                                <el-tooltip class="item" effect="dark" :content="visitUser.visitTime | formatDate" placement="top-start">
                                <el-row>
                                    <img width="67px" 
                                    style="height: 68px;" 
                                    @click="ClickImgToPerson(visitUser.visitId)"
                                    :src="visitUser.visitUserPhoto" alt="">
                                    <el-row style="text-align: center;" class="more-hide-two">
                                    {{ visitUser.visitUsername }}
                                    </el-row>   
                                </el-row>
                               
                            </el-tooltip> 
                            </span>
                    </el-row>
                </el-row>
                <el-row style="background-color: #EEEEEE; height: 10px;"></el-row>
                <el-row>
                    <!-- 点击跳到分类所对应的文章页面 -->
                    <el-row style="padding: 5px;">TA的分类</el-row>
                    <el-row style="background-color: #EEEEEE; height: 1px;"></el-row>
                    <el-row>
                        <!-- todo 实现文章/分类跳转 -->
                        <el-row style="padding: 5px;" 
                        v-for="articleClassfication in articleClassficationList" :key="articleClassfication.id">
                            <div>
                                <el-col :span="22">{{ articleClassfication.labelName }}</el-col>
                                <el-col :span="2">{{ articleClassfication.articleCount }}</el-col>
                            </div>
                        </el-row>
                    </el-row>
                </el-row>
                <el-row style="background-color: #EEEEEE; height: 10px;"></el-row>
                <el-row style="padding: 10px;">
                    <el-row class="achieve">个人成就</el-row>
                    <el-row class="divider"></el-row>
                    <el-row class="achieve">获得 {{ userInformation.likeSum }} 次点赞</el-row>
                    <el-row class="achieve">内容获得 {{ userInformation.commentSum }} 次评论</el-row>
                    <el-row class="achieve">获得 {{ userInformation.userCollect }} 次收藏</el-row>
                </el-row>
                <el-row style="background-color: #EEEEEE; height: 10px;"></el-row>
                </el-aside>


                <el-main  style="background-color: #FFFFFF; margin-left: 10px;">
                    <el-tabs  v-model="tabName" >
                        <el-tab-pane :label="`文章 ${userInformation.articleSum == 0 ? '' : userInformation.articleSum}`" style="padding: 5px;" name="article">
                            <ArticleCard 
                            style="margin-top: 5px; padding: 5px;"
                                @pagination="getArticleListByUserId"
                                :articleInformation="articleInformationList" 
                                :labelId="labelId"/>
                            </el-tab-pane>
                            <el-tab-pane label="社区" name="community"></el-tab-pane>
                        <el-tab-pane label="动态" name="trends">动态</el-tab-pane>
                        <el-tab-pane style="padding: 5px;" :label="`提问 ${userInformation.questionSum == 0 ? '' : userInformation.questionSum}`" name="question">
                            <QuestionCard
                            :questionList="questionList"/>

                        </el-tab-pane>
                        <el-tab-pane label="音乐" name="music">音乐</el-tab-pane>
                        <el-tab-pane label="课程" name="course">课程</el-tab-pane>
                        <el-tab-pane :label="`粉丝 ${userInformation.fans == 0 ? '' : userInformation.fans}`" name="fans" style="padding: 5px;">
                            <UserCard
                            :userList="fansList"
                            @ClickImgToPerson="ClickImgToPerson"
                            @likeAuthor="likeAuthor"
                            @getListByUserId="getFansListByUserId"
                            :userId="userId"/>
                        </el-tab-pane>
                        <el-tab-pane style="padding: 5px;" :label="`关注 ${userInformation.concern == 0 ? '' : userInformation.concern}`" name="concern">
                            <UserCard
                            :userList="concernList"
                            @ClickImgToPerson="ClickImgToPerson"
                            @likeAuthor="likeAuthor"
                            @getListByUserId="getConcernListByUserId"
                            :userId="userId"/>
                        </el-tab-pane>
                        <el-tab-pane style="padding: 5px;" label="收藏" name="collect">
                            <ArticleCard 
                                :articleInformation="collectArticleList" 
                                :labelId="labelId"/>
                        </el-tab-pane>
                        <PagiNation :totals="totals" 
                            :currentPage="currentPage" 
                            :pageSize="pageSize" 
                            @handleCurrentChange = "handleCurrentChange"
                            @handleSizeChange="handleSizeChange"/>
                    </el-tabs>
                </el-main>
            </el-container>
        </el-container>
    </div>
</template>

<script>
import $ from "jquery"
import store from "@/store";
import ArticleCard from "@/components/article/ArticleCard.vue";
import PagiNation from "@/components/pagination/PagiNation.vue";
import UserCard from "@/components/user/UserCard.vue";
import UpdateUserInfoDialog from "@/components/user/UpdateUserInfoDialog.vue";
import QuestionCard from "@/components/question/QuestionCard.vue";

export default {
    name: "UserHome",
    components: {
        ArticleCard,
        PagiNation,
        UserCard,
        UpdateUserInfoDialog,
        QuestionCard
    },
    data() {
        return {
            userCenterHomeUrl: "http://localhost:80/monkey-user/user/center/home",
            checkArticleUrl: "http://localhost:80/monkey-article/check",
            // 编辑用户资料
            userForm: {},
            // 用户信息
            userInformation: [],
            // 当前用户id
            userId: "",
            // 最近访客用户信息
            visitUserList: [],
            // 文章分类列表
            articleClassficationList: [],
            tabName: "article",
            labelId: -1,
            currentPage: 1,
            pageSize: 10,
            totals: 0,
            // 文章列表信息
            articleInformationList: [],
            // 粉丝信息列表
            fansList: [],
            // 关注信息列表
            concernList: [],
            // 用户收藏文章列表
            collectArticleList: [],
            // 修改资料弹框
            dialogFormVisible: false,
            
            // 问答列表
            questionList: [],
            // 弹框标签长度
            formLabelWidth: '80px',
        }
    },

    created() {
        this.userId = this.$route.params.userId;
        this.getUserInformationByUserId(this.userId);
        this.getRecentlyUserInfoByUserId(this.userId);
        this.getUserArticleClassficationCountByuserId(this.userId);
        this.getArticleListByUserId(this.labelId);
    },
    // 解决路由路径不变，路由参数改变，路由界面不加载问题
    watch: {
        $route() {
            // 手动触发数据刷新
            this.userId = this.$route.params.userId;
            this.getUserInformationByUserId(this.userId);
            this.getRecentlyUserInfoByUserId(this.userId);
            this.getArticleListByUserId(this.labelId);
            this.getFansListByUserId(this.userId);
            this.getConcernListByUserId(this.userId);
        },

            tabName(val) {
                this.currentPage = 1;
                this.totals = 0;
                this.pageSize = 10;
                if (val == 'article') { // 文章
                    this.getArticleListByUserId(this.labelId);
                } else if (val == 'community') {
                    console.log(val)
                } else if (val == 'music') { // 音乐
                    console.log(val)
                } else if (val == 'trends') { // 动态
                    
                }  else if (val == 'question') { // 提问
                    this.getQuestionListByUserId(this.userId);
                } else if (val == 'course') { // 课程

                } else if (val == 'fans') { // 粉丝
                    this.getFansListByUserId(this.userId);
                } else if (val == 'concern') { // 关注
                    this.getConcernListByUserId(this.userId);
                } else if (val == 'collect') { // 收藏
                }
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

    methods: {
        // 通过用户id得到文章提问列表
        getQuestionListByUserId(userId) {
            const vue = this;
            $.ajax({
                url: vue.userCenterHomeUrl + "/getQuestionListByUserId",
                type: "get",
                data: {
                    userId,
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize
                },
                success(response) {
                    if (response.code == '200') {
                        vue.questionList = response.data.records;
                        vue.totals = response.data.total;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
        },
        // 提交编辑资料之后更新用户信息
        updateForm(userInformation) {
            const vue = this;
            $.ajax({
                url: vue.userCenterHomeUrl + "/updateInformation",
                type: "put",
                headers: {
                    Authorization: "Bearer " + store.state.user.token
                },
                data: {
                    userInformation: JSON.stringify(userInformation)
                },
                success(response) {
                    if (response.code == '200') {
                        vue.$modal.msgSuccess(response.msg);
                        vue.getUserInformationByUserId(vue.userId);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
                
            })
        },
        // 更新用户图片上传之后的信息
        onUploadSuccess(data) {
            this.userForm.photo = data;
        },
        onUploadRemove(data) {
            this.userForm.photo = data;
        },
        // 改变编辑资料弹窗状态
        dialogVisible(status) {
            this.dialogFormVisible = status;
        },
        // 点击关注跳转到关注列表
        toConcernList(tabName, userId) {
            this.tabName = tabName;
            this.getConcernListByUserId(userId)
        },
        // 通过用户id得到关注列表
        getConcernListByUserId(userId) {
            const vue = this;
            $.ajax({
                url: vue.userCenterHomeUrl + "/getConcernListByUserId",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    userId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token
                },
                success(response) {
                    if (response.code == '200') {
                        vue.totals = response.data.total
                        vue.concernList = response.data.userList
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
        },   
        // 点击粉丝跳转到关注列表
        toFansList(tabName, userId) {
            this.tabName = tabName;
            this.getFansListByUserId(userId)
        },
        // 通过用户id得到用户粉丝列表
        getFansListByUserId(userId) {
            const vue = this;
            $.ajax({
                url: vue.userCenterHomeUrl + "/getFansListByUserId",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    userId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token
                },
                success(response) {
                    if (response.code == '200') {
                        vue.fansList = response.data.userList;
                        vue.totals = response.data.total
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
        },
        handleSizeChange(val) {
            this.pageSize = val;
            if (this.tabName == 'article') {
                this.getArticleListByUserId(this.labelId)
            } else if (this.tabName == 'fans') {
                this.getFansListByUserId(this.userId);
            } else if (this.tabName == 'concern') {
                this.getConcernListByUserId(this.userId);
            } else if (this.tabName == 'question') {
                this.getQuestionListByUserId(this.userId)
            }
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            if (this.tabName == 'article') {
                this.getArticleListByUserId(this.labelId)
            } else if (this.tabName == 'fans') {
                this.getFansListByUserId(this.userId);
            } else if (this.tabName == 'concern') {
                this.getConcernListByUserId(this.userId);
            } else if (this.tabName == 'question') {
                this.getQuestionListByUserId(this.userId)
            }
        },
        // 通过用户id得到文章列表
        getArticleListByUserId(labelId) {
        const vue = this;
        vue.labelId = labelId;
        vue.tabName = 'article'
            $.ajax({
            url: vue.userCenterHomeUrl + "/getArticleListByUserId",
                type: "get",
                headers: {
                Authorization: "Bearer " + store.state.user.token
            },
            data: {
                currentPage: vue.currentPage,
                pageSize: vue.pageSize,
                labelId,
            },
            success(response) {
                if (response.code == "200") {
                    if (response.data != null) {
                        vue.articleInformationList = response.data.records
                        vue.totals = response.data.total;
                    } else {
                        vue.articleInformation = [];
                        vue.totals = 0;
                        vue.$modal.msgError(response.msg);
                    }
                }
            },
        })
        
    },
        // 点击最近访问用户跳掉到个人主页
        ClickImgToPerson(userId) {
            // 跳转之前该用户最近游览信息加入作者主页
            const vue = this;
            $.ajax({
                url: vue.userCenterHomeUrl + "/recentlyView",
                type: "post",
                data: {
                    userId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token
                },
                success(response) {
                    if (response.code == '200') {
                        vue.$router.push({
                        name: "user_home",
                        params: {
                            userId
                        }
                    })
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
        },
        // 跳转到聊天界面
        WebSocketChat(receiverId) {
            this.$router.push({
                name: "webSocket_chat",
                params: {
                    receiverId
                },
                replace: true
            })
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
                        vue.getUserInformationByUserId(vue.userId);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })            
        },
        // 通过用户id得到用户所发表的所有文章分类数
        getUserArticleClassficationCountByuserId(userId) {
            const vue = this;
            $.ajax({
                url: vue.userCenterHomeUrl + "/getUserArticleClassficationCountByuserId",
                type: "get",
                data: {
                    userId,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.articleClassficationList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
        },
        // 通过用户id得到最近来访用户信息
        getRecentlyUserInfoByUserId(userId) {
            const vue = this;
            $.ajax({
                url: vue.userCenterHomeUrl + "/getRecentlyUserInfoByUserId",
                type: "get",
                data: {
                    userId
                },
                
                success(response) {
                    if (response.code == '200') {
                        vue.visitUserList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
        },
        // 通过用户id查询用户信息
        getUserInformationByUserId(userId) {
            const vue = this;
                $.ajax({
                url: vue.userCenterHomeUrl + "/getUserInformationByUserId",
                    type: "get",
                    headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    userId,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.userInformation = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
        }
    }
}
</script>

<style scoped>
.database {
    margin-top: 10px; 
    color: rgba(0, 0, 0, 0.5);
}
.divider {
    background-color: #EEEEEE; 
    height: 1px; 
    margin-top: 10px;
    margin-left: 5px;
}
.achieve {
    margin-top: 10px;
    margin-left: 5px;
}
.UserHome-container {
    display: flex; 
    justify-content: center;
    align-items: center; 
    width: 1150px; 
    margin: 20px auto;
}
/* .gradient {
    background-image: linear-gradient(to top, #d5d4d0 0%, #d5d4d0 1%, #eeeeec 31%, #efeeec 75%, #e9e9e7 100%);
}
.radial-gradient {
   background-image: linear-gradient(to top, #d5d4d0 0%, #d5d4d0 1%, #eeeeec 31%, #efeeec 75%, #e9e9e7 100%);
} */
 .last-wrapper {
    position: relative;
    z-index: 1;
  }

  .first-wrapper {
    position: relative;
    z-index: 200;
  }

.hover-button:hover {
    transition: 0.5s ease;
    transform: scale(1.07) translate3d(0,0,0);
}
.article-classification:hover {
    color: #409EFF;
    cursor: pointer;
}
.hover-border:hover {
    box-shadow: 0 0 5px 3px lightblue;
}
.hover:hover {
    color: #00A1D6;
    cursor: pointer;
}
.hover-recently:hover {
    transition: 0.5s ease;
    transform: scale(1.02);
    cursor: pointer;
}

.hover-image {
    transition: 0.5s ease;
    transform: scale(1.2);
}
.more-hide-one {
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
}
.more-hide-two {
    white-space: nowrap;
    overflow: hidden;
}
</style>