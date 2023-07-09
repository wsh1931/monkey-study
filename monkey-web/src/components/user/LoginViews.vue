<template>
    <div 
    class="login-contrainer" 
    style="
    background-color: rgba(0, 0, 0, 0.5);
    position: fixed;
    width: 100%;
    height: 100%;
    padding-top: 1100px;
    padding-left: 1000px;">
    <el-card style="width: 400px;">
            <span 
            style="text-align: right; 
            color: black; 
            padding-left: 350px;">
            <span @click="closeLogin(false)" class="el-icon-close close"></span>
        </span>
            <el-form ref="userInformation" :model="userInformation" :rules="rules">
                <el-form-item prop="username" label="用户名">
                <el-input v-model="userInformation.username" placeholder="请输入用户名"></el-input>
                </el-form-item>
                <el-form-item prop="password" label="密码">
                <el-input v-model="userInformation.password" placeholder="请输入密码" type="password"></el-input>
                </el-form-item>
                <el-form-item>
                <el-row :gutter="5">
                    <el-col :span="16">
                    <el-input v-model="userInformation.verifyCode" placeholder="请输入验证码"></el-input>
                    </el-col>
                    <el-col :span="8">
                     <!--这里的src路径得看后端对应的Controller路径-->
                        <img :src="userLoginrUrl + '/getCaptcha'" 
                            id="code" 
                            height=30px 
                            width=115px 
                            style="cursor: pointer;"
                            @click="refresh()" />
                    </el-col>
                </el-row>
                </el-form-item>
                <el-form-item style="padding-left: 23px;">
                <el-button type="primary" @click="loginUser()" style="width: 150px;">登录</el-button>
                <el-button @click="registerAndCloseLogin()" type="info" style="margin-left: 10px; width: 150px;">注册</el-button>
                </el-form-item>
            </el-form>
    </el-card>
    </div>
</template>

<script>
import store from '@/store';

export default {
    name: 'LoginView',
    data() {
        return {
            userLoginrUrl: "http://localhost:4500/user",
            userInformation: {
                username: "",
                password: "",
            },
            rules: {
                username: [
                    { required: true, message: '请输入用户名', trigger: 'blur' },
                    { min: 0, max: 30, message: '长度在 0 到 30 个字符', trigger: 'blur' }

                ],
                password: [
                    { required: true, message: '请输入密码', trigger: 'blur' },
                    
                ],
                confirmPassword: [
                    { required: true, message: '请确认密码', trigger: 'blur' },
                    { min: 0, max: 20, message: '长度在 0 到 20 个字符', trigger: 'blur' },
                    { validator: this.validatePasswordConfirm, trigger: 'blur' }
                ],
                email: [
                    { required: true, message: '请输入QQ邮箱', trigger: 'blur' },
                    { type: 'email', message: '请输入有效的邮箱地址', trigger: ['blur', 'change'] }
                ],
                verifyCode: [
                    { required: true, message: '请输入验证码', trigger: 'blur' }
                ]
            },
            
        }
    },

    methods: {
        refresh() {
        //注意 这里的src对应的后端Controller路径
        document.getElementById("code").src="http://localhost:4500/user/getCaptcha?time="+new Date().getTime();
        },
        registerAndCloseLogin() {
            this.$emit("registerAndCloseLogin");
        },
        closeLogin(status) {
            this.$emit("login", status);
        },
        loginUser() {
            const vue = this;
            this.$refs["userInformation"].validate((valid) => {
                if (valid) {
                store.dispatch("login", {
                    username: this.userInformation.username,
                    password: this.userInformation.password,
                    success() {
                        store.dispatch("getUserInfoBytoken", {
                            success(response) {  
                                if (response.code == '10000') {
                                    vue.$modal.msgSuccess("登录成功");
                                    vue.$emit("login", false);
                                } else {
                                    vue.$modal.msgError("用户名或密码错误")
                                }
                            },
                            error() {
                                vue.$modal.msgError("登录失败");
                            }
                        })
                    },
                    error() {
                        vue.$modal.msgError("用户名或密码错误，请重新输入")
                    }
                
                })
                } else {
                    return false;
                }
            });
        }
    },
}
</script>

<style scoped>

.close:hover {
    cursor: pointer;
}

</style>