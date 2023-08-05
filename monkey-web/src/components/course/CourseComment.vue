<template>
    <div>
        <el-card class="card">
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
    </div>
</template>

<script>
import { mavonEditor } from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'
export default {
    name: 'MonkeyWebCourseComment',
    components: {
        mavonEditor
    },
    data() {
        return {
            // 鼠标悬浮显示更多内容
            is_show: false,
            // false为不打开发表评论列表, true为打开发表评论列表
            publishComment: false,
        };
    },

    methods: {
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
    },
};
</script>

<style scoped>
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
.comment-img {
    width: 30px;
    height: 30px;
    border-radius: 50%;
    cursor: pointer;
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
.comment-right {
    text-align: right;
}
.comment-time {
    font-size: 14px;
    opacity: 0.5;
    padding-left: 5px;
}
.card {
    position: relative; 
    margin-top: 10px; 
    padding: 10px;
}
.will-harvest {
    top: 2px;
}
.swap-comment:hover {
    cursor: pointer;
    color: #409EFF;
}
.background-content {
    position: absolute;
    background-color: #409EFF;
    width: 5px;
    height: 20px;
    top: 33px;
    left: 18px;
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
.report:hover {
    background-color: #E4E8EE;
}
.report {
    padding: 5px;
    transition: 0.2s linear all;
}

.comment-name {
    font-size: 14px;
    opacity: 0.5;
}
</style>