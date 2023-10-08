<template>
    <div class="ElUpload-container">
            <el-upload
            accept="image/*"
            style="display: flex;"
            class="upload-box"
            :action="aliyunossUrl + '/upload'"
            :data="{module: module}"
            :on-success="onUploadSuccess"
            :on-remove="onUploadRemove"
            :on-error="errorUpload"
            :before-upload="beforeAvatarUpload"
            list-type="picture-card"
            :file-list="fileList"
            :headers="{ Authorization: 'Bearer ' + $store.state.user.token}">
            <i  class="el-icon-plus avatar-uploader-icon"></i>
        </el-upload> 
        
    </div>
</template>

<script>
import $ from "jquery"
import store from "@/store";

export default {
    name: "ElUploadPicture",
    props: ['photo', 'module'],
    data() {
        return {
            aliyunossUrl: "http://localhost:80/monkey-service/aliyun/oss",
            fileList: [],
        }
    },
    watch: {
        photo(newVal) {
            this.fileList.push({ 'url': newVal });
        }
    },
    methods: {
        // 图片上传失败
        errorUpload(response, file, fileList) {
            if (response.status == '500') {;
                this.$modal.msgError(`上传图片${file.name}失败，服务器出现异常，请稍后再试。`);
                return false;
            } 
        },
        // 删除阿里云的文件
        onUploadRemove(file) {
            const vue = this;
            $.ajax({
                url: vue.aliyunossUrl + "/remove",
                type: "delete",
                headers: {
                    Authorization: 'Bearer ' + store.state.user.token
                },
                data: {
                    fileUrl: file.response.data
                },
                success(response) {
                    if (response.code == "200") {
                        vue.$modal.msgSuccess("删除成功");
                        vue.$emit("onUploadRemove", "");
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
        },
        // 上传成功之后判断上传的图片是否成功
        onUploadSuccess(response) {
            if (response.code == "200") {
                this.$modal.msgSuccess("上传成功");
                this.$emit("onUploadSuccess", response);
            } else {
                this.$modal.msgError("上传失败");
            }
        },  
        beforeAvatarUpload(file) {
            if (this.fileList.length >= 1) {
                this.$modal.msgWarning("上传图片不能超过一张");
                return false;
            }
            const isJPG = file.type === 'image/jpeg';
            const isPng = file.type === 'image/png'
            const isLt2M = file.size / 1024 / 1024 < 2;
            if (!isJPG && !isPng) {
                this.$modal.msgError('上传头像图片只能是 JPG/PNG 格式!');
            }
            if (!isLt2M) {
                this.$modal.msgError('上传头像图片大小不能超过 2MB!');
            }
            return isJPG || isLt2M;
        }, 
    }
}
</script>

<style scoped>

</style>