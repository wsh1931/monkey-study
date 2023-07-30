<template>
    <div class="MonkeyWebCourseDetail-container">
        <el-row class="all-background">
        <el-row class="top up-to-down">
            <el-col :span="16">
                <el-row class="ellipsis-title title">
                   python从0到1：期货量化交易系统（CTP实战，高python从0到1：期货量化交易系统（CTP实战，高
                </el-row>
                <el-row style="color: white; text-align: center; padding: 20px 0px;">
                    <el-col :span="4">
                        
                        <el-row >
                            356
                        </el-row>
                        <el-row>
                            课程收藏数
                        </el-row>
                     <span class="divider-left"></span>
                    </el-col>
                    <el-col :span="6">
                        <el-row>
                            48节
                        </el-row>
                        <el-row>
                            节数
                        </el-row>
                        <span class="divider-left"></span>
                    </el-col>
                    <el-col :span="6">
                        <el-row>
                            9057人
                        </el-row>
                        <el-row>
                            学习人数
                        </el-row>
                        <span class="divider-left"></span>
                    </el-col>
                    <el-col :span="6">
                        <el-row>
                            <el-rate
                                v-model="value"
                                disabled
                                show-score
                                text-color="#ff9900"
                                score-template="{value}">
                                </el-rate>
                        </el-row>
                        <el-row>
                            课程评分
                        </el-row>
                    </el-col>
                </el-row>
                <el-row style=" padding-bottom: 20px; margin-left: 30px;">
                    <el-col :span="6">
                    <el-button type="primary" class="free-view" > <i class="el-icon-view"></i> 免费试看</el-button>
        
                    </el-col>
                    <el-col :span="6">
                        <el-button type="danger" class="buy-view">238.00 购买</el-button>
                    </el-col>
                    <el-col :span="4" class="discount">
                        课程8.0折
                    </el-col>
                    <el-col :span="6" class="original-price">
                        原价￥298
                    </el-col>
                </el-row>
            </el-col>
            <el-col :span="6" style="float: right;">
                <el-row style="text-align: right;">
                    <span class="share-convert">
                        <span class="iconfont icon-shoucang" >&nbsp;收藏</span> &nbsp;
                        <span class="iconfont icon-zhuanfa">&nbsp;转发</span>
                    </span>
                </el-row>
                <el-row class="background-teacher">
                    <el-col :span="4">
                        <img class="teacher-img" src="https://img-bss.csdnimg.cn/20220407205837850.jpg?imageMogr2/auto-orient/thumbnail/150x150!/format/jpg" alt="">
                    </el-col>
                    <el-col :span="6" >
                        <el-row class="teacher-name">吴思豪</el-row>
                        <div class="divider-right-name"></div>
                    </el-col>
                    <el-col :span="6" class="course-sum">
                        <el-row style="color: lightgreen;">44</el-row>
                        <el-row style="color: white;">课程数量</el-row>
                        <div class="divider-right"></div>
                    </el-col>
                    <el-col :span="6" class="course-sum">
                        <el-row style="color: lightgreen;">9544</el-row>
                        <el-row style="color: white;">学生人数</el-row>
                    </el-col>
                </el-row>
                <el-row>
                    <el-row class="teacher-profile">讲师简介</el-row>
                    <el-row class="teacher-profile-content">
                        擅长数据挖掘，量化交易
                    </el-row>
                </el-row>
            </el-col>
        </el-row>
        </el-row>
        <div class="anchor-point up-to-down" style="position: sticky; top: 0; z-index: 10000;">
            <a class="nav-anchor" @click="toAnchor(0)">课程介绍<span v-if="anchor == '0'" class="underline"></span></a>
            <a class="nav-anchor" @click="toAnchor(1)" style="margin-left: 65px;">课程目录 <span v-if="anchor == '1'" class="underline"></span></a>
            <a class="nav-anchor" @click="toAnchor(2)" style="margin-left: 65px; ">讨论留言 <span v-if="anchor == '2'" class="underline"></span></a>
            </div>
        <el-row class="content down-to-up" style="padding: 30px;">
                <el-col  :span="17">
                    <el-row >
                        <el-card id="listshow0" style="position: relative; padding: 10px;" class="card">
                            <el-row class="will-harvest">你将收获</el-row>
                            <span class="background-content"></span>
                            <el-row>
                                <vue-markdown 
                                class="markdown-content"
                                :source="course.harvest" 
                                :highlight="true"
                                :html="true"
                                :xhtmlOut="true">
                                </vue-markdown>
                            </el-row>
                        </el-card>
                        <el-card style="margin-top: 10px; padding: 10px;" class="card">
                            <el-row class="will-harvest">适用人群</el-row>
                            <span class="background-content"></span>
                            <el-row>
                                <vue-markdown 
                                class="markdown-content"
                                :source="course.harvest" 
                                :highlight="true"
                                :html="true"
                                :xhtmlOut="true">
                                </vue-markdown>
                            </el-row>
                        </el-card>

                        <el-card style="position: relative; margin-top: 10px; padding: 10px;" class="card">
                            <el-row class="will-harvest">课程介绍</el-row>
                            <span class="background-content"></span>
                            <el-row>
                                <vue-markdown 
                                class="markdown-content"
                                :source="course.harvest" 
                                :highlight="true"
                                :html="true"
                                :xhtmlOut="true">
                                </vue-markdown>
                            </el-row>
                        </el-card>
                    </el-row>
                    <el-card style="position: relative; margin-top: 10px; padding: 10px;" class="card" id="listshow1">
                            <el-row class="will-harvest" style="margin-bottom: 10px;">课程目录</el-row>
                            <span class="background-content"></span>
                            <el-row class="course-directory-row"> 
                                <el-col class="el-icon-video-play directory" :span="22">【基础篇】第1节：课程介绍一级行情账户的登录 <span class="is-free">免费</span></el-col>
                               <el-col class="play-time" :span="2">25.34</el-col>
                            </el-row>
                            <el-row class="course-directory-row"> 
                                <el-col class="el-icon-video-play directory" :span="22">【基础篇】第1节：课程介绍一级行情账户的登录 <span class="is-free">免费</span></el-col>
                               <el-col class="play-time" :span="2">25.34</el-col>
                            </el-row><el-row class="course-directory-row"> 
                                    <el-col class="el-icon-video-play directory" :span="22">【基础篇】第1节：课程介绍一级行情账户的登录 <span class="is-free">免费</span></el-col>
                                   <el-col class="play-time" :span="2">25.34</el-col>
                                </el-row><el-row class="course-directory-row"> 
                                <el-col class="el-icon-video-play directory" :span="22">【基础篇】第1节：课程介绍一级行情账户的登录 <span class="is-free">免费</span></el-col>
                               <el-col class="play-time" :span="2">25.34</el-col>
                            </el-row><el-row class="course-directory-row"> 
                                    <el-col class="el-icon-video-play directory" :span="22">【基础篇】第1节：课程介绍一级行情账户的登录 <span class="is-free">免费</span></el-col>
                                   <el-col class="play-time" :span="2">25.34</el-col>
                                </el-row><el-row class="course-directory-row"> 
                                    <el-col class="el-icon-video-play directory" :span="22">【基础篇】第1节：课程介绍一级行情账户的登录 <span class="is-free">免费</span></el-col>
                                   <el-col class="play-time" :span="2">25.34</el-col>
                                </el-row><el-row class="course-directory-row"> 
                                    <el-col class="el-icon-video-play directory" :span="22">【基础篇】第1节：课程介绍一级行情账户的登录 <span class="is-free">免费</span></el-col>
                                   <el-col class="play-time" :span="2">25.34</el-col>
                                </el-row>
                    </el-card>
                    
                    <el-card style="position: relative; margin-top: 10px; padding: 10px;" class="card" id="listshow2">
                        <el-row class="will-harvest" style="margin-bottom: 10px;">讨论留言</el-row>
                              <!-- 评论内容 -->
                            <el-row style="padding: 10px 0px;">
                                <el-col :span="13" >1 条回复 </el-col>
                                <el-col :span="6" style="text-align: right;" class="swap-comment"> 
                                    <span class="iconfont icon-zhuanhuan"></span> 切换为未回复评论</el-col>
                                <el-col :span="5" style="text-align: right;" class="swap-comment">
                                    <span class="iconfont icon-zhuanhuan"></span> 切换为按时间正序</el-col>
                            </el-row>
                        <span class="background-content"></span>
                        <el-row v-if="publishComment">
                            <mavon-editor
                            v-model="content" 
                            :toolbars="toolbars"
                            :translate="true"
                            defaultOpen="edit"
                            placeholder="期待您精彩的评论"
                            style="min-height: 200px; z-index: 100001;"
                            :navigation="false"
                            :subfield="false"
                            :scrollStyl="true"
                            @keydown.prevent="handleKeyDown($event)"
                            ></mavon-editor>
                            <el-button type="primary" size="small" class="publish-comment-button">发表评论</el-button>
                            <el-row class="publish-comment-indicate">按下Enter换行，Ctrl+Enter发表内容</el-row>
                        </el-row>
                        <el-row v-if="!publishComment" class="open-publish-comment" style="position: relative;">
                            <div @click="publishComment = true">
                                期待您的优质评论
                                <el-button class="button-comment" type="primary" size="small">发表评论</el-button>
                            </div>
                        </el-row>

                        <el-row>
                            <el-row style="margin-top: 20px;">
                                <el-col :span="1">
                                    <img class="comment-img" src="https://img-bss.csdnimg.cn/20220620155133916.jpg?imageMogr2/auto-orient/thumbnail/150x150!/format/jpg" alt="">
                                </el-col>
                                <el-col :span="23">
                                    <el-row>
                                        <el-row>
                                            <el-col :span="18">
                                                <el-row>
                                                    <span class="comment-name">吴思豪</span>
                                                    <span class="comment-time">2023/07/30</span>
                                                        <span class="curation-comment">
                                                            <span class="iconfont icon-jingxuanyoupin curation-comment-icon"></span>
                                                            <span class="curation-comment-font">精选</span>
                                                        </span>
                                                </el-row>
                                            </el-col>
                                            <el-col :span="6" class="comment-right">
                                                <el-row>
                                                    <el-col :span="8">
                                                        <span class="el-icon-more" 
                                                        style="position: relative; cursor: pointer;"
                                                        @mouseenter="MouseHoverMore()" 
                                                        @mouseleave="MouseLeaveMore()">
                                                            <div class="show-more" v-if="is_show">
                                                                <el-row class="report">举报</el-row>
                                                            </div>
                                                            </span>
                                                
                                                    </el-col>
                                                    <el-col :span="8">
                                                        <span class="iconfont icon-pinglun reply" > 回复</span>
                                                    </el-col>
                                                    <el-col :span="8">
                                                        <span class="iconfont icon-dianzan commend-like"> 3</span>
                                                    </el-col>
                                                </el-row>
                                            </el-col>
                                        </el-row>
                                        <el-row class="comment-content">炮哥，我买了你的视频教程《快速带你入门深度学习与实战》的教程。同时，学习了yolov5的博客，想请你提供一下，你视频里的数据集来学习用的。</el-row>
                                    </el-row>


                                    <!-- 二级评论 -->
                                    <el-row class="two-comment">
                                        <el-row>
                                            <el-row>
                                                <el-col :span="1">
                                                    <img @click="toUserView()" class="two-comment-img" src="https://img0.baidu.com/it/u=1997330805,2252719449&fm=253&app=138&size=w931&n=0&f=JPEG&fmt=auto?sec=1690822800&t=cb083cc72e9b594913f87b2262643136" alt="">
                                                </el-col>
                                                 <el-col :span="17">
                                                    <el-row>
                                                        <span class="comment-name">吴思豪</span>
                                                        <span class="comment-time">2023/07/30</span>    
                                                    </el-row>
                                                </el-col>
                                                <el-col :span="6" class="comment-right">
                                                    <el-row>
                                                        <el-col :span="8">
                                                            <span class="el-icon-more" 
                                                            style="position: relative; cursor: pointer;"
                                                            @mouseenter="MouseHoverMore()" 
                                                            @mouseleave="MouseLeaveMore()">
                                                                <div class="show-more" v-if="is_show">
                                                                    <el-row class="report">举报</el-row>
                                                                </div>
                                                                </span>
                                                        </el-col>
                                                        <el-col :span="8">
                                                            <span class="iconfont icon-pinglun reply" > 回复</span>
                                                        </el-col>
                                                        <el-col :span="8">
                                                            <span class="iconfont icon-dianzan commend-like"> 3</span>
                                                        </el-col>
                                                    </el-row>
                                                </el-col>
                                            </el-row>
                                            <el-row>
                                                <div class="two-comment-reply">wusihao <span class="two-comment-reply-content">回复内容</span> </div>
                                                
                                            </el-row>
                                        </el-row>
                                    </el-row>
                                 </el-col>
                            </el-row>
                        </el-row>
                    </el-card>
                
                </el-col>
                <el-col :span="7">
                    <el-row class="commend-course">
                        <el-row class="recommend-course-background"></el-row>
                        <el-row class="recomment-course-font">推荐</el-row>
                        <el-row class="recommend-card">
                            <el-col :span="7">
                                <img style="width: 90px; height: 60px;"  src="https://img-bss.csdnimg.cn/20208521852875_44232.jpg?imageMogr2/auto-orient/thumbnail/636x360!/format/jpg%7Cwatermark/1/image/aHR0cHM6Ly9pbWctYnNzLmNzZG5pbWcuY24vY-iuoeeul-acuuinhuiniS0wNS5wbmc=/dissolve/85/gravity/Center/dx/0/dy/0/" alt="">
                            </el-col>
                            <el-col :span="17">
                                <el-row>
                                <el-row class="section-title">
                                    计算机视觉实战，如何使用计算机
                                </el-row>
                                <el-row style="padding: 5px;">
                                    <span class="course-section"> 1552节</span>
                                       <span class="section-teacher-name">吴思豪</span>
                                </el-row>
                                </el-row>
                            </el-col>
                        </el-row>
                         <el-row class="recommend-card">
                                <el-col :span="7">
                                    <img style="width: 90px; height: 60px;"  src="https://img-bss.csdnimg.cn/20208521852875_44232.jpg?imageMogr2/auto-orient/thumbnail/636x360!/format/jpg%7Cwatermark/1/image/aHR0cHM6Ly9pbWctYnNzLmNzZG5pbWcuY24vY-iuoeeul-acuuinhuiniS0wNS5wbmc=/dissolve/85/gravity/Center/dx/0/dy/0/" alt="">
                                </el-col>
                                <el-col :span="17">
                                    <el-row>
                                    <el-row class="section-title">
                                        计算机视觉实战，如何使用计算机
                                    </el-row>
                                    <el-row style="padding: 5px;">
                                        <span class="course-section"> 1552节</span>
                                           <span class="section-teacher-name">吴思豪</span>
                                    </el-row>
                                    </el-row>
                                </el-col>
                            </el-row>
                             <el-row class="recommend-card">
                                <el-col :span="7">
                                    <img style="width: 90px; height: 60px;"  src="https://img-bss.csdnimg.cn/20208521852875_44232.jpg?imageMogr2/auto-orient/thumbnail/636x360!/format/jpg%7Cwatermark/1/image/aHR0cHM6Ly9pbWctYnNzLmNzZG5pbWcuY24vY-iuoeeul-acuuinhuiniS0wNS5wbmc=/dissolve/85/gravity/Center/dx/0/dy/0/" alt="">
                                </el-col>
                                <el-col :span="17">
                                    <el-row>
                                    <el-row class="section-title">
                                        计算机视觉实战，如何使用计算机
                                    </el-row>
                                    <el-row style="padding: 5px;">
                                        <span class="course-section"> 1552节</span>
                                           <span class="section-teacher-name">吴思豪</span>
                                    </el-row>
                                    </el-row>
                                </el-col>
                            </el-row>
                             <el-row class="recommend-card">
                                <el-col :span="7">
                                    <img style="width: 90px; height: 60px;"  src="https://img-bss.csdnimg.cn/20208521852875_44232.jpg?imageMogr2/auto-orient/thumbnail/636x360!/format/jpg%7Cwatermark/1/image/aHR0cHM6Ly9pbWctYnNzLmNzZG5pbWcuY24vY-iuoeeul-acuuinhuiniS0wNS5wbmc=/dissolve/85/gravity/Center/dx/0/dy/0/" alt="">
                                </el-col>
                                <el-col :span="17">
                                    <el-row>
                                    <el-row class="section-title">
                                        计算机视觉实战，如何使用计算机
                                    </el-row>
                                    <el-row style="padding: 5px;">
                                        <span class="course-section"> 1552节</span>
                                           <span class="section-teacher-name">吴思豪</span>
                                    </el-row>
                                    </el-row>
                                </el-col>
                            </el-row>
                             <el-row class="recommend-card">
                                <el-col :span="7">
                                    <img style="width: 90px; height: 60px;"  src="https://img-bss.csdnimg.cn/20208521852875_44232.jpg?imageMogr2/auto-orient/thumbnail/636x360!/format/jpg%7Cwatermark/1/image/aHR0cHM6Ly9pbWctYnNzLmNzZG5pbWcuY24vY-iuoeeul-acuuinhuiniS0wNS5wbmc=/dissolve/85/gravity/Center/dx/0/dy/0/" alt="">
                                </el-col>
                                <el-col :span="17">
                                    <el-row>
                                    <el-row class="section-title">
                                        计算机视觉实战，如何使用计算机
                                    </el-row>
                                    <el-row style="padding: 5px;">
                                        <span class="course-section"> 1552节</span>
                                           <span class="section-teacher-name">吴思豪</span>
                                    </el-row>
                                    </el-row>
                                </el-col>
                            </el-row>
                    </el-row>

                    <el-row class="connect-course">
                            <el-row class="connect-course-background"></el-row>
                            <el-row class="recomment-course-font">相关</el-row>
                            <el-row class="recommend-card">
                                <el-col :span="7">
                                    <img style="width: 90px; height: 60px;"  src="https://img-bss.csdnimg.cn/20208521939823_27859.jpg?imageMogr2/auto-orient/thumbnail/636x360!/format/jpg%7Cwatermark/1/image/aHR0cHM6Ly9pbWctYnNzLmNzZG5pbWcuY24v5bWM5YWl5byPXzMucG5n/dissolve/85/gravity/Center/dx/0/dy/0/" alt="">
                                </el-col>
                                <el-col :span="17">
                                    <el-row>
                                    <el-row class="section-title">
                                        计算机视觉实战，如何使用计算机
                                    </el-row>
                                    <el-row style="padding: 5px;">
                                        <span class="course-section"> 1552节</span>
                                           <span class="section-teacher-name">吴思豪</span>
                                    </el-row>
                                    </el-row>
                                </el-col>
                            </el-row>
                            <el-row class="recommend-card">
                                <el-col :span="7">
                                    <img style="width: 90px; height: 60px;"  src="https://img-bss.csdnimg.cn/20208521939823_27859.jpg?imageMogr2/auto-orient/thumbnail/636x360!/format/jpg%7Cwatermark/1/image/aHR0cHM6Ly9pbWctYnNzLmNzZG5pbWcuY24v5bWM5YWl5byPXzMucG5n/dissolve/85/gravity/Center/dx/0/dy/0/" alt="">
                                </el-col>
                                <el-col :span="17">
                                    <el-row>
                                    <el-row class="section-title">
                                        计算机视觉实战，如何使用计算机
                                    </el-row>
                                    <el-row style="padding: 5px;">
                                        <span class="course-section"> 1552节</span>
                                           <span class="section-teacher-name">吴思豪</span>
                                    </el-row>
                                    </el-row>
                                </el-col>
                            </el-row>
                            <el-row class="recommend-card">
                                <el-col :span="7">
                                    <img style="width: 90px; height: 60px;"  src="https://img-bss.csdnimg.cn/20208521939823_27859.jpg?imageMogr2/auto-orient/thumbnail/636x360!/format/jpg%7Cwatermark/1/image/aHR0cHM6Ly9pbWctYnNzLmNzZG5pbWcuY24v5bWM5YWl5byPXzMucG5n/dissolve/85/gravity/Center/dx/0/dy/0/" alt="">
                                </el-col>
                                <el-col :span="17">
                                    <el-row>
                                    <el-row class="section-title">
                                        计算机视觉实战，如何使用计算机
                                    </el-row>
                                    <el-row style="padding: 5px;">
                                        <span class="course-section"> 1552节</span>
                                           <span class="section-teacher-name">吴思豪</span>
                                    </el-row>
                                    </el-row>
                                </el-col>
                            </el-row>
                            <el-row class="recommend-card">
                                    <el-col :span="7">
                                        <img style="width: 90px; height: 60px;"  src="https://img-bss.csdnimg.cn/20208521939823_27859.jpg?imageMogr2/auto-orient/thumbnail/636x360!/format/jpg%7Cwatermark/1/image/aHR0cHM6Ly9pbWctYnNzLmNzZG5pbWcuY24v5bWM5YWl5byPXzMucG5n/dissolve/85/gravity/Center/dx/0/dy/0/" alt="">
                                    </el-col>
                                    <el-col :span="17">
                                        <el-row>
                                        <el-row class="section-title">
                                            计算机视觉实战，如何使用计算机
                                        </el-row>
                                        <el-row style="padding: 5px;">
                                            <span class="course-section"> 1552节</span>
                                               <span class="section-teacher-name">吴思豪</span>
                                        </el-row>
                                        </el-row>
                                    </el-col>
                                </el-row>
                            <el-row class="recommend-card">
                                    <el-col :span="7">
                                        <img style="width: 90px; height: 60px;"  src="https://img-bss.csdnimg.cn/20208521939823_27859.jpg?imageMogr2/auto-orient/thumbnail/636x360!/format/jpg%7Cwatermark/1/image/aHR0cHM6Ly9pbWctYnNzLmNzZG5pbWcuY24v5bWM5YWl5byPXzMucG5n/dissolve/85/gravity/Center/dx/0/dy/0/" alt="">
                                    </el-col>
                                    <el-col :span="17">
                                        <el-row>
                                        <el-row class="section-title">
                                            计算机视觉实战，如何使用计算机
                                        </el-row>
                                        <el-row style="padding: 5px;">
                                            <span class="course-section"> 1552节</span>
                                               <span class="section-teacher-name">吴思豪</span>
                                        </el-row>
                                        </el-row>
                                    </el-col>
                                </el-row>
                        </el-row>
                </el-col>
        </el-row>
    </div>
