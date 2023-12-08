<template>
    <div class="MonkeyWebUserAccount-container">
        <div class="password-header">
            <span class="password">密 码</span>
            <span class="set-password">
                <span @click="toUpdatePasswordViews()">设置&nbsp;/&nbsp;修改密码</span>
            </span>
            <router-view></router-view>
            <div class="divisor"></div>
        </div>

        <div class="password-header">
            <span class="password">手 机</span>
                
            <span class="phone-password">
                <span  v-if="userInfo.phoneVerified == '1'" class="photo-number">{{ userInfo.phone }}</span>
            <span class="set-phone">绑定&nbsp;/&nbsp;修改手机</span>
            </span>
        </div>
        <div class="divisor"></div>

        <div class="password-header">
            <span class="password">邮 箱</span>
                
            <span class="phone-password">
                <span v-if="userInfo.emailVerified == '1'" class="photo-number">{{ userInfo.email }}</span>
            <span @click="toAccountEmailViews(userInfo.emailVerified)" class="set-phone">绑定&nbsp;/&nbsp;修改邮箱</span>
            </span>
        </div>
        <div class="divisor"></div>

        <div class="password-header">
            <span class="password">登录记录</span>
                
            <span class="phone-password">
            <span class="set-phone">查看记录</span>
            </span>
        </div>
        <div class="divisor"></div>
        <div class="password-header">
            <span class="password">第三方账号</span>
                
            <span class="phone-password">
                <span class="photo-number">QQ 微信</span>
            <span class="set-phone">绑定&nbsp;/&nbsp;解绑</span>
            </span>
        </div>
    </div>
</template>

<script>
import $ from 'jquery'
import store from '@/store';
export default {
    name: 'MonkeyWebUserAccount',

    data() {
        return {
            userInfo: {},
            userCenterAccountUrl: "http://localhost:80/monkey-user/center/account",
        };
    },

    created() {
        this.queryUserInfo();
    },

    methods: {
        // 前往账户邮箱页面
        toAccountEmailViews(emailVerified) {
            // 若邮箱已被验证，则进入验证身份页面
            if (emailVerified == '1') {
                this.$router.push({
                    name: "email_verify"
                })
            } else {
                // 否在直接进入绑定邮箱页面
                this.$router.push({
                    name: "email_bind"
                })
            }
        },
        // 得到用户信息
        queryUserInfo() {
            const vue = this;
            $.ajax({
                url: vue.userCenterAccountUrl + "/queryUserInfo",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.userInfo = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        toUpdatePasswordViews() {
            this.$router.push({
                name: "user_center_account_password"
            })
        }
    },
};
</script>

<style scoped>
.photo-number {
    font-size: 14px;
    color: gray;
    margin-right: 10px;
} 
.set-phone {
    color: #409EFF;
    font-size: 14px;
    cursor: pointer;
}
.phone-password {
    position: absolute;
    right: 0;
}
.divisor {
    background-color: rgba(0, 0, 0, 0.1);
    margin: 20px 0;
    height: 1px;
}
.set-password:hover {
    opacity: 0.7;
}
.set-password {
    position: absolute;
    right: 0;
    color: #409EFF;
    font-size: 14px;
    cursor: pointer;
}
.password-header {
    position: relative;
}
.MonkeyWebUserAccount-container {
    padding: 20px;
    vertical-align: middle;
    background-color: #fff;
    animation: slide-out 0.4s linear;
}

@keyframes slide-out {
    0% {
        opacity: 0;
    }
    100% {
        opacity: 1;
    }
}
.password {
    font-size: 14px;
    color: gray;
}
</style>