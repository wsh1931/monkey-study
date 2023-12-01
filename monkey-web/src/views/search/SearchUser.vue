<template>
    <div class="MonkeyWebSearchUser-container">
        <el-tabs v-model="activeName" @tab-click="handleClick" >
            <el-tab-pane label="综合" name="comprehensive"></el-tab-pane>
            <el-tab-pane label="最新" name="latest"></el-tab-pane>
            <el-tab-pane label="最热" name="hire"></el-tab-pane>
            <el-tab-pane label="粉丝" name="fans"></el-tab-pane>
            <el-tab-pane label="作品" name="opus"></el-tab-pane>
            <el-tab-pane label="阅读" name="view"></el-tab-pane>
            <el-tab-pane label="点赞" name="like"></el-tab-pane>
            <el-tab-pane label="收藏" name="collect"></el-tab-pane>
            <div
            class="infinite-list" 
            v-infinite-scroll="loadData" 
            infinite-scroll-distance="30">
                <div 
                v-for="user in userList"
                :key="user.id"
                style="cursor: pointer;">
                    <el-row>
                        <el-col :span="2">
                            <img  class="user-headImg" :src="user.userHeadImg" alt="">
                        </el-col>
                        <el-col :span="22" class="card-right">
                            <button 
                            @click="concernUser(user)"
                            class="concern-button" 
                            v-if="user.isFans == '0'">关注</button>
                            <button
                            @click="cancelConcernUser(user)" 
                            class="concern-button" 
                            v-if="user.isFans == '1'">取消关注</button>
                            <div class="username" v-html="user.username"></div>
                            <div class="brief" v-html="user.userBrief"></div>
                            <div>
                                <span class="achievement">作品&nbsp;{{ getFormatNumber(user.opusCount) }}</span>
                                <span class="achievement">阅读&nbsp;{{ getFormatNumber(user.viewCount) }}</span>
                                <span class="achievement">点赞&nbsp;{{ getFormatNumber(user.likeCount) }}</span>
                                <span class="achievement">收藏&nbsp;{{ getFormatNumber(user.collectCount) }}</span>
                                <span class="achievement">粉丝&nbsp;{{ getFormatNumber(user.fansCount) }}</span>
                                <span class="achievement">关注&nbsp;{{ getFormatNumber(user.connectCount) }}</span>
                            </div>
                        </el-col>
                    </el-row>

                    
                    <div class="divisor"></div>
                </div>
            </div>

        <div
        v-if="userList == null || userList == '' || userList == [] || userList.length <= 0"
        style="text-align: center;" >
            <el-empty description="暂无数据"></el-empty>
        </div>
        </el-tabs>
    </div>
</template>

