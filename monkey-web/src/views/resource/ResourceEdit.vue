<template>
    <div class="MonkeyWebResourceEdit-container">
        <el-form 
        :model="fileForm" 
        :rules="rules" 
        ref="fileForm" 
        label-width="auto" 
        class="demo-fileForm"
        @submit.native.prevent>
            <el-form-item label="上传资源" prop="url" class="upload-resource">
                <ElUploadResource
                ref="uploadResourceChild"
                style="margin-top: 10px; z-index: 1;"
                @uploadSuccess="uploadSuccess"
                @removeUpload="removeUpload"
                :isSubmit="isSubmit"/>
            </el-form-item>
            <el-form-item label="资源名称" prop="name">
                <el-input 
                type="textarea"
                placeholder="请输入资源名称"
                maxlength="100"
                :autosize="{ minRows: 2, maxRows: 6 }"
                show-word-limit
                v-model="fileForm.name"
                ></el-input>
            </el-form-item>
            <el-form-item label="资源描述" prop="description">
                <el-input 
                type="textarea"
                placeholder="请输入资源描述"
                maxlength="500"
                :autosize="{ minRows: 4, maxRows: 6 }"
                show-word-limit
                v-model="fileForm.description"
                ></el-input>
            </el-form-item>
            <el-form-item label="资源标签" prop="resourceLabelList">
                <el-tag
                :key="label"
                v-for="label in fileForm.resourceLabelList"
                closable
                :disable-transitions="false"
                @close="handleCloseLabel(label)">
                {{label}}
                </el-tag>
                <el-input
                class="input-new-tag"
                v-if="addLabelVisible"
                v-model="labelValue"
                ref="saveTagInput"
                size="small"
                @keyup.enter.native="handleInputConfirm()"
                @blur="handleInputConfirm()"
                >
                </el-input>
                <el-button 
                v-else 
                class="button-new-tag 
                el-icon-circle-plus-outline" 
                size="small"
                @click="showLabelInput()">&nbsp;新增资源</el-button>
            </el-form-item>
            <el-form-item label="所属分类" required prop="resourceClassification">
                <el-cascader
                    clearable
                    v-model="fileForm.resourceClassification"
                    :options="resourceClassificationList"
                    @change="handleChange">
                </el-cascader>
            </el-form-item>
            <el-form-item label="发布形式" prop="formTypeId">
                <el-radio-group v-model="fileForm.formTypeId">
                <el-radio :label="1">免费</el-radio>
                <el-radio :label="2">会员免费</el-radio>
                <el-radio :label="3">收费</el-radio>
                <el-input
                v-model.number="fileForm.price"
                v-if="fileForm.formTypeId == '3'"
                placeholder="请输入收费金额，单位（元）" 
                style="display: inline-block; width: 300px"></el-input>
                </el-radio-group>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="uploadFileForm('fileForm')">提交编辑</el-button>
                <el-button @click="resetFileForm('fileForm')">重置</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script>
