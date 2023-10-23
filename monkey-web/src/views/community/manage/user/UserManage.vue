<template>
    <div class="MonkeyWebUserManage-container">
        <div style="font-size: 14px;">
            <el-form :inline="true" label-position="left" :model="queryForm" ref="queryForm" label-width="auto" class="demo-queryForm">
                <el-form-item label="用户角色" prop="roleName">
                    <span>
                        <el-select 
                        size="mini" 
                        v-model="queryForm.roleId" 
                        clearable 
                        placeholder="请选择">
                        <el-option
                            v-for="role in roleList"
                            :key="role.id"
                            :label="role.roleName"
                            :value="role.id">
                        </el-option>
                        </el-select>
                    </span>
                </el-form-item>
                <el-form-item label="用户编号" prop="username">
                    <el-input 
                    class="search-input" 
                    v-model="queryForm.userId" 
                    type="text" 
                    placeholder="请输入用户编号"
                    size="mini"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button size="mini" type="primary" @click="submitForm('queryForm')">查询</el-button>
                    <el-button size="mini" @click="resetForm('queryForm')">重置</el-button>
                </el-form-item>
            </el-form>
        </div>
        <div>
            <el-button @click="exportData(userList)" type="primary" class="el-icon-download" size="mini">&nbsp;导出明细</el-button>
            <el-button @click="inviteUserDialog = true" class="el-icon-circle-plus-outline" type="success" size="mini">&nbsp;邀请用户加入</el-button>
        </div>
        <!-- 邀请用户加入 -->
            <el-dialog
            top="200px"
            title="邀请用户"
            :visible.sync="inviteUserDialog"
            width="30%">
            <div>
                <span style="margin-right: 10px;">用户编号</span>
                    <el-input 
                    class="search-input" 
                    v-model="invite.userId" 
                    type="text" 
                    placeholder="请输入用户编号"></el-input>
            </div>

            <div style="margin-top: 10px;">
                <span style="margin-right: 10px;">用户角色</span>
                <el-select 
                v-model="invite.roleId" 
                clearable 
                placeholder="请选择">
                <el-option
                    v-for="role in roleList"
                    :key="role.id"
                    :label="role.roleName"
                    :value="role.id">
                </el-option>
                </el-select>
            </div>
                <span slot="footer" class="dialog-footer">
                    <el-button @click="inviteUserDialog = false">取 消</el-button>
                    <el-button type="primary" @click="inviteUser(communityId, invite)">确 定</el-button>
                </span>
            </el-dialog>
        <div>
            <el-table
            border
                :data="userList"
                style="width: 100%; margin-top: 10px;">
                <el-table-column
                align="center"
                label="用户id"
                width="220">
                <template slot-scope="scope">
                    <span @click="toUserView(scope.row.id)" class="username">{{ scope.row.id }}</span>
                </template>
                </el-table-column>

                <el-table-column
                align="center"
                label="用户信息"
                width=vue.ResultStatus.SUCCESS>
                <template slot-scope="scope">
                    <img @click="toUserView(scope.row.id)" class="user-img" :src="scope.row.headImg" alt="">
                    <span @click="toUserView(scope.row.id)" class="username">{{ scope.row.username }}</span>
                </template>
                </el-table-column>
                <el-table-column
                align="center"
                label="用户角色"
                width=vue.ResultStatus.SUCCESS>
                <template slot-scope="scope">
                    <el-tag type="info">{{ scope.row.roleName }}</el-tag>
                </template>
                </el-table-column>
                <el-table-column
                align="center"
                label="加入日期"
                width=vue.ResultStatus.SUCCESS>
                <template slot-scope="scope">
                    <i class="el-icon-time"></i>
                    <span style="margin-left: 5px">{{ scope.row.createTime }}</span>
                </template>
                </el-table-column>
                <el-table-column label="操作" align="center">
                <template slot-scope="scope">
                    <el-button
                    size="mini"
                    type="success"
                    @click="toUserChatViews(scope.row.id)">私信</el-button>
                    <el-button
                    v-if="scope.row.roleId == '1'"
                    size="mini"
                    disabled
                    type="primary">编辑用户角色</el-button>
                    <el-button
                    v-else-if="scope.row.roleId == '2' && roleId != '1'"
                    size="mini"
                    disabled
                    type="primary">编辑用户角色</el-button>
                    <el-button
                    v-else
                    size="mini"
                    type="primary"
                    @click="editUserRole(scope.row)">编辑用户角色</el-button>
                    <el-button
                    v-if="scope.row.roleId == '1'"
                    disabled
                    size="mini"
                    type="danger"
                    >移除成员</el-button>
                    <el-button
                    v-else-if="scope.row.roleId == '2' && roleId != '1'"
                    disabled
                    size="mini"
                    type="danger">移除成员</el-button>
                    <el-button
                    v-else
                    size="mini"
                    type="danger"
                    @click="deleteUserRole(scope.row.id, communityId)">移除成员</el-button>
                </template>
                </el-table-column>
            </el-table>

            <!-- 编辑用户角色 -->
            <el-dialog
            center
            top="250px"
            :title="'编辑' + dialogContent.username + '的角色'"
            :visible.sync="isShowRoleDialog"
            width="30%">
            <span style="margin-right: 10px;">用户角色</span>
                <el-select 
                @change="isSelectedRoleInfo()"
                v-model="dialogContent.roleName" 
                clearable 
                placeholder="请选择">
                <el-option
                    v-for="role in roleList"
                    :key="role.id"
                    :label="role.roleName"
                    :value="role.id">
                </el-option>
                </el-select>
                <span slot="footer" class="dialog-footer">
                    <el-button 
                    type="primary" 
                    v-if="isSelectedRole"
                    @click="updateUserRole(dialogContent.roleName, dialogContent.id, communityId)">确 定</el-button>
                    <el-button 
                    type="primary" 
                    v-if="!isSelectedRole"
                    disabled
                    @click="updateUserRole(dialogContent.roleName, dialogContent.id, communityId)">确 定</el-button>
                    <el-button @click="isShowRoleDialog = false">取 消</el-button>
                </span>
            </el-dialog>
        </div>
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
    name: 'MonkeyWebUserManage',
    components: {
        PagiNation
    },
    data() {
        return {
            // 邀请用户
            invite: {
                userId: "",
                roleId: "",
            },
            // 邀请用户对话框
            inviteUserDialog: false,
            // 当前分页查询状态，1表示全部查询，2表示模糊查询
            status: "1",
            // 判断用户是否选择角色
            isSelectedRole: false,
            // 社区角色列表
            roleList: [],
            // 对话框内容
            dialogContent: {},
            // 是否显示用户角色对话框
            isShowRoleDialog: false,
            // 社区id
            communityId: "",
            // 用户集合列表
            userList: [],
            // 当前登录用户角色id
            roleId: "",
            value: "",
            currentPage: 1,
            pageSize: 10,
            totals: 0,
            queryForm: {
                userId: "",
                roleId: "",
            },
            userManageUrl: "http://localhost:80/monkey-community/manage/userManage",
        }
    },
    created() {
        this.communityId = this.$route.params.communityId;
        this.queryRoleList(this.communityId)
        console.log(this.$route.query.roleId);
        if (this.$route.query.roleId != null && this.$route.query.roleId != "") {
            this.queryForm.roleId = this.$route.query.roleName;
            this.queryUserListByVagueFromRomeManage(this.$route.query.roleId);
        } else {
            this.queryUserInfoList(this.communityId);
        }
        
    },
    methods: {
        // 从角色管理跳转，模糊查询用户列表
        queryUserListByVagueFromRomeManage(roleId) {
            const vue = this;
            $.ajax({
                url: vue.userManageUrl + "/queryUserListByVague",
                type: "get",
                data: {
                    roleId,
                    userId: "",
                    communityId: vue.communityId,
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.userList = response.data.data.records;
                        vue.totals = response.data.data.total;
                        vue.status = "2";
                    }  else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 邀请用户
        inviteUser(communityId, invite) {
            if (invite.userId == null || invite.userId == "") {
                this.$modal.msgWarning("请输入用户编号");
                return;
            }

            if (invite.roleId == null || invite.roleId == "") {
                this.$modal.msgWarning("请选择用户角色");
                return;
            }
            const vue = this;
            $.ajax({
                url: this.userManageUrl + "/inviteUser",
                type: "post",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    communityId,
                    inviteUserId: invite.userId,
                    inviteRoleId: invite.roleId,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.inviteUserDialog = false;
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 导出数据
        exportData(userList) {
            var url = this.userManageUrl + "/exportData?";
            // 将每个articleTask对象转换为查询参数
            var param = "userList=" + encodeURIComponent(JSON.stringify(userList));
            // 添加到URL中
            url += param;
            window.location.href = url;
        },
        // 模糊查询用户列表
        queryUserListByVague(queryForm) {
            const vue = this;
            $.ajax({
                url: vue.userManageUrl + "/queryUserListByVague",
                type: "get",
                data: {
                    roleId: queryForm.roleId,
                    userId: queryForm.userId,
                    communityId: vue.communityId,
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.userList = response.data.data.records;
                        vue.totals = response.data.data.total;
                        vue.status = "2";
                    }  else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 删除用户角色
        deleteUserRole(userId, communityId) {
            this.$modal.confirm("确定将此成员从该社区删除？").then(() => {
                const vue = this;
                $.ajax({
                    url: vue.userManageUrl + "/deleteUserRole",
                    type: "delete",
                    data: {
                        userId,
                        communityId,
                    },
                    headers: {
                        Authorization: "Bearer " + store.state.user.token,
                    },
                    success(response) {
                        if (response.code == vue.ResultStatus.SUCCESS) {
                            vue.queryUserInfoList(communityId);
                            vue.$modal.msgSuccess(response.msg);
                        } else {
                            vue.$modal.msgError(response.msg);
                        }
                    }
                })
            }).catch(() => {})
        },
        // 是否选择了角色
        isSelectedRoleInfo() {
            this.isSelectedRole = true;
        },
        // 更新用户角色
        updateUserRole(roleId, userId, communityId) {
            const vue = this;
            $.ajax({
                url: vue.userManageUrl + "/updateUserRole",
                type: "put",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    roleId,
                    userId,
                    communityId,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.isShowRoleDialog = false;
                        vue.$modal.msgSuccess(response.msg);
                        vue.queryUserInfoList(vue.communityId);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 得到社区角色列表
        queryRoleList(communityId) {
            const vue = this;
            $.ajax({
                url: vue.userManageUrl + "/queryRoleList",
                type: "get",
                data: {
                    communityId
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.roleList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 打开编辑用户角色框
        editUserRole(row) {
            this.dialogContent = JSON.parse(JSON.stringify(row));
            this.isShowRoleDialog = true;
        },
        // 前往用户聊天页面
        toUserChatViews(receiverId) {
            const { href } = this.$router.resolve({
                name: "webSocket_chat",
                params: {
                    receiverId
                }
            })

            window.open(href, "_blank")
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
        // 查询用户信息集合
        queryUserInfoList(communityId) {
            const vue = this;
            $.ajax({
                url: vue.userManageUrl + "/queryUserInfoList",
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
                        vue.status = '1';
                        vue.userList = response.data.data.records;
                        vue.totals = response.data.data.total;
                        vue.roleId = response.data.roleId;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        submitForm(formName) {
            const vue = this;
        this.$refs[formName].validate((valid) => {
            if (valid) {
                vue.currentPage = 1;
                vue.pageSize = 10;
                vue.queryUserListByVague(vue.queryForm);
            } else {
                console.log('error submit!!');
                return false;
            }
            });
        },
        resetForm(formName) {
            this.$refs[formName].resetFields();
            this.status = '1';
            this.queryUserInfoList(this.communityId);
        },
        handleSizeChange(val) {
            this.pageSize = val;
            if (this.status == '1') {
                this.queryUserInfoList(this.communityId);
            } else if (this.status == '2') {
                this.queryUserListByVague(this.queryForm);
            }
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            if (this.status == '1') {
                this.queryUserInfoList(this.communityId);
            } else if (this.status == '2') {
                this.queryUserListByVague(this.queryForm);
            }
        },
    },
};
</script>

<style scoped>
::v-deep .el-dialog--center .el-dialog__body {
    padding: 10px 20px;

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
.search-input {
    width: 200px;
    vertical-align: middle;
}
.MonkeyWebUserManage-container {
    background-color: #fff;
    padding: 20px;
}
</style>