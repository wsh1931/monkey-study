<template>
    <div class="MonkeyWebUserCollect-container">
        <CreateCollect
        @closeShowCollect="closeShowCollect"
        @createCollectSuccess="createCollectSuccess"
        v-if="isShowCreateCollect"/>
        <el-row>
            <el-col :span="5">
                <el-menu
                    v-infinite-scroll="loadData"
                    router
                    :default-active="defaultRouter"
                    class="el-menu-vertical-demo infinite-list"
                    text-color="gray"
                    active-text-color="#409EFF">
                    <el-menu-item @click="isShowCreateCollect = true">
                        <i class="el-icon-folder-add"></i>
                        <span slot="title">新建收藏夹</span>
                    </el-menu-item>
                    <el-menu-item 
                    class="menu-item infinite-list-item" 
                    v-for="collectContent in collectContentList" 
                    :key="collectContent.id"
                    :index="`/user/center/collect/detail/${collectContent.id}`">
                        <i class="el-icon-folder"></i>
                        <span slot="title" class="collect-title">{{ collectContent.name }}</span>
                        <div class="collect-number">
                            <i class="el-icon-document"></i>
                            <span>{{ collectContent.collectCount }}</span>
                        </div>
                    </el-menu-item>
                </el-menu>
            </el-col>
            <el-col :span="19">
                <router-view class="child-class"></router-view>
            </el-col>
        </el-row>
    </div>
</template>

<script>
import $ from 'jquery'
import CreateCollect from '@/components/collect/CreateCollect.vue';
import store from '@/store';
export default {
    name: 'MonkeyWebUserCollect',
    components: {
        CreateCollect
    },
    data() {
        return {
            isLoading: false,
            currentPage: 1,
            pageSize: 10,
            totals: 0,
            // 是否显示新建文件夹
            isShowCreateCollect: false,
            defaultRouter: "",
            // 收藏目录集合
            collectContentList: [],
            userCenterCenterUrl: "http://localhost:80/monkey-user/center/collect",
        };
    },
    watch: {
        "$route.path"(path) {
            this.defaultRouter = path;
        },
    },
    
    created() {
        this.defaultRouter = this.$route.path;
        this.loadData();
    },

    methods: {
        loadData() {
            if (this.isLoading) {
                return;
            }
            if ((this.currentPage - 1) * this.pageSize > this.totals) {
                this.$modal.msgWarning("没有更多信息了");
                return false;
            }
            this.isLoading = true;
            this.queryCollectContent();
        },
        // 查询收藏目录以及对应的收藏数
        queryCollectContent() {
            const vue = this;
            $.ajax({
                url: vue.userCenterCenterUrl + "/queryCollectContent",
                type: 'get',
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.isLoading = false;
                        const data = response.data.records;
                        vue.totals = response.data.total;
                        for (let i = 0; i < data.length; i++) {
                            vue.collectContentList.push(data[i]);
                        }
                        vue.currentPage++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        closeShowCollect(status) {
            this.isShowCreateCollect = status;
        },
        // 当创建收藏夹成功时
        createCollectSuccess() {
            if (this.isLoading) {
                return;
            }
            this.isShowCreateCollect = false;
            this.isLoading = true;
            this.collectContentList = [];
            this.currentPage = 1;
            this.queryCollectContent();
        }
    },
};
</script>

<style scoped>
::-webkit-scrollbar {
    width: 10px;
    background-color: #fff;
}

:hover ::-webkit-scrollbar-track-piece {
    background-color: #fff;
    border-radius: 6px;
}

:hover::-webkit-scrollbar-thumb:hover {
    background-color: rgba(0, 0, 0, 0.1);
}

:hover::-webkit-scrollbar-thumb:vertical {
    background-color: rgba(0, 0, 0, 0.1);
    border-radius: 6px;
    outline: 2px solid #fff;
    outline-offset: -2px;
    border: 2px solid #fff;
}
.el-icon-document {
    font-size: 12px;
    transform: scale(0.8);
}
.collect-number {
    position: absolute;
    bottom: 10px;
    right: 10px;
    font-size: 12px;
}
.menu-item {
    position: relative;
    box-shadow: 0 0 5px 0 rgba(128, 128, 128, 0.1);
}
.el-menu-item:nth-child(n + 2), .el-submenu__title:nth-child(n + 2) {
    height: 66px;
    line-height: normal;
    padding-top: 10px;
}
.collect-title {
    display: inline-block;
    white-space: nowrap;
    text-overflow: ellipsis;
    vertical-align: middle;
    overflow: hidden;
    max-width: 140px;
}
.el-menu-item {
    font-size: 12px;
}
.el-menu-vertical-demo {
    text-align: left;
    min-height: calc(100vh - 81px);
    height: calc(100vh - 200px);
    overflow:auto
}
.file-navigate {
    background-color: #fff;
    padding: 16px;
}
.child-class {
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
</style>