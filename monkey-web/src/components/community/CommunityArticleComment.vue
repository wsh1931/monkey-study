<template>
    <div class="MonkeyWebCommunityArticleComment-container">
        <div style="position: relative;">
            <span class="reply-total">{{ commentCount }} 条回复</span>
            <span style="position: absolute; right: 0;">
                <span 
                :class="['iconfont icon-zhuanhuan no-reply-comment', {selected: commentStatus == '3'}]"
                @click="queryNotReplyCommentList(communityArticleId)">&nbsp;切换为未回复评论</span>
                <span class="time-comment" v-if="timeSort == '0'" @click="getDownOrUpgradeCommunityArticleComment(communityArticleId)">
                    <span class="iconfont icon-zhuanhuan">&nbsp;切换为时间降序</span> 
                </span>
                <span class="time-comment" v-if="timeSort == '1'" @click="getDownOrUpgradeCommunityArticleComment(communityArticleId)">
                        <span :class="['iconfont icon-zhuanhuan', {selected: commentStatus == '2'}]">&nbsp;切换为时间升序</span> 
                </span>
                <span class="time-comment" v-if="timeSort == '2'" @click="getDownOrUpgradeCommunityArticleComment(communityArticleId)">
                    <span :class="['iconfont icon-zhuanhuan', {selected: commentStatus == '1'}]">&nbsp;切换为默认排序</span> 
                </span>
            </span>
        </div>

        <div style="position: relative; margin-top: 10px;" @click="openPublishArticle()">
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

        <!-- 一级评论内容 -->
        <div style="margin-top: 20px;">
            <el-row v-for="(oneComment, index) in commentList" :key="oneComment.id" style="margin-top: 10px;">
                <el-col :span="1">
                    <img class="one-comment-img" :src="oneComment.senderHeadImg" alt="">
                </el-col>
                <el-col :span="23">
                    <div 
                    @mouseover="oneComment.isMoreHover = '1'" 
                    @mouseleave="oneComment.isMoreHover = '0'">
                        <span class="one-comment-username">{{ oneComment.senderUsername }}</span>
                        <span class="one-comment-replyTime">{{ getTimeFormat(oneComment.createTime) }}</span>
                        <span class="curation-comment" v-if="oneComment.isCuration == '1'">
                                <span class="iconfont icon-jingxuanyoupin curation-comment-icon"></span>
                                <span class="curation-comment-font">精选</span>
                        </span>
                        <span class="top-comment" v-if="oneComment.isTop == '1'">
                            <span class="el-icon-caret-top top-comment-icon"></span>
                                <span class="top-comment-font">置顶</span>
                        </span>
                        <span class="one-right">
                            <span 
                            v-if="oneComment.isMoreHover == '1'"
                            @mouseover="oneComment.isMoreHoverDetail = '1'" 
                            @mouseleave="oneComment.isMoreHoverDetail = '0'"
                            class="el-icon-more-outline one-more">
                                <div class="more-class" v-if="oneComment.isMoreHoverDetail == '1'">
                                    <div 
                                    @click="curationComment(oneComment)"
                                    v-if="oneComment.isCuration == '0' && isManger == '1'"
                                    >精选</div>
                                    <div 
                                    @click="cancelCurationComment(oneComment)"
                                    v-if="oneComment.isCuration == '1' && isManger == '1'">取消精选</div>
                                    <div>举报</div>
                                    <div v-if="isAuthor == '1' || isManger == '1'" @click="deleteComment(oneComment, 1, index)">删除</div>
                                    <div 
                                    @click="topComment(oneComment)" 
                                    v-if="oneComment.isTop == '0' && isManger == '1'">置顶</div>
                                    <div 
                                    @click="cancelTopComment(oneComment)" 
                                    v-if="oneComment.isTop == '1' && isManger == '1'">取消置顶</div>
                                </div>
                            </span>
                            <span @click="oneComment.isSelected = '1'" v-if="oneComment.isSelected == '0'" class="iconfont icon-pinglun one-reply">&nbsp;回复</span>
                            <span @click="oneComment.isSelected = '0'" v-else class="iconfont icon-pinglun one-reply">&nbsp;收起</span>
                            <span 
                            v-if="oneComment.isLike == '0'"
                            class="iconfont icon-dianzan one-like" 
                            @click="commentLike(oneComment)"></span>
                            <span 
                            v-if="oneComment.isLike == '1'"
                            class="iconfont icon-dianzan cancel-like" 
                            @click="cancelCommentLike(oneComment)"></span>
                        </span>
                    </div>

                    <div>
                        <vue-markdown 
                            class="comment-content"
                            :source="oneComment.content" 
                            :highlight="true"
                            :html="true"
                            :xhtmlOut="true">
                        </vue-markdown>
                    </div>

                    <!-- 一级回复输入框 -->
                    <el-input 
                        class="one-input"
                        v-if="oneComment.isSelected == '1'"
                        :show-word-limit="true"
                        minlength="1"
                        maxlength="255"
                        type="textarea"
                        :autosize="{ minRows: 5, maxRows: 5}"
                        max="100"
                        style="margin-bottom: 10px;"
                        v-model="oneComment.replyContent"
                        @keyup.native="keyDownReplyComment(oneComment, $event, oneComment.id, 1)"
                        :placeholder="'按下Enter换行，Ctrl+Enter发表内容'">
                    </el-input>

                    <!-- 二级评论 -->
                    <div class="two-comment"
                    v-for="twoComment in oneComment.communityArticleCommentList"
                    @mouseover="twoComment.isMoreHover = '1'" 
                    @mouseleave="twoComment.isMoreHover = '0'">
                        <div>
                            <img  class="two-comment-img" :src="twoComment.replyHeadImg" alt="">
                            <span class="one-comment-username">{{ twoComment.replyUsername }}</span>
                            <span class="one-comment-replyTime">{{ getTimeFormat(twoComment.createTime) }}</span>  
                            <span class="curation-comment" v-if="twoComment.isCuration == '1'">
                                <span class="iconfont icon-jingxuanyoupin curation-comment-icon"></span>
                                <span class="curation-comment-font">精选</span>
                            </span>
                            <span class="one-right">
                                <span 
                                v-if="twoComment.isMoreHover == '1'"
                                @mouseover="twoComment.isMoreHoverDetail = '1'" 
                                @mouseleave="twoComment.isMoreHoverDetail = '0'"
                                class="el-icon-more-outline one-more">
                                    <div class="more-class" v-if="twoComment.isMoreHoverDetail == '1'">
                                        <div 
                                        @click="curationComment(twoComment)"
                                        v-if="twoComment.isCuration == '0' && isManger == '1'">精选</div>
                                        <div
                                        @click="cancelCurationComment(twoComment)" 
                                        v-if="twoComment.isCuration == '1' && isManger == '1'">取消精选</div>
                                        <div>举报</div>
                                        <div
                                        @click="deleteComment(twoComment, 2, index, oneComment)"
                                        v-if="isAuthor == '1' || isManger == '1'">删除</div>
                                    </div>
                                </span>
                                <span 
                                @click="twoComment.isSelected = '1'" 
                                v-if="twoComment.isSelected == '0'" 
                                class="iconfont icon-pinglun one-reply">&nbsp;回复</span>
                                <span 
                                @click="twoComment.isSelected = '0'" 
                                v-if="twoComment.isSelected == '1'" 
                                class="iconfont icon-pinglun one-reply">&nbsp;收起</span>
                                <span 
                                v-if="twoComment.isLike == '0'"
                                class="iconfont icon-dianzan one-like" 
                                @click="commentLike(twoComment)"></span>
                                <span 
                                v-if="twoComment.isLike == '1'"
                                class="iconfont icon-dianzan cancel-like" 
                                @click="cancelCommentLike(twoComment)"></span>
                                </span>
                        </div>
                        <div class="reply-people">{{ twoComment.senderUsername }}</div>
                        <!-- 二级评论内容 -->
                        <div>
                            <vue-markdown 
                            class="comment-content"
                            :source="twoComment.content" 
                            :highlight="true"
                            :html="true"
                            :xhtmlOut="true">
                        </vue-markdown>
                        </div>
                        
                        <!-- 二级输入框 -->
                        <el-input 
                        class="one-input"
                        v-if="twoComment.isSelected == '1'"
                        :show-word-limit="true"
                        minlength="1"
                        maxlength="255"
                        type="textarea"
                        :autosize="{ minRows: 5, maxRows: 5}"
                        max="100"
                        v-model="twoComment.replyContent"
                        @keyup.native="keyDownReplyComment(twoComment, $event, twoComment.parentId, 2)"
                        :placeholder="'按下Enter换行，Ctrl+Enter发表内容'">
                        </el-input>
                    </div>
                </el-col>
            </el-row>

            <PagiNation
            style="text-align: right; margin-top: 20px;"
            :totals="totals"
            :currentPage="currentPage" 
            :pageSize="pageSize" 
            @handleCurrentChange = "handleCurrentChange"
            @handleSizeChange="handleSizeChange"/>
        </div>
    </div>
