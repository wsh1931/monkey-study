<template>
    <div class="MonkeyWebResourceComment-container">
        <div style="position: relative;">
            <span class="reply-total">1&nbsp;条回复</span>
            <span style="position: absolute; right: 0;">
                <span 
                class="iconfont icon-zhuanhuan no-reply-comment">&nbsp;切换为未回复评论</span>
                <span class="time-comment" v-if="timeSort == '0'">
                    <span class="iconfont icon-zhuanhuan">&nbsp;切换为时间降序</span> 
                </span>
                <span class="time-comment" v-if="timeSort == '1'">
                        <span class="iconfont icon-zhuanhuan">&nbsp;切换为时间升序</span> 
                </span>
                <span class="time-comment" v-if="timeSort == '2'">
                    <span class="iconfont icon-zhuanhuan">&nbsp;切换为默认排序</span> 
                </span>
            </span>
        </div>

        <div style="position: relative; margin-top: 10px;" @click="isShowInputComment = true">
            <el-row class="open-publish-comment" style="position: relative;" v-if="!isShowInputComment">
                <div>
                    期待您的优质评论
                    <el-button class="button-comment" type="primary" size="small">发表评论</el-button>
                </div>
            </el-row>
        </div>

        <el-row v-if="isShowInputComment">
            <mavon-editor
            v-model="publishCommentContent" 
            :toolbars="toolbars"
            :translate="true"
            defaultOpen="edit"
            placeholder="期待您精彩的评论"
            style="min-height: 200px; z-index: 1;"
            :navigation="false"
            :subfield="false"
            :scrollStyle="true"
            @keyup.native="handleKeyDownComment(publishCommentContent, $event)"
            ></mavon-editor>
            <el-button 
            type="primary" 
            size="small" 
            class="publish-comment-button" @click="publishCommentMethod(publishCommentContent)">发表评论</el-button>
            <el-row class="publish-comment-indicate">按下Enter换行，Ctrl+Enter发表内容</el-row>
        </el-row>

        <div style="margin-top: 20px; font-size: 14px;">
            <el-row>
                <el-col :span="1">
                    <img class="one-comment-img" src="https://ts4.cn.mm.bing.net/th?id=OIP-C.TkprQfoHWf1_EgBAcQGO3gHaE2&w=308&h=202&c=8&rs=1&qlt=90&o=6&pid=3.1&rm=2" alt="">
                </el-col>
                <el-col :span="23">
                    <div 
                    @mouseover="isMoreHover = '1'" 
                    @mouseleave="isMoreHover = '0'">
                        <span class="one-comment-username">吴思豪</span>
                        <span class="one-comment-replyTime">{{ getTimeFormat("2023-10-15 17:18:58") }}</span>
                        <span class="curation-comment">
                                <span class="iconfont icon-jingxuanyoupin curation-comment-icon"></span>
                                <span class="curation-comment-font">精选</span>
                        </span>
                        <span class="top-comment">
                            <span class="el-icon-caret-top top-comment-icon"></span>
                                <span class="top-comment-font">置顶</span>
                        </span>
                        <span class="one-right">
                            <span 
                            v-if="isMoreHover == '1'"
                            @mouseover="isMoreHoverDetail = '1'" 
                            @mouseleave="isMoreHoverDetail = '0'"
                            class="el-icon-more-outline one-more">
                                <div class="more-class" v-if="isMoreHoverDetail == '1'">
                                    <div 
                                    @click="curationComment(oneComment)"
                                    >精选</div>
                                    <div 
                                    @click="cancelCurationComment(oneComment)"
                                    >取消精选</div>
                                    <div>举报</div>
                                    <div>删除</div>
                                    <div 
                                    @click="topComment(oneComment)" 
                                    >置顶</div>
                                    <div 
                                    @click="cancelTopComment(oneComment)" 
                                    >取消置顶</div>
                                </div>
                            </span>
                            <span @click="isSelected = '1'" v-if="isSelected == '0'" class="iconfont icon-pinglun one-reply">&nbsp;回复</span>
                            <span @click="isSelected = '0'" v-else class="iconfont icon-pinglun one-reply">&nbsp;收起</span>
                            <span 
                            class="iconfont icon-dianzan one-like" ></span>
                            <span 
                            class="iconfont icon-dianzan cancel-like" ></span>
                        </span>
                    </div>

                    <div>
                        <vue-markdown 
                            class="comment-content"
                            :source="content" 
                            :highlight="true"
                            :html="true"
                            :xhtmlOut="true">
                        </vue-markdown>
                    </div>

                    <!-- 一级回复输入框 -->
                    <el-input 
                        class="one-input"
                        v-if="isSelected == '1'"
                        :show-word-limit="true"
                        minlength="1"
                        maxlength="255"
                        type="textarea"
                        :autosize="{ minRows: 5, maxRows: 5}"
                        max="100"
                        style="margin-bottom: 10px;"
                        v-model="content"
                        @keyup.native="keyDownReplyComment()"
                        :placeholder="'按下Enter换行，Ctrl+Enter发表内容'">
                    </el-input>

                    <!-- 二级评论 -->
                    <div class="two-comment"
                    @mouseover="isMoreHover = '1'" 
                    @mouseleave="isMoreHover = '0'">
                        <div>
                            <img  class="two-comment-img" src="https://ts4.cn.mm.bing.net/th?id=OIP-C.WkkIXkr-QmHImU57KtkYQgHaEo&w=316&h=197&c=8&rs=1&qlt=90&o=6&pid=3.1&rm=2" alt="">
                            <span class="one-comment-username">吴思豪</span>
                            <span class="one-comment-replyTime">{{ getTimeFormat('2023-10-01 15:43:49') }}</span>  
                            <span class="curation-comment">
                                <span class="iconfont icon-jingxuanyoupin curation-comment-icon"></span>
                                <span class="curation-comment-font">精选</span>
                            </span>
                            <span class="one-right">
                                <span 
                                v-if="isMoreHover == '1'"
                                @mouseover="isMoreHoverDetail = '1'" 
                                @mouseleave="isMoreHoverDetail = '0'"
                                class="el-icon-more-outline one-more">
                                    <div class="more-class" v-if="isMoreHoverDetail == '1'">
                                        <div 
                                        @click="curationComment()">精选</div>
                                        <div
                                        @click="cancelCurationComment()" >取消精选</div>
                                        <div>举报</div>
                                        <div
                                        @click="deleteComment()">删除</div>
                                    </div>
                                </span>
                                <span @click="isSelected = '1'" v-if="!isShowTwoReply" class="iconfont icon-pinglun one-reply">&nbsp;回复</span>
                                <span @click="isSelected = '0'" v-else class="iconfont icon-pinglun one-reply">&nbsp;收起</span>
                                <span 
                                class="iconfont icon-dianzan one-like" 
                                @click="commentLike()"></span>
                                <span 
                                class="iconfont icon-dianzan cancel-like" 
                                @click="cancelCommentLike()"></span>
                                </span>
                        </div>
                        <div class="reply-people">{{ getTimeFormat('2023-10-01 15:43:49') }}</div>
                        <!-- 二级评论内容 -->
                        <div>
                            <vue-markdown 
                            class="comment-content"
                            :source="content" 
                            :highlight="true"
                            :html="true"
                            :xhtmlOut="true">
                        </vue-markdown>
                        </div>
                        
                        <!-- 二级输入框 -->
                        <el-input 
                        class="one-input"
                        v-if="isSelected == '1'"
                        :show-word-limit="true"
                        minlength="1"
                        maxlength="255"
                        type="textarea"
                        :autosize="{ minRows: 5, maxRows: 5}"
                        max="100"
                        v-model="replyContent"
                        @keyup.native="keyDownReplyComment()"
                        :placeholder="'  按下Enter换行，Ctrl+Enter发表内容'">
                        </el-input>
                    </div>
                </el-col>
            </el-row>
        </div>
    </div>
