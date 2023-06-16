<template>
    <div class="UserHome-container" 
    style="display: flex; 
    justify-content: center;
    align-items: center; 
    width: 1150px; 
    padding-left: 150px;
    margin-top: 20px;">
        <el-container>
            <el-header style="background-color: #FFFFFF; height: 200px;">
                <el-row>
                    <el-col :span="4">
                        <img width="180px"
                        height="180px"
                        style="border-radius: 50%; margin-top: 10px;"
                        :src="userInformation.photo" alt="">
                    </el-col>
                    <el-col :span="20" style="margin-top: 10px;">
                        <el-row style="margin-top: 10px;">
                            <el-col :span="16" style="color: rgba(0, 0, 0, 0.5);">用户名：{{ userInformation.username }}</el-col>
                            <el-button style="margin-left: 50px;" @click="dialogFormVisible = true" round icon="el-icon-edit">编辑资料</el-button>
                            <el-button :span="3" round icon="el-icon-s-management">管理博客</el-button>
                            <!-- 编辑资料弹框 -->
                            <UpdateUserInfoDialog
                             @dialogVisible="dialogVisible"
                             :dialogFormVisible="dialogFormVisible"
                             :formLabelWidth="formLabelWidth"
                             @updateForm="updateForm"
                             :userForm="userInformation"/>
                        </el-row > 
                        <el-row style="margin-top: -10px; color: rgba(0, 0, 0, 0.5);">
                            注册时间：{{ userInformation.registerTime }}
                        </el-row>
                        
                        <el-row style="margin-top: 10px; color: rgba(0, 0, 0, 0.5);">
                            学校/工作单位: {{ userInformation.jobUnit }}
                        </el-row>
                        <el-row style="margin-top: 10px; color: rgba(0, 0, 0, 0.5);">
                            职业: {{ userInformation.job }}
                        </el-row>
                        <!-- todo 填写IP -->
                        <el-row style="margin-top: 10px; color: rgba(0, 0, 0, 0.5);">
                            IP所在地: 江西省
                        </el-row>
                        <el-row style="margin-top: 10px; color: rgba(0, 0, 0, 0.5);">
                            个人简介: {{ userInformation.brief }}
                        </el-row>
                       
                    </el-col>
                </el-row>
            </el-header >
            <el-container style="margin-top: 10px;">
                <el-aside  width="300px" style="background-color: #FFFFFF;">
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
                            icon="el-icon-user-solid" 
                            @click="likeAuthor(userInformation.id)" 
                            style="width: 140px;" 
                            round>
                            关注</el-button>
                        </el-col>
                        <el-col :span="12" v-else>
                            <el-button type="danger hover-button" 
                            icon="el-icon-user-solid"  
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
                        <el-row style="padding: 5px;" 
                        class="classfication"
                        v-for="articleClassfication in articleClassficationList" :key="articleClassfication.id">
                            <div @click="getArticleListByUserId(articleClassfication.id)">
                                <el-col :span="22">{{ articleClassfication.labelName }}</el-col>
                                <el-col :span="2">{{ articleClassfication.articleCount }}</el-col>
                            </div>
                        </el-row>
                    </el-row>
                </el-row>
                <el-row style="background-color: #EEEEEE; height: 10px;"></el-row>
                <el-row style="padding: 10px;">
                    <el-row style="margin-top: 10px; margin-left: 5px;">个人成就</el-row>
                    <el-row style="background-color: #EEEEEE; height: 1px; margin-top: 10px;margin-left: 5px;"></el-row>
                    <el-row style="margin-top: 10px;margin-left: 5px;">获得 {{ userInformation.likeSum }} 次点赞</el-row>
                    <el-row style="margin-top: 10px;margin-left: 5px;">内容获得 {{ userInformation.commentSum }} 次评论</el-row>
                    <el-row style="margin-top: 10px;margin-left: 5px;">获得 {{ userInformation.userCollect }} 次收藏</el-row>
                </el-row>
                <el-row style="background-color: #EEEEEE; height: 10px;"></el-row>
                </el-aside>


                <el-main  style="background-color: #FFFFFF; margin-left: 10px;">
                    <el-tabs  v-model="tabName" >
                        <el-tab-pane label="文章" style="padding: 5px;" name="article">
                            <ArticleCard 
                                @pagination="getArticleListByUserId"
                                :articleInformation="articleInformationList" 
                                :labelId="labelId"/>
                                <PagiNation :totals="totals" 
                                :currentPage="currentPage" 
                                :pageSize="pageSize" 
                                @handleCurrentChange = "handleCurrentChange"
                                @handleSizeChange="handleSizeChange"/>
                            </el-tab-pane>
                            <el-tab-pane label="社区" name="community"></el-tab-pane>
                        <el-tab-pane label="动态" name="trends">动态</el-tab-pane>
                        <el-tab-pane label="问答" name="answer">问答</el-tab-pane>
                        <el-tab-pane label="新鲜事" name="freshNews"></el-tab-pane>
                        <el-tab-pane label="学习资源" name="resource">学习资源</el-tab-pane>
                        <el-tab-pane label="粉丝" name="fans" style="padding: 5px;">
                            <UserCard
                            :userList="fansList"
                            @ClickImgToPerson="ClickImgToPerson"
                            @likeAuthor="likeAuthor"
                            @getListByUserId="getFansListByUserId"
                            :userId="userId"/>
                            <PagiNation :totals="totals" 
                                :currentPage="currentPage" 
                                :pageSize="pageSize" 
                                @handleCurrentChange = "handleCurrentChange"
                                @handleSizeChange="handleSizeChange"/>
                        </el-tab-pane>
                        <el-tab-pane style="padding: 5px;" label="关注" name="concern">
                            <UserCard
                            :userList="concernList"
                            @ClickImgToPerson="ClickImgToPerson"
                            @likeAuthor="likeAuthor"
                            @getListByUserId="getConcernListByUserId"
                            :userId="userId"/>
                            <PagiNation :totals="totals" 
                                :currentPage="currentPage" 
                                :pageSize="pageSize" 
                                @handleCurrentChange = "handleCurrentChange"
                                @handleSizeChange="handleSizeChange"/>
                        </el-tab-pane>
                        <el-tab-pane style="padding: 5px;" label="收藏" name="collect">
                            <ArticleCard 
                                @pagination="getUserCollectArticleListByUserId"
                                :articleInformation="collectArticleList" 
                                :labelId="labelId"/>
                                <PagiNation :totals="totals" 
                                :currentPage="currentPage" 
                                :pageSize="pageSize" 
                                @handleCurrentChange = "handleCurrentChange"
                                @handleSizeChange="handleSizeChange"/>
                        </el-tab-pane>
                        
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

