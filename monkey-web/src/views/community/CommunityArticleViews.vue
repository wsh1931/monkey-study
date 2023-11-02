<template>
    <div class="MonkeyWebCommunityArticleViews-container">
        <ReportContent
        v-if="showReportContent"
        @reportContent="reportContent"
        :reportContentType="reportContentType"
        :reportContentAssociationId="reportContentAssociationId"
        @closeReportContent="closeReportContent"/>
        <CollectCard v-if="showCollect"
            :associateId="associateId"
            :showCollect="showCollect"
            :collectType="collectType"
            :collectTitle="collectTitle"
            @closeCollect="closeCollect"/>
        <el-row>
            <el-col :span="18">
                <div class="article-content">
                    <div class="article-title">{{ article.title }}</div>
                    <div style="margin: 10px 0 0 0; padding: 0 20px;">
                        <span>
                            <img class="article-picture" :src="article.userHeadImg" alt="">
                            <span class="username-author" style="max-width: 600px;">{{ article.username }}</span>
                            <span class="publish-time">|&nbsp;&nbsp;&nbsp;{{ article.updateTime }}</span>
                        </span>
                    </div>

                    <div style="padding: 0 20px;">
                        <VueMarkdown 
                        class="markdown-content"
                        :source="article.content" 
                        :highlight="true"
                        :html="true"
                        :xhtmlOut="true">
                        </VueMarkdown>
                    </div>

                    <!-- 文章评分 -->
                <div class="header-score">
                    <span class="score-description">给本帖评分</span>
                    <el-rate
                        @change="communityArticleScore(communityArticleId, userArticleScore)"
                        class="score"
                        v-model="userArticleScore"
                        :texts=texts
                        show-text
                        :colors="colors"
                        text-color="#FC5531">
                    </el-rate>
                </div>
                </div>
                
                <!-- 文章投票 -->
                    <div class="header" v-if="article.isVote == '1'">
                            <div>
                                <span 
                                v-if="articleVoteInfo.isVote == '0' && articleVoteInfo.isOverdue == '0'"  
                                class="vote-status">[请选择]</span>
                                <span 
                                v-else-if="articleVoteInfo.isVote == '1' && articleVoteInfo.isOverdue == '0'"  
                                class="vote-status">[已投票]</span>
                                <span 
                                v-else-if="articleVoteInfo.isOverdue == '1'"  
                                class="vote-status"
                                style="color: #F56C6C;">[已过期]</span>
                                <span class="vote-name">{{ articleVoteInfo.voteName }}</span>
                                <span v-if="articleVoteInfo.voteKind == '0'" class="select-method">单选</span>
                                <span v-if="articleVoteInfo.voteKind == '1'" class="select-method">多选</span>
                            </div>
                            <div 
                            @click="selectArticleVote(articleVoteInfo, articleVoteItem)"
                            :class="['select-content', {selectedVote: articleVoteItem.isSelected == '1'}]" 
                            v-for="articleVoteItem in articleVoteInfo.communityArticleVoteItemList" 
                            :key="articleVoteItem.id">
                                <span>{{ articleVoteItem.voteContent }}</span>

                                <span 
                                v-if="articleVoteInfo.isVeto == '0' || articleVoteInfo.isOverdue == '1'" 
                                style="float: right;">{{ articleVoteItem.poll }}</span>
                            </div>
                            <el-button 
                            v-if="articleVoteInfo.isVote == '0' && articleVoteInfo.isOverdue == '0'" 
                            class="vote-button"
                            @click="judgeVoteIsSuccess(articleVoteInfo.id, articleVoteInfo.communityArticleVoteItemList)"
                            >确定投票</el-button>

                            <div class="discuss-people">
                                <span style="margin-right: 10px;">{{ articleVoteInfo.votePeople }} 人已参与讨论</span>
                                <span>于 {{ articleVoteInfo.voteDuration }} 结束</span>
                            </div>
                    </div>

                    <!-- 文章任务 -->
                    <div class="article-task" v-if="isShowTask">
                        <div class="article-task-title">
                            <span class="task-submit-people">{{ articleTask.submitCount }}&nbsp;人已提交</span>
                            <span style="float: right;">
                                <span class="task-end-time">任务截止：{{ articleTask.endTime }}</span>
                                <span class="task-end-time">完成率&nbsp;{{ articleTask.finish }}&nbsp;%</span>
                                <span class="no-submit-people">
                                    <el-dropdown click="channel-content" size="small" trigger="click">
                                        <el-button type="text" size="mini"  @click="queryNoSubmitTaskPeople(articleTask.id, articleTask.page.records)">
                                            未提交&nbsp;{{ articleTask.notSubmitCount }}人&nbsp;
                                        </el-button>
                                        <el-dropdown-menu slot="dropdown" style="text-align: center;" >
                                        <el-dropdown-item v-for="user in taskNoSubmitPeople" :key="user.id" @click.native="toUserViews(user.id)">
                                            <img class="task-img" :src="user.photo" alt="">
                                            <span class="task-username">{{ user.username }}</span>
                                        </el-dropdown-item>
                                        </el-dropdown-menu>
                                    </el-dropdown>
                                </span>
                                <el-button 
                                v-if="!isShowSubmitTask" 
                                class="button-task" 
                                size="mini" 
                                type="primary"
                                @click="isShowSubmitTask = true">提交任务</el-button>
                                <el-button 
                                v-if="isShowSubmitTask" 
                                class="button-task el-icon-delete" 
                                size="mini" 
                                type="warning"
                                @click="isShowSubmitTask = false">&nbsp;取消</el-button>
                                <el-button 
                                type="success" 
                                class="button-task el-icon-download" 
                                size="mini"
                                @click="exportDataToExcel(articleTask.page.records)">&nbsp;导出</el-button>
                            </span>
                        </div>
                        <el-row v-if="isShowSubmitTask">
                            <mavon-editor
                            v-model="taskContent" 
                            :toolbars="toolbars"
                            :translate="true"
                            defaultOpen="edit"
                            placeholder="请输入任务内容"
                            class="mavon-editor"
                            :navigation="false"
                            :subfield="false"
                            :scrollStyle="true"
                            @keyup.native="handleKeyDown(articleTask.id, taskContent, $event)"
                            ></mavon-editor>
                            <el-button 
                            type="primary" 
                            size="small" 
                            class="publish-comment-button" @click="submitTask(articleTask.id, taskContent)" >提交任务</el-button>
                            <el-row class="publish-comment-indicate">按下Enter换行，Ctrl+Enter提交任务</el-row>
                        </el-row>

                        <!-- 任务内容信息 -->
                        <div>
                            <el-table
                                :data="articleTask.page.records"
                                style="width: 100%;">
                                <el-table-column
                                align="left"
                                label="用户信息"
                                width="180">
                                <template slot-scope="scope">
                                    <img @click="toUserViews(scope.row.userId)" class="user-headImg" :src="scope.row.headImg" alt="">
                                    <span @click="toUserViews(scope.row.userId)" class="username">{{ scope.row.username }}</span>
                                </template>
                                </el-table-column>

                                <el-table-column
                                align="left"
                                label="提交内容"
                                width="180">
                                <template slot-scope="scope">
                                    <span class="submit-content">{{ scope.row.replyContent }}</span>
                                </template>
                                </el-table-column>

                                <el-table-column
                                align="left"
                                label="最近提交"
                                width="190">
                                    <template slot-scope="scope">
                                        <i class="el-icon-time"></i>
                                        <span style="margin-left: 10px">{{ scope.row.createTime }}</span>
                                    </template>
                                </el-table-column>

                                <el-table-column
                                align="left"
                                label="提交次数"
                                width="190">
                                    <template slot-scope="scope">
                                        <span>用户已提交 {{ scope.row.submitCount }} 次</span>
                                    </template>
                                </el-table-column>
                                
                                <el-table-column 
                                label="操作"
                                align="center">
                                <template slot-scope="scope">
                                    <el-button
                                    size="mini"
                                    type="text"
                                    @click="checkTaskContent(scope.row)">查看</el-button>

                                    <el-button
                                    v-if="scope.row.userId == $store.state.user.id"
                                    size="mini"
                                    type="text"
                                    style="color: rgb(243, 68, 68);"
                                    @click="updateTaskContentMethod(scope.row)">修改</el-button>
                                    <el-button
                                    size="mini"
                                    type="text"
                                    style="color: orange;"
                                    @click="queryTaskSubmitHistoryRecords(scope.row)"
                                    >历史</el-button>
                                </template>
                                </el-table-column>
                            </el-table>
                            <!-- 查看文章内容对话框 -->
                                    <el-dialog
                                    class="dialogClass"
                                    :title="dialogContent.username + '的回复内容'"
                                    :visible.sync="isShowTaskReplyContent"
                                    width="50%"
                                    >
                                    <vue-markdown 
                                    class="markdown-content"
                                    :source="dialogContent.replyContent" 
                                    :highlight="true"
                                    :html="true"
                                    :xhtmlOut="true">
                                    </vue-markdown>
                                    <span slot="footer" class="dialog-footer">
                                        <el-button type="primary" @click="isShowTaskReplyContent = false">返 回</el-button>
                                    </span>
                                    </el-dialog>

                                    <!-- 修改文章内容对话框 -->
                                    <el-dialog title="修改提交内容" :visible.sync="updateTaskContent" style="z-index: 2;">
                                        <mavon-editor
                                        style="z-index: 2;"
                                        v-model="dialogContent.replyContent" 
                                        :toolbars="toolbars"
                                        :translate="true"
                                        defaultOpen="edit"
                                        placeholder="请输入任务内容"
                                        class="mavon-editor"
                                        :navigation="false"
                                        :subfield="false"
                                        :scrollStyle="true"
                                        ></mavon-editor>
                                    <div slot="footer" class="dialog-footer">
                                        <el-button @click="updateTaskContent = false">取 消</el-button>
                                        <el-button type="primary" @click="confirmUpdate(dialogContent.id, dialogContent.replyContent)">确 定</el-button>
                                    </div>
                                </el-dialog>

                                <!-- 历史记录对话框 -->
                                <el-dialog 
                                :title="dialogContent.username + '的历史记录'" 
                                :visible.sync="historyRecordDialog" 
                                style="z-index: 2; text-align: center;"
                                width="45%">
                                    <el-table :data="taskHistoryRecords" style="z-index: 2;">
                                        <el-table-column
                                        align="center"
                                        label="提交时间"
                                        width="190">
                                            <template slot-scope="scope">
                                                <i class="el-icon-time"></i>
                                                <span style="margin-left: 10px">{{ scope.row.createTime }}</span>
                                            </template>
                                        </el-table-column>
                                        <el-table-column label="提交内容" width=vue.ResultStatus.SUCCESS>
                                            <template slot-scope="scope">
                                                <span class="submit-content">{{ scope.row.replyContent }}</span>
                                            </template>
                                        </el-table-column>

                                        <el-table-column label="操作">
                                            <template slot-scope="scope">
                                                <el-button
                                                size="mini"
                                                type="primary"
                                                @click="checkTaskContent(scope.row)">查看</el-button>
                                                <el-button
                                                size="mini"
                                                type="danger"
                                                v-if="scope.row.userId == $store.state.user.id"
                                                @click="deleteTaskHistoryRecord(scope.$index, scope.row)">删除</el-button>
                                            </template>
                                        </el-table-column>
                                    </el-table>

                                    <PagiNation
                                    style="text-align: center; margin-top: 10px;"
                                    :totals="totalsHistory"
                                    :currentPage="currentPageHistory" 
                                    :pageSize="pageSizeHistory" 
                                    @handleCurrentChange = "handleCurrentChangeHistory"
                                    @handleSizeChange="handleSizeChangeHistory"/>
                                </el-dialog>
                            <PagiNation
                                style="text-align: right; margin-top: 10px;"
                                :totals="totals"
                                :currentPage="currentPage" 
                                :pageSize="pageSize" 
                                @handleCurrentChange = "handleCurrentChange"
                                @handleSizeChange="handleSizeChange"/>
                        </div>
                    </div>

                    <div class="function">
                        <div class="position">
                            <span style="font-size: 14px;">
                                <i class="el-icon-view" style="vertical-align: middle;"></i>
                                游览&nbsp;7
                            </span>
                            <span 
                            @click="articleLike(communityArticleId)" 
                            class="iconfont icon-dianzan" 
                            v-if="isLike == '0'">&nbsp;点赞&nbsp;{{ article.likeCount }}</span>
                            <span
                            @click="cancelArticleLike(communityArticleId)"
                            class="iconfont icon-dianzan like" 
                            v-if="isLike == '1'">&nbsp;点赞&nbsp;{{ article.likeCount }}</span>
                            <a href="#reply" @click="toReply()">
                                <span class="iconfont icon-pinglun">&nbsp;回复&nbsp;{{ article.commentCount }}</span>
                            </a>
                            <span 
                            @click="userCollect(communityArticleId, article.title)" 
                            class="iconfont icon-shoucang"
                            v-if="isCollect == '0'">&nbsp;收藏&nbsp;{{ article.collectCount }}</span>
                            <span 
                            @click="userCollect(communityArticleId, article.title)" 
                            class="iconfont icon-shoucang collect"
                            v-if="isCollect == '1'">&nbsp;收藏&nbsp;{{ article.collectCount }}</span>

                            <span class="iconfont icon-zhuanfa">&nbsp;转发</span>
                            <span 
                            @mouseover="isHoverMore = true"
                            @mouseleave="isHoverMore = false"
                            class="el-icon-more view more" >
                                <div class="more-background" v-if="isHoverMore && (isAuthor == '1' || isManager == '1')">
                                    <div
                                    v-if="isAuthor == '1' || isManager == '1'" 
                                    class="more-background-content" 
                                    @click="deleteArticle(article.id)">删除</div>
                                    <div 
                                    v-if="article.isExcellent == '0' && isManager == '1'"
                                    class="more-background-content" 
                                    @click="setExcellentArticle(article)">精选</div>
                                    <div 
                                    v-if="article.isExcellent == '1' && isManager == '1'"
                                    class="more-background-content" 
                                    @click="cancelExcellentArticle(article)">取消精选</div>
                                    <div 
                                    v-if="article.isTop == '0' && isManager == '1'"
                                    class="more-background-content" 
                                    @click="setTopArticle(article)">置顶</div>
                                    <div 
                                    v-if="article.isTop == '1' && isManager == '1'"
                                    class="more-background-content" 
                                    @click="cancelTopArticle(article)">取消置顶</div>

                                    <div 
                                    class="more-background-content" 
                                    @click="reportContent(article.id)">举报</div>
                                </div>
                            </span>
                            <span class="channel">
                                <i class="el-icon-menu"></i>
                                <span>频道&nbsp;</span>

                                <el-dropdown click="channel-content" size="small">
                                <el-button type="primary" size="mini" class="channel-button">
                                    {{ channelName }}
                                </el-button>
                                <el-dropdown-menu slot="dropdown">
                                <el-dropdown-item 
                                v-for="channel in channelList" 
                                :key="channel"
                                @click.native="updateCommunityArticleChannel(communityArticleId, channel)">{{ channel.channelName }}</el-dropdown-item>
                                </el-dropdown-menu>
                                </el-dropdown>
                            </span>
                            <scrollactive class="write-reply">
                                <a href="#reply" @click="toReply()">
                                <el-button  type="primary" size="mini" class="el-icon-edit reply-button" >写回复
                                </el-button>
                                </a>
                            </scrollactive>
                        </div>
                    </div>

                    <div style="margin-top: 10px;">
                        <CommunityArticleComment
                        :isManager="isManager"
                        :isAuthor="isAuthor"
                        id="reply"
                        style="z-index: 10;"/>
                    </div>
            </el-col>
            <el-col :span="6" style="padding: 0 10px;">
                <CommunityInfo
                :isShowScore="isShowScore"/>
            </el-col>
        </el-row>
        
    </div>
