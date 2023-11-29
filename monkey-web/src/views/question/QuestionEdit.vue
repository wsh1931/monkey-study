<template>
    <div class="PublishQuestion-container" style="z-index: 1;">
        <LabelSelect 
        v-if="dialogVisible" 
        
        @selectTwoLabel="selectTwoLabel"
        @closeLabelWindow="closeLabelWindow"/>
        <el-container >
            <el-main style=" display: flex;justify-content: center; align-items: center;" >
                <el-row>
                    <el-card>
                        <el-form label-position="top" 
                        :model="questionForm" 
                        style="width: 1300px;"
                        :rules="rules" 
                        ref="questionForm" 
                        label-width="100px" 
                        class="demo-ruleForm">
                            <el-form-item prop="title" label="问答标题">
                                <div class="clearfix" >
                                    <el-input
                                        placeholder="请写下你的问题"
                                        :autosize="{ minRows: 2, maxRows: 4}"
                                        v-model="questionForm.title">
                                    </el-input>
                                </div>
                            </el-form-item>
                            <el-form-item prop="content">
                                <div id="PubilshArticleViews">
                                    <mavon-editor class="bottom" placeholder="说明问题背景，条件等详细信息(选填)" v-model="questionForm.profile"></mavon-editor>
                            </div>
                            </el-form-item>
                
                            <el-footer style="margin-top: 10px; padding: 0px 0px; height: 100%;">
                                <el-row> 
                                    <el-form-item label="问答分类" prop="labelId">
                                        <el-tag
                                            :key="labelTwo"
                                            v-for="labelTwo in selectedTwoLabelList"
                                            closable
                                            :disable-transitions="false"
                                            @close="handleClose(labelTwo)">
                                            {{labelTwo.labelName}}
                                            </el-tag>
                                        <el-button class="button-new-tag el-icon-circle-plus-outline" size="small" @click="dialogVisible = true">添加问答标题</el-button>
                                        
                                    </el-form-item>
                                    <el-form-item style="text-align: right;">
                                        <el-button type="primary" @click="questionUpdate(questionForm)">确定编辑</el-button>
                                        <el-button @click="resetForm('questionForm')">重置</el-button>
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
import $ from "jquery"
import store from "@/store";
import { mavonEditor } from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'
import LabelSelect from '@/components/label/LabelSelect.vue'
export default {
    name: 'MonkeyWebQuestionEdit',
    components: {
        mavonEditor,
        LabelSelect
    },
    data() {
        return {
            qeustionId: "",
            contentEditor: {},
            // 标签弹窗
            dialogVisible: false,
            // 被选择的二级标签列表
            selectedTwoLabelList: [],
            questionEditUrl: "http://localhost:80/monkey-question/edit",
            loading: false,
            list: [],
            labelList: [],
            questionForm: {
                title: "",
                profile: "",
                labelId: [],
            },
            rules: {
                title: [
                    {required: true, min: 1, max: 30, message:'标题长度在1-30个字符之间', trigger: 'blur'}
                ],
                labelId: [
                    {required: true, message: "至少选择一个标题", trigger: 'change'}
                ]

            }
        };
    },

    created() {
        this.questionId = this.$route.params.questionId;
        this.queryQuestionById(this.questionId);
    },

    methods: {
        // 通过问答id查询问答信息
        queryQuestionById(questionId) {
            const vue = this;
            $.ajax({
                url: vue.questionEditUrl + "/queryQuestionById",
                type: "get",
                data: {
                    questionId
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.questionForm = response.data;
                        vue.selectedTwoLabelList = response.data.labelList
                        console.log(vue.selectedTwoLabelList)
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        handleClose(tag) {
            this.questionForm.labelId.splice(this.questionForm.labelId.indexOf(tag.id), 1);
            this.selectedTwoLabelList.splice(this.selectedTwoLabelList.indexOf(tag), 1);
        },

        // 关闭标签窗口
        closeLabelWindow() {
            this.dialogVisible = false;
        },
        // 选择二级标签列表
        selectTwoLabel(labelTwo) {
            let success = true;
            for (let i = 0; i < this.selectedTwoLabelList.length; i++) {
                if (labelTwo.id == this.selectedTwoLabelList[i].id) {
                    success = false;
                    break;
                }
            }
            if (success) {
                this.selectedTwoLabelList.push(labelTwo);
                this.questionForm.labelId.push(labelTwo.id);
            } else {
                this.$modal.msgWarning("不可添加重复的标签")
            }
            
        },
        // 用过标签名模糊查询标签列表
        getLabelListByLabelName(labelName) {
            const vue = this;
            if (labelName == "") {
                this.labelList = [];
            } else {
                this.loading = true;
                $.ajax({
                url: vue.questionUrl + "/getLabelListByLabelName",
                type: "get",
                data: {
                    labelName
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.labelList = response.data;
                        vue.loading = false;
                    } else {
                            vue.$modal.msgError(response.msg);
                        }
                },
            })
            }
            
        },
        // 更新问答信息
        questionUpdate(questionForm) {
            const vue = this;
            this.$refs["questionForm"].validate((valid) => {
                if (valid) {
                    $.ajax({
                        url: vue.questionEditUrl + "/questionUpdate",
                        type: "put",
                        headers: {
                            Authorization: "Bearer " + store.state.user.token
                        },
                        data: {
                            questionId: questionForm.id,
                            questionFormStr: JSON.stringify(questionForm),
                        },
                        success(response) {
                            if (response.code == vue.ResultStatus.SUCCESS) {
                                vue.$modal.msgSuccess(response.msg);
                                vue.$router.go(-1);
                            }  else {
                                    vue.$modal.msgError(response.msg);
                                }
                        },
                    }) 
                } else {
                    return false;
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
.top{
    position: relative;
    z-index: 200;
}
.bottom{
    position: relative;
    z-index: 1;
}
</style>