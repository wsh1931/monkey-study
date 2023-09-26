import Vue from 'vue'
import VueRouter from 'vue-router'
import BlogViews from '@/views/blog/BlogViews'
import CheckArticleViews from '@/views/article/CheckArticleViews'
import PublishArticleViews from '@/views/article/PublishArticleViews'
import WebSocketChatViews from '@/views/chat/WebSocketChatViews'
import UserHomeViews from '@/views/user/UserHomeViews'
import QuestionViews from '@/views/question/QuestionViews'
import PublishQuestionViews from '@/views/question/PublishQuestionViews'
import QuestionReplyViews from '@/views/question/QuestionReplyViews'
import CourseCenterViews from '@/views/course/CourseCenterViews'
import CourseDetailViews from '@/views/course/CourseDetailViews'
import CourseVideoPlayViews from '@/views/course/CourseVideoPlayViews'
import VipViews from '@/views/user/VipViews'
import CoursePayViews from '@/views/course/CoursePayViews'
import CoursePayFinishViews from '@/views/course/CoursePayFinishViews'
import CourseOrderViews from '@/views/user/OrderCenterViews'
import CourseEvaluateViews from '@/views/course/CourseEvaluateViews'
import CommunityViews from '@/views/community/CommunityViews'
import CreateCommunityViews from '@/views/community/CreateCommunityViews'
import CommunityDetailViews from '@/views/community/CommunityDetailViews'
import PublishCommunityArticle from '@/views/community/PublishCommunityArticle'
import CommunityArticleViews from '@/views/community/CommunityArticleViews'
import CommunityRankViews from '@/views/community/CommunityRankViews'

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
    component: BlogViews,
  },
  {
    path: "/article/CheckArticleViews/articleId=:articleId",
    name: "check_article",
    component: CheckArticleViews,
  },
  {
    path: "/article/PublishArticleViews",
    name: "publish_article",
    component: PublishArticleViews
  },
  {
    path: "/chat/WebSocketChatViews/receiverId=:receiverId",
    name: "webSocket_chat",
    component: WebSocketChatViews
  },
  {
    path: "/user/UserHomeViews/userId=:userId",
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
    path: "/question/QuestionReplyViews/questionId=:questionId",
    name: "question_reply",
    component: QuestionReplyViews
  },
  {
    path: "/course/CourseCenterViews",
    name: "course_center",
    component: CourseCenterViews
  },
  {
    path: "/course/CourseDetailViews/courseId=:courseId",
    name: "course_detail",
    component: CourseDetailViews
  },
  {
    path: "/course/CourseVideoPlayViews/courseId=:courseId",
    name: "course_video_play",
    component: CourseVideoPlayViews,
  },
  {
    path: "/user/VipViews",
    name: 'vip',
    component: VipViews,
  },
  {
    path: '/course/CoursePayViews/courseId=:courseId',
    name: 'course_pay',
    component: CoursePayViews
  },
  {
    path: "/course/CoursePayFinishViews/orderId=:orderId",
    name: "course_pay_finish",
    component: CoursePayFinishViews
  },
  {
    path: "/user/OrderCenterViews/userId=:userId",
    name: "order_center",
    component: CourseOrderViews
  },
  {
    path: "/user/CourseEvaluateViews/orderId=:orderId",
    name: "course_evaluate",
    component: CourseEvaluateViews,
  },
  {
    path: "/community",
    name: "community",
    component: CommunityViews,
  },
  {
    path: "/community/create",
    name: "community_create",
    component: CreateCommunityViews
  },
  {
    path: "/community/detail/communityId=:communityId",
    name: "community_detail",
    component: CommunityDetailViews
  },
  {
    path: "/community/publish/article/communityId=:communityId",
    name: "publish_community_article",
    component: PublishCommunityArticle,
  },
  {
    path: "/community/article/communityId=:communityId/communityArticleId=:communityArticleId",
    name: "community_article",
    component:CommunityArticleViews,
  },
  {
    path: "/community/rank",
    name: "community_rank",
    component: CommunityRankViews
  }
  
]

const router = new VueRouter({
  mode: 'history',
  routes
})


const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => err)
}
export default router