</template>

<script>
import ReportContent from '@/components/report/ReportContent'
import CollectCard from '@/components/collect/CollectCard.vue';
import PagiNation from '@/components/pagination/PagiNation.vue';
import { mavonEditor } from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'
import $ from 'jquery'
import store from '@/store';
import CommunityArticleComment from '@/components/community/CommunityArticleComment.vue';
import VueMarkdown from 'vue-markdown';
import CommunityInfo from '@/components/community/CommunityInfo.vue';
export default {
    name: 'MonkeyWebCommunityArticleViews',
    components: {
        CommunityInfo,
        VueMarkdown,
        CommunityArticleComment,
        mavonEditor,
        PagiNation,
        CollectCard,
        ReportContent
    },
    data() {
        
        return {
            // 举报类型(0表示文章，1表示问答，2表示课程, 3表示社区，4表示社区文章, 5表示资源)
            reportContentType: this.reportContentType.communityArticle,
            // 举报关联id
            reportContentAssociationId: "0",
            // 显示举报内容框
            showReportContent: false,
            // 社区文章频道名
            channelName: "",
            // 社区频道集合
            channelList: [],
            // 判断当前登录用户是否是社区管理员
            isManager: '0',
            // 判断当前登录用户是否是作者
            isAuthor: '0',
            // 当前登录用户是否收藏此文章
            isCollect: "0",
            // 是否展示收藏夹
            showCollect: false,
             // 收藏类型
            collectType: 3,
            // 收藏标题
            collectTitle: "",
            // 收藏关联id
            associateId: "",
            // 判断当前登录用户是否点赞文章
            isLike: '0',
            // 是否按下键盘
            isKeyDown: false,
            // 任务历史提交记录
            taskHistoryRecords: [],
            // 对话框内容
            dialogContent: {},
            // 是否显示文章任务历史记录
            historyRecordDialog: false,
            // 是否显示修改内容
            updateTaskContent: false,
            // 是否显示任务回复内容
            isShowTaskReplyContent: false,
            // 查询当前登录用户对该文章的评分
            userArticleScore: 0,
            // 任务未提交用户集合
            taskNoSubmitPeople: [],
            // 目前是否展示未提交任务下拉框
            isShowTaskPeople: false,
            currentPage: 1,
            pageSize: 10,
            totals: 0,
            currentPageHistory: 1,
            pageSizeHistory: 10,
            totalsHistory: 0,
            // 提交任务内容
            taskContent: "",
            // 文章任务信息
            articleTask: {},
            // 当前登录用户是否有资格查看任务
            isShowTask: false,
            // 是否展示提交任务
            isShowSubmitTask: false,
            // 文章投票基本信息
            articleVoteInfo: {},
            // 社区id
            communityId: "",
            // 社区文章id
            communityArticleId: "",
            // 文章基本信息
            article: {},
            // 是否展示内容评分
            isShowScore: true,
            // 是否悬浮在了隐藏更多上
            isHoverMore: false,
            articleScore: '0',
            // 评分描述内容
            texts: ['锋芒小试，眼前一亮', '潜力巨大，未来可期', '持续贡献，值得关注', '成绩优异，大力学习', '贡献巨大，全力支持'],
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
            communityArticleUrl: "http://localhost:80/monkey-community/article",
            communityDetailCardUrl: "http://localhost:80/monkey-community/community/detail/card",
            publishCommunityUrl: "http://localhost:80/monkey-community/publish",
        };
    },

    created() {
        this.communityId = this.$route.params.communityId;
        this.communityArticleId = this.$route.params.communityArticleId;
        this.queryArticleBaseInfo(this.communityArticleId);
        
        this.queryUserArticleScore(this.communityArticleId)
        this.judgeIsLikeArticle(this.communityArticleId);
        this.judgeIsCollectArticle(this.communityArticleId);
        this.judgeIsAuthorOrManager(this.communityId, this.communityArticleId);
        this.querySupportManageModifyChannel(this.communityId)
        this.queryCommunityArticleChannelName(this.communityArticleId);
    },

    methods: {
        closeReportContent() {
            this.showReportContent = false;
        },
        reportContent(resourceId) {
            this.showReportContent = true;
            this.reportContentAssociationId = resourceId;
        },
        // 修改社区文章频道
        updateCommunityArticleChannel(communityArticleId, channel) {
            const vue = this;
            $.ajax({
                url: vue.communityArticleUrl + "/update/communityArticle/channel",
                type: "put",
                data: {
                    communityArticleId,
                    channelId: channel.id,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.channelName = channel.channelName;
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询社区文章频道名称
        queryCommunityArticleChannelName(communityArticleId) {
            const vue = this;
            $.ajax({
                url: vue.communityArticleUrl + "/queryCommunityArticle/channelName",
                type: "get",
                data: {
                    communityArticleId,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.channelName = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询支持管理员修改的社区频道集合
        querySupportManageModifyChannel(communityId) {
            const vue = this;
            $.ajax({
                url: vue.communityArticleUrl + "/querySupportManageModifyChannel",
                type: "get",
                data: {
                    communityId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.channelList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 设置文章置顶
        setTopArticle(article) {
            const vue = this;
            $.ajax({
                url: vue.communityDetailCardUrl + "/setTopArticle",
                type: "put",
                data: {
                    articleId:article.id,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        article.isTop = '1';
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
                error(response) {
                    vue.$modal.msgError(response.msg);
                }
            })
        },
        // 取消文章置顶
        cancelTopArticle(article) {
            const vue = this;
            $.ajax({
                url: vue.communityDetailCardUrl + "/cancelTopArticle",
                type: "put",
                data: {
                    articleId: article.id,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        article.isTop = '0';
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
                error(response) {
                    vue.$modal.msgError(response.msg);
                }
            })
        },
        // 设置文章为精选内容
        setExcellentArticle(article) {
            const vue = this;
            $.ajax({
                url: vue.communityDetailCardUrl + "/setExcellentArticle",
                type: "put",
                data: {
                    articleId: article.id
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        article.isExcellent = '1';
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
                error(response) {
                    vue.$modal.msgError(response.msg);
                }
            })
        },
         // 取消文章为精选内容
        cancelExcellentArticle(article) {
            const vue = this;
            $.ajax({
                url: vue.communityDetailCardUrl + "/cancelExcellentArticle",
                type: "put",
                data: {
                    articleId: article.id
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        article.isExcellent = '0';
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
                error(response) {
                    vue.$modal.msgError(response.msg);
                }
            })
        },
        // 删除文章
        deleteArticle(articleId) {
            const vue = this;
            $.ajax({
                url: vue.communityDetailCardUrl + "/deleteArticle",
                type: "delete",
                data: {
                    articleId,
                    communityId: vue.communityId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.$modal.msgSuccess(response.msg);
                        vue.$router.push({
                            name: "community_detail",
                            params: {
                                communityId: vue.communityId,
                            }
                        })
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
                error(response) {
                    vue.$modal.msgError(response.msg);
                }
            })
        },
        // 判断当前登录用户是否为作者或管理员
        judgeIsAuthorOrManager(communityId, communityArticleId) {
            const vue = this;
            $.ajax({
                url: vue.communityArticleUrl + "/judgeIsAuthorOrManager",
                type: "get",
                data: {
                    communityId,
                    communityArticleId
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.isAuthor = response.data.isAuthor;
                        vue.isManager = response.data.isManager;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 用户收藏课程
        userCollect(articleId, title) {
            this.associateId = articleId;
            this.showCollect = true;
            this.collectTitle = title;
        },
        // 判断当前登录用户是否收藏此社区文章
        judgeIsCollectArticle(communityArticleId) {
            const vue = this;
            $.ajax({
                url: vue.communityArticleUrl + "/judgeIsCollectArticle",
                type: "get",
                data: {
                    communityArticleId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.isCollect = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        closeCollect(status) {
            this.showCollect = status;
            this.judgeIsCollectArticle(this.communityArticleId);
        },
        // 跳转至回复
        toReply() {
            this.$scrollTo("#reply", 500);
        },
        // 点赞文章
        articleLike(communityArticleId) {
            const vue = this;
            $.ajax({
                url: vue.communityArticleUrl + "/articleLike",
                type: "put",
                data: {
                    communityArticleId,
                    recipientId: vue.article.userId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.$modal.msgSuccess(response.msg);
                        vue.isLike = '1';
                        vue.article.likeCount++;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 取消点赞文章
        cancelArticleLike(communityArticleId) {
            const vue = this;
            $.ajax({
                url: vue.communityArticleUrl + "/cancelArticleLike",
                type: "put",
                data: {
                    communityArticleId
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.$modal.msgSuccess(response.msg);
                        vue.isLike = '0';
                        vue.article.likeCount--;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 判断当前登录用户是否点赞该文章
        judgeIsLikeArticle(communityArticleId) {
            const vue = this;
            $.ajax({
                url: vue.communityArticleUrl + "/judgeIsLikeArticle",
                type: "get",
                data: {
                    communityArticleId
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.isLike = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 导出数据至excel
        exportDataToExcel(communityArticleTaskReply) {
            
            var url = this.communityArticleUrl + "/exportDataToExcel?";
            // 将每个articleTask对象转换为查询参数
            var param = "communityArticleTaskReplyStr=" + encodeURIComponent(JSON.stringify(communityArticleTaskReply));
            // 添加到URL中
            url += param;
            window.location.href = url;
            // $.ajax({
            //     url: vue.communityArticleUrl + "/exportDataToExcel",
            //     type: "get",
            //     contentType: "application/json;charset=UTF-8",
            //     xhrFields: {
            //         responseType: 'blob' // 设置响应类型为blob，以支持下载文件
            //     },
            //     success(response) {
            //         var blob = new Blob([response], { type: response.type });
            //         var url = URL.createObjectURL(blob);
            //         var a = document.createElement('a');
            //         a.href = url;
            //         a.download = response.name; // response.name为后端设置的文件名
            //         a.click();
            //         URL.revokeObjectURL(url);
            //     },
            //     error(response) {
            //         console.log(response);
            //     }
             
            // })

            
        },
        // 删除任务历史记录
        deleteTaskHistoryRecord(index, communityArticleTaskReply) {
            const vue = this;
            $.ajax({
                url: vue.communityArticleUrl + "/delete/taskHistoryRecord",
                type: "delete",
                data: {
                    communityArticleTaskReplyId: communityArticleTaskReply.id,
                    communityArticleTaskId: communityArticleTaskReply.communityArticleTaskId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.taskHistoryRecords.splice(index, 1);
                        vue.totalsHistory--;
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 修改任务提交内容
        updateTaskContentMethod(row) {
            this.dialogContent = row;
            this.updateTaskContent = true
        },
        // 查询任务提交历史记录
        queryTaskSubmitHistoryRecords(row) {
            this.dialogContent = row
            this.historyRecordDialog = true;
            const vue = this;
            $.ajax({
                url: vue.communityArticleUrl + "/queryTaskSubmit/historyRecords",
                type: "get",
                data: {
                    currentPage: vue.currentPageHistory,
                    pageSize: vue.pageSizeHistory,
                    communityArticleTaskId: row.communityArticleTaskId,
                    userId: row.userId,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.taskHistoryRecords = response.data.records;
                        vue.totalsHistory = response.data.total;
                    } else {
                        vue.$modal.msgError(response.msg)
                    }
                }
            })
        },
        // 修改社区文章任务内容
        confirmUpdate(communityArticleTaskReplyId, replyContent) {
            if (replyContent == null || replyContent == "") {
                this.$modal.msgWarning("输入内容不能为空")
            } else if (replyContent.length > 255) {
                this.$modal.msgWarning("不能超过255个字符")
            } else {
                // 修改提交内容
                const vue = this;
                $.ajax({
                    url: vue.communityArticleUrl + "/confirmUpdate",
                    type: "put",
                    data: {
                        replyContent,
                        communityArticleTaskReplyId,
                    },
                    headers: {
                        Authorization: "Bearer " + store.state.user.token,
                    },
                    success(response) {
                        if (response.code == vue.ResultStatus.SUCCESS) {
                            vue.updateTaskContent = false;
                            vue.$modal.msgSuccess(response.msg);
                        } else {
                            vue.$modal.msgError(response.msg);
                        }
                    },
                })
            }
        },
        // 查询当前登录用户对该文章的评分
        queryUserArticleScore(communityArticleId) {
            const vue = this;
            $.ajax({
                url: vue.communityArticleUrl + "/queryUserArticleScore",
                type: "get",
                data: {
                    communityArticleId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.userArticleScore = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
        },
        // 跳往用户主页
        toUserViews(userId) {
            const { href } = this.$router.resolve({
                name: "user_home",
                params: {
                    userId
                }
            })

            window.open(href, "_blank")
        },
        // 查询未提交任务成员列表
        queryNoSubmitTaskPeople(communityArticleTaskId, submitUserList) {
            if (this.isShowTaskPeople) {
                // 展示过数据就不需要再重新加载一次了
                return;
            }

            const vue = this;
            $.ajax({
                url: vue.communityArticleUrl + "/queryNoSubmitTaskPeople",
                type: "get",
                data: {
                    communityArticleTaskId,
                    submitUserList: JSON.stringify(submitUserList),
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.taskNoSubmitPeople = response.data;
                        vue.isShowTaskPeople = true;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
        },
        handleCurrentChange(val) {
            this.currentPage = val;
        },
        handleSizeChange(val) {
            this.pageSize = val;
        },
        handleCurrentChangeHistory(val) {
            this.currentPageHistory = val;
        },
        handleSizeChangeHistory(val) {
            this.pageSizeHistory = val;
        },
        // 提交任务
        submitTask(communityArticleTaskId, replyContent) {
            const vue = this;
            $.ajax({
                url: vue.communityArticleUrl + "/submitTask",
                type: "post",
                data: {
                    communityArticleTaskId,
                    replyContent,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.taskContent = "";
                        vue.queryTaskInfoAndJudgeIsExpire(vue.communityArticleId);
                        vue.$modal.msgSuccess(response.msg);
                        vue.isKeyDown = false;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
        },
        // 通过鼠标按键类型判断是否提交任务
        handleKeyDown(communityArticleTaskId, replyContent, event) {
            if (event.keyCode == '13' && !event.ctrlKey) {
                replyContent += "\n";
                e.preventDefault();
            }
            if (event.ctrlKey && event.keyCode == '13') {
                if (replyContent == null || replyContent == "") {
                    this.$modal.msgError("请输入提交内容");
                    return;
                }
                if (replyContent.length >= 255) {
                    this.$modal.msgError("提交内容不能超过 255 个字符")
                    return;
                }
                if (this.isKeyDown) {
                    this.$modal.msgWarning("提交次数过于频繁，请稍后再试")
                }
                this.isKeyDown = true;
                this.submitTask(communityArticleTaskId, replyContent);
            }
        },
        // 得到任务信息并判断当前任务是否过期
        queryTaskInfoAndJudgeIsExpire(communityArticleId) {
            const vue = this;
            $.ajax({
                url: vue.communityArticleUrl + "/queryTaskInfo/judgeIsExpire",
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
                        vue.articleTask = response.data;
                        vue.totals = response.data.page.total;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 判断当前登录用户是否能看到任务
        judgeIsShowTask(communityArticleId) {
            const vue = this;
            $.ajax({
                url: vue.communityArticleUrl + "/judgeIsShowTask",
                type: "get",
                data: {
                    communityArticleId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.isShowTask = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 提交投票选择
        submitVote(communityArticleVoteId, communityArticleVoteItems) {
            const vue = this;
            $.ajax({
                url: vue.communityArticleUrl + "/submitVote",
                type: "post",
                data: {
                    communityArticleVoteId,
                    communityArticleVoteItems: JSON.stringify(communityArticleVoteItems),
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.articleVoteInfo.isVote = '1';
                        vue.articleVoteInfo.votePeople++;

                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 判断投票是否符合规则
        judgeVoteIsSuccess(vetoId, vetoItems) {
            // 得到用户选择数
            let selectCount = 0;
            for (let i = 0; i < vetoItems.length; i++) {
                if (vetoItems[i].isSelected == '1') {
                    selectCount++;
                }
            }
            // 判断用户是否选择
            if (selectCount == 0) {
                this.$modal.msgError("您还未选择任何选项");
                return;
            }
            if (this.articleVoteInfo.vetoKind == '0') {
                // 若为单选
                if (selectCount > 1) {
                    this.$modal.msgError("您只能选择一个选项")
                    return;
                }
            }

            // 否则说明符合规则, 提交选择
            this.submitVote(vetoId, vetoItems);
        },
        // 选中文字投票
        selectArticleVote(articleVoteInfo, articleVoteItem) {
            if (articleVoteInfo.voteKind == '0') {
                // 说明为单选, 选择该选项,将其他选项取消
                let articleVoteItems = articleVoteInfo.communityArticleVoteItemList;
                for (let i = 0; i < articleVoteItems.length; i++) {
                    if (articleVoteItems[i] == articleVoteItem) {
                        articleVoteItems[i].isSelected = '1';
                    } else {
                        articleVoteItems[i].isSelected = '0';
                    }
                }
            } else if (articleVoteInfo.voteKind == '1') {
                // 否则说明为多选
                articleVoteItem.isSelected = '1';
            }
        },
        // 查询文章投票信息
        queryArticleVoteInfo(communityArticleId) {
            const vue = this;
            $.ajax({
                url: vue.communityArticleUrl + "/queryArticleVoteInfo",
                type: "get",
                data: {
                    communityArticleId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.articleVoteInfo = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 社区文章评分
        communityArticleScore(communityArticleId, articleScore) {
            const vue = this;
            $.ajax({
                url: vue.communityArticleUrl + "/communityArticle/score",
                type: "post",
                data: {
                    communityArticleId,
                    articleScore,
                    communityId: vue.communityId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询社区文章基本信息
        queryArticleBaseInfo(communityArticleId) {
            const vue = this;
            $.ajax({
                url: vue.communityArticleUrl + "/queryArticleBaseInfo",
                type: "get",
                data: {
                    communityArticleId
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.article = response.data;
                        if (vue.article.isVote == '1') {
                            // 得到文章投票信息
                            vue.queryArticleVoteInfo(vue.communityArticleId);
                        }
                        if (vue.article.isTask == '1') {
                            vue.judgeIsShowTask(vue.communityArticleId);
                            vue.queryTaskInfoAndJudgeIsExpire(vue.communityArticleId);
                        }
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        checkTaskContent(dialogContent) {
            this.dialogContent = dialogContent;
            this.isShowTaskReplyContent = true;
        },
    },
};
</script>

<style scoped>
.collect {
    color: #409EFF;
}
.reply-button {
    border-radius: 20px;
}
.like {
    color: #409EFF;
}
.dialogClass .el-dialog__body {
    padding-top: 0px;
    margin-left: 20px;
    color: #606266;
    font-size: 14px;
}
.markdown-content {
    padding: 0 !important;
    margin: 0 !important;
}
.task-username {
    display: inline-block;
    margin-left: 5px;
    overflow: hidden;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
    text-overflow: ellipsis;
    white-space: nowrap;
    vertical-align: middle;
    max-width: 100px;

}
.task-img {
    vertical-align: middle;
    width: 24px;
    height: 24px;
    border-radius: 50%;
}
.mavon-editor {
    min-height: 200px; 
    z-index: 1;
    width: 100%;
    margin-top: 20px;
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
    left: 66%;
    z-index: 2;
    font-size: 12px;
    opacity: 0.5;
}
.comment-content {
    font-size: 15px;
    font-weight: 540;
}
.submit-content {
    display: inline-block;
    max-width: 110px;
    overflow: hidden;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
    text-overflow: ellipsis;
    white-space: nowrap;
    vertical-align: middle;
}
.header-score {
    padding-left: 20px;
}
.evaluate-title {
    margin-bottom: 10px;
    font-weight: 600;
    font-size: 16px;
}
.article-content {
    background-color: #fff; 
    padding-bottom: 20px; 
    box-shadow: 0 2px 12px 2px rgba(0,0,0,0.1);
}
.header {
    background-color: #fff; 
    padding: 20px; 
    margin-top: 10px;
    box-shadow: 0 2px 12px 2px rgba(0,0,0,0.1);
}
.username {
    display: inline-block;
    margin-left: 10px;
    max-width: 110px;
    overflow: hidden;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
    text-overflow: ellipsis;
    white-space: nowrap;
    vertical-align: middle;
}
.username:hover {
    opacity: 0.5;
    cursor: pointer;
}
.user-headImg {
    height: 30px;
    width: 30px;
    vertical-align: middle;
    border-radius: 50%;
}
.user-headImg:hover {
    cursor: pointer;
    opacity: 0.5;
}
.no-submit-people {
    margin-right: 20px;
}
.task-end-time {
    font-size: 14px;
    opacity: 0.5;
    margin-right: 20px;
}
.task-submit-people {
    font-weight: 600;
    font-size: 16px;
}
.button-task {
    width: 0;
    height: 0;
    text-align: center;
    line-height: 26px;
    font-size: 14px;
    font-weight: 500;
    width: max-content;
    height: 28px;
    padding: 0 14px;
    border-radius: 16px;
}
.article-task {
    background-color: #fff;
    padding: 20px;
    margin-top: 10px;
    box-shadow: 0 2px 12px 2px rgba(0,0,0,0.1);
}
.write-reply {
    float: right;
}
.channel-button {
    text-overflow: ellipsis;
    padding: 0 8px;
    height: 20px;
    border-radius: 4px;
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    font-size: 12px;
    font-weight: 400;
    color: #fff;
    line-height: 20px;
    position: relative;
    -webkit-transition: all .2s;
    transition: all .2s;
}
.el-dropdown-link {
    cursor: pointer;
    color: #409EFF;
}
.el-icon-arrow-down {
    font-size: 12px;
}
.channel {
    margin-left: 20px;
    font-size: 14px;
}
.position {
    background-color: #F2F5F7;
    position: fixed;
    bottom: 0;
    width: 935px;
    padding: 10px 20px;
    z-index: 2;
    box-shadow: 0 2px 12px 2px rgba(0,0,0,0.1);
}
.function {
    background-color: #fff;
    position: relative;
}
.more-background-content:nth-child(n + 2) {
    padding-top: 12px;
}
.more-background-content:hover {
    color: #409EFF;
    cursor: pointer;
}
.more-background-content {
    animation: slide-out 0.4s linear;
}
@keyframes slide-out {
    0% {
        opacity: 0;
    }
    100% {
        opacity: 1;
    }
}
.more-background {
    position: absolute;
    text-align: center;
    width: 60px;
    padding: 10px 16px;
    bottom: 16px;
    left: -40px;
    background-color: #fff;
    box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
    z-index: 1;
    cursor: auto;
}
.view {
    font-size: 14px;
    margin-left: 20px;
}
.more {
    position: relative;
}
.more:hover {
    cursor: pointer;
}
.icon-zhuanfa {
    margin-left: 20px;
    font-size: 14px;
}
.icon-zhuanfa:hover {
    color: #409EFF;
    cursor: pointer;
}
.icon-dianzan {
    margin-left: 20px;
    font-size: 14px;
}
.icon-dianzan:hover {
    color: #409EFF;
    cursor: pointer;
}
.icon-shoucang {
    margin-left: 20px;
    font-size: 14px;
}
.icon-shoucang:hover {
    color: #409EFF;
    cursor: pointer;
}
.icon-pinglun {
    margin-left: 20px;
    font-size: 14px;
    color: black;
}
.icon-pinglun:hover {
    color: #409EFF;
    cursor: pointer;
}
.score {
    margin-left: 10px;
    display: inline-block;
}
.score-description {
    color: #409EFF;
    font-size: 14px;
}
.vote-button {
    background-color: #F7F7FC;
    width: 100%;
    border: none;
}
.vote-button:hover {
    cursor: pointer;
    background-color: #FEF0F0;
    color: #F56C6C;
}
.discuss-people {
    font-size: 14px; 
    opacity: 0.5; 
    margin: 10px 0;
}
.select-content {
    padding: 10px 20px;
    background-color: #F7F7FC;
    margin: 10px 0;
    font-size: 14px;
}
.selectedVote {
    cursor: pointer;
    background-color: rgba(6, 127, 248, 0.1);
}
.select-content:hover {
    cursor: pointer;
    background-color: rgba(6, 127, 248, 0.1);
}
.select-method {
    font-size: 14px;
    opacity: 0.7;
    font-weight: 550;
}
.vote-name {
    font-size: 14px;
    margin-right: 10px;
    font-weight: 550;
}
.vote-status {
    font-weight: 550;
    font-size: 16px;
    margin-right: 10px;
}
.vote-card {
    background-color: #F7F7FC;
    padding: 20px;
}
.publish-time {
    margin-left: 10px;
    font-size: 15px;
    opacity: 0.8;
}
.username-author {
    display: inline-block;
    vertical-align: middle;
    margin-left: 10px;
    font-size: 15px;
    opacity: 0.8;
    overflow: hidden;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
    text-overflow: ellipsis;
    white-space: nowrap;
    vertical-align: middle;
}
.username-author:hover {
    opacity: 0.5;
    cursor: pointer;
}
.article-picture {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    vertical-align: middle;
}
.article-picture:hover {
    opacity: 0.5;
    cursor: pointer;
}
.article-title {
    font-weight: 600;
    font-size: 24px;
    padding: 20px 20px 0 20px;
}
.MonkeyWebCommunityArticleViews-container {
    margin: 10px auto;
    width: 1300px;
}

</style>