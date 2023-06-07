<template>
    <div class="UserHome-container" 
    style="display: flex; 
    justify-content: center;
    align-items: center; 
    width: 1150px; 
    padding-left: 150px;
    margin-top: 20px;">
        <el-container>
            <el-header style="background-color: lightblue; height: 200px;">
                <el-row>
                    <el-col :span="4">
                        <img width="180px"
                        height="180px"
                        style="border-radius: 50%; margin-top: 10px;"
                        src="https://cdn.acwing.com/media/user/profile/photo/149142_lg_2578f216db.jpg" alt="">
                    </el-col>
                    <el-col :span="20">
                        <el-row>
                            用户名
                        </el-row>
                        <el-row>
                            注册时间
                        </el-row>
                        <el-row>
                            个人简介
                        </el-row>
                    </el-col>
                </el-row>
            </el-header>
            <el-container>
                <el-aside style="background-color: lightgreen;" width="300px">Aside</el-aside>
                <el-main style="background-color: lightcoral;">Main</el-main>
            </el-container>
        </el-container>
    </div>
</template>

<script>
import $ from "jquery"
import store from '@/store';

export default {
    name: "UserHome",
    data() {
        return {
            // 用户信息
            userInformation: [],
            // 当前用户id
            userId: "",
        }
    },

    created() {
        this.userId = this.$route.params.userId;
        // this.getUserInformationByUserId(this.userId);
    },

    methods: {
        // 通过用户id查询用户信息
        getUserInformationByUserId(userId) {
            const vue = this;
            $.ajax({
                url: "http://localhost:4000/user/center/home/getUserInformationByUserId",
                type: "get",
                data: {
                    userId
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,  
                },
                success(response) {
                    if (response.code == '10000') {
                        console.log(response);
                    } else {
                        vue.$modal.msgError("发送未知错误");
                    }
                },
                error() {
                    vue.$modal.msgError("认证失败，无法访问系统资源"); 
                }
            })
        }
    }
}
</script>

<style scoped>

</style>