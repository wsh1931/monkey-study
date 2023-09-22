<template>
    <div class="MonkeyWebCommunityArticleComment-container">
        <div style="position: relative;">
            <span class="reply-total">2 条回复</span>
            <span style="position: absolute; right: 0;">
                <span class="iconfont icon-zhuanhuan no-reply-comment">&nbsp;切换为未回复评论</span>
                <span class="time-comment" v-if="timeSort == '0'" @click="getDownOrUpgradeCommunityArticleComment()">
                    <span class="iconfont icon-zhuanhuan">&nbsp;切换为时间降序</span> 
                </span>
                <span class="time-comment" v-if="timeSort == '1'" @click="getDownOrUpgradeCommunityArticleComment()">
                        <span class="iconfont icon-zhuanhuan">&nbsp;切换为时间升序</span> 
                </span>
                <span class="time-comment" v-if="timeSort == '2'" @click="getCommunityArticleCommentList()">
                    <span class="iconfont icon-zhuanhuan">&nbsp;切换为默认排序</span> 
                </span>
            </span>
        </div>

        <div style="position: relative; margin-top: 10px;" @click="openPublishArticle()">
            <el-row class="open-publish-comment" style="position: relative;" v-if="!isShowInputComment">
                <div @click="publishComment = true">
                    期待您的优质评论
                    <el-button class="button-comment" type="primary" size="small">发表评论</el-button>
                </div>
            </el-row>
        </div>

        <el-row v-if="isShowInputComment">
            <mavon-editor
            v-model="content" 
            :toolbars="toolbars"
            :translate="true"
            defaultOpen="edit"
            placeholder="期待您精彩的评论"
            style="min-height: 200px; z-index: 1;"
            :navigation="false"
            :subfield="false"
            :scrollStyl="true"
            @keydown.native="handleKeyDown($event)"
            ></mavon-editor>
            <el-button 
            type="primary" 
            size="small" 
            class="publish-comment-button" >发表评论</el-button>
            <el-row class="publish-comment-indicate">按下Enter换行，Ctrl+Enter发表内容</el-row>
        </el-row>

        <!-- 一级评论内容 -->
        <div style="margin-top: 20px;">
            <el-row>
                <el-col :span="1">
                    <img class="one-comment-img" src="https://ts4.cn.mm.bing.net/th?id=OIP-C.Fx6P4aoVRKGGircQ7tgtggHaEo&w=316&h=197&c=8&rs=1&qlt=90&o=6&dpr=1.3&pid=3.1&rm=2" alt="">
                </el-col>
                <el-col :span="23">
                    <div 
                    @mouseover="isHoverMore = true" 
                    @mouseleave="isHoverMore = false">
                        <span class="one-comment-username">啥也不会hh</span>
                        <span class="one-comment-replyTime">2023-09-13 17:47:51</span>
                        <span class="curation-comment">
                            <span class="iconfont icon-jingxuanyoupin curation-comment-icon"></span>
                            <span class="curation-comment-font">精选</span>
                        </span>
                        <span class="one-right">
                            <span 
                            v-if="isHoverMore"
                            @mouseover="isHoverOneComment = true" 
                            @mouseleave="isHoverOneComment = false"
                            class="el-icon-more-outline one-more">
                                <div class="more-class" v-if="isHoverOneComment">
                                    <div>精选</div>
                                    <div>举报</div>
                                    <div>删除</div>
                                </div>
                            </span>
                            <span @click="isShowOneReply = true" v-if="!isShowOneReply" class="iconfont icon-pinglun one-reply">&nbsp;回复</span>
                            <span @click="isShowOneReply = false" v-else class="iconfont icon-pinglun one-reply">&nbsp;收起</span>
                            <span class="iconfont icon-dianzan one-like"></span>
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
                        v-if="isShowOneReply"
                        :show-word-limit="true"
                        minlength="1"
                        maxlength="255"
                        type="textarea"
                        :autosize="{ minRows: 5, maxRows: 5}"
                        max="100"
                        v-model="content"
                        :placeholder="'  按下Enter换行，Ctrl+Enter发表内容'">
                    </el-input>

                    <div class="two-comment"
                    @mouseover="isHoverTwoMore = true" 
                    @mouseleave="isHoverTwoMore = false">
                        <div>
                            <img  class="two-comment-img" src="https://tse1-mm.cn.bing.net/th/id/OIP-C.Zte3ljd4g6kqrWWyg-8fhAHaEo?w=281&h=180&c=7&r=0&o=5&dpr=1.3&pid=1.7" alt="">
                            <span class="one-comment-username">啥也不会hh</span>
                            <span class="one-comment-replyTime">2023-09-13 17:47:51</span>   
                            <span class="one-right">
                                <span 
                                v-if="isHoverTwoMore"
                                @mouseover="isHoverTwoComment = true" 
                                @mouseleave="isHoverTwoComment = false"
                                class="el-icon-more-outline one-more">
                                    <div class="more-class" v-if="isHoverTwoComment">
                                        <div>精选</div>
                                        <div>举报</div>
                                        <div>删除</div>
                                    </div>
                                </span>
                                <span @click="isShowTwoReply = true" v-if="!isShowTwoReply" class="iconfont icon-pinglun one-reply">&nbsp;回复</span>
                                <span @click="isShowTwoReply = false" v-else class="iconfont icon-pinglun one-reply">&nbsp;收起</span>
                                <span class="iconfont icon-dianzan one-like"></span>
                            </span>
                        </div>
                        <div class="reply-people">吴思豪</div>
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
                        v-if="isShowTwoReply"
                        :show-word-limit="true"
                        minlength="1"
                        maxlength="255"
                        type="textarea"
                        :autosize="{ minRows: 5, maxRows: 5}"
                        max="100"
                        v-model="content"
                        :placeholder="'  按下Enter换行，Ctrl+Enter发表内容'">
                        </el-input>
                    </div>
                </el-col>
            </el-row>
        </div>
    </div>
