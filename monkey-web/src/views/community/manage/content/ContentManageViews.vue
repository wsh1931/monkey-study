<template>
    <div class="MonkeyWebContentManageViews-container">
        <div style="margin-bottom: 10px;">
            <span style="margin-right: 10px;">状态</span>
            <el-select 
            size="small" 
            v-model="status" 
            placeholder="请选择" 
            style="margin-right: 20px;">
                <el-option
                v-for="status in statusList"
                :key="status.value"
                :label="status.label"
                :value="status.value">
                </el-option>
            </el-select>

            <span style="margin-right: 10px;">频道</span>
            <el-select 
            size="small" 
            v-model="channel" 
            placeholder="请选择" 
            style="margin-right: 20px;">
                <el-option
                v-for="channel in channelList"
                :key="channel.id"
                :label="channel.channelName"
                :value="channel.id + ',' + channel.channelName">
                </el-option>
            </el-select>

            <span style="margin-right: 10px;">发布者</span>
            <el-input size="small" placeholder="请输入发布者编号" style="width: 200px;" v-model="publisherIdx">
            </el-input>
        </div>

        <el-table
        :data="articleList"
        border
        style="width: 100%">
            <el-table-column
            align="center"
            fixed
            prop="date"
            label="发布/收录文章标题"
            width="150">
            <template slot-scope="scope">
                <span @click="toCommunityArticleViews(communityId, scope.row.id)" class="publish-article-title">{{ scope.row.title }}</span>
            </template>
            </el-table-column>
            <el-table-column
            align="center"
            prop="userId"
            label="发布者编号"
            width="150">
            <template slot-scope="scope">
                <span class="user-idx" @click=toUserView(scope.row.userId)>{{ scope.row.userId }}</span>
            </template>
            </el-table-column>
            <el-table-column
            align="center"
            prop="name"
            label="发布者信息"
            width="200">
            <template slot-scope="scope">
                <img @click="toUserView(scope.row.userId)" class="user-img" :src="scope.row.userHeadImg" alt="">
                <span @click="toUserView(scope.row.userId)" class="username">{{ scope.row.username }}</span>
            </template>
            </el-table-column>
            <el-table-column
            align="center"
            prop="channelName"
            label="频道"
            width="120">
            <template slot-scope="scope">
                <el-select 
                size="small" 
                v-model="scope.row.channelName" 
                placeholder="请选择" >
                    <el-option
                    @click.native="updateCommunityArticleChannel(scope.row.id, articleChannel)"
                    v-for="articleChannel in articleChannelList"
                    :key="articleChannel.id"
                    :label="articleChannel.channelName"
                    :value="articleChannel.id">
                    </el-option>
                </el-select>
            </template>
            </el-table-column>
            <el-table-column
            align="center"
            prop="isTask"
            label="任务发布"
            width="100">
            <template slot-scope="scope">
                <span v-if="scope.row.isTask == '1'" class="el-icon-circle-check"></span>
                <span v-else="scope.row.isTask == '1'" class="el-icon-circle-close"></span>
            </template>
            </el-table-column>
            <el-table-column
            align="center"
            prop="isVote"
            label="投票发布"
            width="100">
            <template slot-scope="scope">
                <span v-if="scope.row.isVote == '1'" class="el-icon-circle-check"></span>
                <span v-else class="el-icon-circle-close"></span>
            </template>
            </el-table-column>
            <el-table-column
            align="center"
            prop="createTime"
            label="发布时间"
            width="200">
            </el-table-column>
            <el-table-column
            align="center"
            prop="likeCount"
            label="点赞数"
            width="100">
            </el-table-column>
            <el-table-column
            align="center"
            prop="commentCount"
            label="评论数"
            width="100">
            </el-table-column>
            <el-table-column
            align="center"
            prop="collectCount"
            label="收藏数"
            width="100">
            </el-table-column>
            <el-table-column
            align="center"
            prop="score"
            label="评分"
            width="100">
            </el-table-column>
            <el-table-column
            align="center"
            prop="scoreCount"
            label="评分人数"
            width="100">
            </el-table-column>
            <el-table-column
            align="center"
            prop="status"
            label="状态"
            width="100">
            <template slot-scope="scope">
                <el-tag v-if="scope.row.status == '0'" type="primary">正在审核</el-tag>
                <el-tag v-else-if="scope.row.status == '1'" type="success">审核通过</el-tag>
                <el-tag v-else type="danger">审核失败</el-tag>
            </template>
            </el-table-column>
            <el-table-column
            align="center"
            fixed="right"
            label="操作"
            width="350">
            <template slot-scope="scope">
                <el-button @click="toCommunityArticleViews(communityId, scope.row.id)" type="info" size="mini">查看</el-button>
                <el-button @click="setTopArticle(scope.row)" v-if="scope.row.isTop == '0'" size="mini" type="primary">置顶</el-button>
                <el-button @click="cancelTopArticle(scope.row)" v-if="scope.row.isTop == '1'" size="mini" type="primary">取消置顶</el-button>
                <el-button @click="setExcellentArticle(scope.row)" v-if="scope.row.isExcellent == '0'" type="success" size="mini">精选</el-button>
                <el-button @click="cancelExcellentArticle(scope.row)" v-if="scope.row.isExcellent == '1'" type="success" size="mini">取消精选</el-button>
                <el-button @click="deleteArticle(scope.row.id)" type="danger"  size="mini">删除</el-button>
            </template>
            </el-table-column>
        </el-table>
        <PagiNation
        style="text-align: right; margin-top: 10px;"
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
    name: 'MonkeyWebContentManageViews',
    components: {
        PagiNation
    },
    data() {
        return {
            // 文章展示的频道列表
            articleChannelList: [],
            currentPage: 1,
            pageSize: 10,
            totals: 0,
            // 文章列表
            articleList: [],
            // 发布者id
            publisherId: "",
            // 社区频道集合
            channelList: [],
            communityId: "",
            statusList: [{
                value: 'all',
                label: '全部'
            }, {
                value: 'top',
                label: '已置顶'
            }, {
                value: 'curation',
                label: '已精选'
            },{
                value: "task",
                label: "任务发布",
            },{
                value: "vote",
                label: "投票发布",
            },{
                value: 'wait_approval',
                label: '待审核'
            }, {
                value: 'approval_success',
                label: '审核通过'
            }, {
                value: 'approval_fail',
                label: '审核失败'
            },
            ],
            status: 'all',
            channel: '全部',
            // 发布者编号
            publisherIdx: "",
            publishCommunityUrl: "http://localhost:80/monkey-community/publish",
            contentManageUrl: "http://localhost:80/monkey-community/manage/contentManage",
            communityArticleUrl: "http://localhost:80/monkey-community/article",
            communityDetailCardUrl: "http://localhost:80/monkey-community/community/detail/card",
            };
    },
    watch: {
        status(newVal) {
            this.currentPage = 1;
            this.pageSize = 10;
            this.queryContentManageListByCondition(newVal, this.channel, this.publisherId);
        },
        channel(newVal) {
            this.currentPage = 1;
            this.pageSize = 10;
            this.queryContentManageListByCondition(this.status, newVal, this.publisherId);
        },
        publisherIdx(newVal) {
            this.currentPage = 1;
            this.pageSize = 10;
            this.queryContentManageListByCondition(this.status, this.channel, newVal);
        }
    },
    created() {
        this.communityId = this.$route.params.communityId;
        this.queryCommunityChannelListByCommunityId(this.communityId);
        this.queryContentManageListByCondition(this.status, this.channel, this.publisherId);
    },
    methods: {
        // 设置文章置顶
        setTopArticle(row) {
            const vue = this;
            $.ajax({
                url: vue.communityDetailCardUrl + "/setTopArticle",
                type: "put",
                data: {
                    articleId: row.id,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        row.isTop = '1';
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
                error(response) {
                    vue.$modal.msgError(response.msg);
                }
            })
        },
        // 取消文章置顶
        cancelTopArticle(row) {
            const vue = this;
            $.ajax({
                url: vue.communityDetailCardUrl + "/cancelTopArticle",
                type: "put",
                data: {
                    articleId: row.id,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        row.isTop = '0';
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
                error(response) {
                    vue.$modal.msgError(response.msg);
                }
            })
        },
        // 设置文章为精选内容
        setExcellentArticle(row) {
            const vue = this;
            $.ajax({
                url: vue.communityDetailCardUrl + "/setExcellentArticle",
                type: "put",
                data: {
                    articleId: row.id
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        row.isExcellent = '1';
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
                error(response) {
                    vue.$modal.msgError(response.msg);
                }
            })
        },
         // 取消文章为精选内容
        cancelExcellentArticle(row) {
            const vue = this;
            $.ajax({
                url: vue.communityDetailCardUrl + "/cancelExcellentArticle",
                type: "put",
                data: {
                    articleId: row.id
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        row.isExcellent = '0';
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
                error(response) {
                    vue.$modal.msgError(response.msg);
                }
            })
        },
        // 删除文章
        deleteArticle(articleId) {
            const vue = this;
            $.ajax({
                url: vue.communityDetailCardUrl + "/deleteArticle",
                type: "delete",
                data: {
                    articleId,
                    communityId: vue.communityId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.queryContentManageListByCondition(vue.status, vue.channel, vue.publisherId)
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
                error(response) {
                    vue.$modal.msgError(response.msg);
                }
            })
        },
        // 修改社区文章频道
        updateCommunityArticleChannel(communityArticleId, channel) {
            const vue = this;
            $.ajax({
                url: vue.communityArticleUrl + "/update/communityArticle/channel",
                type: "put",
                data: {
                    communityArticleId,
                    channelId: channel.id,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.channelName = channel.channelName;
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 前往用户主页
        toUserView(userId) {
            const { href } = this.$router.resolve({
                name: "user_home",
                params: {
                    userId
                }
            })

            window.open(href, "_blank")
        },
        // 前往社区文章界面
        toCommunityArticleViews(communityId, communityArticleId) {
            const { href } = this.$router.resolve({
                name: "community_article",
                params: {
                    communityId,
                    communityArticleId
                }
            })
            window.open(href, "_blank")
        },
        // 查询社区频道集合
        queryCommunityChannelListByCommunityId(communityId) {
            const vue = this;
            $.ajax({
                url: vue.publishCommunityUrl + "/queryCommunityChannelListByCommunityId",
                type: "get",
                data: {
                    communityId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.channelList = response.data;
                        vue.articleChannelList = JSON.parse(JSON.stringify(vue.channelList));
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 通过条件查询内容管理集合
        queryContentManageListByCondition(status, channel, publisherId) {
            const vue = this;
            $.ajax({
                url: vue.contentManageUrl + "/queryContentManageList/byCondition",
                type: "get",
                data: {
                    status,
                    channel,
                    publisherId,
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    communityId: vue.communityId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.articleList = response.data.records;
                        vue.totals = response.data.total;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },

        handleSizeChange(val) {
            this.pageSize = val;
            this.queryContentManageListByCondition(this.status, this.channel, this.publisherId);
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            this.queryContentManageListByCondition(this.status, this.channel, this.publisherId);
        },
    },
};
</script>

<style scoped>
.user-idx {
    color: #409EFF;
}
.user-idx:hover {
    cursor: pointer;
    opacity: 0.5;
}
.el-icon-circle-close {
    background-color: #F56C6C;
    color: white;
    font-size: 30px;
    border-radius: 50%;
}
.el-icon-circle-check {
    background-color: #67C23A;
    color: white;
    font-size: 30px;
    border-radius: 50%;
}
.username {
    margin-left: 10px;
    vertical-align: middle;
}
.username:hover {
    opacity: 0.5;
    cursor: pointer;
}
.user-img {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    vertical-align: middle;
}
.user-img:hover {
    cursor: pointer;
    opacity: 0.5;
}
.publish-article-title {
    color: #409EFF;
    cursor: pointer;
}
.publish-article-title:hover {
    opacity: 0.5;
}
.MonkeyWebContentManageViews-container {
    padding: 10px;
    background-color: #fff;
}
</style>