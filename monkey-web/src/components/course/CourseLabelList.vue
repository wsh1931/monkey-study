<template>
    <div class="MonkeyWebCourseLabelList-container" style="background-color: #FFFFFF; padding: 10px; font-size: 12px;">
        <el-row>
            <el-col :span="1">
                <span style="background-color: #EEEEEE; padding: 5px;">形式</span>
            </el-col>
            <el-col :span="21">
                <span 
                v-for="courseForm in courseFormList" :key="courseForm"
                style="padding: 5px; margin: 5px;" 
                :class="['formAllHover', {selectedForm: courseFormSelected == courseForm.name}]" 
                @click="courseFormSelected=courseForm.name">
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
                :class="['formAllHover', {selectedForm: oneLabel.id == selectedOneLabelId}]" 
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
                :class="['formAllHover', {selectedForm: twoLabel.id == selectedTwoLabelId}]" 
                @click="getCourseListByTwoLabelId(twoLabel)">
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
            courseFormSelected: "",
            courseFormList: [
                {
                    id: "1",
                    name: "全部"
                },
                {
                    id: "2",
                    name: "免费课"
                },
                {
                    id: "3",
                    name: "VIP课"
                },
                {
                    id: "4",
                    name: "官方推荐"
                },
            ],
            // 一级标签列表
            oneLabelList: [],
            // 选中的一级标签
            selectedOneLabelId: "",
            // 二级标签列表
            twoLabelList: [],
            // 选中的二级标签
            selectedTwoLabelId: "",
            // 课程列表
            courseList: [],
        };
    },

    created() {
        this.getOneLabelList();
    },
    methods: {
        // 清楚筛选
        clearAllSelected() {
            this.courseFormSelected = "";
            this.selectedOneLabelId = "";
            this.selectedTwoLabelId = "";
        },
        // 通过二级标签id查询课程列表
        getCourseListByTwoLabelId(twoLabel) {
            this.selectedTwoLabelId = twoLabel.id;
            this.$emit("getCourseListByTwoLabelId", twoLabel);
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
                    if (response.code == '10000') {
                        vue.selectedOneLabelId = oneLabel.id;
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
                    if (response.code == '10000') {
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