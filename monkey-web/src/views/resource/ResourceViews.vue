<template>
    <div class="MonkeyWebResourceViews-container">
        <div class="background">
            <img class="background-image-one" src="../../assets/images/resourceViewImgTwo.webp" alt="">
            <img class="background-image-two" src="../../assets/images/resourceViewImg.webp" alt="">
            <div class="search-title">一键检索全站资源</div>
            <el-input
            v-model="searchContent"
            placeholder="输入想要搜索的资源名后，回车"
            class="search-input">
            </el-input>
            <div class="recommend-title">
                <el-button type="primary" size="small">PDF电子书</el-button>
                <el-button type="primary" size="small">Office文档</el-button>
                <el-button type="primary" size="small">热门课程</el-button>
                <el-button type="primary" size="small">压缩包</el-button>
                <el-button type="primary" size="small">word文档</el-button>
            </div>
        </div>
        <e-row>
            <el-col :span="17">
                <div class="curation-resource">
                    <div class="curation-row">
                        <div class="curation-title">精选资源</div>
                        <div 
                        @click="selectCurationResource(classification)"
                        v-for="(classification, index) in oneClassificationList" 
                        v-if="index < 5"
                        :key="classification.id" 
                        :class="['curation-nav', {selected: selectedCurationId == classification.id}]">{{ classification.name }}</div>
                        <div @click="toResourceSearch()" class="curation-more">更多 <span class="el-icon-arrow-right"></span></div>
                    </div>
                    <div class="curation-content">
                        <CurationResourceCardVue
                        :curationResourceList="curationResourceList"/>
                    </div>
                </div>
                <div class="curation-resource">
                    <div class="curation-row">
                        <div class="hottest-title">下载最多</div>
                        <div 
                        @click="selectHottestResource(classification)"
                        v-if="index < 5"
                        v-for="(classification, index) in oneClassificationList" 
                        :key="classification.id" 
                        :class="['curation-nav', {selected: selectedHottestId == classification.id}]">{{ classification.name }}</div>
                        <div @click="toResourceSearch()" class="curation-more">更多 <span class="el-icon-arrow-right"></span></div>
                    </div>
                    <div class="curation-content">
                        <DownMoreCard
                        :hottestResourceList="hottestResourceList"/>
                    </div>
                </div>
            </el-col>
            <el-col :span="7">
                <div class="right-card">
                    <div class="latest-resource-card">
                        <div class="latest-title"></div>
                        <div class="latest-font">最新</div>
                        <div class="latest-scroll">
                            <div 
                            @click="toResourceDetail(latestResource.id)"  
                            class="latest-card" 
                            v-for="latestResource in latestResourceList" :key="latestResource.id">
                                <div >
                                    <img 
                                    @click.stop="toResourceDetail(latestResource.id)" 
                                    class="latest-img" 
                                    :src="latestResource.typeUrl" alt="">
                                    <span 
                                    @click.stop="toResourceDetail(latestResource.id)"  
                                    class="latest-name">{{ latestResource.name }}</span>
                                    <span class="latest-type" v-if="latestResource.formTypeId == '1'">免费</span>
                                    <span class="latest-vip" v-if="latestResource.formTypeId == '2'">vip</span>
                                    <span class="latest-price" v-if="latestResource.formTypeId == '3'">￥{{ latestResource.price }}</span>
                                    <span class="latest-time"> {{ getTimeFormat(latestResource.createTime) }}</span>
                                </div>
                                <div style="margin-top: 5px;">
                                    <img @click.stop="toUserViews(latestResource.userId)" class="latest-headImg"  :src="latestResource.headImg" alt="">
                                    <span @click.stop="toUserViews(latestResource.userId)" class="latest-username">{{ latestResource.username }}</span>
                                    <span class="latest-down el-icon-download">&nbsp;{{ getFormatNumber(latestResource.downCount) }}</span>
                                    <span class="iconfont icon-shoucang latest-down">&nbsp;{{ getFormatNumber(latestResource.collectCount) }}</span>
                                    <span class="iconfont icon-dianzan latest-down">&nbsp;{{ getFormatNumber(latestResource.likeCount) }}</span>
                                </div>
                            </div>
                        </div>
                        <div v-if="latestResourceList == null || latestResourceList == '' || latestResourceList == []">
                            <el-empty description="暂无资源"></el-empty>
                        </div>
                    </div>
                    <div class="user-card">
                        <div class="user-top">
                            <span style="margin-right: 10px;">资源创作排行榜</span>
                            <el-tooltip class="item user-rank-tip" effect="dark" content="每天凌晨 4 点更新，首先以用户下载量为标准，其次以用户资源量" placement="top">
                                <span class="el-icon-question"></span>
                            </el-tooltip>
                        </div>
                        <div class="user-rank-scroll">
                            <div 
                            class="user-content-card" 
                            @click.stop="toUserViews(userResource.userId)" 
                            v-for="(userResource, index) in userRankList" :key="userResource.id">
                                <el-row>
                                    <el-col :span="7">
                                        <span class="user-rank-score" :style="{'color': userRankColor[index]}">{{ index + 1 }}</span>
                                        <img @click.stop="toUserViews(userResource.userId)" class="user-rank-headImg" :src="userResource.headImg" alt="">
                                    </el-col>
                                    <el-col :span="17">
                                        <div>
                                            <span @click.stop="toUserViews(userResource.userId)" class="user-rank-name">{{ userResource.username }}</span>
                                            <span class="user-rank-vip">vip</span>
                                        </div>
                                        <div style="margin-top: 5px;">
                                            <span class="user-resource">资源量&nbsp;{{ getFormatNumber(userResource.resourcesCount) }}</span>
                                            <span class="user-resource">下载次数&nbsp;{{ getFormatNumber(userResource.downCount) }}</span>
                                        </div>
                                    </el-col>
                                </el-row>
                            </div>
                        </div>
                    </div>
                </div>
            </el-col>
        </e-row>
    </div>
