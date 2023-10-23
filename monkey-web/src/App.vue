<template> 
  <div id="app"  style="background-color: #FAFAFA;">
      <NavBar style="z-index: 20000;"/>
      <router-view/>
    </div>
</template>

<script>
import $ from "jquery"
import NavBar from './components/NavBar.vue';

export default{
  name: "App",
  
  components: {
    NavBar,
  },
  data() {
    return {
      
    }
  },
  mounted() {
    const vue = this;
     // 全局错误处理函数
    $(document).ajaxError(function (event, jqXHR) {
      if (jqXHR.status != vue.ResultStatus.SUCCESS) {
        if (jqXHR.responseJSON != null) {
          vue.$modal.msgError(jqXHR.responseJSON.msg);
        }
      }
    });
  },
}
</script>

<style>
 /* @font-face {
    font-family: "方正手迹";
    src: url('/src/assets/font/方正手迹.ttf');
  } */

/* 除去router-link下划线 */ 
a{
  text-decoration: none;
}
.router-link-active {
  text-decoration: none;
}

  @font-face {
  font-family: 'iconfont';
  src: url('./assets/font/font-image/iconfont.woff2?t=1690201633199') format('woff2'),
  url('./assets/font/font-image/iconfont.woff?t=1690201633199') format('woff'),
  url('./assets/font/font-image/iconfont.ttf?t=1690201633199') format('truetype');
}

.iconfont {
  font-family: "iconfont" !important;
  font-size: 16px;
  font-style: normal;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.el-message {
  z-index: 99999999 !important;
}
.el-submenu.is-opened>.el-submenu__title .el-submenu__icon-arrow{
	display: none; 

}

.el-submenu>.el-submenu__title .el-submenu__icon-arrow{
	display: none;
}


/* ::-webkit-scrollbar {
    width: 10px;
    background-color: #fff;
}

:hover ::-webkit-scrollbar-track-piece {
    鼠标移动上去再显示滚动条
    background-color: #fff;
    滚动条的背景颜色
    border-radius: 6px;
    滚动条的圆角宽度
}

:hover::-webkit-scrollbar-thumb:hover {
    background-color: rgba(0, 0, 0, 0.1);
}

:hover::-webkit-scrollbar-thumb:vertical {
    background-color: rgba(0, 0, 0, 0.1);
    border-radius: 6px;
    outline: 2px solid #fff;
    outline-offset: -2px;
    border: 2px solid #fff;
} */


</style>
