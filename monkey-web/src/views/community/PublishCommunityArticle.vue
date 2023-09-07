<template>
    <div class="MonkeyWebPublishCommunityArticle-container">
        <TaskConfiguration
        style="z-index: 1;"
        @openAddMember="openAddMember"
        @updateTask="updateTask"
        @cancelTask="cancelTask"
        v-show="isShowTask"/>
        <CommunityMember
        style="z-index: 1;"
        @returnTask="returnTask"
        @confirmTaskPeople="confirmTaskPeople"
        v-show="isShowMember"/>
        <VoteConfiguration
        style="z-index: 1;"
        @cancelVeto="cancelVeto"
        @updateVeto="updateVeto"
        v-show="isShowVote"/>
        <el-form 
            abel-position="right" 
            :model="form" 
            :rules="rules" 
            ref="form" 
            label-width="100px" 
            class="demo-form">

            <el-form-item prop="title" label="文章标题">
                <div class="clearfix" >
                    <el-input v-model="form.title" placeholder="请输入文章标题"></el-input>
                </div>
            </el-form-item>
            <el-form-item prop="content">
                <mavonEditor
                    v-model="form.content" 
                    :toolbars="toolbars"
                    :translate="true"
                    defaultOpen="edit"
                    placeholder="期待您精彩的评论"
                    style="min-height: 400px; z-index: 0;"
                    :navigation="false"
                    :subfield="false"
                    :scrollStyl="true"
                    @keydown.native="handleKeyDown($event)">
                </mavonEditor>
            </el-form-item>
            <el-form-item label="文章简介" prop="profile">
                <el-input type="textarea" v-model="form.profile"></el-input>
            </el-form-item>

            <el-form-item label="选择频道" prop="channelId">
                <el-select v-model="form.channelId" placeholder="请选择频道">
                <el-option label="区域一" value="shanghai"></el-option>
                <el-option label="区域二" value="beijing"></el-option>
                </el-select>
            </el-form-item>
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
                :form="form"/>          
            </el-form-item>

            <el-form-item label="任务配置" prop="isTask">
                <el-radio-group v-model="form.isTask">
                    <el-radio label="0">不以任务形式发布</el-radio>
                    <span @click="isShowTask = true"><el-radio label="1">以任务形式发布</el-radio></span>
                </el-radio-group>
            </el-form-item>

            <el-form-item label="投票配置" prop="isVote">
                <el-radio-group v-model="form.isVote">
                    <el-radio label="0">不以投票形式发布</el-radio>
                    <span @click="isShowVote = true"><el-radio label="1">以投票形式发布</el-radio></span>
                </el-radio-group>
            </el-form-item>
            
            <el-form-item style="text-align: right;">
                <el-button type="primary" @click="publishArticle(form)">立即发布</el-button>
                <el-button @click="resetForm('form')">重置</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script>
import $ from 'jquery'
import store from '@/store';
import VoteConfiguration from '@/components/community/VoteConfiguration'
import TaskConfiguration from '@/components/community/TaskConfiguration.vue';
import { mavonEditor } from 'mavon-editor'
import ElUploadPicture from '@/components/upload/ElUploadPicture.vue';
import 'mavon-editor/dist/css/index.css'
import CommunityMember from '@/components/community/CommunityMember.vue';
export default {
    name: 'MonkeyWebPublishCommunityArticle',
    components: {
        mavonEditor,
        ElUploadPicture,
        TaskConfiguration,
        CommunityMember,
        VoteConfiguration
    },
    data() {
        return {
            // 社区id
            communityId: "",
            publishCommunityUrl: "http://localhost:80/monkey-community/publish",
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
            // 是否展示任务界面
            isShowTask: false,
            // 是否展示社区成员
            isShowMember: false,
            // 是否展示投票设置
            isShowVote: false,
            form: {
                profile: '',
                content: '',
                photo: null,
                labelId: [],
                title: "",
                // 选中参加任务的成员列表
                memberList: [],
                channelId: "",
                isTask: "",
                picture: "",
                isVote: "",
                // 用户投票信息
                veto: {},
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
                channelId: [
                    { required: true, message: '请选择发布频道', trigger: 'change' }
                ],
                isTask: [
                    { required: true, message: '请选择任务配置', trigger: 'change' }
                ],
                isVote: [
                    { required: true, message: '请选择任务配置', trigger: 'change' }
                ],
                picture: [
                    { required: true, message: '请选择文章封面', trigger: 'change' }
                ],
            },
            pickerOptions: {
            shortcuts: [{
                text: '今天',
                onClick(picker) {
                picker.$emit('pick', new Date());
                }
            }, {
                text: '一天',
                onClick(picker) {
                const date = new Date();
                date.setTime(date.getTime() + 3600 * 1000 * 24);
                picker.$emit('pick', date);
                }
            }, {
                text: '一周',
                onClick(picker) {
                const date = new Date();
                date.setTime(date.getTime() + 3600 * 1000 * 24 * 7);
                picker.$emit('pick', date);
                }
            }, {
                text: '一月',
                onClick(picker) {
                const date = new Date();
                date.setTime(date.getTime() + 3600 * 1000 * 24 * 30);
                picker.$emit('pick', date);
                }
            }]
            },
        };
    },

    created() {
        this.communityId = this.$route.params.communityId;
        this.queryCommunityChannelListByCommunityId(this.communityId);
    },

    methods: {
        // 通过社区id查询社区频道列表
        queryCommunityChannelListByCommunityId(communityId) {
            const vue = this;
            $.ajax({
                url: vue.publishCommunityUrl + "/queryCommunityChannelListByCommunityId",
                type: "get",
                data: {
                    communityId,
                },
                header: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        console.log(response);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 更新投票配置
        updateVeto(veto) {
            this.form.veto = veto;
            this.isShowVote = false;
            console.log(this.form.veto)
        },
        // 取消投票
        cancelVeto() {
            this.isShowVote = false;
            this.form.isVote = '0'
        },
        // 确定任务成员
        confirmTaskPeople(taskUserList) {
            this.isShowTask = true;
            this.isShowMember = false;
            this.form.memberList = taskUserList;
        },
        // 从选择成员界面返回任务界面
        returnTask() {
            this.isShowTask = true;
            this.isShowMember = false;
        },
        // 打开添加成员界面
        openAddMember() {
            this.isShowTask = false;
            this.isShowMember = true;
        },
        // 取消以任务形式发布
        cancelTask() {
            this.form.isTask = '0';
            this.isShowTask = false;
        },
        // 确认配置设定
        updateTask(task) {
            this.form.task = task;
            this.isShowTask = false; 
        },
    },
};
</script>

<style scoped>


.bottom{
    position: relative;
    z-index: 1;
}
.MonkeyWebPublishCommunityArticle-container {
    width: 1300px;
    margin: 10px auto;
    background-color: #fff;
    padding: 20px;
}

</style>