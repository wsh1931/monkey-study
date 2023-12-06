<template>
    <div class="MonkeyWebCollectContent-container">
        <div v-loading="loadingContent">
            <div v-if="!showEditName" style="margin-bottom: 16px;">
                <div class="collect-title">{{ collectContent.name }}</div>
                <i @click="showEditNameInput(collectContent.name)" class="el-icon-edit-outline"></i>
            </div>
            <div v-if="showEditName" style="margin-bottom: 16px;">
                <el-input 
                v-model="edit.name"
                placeholder="请输入收藏标题"
                class="title-edit"
                size="mini"
                ></el-input>
                <i 
                @click="showEditName = false"
                class="el-icon-close icon-close"></i>
                <i
                @click="updateCollectName(edit.name)"
                class="el-icon-check icon-open"></i>
            </div>
            <div class="collect-brief-operation">
                <span v-if="!showEditDescription">
                    <div class="collect-brief">{{ collectContent.description }}</div>
                    <i @click="showEditDescriptionInput(collectContent.description)" class="el-icon-edit-outline"></i>
                </span>
                <span v-if="showEditDescription">
                    <el-input 
                    v-model="edit.description"
                    placeholder="请输入收藏简介"
                    class="title-edit"
                    size="mini"
                    ></el-input>
                    <i
                    @click="showEditDescription = false"
                    class="el-icon-close icon-close"></i>
                    <i 
                    @click="updateCollectDescription(edit.description)"
                    class="el-icon-check icon-open"></i>
                </span>
                <div class="operate">
                    <span @click="deleteCollectContent(collectContent)" class="delete-collect">删除收藏夹</span>
                    <span 
                    @click="setCollectPrivate()"
                    v-if="collectContent.isPrivate == '1'" 
                    class="private-collect">设为私密</span>
                    <span 
                    @click="setCollectPublic()"
                    v-if="collectContent.isPrivate == '0'" 
                    class="private-collect">设为公开</span>
                </div>
            </div>
        </div>
        <div class="divisor"></div>
        <!-- 收藏夹内容 -->
        <div style="position: relative;">
            <el-input
            v-model="keyword"
            size="small"
            class="search-collect"
            placeholder="请输入关键词"
            @keyup.native="keyUp($event, keyword)">
            <template v-slot:suffix>
            <i class="el-icon-search" @click="queryCollectConnectByKeyword(keyword)"></i>
            </template>
        </el-input>
        </div>
        
        <el-tabs v-model="activeName" @tab-click="handleClick">
            <el-tab-pane label="全部" name="all"></el-tab-pane>
            <el-tab-pane label="资源" name="resource"></el-tab-pane>
            <el-tab-pane label="社区文章" name="communityArticle"></el-tab-pane>
            <el-tab-pane label="课程" name="course"></el-tab-pane>
            <el-tab-pane label="问答" name="question"></el-tab-pane>
            <el-tab-pane label="博客" name="article"></el-tab-pane>
        </el-tabs>
        <div class="content-card" v-loading="loadingConnect">
            <div 
                @click="toOpusViews(collectContent)"
                v-for="collectContent in collectContentList" 
                :key="collectContent.id"
                class="collect-content-card">
                <el-tag 
                effect="plain"
                class="collect-connect-type"
                type="info" 
                size="mini">{{ collectType.getMsg(collectContent.type) }}</el-tag>
                <span class="collect-content-title">{{ collectContent.title }}</span>
                <el-tooltip 
                class="item collect" 
                effect="dark" 
                content="取消收藏" 
                placement="top"
                v-if="collectContent.isCollect == '1'">
                    <span class="iconfont icon-shoucang"></span>
                </el-tooltip>
                <el-tooltip 
                class="item cancel-collect" 
                effect="dark" 
                content="点击收藏" 
                placement="top"
                v-if="collectContent.isCollect == '0'">
                    <span class="iconfont icon-shoucang"></span>
                </el-tooltip>
            </div>
        </div>
        
            <PagiNation 
            class="pagination"
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
import PagiNation from "@/components/pagination/PagiNation.vue";
export default {
    name: 'MonkeyWebCollectContent',
    components: {
        PagiNation
    },
    data() {
        return {
            // 搜索关键字
            keyword: "",
            loadingConnect: false,
            // 收藏内容id
            collectContentId: "",
            loadingContent: false,
            currentPage: 1,
            pageSize: 10,
            totals: 0,
            activeName: "all",
            showEditName: false,
            showEditDescription: false,
            collectContent: {
                name: "",
                description: "",
                isPrivate: "",
            },
            edit: {
                name: "",
                description: "",
            },
            collectContentList: [],
            userCenterCenterUrl: "http://localhost:80/monkey-user/center/collect",
            communityArticleUrl: "http://localhost:80/monkey-community/article",
        };
    },
    created() {
        this.collectContentId = this.$route.params.collectContentId;
        this.queryCollectContentById(this.collectContentId);
        this.queryCollectContentConnectByType();
    },
    watch: {
        $route() {
            this.currentPage = 1;
            this.pageSize = 10;
            this.collectContentId = this.$route.params.collectContentId;
            this.queryCollectContentById(this.collectContentId);
            this.queryCollectContentConnectByType();
        }
    },
    methods: {
        // 前往对应的原文页面
        toOpusViews(collectContent) {
            if (collectContent.type == this.collectType.COLLECT_ARTICLE) {
                // 前往文章页面
                const { href } = this.$router.resolve({
                    name: "check_article",
                    params: {
                        articleId: collectContent.associateId,
                    }
                })
                window.open(href, '_black')
            } else if (collectContent.type == this.collectType.COLLECT_QUESTION) {
                // 前往问答页面
                const { href } = this.$router.resolve({
                    name: "question_reply",
                    params: {
                        questionId: collectContent.associateId,
                    }
                })
                window.open(href, '_black')
            } else if (collectContent.type == this.collectType.COLLECT_COURSE) {
                // 前往课程页面
                const { href } = this.$router.resolve({
                    name: "course_detail",
                    params: {
                        courseId: collectContent.associateId,
                    }
                })

                window.open(href, '_black')
            } else if (collectContent.type == this.collectType.COLLECT_COMMUNITY_ARTICLE) {
                // 前往社区文章页面
                const vue = this;
                $.ajax({
                    url: vue.communityArticleUrl + "/queryCommunityIdByArticleId",
                    type: "get",
                    headers: {
                        Authorization: "Bearer " + store.state.user.token,
                    },
                    data: {
                        communityArticleId: collectContent.associateId,
                    },
                    success(response) {
                        if (response.code == '200') {
                            const { href } = vue.$router.resolve({
                                name: "community_article",
                                params: {
                                    communityArticleId: collectContent.associateId,
                                    communityId: response.data,
                                }
                            })
                            window.open(href, "_black");
                        } else {
                            vue.$modal.msgError(response.msg);
                        }
                    }
                })
            } else if (collectContent.type == this.collectType.COLLECT_RESOURCE) {
                // 前往资源页面
                const { href } = this.$router.resolve({
                    name: "resource_detail",
                    params: {
                        resourceId: collectContent.associateId,
                    }
                })
                window.open(href, '_black')
            }
        },
        // 通过关键字搜索收藏内容列表
        queryCollectConnectByKeyword() {
            if (this.keyword == null || this.keyword == "") {
                this.$modal.msgWarning("搜索关键字不能为空");
                return false;
            }
            this.currentPage = 1;
            this.pageSize = 10;
            this.queryCollectContentConnectByType();
        },
        // 按回车搜索
        keyUp(event) {
            if (event.keyCode == '13') {
                this.queryCollectConnectByKeyword();
            }
        },
        // 设置收藏夹为私密收藏夹
        setCollectPrivate() {
            const vue = this;
            $.ajax({
                url: vue.userCenterCenterUrl + "/setCollectPrivate",
                type: "put",
                data: {
                    collectContentId: vue.collectContentId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.collectContent.isPrivate = '0';
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 设置收藏夹为公开收藏夹
        setCollectPublic() {
            const vue = this;
            $.ajax({
                url: vue.userCenterCenterUrl + "/setCollectPublic",
                type: "put",
                data: {
                    collectContentId: vue.collectContentId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.collectContent.isPrivate = '1';
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 删除收藏夹
        deleteCollectContent(collectContent) {
            this.$modal.confirm(`您确定要删除 ${collectContent.name} 文件夹吗?`)
                .then(() => {
                    const vue = this;
                    $.ajax({
                        url: vue.userCenterCenterUrl + "/deleteCollectContent",
                        type: "delete",
                        data: {
                            collectContentId: collectContent.id,
                        },
                        headers: {
                            Authorization: "Bearer " + store.state.user.token,
                        },
                        success(response) {
                            if (response.code == vue.ResultStatus.SUCCESS) {
                                vue.$modal.msgSuccess(response.msg);
                                vue.$router.push({
                                    name: "user_center_collect"
                                }).then(() => {
                                    window.location.reload();
                                })
                            } else {
                                vue.$modal.msgError(response.msg);
                            }
                        }
                    })
                }).catch(() => { });
        },
        // 更新收藏描述
        updateCollectDescription(description) {
            const vue = this;
            $.ajax({
                url: vue.userCenterCenterUrl + "/updateCollectDescription",
                type: "get",
                data: {
                    description,
                    collectContentId: vue.collectContentId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.collectContent.description = description;
                        vue.showEditDescription = false;
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 更新收藏目录标题
        updateCollectName(name) {
            const vue = this;
            $.ajax({
                url: vue.userCenterCenterUrl + "/updateCollectName",
                type: "get",
                data: {
                    name,
                    collectContentId: vue.collectContentId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.collectContent.name = name;
                        vue.showEditName = false;
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询收藏目录关系列表
        queryContentConnect(collectContentId, type) {
            this.loadingConnect = true;
            this.collectContentList = [];
            const vue = this;
            $.ajax({
                url: vue.userCenterCenterUrl + "/queryContentConnect",
                type: "get",
                data: {
                    keyword: vue.keyword,
                    collectContentId,
                    type,
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.collectContentList = response.data.records;
                        vue.totals = response.data.total;
                        vue.loadingConnect = false;
                        
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询收藏内容关系信息通过收藏类型
        queryCollectContentConnectByType() {
            if (this.activeName == 'all') {
                this.queryContentConnect(this.collectContentId, this.collectType.ALL);
            } else if (this.activeName == 'resource') {
                this.queryContentConnect(this.collectContentId, this.collectType.COLLECT_RESOURCE);
            } else if (this.activeName == 'communityArticle') {
                this.queryContentConnect(this.collectContentId, this.collectType.COLLECT_COMMUNITY_ARTICLE);
            } else if (this.activeName == 'course') {
                this.queryContentConnect(this.collectContentId, this.collectType.COLLECT_COURSE);
            } else if (this.activeName == 'question') {
                this.queryContentConnect(this.collectContentId, this.collectType.COLLECT_QUESTION);
            } else if (this.activeName == 'article') {
                this.queryContentConnect(this.collectContentId, this.collectType.COLLECT_ARTICLE);
            }
        },
        handleClick() {
            this.currentPage = 1;
            this.pageSize = 10;
            this.queryCollectContentConnectByType();
        },
        // 查询收藏目录信息通过收藏目录id
        queryCollectContentById(collectContentId) {
            this.loadingContent = true;
            const vue = this;
            $.ajax({
                url: vue.userCenterCenterUrl + "/queryCollectContentById",
                type: "get",
                data: {
                    collectContentId
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.collectContent = response.data;
                        vue.loadingContent = false;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        handleSizeChange(val) {
            this.pageSize = val;
            this.queryCollectContentConnectByType();
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            this.queryCollectContentConnectByType();
        },
        // 显示收藏描述输入框
        showEditDescriptionInput(description) {
            this.showEditDescription = true;
            this.edit.description = JSON.parse(JSON.stringify(description));
        },
        // 显示名称编辑框
        showEditNameInput(name) {
            this.showEditName = true;
            this.edit.name = JSON.parse(JSON.stringify(name));
        }
    },
};
</script>

<style scoped>
.el-icon-search {
    position: absolute;
    top: 8px;
    right: 0;
    font-size: 16px;
}
.el-icon-search:hover {
    cursor: pointer;
    color: #409EFF;
}
.collect {
    vertical-align: middle;
    position: absolute;
    right: 20px;
    top: 20px;
    font-size: 18px;
    color: orange;
}

.cancel-collect {
    vertical-align: middle;
    position: absolute;
    right: 20px;
    top: 20px;
    font-size: 18px;
}

::-webkit-scrollbar {
    width: 10px;
    background-color: #fff;
}

:hover ::-webkit-scrollbar-track-piece {
    background-color: #fff;
    border-radius: 6px;
}

:hover::-webkit-scrollbar-thumb:hover {
    background-color: rgba(0, 0, 0, 0.1);
}

:hover::-webkit-scrollbar-thumb:vertical {
    background-color: rgba(0, 0, 0, 0.1);
    border-radius: 6px;
    outline: 2px solid #fff;
    outline-offset: -2px;
    border: 2px solid #fff;
}

.content-card {
    padding: 5px;
    height: calc(100vh - 302px);
    overflow-y: auto;
}
.pagination {
    text-align: right;
}
.collect-content-card:nth-child(n + 1) {
    margin-bottom: 10px;
}
.collect-content-card:last-child {
    margin-bottom: 0;
}
.collect-content-card {
    padding: 16px;
    box-shadow: 0 0 5px rgba(0, 0, 0, 0.1);
    cursor: pointer;
    transition: 0.2s linear all;
    position: relative;
}
.collect-content-card:hover {
    box-shadow: 0 0 10px grey;
}
.collect-connect-type {
    margin-right: 5px;
    vertical-align: middle;
}
.collect-content-title:hover {
    color: #409EFF;
}
.collect-content-title {
    display: inline-block;
    color: gray;
    white-space: nowrap;
    text-overflow: ellipsis;
    vertical-align: middle;
    overflow: hidden;
    max-width: 540px;
    margin-right: 5px;
}
.search-collect {
    width: 200px;
    position: absolute;
    top: 0;
    right: 0;
    z-index: 10000;
}
.divisor {
    height: 1px;
    background-color: rgba(0, 0, 0, 0.1);
    margin-bottom: 10px;
}
.title-edit {
    width: 300px;
    margin-right: 10px;
}
.icon-open:hover {
    color: #409EFF;
}
.icon-open {
    cursor: pointer;
    font-size: 20px;
    transition: 0.2s linear all;
}
.icon-close:hover {
    color: #F56C6C;
}
.icon-close {
    margin-right: 10px;
    cursor: pointer;
    font-size: 20px;
    transition: 0.2s linear all;
}
.el-icon-edit-outline {
    cursor: pointer;
}
.private-collect:hover {
    opacity: 0.8;
}
.private-collect {
    color: #409EFF;
    cursor: pointer;
    font-size: 14px;
}
.delete-collect:hover {
    opacity: 0.8;
}
.delete-collect {
    cursor: pointer;
    color: #F56C6C;
    margin-right: 10px;
}
.operate {
    position: absolute;
    right: 0;
    top: 0;
}
.collect-brief-operation {
    position: relative;
    margin-bottom: 16px;
}
.collect-brief {
    display: inline-block;
    color: gray;
    white-space: nowrap;
    text-overflow: ellipsis;
    vertical-align: middle;
    overflow: hidden;
    max-width: 400px;
    margin-right: 10px;
}
.collect-title {
    display: inline-block;
    color: gray;
    font-weight: 550;
    white-space: nowrap;
    text-overflow: ellipsis;
    vertical-align: middle;
    overflow: hidden;
    max-width: 620px;
    margin-right: 10px;
}
.MonkeyWebCollectContent-container {
    padding: 20px;
    background-color: #fff;
    font-size: 14px;
    vertical-align: middle;
    min-height: calc(100vh - 121px);
}
</style>