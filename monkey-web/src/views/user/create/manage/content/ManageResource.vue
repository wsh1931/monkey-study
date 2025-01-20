<template>
    <div class="MonkeyWebManageResource-container">
        <el-form :inline="true" :model="formInline" class="demo-form-inline" label-width="auto">
            <el-form-item label="形式类型">
                <el-select v-model="formInline.formTypeId" placeholder="请选择形式类型" size="mini">
                <el-option 
                v-for="formType in formTypeList"  
                :label="formType.name" 
                :value="formType.id"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="资源类型">
                <el-select v-model="formInline.type" placeholder="请选择资源类型" size="mini">
                <el-option v-for="resourceType in typeList" :label="resourceType" :value="resourceType"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="审核状态">
                <el-select v-model="formInline.status" placeholder="请选择审核状态" size="mini">
                <el-option label="审核中" value="0"></el-option>
                <el-option label="已通过" value="1"></el-option>
                <el-option label="未通过" value="-1"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="发布时间">
                    <el-date-picker
                    size="mini"
                    v-model="formInline.dateList"
                    type="daterange"
                    range-separator="至"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期">
                    </el-date-picker>
            </el-form-item>
            <el-form-item label="所属分类">
                <el-cascader
                size="mini"
                    clearable
                    v-model="formInline.resourceClassification"
                    :options="resourceClassificationList">
                </el-cascader>
            </el-form-item>
            <el-form-item label="名称">
                <el-input size="mini" v-model="formInline.name" placeholder="请输入资源名称"></el-input>
            </el-form-item>
            <el-form-item>
                <el-button size="mini" type="primary" @click="submitQuery">查询</el-button>
            </el-form-item>
        </el-form>

        <div 
        v-loading="loading"
        @click="toResourceDetailViews(resource.id)"
        class="resource-card" 
        v-for="resource in resourceList" 
        :key="resource.id">
            <img class="resource-img" :src="resource.typeUrl" alt="">
            <div class="formType">{{ resource.formTypeName }}</div>
            <div class="resource-card-right">
                <div class="resource-title">{{ resource.name }}</div>
                <div class="resource-brief">{{ resource.description }}</div>
                <div class="resource-operator">
                    <span class="achievement-common">游览&nbsp;{{ resource.viewCount }}</span>
                    <span class="achievement-common">评论&nbsp;{{ resource.commentCount }}</span>
                    <span class="achievement-common">下载&nbsp;{{ resource.downCount }}</span>
                    <span class="achievement-common">点赞&nbsp;{{ resource.likeCount }}</span>
                    <span class="achievement-common">收藏&nbsp;{{ resource.collectCount }}</span>
                    <span class="achievement-common">购买&nbsp;{{ resource.buyCount }}</span>
                    <span class="achievement-common">评分&nbsp;{{ resource.score }}</span>

                    <div class="resource-more-operator">
                        <span @click.stop="checkData(resource)" class="achievement-common more-class">查看数据</span>
                        <span @click.stop="toEditResourceViews(resource.id)" class="achievement-common more-class">编辑</span>
                        <span @click.stop="deleteResource(resource)" class="achievement-common more-class">删除</span>
                    </div>
                </div>
            </div>
        </div>

        <div
        v-if="resourceList == null || resourceList == '' || resourceList == [] || resourceList.length <= 0"
        style="text-align: center;" >
            <el-empty description="暂无数据"></el-empty>
        </div>

        <el-drawer
        :title="resourceDetail.name + ' | ' + resourceDetail.createTime"
        size="800px"
        :visible.sync="drawer"
        direction="rtl">
        <div class="draw-card">
            <div class="data-title">发布至今总数据</div>
            <div class="data-card">
                <div class="data-card-title">阅读数</div>
                <div class="data-card-count">{{ resourceDetail.viewCount }}</div>
            </div>
            <div class="data-card">
                <div class="data-card-title">评论数</div>
                <div class="data-card-count">{{ resourceDetail.commentCount }}</div>
            </div>
            <div class="data-card">
                <div class="data-card-title">点赞数</div>
                <div class="data-card-count">{{ resourceDetail.likeCount }}</div>
            </div>
            <div class="data-card">
                <div class="data-card-title">收藏数</div>
                <div class="data-card-count">{{ resourceDetail.collectCount }}</div>
            </div>
            <div class="data-card">
                <div class="data-card-title">下载数</div>
                <div class="data-card-count">{{ resourceDetail.downCount }}</div>
            </div>
            <div class="data-card">
                <div class="data-card-title">购买数</div>
                <div class="data-card-count">{{ resourceDetail.buyCount }}</div>
            </div>
        </div>

        <div id="resource-data"></div>
        </el-drawer>

        <PagiNation
        style="text-align: center;"
            :totals="totals"
            :currentPage="currentPage" 
            :pageSize="pageSize" 
            @handleCurrentChange = "handleCurrentChange"
            @handleSizeChange="handleSizeChange"/>
    </div>
