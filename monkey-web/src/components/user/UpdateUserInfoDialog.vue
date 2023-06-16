<template>
    <div class="UpdateUserInfo">
        <el-drawer 
        title="编辑用户资料"
        :before-close="handleClose"
        :visible.sync="Visible"
        :close-on-click-modal="false"
        direction="rtl"
        custom-class="demo-drawer"
        ref="drawer">
        <el-form
         :model="userInformation"
         style="margin-left: 25px;"
         :rules="rules"
         ref="userInformation">
            <el-form-item  label="用户头像" prop="photo">
                <ElUploadPicture
                @onUploadSuccess="onUploadSuccess"
                @onUploadRemove="onUploadRemove"
                :module="module"
                :form="userInformation"/>
            </el-form-item>
               
            <el-form-item label="用户姓名" prop="username">
                <el-input v-model="userInformation.username" style="width: 200px;" placeholder="请输入用户姓名" />
            </el-form-item>

            <el-form-item label="手机号码" prop="phone">
                <el-input v-model="userInformation.phone" style="width: 200px;" placeholder="请输入用户手机号" />
            </el-form-item>

            <el-form-item label="用户邮箱" prop="email">
                <el-input v-model="userInformation.email" style="width: 200px;" placeholder="请输入用户邮箱" />
            </el-form-item>

            <el-form-item label="工作单位" prop="jobUnit">
                <el-input v-model="userInformation.jobUnit" style="width: 200px;" placeholder="请输入用户工作单位" />
            </el-form-item>

            <el-form-item label="职位" prop="job">
                <el-input v-model="userInformation.job" style="width: 200px;" placeholder="请输入用户职业" />
            </el-form-item>

            <el-form-item label="出生日期" prop="birthday">
                <el-date-picker
                v-model="userInformation.birthday"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="选择日期">
                </el-date-picker>
            </el-form-item>

            <el-form-item label="用户简介" prop="brief">
                <el-input
                type="textarea"
                :autosize="{ minRows: 2, maxRows: 4}"
                placeholder="请输入用户简介"
                style="width: 250px;"
                v-model="userInformation.brief">
                </el-input>
            </el-form-item>
        </el-form>
        <div class="demo-drawer__footer" style="text-align: right; margin-right: 10px;">
            <el-button type="primary" @click="handleSubmit('userInformation')">提交</el-button>
            <el-button @click="cancelForm">取 消</el-button>
    </div>
</el-drawer>
        
    </div>
</template>

<script>
import ElUploadPicture from '@/components/upload/ElUploadPicture'
export default {
    name: "UpdateUserInfo",
    props: {
        dialogFormVisible: Boolean,
        formLabelWidth: String,
        userForm: Object,
    },
    components: {
        ElUploadPicture
    },
    data() {
        return {
            userInformation: {},
            // 图片存放的模块地址
            module: "userPhoto/",
            rules: {
                username: [
                    {required: true, message:'请输入用户姓名', trigger: 'blur'},
                    {min: 1, max: 30, message: '长度在30个字符以内', trigger: 'blur'}
                ],
                jobUnit: [
                    {min: 1, max: 30, message: '长度在30个字符以内', trigger: 'blur'}
                ],
                job: [
                    {max: 20, message: '长度在20个字符以内', trigger: 'blur'}
                ],
                brief: [
                    {max: 255, message: '长度在255个字符以内', trigger: 'blur'},
                ],
                phone: [
                    {min: 11, max: 11, message: '请输入正确的电话号码', trigger: 'blur'}
                ],
                email: [
                    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
                ]
            }
        }
    },
  watch: { //监听父组件传过来的selectItems 
    userForm: { 
        handler(newVal){ 
            this.userInformation = newVal;
        }, 
        immediate:true,//immediate:true代表如果在 wacth 里声明了之后，就会立即先去执行里面的                    
                     //handler方法，如果为 false，不会在绑定的时候就执行。 
        deep:true//deep，默认值是 false，代表是否深度监听。 
    },
            
    },
    computed: {
        // 子组件不能修改父组件传过来的值，改变弹框右上角的×
        Visible: {
            get() {
                return this.dialogFormVisible
            },
            set(val) {
                if (!val) {
                this.$emit('dialogVisible', false)
                }
            }
        }
  },
    methods: {
        handleSubmit(formName) {
            this.$refs[formName].validate((valid) => {
          if (valid) {
            this.$emit("updateForm", this.userInformation)
            this.$emit("dialogVisible", false)
          } else {
            return false;
          }
        });
            
        },
        cancelForm() {
            this.$emit("dialogVisible", false)
        },
        onUploadSuccess(data) {
            this.userInformation.photo = data;
        },
        onUploadRemove(data) {
            this.userInformation.photo = data;
        },
        dialogVisible(status) {
            this.$emit("dialogVisible", status)
        },
    }
}
</script>

<style scoped>
  .test {
    border: 1px solid;
    height: 178px;
    width: 178px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
  }
</style>