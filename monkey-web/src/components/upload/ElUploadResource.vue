<template>
    <div style="text-align: center;">
        <el-upload
        class="upload-demo"
        drag
        :show-file-list="false"
        :data="{module: module}"
        :on-success="successUpload"
        :on-error="errorUpload"
        :on-remove="removeUpload"
        :before-upload="beforeUpload"
        :headers="{ Authorization: 'Bearer ' + $store.state.user.token}"
        :action="aliyunossUrl + '/upload'">
            <div class="el-upload__text" v-if="!isUploadFile">
                将文件拖到此处，或<em>&nbsp;点击上传</em>
                小于1000MB（请不要上传电子书等存在侵权的资源哦！）
            </div>
            <div class="el-upload__text" v-if="isUploadFile">
                <img class="file-img" :src="file.typeImg" alt="">
                <span class="file-name">{{ file.name }}</span>
                <span @click.stop="isShowPreview = true" class="preview">&nbsp;&nbsp;预览(只有文件可预览)</span>
                <em>&nbsp;&nbsp;重新上传</em>
                <span @click.stop="removeUpload(file)" class="delete">&nbsp;&nbsp;删除</span>
            </div>
        </el-upload>
        <el-dialog
        top="10px"
        :title="'预览' + file.name + '文件 '"
        :visible.sync="isShowPreview"
        width="80%">
        <iframe :src="'https://view.officeapps.live.com/op/view.aspx?src=' + file.url" width="100%" height="600px">文件</iframe>
        <span slot="footer" class="dialog-footer">
            <el-button @click="isShowPreview = false" type="primary">返 回</el-button>
        </span>
        </el-dialog>
    </div>
</template>

<script>
import $ from 'jquery'
import store from '@/store';
export default {
    name: 'MonkeyWebElUploadResource',
    props: ['isSubmit'],
    data() {
        return {
            // 是否预览文件
            isShowPreview: false,
            module: "resource/",
            fileList: [],
            aliyunossUrl: "http://localhost:80/monkey-service/aliyun/oss",
            uploadResourceUrl: "http://localhost:80/monkey-resource/uploadResource",
            // 是否上传文件
            isUploadFile: false,
            // 上传的文件信息
            file: {
                name: "",
                // 文件类型
                type: "",
                // 文件类型图片
                typeImg: "",
                // 文件地址
                url: "",
            },
            preview: false,
        };
    },
    watch: {
        isSubmit(newValue) {
            this.isSubmit = newValue;
            alert(newValue);
        }
    },
    methods: {
        // 在路由跳转之前更新
        updateSubmit() {
            this.isSubmit = true;
        },
        // 通过文件类型得到文件类型图片
        queryFileTypeIcon(fileType) {
            const vue = this;
            $.ajax({
                url: vue.uploadResourceUrl + "/queryFileTypeIcon",
                type: "get",
                headers: {
                    Authorization: 'Bearer ' + store.state.user.token
                },
                data: {
                    fileType,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.$set(vue.file, 'typeImg' , response.data);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 删除资源
        removeUpload(file) {
            const vue = this;
            $.ajax({
                url: vue.aliyunossUrl + "/remove",
                type: "delete",
                headers: {
                    Authorization: 'Bearer ' + store.state.user.token
                },
                data: {
                    fileUrl: file.url
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        if (file.name == vue.file.name) {
                            vue.file = {};
                        }
                        vue.isUploadFile = false;
                        vue.$emit("removeUpload");
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
        },
        // 上传成功
        successUpload(response, file, fileList) {
            if (response.code == this.ResultStatus.SUCCESS) {
                this.isUploadFile = true;
                // 得到文件类型
                var flieArr = file.name.split('.');
                let type = flieArr[flieArr.length - 1];
                this.file.type = type;
                this.file.name = file.name;
                this.file.url = response.data;
                // 通过文件类型得到文件类型图片
                this.queryFileTypeIcon(type);
                
                this.$emit("uploadSuccess", file);
                this.$modal.msgSuccess("上传资源成功");
            } else {
                this.$modal.msgError(response.msg);
            }
        },
        beforeUpload(file) {
            if (this.isUploadFile) {
                this.removeUpload(this.file);
            }
            const size = file.size / 1024 / 1024;
            if (size > 1000) {
                this.$modal.msgWarning("文件大小不能超过1000MB");
                return false;
            }
        },
        errorUpload(response, file) {
            if (response.status == '500') {;
                this.$modal.msgError(`上传图片${file.name}失败，服务器出现异常，请稍后再试。`);
                return false;
            } 
        },
    },
    // 在页面跳转之前，若用户没有上传资源则删除该资源
    beforeDestroy() {
        if (this.isUploadFile && !this.isSubmit) {
            // 若还没有提交
            alert(this.isSubmit);
            this.removeUpload(this.file);
        }
    },
};
</script>

<style scoped>
.delete {
    color: #F56C6C;
}
.delete:hover {
    opacity: 0.5;
    cursor: pointer;
}
.preview {
    color: #409EFF;
}
.preview:hover {
    opacity: 0.5;
    cursor: pointer;
}
.file-img {
    height: 40px;
    width: 40px;
    vertical-align: middle;
}
.file-name {
    vertical-align: middle;
}
.el-upload__text {
    line-height: 80px;
}

::v-deep .el-upload{
    width: 100%;
    height: 80px;
}
::v-deep .el-upload .el-upload-dragger{
    width: 100%;
    height: 80px;
}
.upload-demo {
    width: 100%;
    height: 20px;
}

</style>