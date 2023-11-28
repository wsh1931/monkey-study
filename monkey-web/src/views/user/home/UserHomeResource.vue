<template>
    <div class="MonkeyWebUserHomeResource-container">
        <div class="resource-card" 
        @click="toResourceDetailViews(resource.id)"
        v-for="resource in resourceList" 
        :key="resource.id"
        @mouseover="resource.isHover = '1'"
        @mouseleave="resource.isHover = '0'">
            <el-row style="margin-bottom: 10px;">
                <el-col :span="4">
                    <div class="img-border">
                        <img class="resource-img" :src="resource.typeUrl" alt="">
                        <div class="formTypeName">{{ resource.formTypeName }}</div>
                    </div>
                </el-col>
                <el-col :span="20">
                    <div >
                        <span class="resource-name">{{ resource.name }}</span>
                        <span class="create-time">发布于：{{ getTimeFormat(resource.createTime) }}</span>
                    </div>
                    <div class="resource-brief">{{ resource.description }}</div>
                    <div style="position: relative;">
                        <span class="iconfont icon-yanjing operate-common">&nbsp;游览&nbsp;{{ getFormatNumber(resource.viewCount) }}</span>
                        <span class="el-icon-download operate-common">&nbsp;下载&nbsp;{{ getFormatNumber(resource.downCount) }}</span>
                        <span class="operate-common">￥&nbsp;购买&nbsp;{{ getFormatNumber(resource.buyCount) }}</span>
                        <span class="iconfont icon-dianzan operate-common">&nbsp;点赞&nbsp;{{ getFormatNumber(resource.likeCount) }}</span>
                        <span class="iconfont icon-shoucang operate-common">&nbsp;收藏&nbsp;{{ getFormatNumber(resource.collectCount) }}</span>
                        <span class="iconfont icon-pinglun operate-common">&nbsp;评论&nbsp;{{ getFormatNumber(resource.commentCount) }}</span>
                        <span class="iconfont icon-shoucang operate-common">&nbsp;评分&nbsp;{{ resource.score }}</span>
                        <div
                        v-if="resource.isHover == '1' && resource.userId == $store.state.user.id" 
                        @mouseover="resource.isMoreHover = '1'"
                        @mouseleave="resource.isMoreHover = '0'"
                        class="hover el-icon-more-outline">
                            <div v-if="resource.isMoreHover == '1'" class="more-hover">
                                <div @click.stop="toEditResourceViews(resource.id)" class="common-hover">编辑</div>
                                <div @click.stop="deleteResource(resource)" class="common-hover">删除</div>
                            </div>
                        </div>
                    </div>
                </el-col>
            </el-row>

            <div class="divisor"></div>
        </div>


        <div
        v-if="resourceList == null || resourceList == '' || resourceList == [] || resourceList.length <= 0"
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
    name: 'MonkeyWebUserHomeResource',
    components: {
        PagiNation
    },
    data() {
        return {
            userId: "",
            currentPage: 1,
            pageSize: 10,
            totals: 0,
            resourceList: [],
            userHomeResourceUrl: "http://localhost:80/monkey-resource/user/home",
        };
    },

    created() {
        this.userId = this.$route.params.userId;
        this.queryResourceByUserId(this.userId);
    },

    methods: {
        getFormatNumber(val) {
            return getFormatNumber(val);
        },
        getTimeFormat(val) {
            return getTimeFormat(val);
        },
        // 删除资源
        deleteResource(resource) {
            this.$modal.confirm(`"确定删除 ${resource.name} 资源?"`)
                .then(() => {
                    const vue = this;
                    $.ajax({
                        url: vue.userHomeResourceUrl + "/deleteResource",
                        type: "delete",
                        headers: {
                            Authorization: "Bearer " + store.state.user.token,
                        },
                        data: {
                            resourceId: resource.id,
                            userId: resource.userId
                        },
                        success(response) {
                            if (response.code == vue.ResultStatus.SUCCESS) {
                                vue.queryResourceByUserId(this.userId);
                                vue.$modal.msgSuccess(response.msg);
                            } else {
                                vue.$modal.msgError(response.msg);
                            }
                        }
                    })
                }).catch(() => {})
            
        },
        // 前往编辑资源界面
        toEditResourceViews(resourceId) {
            this.$router.push({
                name: "resource_edit",
                params: {
                    resourceId
                }
            })
        },
        // 前往资源详情页面
        toResourceDetailViews(resourceId) {
            const { href } = this.$router.resolve({
                name: "resource_detail",
                params: {
                    resourceId
                }
            })

            window.open(href, "_blank")
        },
        // 通过用户id查询资源集合
        queryResourceByUserId(userId) {
            const vue = this;
            $.ajax({
                url: vue.userHomeResourceUrl + "/queryResourceByUserId",
                type: "get",
                data: {
                    userId,
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.resourceList = response.data.records;
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
            this.queryResourceByUserId(this.userId);
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            this.queryResourceByUserId(this.userId);
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
.resource-name:hover {
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
    top: 5px;
    left: 25px;
    background-image: linear-gradient(to right, #92fe9d 0%, #00c9ff 100%);
    color: white;
    padding: 1px 5px;
    font-size: 14px;
}
.divisor {
    background-color: rgba(0, 0, 0, 0.1);
    width: 100%;
    height: 1px;
}
.resource-card {
    cursor: pointer;
    margin-bottom: 10px;
}
.operate-common {
    margin-right: 10px;
    font-size: 14px;
    color: gray;
}
.resource-name {
    display: inline-block;
    max-width: 590px;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    margin-bottom: 4px;
    vertical-align: middle;
    margin-right: 10px;
}

.resource-brief {
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
.resource-img {
    width: 130px;
    height: 120px;
    cursor: pointer;
    transition: 0.4s linear all;
}
.resource-img:hover {
    transform: scale(1.2);
}
@keyframes slide-out {
    0% {
        opacity: 0;
    }
    100% {
        opacity: 1;
    }
}
.MonkeyWebUserHomeResource-container {
    background-color: #fff;
    padding: 20px;
    animation: show-out 0.4s linear;
}
</style>