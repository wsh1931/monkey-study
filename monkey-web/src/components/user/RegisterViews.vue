<template>
    <div class="register-contrainer" 
    style="
    background-color: rgba(0, 0, 0, 0.5);
    position: fixed;
    width: 100%;
    height: 100%;
    padding-top: 900px;
    padding-left: 1000px;">
        <el-card style="width: 400px;">
            <span 
            style="text-align: right; 
            color: black; 
            padding-left: 350px;">
            <span @click="closeRegister(false)" class="el-icon-close close"></span>
        </span>
            <el-form ref="userInformation" :model="userInformation" :rules="rules">
                <el-form-item prop="username" label="用户名">
                <el-input v-model="userInformation.username" placeholder="请输入用户名"></el-input>
                </el-form-item>
                <el-form-item prop="password" label="密码">
                <el-input v-model="userInformation.password" placeholder="请输入密码" type="password"></el-input>
                </el-form-item>
                <el-form-item prop="confirmPassword" label="确实密码">
                <el-input v-model="userInformation.confirmPassword" placeholder="请确认密码" type="password"></el-input>
                </el-form-item>
                <el-form-item prop="email" label="QQ邮箱">
                <el-input v-model="userInformation.email" placeholder="请输入QQ邮箱"></el-input>
                </el-form-item>
                <el-form-item>
                <el-row :gutter="20">
                    <el-col :span="16">
                    <el-input v-model="userInformation.verifyCode" placeholder="请输入验证码"></el-input>
                    </el-col>
                    <el-col :span="8">
                    <el-button @click="resendVerifyCode" :disabled="resendCodeDisabled">{{ resendCodeText }}</el-button>
                    </el-col>
                </el-row>
                </el-form-item>
                <el-form-item style="padding-left: 23px;">
                <el-button type="primary" @click="register()" style="width: 150px;">注册</el-button>
                <el-button @click="returnLogin()" type="info" style="margin-left: 10px; width: 150px;">返回登录</el-button>
                </el-form-item>
            </el-form>
    </el-card>
    </div>
</template>

<script>
import $ from "jquery"
export default {
    name: 'LoginView',
    data() {
        return {
            userRegisterUrl: "http://localhost:80/monkey-user/user",
            userInformation: {
                username: '',
                password: '',
                confirmPassword: '',
                email: '',
                verifyCode: ''
            },
            resendCodeText: '获取验证码',
            resendCodeDisabled: false,
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

    computed: {
        isValidEmail() {
            const emailRegex = new RegExp("^[a-zA-Z0-9._-]+@[qQ][qQ]\\.com$");
            return emailRegex.test(this.userInformation.email);
        }
    },

    methods: {
        closeRegister() {
            this.$emit("closeRegister");
        },
        validatePasswordConfirm(rule, value, callback) {
        if (value !== this.userInformation.password) {
            callback(new Error('两次输入的密码不一致'));
        } else {
            callback();
        }
        },
        // 发送验证码给对方QQ邮箱
        sendVerifyCode() {
            const vue = this;
            $.ajax({
                url: vue.userRegisterUrl + "/sendVerfyCode",
                type: "post",
                data: {
                    targetEmail: vue.userInformation.email,
                    isRegister: "false",
                },
                success(response) {
                    if (response.code == '200') {
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
                error() {
                    vue.$modal.msgError("发送未知错误，发送验证码失败");
                }
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
        register() {
            const vue = this;
            this.$refs["userInformation"].validate((valid) => {
                if (valid) {
                    $.ajax({
                        url: vue.userRegisterUrl + "/register",
                        type: "post",
                        data: {
                            userInformation: JSON.stringify(vue.userInformation),
                        },
                        success(response) {
                            if (response.code == "200") {
                                vue.$emit("register", true);
                                vue.$modal.msgSuccess("注册成功");
                                vue.returnLogin();
                            } else {
                                vue.$modal.msgError(response.msg);
                            }
                            
                        }
                    })
                } else {
                    return false;
                }
            });
        },
        // 关闭注册组件打开登录组件
        returnLogin() {
            this.$emit("returnLogin");
        },
    }
}
</script>

<style scoped>

.close:hover {
    cursor: pointer;
}
</style>
