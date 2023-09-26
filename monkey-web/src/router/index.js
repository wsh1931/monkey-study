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
    meta: {
      title: "文章主页"
    }
  },
  {
    path: "/blog/BlogViews",
    name: "myblog",
    component: BlogViews,
    meta: {
      title: "文章主页"
    }
  },
  {
    path: "/article/CheckArticleViews/articleId=:articleId",
    name: "check_article",
    component: CheckArticleViews,
    meta: {
      title: "文章详情"
    }
  },
  {
    path: "/article/PublishArticleViews",
    name: "publish_article",
    component: PublishArticleViews,
    meta: {
      title: "发布文章"
    }
  },
  {
    path: "/chat/WebSocketChatViews/receiverId=:receiverId",
    name: "webSocket_chat",
    component: WebSocketChatViews,
    meta: {
      title: "用户聊天"
    }
  },
  {
    path: "/user/UserHomeViews/userId=:userId",
    name: "user_home",
    component: UserHomeViews,
    meta: {
      title: "用户主页"
    }
  },
  {
    path: "/question/QuestionViews",
    name: "question",
    component: QuestionViews,
    meta: {
      title: "问答主页"
    }
  },
  {
    path: "/question/PublishQuestionViews",
    name: "publish_question",
    component: PublishQuestionViews,
    meta: {
      title: "发布问答"
    }
  },
  {
    path: "/question/QuestionReplyViews/questionId=:questionId",
    name: "question_reply",
    component: QuestionReplyViews,
    meta: {
      title: "问答回复"
    }
  },
  {
    path: "/course/CourseCenterViews",
    name: "course_center",
    component: CourseCenterViews,
    meta: {
      title: "课程主页"
    }
  },
  {
    path: "/course/CourseDetailViews/courseId=:courseId",
    name: "course_detail",
    component: CourseDetailViews,
    meta: {
      title: "课程详情"
    }
  },
  {
    path: "/course/CourseVideoPlayViews/courseId=:courseId",
    name: "course_video_play",
    component: CourseVideoPlayViews,
    meta: {
      title: "课程播放"
    }
  },
  {
    path: "/user/VipViews",
    name: 'vip',
    component: VipViews,
    meta: {
      title: "开头VIP"
    }
  },
  {
    path: '/course/CoursePayViews/courseId=:courseId',
    name: 'course_pay',
    component: CoursePayViews,
    meta: {
      title: "课程播放"
    }
  },
  {
    path: "/course/CoursePayFinishViews/orderId=:orderId",
    name: "course_pay_finish",
    component: CoursePayFinishViews,
    meta: {
      title: "课程支付完成"
    }
  },
  {
    path: "/user/OrderCenterViews/userId=:userId",
    name: "order_center",
    component: CourseOrderViews,
    meta: {
      title: "订单中心"
    }
  },
  {
    path: "/user/CourseEvaluateViews/orderId=:orderId",
    name: "course_evaluate",
    component: CourseEvaluateViews,
    meta: {
      title: "课程评价"
    }
  },
  {
    path: "/community",
    name: "community",
    component: CommunityViews,
    meta: {
      title: "社区主页"
    }
  },
  {
    path: "/community/create",
    name: "community_create",
    component: CreateCommunityViews,
    meta: {
      title: "创建社区"
    }
  },
  {
    path: "/community/detail/communityId=:communityId",
    name: "community_detail",
    component: CommunityDetailViews,
    meta: {
      title: "社区详情"
    }
  },
  {
    path: "/community/publish/article/communityId=:communityId",
    name: "publish_community_article",
    component: PublishCommunityArticle,
    meta: {
      title: "发布社区文章"
    }
  },
  {
    path: "/community/article/communityId=:communityId/communityArticleId=:communityArticleId",
    name: "community_article",
    component: CommunityArticleViews,
    meta: {
      title: "社区文章详情"
    }
  },
  {
    path: "/community/rank",
    name: "community_rank",
    component: CommunityRankViews,
    meta: {
      title: "社区排行"
    }
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

router.beforeEach((to, from, next) => {
  document.title = to.meta.title // 更新页面标题
  next()
})
export default router
