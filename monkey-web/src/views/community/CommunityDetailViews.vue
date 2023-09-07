<template>
    <div class="MonkeyWebCommunityDetailViews-container">
        <el-row class="position">
            <el-col :span="4" class="left-aside">
                <div style="padding: 20px 20px 10px 20px;">
                        <el-input v-model="queryCommunity" style="position: relative;" placeholder="搜索社区">
                            <template v-slot:suffix>
                                <i class="el-icon-search"></i>
                            </template>
                        </el-input>
                

                    <div class="divider"></div>

                    <div class="to-community-view">社区首页 <span style="float: right;">></span></div>
                </div>

                <div style="padding: 0 10px;">
                    <el-row>
                        <el-row class="my-add-community">
                            <div @click="myAddCommunity()">
                                <el-col :span="2">
                                    <span v-if="showCommunity == '1'" class="el-icon-caret-bottom">
                                    </span>
                                    <span v-else class="el-icon-caret-right"></span>
                                </el-col>
                                <el-col :span="22">
                                    <span class="el-icon-folder-opened">&nbsp;我加入的社区</span>
                                </el-col>   
                            </div>
                        </el-row>
                         <!-- 展示我加入的社区 -->
                            <el-row v-if="showCommunity == '1'" class="show-community-data">
                                <el-col :span="2">
                                    <img class="left-img" src="https://ts2.cn.mm.bing.net/th?id=OIP-C.th6FlIUU3ebI8pl4ccYHZgHaEo&w=316&h=197&c=8&rs=1&qlt=90&o=6&dpr=1.3&pid=3.1&rm=2" alt="">
                                </el-col>
                                <el-col :span="20" class="left-content">
                                    啥也不会hh啥也不会hh啥也不会hh啥也不会hh啥也不会hh啥也不会hh
                                </el-col>
                            </el-row>
                            <el-row v-if="showCommunity == '1'" class="show-community-data">
                                <el-col :span="2">
                                    <img class="left-img" src="https://ts2.cn.mm.bing.net/th?id=OIP-C.th6FlIUU3ebI8pl4ccYHZgHaEo&w=316&h=197&c=8&rs=1&qlt=90&o=6&dpr=1.3&pid=3.1&rm=2" alt="">
                                </el-col>
                                <el-col :span="20" class="left-content">
                                    啥也不会hh啥也不会hh啥也不会hh啥也不会hh啥也不会hh啥也不会hh
                                </el-col>
                            </el-row>
                            <el-row v-if="showCommunity == '1'" class="show-community-data">
                                <el-col :span="2">
                                    <img class="left-img" src="https://ts2.cn.mm.bing.net/th?id=OIP-C.th6FlIUU3ebI8pl4ccYHZgHaEo&w=316&h=197&c=8&rs=1&qlt=90&o=6&dpr=1.3&pid=3.1&rm=2" alt="">
                                </el-col>
                                <el-col :span="20" class="left-content">
                                    啥也不会hh啥也不会hh啥也不会hh啥也不会hh啥也不会hh啥也不会hh
                                </el-col>
                            </el-row>
                            <el-row v-if="showCommunity == '1'" class="show-community-data">
                                <el-col :span="2">
                                    <img class="left-img" src="https://ts2.cn.mm.bing.net/th?id=OIP-C.th6FlIUU3ebI8pl4ccYHZgHaEo&w=316&h=197&c=8&rs=1&qlt=90&o=6&dpr=1.3&pid=3.1&rm=2" alt="">
                                </el-col>
                                <el-col :span="20" class="left-content">
                                    啥也不会hh啥也不会hh啥也不会hh啥也不会hh啥也不会hh啥也不会hh
                                </el-col>
                            </el-row>
                    </el-row>

                    <el-row>
                        <el-row class="my-add-community">
                            <div @click="myManageCommunity()">
                                <el-col :span="2">
                                    <span v-if="showCommunity == '2'" class="el-icon-caret-bottom">
                                    </span>
                                    <span v-else class="el-icon-caret-right"></span>
                                </el-col>
                                <el-col :span="22">
                                    <span class="el-icon-folder-opened">&nbsp;我管理的社区</span>
                                </el-col>   
                            </div>
                        </el-row>
                         <!-- 展示我加入的社区 -->
                            <el-row v-if="showCommunity == '2'" class="show-community-data">
                                <el-col :span="2">
                                    <img class="left-img" src="https://ts2.cn.mm.bing.net/th?id=OIP-C.th6FlIUU3ebI8pl4ccYHZgHaEo&w=316&h=197&c=8&rs=1&qlt=90&o=6&dpr=1.3&pid=3.1&rm=2" alt="">
                                </el-col>
                                <el-col :span="20" class="left-content">
                                    啥也不会hh啥也不会hh啥也不会hh啥也不会hh啥也不会hh啥也不会hh
                                </el-col>
                            </el-row>
                    </el-row>

                    <el-row>
                        <el-row class="my-add-community">
                            <div @click="schoolCommunity()">
                                <el-col :span="2">
                                    <span v-if="showCommunity == '3'" class="el-icon-caret-bottom">
                                    </span>
                                    <span v-else class="el-icon-caret-right"></span>
                                </el-col>
                                <el-col :span="22">
                                    <span class="el-icon-folder-opened">&nbsp;高校社区</span>
                                </el-col>   
                            </div>
                        </el-row>
                        <!-- 展示我加入的社区 -->
                            <el-row v-if="showCommunity == '3'" class="show-community-data">
                                <el-col :span="2">
                                    <img class="left-img" src="https://ts2.cn.mm.bing.net/th?id=OIP-C.th6FlIUU3ebI8pl4ccYHZgHaEo&w=316&h=197&c=8&rs=1&qlt=90&o=6&dpr=1.3&pid=3.1&rm=2" alt="">
                                </el-col>
                                <el-col :span="20" class="left-content">
                                    啥也不会hh啥也不会hh啥也不会hh啥也不会hh啥也不会hh啥也不会hh
                                </el-col>
                            </el-row>
                    </el-row>
                </div>
            </el-col>
            <el-col :span="15" style="padding: 20px;">
                <div class="article-content" @click="publishCommunityArticle(communityId)">
                    <span class="article-content-font">请您编写文章内容</span>
                    <el-button type="primary" class="publish-article" size="medium">发布文章</el-button>
                </div>
            </el-col>
            <el-col :span="5">

            </el-col>
        </el-row>
    </div>
