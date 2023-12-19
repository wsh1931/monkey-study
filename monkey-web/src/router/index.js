import Vue from 'vue'
import $ from 'jquery';
import VueRouter from 'vue-router'
import BlogViews from '@/views/blog/BlogViews'
import ArticleEdit from '@/views/article/ArticleEdit'
import CheckArticleViews from '@/views/article/CheckArticleViews'
import PublishArticleViews from '@/views/article/PublishArticleViews'
import WebSocketChatViews from '@/views/chat/WebSocketChatViews'
import QuestionViews from '@/views/question/QuestionViews'
import PublishQuestionViews from '@/views/question/PublishQuestionViews'
import QuestionReplyViews from '@/views/question/QuestionReplyViews'
import QuestionEdit from '@/views/question/QuestionEdit'
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
import CommunityArticleEdit from '@/views/community/CommunityArticleEdit'
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
import ResourceEdit from '@/views/resource/ResourceEdit'
import MessageCenter from '@/views/user/notice/MessageCenter'
import UserAttention from '@/views/user/notice/attention/UserAttention'
import UserCollect from '@/views/user/notice/collect/UserCollect'
import UserComment from '@/views/user/notice/comment/UserComment'
import UserLike from '@/views/user/notice/like/UserLike'
import SystemMessage from '@/views/user/notice/system/SystemMessage'
import SearchCenter from '@/views/search/SearchCenter'
import SearchAll from '@/views/search/SearchAll'
import SearchArticle from '@/views/search/SearchArticle'
import SearchQuestion from '@/views/search/SearchQuestion'
import SearchCourse from '@/views/search/SearchCourse'
import SearchCommunityArticle from '@/views/search/SearchCommunityArticle'
import SearchResource from '@/views/search/SearchResource'
import SearchCommunity from '@/views/search/SearchCommunity'
import SearchUser from '@/views/search/SearchUser'
import UserHomeViews from '@/views/user/home/UserHomeViews'
import UserHomeArticle from '@/views/user/home/UserHomeArticle'
import UserHomeCommunity from '@/views/user/home/UserHomeCommunity'
import UserHomeCommunityArticle from '@/views/user/home/UserHomeCommunityArticle'
import UserHomeConcern from '@/views/user/home/UserHomeConcern'
import UserHomeCourse from '@/views/user/home/UserHomeCourse'
import UserHomeFans from '@/views/user/home/UserHomeFans'
import UserHomeQuestion from '@/views/user/home/UserHomeQuestion'
import UserHomeResource from '@/views/user/home/UserHomeResource'
import UserHomeCollect from '@/views/user/home/UserHomeCollect'
import UserCenter from '@/views/user/center/UserCenter'
import UserProfile from '@/views/user/center/profile/UserProfile'
import UserAccount from '@/views/user/center/account/UserAccount'
import HistoryView from '@/views/user/center/history/HistoryView'
import UserCenterCollect from '@/views/user/center/collect/UserCenterCollect'
import AccountPassword from '@/views/user/center/account/password/AccountPassword'
import AccountEmail from '@/views/user/center/account/email/AccountEmail'
import AccountPhone from '@/views/user/center/account/phone/AccountPhone'
import EmailVerify from '@/views/user/center/account/email/EmailVerify'
import BindEmail from '@/views/user/center/account/email/BindEmail'
import BindSuccess from '@/views/user/center/account/email/BindSuccess'
import CollectContent from '@/views/user/center/collect/CollectContent'
import UserCenterAuth from '@/views/user/center/auth/UserCenterAuth'
import UserCenterRecord from '@/views/user/center/record/UserCenterRecord'
import ContentHistory from '@/views/user/center/history/ContentHistory'
import CommentHistory from '@/views/user/center/history/CommentHistory'
import LikeHistory from '@/views/user/center/history/LikeHistory'
import UserCreateCenter from '@/views/user/create/UserCreateCenter'
import UserCreateHome from '@/views/user/create/home/UserCreateHome'
import ManageContent from '@/views/user/create/manage/content/ManageContent'
import ManageComment from '@/views/user/create/manage/ManageComment'
import ManageBarrage from '@/views/user/create/manage/ManageBarrage'

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
    path: "/article/edit/:articleId",
    name: "article_edit",
    component: ArticleEdit,
    meta: {
      title: "编辑文章页面"
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
    path: "/question/edit/:questionId",
    name: "question_edit",
    component: QuestionEdit,
    meta: {
      title: "问答编辑页面",
    },
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
    path: "/community/article/edit/:communityId/:communityArticleId",
    name: "community_article_edit",
    component: CommunityArticleEdit,
    meta: {
      title: "社区文章编辑页面",
    }
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
    path: "/resource/edit/:resourceId",
    name: "resource_edit",
    component: ResourceEdit,
    meta: {
      title: "编辑资源"
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
  },
  {
    path: "/search",
    name: "search_center",
    meta: {
      title: "搜索中心",
    },
    component: SearchCenter,
    children: [
      {
        path: "all",
        name: "search_all",
        component: SearchAll,
        meta: {
          title: "查询全站信息",
        },
      },
      {
        path: "article",
        name: "search_article",
        component: SearchArticle,
        meta: {
          title: "查询文章信息",
        },
      },
      {
        path: "question",
        name: "search_question",
        component: SearchQuestion,
        meta: {
          title: "查询问答信息",
        },
      },
      {
        path: "course",
        name: "search_course",
        component: SearchCourse,
        meta: {
          title: "查询课程信息",
        },
      },
      {
        path: "community/article",
        name: "search_community_article",
        component: SearchCommunityArticle,
        meta: {
          title: "查询社区文章信息",
        },
      },
      {
        path: "resource",
        name: "search_resource",
        component: SearchResource,
        meta: {
          title: "查询资源信息",
        }
      },
      {
        path: "community",
        name: "search_community",
        component: SearchCommunity,
        meta: {
          title: "搜素社区信息"
        }
      },
      {
        path: "user",
        name: "search_user",
        component: SearchUser,
        meta: {
          title: "搜素用户信息"
        }
      }
    ]
  },
  {
    path: "/user/UserHomeViews/:userId",
    name: "user_home",
    component: UserHomeViews,
    meta: {
      title: "用户主页"
    },
    children: [
      {
        path: "resource",
        name: "user_home_resource",
        component: UserHomeResource,
        meta: {
          title: "用户资源"
        },
      },
      {
        path: "course",
        name: "user_home_course",
        component: UserHomeCourse,
        meta: {
          title: "用户课程"
        },
      },
      {
        path: "community",
        name: "user_home_community",
        component: UserHomeCommunity,
        meta: {
          title: "用户社区"
        },
      },
      {
        path: "communityArticle",
        name: "user_home_community_article",
        component: UserHomeCommunityArticle,
        meta: {
          title: "用户社区文章"
        },
      },
      {
        path: "question",
        name: "user_home_question",
        component: UserHomeQuestion,
        meta: {
          title: "用户问答"
        },
      },
      {
        path: "article",
        name: "user_home_article",
        component: UserHomeArticle,
        meta: {
          title: "用户博客"
        },
      },
      {
        path: "concern",
        name: "user_home_concern",
        component: UserHomeConcern,
        meta: {
          title: "用户关注"
        },
      },
      {
        path: "fans",
        name: "user_home_fans",
        component: UserHomeFans,
        meta: {
          title: "用户粉丝"
        },
      },
      {
        path: "collect",
        name: "user_home_collect",
        component: UserHomeCollect,
        meta: {
          title: "用户收藏"
        }
      }
    ]
  },
  {
    path: "/user/center",
    name: "user_center",
    component: UserCenter,
    meta: {
      title: "个人中心",
    },
    children: [
      {
        path: "history",
        name: "user_center_history",
        component: HistoryView,
        meta: {
          title: "个人中心-历史游览"
        },
        children: [
          {
            path: "content",
            name: "user_center_history_content",
            component: ContentHistory,
            meta: {
              title: "历史内容"
            },
          },
          {
            path: "comment",
            name: "user_center_history_comment",
            component: CommentHistory,
            meta: {
              title: "历史评论"
            },
          },
          {
            path: "like",
            name: "user_center_history_like",
            component: LikeHistory,
            meta: {
              title: "历史点赞"
            },
        },
        ]
      },
      {
        path: "auth",
        name: "user_center_auth",
        component: UserCenterAuth,
        meta: {
          title: "个人中心-实名认证"
        },
      },
      {
        path: "record",
        name: "user_center_record",
        component: UserCenterRecord,
        meta: {
          title: "个人中心-我的记录"
        },
      },
      {
        path: "account",
        name: "user_center_account",
        component: UserAccount,
        meta: {
          title: "个人中心-账号设置",
        },
      },
      {
            path: "password",
            name: "user_center_account_password",
            component: AccountPassword,
            meta: {
              title: "设置/修改密码"
            }
          },
          {
            path: "phone",
            name: "user_center_account_phone",
            component: AccountPhone,
            meta: {
              title: "绑定/解绑手机"
            }
          },
          {
            path: "email",
            name: "user_center_account_email",
            component: AccountEmail,
            meta: {
              title: "绑定/解绑邮箱"
            },
            children: [
              {
                path: "verify",
                name: "email_verify",
                component: EmailVerify,
                meta: {
                  title: "验证身份",
                }
              },
              {
                path: "bind",
                name: "email_bind",
                component: BindEmail,
                meta: {
                  title: "绑定邮箱"
                },
              },
              {
                path: "success",
                name: "email_success",
                component: BindSuccess,
                meta: {
                  title: "邮箱绑定成功"
                }
              }
            ]
          },
      {
        path: "collect",
        name: "user_center_collect",
        component: UserCenterCollect,
        meta: {
          title: "个人中心-用户收藏"
        },
        children: [
          {
            path: "detail/:collectContentId",
            name: "user_center_collect_content",
            component: CollectContent,
            meta: {
              title: "收藏内容"
            }
          }
        ]
      },
      {
        path: "profile",
        name: "user_center_profile",
        component: UserProfile,
        meta: {
          title: "个人中心-用户资料"
        },
      },
    ]
  },
  {
    path: "/user/create",
    name: "user_create_center",
    component: UserCreateCenter,
    meta: {
      title: "创作中心"
    },
    children: [
      {
        path: "home",
        name: "user_create_home",
        component: UserCreateHome,
        meta: {
          title: "创作中心-首页"
        }
      },
      {
        path: "manage/content",
        name: "user_create_manage_content",
        component: ManageContent,
        meta: {
          title: "内容管理-创作中心"
        }
      },
      {
        path: "manage/comment",
        name: "user_create_manage_comment",
        component: ManageComment,
        meta: {
          title: "评论管理-创作中心",
        }
      },
      {
        path: "manage/barrage",
        name: "user_create_manage_barrage",
        component: ManageBarrage,
        meta: {
          title: "弹幕管理-创作中心"
        }
      }
      
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
    if (name == 'user_home') {
      // 添加最近用户访问表
      addToRecentUserVisit(to.params.userId);
    } else if (name == "email_bind") {
      // 判断用户邮箱是否被认证通过
      judgeUserEmailIsVerify(next);
    } else if (name == "email_verify") {
      // 判断用户邮箱是否绑定
      judgeUserEmailIsBind(next);
    } else if (name == "check_article") {
      judgeArticleIsExist(to.params.articleId, next)
    } else if (name == "question_reply") {
      judgeQuestionIsExist(to.params.questionId, next)
    } else if (name == "course_detail") {
      judgeCourseIsExist(to.params.courseId, next)
    } else if (name == "community_detail") {
      judgeCommunityIsExist(to.params.communityId, next)
    } if (name == 'community_article') {
      // 判断社区文章是否存在
      judgeCommunityArticleIsExist(to.params.communityArticleId, next);
    } else if (name == "resource_detail") {
      judgeResourceIsExist(to.params.resourceId, next)
    }
    next();
  }
})

