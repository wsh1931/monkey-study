<template>
    <div class="common-layout">
      <el-container>
        <el-main>
          <el-tabs type="border-card" style="margin-top: 10px;">
            <el-tab-pane v-for="label in labelList" :key="label.id" :label="label.labelName">
              <el-card class="box-card">
              <template #header>
                <div class="card-header">
                  <span>Java后端测试</span>
                  <el-button class="button" text>Operation button</el-button>
                </div>
              </template>
              <div v-for="o in 4" :key="o" class="text item">{{ 'List item ' + o }}</div>
            </el-card>
            </el-tab-pane>
          </el-tabs>
        </el-main>
          <el-aside width="200px"></el-aside>
        </el-container>
    </div>
  </template>

<script>
import { ref } from "vue"
import $ from "jquery"

export default({
  setup() {
    let labelList = ref([]);

    $.ajax({
      url: "http://localhost:4000/blog/getLabelList",
      type: "get",
      success(response) {
        labelList.value = response.label_list;
        console.log(labelList.value)
      }
    })

    console.log(labelList)
    return {
      labelList
    }
  }
})
</script>

<style scope>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.text {
  font-size: 14px;
}

.item {
  margin-bottom: 18px;
}

.box-card {
  width: 100%;
  height: 100%;
}
</style>