</template>

<script>
import { mavonEditor } from 'mavon-editor'
import VueMarkdown from 'vue-markdown';
import 'mavon-editor/dist/css/index.css'
export default {
    name: 'MonkeyWebCourseDetail',
    components: {
        mavonEditor,
        VueMarkdown
    },
    data() {
        return {
            // 当前页面的课程id
            courseId: "",
            // 鼠标悬浮显示更多内容
            is_show: false,
            // 是否贴在屏幕最上方
            isSticky: false,
            value: 3.7,
            content: "",
            // false为不打开发表评论列表, true为打开发表评论列表
            publishComment: false,
            course: {
                harvest: "零基础学会CTP下单查询等操作\n多线程稳定CTP交易系统\n期货的保证金，手续费，平仓盈亏等计算方法\n将期货量化策略付诸实践并从中获利"
            },
            // 选中的课程锚点，0表示课程介绍，1表示课程目录，2表示讨论留言
            anchor: 1,
            toolbars: {
                bold: true, // 粗体
                italic: true, // 斜体
                header: true, // 标题
                underline: true, // 下划线
                strikethrough: true, // 中划线
                mark: true, // 标记
                superscript: true, // 上角标
                subscript: true, // 下角标
                quote: true, // 引用
                ol: true, // 有序列表
                ul: true, // 无序列表
                link: true, // 链接
                imagelink: true, // 图片链接
                code: true, // code
                table: true, // 表格
                fullscreen: true, // 全屏编辑
                readmodel: true, // 沉浸式阅读
                help: true, // 帮助
                preview: true, // 预览
            }
        };
    },
    created() {
        this.courseId = this.$route.params.courseId;
    },
    methods: {
        getCourseInfoByCourseId() {
            
        },
        // 鼠标悬浮省略号
        MouseHoverMore() {
            this.is_show = true;
        },
        // 鼠标离开省略号
        MouseLeaveMore() {
            this.is_show = false;
        },
        handleKeyDown(e) {
            if (e.keyCode === 13 && !e.ctrlKey) {
                // Enter，换行
              
                this.content += '\n';
            } else if (e.keyCode === 13 && e.ctrlKey) {
                  // Ctrl + Enter，发送消息
                
            }
        },
        // 跳转到对应的锚点
       toAnchor(num) {
            this.anchor = num;
            var el = document.getElementById(`listshow${num}`);
            if (this.showIndex == num) {
                this.showIndex = null;
            } else {
                this.showIndex = num;
            }
            this.$nextTick(function () {
                window.scrollTo({ "behavior": "smooth", "top": el.getBoundingClientRect().top + window.pageYOffset });
            });
        }


    },
};
</script>

