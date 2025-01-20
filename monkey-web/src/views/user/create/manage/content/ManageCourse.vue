<template>
    <div class="MonkeyWebManageCourse-container">
        <el-form :inline="true" :model="formInline" class="demo-form-inline" label-width="auto">
            <el-form-item label="类型">
                <el-select v-model="formInline.formTypeId" placeholder="请选择形式类型" size="mini">
                <el-option 
                v-for="formType in formTypeList"  
                :label="formType.name" 
                :value="formType.id"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="名称">
                <el-input size="mini" v-model="formInline.name" placeholder="请输入课程名称"></el-input>
            </el-form-item>
            <el-form-item label="时间">
                    <el-date-picker
                    size="mini"
                    v-model="formInline.dateList"
                    type="daterange"
                    range-separator="至"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期">
                    </el-date-picker>
            </el-form-item>
            <el-form-item>
                <el-button size="mini" type="primary" @click="submitQuery">查询</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script>
export default {
    name: 'MonkeyWebManageCourse',

    data() {
        return {
            formTypeList: [],
            formInline: {
                type: "",
                formTypeId: "",
                status: "",
                isCuration: "",
                dateList: "",
                name: "",
                // 资源分类列表
                courseLabel: [],
            },
            courseLabelList: [],
            courseContentManageUrl: "http://localhost:80/monkey-course/content/manage",
        };
    },

    created() {
        this.queryFormType();
    },

    methods: {
        // 查询形式类型列表
        queryFormType() {
            const vue = this;
            $.ajax({
                url: vue.courseContentManageUrl + "/queryFormType",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.formTypeList = response.data;
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
.MonkeyWebManageCourse-container {
    padding: 20px 20px 0 20px;
    background-color: #fff;
    height: calc(100vh - 171px);
    overflow-y: auto;
}
</style>