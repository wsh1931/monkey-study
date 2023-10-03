<template>
    <div class="MonkeyWebLabelSelect-container" >
        <el-row class="position">

        <el-card>
            <div style="text-align: right;">
                <span  style="font-weight: 600; margin-right: 250px;">标签</span>
                <span @click="closeLabelWindow()"  class="el-icon-close" style="cursor: pointer;"></span>
            </div>
            <el-row style="height: 1px; background-color: #CDCDCD;"></el-row>
            <el-row style="margin-top: 10px;">
                <el-input v-model="search" placeholder="请搜索想要找的标签" @input="likeSearchOneLabel(search)"></el-input>
            </el-row>
            <el-row>
                <el-col :span="6" class="overflow">
                    <el-row 
                    v-for="(labelOne) in oneLabelList" 
                    :key="labelOne.id" 
                    :class="['hover', {selected:selectLabelId == labelOne.id}]" 
                    style="padding: 5px; margin-top: 10px;">
                    <div style="width: 100%; height: 100%;" @click="getTwoLabelListByOneLabelId(labelOne.id)"> {{ labelOne.labelName }}</div>
                    </el-row>  
                </el-col>
                <el-col :span="18" class="overflow">
                    <el-tag :class="['hover', {selected:labelTwo.selected}]" 
                    v-for="(labelTwo, index) in towLabelList" 
                    @click="selectTwoLabel(labelTwo, index)" 
                    :key="labelTwo.id" 
                    style="margin: 5px;">{{ labelTwo.labelName }}</el-tag>
                </el-col>
            </el-row>
        </el-card>
        </el-row>
    </div>
</template>

<script>
import $ from "jquery"
export default {
    name: 'MonkeyWebLabelSelect',

    data() {
        return {
            publishUrl: "http://localhost:80/monkey-article/publish",
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
        // 通过标签名模糊查找一级标签
        likeSearchOneLabel(name) {
            const vue = this;
            $.ajax({
                url: vue.publishUrl + "/likeSearchOneLabel",
                type: "get",
                data: {
                    name,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.oneLabelList = response.data;
                    } else {
                        vue.$modal.msgError(response);
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
            this.towLabelList[index].selected = true;
            twoLabel.selected = true;
            
            this.$emit("selectTwoLabel", twoLabel);
        },
        // 通过一级标签id得到二级标签列表
        getTwoLabelListByOneLabelId(labelOneId) {
            const vue = this;
            $.ajax({
                url: vue.publishUrl + "/getTwoLabelListByOneLabelId",
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
                url: vue.publishUrl + "/getOneLevelLabelList",
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
.MonkeyWebLabelSelect-container {
    z-index: 20000;
    position: fixed;
    height: 100%; 
    width: 100%; 
    background-color: rgba(0, 0, 0, 0.5);
    top: 0;
    left: 0;
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
@keyframes slide-up {
    0% {
        opacity: 0;
    }
    100% {
        opacity: 1;
    }
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