<style scoped>
.up-to-down {
    animation:up-to-down  0.4s linear;
}

@keyframes up-to-down {
    0% {
        transform: translateY(-100px);
        opacity: 0;
    }
    60% {
        transform: translateY(-30px);
        opacity: 1;
    }
    80% {
        transform: translateY(20px);
    }
    100% {
        transform: translateY(0px);
    }
}

.down-to-up {
    animation:down-to-up  0.4s linear;
}

@keyframes down-to-up {
    0% {
        transform: translateY(100px);
        opacity: 0;
    }
    60% {
        transform: translateY(30px);
        opacity: 1;
    }
    80% {
        transform: translateY(-20px);
    }
    100% {
        transform: translateY(0px);
    }
}
.two-comment-reply-content {
    color: black;
    font-size: 14px;
}
.two-comment-reply::before {
    content: "@";
}
.two-comment-reply {
    color: #409EFF;
    font-size: 14px;
}
.comment-content {
    font-size: 15px;
    font-weight: 540;
    padding-top: 10px;
}
.two-comment-img {
    width: 20px;
    height: 20px;
    cursor: pointer;
    border-radius: 50%;
}
.two-comment {
    background-color: #eef2f5;
    padding: 10px;
    border-radius: 10px;
    margin-top: 10px;
}

