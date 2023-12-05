<template>
    <div style="background-color: #fff;">
        <div style="text-align: center;">
            <div class="title">验证码将发送到&nbsp;{{ email }}&nbsp;邮箱</div>
            <div class="tip">如果长时间未收到验证码，请检查垃圾箱</div>
            <div class="verify-content">
                <el-input
                v-model="verifyCode"
                placeholder="请输入验证码"
                style="width: 200px;">
                </el-input>
                <el-button @click="getVerify()" :disabled="isValid">{{ verityText }}</el-button>
            </div>
            <div>
                <el-button 
                @click="submitVerify(verifyCode)"
                :disabled="isNext"
                type="primary" 
                size="medium">下一步</el-button>
            </div>
        </div>
    </div>
</template>

<script>
import $ from 'jquery'
import store from '@/store';
import { verifyEmail } from '@/assets/js/CommonMethods';
export default {
    name: 'MonkeyWebVerifyIdentity',

    data() {
        return {
            // 在获得用户输入的验证码是否正确之前下一步按钮不可用
            isNext: false,
            // 验证码
            verifyCode: "",
            verityText: "获取验证码",
            isValid: false,
            email: "",
            userCenterAccountUrl: "http://localhost:80/monkey-user/center/account",
        };
    },

    created() {
        this.queryUserEmail();
    },

    methods: {
        // 提交验证码
        submitVerify(verifyCode) {
            if (verifyCode == null || verifyCode == "") {
                this.$modal.msgWarning("验证码不能为空");
                return false;
            }
            this.isNext = true;
            const vue = this;
            $.ajax({
                url: vue.userCenterAccountUrl + "/submitVerify",
                type: "post",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    verifyCode
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.isNext = false;
                        vue.$router.push({
                            name: "email_bind",
                        })
                    } else {
                        vue.isNext = false;
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 验证用户邮箱
        verifyEmail(email) {
            return verifyEmail(email);
        },
        // 得到用户邮箱
        queryUserEmail() {
            const vue = this;
            $.ajax({
                url: vue.userCenterAccountUrl + "/queryUserEmail",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.email = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 发送邮箱验证的验证码
        sendEmailVerify() {
            const vue = this;
            $.ajax({
                url: vue.userCenterAccountUrl + "/sendEmailVerify",
                type: "post",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
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
        getVerify() {
            this.sendEmailVerify();
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
.header {
    margin-bottom: 20px;
    font-weight: 900;
}
.verify-content {

    margin-bottom: 20px;
}

.tip {
    font-size: 12px;
    color: gray;

    margin-bottom: 20px;
}
.title {
    font-size: 14px;

    margin-bottom: 20px;
}
</style>