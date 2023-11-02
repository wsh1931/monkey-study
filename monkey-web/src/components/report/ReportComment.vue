<template>
    <div class="MonkeyWebreportComment-container">
        <div class="position">
            <div class="header">
                <span class="title">举报反馈</span>
                <span class="el-icon-close"></span>
            </div>
            <div class="report-type-header">
                <div class="report-type">举报类型(必选)</div>
                <div>
                    <el-tag v-for="i in 20" :key="i" type="info" class="report-type-label">政治问题</el-tag>
                </div>
            </div>
            <div class="report-detail-header">
                <div class="report-detail-title">举报详情</div>
                <el-input
                v-model="val"
                type="textarea"
                :autosize="{ minRows: 4, maxRows: 20}"
                placeholder="请输入内容"
                maxlength="1000"
                show-word-limit
                ></el-input>
            </div>
            <div class="submit-button">
                <el-button 
                style="padding: 8px 24px;" 
                round>取消</el-button>
                <el-button 
                style="padding: 8px 24px;" 
                type="primary" 
                round>提交</el-button>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: 'MonkeyWebreportComment',

    data() {
        return {
            val: "",
        };
    },

    mounted() {
        
    },

    methods: {
        
    },
};
</script>

<style scoped>
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
.report-type-label:nth-child(4n) {
    margin-right: 0;
}
.report-type-label {
    width: 100px;
    text-align: center;
    margin-right: 10px;
    margin-bottom: 10px;
    font-size: 16px;
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