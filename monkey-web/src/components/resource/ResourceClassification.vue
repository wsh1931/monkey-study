<template>
    <div class="MonkeyWebSearchCommunity-class">
        <el-row class="position">
            <el-card class="card">
                <div style="text-align: right;">
                <span  style="font-weight: 600; margin-right: 250px;">资源标签</span>
                <span @click="closeLabelWindow()"  class="el-icon-close" style="cursor: pointer;"></span>
            </div>
            <el-row style="height: 1px; background-color: #CDCDCD;"></el-row>
            <el-row style="margin-top: 10px;">
                <el-input @input="queryOneClassification(search)" v-model="search" placeholder="请搜索想要找的标签"></el-input>
            </el-row>
            <el-row>
                <el-col :span="6" class="overflow">
                    <el-row >
                    <div
                    @click="queryTwoClassificationListByOneLabelId(labelOne.id)"
                    v-for="(labelOne) in oneLabelList" 
                    :key="labelOne.id" 
                    :class="['hover', 'left-label',{selected:selectLabelId == labelOne.id}]" 
                    > {{ labelOne.name }}</div>
                    </el-row>  
                </el-col>
                <el-col :span="18" class="overflow">
                    <el-tag :class="['hover', {selected:labelTwo.selected}]" 
                    v-for="(labelTwo, index) in towLabelList" 
                    @click="selectTwoLabel(labelTwo, index)" 
                    :key="labelTwo.id" 
                    style="margin: 12px;">{{ labelTwo.name }}</el-tag>
                </el-col>
            </el-row>
            </el-card>
        </el-row>
    </div>
</template>

<script>
import $ from "jquery"
export default {
    name: 'MonkeyWebSearchCommunity',

    data() {
        return {
            resourceClassificationUrl: "http://localhost:80/monkey-resource/classification",
            // 标签文字搜索
            search: "",
            // 一级标签列表
            oneLabelList: [],
            // 二级标签列表
            towLabelList: [],
            // 被选中的标签id
            selectLabelId: "",
        };
    },

    created() {
        this.queryOneLevelClassificationList();
    },

    methods: {
        // 通过搜索字段得到一级标签
        queryOneClassification(search) {
            const vue = this;
            $.ajax({
                url: vue.resourceClassificationUrl + '/queryOneClassification',
                type: "get",
                data: {
                    search
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.oneLabelList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg)
                    }
                }
            })
        },
        // 关闭标签窗口
        closeLabelWindow() {
            this.$emit("closeLabelWindow");
        },
        // 选择二级标签
        selectTwoLabel(twoLabel, index) {
            if (twoLabel.selected) {
                twoLabel.selected = false;
                this.$emit("removeTwoLabel", twoLabel);
            } else {
                twoLabel.selected = true;
                this.$emit("selectTwoLabel", twoLabel);
            }
        },
        // 通过一级分类id得到二级分类列表
        queryTwoClassificationListByOneLabelId(classificationOneId) {
            const vue = this;
            $.ajax({
                url: vue.resourceClassificationUrl + "/queryTwoClassificationList/by/classificationOneId",
                type: "get",
                data: {
                    classificationOneId
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.selectLabelId = classificationOneId
                        vue.towLabelList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
        },
        // 得到一级标签
        queryOneLevelClassificationList() {
            const vue = this;
            $.ajax({
                url: vue.resourceClassificationUrl + "/queryOneLevelClassificationList",
                type: "get",
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.oneLabelList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
        }
    },
};
</script>

<style scoped>
.left-label {
    padding: 5px; 
    margin-top: 10px;
    width: 100%;
    height: 100%;
}
.MonkeyWebSearchCommunity-class {
    z-index: 20000;
    position: fixed;
    height: 100%; 
    width: 100%; 
    background-color: rgba(0, 0, 0, 0.5);
    top: 0;
    left: 0;
}
@keyframes slide-up {
    0% {
        opacity: 0;
    }
    100% {
        opacity: 1;
    }
}
.position {
    position: absolute;
    width: 640px;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    padding: 20px;
    max-height: 600px;
    overflow-y: auto;
    animation: slide-up 0.4s linear;
}
.selected {
    background-color: #E8E8E8;
}
.overflow {
    height: 400px;
    overflow-y: auto;
    overflow-x: hidden;
}

.hover:hover {
    cursor: pointer;
    background-color: #E8E8E8;
}

</style>