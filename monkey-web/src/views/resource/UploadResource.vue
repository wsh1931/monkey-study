<template>
    <div class="MonkeyWebUploadResource-container">
        <div>
        <ElUploadResource
        style="margin-top: 10px;"
        @uploadSuccess="uploadSuccess"
        :isSubmit="isSubmit"/>
        </div>
        
    </div>
</template>

<script>
import $ from 'jquery'
import store from '@/store';
import ElUploadResource from '@/components/upload/ElUploadResource'
export default {
    name: 'MonkeyWebUploadResource',
    components: {
        ElUploadResource
    },
    data() {
        return {
            uploadForm: {
                fileUrl: "",
                fileType: "",
                fileName: "",
            },
            // 用户是否提交文件，保存数据
            isSubmit: false,
        };
    },

    created() {
        
    },

    methods: {
        
        // 上传文件成功后更新文件信息
        uploadSuccess(file) {
            // 得到文件类型
            var flieArr = file.name.split('.');
            this.uploadForm.fileName = file.name;
            this.uploadForm.fileType = flieArr[flieArr.length - 1];
            this.uploadForm.fileUrl = file.response.data;
        },
    },
};
</script>

<style scoped>
.MonkeyWebUploadResource-container {
    margin: 10px auto;
    width: 1000px;
}
</style>