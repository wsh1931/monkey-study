import Vue from 'vue'
import $ from 'jquery';
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
import OrderPayFinishViews from '@/views/user/OrderPayFinishViews'
import CourseOrderViews from '@/views/user/OrderCenterViews'
import CourseEvaluateViews from '@/views/course/CourseEvaluateViews'
import CommunityViews from '@/views/community/CommunityViews'
import CreateCommunityViews from '@/views/community/CreateCommunityViews'
import CommunityDetailViews from '@/views/community/CommunityDetailViews'
import PublishCommunityArticle from '@/views/community/PublishCommunityArticle'
import CommunityArticleViews from '@/views/community/CommunityArticleViews'
import CommunityRankViews from '@/views/community/CommunityRankViews'
import NotAuthorizationViews from '@/views/error/NotAuthorizationViews'
import ManageViews from '@/views/community/manage/ManageViews'
import UserManage from '@/views/community/manage/user/UserManage'
import RoleManage from '@/views/community/manage/user/RoleManage'
import AddApplication from '@/views/community/manage/user/AddApplication'
import ContentManageViews from '@/views/community/manage/content/ContentManageViews'
import ChannelManage from '@/views/community/manage/function/ChannelManage'
import CommunityInfoManage from '@/views/community/manage/function/CommunityInfoManage'
import AdminConfig from '@/views/community/manage/administrator/AdminConfig'
import NotFoundViews from '@/views/error/NotFoundViews'
import UploadResource from '@/views/resource/UploadResource'
import ResourceViews from '@/views/resource/ResourceViews'
import ResourceDetail from '@/views/resource/ResourceDetail'
import ResourceSearch from '@/views/resource/ResourceSearch'
import ResourcePay from '@/views/resource/ResourcePay'
import ResourcePayFinish from '@/views/resource/ResourcePayFinish'
import MessageCenter from '@/views/user/notice/MessageCenter'
import UserAttention from '@/views/user/notice/attention/UserAttention'
import UserCollect from '@/views/user/notice/collect/UserCollect'
import UserComment from '@/views/user/notice/comment/UserComment'
import UserLike from '@/views/user/notice/like/UserLike'
import SystemMessage from '@/views/user/notice/system/SystemMessage'

Vue.use(VueRouter)

