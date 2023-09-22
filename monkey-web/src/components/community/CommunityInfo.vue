<template>
    <div class="MonkeyWebCommunityInfo-container">
        <div v-if="isShowScore">
            <div class="article-score-title">文章内容评分</div>
            <div class="divider"></div>
            <div style="padding: 16px;">
                <el-row>
                    <el-col :span="6">
                        <span class="article-score">{{ articleScore.totalScore }}</span>
                    </el-col>
                    <el-col :span="18">
                        <el-rate
                            v-model="articleScore.totalScore"
                            disabled
                            text-color="#ff9900"
                            score-template="{value}">
                        </el-rate>
                        <el-button type="text" style="font-size: 16px;">{{ articleScore.scoreCount }} 个用户评价</el-button>
                    </el-col>
                <el-row>
                    <el-col :span="3">
                        <span style="font-size: 14px;">1星</span>
                    </el-col>
                    <el-col :span="21">
                        <el-progress color="#FEDDD5"
                            text-color="black" 
                            :text-inside="true" 
                            :stroke-width="20" 
                            :percentage="articleScore.oneStar" 
                            show-text>
                        </el-progress>
                    </el-col>
                </el-row>
                <el-row style="margin-top: 5px;">
                    <el-col :span="3">
                        <span style="font-size: 14px;">2星</span>
                    </el-col>
                    <el-col :span="21">
                        <el-progress color="#FEDDD5"
                            text-color="black" 
                            :text-inside="true" 
                            :stroke-width="20" 
                            :percentage="articleScore.twoStar" 
                            show-text>
                        </el-progress>
                    </el-col>
                </el-row>
                <el-row style="margin-top: 5px;">
                    <el-col :span="3">
                        <span style="font-size: 14px;">3星</span>
                    </el-col>
                    <el-col :span="21">
                        <el-progress color="#FEDDD5"
                            text-color="black" 
                            :text-inside="true" 
                            :stroke-width="20" 
                            :percentage="articleScore.threeStar" 
                            show-text>
                        </el-progress>
                    </el-col>
                </el-row>
                <el-row style="margin-top: 5px;">
                    <el-col :span="3">
                        <span style="font-size: 14px;">4星</span>
                    </el-col>
                    <el-col :span="21">
                        <el-progress color="#FEDDD5"
                            text-color="black" 
                            :text-inside="true" 
                            :stroke-width="20" 
                            :percentage="articleScore.fourStar" 
                            show-text>
                        </el-progress>
                    </el-col>
                </el-row>
                <el-row style="margin-top: 5px;">
                    <el-col :span="3">
                        <span style="font-size: 14px;">5星</span>
                    </el-col>
                    <el-col :span="21">
                        <el-progress color="#FEDDD5"
                            text-color="black" 
                            :text-inside="true" 
                            :stroke-width="20" 
                            :percentage="articleScore.fiveStar" 
                            show-text>
                        </el-progress>
                    </el-col>
                </el-row>
                </el-row>
            </div>
        </div>
        <div class="header">
            <div>
                <img class="community-img" :src="communityInfo.photo" alt="">
                <span class="community-name">{{ communityInfo.name }}</span>
            </div>
            <el-row style="text-align: center; margin-top: 16px;">
                <el-col :span="11" class="community-member-count">
                    <el-row>{{ communityInfo.peopleCount }}</el-row>
                    <el-row class="community-member-content">社区成员</el-row>
                </el-col>
                <el-col :span="2">&nbsp;</el-col>
                <el-col :span="11" class="community-content-count">
                    <el-row>{{ communityInfo.articleCount }}</el-row>
                    <el-row class="community-member-content">文章总数</el-row>
                </el-col>
            </el-row>
            <el-row class="community-function" >
                <el-col :span="8">
                    <div class="publish-article" @click="toPublishArticleViews(communityInfo.id)">
                        <div class="el-icon-s-promotion"></div>
                        <div>发布帖子</div>
                    </div>
                </el-col>
                <el-col :span="8">
                    <div class="publish-article" @click="toCreateCommunityViews()">
                        <div class="el-icon-s-custom"></div>
                        <div>创建社区</div>
                    </div>
                </el-col>
                <el-col :span="8">
                    <div class="publish-article" @click="toCommunityViews()">
                        <div class="el-icon-s-home"></div>
                        <div>社区主页</div>
                    </div>
                </el-col>
                
            </el-row>

            <div>
                <span v-for="label in labelList" :key="label.id" class="community-label">{{ label.name }}</span>
            </div>

            <el-row class="divider"></el-row>
            <div style="font-size: 14px;">
                <div>社区描述</div>
                <div class="community-description">{{ communityInfo.description }}</div>
                <div style="margin-bottom: 10px;">社区管理员</div>
                <div>
                    <el-tooltip 
                    class="item" 
                    effect="dark" 
                    :content="manager.username" 
                    placement="top"
                    v-for="manager in managerList" :key="manager.id">
                        <img class="manager-img" @click="toUserViews(manager.id)" :src="manager.photo" alt="">
                    </el-tooltip>
                    
                </div>
                <div style="text-align: center;">
                    <el-button 
                    v-if="isManager == '1'" 
                    type="info" 
                    round 
                    plain 
                    size="mini" 
                    class="management-button">管理
                    </el-button>
                    <el-button 
                    v-else-if="inCommunity == '0' && status != '0' && status != '1'" 
                    type="info" 
                    round 
                    plain 
                    size="mini" 
                    class="management-button"
                    @click="applicationAddCommunity(communityInfo)">加入社区
                    </el-button>
                    <el-button 
                    v-else-if="inCommunity == '1' && status == '1'" 
                    type="info" 
                    round 
                    plain 
                    size="mini" 
                    class="management-button"
                    @click="turnOutCommunity(communityInfo.id)">退出社区
                    </el-button>
                    <el-button 
                    v-else-if="inCommunity == '0' && status == '0'" 
                    type="info" 
                    round 
                    plain 
                    size="mini" 
                    class="management-button"
                    disabled>正在审核
                    </el-button>
                </div>

                <div style="margin-top: 10px;">
                    <div style="font-weight: bold;">社区公告</div>
                    <div style="color: gray; margin-top: 5px;">{{ communityInfo.notice }}</div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import $ from 'jquery'
