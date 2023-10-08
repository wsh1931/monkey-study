<template>
    <div class="MonkeyWebCommunityDetailViews-container">
        <el-row style="display: flex;">
            <el-col :span="4" class="left-aside">
                <div style="padding: 20px 20px 10px 20px;">
                        <el-input 
                        @keyup.native="searchCommunityByCommunityName(queryCommunity, $event)" 
                        v-model="queryCommunity" 
                        style="position: relative;" 
                        placeholder="搜索社区">
                            <template v-slot:suffix>
                                <i @click="searchCommunityByCommunityName(queryCommunity)" class="el-icon-search"></i>
                            </template>
                        </el-input>
                        <!-- 展示搜索社区内容 -->
                        <div v-if="isShowSearchCommunityList == '1'" 
                        v-for="searchCommunity in searchCommunityList" 
                        :key="searchCommunity.id" 
                        @click="flushNowView(searchCommunity.id)" 
                        :class="['show-community-data', {'show-selected-community': searchCommunity.id == selectedCommunityId}]">
                            <el-row >
                            <el-col :span="2">
                                <img class="left-img" :src="searchCommunity.photo" alt="">
                            </el-col>
                            <el-col :span="20" class="left-content">
                                {{ searchCommunity.name }}
                            </el-col>
                            </el-row>
                        </div>
                </div>
                
                <div v-if="isShowSearchCommunityList == '0'">
                    <div style="padding: 0px 20px 10px 20px;">
                        <div class="divider"></div>

                        <div @click="toCommunityViews()" class="to-community-view">社区首页 <span style="float: right;">></span></div>
                    </div>
                    <div style="padding: 0 10px;">
                        <!-- 我加入的社区 -->
                        <el-row>
                            <div @click="myAddCommunity()" class="my-add-community">
                                <el-row>
                                    <el-col :span="getMyAddCommunityFirstColumn">
                                        <span v-if="showCommunity == '1'" class="el-icon-caret-bottom">
                                        </span>
                                        <span v-else class="el-icon-caret-right"></span>
                                        <span v-if="loadMyAddCommunityList" class="el-icon-loading"></span>
                                    </el-col>
                                    <el-col :span="getMyAddCommunitySecondColumn">
                                        <span class="el-icon-folder-opened">&nbsp;我加入的社区&nbsp;
                                            <span style="color: gray;">{{ myAddCommunityCount }}</span>
                                        </span>
                                    </el-col>   
                                </el-row>
                            </div>
                            <!-- 展示我加入的社区 -->
                            <div 
                            @click="flushNowView(myAddCommunity.id)"
                            v-if="showCommunity == '1'" 
                            :class="['show-community-data', {'show-selected-community': myAddCommunity.id == selectedCommunityId}]"
                            v-for="myAddCommunity in myAddCommunityList" :key="myAddCommunity.id">
                            <el-row>
                                <el-col :span="2">
                                    <img class="left-img" :src="myAddCommunity.photo" alt="">
                                </el-col>
                                <el-col :span="20" class="left-content">
                                    {{ myAddCommunity.name }}
                                </el-col>
                            </el-row>
                            </div>
                        </el-row>
                        <!-- 我管理的社区 -->
                        <el-row>
                            <div @click="myManageCommunity()" class="my-add-community">
                                    <el-row>
                                    <el-col :span="getMyManageCommunityFirstColumn">
                                        <span v-if="showCommunity == '2'" class="el-icon-caret-bottom">
                                        </span>
                                        <span v-else class="el-icon-caret-right"></span>
                                        <span v-if="loadMyManageCommunityList" class="el-icon-loading"></span>
                                    </el-col>
                                    <el-col :span="getMyManageCommunitySecondColumn">
                                        <span class="el-icon-folder-opened">&nbsp;我管理的社区&nbsp;
                                            <span style="color: gray;">{{ myManageCommunityCount }}</span>
                                        </span>
                                    </el-col>   
                                </el-row>
                            </div>
                            <!-- 展示我管理的社区 -->
                            <div 
                            @click="flushNowView(myManageCommunity.id)"
                            v-if="showCommunity == '2'" 
                                :class="['show-community-data', {'show-selected-community': myManageCommunity.id == selectedCommunityId}]"
                                v-for="myManageCommunity in myManageCommunityList" :key="myManageCommunity.id">
                                <el-row>
                                    <el-col :span="2">
                                        <img class="left-img" :src="myManageCommunity.photo" alt="">
                                    </el-col>
                                    <el-col :span="20" class="left-content">
                                        {{ myManageCommunity.name }}
                                    </el-col>
                                </el-row>
                                </div>
                        </el-row>

                        <!-- 官方推荐社区 -->
                        <el-row>
                            <div @click="recommend()"  class="my-add-community">
                                <el-row>
                                    <el-col :span="getRecommendCommunityFirstColumn">
                                        <span v-if="showCommunity == '3'" class="el-icon-caret-bottom">
                                        </span>
                                        <span v-else class="el-icon-caret-right"></span>
                                        <span v-if="loadRecommendCommunityList" class="el-icon-loading"></span>
                                    </el-col>
                                    <el-col :span="getRecommendCommunitySecondColumn">
                                        <span class="el-icon-folder-opened">&nbsp;官方推荐&nbsp;
                                            <span style="color: gray;">{{ recommendCommunityCount }}</span></span>
                                    </el-col>   
                                </el-row>
                            </div>
                            <!-- 展示官方推荐社区 -->
                            <div 
                            @click="flushNowView(recommendCommunity.id)"
                            v-if="showCommunity == '3'" 
                            :class="['show-community-data', {'show-selected-community': recommendCommunity.id == selectedCommunityId}]"
                            v-for="recommendCommunity in recommendCommunityList" :key="recommendCommunity.id">
                                <el-row >
                                    <el-col :span="2">
                                        <img class="left-img" :src="recommendCommunity.photo" alt="">
                                    </el-col>
                                    <el-col :span="20" class="left-content">
                                        {{ recommendCommunity.name }}
                                    </el-col>
                                </el-row>
                            </div>
                        </el-row>

                        <el-row >
                            <div @click="otherCommunity()" class="my-add-community">
                            <el-row>
                                <el-col :span="getOtherCommunityFirstColumn">
                                    <span v-if="showCommunity == '4'" class="el-icon-caret-bottom">
                                    </span>
                                    <span v-else class="el-icon-caret-right"></span>
                                    <span v-if="loadOtherCommunityList" class="el-icon-loading"></span>
                                </el-col>
                                <el-col :span="getOtherCommunitySecondColumn">
                                    <span class="el-icon-folder-opened">&nbsp;其他社区&nbsp;
                                        <span style="color: gray;">{{ otherCommunityCount }}</span></span>
                                </el-col>   
                            </el-row>
                            </div>
                            <!-- 展示其他社区 -->
                            <el-row>
                                <div 
                                @click="flushNowView(otherCommunity.id)"
                                v-if="showCommunity == '4'" 
                                    :class="['show-community-data', {'show-selected-community': otherCommunity.id == selectedCommunityId}]"
                                    v-for="otherCommunity in otherCommunityList" :key="otherCommunity.id">
                                    <el-row >
                                        <el-col :span="2">
                                            <img class="left-img" :src="otherCommunity.photo" alt="">
                                        </el-col>
                                        <el-col :span="20" class="left-content">
                                            {{ otherCommunity.name }}
                                        </el-col>
                                    </el-row>
                                </div>
                            </el-row>
                        </el-row>
                    </div>
                </div>
            </el-col>
            <el-col :span="15" class="center-content">
                <div style="background-color: #fff; padding: 20px;">
                    <div class="article-content" @click="publishCommunityArticle(communityId)">
                        <span class="article-content-font">请您编写文章内容</span>
                        <el-button type="primary" class="publish-article" size="medium">发布文章</el-button>
                    </div>
                </div>

                <div class="community-channel">
                    <div style="font-size: 14px;">社区频道 {{ channelList.length }}</div>
                    <div>
                        <el-tag 
                        :class="['study-channel', {'selected-study-channel': selectedChannelName == channel.channelName}]" 
                        size="medium"
                        @click="selectedChannel(channel)"
                        v-for="channel in channelList" :key="channel.id">{{ channel.channelName }}</el-tag>
                    </div>
                </div>

                <div class="community-content">
                    <el-input 
                    @keyup.native="searchArticleContent(searchArticle, $event)"
                    v-model="searchArticle" 
                    class="searchArticle"  
                    placeholder="搜索文章">
                            <template v-slot:suffix>
                                <i @click="searchArticleContent(searchArticle)" class="el-icon-search"></i>
                            </template>
                        </el-input>
                    <el-tabs v-model="activeName" @tab-click="changeLabel()">
                        <el-tab-pane label="最新" name="latest"></el-tab-pane>
                        <el-tab-pane label="最热" name="hottest"></el-tab-pane>
                        <el-tab-pane label="评分" name="score"></el-tab-pane>
                        <el-tab-pane label="阅读量" name="viewCount"></el-tab-pane>
                        <el-tab-pane label="精选" name="excellent"></el-tab-pane>
                        <el-tab-pane label="置顶" name="top"></el-tab-pane>
                        <el-tab-pane label="点赞" name="like"></el-tab-pane>
                        <el-tab-pane label="收藏" name="collect"></el-tab-pane>
                        <el-tab-pane label="我的文章" name="myArticle"></el-tab-pane>
                        
                        <CommunityDetailCard
                        :communityArticleList="communityArticleList"/>
                        <PagiNation
                        style="text-align: right; margin-top: 20px;"
                        :totals="totals"
                        :currentPage="currentPage" 
                        :pageSize="pageSize" 
                        @handleCurrentChange = "handleCurrentChange"
                        @handleSizeChange="handleSizeChange"/>
                    </el-tabs>
                </div>
            </el-col>
            <el-col :span="5" style="display: flex; background-color: #fff;">
                <CommunityInfo
                style="padding: 10px;"/>
            </el-col>
        </el-row>
    </div>
