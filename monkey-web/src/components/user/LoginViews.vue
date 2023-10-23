<template>
    <div 
    class="login-contrainer" 
    style="position: fixed;">
    <el-card style="width: 400px;">
            <span class="span">
            <span @click="closeLogin(false)" class="el-icon-close close"></span>
        </span>
            <el-form ref="userInformation" :model="userInformation" :rules="rules">
                <el-form-item prop="username" label="用户名" v-if="!username_or_email">
                <el-input v-model="userInformation.username" placeholder="请输入用户名"></el-input>
                </el-form-item>
                <el-form-item prop="email" label="QQ邮箱" v-else>
                <el-input v-model="userInformation.email" placeholder="请输入QQ邮箱"></el-input>
                </el-form-item>
                
                <el-form-item v-if="!username_or_email" prop="password" label="密码">
                <el-input v-model="userInformation.password" placeholder="请输入密码" type="password"></el-input>
                </el-form-item>
                <el-form-item v-else>
                <el-row :gutter="20">
                    <el-col :span="16">
                    <el-input v-model="userInformation.verifyCode" placeholder="请输入验证码"></el-input>
                    </el-col>
                    <el-col :span="8">
                    <el-button @click="resendVerifyCode" :disabled="resendCodeDisabled">{{ resendCodeText }}</el-button>
                    </el-col>
                </el-row>
                </el-form-item>
                <el-form-item prop="verifyCode" v-if="!username_or_email">
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
                <span 
                v-if="!username_or_email" 
                @click="username_or_email = true" 
                class="hover" >忘记用户名？使用QQ邮箱登录</span>
            <span 
                v-else
                @click="username_or_email = false" 
                class="hover" >返回用户名登录</span>
    </el-card>
    </div>
</template>

<script>
import store from '@/store';
import $ from "jquery"

export default {
    name: 'LoginView',
    data() {
        return {
            userRegisterUrl: "http://localhost:80/monkey-user/user",
            resendCodeText: '获取验证码',
            // false 为使用用户名登录，true为使用邮箱登录
            username_or_email: false,
            userLoginrUrl: "http://localhost:80/user",
            userInformation: {
                username: "",
                password: "",
                verifyCode: "",
                email: "",
            },
            rules: {
                username: [
                    { required: true, message: '请输入用户名', trigger: 'blur' },
                    { min: 0, max: 30, message: '长度在 0 到 30 个字符', trigger: 'blur' }

                ],
                password: [
                    { required: true, message: '请输入密码', trigger: 'blur' },
                    
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
    computed: {
        isValidEmail() {
            const emailRegex = new RegExp("^[a-zA-Z0-9._-]+@[qQ][qQ]\\.com$");
            return emailRegex.test(this.userInformation.email);
        }
    },
    mounted() {
        //注意 这里的src对应的后端Controller路径
        document.getElementById("code").src = "http://localhost:80/monkey-user/user/getCaptcha?time=" + new Date().getTime();
    },

    methods: {
        // 发送验证码给对方QQ邮箱
        sendVerifyCode() {
            const vue = this;
            $.ajax({
                url: vue.userRegisterUrl + "/sendVerfyCode",
                type: "post",
                data: {
                    targetEmail: vue.userInformation.email,
                    isRegister: "true",
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
        },
        resendVerifyCode() {
            // 如果邮箱验证失败
            if (!this.isValidEmail) {
                this.$modal.msgError("请输入正确的邮箱");
                return false;
            }
            this.sendVerifyCode();
            this.resendCodeDisabled = true;
            let countDown = 60;
            const timer = setInterval(() => {
                countDown--;
                this.resendCodeText = `${countDown}s后重试`;
                if (countDown === 0) {
                clearInterval(timer);
                this.resendCodeText = '获取验证码';
                this.resendCodeDisabled = false;
                }
            }, 1000)
        },
        refresh() {
        //注意 这里的src对应的后端Controller路径
        document.getElementById("code").src="http://localhost:80/monkey-user/user/getCaptcha?time="+new Date().getTime();
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
                    if (!vue.username_or_email) {
                        store.dispatch("loginUsername", {
                        username: vue.userInformation.username,
                        password: vue.userInformation.password,
                        verifyCode: vue.userInformation.verifyCode,
                        vue: vue,
                        success() {
                            store.dispatch("getUserInfoBytoken", {
                                success(response) {  
                                    if (response.code == vue.ResultStatus.SUCCESS) {
                                        vue.$modal.msgSuccess("登录成功");
                                        vue.$emit("login", false);
                                    } else {
                                        vue.$modal.msgError(response.msg);
                                    }
                                },
                            })
                            },
                    
                    })
                    } else {
                        store.dispatch("loginEmail", {
                        email: vue.userInformation.email,
                        verifyCode: vue.userInformation.verifyCode,
                        vue: vue,
                        success() {
                            store.dispatch("getUserInfoBytoken", {
                                success(response) {  
                                    if (response.code == vue.ResultStatus.SUCCESS) {
                                        vue.$modal.msgSuccess("登录成功");
                                        vue.$emit("login", false);
                                    } else {
                                            vue.$modal.msgError(response.msg);
                                        }
                                },

                            })
                        },       
                    })
                    }
                
                } else {
                    return false;
                }
            });
        }
    },
}
</script>

<style scoped>
.span {
    text-align: right; 
    color: black; 
    padding-left: 350px;
}
.login-contrainer {
    background-color: rgba(0, 0, 0, 0.5);
    z-index: 20000;
    width: 100%;
    height: 100%;
    padding-top: 1100px;
    padding-left: 1000px;
}

.hover:hover {
    cursor: pointer;
    color: blue;
}
.hover {
    text-align: right;
    margin-left: 140px;
}

.close:hover {
    cursor: pointer;
}

</style>