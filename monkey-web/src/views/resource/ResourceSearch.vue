<template>
    <div class="MonkeyWebResourceSearch-container">
        <div class="label-card">
            <el-row style="margin-bottom: 20px;">
                <el-col :span="1">
                    <span class="form-header">形式</span>
                </el-col>
                <el-col :span="23" class="form-label-header">
                    <span 
                    @click="selectedFormId = formType.id, queryResourceList()"
                    :class="['form-label', {selected: selectedFormId == formType.id}]" 
                    v-for="formType in formTypeList"
                    :key="formType.id">{{ formType.name }}</span>
                </el-col>
            </el-row>
            <el-row style="margin-bottom: 20px;">
                <el-col :span="1">
                    <span class="form-header">方向</span>
                </el-col>
                <el-col :span="23" class="form-label-header">
                    <span 
                    @click="queryClassificationByDirectId(direct)"
                    :class="['form-label', {selected: selectedDirectionId == direct.id}]"
                    v-for="direct in directList" 
                    :key="direct.id">{{ direct.name }}</span>
                </el-col>
            </el-row>
            <el-row 
            style="margin-bottom: 20px;"
            v-if="selectedDirectionId != -1">
                <el-col :span="1">
                    <span class="form-header">分类</span>
                </el-col>
                <el-col :span="23" class="form-label-header">
                    <span 
                    @click="selectedClassificationId = classification.id, queryResourceList()"
                    :class="['form-label', {selected: selectedClassificationId == classification.id}]" 
                    v-for="classification in classificationList" 
                    :key="classification.id">{{ classification.name }}</span>
                </el-col>
            </el-row>
            <el-row style="margin-bottom: 20px;">
                <el-col :span="1">
                    <span class="form-header">格式</span>
                </el-col>
                <el-col :span="23" class="form-label-header">
                    <span 
                    @click="selectedFormat = format, queryResourceList()"
                    :class="['form-label', {selected: selectedFormat == format}]" 
                    v-for="format in formatList" 
                    :key="format">{{ format }}</span>
                </el-col>
            </el-row>
        </div>
        <el-row class="content-card">
            <div @click="selectedType = '最新', queryResourceList()">
                <el-col :class="['click', 'font-size', 'top']" :span="1">
                最热
                <div :class="{ underLine: selectedType == '最热'}"></div>
                </el-col>
            </div>
            <div @click="selectedType = '最新', queryResourceList()">
                <el-col class="click font-size top" :span="1">最新
                <div :class="{ underLine: selectedType == '最新' }"></div>
                </el-col>
            </div>
                
            <div @click="selectedType = '价格', queryResourceList()">
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
                    placeholder="请输入想找的资源名, 按回车进行搜索"
                    suffix-icon="el-icon-search"
                    v-model="resourceName"
                    @keyup.native="queryResource($event)">
                </el-input>
            </el-col>
        </el-row>
        
        <div class="bottom-card">
            <CurationResourceCard
            :curationResourceList="resourceList"/>
            <ResourcePaginationVue
            style="text-align: right;"
            :totals="totals"
            :currentPage="currentPage" 
            :pageSize="pageSize" 
            @handleCurrentChange = "handleCurrentChange"
            @handleSizeChange="handleSizeChange"/>
        </div>
    </div>
</template>

