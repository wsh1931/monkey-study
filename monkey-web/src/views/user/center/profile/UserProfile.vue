<template>
    <div class="MonkeyWebUserProfile-container">
        <el-form :model="userInfo" :rules="rules" ref="userInfo" label-width="auto" class="demo-userInfo">
            <el-form-item label="用户编号">
                <span class="user-idx">132123123132</span>
            </el-form-item>
            <el-form-item label="用户头像" prop="photo">
                <ElUploadPicture
                @onUploadSuccess="onUploadSuccess"
                @onUploadRemove="onUploadRemove"
                :module="module"
                :photo="userInfo.photo"/>          
            </el-form-item>
            <el-form-item label="用户名称" prop="username">
                <el-input v-model="userInfo.username"></el-input>
            </el-form-item>
            <el-form-item label="用户性别" prop="sex">
                <el-radio-group v-model="userInfo.sex">
                <el-radio :label="0">男</el-radio>
                <el-radio :label="1">女</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item label="出生日期" > 
                <el-date-picker 
                type="date"
                format="yyyy-MM-dd"
                placeholder="选择出生日期" 
                v-model="userInfo.birthday" style="width: 200px;"></el-date-picker>
            </el-form-item>
            <el-form-item label="工作单位" prop="jobUnit">
                <el-input v-model="userInfo.jobUnit"></el-input>
            </el-form-item>
            <el-form-item label="工作岗位" prop="job">
                <el-input v-model="userInfo.job"></el-input>
            </el-form-item>
            <el-form-item label="用户简介" prop="brief">
                <el-input
                    type="textarea"
                    :autosize="{ minRows: 3 , maxRows: 3}"
                    placeholder="您可以介绍你自己"
                    v-model="userInfo.brief"
                    :show-word-limit="true"
                    minlength="1"
                    maxlength="255">
                </el-input>
            </el-form-item>
            <el-form-item style="text-align: center;">
                <el-button type="primary" @click="updateUserInfo('userInfo')">确认修改</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script>
import $ from 'jquery'
import store from '@/store';
import ElUploadPicture from '@/components/upload/ElUploadPicture.vue';
export default {
    name: 'MonkeyWebUserProfile',
    components: {
        ElUploadPicture
    },
    data() {
        return {
            module: "userPhoto/",
            value: "",
            userInfo: {
                username: '',
                birthday: '',
                brief: "",
                jobUnit: "",
                job: "",
                photo: "",
            },
            rules: {
                username: [
                    { required: true, message: '请输入用户名', trigger: 'blur' },
                    { min: 1, max: 30, message: '长度在 1 到 30 个字符', trigger: 'blur' }
                ],
                jobUnit: [
                    { message: '请输入工作单位', trigger: 'blur' },
                    { max: 30, message: '长度不能超过 30 个字符', trigger: 'blur' }
                ],
                job: [
                    { message: '请输入工作岗位', trigger: 'blur' },
                    { max: 20, message: '长度不能超过 20 个字符', trigger: 'blur' }
                ],
                brief: [
                    { message: '用输入用户简介', trigger: 'blur' },
                    { min: 1, max: 255, message: '长度不能超过 255 个字符', trigger: 'blur' }
                ],
                sex: [
                    { required: true, message: '请选择您的性别', trigger: 'change' }
                ],
                photo: [
                    { required: true, message: '请上传用户头像', trigger: 'blur' }
                ],
            },
            userCenterProfileUrl: "http://localhost:80/monkey-user/center/profile",
        };
    },

    created() {
        this.queryUserInfo()
    },

    methods: {
        // 更新用户信息
        updateUserInfo(formName) {
            this.$refs[formName].validate((valid) => {
            if (valid) {
                const vue = this;
                $.ajax({
                    url: vue.userCenterProfileUrl + "/updateUserInfo",
                    type: "put",
                    data: {
                        userInfoStr: JSON.stringify(vue.userInfo),
                    },
                    headers: {
                        Authorization: "Bearer " + store.state.user.token,
                    },
                    success(response) {
                        if (response.code == vue.ResultStatus.SUCCESS) {
                            vue.$modal.msgSuccess(response.msg);
                        } else {
                            vue.$modal.msgError(response.msg);
                        }
                    }
                })
            } else {
                alert(valid)
                return false;
            }
            });
        },
        // 查询用户信息
        queryUserInfo() {
            const vue = this;
            $.ajax({
                url: vue.userCenterProfileUrl + "/queryUserInfo",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.userInfo = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 删除阿里云的文件
        onUploadRemove(file) {
            this.userInfo.photo = "";
            // 更新用户头像
            this.updateUserHeadImg(this.userInfo.photo);
        },
        // 上传成功之后判断上传的图片是否成功
        onUploadSuccess(response) {
            this.userInfo.photo = response.data;
            // 更新用户头像
            this.updateUserHeadImg(this.userInfo.photo);
        },
        updateUserHeadImg(photo) {
            const vue = this;
            $.ajax({
                url: vue.userCenterProfileUrl + "/updateUserHeadImg",
                type: "put",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    photo
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.$modal.msgSuccess(response.msg)
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
.user-idx {
    color: gray;
}
.MonkeyWebUserProfile-container {
    padding: 20px;
    background-color: #fff;
}
</style>