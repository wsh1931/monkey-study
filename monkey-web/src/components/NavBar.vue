<template>
  <div class="navbar-container" style="display: flex; justify-content: center; align-items: center;">
  <el-menu :default-active="$route.path"
   class="el-menu-demo"
   style="display: flex; justify-content: center; align-items: center; width: 100%;"
     mode="horizontal"
     router
     active-text-color="#056DE8">
    <el-menu-item index="/"><div class="title-style">花果山</div> </el-menu-item>
    <el-menu-item index="/blog/BlogViews">博客</el-menu-item>
    <el-menu-item index="2">社区</el-menu-item>
    <el-menu-item index="3">学习资源 </el-menu-item>
    <el-menu-item index="4">问答</el-menu-item>
    
        <el-input  placeholder="请输入关键字进行搜索" class="input-style">
          <template slot="prepend">
            <i class="el-icon-search"></i>
        </template>
        </el-input>

    <el-menu-item index="6" style="margin-left: 100px;">历史游览 </el-menu-item>
    <el-submenu index="7" >
      <template slot="title">
        消息
      </template>
      <el-menu-item index="2-1">评论</el-menu-item>
      <el-menu-item index="2-2">新增粉丝</el-menu-item>
      <el-menu-item index="2-3">赞和收藏</el-menu-item>
      <el-menu-item index="2-3">私信</el-menu-item>
    </el-submenu>
    

    <el-submenu  index="8">
      <template slot="title">
      <el-button class="csdn-btn publish-btn el-icon-circle-plus-outline" type="primary" >
        发布
      </el-button>
    </template>
      <el-menu-item @click="publishArticle($store.state.user.id)" index="8-1">发布文章</el-menu-item>
      <el-menu-item index="8-2">发布问答</el-menu-item>
      <el-menu-item index="8-3">发布动态</el-menu-item>
    
    </el-submenu>
    <el-submenu index="9" v-if="$store.state.user.is_login">
      <template slot="title">
        <img width="45px" :src="$store.state.user.photo" alt="" style="border-radius: 50%;">
      </template>
      <el-menu-item :index="`/user/center/UserHomeViews/${$store.state.user.id}`">个人中心</el-menu-item>
      <el-menu-item @click="logout()" index="2-2">退出</el-menu-item>
    </el-submenu>

      <el-menu-item index="/user/LoginViews"  v-if="!$store.state.user.is_login" >
        登录
      </el-menu-item>
      <el-menu-item index="/user/RegisterViews" v-if="!$store.state.user.is_login">
       注册
      </el-menu-item>
      
  </el-menu>
  <div class="line"></div>
</div>
</template>
  
  <script>
  import store from '@/store';

    export default {
     name: 'NavBar',     
      data() {
        return {
          activeIndex: '1',
        };
      },
      methods: {
        // 跳转到发布文章页面
        publishArticle(userId) {
          this.$router.push({
            name: "publish_article",
            params: {
              userId
            }
          })
        },
        // 用户退出登录
        logout() {
          store.dispatch("logout");
          this.$modal.msgSuccess("退出登录成功");
        },
      }
    }
  </script>

  <style scoped>
.input-style {
  width: 300px;
  border-radius: 50%;
}
  .csdn-btn {
  display: inline-block;
  border-radius: 4px;
  font-size: 14px;
  line-height: 1;
  padding: 10px 16px;
  cursor: pointer;
  color: #ffffff;
  border: none;
  text-align: center;
  outline: none;
  border-radius: 45px;
}

.publish-btn {
  background-color: #409EFF;
}

.btn-text {
  display: inline-block;
  margin-right: 6px;
}

.btn-icon {
  display: inline-block;
  font-size: 12px;
  transform: translateY(1px);
}

.iconfont {
  font-family: "iconfont";
  font-style: normal;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  font-size: inherit;
  display: inline-block;
  text-decoration: none;
  text-align: center;
  line-height: inherit;
}
  .title-style {
    color: #056DE8;
    font-size: 24px;
    font-weight: 600;
    font-style: italic;
  }
  .submenu-style{
    margin-left: 900px;
  }
</style>