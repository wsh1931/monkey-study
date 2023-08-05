<template>
    <div class="UserInfoCard">
        <el-card class="el-card">
                <el-row >
                    <el-col :span="6">
                        <img
                        class="hover img"
                        :src="userInformation.photo"
                        @click="toUserHome(userInformation.id)">
                    </el-col>
                    <el-col :span="18" >
                        <el-row class="username">
                            {{ userInformation.username }}
                        </el-row>
                        <el-row style="color: gray;">
                            {{ userInformation.brief }}
                        </el-row>
                        
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="4">
                        <el-row >
                            {{ userInformation.visit }}
                        </el-row>
                        <el-row>
                            游览    
                        </el-row>
                    </el-col>
                    <el-col :span="4">
                        <el-row>
                            {{ userInformation.fans }}
                        </el-row>
                        <el-row>
                            粉丝
                        </el-row>
                    </el-col>
                    <el-col :span="4">
                        <el-row>
                            {{ userInformation.concern }}
                        </el-row>
                        <el-row class="hover">
                            关注
                        </el-row>
                    </el-col>
                    <el-col :span="4">
                        <el-row>
                            {{ userInformation.articleSum }}
                        </el-row>
                        <el-row>
                            文章
                        </el-row>
                    </el-col>
                    <el-col :span="4">
                        <el-row>
                            {{ userInformation.userCollect }}
                        </el-row>
                        <el-row>
                            收藏
                        </el-row>
                    </el-col>
                    <el-col :span="4">
                        <el-row>
                            {{ userInformation.likeSum }}
                        </el-row>
                        <el-row>
                            点赞
                        </el-row>
                    </el-col>
                </el-row>
                <el-row style="margin-top: 5px;">
                    <el-col :span="11">
                        <el-button v-if="userInformation.isFans == '0'"
                            @click="likeAuthor(userInformation.id)"
                            icon="el-icon-plus"
                            round style="width: 100%;" 
                            type="primary"
                            class="hover">关注
                        </el-button>
                        <el-button v-else @click="likeAuthor(userInformation.id)" 
                        icon="el-icon-delete" 
                        round 
                        style="width: 100%;" 
                        type="danger"
                        class="hover">取消关注</el-button>
                    </el-col>
                    <!-- 聊天界面实现 -->
                    <el-col :span="11" style="margin-left: 5px;">
                        <el-button 
                        @click="WebSocketChat(userInformation.id)" 
                        round 
                        style="width: 100%;" 
                        type="success"
                        class="hover">
                        <span class="iconfont icon-pinglun" style="font-size: 15px;"></span>
                        私聊
                        </el-button>
                    </el-col>
                </el-row>
            </el-card>
    </div>
</template>

<script>
 import $ from 'jquery'
 import store from '@/store';
 export default {
    name: "UserInfoCard",
    props: ["userInformation", "articleOrQuestionId"],
    data() {
        return {
            userCenterHomeUrl: "http://localhost:80/monkey-user/user/center/home",
            checkArticleUrl: "http://localhost:80/monkey-article/check",
        }
    },
    methods: {
        // 跳转到用户主页
        toUserHome(userId) {
            // 跳转之前该用户最近游览信息加入作者主页
            const vue = this;
            $.ajax({
                url: vue.userCenterHomeUrl + "/recentlyView",
                type: "post",
                data: {
                    userId,
                    reviewId: store.state.user.id
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
                        vue.$modal.msgError("发送未知错误，查看作者主页失败")
                    }
                },
                error() {
                    vue.$modal.msgError("发送未知错误，查看作者主页失败")
                }
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
                        vue.$emit("getAuthorInfoByArticleOrQuestionId", vue.$props.articleOrQuestionId);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
                error() {
                    vue.$modal.msgError("认证失败，无法访问系统资源。");
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
    }
 }
</script>

<style scoped>
.el-card {
    position: fixed; 
    padding: 0; 
    width: 350px;
}
.username {
    font-size: 20px; 
    font-weight: 600; 
    margin-top: 10px;
}
.img {
    cursor: pointer; 
    border-radius: 50%;
    display: flex; 
    justify-content: center; 
    align-items: center;
    width: 70px;
    height: 70px;
}
.hover:hover {
    transition: 0.5s ease;
    transform: scale(1.07) translate3d(0,0,0);
}
</style>