<script>
import $ from 'jquery'
import { getFormatNumber } from '@/assets/js/NumberMethod';
import store from '@/store';
export default {
    name: 'MonkeyWebSearchUser',

    data() {
        return {
            isScroll: true,
            activeName: "comprehensive",
            currentPage: 1,
            pageSize: 10,
            // 关键词搜索字段
            keyword: this.$route.query.keyword,
            // 资源集合
            userList: [],
            searchUserUrl: "http://localhost:80/monkey-search/user",
            userSearchUrl: "http://localhost:80/monkey-user/search"
        };
    },

    watch: {
        '$route.query.keyword'(newVal, oldVal) {
            this.keyword = newVal;
            this.currentPage = 1;
            this.userList = [];
            if (this.activeName == 'comprehensive') {
                this.queryComprehensiveUser();
            } else if (this.activeName == "latest") {
                this.queryLatestUser();
            } else if (this.activeName == "hire") {
                this.queryHireUser();
            } else if (this.activeName == "fans") {
                this.queryFansUser();
            } else if (this.activeName == "opus") {
                this.queryOpusUser();
            } else if (this.activeName == "view") {
                this.queryViewUser();
            } else if (this.activeName == "like") {
                this.queryLikeUser();
            } else if (this.activeName == "collect") {
                this.queryCollectUser();
            }
        }
    },

    methods: {
        // 关注用户
        concernUser(user) {
            const vue = this;
            $.ajax({
                url: vue.userSearchUrl + "/concernUser",
                type: "put",
                headers: {
                    Authorization: 'Bearer ' + store.state.user.token,
                },
                data: {
                    concernId: user.id
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.$modal.msgSuccess(response.msg);
                        user.isFans = '1';
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 取消关注用户
        cancelConcernUser(user) {
            const vue = this;
            $.ajax({
                url: vue.userSearchUrl + "/cancelConcernUser",
                type: "put",
                headers: {
                    Authorization: 'Bearer ' + store.state.user.token,
                },
                data: {
                    concernId: user.id
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.$modal.msgSuccess(response.msg);
                        user.isFans = '0';
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 取消关注用户
        // 前往用户主页
        toUserViews(userId) {
            const { href } = this.$router.resolve({
                name: "user_home",
                params: {
                    userId
                }
            })

            window.open(href, "_blank")
        },
        // 跳转至用户详情界面
        toUserComment(userId) {
             // 用户游览数 + 1
            const { href } = this.$router.resolve({
                name: "user_detail",
                params: {
                    userId
                }
            })

            window.open(href, '_black');
        },
        handleClick() {
            this.isScroll = false;
            this.currentPage = 1;
            this.userList = [];
            if (this.activeName == 'comprehensive') {
                this.queryComprehensiveUser();
            } else if (this.activeName == "latest") {
                this.queryLatestUser();
            } else if (this.activeName == "hire") {
                this.queryHireUser();
            } else if (this.activeName == "fans") {
                this.queryFansUser();
            } else if (this.activeName == "opus") {
                this.queryOpusUser();
            } else if (this.activeName == "view") {
                this.queryViewUser();
            } else if (this.activeName == "like") {
                this.queryLikeUser();
            } else if (this.activeName == "collect") {
                this.queryCollectUser();
            }
        },
        getFormatNumber(val) {
            return getFormatNumber(val);
        },
        loadData() {
            if (this.isScroll) {
                this.isScroll = false;
                if (this.activeName == 'comprehensive') {
                    this.queryComprehensiveUser();
                } else if (this.activeName == "latest") {
                    this.queryLatestUser();
                } else if (this.activeName == "hire") {
                    this.queryHireUser();
                } else if (this.activeName == "fans") {
                    this.queryFansUser();
                } else if (this.activeName == "opus") {
                    this.queryOpusUser();
                } else if (this.activeName == "view") {
                    this.queryViewUser();
                } else if (this.activeName == "like") {
                    this.queryLikeUser();
                } else if (this.activeName == "collect") {
                    this.queryCollectUser();
                }
            }
        },
        // 查询点赞人数最多用户列表
        queryLikeUser() {
            const vue = this;
            $.ajax({
                url: vue.searchUserUrl + "/queryLikeUser",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data;
                        for (let i = 0; i < data.length; i++) {
                            vue.userList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询粉丝数最多用户列表
        queryFansUser() {
            const vue = this;
            $.ajax({
                url: vue.searchUserUrl + "/queryFansUser",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data;
                        for (let i = 0; i < data.length; i++) {
                            vue.userList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询作品人数最多用户列表
        queryOpusUser() {
            const vue = this;
            $.ajax({
                url: vue.searchUserUrl + "/queryOpusUser",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data;
                        for (let i = 0; i < data.length; i++) {
                            vue.userList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询收藏数最多用户列表
        queryCollectUser() {
            const vue = this;
            $.ajax({
                url: vue.searchUserUrl + "/queryCollectUser",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data;
                        for (let i = 0; i < data.length; i++) {
                            vue.userList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询游览数最多用户列表
        queryViewUser() {
            const vue = this;
            $.ajax({
                url: vue.searchUserUrl + "/queryViewUser",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data;
                        for (let i = 0; i < data.length; i++) {
                            vue.userList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询最热用户列表
        queryHireUser() {
            const vue = this;
            $.ajax({
                url: vue.searchUserUrl + "/queryHireUser",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data;
                        for (let i = 0; i < data.length; i++) {
                            vue.userList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询最新用户列表
        queryLatestUser() {
            const vue = this;
            $.ajax({
                url: vue.searchUserUrl + "/queryLatestUser",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data;
                        for (let i = 0; i < data.length; i++) {
                            vue.userList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询综合用户列表
        queryComprehensiveUser() {
            const vue = this;
            $.ajax({
                url: vue.searchUserUrl + "/queryComprehensiveUser",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    keyword: vue.keyword,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        const data = response.data;
                        for (let i = 0; i < data.length; i++) {
                            vue.userList.push(data[i]);
                        }
                        vue.isScroll = true;
                        vue.currentPage++;
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
.concern-button {
    position: absolute;
    right: 10px;
    top: 20px;
    vertical-align: middle;
    cursor: pointer;
    background-color: #fff;
    border: 0 !important;
    box-shadow: 0 0 3px 0 gray;
    padding: 6px 30px;
    border-radius: 20px;
    transition: 0.2s linear all;
}
.concern-button:hover {
    box-shadow: 0 0 5px 0 #409EFF;
}
.card-right {
    position: relative;
}
.achievement {
    font-size: 14px;
    color: gray;
    margin-right: 20px;
}
.brief {
    font-size: 14px;
    color: gray;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    margin-bottom: 5px;
    max-width: 600px;
}
.username {
    margin-right: 10px;
    font-size: 14px;
    transition: 0.2s linear all;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    margin-bottom: 5px;
    max-width: 600px;
}
.username:hover {
    color: #409EFF;
}
.user-headImg {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    transition: 0.3s linear all;
}
.user-headImg:hover {
    opacity: 0.7;
}
::v-deep .el-tabs__header {
    margin: 0 0 10px 0;
}
.infinite-list {
    overflow:auto; 
    max-height: calc(100vh - 150px);
    border-radius: 10px;
}
::v-deep .el-tabs__nav-wrap::after {
    content: none;
    position: absolute;
    left: 0;
    width: 100%;
    height: 0;
    background-color: #fff;
    z-index: 1;
}
.MonkeyWebSearchUser-container {
    vertical-align: middle;
    animation: slide-out 0.4s linear;
}
.divisor {
    background-color: rgba(0, 0, 0, 0.1);
    height: 1px;
    width: 100%;
    margin: 10px 0;
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