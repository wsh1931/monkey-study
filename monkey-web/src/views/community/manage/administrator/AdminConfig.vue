<template>
    <div class="MonkeyWebAdminConfig-container">
        <div style="margin-bottom: 10px;">
            <el-button type="success" class="el-icon-circle-plus-outline" size="small">&nbsp;新增管理员</el-button>
        </div>
        <el-table
            :data="tableData"
            style="width: 100%">
            <el-table-column
            label="管理员编号"
            align="center"
            width="180">
            </el-table-column>
            <el-table-column
            align="center"
            prop="name"
            label="管理员信息"
            width="300">
            <template slot-scope="scope">
                <img @click="toUserView(scope.row.userId)" class="user-img" src="https://ts3.cn.mm.bing.net/th?id=OIP-C.nkWmM-lReaN8kH-ieXmZrQHaEo&w=316&h=197&c=8&rs=1&qlt=90&o=6&pid=3.1&rm=2" alt="">
                <span @click="toUserView(scope.row.userId)" class="username">啥也不会hh</span>
            </template>
            </el-table-column>
            <el-table-column
            align="center"
            label="管理员简介"
            width="180">
            </el-table-column>
            <el-table-column
            label="添加时间"
            prop="createTime"
            align="center"
            width="200">
            </el-table-column>
            <el-table-column label="操作" align="center">
            <template slot-scope="scope">
                <el-button
                size="mini"
                type="danger"
                @click="handleDelete(scope.$index, scope.row)">删除</el-button>
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
    name: 'MonkeyWebAdminConfig',
    components: {
        PagiNation
    },
    data() {
        return {
            currentPage: 1,
            pageSize: 10,
            totals: 0,
            // 社区id
            communityId: "",
            // 管理员列表
            manageList: [],
            adminConfigUrl: "http://localhost:80/monkey-community/manage/adminConfig",
        };
    },

    created() {
        this.communityId = this.$route.params.communityId;
    },

    methods: {
        // 查询社区管理列表
        queryCommunityManager(communityId) {
            const vue = this;
            $.ajax({
                url: vue.adminConfigUrl + "/queryCommunityManager",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    communityId,
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.manageList = response.data.records;
                        vue.totals = response.data.total;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        handleSizeChange(val) {
            this.pageSize = val;
            this.queryCommunityManager(this.communityId);
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            this.queryCommunityManager(this.communityId);
        },
    },
};
</script>

<style scoped>
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
.MonkeyWebAdminConfig-container {
    padding: 10px;
    background-color: #fff;
}
</style>