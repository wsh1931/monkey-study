<template>
    <div class="MonkeyWebCourseCenterViews-container " style="width: 1300px; margin: 10px auto;">
        <el-row>
            <CourseLabelList
            @updateToFire="updateToFire"
            :formTypeId="formTypeId"
            :oneLabelId="oneLabelId"
            :twoLabelId="twoLabelId"
            @updateOneLabelId="updateOneLabelId"
            @updateTwoLabelId="updateTwoLabelId"
            @updateFormTypeId="updateFormTypeId"
            @getFireCourseListByOneLabelAndTowLabelAndFormId="getFireCourseListByOneLabelAndTowLabelAndFormId"
            @getCourseListByTwoLabelId="getCourseListByTwoLabelId"/>
        </el-row >
        <el-row class="divide"></el-row>
        <el-row  style="background-color: #FFFFFF">
            <el-row style=" padding: 20px;">
                <div @click="getFireCourseListByOneLabelAndTowLabelAndFormId(formTypeId, oneLabelId, twoLabelId)">
                    <el-col :class="['click', 'font-size', 'top']" :span="1">
                    热门
                    <div :class="{ underLine: selectedType == '热门'}"></div>
                    </el-col>
                </div>
                <div @click="getLastlyCourseListByOneLabelAndTowLabelAndFormId(formTypeId, oneLabelId, twoLabelId)">
                    <el-col class="click font-size top" :span="1">最新
                    <div :class="{ underLine: selectedType == '最新' }"></div>
                    </el-col>
                </div>
                    
                <div @click="getPriceCourseListByOneLabelAndTowLabelAndFormId(formTypeId, oneLabelId, twoLabelId)">
                    <el-col class="click font-size top" :span="1">
                        <el-row class="click font-size">
                            <el-col :span="15">价格</el-col>
                            <el-col :span="9">
                                <span v-if="priceType == '0'" class="el-icon-d-caret" style="color: #DCDFE6;"></span>
                                    <span class="el-icon-caret-bottom" v-if="priceType == '2'"></span>
                                    <span class="el-icon-caret-top" v-if="priceType == '1'"></span>
                            </el-col>
                        </el-row>
                        <div :class="{ underLine: selectedType == '价格' }"></div>
                    </el-col>
                </div>
                <!-- todo -->
                    <el-col :span="5">
                        <el-input
                            size="mini"
                            placeholder="请输入想找的课程名"
                            suffix-icon="el-icon-search"
                            v-model="searchContent"
                            @keyup.native="queryCourseByCourseTitle($event)">
                        </el-input>
                    </el-col>
            </el-row>
                <CourseCard
                :courseList="courseList" />
        </el-row>
        <el-row>
            <PagiNation
                style="text-align: right;"
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
            selectedType: "热门",
            // 1为升序，2为降序
            priceType: 0,
            // 课程列表,
            courseList: [],
            formTypeId: -1,
            // 选中的一级标签
            oneLabelId: -1,
            // 选中的二级标签
            twoLabelId: -1,
            // 搜索内容
            searchContent: "",
            // 当前正在查询的状态
            status: "",
        };
    },

    created() {
        this.getFireCourseListByOneLabelAndTowLabelAndFormId(-1, -1, -1);
    },

    methods: {
        // 通过课程名得到课程
        queryCourseByCourseTitle(event) {
            if (!event.ctrlKey && event.keyCode == '13') {
                this.status = '5';
                const vue = this;
                vue.currentPage = 1;
                vue.pageSize = 10;
                $.ajax({
                    url: vue.courseUrl + "/queryCourseByCourseTitle",
                    type: "get",
                    data: {
                        title: vue.searchContent,
                        currentPage: vue.currentPage,
                        pageSize: vue.pageSize
                    },
                    success(response) {
                        if (response.code == vue.ResultStatus.SUCCESS) {
                            vue.selectedType = '';
                            vue.courseList = response.data;
                            vue.totals = vue.courseList.length;
                        } else {
                            vue.$modal.msgError(response.msg);
                        }
                    }
                })
            }
            
        },
        // 得到最新课程列表
        getLastlyCourseListByOneLabelAndTowLabelAndFormId(formTypeId, oneLabelId, twoLabelId) {
            const vue = this;
            vue.formTypeId = formTypeId;
            vue.oneLabelId = oneLabelId;
            vue.twoLabelId = twoLabelId;
            vue.status = 0;
            
            $.ajax({
                url: vue.courseUrl + "/getLastlyCourseListByOneLabelAndTowLabelAndFormId",
                type: "get",
                data: {
                    formTypeId,
                    oneLabelId,
                    twoLabelId,
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.selectedType = '最新';
                        vue.courseList = response.data;
                        vue.totals = vue.courseList.length;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
                
            })
        },
        updateToFire() {
            this.selectedType = "热门"
        },
        updateOneLabelId(oneLabelId) {
            this.oneLabelId = oneLabelId;
        },
        updateTwoLabelId(twoLabelId) {
            this.twoLabelId = twoLabelId;
        },
        updateFormTypeId(formTypeId) {
            this.formTypeId = formTypeId;
        },
        // 通过形式id和一级标签id, 二级标签id得到所有课程列表
        getFireCourseListByOneLabelAndTowLabelAndFormId(formTypeId, oneLabelId, twoLabelId) {
            const vue = this;
            vue.formTypeId = formTypeId;
            vue.oneLabelId = oneLabelId;
            vue.twoLabelId = twoLabelId;
            vue.currentPage = 1;
            vue.pageSize = 10;
            vue.status = 1;
            $.ajax({
                url: vue.courseUrl + "/getFireCourseListByOneLabelAndTowLabelAndFormId",
                type: "get",
                data: {
                    formTypeId,
                    oneLabelId,
                    twoLabelId,
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.selectedType = '热门';
                        vue.courseList = response.data;
                        vue.totals = vue.courseList.length;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
                
            })
        },
        // 得到价格课程列表
        getPriceCourseListByOneLabelAndTowLabelAndFormId(formTypeId, oneLabelId, twoLabelId) {
            this.selectedType = '价格'
            this.priceType = (this.priceType + 1) % 3;
            if (this.priceType == '0') {
                this.priceType = (this.priceType + 1) % 3;
            }

            if (this.priceType == '1') {
                // 得到升序价格列表
                this.getAscPriceCourseListByOneLabelAndTowLabelAndFormId(formTypeId, oneLabelId, twoLabelId);
            } else if (this.priceType == '2'){
                // 得到降序价格列表
                this.getDescPriceCourseListByOneLabelAndTowLabelAndFormId(formTypeId, oneLabelId, twoLabelId);
            }
        },

        // 得到升序价格列表
        getAscPriceCourseListByOneLabelAndTowLabelAndFormId(formTypeId, oneLabelId, twoLabelId) {
            const vue = this;
            vue.formTypeId = formTypeId;
            vue.oneLabelId = oneLabelId;
            vue.twoLabelId = twoLabelId;
            vue.currentPage = 1;
            vue.pageSize = 10;
            vue.status = 2;
            $.ajax({
                url: vue.courseUrl + "/getAscPriceCourseListByOneLabelAndTowLabelAndFormId",
                type: "get",
                data: {
                    formTypeId,
                    oneLabelId,
                    twoLabelId,
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.selectedType = '价格';
                        vue.courseList = response.data;
                        vue.totals = vue.courseList.length;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
                
            })
        },
        // 得到降序价格列表
        getDescPriceCourseListByOneLabelAndTowLabelAndFormId(formTypeId, oneLabelId, twoLabelId) {
            const vue = this;
            vue.formTypeId = formTypeId;
            vue.oneLabelId = oneLabelId;
            vue.twoLabelId = twoLabelId;
            vue.currentPage = 1;
            vue.pageSize = 10;
            vue.status = 3;
            $.ajax({
                url: vue.courseUrl + "/getDescPriceCourseListByOneLabelAndTowLabelAndFormId",
                type: "get",
                data: {
                    formTypeId,
                    oneLabelId,
                    twoLabelId,
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.selectedType = '价格';
                        vue.courseList = response.data;
                        vue.totals = vue.courseList.length;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
                
            })
        },
        handleSizeChange(val) {
            this.pageSize = val;
            if (this.status == '0') {
                this.getLastlyCourseListByOneLabelAndTowLabelAndFormId(this.formTypeId, this.oneLabelId, this.twoLabelId);
            } else if (this.status == '1') {
                this.getFireCourseListByOneLabelAndTowLabelAndFormId(this.formTypeId, this.oneLabelId, this.twoLabelId);
            } else if (this.status == '2') {
                this.getAscPriceCourseListByOneLabelAndTowLabelAndFormId(this.formTypeId, this.oneLabelId, this.twoLabelId);
            } else if (this.status == '3') {
                this.getDescPriceCourseListByOneLabelAndTowLabelAndFormId(this.formTypeId, this.oneLabelId, this.twoLabelId);
            } else if (this.status == '4') {
                this.getCourseListByTwoLabelId(this.formTypeId, this.twoLabelId);
            } else if (this.status == '5') {
                this.queryCourseByCourseTitle($event);
            }
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            if (this.status == '0') {
                this.getLastlyCourseListByOneLabelAndTowLabelAndFormId(this.formTypeId, this.oneLabelId, this.twoLabelId);
            } else if (this.status == '1') {
                this.getFireCourseListByOneLabelAndTowLabelAndFormId(this.formTypeId, this.oneLabelId, this.twoLabelId);
            } else if (this.status == '2') {
                this.getAscPriceCourseListByOneLabelAndTowLabelAndFormId(this.formTypeId, this.oneLabelId, this.twoLabelId);
            } else if (this.status == '3') {
                this.getDescPriceCourseListByOneLabelAndTowLabelAndFormId(this.formTypeId, this.oneLabelId, this.twoLabelId);
            } else if (this.status == '4') {
                this.getCourseListByTwoLabelId(this.formTypeId, this.twoLabelId);
            } else if (this.status == '5') {
                this.queryCourseByCourseTitle($event);
            }
        },
        // 通过形式id, 一级标签id, 二级标签id查询课程列表
        getCourseListByTwoLabelId(formTypeId, twoLabelId) {
            const vue = this;
            vue.currentPage = 1;
            vue.pageSize = 10;
            vue.formTypeId = formTypeId;
            vue.twoLabelId = twoLabelId;
            vue.status = 4;
            
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
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.courseList = response.data;
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

.background {
background-image: linear-gradient(to top, #d5d4d0 0%, #d5d4d0 1%, #eeeeec 31%, #efeeec 75%, #e9e9e7 100%);
}
.divide {
    width: 100%;
    height: 10px;
    background-color: #DEE4EC;
}
.underLine {
    position: relative;
    width: 40px;
    top: 3px;
    right: 5px;
    height: 1px;
    background-color: black;
}
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