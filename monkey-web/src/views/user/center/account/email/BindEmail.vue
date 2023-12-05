<template>
    <div class="MonkeyWebBindEmail-container">
        <div class="email-header">
            <el-input
            class="email"
            v-model="email"
            placeholder="输入您要绑定的邮箱">
            </el-input>
        </div>
        <div class="email-header"> 
            <el-input
                v-model="verifyCode"
                placeholder="请输入验证码"
                style="width: 140px;">
                </el-input>
                <el-button @click="getVerify(email)" :disabled="isValid">{{ verityText }}</el-button>
        </div>
        <div class="email-header">
            <el-button @click="nextStep(verifyCode, email)" type="primary" round size="medium">下一步</el-button>
        </div>
    </div>
</template>

<script>
import $ from 'jquery'
import store from '@/store';
import { verifyEmail } from '@/assets/js/CommonMethods';
export default {
    name: 'MonkeyWebBindEmail',

    data() {
        return {
            email: "",
            verityText: "获取验证码",
            verifyCode: "",
            isValid: false,
            userCenterAccountUrl: "http://localhost:80/monkey-user/center/account",
        };
    },

    mounted() {
        
    },

    methods: {
        // 下一步
        nextStep(verifyCode, email) {
            if (!this.verifyEmail(email)) {
                this.$modal.msgError("请输入正确的QQ邮箱格式");
                return false;
            }
            if (verifyCode == null || verifyCode == "") {
                this.$modal.msgError("输入的验证码不能为空");
                return false;
            }

            // 发送绑定邮箱验证码
            const vue = this;
            $.ajax({
                url: vue.userCenterAccountUrl + "/submitBindVerify",
                type: "put",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    email,
                    verifyCode
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.$modal.msgSuccess(response.msg);
                        vue.$router.push({
                            name: "email_success"
                        })
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        verifyEmail(val) {
            return verifyEmail(val);
        },
        // 发送邮箱绑定验证码
        sendEmailBindVerify(email) {
            const vue = this;
            $.ajax({
                url: vue.userCenterAccountUrl + "/sendEmailBindVerify",
                type: "post",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    email
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 点击提交验证码
        getVerify(email) {
            if (!this.verifyEmail(email)) {
                this.$modal.msgError("请输入正确的QQ邮箱格式");
                return false;
            }
            this.sendEmailBindVerify(email);
            this.isValid = true;
            let seconds = 60;
            this.verityText = `${seconds} 秒后重试`
            let time = setInterval(() => {
                seconds--;
                this.verityText = `${seconds} 秒后重试`
                if (seconds == 0) {
                    clearInterval(time);
                    this.isValid = false;
                    this.verityText = "获取验证码";
                }
            }, 1000)
        }
    },
};
</script>

<style scoped>
.email-header {
    margin-bottom: 20px;
    text-align: center;
}
.email {
    width: 250px;
}
.MonkeyWebBindEmail-container {
    padding: 20px;
    text-align: center;
    background-color: #fff;
}
</style>