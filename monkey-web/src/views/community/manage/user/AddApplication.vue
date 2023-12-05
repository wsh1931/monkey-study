<template>
    <div class="MonkeyWebAddApplication-container">
        <!-- <el-tabs v-model="activeName" @tab-click="handleClick">
            <el-tab-pane label="待审核" name="wait"></el-tab-pane>
            <el-tab-pane label="已通过" name="success"></el-tab-pane>
            <el-tab-pane label="已拒绝" name="refuse"></el-tab-pane>
        </el-tabs> -->

        <div>
            <span class="approval-status">审核状态</span>
            <el-select v-model="activeName" placeholder="请选择" size="mini" style="margin-right: 10px;">
                <el-option
                v-for="status in approvalStatus"
                :key="status.value"
                :label="status.label"
                :value="status.value">
                </el-option>
            </el-select>
            <el-button 
            v-if="activeName == 'wait' && multipleSelection.length == 0" 
            disabled
            class="el-icon-circle-check" 
            type="success" 
            @click="batchPassUserApplication(multipleSelection)"
            size="mini">&nbsp;批量通过</el-button>
            <el-button 
            v-else-if="activeName == 'wait' && multipleSelection.length != 0" 
            class="el-icon-circle-check" 
            type="success" 
            @click="batchPassUserApplication(multipleSelection)"
            size="mini">&nbsp;批量通过</el-button>
            <el-button 
            v-if="activeName == 'wait'  && multipleSelection.length == 0" 
            class="el-icon-circle-close" 
            disabled
            type="danger" 
            @click="batchRefuseUserApplication(multipleSelection)"
            size="mini">&nbsp;批量拒绝</el-button>
            <el-button 
            v-else-if="activeName == 'wait'  && multipleSelection.length != 0" 
            class="el-icon-circle-close" 
            type="danger" 
            @click="batchRefuseUserApplication(multipleSelection)"
            size="mini">&nbsp;批量拒绝</el-button>
            <el-button 
            v-if="activeName == 'success'" 
            class="el-icon-circle-close" 
            type="danger" 
            @click="batchDeleteUserApplication(multipleSelection)"
            size="mini">&nbsp;批量删除</el-button>
            <el-button 
            v-if="activeName == 'success'" 
            class="el-icon-circle-close" 
            type="danger" 
            @click="deleteAllSuccessRecord(communityId)"
            size="mini">&nbsp;全部删除</el-button>
            <el-button 
            v-if="activeName == 'refuse'" 
            class="el-icon-circle-close" 
            type="danger" 
            @click="batchDeleteUserApplication(multipleSelection)"
            size="mini">&nbsp;批量删除</el-button>
            <el-button 
            v-if="activeName == 'refuse'" 
            class="el-icon-circle-close" 
            type="danger" 
            @click="deleteAllRefuseRecord(communityId)"
            size="mini">&nbsp;全部删除</el-button>
        </div>

        <el-table
        header-align="center"
        ref="multipleTable"
        :data="userApplicationTable"
        tooltip-effect="dark"
        style="width: 100%; margin-top: 10px;"
        @selection-change="handleSelectionChange">
            <el-table-column
            align="center"
            type="selection"
            width="55">
            </el-table-column>
            <el-table-column
            align="center"
            prop="name"
            label="申请人信息"
            width="250">
            <template slot-scope="scope">
                <img @click="toUserView(scope.row.userId)" class="user-img" :src="scope.row.headImg" alt="">
                <span @click="toUserView(scope.row.userId)" class="username">{{ scope.row.username }} </span>
            </template>
            </el-table-column>

            <el-table-column
            v-if="activeName != 'wait'"
            align="center"
            prop="name"
            label="审核人信息"
            width="250">
            <template slot-scope="scope">
                <img @click="toUserView(scope.row.approvalId)" class="user-img" :src="scope.row.approvalHeadImg" alt="">
                <span @click="toUserView(scope.row.approvalId)" class="username">{{ scope.row.approvalUsername }} </span>
            </template>
            </el-table-column>

            <el-table-column
            v-if="activeName == 'wait'"
            align="center"
            prop="createTime"
            label="申请人简介"
            width="360">
            </el-table-column>

            <el-table-column
            align="center"
            prop="createTime"
            label="申请时间"
            width="160">
            </el-table-column>
            <el-table-column
            v-if="activeName != 'wait'"
            align="center"
            prop="updateTime"
            label="审核时间"
            width="160">
            </el-table-column>
            
            <el-table-column
            align="center"
            label="审核状态"
            width="100">
                <template slot-scope="scope">
                    <el-tag v-if="scope.row.status == '1'" type="success" size="medium">已同意</el-tag>
                    <el-tag v-if="scope.row.status == '0'" type="primary" size="medium">待审核</el-tag>
                    <el-tag v-if="scope.row.status == '-1'" type="danger" size="medium">已拒绝</el-tag>
                </template>
            </el-table-column>
            <el-table-column 
            label="操作"
            align="center">
                <template slot-scope="scope">
                    <el-button
                    v-if="scope.row.status == '0'"
                    size="mini"
                    type="success"
                    @click="passApplication(scope.row)">通过</el-button>
                    <el-button
                    v-if="scope.row.status == '0'"
                    size="mini"
                    type="danger"
                    @click="refuseApplication(scope.row)">拒绝</el-button>
                    <el-button
                    v-if="scope.row.status != '0'"
                    size="mini"
                    type="danger"
                    @click="deleteApplicationRecords(scope.row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

        <div style="margin-top: 10px;">
            <PagiNation
            style="text-align: right;"
            :totals="totals"
            :currentPage="currentPage" 
            :pageSize="pageSize" 
            @handleCurrentChange = "handleCurrentChange"
            @handleSizeChange="handleSizeChange"/>
        </div>
    </div>