export default {
    name: "UserHome",
    components: {
        ArticleCard,
        PagiNation,
        UserCard,
        UpdateUserInfoDialog
    },
    data() {
        return {
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
        this.getUserCollectArticleListByUserId();
        },

        tabName(val) {
            if (val == 'article') { // 文章
                this.getArticleListByUserId(this.labelId);
            } else if (val == 'community') {
                console.log(val)
            } else if (val == 'freshNews') {
                console.log(val)
            } else if (val == 'trends') { // 动态
                
            } else if (val == 'answer') { // 问答

            } else if (val == 'resource') { // 资源

            } else if (val == 'fans') { // 粉丝
                this.getFansListByUserId(this.userId);
            } else if (val == 'concern') { // 关注
                this.getConcernListByUserId(this.userId);
            } else if (val == 'collect') { // 收藏
                this.getUserCollectArticleListByUserId();
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
        // 提交编辑资料之后更新用户信息
        updateForm(userInformation) {
            const vue = this;
            $.ajax({
                url: "http://localhost:4000/user/center/home/updateInformation",
                type: "put",
                headers: {
                    Authorization: "Bearer " + store.state.user.token
                },
                data: {
                    userInformation: JSON.stringify(userInformation)
                },
                success(response) {
                    if (response.code == '10000') {
                        vue.$modal.msgSuccess("更新用户信息成功");
                        vue.getUserInformationByUserId(vue.userId);
                    } else {
                        vue.$modal.msgError("发生未知错误，编辑用户信息失败");
                    }
                },
                error() {
                    vue.$modal.msgError("认证失败，无法访问系统资源");
                }
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
        // 通过用户id得到用户收藏文章列表
        getUserCollectArticleListByUserId() {
            const vue = this;
            $.ajax({
                url: "http://localhost:4000/user/center/home/getUserCollectArticleListByUserId",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    userId: vue.userId,
                    nowUserId: store.state.user.id
                },
                success(response) { 
                    if (response.code == '10000') {
                        if (response.data != null) vue.totals = response.data.total;
                        else {
                            vue.totals = 0;
                        }
                        if (response.data != null) vue.collectArticleList = response.data.records;
                    } else {
                        vue.$modal.msgError("发生未知错误，查询用户文章列表失败");
                    }
                },
                error() {
                    vue.$modal.msgError("发生未知错误，查询用户文章列表失败");
                }
            })
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
                url: "http://localhost:4000/user/center/home/getConcernListByUserId",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    userId,
                    nowUserId: store.state.user.id
                },
                success(response) {
                    if (response.code == '10000') {
                        vue.totals = response.data.total
                        vue.concernList = response.data.userList
                    } else {
                        vue.$modal.msgError("发生未知错误，查询关注列表失败")
                    }
                },
                error() {
                    vue.$modal.msgError("发生未知错误，查询关注列表失败")
                }
            })
        },   
        // 点击粉丝跳转到关注列表
        toFansList(tabName, userId) {
            this.tabName = tabName;
            this.getArticleListByUserId(userId)
        },
        // 通过用户id得到用户粉丝列表
        getFansListByUserId(userId) {
            const vue = this;
            $.ajax({
                url: "http://localhost:4000/user/center/home/getFansListByUserId",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    userId,
                    nowUserId: store.state.user.id
                },
                success(response) {
                    if (response.code == '10000') {
                        vue.fansList = response.data.userList;
                        vue.totals = response.data.total
                    } else {
                        vue.$modal.msgError("发生未知错误，查找粉丝列表失败")
                    }
                },
                error() {
                    vue.$modal.msgError("发生未知错误，查找粉丝列表失败")
                }
            })
        },
        handleSizeChange(val) {
            this.pageSize = val;
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            if (this.tabName == 'article') {
                this.getArticleListByUserId(this.labelId)
            } else if (this.tabName == 'fans') {
                this.getFansListByUserId(this.userId);
            } else if (this.tabName == 'concern') {
                this.getConcernListByUserId(this.userId);
            } else if (this.tabName == 'collect') {
                this.getUserCollectArticleListByUserId();
            }
        },
        // 通过用户id得到文章列表
        getArticleListByUserId(labelId) {
        const vue = this;
        vue.labelId = labelId;
        vue.tabName = 'article'
        setTimeout(() => {
            $.ajax({
            url: "http://localhost:4000/user/center/home/getArticleListByUserId",
            type: "get",
            data: {
                currentPage: vue.currentPage,
                pageSize: vue.pageSize,
                labelId,
                userId: vue.userId
            },
            success(response) {
                if (response.code == "10000") {
                    if (response.data != null) {
                        vue.articleInformationList = response.data.records
                         vue.totals = response.data.total;
                    } else {
                        vue.articleInformation = [];
                        vue.totals = 0;
                    }
                } else {
                    vue.$modal.msgError("发生未知错误。");
                }
            },
            erorr() {
                vue.$modal.msgError("发生未知错误。");
            }
        })
        }, 1)
        
      },
        // 点击最近访问用户跳掉到个人主页
        ClickImgToPerson(userId) {
            // 跳转之前该用户最近游览信息加入作者主页
            const vue = this;
            $.ajax({
                url: "http://localhost:4000/user/center/home/recentlyView",
                type: "post",
                data: {
                    userId,
                    reviewId: store.state.user.id
                },
                success(response) {
                    if (response.code == '10000') {
                        vue.$router.push({
                        name: "user_home",
                        params: {
                            userId
                        }
                    })
                    } else {
                        vue.$modal.msgError("发送未知错误，查看作者主页失败")
                    }
                },
                error() {
                    vue.$modal.msgError("发送未知错误，查看作者主页失败")
                }
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
                url: "http://localhost:4000/check/article/likeAuthor",
                type: "get",
                data: {
                    userId
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token
                },
                success(response) {
                    if (response.code == "10000") {
                        vue.$modal.msgSuccess(response.msg);
                        vue.getUserInformationByUserId(vue.userId);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
                error() {
                    vue.$modal.msgError("认证失败，无法访问系统资源。");
                }
            })            
        },
        // 通过用户id得到用户所发表的所有文章分类数
        getUserArticleClassficationCountByuserId(userId) {
            const vue = this;
            $.ajax({
                url: "http://localhost:4000/user/center/home/getUserArticleClassficationCountByuserId",
                type: "get",
                data: {
                    userId,
                },
                success(response) {
                    if (response.code == '10000') {
                        vue.articleClassficationList = response.data;
                    } else {
                        vue.$modal.msgError("发生未知错误，查询用户文章分类失败");
                    }
                },
                error() {
                    vue.$modal.msgError("发生未知错误，查询用户文章分类失败");
                }
            })
        },
        // 通过用户id得到最近来访用户信息
        getRecentlyUserInfoByUserId(userId) {
            const vue = this;
            $.ajax({
                url: "http://localhost:4000/user/center/home/getRecentlyUserInfoByUserId",
                type: "get",
                data: {
                    userId
                },
                success(response) {
                    if (response.code == '10000') {
                        vue.visitUserList = response.data;
                    } else {
                        vue.$modal.msgError("发送未知错误，查询最近来访信息失败");
                    }
                },
                error() {
                    vue.$modal.msgError("发送未知错误，查询最近来访信息失败");
                }
            })
        },
        // 通过用户id查询用户信息
        getUserInformationByUserId(userId) {
            const vue = this;
            setTimeout(() => {
                $.ajax({
                url: "http://localhost:4000/user/center/home/getUserInformationByUserId",
                type: "get",
                data: {
                    userId,
                    nowUserId: store.state.user.id
                },
                success(response) {
                    if (response.code == '10000') {
                        vue.userInformation = response.data;
                    } else {
                        vue.$modal.msgError("发送未知错误");
                    }
                },
                error() {
                    vue.$modal.msgError("认证失败，无法访问系统资源"); 
                }
            })
            })
            
        }
    }
}
</script>

<style scoped>
.classfication:hover {
    cursor: pointer;
    background-color: lightgray;
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