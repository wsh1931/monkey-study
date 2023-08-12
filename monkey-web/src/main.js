import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementUI from 'element-ui';
import plugins from './plugins';
import VueScrollTo from 'vue-scrollto'
import VueScrollactive from 'vue-scrollactive';
import { vueBaberrage } from 'vue-baberrage'
// vue-video-player
import VideoPlayer from 'vue-video-player'
require('video.js/dist/video-js.css')
require('vue-video-player/src/custom-theme.css')
import 'videojs-contrib-hls' //单是 RTMP 的话不需要第三方库，如果是 HLS 的话需要引入videojs-contrib-hls，看具体情况而定。

import 'mui-player/dist/mui-player.min.css'
import './assets/font/font-image/iconfont.css';
import 'element-ui/lib/theme-chalk/index.css';

Vue.config.productionTip = false
Vue.use(VueScrollTo)
Vue.use(ElementUI)
Vue.use(plugins)
Vue.use(VueScrollactive);
Vue.use(vueBaberrage)
Vue.use(VideoPlayer)


new Vue({
  router,
  store,
  created() {
    const token = localStorage.getItem("token");
    if (token != null && token != "") {
      store.state.user.token = token;
      store.dispatch("getUserInfoBytoken");
    }
  },
  render: h => h(App)
}).$mount('#app')

