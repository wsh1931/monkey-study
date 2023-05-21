<template>

    <el-menu
    router
    style="height: 30px;"
      :default-active="this.$router.path"
      class="el-menu-demo"
      mode="horizontal"
      :ellipsis="false"
      text-color="black"
      active-text-color="black"
    >

    <el-menu-item index="/" style="margin-left: 100px;">
        <div class="monkey">花果山</div>
    </el-menu-item>
    
      <el-menu-item index="/monkeyblog/monkeyBlogViews">博客</el-menu-item>


      <div class="flex-grow" />
      <el-menu-item index="2" style="height: 5vh; margin-right: 30px;">
      <el-dropdown  trigger="click" >
        <span class="el-dropdown-link">
          吴思豪<el-icon ><ArrowDown /></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item >个人中心</el-dropdown-item>
            <el-dropdown-item >
              退出
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
      </el-menu-item>
    </el-menu>
  </template>
  
  <script >
  import { ref } from 'vue'
  
  export default ({
    setup() {
    const activeIndex = ref('1')
        
    return {
        activeIndex,
        }
  }
  })
  
  
  </script>
  
  <style scoped>
  .monkey {
    color: rgb(5, 109, 232);
    font-size: 20px;
    font-weight: 600;
  }

  .icon {
    height: 9vh;
  }
  .flex-grow {
    flex-grow: 1;
  }
  </style>