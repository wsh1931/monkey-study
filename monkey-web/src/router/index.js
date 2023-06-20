import Vue from 'vue'
import VueRouter from 'vue-router'
import BlogViews from '@/views/blog/BlogViews'
import LoginViews from '@/views/user/LoginViews'
import RegisterViews from '@/views/user/RegisterViews'
import CheckArticleViews from '@/views/article/CheckArticleViews'
import PubilishArticleViews from '@/views/article/PubilishArticleViews'
import WebSocketChatViews from '@/views/chat/WebSocketChatViews'
import UserHomeViews from '@/views/user/center/UserHomeViews'
import QuestionViews from '@/views/question/QuestionViews'
import PublishQuestionViews from '@/views/question/PublishQuestionViews'
import QuestionReplyViews from '@/views/question/QuestionReplyViews'

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
    path: "/article/PublishArticleViews",
    name: "publish_article",
    component: PubilishArticleViews
  },
  {
    path: "/chat/WebSocketChatViews/:receiverId",
    name: "webSocket_chat",
    component: WebSocketChatViews
  },
  {
    path: "/user/center/UserHomeViews/:userId",
    name: "user_home",
    component: UserHomeViews,
  },
  {
    path: "/question/QuestionViews",
    name: "question",
    component: QuestionViews
  },
  {
    path: "/question/PublishQuestionViews",
    name: "publish_question",
    component: PublishQuestionViews
  },
  {
    path: "/question/QuestionReplyViews/:questionId",
    name: "question_reply",
    component: QuestionReplyViews
  }
]

const router = new VueRouter({
  mode: 'history',
  routes
})



export default router
