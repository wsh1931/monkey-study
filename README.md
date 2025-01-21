## 前言

此项目是个人大二开始从零开发的项目，目的是将所学的知识学会运用，并顺带完成毕业设计。

## 项目介绍

花果山交流平台( **MonkeyBlog** )， 类似于**CSDN**， 是一个**基于微服务架构的前后端分离学习交流系统**。包括**社区，课程，资源（电子书等文件），文章，问答，用户** 功能。**Web** 端使用 **Vue** + **ElementUi** , 后端使用 **SpringCloudAlibaba** + **SpringBoot** + **Mybatis-plus**进行开发。

### 后端所用技术

SpringBoot, SpringCloud, SpringCloudAlibaba, SpringSecurity, MyBatis-Plus, Swagger-UI, Elasticsearch, Kibana, RabbitMQ, Redis，WebSocket，Starter-Email，EasyExcel, Swagger，Renren-Generator, 阿里云OSS，阿里云视频点播，阿里云短信服务。

* 使用 **Gateway** 网关，**Nacos** 注册中心并完成远程文件配置管理，**OpenFeign** 完成用户之间的服务调用， **Sentinel** 做服务限流和熔断， **Zipkin** 链路追踪。

* 使用 **Jwt** + **SpringSecurity** + **Redis** +  **RDBC模型**完成登录验证和权限校验, 支持 **用户邮箱** 与 **手机号短信** 登录
* 使用 **WebSocket** 完成在线聊天与课程实时弹幕功能
* 使用 **ElasticSearch** 完成文章，课程，问答，社区，资源的全文检索功能，引入**kibana**实现可视化管理，支持中文/英文/拼音检索，关键词高亮功能。
* 引入 **支付宝沙箱付款** 完成购买课程与资源付费功能，并使用 **Rabbitmq** 死信队列完成当用户在一定时间内未支付更新订单状态，使用 **ReentrantLock** 防止用户重复提交订单。

* 使用 **阿里云 OSS** 完成图片与文件的存储,  **阿里云 Video** 完成课程的存储，**阿里云短信验证** 完成用户手机号登录。
* 使用 **SpringBoot** 定时任务 + **Redis** + **Rabbitmq** 实现排行榜定时更新功能

* 使用 **Renren-Generator** 一键生成实体类与CRUD接口，加快项目开发速度。

* 使用 **EasyExcel** 完成数据的批量导出，方便用户查看详细信息。

* 引入 **Swagger** 实现文档统一接口管理，方便开发中调试接口。

### 前端所用技术

Vue, Element, Vue-router, Vuex, Ajax, Echarts, Mui-Player, Mavon-Editor, Vue-Danmaku, Vue-Markdown, Vue-Scrollactive。

* 使用 **Vuex** 完成全局状态管理框架，以便及时更新用户状态，以及获得用户信息。
* 使用 **Vue-Router** 路由框架完成路由管理以及权限校验, 防止用户越权操作。
* 使用 **Element-ui** 完成页面开发，保证页面美观。
* 使用 **Ajax** 发送前端请求功能，对接前后端接口。
* 使用 **Echarts** 实现用户游览，收藏等信息可视化，方便用户查看数据。
* 使用 **Mui-Player** 视频播放器完成课程视频播放功能。
* 使用 **Vue-Danmaku** 完成课程视频弹幕功能。
* 引入 **Mavon-Editor** + **Vue-Markdown** MakenDown 编辑器，实现文章，问答，课程，资源的展示与编辑。
* 引入 **Vue-Scrollactive** 实现课程目录的导航跳转。

## 项目功能展示

### 用户模块

#### 用户首页

