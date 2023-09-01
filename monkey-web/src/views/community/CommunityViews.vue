<template>
    <div class="MonkeyWebCommunityViews-container">
        <SearchCommunity
        v-if="inputFocus"
        @closeLabelWindow="closeLabelWindow"
        @selectTwoLabel="selectTwoLabel"/>
        <!-- 头部 -->
        <el-row>
            <el-col :span="17" style="height:260px">
                <el-carousel trigger="click" style="overflow: hidden;" height="260px">
                    <el-carousel-item v-for="item in 4" :key="item">
                        <img class="carousel-img" src="https://tse2-mm.cn.bing.net/th/id/OIP-C.Lf2u2dPln44gRiIC7IR3IwHaEK?w=318&h=180&c=7&r=0&o=5&dpr=1.3&pid=1.7" alt="">
                    </el-carousel-item>
                </el-carousel>
            </el-col>
            <el-col :span="7" style="padding-left: 20px; height: 260px;">
                <el-row class="create-community">
                    <span class="iconfont icon-shequ create-community-icon">&nbsp;&nbsp;</span>
                    <span class="create-community-content">创建社区</span>
                    <span class="arrow">></span>
                </el-row>

                <el-row class="school-community">
                    <span class="iconfont icon-xuexiao create-community-icon">&nbsp;&nbsp;</span>
                    <span class="create-community-content">高校社区</span>
                    <span class="arrow">></span>
                </el-row>

                <el-row class="notice-community">
                    <span class="iconfont icon-gonggao create-community-icon">&nbsp;&nbsp;</span>
                    <span class="create-community-content">系统通知</span>
                    <span class="arrow">></span>
                </el-row>

                <el-row class="manage-community">
                    <span class="iconfont icon-guanli create-community-icon">&nbsp;&nbsp;</span>
                    <span class="create-community-content">社区管理</span>
                    <span class="arrow">></span>
                </el-row>
                
            </el-col>
        </el-row>

        <!-- 中间内容 -->
        <el-row style="margin-top: 50px;">    
            <el-col :span="17">
                <el-tabs v-model="activeName" @tab-click="handleTagClick(activeName)">
                    <el-tab-pane label="与我有关" name="withMe"></el-tab-pane>
                    <el-tab-pane label="最新发布" name="latestPublish"></el-tab-pane>
                    <el-tab-pane label="最新回复" name="latestReply"></el-tab-pane>
                    <el-tab-pane label="最热" name="latestHire"></el-tab-pane>
                    <el-tab-pane label="有问题" name="question"></el-tab-pane>
                    <CommunityCard/>
                    <CommunityCard/>
                    <PagiNation
                    style="text-align: right;"
                    :totals="totals"
                    :currentPage="currentPage" 
                    :pageSize="pageSize" 
                    @handleCurrentChange = "handleCurrentChange"
                    @handleSizeChange="handleSizeChange"/>
                </el-tabs>
                
            </el-col>
            <el-col :span="7" style="padding: 0 20px;">
                <el-row >
                    <el-input 
                    @keydown.native="searchArticle(communityName, dynamicTags)"
                    type="text"
                    placeholder="输入想要搜索的社区名"
                    v-model="communityName">
                    
                    <div slot="append" class="search">
                        <el-button @click="searchArticle()" size="mini"><i class="el-icon-search"> 搜索</i></el-button>
                        
                    </div>
                    <template slot="prepend">
                        <el-button 
                            type="primary" 
                            class="el-icon-circle-plus-outline add-label" 
                            @click="inputFocusShow(communityName, dynamicTags)"
                            size="mini">&nbsp;标签
                        </el-button>
                    </template>
                </el-input>
                    
                </el-row>
                    <el-row>
                        <el-tag
                            class="inner-tag"
                            :key="tag"
                            v-for="tag in dynamicTags"
                            closable
                            :disable-transitions="false"
                            @close="handleClose(tag)">
                            {{tag.name}}
                        </el-tag>
                    </el-row>

                    <el-tabs v-model="activeNameAside" type="card" @tab-click="handleClick" style="margin-top: 10px;">
                        <el-tab-pane label="我的社区" name="myCommunity"></el-tab-pane>
                        <el-tab-pane label="热门社区" name="hire"></el-tab-pane>
                        <el-tab-pane label="最新社区" name="latest"></el-tab-pane>

                        <el-row style="margin-bottom: 10px;">
                            <el-col :span="4" style="overflow: hidden;">
                                <img class="aside-img" src="https://ts2.cn.mm.bing.net/th?id=OIP-C.1zMo-77F0KNEbklTzOledQHaEo&w=316&h=197&c=8&rs=1&qlt=90&o=6&dpr=1.3&pid=3.1&rm=2" alt="">
                            </el-col>
                            <el-col :span="15" style="padding-left: 10px;">
                                <el-row class="community-name-aside">社区名社区名社区名社区名社区名社区名社区名社区名社区名</el-row>
                                <el-row class="member-aside">
                                    888 成员
                                </el-row>
                            </el-col>
                            <el-col :span="5">
                                <el-button v-if="activeNameAside != 'myCommunity'" size="mini" class="button-aside">加入</el-button>
                            </el-col>
                        </el-row>
                        
                        
                    </el-tabs>
            </el-col>
        </el-row>
    </div>
