<template>
    <div class="MonkeyWebCreateCommunityViews-container">
        <SearchCommunity
        v-if="showLabelList"
        @closeLabelWindow="closeLabelWindow"
        @selectTwoLabel="selectTwoLabel"
        @removeTwoLabel="removeTwoLabel"/>
        <el-row class="create-community">创建社区</el-row> 

        <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="auto" class="demo-ruleForm">
            <el-form-item label="社区名称" prop="name" style="width: 50%;">
                <el-input v-model="ruleForm.name" ></el-input>
            </el-form-item>
            <el-form-item label="社区描述" prop="description" style="width: 50%;">
                <el-input
                    type="textarea"
                    :autosize="{ minRows: 5 , maxRows: 5}"
                    placeholder="请输入社区描述"
                    v-model="ruleForm.description"
                    :show-word-limit="true"
                    minlength="5"
                    maxlength="255">
                </el-input>
            </el-form-item>
            <el-form-item label="社区头像" prop="photo">
                <ElUploadPicture
                @onUploadSuccess="onUploadSuccess"
                @onUploadRemove="onUploadRemove"
                :module="module"
                :photo="ruleForm.photo"/> 
            </el-form-item>
            <el-form-item label="社区分类" prop="classificationId">
                <el-select v-model="ruleForm.classificationId" placeholder="请选择社区分类">
                <el-option
                    v-for="communityClassification in communityClassificationList"
                    :key="communityClassification.id"
                    :label="communityClassification.name"
                    :value="communityClassification.id">
                </el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="属性标签" prop="attributeLabel">
                <el-select v-model="ruleForm.attributeLabelId" placeholder="请选择属性标签">
                <el-option
                    v-for="communityAttribute in communityAttributeList"
                    :key="communityAttribute.id"
                    :label="communityAttribute.name"
                    :value="communityAttribute.id">
                </el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="内容标签" prop="communityClassificationLabelList">
                <el-tag
                    class="inner-tag"
                    :key="tag"
                    v-for="tag in ruleForm.communityClassificationLabelList"
                    closable
                    :disable-transitions="false"
                    @close="handleClose(tag)">
                    {{tag.name}}
                </el-tag>
                <el-button 
                    class="button-new-tag el-icon-circle-plus-outline" 
                    size="small" 
                    @click="showLabelList = true">添加内容标签
                </el-button>
            </el-form-item>
            
            <el-form-item label="评论前需先加入社区" prop="isComment">
                <el-switch v-model="ruleForm.isComment"></el-switch>
            </el-form-item>
            <el-form-item label="加入方式" prop="enterWay">
                <el-radio-group v-model="ruleForm.enterWay">
                <el-radio label="0">无限制</el-radio>
                <el-radio label="1">需要管理员批准后加入</el-radio>
                </el-radio-group>
            </el-form-item>
            
            <el-form-item style="text-align: right;">
                <el-button type="primary" @click="submitForm('ruleForm')">立即创建</el-button>
                <el-button @click="resetForm('ruleForm')">重置</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script>
import $ from 'jquery';
import store from '@/store';
import SearchCommunity from '@/components/community/SearchCommunity.vue';
import ElUploadPicture from '@/components/upload/ElUploadPicture.vue';
export default {
    name: 'MonkeyWebCreateCommunityViews',
    components: {
        ElUploadPicture,
        SearchCommunity
    },
    data() {
        return {
            module: 'communityPhoto/',
            // 社区属性列表
            communityAttributeList: [],
            // 社区分类列表
            communityClassificationList: [],
            // 是否展示标签列表
            showLabelList: false,
            createCommunityUrl: "http://localhost:80/monkey-community/create",
            ruleForm: {
                name: '',
                description: '',
                classificationId: '',
                isComment: false,
                enterWay: '',
                photo: "",
                // 内容标签列表
                communityClassificationLabelList: [],
                attributeLabelId: "",
                },
            rules: {
                name: [
                    { required: true, message: '请输入社区名称', trigger: 'blur' },
                    { min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' }
                ],
                description: [
                    { required: true, message: '请输入社区描述', trigger: 'blur' },
                    { min: 5, max: 255, message: '长度在 5 到 255 个字符', trigger: 'blur' }
                ],
                photo: [
                    { required: true, message: '请选择社区头像', trigger: 'change' }
                ],
                classificationId: [
                    { required: true, message: '请选择社区分类', trigger: 'change' }
                ],
                attributeLabelId: [
                    { required: true, message: '请选择属性标签', trigger: 'change' }
                ],
                communityClassificationLabelList: [
                    { type: 'array', required: true, message: '至少选中一个内容标签', trigger: 'change' }
                ],
                enterWay: [
                    { required: true, message: '请选择加入方式', trigger: 'change' }
                ]
                
            }
        };
    },

    created() {
        this.queryCommunityAttributeList();
        this.queryCommunityClassificationList();
    },

    methods: {
        // 创建社区
        createCommunity(community) {
            const vue = this;
            $.ajax({
                url: vue.createCommunityUrl + '/createCommunity',
                type: "post",
                data: {
                    community: JSON.stringify(community),
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.$modal.msgSuccess(response.msg);
                        vue.$router.push({
                            name: "community"
                        })
                    }
                },
                error(response) {
                    vue.$modal.msgError(response.msg);
                }
            })
        },
        // 得到社区分类列表
        queryCommunityClassificationList() {
            const vue = this;
            $.ajax({
                url: vue.createCommunityUrl + "/queryCommunityClassificationList",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.communityClassificationList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 得到社区属性列表
        queryCommunityAttributeList() {
            const vue = this;
            $.ajax({
                url: vue.createCommunityUrl + "/queryCommunityAttributeList",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.communityAttributeList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        handleClose(tag) {
            this.ruleForm.communityClassificationLabelList.splice(this.ruleForm.communityClassificationLabelList.indexOf(tag), 1);
        },
        removeTwoLabel(twoLabel) {
            this.ruleForm.communityClassificationLabelList.splice(this.ruleForm.communityClassificationLabelList.indexOf(twoLabel), 1);
        },
        // 选中了二级标签
        selectTwoLabel(twoLabel) {
            this.ruleForm.communityClassificationLabelList.push(twoLabel);
        },
        // 关闭标签选择框
        closeLabelWindow() {
            this.showLabelList = false;
        },
        submitForm(formName) {
            this.$refs[formName].validate((valid) => {
                if (valid) {
                    this.createCommunity(this.ruleForm);
                } else {
                    return false;
                }
        });
        },
        resetForm(formName) {
            this.$refs[formName].resetFields();
        },
        // 删除阿里云的文件
        onUploadRemove(data) {
            this.ruleForm.photo = data;
        },
        // 上传成功之后判断上传的图片是否成功
        onUploadSuccess(response) {
            this.ruleForm.photo = response.data;
        },  
    },
};
</script>

<style scoped>
.inner-tag {
    margin-right: 10px;
}
  .button-new-tag {
    margin-left: 10px;
    height: 32px;
    line-height: 30px;
    padding-top: 0;
    padding-bottom: 0;
  }
.MonkeyWebCreateCommunityViews-container {
    width: 1000px;
    margin: 20px auto;
    background-color: #fff;
    padding: 20px;
}
.create-community {
    font-weight: bold;
    font-size: 30px;
    text-align: center;
    margin-bottom: 20px;
}

</style>