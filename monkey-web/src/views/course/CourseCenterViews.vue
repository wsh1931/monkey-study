<template>
    <div class="MonkeyWebCourseCenterViews-container" style="width: 1100px; margin: 10px auto;">
        <el-row>
            <CourseLabelList
            @getCourseListByTwoLabelId="getCourseListByTwoLabelId"/>
        </el-row>
        <el-row>

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
export default {
    name: 'MonkeyWebCourseCenterViews',
    components: {
        CourseLabelList,
        PagiNation
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
        // 通过二级标签id查询课程列表
        getCourseListByTwoLabelId(twoLabel) {
            const vue = this;
            $.ajax({
                url: vue.courseUrl + "/getCourseListByTwoLabelId",
                type: "get",
                data: {
                    twoLabelId: twoLabel.id,
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize
                },
                success(response) {
                    if (response.code == '10000') {
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

<style lang="scss" scoped>

</style>