<template>
    <div class="MonkeyWebUserCreateHome-container">
        <div class="header-first">
            <div class="achievement-card">
                <el-row>
                    <el-col :span="8">
                        <i class="iconfont icon-wenzhang opus-icon"></i>
                    </el-col>
                    <el-col :span="16" class="achievement-right">
                        <div class="opus-title">原文数</div>
                        <div class="opus-count">{{ userAchievement.opusCount }}</div>
                    </el-col>
                </el-row>
            </div>
            <div class="achievement-card">
                <el-row>
                    <el-col :span="8">
                        <i class="el-icon-view comment-icon"></i>
                    </el-col>
                    <el-col :span="16" class="achievement-right">
                        <div class="opus-title">游览数</div>
                        <div class="opus-count">{{ userAchievement.viewCount }}</div>
                    </el-col>
                </el-row>
            </div>
            <div class="achievement-card">
                <el-row>
                    <el-col :span="8">
                        <i class="iconfont icon-dianzan like-icon"></i>
                    </el-col>
                    <el-col :span="16" class="achievement-right">
                        <div class="opus-title">点赞数</div>
                        <div class="opus-count">{{ userAchievement.likeCount }}</div>
                    </el-col>
                </el-row>
            </div>
            <div class="achievement-card">
                <el-row>
                    <el-col :span="8">
                        <i class="iconfont icon-shoucang collect-icon"></i>
                    </el-col>
                    <el-col :span="16" class="achievement-right">
                        <div class="opus-title">收藏数</div>
                        <div class="opus-count">{{ userAchievement.collectCount }}</div>
                    </el-col>
                </el-row>
            </div>
        </div>
        <div style="position: relative;">
            <div class="opus-records" v-loading="loadingCanvas"></div>
            <el-tooltip 
            class="item opus-tip" effect="dark" content="原文数是指文章数，问答数，课程数，资源数，社区文章数的总和" 
            placement="top">
            <i class="iconfont icon-tiwenquestion"></i>
            </el-tooltip>
        </div>
        <div class="recently-opus">
            <div class="recently-opus-title">近期收藏</div>
            <el-table
            height="214"
                :show-header="false"
                :data="recentlyCollectList"
                style="width: 100%">
                <el-table-column
                width="80">
                <template slot-scope="scope">
                    <el-tag>{{ scope.row.typename }}</el-tag>
                </template>
                </el-table-column>
                <el-table-column
                width="380">
                <template slot-scope="scope">
                    <div @click="toOpusViews(scope.row)" class="associate-title">{{ scope.row.title }}</div>
                    <div @click="toOpusViews(scope.row)" class="associate-brief">{{ scope.row.brief }}</div>
                </template>
                </el-table-column>
                <el-table-column
                width="170">
                <template slot-scope="scope">
                    <div class="associate-common-number">{{ scope.row.viewCount }}</div>
                    <div class="associate-common-title">阅读数</div>
                </template>
                </el-table-column>
                <el-table-column
                width="170">
                <template slot-scope="scope">
                    <div class="associate-common-number">{{ scope.row.commentCount }}</div>
                    <div class="associate-common-title">评论数</div>
                </template>
                </el-table-column>
                <el-table-column
                width="170">
                <template slot-scope="scope">
                    <div class="associate-common-number">{{ scope.row.collectCount }}</div>
                    <div class="associate-common-title">收藏数</div>
                </template>
                </el-table-column>
            </el-table>
        </div>
    </div>
</template>