import $ from 'jquery'
import store from '@/store';
import ElUploadResource from '@/components/upload/ElUploadResource'
export default {
    name: 'MonkeyWebResourceEdit',
    components: {
        ElUploadResource
    },
    data() {
        return {
            resourceId: "",
            resourceClassificationList:[],
            showLabelList: false,
            valueList: [],
            // 添加标签是否看得见
            addLabelVisible: false,
            // 用户是否提交文件，保存数据
            isSubmit: false,
            // 新增的标签值
            labelValue: "",
            fileForm: {
                userId: "",
                url: "",
                type: "",
                name: "",
                type: [],
                price: "",
                resourceLabelList: [],
                // 资源分类列表
                resourceClassification: [],
                // 资源类型
                formTypeId: "1",
            },
            rules: {
                url: [
                    { required: true, message: '请上传资源', trigger: 'blur' },
                ],
                name: [
                    { required: true, message: '请输入资源名称', trigger: 'change' },
                    { min: 5, max: 100, message: "资源名称字符必须在 5 到 100 之间", trigger: 'blur'}
                ],
                resourceClassification: [
                { required: true, message: '分类标签不能为空', trigger: 'blur' },
                ],
                resourceLabelList: [
                    { type: 'array', required: true, message: '至少选中一个资源标签', trigger: 'blur' },
                ],
                formTypeId: [
                    { required: true, message: '请选择资源类型', trigger: 'change' }
                ],
                type: [
                    { type: 'array', required: true, message: '请至少选择一个活动性质', trigger: 'change' }
                ],
                description: [
                    { required: true, message: '请填写资源描述', trigger: 'blur' },
                    { min: 5, max: 500, message: "资源描述字符在 5 到 500 之间"},
                ]
            },
            uploadResourceUrl: "http://localhost:80/monkey-resource/uploadResource",
            userHomeUrl: "http://localhost:80/monkey-resource/user/home"
        };
    },

    created() {
        this.resourceId = this.$route.params.resourceId;
        this.queryCascaderList();
        this.queryResourceById(this.resourceId);
    },

    methods: {
        // 通过资源id得到资源信息
        queryResourceById(resourceId) {
            const vue = this;
            $.ajax({
                url: vue.userHomeUrl + "/queryResourceById",
                type: "get",
                data: {
                    resourceId
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.fileForm = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询联级选择器列表
        queryCascaderList() {
            const vue = this;
            $.ajax({
                url: vue.uploadResourceUrl + "/queryCascaderList",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.resourceClassificationList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        handleChange(value) {
            console.log(value);
        },
        // 更新资源
        updateResource(resource) {
            const vue = this;
            $.ajax({
                url: vue.userHomeUrl + "/updateResource",
                type: "put",
                data: {
                    resourceVoStr: JSON.stringify(resource),
                    userId: resource.userId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.$refs.uploadResourceChild.updateSubmit();
                        vue.$modal.msgSuccess(response.msg);
                        vue.$router.go(-1);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        handleCloseLabel(tag) {
            this.fileForm.resourceLabelList.splice(this.fileForm.resourceLabelList.indexOf(tag), 1);
        },
        showLabelInput() {
            this.addLabelVisible = true;
            this.$nextTick(_ => {
            this.$refs.saveTagInput.$refs.input.focus();
            });
        },
        handleInputConfirm() {
            let labelValue = this.labelValue;
            if (labelValue != null && labelValue != "") {
                this.fileForm.resourceLabelList.push(labelValue);
            }
            this.addLabelVisible = false;
            this.labelValue = '';
        },
        // 删除下载的资源
        removeUpload() {
            this.fileForm.name = "";
            this.fileForm.type = "";
            this.fileForm.url = "";
        },
        uploadFileForm(formName) {
            this.$refs[formName].validate((valid) => {
                if (valid) {
                    if (this.fileForm.formTypeId == '3') {
                        if (!Number.isInteger(this.fileForm.price)) {
                            this.$modal.msgError("收费金额必需为整数");
                            return false;
                        }
                        if (this.fileForm.price <= 0) {
                            this.$modal.msgError("输入金额不能为空")
                            return false;
                        }
                    }
                    this.updateResource(this.fileForm);
            } else {
                return false;
            }
            });
        },
        resetFileForm(formName) {
            // 重置之前判断是否上传文件
            if (this.$refs.uploadResourceChild.isUploadFile) {
                this.$refs.uploadResourceChild.removeUpload(this.fileForm);
            }
            this.$refs[formName].resetFields();
        },
        // 上传文件成功后更新文件信息
        uploadSuccess(file) {
            // 得到文件类型
            var flieArr = file.name.split('.');
            this.fileForm.name = file.name;
            this.fileForm.type = flieArr[flieArr.length - 1];
            this.fileForm.url = file.response.data;
        },
    },
};
</script>

<style scoped>
.inner-tag {
    margin-right: 10px;
}
.el-tag + .el-tag {
    margin-left: 10px;
}
.button-new-tag {
    margin-left: 10px;
    height: 32px;
    line-height: 30px;
    padding-top: 0;
    padding-bottom: 0;
}
.input-new-tag {
    width: 90px;
    margin-left: 10px;
    vertical-align: bottom;
}
.upload-resource {
    height: 100px; 
    line-height: 100px;
}
.demo-fileForm {
    background-color: #fff;
    padding: 20px;
    vertical-align: middle;
}
.MonkeyWebResourceEdit-container {
    margin: 10px auto;
    width: 1000px;
}
</style>