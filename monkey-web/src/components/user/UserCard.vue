<template>
    <div class = "UserCard-container" style="width: 100%; height: 100%;">
        <el-card class="hover-border box-card" v-for="user in userList" :key="user.id" style="margin-top: 5px;background-color: #FFFFFF;">
            <el-row>
                <el-col :span="2">
                    <img style="width: 60px; 
                    height: 60px; 
                    border-radius: 50%;
                    cursor: pointer;"
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
                        vue.$emit("getListByUserId", vue.$props.userId);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
                error() {
                    vue.$modal.msgError("认证失败，无法访问系统资源。");
                }
            })  
        }
    }
}
</script> 

<style scoped>
.box-card {
    border-radius: 20px;
    margin-top: 10px;
    border-radius: 2px;
    margin-bottom: 20px;
    border: 1px solid #dcdfe6;
    -webkit-box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
    box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
    }
.hover-border:hover {
    box-shadow: 0 0 5px 5px lightblue;
}
</style>