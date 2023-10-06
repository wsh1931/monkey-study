<template>
    <div class="MonkeyWebCommunityInfoManageView-container">
        <div class="header">
            <SearchCommunity
            v-if="showLabelList"
            @closeLabelWindow="closeLabelWindow"
            @selectTwoLabel="selectTwoLabel"/>
            <el-tabs v-model="activeName" @tab-click="handleClick">
                <el-tab-pane label="基本信息" name="base">
                    <el-form :model="communityForm" :rules="rules" ref="communityForm" label-width="auto" class="demo-communityForm">
                        <el-form-item label="社区名称" prop="name">
                            <el-input placeholder="请输入社区名称" v-model="communityForm.name"></el-input>
                        </el-form-item>
                        <el-form-item label="社区描述" prop="description">
                            <el-input 
                            placeholder="请输入社区描述"
                            :autosize="{ minRows: 2, maxRows: 10 }" 
                            maxlength="255" 
                            show-word-limit 
                            type="textarea" 
                            v-model="communityForm.description"></el-input>
                        </el-form-item>
                        <el-form-item label="社区头像" prop="photo">
                            <ElUploadPicture
                            @onUploadSuccess="onUploadSuccess"
                            @onUploadRemove="onUploadRemove"
                            :module="module"
                            :photo="communityForm.photo"/> 
                        </el-form-item>
                        <el-form-item label="社区分类" prop="classification">
                            <el-select v-model="communityForm.classification" placeholder="请选择社区分类">
                            <el-option
                                v-for="communityClassification in communityClassificationList"
                                :key="communityClassification.id"
                                :label="communityClassification.name"
                                :value="communityClassification.id">
                            </el-option>
                            </el-select>
                        </el-form-item>
                        <el-form-item label="评论前需要加入社区" prop="isComment">
                            <el-switch 
                            v-model="communityForm.isComment"
                            :active-value="1"
                            :inactive-value="0"
                            ></el-switch>
                        </el-form-item>
                        <el-form-item label="内容标签" prop="communityClassificationLabelList">
                            <el-tag
                                class="inner-tag"
                                :key="tag"
                                v-for="tag in communityForm.communityClassificationLabelList"
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
                        <el-form-item label="属性标签" prop="attributeLabel">
                            <el-select v-model="communityForm.attributeLabel" placeholder="请选择属性标签">
                            <el-option
                                v-for="communityAttribute in communityAttributeList"
                                :key="communityAttribute.id"
                                :label="communityAttribute.name"
                                :value="communityAttribute.name">
                            </el-option>
                            </el-select>
                        </el-form-item>
                        <el-form-item label="加入方式" prop="enterWay">
                            <el-radio-group v-model="communityForm.enterWay">
                            <el-radio :label="0">无限制</el-radio>
                            <el-radio :label="1">需要管理员批准后加入</el-radio>
                            </el-radio-group>
                        </el-form-item>
                        <el-form-item>
                            <el-button type="primary" @click="submitForm('communityForm')" size="small">保存</el-button>
                        </el-form-item>
                    </el-form>
                </el-tab-pane>
                <el-tab-pane label="社区公告" name="notice" style="padding: 10px;">
                    <mavon-editor
                    v-model="communityNotice" 
                    :toolbars="toolbars"
                    :translate="true"
                    defaultOpen="edit"
                    placeholder="请编写您的社区公告"
                    style="min-height: 200px; z-index: 100001;"
                    :navigation="false"
                    :subfield="false"
                    :scrollStyle="true"
                    ></mavon-editor>
                    <div class="notice-div">
                        <el-button @click="updateCommunityNotice(communityId, communityNotice)" type="primary" size="small">保存</el-button>
                    </div>
                </el-tab-pane>
            </el-tabs>
        </div>
    </div>
</template>

