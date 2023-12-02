<template>
    <div class="MonkeyWebPublishCommunityArticle-container">
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
                    placeholder="请输入文章内容"
                    style="min-height: 400px; z-index: 0;"
                    :navigation="false"
                    :subfield="false"
                    :scrollStyle="true"
                    @keydown.native="handleKeyDown($event)">
                </mavonEditor>
            </el-form-item>
            <el-form-item label="文章简介" prop="brief">
                <el-input type="textarea" v-model="form.brief"></el-input>
            </el-form-item>

            <el-form-item label="选择频道" prop="channelId">
                <el-select v-model="form.channelId" placeholder="请选择频道">
                <el-option
                    v-for="channel in channelList"
                    :key="channel.id"
                    :label="channel.channelName"
                    :value="channel.id">
                </el-option>
                </el-select>
            </el-form-item>

            <el-form-item label="文章封面" prop="picture">
                <ElUploadPicture
                @onUploadSuccess="onUploadSuccess"
                @onUploadRemove="onUploadRemove"
                :module="module"
                :photo="form.picture"/>          
            </el-form-item>
            
            <el-form-item style="text-align: right;">
                <div class="tip">注意：任务配置以及投票配置不支持编辑</div>
                <el-button type="primary" @click="updateCommunityArticle(form)">确认修改</el-button>
            </el-form-item>
        </el-form>

    </div>
</template>

<script>
import $ from 'jquery'
import store from '@/store';
import { mavonEditor } from 'mavon-editor'
import ElUploadPicture from '@/components/upload/ElUploadPicture.vue';
import 'mavon-editor/dist/css/index.css'
export default {
    name: 'MonkeyWebCommunityArticleEdit',
    components: {
        mavonEditor,
        ElUploadPicture,
    },
    data() {
        return {
            communityArticleId: "",
            // 发布社区文章图片模块
            module: "community/article/",
            // 社区频道列表
            channelList: [],
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
                brief: '',
                content: '',
                title: "",
                channelId: "",
                picture: "",
                communityId: "",
            },
            rules: {
                title: [
                    {required: true, min: 1, max: 100, message: '请输入文章标题(1 - 100字)', trigger: 'blur'}
                ],
                content: [
                    {required: true, message: '请输入文章内容', trigger: 'blur'}
                ],
                brief: [
                    {required: true,  min: 1, max: 100, message: '长度在 1 到 100 个字符', trigger: 'blur' }
                ],
                channelId: [
                    { required: true, message: '请选择发布频道', trigger: 'blur' }
                ],
                picture: [
                    { required: true, message: '请选择文章封面', trigger: 'blur' }
                ],
            },
            communityArticleUpdateUrl: "http://localhost:80/monkey-community/community/article/edit",
            aliyunossUrl: "http://localhost:80/monkey-service/aliyun/oss",
        };
    },

    created() {
        this.communityId = this.$route.params.communityId;
        this.communityArticleId = this.$route.params.communityArticleId;
        this.queryCommunityArticle(this.communityArticleId);
        this.queryCommunityChannelListByCommunityIdExceptAll(this.communityId);
    },

    methods: {
        queryCommunityArticle(communityArticleId) {
            const vue = this;
            $.ajax({
                url: vue.communityArticleUpdateUrl + "/queryCommunityArticle",
                type: "get",
                data: {
                    communityArticleId
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.form = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 更新社区文章
        updateCommunityArticle(form) {
            const vue = this;
            this.$refs["form"].validate((valid) => {
                if (valid) {
                    $.ajax({
                        url: vue.communityArticleUpdateUrl + "/updateCommunityArticle",
                        type: "put",
                        data: {
                            communityArticleId: vue.communityArticleId,
                            communityArticleStr: JSON.stringify(form),
                        },
                        headers: {
                            Authorization: "Bearer " + store.state.user.token,
                        },
                        success(response) {
                            if (response.code == vue.ResultStatus.SUCCESS) {
                                vue.$modal.msgSuccess(response.msg);
                                vue.$router.go(-1);
                            } else {
                                vue.$modal.msgError(response.msg);
                            }
                        }
                    })
                }
            })
        },
        // 删除阿里云的文件
        onUploadRemove(file) {
            this.form.picture = null;
            this.deleteCommunityArticlePicture(this.communityArticleId);
        },
        // 删除数据库中的图片
        deleteCommunityArticlePicture(communityArticleId) {
            const vue = this;
            $.ajax({
                url: vue.communityArticleUpdateUrl + "/deleteCommunityArticlePicture",
                type: "delete",
                data: {
                    communityArticleId
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
            this.form.picture = response.data;
            this.updateCommunityArticlePicture(this.form.picture);
        },
        // 更新社区文章图片
        updateCommunityArticlePicture(picture) {
            const vue = this;
            $.ajax({
                url: vue.communityArticleUpdateUrl + "/updateCommunityArticlePicture",
                type: "put",
                data: {
                    picture,
                    communityArticleId: vue.communityArticleId,
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
        handleClose(tag) {
            this.form.communityClassificationLabelList.splice(this.dynamicTags.indexOf(tag), 1);
        },
        // 通过社区id查询除了全部的社区频道列表
        queryCommunityChannelListByCommunityIdExceptAll(communityId) {
            const vue = this;
            $.ajax({
                url: vue.publishCommunityUrl + "/queryCommunityChannelListByCommunityIdExceptAll",
                type: "get",
                data: {
                    communityId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.channelList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
    },
};
</script>

<style scoped>
.tip {
    display: inline-block;
    color: #409EFF;
    font-size: 14p;
    margin-right: 10px;
}
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