const token = localStorage.getItem("token");
const communityDetailCardUrl = "http://localhost:80/monkey-community/community/detail/card";
const communityContentManageUrl = "http://localhost:80/monkey-community/manage/contentManage";
const userHomeUrl = "http://localhost/monkey-user/user/home";
const userCenterAccountUrl = "http://localhost:80/monkey-user/center/account";
const checkArticleUrl = "http://localhost:80/monkey-article/check";
const questionReplyUrl = "http://localhost:80/monkey-question/reply";
const courseDetailUrl = "http://localhost:80/monkey-course/detail";
const communityDetailUrl = "http://localhost:80/monkey-community/community/detail";
const communityArticleUrl = "http://localhost:80/monkey-community/article";
const resourceDetailUrl = "http://localhost:80/monkey-resource/detail";
const questionUrl = "http://localhost:80/monkey-question/question";
const communityUrl = "http://localhost:80/monkey-community/community";
const resourceHomePageUrl = "http://localhost:80/monkey-resource/homePage";
// 判断文章是否存在
function judgeArticleIsExist(articleId, next) {
  $.ajax({
    url: checkArticleUrl + "/judgeArticleIsExist",
    type: "get",
    data: {
      articleId
    },
    success(response) {
      if (response.code == '200') {
        articleViewCountAddOne(articleId);
        next();
      } else {
        next({
          name: "not_found",
        });
      }
    },
    error() {
      next({
          name: "not_found",
        });
    }
  })
}

