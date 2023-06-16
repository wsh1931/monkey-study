<template>
    <div class="InterlocutionView-container"
        style="display: flex; 
        justify-content: center;
        align-items: center; 
        width: 1150px; 
        padding-left: 150px;
        margin-top: 20px;"
        >
        <el-container>
        <el-main style="background-color: #FFFFFF;">
            <el-tabs v-model="tabName" @tab-click="handleClick">
                <el-tab-pane label="最新" name="first">最新</el-tab-pane>
                <el-tab-pane label="最热" name="second">最热</el-tab-pane>
                <el-tab-pane label="等你来答" name="third">等你来答</el-tab-pane>
                <el-tab-pane label="为你推荐" name="fourth">为你推荐</el-tab-pane>
            </el-tabs>
        </el-main>
        <el-aside width="300px" style="background-color: #FFFFFF; margin-left: 10px;">
            <el-row style="margin: 10px 10px;">
                <el-button type="success">提问</el-button>
            </el-row>
            <div style="margin: 10px 10px;">热门回答</div>
        </el-aside>
        </el-container>
    </div>
</template>

<script>
 export default {
    name: 'InterlocutionView',
    data() {
        return {
            tabName: "first",
        }
    },
 }
</script>

<style scoped>

</style>