</template>

<script>
import { mavonEditor } from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'
import VueMarkdown from 'vue-markdown';
export default {
    name: 'MonkeyWebCommunityArticleComment',
    components: {
        mavonEditor,
        VueMarkdown
    },
    data() {
        return {
            isShowTwoReply: false,
            isHoverTwoComment: false,
            isHoverTwoMore: false,
            isShowOneReply: false,
            content: "吴思豪",
            // 是否显示更多
            isHoverMore: false,
            // 是否显示一级评论
            isHoverOneComment: false,
            // 0为默认排序, 1为课程列表按降序排序, 2为升序
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
        };
    },

    mounted() {
        
    },

    methods: {
        // 打开发布文章按钮
        openPublishArticle() {
            this.isShowInputComment = true
        },
        getCommunityArticleCommentList() {
            this.timeSort = (this.timeSort + 1) % 3;
        },
        getDownOrUpgradeCommunityArticleComment() {
            this.timeSort = (this.timeSort + 1) % 3;
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
.two-comment-img {
    width: 25px;
    height: 25px;
    cursor: pointer;
    border-radius: 50%;
    vertical-align: middle;
    margin-right: 10px;
}
.two-comment-img:hover {
    opacity: 0.5;
}
.two-comment {
    background-color: #eef2f5;
    padding: 10px;
    vertical-align: middle;
}
.two-comment {
    background-color: #eef2f5;
    padding: 10px;
    vertical-align: middle;
}
.one-input {
    animation: slide-out 0.2s linear
}
@keyframes slide-out {
    0% {
        opacity: 0;
    }
    100% {
        opacity: 1;
    }
}
.comment-content {
    font-size: 15px;
    font-weight: 540;
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
.one-reply {
    margin-right: 20px;
    font-size: 17px;
    vertical-align: middle;
}
.one-reply:hover {
    cursor: pointer;
    opacity: 0.5;
}
.one-more {
    position: relative;
    font-size: 24px;
    cursor: pointer;
    margin-right: 20px;
    vertical-align: middle;
}
.one-like {
    vertical-align: middle;
    font-size: 20px;
}
.one-like:hover {
    cursor: pointer;
    color: #409EFF;
}
.one-right {
    float: right;
    font-size: 20px;
}
.curation-comment-font {
    color: #FF4D4D;
    font-size: 12px;
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
.one-comment-replyTime {
    cursor: pointer;
    font-weight: 400;
    color: #777888;
    margin-left: 10px;
    vertical-align: middle;
}
.one-comment-img {
    width: 30px;
    height: 30px;
    border-radius: 50%;
    cursor: pointer;
    vertical-align: middle;
}
.one-comment-img:hover {
    opacity: 0.5;
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
.publish-comment-button {
    position: absolute;
    border-radius: 20px;
    top: 168px;
    text-align: center;
    left: 89%;
    z-index: 2;
    width: 76px;
    height: 28px;
    line-height: 10px;
    
}
.publish-comment-indicate {
    position: absolute;
    top: 175px;
    left: 62%;
    z-index: 1;
    font-size: 12px;
    opacity: 0.5;
}
.background-content {
    position: absolute;
    background-color: #409EFF;
    width: 5px;
    height: 20px;
    top: 33px;
    left: 18px;
}
.button-comment {
    position: absolute;
    width: 80px;
    height: 30px;
    text-align: center;
    z-index: 1;
    left: 88%;
    bottom: 7px;
    border-radius: 20px;
    background-color: #9a9d9d;
    border: none;
}
.reply-total {
    font-size: 16px;
    font-weight: bolder;
}
.time-comment {
    margin-left: 10px;
}
.time-comment:hover {
    cursor: pointer;
    color: #409EFF;
}
.no-reply-comment:hover {
    cursor: pointer;
    color: #409EFF;
}
.MonkeyWebCommunityArticleComment-container {
    padding: 20px;
    background-color: #fff;
    height: 100vh;
}
</style>