import $ from "jquery"

export default({
    state: {
        id: "",
        username: "",
        photo: "",
        job: "",
        brief: "",
        token: "",
        is_login: false,
        socket: null, // 前端和后端建立的连接是什么
    },
    getters: {
    },
    mutations: {
        updateSocket(state, socket) {
            state.socket = socket;
        },
        updateToken(state, token) {
            state.token = token;
        },
        updateUserInfo(state, user) {
            state.id = user.id;
            state.username = user.username;
            state.photo = user.photo;
            state.job = user.job;
            state.brief = user.brief;
            state.is_login = user.is_login;
        },
        logout(state) {
            state.id = "";
            state.username = "";
            state.photo = "";
            state.job = "";
            state.brief = "";
            state.is_login = false;
            state.token = "";
        }
    },
    actions: {
        loginUsername(context, data) {
            $.ajax({
                url: "http://localhost:80/monkey-user/user/login/loginUsername",
                type: "post",
                data: {
                    username: data.username,
                    password: data.password,
                    verifyCode: data.verifyCode
                },
                success(response) {
                    if (response.code == data.vue.ResultStatus.SUCCESS) {
                        localStorage.setItem("token", response.data);
                        context.commit("updateToken", response.data);
                        if (data != null) data.success(response);
                    } else {
                        data.vue.$modal.msgError(response.msg)
                    }
                },
            })
        },
        loginEmail(context, data) {
            $.ajax({
                url: "http://localhost:80/user/login/loginEmail",
                type: "post",
                data: {
                    email: data.email,
                    verifyCode: data.verifyCode
                },
                success(response) {
                    if (response.code == data.vue.ResultStatus.SUCCESS) {
                        localStorage.setItem("token", response.data);
                        context.commit("updateToken", response.data);
                        if (data != null) data.success(response);
                    } else {
                        data.vue.$modal.msgError(response.msg)
                    }
                },
            })
        },

        // 通过token得到用户信息
        getUserInfoBytoken(context, data) {
            $.ajax({
                url: "http://localhost:80/monkey-user/user/getUserInfoBytoken",
                type: "get",
                headers: {
                    Authorization: "Bearer " + context.state.token,
                },
                success(response) {
                    if (response.code == '200') {
                        context.commit("updateUserInfo", {
                            ...response.data,
                            is_login: true,
                        });
                        if (data != null) data.success(response)
                    }
                },
            })
        },

        // 用户退出登录
        logout(context, data) {
            $.ajax({
                url: "http://localhost:80/monkey-user/user/logout",
                type: "put",
                headers: {
                    Authorization: "Bearer " + context.state.token,
                },
                success(response) {
                    if (response.code == data.vue.ResultStatus.SUCCESS) {
                        localStorage.removeItem("token");
                        context.commit("logout");   
                        data.vue.$modal.msgSuccess(response.msg);
                    } else {
                        data.vue.$modal.msgError(response.msg);
                    }
                },
                error(response) {
                    data.vue.$modal.msgError(response.msg);
                }
            })
            
        }
    },
    modules: {
    }
})