</template>

<script>
import { getTimeFormat } from '@/assets/js/DateMethod';
import PagiNation from '../pagination/PagiNation.vue';
import $ from 'jquery'
import store from '@/store';
import { mavonEditor } from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'
import VueMarkdown from 'vue-markdown';
export default {
    name: 'MonkeyWebCommunityArticleComment',
    props: ['isAuthor', 'isManger'],
    components: {
        mavonEditor,
        VueMarkdown,
        PagiNation
    },
    data() {
        return {
            communityId: "社区id",
            // 评论状态，0表示默认排序，1表示时间升序，2表示时间降序，3表示未回复评论
            commentStatus: 0,
            // 评论分页
            currentPage: 1,
            pageSize: 10,
            totals: 0,
            // 是否按下键盘
            isKeyDown: false,
            // 发表评论内容
            publishCommentContent: "",
            // 判断当前登录用户是否为作者
            isAuthor: "0",
            // 评论集合
            commentList: [],
            // 评论总数
            commentCount: 0,
            // 社区文章id
            communityArticleId: "",
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
            timeSort: -1,
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
            // 社区评论地址
            communityCommentUrl: "http://localhost:80/monkey-community/comment",
        };
    },

    created() {
        this.communityId = this.$route.params.communityId;
        this.communityArticleId = this.$route.params.communityArticleId;
        this.queryDefaultCommentList(this.communityArticleId);
    },

    methods: {
        getTimeFormat(time) {
            return getTimeFormat(time);
        },
        // 评论点赞
        commentLike(comment) {
            const vue = this;
            $.ajax({
                url: vue.communityCommentUrl + "/commentLike",
                type: "post",
                data: {
                    commentId: comment.id,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        comment.isLike = '1';
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 取消评论点赞
        cancelCommentLike(comment) {
            const vue = this;
            $.ajax({
                url: vue.communityCommentUrl + "/cancelCommentLike",
                type: "post",
                data: {
                    commentId: comment.id,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        comment.isLike = '0';
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 按下回复键盘
        keyDownReplyComment(comment, event, parentId, type) {
            if (event.ctrlKey && event.keyCode == '13') {
                const replyCount = comment.replyContent;
                if (replyCount == null || replyCount == "") {
                    this.$modal.msgWarning("回复内容不能为空")
                    return;
                }
                if (replyCount.length >= 255) {
                    this.$modal.msgWarning("回复内容不能超过 255 个字符")
                    return;
                }

                if (comment.isKeyDown == '1') {
                    this.$modal.msgWarning("您的操作过于频繁，请稍后再试")
                    return;
                }

                comment.isKeyDown = '1';
                this.publishCommentReply(comment, parentId, type);
                
            }
        },
        // 发表评论回复 type = 1代表一级评论回复，type = 2代表子评论回复
        publishCommentReply(comment, parentId, type) {
            const vue = this;
            $.ajax({
                url: vue.communityCommentUrl + "/publishCommentReply",
                type: "post",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    senderId: (type == 1) ? comment.senderId :comment.replyId,
                    parentId,
                    replyContent: comment.replyContent,
                    communityArticleId: vue.communityArticleId,
                    communityId: vue.communityId,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        comment.replyContent = '';
                        comment.isKeyDown = '0';
                        comment.isSelected = '0';
                        vue.queryDefaultCommentList(vue.communityArticleId);
                        vue.$modal.msgSuccess(response.msg)
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        handleSizeChange(val) {
            this.pageSize = val;
            if (this.commentStatus == '0') {
                // 默认排序
                this.queryDefaultCommentList(this.communityArticleId);
            } else if (this.commentStatus == '1') {
                // 按时间升序排序
                this.queryTimeUpgradeComment(this.communityArticleId);
            } else if (this.commentStatus == '2') {
                // 按时间降序排序
                this.queryTimeDownSortComment(this.communityArticleId);
            } else if (this.commentStatus == '3') {
                // 未回复排序
                this.queryNotReplyCommentList(this.communityArticleId);
            }
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            if (this.commentStatus == '0') {
                // 默认排序
                this.queryDefaultCommentList(this.communityArticleId);
            } else if (this.commentStatus == '1') {
                // 按时间升序排序
                this.queryTimeUpgradeComment(this.communityArticleId);
            } else if (this.commentStatus == '2') {
                // 按时间降序排序
                this.queryTimeDownSortComment(this.communityArticleId);
            } else if (this.commentStatus == '3') {
                // 未回复排序
                this.queryNotReplyCommentList(this.communityArticleId);
            }
        },
        handleKeyDownComment(commentContent, event) {
            if (event.ctrlKey && event.keyCode == '13') {
                if (commentContent == null || commentContent == "") {
                    this.$modal.msgWarning("输入的内容不能为空!");
                    return;
                }
                if (commentContent.length >= 255) {
                    this.$modal.msgWarning("输入的字符不能超过255!")
                }
                if (this.isKeyDown) {
                    this.$modal.msgWarning("您的操作太频繁，请稍后再试!")
                    return;
                }
                this.isKeyDown = true;
                this.publishCommentMethod(commentContent, this.communityArticleId);
            }
        },
        // 发表社区文章评论
        publishCommentMethod(commentContent, communityArticleId) {
            const vue = this;
            $.ajax({
                url: vue.communityCommentUrl + "/publishComment",
                type: "post",
                data: {
                    commentContent,
                    communityArticleId,
                    communityId: vue.communityId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.publishCommentContent = "";
                        vue.isKeyDown = false;
                        vue.queryDefaultCommentList(communityArticleId);
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 精选评论
        curationComment(comment) {
            const vue = this;
            $.ajax({
                url: vue.communityCommentUrl + "/curationComment",
                type: "put",
                data: {
                    commentId: comment.id,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        comment.isCuration = '1';
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 取消精选评论
        cancelCurationComment(comment) {
            const vue = this;
            $.ajax({
                url: vue.communityCommentUrl + "/cancelCurationComment",
                type: "put",
                data: {
                    commentId: comment.id,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        comment.isCuration = '0';
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 置顶评论
        topComment(comment) {
            const vue = this;
            $.ajax({
                url: vue.communityCommentUrl + "/topComment",
                type: "put",
                data: {
                    commentId: comment.id,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        comment.isTop = '1';
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 取消置顶评论
        cancelTopComment(comment) {
            const vue = this;
            $.ajax({
                url: vue.communityCommentUrl + "/cancelTopComment",
                type: "put",
                data: {
                    commentId: comment.id,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        comment.isTop = '0';
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 删除评论
        deleteComment(comment, type, index, oneComment) {
            const vue = this;
            $.ajax({
                url: vue.communityCommentUrl + "/deleteComment",
                type: "delete",
                data: {
                    commentId: comment.id,
                    communityArticleId: vue.communityArticleId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        if(type == '1') {
                            vue.commentList.splice(index, 1);
                        } else if (type == '2') {
                            oneComment.communityArticleCommentList.splice(index, 1);
                        }
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询未回复评论列表
        queryNotReplyCommentList(communityArticleId) {
            const vue = this;
            vue.currentPage = 1;
            vue.pageSize = 10;
            $.ajax({
                url: vue.communityCommentUrl + "/query/notReply/comment",
                type: "get",
                data: {
                    communityArticleId,
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.commentStatus = 3;
                        vue.commentCount = response.data.commentCount;
                        vue.commentList = response.data.selectPage.records;
                        vue.totals = response.data.selectPage.total;
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询默认排序评论列表
        queryDefaultCommentList(communityArticleId) {
            const vue = this;
            vue.currentPage = 1;
            vue.pageSize = 10;
            $.ajax({
                url: vue.communityCommentUrl + "/queryDefault/commentList",
                type: "get",
                data: {
                    communityArticleId,
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.commentCount = response.data.commentCount;
                        vue.commentList = response.data.selectPage.records;
                        vue.totals = response.data.selectPage.total;
                        vue.timeSort = -1;
                        vue.timeSort = (vue.timeSort + 1) % 3;
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 打开发布文章按钮
        openPublishArticle() {
            this.isShowInputComment = true
        },
        getDownOrUpgradeCommunityArticleComment(communityArticleId) {
            if (this.timeSort == '0') {
                // 得到时间降序评论列表
                this.queryTimeDownSortComment(communityArticleId);
            } else if (this.timeSort == '1') {
                // 得到时间升序评论列表
                this.queryTimeUpgradeComment(communityArticleId);
            } else if (this.timeSort == '2') {
                // 得到默认排序列表
                this.queryDefaultCommentList(communityArticleId);
            }
        },

        // 得到时间降序评论列表
        queryTimeDownSortComment(communityArticleId) {
            const vue = this;
            vue.currentPage = 1;
            vue.pageSize = 10;
            $.ajax({
                url: vue.communityCommentUrl + "/query/timeDownSort/comment",
                type: "get",
                data: {
                    communityArticleId,
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.commentStatus = 2;
                        vue.commentCount = response.data.commentCount;
                        vue.commentList = response.data.selectPage.records;
                        vue.totals = response.data.selectPage.total;
                        vue.timeSort = (vue.timeSort + 1) % 3;
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询时间升序评论列表
        queryTimeUpgradeComment(communityArticleId) {
            const vue = this;
            vue.currentPage = 1;
            vue.pageSize = 10;
            $.ajax({
                url: vue.communityCommentUrl + "/query/timeUpgrade/comment",
                type: "get",
                data: {
                    communityArticleId,
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.commentStatus = 1;
                        vue.commentCount = response.data.commentCount;
                        vue.commentList = response.data.selectPage.records;
                        vue.totals = response.data.selectPage.total;
                        vue.timeSort = (vue.timeSort + 1) % 3;
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        }
    },
};
</script>

<style scoped>
.top-comment {
    background-color: #D9ECFF;
    text-align: center;
    margin-left: 10px;
    width: 50px;
    height: 20px;
    display: inline-block;
    line-height: 20px;
}
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

.one-input {
    box-shadow: 0 0 10px 0 rgba(0, 0, 0, 0.5);
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
    z-index: 8;
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
.one-right {
    float: right;
    font-size: 20px;
}
.top-comment-font {
    color: #409EFF;
    font-size: 12px;
    vertical-align: middle;
}
.curation-comment-font {
    color: #FF4D4D;
    font-size: 12px;
    vertical-align: middle;
}
.top-comment-icon {
    color: #409EFF;
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
    left: 90%;
    z-index: 2;
    width: 76px;
    height: 28px;
    line-height: 10px;
    
}
.publish-comment-indicate {
    position: absolute;
    top: 175px;
    left: 67%;
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
    left: 90%;
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
}
</style>