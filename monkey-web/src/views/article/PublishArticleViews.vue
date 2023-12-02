<template>
    <div class="publish-article-container">
        <LabelSelect 
        v-if="dialogVisible" 
        @selectTwoLabel="selectTwoLabel"
        @closeLabelWindow="closeLabelWindow"/>
        <el-container >
            <el-main class="el-main">
                <el-row>
                    <el-card>
                        <el-form label-position="top" 
                        :model="ruleForm" 
                        style="width: 1300px;"
                        :rules="rules" 
                        ref="ruleForm" 
                        label-width="100px" 
                        class="demo-ruleForm">
                            <el-form-item prop="title" label="文章标题">
                                <div class="clearfix" >
                                    <el-input v-model="ruleForm.title" placeholder="请输入文章标题"></el-input>
                                </div>
                            </el-form-item>
                            <el-form-item prop="content">
                                <div id="PubilshArticleViews">
                                <mavon-editor class="bottom" v-model="ruleForm.content" ></mavon-editor>
                            </div>
                            </el-form-item>
                
                            <el-footer style="margin-top: 10px; padding: 0px 0px; height: 100%;">
                                <el-row> 
                                    <el-form-item  label="文章分类" prop="labelId">
                                        <el-tag
                                            :key="labelTwo"
                                            v-for="labelTwo in selectedTwoLabelList"
                                            closable
                                            :disable-transitions="false"
                                            @close="handleClose(labelTwo)">
                                            {{labelTwo.labelName}}
                                            </el-tag>
                                        <el-button class="button-new-tag el-icon-circle-plus-outline" size="small" @click="dialogVisible = true">添加文章标题</el-button>
                                    </el-form-item>

                                    <el-form-item label="文章封面" prop="picture">
                                        <ElUploadPicture
                                        @onUploadSuccess="onUploadSuccess"
                                        @onUploadRemove="onUploadRemove"
                                        :module="module"
                                        :photo="ruleForm.photo"/>          
                                    </el-form-item>
                                    <el-form-item label="文章简介" prop="profile">
                                        <el-input type="textarea" v-model="ruleForm.profile"></el-input>
                                    </el-form-item>
                                    <el-form-item style="text-align: right;">
                                        <el-button type="primary" @click="publishArticle(ruleForm)">立即发布</el-button>
                                        <el-button @click="resetForm('ruleForm')">重置</el-button>
                                    </el-form-item>
                                </el-row>
                            </el-footer>
                        </el-form>
                    </el-card>
                </el-row>
            </el-main>
        </el-container>
    </div>
</template>

<script>
import { mavonEditor } from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'
import store from '@/store'
import $ from "jquery"
import ElUploadPicture from '@/components/upload/ElUploadPicture.vue'
import LabelSelect from '@/components/label/LabelSelect.vue'
export default {
    name: "PubilshArticleViews",
    components: {
        mavonEditor,
        ElUploadPicture,
        LabelSelect
    },
    data() {
        return {
            // 标签弹窗
            dialogVisible: false,
            // 被选择的二级标签列表
            selectedTwoLabelList: [],
            publishUrl: "http://localhost:80/monkey-article/publish",
            blogLabelUrl: "http://localhost:80/monkey-article/blog/label",
            aliyunossUrl: "http://localhost:80/monkey-service/aliyun/oss",
            labelNameList: [],
            module: "articlePhoto/",
            ruleForm: {
                profile: '',
                content: '',
                photo: null,
                labelId: [],
                title: "",
                picture: "",
            },
            rules: {
                title: [
                    {required: true, min: 1, max: 100, message: '请输入文章标题(1 - 100字)', trigger: 'blur'}
                ],
                content: [
                    {required: true, message: '请输入文章内容', trigger: 'blur'}
                ],
                labelId: [
                    { required: true, message: '请选择标签名称', trigger: 'change' }
                ],
                profile: [
                    {required: true,  min: 1, max: 100, message: '长度在 1 到 100 个字符', trigger: 'blur' }
                ],
                picture: [
                    { required: true, message: '请选择文章封面', trigger: 'blur' }
                ],
            }
        }
    },

    created() {
        if (store.state.user.token == null || store.state.user.token == "") {
            this.$modal.msgError("请先登录");
            return ;
        }
        this.getLabelList();
        this.ruleForm.photo = "";
    },

    methods: {
        handleClose(tag) {
            this.ruleForm.labelId.splice(this.ruleForm.labelId.indexOf(tag.id), 1);
            this.selectedTwoLabelList.splice(this.selectedTwoLabelList.indexOf(tag), 1);
        },

        // 关闭标签窗口
        closeLabelWindow() {
            this.dialogVisible = false;
        },
        // 选择二级标签列表
        selectTwoLabel(labelTwo) {
            this.selectedTwoLabelList.push(labelTwo);
            this.ruleForm.labelId.push(labelTwo.id);
        },
        // 发布文章
        publishArticle(ruleForm) {
            const vue = this;
            this.$refs["ruleForm"].validate((valid) => {
            if (valid) {
                $.ajax({
                    url: vue.publishUrl + "/article",
                    type: "post",
                    headers: {
                        Authorization: "Bearer " + store.state.user.token
                    },
                    data: {
                        title: ruleForm.title,
                        labelId: JSON.stringify(ruleForm.labelId),
                        content: ruleForm.content,
                        profile: ruleForm.profile,
                        photo: ruleForm.photo
                    },
                    success(response) {
                        if (response.code == vue.ResultStatus.SUCCESS) {
                            vue.$modal.msgSuccess("发布成功")
                            vue.$refs["ruleForm"].resetFields();
                            vue.ruleForm = {};
                            vue.$router.push({
                                name: "myblog"
                            })
                            
                        } else {
                                vue.$modal.msgError(response.msg);
                            }
                        
                    },
                })
            } else {
                return false;
            }
            });
    },
        // 得到下拉框标签内容
        getLabelList() {
            const vue = this;
            $.ajax({
                url: vue.blogLabelUrl + "/getLabelList",
                type: "get",
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.labelNameList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
        },
        // 删除阿里云的文件
        onUploadRemove(file) {
            const vue = this;
            this.ruleForm.photo = "";
        },
        // 上传成功之后判断上传的图片是否成功
        onUploadSuccess(response) {
            this.ruleForm.photo = response.data;
        },  
        
        resetForm(formName) {
            this.$refs[formName].resetFields();
            this.$refs.upload.clearFiles();
        }
    }
}

</script>

<style scoped>
.el-main {
    display: flex; 
    justify-content: center; 
    align-items: center;
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
.bottom{
    position: relative;
    z-index: 1;
}
.top{
    position: relative;
    z-index: 200;
}
.upload-box {
    border: 1px solid #dcdfe6;
    border-radius: 6px;
    width: 10%;
    height: 145px;
}

.upload-box:hover {
    border-color: #409EFF;
}

.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 150px;
    text-align: center;
} 
.avatar {
    width: 178px;
    height: 178px;
    display: block;
}
</style>