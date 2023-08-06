<template>
    <div class="MonkeyWebCollectCard-container">
        <el-row class="position">
            <span class="el-icon-close icon-close" @click="closeCollect(false)"></span>
            <el-row v-if="!addCollectContent">
                <el-row class="add-collect">添加收藏</el-row>
                <el-row class="select-collect">选则您想要添加的收藏</el-row>
                <el-row v-for="courseContent in collectContentList" :key="courseContent.id">
                    <el-row>
                        <el-col :span="19">
                            <el-row>
                                <span class="collect-content">
                                    {{ courseContent.name }}
                                    <span v-if="courseContent.isPrivate == '1'" class="el-icon-unlock"></span>
                                    <span v-if="courseContent.isPrivate == '0'" class="el-icon-lock"></span>
                                </span>
                            </el-row>
                            <el-row class="collect-sum">
                                {{ courseContent.collectCount }} 条内容
                            </el-row>
                        </el-col>
                        <el-col :span="5" >
                            <el-button v-if="courseContent.isCollect == '0'" 
                            class="collect-button" 
                            @click="collectContent(courseContent.id, associateId, collectType, collectTitle)">
                                <span class=" iconfont icon-shoucang"></span> 收藏
                            </el-button>
                            <el-button 
                            v-if="courseContent.isCollect == '1'" 
                            class="collect-button-already"
                            @click="collectContent(courseContent.id, associateId, collectType, collectTitle)">
                                    <span class=" iconfont icon-shoucang"></span> <span>已收藏</span>
                            </el-button>
                        </el-col>
                    </el-row>
                    <el-row class="divider"></el-row>
                </el-row> 
                <el-row style="text-align: center;">
                    <el-button class="createCollectContent" type="primary" size="small" @click="addCollectContent = true">创建收藏夹</el-button>
                </el-row>
            </el-row>
            
            <!-- 新加收藏夹 -->
            <el-row v-if="addCollectContent">
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
                            <el-button @click="addCollectContent = false" class="create-content-return-button">返回</el-button>
                            <el-button type="primary" 
                            class="create-content-create-button"
                            @click="submitForm('ruleForm')" >
                            立即创建</el-button>
                            
                        </el-form-item>
                    </el-form>
                </el-row>
            </el-row>
        </el-row>
    </div>
</template>

<script>
import $ from 'jquery'
import store from '@/store';

export default {
    name: 'MonkeyWebCollectCard',
    props: ['associateId', 'collectType', 'collectTitle', 'showCollect'],
    data() {
        return {
            // 当前登录用户id
            userId: "",
            // 是否显示新建收藏夹
            addCollectContent: false,
            userCollectUrl: "http://localhost:80/monkey-user/collect",
            isPrivate: 1,
            // 收藏目录列表
            collectContentList: [],
            ruleForm: {
                name: '',
                description: '',
                 // 选中文件夹类型
                 isPrivate: '0',
                 userId: store.state.user.id
            },
            rules: {
                name: [
                    { required: true, message: '请输入收藏夹标题', trigger: 'blur' },
                    { max: 30, message: '长度在 30 个字符以内', trigger: 'blur' }
                ],
                description: [
                    {max: 100, message: "长度在 100 个字符以内", trigger: 'blur'}
                ]
            }
        };
    },

    created() {
        this.userId = store.state.user.id;
        this.getCollectContentListByUserId(this.userId, this.associateId, this.collectType);
    },

    methods: {
        // 关闭收藏夹
        closeCollect(status) {
            this.$emit("closeCollect", status);
        },
        // 收藏功能实现
        collectContent(collectContentId, associateId, collectType, collectTitle) {
            const vue = this;
            $.ajax({
                url: vue.userCollectUrl + "/collectContent",
                type: "post",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    collectContentId,
                    associateId,
                    collectType,
                    collectTitle,
                    userId: vue.userId,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.getCollectContentListByUserId(vue.userId, associateId, collectType);
                        vue.$modal.msgSuccess(response.msg);
                    }
                },
                 
            })
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
                            if (response.code == '200') {
                                vue.ruleForm = {};
                                vue.ruleForm.isPrivate = '0';
                                vue.addCollectContent = false;
                                vue.getCollectContentListByUserId(vue.userId, vue.associateId, vue.collectType);
                                vue.$modal.msgSuccess(response.msg);
                            }
                        },
                    })
                } else {
                    return false;
                }
            });
        },
        // 通过用户id得到文章收藏目录
        getCollectContentListByUserId(userId, associateId, collectType) {
            const vue = this;
            $.ajax({
                url: vue.userCollectUrl + "/getCollectContentListByUserId",
                type: "get",
                data: {
                    userId,
                    associateId,
                    collectType
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token
                },
                success(response) {
                    if (response.code == '200') {
                        vue.collectContentList = response.data;
                    }
                },
            })
        }
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
.tip-style {
    font-size: 14px;
    color: grey;
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
.collect-name ::placeholder{
    font-size: 12px;
    color: black;
    opacity: 0.5;
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
.demo-ruleForm {
    padding-top: 20px;
    padding-left: 10px;
    padding-right: 10px;
}
.createCollectContent {
    background-color: #056DE8;
    width: 140px;
}
.divider {
    height: 1px;
    width: 100%;
    background: gray;
    opacity: 0.5;
    margin-bottom: 20px;
}
.collect-button {
    border: #409EFF 1px solid;
    color: #409EFF;
    width: 100%;
    display: flex; 
    justify-content: center; 
    align-items: center;
}
.collect-button-already {
    display: flex; 
    justify-content: center; 
    align-items: center;
    color: white;
    width: 100%;
    background-color: #8590A6;
}

.collect-button-already:hover {
    background-color: #76839B;
    color: white;
}
.collect-sum {
    font-size: 14px;
    opacity: 0.5;
    padding: 10px 0px;
}
.collect-content {
    font-size: 14px;
    font-weight: bold;
}
.select-collect {
    text-align: center;
    padding: 10px;
    opacity: 0.5;
    font-size: 14px;
}
.add-collect {
    font-weight: 500;
    font-size: 25px;
    text-align: center;
}
.MonkeyWebCollectCard-container {
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
}

</style>