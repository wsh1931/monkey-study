<template>
    <div class="MonkeyWebSearchCommunity-class">
        <el-row class="position">
            <el-card class="card">
                <div style="text-align: right;">
                <span  style="font-weight: 600; margin-right: 250px;">社区标签</span>
                <span @click="closeLabelWindow()"  class="el-icon-close" style="cursor: pointer;"></span>
            </div>
            <el-row style="height: 1px; background-color: #CDCDCD;"></el-row>
            <el-row style="margin-top: 10px;">
                <el-input @input="queryOneLabel(search)" v-model="search" placeholder="请搜索想要找的标签"></el-input>
            </el-row>
            <el-row>
                <el-col :span="6" class="overflow">
                    <el-row >
                    <div
                    @click="getTwoLabelListByOneLabelId(labelOne.id)"
                    v-for="(labelOne) in oneLabelList" 
                    :key="labelOne.id" 
                    :class="['hover', 'left-label', {selected:selectLabelId == labelOne.id}]" > {{ labelOne.name }}</div>
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
            communityUrl: "http://localhost:80/monkey-community/community",
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
        this.getOneLevelLabelList();
    },

    methods: {
        // 通过搜索字段得到一级标签
        queryOneLabel(search) {
            const vue = this;
            $.ajax({
                url: vue.communityUrl + '/queryOneLabel',
                type: "get",
                data: {
                    search
                },
                success(response) {
                    if (response.code == '200') {
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
        // 通过一级标签id得到二级标签列表
        getTwoLabelListByOneLabelId(labelOneId) {
            const vue = this;
            $.ajax({
                url: vue.communityUrl + "/getTwoLabelListByOneLabelId",
                type: "get",
                data: {
                    labelOneId
                },
                success(response) {
                    if (response.code == '200') {
                        vue.selectLabelId = labelOneId
                        vue.towLabelList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
        },
        // 得到一级标签
        getOneLevelLabelList() {
            const vue = this;
            $.ajax({
                url: vue.communityUrl + "/getOneLevelLabelList",
                type: "get",
                success(response) {
                    if (response.code == '200') {
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
    overflow: auto;
    animation: slide-up 0.4s linear;
}
.selected {
    background-color: #E8E8E8;
}
.overflow {
    height: 400px;
    overflow: auto;
}

.hover:hover {
    cursor: pointer;
    background-color: #E8E8E8;
}

</style>