const routes = [
  {
    path: "/",
    name: "resource",
    component: ResourceViews,
    meta: {
      title: "资源主页",
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
      title: "开通VIP"
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
    path: "/order/orderPayFinish/:orderId",
    name: "order_pay_finish",
    component: OrderPayFinishViews,
    meta: {
      title: "订单支付完成"
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
      title: "社区文章详情",
      isExistArticle: true,
    },
  },
  {
    path: "/community/rank",
    name: "community_rank",
    component: CommunityRankViews,
    meta: {
      title: "社区排行"
    }
  },
  {
    path: "/community/manage/:communityId",
    name: "community_manage",
    component: ManageViews,
    meta: {
      title: "社区管理",
      // 授权
      isAuthorization: true,
    },
    children: [
    {
      path: "userManage",
      name: "manage_user",
      component: UserManage,
      meta: {
        title: "用户管理",
        isAuthorization: true,
      }
      },
      {
        path: "roleManage",
        name: "role_manage",
        component: RoleManage,
        meta: {
          title: "角色管理",
          isAuthorization: true,
        }
      },
      {
        path: "userApplication",
        name: "user_application",
        component: AddApplication,
        meta: {
          title: "加入申请",
          isAuthorization: true,
        }
      },
      {
        path: "contentManage",
        name: "content_manage",
        component: ContentManageViews,
        meta: {
          title: "内容管理",
          isAuthorization: true,
        }
      },
      {
        path: "channelManage",
        name: "channel_manage",
        component: ChannelManage,
        meta: {
          title: "频道管理",
          isAuthorization: true,
        }
      },
      {
        path: "informationManage",
        name: "information_manage",
        component: CommunityInfoManage,
        meta: {
          title: "信息管理",
          isAuthorization: true,
        }
      },
    {
      path: "administratorConfig",
      name: "administrator_config",
      component: AdminConfig,
      meta: {
        title: "管理员配置",
        isAuthorization: true,
      }
    }
  ]
  },
  {
    path: "/error/not/authorization",
    name: "not_authorization",
    component: NotAuthorizationViews,
    meta: {
      title: "未授权"
    }
  },
  {
    path: "/error/not/NotFound",
    name: "not_found",
    component: NotFoundViews,
    meta: {
      title: "页面不存在",
    }
  },
  {
    path: "/resource/uploadResource",
    name: "upload_resource",
    component: UploadResource,
    meta: {
      title: "上传资源"
    }
  },
  {
    path: "/resource",
    name: "resource",
    component: ResourceViews,
    meta: {
      title: "资源主页",
    }
  },
  {
    path: "/resource/detail/:resourceId",
    name: "resource_detail",
    component: ResourceDetail,
    meta: {
      title: "资源详情",
    },
  },
  {
    path: "/resource/search",
    name: "resource_search",
    component: ResourceSearch,
    meta: {
      title: "资源搜索"
    }
  },
  {
    path: "/resource/pay/:resourceId",
    name: "resource_pay",
    component: ResourcePay,
    meta: {
      title: "资源支付",
    },
  },
  {
    path: "/resource/payFinish/:resourceId",
    name: "resource_pay_finish",
    component: ResourcePayFinish,
    meta: {
      title: "资源支付成功"
    }
  },
  {
    path: "/message",
    name: "message_center",
    component: MessageCenter,
    meta: {
      title: "消息中心"
    },
    children: [
      {
        path: "comment",
        name: "message_comment",
        component: UserComment,
        meta: {
          title: "用户评论消息",
        }
      },
      {
        path: "like",
        name: "message_like",
        component: UserLike,
        meta: {
          title: "用户点赞消息",
        }
      },
      {
        path: "collect",
        name: "message_collect",
        component: UserCollect,
        meta: {
          title: "用户收藏消息",
        }
      },
      {
        path: "attention",
        name: "message_attention",
        component: UserAttention,
        meta: {
          title: "用户关注消息",
        }
      },
      {
        path: "system",
        name: "message_system",
        component: SystemMessage,
        meta: {
          title: "官方通知",
        }
      },
    ]
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
  // 判断该界面是否需要授权
  if (to.meta.isAuthorization) {
    const event = to.query.event;
    if (event == "to_community_manage") {
      // 判断用户是否能前往社区管理界面
      judgePower(to, next);
    }
  } else {
    const name = to.name;
    if (name == 'community_article') {
      // 判断社区文章是否存在
      judgeCommunityArticleIsExist(to, next);
    }
    next();
  }
})

const communityDetailCardUrl = "http://localhost:80/monkey-community/community/detail/card";
const communityContentManageUrl = "http://localhost:80/monkey-community/manage/contentManage"

// 判断社区文章是否存在
function judgeCommunityArticleIsExist(to, next) {
  const communityArticleId = to.params.communityArticleId;
  $.ajax({
    url: communityContentManageUrl + "/judgeCommunityArticleIsExist",
    type: "get",
    data: {
      communityArticleId
    },
    success(response) {
      if (response.code == '200') {
        next();
      } else {
        next({
          path: "/error/not/notFound",
        });
      }
    },
    error() {
      next({
          path: "/error/not/notFound",
        });
    }
  })
};

 // 判断用户是否能前往社区管理界面
function judgePower(to, next) {
  const token = localStorage.getItem("token");
  const communityId = to.params.communityId;
  $.ajax({
    url: communityDetailCardUrl + '/judgePower',
    type: "get",
    data: {
      communityId,
    },
    headers: {
      Authorization: "Bearer " + token,
    },
    success(response) {
      if (response.code == '200') {
        const data = response.data;
        if (data) {
          next();
        } else {
          // 说明没有有权限，前往403
        next({
          path: "/error/not/authorization",
        });
        }
      }
    },
    error() {
      next({
          path: "/error/not/authorization",
        });
    }
  })
}
export default router
