<template>
    <div class="MonkeyWebCourseLabelList-container">
        <el-row class="show-label">
            <el-row> 
                <el-col :span="1">
                    <span style="background-color: #EEEEEE; padding: 5px;">形式</span>
                </el-col>
                <el-col :span="21">
                    <span 
                    v-for="courseForm in courseFormList" :key="courseForm"
                    style="padding: 5px; margin: 5px;" 
                    :class="['formAllHover', {selectedForm: formTypeId == courseForm.id}]" 
                    @click="updateFormType(courseForm)">
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
                <el-col :span="1" v-if="oneLabelId != '-1'">
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
        </el-row>
    </div>
</template>

<script>
import $ from 'jquery'
export default {
    name: 'MonkeyWebCourseLabelList',
    props: ['formTypeId', 'oneLabelId', 'twoLabelId'],
    data() {
        return {
            // 课程形式
            courseUrl: "http://localhost/monkey-course/course",
            courseFormList: [],
            // 一级标签列表
            oneLabelList: [],
            // 二级标签列表
            twoLabelList: [],
            // 课程列表
            courseList: [],
        };
    },

    created() {
        this.getOneLabelList();
        this.getFormTypeList();
    },
    methods: {
        updateFormType(formType) {
            this.$emit('updateFormTypeId', formType.id);
            this.$emit('updateToFire');
            this.$emit('getFireCourseListByOneLabelAndTowLabelAndFormId', formType.id, this.$props.oneLabelId, this.$props.twoLabelId);
        },
        // 得到形式列表集合
        getFormTypeList() {
            const vue = this;
            $.ajax({
                url: vue.courseUrl + "/getFormTypeList",
                type: "get",
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.courseFormList = response.data;
                        vue.$props.formTypeId = vue.courseFormList[0].id;
                    }
                },
            })
        },
        // 清楚筛选
        clearAllSelected() {
            this.$emit('updateOneLabelId', -1);
            this.$emit('updateTwoLabelId', -1);
            this.$emit('updateFormTypeId', -1);
            this.twoLabelList = [];
            this.$emit("getFireCourseListByOneLabelAndTowLabelAndFormId", -1, -1, -1);
        },
        // 通过二级标签id查询课程列表
        getCourseListByTwoLabelId(formTypeId, twoLabelId) {
            this.$emit('updateTwoLabelId', twoLabelId);
            this.$emit("getFireCourseListByOneLabelAndTowLabelAndFormId", formTypeId, this.oneLabelId , twoLabelId);
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
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.$emit('updateOneLabelId', oneLabel.id);
                        vue.$emit('updateTwoLabelId', -1);
                        vue.$emit("getFireCourseListByOneLabelAndTowLabelAndFormId", vue.formTypeId, oneLabel.id, -1); 
                        vue.twoLabelList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
        },
        // 得到一级标签列表
        getOneLabelList() {
            const vue = this;
            $.ajax({
                url: vue.courseUrl + "/getOneLabelList",
                type: "get",
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.oneLabelList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
        },
    },
};
</script>

<style scoped>
.MonkeyWebCourseLabelList-container {
    background-color: #FFFFFF;
     padding: 20px; 
     font-size: 12px;
}
.show-label {
    animation: show-label 0.4s linear;
}

@keyframes show-label {
    0% {
        transform: translateY(-100px);
        opacity: 0;
    }

    100% {
        transform: translateY(0);
        opacity: 1;
    }
}
.selectedForm {
    color: #409EFF;
    background-color: #ECF5FF;
}

.formAllHover:hover {
    cursor: pointer;
    color: #409EFF;
}

</style>