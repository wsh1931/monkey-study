<template>
    <div class="MonkeyWebCourseCenterViews-container" style="width: 1300px; margin: 10px auto;">
        <el-row>
            <CourseLabelList
            @getCourseListByTwoLabelId="getCourseListByTwoLabelId"/>
        </el-row >
        <el-row  style="background-color: #FFFFFF">
             <el-row style=" padding: 20px;">
                    <el-col class="click font-size top" :span="1">热门</el-col>
                    <el-col class="click font-size top" :span="1">最新</el-col>
                    <el-col class="click font-size top" :span="1">
                        <el-row class="click font-size">
                            <el-col :span="15">价格</el-col>
                            <el-col :span="9">
                                <span v-if="priceType == '0'" class="el-icon-d-caret" style="color: #DCDFE6;"></span>
                                    <span class="el-icon-caret-bottom" v-if="priceType == '1'"></span>
                                    <span class="el-icon-caret-top" v-if="priceType == '-1'"></span>
                            </el-col>
                        </el-row>
                    </el-col>
                    <el-col :span="5" >
                        <el-input
                            size="mini"
                            placeholder="请输入想找的课程名"
                            suffix-icon="el-icon-search"
                            v-model="searchContent">
                        </el-input>
                    </el-col>
                     
                </el-row>
                <CourseCard
                style="padding: 10px;"/>
                </el-row>
        <el-row>
            <PagiNation
                :totals="totals"
                :currentPage="currentPage" 
                :pageSize="pageSize" 
                @handleCurrentChange = "handleCurrentChange"
                @handleSizeChange="handleSizeChange"/>
    </el-row>
    </div>
</template>

<script>
import $ from 'jquery'
import CourseLabelList from '@/components/course/CourseLabelList.vue';
import PagiNation from '@/components/pagination/PagiNation.vue';
import CourseCard from '@/components/course/CourseCard.vue';

export default {
    name: 'MonkeyWebCourseCenterViews',
    components: {
        CourseLabelList,
        PagiNation,
        CourseCard
    },

    data() {
        return {
            courseUrl: "http://localhost/monkey-course/course",
            currentPage: 1,
            pageSize: 10,
            totals: 0,
        };
    },

    methods: {
        handleSizeChange(val) {
            this.pageSize = val;
        },
        handleCurrentChange(val) {
            this.currentPage = val;
        },
        // 通过形式id, 一级标签id, 二级标签id查询课程列表
        getCourseListByTwoLabelId(formTypeId, twoLabelId) {
            const vue = this;
            $.ajax({
                url: vue.courseUrl + "/getCourseListByTwoLabelId",
                type: "get",
                data: {
                    formTypeId,
                    twoLabelId,
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize
                },
                success(response) {
                    if (response.code == '200') {
                        vue.courseList = response.data;
                    } else {
                        vue.$modal.msgError("发生未知错误，查找二级标签失败");
                    }
                },
                error() {
                    vue.$modal.msgError("发生未知错误，查找二级标签失败");
                }
            })
        },
    },
};
</script>

<style scoped>
.top {
    margin-top: 4px;
}
.font-size {
    font-size: 14px;
}
.click:hover {
    cursor: pointer;
    color: #409EFF;
}
</style>