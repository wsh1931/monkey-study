<template>
    <div class="MonkeyWebLabelSelect-container" style="
    background-color: rgba(0, 0, 0, 0.5);
    position: fixed;
    width: 100%;
    height: 100%;
    margin-top: -625px;
    margin-left: -35px;
    ">
        <el-card style=" width: 600px; margin: 100px auto;">
            <div style="text-align: right;">
                <span  style="font-weight: 600; margin-right: 250px;">标签</span>
                <span @click="closeLabelWindow()"  class="el-icon-close" style="cursor: pointer;"></span>
            </div>
            <el-row style="height: 1px; background-color: #CDCDCD;"></el-row>
            <el-row style="margin-top: 10px;">
                <el-input v-model="search" placeholder="请搜索想要找的标签"></el-input>
            </el-row>
            <el-row>
                <el-col :span="6" class="overflow">
                      <el-row 
                      v-for="(labelOne) in oneLabelList" 
                      :key="labelOne.id" 
                      :class="['hover', {selected:selectLabelId == labelOne.id}]" 
                      style="padding-left: 5px;">
                       <div style="width: 100%; height: 100%;" @click="getTwoLabelListByOneLabelId(labelOne.id)"> {{ labelOne.labelName }}</div>
                       
                    </el-row>  
                </el-col>
                <el-col :span="18" class="overflow">
                    <el-tag :class="['hover', {selected:labelTwo.selected}]" 
                    v-for="(labelTwo, index) in towLabelList" 
                    @click="selectTwoLabel(labelTwo, index)" 
                    :key="labelTwo.id" 
                    style="margin: 2px;">{{ labelTwo.labelName }}</el-tag>
                </el-col>
            </el-row>
        </el-card>
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
                    if (response.code == '10000') {
                        vue.selectLabelId = labelOneId
                        vue.towLabelList = response.data;
                        console.log( vue.towLabelList)
                    } else {
                        vue.$modal.msgError("发送未知错误，查询二级标签失败");
                    }
                },
                error() {
                    vue.$modal.msgError("发送未知错误，查询二级标签失败");
                }
            })
        },
        // 得到一级标签
        getOneLevelLabelList() {
            const vue = this;
            $.ajax({
                url: vue.publishUrl + "/getOneLevelLabelList",
                type: "get",
                success(response) {
                    if (response.code == '10000') {
                        vue.oneLabelList = response.data;
                    } else {
                        vue.$modal.msgError("发生未知错误，查询一级标签失败");
                    }
                },
                error() {
                    vue.$modal.msgError("发生未知错误，查询一级标签失败");
                }
            })
        }
    },
};
</script>

<style scoped>

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