// 文章游览数 + 1
function articleViewCountAddOne(articleId) {
    $.ajax({
        url: checkArticleUrl + "/addArticleVisit",
        type: "post",
        data: {
            articleId,
        },
        headers: {
          Authorization: "Bearer " + token,
        }
    })
}

// 判断问答是否存在
function judgeQuestionIsExist(questionId, next) {
  $.ajax({
    url: questionReplyUrl + "/judgeQuestionIsExist",
    type: "get",
    data: {
      questionId
    },
    success(response) {
      if (response.code == '200') {
        questionViewCountAddOne(questionId);
        next();
      } else {
        next({
          name: "not_found",
        });
      }
    },
    error() {
      next({
          name: "not_found",
        });
    }
  })
}

// 问答游览数 + 1
function questionViewCountAddOne(questionId) {
    $.ajax({
        url: questionUrl + "/questionViewCountAddOne",
        type: "get",
        data: {
            questionId
        },
        headers: {
          Authorization: "Bearer " + token,
        }
    })
};

// 判断课程是否存在
function judgeCourseIsExist(courseId, next) {
  $.ajax({
    url: courseDetailUrl + "/judgeCourseIsExist",
    type: "get",
    data: {
      courseId
    },
    success(response) {
      if (response.code == '200') {
        courseViewCountAddOne(courseId);
        next();
      } else {
        next({
          name: "not_found",
        });
      }
    },
    error() {
      next({
          name: "not_found",
        });
    }
  })
}

