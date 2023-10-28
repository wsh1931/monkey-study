<template>
    <div class="MonkeyWebResourceComment-container">
        <div style="position: relative;">
            <span class="reply-total">{{ commentCount }}&nbsp;条回复</span>
            <span style="position: absolute; right: 0;">
                <span 
                @click="queryNotReplyComment(resourceId)"
                :class="['iconfont icon-zhuanhuan no-reply-comment', {selected: commentStatus == '3'}]">&nbsp;切换为未回复评论</span>
                <span @click="queryDownOrUpgradeResourceComment(resourceId)" class="time-comment" v-if="timeSort == '0'">
                    <span :class="['iconfont icon-zhuanhuan']">&nbsp;切换为时间降序</span> 
                </span>
                <span @click="queryDownOrUpgradeResourceComment(resourceId)" class="time-comment" v-if="timeSort == '1'">
                        <span :class="['iconfont icon-zhuanhuan', {selected: commentStatus == '2'}]">&nbsp;切换为时间升序</span> 
                </span>
                <span @click="queryDownOrUpgradeResourceComment(resourceId)" class="time-comment" v-if="timeSort == '2'">
                    <span :class="['iconfont icon-zhuanhuan', {selected: commentStatus == '1'}]">&nbsp;切换为默认排序</span> 
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
            <el-row v-for="oneComment in resourceCommentList" :key="oneComment.id">
                <el-col :span="1">
                    <img @click="toUserViews(oneComment.senderId)" class="one-comment-img" :src="oneComment.senderHeadImg" alt="">
                </el-col>
                <el-col :span="23">
                    <div 
                    @mouseover="oneComment.isMoreHover = '1'" 
                    @mouseleave="oneComment.isMoreHover = '0'">
                        <span @click="toUserViews(oneComment.senderId)" class="one-comment-username">{{ oneComment.senderUsername }}</span>
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
                                    v-if="oneComment.isCuration == '0' && isAuthor == '1'"
                                    >精选</div>
                                    <div 
                                    @click="cancelCurationComment(oneComment)"
                                    v-if="oneComment.isCuration == '1' && isAuthor == '1'">取消精选</div>
                                    <div 
                                    @click="topComment(oneComment)" 
                                    v-if="oneComment.isTop == '0' && isAuthor == '1'">置顶</div>
                                    <div 
                                    @click="cancelTopComment(oneComment)" 
                                    v-if="oneComment.isTop == '1' && isAuthor == '1'">取消置顶</div>
                                    <div>举报</div>
                                    <div v-if="isAuthor == '1'" @click="deleteComment(oneComment)">删除</div>
                                    
                                </div>
                            </span>
                            <span 
                            @click="oneComment.isSelected = '1'" 
                            v-if="oneComment.isSelected == '0'" 
                            class="iconfont icon-pinglun one-reply">&nbsp;回复</span>
                            <span 
                            @click="oneComment.isSelected = '0'" 
                            v-else 
                            class="iconfont icon-pinglun one-reply">&nbsp;收起</span>
                            <span 
                            @click="commentLike(oneComment, 1)"
                            v-if="oneComment.isLike == '0'"
                            class="iconfont icon-dianzan one-like" ></span>
                            <span 
                            @click="cancelCommentLike(oneComment, 1)"
                            v-if="oneComment.isLike == '1'"
                            class="iconfont icon-dianzan cancel-like" ></span>
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
                    v-for="twoComment in oneComment.resourceCommentList"
                    @mouseover="twoComment.isMoreHover = '1'" 
                    @mouseleave="twoComment.isMoreHover = '0'">
                        <div>
                            <img @click="toUserViews(twoComment.replyId)"  class="two-comment-img" :src="twoComment.replyHeadImg" alt="">
                            <span @click="toUserViews(twoComment.replyId)" class="one-comment-username">{{ twoComment.replyUsername }}</span>
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
                                        v-if="twoComment.isCuration == '0' && isAuthor == '1'"
                                        @click="curationComment(twoComment)">精选</div>
                                        <div
                                        v-if="twoComment.isCuration == '1' && isAuthor == '1'"
                                        @click="cancelCurationComment(twoComment)" >取消精选</div>
                                        <div>举报</div>
                                        <div
                                        v-if="isAuthor == '1'"
                                        @click="deleteComment(twoComment)">删除</div>
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
                                @click="commentLike(twoComment, 2)"></span>
                                <span 
                                v-if="twoComment.isLike == '1'"
                                class="iconfont icon-dianzan cancel-like" 
                                @click="cancelCommentLike(twoComment, 2)"></span>
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
        </div>

        <PagiNation
            style="text-align: right; margin-top: 20px;"
            :totals="totals"
            :currentPage="currentPage" 
            :pageSize="pageSize" 
            @handleCurrentChange = "handleCurrentChange"
            @handleSizeChange="handleSizeChange"/>
    </div>