</template>

<script>
import { getTimeFormat } from '@/assets/js/DateMethod';
import { mavonEditor } from 'mavon-editor'
import VueMarkdown from 'vue-markdown';
import 'mavon-editor/dist/css/index.css'
export default {
    name: 'MonkeyWebResourceComment',
    components: {
        mavonEditor,
        VueMarkdown
    },
    data() {
        return {
            timeSort: '0',
            isShowInputComment: false,
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
            publishCommentContent: "",
            isMoreHover: "0",
            isSelected: "0",
            isMoreHoverDetail: "0",
            content: "来了",
        };
    },

    mounted() {
        
    },

    methods: {
        getTimeFormat(time) {
            return getTimeFormat(time);
        }
    },
};
</script>

<style scoped>
.reply-people {
    margin-top: 10px;
    color: #409EFF;
}
.reply-people::before {
    content: "@";
}
.one-right {
    float: right;
    font-size: 20px;
}
.curation-comment-font {
    color: #FF4D4D;
    font-size: 12px;
    vertical-align: middle;
}
.curation-comment-icon {
    color: #FC5531;
    font-size: 12px;
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
.one-comment-replyTime {
    cursor: pointer;
    font-weight: 400;
    color: #777888;
    margin-left: 10px;
    vertical-align: middle;
}
.one-comment-username {
    cursor: pointer;
    font-weight: 400;
    color: #777888;
    vertical-align: middle;
}
.one-comment-username:hover {
    opacity: 0.5;
    cursor: pointer;
}
.two-comment-img {
    width: 25px;
    height: 25px;
    cursor: pointer;
    border-radius: 50%;
    vertical-align: middle;
    margin-right: 10px;
}
.two-comment-img:hover {
    filter: brightness(1.5);
}
.two-comment {
    background-color: #eef2f5;
    padding: 10px;
    vertical-align: middle;
}
@keyframes slide-out {
    0% {
        opacity: 0;
    }
    100% {
        opacity: 1;
    }
}
.one-input {
    box-shadow: 0 0 10px 0 rgba(0, 0, 0, 0.5);
    animation: slide-out 0.2s linear
}
.comment-content {
    font-size: 15px;
    font-weight: 540;
}
.cancel-like {
    vertical-align: middle;
    font-size: 20px;
    cursor: pointer;
    color: #409EFF;
}
.one-like {
    vertical-align: middle;
    font-size: 20px;
}
.one-like:hover {
    cursor: pointer;
    color: #409EFF;
}
.one-reply {
    margin-right: 20px;
    font-size: 17px;
    vertical-align: middle;
}
.one-reply:hover {
    cursor: pointer;
    opacity: 0.5;
}
.more-class > :nth-child(n + 2) {
    margin-top: 10px;
}
.more-class > :nth-child(n) {
    font-size: 16px;
}
.more-class > :nth-child(n):hover {
    cursor: pointer;
    color: #409EFF;
}
.more-class {
    position: absolute;
    padding: 10px;
    width: 70px;
    left: -32px;
    box-shadow: 0 0 10px 2px rgba(0, 0, 0, 0.5);
    text-align: center;
    animation: slide-out 0.3s linear;
}
.one-more {
    position: relative;
    font-size: 24px;
    cursor: pointer;
    margin-right: 20px;
    vertical-align: middle;
    z-index: 8;
}
.one-right {
    float: right;
    font-size: 20px;
}
.top-comment-font {
    color: #409EFF;
    font-size: 12px;
    vertical-align: middle;
}
.top-comment-icon {
    color: #409EFF;
    font-size: 12px;
}

.top-comment {
    background-color: #D9ECFF;
    text-align: center;
    margin-left: 10px;
    width: 50px;
    height: 20px;
    display: inline-block;
    line-height: 20px;
}
.curation-comment-font {
    color: #FF4D4D;
    font-size: 12px;
    vertical-align: middle;
}
.curation-comment-icon {
    color: #FC5531;
    font-size: 12px;
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
.one-comment-replyTime {
    cursor: pointer;
    font-weight: 400;
    color: #777888;
    margin-left: 10px;
    vertical-align: middle;
}
.one-comment-username {
    cursor: pointer;
    font-weight: 400;
    color: #777888;
    vertical-align: middle;
}
.one-comment-username:hover {
    opacity: 0.5;
    cursor: pointer;
}
.one-comment-img {
    width: 30px;
    height: 30px;
    border-radius: 50%;
    cursor: pointer;
    vertical-align: middle;
    transition: 0.2s linear all;
}
.one-comment-img:hover {
    filter: brightness(1.5);
}
.publish-comment-indicate {
    position: absolute;
    top: 175px;
    left: 66%;
    z-index: 1;
    font-size: 12px;
    opacity: 0.5;
}
.publish-comment-button {
    position: absolute;
    border-radius: 20px;
    top: 168px;
    text-align: center;
    left: 90%;
    z-index: 2;
    width: 76px;
    height: 28px;
    line-height: 10px;
    
}
.open-publish-comment {
    position: relative;
    width: 100%;
    height: 45px;
    background-color: #F7F7FC;
    padding: 10px;
    cursor: pointer;
    font-size: 15px;
    line-height: 25px;
    color: #7f8389;
    border-radius: 10px;
}
.button-comment {
    position: absolute;
    width: 80px;
    height: 30px;
    text-align: center;
    z-index: 1;
    left: 90%;
    bottom: 7px;
    border-radius: 20px;
    background-color: #9a9d9d;
    border: none;
}
.time-comment:hover {
    cursor: pointer;
    color: #409EFF;
}
.no-reply-comment {
    margin-right: 10px;
}
.no-reply-comment:hover {
    cursor: pointer;
    color: #409EFF;
}
.reply-total {
    font-size: 16px;
    font-weight: bolder;
}
.MonkeyWebResourceComment-container {
    margin-top: 20px;
    padding: 20px;
    background-color: #fff;
    height: 2000px;
    box-shadow: 0 0 5px 0 rgba(0, 0, 0, 0.1);
}
</style>