</template>

<script>
import $ from 'jquery'
import DownMoreCard from '@/components/resource/DownMoreCard'
import CurationResourceCardVue from '@/components/resource/CurationResourceCard.vue';
import { getTimeFormat } from '@/assets/js/DateMethod'
import { getFormatNumber } from '@/assets/js/NumberMethod'
export default {
    name: 'MonkeyWebResourceViews',
    components: {
        CurationResourceCardVue,
        DownMoreCard,
    },
    data() {
        return {
            // 选中精选资源id
            selectedCurationId: "",
            // 选中最热资源id
            selectedHottestId: "",
            // 最热资源集合
            hottestResourceList: [],
            searchContent: "",
            userRankColor: ["red", '#F56C6C', "#FC5531"],
            // 一级分类列表
            oneClassificationList: [],
            curationResourceList: [],
            latestResourceList: [],
            userRankList: [],
            resourceClassificationUrl: "http://localhost:80/monkey-resource/classification",
            resourceHomePageUrl: "http://localhost:80/monkey-resource/homePage",
            resourceHomePageUrl: "http://localhost:80/monkey-resource/homePage",
        };
    },

    created() {
        this.queryOneLevelClassificationList();
        this.queryAllCurationResource();
        this.queryAllHottestResource();
        this.queryLatestResource();
        this.queryUserRank();
    },

    methods: {
        // 前往资源搜索页面
        toResourceSearch() {
            this.$router.push({
                name: "resource_search"
            })
        },
        resourceViewCountAddOne(resourceId) {
            const vue = this;
            $.ajax({
                url: vue.resourceHomePageUrl + "/resourceViewCountAddOne",
                type: "put",
                data: {
                    resourceId
                },
                success(response) {
                    if (response.code != vue.ResultStatus.SUCCESS) {
                        vue.$modal.msgError(response.msg);
                    } 
                }
            })
        },
        // 前往资源详情页面
        toResourceDetail(resourceId) {
            this.resourceViewCountAddOne(resourceId);
            const { href } = this.$router.resolve({
                name: "resource_detail",
                params: {
                    resourceId
                }
            })

            window.open(href, "_blank")
        },
        // 查询资源创作用户排行
        queryUserRank() {
            const vue = this;
            $.ajax({
                url: vue.resourceHomePageUrl + "/queryUserRank",
                type: "get",
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.userRankList = response.data;
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
        // 查询最新资源集合
        queryLatestResource() {
            const vue = this;
            $.ajax({
                url: vue.resourceHomePageUrl + "/queryLatestResource",
                type: "get",
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.latestResourceList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 通过选则标签id得到下载最多资源
        selectHottestResource(classification) {
            const vue = this;
            $.ajax({
                url: vue.resourceHomePageUrl + "/selectHottestResource",
                type: "get",
                data: {
                    classificationId: classification.id,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.selectedHottestId = classification.id
                        vue.hottestResourceList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询全部下载最多资源
        queryAllHottestResource() {
            const vue = this;
            $.ajax({
                url: vue.resourceHomePageUrl + "/queryAllHottestResource",
                type: "get",
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.hottestResourceList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 通过选则标签id得到精选资源
        selectCurationResource(classification) {
            const vue = this;
            $.ajax({
                url: vue.resourceHomePageUrl + "/selectCurationResource",
                type: "get",
                data: {
                    classificationId: classification.id,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.selectedCurationId = classification.id
                        vue.curationResourceList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询全部精选资源
        queryAllCurationResource() {
            const vue = this;
            $.ajax({
                url: vue.resourceHomePageUrl + "/queryAllCurationResource",
                type: "get",
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.curationResourceList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 得到一级分类
        queryOneLevelClassificationList() {
            const vue = this;
            $.ajax({
                url: vue.resourceClassificationUrl + "/queryOneLevelClassificationList",
                type: "get",
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.oneClassificationList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
        },
        getTimeFormat(timeStamp) {
            return getTimeFormat(timeStamp);
        },
        getFormatNumber(numbers) {
            return getFormatNumber(numbers);
        },
    },
};
</script>

<style scoped>
.user-resource {
    display: inline-block;
    vertical-align: middle;
    opacity: 0.5;
    font-size: 14px;
    margin-right: 10px;
    max-width: 120px;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
}
.user-rank-tip {
    cursor: pointer;
}
.latest-vip {
    border-radius: 5px;
    color: white;
    padding: 2px 5px;
    background-image: linear-gradient(to right, #fa709a 0%, #fee140 100%);
    font-size: 14px;
    vertical-align: middle;
}
.latest-price {
    display: inline-block;
    padding: 2px 5px;
    text-align: center;
    background-color: #f90a0a;
    color: #fff;
    border-radius: 5px;
    font-size: 14px;
}
.user-rank-scroll {
    max-height: 450px;
    overflow-y: auto;
}
.latest-scroll {
    max-height: 470px;
    overflow-y: auto;
}
::-webkit-scrollbar {
    width: 10px;
    background-color: #fff;
}

:hover ::-webkit-scrollbar-track-piece {
    /* 鼠标移动上去再显示滚动条 */
    background-color: #fff;
    /* 滚动条的背景颜色 */
    border-radius: 6px;
    /* 滚动条的圆角宽度 */
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
.user-rank-vip {
    border-radius: 5px;
    color: white;
    padding: 2px 5px;
    background-image: linear-gradient(to right, #fa709a 0%, #fee140 100%);
    font-size: 14px;
    vertical-align: middle;
}
.user-rank-name {
    display: inline-block;
    vertical-align: middle;
    opacity: 0.5;
    font-size: 14px;
    margin-right: 10px;
    max-width: 120px;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
}
.user-rank-name:hover {
    color: #00f2fe;
    cursor: pointer;
}
.user-rank-score {
    font-size: 20px;
    font-weight: 600;
    vertical-align: middle;
    margin-right: 15px;
    color: rgba(0, 0, 0, 0.5);
}
.user-rank-headImg {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    vertical-align: middle;
    transition: 0.2s linear all;
}
.user-rank-headImg:hover {
    cursor: pointer;
    filter: brightness(1.2);
}
.user-content-card {
    padding: 20px;
    transition: 0.4s linear all;
    border-radius: 10px;
    vertical-align: middle;
    cursor: pointer;
}
.user-content-card:hover {
    
    background-color: rgba(0, 0, 0, 0.1);
}
.user-top {
    display: inline-block;
    background-color: rgba(8,211,252);
    padding: 10px 20px;
    border-radius: 0 20px 20px 0;
    color: white;
}
.user-card {
    margin-left: 10px;
    margin-top: 20px;
    background-color: #fff;
    box-shadow: 0 0 3px 0 rgba(0, 0, 0, 0.1);
}
.latest-card {
    border-radius: 10px;
    padding: 10px;
    transition: 0.4s linear all;
}
.latest-card:hover {
    cursor: pointer;
    background-color: rgba(0, 0, 0, 0.1);
}
.divisor {
    height: 1px;
    background-color: rgba(0, 0, 0, 0.1);
    margin: 10px;
}
.latest-down {
    font-size: 14px;
    opacity: 0.5;
    vertical-align: middle;
    margin-right: 10px;
}
.latest-username {
    display: inline-block;
    font-size: 14px;
    opacity: 0.5;
    vertical-align: middle;
    margin-right: 5px;
    max-width: 70px;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
}
.latest-username:hover {
    cursor: pointer;
    color: #00f2fe;
}
.latest-headImg {
    width: 35px;
    height: 35px;
    vertical-align: middle;
    transition: 0.4s linear all;
    margin-right: 10px;
    border-radius: 50%;
}
.latest-headImg:hover {
    cursor: pointer;
    transform: scale(1.2);
}
.latest-time {
    font-size: 14px;
    opacity: 0.5;
    vertical-align: middle;
}
.latest-type {
    display: inline-block;
    margin-right: 10px;
    vertical-align: middle;
    padding: 2px 5px;
    font-size: 14px;
    color: white;
    background-color: #00f2fe;
    border-radius: 5px;
}
.latest-name {
    display: inline-block;
    opacity: 0.5;
    vertical-align: middle;
    margin-right: 10px;
    max-width: 130px;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
    font-size: 14px;
}
.latest-name:hover {
    cursor: pointer;
    color: #00f2fe;
}
.latest-img {
    width: 35px;
    height: 35px;
    vertical-align: middle;
    transition: 0.4s linear all;
    margin-right: 10px;
}
.latest-img:hover {
    cursor: pointer;
    transform: scale(1.5);
}
.right-card {
    padding-top: 20px; 
}
.latest-font {
    position: absolute;
    color: white;
    top: 9px;
    left: 0px;
    font-size: 16px;
    transform: rotate(-45deg);
}
.latest-title {
    position: absolute;
    background-color: #08d3fc;
    color: white;
    top: -39px;
    left: -39px;
    height: 80px;
    width: 80px;
    text-align: center;
    line-height: 25px;
    transform: rotate(-45deg);
}
.latest-resource-card {
    position: relative;
    margin-top: 20px;
    margin-left: 10px;
    padding: 10px;
    padding-top: 20px;
    background-color: #fff;
    overflow: hidden;
    box-shadow: 0 0 3px 0 rgba(0, 0, 0, 0.1);
}

.el-icon-star-off {
    font-size: 20px;
    vertical-align: middle;
    color: orange;
}
.curation-content {
    padding-left: 20px;
    font-size: 14px;
}
.curation-more {
    display: inline-block;
    font-size: 12px;
}
.curation-more:hover {
    cursor: pointer;
    opacity: 0.5;
}
.curation-nav {
    font-size: 12px;
    display: inline-block;
    padding: 5px 10px;
    margin-right: 10px;
    font-weight: 500;
    background-color: rgba(0, 0, 0, 0.1);
}
.curation-nav:hover {
    cursor: pointer;
    background-color: rgba(147,248,254);
    color: black;
    font-weight: bolder;
}
.selected {
    cursor: pointer;
    background-color: rgba(147,248,254);
    color: black;
    font-weight: bolder;
}
.curation-row {
    overflow-x: hidden;
    width: 100%;
    padding: 10px 20px;
}
.hottest-title {
    display: inline-block;
    background-image: linear-gradient(to right, #f83600 0%, #fff 100%);
    padding: 10px 20px;
    color: black;
    font-weight: bolder;
    font-size: 15px;
    width: 200px;
    margin-bottom: 10px;
}
.curation-title {
    display: inline-block;
    background-image: linear-gradient(to right, #89f7fe 0%, #fff 100%);
    padding: 10px 20px;
    color: black;
    font-weight: bolder;
    font-size: 15px;
    width: 200px;
    margin-bottom: 10px;
}
.curation-resource {
    position: relative;
    margin-top: 20px;
    background-color: #fff;
    padding: 20px;
}
.recommend-title {
    position: absolute;
    top: 0;
    color: white;
    opacity: 1;
    font-weight: 600;
    font-size: 30px;
    left: 50%;
    top: 75%;
    transform: translate(-50%, -50%);
}
.search-input ::placeholder {
    position: absolute;
    top: 50%;
    transform: translateY(-50%);
    font-size: 14px;
    vertical-align: middle;
}
.search-input {
    position: absolute;
    top: 0;
    color: white;
    opacity: 1;
    font-size: 30px;
    left: 50%;
    top: 50%;
    width: 800px;
    transform: translate(-50%, -50%);
    font-size: 14px;
    font-weight: bolder;
    transition: 0.2s linear all;
} 
.search-input:hover {
    box-shadow: 0 0 10px 0 black;
}

.search-title {
    position: absolute;
    top: 0;
    color: white;
    opacity: 1;
    font-weight: 600;
    font-size: 30px;
    left: 50%;
    top: 30%;
    transform: translate(-50%, -50%);
}
.background-image-two {
    position: absolute;
    height: 380px;
    vertical-align: middle;
    right: 0;
    filter: brightness(0.9);
}
.background-image-one {
    height: 380px;
    vertical-align: middle;
    left: 0;
    /* 调整明亮度为50% （范围从0到1）*/
    filter: brightness(0.9);
}
.background {
    margin-top: 20px;
    position: relative;
    background-image: url("https://img-home.csdnimg.cn/images/20230209102851.png");
    background-image: linear-gradient(to right, #4facfe 0%, #00f2fe 100%);
    /* background-image: linear-gradient(90deg,#141e30,#243b55); */
    /* filter: brightness(0.8); */
}
.MonkeyWebResourceViews-container {
    width: 1200px;
    margin: 0 auto;
    height: 2000px;
    animation: show-out 0.4s linear;
}
@keyframes show-out {
    0% {
        opacity: 0;
    }
    100% {
        opacity: 1;
    }
}
</style>