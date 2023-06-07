<template>
    <div class="register-contrainer" style="height: 385px;background: linear-gradient(#141e30,#243b55); padding-top: 300px;">
        <div class="my-login-box">
            <h2>注册</h2>
            <form>
                <div class="user-box">
                    <input type="text" v-model="userInformation.username" required="">
                    <label>用户名</label>
                </div>
                <div class="user-box">
                    <input type="password" v-model="userInformation.password" required="">
                    <label>密码</label>
                </div>
                <div class="user-box">
                    <input type="password" v-model="userInformation.confirePassword" required="">
                    <label>确认密码</label>
                </div>
                <a href="#" @click="registerUser()">
                    <span></span>
                    <span></span>
                    <span></span>
                    <span></span> 提交
                </a>
            </form>
        </div>
    </div>
</template>

<script>
import $ from "jquery"
import router from '@/router';

export default {
    name: 'LoginView',
    data() {
        return {
            error_message: "",
            userInformation: {
                username: "",
                password: "",
                confirePassword: "",
            }
            
        }
    },

    methods: {
        registerUser() {
            const vue = this.$modal;
            $.ajax({
                url: "http://localhost:4000/user/register",
                type: "post",
                data: {
                    username: this.userInformation.username,
                    password: this.userInformation.password,
                    confirePassword: this.userInformation.confirePassword
                },
                success(response) {
                    if (response.code == "10000") {
                        vue.msgSuccess("注册成功");
                        router.push("/user/LoginViews.vue")
                    } else {
                        vue.msgError(response.msg);
                    }
                    
                }
            })
        }
    }
}
</script>

<style scoped>

html {
    height: 100%;
}
body {
    margin: 0;
    padding: 0;
    font-family: sans-serif;
    background: linear-gradient(#141e30, #243b55);
}
.my-login-box {
    margin-left: 750px;
    
    width: 400px;
    padding: 40px;
    transform: translate(-50%, -50%);
    background: rgba(0, 0, 0, .5);
    box-sizing: border-box;
    box-shadow: 0 15px 25px rgba(0, 0, 0, .6);
    border-radius: 10px;
}
.my-login-box h2 {
    margin: 0 0 30px;
    padding: 0;
    color: #fff;
    text-align: center;
}
.my-login-box .user-box {
    position: relative;
}
.my-login-box .user-box input {
    width: 100%;
    padding: 10px 0;
    font-size: 16px;
    color: #fff;
    margin-bottom: 30px;
    border: none;
    border-bottom: 1px solid #fff;
    outline: none;
    background: transparent;
}
.my-login-box .user-box label {
    position: absolute;
    top: 0;
    left: 0;
    padding: 10px 0;
    font-size: 16px;
    color: #fff;
    pointer-events: none;
    transition: .5s;
}
.my-login-box .user-box input:focus ~ label,
.my-login-box .user-box input:valid ~ label {
    top: -30px;
    left: 0;
    color: #03e9f4;
    font-size: 12px;
}
.my-login-box form a {
    position: relative;
    display: inline-block;
    padding: 10px 20px;
    color: #03e9f4;
    font-size: 16px;
    text-decoration: none;
    text-transform: uppercase;
    overflow: hidden;
    transition: .5s;
    margin-top: 40px;
    letter-spacing: 4px
}
.my-login-box a:hover {
    background: #03e9f4;
    color: #fff;
    border-radius: 5px;
    box-shadow: 0 0 5px #03e9f4, 0 0 25px #03e9f4, 0 0 50px #03e9f4, 0 0 100px #03e9f4;
}
.my-login-box a span {
    position: absolute;
    display: block;
}
.my-login-box a span:nth-child(1) {
    top: 0;
    left: -100%;
    width: 100%;
    height: 2px;
    background: linear-gradient(90deg, transparent, #03e9f4);
    animation: btn-anim1 1s linear infinite;
}
@keyframes btn-anim1 {
    0% {
        left: -100%;
    }
    50%,
    100% {
        left: 100%;
    }
}
.my-login-box a span:nth-child(2) {
    top: -100%;
    right: 0;
    width: 2px;
    height: 100%;
    background: linear-gradient(180deg, transparent, #03e9f4);
    animation: btn-anim2 1s linear infinite;
    animation-delay: .25s
}
@keyframes btn-anim2 {
    0% {
        top: -100%;
    }
    50%,
    100% {
        top: 100%;
    }
}
.my-login-box a span:nth-child(3) {
    bottom: 0;
    right: -100%;
    width: 100%;
    height: 2px;
    background: linear-gradient(270deg, transparent, #03e9f4);
    animation: btn-anim3 1s linear infinite;
    animation-delay: .5s
}
@keyframes btn-anim3 {
    0% {
        right: -100%;
    }
    50%,
    100% {
        right: 100%;
    }
}
.my-login-box a span:nth-child(4) {
    bottom: -100%;
    left: 0;
    width: 2px;
    height: 100%;
    background: linear-gradient(360deg, transparent, #03e9f4);
    animation: btn-anim4 1s linear infinite;
    animation-delay: .75s
}
@keyframes btn-anim4 {
    0% {
        bottom: -100%;
    }
    50%,
    100% {
        bottom: 100%;
    }
}
</style>