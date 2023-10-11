<template>
    <div class="MonkeyWebUploadResource-container">
        <ResourceClassification
        style="z-index: 2;"
        v-show="showLabelList"
        @closeLabelWindow="closeLabelWindow"
        @selectTwoLabel="selectTwoLabel"
        @removeTwoLabel="removeTwoLabel"/>
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
                :key="label.name"
                v-for="label in fileForm.resourceLabelList"
                closable
                :disable-transitions="false"
                @close="handleCloseLabel(label.name)">
                {{label.name}}
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
            <el-form-item label="所属分类" required prop="resourceClassificationList">
                    <el-tag
                    class="inner-tag"
                    :key="tag"
                    v-for="tag in fileForm.resourceClassificationList"
                    closable
                    :disable-transitions="false"
                    @close="handleCloseClassification(tag)">
                    {{tag.name}}
                </el-tag>
                <el-button 
                    class="button-new-tag el-icon-circle-plus-outline" 
                    size="small" 
                    @click="showLabelList = true">&nbsp;添加分类标签
                </el-button>
            </el-form-item>
            <el-form-item label="发布形式" prop="formTypeId">
                <el-radio-group v-model="fileForm.formTypeId">
                <el-radio label="1">免费</el-radio>
                <el-radio label="2">会员免费</el-radio>
                <el-radio label="3">收费</el-radio>
                <el-input
                v-model.number="fileForm.price"
                v-if="fileForm.formTypeId == '3'"
                placeholder="请输入收费金额，单位（元）" 
                style="display: inline-block; width: 300px"></el-input>
                </el-radio-group>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="uploadFileForm('fileForm')">立即创建</el-button>
                <el-button @click="resetFileForm('fileForm')">重置</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script>
import $ from 'jquery'
import store from '@/store';
import ResourceClassification from '@/components/resource/ResourceClassification'
import ElUploadResource from '@/components/upload/ElUploadResource'
export default {
    name: 'MonkeyWebUploadResource',
    components: {
        ElUploadResource,
        ResourceClassification
    },
    data() {
        return {
            showLabelList: false,
            valueList: [],
            // 添加标签是否看得见
            addLabelVisible: false,
            // 用户是否提交文件，保存数据
            isSubmit: false,
            // 新增的标签值
            labelValue: "",
            fileForm: {
                url: "",
                type: "",
                name: "",
                type: [],
                price: "",
                resourceLabelList: [],
                // 资源分类列表
                resourceClassificationList: [],
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
                resourceClassificationList: [
                { type: 'array', required: true, message: '至少选中一个分类标签', trigger: 'blur' },
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
        };
    },
    created() {
        
    },

    methods: {
        // 上传资源
        uploadResource(resource) {
            const vue = this;
            $.ajax({
                url: vue.uploadResourceUrl + "/uploadResource",
                type: "post",
                data: {
                    resourceVoStr: JSON.stringify(resource),
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.$modal.msgSuccess(response.msg);
                        vue.$router.push({
                            name: "resource",
                        })
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 删除选中的二级标签
        removeTwoLabel(twoLabel) {
            this.fileForm.resourceClassificationList.splice(this.fileForm.resourceClassificationList.indexOf(twoLabel), 1);
        },
        // 选中了二级标签
        selectTwoLabel(twoLabel) {
            this.fileForm.resourceClassificationList.push(twoLabel);
        },
        // 关闭标签选择框
        closeLabelWindow() {
            this.showLabelList = false;
        },
        // 关闭分类标签
        handleCloseClassification(tag) {
            this.fileForm.resourceClassificationList.splice(this.fileForm.resourceClassificationList.indexOf(tag), 1);
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
                this.fileForm.resourceLabelList.push({ 'name': labelValue });
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
                    this.uploadResource(this.fileForm);
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
.MonkeyWebUploadResource-container {
    margin: 10px auto;
    width: 1000px;
}
</style>