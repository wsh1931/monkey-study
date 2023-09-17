<template>
    <div>
        <el-card class="card">
            <el-row class="will-harvest" style="margin-bottom: 10px;">讨论留言</el-row>
                    <!-- 评论内容 -->
                <el-row style="padding: 10px 0px;">
                    <el-col :span="13" >{{ commentCount }} 条回复 </el-col>
                    <el-col :span="6" style="text-align: right;" class="swap-comment"> 
                        <span @click="getUnReplyCourseComment(courseId)">
                            <span class="iconfont icon-zhuanhuan"></span> 切换为未回复评论
                        </span>
                    </el-col>
                    <el-col :span="5" style="text-align: right;" class="swap-comment">
                        <span v-if="orderInformation == '0'" @click="getDownOrUpgradeCourseComment(courseId, 1)">
                            <span class="iconfont icon-zhuanhuan"></span> 切换为时间降序
                        </span>
                        <span v-if="orderInformation == '1'" @click="getDownOrUpgradeCourseComment(courseId, 2)">
                                <span class="iconfont icon-zhuanhuan"></span> 切换为时间升序
                        </span>
                        <span v-if="orderInformation == '2'" @click="getCourseCommentList(courseId, 0)">
                            <span class="iconfont icon-zhuanhuan"></span> 切换为默认排序
                        </span>
                    </el-col>
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
                @keydown.native="handleKeyDown($event)"
                ></mavon-editor>
                <el-button 
                type="primary" 
                size="small" 
                class="publish-comment-button" 
                @click="publishCourseComment(content, courseId)">发表评论</el-button>
                <el-row class="publish-comment-indicate">按下Enter换行，Ctrl+Enter发表内容</el-row>
            </el-row>
            <el-row v-if="!publishComment" class="open-publish-comment" style="position: relative;">
                <div @click="publishComment = true">
                    期待您的优质评论
                    <el-button class="button-comment" type="primary" size="small">发表评论</el-button>
                </div>
            </el-row>

            <el-row>
                <el-row style="margin-top: 20px;" v-for="(courseOneComment, oneIndex) in courseCommentList" :key="courseOneComment.id">
                    <el-col :span="1">
                        <img class="comment-img" :src="courseOneComment.senderPhoto" alt="">
                    </el-col>
                    <el-col :span="23">
                        <el-row>
                            <div 
                                @mouseenter="MouseHoverMore(courseOneComment)" 
                                @mouseleave="MouseLeaveMore(courseOneComment)"
                                >
                            <el-row >
                                <el-col :span="18">
                                    <el-row>
                                        <span class="comment-name">{{ courseOneComment.senderName }}</span>
                                        <span class="comment-time">{{ courseOneComment.commentTime }}</span>
                                            <span class="curation-comment" v-if="courseOneComment.isCuration == '1'">
                                                <span class="iconfont icon-jingxuanyoupin curation-comment-icon">&nbsp;精选</span>
                                            </span>
                                    </el-row>
                                </el-col>
                                <el-col :span="6" class="comment-right">
                                    <el-row>
                                        <el-col :span="8">
                                            <span class="el-icon-more more" 
                                                @mouseenter="MouseHoverMoreContent(courseOneComment, $event)" 
                                                @mouseleave="MouseLeaveMoreContent(courseOneComment)"
                                                v-if="courseOneComment.isShowMore == '1'"
                                                >
                                                <div class="show-more" 
                                                
                                                v-if="courseOneComment.isShowMoreContent == '1'">
                                                    <div 
                                                    @click="excellentSelect(courseOneComment)" 
                                                    class="report" 
                                                    v-if="courseOneComment.isCuration == '0' && isAuthorization">精选
                                                    </div>

                                                    <div 
                                                    @click="excellentSelect(courseOneComment)" 
                                                    class="report" v-if="courseOneComment.isCuration == '1' && isAuthorization">取消精选
                                                    </div>
                                                    <div class="report">举报</div>
                                                    <el-row 
                                                        class="report" 
                                                        v-if="courseOneComment.commentIsOfLoginUser == '1'">
                                                        <span @click="deleteCourseComment(courseOneComment, courseOneComment.parentId, oneIndex, 1)">删除</span> 
                                                    </el-row>
                                                </div>
                                            </span>

                                                <span
                                                    v-if="courseOneComment.isShowMore == '0'"
                                                    style="position: relative; cursor: pointer;">
                                                    &nbsp;
                                                </span>
                                
                                        </el-col>
                                        <el-col :span="8">
                                            <span v-if="courseOneComment.showInput == '0'" class="iconfont icon-pinglun reply" @click="showReply(courseOneComment, 1)"> 回复</span>
                                            <span v-if="courseOneComment.showInput == '1'" class="iconfont icon-pinglun reply" @click="showReply(courseOneComment, 0)"> 收起</span>
                                        </el-col>
                                        <el-col :span="8">
                                            <span 
                                            v-if="courseOneComment.isLike == '0'"
                                            class="iconfont icon-dianzan commend-like"  
                                            @click="likeCourseComment(courseOneComment)"> 
                                                {{ courseOneComment.commentLikeSum }}
                                            </span>
                                            <span 
                                                v-if="courseOneComment.isLike == '1'"
                                                class="iconfont icon-dianzan commend-like" 
                                                style="color: lightgreen;"
                                                @click="likeCourseComment(courseOneComment)">
                                                    {{ courseOneComment.commentLikeSum }}
                                            </span>
                                        </el-col>
                                    </el-row>
                                </el-col>
                            
                            </el-row>
                            
                            <el-row >
                                <vue-markdown 
                                class="comment-content"
                                :source="courseOneComment.content" 
                                :highlight="true"
                                :html="true"
                                :xhtmlOut="true">
                                </vue-markdown>
                            </el-row>
                            <el-row style="padding: 10px;" v-if="courseOneComment.showInput == '1'" >
                                <el-input 
                                :show-word-limit="true"
                                    minlength="1"
                                    maxlength="255"
                                    type="textarea"
                                    :autosize="{ minRows: 5, maxRows: 5}"
                                    max="100"
                                    v-model="courseOneComment.replyContent"
                                    @keydown.native="handleKeyDownReplyComment($event, courseOneComment, courseOneComment)"
                                    :placeholder="courseOneComment.placeholderContent + '  按下Enter换行，Ctrl+Enter发表内容'">
                                </el-input>
                            </el-row>
                            </div>  
                        </el-row>
                        <!-- 二级评论 -->
                        
                        <el-row class="two-comment" v-for="(courseTwoComment, twoIndex) in courseOneComment.downComment" :key="courseTwoComment.id">
                            <div @mouseenter="MouseHoverMore(courseTwoComment)" 
                                @mouseleave="MouseLeaveMore(courseTwoComment)">
                            <el-row>
                                <el-row>
                                    <el-col :span="1">
                                        <img @click="toUserView()" class="two-comment-img" :src="courseTwoComment.replyPhoto" alt="">
                                    </el-col>
                                        <el-col :span="17">
                                        <el-row>
                                            <span class="comment-name">{{ courseTwoComment.replyName }}</span>
                                            <span class="comment-time">{{ courseTwoComment.commentTime }}</span>  
                                            <span class="curation-comment" v-if="courseTwoComment.isCuration == '1'">
                                                <span class="iconfont icon-jingxuanyoupin curation-comment-icon">&nbsp;精选</span>
                                            </span>  
                                        </el-row>
                                    </el-col>
                                    <el-col :span="6" class="comment-right">
                                        <el-row>
                                            <el-col :span="8">
                                                <span class="el-icon-more more" 
                                                    @mouseenter="MouseHoverMoreContent(courseTwoComment)" 
                                                    @mouseleave="MouseLeaveMoreContent(courseTwoComment)"
                                                    v-if="courseTwoComment.isShowMore == '1'"
                                                    >
                                                    <div class="show-more" 
                                                
                                                    v-if="courseTwoComment.isShowMoreContent == '1'">
                                                    <div
                                                    class="report" 
                                                    @click="excellentSelect(courseTwoComment)" 
                                                    v-if="courseTwoComment.isCuration == '0' && isAuthorization">精选
                                                    </div>

                                                    <div
                                                    class="report" 
                                                    v-if="courseTwoComment.isCuration == '1' && isAuthorization"
                                                    @click="excellentSelect(courseTwoComment)">取消精选
                                                    </div>
                                                        <el-row class="report" >举报</el-row>
                                                        <el-row class="report" v-if="courseTwoComment.commentIsOfLoginUser == '1'">
                                                            <span @click="deleteCourseComment(courseOneComment, courseOneComment.parentId, twoIndex, 2)">删除</span> 
                                                        </el-row>
                                                    </div>
                                                </span>

                                                    <span
                                                        v-if="courseTwoComment.isShowMore == '0'"
                                                        style="position: relative; cursor: pointer;">
                                                        &nbsp;
                                                    </span>
                                            </el-col>
                                            <el-col :span="8">
                                                <span v-if="courseTwoComment.showInput == '0'" class="iconfont icon-pinglun reply" @click="showReply(courseTwoComment, 1)"> 回复</span>
                                                <span v-if="courseTwoComment.showInput == '1'" class="iconfont icon-pinglun reply" @click="showReply(courseTwoComment, 0)"> 收起</span>
                                            </el-col>
                                            <el-col :span="8">
                                                <span 
                                                v-if="courseTwoComment.isLike == '0'"
                                                class="iconfont icon-dianzan commend-like" 
                                                @click="likeCourseComment(courseTwoComment)">
                                                    {{ courseTwoComment.commentLikeSum }}
                                                </span>
                                                <span 
                                                    v-if="courseTwoComment.isLike == '1'"
                                                    class="iconfont icon-dianzan commend-like" 
                                                    style="color: lightgreen;"
                                                    @click="likeCourseComment(courseTwoComment)">
                                                        {{ courseTwoComment.commentLikeSum }}
                                                </span>
                                            </el-col>
                                        </el-row>
                                    </el-col>
                                </el-row>
                                
                                <el-row>
                                    <div class="two-comment-reply">{{ courseTwoComment.senderName }}
                                        <span >
                                            <vue-markdown 
                                            class="two-comment-reply-content"
                                            :source="courseTwoComment.content" 
                                            :highlight="true"
                                            :html="true"
                                            :xhtmlOut="true">
                                            </vue-markdown>
                                        </span> 
                                    </div>
                                </el-row>
                                <el-row style="padding: 10px;" v-if="courseTwoComment.showInput == '1'" >
                                        <el-input 
                                        :show-word-limit="true"
                                            minlength="1"
                                            maxlength="255"
                                            type="textarea"
                                            :autosize="{ minRows: 5, maxRows: 5 }"
                                            max="100"
                                            v-model="courseTwoComment.replyContent"
                                            @keydown.native="handleKeyDownReplyComment($event, courseTwoComment, courseOneComment)"
                                            :placeholder="courseOneComment.placeholderContent + '  按下Enter换行，Ctrl+Enter发表内容'">
                                        </el-input>
                                    </el-row>
                            </el-row>
                            </div>
                        </el-row>
                        
                        </el-col>
                </el-row>
            </el-row>
        </el-card>
    </div>
