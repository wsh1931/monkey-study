<template>
    <div class="QuestionView-container"
        style="display: flex; 
        justify-content: center;
        align-items: center; 
        width: 1150px; 
        padding-left: 150px;
        margin-top: 10px;"
        >
        <el-container>
        <el-main style="background-color: #FFFFFF;">
            <el-tabs v-model="tabName" @tab-click="handleClick">
                <el-tab-pane label="最新" style="padding: 5px;" name="first">
                    <QuestionCard/>
                </el-tab-pane>
                <el-tab-pane label="最热" name="second">最热</el-tab-pane>
                <el-tab-pane label="等你来答" name="third">等你来答</el-tab-pane>
                <el-tab-pane label="为你推荐" name="fourth">为你推荐</el-tab-pane>
            </el-tabs>
        </el-main>
        <el-aside width="300px" style="background-color: #FFFFFF; margin-left: 10px;">
            <el-row style="margin: 10px 10px;">
                <el-button icon="el-icon-question" type="success">提问</el-button>
            </el-row>
            <el-row>
            <div style="margin: 10px 10px;">
                <el-row>热门回答</el-row>
                <el-row style="margin-top: 10px;">
                    <el-col :span="2" style="background-color: #FEF0F0; color: #F9926C; width: 20px; height: 20px; text-align: center;">1</el-col>
                    <el-col style="margin-left: 2px;" :span="22">花果山博客如何集成QQ登录</el-col>

                </el-row>
            </div>
        </el-row>
        </el-aside>
        </el-container>
    </div>
</template>

<script>
import QuestionCard from '@/components/question/QuestionCard'
 export default {
    name: 'QuestionionView',
    components: {
        QuestionCard
    },
    data() {
        return {
            tabName: "first",
        }
    },
 }
</script>

<style scoped>

</style>