.commend-like:hover {
    cursor: pointer;
    color: #409EFF;
}
.reply {
    font-size: 14px;
    cursor: pointer;
}
.reply:hover {
    color: #409EFF;
}
.report:hover {
    background-color: #E4E8EE;
}
.report {
    padding: 5px;
    transition: 0.2s linear all;
}
.show-more {
    position: absolute;
    background-color: #FFFFFF;
    border: 1px solid #E2E7ED;
    box-shadow: 0 0 5px #E2E7ED;
    width: 80px;
    text-align: center;
    left: -30px;
    padding: 10px;
}
.comment-right {
    text-align: right;
}
.curation-comment {
    background-color: #FFEEE9;
    text-align: center;
    margin-left: 10px;
    width: 50px;
    height: 20px;
    display: inline-block;
    line-height: 20px;

}
.curation-comment-icon {
    color: #FC5531;
    font-size: 12px;
}
.curation-comment-font {
    color: #FF4D4D;
    font-size: 12px;
}
.comment-time {
    font-size: 14px;
    opacity: 0.5;
    padding-left: 5px;
}
.comment-name {
    font-size: 14px;
    opacity: 0.5;
}
.comment-img {
    width: 30px;
    height: 30px;
    border-radius: 50%;
    cursor: pointer;
}
.swap-comment:hover {
    cursor: pointer;
    color: #409EFF;
}
.connect-course-background {
    position: absolute;
    background-color: #00984E;
    color: white;
    top: -29px;
    left: -29px;
    height: 60px;
    width: 60px;
    text-align: center;
    line-height: 25px;
    transform: rotate(-45deg);
}
.connect-course {
    background-color: #FFFFFF;
    position: relative;
    margin-left: 10px;
    padding: 10px;
    border-radius: 10px;
    overflow: hidden;
    margin-top: 10px;
}
.recommend-card {
    padding: 10px;
    transition: 0.2s linear all;
}
.recommend-card:hover {
    cursor: pointer;
    background-image: linear-gradient(to top, #d5d4d0 0%, #d5d4d0 1%, #eeeeec 31%, #efeeec 75%, #e9e9e7 100%);
    opacity: 0.5;
}
.section-teacher-name {
    padding-left: 10px;
    font-size: 14px;
    opacity: 0.7;
}
.course-section::after {
    content: "\2022";
    margin-left: 10px;
    position: relative;
    top: -4px;
    color: gray;
}
.section-title {
    display: block;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
    text-align: left;
    opacity: 0.8;
    padding: 5px;
}
.course-section {
    text-align: left;
    font-size: 14px;
    opacity: 0.7;

}
.recomment-course-font {
    position: absolute;
    color: white;
    top: 4px;
    left: 0px;
    font-size: 14px;
    transform: rotate(-45deg);
}
.recommend-course-background {
    position: absolute;
    background-color: #FFAA00;
    color: white;
    top: -29px;
    left: -29px;
    height: 60px;
    width: 60px;
    text-align: center;
    line-height: 25px;
    transform: rotate(-45deg);
}
.commend-course {
    background-color: #FFFFFF;
    position: relative;
    margin-left: 10px;
    padding: 10px;
    border-radius: 10px;
    overflow: hidden;
}
.sticky {
    position: fixed;
    margin-top: 10px;
}
.button-comment {
    position: absolute;
    width: 80px;
    height: 30px;
    text-align: center;
    z-index: 10000;
    left: 800px;
    bottom: 7px;
    border-radius: 20px;
    background-color: #9a9d9d;
    border: none;
}
.button-comment:hover {
    background-color: #9a9d9d;
}
.open-publish-comment {
    position: relative;
    width: 100%;
    height: 45px;
    background-color: #dde1e5;
    padding: 10px;
    cursor: pointer;
    font-size: 15px;
    line-height: 25px;
    color: #7f8389;
    border-radius: 10px;
}
.publish-comment-indicate {
    position: absolute;
     top: 175px;
     left: 600px;
    z-index: 100002;
    font-size: 12px;
    opacity: 0.5;
}
.publish-comment-button {
    position: absolute;
    border-radius: 20px;
    top: 168px;
    text-align: center;
    left: 805px;
    z-index: 100002;
    width: 76px;
    height: 28px;
    line-height: 10px;
    
}
.custom-input {
  border-color: #42b983;
  background-color: #f0f9eb;
  color: #555;
}

.custom-input:focus {
  border-color: #42b983;
  box-shadow: 0 0 0 2px rgba(66, 185, 131, 0.2);
}


.play-time {
     font-size: 12px; 
     line-height: 34px;
}
.course-directory-row {
    padding: 10px;
}
.course-directory-row:hover {
    color: #087df3;
    cursor: pointer;
    background-color: rgb(227, 240, 244);
    transition: 0.3s linear all;
}
.directory {
    font-size: 14px;
    padding: 10px;
}

.is-free {
    position: absolute;
    width: 30px;
    height: 14px;
  background-image: linear-gradient(to right, #92fe9d 0%, #00c9ff 100%);
    margin-left: 10px;
    font-size: 12px;
    text-align: center;
    line-height: 14px;
    color: white;
}
.card {
    border-radius: 20px;
    position: relative; 
}
.markdown-content {
    font-size: 14px;
    opacity: 0.8;
}
.background-content {
    position: absolute;
    background-color: #409EFF;
    width: 5px;
    height: 20px;
    top: 33px;
    left: 18px;
}
.will-harvest {
    top: 2px;
}
.content {
    margin: 10px auto; 
    width: 1400px;
}
.underline {
    width: 70px;
    height: 1px;
    background-color: black;
    position: absolute;
    top: 25px;
    left: -2px;
}
.nav-anchor:hover {
    cursor: pointer;
    color: black;
    opacity: 1;
}
.nav-anchor {
    opacity: 0.8;
    position: relative;
}
.anchor-point {
    line-height: 60px;
    background: white;
    height: 60px;
    text-align: center;
}
.teacher-profile-content {
    color: white;
    font-size: 14px;
    padding-top: 10px;
}
.teacher-profile {
    color: white;
    padding-top: 10px;
    font-size: 14px;
}
.divider-right-name {
    position: absolute; 
    height: 25px; 
    width: 1px; 
    margin-left: 80px;
    top: 20px;
    background-color: white;
}
.divider-right {
    position: absolute; 
    height: 25px; 
    width: 1px; 
    margin-left: 80px;
    top: 20px;
    background-color: white;
}
.course-sum {
    margin-top: 10px;
}
.teacher-name {
    margin-top: 20px;
    color: white;
}
.teacher-sex {
    margin-top: 10px;
    color: white;
}
.background-teacher {
    text-align: center;
    padding: 0;
    background-color: rgba(0, 0, 0, 0.2);
    border-radius: 15px;
}
.teacher-img {
    width: 55px; 
    height: 55px; 
    border-radius: 50%;
    padding-top: 3px;
}
.icon-zhuanfa:hover {
    cursor: pointer;
    color: #57c6e1;
}
.share-convert{
    color: white;
    font-size: 30px;
    padding: 5px;
}
.icon-shoucang:hover {
    cursor: pointer;
    color: #57c6e1;
}
.title {
    padding-left: 29px;
}
.original-price {
    margin-top: 12px;
    text-align: left;
    padding-left: 20px;
    color: #E5E9F0;
    text-decoration: line-through;
}
.discount {
    margin-top: 12px;
    text-align: center;
    color: white;
    height: 22px;
    font-size: 14px;
    padding-top: 1px;
    width: 110px;
    background-image: linear-gradient(to right, #eea2a2 0%, #bbc1bf 19%, #57c6e1 42%, #b49fda 79%, #7ac5d8 100%);
}
.buy-view::before {
    content: "￥";
}
.buy-view {
    height: 50px; 
    font-size: 20px; 
    font-weight: bolder;
    color: black;
}
.el-icon-view {
    font-size: 20px; 
    font-weight: bolder;
}
.free-view {
    width: 150px; 
    height: 50px; 
    font-size: 20px; 
    font-weight: bolder;
}

.divider-left {
    position: absolute; 
    height: 25px; 
    width: 1px; 
    margin-left: 115px;
    top: 30px;
    background-color: white;
}
.ellipsis-title {
    font-size: 27px;
    font-weight: bold;
    color: white;
    display: block;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
    text-align: left;
}

.top {
    background-image: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    padding: 20px 40px;
    width: 1380px;
    margin: 0 auto;
  

}
.all-background {
    background-image: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    margin: 0 auto;
    height: 100%;
}
</style>