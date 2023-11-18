<template>
    <div class="MonkeyWebSearchCenter-container">
        <el-row>
            <el-col :span="18">
                <el-tabs v-model="activeName" @tab-click="handleClick" class="el-tabs" type="card">
                    <el-tab-pane name="search_all">
                        <span slot="label">
                            <i class="iconfont icon-quanbu">&nbsp;</i>
                            <span>全站</span>
                        </span>
                    </el-tab-pane>
                    <el-tab-pane name="search_resource">
                        <span slot="label">
                            <i class="iconfont icon-ziyuan">&nbsp;</i>
                            <span>资源</span>
                        </span>
                    </el-tab-pane>
                    <el-tab-pane  name="search_community">
                        <span slot="label">
                            <i class="iconfont icon-shequ">&nbsp;</i>
                            <span>社区</span>
                        </span>
                    </el-tab-pane>
                    <el-tab-pane label="社区文章" name="search_community_article">
                        <span slot="label">
                            <i class="iconfont iconfont icon-shequ1">&nbsp;</i>
                            <span>社区文章</span>
                        </span>
                    </el-tab-pane>
                    <el-tab-pane label="课程" name="search_course">
                        <span slot="label">
                            <i class="iconfont icon-kecheng-">&nbsp;</i>
                            <span>课程</span>
                        </span>
                    </el-tab-pane>
                    <el-tab-pane name="search_question">
                        <span slot="label">
                            <i class="iconfont icon-tiwenquestion">&nbsp;</i>
                            <span>问答</span>
                        </span>
                    </el-tab-pane>
                    <el-tab-pane  name="search_article">
                        <span slot="label">
                            <i class="iconfont icon-wenzhang">&nbsp;</i>
                            <span>文章</span>
                        </span>
                    </el-tab-pane>
                    <el-tab-pane  name="search_user">
                        <span slot="label">
                            <i class="el-icon-user">&nbsp;</i>
                            <span>用户</span>
                        </span>
                    </el-tab-pane>
                    <router-view ref="children"></router-view>
                </el-tabs>
                
            </el-col>
            <el-col :span="6" style="padding-left: 10px;">
                <div style="background-color: #fff;">
                    <div class="user-top">
                        <span>相关搜索</span>
                            <span class="user-rank-tip"></span>
                    </div>

                    <div class="relate-search-card">
                        <span v-if="searchType == SearchType.USER">
                            <el-tag
                            @click="searchInfoByRelated(relatedSearch.username)"
                            v-for="relatedSearch in relatedSearchList" 
                            :key="relatedSearch.username" 
                            class="relate-search-tag"
                            type="info">{{ relatedSearch.username }}</el-tag>
                        </span>

                        <span v-else-if="searchType == SearchType.COMMUNITY">
                            <el-tag
                            @click="searchInfoByRelated(relatedSearch.name)"
                            v-for="relatedSearch in relatedSearchList" 
                            :key="relatedSearch.name" 
                            class="relate-search-tag"
                            type="info">{{ relatedSearch.name }}</el-tag>
                        </span>

                        <span v-else>
                            <el-tag
                            @click="searchInfoByRelated(relatedSearch.title)"
                            v-for="relatedSearch in relatedSearchList" 
                            :key="relatedSearch.title" 
                            class="relate-search-tag"
                            type="info">{{ relatedSearch.title }}</el-tag>
                        </span>
                    </div>
                </div>

                <div style="background-color: #fff; margin-top: 10px;">
                    <div class="latest-search">
                        <span>最近搜索</span>
                            <span class="user-rank-tip"></span>
                    </div>

                    <div class="relate-search-card">
                        <el-tag
                        @click="searchInfoByLatest(searchHistory.content)"
                        v-for="searchHistory in searchHistoryList" 
                        :key="searchHistory.id" 
                        class="latest-search-tag"
                        type="info">{{ searchHistory.content }}</el-tag>
                    </div>
                </div>
            </el-col>
        </el-row>
    </div>
</template>

