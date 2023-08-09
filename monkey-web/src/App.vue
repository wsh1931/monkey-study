<template> 
  <div id="app"  style="background-color: #EEEEEE;">
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
      if (jqXHR.status != '200') {
        vue.$modal.msgError(jqXHR.responseJSON.msg);
      }
    });
  }
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
 #app {
 background: linear-gradient(to bottom, #D5DEE7 0%, #E8EBF2 50%, #E2E7ED 100%), linear-gradient(to bottom, rgba(0,0,0,0.02) 50%, rgba(255,255,255,0.02) 61%, rgba(0,0,0,0.02) 73%), linear-gradient(33deg, rgba(255,255,255,0.20) 0%, rgba(0,0,0,0.20) 100%); background-blend-mode: normal,color-burn;
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



</style>
