<template>
    <div class="MonkeyWebUserHomeCommunity-container">
        <div class="community-card" 
        @click="toCommunityDetailViews(community.id)"
        v-for="community in communityList" 
        :key="community.id"
        @mouseover="community.isHover = '1'"
        @mouseleave="community.isHover = '0'">
            <el-row style="margin-bottom: 10px;">
                <el-col :span="4">
                    <div class="img-border">
                        <img class="community-img" :src="community.photo" alt="">
                        <div v-if="community.isRecommend == '1'" class="formTypeName">官方推荐</div>
                    </div>
                </el-col>
                <el-col :span="20">
                    <div >
                        <span class="community-name">{{ community.name }}</span>
                        <span class="create-time">创建于：{{ getTimeFormat(community.createTime) }}</span>
                    </div>
                    <div class="community-brief">{{ community.description }}</div>
                    <div style="position: relative;">
                        <span class="el-icon-user operate-common">&nbsp;成员&nbsp;{{ getFormatNumber(community.memberCount) }}</span>
                        <span class="el-icon-document operate-common">&nbsp;文章&nbsp;{{ getFormatNumber(community.articleCount) }}</span>
                        <div
                        v-if="community.isHover == '1' && community.userId == $store.state.user.id" 
                        @mouseover="community.isMoreHover = '1'"
                        @mouseleave="community.isMoreHover = '0'"
                        class="hover el-icon-more-outline">
                            <div v-if="community.isMoreHover == '1'" class="more-hover">
                                <div @click.stop="toCommunityManageViews(community.id)" class="common-hover">编辑</div>
                                <div @click.stop="deleteCommunity(community)" class="common-hover">删除</div>
                            </div>
                        </div>
                    </div>
                </el-col>
            </el-row>

            <div class="divisor"></div>
        </div>


        <div
        v-if="communityList == null || communityList == '' || communityList == [] || communityList.length <= 0"
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
import { getFormatNumber } from '@/assets/js/NumberMethod';
import { getTimeFormat } from '@/assets/js/DateMethod';
export default {
    name: 'MonkeyWebUserHomeCommunity',
    components: {
        PagiNation
    },
    data() {
        return {
            userId: "",
            currentPage: 1,
            pageSize: 10,
            totals: 0,
            communityList: [],
            userHomeCommunityUrl: "http://localhost:80/monkey-community/user/home/community",
        };
    },

    created() {
        this.userId = this.$route.params.userId;
        this.queryCommunityByUserId(this.userId);
    },

    methods: {
        getFormatNumber(val) {
            return getFormatNumber(val);
        },
        getTimeFormat(val) {
            return getTimeFormat(val);
        },
        // 删除社区
        deleteCommunity(community) {
            this.$modal.confirm(`"您确定要删除 ${community.name} 社区?"`)
                .then(() => {
                const vue = this;
                $.ajax({
                    url: vue.userHomeCommunityUrl + "/deleteCommunity",
                    type: "delete",
                    headers: {
                        Authorization: "Bearer " + store.state.user.token,
                    },
                    data: {
                        communityId: community.id,
                        userId: community.userId
                    },
                    success(response) {
                        if (response.code == vue.ResultStatus.SUCCESS) {
                            vue.queryCommunityByUserId(vue.userId);
                            vue.$modal.msgSuccess(response.msg);
                        } else {
                            vue.$modal.msgError(response.msg);
                        }
                    }
                })
            }).catch(() => {})
            
        },
        // 前往编辑社区界面
        toCommunityManageViews(communityId) {
            const { href } = this.$router.resolve({
                name: "manage_user",
                params: {
                    communityId,
                },
                query: {
                    event: "to_community_manage",
                }
            })

            window.open(href, "_blank")
        },
        // 前往社区详情页面
        toCommunityDetailViews(communityId) {
            const { href } = this.$router.resolve({
                name: "community_detail",
                params: {
                    communityId
                }
            })

            window.open(href, "_blank")
        },
        // 通过用户id查询社区集合
        queryCommunityByUserId(userId) {
            const vue = this;
            $.ajax({
                url: vue.userHomeCommunityUrl + "/queryCommunityByUserId",
                type: "get",
                data: {
                    userId,
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.communityList = response.data.records;
                        vue.totals = response.data.total;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        handleSizeChange(val) {
            this.pageSize = val;
            this.currentPage = 1;
            this.queryCommunityByUserId(this.userId);
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            this.queryCommunityByUserId(this.userId);
        },
        getFormatNumber(val) {
            return getFormatNumber(val);
        }
    },
};
</script>

<style scoped>
.create-time {
    font-size: 14px;
    color: gray;
    vertical-align: middle;
}
.community-name:hover {
    color: #409EFF;
}
.common-hover:hover {
    color: #409EFF;
}
.common-hover:nth-child(n + 1) {
    margin-bottom: 10px;
}

.common-hover:last-child {
    margin-bottom: 0;
}
.more-hover {
    position: absolute;
    bottom: 20px;
    right: -20px;
    width: 50px;
    font-size: 14px;
    padding: 10px;
    background-color: #fff;
    text-align: center;
    box-shadow: 0 0 5px 0 black;
    animation: slide-out 0.4s linear;
}

@keyframes slide-out {
    0% {
        opacity: 0;
    }
    100% {
        overflow: 1;
    }
}

.hover {
    position: absolute;
    right: 0;
    font-size: 24px;
    animation: slide-out 0.4s linear;
}
.formTypeName {
    position: absolute;
    top: 0px;
    left: 0px;
    background-image: linear-gradient(to right, #f9d423 0%, #ff4e50 100%);
    color: white;
    padding: 1px 5px;
    font-size: 14px;
}
.divisor {
    background-color: rgba(0, 0, 0, 0.1);
    width: 100%;
    height: 1px;
}
.community-card {
    cursor: pointer;
    margin-bottom: 10px;
}
.operate-common {
    margin-right: 10px;
    font-size: 14px;
    color: gray;
}
.community-name {
    display: inline-block;
    max-width: 590px;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    margin-bottom: 4px;
    vertical-align: middle;
    margin-right: 10px;
}

.community-brief {
    display: -webkit-box;
    height: 75px;
    color: gray;
    font-size: 14px;
    overflow: hidden;
    -webkit-line-clamp: 4;
    -webkit-box-orient: vertical;
    text-overflow: ellipsis;
    white-space: normal;
    vertical-align: middle;
}
.img-border {
    overflow: hidden; 
    display: inline-block;
    position: relative;
}
.community-img {
    width: 130px;
    height: 120px;
    cursor: pointer;
    transition: 0.4s linear all;
}
.community-img:hover {
    transform: scale(1.2);
}
.MonkeyWebUserHomeCommunity-container {
    background-color: #fff;
    padding: 20px;
    animation: slide-out 0.4s linear;
}
</style>