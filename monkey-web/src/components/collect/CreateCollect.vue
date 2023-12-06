<template>
    <div class="MonkeyWebCreateCollect-container">
        <div class="position">
            <span class="el-icon-close icon-close" @click="closeShowCollect(false)"></span>
            <el-row class="add-collect">创建新收藏夹</el-row>
                <el-row>
                    <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm" label-position="top">
                        <el-form-item prop="name">
                            <el-input style="font-size: 13px;" v-model="ruleForm.name" placeholder="收藏标题" class="collect-name"></el-input>
                        </el-form-item>
                        <el-form-item  prop="description">
                            <el-input 
                            :autosize="{ minRows: 5, maxRows: 5 }" 
                            maxlength="100" 
                            show-word-limit 
                            type="textarea" 
                            v-model="ruleForm.description" 
                            placeholder="收藏描述（选填）" 
                            resize="none"
                            class="collect-description" 
                            ></el-input>
                        </el-form-item>
                        <el-row>
                            <el-radio v-model="ruleForm.isPrivate" label="0">私密</el-radio>
                            <span class="tip-style">只有你自己可以查看这个收藏夹</span>
                        </el-row>
                        <el-row>
                            <el-radio v-model="ruleForm.isPrivate" label="1" class="public">公开</el-radio>
                            <span class="tip-style">所有人均可看到你的收藏夹内容</span>
                        </el-row>
                        <el-form-item >
                            <el-button class="create-content-return-button" @click="closeShowCollect(false)">返回</el-button>
                            <el-button type="primary" 
                            class="create-content-create-button"
                            @click="submitForm('ruleForm')" >
                            立即创建</el-button>
                            
                        </el-form-item>
                    </el-form>
                </el-row>
        </div>
    </div>
</template>

<script>
import $ from "jquery"
import store from "@/store";
export default {
    name: 'MonkeyWebCreateCollect',
    data() {
        return {
            ruleForm: {
                name: '',
                description: '',
                 // 选中文件夹类型
                isPrivate: '0',
            },
            rules: {
                name: [
                    { required: true, message: '请输入收藏夹标题', trigger: 'blur' },
                    { max: 30, message: '长度在 30 个字符以内', trigger: 'blur' }
                ],
                description: [
                    {max: 100, message: "长度在 100 个字符以内", trigger: 'blur'}
                ]
            },
            userCollectUrl: "http://localhost:80/monkey-user/collect",
        };
    },
    methods: {
         // 关闭收藏夹
        closeShowCollect(status) {
            this.$emit("closeShowCollect", status);
        },
        // 提交表单
        submitForm(formName) {
            const vue = this;
            this.$refs[formName].validate((valid) => {
                if (valid) {
                    // 创建收藏夹
                    $.ajax({
                        url: vue.userCollectUrl + '/create/content',
                        type: 'post',
                        data: {
                            content: JSON.stringify(vue.ruleForm),
                        },
                        headers: {
                            Authorization: "Bearer " + store.state.user.token
                        },
                        success(response) {
                            if (response.code == vue.ResultStatus.SUCCESS) {
                                vue.ruleForm = {};
                                vue.$emit("createCollectSuccess");
                                vue.$modal.msgSuccess(response.msg);
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
    },
};
</script>

<style scoped>
.icon-close {
    position: absolute;
    right: 20px;
    font-size: 22px;
    z-index: 200001;
}
.icon-close:hover {
    cursor: pointer;
}
.create-content-create-button {
    width: 48%; 
    height: 30px; 
    background-color:#056DE8;
    line-height: 0px;
}
.create-content-return-button{
    height: 30px;
    width: 48%;
    line-height: 0px;
}
.public {
    padding-top: 10px;
    padding-bottom: 10px;
}
.tip-style {
    font-size: 14px;
    color: grey;
}
.collect-description ::placeholder {
    font-size: 12px;
    color: black;
    opacity: 0.5;
}
.collect-description {
    font-size: 13px;
    font-weight: bold;
}
.collect-name ::placeholder{
    font-size: 12px;
    color: black;
    opacity: 0.5;
}
.demo-ruleForm {
    padding-top: 20px;
    padding-left: 10px;
    padding-right: 10px;
}
.add-collect {
    font-weight: 500;
    font-size: 25px;
    text-align: center;
}
.MonkeyWebCreateCollect-container {
    z-index: 20000;
    position: fixed;
    height: 100%; 
    width: 100%; 
    background-color: rgba(0, 0, 0, 0.5);
    top: 0;
    left: 0;
}
.position {
    position: absolute;
    width: 480px;
    background-color: #FFFFFF;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    padding: 20px;
    max-height: 600px;
    overflow: auto;
    animation: slide-up 0.4s linear;
}
@keyframes slide-up {
    0% {
        opacity: 0;
    }
    100% {
        opacity: 1;
    }
}
</style>