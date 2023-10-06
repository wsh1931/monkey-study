<template>
    <div class="MonkeyWebChannelManage-container">
        <div style="margin-bottom: 10px;">
            <span style="margin-right: 10px;">频道名称</span>
            <el-input 
            size="small" 
            placeholder="请输入频道名称" 
            style="width: 200px; margin-right: 20px;" 
            v-model="channelName">
            </el-input>
            <el-button 
            size="small" 
            type="success" 
            @click="openChannelDialog()"
            class="el-icon-circle-plus-outline">新增频道</el-button>
            <!-- 编辑对话框 -->
            <el-dialog
            center
            title="编辑频道信息"
            :visible.sync="addChannelDialog"
            width="30%">
                <el-form :model="addForm" :rules="rules" ref="editForm" label-width="auto" class="demo-editForm">
                    <el-form-item label="频道名称" prop="channelName">
                        <el-input v-model="addForm.channelName"></el-input>
                    </el-form-item>

                    <el-form-item label="频道排序" prop="sort">
                        <el-input v-model.number="addForm.sort"></el-input>
                    </el-form-item>
                    <el-form-item style="text-align: center;">
                        <el-button @click="resetAddForm('editForm')">重置</el-button>
                        <el-button type="primary" @click="submitAddForm('editForm')">确定</el-button>
                    </el-form-item>
                </el-form>
            </el-dialog>
        </div>
        <el-table
        border
        :data="channelList"
        style="width: 100%">
            <el-table-column
            label="频道名称"
            align="center"
            width="180">
            <template slot-scope="scope">
                <span>{{ scope.row.channelName }}</span>
            </template>
            </el-table-column>
            <el-table-column
            label="前端展示"
            align="center"
            width="180">
            <template slot-scope="scope">
                <el-switch
                    v-model="scope.row.supportShow"
                    active-color="#13ce66"
                    inactive-color="#ff4949"
                    :inactive-value="1"
                    @change="updateSupportShow(scope.row)"
                    :active-value="0">
                </el-switch>
            </template>
            </el-table-column>
            <el-table-column
            align="center"
            label="支持用户发表文章"
            width="180">
            <template slot-scope="scope">
                <el-switch
                    v-if="scope.row.channelName == '全部'"
                    disabled
                    v-model="scope.row.supportUserPublish"
                    active-color="#13ce66"
                    inactive-color="#ff4949"
                    :inactive-value="1"
                    :active-value="0">
                </el-switch>
                <el-switch
                    v-else
                    @change="updateSupportUserPublish(scope.row)"
                    v-model="scope.row.supportUserPublish"
                    active-color="#13ce66"
                    inactive-color="#ff4949"
                    :inactive-value="1"
                    :active-value="0">
                </el-switch>
            </template>
            </el-table-column>
            <el-table-column
            align="center"
            label="支持管理员修改"
            width="180">
            <template slot-scope="scope">
                <el-switch
                    v-if="scope.row.channelName == '全部'"
                    disabled
                    v-model="scope.row.supportUserPublish"
                    active-color="#13ce66"
                    inactive-color="#ff4949"
                    :inactive-value="1"
                    :active-value="0">
                </el-switch>
                <el-switch
                    v-else
                    @change="updateSupportManageModify(scope.row)"
                    v-model="scope.row.supportManageModify"
                    active-color="#13ce66"
                    inactive-color="#ff4949"
                    :inactive-value="1"
                    :active-value="0">
                </el-switch>
            </template>
            </el-table-column>
            <el-table-column
            align="center"
            label="频道排序"
            width="180">
            <template slot-scope="scope">
                <span>{{ scope.row.sort }}</span>
            </template>
            </el-table-column>
            <el-table-column label="操作" align="center">
            <template slot-scope="scope">
                <el-button
                size="mini"
                type="primary"
                v-if="scope.row.channelName == '全部'"
                disabled
                @click="editChannel(scope.row)">编辑</el-button>
                <el-button
                size="mini"
                type="primary"
                v-else
                @click="editChannel(scope.row)">编辑</el-button>
                <el-button
                v-if="scope.row.channelName == '全部'"
                disabled
                size="mini"
                type="danger"
                @click="deleteChannel(scope.row)">删除</el-button>
                <el-button
                v-else
                size="mini"
                type="danger"
                @click="deleteChannel(scope.row)">删除</el-button>
            </template>
            </el-table-column>
        </el-table>
        <!-- 编辑对话框 -->
        <el-dialog
        center
        title="编辑频道信息"
        :visible.sync="editChannelDialog"
        width="30%">
            <el-form :model="editForm" :rules="rules" ref="editForm" label-width="auto" class="demo-editForm">
                <el-form-item label="频道名称" prop="channelName">
                    <el-input v-model="editForm.channelName"></el-input>
                </el-form-item>

                <el-form-item label="频道排序" prop="sort">
                    <el-input v-model.number="editForm.sort"></el-input>
                </el-form-item>
                <el-form-item style="text-align: center;">
                    <el-button @click="resetEditForm('editForm')">重置</el-button>
                    <el-button type="primary" @click="submitEditForm('editForm')">确定</el-button>
                </el-form-item>
            </el-form>
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
import store from '@/store';
import PagiNation from '@/components/pagination/PagiNation.vue';
export default {
    name: 'MonkeyWebChannelManage',
    components: {
        PagiNation
    },
    data() {
        var checkSort = (rule, value, callback) => {
        if (!value) {
            return callback(new Error('排序不能为空'));
        }
        setTimeout(() => {
            if (!Number.isInteger(value)) {
            callback(new Error('请输入数字值'));
        } else {
            if (value < -10000 || value > 10000) {
            callback(new Error('数值必需在-10000 到 10000之间'));
            } else {
            callback();
            }
        }
        }, 500);
        };
        return {
            // 新增表单
            addForm: {},
            // 新增频道信息对话框
            addChannelDialog: false,
            // 对话框内容
            dialogContent: {},
            // 编辑频道信息对话框
            editChannelDialog: false,
            name: "",
            // 频道集合
            channelList: [],
            // 社区id
            communityId: "",
            channelName: "",
            currentPage: 1,
            pageSize: 10,
            totals: 0,
            value: '1',
            editForm: {
                name: '',
            },
            rules: {
                channelName: [
                    { required: true, message: '请输入频道名称', trigger: 'blur' },
                    { min: 1, max: 30, message: '长度在 1 到 30 个字符', trigger: 'blur' }
                ],
                sort: [
                    { validator: checkSort, trigger: 'blur' }
                ]
            },
            contentManageUrl: "http://localhost:80/monkey-community/manage/channelManage",
        };
    },

    watch: {
        channelName(newVal) {
            this.currentPage = 1;
            this.pageSize = 10;
            this.queryChannelManageList(newVal);
        },
    },
    created() {
        this.communityId = this.$route.params.communityId;
        this.queryChannelManageList(this.channelName);
    },

    methods: {
        // 新增频道
        addChannel(communityId, addForm) {
            const vue = this;
            $.ajax({
                url: vue.contentManageUrl + "/addChannel",
                type: "post",
                data: {
                    communityId,
                    channelName: addForm.channelName,
                    sort: addForm.sort,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.addChannelDialog = false;
                        vue.queryChannelManageList(vue.channelName);
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 提交新增表单
        submitAddForm(formName) {
            this.$refs[formName].validate((valid) => {
                if (valid) {
                    this.addChannel(this.communityId, this.addForm);
                } else {
                    return false;
                }
            });
        },
        resetAddForm(formName) {
            this.$refs[formName].resetFields();
        },
        // 新增频道
        openChannelDialog() {
            this.addForm = {};
            this.addChannelDialog = true;
        },
        // 删除频道
        deleteChannel(row) {
            this.$modal.confirm("请先将本频道内容迁移到其它频道后，再进行频道删除操作，否则系统将不允许删除")
                .then(() => {
                    const vue = this;
                    $.ajax({
                        url: vue.contentManageUrl + "/deleteChannel",
                        type: "delete",
                        data: {
                            channelId: row.id,
                        },
                        headers: {
                            Authorization: "Bearer " + store.state.user.token,
                        },
                        success(response) {
                            if (response.code == '200') {
                                vue.queryChannelManageList(vue.channelName);
                                vue.$modal.msgSuccess(response.msg);
                            } else {
                                vue.$modal.msgError(response.msg);
                            }
                        }
                    })
                }).catch(() => { });
        },
        // 提交频道编辑
        submitChannelEdit(editForm) {
            const vue = this;
            $.ajax({
                url: vue.contentManageUrl + "/submitChannelEdit",
                type: "put",
                data: {
                    communityChannelStr: JSON.stringify(editForm),
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.editChannelDialog = false;
                        vue.queryChannelManageList(vue.channelName);
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 提交编辑表单
        submitEditForm(formName) {
            this.$refs[formName].validate((valid) => {
                if (valid) {
                    this.submitChannelEdit(this.editForm);
            } else {
                return false;
            }
            });
        },
        resetEditForm(formName) {
            this.$refs[formName].resetFields();
        },
        // 编辑频道信息
        editChannel(row) {
            this.editChannelDialog = true;
            this.editForm = JSON.parse(JSON.stringify(row));
        },
        // 更新是否支持前端展示
        updateSupportShow(row) {
            const vue = this;
            $.ajax({
                url: vue.contentManageUrl + "/updateSupportShow",
                type: "put",
                data: {
                    channelId: row.id,
                    supportShow: row.supportShow,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 更新是否支持用户发表文章
        updateSupportUserPublish(row) {
            const vue = this;
            $.ajax({
                url: vue.contentManageUrl + "/updateSupportUserPublish",
                type: "put",
                data: {
                    channelId: row.id,
                    supportUserPublish: row.supportUserPublish,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 更新是否支持管理员修改
        updateSupportManageModify(row) {
            const vue = this;
            $.ajax({
                url: vue.contentManageUrl + "/updateSupportManageModify",
                type: "put",
                data: {
                    channelId: row.id,
                    supportManageModify: row.supportManageModify,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询社区频道管理列表
        queryChannelManageList(channelName) {
            const vue = this;
            $.ajax({
                url: vue.contentManageUrl + "/queryChannelManageList",
                type: 'get',
                data: {
                    channelName,
                    communityId: vue.communityId,
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.channelList = response.data.records;
                        vue.totals = response.data.total;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        handleSizeChange(val) {
            this.pageSize = val;
            this.queryChannelManageList(this.channelName);
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            this.queryChannelManageList(this.channelName);
        },
    },
};
</script>

<style scoped>
.MonkeyWebChannelManage-container {
    padding: 10px;
    background-color: #fff;
}
</style>