<script>
import $ from 'jquery'
import store from '@/store';
export default {
    name: 'MonkeyWebSearchCenter',

    data() {
        return {
            activeName: "",
            searchType: "",
            searchCenterUrl: "http://localhost:80/monkey-search/search/center",
            // 相关搜索集合
            relatedSearchList: [],
            // 历史搜索集合
            searchHistoryList: [],
        };
    },            
    created() {
        this.activeName = this.$route.name;
        this.queryHistorySearch();
        if (this.activeName == 'search_all') {
            this.searchType = this.SearchType.ALL;
            this.queryRelatedSearch(this.$route.query.keyword, this.SearchType.ALL);
        } else if (this.activeName == 'search_resource') {
            this.searchType = this.SearchType.RESOURCE;
            this.queryRelatedSearch(this.$route.query.keyword, this.SearchType.RESOURCE);
        } else if (this.activeName == 'search_community_article') {
            this.searchType = this.SearchType.COMMUNITY_ARTICLE;
            this.queryRelatedSearch(this.$route.query.keyword, this.SearchType.COMMUNITY_ARTICLE);
        } else if (this.activeName == 'search_course') {
            this.searchType = this.SearchType.COURSE;
            this.queryRelatedSearch(this.$route.query.keyword, this.SearchType.COURSE);
        } else if (this.activeName == 'search_question') {
            this.searchType = this.SearchType.QUESTION;
            this.queryRelatedSearch(this.$route.query.keyword, this.SearchType.QUESTION);
        } else if (this.activeName == 'search_article') {
            this.searchType = this.SearchType.ARTICLE;
            this.queryRelatedSearch(this.$route.query.keyword, this.SearchType.ARTICLE);
        } else if (this.activeName == 'search_community') {
            this.searchType = this.SearchType.COMMUNITY;
            this.queryRelatedSearch(this.$route.query.keyword, this.SearchType.COMMUNITY);
        } else if (this.activeName == 'search_user') {
            this.searchType = this.SearchType.USER;
            this.queryRelatedSearch(this.$route.query.keyword, this.SearchType.USER);
        }
        
    },
    watch: {
        '$route.query.keyword'(newVal, oldVal) {
            this.keyword = newVal;
            this.insertHistorySearch(newVal);
            this.queryHistorySearch();
            if (this.activeName == 'search_all') {
                this.queryRelatedSearch(this.$route.query.keyword, this.SearchType.ALL);
            } else if (this.activeName == 'search_resource') {
                this.queryRelatedSearch(this.$route.query.keyword, this.SearchType.RESOURCE);
            } else if (this.activeName == 'search_community_article') {
                this.queryRelatedSearch(this.$route.query.keyword, this.SearchType.COMMUNITY_ARTICLE);
            } else if (this.activeName == 'search_course') {
                this.queryRelatedSearch(this.$route.query.keyword, this.SearchType.COURSE);
            } else if (this.activeName == 'search_question') {
                this.queryRelatedSearch(this.$route.query.keyword, this.SearchType.QUESTION);
            } else if (this.activeName == 'search_article') {
                this.queryRelatedSearch(this.$route.query.keyword, this.SearchType.ARTICLE);
            } else if (this.activeName == 'search_community') {
                this.queryRelatedSearch(this.$route.query.keyword, this.SearchType.COMMUNITY);
            } else if (this.activeName == 'search_user') {
                this.queryRelatedSearch(this.$route.query.keyword, this.SearchType.USER);
            }
        }
    },
    methods: {
        // 将搜索信息插入历史搜索
        insertHistorySearch(keyword) {
            const vue = this;
            $.ajax({
            url: vue.searchCenterUrl + "/insertHistorySearch",
            type: "post",
            data: {
                keyword,
            },
            headers: {
                Authorization: "Bearer " + store.state.user.token,
            },
            success(response) {
                if (response.code != vue.ResultStatus.SUCCESS) {
                vue.$modal.msgError(response.msg);
                }
            }
            })
        },
        // 点击最近搜素标签搜素信息
        searchInfoByLatest(keyword) {
            this.$router.push({
                name: this.activeName,
                query: {
                    keyword
                },
            })
        },
        // 点击相关搜索标签搜索信息
        searchInfoByRelated(keyword) {
            this.$router.push({
                name: this.activeName,
                query: {
                    keyword
                },
            })
        },
        // 查找相关搜索信息
        queryRelatedSearch(keyword, searchType) {
            const vue = this;
            $.ajax({
                url: vue.searchCenterUrl + "/queryRelatedSearch",
                type: "get",
                data: {
                    keyword,
                    searchType
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.relatedSearchList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询该登录用户最近搜索信息
        queryHistorySearch() {
            const vue = this;
            $.ajax({
                url: vue.searchCenterUrl + "/queryHistorySearch",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.searchHistoryList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        toSearchAllViews() {
            this.searchType = this.SearchType.ALL;
            this.queryRelatedSearch(this.$route.query.keyword, this.searchType);
            this.$router.push({
                name: "search_all",
                query: {
                    keyword: this.$route.query.keyword,
                }
            })
        },
        toSearchResourceViews() {
            this.searchType = this.SearchType.RESOURCE;
            this.queryRelatedSearch(this.$route.query.keyword, this.searchType);
            this.$router.push({
                name: "search_resource",
                query: {
                    keyword: this.$route.query.keyword,
                }
            })
        },
        toSearchCommunityArticleViews() {
            this.searchType = this.SearchType.COMMUNITY_ARTICLE;
            this.queryRelatedSearch(this.$route.query.keyword, this.searchType);
            this.$router.push({
                name: "search_community_article",
                query: {
                    keyword: this.$route.query.keyword,
                }
            })
        },
        toSearchCourseViews() {
            this.searchType = this.SearchType.COURSE;
            this.queryRelatedSearch(this.$route.query.keyword, this.searchType);
            this.$router.push({
                name: "search_course",
                query: {
                    keyword: this.$route.query.keyword,
                }
            })
        },
        toSearchQuestionViews() {
            this.searchType = this.SearchType.QUESTION;
            this.queryRelatedSearch(this.$route.query.keyword, this.searchType);
            this.$router.push({
                name: "search_question",
                query: {
                    keyword: this.$route.query.keyword,
                }
            })
        },
        toSearchArticleViews() {
            this.searchType = this.SearchType.ARTICLE;
            this.queryRelatedSearch(this.$route.query.keyword, this.searchType);
            this.$router.push({
                name: "search_article",
                query: {
                    keyword: this.$route.query.keyword,
                }
            })
        },
        toSearchCommunityViews() {
            this.searchType = this.SearchType.COMMUNITY;
            this.queryRelatedSearch(this.$route.query.keyword, this.searchType);
            this.$router.push({
                name: "search_community",
                query: {
                    keyword: this.$route.query.keyword,
                }
            })
        },
        toSearchUserViews() {
            this.searchType = this.SearchType.USER;
            this.queryRelatedSearch(this.$route.query.keyword, this.searchType);
            this.$router.push({
                name: "search_user",
                query: {
                    keyword: this.$route.query.keyword,
                }
            })
        },
        handleClick() {
            if (this.activeName == 'search_all') {
                this.toSearchAllViews();
            } else if (this.activeName == 'search_resource') {
                this.toSearchResourceViews();
            } else if (this.activeName == 'search_community_article') {
                this.toSearchCommunityArticleViews();
            } else if (this.activeName == 'search_course') {
                this.toSearchCourseViews();
            } else if (this.activeName == 'search_question') {
                this.toSearchQuestionViews();
            } else if (this.activeName == 'search_article') {
                this.toSearchArticleViews();
            } else if (this.activeName == 'search_community') {
                this.toSearchCommunityViews();
            } else if (this.activeName == 'search_user') {
                this.toSearchUserViews();
            }
        }
    },
};
</script>

<style scoped>
.el-tag.el-tag--info {
    color: black;
    font-weight: 500;
}
.relate-search-tag:hover {
    background-color: rgba(8,211,252, 0.7);
}
.latest-search-tag:hover {
    background-color: rgba(230,162,60);

}
.latest-search-tag {
    margin-right: 10px;
    margin-bottom: 10px;
    color: black;
    cursor: pointer;
    transition: 0.2s linear all;
}
.relate-search-tag {
    margin-right: 10px;
    margin-bottom: 10px;
    color: black;
    cursor: pointer;
    transition: 0.2s linear all;
}
.relate-search-card {
    padding: 10px;
}
.latest-search {
    display: inline-block;
    background-color: rgba(230,162,60);
    padding: 8px 20px;
    border-radius: 0 20px 20px 0;
    color: white;
    font-size: 15px;
}
.user-top {
    display: inline-block;
    background-color: rgba(8,211,252);
    padding: 8px 20px;
    border-radius: 0 20px 20px 0;
    color: white;
    font-size: 15px;
}
.user-rank-tip {
    cursor: pointer;
}
::v-deep .el-tabs__header {
    margin: 0;
}
.el-tabs {
    background-color: #fff;
    padding: 0 20px;
}
.MonkeyWebSearchCenter-container {
    margin: 10px auto;
    width: 1100px;
}
</style>