<script>
import $ from 'jquery'
import store from '@/store';
export default {
    name: 'MonkeyWebUserCreateHome',

    data() {
        return {
            loadingCanvas: true,
            recentlyCollectList: [],
            userOpusCountInYear: {},
            userAchievement: {},
            userCreateHomeUrl: "http://localhost:80/monkey-user/create/home",
            searchUserFeignUrl: "http://localhost:80/monkey-search/user/feign",
            communityArticleUrl: "http://localhost:80/monkey-community/article",
        };
    },
    created() {
        this.queryRecentlyCollect();
        this.queryUserAchievement();
        
    },
    mounted() {
        this.queryUserOpusCountInYear();
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
        // 查询用户近期收藏信息
        queryRecentlyCollect() {
            const vue = this;
            $.ajax({
                url: vue.userCreateHomeUrl + "/queryRecentlyCollect",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.recentlyCollectList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 得到用户一年中所发表的文章数
        queryUserOpusCountInYear() {
            const vue = this;
            $.ajax({
                url: vue.userCreateHomeUrl + "/queryUserOpusCountInYear",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.userOpusCountInYear = response.data;
                        vue.initEScharts();
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询用户原文数，游览数，点赞数，收藏数
        queryUserAchievement() {
            const vue = this;
            $.ajax({
                url: vue.searchUserFeignUrl + "/queryUserAchievement",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.userAchievement = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        initEScharts() {
            // 初始化原文记录
            this.initOpusRecords()
        },
        initOpusRecords() {
            var chartDom = document.querySelector('.opus-records');
            var myChart = this.$ECharts.init(chartDom);
            var option = {
                title: {
                    top: 20,
                    left: 'center',
                    text: '用户发表原文量',
                },
                tooltip: {
                    show: true,
                    trigger: "item",
                    formatter(p) {
                        return p.value[0] + "<br>" + "发表原文数：" +  p.value[1];
                    },
                    axisPointer: {
                        type: "shadow",
                        label: {
                            show: true,
                        }
                    }
                },
                legend: {
                    testStyle: {
                        borderRadius: "50%"
                    }
                },
                visualMap: {
                    min: 0,
                    max: 100,
                    type: 'piecewise',
                    orient: 'horizontal',
                    left: 'center',
                    top: 65,
                    inRange: {
                        color: ['#A0CFFF', '#409EFF'],
                    }
                },
                calendar: {
                    top: 110,
                    left: 30,
                    right: 30,
                    cellSize: ['auto', "auto"],
                    range: new Date().getFullYear(),
                    itemStyle: {
                        borderWidth: 0.5,
                        color: "#fff"
                    },
                    yearLabel: {
                        show: false,
                    },
                    dayLabel: {
                        show: true,
                        fontSize: 14
                    },
                    monthLabel: {
                        fontSize: 14
                    }
                },
                series: {
                    type: 'heatmap',
                    coordinateSystem: 'calendar',
                    data: this.getVirtualData(),
                    
                }
            };

            myChart.setOption(option)
            window.addEventListener("resize", function () {
                myChart.resize();
            })

            this.loadingCanvas = false;
        },
        getVirtualData() {
            const data = [];
            for (let i = 0; i < this.userOpusCountInYear.length; i++) {
                const userOpus = this.userOpusCountInYear[i];
                data.push([
                    userOpus.createTime,
                    userOpus.opusCount
                ])
            }
            return data;
        }
    },
};
</script>

<style scoped>
::-webkit-scrollbar {
    width: 10px;
    background-color: #EFEFEF;
}

:hover ::-webkit-scrollbar-track-piece {
    background-color: #EFEFEF;
    
    border-radius: 6px;
    
}

:hover::-webkit-scrollbar-thumb:hover {
    background-color: #EFEFEF;
}

:hover::-webkit-scrollbar-thumb:vertical {
    background-color: #EFEFEF;
    border-radius: 6px;
    background-color: #EFEFEF;
    outline-offset: -2px;
    background-color: #EFEFEF;
}
.associate-common-title {
    text-align: center;
}
.associate-common-number {
    text-align: center;
}
.associate-brief:hover {
    color: #409EFF;
}
.associate-brief {
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    font-size: 12px;
    color: gray;
    cursor: pointer;
    transition: 0.2s linear all;
}
.associate-title:hover {
    color: #409EFF;
}
.associate-title {
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    font-size: 14px;
    font-weight: bolder;
    cursor: pointer;
    transition: 0.2s linear all;
}
.recently-opus-title {
    text-align: center;
    font-size: 16px;
    font-weight: 600;
}
.opus-tip:hover {
    opacity: 0.7;
}
.opus-tip {
    position: absolute;
    right: 395px;
    top: 20px;
    font-size: 20px;
    cursor: pointer;
    transition: 0.4s linear all;
} 
.header-first {
    margin-bottom: 20px;
    height: 100px;
}
.recently-opus {
    background-color: #fff;
    padding: 20px 20px 0 20px;
}
.opus-records {
    width: 100%;
    height: 230px;
    margin-bottom: 20px;
    background-color: #fff;
    padding-bottom: 20px;
}
.achievement-right {
    padding-left: 50px;
}
.opus-count {
    font-size: 18px;
    font-weight: 600;
    color: rgba(0, 0, 0, 0.6);
    text-align: center;
}
.opus-title {
    margin-top: 10px;
    font-size: 18px;
    color: gray;
    font-weight: 600;
    margin-bottom: 10px;
    text-align: center;
}
.comment-icon {
    font-size: 70px;
    color: #40C9C6;
    vertical-align: middle;
}
.collect-icon {
    font-size: 70px;
    color: #E6A23C;
    vertical-align: middle;
}
.like-icon {
    font-size: 70px;
    color: #67C23A;
    vertical-align: middle;
}
.opus-icon {
    font-size: 80px;
    color: #409EFF;
    vertical-align: middle;
}
.achievement-card:hover {
    box-shadow: 0 0 10px gray;
}

.achievement-card {
    background-color: #fff;
    padding: 10px;
    cursor: pointer;
    transition: 0.4s linear all;
    box-shadow: 0 0 5px rgba(0, 0, 0, 0.1);
    width: 200px;
    display: inline-block;
    margin-right: 26px;
    height: 80px;
}
.achievement-card:last-child {
    margin-right: 0;
}
.MonkeyWebUserCreateHome-container {
    padding: 16px 16px 0 16px;
    vertical-align: middle;
     overflow: auto;
}
</style>