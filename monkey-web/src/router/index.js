import { createRouter, createWebHistory } from 'vue-router'
import MonkeyBloyViews from '@/views/monkeyblog/MonkeyBlogViews'

const routes = [
  {
    path: '/',
    name: 'home',
    component: MonkeyBloyViews
  },
  {
    path: "/monkeyblog/monkeyBlogViews",
    name: "blog-index",
    component: MonkeyBloyViews
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
