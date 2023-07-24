<template>
    <div class="MonkeyWebCourseLabelList-container" style="background-color: #FFFFFF; padding: 20px; font-size: 12px;">
        <el-row>
            <el-col :span="1">
                <span style="background-color: #EEEEEE; padding: 5px;">形式</span>
            </el-col>
            <el-col :span="21">
                <span 
                v-for="courseForm in courseFormList" :key="courseForm"
                style="padding: 5px; margin: 5px;" 
                :class="['formAllHover', {selectedForm: formTypeId == courseForm.id}]" 
                @click="formTypeId=courseForm.id">
                {{ courseForm.name }}</span>
            </el-col>
            <el-col :span="2">
                <span 
                style="padding: 5px; margin: 5px;" 
                class='formAllHover'
                @click="clearAllSelected()">
                清除筛选</span>
            </el-col>
        </el-row>
        <el-row style="margin-top: 20px;">
            <el-col :span="1">
                <span style="background-color: #EEEEEE; padding: 5px;">方向</span>
            </el-col>
            <el-col :span="23">
                <span 
                v-for="oneLabel in oneLabelList" :key="oneLabel.id"
                style="padding: 5px; margin: 5px;" 
                :class="['formAllHover', {selectedForm: oneLabel.id == oneLabelId}]" 
                @click="getTwoLabelListByOneLabelId(oneLabel)">
                {{ oneLabel.labelName }}</span>
            </el-col>
        </el-row>

        <el-row style="margin-top: 20px;">
            <el-col :span="1">
                <span style="background-color: #EEEEEE; padding: 5px;">分类</span>
            </el-col>
            <el-col :span="23">
                <span 
                v-for="twoLabel in twoLabelList" :key="twoLabel.id"
                style="padding: 5px; margin: 5px;" 
                :class="['formAllHover', {selectedForm: twoLabel.id == twoLabelId}]" 
                @click="getCourseListByTwoLabelId(formTypeId, twoLabel.id)">
                {{ twoLabel.labelName }}</span>
            </el-col>
        </el-row>
    </div>
</template>

<script>
import $ from 'jquery'
export default {
    name: 'MonkeyWebCourseLabelList',

    data() {
        return {
            // 课程形式
            courseUrl: "http://localhost/monkey-course/course",
            formTypeId: -1,
            courseFormList: [],
            // 一级标签列表
            oneLabelList: [],
            // 选中的一级标签
            oneLabelId: -1,
            // 二级标签列表
            twoLabelList: [],
            // 选中的二级标签
            twoLabelId: -1,
            // 课程列表
            courseList: [],
        };
    },

    created() {
        this.getOneLabelList();
        this.getFormTypeList();
    },
    methods: {
        // 得到形式列表集合
        getFormTypeList() {
            const vue = this;
            $.ajax({
                url: vue.courseUrl + "/getFormTypeList",
                type: "get",
                success(response) {
                    if (response.code == '200') {
                        vue.courseFormList = response.data;
                        vue.formTypeId = vue.courseFormList[0].id;
                    } else {
                        vue.$modal.msgError("查找形式标签失败");
                    }
                },
                error() {
                    vue.$modal.msgError("查找形式标签失败");
                }
            })
        },
        // 清楚筛选
        clearAllSelected() {
            this.formTypeId = -1;
            this.oneLabelId = -1;
            this.twoLabelId = "";
        },
        // 通过二级标签id查询课程列表
        getCourseListByTwoLabelId(formTypeId, twoLabelId) {
            this.twoLabelId = twoLabelId;
            this.$emit("getCourseListByTwoLabelId", formTypeId, twoLabelId);
        },
        getTwoLabelListByOneLabelId(oneLabel) {
            const vue = this;
            $.ajax({
                url: vue.courseUrl + "/getTwoLabelListByOneLabelId",
                type: "get",
                data: {
                    oneLabelId: oneLabel.id,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.oneLabelId = oneLabel.id;
                        vue.twoLabelList = response.data;
                    } else {
                        vue.$modal.msgError("发生未知错误，查找二级标签失败");
                    }
                },
                error() {
                    vue.$modal.msgError("发生未知错误，查找二级标签失败");
                }
            })
        },
        // 得到一级标签列表
        getOneLabelList() {
            const vue = this;
            $.ajax({
                url: vue.courseUrl + "/getOneLabelList",
                type: "get",
                success(response) {
                    if (response.code == '200') {
                        vue.oneLabelList = response.data;
                    } else {
                        vue.$modal.msgError("发生未知错误，查找一级标签失败");
                    }
                },
                error() {
                    vue.$modal.msgError("发生未知错误，查找一级标签失败");
                }
            })
        },
    },
};
</script>

<style scoped>
.selectedForm {
    color: #409EFF;
    background-color: #ECF5FF;
}

.formAllHover:hover {
    cursor: pointer;
    color: #409EFF;
}

</style>