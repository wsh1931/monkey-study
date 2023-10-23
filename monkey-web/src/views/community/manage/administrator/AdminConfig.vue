<template>
    <div class="MonkeyWebAdminConfig-container">
        <div style="margin-bottom: 10px;">
            <span style="margin-right: 10px;">管理员编号</span>
            <el-input 
            size="small" 
            placeholder="请输入管理员编号" 
            style="width: 200px; margin-right: 20px;" 
            v-model="manageIdx">
            </el-input>
            <el-button 
            @click="addManageDialog = true" 
            type="success" 
            class="el-icon-circle-plus-outline" 
            size="small">&nbsp;新增管理员</el-button>
        </div>
        <!-- 新增管理员对话框 -->
        <el-dialog
        title="请输入新增管理员编号"
        :visible.sync="addManageDialog"
        width="30%">
            <span style="margin-right: 10px;">管理员编号</span>
            <el-input 
            size="small" 
            placeholder="请输入新增管理员编号" 
            style="width: 200px; margin-right: 20px;" 
            v-model="addManageId">
            </el-input>
        <span slot="footer" class="dialog-footer">
            <el-button @click="addManageDialog = false">取 消</el-button>
            <el-button type="primary" @click="addManager(communityId, addManageId)">确 定</el-button>
        </span>
        </el-dialog>
        <el-table
            :data="manageList"
            style="width: 100%">
            <el-table-column
            label="管理员编号"
            prop="userId"
            align="center"
            width="180">
            </el-table-column>
            <el-table-column
            align="center"
            prop="name"
            label="管理员信息"
            width="300">
            <template slot-scope="scope">
                <img @click="toUserViews(scope.row.userId)" class="user-img" :src="scope.row.headImg" alt="">
                <span @click="toUserViews(scope.row.userId)" class="username">{{ scope.row.username }}</span>
            </template>
            </el-table-column>
            <el-table-column
            align="center"
            label="管理员名称"
            width="180">
            <template slot-scope="scope">
                <span v-if="scope.row.isPrime == '1'">主管理员</span>
                <span v-else>管理员</span>
            </template>
            </el-table-column>
            <el-table-column
            label="添加时间"
            prop="createTime"
            align="center"
            width=vue.ResultStatus.SUCCESS>
            </el-table-column>
            <el-table-column label="操作" align="center">
            <template slot-scope="scope">
                <el-button
                v-if="scope.row.isPrime == '1'"
                disabled
                size="mini"
                type="danger"
                @click="deleteManager(scope.row)">删除</el-button>
                <el-button
                v-else
                size="mini"
                type="danger"
                @click="deleteManager(scope.row)">删除</el-button>
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
            // 新增管理员id
            addManageId: "",
            // 新增管理员
            addManageDialog: false,
            // 管理员编号
            manageIdx: "",
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

    watch: {
        manageIdx(newVal) {
            this.queryCommunityManager(newVal, this.communityId);
        },
    },
    created() {
        this.communityId = this.$route.params.communityId;
        this.queryCommunityManager(this.manageIdx, this.communityId);
    },

    methods: {
        // 删除管理员
        deleteManager(row) {
            const vue = this;
            $.ajax({
                url: vue.adminConfigUrl + "/deleteManager",
                type: "delete",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    communityManageId: row.id,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.queryCommunityManager(vue.manageIdx, vue.communityId);
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 新增管理员
        addManager(communityId, userId) {
            const vue = this;
            $.ajax({
                url: vue.adminConfigUrl + "/addManager",
                type: "post",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    communityId,
                    userId,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.queryCommunityManager(vue.manageIdx, vue.communityId);
                        vue.$modal.msgSuccess(response.data);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
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
        // 查询社区管理列表
        queryCommunityManager(manageIdx, communityId) {
            const vue = this;
            $.ajax({
                url: vue.adminConfigUrl + "/queryCommunityManager",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    manageIdx,
                    communityId,
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
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
            this.queryCommunityManager(this.manageIdx, this.communityId);
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            this.queryCommunityManager(this.manageIdx, this.communityId);
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