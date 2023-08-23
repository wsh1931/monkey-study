import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementUI from 'element-ui';
import plugins from './plugins';
import VueScrollTo from 'vue-scrollto'
import VueScrollactive from 'vue-scrollactive';


import 'mui-player/dist/mui-player.min.css'
import './assets/font/font-image/iconfont.css';
import 'element-ui/lib/theme-chalk/index.css';

Vue.config.productionTip = false
Vue.use(VueScrollTo)
Vue.use(ElementUI)
Vue.use(plugins)
Vue.use(VueScrollactive);


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