</template>

<script>
export default {
    name: 'MonkeyWebCommunityDetailViews',

    data() {
        return {
            // 社区id
            communityId: "",
            // 通过社区名搜索社区字段
            queryCommunity: "",
            // 点击展示的社区, 1为我加入的社区，2为我管理的社区，3为高校社区
            showCommunity: "",
        };
    },
    created() {
        this.communityId = this.$route.params.communityId;
    },

    methods: {
        // 发表社区文章
        publishCommunityArticle(communityId) {
            const { href } = this.communityId = this.$router.resolve({
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
            } else {
                this.showCommunity = -1;
            }
        },
        // 我管理的社区
        myManageCommunity() {
            if (this.showCommunity != 2) {
                this.showCommunity = 2;
            } else {
                this.showCommunity = -1;
            }
            
        },
        // 高校社区
        schoolCommunity() {
            if (this.showCommunity != 3) {
                this.showCommunity = 3;
            } else {
                this.showCommunity = -1;
            }
            
        }
    },
};
</script>

<style scoped>
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
    height: 30px;
    line-height: 30px;
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
    padding: 5px 0px 5px 20px;
    width: 100%;
    margin-bottom: 10px;
    margin-top: 10px;
}
.show-community-data:hover {
    cursor: pointer;
    background-color: #F5F6F7;
}
.left-img {
    height: 20px;
    width: 20px;
    cursor: pointer;
}
@keyframes slow-arrow {
    0% {

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
}
.divider {
    height: 1px;
    background: #e8e8ed;
    margin: 20px 0;
}
.MonkeyWebCommunityDetailViews-container {
    background-color: #fff;
    margin: 0 auto;
    width: 1400px;
    height: 400px;
}
.position {
    margin-top: 10px;
}
.el-icon-search {
    position: absolute;
    top: 30%;
    right: 0;
    font-size: 16px;
}
    
</style>