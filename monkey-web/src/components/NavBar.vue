<template>
  <div class="navbar-container parent-wrapper">
  <el-menu :default-active="$route.path"
  class="el-menu-demo last-wrapper title"
  mode="horizontal"
  router
  active-text-color="#056DE8">
    <el-menu-item class="menu-item"  index="/" ><div class="title-style">花果山</div> </el-menu-item>
    <el-menu-item index="/resource">资源</el-menu-item>
    <el-menu-item index="/community">社区</el-menu-item>
    <el-menu-item index="/course/CourseCenterViews">课程 </el-menu-item>
    <el-menu-item index="/question/QuestionViews">问答</el-menu-item>
    <el-menu-item index="/blog/BlogViews">文章</el-menu-item>
        <!-- <el-input  
        v-model="search"
        placeholder="按回车搜索全文内容" 
        class="input-style" 
        @keyup.native="searchInfo(search, $event)">
          <template v-slot:suffix>
            <i @click="toSearchAll(search)" class="el-icon-search"></i>
            </template>
        </el-input> -->
          <el-select
          @keyup.native="keyDownSearch($event, searchValue)"
          v-model="selectValue"
          filterable
          remote
          @change="selectSearch"
          size="medium"
          reserve-keyword
          placeholder="请输入关键词"
          :remote-method="searchTitleByEnglishOrChina"
          :loading="loading">
            <el-option
              v-for="item in searchTitleList"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
        </el-select>
        <el-button 
        @click="toSearchAll(searchValue)"
        size="medium" 
        class="el-icon-search search-button" 
        type="primary"></el-button>
    <el-submenu  style="margin-left: 100px;" index="/message"> 
      <template slot="title">
        <router-link to="/message"  class="el-icon-bell" style="font-size: 20px; position: relative;">
          <el-badge v-if="unCheckMessageTotal != '0'" :value="unCheckMessageTotal" class="item message-sign" :max="99">
        </el-badge>
        </router-link>
        
      </template>
      
        <el-menu-item :index="`/message/comment`">
          <span style="position: relative;">
            评论
            <el-badge v-if="unCheckCommentCount != '0'" :value="unCheckCommentCount" :max="99" class="message-content-sign">
            </el-badge>
          </span>
        </el-menu-item>
      <el-menu-item :index="`/message/like`">
        <span style="position: relative;">
          点赞
          <el-badge v-if="unCheckLikeCount != '0'" :value="unCheckLikeCount" :max="99" class="message-content-sign">
          </el-badge>
        </span>
      </el-menu-item>
      
      <el-menu-item :index="`/message/collect`">
        <span style="position: relative;">
          收藏
          <el-badge v-if="unCheckCollectCount != '0'" :value="unCheckCollectCount" :max="99" class="message-content-sign">
          </el-badge>
        </span>
      </el-menu-item>

      <el-menu-item :index="`/message/attention`">
        <span style="position: relative;">
        新增关注
        <el-badge v-if="unCheckAttentionCount != '0'" :value="unCheckAttentionCount" :max="99" class="message-content-sign">
          
        </el-badge>
        </span>
      </el-menu-item>

      <el-menu-item :index="`/message/system`">
        <span style="position: relative;">
        官方通知
        <!-- <el-badge :value="100" :max="99" class="message-content-sign">
          
        </el-badge> -->
        </span>
      </el-menu-item>
      
    </el-submenu>
    
    <el-menu-item :index="`/chat/WebSocketChatViews/receiverId=${$store.state.user.id}`">
      <span  class="iconfont icon-sixin" style="font-size: 22px;"></span>
    </el-menu-item>

    <el-submenu  index="8">
      <template slot="title">
      <el-button class="csdn-btn publish-btn el-icon-circle-plus-outline" type="primary" >
        发布
      </el-button>
    </template>
      <el-menu-item index="/article/publishArticleViews">发布文章</el-menu-item>
      <el-menu-item index="/question/publishQuestionViews">发布问答</el-menu-item>
      <el-menu-item index="/resource/uploadResource">上传资源</el-menu-item>
      <el-menu-item index="/community/create">创建社区</el-menu-item>
    
    </el-submenu>
    <el-submenu index="9" v-if="$store.state.user.is_login" style="width: 50px;">
      <template slot="title">
        <img width="45px" :src="$store.state.user.photo" alt="" style="border-radius: 50%; height: 45px;">
      </template>
      <el-menu-item :index="`/user/UserHomeViews/${$store.state.user.id}/resource`">用户主页</el-menu-item>
      <el-menu-item index="/user/center/profile">个人中心</el-menu-item>
      <el-menu-item index="/user/create/home">创作中心</el-menu-item>
      <el-menu-item :index="`/user/OrderCenterViews/userId=${$store.state.user.id}`">我的订单</el-menu-item>
      <el-menu-item :index="`/user/VipViews`">会员中心</el-menu-item>
      <el-menu-item @click="logout()" index="">退出</el-menu-item>
    </el-submenu>

      <el-menu-item  @click="login(true)" v-if="!$store.state.user.is_login" >
        登录
      </el-menu-item>

      <el-menu-item @click="register(true)" v-if="!$store.state.user.is_login">
      注册
      </el-menu-item>
      
  </el-menu>
  <LoginViews 

      v-if="is_show_login" 
      class="child-wrapper show-out"
      @login="login"

      @registerAndCloseLogin="registerAndCloseLogin"/>
  <div class="line"></div>
  <RegisterViews 
  class="show-out child-wrapper"
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
import $ from 'jquery'
  export default {
    name: 'NavBar',     
    components: {
      LoginViews,
      RegisterViews
    },
      data() {
        return {
          // 搜索内容
          search: "",
          activeIndex: '1',
          // 评论的提示
          commentTip: false,
          // 登录组件显示
          is_show_login: false,
          // 注册组件显示
          is_show_register: false,
          // 未查看评论数
          unCheckCommentCount: 0,
          // 未查看点赞数
          unCheckLikeCount: 0,
          // 未查看收藏数
          unCheckCollectCount: 0,
          // 未查看关注消息数
          unCheckAttentionCount: 0,
          // 未查看消息总数
          unCheckMessageTotal: 0,
          searchTitleList: [],
          // 下拉框选中的值
          selectValue: "",
          // 下拉框输入的值
          searchValue: '',
          loading: false,
          messageCenterUrl: "http://localhost/monkey-user/message/center",
          searchCenterUrl: "http://localhost:80/monkey-search/search/center",
        };
    },
    created() {
      this.queryNoCheckAttentionCount();
      this.queryNoCheckCollectCount();
      this.queryNoCheckCommentCount();
      this.queryNoCheckLikeCount();
    },
    methods: {
      selectSearch(keyword) {
        this.toSearchAll(keyword);
      },
      keyDownSearch(event, keyword) {
        if (event.keyCode == '13') {
          this.toSearchAll(keyword);
        }
      },
      // 通过拼音/中文搜索标题信息
      searchTitleByEnglishOrChina(keyword) {
        this.searchValue = keyword;
        if (keyword !== '') {
          this.loading = true;
          const vue = this;
          $.ajax({
            url: vue.searchCenterUrl + "/searchTitleByEnglishOrChina",
            type: "get",
            data: {
              keyword
            },
            success(response) {
              if (response.code == vue.ResultStatus.SUCCESS) {
                vue.searchTitleList = response.data;
                vue.loading = false;
              } else {
                vue.$modal.msgError(response.msg);
              }
            }
          })
        } else {
          this.searchTitleList = [];
        }
      },
      // 将搜索信息插入历史搜索
      insertHistorySearch(keyword) {
        const vue = this;
        $.ajax({
          url: vue.searchCenterUrl + "/insertHistorySearch",
          type: "post",
          data: {
            keyword,
          },
          headers: {
            Authorization: "Bearer " + store.state.user.token,
          },
          success(response) {
            if (response.code != vue.ResultStatus.SUCCESS) {
              vue.$modal.msgError(response.msg);
            }
          }
        })
      },
      // 前往搜索全部信息页面
      toSearchAll(search) {
        if (search == null || search == "") {
          this.$modal.msgWarning("请输入搜索内容")
            return;
        }
        this.selectValue = search
        this.insertHistorySearch(search);

        this.$router.push({
          name: "search_all",
          query: {
            keyword: search,
          },
        })
      },
      // 搜索信息
      searchInfo(search, event) {
        if (event.keyCode == '13') {
          this.toSearchAll(search);
        }
      },
      // 查询未查看消息关注数
      queryNoCheckAttentionCount() {
          const vue = this;
          $.ajax({
              url: vue.messageCenterUrl + "/queryNoCheckAttentionCount",
              type: "get",
              headers: {
                  Authorization: "Bearer " + store.state.user.token,
              },
              success(response) {
                  if (response.code == '200') {
                      vue.unCheckAttentionCount = response.data;
                      vue.unCheckMessageTotal += vue.unCheckAttentionCount;
                  } else {
                      vue.$modal.msgError(response.msg);
                  }
              }
          })
      },
      // 查询未查看消息收藏数
      queryNoCheckCollectCount() {
          const vue = this;
          $.ajax({
              url: vue.messageCenterUrl + "/queryNoCheckCollectCount",
              type: "get",
              headers: {
                  Authorization: "Bearer " + store.state.user.token,
              },
              success(response) {
                  if (response.code == '200') {
                      vue.unCheckCollectCount = response.data;
                      vue.unCheckMessageTotal += vue.unCheckCollectCount;
                  } else {
                      vue.$modal.msgError(response.msg);
                  }
              }
          })
      },
      // 查询未查看评论回复消息数
      queryNoCheckCommentCount() {
          const vue = this;
          $.ajax({
              url: vue.messageCenterUrl + "/queryNoCheckCommentCount",
              type: "get",
              headers: {
                  Authorization: "Bearer " + store.state.user.token,
              },
              success(response) {
                  if (response.code == '200') {
                      vue.unCheckCommentCount = response.data
                      vue.unCheckMessageTotal += vue.unCheckCommentCount;
                  } else {
                      vue.$modal.msgError(response.msg);
                  }
              }
          })
      },
      // 查询未查看消息点赞数
      queryNoCheckLikeCount() {
        const vue = this;
        $.ajax({
            url: vue.messageCenterUrl + "/queryNoCheckLikeCount",
            type: "get",
            headers: {
                Authorization: "Bearer " + store.state.user.token,
            },
            success(response) {
                if (response.code == '200') {
                    vue.unCheckLikeCount = response.data;
                    vue.unCheckMessageTotal += vue.unCheckLikeCount;
                } else {
                    vue.$modal.msgError(response.msg);
                }
            }
        })
    },
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
        const vue = this;
        store.dispatch("logout", {
          vue,
        });
      },
    }
  }
  </script>

  <style scoped>
  ::v-deep .el-input__inner {
    border-radius: 5px 0 0 5px;
  }
  .el-button--medium {
    border-radius: 0 5px 5px 0;
  }
  .icon-sixin {
    font-size: 30px;
    vertical-align: middle;
    position: relative;
  }
  .message-content-sign {
    position: absolute;
    padding-left: 10px;
  }
  .message-sign {
    position: absolute;
    left: 10px;
    top: -5px;
  }

  .title {
    display: flex; 
    justify-content: center; 
    align-items: center; 
    width: 100%;
  }
  .navbar-container {
    display: flex;
    justify-content: center;
    align-items: center;
  }
  .show-out {
    animation: show-out 0.2s linear;
  }
  @keyframes show-out {
    0% {
      transform: translateY(100px);
      opacity: 0;
    }
    100% {
      transform: translateY(0px);
      opacity: 1;
    }
  }
  .title-style {
    padding: 2px;
    font-size: 24px;
    font-weight: bold;
    font-style: italic;
  background-image: linear-gradient(to right, #00dbde 0%, #fc00ff 100%);
    color: transparent;
    background-clip: text;
    font-family: "方正手迹";
  }

  .last-wrapper {
    position: relative;
    z-index: 1;
  }
  .parent-wrapper {
  z-index: 2;
  background-color: rgba(0, 0, 0, 0.1);
}

.child-wrapper {
  position: relative;
  z-index: 100002;
}
  .item {
  margin-top: -10px;
  margin-left: 2px;
}
.input-style {
  width: 300px;
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
  .submenu-style{
    margin-left: 900px;
  }
</style>