</template>

<script>
import CommunityInfo from '@/components/community/CommunityInfo.vue';
import PagiNation from '@/components/pagination/PagiNation.vue';
import CommunityDetailCard from '@/components/community/CommunityDetailCard'
import $ from 'jquery'
import store from '@/store';
export default {
    name: 'MonkeyWebCommunityDetailViews',
    components: {
        CommunityDetailCard,
        PagiNation,
        CommunityInfo
    },
    data() {
        return {
            activeName: "latest",
            dark: "dark",
            // 加载我加入的社区集合
            loadMyAddCommunityList: false,
            // 加载我管理的社区集合
            loadMyManageCommunityList: false,
            // 加载官方推荐社区集合
            loadRecommendCommunityList: false,
            // 加载其他集合
            loadOtherCommunityList: false,
            // 社区频道集合
            channelList: [],
            // 被选中的社区id
            selectedCommunityId: "",
            // 是否显示搜索社区列表（0不表示不显示，1表示显示）
            isShowSearchCommunityList: '0',
            // 搜索社区列表
            searchCommunityList: [],
            // 我加入的社区数
            myAddCommunityCount: "",
            // 我管理的社区数
            myManageCommunityCount: "",
            // 官方推荐社区数
            recommendCommunityCount: "",
            // 其他社区数
            otherCommunityCount: "",
            // 我加入的社区集合
            myAddCommunityList: [],
            // 我管理的社区集合
            myManageCommunityList: [],
            // 官方推荐社区集合
            recommendCommunityList: [],
            // 其他社区集合
            otherCommunityList: [],
            // 定义动态css对象 和 滚动条隐藏控制变量
            scrollBoxStyle: {
                "--backgroundColor": "rgba(187,255,170,0)",
            },
            scrollHideFlag: false,
            // 搜索文章内容
            searchArticle: "",
            // 选中的频道id
            selectedChannelId: "",
            // 选中的频道名称
            selectedChannelName: "全部",
            // 社区id
            communityId: "",
            // 通过社区名搜索社区字段
            queryCommunity: "",
            // 点击展示的社区, 1为我加入的社区，2为我管理的社区，3为官方推荐
            showCommunity: "",
            currentPage: 1,
            pageSize: 10,
            totals: 0,
            // 社区文章列表
            communityArticleList: [],
            communityUrl: "http://localhost:80/monkey-community/community",
            communityDetailUrl: "http://localhost:80/monkey-community/community/detail",
            publishCommunityUrl: "http://localhost:80/monkey-community/publish",
        };
    },
    watch: {
        $route() {
            // 手动触发数据刷新
            this.communityId = this.$route.params.communityId;
            this.isShowSearchCommunityList = '0';
            this.selectedCommunityId = this.communityId;
            this.queryCommunityChannel(this.communityId);
            this.queryLatestArticleListByChannelIdAndCommunityId();
        },
        queryCommunity(newVal) {
            if (newVal == '') {
                this.isShowSearchCommunityList = '0';
            }
        },
        // 得到标签颜色
        selectedChannelName(newVal) {
            if (newVal == this.selectedChannelName) {
                return "dark";
            } else {
                return "plain";
            }
        },
    },
    computed: {
        // 计算我加入的社区加载时候的列
        getMyAddCommunityFirstColumn() {
            return this.loadMyAddCommunityList ? 4 : 2;
        },
        getMyAddCommunitySecondColumn() {
            return this.loadMyAddCommunityList ? 20 : 22;
        },
        // 计算我管理的社区集合时候的加载序列
        getMyManageCommunityFirstColumn() {
            return this.loadMyManageCommunityList ? 4 : 2;
        },
        getMyManageCommunitySecondColumn() {
            return this.loadMyManageCommunityList ? 20 : 22;
        },
        // 计算官方推荐社区集合的加载序列
        getRecommendCommunityFirstColumn() {
            return this.loadRecommendCommunityList ? 4 : 2;
        },
        getRecommendCommunitySecondColumn() {
            return this.loadRecommendCommunityList ? 20 : 22;
        },
        // 计算其他社区加载时候的列
        getOtherCommunityFirstColumn() {
            return this.loadOtherCommunityList ? 4 : 2;
        },
        getOtherCommunitySecondColumn() {
            return this.loadOtherCommunityList ? 20 : 22;
        }
    },
    created() {
        this.communityId = this.$route.params.communityId;
        this.selectedCommunityId = this.communityId;
        this.queryMyAddCommunityCount();
        this.queryMyManageCommunityCount();
        this.queryRecommendCommunityCount();
        this.queryOtherCommunityCount();
        this.queryCommunityChannel(this.communityId);
        this.queryLatestArticleListByChannelIdAndCommunityId();
    },
    methods: {
        // 通过搜索字段模糊搜索文章标题
        searchArticleContent(title, event) {
            if (event == null || event.keyCode == '13') {
                const vue = this;
                vue.pageSize = 10;
                vue.currentPage = 1;
                $.ajax({
                    url: vue.communityDetailUrl + "/searchArticleContent",
                    type: "get",
                    data: {
                        title,
                        currentPage: vue.currentPage,
                        pageSize: vue.pageSize,
                        communityId: vue.communityId,
                    },
                    success(response) {
                        if (response.code == '200') {
                            vue.communityArticleList = response.data.records;
                            vue.totals = response.data.total;
                        }
                    }
                })
            }
        },
        selectedChannel(channel) {
            this.selectedChannelName = channel.channelName
            this.selectedChannelId = channel.id
            this.queryLatestArticleListByChannelIdAndCommunityId();
        },
        // 得到最新文章列表
        queryLatestArticleListByChannelIdAndCommunityId() {
            const vue = this;
            $.ajax({
                url: vue.communityDetailUrl + "/queryLatestArticleList/ByChannelId/CommunityId",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    communityId: vue.communityId,
                    channelId: vue.selectedChannelId,
                    channelName: vue.selectedChannelName,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.communityArticleList = response.data.records;
                        vue.totals = response.data.total;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 得到最热文章列表
        queryHottestArticleListByChannelIdAndCommunityId() {
            const vue = this;
            $.ajax({
                url: vue.communityDetailUrl + "/queryHottestArticleList/ByChannelId/CommunityId",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    communityId: vue.communityId,
                    channelId: vue.selectedChannelId,
                    channelName: vue.selectedChannelName,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.communityArticleList = response.data.records;
                        vue.totals = response.data.total;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 得到评分降序列表
        queryScoreArticleListByChannelIdAndCommunityId() {
            const vue = this;
            $.ajax({
                url: vue.communityDetailUrl + "/queryScoreArticleList/ByChannelId/CommunityId",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    communityId: vue.communityId,
                    channelId: vue.selectedChannelId,
                    channelName: vue.selectedChannelName,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.communityArticleList = response.data.records;
                        vue.totals = response.data.total;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 得到阅读量文章列表
        queryViewsArticleListByChannelIdAndCommunityId() {
            const vue = this;
            $.ajax({
                url: vue.communityDetailUrl + "/queryViewsArticleList/ByChannelId/CommunityId",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    communityId: vue.communityId,
                    channelId: vue.selectedChannelId,
                    channelName: vue.selectedChannelName,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.communityArticleList = response.data.records;
                        vue.totals = response.data.total;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 得到精选文章列表
        queryExcellentArticleListByChannelIdAndCommunityId() {
            const vue = this;
            $.ajax({
                url: vue.communityDetailUrl + "/queryExcellentArticleList/ByChannelId/CommunityId",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    communityId: vue.communityId,
                    channelId: vue.selectedChannelId,
                    channelName: vue.selectedChannelName,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.communityArticleList = response.data.records;
                        vue.totals = response.data.total;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 得到置顶文章列表
        queryTopArticleListByChannelIdAndCommunityId() {
            const vue = this;
            $.ajax({
                url: vue.communityDetailUrl + "/queryTopArticleList/ByChannelId/CommunityId",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    communityId: vue.communityId,
                    channelId: vue.selectedChannelId,
                    channelName: vue.selectedChannelName,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.communityArticleList = response.data.records;
                        vue.totals = response.data.total;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 得到点赞排序文章列表
        queryLikeArticleListByChannelIdAndCommunityId() {
            const vue = this;
            $.ajax({
                url: vue.communityDetailUrl + "/queryLikeArticleList/ByChannelId/CommunityId",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    communityId: vue.communityId,
                    channelId: vue.selectedChannelId,
                    channelName: vue.selectedChannelName,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.communityArticleList = response.data.records;
                        vue.totals = response.data.total;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 得到收藏排序文章列表
        queryCollectArticleListByChannelIdAndCommunityId() {
            const vue = this;
            $.ajax({
                url: vue.communityDetailUrl + "/queryCollectArticleList/ByChannelId/CommunityId",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    communityId: vue.communityId,
                    channelId: vue.selectedChannelId,
                    channelName: vue.selectedChannelName,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.communityArticleList = response.data.records;
                        vue.totals = response.data.total;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 得到我的文章列表
        queryWithMeCommunityListByCommunityIdAndChannelId() {
            const vue = this;
            $.ajax({
                url: vue.communityUrl + "/queryWithMetArticleList/ByChannelId/CommunityId",
                type: "get",
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                    communityId: vue.communityId,
                    channelId: vue.selectedChannelId,
                    channelName: vue.selectedChannelName,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.communityArticleList = response.data.records;
                        vue.totals = response.data.total;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 改变标签时执行
        changeLabel() {
            this.currentPage = 1;
            this.pageSize = 10;
            if (this.activeName == 'latest') {
                this.queryLatestArticleListByChannelIdAndCommunityId();
            } else if (this.activeName == 'hottest') {
                this.queryHottestArticleListByChannelIdAndCommunityId();
            } else if (this.activeName == 'score') {
                this.queryScoreArticleListByChannelIdAndCommunityId();
            } else if (this.activeName == 'viewCount') {
                this.queryViewsArticleListByChannelIdAndCommunityId();
            } else if (this.activeName == 'excellent') {
                this.queryExcellentArticleListByChannelIdAndCommunityId();
            } else if (this.activeName == 'top') {
                this.queryTopArticleListByChannelIdAndCommunityId();
            } else if (this.activeName == 'like') {
                this.queryLikeArticleListByChannelIdAndCommunityId();
            } else if (this.activeName == 'collect') {
                this.queryCollectArticleListByChannelIdAndCommunityId();
            } else if (this.activeName == 'myArticle') {
                this.queryWithMeCommunityList();
            }
        },
        handleSizeChange(val) {
            this.pageSize = val;
            if (this.activeName == 'latest') {
                this.queryLatestArticleListByChannelIdAndCommunityId();
            } else if (this.activeName == 'hottest') {
                this.queryHottestArticleListByChannelIdAndCommunityId();
            } else if (this.activeName == 'score') {
                this.queryScoreArticleListByChannelIdAndCommunityId();
            } else if (this.activeName == 'viewCount') {
                this.queryViewsArticleListByChannelIdAndCommunityId();
            } else if (this.activeName == 'excellent') {
                this.queryExcellentArticleListByChannelIdAndCommunityId();
            } else if (this.activeName == 'top') {
                this.queryTopArticleListByChannelIdAndCommunityId();
            } else if (this.activeName == 'like') {
                this.queryLikeArticleListByChannelIdAndCommunityId();
            } else if (this.activeName == 'collect') {
                this.queryCollectArticleListByChannelIdAndCommunityId();
            } else if (this.activeName == 'myArticle') {
                this.queryWithMeCommunityList();
            }
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            if (this.activeName == 'latest') {
                this.queryLatestArticleListByChannelIdAndCommunityId();
            } else if (this.activeName == 'hottest') {
                this.queryHottestArticleListByChannelIdAndCommunityId();
            } else if (this.activeName == 'score') {
                this.queryScoreArticleListByChannelIdAndCommunityId();
            } else if (this.activeName == 'viewCount') {
                this.queryViewsArticleListByChannelIdAndCommunityId();
            } else if (this.activeName == 'excellent') {
                this.queryExcellentArticleListByChannelIdAndCommunityId();
            } else if (this.activeName == 'top') {
                this.queryTopArticleListByChannelIdAndCommunityId();
            } else if (this.activeName == 'like') {
                this.queryLikeArticleListByChannelIdAndCommunityId();
            } else if (this.activeName == 'collect') {
                this.queryCollectArticleListByChannelIdAndCommunityId();
            } else if (this.activeName == 'myArticle') {
                this.queryWithMeCommunityList();
            }
        },
        // 查询社区频道集合
        queryCommunityChannel(communityId) {
            const vue = this;
            $.ajax({
                url: vue.communityDetailUrl + "/queryCommunityChannel",
                type: "get",
                data: {
                    communityId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.channelList = response.data;
                        vue.selectedChannelName = vue.channelList[0].channelName;
                        vue.selectedChannelId = vue.channelList[0].id;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 通过社区名搜索社区
        searchCommunityByCommunityName(communityName, event) {
            const vue = this;
            if (event == null || event.keyCode == '13') {
                $.ajax({
                    url: vue.communityDetailUrl + "/searchCommunityByCommunityName",
                    type: "get",
                    data: {
                        communityName
                    },
                    success(response) {
                        if (response.code == '200') {
                            vue.searchCommunityList = response.data;
                            vue.isShowSearchCommunityList = '1';
                        } else {
                            vue.$modal.msgError(response.msg);
                        }
                    }
                })
            }
            
        },
        toCommunityViews() {
            const { href } = this.$router.resolve({
                name: "community"
            })

            window.open(href, "_blank")
        },
        // 选中了其他社区刷新当前页
        flushNowView(communityId) {
            this.$router.push({
                name: "community_detail",
                params: {
                    communityId
                }
            })
        },
        // 得到我加入的社区数量
        queryMyAddCommunityCount() {
            const vue = this;
            $.ajax({
                url: vue.communityDetailUrl + '/queryMyAddCommunityCount',
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.myAddCommunityCount = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 得到我管理的社区数
        queryMyManageCommunityCount() {
            const vue = this;
            $.ajax({
                url: vue.communityDetailUrl + '/queryMyManageCommunityCount',
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.myManageCommunityCount = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 得到官方推荐社区数
        queryRecommendCommunityCount() {
            const vue = this;
            $.ajax({
                url: vue.communityDetailUrl + '/queryRecommendCommunityCount',
                type: "get",
                success(response) {
                    if (response.code == '200') {
                        vue.recommendCommunityCount = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 得到其他社区数
        queryOtherCommunityCount() {
            const vue = this;
            $.ajax({
                url: vue.communityDetailUrl + '/queryOtherCommunityCount',
                type: "get",
                success(response) {
                    if (response.code == '200') {
                        vue.otherCommunityCount = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 发表社区文章
        publishCommunityArticle(communityId) {
            const { href } = this.$router.resolve({
                name: "publish_community_article",
                params: {
                    communityId,
                },
            })

        window.open(href, "_blank");
        },
        // 我加入的社区 
        myAddCommunity() {
            if (this.showCommunity != 1) {
                this.showCommunity = 1;
                // 防止多次查询
                if (this.myAddCommunityList.length == '0') {
                    this.queryMyAddCommunityList();
                }
            } else {
                this.showCommunity = -1;
            }
        },
        // 查询我加入的社区集合
        queryMyAddCommunityList() {
            this.loadMyAddCommunityList = true;
            const vue = this;
            $.ajax({
                url: vue.communityDetailUrl + '/queryMyAddCommunityList',
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.myAddCommunityList = response.data;
                        vue.loadMyAddCommunityList = false;
                        
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 我管理的社区
        myManageCommunity() {
            if (this.showCommunity != 2) {
                this.showCommunity = 2;
                // 防止多次查询
                if (this.myManageCommunityList.length == '0') {
                    this.queryMyManegeCommunityList();
                }
            } else {
                this.showCommunity = -1;
            }
            
        },
        // 查询我管理的社区集合
        queryMyManegeCommunityList() {
            this.loadMyManageCommunityList = true;
            const vue = this;
            $.ajax({
                url: vue.communityDetailUrl + '/queryMyManegeCommunityList',
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.myManageCommunityList = response.data;
                        vue.loadMyManageCommunityList = false;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 推荐社区
        recommend() {
            if (this.showCommunity != 3) {
                this.showCommunity = 3;
                // 防止多次查询
                if (this.recommendCommunityList.length == '0') {
                    this.queryRecommendCommunityList();
                }
            } else {
                this.showCommunity = -1;
            }
            
        },
        // 查询推荐社区集合
        queryRecommendCommunityList() {
            this.loadRecommendCommunityList = true;
            const vue = this;
            $.ajax({
                url: vue.communityDetailUrl + '/queryRecommendCommunityList',
                type: "get",
                success(response) {
                    if (response.code == '200') {
                        vue.recommendCommunityList = response.data;
                        vue.loadRecommendCommunityList = false;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 其他社区
        otherCommunity() {
            if (this.showCommunity != 4) {
                this.showCommunity = 4;
                // 防止多次查询
                if (this.otherCommunityList.length == '0') {
                    this.queryOtherCommunityListList();
                }
            } else {
                this.showCommunity = -1;
            }
        },
        // 查询其他社区集合
        queryOtherCommunityListList() {
            this.loadOtherCommunityList = true;
            const vue = this;
            $.ajax({
                url: vue.communityDetailUrl + '/queryOtherCommunityListList',
                type: "get",
                success(response) {
                    if (response.code == '200') {
                        vue.otherCommunityList = response.data;
                        vue.loadOtherCommunityList = false;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
    },
};
</script>

<style scoped>

.searchArticle {
    position: absolute; 
    width: 22%; 
    right: 0; 
    z-index: 5;
}
.search-function {
    position: absolute;
    right: 10px;
    top: 22px;
    width: 24%;
    z-index: 2;
}
.community-channel {
    background-color: #fff; 
    margin-top: 10px; 
    padding: 20px;
}
.community-content {
    position: relative;
    background-color: #fff; 
    margin-top: 10px; 
    padding: 20px;
}
.study-channel {
    margin-right: 10px;
    margin-top: 10px;
}
.selected-study-channel {
    background-color: #409EFF;
    color: #fff;
    cursor: pointer;
}
.study-channel:hover {
    background-color: #409EFF;
    color: #fff;
    cursor: pointer;
}
.center-content {
    margin-right: 2px;
    position: relative;
}
.publish-article {
    position: absolute;
    right: 10px;
    background-color: #66B1FF;
    border-radius: 20px;
    top: 12px;
}
.article-content-font {
    color: #777888;
}
.article-content {
    padding: 15px;
    height: 26px;
    line-height: 26px;
    background-color: rgba(232,232,237,.8);
    border-radius: 8px;
    position: relative;
    cursor: pointer;
}
.article-content:hover {
    .publish-article {
        background-color: #409EFF;
    }
}
.left-content {
    line-height: 20px;
    padding-left: 10px;
    font-size: 14px;
    overflow: hidden;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
    text-overflow: ellipsis;
    white-space: nowrap;
    vertical-align: middle;
}
.show-community-data {
    font-size: 14px;
    width: 100%;
    margin-bottom: 10px;
    padding: 5px 0 5px 20px;
    margin-top: 10px;
}

.show-selected-community {
    cursor: pointer;
    background-color: #cccccd;
}
.show-community-data:hover {
    cursor: pointer;
    background-color: #cccccd;
}
.left-img {
    height: 20px;
    width: 20px;
    cursor: pointer;
}
@keyframes slow-arrow {
    0% {
        opacity: 0;
    } 
    100% {
        opacity: 1;
    }
}
.el-icon-caret-right {
    animation: slow-arrow 0.4s linear;
}
.el-icon-folder-opened {
    animation: slow-arrow 0.4s linear;
}
.selected-community-label {
    cursor: pointer;
    background-color: #F5F6F7;
}
.my-add-community {
    font-size: 14px;
    padding: 5px 10px 5px 10px;
    width: 100%;
}
.my-add-community:hover {
    cursor: pointer;
    background-color: #F5F6F7;
}
.to-community-view {
    font-size: 20px;
    cursor: pointer;
}
.to-community-view:hover {
    opacity: 0.5;
}
.left-aside {
    position: relative;
    background-color: #fff;
    margin-right: 2px;
    max-width: 100%;
}
.divider {
    height: 1px;
    background: #e8e8ed;
    margin: 20px 0;
}
.MonkeyWebCommunityDetailViews-container {
    margin: 0 auto;
    width: 1300px;
}
.el-icon-search {
    position: absolute;
    top: 30%;
    right: 0;
    font-size: 16px;
}
.el-icon-search:hover {
    cursor: pointer;
    color: #409EFF;
}
    
</style>