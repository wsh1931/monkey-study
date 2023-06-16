<template>
    <div class="publish-article-container" >
        <el-container >
            <el-main style=" display: flex;justify-content: center; align-items: center;" >
                <el-row>
                    <el-card>
                        <el-form label-position="top" 
                        :model="ruleForm" 
                        style="width: 1400px;"
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
                                <mavon-editor v-model="ruleForm.content" ></mavon-editor>
                            </div>
                            </el-form-item>
                
                            <el-footer style="margin-top: 10px; padding: 0px 0px; height: 100%;">
                                <el-row> 
                                    <el-form-item label="文章分类" prop="labelId">
                                        <el-checkbox-group v-model="ruleForm.labelId" size="small">
                                        <el-checkbox v-for="label in labelNameList" :key="label.id" :label="label.id" >{{label.labelName}}</el-checkbox>
                                    </el-checkbox-group>
                                    </el-form-item>

                                    <el-form-item label="文章封面">
                                        <ElUploadPicture
                                        @onUploadSuccess="onUploadSuccess"
                                        @onUploadRemove="onUploadRemove"
                                        :module="module"
                                        :form="ruleForm"/>          
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


export default {
    name: "PubilshArticleViews",
    components: {
        mavonEditor,
        ElUploadPicture
    },
    data() {
        return {
            checkboxGroup1: ['上海'],
            labelNameList: [],
            module: "articlePhoto",
            ruleForm: {
                profile: '',
                content: '',
                photo: null,
                labelId: [],
                title: "",
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
                ]
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
        // 发布文章
        publishArticle(ruleForm) {
            const vue = this;
            this.$refs["ruleForm"].validate((valid) => {
            if (valid) {
                $.ajax({
                    url: "http://localhost:4000/publish/article",
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
                        if (response.code == "10000") {
                            vue.$modal.msgSuccess("发布成功")
                            vue.$refs["ruleForm"].resetFields();
                            vue.ruleForm = {};
                            vue.$router.push({
                                name: "myblog"
                            })
                            
                        } else {
                            vue.$modal.msgError("发布失败");
                        }
                        
                    },
                    error() {
                        vue.$modal.msgError("发布失败");
                    }
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
                url: "http://localhost:4000/blog/label/getLabelList",
                type: "get",
                success(response) {
                    if (response.code == "10000") {
                        vue.labelNameList = response.data;
                    } else {
                        vue.$modal.msgError("发现未知错误")
                    }
                },
                error() {
                    vue.$modal.msgError("发现未知错误")
                }
            })
        },
        // 删除阿里云的文件
        onUploadRemove(file) {
            const vue = this;
            $.ajax({
                url: "http://localhost:5000/monkeyoss/remove",
                type: "delete",
                headers: {
                    Authorization: 'Bearer ' + store.state.user.token
                },
                data: {
                    fileUrl: file.response.data
                },
                success(response) {
                    if (response.code == "10000") {
                        vue.$modal.msgSuccess("删除成功");
                        vue.resetForm.photo = "";
                    } else {
                        vue.$modal.msgError("删除失败");
                    }
                },
                error() {
                    vue.$modal.msgError("删除失败")
                }
            })
        },
        // 上传成功之后判断上传的图片是否成功
        onUploadSuccess(response) {
            if (response.code == "10000") {
                this.$modal.msgSuccess("上传成功");
                this.ruleForm.photo = response.data;
            } else {
                this.$modal.msgError("上传失败");
            }
        },  
        beforeAvatarUpload(file) {
            const isJPG = file.type === 'image/jpeg';
            const isPng = file.type === 'image/png'
            const isLt2M = file.size / 1024 / 1024 < 2;
            if (!isJPG && !isPng) {
                this.$message.error('上传头像图片只能是 JPG/PNG 格式!');
            }
            if (!isLt2M) {
                this.$message.error('上传头像图片大小不能超过 2MB!');
            }
            return isJPG && isLt2M;
        }, 
        
      resetForm(formName) {
        this.$refs[formName].resetFields();
        this.$refs.upload.clearFiles();
      }
    }
    }

</script>

<style scoped>


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