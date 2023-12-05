<template>
    <div class="MonkeyWebAccountPassword-container">
        <div>
            <el-input 
            class="input"
            placeholder="请输入密码" 
            v-model="password" 
            show-password ></el-input>
        </div>
        <div>
            <el-input 
            class="input"
            placeholder="请确认密码" 
            v-model="confirmPassword" 
            show-password></el-input>
        </div>

        <el-button
        round
        @click="modifyPassword(password, confirmPassword)"
        size="medium">确认修改</el-button>
    </div>
</template>

<script>
import $ from 'jquery'
import store from '@/store';
export default {
    name: 'MonkeyWebAccountPassword',

    data() {
        return {
            password: "",
            confirmPassword: "",
            userCenterAccountUrl: "http://localhost:80/monkey-user/center/account",
        };
    },

    methods: {
        // 修改密码
        modifyPassword(password, confirmPassword) {
            if (password == null || password == "") {
                this.$modal.msgWarning("请输入密码")
                return;
            }
            if (confirmPassword == null || confirmPassword == "") {
                this.$modal.msgWarning("请输入确定密码");
                return;
            }
            if (password.length < 1 || password.length > 20) {
                this.$modal.msgWarning("密码长度必需在 1 到 20 个字符之间");
                return;
            }
            if (confirmPassword.length < 1 || confirmPassword.length > 20) {
                this.$modal.msgWarning("确认密码长度必需在 1 到 20 个字符之间");
                return;
            }
            if (password != confirmPassword) {
                this.$modal.msgWarning("两次输入的密码不一样，请重新输入");
                return;
            }

            const vue = this;
            $.ajax({
                url: vue.userCenterAccountUrl + "/modifyPassword",
                type: "put",
                data: {
                    password,
                    confirmPassword
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        password = "";
                        confirmPassword = "";
                        vue.$modal.msgSuccess(response.msg);
                        vue.$router.go(-1);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        }
    },
};
</script>

<style scoped>
.input {
    text-align: center;
    margin-bottom: 20px;
    width: 250px;
}
.MonkeyWebAccountPassword-container {
    padding: 20px;
    text-align: center;
    background-color: #fff;
}
</style>