<script>
import ResourcePaginationVue from '@/components/pagination/ResourcePagination.vue';
import $ from 'jquery'
import store from '@/store';
import CurationResourceCard from '@/components/resource/CurationResourceCard.vue';
export default {
    name: 'MonkeyWebResourceSearch',
    components: {
        CurationResourceCard,
        ResourcePaginationVue
    },
    data() {
        return {
            // 模糊查询资源名
            resourceName: "",
            // 查找类型, 0表示最热，1表示最新，2表示价格默认排序，3表示价格升序，4表示价格降序
            searchType: "0",
            // 1为升序，2为降序
            priceType: "0",
            // 选中的资源类型
            selectedType: "最热",
            // 选中的形式id
            selectedFormId: "-1",
            // 选中的方向id
            selectedDirectionId: "-1",
            directList: [],
            // 选中的分类id
            selectedClassificationId: "-1",
            classificationList: [],
            // 选中的格式
            selectedFormat: "全部",
            formatList: [],
            resourceList: [],
            currentPage: 1,
            pageSize: 70,
            totals: 0,
            formTypeList: [],
            resourceHomePageUrl: "http://localhost:80/monkey-resource/homePage",
            resourceSearchUrl: "http://localhost:80/monkey-resource/search",
        };
    },

    created() {
        this.queryFormTypeList();
        this.queryDirectList();
        this.queryFormatList();
        this.queryHottestResourceList();
    },

    methods: {
        // 按回车查询资源列表
        queryResource(event) {
            if (event.keyCode == '13') {
                this.queryResourceList();
            }
        },
        // 查询资源列表
        queryResourceList() {
            this.currentPage = 1;
            this.pageSize = 70;
            if (this.selectedType == '最热') {
                this.queryHottestResourceList();
            } else if (this.selectedType == '最新') {
                this.queryLatestResourceList();
            } else if (this.selectedType == '价格') {
                if (this.selectedFormId != '3') {
                    this.$modal.msgWarning("必需先选择收费形式");
                    this.selectedType = '最热';
                    this.queryResourceList();
                    return false;
                }
                this.priceType = (this.priceType + 1) % 3;
                if (this.priceType == '0') {
                    this.priceType = (this.priceType + 1) % 3;
                }
                if (this.priceType == '1') {
                    // 得到升序价格列表
                    this.queryAscPriceResourceList();
                } else if (this.priceType == '2'){
                    // 得到降序价格列表
                    this.queryDescPriceResourceList();
                }
            }
        },
        // 得到降序价格列表
        queryDescPriceResourceList() {
            const vue = this;
            $.ajax({
                url: vue.resourceSearchUrl + "/queryDescPriceResourceList",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    formTypeId: vue.selectedFormId,
                    directionId: vue.selectedDirectionId,
                    format: vue.selectedFormat,
                    classificationId: vue.selectedClassificationId,
                    resourceName: vue.resourceName,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.resourceList = response.data.records;
                        vue.totals = response.data.total;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 得到升序价格列表
        queryAscPriceResourceList() {
            const vue = this;
            $.ajax({
                url: vue.resourceSearchUrl + "/queryAscPriceResourceList",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    formTypeId: vue.selectedFormId,
                    directionId: vue.selectedDirectionId,
                    format: vue.selectedFormat,
                    classificationId: vue.selectedClassificationId,
                    resourceName: vue.resourceName,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.resourceList = response.data.records;
                        vue.totals = response.data.total;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查找最新资源列表
        queryLatestResourceList() {
            const vue = this;
            $.ajax({
                url: vue.resourceSearchUrl + "/queryLatestResourceList",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    formTypeId: vue.selectedFormId,
                    directionId: vue.selectedDirectionId,
                    format: vue.selectedFormat,
                    classificationId: vue.selectedClassificationId,
                    resourceName: vue.resourceName,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.resourceList = response.data.records;
                        vue.totals = response.data.total;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查找最热资源列表
        queryHottestResourceList() {
            const vue = this;
            $.ajax({
                url: vue.resourceSearchUrl + "/queryHottestResourceList",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    formTypeId: vue.selectedFormId,
                    directionId: vue.selectedDirectionId,
                    format: vue.selectedFormat,
                    classificationId: vue.selectedClassificationId,
                    resourceName: vue.resourceName,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.resourceList = response.data.records;
                        vue.totals = response.data.total;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询格式列表
        queryFormatList() {
            const vue = this;
            $.ajax({
                url: vue.resourceSearchUrl + "/queryFormatList",
                type: "get",
                success(response) {
                    if (response.code == '200') {
                        vue.formatList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
        },
        // 通过方向集合查询分类集合
        queryClassificationByDirectId(direct) {
            const vue = this;
            $.ajax({
                url: vue.resourceSearchUrl + "/queryClassificationByDirectId",
                type: "get",
                data: {
                    directId: direct.id,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.classificationList = response.data;
                        vue.selectedDirectionId = direct.id;
                        vue.selectedClassificationId = '-1'
                        vue.queryResourceList();
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
        },
        // 得到方向集合(包括全部)
        queryDirectList() {
            const vue = this;
            $.ajax({
                url: vue.resourceSearchUrl + "/queryDirectList",
                type: "get",
                success(response) {
                    if (response.code == '200') {
                        vue.directList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
        },
        // 得到形式类型列表
        queryFormTypeList() {
            const vue = this;
            $.ajax({
                url: vue.resourceSearchUrl + "/queryFormTypeList",
                type: "get",
                success(response) {
                    if (response.code == '200') {
                        vue.formTypeList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        handleSizeChange(val) {
            this.pageSize = val;
            if (this.selectedType == '最热') {
                this.queryHottestResourceList();
            } else if (this.selectedType == '最新') {
                this.queryLatestResourceList();
            } else if (this.selectedType == '价格') {
                if (this.priceType == '1') {
                    // 得到升序价格列表
                    this.queryAscPriceResourceList();
                } else if (this.priceType == '2'){
                    // 得到降序价格列表
                    this.queryDescPriceResourceList();
                }
            }
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            if (this.selectedType == '最热') {
                this.queryHottestResourceList();
            } else if (this.selectedType == '最新') {
                this.queryLatestResourceList();
            } else if (this.selectedType == '价格') {
                if (this.priceType == '1') {
                    // 得到升序价格列表
                    this.queryAscPriceResourceList();
                } else if (this.priceType == '2'){
                    // 得到降序价格列表
                    this.queryDescPriceResourceList();
                }
            }
        },
    },
};
</script>

<style scoped>
.bottom-card {
    animation: show-out 0.4s linear;
    background-color: #fff;
    padding-bottom: 20px;
}
.selected {
    font-size: 14px;
    background-color: rgba(0,242,254, 0.1);
    color: #00f2fe;
    transition: 0.2s linear all;
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
.content-card {
    margin-top: 10px;
    padding: 20px;
    background-color: #fff;
    animation: show-out 0.4s linear;
}
.form-label-header {
    display: flex;
    flex-wrap: wrap;
}
.form-label {
    margin-right: 10px;
    font-size: 14px;
    padding: 2px 5px;
}
.form-label:hover {
    color: #00f2fe;
    cursor: pointer;
}
.form-header {
    font-size: 14px;
    padding: 2px 5px;
    background-color: rgba(0, 0, 0, 0.1);
}
.label-card {
    margin-top: 10px;
    padding: 20px 20px 10px 20px;
    background-color: #fff;
    font-size: 16px;
    box-shadow: 0 0 5px 0 rgba(0, 0, 0, 0.1);
    animation: show-out 0.4s linear;
}
@keyframes show-out{
    0% {
        opacity: 0;
    }
    100% {
        opacity: 1;
    }
}
.MonkeyWebResourceSearch-container {
    margin: 0 auto;
    width: 1280px;
}
</style>