</template>

<script>
import $ from 'jquery'
import store from '@/store';
import { getTimeFormat } from '@/assets/js/DateMethod';
import { mavonEditor } from 'mavon-editor'
import VueMarkdown from 'vue-markdown';
import 'mavon-editor/dist/css/index.css'
import PagiNation from '@/components/pagination/PagiNation.vue';
export default {
    name: 'MonkeyWebResourceComment',
    components: {
        mavonEditor,
        VueMarkdown,
        PagiNation
    },
    data() {
        return {
            // 评论状态，0表示默认排序，1表示时间升序，2表示时间降序，3表示未回复评论
            commentStatus: 0,
            // 资源评论集合
            resourceCommentList: [],
            timeSort: '0',
            isShowInputComment: false,
            // 评论分页
            currentPage: 1,
            pageSize: 10,
            totals: 0,
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
            isAuthor: '0',
            // 是否按下键盘
            isKeyDown: false,
            // 评论总数
            commentCount: 0,
            resourceCommentUrl: "http://localhost:80/monkey-resource/comment"
        };
    },

    created() {
        this.resourceId = this.$route.params.resourceId;
        this.queryDefaultCommentList(this.resourceId);
        this.judgeIsAuthor(this.resourceId);
    },

    methods: {
        // 评论点赞
        commentLike(comment, type) {
            const vue = this;
            let recipientId;
            if (type == '1') {
                recipientId = comment.senderId;
            } else if (type == '2') {
                recipientId = comment.replyId
            };
            $.ajax({
                url: vue.resourceCommentUrl + "/commentLike",
                type: "post",
                data: {
                    commentId: comment.id,
                    recipientId,
                    resourceId: vue.resourceId,
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
                url: vue.resourceCommentUrl + "/cancelCommentLike",
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
        // 精选评论
        curationComment(comment) {
            const vue = this;
            $.ajax({
                url: vue.resourceCommentUrl + "/curationComment",
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
                url: vue.resourceCommentUrl + "/cancelCurationComment",
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
                url: vue.resourceCommentUrl + "/topComment",
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
                url: vue.resourceCommentUrl + "/cancelTopComment",
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
        deleteComment(comment) {
            const vue = this;
            $.ajax({
                url: vue.resourceCommentUrl + "/deleteComment",
                type: "delete",
                data: {
                    commentId: comment.id,
                    resourceId: vue.resourceId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        if (vue.commentStatus == '0') {
                            // 默认排序
                            vue.queryDefaultCommentList(vue.resourceId);
                        } else if (vue.commentStatus == '1') {
                            // 按时间升序排序
                            vue.queryTimeUpgradeComment(vue.resourceId);
                        } else if (vue.commentStatus == '2') {
                            // 按时间降序排序
                            vue.queryTimeDownSortComment(vue.resourceId);
                        } else if (vue.commentStatus == '3') {
                            // 未回复排序
                            vue.queryNotReplyComment(vue.resourceId);
                        }
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 发表评论
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
        // 发表评论回复 type = 1代表一级评论回复，type = 2代表子评论回复
        publishCommentReply(comment, parentId, type) {
            const vue = this;
            $.ajax({
                url: vue.resourceCommentUrl + "/publishCommentReply",
                type: "post",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    senderId: (type == 1) ? comment.senderId :comment.replyId,
                    parentId,
                    replyContent: comment.replyContent,
                    resourceId: vue.resourceId,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        comment.replyContent = '';
                        comment.isKeyDown = '0';
                        comment.isSelected = '0';
                        if (vue.commentStatus == '0') {
                            // 默认排序
                            vue.queryDefaultCommentList(vue.resourceId);
                        } else if (vue.commentStatus == '1') {
                            // 按时间升序排序
                            vue.queryTimeUpgradeComment(vue.resourceId);
                        } else if (vue.commentStatus == '2') {
                            // 按时间降序排序
                            vue.queryTimeDownSortComment(vue.resourceId);
                        } else if (vue.commentStatus == '3') {
                            // 未回复排序
                            vue.queryNotReplyComment(vue.resourceId);
                        }
                        vue.$modal.msgSuccess(response.msg)
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        queryDownOrUpgradeResourceComment(resourceId) {
            if (this.timeSort == '0') {
                // 得到时间降序评论列表
                this.queryTimeDownSortComment(resourceId);
            } else if (this.timeSort == '1') {
                // 得到时间升序评论列表
                this.queryTimeUpgradeComment(resourceId);
            } else if (this.timeSort == '2') {
                // 得到默认排序列表
                this.queryDefaultCommentList(resourceId);
            }
        },
        // 得到时间降序评论列表
        queryTimeDownSortComment(resourceId) {
            const vue = this;
            vue.currentPage = 1;
            vue.pageSize = 10;
            $.ajax({
                url: vue.resourceCommentUrl + "/query/timeDownSort/comment",
                type: "get",
                data: {
                    resourceId,
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
                        vue.resourceCommentList = response.data.selectPage.records;
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
        queryTimeUpgradeComment(resourceId) {
            const vue = this;
            vue.currentPage = 1;
            vue.pageSize = 10;
            $.ajax({
                url: vue.resourceCommentUrl + "/query/timeUpgrade/comment",
                type: "get",
                data: {
                    resourceId,
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
                        vue.resourceCommentList = response.data.selectPage.records;
                        vue.totals = response.data.selectPage.total;
                        vue.timeSort = (vue.timeSort + 1) % 3;
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询未回复评论列表
        queryNotReplyComment(resourceId) {
            this.currentPage = 1;
            this.pageSize = 10;
            const vue = this;
            $.ajax({
                url: vue.resourceCommentUrl + "/queryNotReplyComment",
                type: "get",
                data: {
                    resourceId,
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
                        vue.resourceCommentList = response.data.selectPage.records;
                        vue.totals = response.data.selectPage.total;
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 判断当前登录用户是否是文章作者
        judgeIsAuthor(resourceId) {
            const vue = this;
            $.ajax({
                url: vue.resourceCommentUrl + "/judgeIsAuthor",
                type: "get",
                data: {
                    resourceId
                },
                headers: {
                    Authorization: 'Bearer ' + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.isAuthor = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 前往用户主页
        toUserViews(userId) {
            const { href } = this.$router.resolve({
                name: "user_home",
                params: {
                    userId
                }
            })

            window.open(href, "_blank")
        },
        handleSizeChange(val) {
            this.pageSize = val;
            if (this.commentStatus == '0') {
                // 默认排序
                this.queryDefaultCommentList(this.resourceId);
            } else if (this.commentStatus == '1') {
                // 按时间升序排序
                this.queryTimeUpgradeComment(this.resourceId);
            } else if (this.commentStatus == '2') {
                // 按时间降序排序
                this.queryTimeDownSortComment(this.resourceId);
            } else if (this.commentStatus == '3') {
                // 未回复排序
                this.queryNotReplyComment(this.resourceId);
            }
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            if (this.commentStatus == '0') {
                // 默认排序
                this.queryDefaultCommentList(this.resourceId);
            } else if (this.commentStatus == '1') {
                // 按时间升序排序
                this.queryTimeUpgradeComment(this.resourceId);
            } else if (this.commentStatus == '2') {
                // 按时间降序排序
                this.queryTimeDownSortComment(this.resourceId);
            } else if (this.commentStatus == '3') {
                // 未回复排序
                this.queryNotReplyComment(this.resourceId);
            }
        },
        // 查询评论列表
        queryDefaultCommentList(resourceId) {
            const vue = this;
            vue.currentPage = 1;
            vue.pageSize = 10;
            $.ajax({
                url: vue.resourceCommentUrl + "/queryCommentList",
                type: "get",
                data: {
                    resourceId,
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.resourceCommentList = response.data.selectPage.records;
                        vue.totals = response.data.selectPage.total;
                        vue.commentCount = response.data.commentCount;
                        vue.timeSort = -1;
                        vue.timeSort = (vue.timeSort + 1) % 3;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 发布社区评论
        publishCommentMethod(commentContent) {
            if (commentContent == null || commentContent == "" || commentContent.length <= 0) {
                this.$modal.msgWarning("评论内容不能为空")
                return false;
            }
            const vue = this;
            $.ajax({
                url: vue.resourceCommentUrl + "/publishCommentMethod",
                type: "post",
                data: {
                    resourceId: vue.resourceId,
                    commentContent,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.isKeyDown = false;
                        vue.publishCommentContent = "";
                        vue.$modal.msgSuccess(response.msg);
                        if (vue.commentStatus == '0') {
                            // 默认排序
                            vue.queryDefaultCommentList(vue.resourceId);
                        } else if (vue.commentStatus == '1') {
                            // 按时间升序排序
                            vue.queryTimeUpgradeComment(vue.resourceId);
                        } else if (vue.commentStatus == '2') {
                            // 按时间降序排序
                            vue.queryTimeDownSortComment(vue.resourceId);
                        } else if (vue.commentStatus == '3') {
                            // 未回复排序
                            vue.queryNotReplyComment(vue.resourceId);
                        }
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // ctrl + enter发表评论
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
        getTimeFormat(time) {
            return getTimeFormat(time);
        }
    },
};
</script>

<style scoped>
.selected {
    color: #409EFF;
}
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
    opacity: 0.5;
}
.publish-comment-indicate {
    position: absolute;
    top: 175px;
    left: 65%;
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
    box-shadow: 0 0 5px 0 rgba(0, 0, 0, 0.1);
}
</style>