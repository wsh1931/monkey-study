<template>
    <div class="MonkeyWebreportComment-container">
        <div class="position">
            <div class="header">
                <span class="title">举报反馈</span>
                <span @click="closeReportContent()" class="el-icon-close"></span>
            </div>
            <div class="report-type-header">
                <div class="report-type">举报类型(必选)</div>
                <div>
                    <span 
                    @click="queryTwoReportType(oneReport.id)"
                    v-for="oneReport in oneReportTypeList" 
                    :key="oneReport.id" 
                    type="info" 
                    :class="['report-type-label', {selectedReportType: oneReport.id == selectedOneReportId}]">{{ oneReport.name }}</span>
                </div>
            </div>
            <div 
            class="report-detail-type"
            v-if="twoReportTypeList != null && twoReportTypeList != '' && twoReportTypeList.length > 0">
                <div class="report-type">举报详细类型(选填)</div>
                <div>
                    <span
                    @click="selectedTwoReportType(twoReport.id)"
                    v-for="twoReport in twoReportTypeList" 
                    :key="twoReport.id" 
                    type="info" 
                    :class="['report-type-label', {'selectedReportType': twoReport.id == selectedTwoReportId}]">{{ twoReport.name }}</span>
                </div>
            </div>
            <div class="report-detail-header">
                <div class="report-detail-title">举报详情</div>
                <el-input
                v-model="reportDetail"
                type="textarea"
                :autosize="{ minRows: 4, maxRows: 20}"
                placeholder="请输入内容(不得少于 5 个字符)"
                maxlength="1000"
                show-word-limit
                ></el-input>
            </div>
            <div class="submit-button">
                <el-button 
                style="padding: 8px 24px;" 
                round
                @click="closeReportContent()">取消</el-button>
                <el-button 
                style="padding: 8px 24px;" 
                type="primary" 
                @click="submitButton()"
                round>提交</el-button>
            </div>
        </div>
    </div>
</template>

<script>
import $ from 'jquery'
import store from '@/store';
export default {
    name: 'MonkeyWebreportComment',
    props: ['reportCommentType', 'reportCommentAssociationId'],
    data() {
        return {
           
            // 一级举报id
            selectedOneReportId: "",
            selectedTwoReportId: "",
            reportDetail: "",
            oneReportTypeList: [],
            twoReportTypeList: [],
            reportUrl: "http://localhost:80/monkey-user/report",
        };
    },

    created() {
        this.queryOneReportType();
    },

    methods: {
        // 提交举报评论类型
        submitReportCommentType() {
            const vue = this;
            $.ajax({
                url: vue.reportUrl + "/submitReportCommentType",
                type: "post",
                data: {
                    oneReportTypeId: vue.selectedOneReportId,
                    twoReportTypeId: vue.selectedTwoReportId,
                    reportDetail: vue.reportDetail,
                    reportCommentType: vue.reportCommentType,
                    reportCommentAssociationId: vue.reportCommentAssociationId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.$emit("closeReportComment")
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 提交按钮
        submitButton() {
            if (this.selectedOneReportId == "") {
                this.$modal.msgWarning(this.exceptionMessage.selectReportType);
                return;
            }

            if (this.reportDetail.length < 5) {
                this.$modal.msgWarning(this.exceptionMessage.reportTypeMin);
                return;
            }

            if (this.reportDetail.length > 1000) {
                this.$modal.msgWarning(this.exceptionMessage.reportTypeMax);
                return;
            }

            this.submitReportCommentType();
        },
        // 选中二级举报类型
        selectedTwoReportType(twoReportTypeId) {
            this.selectedTwoReportId = twoReportTypeId;
        },
        // 查询二级举报类型集合
        queryTwoReportType(oneReportTypeId) {
            const vue = this;
            $.ajax({
                url: vue.reportUrl + "/queryTwoReportType",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    oneReportTypeId
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.selectedOneReportId = oneReportTypeId;
                        vue.twoReportTypeList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
        },
        // 查询一级举报类型集合
        queryOneReportType() {
            const vue = this;
            $.ajax({
                url: vue.reportUrl + "/queryOneReportType",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.oneReportTypeList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
        },
        closeReportContent() {
            this.$emit("closeReportComment")
        },
    },
};
</script>

<style scoped>
.report-detail-type {
    margin-bottom: 20px;
}
.selectedReportType {
    color: #409EFF;
}
.submit-button {
    text-align: right;
}
.report-detail-header {
    margin-bottom: 20px;
}
::-webkit-scrollbar {
    width: 10px;
    background-color: #fff;
}

:hover ::-webkit-scrollbar-track-piece {
    /* 鼠标移动上去再显示滚动条 */
    background-color: #fff;
    /* 滚动条的背景颜色 */
    border-radius: 6px;
    /* 滚动条的圆角宽度 */
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
.report-detail-title {
    margin-bottom: 10px;
    color: gray;
}
/* .report-type-label:nth-child(4n) {
    margin-right: 0;
} */
.report-type-label {
    display: inline-block;
    animation: slide-up 0.4s linear;
    text-align: center;
    margin-right: 10px;
    margin-bottom: 10px;
    font-size: 16px;
    cursor: pointer;
    padding: 5px 10px;
    background-color: rgba(0, 0, 0, 0.1);
}
.report-type {
    margin-bottom: 10px;
    color: gray;
}
.report-type-header {
    margin-bottom: 10px;
}
.el-icon-close {
    position: absolute;
    right: 0;
    cursor: pointer;
}
.el-icon-close:hover {
    color: #409EFF;
}
.header {
    position: relative;
    margin-bottom: 20px;
}
.title {
    font-weight: 600;
    font-size: 20px;
}
.position {
    position: absolute;
    width: 430px;
    background-color: #FFFFFF;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    padding: 20px;
    max-height: 700px;
    overflow: auto;
    animation: slide-up 0.4s linear;
}
@keyframes slide-up {
    0% {
        opacity: 0;
    }
    100% {
        opacity: 1;
    }
}
.MonkeyWebreportComment-container {
    z-index: 20000;
    position: fixed;
    height: 100%; 
    width: 100%; 
    background-color: rgba(0, 0, 0, 0.5);
    top: 0;
    left: 0;
}
</style>