// 课程游览数 + 1
function courseViewCountAddOne(courseId) {
    $.ajax({
        url: courseDetailUrl + "/courseViewAdd",
        type: "put",
        data: {
            courseId
        },
        headers: {
          Authorization: "Bearer " + token,
        }
    })
};

// 判断社区是否存在
function judgeCommunityIsExist(communityId, next) {
  $.ajax({
    url: communityDetailUrl + "/judgeCommunityIsExist",
    type: "get",
    data: {
      communityId
    },
    success(response) {
      if (response.code == '200') {
        next();
      } else {
        next({
          name: "not_found",
        });
      }
    },
    error() {
      next({
          name: "not_found",
        });
    }
  })
}

// 判断资源是否存在
function judgeResourceIsExist(resourceId, next) {
  $.ajax({
    url: resourceDetailUrl + "/judgeResourceIsExist",
    type: "get",
    data: {
      resourceId
    },
    success(response) {
      if (response.code == '200') {
        resourceViewCountAddOne(resourceId);
        next();
      } else {
        next({
          name: "not_found",
        });
      }
    },
    error() {
      next({
          name: "not_found",
        });
    }
  })
}

// 资源游览数 + 1 
function resourceViewCountAddOne(resourceId) {
    $.ajax({
        url: resourceHomePageUrl + "/resourceViewCountAddOne",
        type: "put",
        data: {
            resourceId
        },
        headers: {
          Authorization: "Bearer " + token,
        }
    })
};

// 判断社区文章是否存在
function judgeCommunityArticleIsExist(communityArticleId, next) {
  $.ajax({
    url: communityContentManageUrl + "/judgeCommunityArticleIsExist",
    type: "get",
    data: {
      communityArticleId
    },
    success(response) {
      if (response.code == '200') {
        communityArticleViewCountAddOne(communityArticleId);
        next();
      } else {
        next({
          name: "not_found",
        });
      }
    },
    error() {
      next({
          name: "not_found",
        });
    }
  })
};

// 社区文章游览数 + 1
function communityArticleViewCountAddOne(communityArticleId) {
    $.ajax({
        url: communityUrl + "/articleViewCount/addOne",
        type: "put",
        data: {
            communityArticleId,
        },
        headers: {
          Authorization: "Bearer " + token,
        }
    })
};


// 判断用户邮箱是否绑定
function judgeUserEmailIsBind(next) {
  $.ajax({
    url: userCenterAccountUrl + "/judgeUserEmailIsBind",
    type: "get",
    headers: {
      Authorization: "Bearer " + token,
    },
    success(response) {
      if (response.code == '200') {
        next();
      } else {
        next({
          name: "email_bind"
        })
      }
    }
  })
}

// 判断用户邮箱是否被认证通过
function judgeUserEmailIsVerify(next) {
  $.ajax({
    url: userCenterAccountUrl + "/judgeUserEmailIsVerify",
    type: "get",
    headers: {
      Authorization: "Bearer " + token,
    },
    success(response) {
      if (response.code == '200') {
        next();
      } else {
        next({
          name: "email_verify",
        })
      }
    }
  })
}

// 添加最近用户访问表
function addToRecentUserVisit(userId) {
  $.ajax({
    url: userHomeUrl + "/addToRecentUserVisit",
    type: "post",
    data: {
      userId,
    },
    headers: {
      Authorization: "Bearer " + token,
    },
  })
}

 // 判断用户是否能前往社区管理界面
function judgePower(to, next) {
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
