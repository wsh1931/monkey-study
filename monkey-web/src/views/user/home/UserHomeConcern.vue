<template>
    <div class="MonkeyWebUserHomeConcern-container">
        <div
        @click="toUserViews(user.id)" 
        v-for="user in userList" 
        :key="user.id" 
        class="user-card">
            <el-row>
                <el-col :span="1">
                    <img class="user-headImg" :src="user.photo" alt="">
                </el-col>
                <el-col :span="23" class="content">
                    <div class="username">{{ user.username }}</div>
                    <div class="brief">{{ user.brief }}</div>
                    <el-button
                    v-if="user.isFans == '0'"
                    class="button"
                    round
                    @click.stop="connectUser(user.id)"
                    size="small">关注</el-button>
                    <el-button
                    @click.stop="cancelConnectUser(user.id)"
                    v-if="user.isFans == '1'"
                    class="button"
                    round
                    size="small">取消关注</el-button>
                </el-col>
            </el-row>
        </div>
        <div
        v-if="userList == null || userList == '' || userList == [] || userList.length <= 0"
        style="text-align: center;" >
            <el-empty description="暂无数据"></el-empty>
        </div>

        <PagiNation
        style="text-align: right;"
            :totals="totals"
            :currentPage="currentPage" 
            :pageSize="pageSize" 
            @handleCurrentChange = "handleCurrentChange"
            @handleSizeChange="handleSizeChange"/>
    </div>
</template>

<script>
import $ from 'jquery'
import store from '@/store';
import PagiNation from '@/components/pagination/PagiNation.vue';
export default {
    name: 'MonkeyWebUserHomeConcern',
    components: {
        PagiNation
    },
    data() {
        return {
            userId: "",
            userList: [],
            currentPage: 1,
            pageSize: 10,
            totals: 0,
            userHomeConnectUrl: "http://localhost:80/monkey-user/home/connect",
            userSearchUrl: "http://localhost:80/monkey-user/search"
        };
    },

    created() {
        this.userId = this.$route.params.userId;
        this.queryUserConnectById(this.userId);
    },

    methods: {
        // 前往用户主页
        toUserViews(userId) {
            const { href } = this.$router.resolve({
                name: "user_home",
                params: {
                    userId
                },
            })

            window.open(href, "_blank")
        },
        connectUser(concernId) {
            const vue = this;
            $.ajax({
                url: vue.userSearchUrl + "/concernUser",
                type: "put",
                data: {
                    concernId
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.$modal.msgSuccess(response.msg);
                        vue.queryUserConnectById(vue.userId);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        cancelConnectUser(concernId) {
            const vue = this;
            $.ajax({
                url: vue.userSearchUrl + "/cancelConcernUser",
                type: "put",
                data: {
                    concernId
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.$modal.msgSuccess(response.msg);
                        vue.queryUserConnectById(vue.userId);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        handleSizeChange(val) {
            this.pageSize = val;
            this.currentPage = 1;
            this.queryUserConnectById(this.userId);
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            this.queryUserConnectById(this.userId);
        },
        // 通过用户id查询用户集合
        queryUserConnectById(userId) {
            const vue = this;
            $.ajax({
                url: vue.userHomeConnectUrl + "/queryUserConnectById",
                type: "get",
                data: {
                    userId,
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.userList = response.data.records;
                        vue.totals = response.data.total;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
    },
};
</script>

<style scoped>
.button {
    position: absolute;
    right: 0;
    top: 5px;
}
.content {
    padding-left: 20px;
    position: relative;
}
.user-card {
    transition: 0.4s linear all;
    padding: 20px;
}
.user-card:hover {
    cursor: pointer;
    box-shadow: 0 0 10px 0 #00f2fe;
}
.brief {
    max-width: 680px;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    margin-bottom: 4px;
    vertical-align: middle;
    margin-right: 10px;
    font-size: 14px;
    color: gray;
}
.username:hover {
    color: #409EFF;
}
.username {
    display: inline-block;
    max-width: 680px;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    margin-bottom: 4px;
    vertical-align: middle;
    margin-right: 10px;
    transition: 0.4s linear all;
    cursor: pointer;
    font-weight: 550;
}
.MonkeyWebUserHomeConcern-container {
    background-color: #fff;
    padding: 20px;
    vertical-align: middle;
    cursor: pointer;
    animation: slide-out 0.4s linear;
}
.user-headImg {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    transition: 0.4s linear all;
}
.user-headImg:hover {
    cursor: pointer;
    opacity: 0.7;
}
@keyframes slide-out {
    0% {
        opacity: 0;
    }
    100% {
        opacity: 1;
    }
}
</style>