import Vue from 'vue'
import VueRouter from 'vue-router'
import BlogViews from '@/views/blog/BlogViews'
import LoginViews from '@/views/user/LoginViews'
import RegisterViews from '@/views/user/RegisterViews'
import CheckArticleViews from '@/views/article/CheckArticleViews'
import PubilishArticleViews from '@/views/article/PubilishArticleViews'

Vue.use(VueRouter)

const routes = [
  {
    path: "/",
    name: "home",
    redirect: "/blog/BlogViews",
    component: BlogViews,
  },
  {
    path: "/blog/BlogViews",
    name: "myblog",
    component: BlogViews
  },
  {
    path: "/user/LoginViews",
    name: "login",
    component: LoginViews,
  },
  {
    path: "/user/RegisterViews",
    name: "register",
    component: RegisterViews,
  },
  {
    path: "/article/CheckArticleViews/:articleId",
    name: "check_article",
    component: CheckArticleViews,
  },
  {
    path: "/article/PublishArticleViews/:userId",
    name: "publish_article",
    component: PubilishArticleViews
  }
]

const router = new VueRouter({
  mode: 'history',
  routes
})

export default router
