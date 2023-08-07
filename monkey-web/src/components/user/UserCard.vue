<template>
    <div class = "UserCard-container">
        <el-card class="hover-border box-card show-card" v-for="user in userList" :key="user.id" style="margin-top: 5px;background-color: #FFFFFF;">
            <el-row>
                <el-col :span="2" style="overflow: hidden; border-radius: 50%;">
                    <img class="img"
                    @click="toUserHome(user.id)" :src="user.photo" alt="">
                </el-col>
                <el-col :span="22">
                    <el-row>
                        <el-col :span="21">
                            {{ user.username }}
                        </el-col>
                        <el-col :span="3">
                            <el-button v-if="user.isFans == '0'" 
                            round 
                            icon="el-icon-plus" 
                            size="small"
                            @click="likeUser(user.id)">关注</el-button>
                            <el-button v-else
                            round 
                            icon="el-icon-user-solid" 
                            size="small" 
                            style="color: rgba(0, 0, 0, 0.5);"
                            @click="likeUser(user.id)">已关注</el-button>
                        </el-col>
                    </el-row>
                    <el-row>
                        {{ user.brief }}
                    </el-row>
                </el-col>
            </el-row>
        </el-card>
    </div>    
</template>

<script>
import $ from "jquery"
import store from "@/store";
export default {
    name: "UserCard",
    data() {
        return {
            checkArticleUrl: "http://localhost:80/monkey-article/check",
        }
    },
    props: {
        userList: Array,
        userId: String
    },
    methods: {
        // 点击粉丝头像跳转到用户首页
        toUserHome(userId) {
            this.$emit("ClickImgToPerson", userId);
        },
        likeUser(userId) {
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
                        vue.$emit("getListByUserId", vue.$props.userId);
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
.img {
    width: 60px; 
    height: 60px; 
    border-radius: 50%;
    cursor: pointer;
}
.UserCard-container {
    width: 100%;
    height: 100%;
}
.show-card {
    animation: show-card 0.6s linear;
}

@keyframes show-card {
    0% {
        opacity: 0;
        transform: translateY(-100px);
    }
    60% {
        opacity: 1;
        transform: translateY(30px);
    }
    80% {
        transform: -10px;
    }
    100% {
        opacity: translateX(0);
    }
}
.box-card {
    border-radius: 20px;
    margin-top: 10px;
    border-radius: 2px;
    margin-bottom: 20px;
    border: 1px solid #dcdfe6;
    -webkit-box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
    box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
    background-image: linear-gradient(45deg, #93a5cf 0%, #e4efe9 100%);
    }
.hover-border:hover {
    box-shadow: 0 0 20px 8px #409EFF;
    position: relative;
    top: -2px;
    transition: 0.4s linear all; 
}
img {
    transition: 0.4s linear all;
}

.hover-border:hover img {
    transform: scale(1.2);
}
</style>