![用户主页](https://github.com/wsh1931/monkey-study/blob/master/doc/image/userHomeView.png?raw=true 用户主页)

#### 用户中心

![用户中心简介](https://github.com/wsh1931/monkey-study/blob/master/doc/image/userCenterProfile.png?raw=true 用户中心简介页面)

![用户中心账号](https://github.com/wsh1931/monkey-study/blob/master/doc/image/userCenterAccount.png?raw=true 用户中心账号页面)

![用户中心收藏](https://github.com/wsh1931/monkey-study/blob/master/doc/image/userCenterCollectDetail.png?raw=true 用户中心收藏)

![用户中心游览历史](https://github.com/wsh1931/monkey-study/blob/master/doc/image/userCenterHistoryContent.png?raw=true 用户中心游览历史)

#### 用户创作中心

![用户创作中心首页](https://github.com/wsh1931/monkey-study/blob/master/doc/image/userCreateHome.png?raw=true 用户创作中心首页)

![用户创作中心管理页面](https://github.com/wsh1931/monkey-study/blob/master/doc/image/userCreateManageContent.png?raw=true 用户创作中心管理页面)

#### 用户消息中心

![用户消息中心评论页面](https://github.com/wsh1931/monkey-study/blob/master/doc/image/userMessageComment.png?raw=true 用户消息中心评论页面)

#### 用户订单页面

![用户订单页面](https://github.com/wsh1931/monkey-study/blob/master/doc/image/userOrderCenter.png?raw=true 用户订单页面)

#### 用户聊天页面

![用户聊天页面](https://github.com/wsh1931/monkey-study/blob/master/doc/image/userChatViews.png?raw=true 用户聊天页面)

#### 用户会员界面

![用户会员界面](https://github.com/wsh1931/monkey-study/blob/master/doc/image/userVipView.png?raw=true 用户会员界面)

### 资源模块

#### 上传资源

![上传资源](https://github.com/wsh1931/monkey-study/blob/master/doc/image/uploadResource.png?raw=true 上传资源页面)

#### 资源主页

![资源主页](https://github.com/wsh1931/monkey-study/blob/master/doc/image/resourceView.png?raw=true 资源主页页面)



#### 资源详情

![资源详情](https://github.com/wsh1931/monkey-study/blob/master/doc/image/resourceDetail.png?raw=true 资源详情页面)

#### 资源支付页面

![资源支付页面](https://github.com/wsh1931/monkey-study/blob/master/doc/image/resourcePay.png?raw=true 资源支付页面)



![资源支付](https://github.com/wsh1931/monkey-study/blob/master/doc/image/resourcePayMoney.png?raw=true)

#### 资源支付完成

![资源支付完成](https://github.com/wsh1931/monkey-study/blob/master/doc/image/resourcePayFinish.png?raw=true 资源支付完成)

### 社区模块

#### 创建社区

![创建社区页面](https://github.com/wsh1931/monkey-study/blob/master/doc/image/createCommunity.png?raw=true 创建社区页面)

#### 社区首页

![社区首页图片](https://github.com/wsh1931/monkey-study/blob/master/doc/image/communityViews.png?raw=true 社区首页图片)

#### 社区详情

![社区详情页面](https://github.com/wsh1931/monkey-study/blob/master/doc/image/communityDetail.png?raw=true 社区详情页面)

#### 发布社区文章

![发布社区文章页面](https://github.com/wsh1931/monkey-study/blob/master/doc/image/communityArticlePublish.png?raw=true 发布社区文章页面)

![社区文章发布任务页面](https://github.com/wsh1931/monkey-study/blob/master/doc/image/communityArticlePublishTash.png?raw=true 社区文章发布任务页面)

![社区文章发布投票页面](https://github.com/wsh1931/monkey-study/blob/master/doc/image/communityArticlePublishVeto.png?raw=true 社区文章发布投票页面)

#### 社区文章详情

![社区文章详情页面](https://github.com/wsh1931/monkey-study/blob/master/doc/image/communityArticleDetail.png?raw=true 社区文章详情页面)

#### 社区管理系统

![社区管理系统页面](https://github.com/wsh1931/monkey-study/blob/master/doc/image/communityArticleManage.png?raw=true 社区管理系统页面)

### 课程模块

##### 课程首页

![课程首页页面](https://github.com/wsh1931/monkey-study/blob/master/doc/image/courseView.png?raw=true 课程首页页面)

#### 课程详情

![课程详情页面](https://github.com/wsh1931/monkey-study/blob/master/doc/image/courseDetail.png?raw=true 课程详情页面)

#### 课程播放

![课程播放页面](https://github.com/wsh1931/monkey-study/blob/master/doc/image/courseVideoPlay.png?raw=true 课程播放页面)