</template>

<script>
import $ from 'jquery'
import store from '@/store';
import { getTimeFormat } from '@/assets/js/DateMethod';
import PagiNation from '@/components/pagination/PagiNation.vue';
export default {
    name: 'MonkeyWebManageResource',
    components: {
        PagiNation
    },
    data() {
        return {
            loading: true,
            // 资源近 7 天数据
            resourceData: {},
            // 查看资源数据详情
            resourceDetail: {},
            isCondition: false,
            drawer: false,
            typeList: [],
            formTypeList: [],
            currentPage: 1,
            pageSize: 10,
            totals: 0,
            formInline: {
                type: "",
                formTypeId: "",
                status: "",
                isCuration: "",
                dateList: "",
                name: "",
                // 资源分类列表
                resourceClassification: [],
            },
            resourceClassificationList: [],
            resourceList: [],
            resourceSearchUrl: "http://localhost:80/monkey-resource/search",
            uploadResourceUrl: "http://localhost:80/monkey-resource/uploadResource",
            resourceContentManageUrl: "http://localhost:80/monkey-resource/content/manage",
            userHomeResourceUrl: "http://localhost:80/monkey-resource/user/home",
        };
    },

    created() {
        this.queryFormType();
        this.queryResourceType();
        this.queryCascaderList();
        this.queryResource();
    },

    mounted() {
        
        
    },

    methods: {
        // 初始化资源图标
        initResourceECharts() {
            var chartDom = document.getElementById('resource-data');
            var myChart = this.$ECharts.init(chartDom);
            let option = {
            title: {
                text: `${this.resourceDetail.name} 近7天数据统计`
            },
            tooltip: {
                trigger: 'axis'
            },
            legend: {
                bottom: 0,    
                data: ['点赞', '游览', '下载', '收藏', '评论', "购买"]
            },
            grid: {
                left: '3%',
                right: '4%',
                bottom: '10%',
                containLabel: true
            },
            toolbox: {
                feature: {
                saveAsImage: {}
                }
            },
            xAxis: {
                type: 'category',
                boundaryGap: false,
                data: this.resourceData.dateList,
            },
            yAxis: {
                type: 'value'
            },
            series: [
                {
                    name: '点赞',
                    type: 'line',
                    stack: 'Total',
                    data: this.resourceData.likeList
                },
                {
                    name: '游览',
                    type: 'line',
                    stack: 'Total',
                    data: this.resourceData.viewList
                },
                {
                    name: '下载',
                    type: 'line',
                    stack: 'Total',
                    data: this.resourceData.downList
                },
                {
                    name: '收藏',
                    type: 'line',
                    stack: 'Total',
                    data: this.resourceData.collectList
                },
                {
                    name: '评论',
                    type: 'line',
                    stack: 'Total',
                    data: this.resourceData.commentList
                },
                {
                    name: '购买',
                    type: 'line',
                    stack: 'Total',
                    data: this.resourceData.buyList
                }
            ]
            };

            myChart.setOption(option);

            window.addEventListener("resize", function () {
                myChart.resize();
            })
        },
        checkData(resource) {
            this.drawer = true;
            this.resourceDetail = JSON.parse(JSON.stringify(resource));
            this.$nextTick(() => {
                this.queryResourceDataRecentWeek(resource);
            })
        },
        // 查询资源近一周的数据
        queryResourceDataRecentWeek(resource) {
            const vue = this;
            $.ajax({
                url: vue.resourceContentManageUrl + '/queryResourceDataRecentWeek',
                type: "get",
                data: {
                    resourceId: resource.id,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.resourceData = response.data;
                        vue.initResourceECharts();
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询资源
        queryResource() {
            const vue = this;
            vue.loading = true;
            vue.formInline.currentPage = vue.currentPage;
            vue.formInline.pageSize = vue.pageSize;
            $.ajax({
                url: vue.resourceContentManageUrl + "/queryResource",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    conditionStr: JSON.stringify(vue.formInline)
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.resourceList = response.data.records;
                        vue.totals = response.data.total;
                        vue.loading = false;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 删除资源
        deleteResource(resource) {
            this.$modal.confirm(`"确定删除 ${resource.name} 资源?"`)
                .then(() => {
                    const vue = this;
                    $.ajax({
                        url: vue.userHomeResourceUrl + "/deleteResource",
                        type: "delete",
                        headers: {
                            Authorization: "Bearer " + store.state.user.token,
                        },
                        data: {
                            resourceId: resource.id,
                            userId: resource.userId
                        },
                        success(response) {
                            if (response.code == vue.ResultStatus.SUCCESS) {
                                if (vue.isCondition) {
                                    vue.queryResourceByCondition(this.userId);
                                } else {
                                    vue.queryResource();
                                }
                                
                                vue.$modal.msgSuccess(response.msg);
                            } else {
                                vue.$modal.msgError(response.msg);
                            }
                        }
                    })
                }).catch(() => {})
            
        },
        toResourceDetailViews(resourceId) {
            const { href } = this.$router.resolve({
                name: "resource_detail",
                params: {
                    resourceId
                }
            })

            window.open(href, "_blank")
        },
        // 前往编辑资源界面
        toEditResourceViews(resourceId) {
            this.$router.push({
                name: "resource_edit",
                params: {
                    resourceId
                }
            })
        },
        // 查询联级选择器列表
        queryCascaderList() {
            const vue = this;
            $.ajax({
                url: vue.uploadResourceUrl + "/queryCascaderList",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.resourceClassificationList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 通过条件查询资源列表
        queryResourceByCondition() {
            const vue = this;
            vue.loading = true;
            vue.formInline.currentPage = vue.currentPage;
            vue.formInline.pageSize = vue.pageSize;
            $.ajax({
                url: vue.resourceContentManageUrl + "/queryResourceByCondition",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    conditionStr: JSON.stringify(vue.formInline)
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.resourceList = response.data.records;
                        vue.totals = response.data.total;
                        vue.loading = false;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        getTimeFormat(val) {
            return getTimeFormat(val)
        },
        // 提交查询
        submitQuery() {
            this.isCondition = true;
            this.queryResourceByCondition();
        },
        // 得到资源类型集合
        queryResourceType() {
            const vue = this;
            $.ajax({
                url: vue.resourceContentManageUrl + "/queryResourceType",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.typeList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询形式类型列表
        queryFormType() {
            const vue = this;
            $.ajax({
                url: vue.resourceContentManageUrl + "/queryFormType",
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
        handleSizeChange(val) {
            this.pageSize = val;
            this.currentPage = 1;
            if (this.isCondition) {
                this.queryResourceByCondition();
            } else {
                this.queryResource();
            }
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            if (this.isCondition) {
                this.queryResourceByCondition();
            } else {
                this.queryResource();
            }
        },
    },
};
</script>

<style scoped>
#resource-data {
    width: 750px;
    height: 280px;
}
.data-card-count {
    margin-top: 10px;
    font-size: 18px;
    font-weight: bold;
}
.data-card-title {
    margin-top: 20px;
    font-size: 16px;
    color: gray;
}
.data-card:nth-child(3n + 1) {
    margin-right: 0;
}
.data-card {
    display: inline-block;
    background-color: #F5F6F7;
    width: 210px;
    height: 100px;
    text-align: center;
    margin-right: 30px;
    margin-bottom: 20px;
}
.draw-card {
    padding: 0 20px 0 20px;
}
.data-title {
    font-size: 16px;
    margin-bottom: 10px;

}
::-webkit-scrollbar {
    width: 10px;
    background-color: #fff;
}

:hover ::-webkit-scrollbar-track-piece {
    background-color: #fff;
    border-radius: 6px;
}

:hover::-webkit-scrollbar-thumb:hover {
    background-color: rgba(0, 0, 0, 0.1);
}

:hover::-webkit-scrollbar-thumb:vertical {
    background-color: rgba(0, 0, 0, 0.1);
    border-radius: 6px;
    outline: 2px solid #fff;
    outline-offset: -2px;
    border: 2px solid #fff;
}
.formType {
    background-image: linear-gradient(to right, #92fe9d 0%, #00c9ff 100%);
    font-size: 12px;
    text-align: center;
    color: white;
    padding: 2px 5px;
    position: absolute;
    left: 0;
    top: 0;
}
.resource-card:hover {
    box-shadow: 0 0 10px grey;
}
.resource-card {
    padding: 20px;
    margin-bottom: 20px;
    cursor: pointer;
    transition: 0.4s linear all;
    position: relative;
}
.more-class {
    cursor: pointer;
}
.more-class:hover {
    color: #409EFF;
}
.resource-more-operator {
    position: absolute;
    top: 0;
    right: 0;
}
.resource-operator {
    display: block;
    position: relative;
    width: 690px;
}
.achievement-common {
    font-size: 12px;
    margin-right: 20px;
}
.resource-brief {
    font-size: 14px;
    height: 35px;
    max-width: 690px;
    color: gray;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    overflow: hidden;
    margin-bottom: 10px;
}
.resource-title:hover {
    color: #409EFF;
}
.resource-title {
    vertical-align: top;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden; 
    margin-bottom: 10px;
    cursor: pointer;
    transition: 0.4s linear all;
    font-size: 14px;
    max-width: 690px;
}
.resource-card-right {
    display: inline-block;
    max-width: 780px;
}
.resource-img {
    width: 160px;
    height: 90px;
    margin-right: 20px;
    vertical-align: top;
}
.MonkeyWebManageResource-container {
    padding: 20px 20px 0 20px;
    background-color: #fff;
    height: calc(100vh - 171px);
    overflow-y: auto;
}
</style>