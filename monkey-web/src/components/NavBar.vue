<template>
  <div class="navbar-container parent-wrapper" style="display: flex; justify-content: center; align-items: center;">
  <el-menu :default-active="$route.path"
   class="el-menu-demo last-wrapper"
   style="display: flex; justify-content: center; align-items: center; width: 100%;"
     mode="horizontal"
     router
     active-text-color="#056DE8">
    <el-menu-item index="/"><div class="title-style">花果山</div> </el-menu-item>
    <el-menu-item index="/blog/BlogViews">博客</el-menu-item>
    <el-menu-item index="2">社区</el-menu-item>
    <el-menu-item index="/course/CourseCenterViews">课程 </el-menu-item>
    <el-menu-item index="/question/QuestionViews">问答</el-menu-item>
    <el-menu-item index="5">新鲜事</el-menu-item>
    
        <el-input  placeholder="请输入关键字进行搜索" class="input-style">
          <template slot="prepend">
            <i class="el-icon-search"></i>
        </template>
        </el-input>

    <el-menu-item index="6" style="margin-left: 100px;">地图 </el-menu-item>
    <el-submenu index="7" >
      <template slot="title">
        消息
      </template>
      <el-menu-item index="2-1">评论<el-badge :is-dot="commentTip" class="item"></el-badge></el-menu-item>
      <el-menu-item index="2-2">新增粉丝</el-menu-item>
      <el-menu-item index="2-3">赞和收藏</el-menu-item>
      <el-menu-item :index="`/chat/WebSocketChatViews/${$store.state.user.id}`">私信</el-menu-item>
    </el-submenu>
    

    <el-submenu  index="8">
      <template slot="title">
      <el-button class="csdn-btn publish-btn el-icon-circle-plus-outline" type="primary" >
        发布
      </el-button>
    </template>
      <el-menu-item index="/article/publishArticleViews">发布文章</el-menu-item>
      <el-menu-item index="/question/publishQuestionViews">发布问答</el-menu-item>
      <el-menu-item index="8-3">发布动态</el-menu-item>
    
    </el-submenu>
    <el-submenu index="9" v-if="$store.state.user.is_login">
      <template slot="title">
        <img width="45px" :src="$store.state.user.photo" alt="" style="border-radius: 50%; height: 45px;">
      </template>
      <el-menu-item :index="`/user/center/UserHomeViews/${$store.state.user.id}`">个人中心</el-menu-item>
      <el-menu-item @click="logout()" index="">退出</el-menu-item>
    </el-submenu>

      <el-menu-item  @click="login(true)" index=""  v-if="!$store.state.user.is_login" >
        登录
      </el-menu-item>

      <el-menu-item @click="register(true)" index="" v-if="!$store.state.user.is_login">
       注册
      </el-menu-item>
     
      
  </el-menu>
  <LoginViews 
      v-if="is_show_login" 
      class="child-wrapper"
      @login="login"

      @registerAndCloseLogin="registerAndCloseLogin"/>
  <div class="line"></div>
  <RegisterViews 
      @closeRegister="closeRegister"
      v-if="is_show_register"
      @register="register"
      @returnLogin="returnLogin"/>
</div>
</template>
  
  <script>
 import LoginViews from "@/components/user/LoginViews.vue"
 import RegisterViews from "@/components/user/RegisterViews.vue";
  import store from '@/store';

    export default {
     name: 'NavBar',     
     components: {
      LoginViews,
      RegisterViews
     },
      data() {
        return {
          activeIndex: '1',
          // 评论的提示
          commentTip: false,
          // 登录组件显示
          is_show_login: false,
          // 注册组件显示
          is_show_register: false
        };
      },
      methods: {
        closeRegister() {
          this.is_show_register = false;
        },
        returnLogin() {
          this.is_show_register = false;
          this.is_show_login = true;
        },
        // 进入注册界面退出登录界面
        registerAndCloseLogin() {
          this.is_show_login = false;
          this.is_show_register = true;
        },
        login(status) {
          this.is_show_login = status;
        },
        register(status) {
          this.is_show_register = status;
        },
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
  .last-wrapper {
    position: relative;
    z-index: 1;
  }
  .parent-wrapper {
  position: relative;
  z-index: 2;
}

.child-wrapper {
  position: relative;
  z-index: 10000;
}
  .item {
  margin-top: -10px;
  margin-left: 2px;
}
.input-style {
  width: 300px;
  border-radius: 50%;
}

.loading {
  background-color: red;
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