import store from '@/store';
export default {
    name: 'MonkeyWebCommunityInfo',
    props: ['isShowScore'],
    data() {
        return {
            score: '4',
            // 社区id
            communityId: "",
            // 社区基本信息
            communityInfo: {},
            // 社区标签列表
            labelList: [],
            // 社区管理员列表
            managerList: [],
            // 判断当前登录用户是否是管理员(0表示不是，1表示是)
            isManager: '0',
            // 判断当前用户是否在社区（0表示不在，1表示在）
            inCommunity: '0',
            // 当前申请状态（0申请中，1已同意申请）
            status: '',
            // 课程评分内容
            articleScore: {},
            communityBaseInfoUrl: "http://localhost:80/monkey-community/community/baseInfo",
            communityUrl: "http://localhost:80/monkey-community/community",
            communityArticleUrl: "http://localhost:80/monkey-community/article",
        };
    },
    created() {
        this.communityId = this.$route.params.communityId;
        this.communityArticleId = this.$route.params.communityArticleId;
        this.queryCommunityLabelList(this.communityId);
        this.queryCommunityManagerList(this.communityId);
        this.queryCommunityBaseInfoByCommunityId(this.communityId);
        this.judgeUserIsCommunityManagerAndIsInCommunity(this.communityId);
        if (this.communityArticleId != null && this.communityArticleId != "") {
            this.queryCommunityArticleScore(this.communityArticleId)
        }
        
    },
    methods: {
        // 查询文章评分内容
        queryCommunityArticleScore(communityArticleId) {
            const vue = this;
            $.ajax({
                url: vue.communityArticleUrl + "/queryCommunityArticle/score",
                type: "get",
                data: {
                    communityArticleId,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.articleScore = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
        },
        // 加入社区功能实现
        applicationAddCommunity(community) {
            const vue = this;
            $.ajax({
                url: vue.communityUrl + '/applicationAddCommunity',
                type: "post",
                data: {
                    community: JSON.stringify(community)
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        if (vue.communityInfo.enterWay == '1') {
                            vue.status = '0';
                        } else {
                            vue.inCommunity = '1';
                            vue.status = '1';
                        }
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 退出社区功能实现
        turnOutCommunity(communityId) {
            const vue = this;
            $.ajax({
                url: vue.communityUrl + '/turnOutCommunity',
                type: "delete",
                data: {
                    communityId
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.inCommunity = '0';
                        vue.status = "";
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 判断当前登录用户是否是社区管理员以及判断该用户是否在该社区
        judgeUserIsCommunityManagerAndIsInCommunity(communityId) {
            const vue = this;
            $.ajax({
                url: vue.communityBaseInfoUrl + "/judgeUserIsCommunityManager/AndIsInCommunity",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    communityId,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.isManager = response.data.isManager;
                        vue.inCommunity = response.data.inCommunity;
                        vue.status = response.data.status;
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

            window.open(href, "_blank");
        },
        // 得到社区管理员列表
        queryCommunityManagerList(communityId) {
            const vue = this;
            $.ajax({
                url: vue.communityBaseInfoUrl + "/queryCommunityManagerList",
                type: "get",
                data: {
                    communityId
                },
                success(response) {
                    if (response.code == '200') {
                        vue.managerList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 得到社区标签列表
        queryCommunityLabelList(communityId) {
            const vue = this;
            $.ajax({
                url: vue.communityBaseInfoUrl + "/queryCommunityLabelList",
                type: "get",
                data: {
                    communityId
                },
                success(response) {
                    if (response.code == '200') {
                        vue.labelList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 前往社区主页
        toCommunityViews() {
            this.$router.push({
                name: "community",
            })
        },
        // 前往创建社区界面
        toCreateCommunityViews() {
            const { href } = this.$router.resolve({
                name: "community_create",
            })

            window.open(href, "_blank");
        },
        // 前往发布文章页面
        toPublishArticleViews(communityId) {
            const { href } = this.$router.resolve({
                name: "publish_community_article",
                params: {
                    communityId
                }
            })

            window.open(href, "_blank");
        },
        // 通过社区id查询社区基本信息
        queryCommunityBaseInfoByCommunityId(communityId) {
            const vue = this;
            $.ajax({
                url: vue.communityBaseInfoUrl + "/queryCommunityBaseInfo/ByCommunityId",
                type: "get",
                data: {
                    communityId,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.communityInfo = response.data;
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
.article-score {
    font-weight: 600;
    font-size: 34px;
    color: #F56C6C;
}
.article-score-title {
    font-weight: 600;
    padding: 16px;
}
.community-description {
    font-size: 13px;
    color: gray;
    margin: 10px 0;
}
.management-button {
    width: 100%;
    margin-top: 10px;
}
.manager-img {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    margin: 0 10px 0 0;
}
.manager-img:hover {
    cursor: pointer;
    opacity: 0.5;
}
.divider {
    height: 1px;
    background: #e8e8ed;
    margin: 10px 0;
}
.community-label {
    display: inline-block;
    padding: 0 8px;
    height: 24px;
    background: #f0f0f2;
    border-radius: 6px;
    line-height: 24px;
    font-size: 14px;
    font-family: PingFangSC-Regular,PingFang SC;
    font-weight: 400;
    color: #555666;
    margin: 8px 8px 8px 0;
}
.el-icon-s-custom {
    padding: 14px;
    background-color: #E1F3D8;
    color: #67C23A;
    border-radius: 50%;
    font-size: 20px;
}
.el-icon-s-home {
    padding: 14px;
    background-color: #FAECD8;
    color: #E6A23C;
    border-radius: 50%;
    font-size: 20px;
}
.el-icon-s-data {
    padding: 14px;
    color: #F56C6C;
    background-color: #FDE2E2;
    border-radius: 50%;
    font-size: 20px;
}
.publish-article:hover {
    opacity: 0.5;
    cursor: pointer;
}
.el-icon-s-promotion {
    padding: 14px;
    background-color: #D9ECFF;
    color: #53A8FF;
    border-radius: 50%;
    font-size: 20px;
}

.community-function {
    margin-top: 16px;
    font-size: 12px;
    padding: 10px;
    background-color: #F5F6F7;
    text-align: center;
}
.community-member-content {
    font-size: 14px;
    color: gray;
}
.community-content-count {
    padding: 10px;
    background-color: #F5F6F7;
}
.community-member-count {
    padding: 10px;
    background-color: #F5F6F7;
}
.community-name {
    vertical-align: middle;
    font-size: 14px;
    text-align: left;
    font-weight: bold;
    max-width: 180px;
    display: inline-block;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
    
}
.community-img {
    width: 40px;
    height: 40px;
    vertical-align: middle;
    margin-right: 10px;
}
.community-img:hover {
    cursor: pointer;
    opacity: 0.5;
}
.header {
    padding: 16px; 
    min-height: 89vh;
    border: 1px solid rgba(0, 0, 0, 0.1);
}
.MonkeyWebCommunityInfo-container {
    background-color: #fff;
    min-width: 100%; 
    max-width: 100%;
}

</style>