</template>

<script>
import $ from "jquery"
import { mavonEditor } from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'
import store from "@/store";
import VueMarkdown from 'vue-markdown';
export default {
    name: 'MonkeyWebCourseComment',
    components: {
        mavonEditor,
        VueMarkdown
    },
    data() {
        return {
            // 判断当前登录用户是否是课程作者
            isAuthorization: false,
            // 0为默认排序, 1为课程列表按降序排序, 2为升序
            orderInformation: 0,
            courseCommentUrl: "http://localhost/monkey-course/comment",
            // 鼠标悬浮显示更多内容
            is_show: false,
            // false为不打开发表评论列表, true为打开发表评论列表
            publishComment: false,
            courseCommentList: [],
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
            },
            // 课程id
            courseId: "",
            // 课程评论内容
            content: "",
            // 评论总数
            commentCount: "0",
        };
    },
    created() {
        this.courseId = this.$route.params.courseId;
        this.getCourseCommentList(this.courseId);
        this.judgeIsAuthor(this.courseId);
    },
    methods: {
        // 精选课程评论
        excellentSelect(courseComment) {
            const vue = this;
            $.ajax({
                url: vue.courseCommentUrl + "/excellentSelect",
                type: "put",
                data: {
                    courseComment: JSON.stringify(courseComment),
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        if (courseComment.isCuration == '1') {
                            courseComment.isCuration = '0';
                        } else {
                            courseComment.isCuration = '1';
                        }

                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 判断当前课程用户是否是课程作者
        judgeIsAuthor(courseId) {
            const vue = this;
            $.ajax({
                url: vue.courseCommentUrl + "/judgeIsAuthor",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    courseId
                },
                success(response) {
                    if (response.code == '200') {
                        vue.isAuthorization = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 得到未回复评论集合
        getUnReplyCourseComment(courseId, type) {
            const vue = this;
            $.ajax({
                url: vue.courseCommentUrl + "/getUnReplyCourseComment",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    courseId,
                    type
                },
                success(response) {
                    if (response.code == '200') {
                        vue.courseCommentList = response.data.courseCommentVoList;
                        vue.commentCount = response.data.commentCount;
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 得到时间评论降序/升序课程评论列表(type == -1为降序，type == 1为升序)
        getDownOrUpgradeCourseComment(courseId, type) {
            const vue = this;
            $.ajax({
                url: vue.courseCommentUrl + "/getDownOrUpgradeCourseComment",
                type: "get",
                data: {
                    type,
                    courseId
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.orderInformation = (vue.orderInformation + 1) % 3;
                        vue.courseCommentList = response.data.courseCommentVoList;
                        vue.commentCount = response.data.commentCount;
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 删除评论
        deleteCourseComment(courseComment, parentId, index, type) {
            const vue = this;
            $.ajax({
                url: vue.courseCommentUrl + "/deleteCourseComment",
                type: "delete",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    courseCommentId: courseComment.id,
                    parentId,
                    courseId: vue.courseId
                },
                success(response) {
                    if (response.code == '200') {
                        if (type == '1') {
                            // 删除一级评论
                            vue.courseCommentList.splice(index, 1);
                        } else if (type == '2') {
                            // 删除二级评论
                            courseComment.downComment.splice(index, 1);
                        }
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                } 
            })
        },
        // 用户评论点赞实现
        likeCourseComment(courseComment) {
            const vue = this;
            $.ajax({
                url: vue.courseCommentUrl + "/likeCourseComment",
                type: "put",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    courseCommentId: courseComment.id
                },
                success(response) {
                    if (response.code == '200') {
                        if (courseComment.isLike == '1') {
                            courseComment.isLike = '0';
                            courseComment.commentLikeSum--;
                        } else {
                            courseComment.isLike = '1';
                            courseComment.commentLikeSum++;
                        }
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 课程回复功能
        replyCourseComment(senderId, replyContent, courseCommentId, courseId) {
            const vue = this;
            $.ajax({
                url: vue.courseCommentUrl + "/replyCourseComment",
                type: "post",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    senderId,
                    replyContent,
                    courseCommentId,
                    courseId,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.getCourseCommentList(vue.courseId);
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 展示回复
        showReply(courseComment, type) {
            courseComment.showInput = type;
            courseComment.placeholderContent = "@"  + courseComment.senderName
        },
        // 得到课程评论列表
        getCourseCommentList(courseId, type) {
            const vue = this;
            $.ajax({
                url: vue.courseCommentUrl + "/getCourseCommentList",
                type: "get",
                data: {
                    courseId
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        if (type == 0) {
                            vue.orderInformation = (vue.orderInformation + 1) % 3;
                        }
                        vue.commentCount = response.data.commentCount
                        vue.courseCommentList = response.data.courseCommentVoList;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 发表课程评论
        publishCourseComment(content, courseId) {
            const vue = this;
            $.ajax({
                url: vue.courseCommentUrl + "/publishCourseComment",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    content,
                    courseId,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.content = "";
                        vue.getCourseCommentList(courseId);
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },

        // 鼠标悬浮选中行
        MouseHoverMore(courseComment) {
            courseComment.isShowMore = 1;
        },
        // 鼠标离开选中行
        MouseLeaveMore(courseComment) {
            courseComment.isShowMore = 0;
            courseComment.isShowMoreContent = 0
        },
        // 鼠标悬浮省略号
        MouseHoverMoreContent(courseComment) {
            courseComment.isShowMoreContent = 1;
        },
        // 鼠标离开省略号
        MouseLeaveMoreContent(courseComment) {
            courseComment.isShowMoreContent = 0;
        },
        handleKeyDown(e) {
            if (e.keyCode === 13 && !e.ctrlKey) {
                // Enter，换行

                this.content += '\n';
            } else if (e.keyCode === 13 && e.ctrlKey) {
                // Ctrl + Enter，发送消息
                this.publishCourseComment(this.content, this.courseId);
            }
        },
        handleKeyDownReplyComment(e, courseComment, courseOneComment) {
            if (e.keyCode === 13 && !e.ctrlKey) {
                // Enter，换行
                e.preventDefault();
                courseComment.replyContent += '\n';
            } else if (e.keyCode === 13 && e.ctrlKey) {
                e.preventDefault();
                if (courseComment.replyContent == "") {
                    this.$modal.msgError("您还未输入内容")
                    return;
                }
                this.replyCourseComment(courseComment.senderId, courseComment.replyContent, courseOneComment.id, courseComment.courseId);
                
            }
            
        },
    },
};
</script>

<style scoped>
.more {
    position: relative; 
    cursor: pointer; 
    z-index: 10000;
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
    left: 62%;
    z-index: 100002;
    font-size: 12px;
    opacity: 0.5;
}
.button-comment {
    position: absolute;
    width: 80px;
    height: 30px;
    text-align: center;
    z-index: 9999;
    left: 88%;
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
    
}
.two-comment-img {
    width: 25px;
    height: 25px;
    cursor: pointer;
    border-radius: 50%;
}
.two-comment {
    background-color: #eef2f5;
    padding: 10px;
    
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
    left: 89%;
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
    width: 100px;
    text-align: center;
    left: -50px;
    padding: 5px;
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