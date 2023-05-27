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
    },
    getters: {
    },
    mutations: {
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
        login(context, data) {
            $.ajax({
                url: "http://localhost:4000/user/login",
                type: "post",
                data: {
                    username: data.username,
                    password: data.password
                },
                success(response) {
                    if (response.code == "10000") {
                        localStorage.setItem("token", response.data);
                        context.commit("updateToken", response.data);
                        data.success(response);
                    } else {
                        data.error(response);
                    }
                },
                error(response) {
                    data.error(response);
                }
            })
        },

        // 通过token得到用户信息
        getUserInfoBytoken(context, data) {
            $.ajax({
                url: "http://localhost:4000/user/getUserInfoBytoken",
                type: "get",
                headers: {
                    Authorization: "Bearer " + context.state.token,
                },
                success(response) {
                    if (response.code == "10000") {
                        context.commit("updateUserInfo", {
                            ...response.data,
                            is_login: true,
                        });
                        if (data != null) data.success(response)
                    } else {
                        if (data != null) data.error(response);
                    }
                },
                errror(response) {
                    data.error(response);
                }
            })
        },

        // 用户退出登录
        logout(context) {
            localStorage.removeItem("token");
            context.commit("logout");   
        }
    },
    modules: {
    }
})