</template>

<script>
import SearchCommunity from '@/components/community/SearchCommunity'
import PagiNation from '@/components/pagination/PagiNation.vue';
import CommunityCard from '@/components/community/CommunityCard'
import $ from 'jquery'
import CarouselMap from '@/components/carousel/CarouselMap'
import store from '@/store';
export default {
    name: 'MonkeyWebCommunityViews',
    components: {
        CarouselMap,
        CommunityCard,
        PagiNation,
        SearchCommunity
    },
    data() {
        return {
            activeName: "withMe",
            currentPage: 1,
            pageSize: 10,
            totals: 0,
            dynamicTags: [],
            // 输入框是否聚焦
            inputFocus: false,
            // 想要搜索的社区名
            communityName: "",
            // 旁边标签选则
            activeNameAside: "myCommunity",
        };
    },
    watch: {

    },
    mounted() {
        this.$watch("dynamicTags", (newVal) => {
            if (newVal.length > 0) {
                this.isShowConfirmSearch = true;
            } else {
                this.isShowConfirmSearch = false;
            }
        })
    },

    created() {

    },

    methods: {
        // 根据条件搜索文章
        searchArticle(name, tabList) {
            
        },
        // 选中了二级标签
        selectTwoLabel(twoLabel) {
            this.dynamicTags.push(twoLabel);
        },
        // 关闭标签选择框
        closeLabelWindow() {
            this.inputFocus = false;
        },
        inputFocusShow() {
            this.inputFocus = true;
        },
        handleClose(tag) {
            this.dynamicTags.splice(this.dynamicTags.indexOf(tag), 1);
        },
        handleTagClick(name) {
            if (name == 'withMe') {
            
            } 
        },
        handleSizeChange(val) {
            this.pageSize = val;

        },
        handleCurrentChange(val) {
            this.currentPage = val;
        },
    },
};
</script>

<style scoped>
.member-aside {
    font-size: 12px;
    font-weight: 400;
    color: #555666;
    line-height: 20px;
}
.button-aside {
    color: #555666;
    background-color: #fff;
    border-radius: 12px;
    margin-left: 12px;
    border: 1px solid #ccccd8;
    width: 48px;
    overflow: hidden;
    -ms-flex-negative: 0;
    flex-shrink: 0;
    padding: 0;
    outline: none;
    -ms-flex-item-align: center;
    align-self: center;
    height: 24px;
    font-size: 12px;
    font-weight: 400;
    margin-top: 10px;
}
.community-name-aside {
    font-size: 14px;
    font-weight: 400;
    color: #222226;
    line-height: 22px;
    max-width: 100%;
    overflow: hidden;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
    text-overflow: ellipsis;
    white-space: nowrap;
    vertical-align: middle;
    cursor: pointer;
}
.aside-img {
    width: 100%;
    height: 44px;
    cursor: pointer;
    transition: 0.2s linear all;
}
.aside-img:hover {
    transform: scale(1.4);
}
.search {
    cursor: pointer;
}
.add-label {
    margin: 10px 0;
    margin-right: 10px;
}
.inner-tag {
    margin: 10px 0px;
    margin-right: 10px;
}
.before-input {
    width: 100%;
    height: 100%;
    border: none;
    outline: none;
    border-radius: 19px;
    background-color: transparent;
    cursor: pointer;
}
.before-input:hover {
    color: #409EFF;
}

.arrow {
    position: absolute;
    right: 10%;
}
.create-community-content {
    font-size: 18px;
}
.create-community-icon {
    display: inline-block;
    font-size: 20px;
}
.school-community {
    position: relative;
    padding: 15px;
    cursor: pointer;
    border-radius: 10px;
    margin-top: 10px;
    background-image: linear-gradient(to right, #fddb92 0%, #d1fdff 100%);
}
.notice-community {
    position: relative;
    padding: 15px;
    cursor: pointer;
    border-radius: 10px;
    margin-top: 10px;
    background-image: linear-gradient(to right, #fff1eb 0%, #ace0f9 100%);
}
.manage-community {
    position: relative;
    padding: 15px;
    cursor: pointer;
    border-radius: 10px;
    margin-top: 10px;
    background-image: linear-gradient(to right, #cd9cf2 0%, #f6f3ff 100%);
}
.create-community {
    position: relative;
    padding: 15px;
    cursor: pointer;
    border-radius: 10px;
    background-image: linear-gradient(to right, #96fbc4 0%, #f9f586 100%);
}
.carousel-img {
    width: 100%;
    height: 100%;
    cursor: pointer;
    transition: 0.4s linear all;
}
.carousel-img:hover{
    transform: scale(1.4);
}
.el-carousel__item h3 {
    color: #475669;
    font-size: 14px;
    opacity: 0.75;
    line-height: 150px;
    margin: 0;
}

.el-carousel__item:nth-child(2n) {
    background-color: #99a9bf;
}

.el-carousel__item:nth-child(2n+1) {
    background-color: #d3dce6;
}
.MonkeyWebCommunityViews-container {
    width: 1280px;
    background-color: #FFFFFF;
    margin: 0 auto;
    padding: 20px;
}
</style>