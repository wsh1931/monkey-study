<template>
    <div class="MonkeyWebRoleManage-container">
        <div>
            <el-button 
            @click="addRoleDialog = true"
            style="vertical-align: middle;" 
            size="mini" 
            class="el-icon-circle-plus-outline"  
            type="primary">&nbsp;新增角色</el-button>
        </div>
        <!-- 新增角色 -->
        <el-dialog
        title="新增角色"
        :visible.sync="addRoleDialog"
        width="40%"
        center>
            <el-form 
            :model="addForm" 
            :rules="addRules" 
            ref="addForm" 
            label-width="auto" 
            class="demo-roleForm"  
            style="max-height: 400px; 
            overflow-y: auto;">
                <el-form-item label="角色名称" prop="roleName">
                    <el-input placeholder="请输入用户角色" v-model="addForm.roleName"></el-input>
                </el-form-item>
                <el-form-item label="紧急条件" prop="promotionCondition">
                    <el-input 
                    :autosize="{ minRows: 2, maxRows: 6}" 
                    maxlength="255" 
                    show-word-limit 
                    type="textarea" 
                    placeholder="暂不支持用户自动晋级"
                    v-model="addForm.promotionCondition"></el-input>
                </el-form-item>
                <el-form-item label="相关权益" prop="relatedBenefit">
                    <el-input 
                    :autosize="{ minRows: 2, maxRows: 6}" 
                    maxlength="255" 
                    show-word-limit 
                    type="textarea" 
                    placeholder="暂不支持用户自动获取相关权益"
                    v-model="addForm.relatedBenefit"></el-input>
                </el-form-item>

                <el-form-item label="下设头衔" prop="downName">
                    <el-input 
                    placeholder="请输入下设头衔"
                    type="textarea" 
                    v-model="addForm.downName"></el-input>
                    <div>若想录入多个称号，可以使用中文逗号【,】进行分割</div>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="resetAddForm('addForm')">重 置</el-button>
                <el-button type="primary" @click="submitAddForm('addForm')">确 定</el-button>
            </span>
        </el-dialog>
        <el-table
        border
        :data="roleManageList"
        style="width: 100%; margin-top: 10px;">
        <el-table-column
        align="center"
        label="用户角色"
        width="180">
        <template slot-scope="scope">
            <span>{{ scope.row.roleName }}</span>
        </template>
        </el-table-column>
        <el-table-column
        align="center"
        label="下设头衔"
        width="180">
        <template slot-scope="scope">
            <el-tag 
            style="margin-right: 5px; margin-bottom: 5px;"
            v-for="downNameVo in scope.row.downNameVoList" 
            :key="downNameVo.downName" 
            type="info" 
            size="medium">{{ downNameVo.downName }}</el-tag>
        </template>
        </el-table-column>

        <el-table-column
        align="center"
        label="对应用户数"
        width="180">
        <template slot-scope="scope">
            <span class="userCount" @click="toUserManageViews(scope.row)">{{ scope.row.userCount }}</span>
        </template>
        </el-table-column>
        <el-table-column
        align="center"
        label="晋级条件"
        width="180">
        <template slot-scope="scope">
            <span>{{ scope.row.promotionCondition }}</span>
        </template>
        </el-table-column>

        <el-table-column
        align="center"
        label="相关权益"
        width="180">
        <template slot-scope="scope">
            <span>{{ scope.row.relatedBenefit }}</span>
        </template>
        </el-table-column>

        <el-table-column label="操作" align="center">
        <template slot-scope="scope">
            <el-button
            size="mini"
            type="primary"
            @click="handleEdit(scope.row)">编辑</el-button>
            <el-button
            v-if="scope.row.roleName == '社区成员'"
            disabled
            size="mini"
            type="danger"
            @click="handleDelete(scope.row)">删除</el-button>
            <el-button
            v-else
            size="mini"
            type="danger"
            @click="handleDelete(scope.row)">删除</el-button>
        </template>
        </el-table-column>
    </el-table>

    <!-- 编辑对话框 -->
    <el-dialog
    :title="'编辑' + dialogContent.roleName + '的角色信息'"
    :visible.sync="isShowEdit"
    width="40%"
    center>
        <el-form 
        :model="dialogContent" 
        :rules="editRules" 
        ref="roleForm" 
        label-width="auto" 
        class="demo-roleForm" 
        style="max-height: 400px; overflow-y: auto;">
            <el-form-item label="角色名称" prop="roleName">
                <el-input v-model="dialogContent.roleName"></el-input>
            </el-form-item>
            <el-form-item label="紧级条件">
                <el-input 
                :autosize="{ minRows: 2, maxRows: 6}" 
                maxlength="255" 
                show-word-limit 
                type="textarea" 
                v-model="dialogContent.promotionCondition"></el-input>
            </el-form-item>
            <el-form-item label="相关权益">
                <el-input 
                :autosize="{ minRows: 2, maxRows: 6}" 
                maxlength="255" 
                show-word-limit 
                type="textarea" 
                v-model="dialogContent.relatedBenefit"></el-input>
            </el-form-item>

            <el-form-item label="下设头衔">
                <div v-for="(downNameVo, index) in dialogContent.downNameVoList" :key="index">
                    <el-input 
                    disabled
                    v-if="downNameVo.isPreserve == '1'"
                    v-model="downNameVo.downName" 
                    size="small" 
                    style="width: 360px; margin-right: 10px;"></el-input>

                    <el-input 
                    v-if="downNameVo.isPreserve == '0'"
                    v-model="downNameVo.downName" 
                    size="small" 
                    style="width: 360px; margin-right: 10px;"></el-input>

                    <el-button 
                    v-if="downNameVo.isPreserve == '0'"
                    type="primary" 
                    style="border-radius: 20px; vertical-align: middle;" 
                    @click="preserveDownName(dialogContent.id, downNameVo)"
                    size="mini">保存</el-button>

                    <span 
                    v-if="downNameVo.isPreserve == '1'"
                    class="el-icon-delete 
                    delete-extra"
                    @click="deleteDownName(dialogContent.id, downNameVo, index)"></span>
                </div>
                <div>
                    <el-button 
                    type="primary" 
                    size="mini" 
                    style="border-radius: 20px;" 
                    class="el-icon-circle-plus-outline"
                    @click="addOtherRoleName()"
                    plain>增加下设头衔</el-button>
                </div>
            </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
            <el-button type="primary" @click="submitEditForm('roleForm', dialogContent)">提 交</el-button>
        </span>
    </el-dialog>

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
import PagiNation from '@/components/pagination/PagiNation.vue';
import store from '@/store';
export default {
    name: 'MonkeyWebRoleManage',
    components: {
        PagiNation,
    },
    data() {
        return {
            // 角色管理列表
            roleManageList: [],
            // 社区id
            communityId: "",
            // 新增角色对话框
            addRoleDialog: false,
            // 新增角色表单
            addForm: {
                roleName: '',
                promotionCondition: "",
                relatedBenefit: "",
                downName:""
            },
            currentPage: 1,
            pageSize: 10,
            totals: 0,
            // 对话框内容
            dialogContent: false,
            // 是否展示编辑框
            isShowEdit: false,
            roleForm: {
                roleName: '',
                promotionCondition: "",
                relatedBenefit: "",
                downNameList: [''],
            },
            editRules: {
                roleName: [
                    { required: true, message: '请输入角色名称', trigger: 'blur' },
                    { min: 1, max: 30, message: '长度在 1 到 30 个字符', trigger: 'blur' }
                ],
            },
            addRules: {
                roleName: [
                    { required: true, message: '请输入角色名称', trigger: 'blur' },
                    { min: 1, max: 30, message: '长度在 1 到 30 个字符', trigger: 'blur' }
                ],
            },
            roleManageUrl: "http://localhost:80/monkey-community/manage/roleManage"
        };
    },

    created() {
        this.communityId = this.$route.params.communityId;
        this.queryRoleManageList(this.communityId);
    },

    methods: {
        // 删除用户角色
        handleDelete(row) {
            this.$modal.confirm("确定删除此角色？").then(() => {
                const vue = this;
                $.ajax({
                    url: vue.roleManageUrl + "/deleteRole",
                    type: "delete",
                    data: {
                        roleId: row.id,
                        communityId: vue.communityId,
                    },
                    headers: {
                        Authorization: "Bearer " + store.state.user.token,
                    },
                    success(response) {
                        if (response.code == vue.ResultStatus.SUCCESS) {
                            vue.queryRoleManageList(vue.communityId);
                            vue.$modal.msgSuccess(response.msg);
                        } else {
                            vue.$modal.msgError(response.msg);
                        }
                    }
                })
            }).catch(() => { });
        },
        // 保存下设头衔
        preserveDownName(roleId, downNameVo) {
            const vue = this;
            $.ajax({
                url: vue.roleManageUrl + "/preserveDownName",
                type: "post",
                data: {
                    roleId,
                    downName: downNameVo.downName,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.$modal.msgSuccess(response.msg);
                        downNameVo.isPreserve = '1';
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询角色管理列表
        queryRoleManageList(communityId) {
            const vue = this;
            $.ajax({
                url: vue.roleManageUrl + "/queryRoleManageList",
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
                        vue.roleManageList = response.data.records;
                        vue.totals = response.data.total;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 前往用户管理界面
        toUserManageViews(row) {
            this.$router.push({
                path: "userManage",
                query: {
                    event: "to_community_manage",
                    roleName: row.roleName,
                    roleId: row.id,
                }
            })
        },
        // 删除下设头衔
        deleteDownName(roleId, downNameVo, index) {
            const vue = this;
            $.ajax({
                url: vue.roleManageUrl + "/deleteDownName",
                type: "delete",
                data: {
                    roleId,
                    downName: downNameVo.downName,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.$modal.msgSuccess(response.msg);
                        vue.dialogContent.downNameVoList.splice(index, 1);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
            
        },
        // 添加下设头像
        addOtherRoleName() {
            this.dialogContent.downNameVoList.push({downName: "", isPreserve: '0'});
        },
        // 提交编辑角色表单
        submitEditForm(formName, dialogContent) {
            this.$refs[formName].validate((valid) => {
                if (valid) {
                    this.submitEditRole(dialogContent);
            } else {
                console.log('error submit!!');
                return false;
            }
            });
        },
        // 提交编辑角色信息
        submitEditRole(communityRole) {
            const vue = this;
            $.ajax({
                url: vue.roleManageUrl + "/submitEditRole",
                type: "put",
                data: {
                    communityRoleStr: JSON.stringify(communityRole),
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.queryRoleManageList(vue.communityId);
                        vue.$modal.msgSuccess(response.msg);
                        vue.isShowEdit = false;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 重置编辑角色表单
        resetEditForm(formName) {
            this.$refs[formName].resetFields();
        },
        // 重置添加角色表单
        resetAddForm(formName) {
            this.$refs[formName].resetFields();
        },
        // 提交添加角色表单
        submitAddForm(formName) {
            this.$refs[formName].validate((valid) => {
                if (valid) {
                    this.addRole(this.addForm);
            } else {
                return false;
            }
            });
        },
        // 新增角色
        addRole(communityRole) {
            const vue = this;
            $.ajax({
                url: vue.roleManageUrl + "/addRole",
                type: "post",
                data: {
                    communityRoleStr: JSON.stringify(communityRole),
                    communityId: vue.communityId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.addRoleDialog = false;
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 展示编辑框
        handleEdit(row) {
            this.dialogContent = JSON.parse(JSON.stringify(row));
            this.isShowEdit = true;
        },
        handleSizeChange(val) {
            this.pageSize = val;
        },
        handleCurrentChange(val) {
            this.currentPage = val;
        },
    },
};
</script>

<style scoped>
.userCount {
    cursor: pointer;
    color: #409EFF;
}
.delete-extra:hover {
    color: red;
    cursor: pointer;
}
.delete-extra {
    margin-left: 10px;
    font-size: 18px;
    vertical-align: middle;
}
.MonkeyWebRoleManage-container {
    padding: 10px;
    background-color: #fff;
}
</style>