</template>

<script>
import $ from 'jquery'
import store from '@/store';
import PagiNation from '@/components/pagination/PagiNation.vue';
export default {
    name: 'MonkeyWebAddApplication',
    components: {
        PagiNation
    },
    data() {
        return {
            approvalStatus: [{
                value: 'wait',
                label: '待审核'
            }, {
                value: 'success',
                label: '已通过'
            }, {
                value: 'refuse',
                label: '已拒绝'
            }],
            activeName: "wait",
            currentPage: 1,
            pageSize: 10,
            totals: 0,
            // 社区id
            communityId: "",
            userApplicationTable: [],
            multipleSelection: [],
            userApplicationUrl: "http://localhost:80/monkey-community/manage/userApplication"
        };
    },

    created() {
        this.communityId = this.$route.params.communityId;
        this.queryUserApplicationList(this.communityId);
    },

    methods: {
        // 删除全部拒绝用户申请记录
        deleteAllRefuseRecord(communityId) {
            const vue = this;
            $.ajax({
                url: vue.userApplicationUrl + "/deleteAllRefuseRecord",
                type: "delete",
                data: {
                    communityId
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        if (vue.activeName == 'wait') {
                            vue.queryUserApplicationList(vue.communityId);
                        } else if (vue.activeName == 'success') {
                            vue.querySuccessApplicationList(vue.communityId);
                        } else if (vue.activeName == 'refuse') {
                            vue.queryRefuseApplicationList(vue.communityId);
                        }
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 删除全部已通过用户申请记录
        deleteAllSuccessRecord(communityId) {
            const vue = this;
            $.ajax({
                url: vue.userApplicationUrl + "/deleteAllSuccessRecord",
                type: "delete",
                data: {
                    communityId
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        if (vue.activeName == 'wait') {
                            vue.queryUserApplicationList(vue.communityId);
                        } else if (vue.activeName == 'success') {
                            vue.querySuccessApplicationList(vue.communityId);
                        } else if (vue.activeName == 'refuse') {
                            vue.queryRefuseApplicationList(vue.communityId);
                        }
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 批量删除申请记录
        batchDeleteUserApplication(multipleSelection) {
            const vue = this;
            $.ajax({
                url: vue.userApplicationUrl + "/batchDelete/userApplication",
                type: "delete",
                data: {
                    communityUserApplicationStrList: JSON.stringify(multipleSelection),
                    communityId: vue.communityId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        if (vue.activeName == 'wait') {
                            vue.queryUserApplicationList(vue.communityId);
                        } else if (vue.activeName == 'success') {
                            vue.querySuccessApplicationList(vue.communityId);
                        } else if (vue.activeName == 'refuse') {
                            vue.queryRefuseApplicationList(vue.communityId);
                        }
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 删除申请记录
        deleteApplicationRecords(row) {
            const vue = this;
            const multipleSelection = [row];
            $.ajax({
                url: vue.userApplicationUrl + "/batchDelete/userApplication",
                type: "delete",
                data: {
                    communityUserApplicationStrList: JSON.stringify(multipleSelection),
                    communityId: vue.communityId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        if (vue.activeName == 'wait') {
                            vue.queryUserApplicationList(vue.communityId);
                        } else if (vue.activeName == 'success') {
                            vue.querySuccessApplicationList(vue.communityId);
                        } else if (vue.activeName == 'refuse') {
                            vue.queryRefuseApplicationList(vue.communityId);
                        }
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询已拒绝申请列表
        queryRefuseApplicationList(communityId) {
            const vue = this;
            $.ajax({
                url: vue.userApplicationUrl + "/queryRefuseApplicationList",
                type: "get",
                data: {
                    communityId,
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.userApplicationTable = response.data.records;
                        vue.totals = response.data.total;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询已通过申请列表
        querySuccessApplicationList(communityId) {
            const vue = this;
            $.ajax({
                url: vue.userApplicationUrl + "/querySuccessApplicationList",
                type: "get",
                data: {
                    communityId,
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.userApplicationTable = response.data.records;
                        vue.totals = response.data.total;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 批量拒绝用户申请
        batchRefuseUserApplication(multipleSelection) {
            const vue = this;
            $.ajax({
                url: vue.userApplicationUrl + "/batchRefuse/userApplication",
                type: "post",
                data: {
                    communityUserApplicationStrList: JSON.stringify(multipleSelection),
                    communityId: vue.communityId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        if (vue.activeName == 'wait') {
                            vue.queryUserApplicationList(vue.communityId);
                        } else if (vue.activeName == 'success') {
                            vue.querySuccessApplicationList(vue.communityId);
                        } else if (vue.activeName == 'refuse') {
                            vue.queryRefuseApplicationList(vue.communityId);
                        }
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 批量通过用户申请
        batchPassUserApplication(multipleSelection) {
            const vue = this;
            $.ajax({
                url: vue.userApplicationUrl + "/batchPass/userApplication",
                type: "post",
                data: {
                    communityUserApplicationStrList: JSON.stringify(multipleSelection),
                    communityId: vue.communityId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        if (vue.activeName == 'wait') {
                            vue.queryUserApplicationList(vue.communityId);
                        } else if (vue.activeName == 'success') {
                            vue.querySuccessApplicationList(vue.communityId);
                        } else if (vue.activeName == 'refuse') {
                            vue.queryRefuseApplicationList(vue.communityId);
                        }
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 拒绝用户加入社区
        refuseApplication(row) {
            const vue = this;
            const multipleSelection = [row];
            $.ajax({
                url: vue.userApplicationUrl + "/batchRefuse/userApplication",
                type: "post",
                data: {
                    communityUserApplicationStrList: JSON.stringify(multipleSelection),
                    communityId: vue.communityId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        if (vue.activeName == 'wait') {
                            vue.queryUserApplicationList(vue.communityId);
                        } else if (vue.activeName == 'success') {
                            vue.querySuccessApplicationList(vue.communityId);
                        } else if (vue.activeName == 'refuse') {
                            vue.queryRefuseApplicationList(vue.communityId);
                        }
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 通过申请加入社区
        passApplication(row) {
            const vue = this;
            const multipleSelection = [row];
            $.ajax({
                url: vue.userApplicationUrl + "/batchPass/userApplication",
                type: "post",
                data: {
                    communityUserApplicationStrList: JSON.stringify(multipleSelection),
                    communityId: vue.communityId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        if (vue.activeName == 'wait') {
                            vue.queryUserApplicationList(vue.communityId);
                        } else if (vue.activeName == 'success') {
                            vue.querySuccessApplicationList(vue.communityId);
                        } else if (vue.activeName == 'refuse') {
                            vue.queryRefuseApplicationList(vue.communityId);
                        }
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        handleClick() {
            this.currentPage = 1;
            this.pageSize = 10;
            if (this.activeName == 'wait') {
                this.queryUserApplicationList(this.communityId);
            } else if (this.activeName == 'success') {
                this.querySuccessApplicationList(this.communityId);
            } else if (this.activeName == 'refuse') {
                this.queryRefuseApplicationList(this.communityId);
            }
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
        // 查询用户申请列表
        queryUserApplicationList(communityId) {
            const vue = this;
            $.ajax({
                url: vue.userApplicationUrl + "/queryUserApplicationList",
                type: "get",
                data: {
                    communityId,
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.userApplicationTable = response.data.records;
                        vue.totals = response.data.total;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        toggleSelection(rows) {
            if (rows) {
            rows.forEach(row => {
                this.$refs.multipleTable.toggleRowSelection(row);
            });
            } else {
            this.$refs.multipleTable.clearSelection();
            }
        },
        handleSelectionChange(val) {
            this.multipleSelection = val;
        },
        handleSizeChange(val) {
            this.pageSize = val;
            if (this.activeName == 'wait') {
                this.queryUserApplicationList(this.communityId);
            } else if (this.activeName == 'success') {
                this.querySuccessApplicationList(this.communityId);
            } else if (this.activeName == 'refuse') {
                this.queryRefuseApplicationList(this.communityId);
            }
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            if (this.activeName == 'wait') {
                this.queryUserApplicationList(this.communityId);
            } else if (this.activeName == 'success') {
                this.querySuccessApplicationList(this.communityId);
            } else if (this.activeName == 'refuse') {
                this.queryRefuseApplicationList(this.communityId);
            }
        },
    },
};
</script>

<style scoped>
.approval-status {
    margin-right: 10px;
    font-size: 14px;
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
.MonkeyWebAddApplication-container {
    padding: 10px;
    background-color: #fff;
}
</style>