<script>
import $ from 'jquery';
import store from '@/store';
import { mavonEditor } from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'
import SearchCommunity from '@/components/community/SearchCommunity.vue';
import ElUploadPicture from '@/components/upload/ElUploadPicture.vue';
export default {
    name: 'MonkeyWebCommunityInfoManageView',
    components: {
        ElUploadPicture,
        SearchCommunity,
        mavonEditor
    },
    data() {
        return {
            // 社区公告
            communityNotice: "",
            // 社区id
            communityId: "",
            // 社区属性集合
            communityAttributeList: [],
            // 社区分类集合
            communityClassificationList: [],
            // 是否展示标签列表
            showLabelList: false,
            module: 'communityPhoto',
            activeName: "base",
            communityForm: {
                name: '',
                region: '',
                classification: "",
                communityClassificationLabelList: [],
                description: '',
                photo: '',
                attributeLabel: "",
                enterWay: "",
            },
            rules: {
                name: [
                    { required: true, message: '请输入活动名称', trigger: 'blur' },
                    { min: 3, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' }
                ],
                photo: [
                    { required: true, message: '请选择社区头像', trigger: 'change' }
                ],
                communityClassificationLabelList: [
                    { type: 'array', required: true, message: '至少选中一个内容标签', trigger: 'change' }
                ],
                classification: [
                    { required: true, message: '请选择社区分类', trigger: 'change' }
                ],
                description: [
                    { required: true, message: '请填写社区描述', trigger: 'blur' },
                    {min: 1, max: 255, message: "社区描述不可超过 255 个字符", trigger: 'blur'},
                ],
                attributeLabel: [
                    { required: true, message: '请选择属性标签', trigger: 'change' }
                ],
                enterWay: [
                    { required: true, message: '请选择加入方式', trigger: 'change' }
                ]
            },
            toolbars: {
                bold: true, // 粗体
                italic: true, // 斜体
                header: true, // 标题
                underline: true, // 下划线
                strikethrough: true, // 中划线
                mark: true, // 标记
                superscript: true, // 上角标
                subscript: true, // 下角标
                quote: true, // 引用
                ol: true, // 有序列表
                ul: true, // 无序列表
                link: true, // 链接
                imagelink: true, // 图片链接
                code: true, // code
                table: true, // 表格
                fullscreen: true, // 全屏编辑
                readmodel: true, // 沉浸式阅读
                help: true, // 帮助
                preview: true, // 预览
            },
            createCommunityUrl: "http://localhost:80/monkey-community/create",
            infoManageUrl: "http://localhost:80/monkey-community/manage/infoManage",
            communityBaseInfoUrl: "http://localhost:80/monkey-community/community/baseInfo",
        }
    },

    created() {
        this.communityId = this.$route.params.communityId;
        this.queryCommunityClassificationList();
        this.queryCommunityAttributeList();
        this.queryCommunityInfo(this.communityId)
    },

    methods: {
        // 更新社区公告
        updateCommunityNotice(communityId, communityNotice) {
            const vue = this;
            $.ajax({
                url: vue.infoManageUrl + "/updateCommunityNotice",
                type: "put",
                data: {
                    communityId,
                    communityNotice
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
        // 更新社区信息
        updateCommunityInfo(communityVo) {
            const vue = this;
            $.ajax({
                url: vue.infoManageUrl + "/updateCommunityInfo",
                type: "put",
                data: {
                    communityVoStr: JSON.stringify(communityVo),
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
        // 通过社区id查询社区基本信息
        queryCommunityInfo(communityId) {
            const vue = this;
            $.ajax({
                url: vue.infoManageUrl + "/queryCommunityInfo",
                type: "get",
                data: {
                    communityId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.communityForm = response.data;
                        vue.communityNotice = JSON.parse(JSON.stringify(vue.communityForm.notice));
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
        // 取消选中内容标签
        handleClose(tag) {
            this.communityForm.communityClassificationLabelList.splice(this.communityForm.communityClassificationLabelList.indexOf(tag), 1);
        },
        // 选中了二级标签
        selectTwoLabel(twoLabel) {
            this.communityForm.communityClassificationLabelList.push(twoLabel);
        },
        // 关闭标签选择框
        closeLabelWindow() {
            this.showLabelList = false;
        },
        submitForm(formName) {
            this.$refs[formName].validate((valid) => {
            if (valid) {
                this.updateCommunityInfo(this.communityForm);
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
            this.communityForm.photo = data;
        },
        // 上传成功之后判断上传的图片是否成功
        onUploadSuccess(response) {
            this.communityForm.photo = response.data;
        },  
    },
};
</script>

<style scoped>
.notice-div {
    margin-top: 10px;
}
.button-new-tag {
    margin-left: 10px;
    height: 32px;
    line-height: 30px;
    padding-top: 0;
    padding-bottom: 0;
}
.inner-tag {
    margin-right: 10px;
}
.demo-communityForm {
    width: 40%;
}
.header {
    padding: 20px;
    background-color: #fff;
}
.MonkeyWebCommunityInfoManageView-container {
    padding: 10px;
}
</style>