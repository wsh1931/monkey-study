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
                                    <el-form-item  label="文章分类" prop="labelList">
                                        <el-tag
                                            :key="labelTwo"
                                            v-for="labelTwo in ruleForm.labelList"
                                            closable
                                            :disable-transitions="false"
                                            @close="handleClose(labelTwo)">
                                            {{labelTwo.labelName}}
                                            </el-tag>
                                        <el-button class="button-new-tag el-icon-circle-plus-outline" size="small" @click="dialogVisible = true">添加文章标题</el-button>
                                    </el-form-item>

                                    <el-form-item label="文章封面" prop="photo">
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
                                        <el-button type="primary" @click="updateArticle(ruleForm)">确定更新</el-button>
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
    name: 'MonkeyWebArticleEdit',
    components: {
        mavonEditor,
        ElUploadPicture,
        LabelSelect
    },
    data() {
        return {
            articleId: "",
            // 标签弹窗
            dialogVisible: false,
            // 被选择的二级标签列表
            labelList: [],
            publishUrl: "http://localhost:80/monkey-article/publish",
            blogLabelUrl: "http://localhost:80/monkey-article/blog/label",
            articleUpdateUrl: "http://localhost:80/monkey-article/edit",
            labelNameList: [],
            module: "articlePhoto/",
            ruleForm: {
                profile: '',
                content: '',
                photo: null,
                labelList: [],
                title: "",
            },
            rules: {
                photo: [
                    {required: true, message: '课程封面不能为空', trigger: 'blur'}
                ],
                title: [
                    {required: true, min: 1, max: 100, message: '请输入文章标题(1 - 100字)', trigger: 'blur'}
                ],
                content: [
                    {required: true, message: '请输入文章内容', trigger: 'blur'}
                ],
                labelList: [
                    { required: true, message: '请选择标签名称', trigger: 'change' }
                ],
                profile: [
                    {required: true,  min: 1, max: 100, message: '长度在 1 到 100 个字符', trigger: 'blur' }
                ]
            }
        };
    },

    created() {
        this.articleId = this.$route.params.articleId;
        this.queryArticleInfoById(this.articleId);
        this.getLabelList();
        this.ruleForm.photo = "";
    },

    methods: {
        // 通过文章id查询文章信息
        queryArticleInfoById(articleId) {
            const vue = this;
            $.ajax({
                url: vue.articleUpdateUrl + "/queryArticleInfoById",
                type: "get",
                data: {
                    articleId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.ruleForm = response.data;
                        vue.ruleForm.labelList = response.data.labelList;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        handleClose(tag) {
            this.ruleForm.labelList.splice(this.ruleForm.labelList.indexOf(tag), 1);
        },

        // 关闭标签窗口
        closeLabelWindow() {
            this.dialogVisible = false;
        },
        // 选择二级标签列表
        selectTwoLabel(labelTwo) {
            let success = true;
            for (let i = 0; i < this.ruleForm.labelList.length; i++) {
                if (labelTwo.id == this.ruleForm.labelList[i].id) {
                    success = false;
                    break;
                }
            }
            if (success) {
                this.ruleForm.labelList.push(labelTwo);
            } else {
                this.$modal.msgWarning("不可添加重复的标签")
            }
        },
        // 更新文章
        updateArticle(ruleForm) {
            const vue = this;
            this.$refs["ruleForm"].validate((valid) => {
            if (valid) {
                $.ajax({
                    url: vue.articleUpdateUrl + "/updateArticle",
                    type: "put",
                    headers: {
                        Authorization: "Bearer " + store.state.user.token
                    },
                    data: {
                        articleId: ruleForm.id,
                        articleStr: JSON.stringify(ruleForm),
                    },
                    success(response) {
                        if (response.code == vue.ResultStatus.SUCCESS) {
                            vue.$modal.msgSuccess("发布成功")
                            vue.$router.go(-1);
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
            this.ruleForm.photo = null;
            this.deleteArticlePicture(this.articleId);
        },
        // 删除数据库中的图片
        deleteArticlePicture(articleId) {
            const vue = this;
            $.ajax({
                url: vue.articleUpdateUrl + "/deleteArticlePicture",
                type: "delete",
                data: {
                    articleId
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code != vue.ResultStatus.SUCCESS) {
                        vue.$modal.msgError(response.msg);
                    } else {
                        vue.$modal.msgSuccess(response.msg);
                    }
                }
            })
        },
        // 上传成功之后判断上传的图片是否成功
        onUploadSuccess(response) {
            this.ruleForm.photo = response.data;
            this.uploadArticlePicture(this.ruleForm.photo);
        },  
        // 上传图片
        uploadArticlePicture(photo) {
            const vue = this;
            $.ajax({
                url: vue.articleUpdateUrl + "/uploadArticlePicture",
                type: "put",
                data: {
                    articleId: vue.articleId,
                    photo
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        resetForm(formName) {
            this.$refs[formName].resetFields();
            this.$refs.upload.clearFiles();
        }
    },
};
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